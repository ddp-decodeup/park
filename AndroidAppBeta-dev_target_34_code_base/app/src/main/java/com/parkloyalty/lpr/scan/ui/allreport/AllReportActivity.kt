package com.parkloyalty.lpr.scan.ui.allreport

import android.Manifest
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.location.Address
import android.location.Geocoder
import android.media.ExifInterface
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.text.InputType
import android.text.TextUtils
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textfield.TextInputLayout.END_ICON_NONE
import com.parkloyalty.lpr.scan.BuildConfig
import com.parkloyalty.lpr.scan.R
import com.parkloyalty.lpr.scan.basecontrol.BaseActivity
import com.parkloyalty.lpr.scan.basecontrol.BaseApplication
import com.parkloyalty.lpr.scan.database.AppDatabase
import com.parkloyalty.lpr.scan.database.Singleton
import com.parkloyalty.lpr.scan.extensions.getCitaitonImageFormat
import com.parkloyalty.lpr.scan.extensions.nullSafety
import com.parkloyalty.lpr.scan.extensions.showToast
import com.parkloyalty.lpr.scan.interfaces.Constants
import com.parkloyalty.lpr.scan.interfaces.CustomDialogHelper
import com.parkloyalty.lpr.scan.interfaces.SharedPrefKey
import com.parkloyalty.lpr.scan.network.*
import com.parkloyalty.lpr.scan.network.models.ScannedImageUploadResponse
import com.parkloyalty.lpr.scan.ui.allreport.model.*
import com.parkloyalty.lpr.scan.ui.boot.model.ResponseBoot
import com.parkloyalty.lpr.scan.ui.check_setup.adapter.ViewPagerBannerAdapterWithDefaultImage
import com.parkloyalty.lpr.scan.ui.check_setup.model.lpr_model.UploadImageViewModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.CitationImagesModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DatasetResponse
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DropdownDatasetResponse
import com.parkloyalty.lpr.scan.ui.login.model.*
import com.parkloyalty.lpr.scan.util.*
import com.parkloyalty.lpr.scan.utils.ObjectMapperProvider
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import se.warting.signatureview.views.SignaturePad
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.regex.Pattern
import kotlin.getValue


class AllReportActivity : BaseActivity(), CustomDialogHelper {

    @BindView(R.id.officername)
    lateinit var mEditTextOfficerName: AppCompatTextView

    @BindView(R.id.txt_beat)
    lateinit var mEditTextOfficerBeat: AppCompatTextView

    @BindView(R.id.txt_badge_id)
    lateinit var mEditTextOfficerId: AppCompatTextView

    @BindView(R.id.txt_issuedate)
    lateinit var mEditTextDateTime: AppCompatTextView

    @BindView(R.id.txt_report_number)
    lateinit var mEditTextReportNumber: AppCompatTextView

    @BindView(R.id.linear_layout_location)
    lateinit var mLocationDetails: LinearLayoutCompat

    @BindView(R.id.linear_layout_details)
    lateinit var mOtherDetails: LinearLayoutCompat

    @BindView(R.id.ll_images)
    lateinit var mCameraImagesLayout: LinearLayoutCompat

    @BindView(R.id.pager_banner)
    lateinit var mViewPagerBanner: ViewPager

    @BindView(R.id.viewPagerCountDots)
    lateinit var pagerIndicator: LinearLayoutCompat

    @BindView(R.id.tvCameraTitle)
    lateinit var tvCameraTitle : AppCompatTextView

    @BindView(R.id.ivCameraBottom)
    lateinit var appCompatImageViewCameraIcon : AppCompatImageView


    private var fromForm = ""
    private val mSideItem = ""
    private var clientTime = ""
    private val mTureFalseList = arrayOf("TRUE", "FALSE")
    private val mYesNoList = arrayOf("YES", "NO")
    private val mPassFailedList = arrayOf("PASS", "FAIL")
    private val mActiveInactiveList = arrayOf("ACTIVE", "INACTIVE")
    private var mAssignedBikeList = arrayOf("BK M", "BK L","BK M1", "BK L1","BK M2", "BK L2")

    private val mOutInServiceList = arrayOf("OUT OF SERVICE", "IN-SERVICE")
    private val mColorList = arrayOf("RED","YELLOW","WHITE", "BLUE","GREEN")
    private val mGasList = arrayOf("Empty", "1/4 Tank", "1/2 Tank", "3/4 Tank", "Full Tank")
    private val mBatterChargeList = arrayOf("Full", "1/2", "1/4", "No Charge")
    private val mMeterTypeList = arrayOf("Pay Station", "Single Head Meter","Multispace Meters")
    private var mDeviceList = arrayOf("101", "102", "103", "104", "105", "106", "107", "108", "109", "110","111")
    private var mRequestToTowList = arrayOf("Tow", "Relocate")
//    private val mDeviceListBurbank = arrayOf("B 01", "B 02", "B 03", "B 04", "B 05")
    private var mUnitNumberList = arrayOf("BK M","BK L","101", "102", "103", "104", "105", "106", "107", "108", "109", "110")
    private val mAssignAreaList = arrayOf("Beat 11", "Beat 12", "Beat 13", "Beat 14", "Beat 15")
    private val mDutyHourAreaList = arrayOf("1", "2", "3", "4", "5","6", "7", "8", "9", "10",
            "11", "12", "13", "14", "15","16", "17", "18", "19", "20","21","22","23","24")
    private val mReasonForTowList = arrayOf("Park in tow away zones, such as disabled, reserved and no parking areas",
            "22651(k) VC - Parked Over 72 Hours in Violation of an Ordinance",
            "22658 - Removal From Private Property",
            "Abandoned Vehicle",
            "Have expired vehicle registration (more than six months), or have no license plates or other evidence of registration displayed.",
            "Have three or more outstanding (unpaid) METRO parking violations." ,
            "Have five or more outstanding (unpaid) parking violations from any agency in the State.",
            "Vehicle Exceeds Load Size Limit",
            "CVC 22651(O) - Vehicle Registration Expired Over Six Months",
            "CVC 22651(j)",
            "CVC 22651(n)  Local Ordinance Parking Violation",
            "8 05 040 Failure to obey signs",
            "CVC 22659(a)",
            "CVC 22669")
    private val mVehicleStoredAtList = arrayOf("601 N. Inglewood Ave. Inglewood, CA 90302 Bryant’s Inglewood Towing (310)419-8700",
        "1654 12th Street Santa Monica CA 90404 Tip Top (310)314-4040","7891 Deering Ave. Canoga Park, CA 91304 Howard Sommers Towing Inc (818)884-5600",
    "906 Westminster Ave. Alhambra, CA 91803 JM Towing LLC (626)289-3288","12565 Strathern St. N. Hollywood, CA 91605 Archers Vineland Service Inc (818)982-1464",
    "8750 Vanalden Ave Northridge CA 91324 Ross Baker Towing Inc (818)886-7411","7817 Woodley Ave Van Nuys CA 91706 Keystone (818)782-1996",
    "332 Foothill RD Beverly Hills CA 90210 Tip Top (310)314-4040","2119 East 25th Street Los Angeles CA 90058 US Tow (213)749-7100",
    "1615 East Adams BL Los Angeles CA 90011 US Tow (213)749-7100","1615 East Mauretania Ave Wilmington CA 90744 7th Street Garage (310)856-1980",
    "10857 San Fernando RD. Pacoima, CA 91331 Black and White Towing Inc (818)896-9511")
    private val mSafetyReportList = arrayOf("Oil Spil","Trip Hazard","Elecrical", "Vehicle", "Glass", "Other")
    private val mRequiredServicesList = arrayOf("Trash Pick-up","Broken Fence","Parking Stencils","Vehicle","Shopping Cart Removal","Parking Guidance System","Graffiti", "Charging Station","Other")


    private var mContext: Context? = null
    private var mDb: AppDatabase? = null
    private var mWelcomeFormData: WelcomeForm? = null
    private var mWelcomeListDataSet: WelcomeListDatatbase? = null
    private var mDatabaseWelcomeList: WelcomeList? = WelcomeList()
    private var selectedDeViceId = OfficerDeviceIdObject()
    private var picUri: Uri? = null
    private var tempUri: String? = null


    private var rootView: View? = null
    private var mAutoComTextViewBlock: AppCompatAutoCompleteTextView? = null
    private var mAppComTextViewBlock: AppCompatTextView? = null
    private var mLayoutCompatBlock: LinearLayoutCompat? = null
    private var textInputLayoutBlock: TextInputLayout? = null

    private var mAutoComTextViewStreet: AppCompatAutoCompleteTextView? = null
    private var mAppComTextViewStreet: AppCompatTextView? = null
    private var mLayoutCompatStreet: LinearLayoutCompat? = null
    private var textInputLayoutStreet: TextInputLayout? = null


    private var mAutoComTextViewDirection: AppCompatAutoCompleteTextView? = null
    private var mAppComTextViewDirection: AppCompatTextView? = null
    private var mLayoutCompatDirection: LinearLayoutCompat? = null
    private var textInputLayoutDirection: TextInputLayout? = null


    private var mAutoComTextViewDeviceId: AppCompatAutoCompleteTextView? = null
    private var mAppComTextViewDeviceId: AppCompatTextView? = null
    private var mLayoutCompatDeviceId: LinearLayoutCompat? = null
    private var textInputLayoutDeviceId: TextInputLayout? = null


    private var mAutoComTextViewMeterNo: AppCompatAutoCompleteTextView? = null
    private var mAppComTextViewMeterNo: AppCompatTextView? = null
    private var mLayoutCompatMeterNo: LinearLayoutCompat? = null
    private var textInputLayoutMeterNo: TextInputLayout? = null


    private var mAutoComTextViewLotArea: AppCompatAutoCompleteTextView? = null
    private var mAppComTextViewLotArea: AppCompatTextView? = null
    private var mLayoutCompatLotArea: LinearLayoutCompat? = null
    private var mInputLayoutLotArea: TextInputLayout? = null

    private var mAutoComTextViewVehicleRelocatedToDifferentStallWithinTheLot: AppCompatAutoCompleteTextView? = null
    private var mAppComTextViewVehicleRelocatedToDifferentStallWithinTheLot: AppCompatTextView? = null
    private var mLayoutCompatVehicleRelocatedToDifferentStallWithinTheLot: LinearLayoutCompat? = null
    private var textInputLayoutVehicleRelocatedToDifferentStallWithinTheLot: TextInputLayout? = null

    private var mAutoComTextViewVehicleStoredAt: AppCompatAutoCompleteTextView? = null
    private var mAppComTextViewVehicleStoredAt: AppCompatTextView? = null
    private var mLayoutCompatVehicleStoredAt: LinearLayoutCompat? = null
    private var mInputLayoutVehicleStoredAt: TextInputLayout? = null

    private var mAutoComTextViewReasonForTow: AppCompatAutoCompleteTextView? = null
    private var mAppComTextViewReasonForTow: AppCompatTextView? = null
    private var mLayoutCompatReasonForTow: LinearLayoutCompat? = null
    private var textInputLayoutReasonForTow: TextInputLayout? = null


    private var mAutoComTextViewTowFileNumber: AppCompatAutoCompleteTextView? = null
    private var mAppComTextViewTowFileNumber: AppCompatTextView? = null
    private var mLayoutCompatTowFileNumber: LinearLayoutCompat? = null
    private var mInputLayoutTowFileNumber: TextInputLayout? = null

    private var mAutoComTextViewLicensePlate: AppCompatAutoCompleteTextView? = null
    private var mAppComTextViewLicensePlate: AppCompatTextView? = null
    private var mLayoutCompatLicensePlate: LinearLayoutCompat? = null
    private var mInputLayoutLicensePlate: TextInputLayout? = null

    private var mAutoComTextViewVehicleWithInLot: AppCompatAutoCompleteTextView? = null
    private var mAppComTextViewVehicleWithInLot: AppCompatTextView? = null
    private var mLayoutCompatVehicleWithInLot: LinearLayoutCompat? = null
    private var mInputLayoutVehicleWithInLot: TextInputLayout? = null

    private var mAutoComTextViewVin: AppCompatAutoCompleteTextView? = null
    private var mAppComTextViewVin: AppCompatTextView? = null
    private var mLayoutCompatVin: LinearLayoutCompat? = null
    private var textInputLayoutVin: TextInputLayout? = null


    private var mAutoComTextViewState: AppCompatAutoCompleteTextView? = null
    private var mAppComTextViewState: AppCompatTextView? = null
    private var mLayoutCompatState: LinearLayoutCompat? = null
    private var textInputLayoutState: TextInputLayout? = null


    private var mAutoComTextViewMake: AppCompatAutoCompleteTextView? = null
    private var mAppComTextViewMake: AppCompatTextView? = null
    private var mLayoutCompatMake: LinearLayoutCompat? = null
    private var textInputLayoutMake: TextInputLayout? = null


    private var mAutoComTextViewModel: AppCompatAutoCompleteTextView? = null
    private var mAppComTextViewModel: AppCompatTextView? = null
    private var mLayoutCompatModel: LinearLayoutCompat? = null
    private var textInputLayoutModel: TextInputLayout? = null


    private var mAutoComTextViewColor: AppCompatAutoCompleteTextView? = null
    private var mAppComTextViewColor: AppCompatTextView? = null
    private var mLayoutCompatColor: LinearLayoutCompat? = null
    private var textInputLayoutColor: TextInputLayout? = null


    private var mAutoComTextViewZone: AppCompatAutoCompleteTextView? = null
    private var mAppComTextViewZone: AppCompatTextView? = null
    private var mLayoutCompatZone: LinearLayoutCompat? = null
    private var textInputLayoutZone: TextInputLayout? = null


    private var mAutoComTextViewPBCZone: AppCompatAutoCompleteTextView? = null
    private var mAppComTextViewPBCZone: AppCompatTextView? = null
    private var mLayoutCompatPBCZone: LinearLayoutCompat? = null
    private var textInputLayoutPBCZone: TextInputLayout? = null


    private var mAutoComTextViewMeterType: AppCompatAutoCompleteTextView? = null
    private var mAppComTextViewMeterType: AppCompatTextView? = null
    private var mLayoutCompatMeterType: LinearLayoutCompat? = null
    private var textInputLayoutMeterType: TextInputLayout? = null


    private var mAutoComTextViewMeterLabelVisible: AppCompatAutoCompleteTextView? = null
    private var mAppComTextViewMeterLabelVisible: AppCompatTextView? = null
    private var mLayoutCompatMeterLabelVisible: LinearLayoutCompat? = null
    private var textInputLayoutMeterLableVisible: TextInputLayout? = null


    private var mAutoComTextViewDigitalDisplayVisible: AppCompatAutoCompleteTextView? = null
    private var mAppComTextViewDigitalDisplayVisible: AppCompatTextView? = null
    private var mLayoutCompatDigitalDisplayVisible: LinearLayoutCompat? = null
    private var textInputLayoutDigitalDisplayVisible: TextInputLayout? = null


    private var mAutoComTextViewCoinJam: AppCompatAutoCompleteTextView? = null
    private var mAppComTextViewCoinJam: AppCompatTextView? = null
    private var mLayoutCompatCoinJam: LinearLayoutCompat? = null
    private var textInputLayoutCoinJam: TextInputLayout? = null


    private var mAutoComTextViewCreditCardJam: AppCompatAutoCompleteTextView? = null
    private var mAppComTextViewCreditCardJam: AppCompatTextView? = null
    private var mLayoutCompatCreditCardJam: LinearLayoutCompat? = null
    private var textInputLayoutCreditCardJam: TextInputLayout? = null


    private var mAutoComTextViewComments: TextInputEditText? = null
    private var mAppComTextViewComments: AppCompatTextView? = null
    private var mLayoutCompatComments: LinearLayoutCompat? = null
    private var textInputLayoutComments: TextInputLayout? = null


    private var mAutoComTextViewComment2: TextInputEditText? = null
    private var mAppComTextViewComment2: AppCompatTextView? = null
    private var mLayoutCompatComment2: LinearLayoutCompat? = null
    private var textInputLayoutComment2: TextInputLayout? = null


    private var mAutoComTextViewComment3: TextInputEditText? = null
    private var mAppComTextViewComment3: AppCompatTextView? = null
    private var mLayoutCompatComment3: LinearLayoutCompat? = null
    private var textInputLayoutComment3: TextInputLayout? = null


    private var mAutoComTextViewStaus: AppCompatAutoCompleteTextView? = null
    private var mAppComTextViewStaus: AppCompatTextView? = null
    private var mLayoutCompatStaus: LinearLayoutCompat? = null
    private var textInputLayoutStatus: TextInputLayout? = null


    private var mAutoComTextViewEnforceable: AppCompatAutoCompleteTextView? = null
    private var mAppComTextViewEnforceable: AppCompatTextView? = null
    private var mLayoutCompatEnforceable: LinearLayoutCompat? = null
    private var textInputLayoutEnforceable: TextInputLayout? = null


    private var mAutoComTextViewCurbColor: AppCompatAutoCompleteTextView? = null
    private var mAppComTextViewCurbColor: AppCompatTextView? = null
    private var mLayoutCompatCurbColor: LinearLayoutCompat? = null
    private var textInputLayoutCurbColor: TextInputLayout? = null


    private var mAutoComTextViewDate: AppCompatAutoCompleteTextView? = null
    private var mAppComTextViewDate: AppCompatTextView? = null
    private var mLayoutCompatDate: LinearLayoutCompat? = null
    private var textInputLayoutDate: TextInputLayout? = null


    private var mAutoComTextViewDutyHours: AppCompatAutoCompleteTextView? = null
    private var mAppComTextViewDutyHours: AppCompatTextView? = null
    private var mLayoutCompatDutyHours: LinearLayoutCompat? = null
    private var mInputLayoutDutyHours: TextInputLayout? = null

    private var mAutoComTextViewOfficer: AppCompatAutoCompleteTextView? = null
    private var mAppComTextViewOfficer: AppCompatTextView? = null
    private var mLayoutCompatOfficer: LinearLayoutCompat? = null
    private var textInputLayoutOfficer: TextInputLayout? = null


    private var mAutoComTextViewLunchTaken: AppCompatAutoCompleteTextView? = null
    private var mAppComTextViewLunchTaken: AppCompatTextView? = null
    private var mLayoutCompatLunchTaken: LinearLayoutCompat? = null
    private var textInputLayoutLunchTaken: TextInputLayout? = null


    private var mAutoComTextViewFirst10MinBreak: AppCompatAutoCompleteTextView? = null
    private var mAppComTextViewFirst10MinBreak: AppCompatTextView? = null
    private var mLayoutCompatFirst10MinBreak: LinearLayoutCompat? = null
    private var textInputLayoutFirst10MinBreak: TextInputLayout? = null


    private var mAutoComTextViewSecond10MinBreak: AppCompatAutoCompleteTextView? = null
    private var mAppComTextViewSecond10MinBreak: AppCompatTextView? = null
    private var mLayoutCompatSecond10MinBreak: LinearLayoutCompat? = null
    private var textInputLayoutSecond10MinBreak: TextInputLayout? = null


    private var mAutoComTextViewUnitNo: AppCompatAutoCompleteTextView? = null
    private var mAppComTextViewUnitNo: AppCompatTextView? = null
    private var mLayoutCompatUnitNo: LinearLayoutCompat? = null
    private var textInputLayoutUnitNo: TextInputLayout? = null


    private var mAutoComTextViewAssignedArea: AppCompatAutoCompleteTextView? = null
    private var mAppComTextViewAssignedArea: AppCompatTextView? = null
    private var mLayoutCompatAssignedArea: LinearLayoutCompat? = null
    private var textInputLayoutAssignedArea: TextInputLayout? = null


    private var mAutoComTextViewViolationDescription: AppCompatAutoCompleteTextView? = null
    private var mAppComTextViewViolationDescription: AppCompatTextView? = null
    private var mLayoutCompatViolationDescription: LinearLayoutCompat? = null
    private var textInputLayoutViolationDescription: TextInputLayout? = null


//    private var mAutoComTextViewOssiDeviceNo: AppCompatAutoCompleteTextView? = null
//    private var mAppComTextViewOssiDeviceNo: AppCompatTextView? = null
//    private var mLayoutCompatOssiDeviceNo: LinearLayoutCompat? = null

    private var mAutoComTextViewHandheldUnitNo: AppCompatAutoCompleteTextView? = null
    private var mAppComTextViewHandheldUnitNo: AppCompatTextView? = null
    private var mLayoutCompatHandheldUnitNo: LinearLayoutCompat? = null
    private var textInputLayoutHandheldUnitNo: TextInputLayout? = null


    private var mAutoComTextViewCitationsIssued: AppCompatAutoCompleteTextView? = null
    private var mAppComTextViewCitationsIssued: AppCompatTextView? = null
    private var mLayoutCompatCitationsIssued: LinearLayoutCompat? = null
    private var mInputLayoutCitationsIssued: TextInputLayout? = null

    private var mAutoComTextViewSpecialEnforcementRequest: AppCompatAutoCompleteTextView? = null
    private var mAppComTextViewSpecialEnforcementRequest: AppCompatTextView? = null
    private var mLayoutCompatSpecialEnforcementRequest: LinearLayoutCompat? = null
    private var mInputLayoutSpecialEnforcementRequest: TextInputLayout? = null

    private var mAutoComTextViewGarageClearance: AppCompatAutoCompleteTextView? = null
    private var mAppComTextViewGarageClearance: AppCompatTextView? = null
    private var mLayoutCompatGarageClearance: LinearLayoutCompat? = null
    private var mInputLayoutGarageClearance: TextInputLayout? = null

    private var mAutoComTextViewFileNumber: AppCompatAutoCompleteTextView? = null
    private var mAppComTextViewFileNumber: AppCompatTextView? = null
    private var mLayoutCompatFileNumber: LinearLayoutCompat? = null
    private var mInputLayoutFileNumber: TextInputLayout? = null

    private var mAutoComTextViewTriedToRestartHandHeld: AppCompatAutoCompleteTextView? = null
    private var mAppComTextViewTriedToRestartHandHeld: AppCompatTextView? = null
    private var mLayoutCompatTriedToRestartHandHeld: LinearLayoutCompat? = null
    private var textInputLayoutTriedToRestartHandheld: TextInputLayout? = null

    private var mAutoComTextViewPrintingCorrectly: AppCompatAutoCompleteTextView? = null
    private var mAppComTextViewPrintingCorrectly: AppCompatTextView? = null
    private var mLayoutCompatPrintingCorrectly: LinearLayoutCompat? = null
    private var textInputLayoutPrintingCorrectly: TextInputLayout? = null

    private var mAutoComTextViewOverHeating: AppCompatAutoCompleteTextView? = null
    private var mAppComTextViewOverHeating: AppCompatTextView? = null
    private var mLayoutCompatOverHeating: LinearLayoutCompat? = null
    private var textInputLayoutOverHeating: TextInputLayout? = null

    private var mAutoComTextViewBatteryHoldCharge: AppCompatAutoCompleteTextView? = null
    private var mAppComTextViewBatteryHoldCharge: AppCompatTextView? = null
    private var mLayoutCompatBatteryHoldCharge: LinearLayoutCompat? = null
    private var textInputLayoutBatteryHoldCharge: TextInputLayout? = null

    private var mAutoComTextViewInternetConnectivity: AppCompatAutoCompleteTextView? = null
    private var mAppComTextViewInternetConnectivity: AppCompatTextView? = null
    private var mLayoutCompatInternetConnectivity: LinearLayoutCompat? = null
    private var textInputLayoutInternetConnectivity: TextInputLayout? = null

    private var mAutoComTextViewDescribeHandHeldMalfunctionInDetail: AppCompatAutoCompleteTextView? = null
    private var mAppComTextViewDescribeHandHeldMalfunctionInDetail: AppCompatTextView? = null
    private var mLayoutCompatDescribeHandHeldMalfunctionInDetail: LinearLayoutCompat? = null
    private var mInputLayoutDescribeHandHeldMalfunctionInDetail: TextInputLayout? = null

    private var mAutoComTextViewGraffiti: AppCompatAutoCompleteTextView? = null
    private var mAppComTextViewGraffiti: AppCompatTextView? = null
    private var mLayoutCompatGraffiti: LinearLayoutCompat? = null
    private var textInputLayoutGraffiti: TextInputLayout? = null

    private var mAutoComTextViewMissingSign: AppCompatAutoCompleteTextView? = null
    private var mAppComTextViewMissingSign: AppCompatTextView? = null
    private var mLayoutCompatMissingSign: LinearLayoutCompat? = null
    private var textInputLayoutMissingSign: TextInputLayout? = null

    private var mAutoComTextViewVehicle: AppCompatAutoCompleteTextView? = null
    private var mAppComTextViewVehicle: AppCompatTextView? = null
    private var mLayoutCompatVehicle: LinearLayoutCompat? = null
    private var textInputLayoutVehicle: TextInputLayout? = null

    private var mAutoComTextViewVehicleMark: AppCompatAutoCompleteTextView? = null
    private var mAppComTextViewVehicleMark: AppCompatTextView? = null
    private var mLayoutCompatVehicleMark: LinearLayoutCompat? = null
    private var mInputLayoutVehicleMark: TextInputLayout? = null

    private var mAutoComTextViewVehicleCondition: AppCompatAutoCompleteTextView? = null
    private var mAppComTextViewVehicleCondition: AppCompatTextView? = null
    private var mLayoutCompatVehicleCondition: LinearLayoutCompat? = null
    private var mInputLayoutVehicleCondition: TextInputLayout? = null

    private var mAutoComTextViewOfficerPhoneNumber: AppCompatAutoCompleteTextView? = null
    private var mAppComTextViewOfficerPhoneNumber: AppCompatTextView? = null
    private var mLayoutCompatOfficerPhoneNumber: LinearLayoutCompat? = null
    private var mInputLayoutOfficerPhoneNumber: TextInputLayout? = null

    private var mAutoComTextViewTimeMark: AppCompatAutoCompleteTextView? = null
    private var mAppComTextViewTimeMark: AppCompatTextView? = null
    private var mLayoutCompatTimeMark: LinearLayoutCompat? = null
    private var mInputLayoutTimeMark: TextInputLayout? = null

    private var mAutoComTextViewTimeMark2: AppCompatAutoCompleteTextView? = null
    private var mAppComTextViewTimeMark2: AppCompatTextView? = null
    private var mLayoutCompatTimeMark2: LinearLayoutCompat? = null
    private var mInputLayoutTimeMark2: TextInputLayout? = null

    private var mAutoComTextViewTimeMark3: AppCompatAutoCompleteTextView? = null
    private var mAppComTextViewTimeMark3: AppCompatTextView? = null
    private var mLayoutCompatTimeMark3: LinearLayoutCompat? = null
    private var mInputLayoutTimeMark3: TextInputLayout? = null

    private var mAutoComTextViewTimeMark4: AppCompatAutoCompleteTextView? = null
    private var mAppComTextViewTimeMark4: AppCompatTextView? = null
    private var mLayoutCompatTimeMark4: LinearLayoutCompat? = null
    private var mInputLayoutTimeMark4: TextInputLayout? = null

    private var mAutoComTextViewStartingMileage: AppCompatAutoCompleteTextView? = null
    private var mAppComTextViewStartingMileage: AppCompatTextView? = null
    private var mLayoutCompatStartingMileage: LinearLayoutCompat? = null
    private var textInputLayoutStartingMileage: TextInputLayout? = null

    private var mAutoComTextViewGasLevel: AppCompatAutoCompleteTextView? = null
    private var mAppComTextViewGasLevel: AppCompatTextView? = null
    private var mLayoutCompatGasLevel: LinearLayoutCompat? = null
    private var textInputLayoutGasLevel: TextInputLayout? = null

    private var mAutoComTextViewLightBar: AppCompatAutoCompleteTextView? = null
    private var mAppComTextViewLightBar: AppCompatTextView? = null
    private var mLayoutCompatLightBar: LinearLayoutCompat? = null
    private var textInputLayoutLightBar: TextInputLayout? = null

    private var mAutoComTextViewDashBoardIndications: AppCompatAutoCompleteTextView? = null
    private var mAppComTextViewDashBoardIndications: AppCompatTextView? = null
    private var mLayoutCompatDashBoardIndications: LinearLayoutCompat? = null
    private var textInputLayoutDashBoardIndications: TextInputLayout? = null


    private var mAutoComTextViewSeatBeltOperational: AppCompatAutoCompleteTextView? = null
    private var mAppComTextViewSeatBeltOperational: AppCompatTextView? = null
    private var mLayoutCompatSeatBeltOperational: LinearLayoutCompat? = null
    private var textInputLayoutSeatBeltOperational: TextInputLayout? = null


    private var mAutoComTextViewBrakes: AppCompatAutoCompleteTextView? = null
    private var mAppComTextViewBrakes: AppCompatTextView? = null
    private var mLayoutCompatBrakes: LinearLayoutCompat? = null
    private var textInputLayoutBrakes: TextInputLayout? = null


    private var mAutoComTextViewBrakeLights: AppCompatAutoCompleteTextView? = null
    private var mAppComTextViewBrakeLights: AppCompatTextView? = null
    private var mLayoutCompatBrakeLights: LinearLayoutCompat? = null
    private var textInputLayoutBrakeLights: TextInputLayout? = null


    private var mAutoComTextViewHeadLights: AppCompatAutoCompleteTextView? = null
    private var mAppComTextViewHeadLights: AppCompatTextView? = null
    private var mLayoutCompatHeadLights: LinearLayoutCompat? = null
    private var textInputLayoutHeadLights: TextInputLayout? = null


    private var mAutoComTextViewTurnSignals: AppCompatAutoCompleteTextView? = null
    private var mAppComTextViewTurnSignals: AppCompatTextView? = null
    private var mLayoutCompatTurnSignals: LinearLayoutCompat? = null
    private var textInputLayoutSignals: TextInputLayout? = null


    private var mAutoComTextViewSteeringWheelOperational: AppCompatAutoCompleteTextView? = null
    private var mAppComTextViewSteeringWheelOperational: AppCompatTextView? = null
    private var mLayoutCompatSteeringWheelOperational: LinearLayoutCompat? = null
    private var textInputLayoutSteeringWheelOperational: TextInputLayout? = null

    private var mAutoComTextViewWindshieldVisibility: AppCompatAutoCompleteTextView? = null
    private var mAppComTextViewWindshieldVisibility: AppCompatTextView? = null
    private var mLayoutCompatWindshieldVisibility: LinearLayoutCompat? = null
    private var textInputLayoutWindshieldVisibility: TextInputLayout? = null


    private var mAutoComTextViewSideAndRearViewMirrorsOperational: AppCompatAutoCompleteTextView? = null
    private var mAppComTextViewSideAndRearViewMirrorsOperational: AppCompatTextView? = null
    private var mLayoutCompatSideAndRearViewMirrorsOperational: LinearLayoutCompat? = null
    private var textInputLayoutSideAndRearViewMirrorsOperational: TextInputLayout? = null


    private var mAutoComTextViewWindshieldWipersOperational: AppCompatAutoCompleteTextView? = null
    private var mAppComTextViewWindshieldWipersOperational: AppCompatTextView? = null
    private var mLayoutCompatWindshieldWipersOperational: LinearLayoutCompat? = null
    private var textInputLayoutWindshieldWipersOperational: TextInputLayout? = null


    private var mAutoComTextViewVehicleRegistrationAndInsurance: AppCompatAutoCompleteTextView? = null
    private var mAppComTextViewVehicleRegistrationAndInsurance: AppCompatTextView? = null
    private var mLayoutCompatVehicleRegistrationAndInsurance: LinearLayoutCompat? = null
    private var textInputLayoutVehicleRegistrationAndInsurance: TextInputLayout? = null


    private var mAutoComTextViewConesSixPerVehicle: AppCompatAutoCompleteTextView? = null
    private var mAppComTextViewConesSixPerVehicle: AppCompatTextView? = null
    private var mLayoutCompatConesSixPerVehicle: LinearLayoutCompat? = null
    private var textInputLayoutConesSixPerVehicle: TextInputLayout? = null


    private var mAutoComTextViewFirstAidKit: AppCompatAutoCompleteTextView? = null
    private var mAppComTextViewFirstAidKit: AppCompatTextView? = null
    private var mLayoutCompatFirstAidKit: LinearLayoutCompat? = null
    private var textInputLayoutFirstAidKit: TextInputLayout? = null


    private var mAutoComTextViewHorn: AppCompatAutoCompleteTextView? = null
    private var mAppComTextViewHorn: AppCompatTextView? = null
    private var mLayoutCompatHorn: LinearLayoutCompat? = null
    private var textInputLayoutHorn: TextInputLayout? = null


    private var mAutoComTextViewInteriorCleanliness: AppCompatAutoCompleteTextView? = null
    private var mAppComTextViewInteriorCleanliness: AppCompatTextView? = null
    private var mLayoutCompatInteriorCleanliness: LinearLayoutCompat? = null
    private var mInputLayoutInteriorCleanliness: TextInputLayout? = null

    private var mAutoComTextViewExteriorCleanliness: AppCompatAutoCompleteTextView? = null
    private var mAppComTextViewExteriorCleanliness: AppCompatTextView? = null
    private var mLayoutCompatExteriorCleanliness: LinearLayoutCompat? = null
    private var textInputLayoutExteriorCleanliness: TextInputLayout? = null


    private var mAutoComTextViewLprLensFreeOfDebris: AppCompatAutoCompleteTextView? = null
    private var mAppComTextViewLprLensFreeOfDebris: AppCompatTextView? = null
    private var mLayoutCompatLprLensFreeOfDebris: LinearLayoutCompat? = null
    private var textInputLayoutLprLensFreeOfDebris: TextInputLayout? = null

    private var mAutoComTextViewVisibleLeaks: AppCompatAutoCompleteTextView? = null
    private var mAppComTextViewVisibleLeaks: AppCompatTextView? = null
    private var mLayoutCompatVisibleLeaks: LinearLayoutCompat? = null
    private var textInputLayoutVisibleLeaks: TextInputLayout? = null

    private var mAutoComTextViewTiresVisualInspection: AppCompatAutoCompleteTextView? = null
    private var mAppComTextViewTiresVisualInspection: AppCompatTextView? = null
    private var mLayoutCompatTiresVisualInspection: LinearLayoutCompat? = null
    private var textInputLayoutTiresVisualInspection: TextInputLayout? = null

    //    Bike Inspection
    private var mAutoComTextViewTirePressure: AppCompatAutoCompleteTextView? = null
    private var mAppComTextViewTirePressure: AppCompatTextView? = null
    private var mLayoutCompatTirePressure: LinearLayoutCompat? = null
    private var textInputLayoutTirePressure: TextInputLayout? = null

    private var mAutoComTextViewAssignedBike: AppCompatAutoCompleteTextView? = null
    private var mAppComTextViewAssignedBike: AppCompatTextView? = null
    private var mLayoutCompatAssignedBike: LinearLayoutCompat? = null
    private var textInputLayoutAssignedBike: TextInputLayout? = null

    private var mAutoComTextViewBreaksRotors: AppCompatAutoCompleteTextView? = null
    private var mAppComTextViewBreaksRotors: AppCompatTextView? = null
    private var mLayoutCompatBreaksRotors: LinearLayoutCompat? = null
    private var textInputLayoutBreakRotors: TextInputLayout? = null

    private var mAutoComTextViewLightsAndReflectors: AppCompatAutoCompleteTextView? = null
    private var mAppComTextViewLightsAndReflectors: AppCompatTextView? = null
    private var mLayoutCompatLightsAndReflectors: LinearLayoutCompat? = null
    private var textInputLayoutLightsAndReflectors: TextInputLayout? = null

    private var mAutoComTextViewChainCrank: AppCompatAutoCompleteTextView? = null
    private var mAppComTextViewChainCrank: AppCompatTextView? = null
    private var mLayoutCompatChainCrank: LinearLayoutCompat? = null
    private var textInputLayoutChainCrank: TextInputLayout? = null

    private var mAutoComTextViewBatteryFreeOfDebris: AppCompatAutoCompleteTextView? = null
    private var mAppComTextViewBatteryFreeOfDebris: AppCompatTextView? = null
    private var mLayoutCompatBatteryFreeOfDebris: LinearLayoutCompat? = null
    private var textInputLayoutBatteryFreeDebris: TextInputLayout? = null

    private var mAutoComTextViewFlatPack: AppCompatAutoCompleteTextView? = null
    private var mAppComTextViewFlatPack: AppCompatTextView? = null
    private var mLayoutCompatFlatPack: LinearLayoutCompat? = null
    private var textInputLayoutFlatPack: TextInputLayout? = null

    private var mAutoComTextViewTotalEnforcementPersonnel: AppCompatAutoCompleteTextView? = null
    private var mAppComTextViewTotalEnforcementPersonnel: AppCompatTextView? = null
    private var mLayoutCompatTotalEnforcementPersonnel: LinearLayoutCompat? = null
    private var textInputLayoutTotalEnforcementPersonnel: TextInputLayout? = null

    private var mAutoComTextViewComplaintsTowardsOfficer: AppCompatAutoCompleteTextView? = null
    private var mAppComTextViewComplaintsTowardsOfficer: AppCompatTextView? = null
    private var mLayoutCompatComplaintsTowardsOfficer: LinearLayoutCompat? = null
    private var mInputLayoutComplaintsTowardsOfficer: TextInputLayout? = null

    private var mAutoComTextViewResidentComplaints: AppCompatAutoCompleteTextView? = null
    private var mAppComTextViewResidentComplaints: AppCompatTextView? = null
    private var mLayoutCompatResidentComplaints: LinearLayoutCompat? = null
    private var mInputLayoutResidentComplaints: TextInputLayout? = null

    private var mAutoComTextViewWarningsIssued: AppCompatAutoCompleteTextView? = null
    private var mAppComTextViewWarningsIssued: AppCompatTextView? = null
    private var mLayoutCompatWarningsIssued: LinearLayoutCompat? = null
    private var mInputLayoutWarningsIssued: TextInputLayout? = null

    private var mAutoComTextViewRo: AppCompatAutoCompleteTextView? = null
    private var mAppComTextViewRo: AppCompatTextView? = null
    private var mLayoutCompatRo: LinearLayoutCompat? = null
    private var mInputLayoutRo: TextInputLayout? = null

    private var mAutoComTextViewDriverSideComment: AppCompatAutoCompleteTextView? = null
    private var mAppComTextViewDriverSideComment: AppCompatTextView? = null
    private var mLayoutCompatDriverSideComment: LinearLayoutCompat? = null
    private var mInputLayoutDriverSideComment: TextInputLayout? = null

    private var mAutoComTextViewPassengerSideComment: AppCompatAutoCompleteTextView? = null
    private var mAppComTextViewPassengerSideComment: AppCompatTextView? = null
    private var mLayoutCompatPassengerSideComment: LinearLayoutCompat? = null
    private var mInputLayoutPassengerSideComment: TextInputLayout? = null

    private var mAutoComTextViewFrontSideComment: AppCompatAutoCompleteTextView? = null
    private var mAppComTextViewFrontSideComment: AppCompatTextView? = null
    private var mLayoutCompatFrontSideComment: LinearLayoutCompat? = null
    private var mInputLayoutFrontSideComment: TextInputLayout? = null

    private var mAutoComTextViewRearSideComment: AppCompatAutoCompleteTextView? = null
    private var mAppComTextViewRearSideComment: AppCompatTextView? = null
    private var mLayoutCompatRearSideComment: LinearLayoutCompat? = null
    private var mInputLayoutRearSideComment: TextInputLayout? = null

    private var mAutoComTextViewTrailerComment: AppCompatAutoCompleteTextView? = null
    private var mAppComTextViewTrailerComment: AppCompatTextView? = null
    private var mLayoutCompatTrailerComment: LinearLayoutCompat? = null
    private var mInputLayoutTrailerComment: TextInputLayout? = null

    private var mAutoComTextViewBikeGlassess: AppCompatAutoCompleteTextView? = null
    private var mAppComTextViewBikeGlassess: AppCompatTextView? = null
    private var mLayoutCompatBikeGlassess: LinearLayoutCompat? = null
    private var textInputLayoutGlasses: TextInputLayout? = null

    private var mAutoComTextViewBatteryCharge: AppCompatAutoCompleteTextView? = null
    private var mAppComTextViewBatteryCharge: AppCompatTextView? = null
    private var mLayoutCompatBatteryCharge: LinearLayoutCompat? = null
    private var textInputLayoutBatteryCharge: TextInputLayout? = null

    private var mAutoComTextViewGloveVisualInspection: AppCompatAutoCompleteTextView? = null
    private var mAppComTextViewGloveVisualInspection: AppCompatTextView? = null
    private var mLayoutCompatGloveVisualInspection: LinearLayoutCompat? = null
    private var textInputLayoutGloveVisualInspection: TextInputLayout? = null

    private var mAutoComTextViewCarCount: AppCompatAutoCompleteTextView? = null
    private var mAppComTextViewCarCount: AppCompatTextView? = null
    private var mLayoutCompatCarCount: LinearLayoutCompat? = null
    private var mInputLayoutCarCount: TextInputLayout? = null

    private var mAutoComTextViewEmptySpace: AppCompatAutoCompleteTextView? = null
    private var mAppComTextViewEmptySpace: AppCompatTextView? = null
    private var mLayoutCompatEmptySpace: LinearLayoutCompat? = null
    private var mInputLayoutEmptySpace: TextInputLayout? = null

    private var mAutoComTextViewNumberOfViolatingVehicles: AppCompatAutoCompleteTextView? = null
    private var mAppComTextViewNumberOfViolatingVehicles: AppCompatTextView? = null
    private var mLayoutCompatNumberOfViolatingVehicles: LinearLayoutCompat? = null
    private var mInputLayoutNumberOfViolatingVehicles: TextInputLayout? = null

    private var mAutoComTextViewViolationRate: AppCompatAutoCompleteTextView? = null
    private var mAppComTextViewViolationRate: AppCompatTextView? = null
    private var mLayoutCompatViolationRate: LinearLayoutCompat? = null
    private var mInputLayoutViolationRate: TextInputLayout? = null

    private var mAutoComTextViewSecurityObservation: AppCompatAutoCompleteTextView? = null
    private var mAppComTextViewSecurityObservation: AppCompatTextView? = null
    private var mLayoutCompatSecurityObservation: LinearLayoutCompat? = null
    private var mInputLayoutSecurityObservation: TextInputLayout? = null

    private var mAutoComTextViewSafetyIssue: AppCompatAutoCompleteTextView? = null
    private var mAppComTextViewSafetyIssue: AppCompatTextView? = null
    private var mLayoutCompatSafetyIssue: LinearLayoutCompat? = null
    private var textInputLayoutSafetyIssue: TextInputLayout? = null


    private var mAutoComTextViewRequiredServices: AppCompatAutoCompleteTextView? = null
    private var mAppComTextViewRequiredServices: AppCompatTextView? = null
    private var mLayoutCompatRequiredServices: LinearLayoutCompat? = null
    private var textInputLayoutRequiredServices: TextInputLayout? = null


    private var mAutoComTextViewField1: AppCompatAutoCompleteTextView? = null
    private var mAppComTextViewField1: AppCompatTextView? = null
    private var mLayoutCompatField1: LinearLayoutCompat? = null
    private var mInputLayoutField1: TextInputLayout? = null

    private var mAutoComTextViewField2: AppCompatAutoCompleteTextView? = null
    private var mAppComTextViewField2: AppCompatTextView? = null
    private var mLayoutCompatField2: LinearLayoutCompat? = null
    private var mInputLayoutField2: TextInputLayout? = null

    private var mAutoComTextViewField3: AppCompatAutoCompleteTextView? = null
    private var mAppComTextViewField3: AppCompatTextView? = null
    private var mLayoutCompatField3: LinearLayoutCompat? = null
    private var mInputLayoutField3: TextInputLayout? = null

    private var mAutoComTextViewField4: AppCompatAutoCompleteTextView? = null
    private var mAppComTextViewField4: AppCompatTextView? = null
    private var mLayoutCompatField4: LinearLayoutCompat? = null
    private var mInputLayoutField4: TextInputLayout? = null

    private var mAutoComTextViewField5: AppCompatAutoCompleteTextView? = null
    private var mAppComTextViewField5: AppCompatTextView? = null
    private var mLayoutCompatField5: LinearLayoutCompat? = null
    private var mInputLayoutField5: TextInputLayout? = null

    private var mAutoComTextViewField6: AppCompatAutoCompleteTextView? = null
    private var mAppComTextViewField6: AppCompatTextView? = null
    private var mLayoutCompatField6: LinearLayoutCompat? = null
    private var mInputLayoutField6: TextInputLayout? = null

    private var mAutoComTextViewLine: AppCompatAutoCompleteTextView? = null
    private var mAppComTextViewLine: AppCompatTextView? = null
    private var mLayoutCompatLine: LinearLayoutCompat? = null
    private var mInputLayoutLine: TextInputLayout? = null

    private var mAutoComTextViewTowingOfficer: AppCompatAutoCompleteTextView? = null
    private var mAppComTextViewTowingOfficer: AppCompatTextView? = null
    private var mLayoutCompatTowingOfficer: LinearLayoutCompat? = null
    private var textInputLayoutTowingOfficer: TextInputLayout? = null



    private var bannerList: MutableList<CitationImagesModel?>? = ArrayList()
    private var mBannerAdapter: ViewPagerBannerAdapterWithDefaultImage? = null
    private var mShowBannerCount = 0
    private var mDotsCount = 0
    private var mDots: Array<ImageView?>? = null
    private var imageValidationCount = 0;
    private var cameraCount = 0;
    private var minimumImageRequired = 1;
    private var mImagesLinks: MutableList<String> = ArrayList()
    private val myCalendar = Calendar.getInstance()
    var violationDescription: MutableList<String>  = ArrayList()

    companion object {
        private const val REQUEST_CAMERA = 0
        private const val PERMISSION_REQUEST_CODE = 2
    }

    private val brokenMeterReportViewModel: BrokenMeterReportsViewModel? by viewModels()
    private val curbReportViewModel: CurbReportViewModel? by viewModels()
    private val fullTimeReportViewModel: FullTimeReportViewModel? by viewModels()
    private val partTimeReportViewModel: PartTimeReportViewModel? by viewModels()
    private val handHeldMalFunctionsViewModel : HandHeldMalFunctionsViewModel? by viewModels()
    private val signReportViewModel : SignReportViewModel? by viewModels()
    private val vehicleInspectionViewModel : VehicleInspectionViewModel? by viewModels()
    private val hourMarkedVehiclesViewModel : HourMarkedVehiclesViewModel? by viewModels()
    private val bikeInspectionViewModel : BikeInspectionViewModel? by viewModels()
    private val supervisorReportsViewModel : SupervisorReportsViewModel? by viewModels()
    private val signOffReportViewModel : SignOffReportViewModel? by viewModels()
    private val noticeToTowViewModel : NoticeToTowViewModel? by viewModels()
    private val towReportViewModel : TowReportViewModel? by viewModels()
    private val reportServicesViewModel : ReportServicesViewModel? by viewModels()
    private val mUploadImageViewModel: UploadImageViewModel? by viewModels()

    private var mSelectedMake: String? = ""
    private var mSelectedMakeValue: String? = ""
    private var mSelectedModel: String? = ""
    private var mSelecteModelLookupCode: String? = ""
    private var mSelectedColor: String? = ""
    private var mState2DigitCode: String? = ""
    private val mModelList: MutableList<DatasetResponse>? = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_report)
        setFullScreenUI()
        ButterKnife.bind(this)
        addObservers()
        showProgressLoader(getString(R.string.scr_message_please_wait))
        mViewPagerBanner.visibility = View.VISIBLE

        if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BURBANK, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WESTCHESTER, ignoreCase = true)
        ) {
            mDeviceList = arrayOf("B 01", "B 02", "B 03", "B 04", "B 05", "B 06")
        }
        if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAMETRO, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CORPUSCHRISTI, ignoreCase = true)
        ) {
            mDeviceList = arrayOf("SP10", "SP11", "SP12", "SP13", "SP14", "SP15")
        }
        if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BURBANK, ignoreCase = true)) {
            mAssignedBikeList = arrayOf(" BK-01", " BK-02")
        }
        if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLENDALE, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLENDALE_POLICE, ignoreCase = true)) {
            mUnitNumberList = arrayOf(
                "BK M",
                "BK L",
                "BK M1",
                "BK L1",
                "BK M2",
                "BK L2",
                "101",
                "102",
                "103",
                "104",
                "105",
                "106",
                "107",
                "108",
                "109",
                "110"
            )
        }

        init()
        removeTimingImagesFromFolder()

//        Bundle bundle = new Bundle();
//        bundle.putString("SCREEN_NAME", "BrokenMeter");
//        bundle.putString("API_NAME", "BrokenMeter API");
//        bundle.putString("REQUEST", "BrokenMeter REQUEST ");
//        bundle.putString("RESPONSE", "BrokenMeter RESPONSE");
//        getInstanceOfAnalytics(bundle, "BROKENAPI");

        mainScope.launch {
            mBannerAdapter = ViewPagerBannerAdapterWithDefaultImage(
                    this@AllReportActivity,
                    object : ViewPagerBannerAdapterWithDefaultImage.ListItemSelectListener {
                        override fun onItemClick(position: Int) {
//                            setCameraImages()
                            if(fromForm.equals("Sign Off Report")||
                                (fromForm.equals("PCH Daily Updates") && position ==7))
                            {
                                setSignatureView()
                            }else {
                                requestPermission()
                            }
                        }
                    })
            setDefaultImages()
        }

        /**
         * Upload API LOG File whenever officer come to Report form
         */
//        callUploadAPILogsTextFile()

    }

    private fun init() {
        mContext = this
        mDb = BaseApplication.instance?.getAppDatabase()
        setToolbar()
        mWelcomeFormData = mDb?.dbDAO?.getWelcomeForm()
        mWelcomeListDataSet = Singleton.getWelcomeDbObject(mDb);
        if (mWelcomeListDataSet != null) {
            mDatabaseWelcomeList = mWelcomeListDataSet?.welcomeList
        }
         getIntentData();
        mEditTextDateTime?.text = AppUtils.getCurrentDateTimeforBoot("UI")
        clientTime = AppUtils.getCurrentDateTimeforBoot("Normal").trim().replace(" ", "T")
//        mainScope.launch {
            loadUIForm(fromForm);
            setDropDowns()
//        }
    }

    fun getIntentData()
    {
        if(intent.hasExtra("from_scr"))
          fromForm = intent.getStringExtra("from_scr").toString()
    }


    private val brokenMeterResponseObserver = Observer { apiResponse: ApiResponse? ->
        consumeResponse(apiResponse, DynamicAPIPath.POST_BROKEN_METER_REPORT)
    }
    private val fullTimeResponseObserver = Observer { apiResponse: ApiResponse? ->
        consumeResponse(apiResponse, DynamicAPIPath.POST_FULLTIME_REPORT)
    }

    private val partTimeResponseObserver = Observer { apiResponse: ApiResponse? ->
        consumeResponse(apiResponse, DynamicAPIPath.POST_PARTTIME_REPORT)
    }

    private val ResponsehandHeldMalFunctionsbserver = Observer { apiResponse: ApiResponse? ->
        consumeResponse(apiResponse, DynamicAPIPath.POST_HAND_HELD_MALFUNCTIONS_REPORT)
    }

    private val signReportObserver = Observer { apiResponse: ApiResponse? ->
        consumeResponse(apiResponse, DynamicAPIPath.POST_SIGN_REPORT)
    }

    private val vehicleInspectionReportObserver = Observer { apiResponse: ApiResponse? ->
        consumeResponse(apiResponse, DynamicAPIPath.POST_VEHICLE_INSPECTIONS_REPORT)
    }


    private val hourMarkedVehiclesReportObserver = Observer { apiResponse: ApiResponse? ->
        consumeResponse(apiResponse, DynamicAPIPath.POST_HOUR_MARKED_VEHICLE_REPORT)
    }

    private val supervisorReportObserver = Observer { apiResponse: ApiResponse? ->
        consumeResponse(apiResponse, DynamicAPIPath.POST_SUPERVISOR_REPORT)
    }
    private val signOffReportObserver = Observer { apiResponse: ApiResponse? ->
        consumeResponse(apiResponse, DynamicAPIPath.POST_SIGN_OFF_REPORT)
    }
    private val noticeToTowReportObserver = Observer { apiResponse: ApiResponse? ->
        consumeResponse(apiResponse, DynamicAPIPath.POST_NOTICE_TO_TOW_REPORT)
    }
    private val towReportObserver = Observer { apiResponse: ApiResponse? ->
        consumeResponse(apiResponse, DynamicAPIPath.POST_TOW_REPORT)
    }
    private val reportServicesObserver = Observer { apiResponse: ApiResponse? ->
        consumeResponse(apiResponse, DynamicAPIPath.POST_NFL_REPORT)
    }

    private val bikeInspectionsObserver = Observer { apiResponse: ApiResponse? ->
        consumeResponse(apiResponse, DynamicAPIPath.POST_BIKE_INSPECTIONS_REPORT)
    }

    private val curbResponseObserver = Observer { apiResponse: ApiResponse? ->
        consumeResponse(apiResponse, DynamicAPIPath.POST_CURB_REPORT)
    }

    private fun addObservers() {
        brokenMeterReportViewModel?.response?.observe(this, brokenMeterResponseObserver)
        curbReportViewModel?.response?.observe(this, curbResponseObserver)
        fullTimeReportViewModel?.response?.observe(this, fullTimeResponseObserver)
        partTimeReportViewModel?.response?.observe(this, partTimeResponseObserver)
        handHeldMalFunctionsViewModel?.response?.observe(this, ResponsehandHeldMalFunctionsbserver)
        signReportViewModel?.response?.observe(this, signReportObserver)
        vehicleInspectionViewModel?.response?.observe(this, vehicleInspectionReportObserver)
        hourMarkedVehiclesViewModel?.response?.observe(this, hourMarkedVehiclesReportObserver)
        bikeInspectionViewModel?.response?.observe(this, bikeInspectionsObserver)
        supervisorReportsViewModel?.response?.observe(this, supervisorReportObserver)
        signOffReportViewModel?.response?.observe(this, signOffReportObserver)
        noticeToTowViewModel?.response?.observe(this, noticeToTowReportObserver)
        towReportViewModel?.response?.observe(this, towReportObserver)
        reportServicesViewModel?.response?.observe(this, reportServicesObserver)
        mUploadImageViewModel?.uploadAllImagesAPIStatus?.observe(this, uploadScannedImagesAPIResponseObserver)
    }

    override fun removeObservers() {
        super.removeObservers()
        brokenMeterReportViewModel?.response?.removeObserver(brokenMeterResponseObserver)
        curbReportViewModel?.response?.removeObserver(curbResponseObserver)
        fullTimeReportViewModel?.response?.removeObserver(fullTimeResponseObserver)
        partTimeReportViewModel?.response?.removeObserver(partTimeResponseObserver)
        handHeldMalFunctionsViewModel?.response?.removeObserver(ResponsehandHeldMalFunctionsbserver)
        signReportViewModel?.response?.removeObserver(signReportObserver)
        vehicleInspectionViewModel?.response?.removeObserver(vehicleInspectionReportObserver)
        hourMarkedVehiclesViewModel?.response?.removeObserver(hourMarkedVehiclesReportObserver)
        bikeInspectionViewModel?.response?.removeObserver(bikeInspectionsObserver)
        supervisorReportsViewModel?.response?.removeObserver(supervisorReportObserver)
        signOffReportViewModel?.response?.removeObserver(signOffReportObserver)
        noticeToTowViewModel?.response?.removeObserver(noticeToTowReportObserver)
        towReportViewModel?.response?.removeObserver(towReportObserver)
        reportServicesViewModel?.response?.removeObserver(reportServicesObserver)
        mUploadImageViewModel?.uploadAllImagesAPIStatus?.observe(this, uploadScannedImagesAPIResponseObserver)
    }

    override fun onResume() {
        super.onResume()
        registerBroadcastReceiver()

    }


    //init toolbar navigation
    private fun setToolbar() {
//        if(imgOptions)
        initToolbar(
                2,
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

    /*perform click actions*/
    @OnClick(R.id.btn_submit,R.id.ivCameraBottom)
    fun onClick(view: View) {
        val id = view.id
        when (id) {
            R.id.btn_submit -> if (isFormValid(fromForm)) {
                setRequest(fromForm)
            }
            R.id.ivCameraBottom ->
                if(cameraCount< imageValidationCount) {
                    if(fromForm.equals("Sign Off Report")||
                        (fromForm.equals("PCH Daily Updates") &&  bannerList!!.size==7))
                    {
                        setSignatureView()
                    }else {
                        requestPermission()
                    }
            }else{
                LogUtil.printToastMSGForErrorWarning(
                        applicationContext,
                        getString(R.string.msg_min_image).replace(
                                "#",
                            minimumImageRequired.nullSafety().toString() + ""
                        ))
            }
        }
    }

    //request camera and storage permission
    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (PermissionUtils.requestCameraAndStoragePermission(this@AllReportActivity)) {
                cameraIntent()
            }
        } else {
            cameraIntent()
        }
    }

    private fun cameraIntent() {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            picUri = getOutputPhotoFile() //Uri.fromFile(getOutputPhotoFile());
            //tempUri=picUri;
            intent.putExtra(MediaStore.EXTRA_OUTPUT, picUri)
            //intent.putExtra("URI", picUri);
            startActivityForResult(intent, REQUEST_CAMERA)
    }

    private fun getOutputPhotoFile(): Uri? {
        val directory = File(
                Environment.getExternalStorageDirectory().absolutePath,
                Constants.FILE_NAME + Constants.ALLREPORTIMAGES
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

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CAMERA) {
            if (resultCode == RESULT_OK) {
                try {
                    ioScope.launch {
                        if(tempUri==null)
                        {
                            getOutputPhotoFile()
                        }
                        val file = File("$tempUri/IMG_temp.jpg")
                        var mImgaeBitmap: Bitmap? = null
                        try {
//                            LogUtil.printToastMSG(mContext,"options image")
                            val options = BitmapFactory.Options()
                            options.inSampleSize = 4
                            options.inJustDecodeBounds = true
                            BitmapFactory.decodeFile(file.absolutePath, options)

                            // Calculate inSampleSize
                            options.inSampleSize = Util.calculateInSampleSize(options, 300, 300)

                            // Decode bitmap with inSampleSize set
                            options.inJustDecodeBounds = false
                            val scaledBitmap = BitmapFactory.decodeFile(file.absolutePath, options)
//                            LogUtil.printToastMSG(mContext,"scaledBitmap")
                            //check the rotation of the image and display it properly
                            val exif: ExifInterface
                            exif = ExifInterface(file.absolutePath)
                            val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0)
//                            LogUtil.printToastMSG(mContext,"orientation "+ orientation)
                            val matrix = Matrix()
                            if (orientation == 6) {
                                matrix.postRotate(90f)
                            } else if (orientation == 3) {
                                matrix.postRotate(180f)
                            } else if (orientation == 8) {
                                matrix.postRotate(270f)
                            }
                            mImgaeBitmap = Bitmap.createBitmap(
                                    scaledBitmap,
                                    0,
                                    0,
                                    scaledBitmap.width,
                                    scaledBitmap.height,
                                    matrix,
                                    true
                            )
                            mainScope.async {
//                                val timeStampBitmap = AppUtils.timestampItAndSave(mImgaeBitmap);
                                SaveImageMM(mImgaeBitmap)
//                                SaveImageMM(mImgaeBitmap)
                            }

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    //TODO will add comments later
    private fun SaveImageMM(finalBitmap: Bitmap?) {
        val myDir = File(
                Environment.getExternalStorageDirectory().absolutePath,
                Constants.FILE_NAME + Constants.ALLREPORTIMAGES
        )
        myDir.mkdirs()
        val timeStamp = SimpleDateFormat("yyyMMdd_HHmmss", Locale.US).format(Date())
        val fname = "Image_" + timeStamp + "_report.jpg"
        val file = File(myDir, fname)
//        LogUtil.printToastMSG(mContext,"path "+ fname)
        if (file.exists()) file.delete()
        try {
            val out = FileOutputStream(file)
            finalBitmap!!.compress(Bitmap.CompressFormat.JPEG, 70, out) //20less than 300 kb
            out.flush()
            out.close()
            val oldFname = "IMG_temp.jpg"
            val oldFile = File(myDir, oldFname)
            if (oldFile.exists()) oldFile.delete()

            val id = SimpleDateFormat("HHmmss", Locale.US).format(Date())
            //save image to db
            val pathDb = file.path
            val mImage = CitationImagesModel()
            mImage.status = 1
            mImage.citationImage = pathDb
            mImage.id = id.toInt()
//            bannerList?.add(mImage)

            bannerList?.set(cameraCount,mImage)
            showImagesBanner()
            finalBitmap.recycle()
            cameraCount++
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setDefaultImages()
    {
        if(fromForm.equals("Curb",ignoreCase = true)) {
//            Front Photo, Back Photo, Passenger Side Photo, Driver Side Photo.
            mCameraImagesLayout!!.visibility = View.VISIBLE
            imageValidationCount = 1
            minimumImageRequired = 1
            val mImage = CitationImagesModel()
            mImage.status = 0
            mImage.citationImage = "Curb Photo"
            mImage.id = 101
            bannerList?.add(mImage)
        }else if(fromForm.equals("Sign Report",ignoreCase = true))
        {
            mCameraImagesLayout!!.visibility = View.VISIBLE
            imageValidationCount = 1
            minimumImageRequired = 1
            val mImage = CitationImagesModel()
            mImage.status = 0
            mImage.citationImage = "Add Photo"
            mImage.id = 101
            bannerList?.add(mImage)
        }
        else if(fromForm.equals("Vehicle Inspection",ignoreCase = true)) {
            mCameraImagesLayout!!.visibility = View.VISIBLE
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

            if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAMETRO, ignoreCase = true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CORPUSCHRISTI, ignoreCase = true)) {
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

        } else if(fromForm.equals("Sign Off Report",ignoreCase = true)) {
            mCameraImagesLayout!!.visibility = View.VISIBLE
            appCompatImageViewCameraIcon!!.visibility = View.GONE
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

        }else if(fromForm.equals("72hrs Notice To Tow Report",ignoreCase = true)) {
            mCameraImagesLayout!!.visibility = View.VISIBLE
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

        } else if(fromForm.equals("Bike Inspection",ignoreCase = true)) {

            mCameraImagesLayout!!.visibility = View.VISIBLE
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
        } else if(fromForm.equals("Tow Report",ignoreCase = true)) {

            mCameraImagesLayout!!.visibility = View.VISIBLE
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
        }else if(fromForm.equals("NFL Special Assignment Report",ignoreCase = true)||
            fromForm.equals("Pay Station Report",ignoreCase = true)||
            fromForm.equals("Signage Report",ignoreCase = true)||
            fromForm.equals("SPECIAL EVENT REPORT",ignoreCase = true)) {

            mCameraImagesLayout!!.visibility = View.VISIBLE
            imageValidationCount = 3
            minimumImageRequired = 3
            if(fromForm.equals("Signage Report",ignoreCase = true)||
                fromForm.equals("Pay Station Report",ignoreCase = true)){
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

        }else if(fromForm.equals("Homeless Report",ignoreCase = true)) {

            mCameraImagesLayout!!.visibility = View.VISIBLE
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

        }else if(fromForm.equals("PCH Daily Updates",ignoreCase = true)) {

            mCameraImagesLayout!!.visibility = View.VISIBLE
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

        }else if(fromForm.equals("Work Order Report",ignoreCase = true)) {

            mCameraImagesLayout!!.visibility = View.VISIBLE
            imageValidationCount = 1
            minimumImageRequired = 1

            val mImage = CitationImagesModel()
            mImage.status = 0
            mImage.citationImage = "Photo 1"
            mImage.id = 101
            bannerList?.add(mImage)

        }else if(fromForm.equals("Safety Report Immediate Attention Required",ignoreCase = true)||
            fromForm.equals("Trash Lot Maintenance Report",ignoreCase = true)) {

            mCameraImagesLayout!!.visibility = View.VISIBLE
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
        if (bannerList != null && bannerList!!.size > 0 ) {
            mBannerAdapter!!.setAnimalBannerList(bannerList)
            mViewPagerBanner.adapter = mBannerAdapter
            mViewPagerBanner.currentItem = 0
        }
        mViewPagerBanner.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
            ) { }

            override fun onPageSelected(position: Int) {
                if (bannerList == null || bannerList!!.size == 0) {
                    // Log.e("length--",""+animalInfo.getImageList().size());
                    return
                }
                try {
                    for (i in bannerList!!.indices) {
                        mDots!![i]?.setImageDrawable(resources.getDrawable(R.drawable.ic_pager_unselected_dot))
                    }
                    mDots!![position]?.setImageDrawable(resources.getDrawable(R.drawable.ic_pager_selected_dot))
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
        if (bannerList != null && bannerList!!.size > 0) {
            setUiPageViewController(mBannerAdapter!!.count)
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

    private fun removeTimingImagesFromFolder() {
        try {
            val directoryCamera = File(
                    Environment.getExternalStorageDirectory().absolutePath,
                    Constants.FILE_NAME + Constants.ALLREPORTIMAGES
            )
            deleteRecursive(directoryCamera)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun deleteRecursive(directory: File) {
        try {
            if (directory.exists()) {
                if (directory.isDirectory)
                    for (child in directory.listFiles())
                        deleteRecursive(child)

                directory.delete()
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun setDropDowns() {
        try {
            if(mAutoComTextViewComments!=null) {
//                setDropdownRemark(Singleton.getDataSetList(DATASET_REMARKS_LIST, it) })
            }
//            setDropdownSide(Singleton.getDataSetList(DATASET_SIDE_LIST, it) })
            if(mAutoComTextViewMeterNo!=null) {
                setDropdownMeterName(Singleton.getDataSetList(DATASET_METER_LIST, mDb))
            }
            if(mAutoComTextViewStreet!=null) {
                setDropdownStreet(Singleton.getDataSetList(DATASET_STREET_LIST, mDb))
            }
            if(mAutoComTextViewBlock!=null) {
                setDropdownBlock(Singleton.getDataSetList(DATASET_BLOCK_LIST, mDb))
            }

            if(mAutoComTextViewViolationDescription!=null) {
                setDropdownViolation(Singleton.getDataSetList(DATASET_VIOLATION_LIST, mDb))
            }

            if(mAutoComTextViewDutyHours!=null) {
                setDropdownDutyHour(mWelcomeListDataSet?.welcomeList?.shiftStats)
            }
            if(mAutoComTextViewMeterType!=null) {
                setDropdownMeterType()
            }
            if(mAutoComTextViewMeterLabelVisible!=null) {
                setDropdownMeterLabelVisible()
            }
            if(mAutoComTextViewDigitalDisplayVisible!=null) {
                setDropdownDigitalDisplayVisible()
            }
            if(mAutoComTextViewCoinJam!=null) {
                setDropdownCoinJam()
            }
            if(mAutoComTextViewCreditCardJam!=null) {
                setDropdownCreditCardJam()
            }
            if(mAutoComTextViewDeviceId!=null) {
                setDropdownDevice(mWelcomeListDataSet?.welcomeList?.deviceStats)
            }
            if(mAutoComTextViewCurbColor!=null) {
                setDropdownCurbColor()
            }
            if(mAutoComTextViewEnforceable!=null) {
                setDropdownEnforceable()
            }
            if(mAutoComTextViewTriedToRestartHandHeld!=null) {
                setDropdownTriedToRestartHandHeld()
            }
            if(mAutoComTextViewPrintingCorrectly!=null) {
                setDropdownPrintingCorrectly()
            }
            if(mAutoComTextViewOverHeating!=null) {
                setDropdownOverHeating()
            }
            if(mAutoComTextViewBatteryHoldCharge!=null) {
                setDropdownBatteryHoldCharge()
            }
            if(mAutoComTextViewInternetConnectivity!=null) {
                setDropdownInternetConnectivity()
            }
            if(mAutoComTextViewGraffiti!=null) {
                setDropdownGraffiti()
            }
            if(mAutoComTextViewMissingSign!=null) {
                setDropdownMissingSign()
            }
            if(mAutoComTextViewTirePressure!=null) {
                setDropdownTirePressure()
            }
            if(mAutoComTextViewAssignedBike!=null) {
                setDropdownAssignedBike()
            }
            if(mAutoComTextViewBreaksRotors!=null) {
                setDropdownBreaksRotors()
            }
            if(mAutoComTextViewLightsAndReflectors!=null) {
                setDropdownLightsAndReflectors()
            }
            if(mAutoComTextViewChainCrank!=null) {
                setDropdownChainCrank()
            }
            if(mAutoComTextViewBatteryFreeOfDebris!=null) {
                setDropdownBatteryFreeOfDebris()
            }
            if(mAutoComTextViewFlatPack!=null) {
                setDropdownFlatPack()
            }
            if(mAutoComTextViewLightBar!=null) {
                setDropdownLightBar()
            }
            if(mAutoComTextViewDashBoardIndications!=null) {
                setDropdownDashboardIndications()
            }
            if(mAutoComTextViewSeatBeltOperational!=null) {
                setDropdownSeatBeltOperational()
            }
            if(mAutoComTextViewBrakes!=null) {
                setDropdownBrakes()
            }
            if(mAutoComTextViewBrakeLights!=null) {
                setDropdownBrakeLights()
            }
            if(mAutoComTextViewHeadLights!=null) {
                setDropdownHeadLights()
            }
            if(mAutoComTextViewTurnSignals!=null) {
                setDropdownTurnSignals()
            }
            if(mAutoComTextViewSteeringWheelOperational!=null) {
                setDropdownSteeringWheelOperational()
            }
            if(mAutoComTextViewWindshieldVisibility!=null) {
                setDropdownWindshieldVisibility()
            }
            if(mAutoComTextViewSideAndRearViewMirrorsOperational!=null) {
                setDropdownSideAndRearViewMirrorsOperational()
            }
            if(mAutoComTextViewWindshieldWipersOperational!=null) {
                setDropdownWindshieldWipersOperational()
            }
            if(mAutoComTextViewVehicleRegistrationAndInsurance!=null) {
                setDropdownVehicleRegistrationAndInsurance()
            }
            if(mAutoComTextViewConesSixPerVehicle!=null) {
                setDropdownConesSixPerVehicle()
            }
            if(mAutoComTextViewFirstAidKit!=null) {
                setDropdownFirstAidKit()
            }
            if(mAutoComTextViewHorn!=null) {
                setDropdownHorn()
            }
            if(mAutoComTextViewInteriorCleanliness!=null) {
                setDropdownInteriorCleanliness()
            }
            if(mAutoComTextViewExteriorCleanliness!=null) {
                setDropdownExteriorCleanliness()
            }
            if(mAutoComTextViewVisibleLeaks!=null) {
                setDropdownVisibleLeaks()
            }
            if(mAutoComTextViewLprLensFreeOfDebris!=null) {
                setDropdownLprLensFreeOfDebris()
            }
            if(mAutoComTextViewTiresVisualInspection!=null) {
                setDropdownTiresVisualInspection()
            }
            if(mAutoComTextViewStaus!=null) {
                setDropdownStatus()
            }
            if(mAutoComTextViewUnitNo!=null) {
                setDropdownUnitNo(mWelcomeListDataSet?.welcomeList?.equipmentStates)
            }

            if(mAutoComTextViewAssignedArea!=null) {
                setDropdownAssignedArea(mWelcomeListDataSet?.welcomeList?.beatStats)
            }

            if(mAutoComTextViewHandheldUnitNo!=null) {
                setDropdownHandHeldUnit(mWelcomeListDataSet?.welcomeList?.deviceStats)
            }

            if(mAutoComTextViewDirection!=null) {
                setDropdownDirection()
            }

            if(mAutoComTextViewVehicle!=null) {
                setDropdownVehicle()
            }
            if(mAutoComTextViewGasLevel!=null) {
                setDropdownGasLevel()
            }

            if(mAutoComTextViewDutyHours!=null) {
                setDropdownDutyHour(mWelcomeListDataSet?.welcomeList?.shiftStats)
            }

            if(mAutoComTextViewBikeGlassess!=null) {
                setDropdownBikeGlassess()
            }

            if(mAutoComTextViewGloveVisualInspection!=null) {
                setDropdownGloveVisualInspection()
            }

            if(mAutoComTextViewBatteryCharge!=null) {
                setDropdownBatterCharge()
            }
            if(mAutoComTextViewMake!=null) {
                setDropdownMakeVehicle()
            }
            if(mAutoComTextViewColor!=null) {
                setDropdownVehicleColour()
            }
            if(mAutoComTextViewLotArea!=null) {
                setDropdownLot()
            }
            if(mAutoComTextViewState!=null) {
                setDropdownState()
            }
            if(mAutoComTextViewZone!=null) {
                setDropdownZone()
            }
            if(mAutoComTextViewPBCZone!=null) {
                setDropdownPBCZone()
            }
            if(mAppComTextViewVehicleRelocatedToDifferentStallWithinTheLot!=null) {
                setDropdownVehicleRelocatedToDifferentStallWithLot()
            }
            if(mAppComTextViewVehicleStoredAt!=null) {
                setDropdownVehicleStoredAtList()
            }
            if(mAppComTextViewSafetyIssue!=null) {
                setDropdownSafetyIssueList()
            }
            if(mAppComTextViewRequiredServices!=null) {
                setDropdownRequiredSevicesList()
            }
            if(mAppComTextViewReasonForTow!=null) {
                setDropdownReasonForTowList()
            }
            if(mAutoComTextViewSpecialEnforcementRequest!=null) {
                setDropdownRequestToTowList()
            }
            if(mAutoComTextViewTowingOfficer!=null) {
                setDropdownTowingOfficerList()
            }

            if(mAutoComTextViewDate!=null)
            {
                if(!BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAMETRO,true)&&
                    !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CORPUSCHRISTI,true)) {
                    mAutoComTextViewDate?.setOnClickListener {
                        AppUtils.hideSoftKeyboard(this@AllReportActivity)
                        AppUtils.openDataPicker(
                            mAutoComTextViewDate, 1, supportFragmentManager,
                            this@AllReportActivity, myCalendar
                        )
                    }
                }
            }
            if(mAutoComTextViewDutyHours!=null)
            {
//                mAutoComTextViewDutyHours?.setOnClickListener {
//                    AppUtils.hideSoftKeyboard(this@AllReportActivity)
//                    openTimePicker(mAutoComTextViewDutyHours)
//                }
            }
            if(mAutoComTextViewLunchTaken!=null)
            {
                mAutoComTextViewLunchTaken?.setOnClickListener {
                    AppUtils.hideSoftKeyboard(this@AllReportActivity)
                    openTimePicker(mAutoComTextViewLunchTaken)
                }
            }
            if(mAutoComTextViewFirst10MinBreak!=null)
            {
                mAutoComTextViewFirst10MinBreak?.setOnClickListener {
                    AppUtils.hideSoftKeyboard(this@AllReportActivity)
                    openTimePicker(mAutoComTextViewFirst10MinBreak)
                }
            }
            if(mAutoComTextViewSecond10MinBreak!=null)
            {
                mAutoComTextViewSecond10MinBreak?.setOnClickListener {
                    AppUtils.hideSoftKeyboard(this@AllReportActivity)
                    openTimePicker(mAutoComTextViewSecond10MinBreak)
                }
            }
            if(mAutoComTextViewTimeMark!=null && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAMETRO, ignoreCase = true)
                && mAutoComTextViewTimeMark!=null && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CORPUSCHRISTI, ignoreCase = true))
            {
                mAutoComTextViewTimeMark?.setOnClickListener {
                    AppUtils.hideSoftKeyboard(this@AllReportActivity)
                    openTimePicker(mAutoComTextViewTimeMark)
                }
            }
            if(mAutoComTextViewTimeMark2!=null)
            {
                mAutoComTextViewTimeMark2?.setOnClickListener {
                    AppUtils.hideSoftKeyboard(this@AllReportActivity)
                    openTimePicker(mAutoComTextViewTimeMark2)
                }
            }
            if(mAutoComTextViewTimeMark3!=null)
            {
                mAutoComTextViewTimeMark3?.setOnClickListener {
                    AppUtils.hideSoftKeyboard(this@AllReportActivity)
                    openTimePicker(mAutoComTextViewTimeMark3)
                }
            }
            if(mAutoComTextViewTimeMark4!=null)
            {
                mAutoComTextViewTimeMark4?.setOnClickListener {
                    AppUtils.hideSoftKeyboard(this@AllReportActivity)
                    openTimePicker(mAutoComTextViewTimeMark4)
                }
            }
            if(mAutoComTextViewCitationsIssued!=null && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAMETRO, ignoreCase = true)
                && mAutoComTextViewCitationsIssued!=null && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CORPUSCHRISTI, ignoreCase = true))
            {
                mAutoComTextViewCitationsIssued?.setOnClickListener {
                    AppUtils.hideSoftKeyboard(this@AllReportActivity)
                    openTimePicker(mAutoComTextViewCitationsIssued)
                }
            }
            if(mAutoComTextViewOfficer!=null)
            {
                mAutoComTextViewOfficer!!.setText(mWelcomeFormData!!
                        .officerFirstName+" "+mWelcomeFormData!!.officerLastName)
            }
//            geoAddress()
            mEditTextOfficerName?.text = mWelcomeFormData?.officerFirstName + " " +
                    mWelcomeFormData?.officerLastName

            mEditTextOfficerId?.text = mWelcomeFormData?.officerBadgeId
            mEditTextOfficerBeat?.text = mWelcomeFormData?.officerBeatName
            val timeStamp = SimpleDateFormat("yyyMMdd_HHmmss", Locale.US).format(Date())
            mEditTextReportNumber!!.setText(timeStamp.toString())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun geoAddress() {
        mEditTextDateTime?.text = AppUtils.getCurrentDateTimeforBoot("UI")
        clientTime = AppUtils.getCurrentDateTimeforBoot("Normal").trim()
                .replace(" ", "T")
        val mLat = sharedPreference.read(SharedPrefKey.LAT, "0.0")?.toDouble().nullSafety()
        val mLong = sharedPreference.read(SharedPrefKey.LONG, "0.0")?.toDouble().nullSafety()
        val geocoder: Geocoder
        val addresses: List<Address>
        geocoder = Geocoder(this, Locale.getDefault())
        addresses = geocoder.getFromLocation(
                mLat,
                mLong,
                1
        )!! // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        val address =
                addresses[0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        val separated = address.split(",").toTypedArray()
        val printAddress = separated[0] // this will contain "Fruit"
        mAutoComTextViewBlock?.setText(
                if (printAddress.split(" ").toTypedArray().size > 1) printAddress.split(" ")
                        .toTypedArray()[0] else printAddress
        )
        try {
            val count = printAddress.split(" ").toTypedArray().size
            if (count > 2) {
                mAutoComTextViewStreet?.setText(
                        if (count > 0) printAddress.split(
                                Pattern.compile(" "),
                                count - 1.coerceAtLeast(0)
                        ).toTypedArray().get(1) else printAddress
                )
            } else {
                mAutoComTextViewStreet?.setText(
                        if (count > 1) printAddress.split(" ").toTypedArray()[1] else printAddress
                )
            }
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
                    this,
                    R.layout.row_dropdown_menu_popup_item,
                    mDropdownList
            )
           try {
               mAutoComTextViewComments?.threshold = 1
               mAutoComTextViewComments?.setAdapter<ArrayAdapter<String?>>(adapter)
                //mSelectedShiftStat = mApplicationList.get(pos);
               mAutoComTextViewComments?.onItemClickListener =
                        OnItemClickListener { parent, view, position, id -> // mSelectedShiftStat = mApplicationList.get(position);
                            AppUtils.hideSoftKeyboard(this@AllReportActivity)
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
                    this,
                    R.layout.row_dropdown_menu_popup_item,
                    mDropdownList
            )
            try {
                mAutoComTextViewStreet!!.threshold = 1
                mAutoComTextViewStreet!!.setAdapter<ArrayAdapter<String?>>(adapter)
                mAutoComTextViewStreet!!.onItemClickListener =
                        OnItemClickListener { parent, view, position, id -> //mAutoComTextViewDirection.setText(mApplicationList.get(position).getDirection()); ;
                            AppUtils.hideSoftKeyboard(this@AllReportActivity)
                            mAutoComTextViewStreet!!.setError(null)
                        }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
    }

    //set value to Meter Name dropdown
    private fun setDropdownBlock(mApplicationList: List<DatasetResponse>?) {
//        hideSoftKeyboard(this@LprDetails2Activity)
        var pos = 0
        if (mApplicationList != null && mApplicationList.size > 0) {
            val mDropdownList = arrayOfNulls<String>(mApplicationList.size)
            for (i in mApplicationList.indices) {
                mDropdownList[i] = mApplicationList[i].blockName.toString()
        }
        Arrays.sort(mDropdownList)
        val adapter = ArrayAdapter(
                this,
                R.layout.row_dropdown_menu_popup_item,
                mDropdownList
        )
        try {
            mAutoComTextViewBlock?.threshold = 1
            mAutoComTextViewBlock?.setAdapter<ArrayAdapter<String?>>(adapter)
            mAutoComTextViewBlock?.onItemClickListener =
                    OnItemClickListener { parent, view, position, id ->
                        AppUtils.hideSoftKeyboard(this@AllReportActivity)
                        mAppComTextViewBlock!!.setError(null)
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
                this,
                R.layout.row_dropdown_menu_popup_item,
                mDropdownList
        )
        try {
            mAutoComTextViewViolationDescription?.threshold = 1
            mAutoComTextViewViolationDescription?.setAdapter<ArrayAdapter<String?>>(adapter)
            mAutoComTextViewViolationDescription?.onItemClickListener =
                    OnItemClickListener { parent, view, position, id ->
                            AppUtils.hideSoftKeyboard(this@AllReportActivity)
                            mAutoComTextViewViolationDescription!!.setError(null)
                            val index = getIndexOfViotiona(
                                mApplicationList,
                                parent.getItemAtPosition(position).toString()
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
                    this,
                    R.layout.row_dropdown_menu_popup_item,
                    mDropdownList
            )
            try {
                mAutoComTextViewMeterNo?.threshold = 1
                mAutoComTextViewMeterNo?.setAdapter<ArrayAdapter<String?>>(adapter)
                mAutoComTextViewMeterNo?.onItemClickListener =
                        OnItemClickListener { parent, view, position, id ->
                            AppUtils.hideSoftKeyboard(this@AllReportActivity)
                            try {
                                mAutoComTextViewMeterNo?.error = null
//                                val index: Int = getIndexOfMeter(mApplicationList,
//                                        mAutoComTextViewMeterNo!!.text.toString())

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
                    this,
                    R.layout.row_dropdown_menu_popup_item,
                    mYesNoList
            )
            try {
                mAutoComTextViewMeterLabelVisible?.threshold = 1
                mAutoComTextViewMeterLabelVisible?.setAdapter<ArrayAdapter<String?>>(adapter)
                mAutoComTextViewMeterLabelVisible?.onItemClickListener =
                        OnItemClickListener { parent, view, position, id ->
                            AppUtils.hideSoftKeyboard(this@AllReportActivity)
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
                    this,
                    R.layout.row_dropdown_menu_popup_item,
                    mYesNoList
            )
            try {
                mAutoComTextViewVehicleRelocatedToDifferentStallWithinTheLot?.threshold = 1
                mAutoComTextViewVehicleRelocatedToDifferentStallWithinTheLot?.setAdapter<ArrayAdapter<String?>>(adapter)
                mAutoComTextViewVehicleRelocatedToDifferentStallWithinTheLot?.onItemClickListener =
                        OnItemClickListener { parent, view, position, id ->
                            AppUtils.hideSoftKeyboard(this@AllReportActivity)
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
                    this,
                    R.layout.row_dropdown_menu_popup_item,
                mVehicleStoredAtList
            )
            try {
                mAutoComTextViewVehicleStoredAt?.threshold = 1
                mAutoComTextViewVehicleStoredAt?.setAdapter<ArrayAdapter<String?>>(adapter)
                mAutoComTextViewVehicleStoredAt?.onItemClickListener =
                        OnItemClickListener { parent, view, position, id ->
                            AppUtils.hideSoftKeyboard(this@AllReportActivity)
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
                    this,
                    R.layout.row_dropdown_menu_popup_item,
                mSafetyReportList
            )
            try {
                mAutoComTextViewSafetyIssue?.threshold = 1
                mAutoComTextViewSafetyIssue?.setAdapter<ArrayAdapter<String?>>(adapter)
                mAutoComTextViewSafetyIssue?.onItemClickListener =
                        OnItemClickListener { parent, view, position, id ->
                            AppUtils.hideSoftKeyboard(this@AllReportActivity)
                            mAutoComTextViewSafetyIssue?.error = null

                        }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }  private fun setDropdownRequiredSevicesList() {
        val pos = 0
        if (mRequiredServicesList != null && mRequiredServicesList.isNotEmpty()) {

            val adapter = ArrayAdapter(
                    this,
                    R.layout.row_dropdown_menu_popup_item,
                mRequiredServicesList
            )
            try {
                mAutoComTextViewRequiredServices?.threshold = 1
                mAutoComTextViewRequiredServices?.setAdapter<ArrayAdapter<String?>>(adapter)
                mAutoComTextViewRequiredServices?.onItemClickListener =
                        OnItemClickListener { parent, view, position, id ->
                            AppUtils.hideSoftKeyboard(this@AllReportActivity)
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
                    this,
                    R.layout.row_dropdown_menu_popup_item,
                mReasonForTowList
            )
            try {
                mAutoComTextViewReasonForTow?.threshold = 1
                mAutoComTextViewReasonForTow?.setAdapter<ArrayAdapter<String?>>(adapter)
                mAutoComTextViewReasonForTow?.onItemClickListener =
                        OnItemClickListener { parent, view, position, id ->
                            AppUtils.hideSoftKeyboard(this@AllReportActivity)
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
                    this,
                    R.layout.row_dropdown_menu_popup_item,
                mRequestToTowList
            )
            try {
                mAutoComTextViewSpecialEnforcementRequest?.threshold = 1
                mAutoComTextViewSpecialEnforcementRequest?.setAdapter<ArrayAdapter<String?>>(adapter)
                mAutoComTextViewSpecialEnforcementRequest?.onItemClickListener =
                        OnItemClickListener { parent, view, position, id ->
                            AppUtils.hideSoftKeyboard(this@AllReportActivity)
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
                    this,
                    R.layout.row_dropdown_menu_popup_item,
                ReportArrayClass.mTowingOfficerListTowReport
            )
            try {
                mAutoComTextViewTowingOfficer?.threshold = 1
                mAutoComTextViewTowingOfficer?.setAdapter<ArrayAdapter<String?>>(adapter)
                mAutoComTextViewTowingOfficer?.onItemClickListener =
                        OnItemClickListener { parent, view, position, id ->
                            AppUtils.hideSoftKeyboard(this@AllReportActivity)
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
                    this,
                    R.layout.row_dropdown_menu_popup_item,
                    mYesNoList
            )
            try {
                mAutoComTextViewDigitalDisplayVisible?.threshold = 1
                mAutoComTextViewDigitalDisplayVisible?.setAdapter<ArrayAdapter<String?>>(adapter)
                mAutoComTextViewDigitalDisplayVisible?.onItemClickListener =
                        OnItemClickListener { parent, view, position, id ->
                            AppUtils.hideSoftKeyboard(this@AllReportActivity)
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
                    this,
                    R.layout.row_dropdown_menu_popup_item,
                    mYesNoList
            )
            try {
                mAutoComTextViewCoinJam?.threshold = 1
                mAutoComTextViewCoinJam?.setAdapter<ArrayAdapter<String?>>(adapter)
                mAutoComTextViewCoinJam?.onItemClickListener =
                        OnItemClickListener { parent, view, position, id ->
                            AppUtils.hideSoftKeyboard(this@AllReportActivity)
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
                        this,
                        R.layout.row_dropdown_menu_popup_item,
                        mYesNoList
                )
                try {
                    mAutoComTextViewCreditCardJam?.threshold = 1
                    mAutoComTextViewCreditCardJam?.setAdapter<ArrayAdapter<String?>>(adapter)
                    mAutoComTextViewCreditCardJam?.onItemClickListener =
                            OnItemClickListener { parent, view, position, id ->
                                AppUtils.hideSoftKeyboard(this@AllReportActivity)
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
                    this,
                    R.layout.row_dropdown_menu_popup_item,
                    mColorList
            )
            try {
                mAutoComTextViewCurbColor?.threshold = 1
                mAutoComTextViewCurbColor?.setAdapter<ArrayAdapter<String?>>(adapter)
                mAutoComTextViewCurbColor?.onItemClickListener =
                        OnItemClickListener { parent, view, position, id ->
                            AppUtils.hideSoftKeyboard(this@AllReportActivity)
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
                    this,
                    R.layout.row_dropdown_menu_popup_item,
                    mMeterTypeList
            )
            try {
                mAutoComTextViewMeterType?.threshold = 1
                mAutoComTextViewMeterType?.setAdapter<ArrayAdapter<String?>>(adapter)
                mAutoComTextViewMeterType?.onItemClickListener =
                        OnItemClickListener { parent, view, position, id ->
                            AppUtils.hideSoftKeyboard(this@AllReportActivity)
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
                    this,
                    R.layout.row_dropdown_menu_popup_item,
                    mYesNoList
            )
            try {
                mAutoComTextViewEnforceable?.threshold = 1
                mAutoComTextViewEnforceable?.setAdapter<ArrayAdapter<String?>>(adapter)
                mAutoComTextViewEnforceable?.onItemClickListener =
                        OnItemClickListener { parent, view, position, id ->
                            AppUtils.hideSoftKeyboard(this@AllReportActivity)
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
                    this,
                    R.layout.row_dropdown_menu_popup_item,
                    mYesNoList
            )
            try {
                mAutoComTextViewTriedToRestartHandHeld?.threshold = 1
                mAutoComTextViewTriedToRestartHandHeld?.setAdapter<ArrayAdapter<String?>>(adapter)
                mAutoComTextViewTriedToRestartHandHeld?.onItemClickListener =
                        OnItemClickListener { parent, view, position, id ->
                            AppUtils.hideSoftKeyboard(this@AllReportActivity)
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
                    this,
                    R.layout.row_dropdown_menu_popup_item,
                    mYesNoList
            )
            try {
                mAutoComTextViewPrintingCorrectly?.threshold = 1
                mAutoComTextViewPrintingCorrectly?.setAdapter<ArrayAdapter<String?>>(adapter)
                mAutoComTextViewPrintingCorrectly?.onItemClickListener =
                        OnItemClickListener { parent, view, position, id ->
                            AppUtils.hideSoftKeyboard(this@AllReportActivity)
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
                    this,
                    R.layout.row_dropdown_menu_popup_item,
                    mYesNoList
            )
            try {
                mAutoComTextViewOverHeating?.threshold = 1
                mAutoComTextViewOverHeating?.setAdapter<ArrayAdapter<String?>>(adapter)
                mAutoComTextViewOverHeating?.onItemClickListener =
                        OnItemClickListener { parent, view, position, id ->
                            AppUtils.hideSoftKeyboard(this@AllReportActivity)
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
                    this,
                    R.layout.row_dropdown_menu_popup_item,
                    mYesNoList
            )
            try {
                mAutoComTextViewBatteryHoldCharge?.threshold = 1
                mAutoComTextViewBatteryHoldCharge?.setAdapter<ArrayAdapter<String?>>(adapter)
                mAutoComTextViewBatteryHoldCharge?.onItemClickListener =
                        OnItemClickListener { parent, view, position, id ->
                            AppUtils.hideSoftKeyboard(this@AllReportActivity)
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
                    this,
                    R.layout.row_dropdown_menu_popup_item,
                    mYesNoList
            )
            try {
                mAutoComTextViewInternetConnectivity?.threshold = 1
                mAutoComTextViewInternetConnectivity?.setAdapter<ArrayAdapter<String?>>(adapter)
                mAutoComTextViewInternetConnectivity?.onItemClickListener =
                        OnItemClickListener { parent, view, position, id ->
                            AppUtils.hideSoftKeyboard(this@AllReportActivity)
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
                    this,
                    R.layout.row_dropdown_menu_popup_item,
                    mYesNoList
            )
            try {
                mAutoComTextViewGraffiti?.threshold = 1
                mAutoComTextViewGraffiti?.setAdapter<ArrayAdapter<String?>>(adapter)
                mAutoComTextViewGraffiti?.onItemClickListener =
                        OnItemClickListener { parent, view, position, id ->
                            AppUtils.hideSoftKeyboard(this@AllReportActivity)
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
                    this,
                    R.layout.row_dropdown_menu_popup_item,
                    mYesNoList
            )
            try {
                mAutoComTextViewMissingSign?.threshold = 1
                mAutoComTextViewMissingSign?.setAdapter<ArrayAdapter<String?>>(adapter)
                mAutoComTextViewMissingSign?.onItemClickListener =
                        OnItemClickListener { parent, view, position, id ->
                            AppUtils.hideSoftKeyboard(this@AllReportActivity)
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

            mAutoComTextViewTirePressure!!.post {
                val adapter = ArrayAdapter(
                        this,
                        R.layout.row_dropdown_menu_popup_item,
                        mPassFailedList
                )
                try {
                    mAutoComTextViewTirePressure?.threshold = 1
                    mAutoComTextViewTirePressure?.setAdapter<ArrayAdapter<String?>>(adapter)
                    mAutoComTextViewTirePressure?.onItemClickListener =
                            OnItemClickListener { parent, view, position, id ->
                                AppUtils.hideSoftKeyboard(this@AllReportActivity)
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

            mAutoComTextViewAssignedBike!!.post {
                val adapter = ArrayAdapter(
                        this,
                        R.layout.row_dropdown_menu_popup_item,
                        mAssignedBikeList
                )
                try {
                    mAutoComTextViewAssignedBike?.threshold = 1
                    mAutoComTextViewAssignedBike?.setAdapter<ArrayAdapter<String?>>(adapter)
                    mAutoComTextViewAssignedBike?.onItemClickListener =
                            OnItemClickListener { parent, view, position, id ->
                                AppUtils.hideSoftKeyboard(this@AllReportActivity)
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

            mAutoComTextViewBreaksRotors!!.post {
                val adapter = ArrayAdapter(
                        this,
                        R.layout.row_dropdown_menu_popup_item,
                        mPassFailedList
                )
                try {
                    mAutoComTextViewBreaksRotors?.threshold = 1
                    mAutoComTextViewBreaksRotors?.setAdapter<ArrayAdapter<String?>>(adapter)
                    mAutoComTextViewBreaksRotors?.onItemClickListener =
                            OnItemClickListener { parent, view, position, id ->
                                AppUtils.hideSoftKeyboard(this@AllReportActivity)
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

            mAutoComTextViewLightsAndReflectors!!.post {
                val adapter = ArrayAdapter(
                        this,
                        R.layout.row_dropdown_menu_popup_item,
                        mPassFailedList
                )
                try {
                    mAutoComTextViewLightsAndReflectors?.threshold = 1
                    mAutoComTextViewLightsAndReflectors?.setAdapter<ArrayAdapter<String?>>(adapter)
                    mAutoComTextViewLightsAndReflectors?.onItemClickListener =
                            OnItemClickListener { parent, view, position, id ->
                                AppUtils.hideSoftKeyboard(this@AllReportActivity)
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

            mAutoComTextViewChainCrank!!.post {
                val adapter = ArrayAdapter(
                        this,
                        R.layout.row_dropdown_menu_popup_item,
                        mPassFailedList
                )
                try {
                    mAutoComTextViewChainCrank?.threshold = 1
                    mAutoComTextViewChainCrank?.setAdapter<ArrayAdapter<String?>>(adapter)
                    mAutoComTextViewChainCrank?.onItemClickListener =
                            OnItemClickListener { parent, view, position, id ->
                                AppUtils.hideSoftKeyboard(this@AllReportActivity)
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

            mAutoComTextViewBatteryFreeOfDebris!!.post {
                val adapter = ArrayAdapter(
                        this,
                        R.layout.row_dropdown_menu_popup_item,
                        mPassFailedList
                )
                try {
                    mAutoComTextViewBatteryFreeOfDebris?.threshold = 1
                    mAutoComTextViewBatteryFreeOfDebris?.setAdapter<ArrayAdapter<String?>>(adapter)
                    mAutoComTextViewBatteryFreeOfDebris?.onItemClickListener =
                            OnItemClickListener { parent, view, position, id ->
                                AppUtils.hideSoftKeyboard(this@AllReportActivity)
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

            mAutoComTextViewFlatPack!!.post {
                val adapter = ArrayAdapter(
                        this,
                        R.layout.row_dropdown_menu_popup_item,
                        mPassFailedList
                )
                try {
                    mAutoComTextViewFlatPack?.threshold = 1
                    mAutoComTextViewFlatPack?.setAdapter<ArrayAdapter<String?>>(adapter)
                    mAutoComTextViewFlatPack?.onItemClickListener =
                            OnItemClickListener { parent, view, position, id ->
                                AppUtils.hideSoftKeyboard(this@AllReportActivity)
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

            mAutoComTextViewLightBar!!.post {
                val adapter = ArrayAdapter(
                        this,
                        R.layout.row_dropdown_menu_popup_item,
                        mPassFailedList
                )
                try {
                    mAutoComTextViewLightBar?.threshold = 1
                    mAutoComTextViewLightBar?.setAdapter<ArrayAdapter<String?>>(adapter)
                    mAutoComTextViewLightBar?.onItemClickListener =
                            OnItemClickListener { parent, view, position, id ->
                                AppUtils.hideSoftKeyboard(this@AllReportActivity)
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
        if (mPassFailedList != null && mPassFailedList.isNotEmpty()) {

            mAutoComTextViewDashBoardIndications!!.post {
                val adapter = ArrayAdapter(
                        this,
                        R.layout.row_dropdown_menu_popup_item,
                        mPassFailedList
                )
                try {
                    mAutoComTextViewDashBoardIndications?.threshold = 1
                    mAutoComTextViewDashBoardIndications?.setAdapter<ArrayAdapter<String?>>(adapter)
                    mAutoComTextViewDashBoardIndications?.onItemClickListener =
                            OnItemClickListener { parent, view, position, id ->
                                AppUtils.hideSoftKeyboard(this@AllReportActivity)
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

            mAutoComTextViewSeatBeltOperational!!.post {
                val adapter = ArrayAdapter(
                        this,
                        R.layout.row_dropdown_menu_popup_item,
                        mPassFailedList
                )
                try {
                    mAutoComTextViewSeatBeltOperational?.threshold = 1
                    mAutoComTextViewSeatBeltOperational?.setAdapter<ArrayAdapter<String?>>(adapter)
                    mAutoComTextViewSeatBeltOperational?.onItemClickListener =
                            OnItemClickListener { parent, view, position, id ->
                                AppUtils.hideSoftKeyboard(this@AllReportActivity)
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

            mAutoComTextViewBrakes!!.post {
                val adapter = ArrayAdapter(
                        this,
                        R.layout.row_dropdown_menu_popup_item,
                        mPassFailedList
                )
                try {
                    mAutoComTextViewBrakes?.threshold = 1
                    mAutoComTextViewBrakes?.setAdapter<ArrayAdapter<String?>>(adapter)
                    mAutoComTextViewBrakes?.onItemClickListener =
                            OnItemClickListener { parent, view, position, id ->
                                AppUtils.hideSoftKeyboard(this@AllReportActivity)
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

            mAutoComTextViewBrakeLights!!.post {
                val adapter = ArrayAdapter(
                        this,
                        R.layout.row_dropdown_menu_popup_item,
                        mPassFailedList
                )
                try {
                    mAutoComTextViewBrakeLights?.threshold = 1
                    mAutoComTextViewBrakeLights?.setAdapter<ArrayAdapter<String?>>(adapter)
                    mAutoComTextViewBrakeLights?.onItemClickListener =
                            OnItemClickListener { parent, view, position, id ->
                                AppUtils.hideSoftKeyboard(this@AllReportActivity)
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

            mAutoComTextViewHeadLights!!.post {
                val adapter = ArrayAdapter(
                        this,
                        R.layout.row_dropdown_menu_popup_item,
                        mPassFailedList
                )
                try {
                    mAutoComTextViewHeadLights?.threshold = 1
                    mAutoComTextViewHeadLights?.setAdapter<ArrayAdapter<String?>>(adapter)
                    mAutoComTextViewHeadLights?.onItemClickListener =
                            OnItemClickListener { parent, view, position, id ->
                                AppUtils.hideSoftKeyboard(this@AllReportActivity)
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

            mAutoComTextViewTurnSignals!!.post {
                val adapter = ArrayAdapter(
                        this,
                        R.layout.row_dropdown_menu_popup_item,
                        mPassFailedList
                )
                try {
                    mAutoComTextViewTurnSignals?.threshold = 1
                    mAutoComTextViewTurnSignals?.setAdapter<ArrayAdapter<String?>>(adapter)
                    mAutoComTextViewTurnSignals?.onItemClickListener =
                            OnItemClickListener { parent, view, position, id ->
                                AppUtils.hideSoftKeyboard(this@AllReportActivity)
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

            mAutoComTextViewSteeringWheelOperational!!.post {
                val adapter = ArrayAdapter(
                        this,
                        R.layout.row_dropdown_menu_popup_item,
                        mPassFailedList
                )
                try {
                    mAutoComTextViewSteeringWheelOperational?.threshold = 1
                    mAutoComTextViewSteeringWheelOperational?.setAdapter<ArrayAdapter<String?>>(adapter)
                    mAutoComTextViewSteeringWheelOperational?.onItemClickListener =
                            OnItemClickListener { parent, view, position, id ->
                                AppUtils.hideSoftKeyboard(this@AllReportActivity)
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

            mAutoComTextViewWindshieldVisibility!!.post {
                val adapter = ArrayAdapter(
                        this,
                        R.layout.row_dropdown_menu_popup_item,
                        mPassFailedList
                )
                try {
                    mAutoComTextViewWindshieldVisibility?.threshold = 1
                    mAutoComTextViewWindshieldVisibility?.setAdapter<ArrayAdapter<String?>>(adapter)
                    mAutoComTextViewWindshieldVisibility?.onItemClickListener =
                            OnItemClickListener { parent, view, position, id ->
                                AppUtils.hideSoftKeyboard(this@AllReportActivity)
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

            mAutoComTextViewSideAndRearViewMirrorsOperational!!.post {
                val adapter = ArrayAdapter(
                        this,
                        R.layout.row_dropdown_menu_popup_item,
                        mPassFailedList
                )
                try {
                    mAutoComTextViewSideAndRearViewMirrorsOperational?.threshold = 1
                    mAutoComTextViewSideAndRearViewMirrorsOperational?.setAdapter<ArrayAdapter<String?>>(adapter)
                    mAutoComTextViewSideAndRearViewMirrorsOperational?.onItemClickListener =
                            OnItemClickListener { parent, view, position, id ->
                                AppUtils.hideSoftKeyboard(this@AllReportActivity)
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

            mAutoComTextViewWindshieldWipersOperational!!.post {
                val adapter = ArrayAdapter(
                        this,
                        R.layout.row_dropdown_menu_popup_item,
                        mPassFailedList
                )
                try {
                    mAutoComTextViewWindshieldWipersOperational?.threshold = 1
                    mAutoComTextViewWindshieldWipersOperational?.setAdapter<ArrayAdapter<String?>>(adapter)
                    mAutoComTextViewWindshieldWipersOperational?.onItemClickListener =
                            OnItemClickListener { parent, view, position, id ->
                                AppUtils.hideSoftKeyboard(this@AllReportActivity)
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

            mAutoComTextViewVehicleRegistrationAndInsurance!!.post {
                val adapter = ArrayAdapter(
                        this,
                        R.layout.row_dropdown_menu_popup_item,
                        mYesNoList
                )
                try {
                    mAutoComTextViewVehicleRegistrationAndInsurance?.threshold = 1
                    mAutoComTextViewVehicleRegistrationAndInsurance?.setAdapter<ArrayAdapter<String?>>(adapter)
                    mAutoComTextViewVehicleRegistrationAndInsurance?.onItemClickListener =
                            OnItemClickListener { parent, view, position, id ->
                                AppUtils.hideSoftKeyboard(this@AllReportActivity)
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

            mAutoComTextViewFirstAidKit!!.post {
                val adapter = ArrayAdapter(
                        this,
                        R.layout.row_dropdown_menu_popup_item,
                        mYesNoList
                )
                try {
                    mAutoComTextViewFirstAidKit?.threshold = 1
                    mAutoComTextViewFirstAidKit?.setAdapter<ArrayAdapter<String?>>(adapter)
                    mAutoComTextViewFirstAidKit?.onItemClickListener =
                            OnItemClickListener { parent, view, position, id ->
                                AppUtils.hideSoftKeyboard(this@AllReportActivity)
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

            mAutoComTextViewConesSixPerVehicle!!.post {
                val adapter = ArrayAdapter(
                        this,
                        R.layout.row_dropdown_menu_popup_item,
                        mPassFailedList
                )
                try {
                    mAutoComTextViewConesSixPerVehicle?.threshold = 1
                    mAutoComTextViewConesSixPerVehicle?.setAdapter<ArrayAdapter<String?>>(adapter)
                    mAutoComTextViewConesSixPerVehicle?.onItemClickListener =
                            OnItemClickListener { parent, view, position, id ->
                                AppUtils.hideSoftKeyboard(this@AllReportActivity)
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

            mAutoComTextViewHorn!!.post {
                val adapter = ArrayAdapter(
                        this,
                        R.layout.row_dropdown_menu_popup_item,
                        mPassFailedList
                )
                try {
                    mAutoComTextViewHorn?.threshold = 1
                    mAutoComTextViewHorn?.setAdapter<ArrayAdapter<String?>>(adapter)
                    mAutoComTextViewHorn?.onItemClickListener =
                            OnItemClickListener { parent, view, position, id ->
                                AppUtils.hideSoftKeyboard(this@AllReportActivity)
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

            mAutoComTextViewVehicle!!.post {
                val adapter = ArrayAdapter(
                        this,
                        R.layout.row_dropdown_menu_popup_item,
                        mDeviceList
                )
                try {
                    mAutoComTextViewVehicle?.threshold = 1
                    mAutoComTextViewVehicle?.setAdapter<ArrayAdapter<String?>>(adapter)
                    mAutoComTextViewVehicle?.onItemClickListener =
                            OnItemClickListener { parent, view, position, id ->
                                AppUtils.hideSoftKeyboard(this@AllReportActivity)
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

            mAutoComTextViewGasLevel!!.post {
                val adapter = ArrayAdapter(
                        this,
                        R.layout.row_dropdown_menu_popup_item,
                        mGasList
                )
                try {
                    mAutoComTextViewGasLevel?.threshold = 1
                    mAutoComTextViewGasLevel?.setAdapter<ArrayAdapter<String?>>(adapter)
                    mAutoComTextViewGasLevel?.onItemClickListener =
                            OnItemClickListener { parent, view, position, id ->
                                AppUtils.hideSoftKeyboard(this@AllReportActivity)
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
                        this,
                        R.layout.row_dropdown_menu_popup_item,
                        mDropdownList
                )
                try {
                    mAutoComTextViewDutyHours!!.threshold = 1
                    mAutoComTextViewDutyHours!!.setAdapter<ArrayAdapter<String?>>(adapter)
                    //                mSelectedShiftStat = mApplicationList.get(pos);
                    mAutoComTextViewDutyHours!!.onItemClickListener =
                            OnItemClickListener { parent, view, position, id -> //                        mSelectedShiftStat = mApplicationList.get(position);
                                AppUtils.hideSoftKeyboard(this@AllReportActivity)
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
//            mAutoComTextViewDutyHours!!.post {
//                val adapter = ArrayAdapter(
//                        this,
//                        R.layout.row_dropdown_menu_popup_item,
//                        mDutyHourAreaList
//                )
//                try {
//                    mAutoComTextViewDutyHours?.threshold = 1
//                    mAutoComTextViewDutyHours?.setAdapter<ArrayAdapter<String?>>(adapter)
//                    mAutoComTextViewDutyHours?.onItemClickListener =
//                            OnItemClickListener { parent, view, position, id ->
//                                AppUtils.hideSoftKeyboard(this@AllReportActivity)
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

            mAutoComTextViewInteriorCleanliness!!.post {
                val adapter = ArrayAdapter(
                        this,
                        R.layout.row_dropdown_menu_popup_item,
                        mPassFailedList
                )
                try {
                    mAutoComTextViewInteriorCleanliness?.threshold = 1
                    mAutoComTextViewInteriorCleanliness?.setAdapter<ArrayAdapter<String?>>(adapter)
                    mAutoComTextViewInteriorCleanliness?.onItemClickListener =
                            OnItemClickListener { parent, view, position, id ->
                                AppUtils.hideSoftKeyboard(this@AllReportActivity)
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

            mAutoComTextViewExteriorCleanliness!!.post {
                val adapter = ArrayAdapter(
                        this,
                        R.layout.row_dropdown_menu_popup_item,
                        mPassFailedList
                )
                try {
                    mAutoComTextViewExteriorCleanliness?.threshold = 1
                    mAutoComTextViewExteriorCleanliness?.setAdapter<ArrayAdapter<String?>>(adapter)
                    mAutoComTextViewExteriorCleanliness?.onItemClickListener =
                            OnItemClickListener { parent, view, position, id ->
                                AppUtils.hideSoftKeyboard(this@AllReportActivity)
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

            mAutoComTextViewVisibleLeaks!!.post {
                val adapter = ArrayAdapter(
                        this,
                        R.layout.row_dropdown_menu_popup_item,
                        mYesNoList
                )
                try {
                    mAutoComTextViewVisibleLeaks?.threshold = 1
                    mAutoComTextViewVisibleLeaks?.setAdapter<ArrayAdapter<String?>>(adapter)
                    mAutoComTextViewVisibleLeaks?.onItemClickListener =
                            OnItemClickListener { parent, view, position, id ->
                                AppUtils.hideSoftKeyboard(this@AllReportActivity)
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

            mAutoComTextViewLprLensFreeOfDebris!!.post {
                val adapter = ArrayAdapter(
                        this,
                        R.layout.row_dropdown_menu_popup_item,
                        mPassFailedList
                )
                try {
                    mAutoComTextViewLprLensFreeOfDebris?.threshold = 1
                    mAutoComTextViewLprLensFreeOfDebris?.setAdapter<ArrayAdapter<String?>>(adapter)
                    mAutoComTextViewLprLensFreeOfDebris?.onItemClickListener =
                            OnItemClickListener { parent, view, position, id ->
                                AppUtils.hideSoftKeyboard(this@AllReportActivity)
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

            mAutoComTextViewTiresVisualInspection!!.post {
                val adapter = ArrayAdapter(
                        this,
                        R.layout.row_dropdown_menu_popup_item,
                        mPassFailedList
                )
                try {
                    mAutoComTextViewTiresVisualInspection?.threshold = 1
                    mAutoComTextViewTiresVisualInspection?.setAdapter<ArrayAdapter<String?>>(adapter)
                    mAutoComTextViewTiresVisualInspection?.onItemClickListener =
                            OnItemClickListener { parent, view, position, id ->
                                AppUtils.hideSoftKeyboard(this@AllReportActivity)
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

            mAutoComTextViewStaus!!.post {
                val adapter = ArrayAdapter(
                        this,
                        R.layout.row_dropdown_menu_popup_item,
                        mOutInServiceList
                )
                try {
                    mAutoComTextViewStaus?.threshold = 1
                    mAutoComTextViewStaus?.setAdapter<ArrayAdapter<String?>>(adapter)
                    mAutoComTextViewStaus?.onItemClickListener =
                            OnItemClickListener { parent, view, position, id ->
                                AppUtils.hideSoftKeyboard(this@AllReportActivity)
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
//        AppUtils.hideSoftKeyboard(this@AllReportActivity)
        ioScope.launch {
            val mDevice = mDb?.dbDAO?.getWelcomeForm()?.officerDeviceName
            var pos = -1
            if (mApplicationList != null && mApplicationList.size > 0) {
                val mDropdownList = arrayOfNulls<String>(mApplicationList.size)
                for (i in mApplicationList.indices) {
                    mDropdownList[i] = mApplicationList[i].friendlyName.toString()
                    try {
                        if (!TextUtils.isEmpty(mDevice)) {
                            if (mApplicationList[i].friendlyName.equals(mDevice, ignoreCase = true)) {
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
//                        mAutoComTextViewDeviceId!!.setText(mDropdownList[pos])
//                        selectedDeViceId.mDeviceFriendlyName = mApplicationList[pos].friendlyName
//                        selectedDeViceId.mDeviceId = mApplicationList[pos].friendlyName
                    }
                    val adapter = ArrayAdapter(
                            this@AllReportActivity,
                            R.layout.row_dropdown_menu_popup_item,
                            mDropdownList
                    )
                    try {
                        mAutoComTextViewDeviceId!!.threshold = 1
                        mAutoComTextViewDeviceId!!.setAdapter<ArrayAdapter<String?>>(adapter)
                        //                mSelectedAgency = mApplicationList.get(pos).getAgency_name();
                        mAutoComTextViewDeviceId!!.onItemClickListener =
                                OnItemClickListener { parent, view, position, id ->
                                    mAutoComTextViewDeviceId?.error = null
//                                    val index = getIndexOfDevice(
//                                            mApplicationList,
//                                            parent.getItemAtPosition(position).toString()
//                                    )
//                                    selectedDeViceId.mDeviceFriendlyName =
//                                            mApplicationList[index].friendlyName
//                                    selectedDeViceId.mDeviceId = mApplicationList[index].friendlyName
//                                    AppUtils.hideSoftKeyboard(this@AllReportActivity)
//                                    mAutoComTextViewDeviceId!!.setText(mApplicationList[index].friendlyName)
//                                    AppUtils.hideSoftKeyboard(this@AllReportActivity)
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
//        AppUtils.hideSoftKeyboard(this@AllReportActivity)
        ioScope.launch {
            val mDevice = mDb?.dbDAO?.getWelcomeForm()?.officerDeviceName
            var pos = -1
            if (mApplicationList != null && mApplicationList.size > 0) {
                val mDropdownList = arrayOfNulls<String>(mApplicationList.size)
                for (i in mApplicationList.indices) {
                    mDropdownList[i] = mApplicationList[i].friendlyName.toString()
                    try {
                        if (!TextUtils.isEmpty(mDevice)) {
                            if (mApplicationList[i].friendlyName.equals(mDevice, ignoreCase = true)) {
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
                        mAutoComTextViewHandheldUnitNo!!.setText(mDropdownList[pos])
                        selectedDeViceId.mDeviceFriendlyName = mApplicationList[pos].friendlyName
                        selectedDeViceId.mDeviceId = mApplicationList[pos].friendlyName
                    }
                    val adapter = ArrayAdapter(
                            this@AllReportActivity,
                            R.layout.row_dropdown_menu_popup_item,
                            mDropdownList
                    )
                    try {
                        mAutoComTextViewDeviceId!!.threshold = 1
                        mAutoComTextViewDeviceId!!.setAdapter<ArrayAdapter<String?>>(adapter)
                        //                mSelectedAgency = mApplicationList.get(pos).getAgency_name();
                        mAutoComTextViewHandheldUnitNo!!.onItemClickListener =
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
        ioScope.launch {
            var pos = -1
            val mApplicationList = Singleton.getDataSetList(DATASET_SIDE_LIST, mDb)

            if (mApplicationList != null && mApplicationList.size > 0) {
                try {
                    Collections.sort(mApplicationList, object : Comparator<DatasetResponse?> {
                        override fun compare(lhs: DatasetResponse?, rhs: DatasetResponse?): Int {
                            return lhs?.sideName!!.nullSafety().compareTo(rhs?.sideName!!.nullSafety())
                        }
                    })
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                val mDropdownList = arrayOfNulls<String>(mApplicationList.size)
                for (i in mApplicationList.indices) {
                    mDropdownList[i] = mApplicationList[i].sideName.toString()
                }
                mAutoComTextViewDirection?.post {
                    val adapter = ArrayAdapter(
                            this@AllReportActivity,
                            R.layout.row_dropdown_menu_popup_item,
                            mDropdownList
                    )
                    try {
                        mAutoComTextViewDirection?.threshold = 1
                        mAutoComTextViewDirection?.setAdapter<ArrayAdapter<String?>>(adapter)
                        //mSelectedShiftStat = mApplicationList.get(pos);
                        mAutoComTextViewDirection?.onItemClickListener =
                                OnItemClickListener { parent, view, position, id -> // mSelectedShiftStat = mApplicationList.get(position);
                                    AppUtils.hideSoftKeyboard(this@AllReportActivity)

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
//        AppUtils.hideSoftKeyboard(this@AllReportActivity)

//        if (mUnitNumberList != null && mUnitNumberList.isNotEmpty()) {
        var unitList = mDeviceList
        if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLENDALE,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLENDALE_POLICE,ignoreCase = true))
        {
            unitList =  mUnitNumberList
        }
        if (unitList != null && unitList.isNotEmpty()) {
            mAutoComTextViewUnitNo!!.post {
                val adapter = ArrayAdapter(
                        this,
                        R.layout.row_dropdown_menu_popup_item,
                    unitList
                )
                try {
                    mAutoComTextViewUnitNo?.threshold = 1
                    mAutoComTextViewUnitNo?.setAdapter<ArrayAdapter<String?>>(adapter)
                    mAutoComTextViewUnitNo?.onItemClickListener =
                            OnItemClickListener { parent, view, position, id ->
                                AppUtils.hideSoftKeyboard(this@AllReportActivity)
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
         if (mApplicationList != null && mApplicationList.size > 0) {
            ioScope.launch {
                val mDropdownList = arrayOfNulls<String>(mApplicationList.size)
                for (i in mApplicationList.indices) {
                    mDropdownList[i] = mApplicationList[i].beatName.toString()
                }
                //  Arrays.sort(mDropdownList);
                mAutoComTextViewAssignedArea?.post {

                    val adapter = ArrayAdapter(this@AllReportActivity,
                            R.layout.row_dropdown_menu_popup_item, mDropdownList)
                    try {
                        mAutoComTextViewAssignedArea!!.threshold = 1
                        mAutoComTextViewAssignedArea!!.setAdapter<ArrayAdapter<String?>>(adapter)

                        mAutoComTextViewAssignedArea!!.onItemClickListener =
                                OnItemClickListener { parent, view, position, id ->
//                                    val index = mApplicationList.indexOfFirst{
//                                        it.beatName == mAutoComTextViewAssignedArea?.text.toString()
//                                    }
                                    mAutoComTextViewAssignedArea?.error = null
                                    AppUtils.hideSoftKeyboard(this@AllReportActivity)
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

    //set value to ture false dropdown
    private fun setDropdownBikeGlassess() {
        val pos = 0
        if (mPassFailedList != null && mPassFailedList.isNotEmpty()) {

            mAutoComTextViewBikeGlassess!!.post {
                val adapter = ArrayAdapter(
                    this,
                    R.layout.row_dropdown_menu_popup_item,
                    mPassFailedList
                )
                try {
                    mAutoComTextViewBikeGlassess?.threshold = 1
                    mAutoComTextViewBikeGlassess?.setAdapter<ArrayAdapter<String?>>(adapter)
                    mAutoComTextViewBikeGlassess?.onItemClickListener =
                        OnItemClickListener { parent, view, position, id ->
                            AppUtils.hideSoftKeyboard(this@AllReportActivity)
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

            mAutoComTextViewGloveVisualInspection!!.post {
                val adapter = ArrayAdapter(
                    this,
                    R.layout.row_dropdown_menu_popup_item,
                    mPassFailedList
                )
                try {
                    mAutoComTextViewGloveVisualInspection?.threshold = 1
                    mAutoComTextViewGloveVisualInspection?.setAdapter<ArrayAdapter<String?>>(adapter)
                    mAutoComTextViewGloveVisualInspection?.onItemClickListener =
                        OnItemClickListener { parent, view, position, id ->
                            AppUtils.hideSoftKeyboard(this@AllReportActivity)
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

            mAutoComTextViewBatteryCharge!!.post {
                val adapter = ArrayAdapter(
                    this,
                    R.layout.row_dropdown_menu_popup_item,
                    mBatterChargeList
                )
                try {
                    mAutoComTextViewBatteryCharge?.threshold = 1
                    mAutoComTextViewBatteryCharge?.setAdapter<ArrayAdapter<String?>>(adapter)
                    mAutoComTextViewBatteryCharge?.onItemClickListener =
                        OnItemClickListener { parent, view, position, id ->
                            AppUtils.hideSoftKeyboard(this@AllReportActivity)
                            mAutoComTextViewBatteryCharge?.error = null
                        }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
   /* //set value to side dropdown
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
                    this,
                    R.layout.row_dropdown_menu_popup_item,
                    mDropdownList
            )
            try {
                mEditTextViewTextViewSideOfState?.threshold = 1
                mEditTextViewTextViewSideOfState?.setAdapter<ArrayAdapter<String?>>(adapter)
                //mSelectedShiftStat = mApplicationList.get(pos);
                mEditTextViewTextViewSideOfState?.onItemClickListener =
                        OnItemClickListener { parent, view, position, id -> // mSelectedShiftStat = mApplicationList.get(position);
                            AppUtils.hideSoftKeyboard(this@BrokenMeterActivity)
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
                this,
                R.layout.row_dropdown_menu_popup_item,
                mApplicationList
        )
        try {
            mEditTextViewTextViewMeterOutage?.threshold = 1
            mEditTextViewTextViewMeterOutage?.setAdapter(adapter)
            //mSelectedShiftStat = mApplicationList.get(pos);
            mEditTextViewTextViewMeterOutage?.onItemClickListener =
                    OnItemClickListener { parent, view, position, id ->
                        AppUtils.hideSoftKeyboard(this@BrokenMeterActivity)
                        outageValue = meterOutageListValue[position]
                        outageTypeValue = meterOutageListTypeValue[position]
                    }
        } catch (e: Exception) {
        }
    }


*/


    private fun setRequest(isFrom : String) {
        if (NetworkCheck.isInternetAvailable(this@AllReportActivity)) {
            if(isFrom.equals("Broken Meter",ignoreCase = true)) {
                val mLat = sharedPreference.read(SharedPrefKey.LAT, "0.0")?.toDouble().nullSafety()
                val mLong = sharedPreference.read(SharedPrefKey.LONG, "0.0")?.toDouble().nullSafety()
                val brokenRequest = BrokenMeterReportRequest()
                brokenRequest.form = isFrom

                val locationDetail = LocationDetails()
                locationDetail!!.latitude = mLat
                locationDetail!!.longitude = mLong
                locationDetail!!.block = mAutoComTextViewBlock!!.text.toString()
                locationDetail!!.street = mAutoComTextViewStreet!!.text.toString()
                locationDetail!!.direction = mAutoComTextViewDirection!!.text.toString()
                brokenRequest.locationDetails = locationDetail


                val officerDetails = OfficerDetails()
                officerDetails!!.officerName = (mWelcomeFormData?.officerFirstName + " " + mWelcomeFormData?.officerLastName)
                officerDetails!!.shiftId = sharedPreference.read(SharedPrefKey.LOGIN_SHIFT, "").toString()
                officerDetails!!.badgeId = mWelcomeFormData?.officerBadgeId
                officerDetails!!.beat = mWelcomeFormData?.officerBeat
                brokenRequest.officerDetails = officerDetails

                val details = Details()
                details!!.deviceId = ""//mAutoComTextViewDeviceId!!.text.toString()
                details!!.meterNo = mAutoComTextViewMeterNo!!.text.toString()
                details!!.meterType = mAutoComTextViewMeterType!!.text.toString()
                details!!.meterLabelVisible = mAutoComTextViewMeterLabelVisible!!.text.toString().equals("YES", ignoreCase = true)
                details!!.digitalDisplayVisible = mAutoComTextViewDigitalDisplayVisible!!.text.toString().equals("YES", ignoreCase = true)
                details!!.coinJam = mAutoComTextViewCoinJam!!.text.toString().equals("YES", ignoreCase = true)
                details!!.creditCardJam = mAutoComTextViewCreditCardJam!!.text.toString().equals("YES", ignoreCase = true)
                details!!.comments = mAutoComTextViewComments!!.text.toString()
                details!!.status = ""
                brokenRequest.details = details

                brokenMeterReportViewModel?.hitBrokenMeterReportSubmitApi(brokenRequest)
                try {
                    if(LogUtil.WRITE_ALL_API_IN_LOGS_FILE) {
                        ApiLogsClass.writeApiPayloadTex(
                            BaseApplication.instance?.applicationContext!!,
                            "---------Broken Meter Report--------"
                        )
                        ApiLogsClass.writeApiPayloadTex(
                            BaseApplication.instance?.applicationContext!!,
                            "REQUEST: " + ObjectMapperProvider.instance.writeValueAsString(
                                brokenRequest
                            )
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }else if(isFrom.equals("Curb",ignoreCase = true))
            {

                val handler = Handler(Looper.getMainLooper())
                handler.postDelayed({
                    if (NetworkCheck.isInternetAvailable(this@AllReportActivity)) {
                        mUploadImageViewModel!!.callUploadScannedImagesAPI(
                                this@AllReportActivity,
                                createImagesNameList(bannerList),
                                createImageMultipart(bannerList), API_CONSTANT_DOWNLOAD_TYPE_REPORT_IMAGES,
                                "LprDetails2Activity")
                    } else {
                        LogUtil.printToastMSG(
                                this@AllReportActivity,
                                getString(R.string.err_msg_connection_was_refused)
                        )
                    }
                }, 300)


            }
            else if(isFrom.equals("Full Time Report",ignoreCase = true))
            {
                val mLat = sharedPreference.read(SharedPrefKey.LAT, "0.0")?.toDouble().nullSafety()
                val mLong = sharedPreference.read(SharedPrefKey.LONG, "0.0")?.toDouble().nullSafety()
                val fullTimeRequest = FullTimeRequest()
                fullTimeRequest.form = "FULL TIME"

                val locationDetail = LocationDetailsFulltime()
                locationDetail!!.latitude = mLat
                locationDetail!!.longitude = mLong
                locationDetail!!.block = ""//mAutoComTextViewBlock!!.text.toString()
                locationDetail!!.street = ""//mAutoComTextViewStreet!!.text.toString()

                fullTimeRequest.locationDetailsFulltime = locationDetail


                val officerDetails = OfficerDetailsFulltime()
                officerDetails!!.officerName = (mWelcomeFormData?.officerFirstName + " " + mWelcomeFormData?.officerLastName)
                officerDetails!!.shiftId = sharedPreference.read(SharedPrefKey.LOGIN_SHIFT, "").toString()
                officerDetails!!.badgeId = mWelcomeFormData?.officerBadgeId
                officerDetails!!.beat = mWelcomeFormData?.officerBeat
                fullTimeRequest.officerDetailsFulltime = officerDetails

                val details = DetailsFulltime()
                details!!.deviceId = mAutoComTextViewDeviceId!!.text.toString()
                details!!.date = ""// mAutoComTextViewDate!!.text.toString()
                details!!.dutyHours = mAutoComTextViewDutyHours!!.text.toString()
                details!!.officer = ""//mAutoComTextViewOfficer!!.text.toString()
                details!!.lunchTaken = (mAutoComTextViewLunchTaken!!.tag.toString())
                details!!.first10MinBreak = (mAutoComTextViewFirst10MinBreak!!.tag.toString())
                details!!.second10MinBreak = (mAutoComTextViewSecond10MinBreak!!.tag.toString())
                details!!.unitNo = mAutoComTextViewUnitNo!!.text.toString()
                details!!.assignedArea = mAutoComTextViewAssignedArea!!.text.toString()
                details!!.ossiDeviceNo = ""//mAutoComTextViewOssiDeviceNo!!.text.toString()
                details!!.handheldUnitNo = ""//mAutoComTextViewHandheldUnitNo!!.text.toString()
                details!!.citationsIssued = mAutoComTextViewCitationsIssued!!.text.toString()
                details!!.specialEnforcementRequest = mAutoComTextViewSpecialEnforcementRequest!!.text.toString()
                details!!.status = ""//mAutoComTextViewStaus!!.text.toString()
                fullTimeRequest.detailsFulltime = details
                fullTimeReportViewModel?.hitFullTimeReportSubmitApi(fullTimeRequest)
                try {
                    if(LogUtil.WRITE_ALL_API_IN_LOGS_FILE) {
                        ApiLogsClass.writeApiPayloadTex(
                            BaseApplication.instance?.applicationContext!!,
                            "---------Full Time Report--------"
                        )
                        ApiLogsClass.writeApiPayloadTex(
                            BaseApplication.instance?.applicationContext!!,
                            "REQUEST: " + ObjectMapperProvider.instance.writeValueAsString(
                                fullTimeRequest
                            )
                        )
                    }
                    } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else if(isFrom.equals("Part Time Report",ignoreCase = true))
            {
                val mLat = sharedPreference.read(SharedPrefKey.LAT, "0.0")?.toDouble().nullSafety()
                val mLong = sharedPreference.read(SharedPrefKey.LONG, "0.0")?.toDouble().nullSafety()
                val fullTimeRequest = FullTimeRequest()
                fullTimeRequest.form = "PART TIME"

                val locationDetail = LocationDetailsFulltime()
                locationDetail!!.latitude = mLat
                locationDetail!!.longitude = mLong
                locationDetail!!.block = ""//mAutoComTextViewBlock!!.text.toString()
                locationDetail!!.street = ""//mAutoComTextViewStreet!!.text.toString()
                fullTimeRequest.locationDetailsFulltime = locationDetail


                val officerDetails = OfficerDetailsFulltime()
                officerDetails!!.officerName = (mWelcomeFormData?.officerFirstName + " " + mWelcomeFormData?.officerLastName)
                officerDetails!!.shiftId = sharedPreference.read(SharedPrefKey.LOGIN_SHIFT, "").toString()
                officerDetails!!.badgeId = mWelcomeFormData?.officerBadgeId
                officerDetails!!.beat = mWelcomeFormData?.officerBeat
                fullTimeRequest.officerDetailsFulltime = officerDetails

                val details = DetailsFulltime()
                details!!.deviceId = mAutoComTextViewDeviceId!!.text.toString()
                details!!.date = ""// mAutoComTextViewDate!!.text.toString()
                details!!.dutyHours = mAutoComTextViewDutyHours!!.text.toString()
                details!!.officer = ""//mAutoComTextViewOfficer!!.text.toString()
//                details!!.lunchTaken ="remove"// mAutoComTextViewLunchTaken!!.text.toString()
                details!!.first10MinBreak = (mAutoComTextViewFirst10MinBreak!!.tag.toString())
                details!!.second10MinBreak = (mAutoComTextViewSecond10MinBreak!!.tag.toString())
                details!!.unitNo = mAutoComTextViewUnitNo!!.text.toString()
                details!!.assignedArea = mAutoComTextViewAssignedArea!!.text.toString()
                details!!.ossiDeviceNo = ""//mAutoComTextViewOssiDeviceNo!!.text.toString()
                details!!.handheldUnitNo = ""//mAutoComTextViewHandheldUnitNo!!.text.toString()
                details!!.citationsIssued = mAutoComTextViewCitationsIssued!!.text.toString()
                details!!.specialEnforcementRequest = mAutoComTextViewSpecialEnforcementRequest!!.text.toString()
                details!!.status = ""//mAutoComTextViewStaus!!.text.toString()
                fullTimeRequest.detailsFulltime = details
                partTimeReportViewModel?.hitPartTimeReportSubmitApi(fullTimeRequest)

                try {
                    if(LogUtil.WRITE_ALL_API_IN_LOGS_FILE) {
                        ApiLogsClass.writeApiPayloadTex(
                            BaseApplication.instance?.applicationContext!!,
                            "---------Part Time Report--------"
                        )
                        ApiLogsClass.writeApiPayloadTex(
                            BaseApplication.instance?.applicationContext!!,
                            "REQUEST: " + ObjectMapperProvider.instance.writeValueAsString(
                                fullTimeRequest
                            )
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else if(isFrom.equals("Hand Held Malfunction",ignoreCase = true))
            {
                val mLat = sharedPreference.read(SharedPrefKey.LAT, "0.0")?.toDouble().nullSafety()
                val mLong = sharedPreference.read(SharedPrefKey.LONG, "0.0")?.toDouble().nullSafety()
                val handHeldMalFunctionsRequest = HandHeldMalFunctionsRequest()
                handHeldMalFunctionsRequest.form = isFrom

                val locationDetail = LocationDetailsHandHeldMalfunctions()
                locationDetail!!.latitude = mLat
                locationDetail!!.longitude = mLong
                locationDetail!!.block = ""//mAutoComTextViewBlock!!.text.toString()
                locationDetail!!.street = ""//mAutoComTextViewStreet!!.text.toString()
                handHeldMalFunctionsRequest.locationDetailsHandHeldMalfunctions = locationDetail


                val officerDetails = OfficerDetailsHandHeldMalfunctions()
                officerDetails!!.officerName = (mWelcomeFormData?.officerFirstName + " " + mWelcomeFormData?.officerLastName)
                officerDetails!!.shiftId = sharedPreference.read(SharedPrefKey.LOGIN_SHIFT, "").toString()
                officerDetails!!.badgeId = mWelcomeFormData?.officerBadgeId
                officerDetails!!.beat = mWelcomeFormData?.officerBeat
                handHeldMalFunctionsRequest.officerDetailsHandHeldMalfunctions = officerDetails

                val details = DetailsHandHeldMalfunctions()
                details!!.deviceId = mAutoComTextViewDeviceId!!.text.toString()
                details!!.handHeldUnitNo = ""//mAutoComTextViewHandheldUnitNo!!.text.toString()
                details!!.triedToRestartHandHeld = mAutoComTextViewTriedToRestartHandHeld!!.text.toString().equals("YES", ignoreCase = true)
                details!!.printingCorrectly = mAutoComTextViewPrintingCorrectly!!.text.toString().equals("YES", ignoreCase = true)
                details!!.overheating = mAutoComTextViewOverHeating!!.text.toString().equals("YES", ignoreCase = true)
                details!!.batteryHoldCharge = mAutoComTextViewBatteryHoldCharge!!.text.toString().equals("YES", ignoreCase = true)
                details!!.internetConnectivity = mAutoComTextViewInternetConnectivity!!.text.toString().equals("YES", ignoreCase = true)
                details!!.describeHandHeldMalfunctionInDetail = mAutoComTextViewDescribeHandHeldMalfunctionInDetail!!.text.toString()
                details!!.status = mAutoComTextViewStaus!!.text.toString()
                handHeldMalFunctionsRequest.detailsHandHeldMalfunctions = details
                handHeldMalFunctionsViewModel?.hitHandHeldMalFunctionsReportAPI(handHeldMalFunctionsRequest)
                try {
                    if(LogUtil.WRITE_ALL_API_IN_LOGS_FILE) {
                        ApiLogsClass.writeApiPayloadTex(
                            BaseApplication.instance?.applicationContext!!,
                            "---------Hand Held Malfunction Report--------"
                        )
                        ApiLogsClass.writeApiPayloadTex(
                            BaseApplication.instance?.applicationContext!!,
                            "REQUEST: " + ObjectMapperProvider.instance.writeValueAsString(
                                handHeldMalFunctionsRequest
                            )
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            else if(isFrom.equals("Sign Report",ignoreCase = true))
            {

                val handler = Handler(Looper.getMainLooper())
                handler.postDelayed({
                    if (NetworkCheck.isInternetAvailable(this@AllReportActivity)) {
                        mUploadImageViewModel!!.callUploadScannedImagesAPI(
                                this@AllReportActivity,
                                createImagesNameList(bannerList),
                                createImageMultipart(bannerList), API_CONSTANT_DOWNLOAD_TYPE_REPORT_IMAGES,
                                "LprDetails2Activity")
                    } else {
                        LogUtil.printToastMSG(
                                this@AllReportActivity,
                                getString(R.string.err_msg_connection_was_refused)
                        )
                    }
                }, 300)

            }else if(isFrom.equals("Vehicle Inspection",ignoreCase = true))
            {

                val handler = Handler(Looper.getMainLooper())
                handler.postDelayed({
                    if (NetworkCheck.isInternetAvailable(this@AllReportActivity)) {
                        mUploadImageViewModel!!.callUploadScannedImagesAPI(
                                this@AllReportActivity,
                                createImagesNameList(bannerList),
                                createImageMultipart(bannerList), API_CONSTANT_DOWNLOAD_TYPE_REPORT_IMAGES,
                                "LprDetails2Activity")
                    } else {
                        LogUtil.printToastMSG(
                                this@AllReportActivity,
                                getString(R.string.err_msg_connection_was_refused)
                        )
                    }
                }, 300)

            }else if(isFrom.equals("72 Hour Marked Vehicles",ignoreCase = true))
            {

                val handler = Handler(Looper.getMainLooper())
                handler.postDelayed({
                    if (NetworkCheck.isInternetAvailable(this@AllReportActivity)) {
                        mUploadImageViewModel!!.callUploadScannedImagesAPI(
                                this@AllReportActivity,
                                createImagesNameList(bannerList),
                                createImageMultipart(bannerList), API_CONSTANT_DOWNLOAD_TYPE_REPORT_IMAGES,
                                "LprDetails2Activity")
                    } else {
                        LogUtil.printToastMSG(
                                this@AllReportActivity,
                                getString(R.string.err_msg_connection_was_refused)
                        )
                    }
                }, 300)

            }else if(fromForm.equals("Bike Inspection",ignoreCase = true)){

                val handler = Handler(Looper.getMainLooper())
                handler.postDelayed({
                    if (NetworkCheck.isInternetAvailable(this@AllReportActivity)) {
                        mUploadImageViewModel!!.callUploadScannedImagesAPI(
                                this@AllReportActivity,
                                createImagesNameList(bannerList),
                                createImageMultipart(bannerList), API_CONSTANT_DOWNLOAD_TYPE_REPORT_IMAGES,
                                "LprDetails2Activity")
                    } else {
                        LogUtil.printToastMSG(
                                this@AllReportActivity,
                                getString(R.string.err_msg_connection_was_refused)
                        )
                    }
                }, 300)

            }
            else if(fromForm.equals("EOW Supervisor Shift Report",ignoreCase = true))
            {
                val mLat = sharedPreference.read(SharedPrefKey.LAT, "0.0")?.toDouble().nullSafety()
                val mLong = sharedPreference.read(SharedPrefKey.LONG, "0.0")?.toDouble().nullSafety()
                val supervisorReportRequest = SupervisorReportRequest()
                supervisorReportRequest.form = "Supervisor Report"

                val locationDetail = LocationDetailsSupervisor()
                locationDetail!!.latitude = mLat
                locationDetail!!.longitude = mLong
                locationDetail!!.block = ""//mAutoComTextViewBlock!!.text.toString()
                locationDetail!!.street = ""//mAutoComTextViewStreet!!.text.toString()
                supervisorReportRequest.locationDetailsSupervisor = locationDetail


                val officerDetails = OfficerDetailsSupervisor()
                officerDetails!!.officerName = (mWelcomeFormData?.officerFirstName + " " + mWelcomeFormData?.officerLastName)
                officerDetails!!.shiftId = sharedPreference.read(SharedPrefKey.LOGIN_SHIFT, "").toString()
                officerDetails!!.badgeId = mWelcomeFormData?.officerBadgeId
                officerDetails!!.beat = mWelcomeFormData?.officerBeat
                supervisorReportRequest.officerDetailsSupervisor = officerDetails

                val details = DetailsSupervisor()
//                details!!.deviceId = "remove"//mAutoComTextViewDeviceId!!.text.toString()
//                details!!.date = "remove"//mAutoComTextViewHandheldUnitNo!!.text.toString()
//                details!!.dutyHours = "remove"//mAutoComTextViewTriedToRestartHandHeld!!.text.toString().equals("YES", ignoreCase = true)
//                details!!.officer = "remove"//mAutoComTextViewPrintingCorrectly!!.text.toString().equals("YES", ignoreCase = true)
                details!!.lunchTaken = (mAutoComTextViewLunchTaken!!.tag.toString())
                details!!.first10MinBreak = (mAutoComTextViewFirst10MinBreak!!.tag.toString())
                details!!.second10MinBreak = (mAutoComTextViewSecond10MinBreak!!.tag.toString())
//                details!!.unitNo = "remove"//mAutoComTextViewDescribeHandHeldMalfunctionInDetail!!.text.toString()
//                details!!.assignedArea = "remove"
//                details!!.ossiDeviceNo = "remove"
//                details!!.handheldUnitNo = "remove"
                details!!.citationsIssued = mAutoComTextViewCitationsIssued!!.text.toString()
//                details!!.specialEnforcementRequest = "remove"//mAutoComTextViewCitationsIssued!!.text.toString()
                details!!.shiftSummaryComments = mAutoComTextViewComments!!.text.toString()
                details!!.warningsIssued = mAutoComTextViewWarningsIssued!!.text.toString()
                details!!.warningsIssued = mAutoComTextViewWarningsIssued!!.text.toString()
                details!!.residentComplaints = mAutoComTextViewResidentComplaints!!.text.toString()
                details!!.complaintsTowardsOfficers = mAutoComTextViewComplaintsTowardsOfficer!!.text.toString()
                details!!.totalEnforcementPersonnel = mAutoComTextViewTotalEnforcementPersonnel!!.text.toString()
//                details!!.status = "remove"//mAutoComTextViewStaus!!.text.toString()
                supervisorReportRequest.detailsSupervisor = details
                supervisorReportsViewModel?.hitSupervisorReportAPI(supervisorReportRequest)

                try {
                    if(LogUtil.WRITE_ALL_API_IN_LOGS_FILE) {
                        ApiLogsClass.writeApiPayloadTex(
                            BaseApplication.instance?.applicationContext!!,
                            "---------EOW Supervisor Shift Report--------"
                        )
                        ApiLogsClass.writeApiPayloadTex(
                            BaseApplication.instance?.applicationContext!!,
                            "REQUEST: " + ObjectMapperProvider.instance.writeValueAsString(
                                supervisorReportRequest
                            )
                        )
                    }
                    } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            else if(fromForm.equals("Special Assignment Report",ignoreCase = true))
            {
                val mLat = sharedPreference.read(SharedPrefKey.LAT, "0.0")?.toDouble().nullSafety()
                val mLong = sharedPreference.read(SharedPrefKey.LONG, "0.0")?.toDouble().nullSafety()
                val supervisorReportRequest = SpecialAssignementRequest()
                supervisorReportRequest.form = "Special Assignment Report"

                val locationDetail = LocationDetailsSpecial()
                locationDetail!!.latitude = mLat
                locationDetail!!.longitude = mLong
                locationDetail!!.block = mAutoComTextViewBlock!!.text.toString()
                locationDetail!!.street = mAutoComTextViewStreet!!.text.toString()
                locationDetail!!.direction = mAutoComTextViewDirection!!.text.toString()
                supervisorReportRequest.locationDetails = locationDetail


                val officerDetails = OfficerDetailsSpecial()
                officerDetails!!.officerName = (mWelcomeFormData?.officerFirstName + " " + mWelcomeFormData?.officerLastName)
                officerDetails!!.shiftId = sharedPreference.read(SharedPrefKey.LOGIN_SHIFT, "").toString()
                officerDetails!!.badgeId = mWelcomeFormData?.officerBadgeId
                officerDetails!!.beat = mWelcomeFormData?.officerBeat
                supervisorReportRequest.officerDetails = officerDetails

                val details = DetailsSpecial()
                details!!.lunchTaken = ""//AppUtils.timeFormatForReport(mAutoComTextViewLunchTaken!!.tag.toString())
                details!!.citationsIssued = mAutoComTextViewCitationsIssued!!.text.toString()
                details!!.shiftSummaryComments = mAutoComTextViewComments!!.text.toString()
                details!!.comments = mAutoComTextViewComments!!.text.toString()
                details!!.warningsIssued = mAutoComTextViewWarningsIssued!!.text.toString()
                details!!.warningsIssued = mAutoComTextViewWarningsIssued!!.text.toString()
//                if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAMETRO,ignoreCase = true)) {
//                    details!!.timeMarkedAt = mAutoComTextViewTimeMark!!.text.toString()
//                }else {
                    details!!.timeMarkedAt =
                        if (mAutoComTextViewTimeMark!!.tag != null) mAutoComTextViewTimeMark!!.tag.toString() else ""//mAutoComTextViewResidentComplaints!!.text.toString()
//                }
                details!!.timeMarkedAt2 = if(mAutoComTextViewTimeMark2!!.tag!=null) mAutoComTextViewTimeMark2!!.tag.toString() else ""//mAutoComTextViewResidentComplaints!!.text.toString()
                details!!.timeMarkedAt3 = if(mAutoComTextViewTimeMark3!!.tag!=null) mAutoComTextViewTimeMark3!!.tag.toString() else ""//mAutoComTextViewResidentComplaints!!.text.toString()
                details!!.timeMarkedAt4 = if(mAutoComTextViewTimeMark4!!.tag!=null) mAutoComTextViewTimeMark4!!.tag.toString() else ""//mAutoComTextViewResidentComplaints!!.text.toString()
                details!!.vehiclesMarked = mAutoComTextViewVehicleMark!!.text.toString()
                details!!.violationDescriptions = violationDescription
                supervisorReportRequest.details = details
                supervisorReportsViewModel?.hitSpecialAssignmentReportAPI(supervisorReportRequest)

                try {
                    if(LogUtil.WRITE_ALL_API_IN_LOGS_FILE) {
                        ApiLogsClass.writeApiPayloadTex(
                            BaseApplication.instance?.applicationContext!!,
                            "---------Special Assignment Report--------"
                        )
                        ApiLogsClass.writeApiPayloadTex(
                            BaseApplication.instance?.applicationContext!!,
                            "REQUEST: " + ObjectMapperProvider.instance.writeValueAsString(
                                supervisorReportRequest
                            )
                        )
                    }
                    } catch (e: Exception) {
                    e.printStackTrace()
                }
            }else if(fromForm.equals("Lot Inspection Report",ignoreCase = true)){
                val mLat = sharedPreference.read(SharedPrefKey.LAT, "0.0")?.toDouble().nullSafety()
                val mLong = sharedPreference.read(SharedPrefKey.LONG, "0.0")?.toDouble().nullSafety()
                val lotInspectionRequest = LotInspectionRequest()
                lotInspectionRequest.form = fromForm

                val locationDetail = LocationDetailsLotInspection()
                locationDetail!!.latitude = mLat
                locationDetail!!.longitude = mLong
//                locationDetail!!.block = mAutoComTextViewBlock!!.text.toString()
//                locationDetail!!.street = mAutoComTextViewStreet!!.text.toString()
//                locationDetail!!.direction = mAutoComTextViewDirection!!.text.toString()
                locationDetail!!.zone = mAutoComTextViewPBCZone!!.text.toString()
                lotInspectionRequest.locationDetails = locationDetail


                val officerDetails = OfficerDetailsLotInspection()
                officerDetails!!.officerName = (mWelcomeFormData?.officerFirstName + " " + mWelcomeFormData?.officerLastName)
                officerDetails!!.shiftId = sharedPreference.read(SharedPrefKey.LOGIN_SHIFT, "").toString()
                officerDetails!!.badgeId = mWelcomeFormData?.officerBadgeId
                officerDetails!!.beat = mWelcomeFormData?.officerBeat
                lotInspectionRequest.officerDetails = officerDetails

                val details = DetailsLotInspection()
                details!!.reportNumber = mEditTextReportNumber!!.text.toString()
                details!!.zone = mAutoComTextViewPBCZone!!.text.toString()
                details!!.allSignsReported = mAutoComTextViewInternetConnectivity!!.text.toString().equals("YES", ignoreCase = true)
                details!!.allPaystationsReported = mAutoComTextViewMeterLabelVisible!!.text.toString().equals("YES", ignoreCase = true)
                details!!.anyHomelessReported = mAutoComTextViewDigitalDisplayVisible!!.text.toString().equals("YES", ignoreCase = true)
                details!!.anySafetyIssuesReported = mAutoComTextViewCoinJam!!.text.toString().equals("YES", ignoreCase = true)
                details!!.line = mAutoComTextViewLine!!.text.toString()

                lotInspectionRequest.details = details
                reportServicesViewModel?.hitLotInspectionReportSubmitApi(lotInspectionRequest)
                try {
                    if(LogUtil.WRITE_ALL_API_IN_LOGS_FILE) {
                        ApiLogsClass.writeApiPayloadTex(
                            BaseApplication.instance?.applicationContext!!,
                            "---------Lot Inspection  Report--------"
                        )
                        ApiLogsClass.writeApiPayloadTex(
                            BaseApplication.instance?.applicationContext!!,
                            "REQUEST: " + ObjectMapperProvider.instance.writeValueAsString(
                                lotInspectionRequest
                            )
                        )

                    }} catch (e: Exception) {
                    e.printStackTrace()
                }
            }else if(fromForm.equals("Lot Count Vio Rate Report",ignoreCase = true)){
                val mLat = sharedPreference.read(SharedPrefKey.LAT, "0.0")?.toDouble().nullSafety()
                val mLong = sharedPreference.read(SharedPrefKey.LONG, "0.0")?.toDouble().nullSafety()
                val nflRequest = LotCountVioRateRequest()
                nflRequest.form = fromForm

                val locationDetail = LocationDetailsHard()
                locationDetail!!.latitude = mLat
                locationDetail!!.longitude = mLong
//            locationDetail!!.block = mAutoComTextViewBlock!!.text.toString()
//            locationDetail!!.street = mAutoComTextViewStreet!!.text.toString()
                locationDetail!!.zone = mAutoComTextViewPBCZone!!.text.toString()
                nflRequest.locationDetails = locationDetail


                val officerDetails = OfficerDetailsHard()
                officerDetails!!.officerName = (mWelcomeFormData?.officerFirstName + " " + mWelcomeFormData?.officerLastName)
                officerDetails!!.shiftId = sharedPreference.read(SharedPrefKey.LOGIN_SHIFT, "").toString()
                officerDetails!!.badgeId = mWelcomeFormData?.officerBadgeId
                officerDetails!!.beat = mWelcomeFormData?.officerBeat
                nflRequest.officerDetails = officerDetails

                val details = DetailsHard()
                details!!.reportNumber = mEditTextReportNumber!!.text.toString()
                details!!.zone = mAutoComTextViewPBCZone!!.text.toString()
                details!!.meter = if(mAutoComTextViewMeterNo!=null)mAutoComTextViewMeterNo!!.text.toString() else " "
                details!!.carSpace = (mAutoComTextViewField1!!.text.toString())
                details!!.carsCounted = (mAutoComTextViewCarCount!!.text.toString())
                details!!.emptySpaces = (mAutoComTextViewEmptySpace!!.text.toString())
                details!!.numberOfViolatingVehicles = (mAutoComTextViewNumberOfViolatingVehicles!!.text.toString())
                details!!.violationRate = mAutoComTextViewViolationRate!!.text.toString()
                details!!.comments = mAutoComTextViewComments!!.text.toString()
                details!!.line = mAutoComTextViewLine!!.text.toString()

                try {
//                    details!!.photo1 = mImagesLinks!![0]
//                    details!!.photo2 = mImagesLinks!![1]
//                    details!!.photo3 = mImagesLinks!![2]
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                nflRequest.details = details
                reportServicesViewModel?.hitLotCountVioRateReportSubmitApi(nflRequest)
                try {
                    if(LogUtil.WRITE_ALL_API_IN_LOGS_FILE) {
                        ApiLogsClass.writeApiPayloadTex(
                            BaseApplication.instance?.applicationContext!!,
                            "---------Lot Count Vio Rate  Report--------"
                        )
                        ApiLogsClass.writeApiPayloadTex(
                            BaseApplication.instance?.applicationContext!!,
                            "REQUEST: " + ObjectMapperProvider.instance.writeValueAsString(
                                nflRequest
                            )
                        )
                    }
                    } catch (e: Exception) {
                    e.printStackTrace()
                }
            }else if(fromForm.equals("72hrs Notice To Tow Report",ignoreCase = true)||
                fromForm.equals("Signage Report",ignoreCase = true)||
                fromForm.equals("Tow Report",ignoreCase = true)||
                fromForm.equals("Pay Station Report",ignoreCase = true)||
                fromForm.equals("NFL Special Assignment Report",ignoreCase = true)||
                fromForm.equals("SPECIAL EVENT REPORT",ignoreCase = true)||
                fromForm.equals("PCH Daily Updates",ignoreCase = true)||
                fromForm.equals("Homeless Report",ignoreCase = true)||
                fromForm.equals("Work Order Report",ignoreCase = true)||
                fromForm.equals("Safety Report Immediate Attention Required",ignoreCase = true)||
                fromForm.equals("Trash Lot Maintenance Report",ignoreCase = true)||
                fromForm.equals("Sign Off Report",ignoreCase = true)) {
                val handler = Handler(Looper.getMainLooper())
                handler.postDelayed({
                    if (NetworkCheck.isInternetAvailable(this@AllReportActivity)) {
                        mUploadImageViewModel!!.callUploadScannedImagesAPI(
                            this@AllReportActivity,
                            createImagesNameList(bannerList),
                            createImageMultipart(bannerList),
                            API_CONSTANT_DOWNLOAD_TYPE_REPORT_IMAGES,
                            "LprDetails2Activity"
                        )
                    } else {
                        LogUtil.printToastMSG(
                            this@AllReportActivity,
                            getString(R.string.err_msg_connection_was_refused)
                        )
                    }
                }, 300)
            }
        }else {
            LogUtil.printToastMSG(
                    this@AllReportActivity,
                    getString(R.string.err_msg_connection_was_refused)
            )
        }
    }

    private fun sendRequestWithImage(){
        if(fromForm.equals("Curb",ignoreCase = true)) {
            val mLat = sharedPreference.read(SharedPrefKey.LAT, "0.0")?.toDouble().nullSafety()
            val mLong = sharedPreference.read(SharedPrefKey.LONG, "0.0")?.toDouble().nullSafety()
            val curbRequest = CurbRequest()
            curbRequest.form = fromForm

            val locationDetail = LocationDetailsCurb()
            locationDetail!!.latitude = mLat
            locationDetail!!.longitude = mLong
            locationDetail!!.block = mAutoComTextViewBlock!!.text.toString()
            locationDetail!!.street = mAutoComTextViewStreet!!.text.toString()
            locationDetail!!.direction = mAutoComTextViewDirection!!.text.toString()
            curbRequest.locationDetailsCurb = locationDetail

            val officerDetails = OfficerDetailsCurb()
            officerDetails!!.officerName = (mWelcomeFormData?.officerFirstName + " " + mWelcomeFormData?.officerLastName)
            officerDetails!!.shiftId = sharedPreference.read(SharedPrefKey.LOGIN_SHIFT, "").toString()
            officerDetails!!.badgeId = mWelcomeFormData?.officerBadgeId
            officerDetails!!.beat = mWelcomeFormData?.officerBeat
            curbRequest.officerDetailsCurb = officerDetails

            val details = DetailsCurb()
            details!!.deviceId = ""//mAutoComTextViewDeviceId!!.text.toString()
            details!!.enforceable = mAutoComTextViewEnforceable!!.text.toString().equals("YES", ignoreCase = true)
            details!!.curbColor = mAutoComTextViewCurbColor!!.text.toString()
            details!!.comments = mAutoComTextViewComments!!.text.toString()
            details!!.status = ""//mAutoComTextViewStaus!!.text.toString()

            details!!.pictureOfCurb = mImagesLinks!![0]
            curbRequest.detailsCurb = details

            curbReportViewModel?.hitCurbReportSubmitApi(curbRequest)
            try {
                if(LogUtil.WRITE_ALL_API_IN_LOGS_FILE) {
                    ApiLogsClass.writeApiPayloadTex(
                        BaseApplication.instance?.applicationContext!!,
                        "---------Curb Report--------"
                    )
                    ApiLogsClass.writeApiPayloadTex(
                        BaseApplication.instance?.applicationContext!!,
                        "REQUEST: " + ObjectMapperProvider.instance.writeValueAsString(curbRequest)
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }else if(fromForm.equals("Sign Report",ignoreCase = true)){
            val mLat = sharedPreference.read(SharedPrefKey.LAT, "0.0")?.toDouble().nullSafety()
            val mLong = sharedPreference.read(SharedPrefKey.LONG, "0.0")?.toDouble().nullSafety()
            val signReportRequest = SignReportRequest()
            signReportRequest.form = fromForm

            val locationDetail = LocationDetailsSign()
            locationDetail!!.latitude = mLat
            locationDetail!!.longitude = mLong
            locationDetail!!.block = mAutoComTextViewBlock!!.text.toString()
            locationDetail!!.street = mAutoComTextViewStreet!!.text.toString()
            locationDetail!!.direction = mAutoComTextViewDirection!!.text.toString()
            signReportRequest.locationDetailsSign = locationDetail


            val officerDetails = OfficerDetailsSign()
            officerDetails!!.officerName = (mWelcomeFormData?.officerFirstName + " " + mWelcomeFormData?.officerLastName)
            officerDetails!!.shiftId = sharedPreference.read(SharedPrefKey.LOGIN_SHIFT, "").toString()
            officerDetails!!.badgeId = mWelcomeFormData?.officerBadgeId
            officerDetails!!.beat = mWelcomeFormData?.officerBeat
            signReportRequest.officerDetailsSign = officerDetails

            val details = DetailsSign()
            details!!.deviceId = ""//mAutoComTextViewDeviceId!!.text.toString()
            details!!.enforceable = mAutoComTextViewEnforceable!!.text.toString().equals("YES", ignoreCase = true)
            details!!.graffiti = mAutoComTextViewGraffiti!!.text.toString().equals("YES", ignoreCase = true)
            details!!.missingSign = mAutoComTextViewMissingSign!!.text.toString().equals("YES", ignoreCase = true)
            details!!.comments = mAutoComTextViewComments!!.text.toString()
            details!!.status = ""//mAutoComTextViewStaus!!.text.toString()
            details!!.picture = mImagesLinks!![0]
            signReportRequest.detailsSign = details
            signReportViewModel?.hitSignReportAPI(signReportRequest)
            try {
                if(LogUtil.WRITE_ALL_API_IN_LOGS_FILE) {
                    ApiLogsClass.writeApiPayloadTex(
                        BaseApplication.instance?.applicationContext!!,
                        "---------Sign Report--------"
                    )
                    ApiLogsClass.writeApiPayloadTex(
                        BaseApplication.instance?.applicationContext!!,
                        "REQUEST: " + ObjectMapperProvider.instance.writeValueAsString(
                            signReportRequest
                        )
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }else if(fromForm.equals("Vehicle Inspection",ignoreCase = true)){
            val mLat = sharedPreference.read(SharedPrefKey.LAT, "0.0")?.toDouble().nullSafety()
            val mLong = sharedPreference.read(SharedPrefKey.LONG, "0.0")?.toDouble().nullSafety()
            val vehicleInspectionRequest = VehicleInspectionRequest()
            vehicleInspectionRequest.form = fromForm

            val locationDetail = LocationDetailsVehicleInspection()
            locationDetail!!.latitude = mLat
            locationDetail!!.longitude = mLong
            locationDetail!!.block = ""//mAutoComTextViewBlock!!.text.toString()
            locationDetail!!.street = ""//mAutoComTextViewStreet!!.text.toString()
            vehicleInspectionRequest.locationDetailsVehicleInspection = locationDetail


            val officerDetails = OfficerDetailsVehicleInspection()
            officerDetails!!.officerName = (mWelcomeFormData?.officerFirstName + " " + mWelcomeFormData?.officerLastName)
            officerDetails!!.shiftId = sharedPreference.read(SharedPrefKey.LOGIN_SHIFT, "").toString()
            officerDetails!!.badgeId = mWelcomeFormData?.officerBadgeId
            officerDetails!!.beat = mWelcomeFormData?.officerBeat
            vehicleInspectionRequest.officerDetailsVehicleInspection = officerDetails

            val details = DetailsVehicleInspection()
            details!!.deviceId = ""//mAutoComTextViewDeviceId!!.text.toString()
            details!!.officer = ""//mAutoComTextViewOfficer!!.text.toString()
            details!!.vehicle = mAutoComTextViewVehicle!!.text.toString()
            details!!.startingMileage = mAutoComTextViewStartingMileage!!.text.toString()
            details!!.gasLevel = mAutoComTextViewGasLevel!!.text.toString()
            details!!.lightBar = mAutoComTextViewLightBar!!.text.toString()
            details!!.dashboardIndications = mAutoComTextViewDashBoardIndications!!.text.toString()
            details!!.seatBeltOperational = mAutoComTextViewSeatBeltOperational!!.text.toString()
            details!!.brakes = if(mAutoComTextViewBrakes!=null) mAutoComTextViewBrakes!!.text.toString() else ""
            details!!.brakeLights = if(mAutoComTextViewBrakeLights!=null) mAutoComTextViewBrakeLights!!.text.toString() else ""
            details!!.headlights = mAutoComTextViewHeadLights!!.text.toString()
            details!!.turnSignals = mAutoComTextViewTurnSignals!!.text.toString()
            details!!.turnSignals = mAutoComTextViewTurnSignals!!.text.toString()
            details!!.steeringWheelOperational = if(mAutoComTextViewSteeringWheelOperational!=null)mAutoComTextViewSteeringWheelOperational!!.text.toString() else ""
            details!!.windshieldVisibility = mAutoComTextViewWindshieldVisibility!!.text.toString()
            details!!.sideAndRearViewMirrorsOperational = mAutoComTextViewSideAndRearViewMirrorsOperational!!.text.toString()
            details!!.windshieldWipersOperational = mAutoComTextViewWindshieldWipersOperational!!.text.toString()
            details!!.vehicleRegistrationAndInsurance = if(mAutoComTextViewVehicleRegistrationAndInsurance!=null)mAutoComTextViewVehicleRegistrationAndInsurance!!.text.toString().equals("YES", ignoreCase = true) else false
            details!!.conesSixPerVehicle = if(mAutoComTextViewConesSixPerVehicle!=null)mAutoComTextViewConesSixPerVehicle!!.text.toString() else ""
            details!!.firstAidKit =if(mAutoComTextViewFirstAidKit!=null) mAutoComTextViewFirstAidKit!!.text.toString().equals("YES", ignoreCase = true) else false
            details!!.horn = mAutoComTextViewHorn!!.text.toString()
            details!!.interiorCleanliness = mAutoComTextViewInteriorCleanliness!!.text.toString()
            details!!.exteriorCleanliness = mAutoComTextViewExteriorCleanliness!!.text.toString()
            details!!.lprLensFreeOfDebris = mAutoComTextViewLprLensFreeOfDebris!!.text.toString()
            details!!.visibleLeaks = mAutoComTextViewVisibleLeaks!!.text.toString().equals("YES", ignoreCase = true)
            details!!.tiresVisualInspection = mAutoComTextViewTiresVisualInspection!!.text.toString()
            details!!.comments = mAutoComTextViewComments!!.text.toString()
            details!!.status = ""//mAutoComTextViewStaus!!.text.toString()
            try {
                details!!.frontPhoto = mImagesLinks!![0]
                details!!.passengerSidePhoto = mImagesLinks!![1]
                details!!.driverSidePhoto = mImagesLinks!![2]
                details!!.rearPhoto = mImagesLinks!![3]
                details!!.signature = mImagesLinks!![4]
                details!!.image_1 = mImagesLinks!![5]
                details!!.image_2 = mImagesLinks!![6]
            } catch (e: Exception) {
                e.printStackTrace()
            }
            vehicleInspectionRequest.detailsVehicleInspection = details
            vehicleInspectionViewModel?.hitVehicleInspectionReportAPI(vehicleInspectionRequest)
            try {
                if(LogUtil.WRITE_ALL_API_IN_LOGS_FILE) {
                    ApiLogsClass.writeApiPayloadTex(
                        BaseApplication.instance?.applicationContext!!,
                        "---------Vehicle Inspection Report--------"
                    )
                    ApiLogsClass.writeApiPayloadTex(
                        BaseApplication.instance?.applicationContext!!,
                        "REQUEST: " + ObjectMapperProvider.instance.writeValueAsString(
                            vehicleInspectionRequest
                        )
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }else if(fromForm.equals("72 Hour Marked Vehicles",ignoreCase = true)){
            val mLat = sharedPreference.read(SharedPrefKey.LAT, "0.0")?.toDouble().nullSafety()
            val mLong = sharedPreference.read(SharedPrefKey.LONG, "0.0")?.toDouble().nullSafety()
            val hourMarkedVehiclesRequest = HourMarkedVehiclesRequest()
            hourMarkedVehiclesRequest.form = fromForm

            val locationDetail = LocationDetailsHourMarkedVehicle()
            locationDetail!!.latitude = mLat
            locationDetail!!.longitude = mLong
            locationDetail!!.block = mAutoComTextViewBlock!!.text.toString()
            locationDetail!!.street = mAutoComTextViewStreet!!.text.toString()
            hourMarkedVehiclesRequest.locationDetailsHourMarkedVehicle = locationDetail


            val officerDetails = OfficerDetailsHourMarkedVehicle()
            officerDetails!!.officerName = (mWelcomeFormData?.officerFirstName + " " + mWelcomeFormData?.officerLastName)
            officerDetails!!.shiftId = sharedPreference.read(SharedPrefKey.LOGIN_SHIFT, "").toString()
            officerDetails!!.badgeId = mWelcomeFormData?.officerBadgeId
            officerDetails!!.beat = mWelcomeFormData?.officerBeat
            hourMarkedVehiclesRequest.officerDetailsHourMarkedVehicle = officerDetails

            val details = DetailsHourMarkedVehicle()
            details!!.deviceId = mAutoComTextViewDeviceId!!.text.toString()
            details!!.comments = mAutoComTextViewComments!!.text.toString()
            details!!.status = mAutoComTextViewStaus!!.text.toString()
            try {
                details!!.redStationCopyAndParkingWarning = mImagesLinks!![0]
                details!!.warningCitation = mImagesLinks!![1]
            } catch (e: Exception) {
                e.printStackTrace()
            }
            hourMarkedVehiclesRequest.detailsHourMarkedVehicle = details
            hourMarkedVehiclesViewModel?.hitHourMarkedVehiclesReportAPI(hourMarkedVehiclesRequest)
            try {
                if(LogUtil.WRITE_ALL_API_IN_LOGS_FILE) {
                    ApiLogsClass.writeApiPayloadTex(
                        BaseApplication.instance?.applicationContext!!,
                        "---------72 Hour Marked Vehicles Report--------"
                    )
                    ApiLogsClass.writeApiPayloadTex(
                        BaseApplication.instance?.applicationContext!!,
                        "REQUEST: " + ObjectMapperProvider.instance.writeValueAsString(
                            hourMarkedVehiclesRequest
                        )
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }else if(fromForm.equals("Bike Inspection",ignoreCase = true)){
            val mLat = sharedPreference.read(SharedPrefKey.LAT, "0.0")?.toDouble().nullSafety()
            val mLong = sharedPreference.read(SharedPrefKey.LONG, "0.0")?.toDouble().nullSafety()
            val bikeInspectionsRequest = BikeInspectionsRequest()
            bikeInspectionsRequest.form = fromForm

            val locationDetail = LocationDetailsBikeInspections()
            locationDetail!!.latitude = mLat
            locationDetail!!.longitude = mLong
            locationDetail!!.block = ""//mAutoComTextViewBlock!!.text.toString()
            locationDetail!!.street = ""//mAutoComTextViewStreet!!.text.toString()
            bikeInspectionsRequest.locationDetailsBikeInspections = locationDetail


            val officerDetails = OfficerDetailsBikeInspections()
            officerDetails!!.officerName = (mWelcomeFormData?.officerFirstName + " " + mWelcomeFormData?.officerLastName)
            officerDetails!!.shiftId = sharedPreference.read(SharedPrefKey.LOGIN_SHIFT, "").toString()
            officerDetails!!.badgeId = mWelcomeFormData?.officerBadgeId
            officerDetails!!.beat = mWelcomeFormData?.officerBeat
            bikeInspectionsRequest.officerDetailsBikeInspections = officerDetails

            val details = DetailsBikeInspections()
            details!!.deviceId = ""//mAutoComTextViewDeviceId!!.text.toString()
            details!!.officer = ""//mAutoComTextViewOfficer!!.text.toString()
            details!!.tirePressure = mAutoComTextViewTirePressure!!.text.toString()
            details!!.assignedBike = mAutoComTextViewAssignedBike!!.text.toString()
            details!!.breaksRotors = mAutoComTextViewBreaksRotors!!.text.toString()
            details!!.lightsAndReflectors = mAutoComTextViewLightsAndReflectors!!.text.toString()
            details!!.chainCrank = mAutoComTextViewChainCrank!!.text.toString()
            details!!.batteryFreeOfDebris = mAutoComTextViewBatteryFreeOfDebris!!.text.toString()
            details!!.flatPack = mAutoComTextViewFlatPack!!.text.toString()
            details!!.firstAidKit = mAutoComTextViewFirstAidKit!!.text.toString()
            details!!.helmetVisualInspection = mAutoComTextViewTiresVisualInspection!!.text.toString()
            details!!.bikeGlasses = mAutoComTextViewBikeGlassess!!.text.toString()
            details!!.batterCharge = mAutoComTextViewBatteryCharge!!.text.toString()
            details!!.gloveVisualInspection = mAutoComTextViewGloveVisualInspection!!.text.toString()
            details!!.status = ""//mAutoComTextViewStaus!!.text.toString()
            try {
                details!!.font = mImagesLinks!![0]
                details!!.back = mImagesLinks!![1]
                details!!.leftSide = mImagesLinks!![2]
                details!!.rightSide = mImagesLinks!![3]
            } catch (e: Exception) {
                e.printStackTrace()
            }
            bikeInspectionsRequest.detailsBikeInspections = details
            bikeInspectionViewModel?.hitBikeInspectionReportAPI(bikeInspectionsRequest)
            try {
                if(LogUtil.WRITE_ALL_API_IN_LOGS_FILE) {
                    ApiLogsClass.writeApiPayloadTex(
                        BaseApplication.instance?.applicationContext!!,
                        "---------Bike Inspection Report--------"
                    )
                    ApiLogsClass.writeApiPayloadTex(
                        BaseApplication.instance?.applicationContext!!,
                        "REQUEST: " + ObjectMapperProvider.instance.writeValueAsString(
                            bikeInspectionsRequest
                        )
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }else if(fromForm.equals("Sign Off Report",ignoreCase = true)){
            val mLat = sharedPreference.read(SharedPrefKey.LAT, "0.0")?.toDouble().nullSafety()
            val mLong = sharedPreference.read(SharedPrefKey.LONG, "0.0")?.toDouble().nullSafety()
            val signOffReportRequest = SignOffReportRequest()
            signOffReportRequest.form = fromForm

            val locationDetail = LocationDetailsSignOff()
            locationDetail!!.latitude = mLat
            locationDetail!!.longitude = mLong
            locationDetail!!.block = ""//mAutoComTextViewBlock!!.text.toString()
            locationDetail!!.street = ""//mAutoComTextViewStreet!!.text.toString()
            signOffReportRequest.locationDetails = locationDetail


            val officerDetails = OfficerDetailsSignOff()
            officerDetails!!.officerName = (mWelcomeFormData?.officerFirstName + " " + mWelcomeFormData?.officerLastName)
            officerDetails!!.shiftId = sharedPreference.read(SharedPrefKey.LOGIN_SHIFT, "").toString()
            officerDetails!!.badgeId = mWelcomeFormData?.officerBadgeId
            officerDetails!!.beat = mWelcomeFormData?.officerBeat
            signOffReportRequest.officerDetails = officerDetails

            val details = DetailsSignOff()
            details!!.reportNumber = mEditTextReportNumber!!.text.toString()
            details!!.vehicleStoredAt = mAutoComTextViewVehicleStoredAt!!.text.toString()
            details!!.vehicleRelocated = mAutoComTextViewVehicleRelocatedToDifferentStallWithinTheLot!!.text.toString().equals("YES")
            details!!.towFileNumber = mAutoComTextViewTowFileNumber!!.text.toString()
            try {
                details!!.officerPhoto = mImagesLinks!![0]
                details!!.towDriver = mImagesLinks!![1]
            } catch (e: Exception) {
                e.printStackTrace()
            }
            signOffReportRequest.details = details
            signOffReportViewModel?.hitSignOffReportSubmitApi(signOffReportRequest)
            try {
                if(LogUtil.WRITE_ALL_API_IN_LOGS_FILE) {
                    ApiLogsClass.writeApiPayloadTex(
                        BaseApplication.instance?.applicationContext!!,
                        "---------Sign Off Report--------"
                    )
                    ApiLogsClass.writeApiPayloadTex(
                        BaseApplication.instance?.applicationContext!!,
                        "REQUEST: " + ObjectMapperProvider.instance.writeValueAsString(
                            signOffReportRequest
                        )
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }else if(fromForm.equals("72hrs Notice To Tow Report",ignoreCase = true)){
            val mLat = sharedPreference.read(SharedPrefKey.LAT, "0.0")?.toDouble().nullSafety()
            val mLong = sharedPreference.read(SharedPrefKey.LONG, "0.0")?.toDouble().nullSafety()
            val noticeToTowRequest = NoticeToTowRequest()
            noticeToTowRequest.form = "72 Hours Tow Notice"

            val locationDetail = LocationDetailsNoticeToTow()
            locationDetail!!.latitude = mLat
            locationDetail!!.longitude = mLong
            locationDetail!!.block = ""//mAutoComTextViewBlock!!.text.toString()
            locationDetail!!.street = ""//mAutoComTextViewStreet!!.text.toString()
            noticeToTowRequest.locationDetails = locationDetail


            val officerDetails = OfficerDetailsNoticeToTow()
            officerDetails!!.officerName = (mWelcomeFormData?.officerFirstName + " " + mWelcomeFormData?.officerLastName)
            officerDetails!!.shiftId = sharedPreference.read(SharedPrefKey.LOGIN_SHIFT, "").toString()
            officerDetails!!.badgeId = mWelcomeFormData?.officerBadgeId
            officerDetails!!.beat = mWelcomeFormData?.officerBeat
            noticeToTowRequest.officerDetails = officerDetails

            val details = DetailsNoticeToTow()
            details!!.reportNumber = mEditTextReportNumber!!.text.toString()
            details!!.zone = mAutoComTextViewPBCZone!!.text.toString()
            details!!.meter = mAutoComTextViewMeterNo!!.text.toString()
            details!!.lot = ""//mAutoComTextViewLotArea!!.text.toString()
            details!!.vehicleLicensePlate = mAutoComTextViewLicensePlate!!.text.toString()
            details!!.vehicleVin = mAutoComTextViewVin!!.text.toString()
            details!!.state = mAutoComTextViewState!!.text.toString()
            details!!.vehicleMake = mAutoComTextViewMake!!.text.toString()
            details!!.vehicleModel = mAutoComTextViewModel!!.text.toString()
            details!!.vehicleColor = mAutoComTextViewColor!!.text.toString()
            details!!.firstMarkTimestamp = mAutoComTextViewTimeMark!!.text.toString()
            details!!.citationIssueTimestamp = mAutoComTextViewCitationsIssued!!.text.toString()
            details!!.scheduledTowDate = mAutoComTextViewDate!!.text.toString()
            details!!.line = mAutoComTextViewLine!!.text.toString()

            try {
                details!!.driverSideTireAirvalve = mImagesLinks!![0]
                details!!.photos = mImagesLinks
            } catch (e: Exception) {
                e.printStackTrace()
            }
            noticeToTowRequest.details = details
            noticeToTowViewModel?.hitNoticeToTowReportSubmitApi(noticeToTowRequest)
            try {
                if(LogUtil.WRITE_ALL_API_IN_LOGS_FILE) {
                    ApiLogsClass.writeApiPayloadTex(
                        BaseApplication.instance?.applicationContext!!,
                        "---------72hrs Notice To Tow Report--------"
                    )
                    ApiLogsClass.writeApiPayloadTex(
                        BaseApplication.instance?.applicationContext!!,
                        "REQUEST: " + ObjectMapperProvider.instance.writeValueAsString(
                            noticeToTowRequest
                        )
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }else if(fromForm.equals("Tow Report",ignoreCase = true)){
            val mLat = sharedPreference.read(SharedPrefKey.LAT, "0.0")?.toDouble().nullSafety()
            val mLong = sharedPreference.read(SharedPrefKey.LONG, "0.0")?.toDouble().nullSafety()
            val towReportRequest = TowReportRequest()
            towReportRequest.form = fromForm

            val locationDetail = LocationDetailsTowReport()
            locationDetail!!.latitude = mLat
            locationDetail!!.longitude = mLong
            locationDetail!!.block = ""//mAutoComTextViewBlock!!.text.toString()
            locationDetail!!.street = ""//mAutoComTextViewStreet!!.text.toString()
            towReportRequest.locationDetails = locationDetail


            val officerDetails = OfficerDetailsTowReport()
            officerDetails!!.officerName = (mWelcomeFormData?.officerFirstName + " " + mWelcomeFormData?.officerLastName)
            officerDetails!!.shiftId = sharedPreference.read(SharedPrefKey.LOGIN_SHIFT, "").toString()
            officerDetails!!.badgeId = mWelcomeFormData?.officerBadgeId
            officerDetails!!.beat = mWelcomeFormData?.officerBeat
            towReportRequest.officerDetails = officerDetails

            val details = DetailsTowReport()
            details!!.vehicleLicensePlate = mAutoComTextViewLicensePlate!!.text.toString()
            details!!.vehicleVin = mAutoComTextViewVin!!.text.toString()
            details!!.state = mAutoComTextViewState!!.text.toString()
            details!!.vehicleMake = mAutoComTextViewMake!!.text.toString()
            details!!.vehicleModel = mAutoComTextViewModel!!.text.toString()
            details!!.vehicleColor = mAutoComTextViewColor!!.text.toString()
            details!!.zone = mAutoComTextViewPBCZone!!.text.toString()
            details!!.meter = mAutoComTextViewMeterNo!!.text.toString()
            details!!.lot = ""//mAutoComTextViewLotArea!!.text.toString()
            details!!.towReason = mAutoComTextViewReasonForTow!!.text.toString()
            details!!.towNoticeDate = mAutoComTextViewDate!!.text.toString()
            details!!.visibleInteriorItems = mAutoComTextViewInteriorCleanliness!!.text.toString()
            details!!.trailerAttached = mAutoComTextViewOverHeating!!.text.toString().equals("YES")
            details!!.requestTo = mAutoComTextViewSpecialEnforcementRequest!!.text.toString()
            details!!.driverSideTireAirValve = mAutoComTextViewDriverSideComment!!.text.toString()
            details!!.garageClearance = mAutoComTextViewGarageClearance!!.text.toString()
            details!!.fileNumber = mAutoComTextViewFileNumber!!.text.toString()
            details!!.roAddress = mAutoComTextViewRo!!.text.toString()
            details!!.vehicleWithinTheLot = mAutoComTextViewVehicleWithInLot!!.text.toString()
            details!!.driverSideComments = mAutoComTextViewDriverSideComment!!.text.toString()
            details!!.passengerSideComments = mAutoComTextViewPassengerSideComment!!.text.toString()
            details!!.frontSideComments = mAutoComTextViewFrontSideComment!!.text.toString()
            details!!.rearSideComments = mAutoComTextViewRearSideComment!!.text.toString()
            details!!.trailerComments = mAutoComTextViewTrailerComment!!.text.toString()
            details!!.vehicleCondition = mAutoComTextViewVehicleCondition!!.text.toString()
            details!!.officerPhoneNumber = mAutoComTextViewOfficerPhoneNumber!!.text.toString()
            details!!.generalComments = mAutoComTextViewComments!!.text.toString()
            details!!.towingOfficer = mAutoComTextViewTowingOfficer!!.text.toString()
            details!!.line = mAutoComTextViewLine!!.text.toString()

            try {
                details!!.platePhoto = mImagesLinks!![0]
                details!!.curbPhoto = mImagesLinks!![1]
                details!!.driverSidePhoto = mImagesLinks!![2]
                details!!.frontPhoto = mImagesLinks!![3]
                details!!.passengerSidePhoto = mImagesLinks!![4]
                details!!.rearPhoto = mImagesLinks!![5]
                details!!.interiorPhotoOne = mImagesLinks!![6]
                details!!.interiorPhotoTwo = mImagesLinks!![7]
                details!!.trailerPhotoOne = mImagesLinks!![8]
            } catch (e: Exception) {
                e.printStackTrace()
            }
            towReportRequest.details = details
            towReportViewModel?.hitTowReportSubmitApi(towReportRequest)
            try {
                if(LogUtil.WRITE_ALL_API_IN_LOGS_FILE) {
                    ApiLogsClass.writeApiPayloadTex(
                        BaseApplication.instance?.applicationContext!!,
                        "---------Tow Report--------"
                    )
                    ApiLogsClass.writeApiPayloadTex(
                        BaseApplication.instance?.applicationContext!!,
                        "REQUEST: " + ObjectMapperProvider.instance.writeValueAsString(
                            towReportRequest
                        )
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }else if(fromForm.equals("NFL Special Assignment Report",ignoreCase = true)||
            fromForm.equals("SPECIAL EVENT REPORT",ignoreCase = true)){
            val mLat = sharedPreference.read(SharedPrefKey.LAT, "0.0")?.toDouble().nullSafety()
            val mLong = sharedPreference.read(SharedPrefKey.LONG, "0.0")?.toDouble().nullSafety()
            val nflRequest = NFLRequest()
            nflRequest.form = fromForm

            val locationDetail = LocationDetailsNfl()
            locationDetail!!.latitude = mLat
            locationDetail!!.longitude = mLong
//            locationDetail!!.block = mAutoComTextViewBlock!!.text.toString()
//            locationDetail!!.street = mAutoComTextViewStreet!!.text.toString()
            locationDetail!!.zone = mAutoComTextViewPBCZone!!.text.toString()
            nflRequest.locationDetails = locationDetail


            val officerDetails = OfficerDetailsNfl()
            officerDetails!!.officerName = (mWelcomeFormData?.officerFirstName + " " + mWelcomeFormData?.officerLastName)
            officerDetails!!.shiftId = sharedPreference.read(SharedPrefKey.LOGIN_SHIFT, "").toString()
            officerDetails!!.badgeId = mWelcomeFormData?.officerBadgeId
            officerDetails!!.beat = mWelcomeFormData?.officerBeat
            nflRequest.officerDetails = officerDetails

            val details = DetailsNfl()
            details!!.reportNumber = mEditTextReportNumber!!.text.toString()
            details!!.zone = mAutoComTextViewPBCZone!!.text.toString()
            details!!.meter = if(mAutoComTextViewMeterNo!=null)mAutoComTextViewMeterNo!!.text.toString() else " "
            details!!.carsCounted = (mAutoComTextViewCarCount!!.text.toString()).toInt()
            details!!.emptySpaces = (mAutoComTextViewEmptySpace!!.text.toString()).toInt()
            details!!.numberOfViolatingVehicles = (mAutoComTextViewNumberOfViolatingVehicles!!.text.toString()).toInt()
            details!!.violationRate = mAutoComTextViewViolationRate!!.text.toString()
            details!!.comments = mAutoComTextViewComments!!.text.toString()
            if(fromForm.equals("NFL Special Assignment Report",ignoreCase = true)) {
                details!!.line = mAutoComTextViewLine!!.text.toString()
            }
            if(fromForm.equals("SPECIAL EVENT REPORT",ignoreCase = true)) {
                details!!.eventName = mAutoComTextViewField1!!.text.toString()
            }

            try {
                details!!.photo1 = mImagesLinks!![0]
                details!!.photo2 = mImagesLinks!![1]
                details!!.photo3 = mImagesLinks!![2]
            } catch (e: Exception) {
                e.printStackTrace()
            }
            nflRequest.details = details
            if(fromForm.equals("NFL Special Assignment Report",ignoreCase = true)) {
                reportServicesViewModel?.hitNFLReportSubmitApi(nflRequest)
            }else{
//                nflRequest.form = "Intuit Dome Report"
                reportServicesViewModel?.hitHardSummerReportSubmitApi(nflRequest)
            }
            try {
                if(LogUtil.WRITE_ALL_API_IN_LOGS_FILE) {
                    ApiLogsClass.writeApiPayloadTex(
                        BaseApplication.instance?.applicationContext!!,
                        "---------" + fromForm + " Report--------"
                    )
                    ApiLogsClass.writeApiPayloadTex(
                        BaseApplication.instance?.applicationContext!!,
                        "REQUEST: " + ObjectMapperProvider.instance.writeValueAsString(nflRequest)
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }else if(fromForm.equals("PCH Daily Updates",ignoreCase = true)){
            val mLat = sharedPreference.read(SharedPrefKey.LAT, "0.0")?.toDouble().nullSafety()
            val mLong = sharedPreference.read(SharedPrefKey.LONG, "0.0")?.toDouble().nullSafety()
            val afterRequest = AfterSevenPMRequest()
            afterRequest.form = fromForm

            val locationDetail = LocationDetailsAfter()
            locationDetail!!.latitude = mLat
            locationDetail!!.longitude = mLong
//            locationDetail!!.block = mAutoComTextViewBlock!!.text.toString()
//            locationDetail!!.street = mAutoComTextViewStreet!!.text.toString()
            locationDetail!!.zone = mAutoComTextViewPBCZone!!.text.toString()
            afterRequest.locationDetails = locationDetail


            val officerDetails = OfficerDetailsAfter()
            officerDetails!!.officerName = (mWelcomeFormData?.officerFirstName + " " + mWelcomeFormData?.officerLastName)
            officerDetails!!.shiftId = sharedPreference.read(SharedPrefKey.LOGIN_SHIFT, "").toString()
            officerDetails!!.badgeId = mWelcomeFormData?.officerBadgeId
            officerDetails!!.beat = mWelcomeFormData?.officerBeat
            afterRequest.officerDetails = officerDetails

            val details = DetailsAfter()
            details!!.reportNumber = mEditTextReportNumber!!.text.toString()
            details!!.zone = mAutoComTextViewPBCZone!!.text.toString()
            details!!.vendorCount = (mAutoComTextViewCarCount!!.text.toString()).toInt()
            details!!.vendorLocations = (mAutoComTextViewLotArea!!.text.toString())
            details!!.securityObservation = (mAutoComTextViewSecurityObservation!!.text.toString())
            details!!.comments = mAutoComTextViewComments!!.text.toString()

            try {
                details!!.photo1 = mImagesLinks!![0]
                details!!.photo2 = mImagesLinks!![1]
                details!!.photo3 = mImagesLinks!![2]
                details!!.photo4 = mImagesLinks!![3]
                details!!.photo5 = mImagesLinks!![4]
                details!!.photo6 = mImagesLinks!![5]
                details!!.photo7 = mImagesLinks!![6]
                details!!.signature =mImagesLinks!![7]
            } catch (e: Exception) {
                e.printStackTrace()
            }
            afterRequest.details = details
            reportServicesViewModel?.hitAfterSevenPMReportSubmitApi(afterRequest)
            try {
                if(LogUtil.WRITE_ALL_API_IN_LOGS_FILE) {
                    ApiLogsClass.writeApiPayloadTex(
                        BaseApplication.instance?.applicationContext!!,
                        "---------PCH Daily Updates Report--------"
                    )
                    ApiLogsClass.writeApiPayloadTex(
                        BaseApplication.instance?.applicationContext!!,
                        "REQUEST: " + ObjectMapperProvider.instance.writeValueAsString(afterRequest)
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }else if(fromForm.equals("Pay Station Report",ignoreCase = true)){
            val mLat = sharedPreference.read(SharedPrefKey.LAT, "0.0")?.toDouble().nullSafety()
            val mLong = sharedPreference.read(SharedPrefKey.LONG, "0.0")?.toDouble().nullSafety()
            val afterRequest = PayStationRequest()
            afterRequest.form = fromForm

            val locationDetail = LocationDetailsPayStation()
            locationDetail!!.latitude = mLat
            locationDetail!!.longitude = mLong
//            locationDetail!!.block = mAutoComTextViewBlock!!.text.toString()
//            locationDetail!!.street = mAutoComTextViewStreet!!.text.toString()
//            locationDetail!!.direction = mAutoComTextViewDirection!!.text.toString()
            locationDetail!!.zone = mAutoComTextViewPBCZone!!.text.toString()
            afterRequest.locationDetails = locationDetail


            val officerDetails = OfficerDetailsPayStation()
            officerDetails!!.officerName = (mWelcomeFormData?.officerFirstName + " " + mWelcomeFormData?.officerLastName)
            officerDetails!!.shiftId = sharedPreference.read(SharedPrefKey.LOGIN_SHIFT, "").toString()
            officerDetails!!.badgeId = mWelcomeFormData?.officerBadgeId
            officerDetails!!.beat = mWelcomeFormData?.officerBeat
            afterRequest.officerDetails = officerDetails

            val details = DetailsPayStation()
            details!!.reportNumber = mEditTextReportNumber!!.text.toString()
            details!!.zone = mAutoComTextViewPBCZone!!.text.toString()
            details!!.meter = if(mAutoComTextViewMeterNo!=null)mAutoComTextViewMeterNo!!.text.toString() else " "
            details!!.machineNumber1 = (mAutoComTextViewField1!!.text.toString()).toInt()
            details!!.machineNumber2 = if(mAutoComTextViewField2!!.text!!.isNotEmpty())(mAutoComTextViewField2!!.text.toString()).toInt() else 0
            details!!.machineNumber3 = if(mAutoComTextViewField2!!.text!!.isNotEmpty())(mAutoComTextViewField3!!.text.toString()).toInt() else 0
            details!!.description1 = (mAutoComTextViewField4!!.text.toString())
            details!!.description2 = mAutoComTextViewField5!!.text.toString()
            details!!.description3 = mAutoComTextViewField6!!.text.toString()
            details!!.comments1 = mAutoComTextViewComments!!.text.toString()
            details!!.comments2 = mAutoComTextViewComment2!!.text.toString()
            details!!.comments3 = mAutoComTextViewComment3!!.text.toString()
            details!!.line = mAutoComTextViewLine!!.text.toString()

            try {
                details!!.photo1 = mImagesLinks!![0]
                details!!.photo2 = mImagesLinks!![1]
                details!!.photo3 = mImagesLinks!![2]
            } catch (e: Exception) {
                e.printStackTrace()
            }
            afterRequest.details = details
            reportServicesViewModel?.hitPayStationReportSubmitApi(afterRequest)
            try {
                if(LogUtil.WRITE_ALL_API_IN_LOGS_FILE) {
                    ApiLogsClass.writeApiPayloadTex(
                        BaseApplication.instance?.applicationContext!!,
                        "---------Pay Station Report--------"
                    )
                    ApiLogsClass.writeApiPayloadTex(
                        BaseApplication.instance?.applicationContext!!,
                        "REQUEST: " + ObjectMapperProvider.instance.writeValueAsString(afterRequest)
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }else if(fromForm.equals("Signage Report",ignoreCase = true)){
            val mLat = sharedPreference.read(SharedPrefKey.LAT, "0.0")?.toDouble().nullSafety()
            val mLong = sharedPreference.read(SharedPrefKey.LONG, "0.0")?.toDouble().nullSafety()
            val signageRequest = SignageReportRequest()
            signageRequest.form = fromForm

            val locationDetail = LocationDetailsSignage()
            locationDetail!!.latitude = mLat
            locationDetail!!.longitude = mLong
//            locationDetail!!.block = mAutoComTextViewBlock!!.text.toString()
//            locationDetail!!.street = mAutoComTextViewStreet!!.text.toString()
//            locationDetail!!.direction = mAutoComTextViewDirection!!.text.toString()
            locationDetail!!.zone = mAutoComTextViewPBCZone!!.text.toString()
            signageRequest.locationDetails = locationDetail


            val officerDetails = OfficerDetailsSignage()
            officerDetails!!.officerName = (mWelcomeFormData?.officerFirstName + " " + mWelcomeFormData?.officerLastName)
            officerDetails!!.shiftId = sharedPreference.read(SharedPrefKey.LOGIN_SHIFT, "").toString()
            officerDetails!!.badgeId = mWelcomeFormData?.officerBadgeId
            officerDetails!!.beat = mWelcomeFormData?.officerBeat
            signageRequest.officerDetails = officerDetails

            val details = DetailsSignage()
            details!!.reportNumber = mEditTextReportNumber!!.text.toString()
            details!!.zone = mAutoComTextViewPBCZone!!.text.toString()
            details!!.meter = if(mAutoComTextViewMeterNo!=null)mAutoComTextViewMeterNo!!.text.toString() else " "
            details!!.description1 = (mAutoComTextViewField1!!.text.toString())
            details!!.description2 = (mAutoComTextViewField2!!.text.toString())
            details!!.description3 = (mAutoComTextViewField3!!.text.toString())
            details!!.comments = mAutoComTextViewComments!!.text.toString()
            details!!.line = mAutoComTextViewLine!!.text.toString()

            try {
                details!!.photo1 = mImagesLinks!![0]
                details!!.photo2 = mImagesLinks!![1]
                details!!.photo3 = mImagesLinks!![2]
            } catch (e: Exception) {
                e.printStackTrace()
            }
            signageRequest.details = details
            reportServicesViewModel?.hitSignageReportSubmitApi(signageRequest)
            try {
                if(LogUtil.WRITE_ALL_API_IN_LOGS_FILE) {
                    ApiLogsClass.writeApiPayloadTex(
                        BaseApplication.instance?.applicationContext!!,
                        "---------Signage Report--------"
                    )
                    ApiLogsClass.writeApiPayloadTex(
                        BaseApplication.instance?.applicationContext!!,
                        "REQUEST: " + ObjectMapperProvider.instance.writeValueAsString(
                            signageRequest
                        )
                    )
                }
                } catch (e: Exception) {
                e.printStackTrace()
            }
        }else if(fromForm.equals("Homeless Report",ignoreCase = true)){
            val mLat = sharedPreference.read(SharedPrefKey.LAT, "0.0")?.toDouble().nullSafety()
            val mLong = sharedPreference.read(SharedPrefKey.LONG, "0.0")?.toDouble().nullSafety()
            val homelessRequest = HomelessRequest()
            homelessRequest.form = fromForm

            val locationDetail = LocationDetailsHomeless()
            locationDetail!!.latitude = mLat
            locationDetail!!.longitude = mLong
//            locationDetail!!.block = mAutoComTextViewBlock8!!.text.toString()
//            locationDetail!!.street = mAutoComTextViewStreet!!.text.toString()
//            locationDetail!!.direction = mAutoComTextViewDirection!!.text.toString()
            locationDetail!!.zone = mAutoComTextViewPBCZone!!.text.toString()
            locationDetail!!.meter = mAutoComTextViewMeterNo!!.text.toString()
            homelessRequest.locationDetails = locationDetail


            val officerDetails = OfficerDetailsHomeless()
            officerDetails!!.officerName = (mWelcomeFormData?.officerFirstName + " " + mWelcomeFormData?.officerLastName)
            officerDetails!!.shiftId = sharedPreference.read(SharedPrefKey.LOGIN_SHIFT, "").toString()
            officerDetails!!.badgeId = mWelcomeFormData?.officerBadgeId
            officerDetails!!.beat = mWelcomeFormData?.officerBeat
            homelessRequest.officerDetails = officerDetails

            val details = DetailsHomeless()
            details!!.reportNumber = mEditTextReportNumber!!.text.toString()
            details!!.zone = mAutoComTextViewPBCZone!!.text.toString()
            details!!.meter = if(mAutoComTextViewMeterNo!=null)mAutoComTextViewMeterNo!!.text.toString() else " "
            details!!.description = (mAutoComTextViewField1!!.text.toString())
            details!!.comments = mAutoComTextViewComments!!.text.toString()
            details!!.line = mAutoComTextViewLine!!.text.toString()

            try {
                details!!.photo1 = mImagesLinks!![0]
                details!!.photo2 = mImagesLinks!![1]
                details!!.photo3 = mImagesLinks!![2]
                details!!.photo4 = mImagesLinks!![3]
            } catch (e: Exception) {
                e.printStackTrace()
            }
            homelessRequest.details = details
            reportServicesViewModel?.hitHomelessReportSubmitApi(homelessRequest)
            try {
                if(LogUtil.WRITE_ALL_API_IN_LOGS_FILE) {
                    ApiLogsClass.writeApiPayloadTex(
                        BaseApplication.instance?.applicationContext!!,
                        "---------Homeless Report--------"
                    )
                    ApiLogsClass.writeApiPayloadTex(
                        BaseApplication.instance?.applicationContext!!,
                        "REQUEST: " + ObjectMapperProvider.instance.writeValueAsString(
                            homelessRequest
                        )
                    )
                }
                } catch (e: Exception) {
                e.printStackTrace()
            }
        }else if(fromForm.equals("Work Order Report",ignoreCase = true)){
            val mLat = sharedPreference.read(SharedPrefKey.LAT, "0.0")?.toDouble().nullSafety()
            val mLong = sharedPreference.read(SharedPrefKey.LONG, "0.0")?.toDouble().nullSafety()
            val workOrderRequest = WorkOrderRequest()
            workOrderRequest.form = fromForm

            val locationDetail = LocationDetailsWorkOrder()
            locationDetail!!.latitude = mLat
            locationDetail!!.longitude = mLong
            locationDetail!!.block = mAutoComTextViewBlock!!.text.toString()
            locationDetail!!.street = mAutoComTextViewStreet!!.text.toString()
            locationDetail!!.direction = mAutoComTextViewDirection!!.text.toString()
//            locationDetail!!.zone = mAutoComTextViewPBCZone!!.text.toString()
            workOrderRequest.locationDetails = locationDetail


            val officerDetails = OfficerDetailsWorkOrder()
            officerDetails!!.officerName = (mWelcomeFormData?.officerFirstName + " " + mWelcomeFormData?.officerLastName)
            officerDetails!!.shiftId = sharedPreference.read(SharedPrefKey.LOGIN_SHIFT, "").toString()
            officerDetails!!.badgeId = mWelcomeFormData?.officerBadgeId
            officerDetails!!.beat = mWelcomeFormData?.officerBeat
            workOrderRequest.officerDetails = officerDetails

            val details = DetailsWorkOrder()
            details!!.reportNumber = mEditTextReportNumber!!.text.toString()
            details!!.sign = mAutoComTextViewField1!!.text.toString()
            details!!.actionsRequired = mAutoComTextViewField2!!.text.toString()
            details!!.comments = mAutoComTextViewComments!!.text.toString()

            try {
                details!!.photo = mImagesLinks!![0]
            } catch (e: Exception) {
                e.printStackTrace()
            }
            workOrderRequest.details = details
            reportServicesViewModel?.hitWorkOrderReportSubmitApi(workOrderRequest)
            try {
                if(LogUtil.WRITE_ALL_API_IN_LOGS_FILE) {
                    ApiLogsClass.writeApiPayloadTex(
                        BaseApplication.instance?.applicationContext!!,
                        "---------Work Order Report--------"
                    )
                    ApiLogsClass.writeApiPayloadTex(
                        BaseApplication.instance?.applicationContext!!,
                        "REQUEST: " + ObjectMapperProvider.instance.writeValueAsString(
                            workOrderRequest
                        )
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }else if(fromForm.equals("Safety Report Immediate Attention Required",ignoreCase = true)){
            val mLat = sharedPreference.read(SharedPrefKey.LAT, "0.0")?.toDouble().nullSafety()
            val mLong = sharedPreference.read(SharedPrefKey.LONG, "0.0")?.toDouble().nullSafety()
            val safetyIssueRequest = SafetyIssueRequest()
            safetyIssueRequest.form = fromForm

            val locationDetail = LocationDetailsSafetyIssue()
            locationDetail!!.latitude = mLat
            locationDetail!!.longitude = mLong
//            locationDetail!!.block = mAutoComTextViewBlock!!.text.toString()
//            locationDetail!!.street = mAutoComTextViewStreet!!.text.toString()
//            locationDetail!!.direction = mAutoComTextViewDirection!!.text.toString()
            locationDetail!!.zone = mAutoComTextViewPBCZone!!.text.toString()
            safetyIssueRequest.locationDetails = locationDetail

            val officerDetails = OfficerDetailsSafetyIssue()
            officerDetails!!.officerName = (mWelcomeFormData?.officerFirstName + " " + mWelcomeFormData?.officerLastName)
            officerDetails!!.shiftId = sharedPreference.read(SharedPrefKey.LOGIN_SHIFT, "").toString()
            officerDetails!!.badgeId = mWelcomeFormData?.officerBadgeId
            officerDetails!!.beat = mWelcomeFormData?.officerBeat
            safetyIssueRequest.officerDetails = officerDetails

            val details = DetailsSafetyIssue()
            details!!.reportNumber = mEditTextReportNumber!!.text.toString()
            details!!.safetyIssue = mAutoComTextViewSafetyIssue!!.text.toString()
            details!!.description = mAutoComTextViewField2!!.text.toString()
            details!!.zone = mAutoComTextViewPBCZone!!.text.toString()
            details!!.meter = if(mAutoComTextViewMeterNo!=null)mAutoComTextViewMeterNo!!.text.toString() else " "
            details!!.line = mAutoComTextViewLine!!.text.toString()
//            details!!.comments = mAutoComTextViewComments!!.text.toString()

            try {
                details!!.photo1 = mImagesLinks!![0]
                details!!.photo2 = mImagesLinks!![1]
                details!!.photo3 = mImagesLinks!![2]
            } catch (e: Exception) {
                e.printStackTrace()
            }
            safetyIssueRequest.details = details
            reportServicesViewModel?.hitSafetyIssueReportSubmitApi(safetyIssueRequest)
            try {
                if(LogUtil.WRITE_ALL_API_IN_LOGS_FILE) {
                    ApiLogsClass.writeApiPayloadTex(
                        BaseApplication.instance?.applicationContext!!,
                        "---------Safety Report Immediate Attention Required Report--------"
                    )
                    ApiLogsClass.writeApiPayloadTex(
                        BaseApplication.instance?.applicationContext!!,
                        "REQUEST: " + ObjectMapperProvider.instance.writeValueAsString(
                            safetyIssueRequest
                        )
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }else if(fromForm.equals("Trash Lot Maintenance Report",ignoreCase = true)){
            val mLat = sharedPreference.read(SharedPrefKey.LAT, "0.0")?.toDouble().nullSafety()
            val mLong = sharedPreference.read(SharedPrefKey.LONG, "0.0")?.toDouble().nullSafety()
            val trashLotRequest = TrashLotRequest()
            trashLotRequest.form = fromForm

            val locationDetail = LocationDetailsTrashLot()
            locationDetail!!.latitude = mLat
            locationDetail!!.longitude = mLong
//            locationDetail!!.block = mAutoComTextViewBlock!!.text.toString()
//            locationDetail!!.street = mAutoComTextViewStreet!!.text.toString()
//            locationDetail!!.direction = mAutoComTextViewDirection!!.text.toString()
            locationDetail!!.zone = mAutoComTextViewPBCZone!!.text.toString()
            trashLotRequest.locationDetails = locationDetail


            val officerDetails = OfficerDetailsTrashLot()
            officerDetails!!.officerName = (mWelcomeFormData?.officerFirstName + " " + mWelcomeFormData?.officerLastName)
            officerDetails!!.shiftId = sharedPreference.read(SharedPrefKey.LOGIN_SHIFT, "").toString()
            officerDetails!!.badgeId = mWelcomeFormData?.officerBadgeId
            officerDetails!!.beat = mWelcomeFormData?.officerBeat
            trashLotRequest.officerDetails = officerDetails

            val details = DetailsTrashLot()
            details!!.reportNumber = mEditTextReportNumber!!.text.toString()
            details!!.requiredServices = mAutoComTextViewRequiredServices!!.text.toString()
            details!!.description = mAutoComTextViewField2!!.text.toString()
            details!!.zone = mAutoComTextViewPBCZone!!.text.toString()
            details!!.line = mAutoComTextViewLine!!.text.toString()
//            details!!.comments = mAutoComTextViewComments!!.text.toString()

            try {
                details!!.photo1 = mImagesLinks!![0]
                details!!.photo2 = mImagesLinks!![1]
                details!!.photo3 = mImagesLinks!![2]
            } catch (e: Exception) {
                e.printStackTrace()
            }
            trashLotRequest.details = details
            reportServicesViewModel?.hitTrashLotReportSubmitApi(trashLotRequest)
            try {
                if(LogUtil.WRITE_ALL_API_IN_LOGS_FILE) {
                    ApiLogsClass.writeApiPayloadTex(
                        BaseApplication.instance?.applicationContext!!,
                        "---------Trash Lot Maintenance Report--------"
                    )
                    ApiLogsClass.writeApiPayloadTex(
                        BaseApplication.instance?.applicationContext!!,
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

    private fun createImagesNameList(imageList: MutableList<CitationImagesModel?>?): Array<String?> {
        val imageNameList = arrayOfNulls<String>(imageList!!.size)

        imageList!!.forEachIndexed { index, scanDataModel ->
            if (imageList!!.get(index)!!.status == 1) {
                val timeStamp = SimpleDateFormat("yyyMMdd_HHmmss", Locale.US).format(Date())
                imageNameList.set(
                    index,
                    getCitaitonImageFormat("ReportIssue_" + timeStamp, index)
                )
                try {
                    if(LogUtil.WRITE_ALL_API_IN_LOGS_FILE) {
                        ApiLogsClass.writeApiPayloadTex(
                            BaseApplication.instance?.applicationContext!!,
                            "---------Report Request Image Upload--------"
                        )
                        ApiLogsClass.writeApiPayloadTex(
                            BaseApplication.instance?.applicationContext!!,
                            "Request " + " :- " + imageNameList!!.get(index)
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        return imageNameList
    }

    private fun createImageMultipart(imageList: MutableList<CitationImagesModel?>?): List<MultipartBody.Part?> {
        val imageMultipartList = ArrayList<MultipartBody.Part?>()

        imageList!!.forEach {
            if (it!!.status == 1) {
                val tempFile: File = File(it!!.citationImage.nullSafety())
                val requestFile =
                    RequestBody.create("multipart/form-data".toMediaTypeOrNull(), tempFile!!)
                val files = MultipartBody.Part.createFormData(
                    "files",
                    tempFile.name,
                    requestFile
                )
                imageMultipartList.add(files)

            }
        }

        return imageMultipartList
    }

    private val uploadScannedImagesAPIResponseObserver = Observer<Any> {
        when (it) {
            is Resource.Error<*> -> {
                dismissLoader()
                showToast(context = this@AllReportActivity, message = it.message.nullSafety())
            }

            is Resource.Success<*> -> {
                val response = it.data as ScannedImageUploadResponse

                when (response.status) {
                    true -> {
//                        mCitationImagesLinks = response.data.get(0).response!!.links
                        try {
                            if(LogUtil.WRITE_ALL_API_IN_LOGS_FILE) {
                                ApiLogsClass.writeApiPayloadTex(
                                    BaseApplication.instance?.applicationContext!!,
                                    "Image Upload Response" + " :- " + response.data.get(0).response!!.links
                                )
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        mImagesLinks.addAll(response.data.get(0).response!!.links)
                        sendRequestWithImage()

                    }
                    else -> {
                        showToast(
                                context = this@AllReportActivity,
                                message = getString(R.string.err_msg_something_went_wrong_image)
                        )
                        try {
                            if(LogUtil.WRITE_ALL_API_IN_LOGS_FILE) {
                                ApiLogsClass.writeApiPayloadTex(
                                    BaseApplication.instance?.applicationContext!!,
                                    "Image Upload ERROR Response" + " :- " + response.message.nullSafety()
                                )
                            }
                            } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }

            is Resource.Loading<*> -> {
                it.isLoadingShow.let {
                    if (it as Boolean) {
                        showProgressLoader(getString(R.string.scr_message_please_wait))
                    } else {
                        dismissLoader()
                    }
                }
            }

            is Resource.NoInternetError<*> -> {
                dismissLoader()
                showToast(context = this@AllReportActivity, message = it.message.nullSafety())
                try {
                    if(LogUtil.WRITE_ALL_API_IN_LOGS_FILE) {
                        ApiLogsClass.writeApiPayloadTex(
                            BaseApplication.instance?.applicationContext!!,
                            "Image Upload ERROR Response" + " :- " + it.message.nullSafety()
                        )
                    }
                    } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    /*Api response */
    private fun consumeResponse(apiResponse: ApiResponse?, tag: String) {
        when (apiResponse?.status) {
            Status.LOADING -> showProgressLoader(getString(R.string.scr_message_please_wait))
            Status.SUCCESS -> {
                dismissLoader()
                if (!apiResponse.data!!.isNull) {
                    LogUtil.printLog(tag, apiResponse.data.toString())
                    try {
                        if (tag.equals(DynamicAPIPath.POST_BROKEN_METER_REPORT, ignoreCase = true)||
                                tag.equals(DynamicAPIPath.POST_CURB_REPORT, ignoreCase = true)||
                                tag.equals(DynamicAPIPath.POST_FULLTIME_REPORT, ignoreCase = true)||
                                tag.equals(DynamicAPIPath.POST_PARTTIME_REPORT, ignoreCase = true)||
                                tag.equals(DynamicAPIPath.POST_HAND_HELD_MALFUNCTIONS_REPORT, ignoreCase = true)||
                                tag.equals(DynamicAPIPath.POST_SIGN_REPORT, ignoreCase = true)||
                                tag.equals(DynamicAPIPath.POST_VEHICLE_INSPECTIONS_REPORT, ignoreCase = true)||
                                tag.equals(DynamicAPIPath.POST_SUPERVISOR_REPORT, ignoreCase = true)||
                                tag.equals(DynamicAPIPath.POST_BIKE_INSPECTIONS_REPORT, ignoreCase = true)||
                                tag.equals(DynamicAPIPath.POST_SIGN_OFF_REPORT, ignoreCase = true)||
                                tag.equals(DynamicAPIPath.POST_NOTICE_TO_TOW_REPORT, ignoreCase = true)||
                                tag.equals(DynamicAPIPath.POST_TOW_REPORT, ignoreCase = true)||
                                tag.equals(DynamicAPIPath.POST_NFL_REPORT, ignoreCase = true)||
                                tag.equals(DynamicAPIPath.POST_HARD_SUMMER_REPORT, ignoreCase = true)||
                                tag.equals(DynamicAPIPath.POST_HOUR_MARKED_VEHICLE_REPORT, ignoreCase = true)) {


                            val responseModel = ObjectMapperProvider.fromJson(apiResponse.data.toString(), ResponseBoot::class.java)

                            try {
                                if(LogUtil.WRITE_ALL_API_IN_LOGS_FILE) {
                                    ApiLogsClass.writeApiPayloadTex(
                                        BaseApplication.instance?.applicationContext!!,
                                        "Response  " + tag
                                    )
                                    ApiLogsClass.writeApiPayloadTex(
                                        BaseApplication.instance?.applicationContext!!,
                                        "Response " + " :- " + ObjectMapperProvider.instance.writeValueAsString(
                                            responseModel
                                        )
                                    )
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                            if (responseModel != null && responseModel.isSuccess) {
                                try {
                                    AppUtils.showCustomAlertDialog(
                                            mContext, fromForm,
                                            "Submit "+fromForm+" successfully", getString(R.string.alt_lbl_OK),
                                            getString(R.string.scr_btn_cancel), this
                                    )
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            } else {
                                AppUtils.showCustomAlertDialog(
                                        mContext,
                                        fromForm,
                                        getString(R.string.err_msg_something_went_wrong),
                                        getString(R.string.alt_lbl_OK),
                                        getString(R.string.scr_btn_cancel),
                                        this
                                )
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        AppUtils.showCustomAlertDialog(
                                mContext,
                                fromForm,
                                getString(R.string.err_msg_something_went_wrong),
                                getString(R.string.alt_lbl_OK),
                                getString(R.string.scr_btn_cancel),
                                this
                        )
                        try {
                            if(LogUtil.WRITE_ALL_API_IN_LOGS_FILE) {
                                ApiLogsClass.writeApiPayloadTex(
                                    BaseApplication.instance?.applicationContext!!,
                                    "---------Response ERROR--------" + tag
                                )
                                ApiLogsClass.writeApiPayloadTex(
                                    BaseApplication.instance?.applicationContext!!,
                                    "Response " + " :- " + ObjectMapperProvider.instance.writeValueAsString(
                                        apiResponse.error?.message
                                    )
                                )
                            }
                            } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
            Status.ERROR -> {
                dismissLoader()
                AppUtils.showCustomAlertDialog(
                        mContext,
                        fromForm,
                        getString(R.string.err_msg_something_went_wrong),
                        getString(R.string.alt_lbl_OK),
                        getString(R.string.scr_btn_cancel),
                        this
                )
                try {
                    if(LogUtil.WRITE_ALL_API_IN_LOGS_FILE) {
                        ApiLogsClass.writeApiPayloadTex(
                            BaseApplication.instance?.applicationContext!!,
                            "---------Response ERROR--------" + tag
                        )
                        ApiLogsClass.writeApiPayloadTex(
                            BaseApplication.instance?.applicationContext!!,
                            "Response " + " :- " + ObjectMapperProvider.instance.writeValueAsString(
                                apiResponse.error?.message
                            )
                        )
                        ApiLogsClass.writeApiPayloadTex(
                            BaseApplication.instance?.applicationContext!!,
                            "Response " + " :- " + ObjectMapperProvider.instance.writeValueAsString(
                                apiResponse.data
                            )
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            else -> {}
        }
    }

    override fun onYesButtonClick() {
        finish()
//        val intent = Intent(mContext, WelcomeActivity::class.java)
//        startActivity(intent)
    }

    override fun onNoButtonClick() {}
    override fun onYesButtonClickParam(msg: String?) {
        finish()
    }

    override fun onDestroy() {
        mContext = null

        super.onDestroy()
    }


    fun isFormValid(reportType : String): Boolean {
        try {
            if(reportType.equals("Broken Meter",ignoreCase = true)) {
                if (TextUtils.isEmpty(mAutoComTextViewBlock?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewBlock!!,mAppComTextViewBlock)
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewStreet?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewStreet!!,mAppComTextViewStreet)
                    return false
                }
//                if (TextUtils.isEmpty(mAutoComTextViewDirection?.text.toString().trim())) {
//                    setErrorOnField(mAutoComTextViewDirection!!,mAppComTextViewDirection)
//                    return false
//                }
                if (TextUtils.isEmpty(mAutoComTextViewDeviceId?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewDeviceId!!,mAppComTextViewDeviceId)
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewMeterNo?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewMeterNo!!,mAppComTextViewMeterNo)
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewMeterType?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewMeterType!!,mAppComTextViewMeterType)
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewMeterLabelVisible?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewMeterLabelVisible!!,mAppComTextViewMeterLabelVisible)
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewDigitalDisplayVisible?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewDigitalDisplayVisible!!,mAppComTextViewDigitalDisplayVisible)
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewDigitalDisplayVisible?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewDigitalDisplayVisible!!,mAppComTextViewDigitalDisplayVisible)
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewCoinJam?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewCoinJam!!,mAppComTextViewCoinJam)
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewCreditCardJam?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewCreditCardJam!!,mAppComTextViewCreditCardJam)
                    return false
                }
               /* if (TextUtils.isEmpty(mAutoComTextViewComments?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewComments!!,mAppComTextViewComments)
                    return false
                }*/
               /* if (TextUtils.isEmpty(mAutoComTextViewStaus?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewStaus!!,mAppComTextViewStaus)
                    return false
                }*/
            }else if(reportType.equals("Curb",ignoreCase = true))
            {
                if (TextUtils.isEmpty(mAutoComTextViewBlock?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewBlock!!,mAppComTextViewBlock)
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewStreet?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewStreet!!,mAppComTextViewStreet)
                    return false
                }
//                if (TextUtils.isEmpty(mAutoComTextViewDirection?.text.toString().trim())) {
//                    setErrorOnField(mAutoComTextViewDirection!!,mAppComTextViewDirection)
//                    return false
//                }
               /* if (TextUtils.isEmpty(mAutoComTextViewDeviceId?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewDeviceId!!,mAppComTextViewDeviceId)
                    return false
                }*/
                if (TextUtils.isEmpty(mAutoComTextViewEnforceable?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewEnforceable!!,mAppComTextViewEnforceable)
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewCurbColor?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewCurbColor!!,mAppComTextViewCurbColor)
                    return false
                }
              /*  if (TextUtils.isEmpty(mAutoComTextViewComments?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewComments!!,mAppComTextViewComments)
                    return false
                }*/
               /* if (TextUtils.isEmpty(mAutoComTextViewStaus?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewStaus!!,mAppComTextViewStaus)
                    return false
                }*/

            }else if(reportType.equals("Full Time Report",ignoreCase = true))
            {
                /*if (TextUtils.isEmpty(mAutoComTextViewBlock?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewBlock!!,mAppComTextViewBlock)
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewStreet?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewStreet!!,mAppComTextViewStreet)
                    return false
                }*/
                if (TextUtils.isEmpty(mAutoComTextViewDeviceId?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewDeviceId!!,mAppComTextViewDeviceId)
                    return false
                }
//                if (TextUtils.isEmpty(mAutoComTextViewDate?.text.toString().trim())) {
//                    setErrorOnField(mAutoComTextViewDate!!,mAppComTextViewDate)
//                    return false
//                }
                if (TextUtils.isEmpty(mAutoComTextViewDutyHours?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewDutyHours!!,mAppComTextViewDutyHours)
                    return false
                }
//                if (TextUtils.isEmpty(mAutoComTextViewOfficer?.text.toString().trim())) {
//                    setErrorOnField(mAutoComTextViewOfficer!!,mAppComTextViewOfficer)
//                    return false
//                }
                if (TextUtils.isEmpty(mAutoComTextViewLunchTaken?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewLunchTaken!!,mAppComTextViewLunchTaken)
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewFirst10MinBreak?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewFirst10MinBreak!!,mAppComTextViewFirst10MinBreak)
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewSecond10MinBreak?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewSecond10MinBreak!!,mAppComTextViewSecond10MinBreak)
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewUnitNo?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewUnitNo!!,mAppComTextViewUnitNo)
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewAssignedArea?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewAssignedArea!!,mAppComTextViewAssignedArea)
                    return false
                }
//                if (TextUtils.isEmpty(mAutoComTextViewOssiDeviceNo?.text.toString().trim())) {
//                    setErrorOnField(mAutoComTextViewOssiDeviceNo!!,mAppComTextViewOssiDeviceNo)
//                    return false
//                }
//                if (TextUtils.isEmpty(mAutoComTextViewHandheldUnitNo?.text.toString().trim())) {
//                    setErrorOnField(mAutoComTextViewHandheldUnitNo!!,mAppComTextViewHandheldUnitNo)
//                    return false
//                }
                if (TextUtils.isEmpty(mAutoComTextViewCitationsIssued?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewCitationsIssued!!,mAppComTextViewCitationsIssued)
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewSpecialEnforcementRequest?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewSpecialEnforcementRequest!!,mAppComTextViewSpecialEnforcementRequest)
                    return false
                }
              /*  if (TextUtils.isEmpty(mAutoComTextViewStaus?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewStaus!!,mAppComTextViewStaus)
                    return false
                }*/
            }else if(reportType.equals("Part Time Report",ignoreCase = true))
            {
                /*if (TextUtils.isEmpty(mAutoComTextViewBlock?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewBlock!!,mAppComTextViewBlock)
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewStreet?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewStreet!!,mAppComTextViewStreet)
                    return false
                }*/
                if (TextUtils.isEmpty(mAutoComTextViewDeviceId?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewDeviceId!!,mAppComTextViewDeviceId)
                    return false
                }
//                if (TextUtils.isEmpty(mAutoComTextViewDate?.text.toString().trim())) {
//                    setErrorOnField(mAutoComTextViewDate!!,mAppComTextViewDate)
//                    return false
//                }
                if (TextUtils.isEmpty(mAutoComTextViewDutyHours?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewDutyHours!!,mAppComTextViewDutyHours)
                    return false
                }
//                if (TextUtils.isEmpty(mAutoComTextViewOfficer?.text.toString().trim())) {
//                    setErrorOnField(mAutoComTextViewOfficer!!,mAppComTextViewOfficer)
//                    return false
//                }
//                if (TextUtils.isEmpty(mAutoComTextViewLunchTaken?.text.toString().trim())) {
//                    setErrorOnField(mAutoComTextViewLunchTaken!!,mAppComTextViewLunchTaken)
//                    return false
//                }
                if (TextUtils.isEmpty(mAutoComTextViewFirst10MinBreak?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewFirst10MinBreak!!,mAppComTextViewFirst10MinBreak)
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewSecond10MinBreak?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewSecond10MinBreak!!,mAppComTextViewSecond10MinBreak)
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewUnitNo?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewUnitNo!!,mAppComTextViewUnitNo)
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewAssignedArea?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewAssignedArea!!,mAppComTextViewAssignedArea)
                    return false
                }
//                if (TextUtils.isEmpty(mAutoComTextViewOssiDeviceNo?.text.toString().trim())) {
//                    setErrorOnField(mAutoComTextViewOssiDeviceNo!!,mAppComTextViewOssiDeviceNo)
//                    return false
//                }
//                if (TextUtils.isEmpty(mAutoComTextViewHandheldUnitNo?.text.toString().trim())) {
//                    setErrorOnField(mAutoComTextViewHandheldUnitNo!!,mAppComTextViewHandheldUnitNo)
//                    return false
//                }
                if (TextUtils.isEmpty(mAutoComTextViewCitationsIssued?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewCitationsIssued!!,mAppComTextViewCitationsIssued)
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewSpecialEnforcementRequest?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewSpecialEnforcementRequest!!,mAppComTextViewSpecialEnforcementRequest)
                    return false
                }
              /*  if (TextUtils.isEmpty(mAutoComTextViewStaus?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewStaus!!,mAppComTextViewStaus)
                    return false
                }*/
            }
            else if(reportType.equals("Hand Held Malfunction",ignoreCase = true))
            {
                /*if (TextUtils.isEmpty(mAutoComTextViewBlock?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewBlock!!,mAppComTextViewBlock)
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewStreet?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewStreet!!,mAppComTextViewStreet)
                    return false
                }*/
                if (TextUtils.isEmpty(mAutoComTextViewDeviceId?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewDeviceId!!,mAppComTextViewDeviceId)
                    return false
                }
               /* if (TextUtils.isEmpty(mAutoComTextViewHandheldUnitNo?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewHandheldUnitNo!!,mAppComTextViewHandheldUnitNo)
                    return false
                }*/
                if (TextUtils.isEmpty(mAutoComTextViewTriedToRestartHandHeld?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewTriedToRestartHandHeld!!,mAppComTextViewTriedToRestartHandHeld)
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewPrintingCorrectly?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewPrintingCorrectly!!,mAppComTextViewPrintingCorrectly)
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewOverHeating?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewOverHeating!!,mAppComTextViewOverHeating)
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewOverHeating?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewOverHeating!!,mAppComTextViewOverHeating)
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewBatteryHoldCharge?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewBatteryHoldCharge!!,mAppComTextViewBatteryHoldCharge)
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewInternetConnectivity?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewInternetConnectivity!!,mAppComTextViewInternetConnectivity)
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewDescribeHandHeldMalfunctionInDetail?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewDescribeHandHeldMalfunctionInDetail!!,mAppComTextViewDescribeHandHeldMalfunctionInDetail)
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewStaus?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewStaus!!,mAppComTextViewStaus)
                    return false
                }
            }else if(reportType.equals("Sign Report",ignoreCase = true))
            {
                if (TextUtils.isEmpty(mAutoComTextViewBlock?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewBlock!!,mAppComTextViewBlock)
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewStreet?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewStreet!!,mAppComTextViewStreet)
                    return false
                }
              /*  if (TextUtils.isEmpty(mAutoComTextViewDeviceId?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewDeviceId!!,mAppComTextViewDeviceId)
                    return false
                }*/
                if (TextUtils.isEmpty(mAutoComTextViewEnforceable?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewEnforceable!!,mAppComTextViewEnforceable)
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewGraffiti?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewGraffiti!!,mAppComTextViewGraffiti)
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewMissingSign?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewMissingSign!!,mAppComTextViewMissingSign)
                    return false
                }
             /*   if (TextUtils.isEmpty(mAutoComTextViewComments?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewComments!!,mAppComTextViewComments)
                    return false
                }*/
//                if (TextUtils.isEmpty(mAutoComTextViewStaus?.text.toString().trim())) {
//                    setErrorOnField(mAutoComTextViewStaus!!,mAppComTextViewStaus)
//                    return false
//                }
            }else if(reportType.equals("Vehicle Inspection",ignoreCase = true))
            {
               /* if (TextUtils.isEmpty(mAutoComTextViewBlock?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewBlock!!,mAppComTextViewBlock)
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewStreet?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewStreet!!,mAppComTextViewStreet)
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewDeviceId?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewDeviceId!!,mAppComTextViewDeviceId)
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewOfficer?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewOfficer!!,mAppComTextViewOfficer)
                    return false
                }*/
                if (TextUtils.isEmpty(mAutoComTextViewVehicle?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewVehicle!!,mAppComTextViewVehicle)
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewStartingMileage?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewStartingMileage!!,mAppComTextViewStartingMileage)
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewGasLevel?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewGasLevel!!,mAppComTextViewGasLevel)
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewLightBar?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewLightBar!!,mAppComTextViewGasLevel)
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewDashBoardIndications?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewDashBoardIndications!!,mAppComTextViewDashBoardIndications)
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewSeatBeltOperational?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewSeatBeltOperational!!,mAppComTextViewSeatBeltOperational)
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewBrakes?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewBrakes!!,mAppComTextViewBrakes)
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewBrakeLights?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewBrakeLights!!,mAppComTextViewBrakeLights)
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewHeadLights?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewHeadLights!!,mAppComTextViewHeadLights)
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewTurnSignals?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewTurnSignals!!,mAppComTextViewTurnSignals)
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewSteeringWheelOperational?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewSteeringWheelOperational!!,mAppComTextViewSteeringWheelOperational)
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewWindshieldVisibility?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewWindshieldVisibility!!,mAppComTextViewWindshieldVisibility)
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewSideAndRearViewMirrorsOperational?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewSideAndRearViewMirrorsOperational!!,mAppComTextViewSideAndRearViewMirrorsOperational)
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewWindshieldWipersOperational?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewWindshieldWipersOperational!!,mAppComTextViewWindshieldWipersOperational)
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewVehicleRegistrationAndInsurance?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewVehicleRegistrationAndInsurance!!,mAppComTextViewVehicleRegistrationAndInsurance)
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewConesSixPerVehicle?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewConesSixPerVehicle!!,mAppComTextViewConesSixPerVehicle)
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewFirstAidKit?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewFirstAidKit!!,mAppComTextViewFirstAidKit)
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewHorn?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewHorn!!,mAppComTextViewHorn)
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewInteriorCleanliness?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewInteriorCleanliness!!,mAppComTextViewInteriorCleanliness)
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewExteriorCleanliness?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewExteriorCleanliness!!,mAppComTextViewExteriorCleanliness)
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewLprLensFreeOfDebris?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewLprLensFreeOfDebris!!,mAppComTextViewLprLensFreeOfDebris)
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewVisibleLeaks?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewVisibleLeaks!!,mAppComTextViewVisibleLeaks)
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewTiresVisualInspection?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewTiresVisualInspection!!,mAppComTextViewTiresVisualInspection)
                    return false
                }
               /* if (TextUtils.isEmpty(mAutoComTextViewComments?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewComments!!,mAppComTextViewComments)
                    return false
                }*/
              /*  if (TextUtils.isEmpty(mAutoComTextViewStaus?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewStaus!!,mAppComTextViewStaus)
                    return false
                }*/
            }else if(reportType.equals("72 Hour Marked Vehicles",ignoreCase = true))
            {
                if (TextUtils.isEmpty(mAutoComTextViewBlock?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewBlock!!,mAppComTextViewBlock)
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewStreet?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewStreet!!,mAppComTextViewStreet)
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewDeviceId?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewDeviceId!!,mAppComTextViewDeviceId)
                    return false
                }
               /* if (TextUtils.isEmpty(mAutoComTextViewComments?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewComments!!,mAppComTextViewComments)
                    return false
                }*/
                if (TextUtils.isEmpty(mAutoComTextViewStaus?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewStaus!!,mAppComTextViewStaus)
                    return false
                }
            }else if(reportType.equals("Bike Inspection",ignoreCase = true))
            {
                /*if (TextUtils.isEmpty(mAutoComTextViewBlock?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewBlock!!,mAppComTextViewBlock)
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewStreet?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewStreet!!,mAppComTextViewStreet)
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewDeviceId?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewDeviceId!!,mAppComTextViewDeviceId)
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewOfficer?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewOfficer!!,mAppComTextViewOfficer)
                    return false
                }*/
                if (TextUtils.isEmpty(mAutoComTextViewTirePressure?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewTirePressure!!,mAppComTextViewTiresVisualInspection)
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewAssignedBike?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewAssignedBike!!,mAppComTextViewAssignedBike)
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewBreaksRotors?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewBreaksRotors!!,mAppComTextViewBreaksRotors)
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewLightsAndReflectors?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewLightsAndReflectors!!,mAppComTextViewLightsAndReflectors)
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewChainCrank?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewChainCrank!!,mAppComTextViewChainCrank)
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewBatteryFreeOfDebris?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewBatteryFreeOfDebris!!,mAppComTextViewBatteryFreeOfDebris)
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewFlatPack?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewFlatPack!!,mAppComTextViewFlatPack)
                    return false
                }

                if (TextUtils.isEmpty(mAutoComTextViewFirstAidKit?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewFirstAidKit!!,mAppComTextViewFirstAidKit)
                    return false
                }

                if (TextUtils.isEmpty(mAutoComTextViewTiresVisualInspection?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewTiresVisualInspection!!,mAppComTextViewTiresVisualInspection)
                    return false
                }

                if (TextUtils.isEmpty(mAutoComTextViewBikeGlassess?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewBikeGlassess!!,mAppComTextViewBikeGlassess)
                    return false
                }

                if (TextUtils.isEmpty(mAutoComTextViewBatteryCharge?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewBatteryCharge!!,mAppComTextViewBatteryCharge)
                    return false
                }

                if (TextUtils.isEmpty(mAutoComTextViewGloveVisualInspection?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewGloveVisualInspection!!,mAppComTextViewGloveVisualInspection)
                    return false
                }
             /*   if (TextUtils.isEmpty(mAutoComTextViewStaus?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewStaus!!,mAppComTextViewStaus)
                    return false
                }*/
            }else if(reportType.equals("EOW Supervisor Shift Report",ignoreCase = true))
            {
                if (TextUtils.isEmpty(mAutoComTextViewTotalEnforcementPersonnel?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewTotalEnforcementPersonnel!!,mAppComTextViewTotalEnforcementPersonnel)
                    return false
                }

                if (TextUtils.isEmpty(mAutoComTextViewFirst10MinBreak?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewFirst10MinBreak!!,mAppComTextViewFirst10MinBreak)
                    return false
                }

                if (TextUtils.isEmpty(mAutoComTextViewLunchTaken?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewLunchTaken!!,mAppComTextViewLunchTaken)
                    return false
                }

                if (TextUtils.isEmpty(mAutoComTextViewSecond10MinBreak?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewSecond10MinBreak!!,mAppComTextViewSecond10MinBreak)
                    return false
                }

                if (TextUtils.isEmpty(mAutoComTextViewComplaintsTowardsOfficer?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewComplaintsTowardsOfficer!!,mAppComTextViewComplaintsTowardsOfficer)
                    return false
                }

                if (TextUtils.isEmpty(mAutoComTextViewResidentComplaints?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewResidentComplaints!!,mAppComTextViewResidentComplaints)
                    return false
                }

                if (TextUtils.isEmpty(mAutoComTextViewWarningsIssued?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewWarningsIssued!!,mAppComTextViewWarningsIssued)
                    return false
                }

                if (TextUtils.isEmpty(mAutoComTextViewCitationsIssued?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewCitationsIssued!!,mAppComTextViewCitationsIssued)
                    return false
                }

               /* if (TextUtils.isEmpty(mAutoComTextViewComments?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewComments!!,mAppComTextViewComments)
                    return false
                }*/
            }else if(reportType.equals("Special Assignment Report",ignoreCase = true))
            {
                if (TextUtils.isEmpty(mAutoComTextViewBlock?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewBlock!!,mAppComTextViewBlock)
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewStreet?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewStreet!!,mAppComTextViewStreet)
                    return false
                }

//                if (TextUtils.isEmpty(mAutoComTextViewDirection?.text.toString().trim())) {
//                    setErrorOnField(mAutoComTextViewDirection!!,mAppComTextViewDirection)
//                    return false
//                }

                if (TextUtils.isEmpty(mAutoComTextViewTotalEnforcementPersonnel?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewTotalEnforcementPersonnel!!,mAppComTextViewTotalEnforcementPersonnel)
                    return false
                }

                if (TextUtils.isEmpty(mAutoComTextViewFirst10MinBreak?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewFirst10MinBreak!!,mAppComTextViewFirst10MinBreak)
                    return false
                }

//                if (TextUtils.isEmpty(mAutoComTextViewLunchTaken?.text.toString().trim())) {
//                    setErrorOnField(mAutoComTextViewLunchTaken!!,mAppComTextViewLunchTaken)
//                    return false
//                }

                if (TextUtils.isEmpty(mAutoComTextViewSecond10MinBreak?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewSecond10MinBreak!!,mAppComTextViewSecond10MinBreak)
                    return false
                }

                if (TextUtils.isEmpty(mAutoComTextViewComplaintsTowardsOfficer?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewComplaintsTowardsOfficer!!,mAppComTextViewComplaintsTowardsOfficer)
                    return false
                }

                if (TextUtils.isEmpty(mAutoComTextViewResidentComplaints?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewResidentComplaints!!,mAppComTextViewResidentComplaints)
                    return false
                }

                if (TextUtils.isEmpty(mAutoComTextViewWarningsIssued?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewWarningsIssued!!,mAppComTextViewWarningsIssued)
                    return false
                }

                if (TextUtils.isEmpty(mAutoComTextViewCitationsIssued?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewCitationsIssued!!,mAppComTextViewCitationsIssued)
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewVehicleMark?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewVehicleMark!!,mAppComTextViewVehicleMark)
                    return false
                }
//                if (TextUtils.isEmpty(mAutoComTextViewTimeMark?.text.toString().trim())) {
//                    setErrorOnField(mAutoComTextViewTimeMark!!,mAppComTextViewTimeMark)
//                    return false
//                }
//                if (TextUtils.isEmpty(mAutoComTextViewTimeMark2?.text.toString().trim())) {
//                    setErrorOnField(mAutoComTextViewTimeMark2!!,mAppComTextViewTimeMark2)
//                    return false
//                }
//                if (TextUtils.isEmpty(mAutoComTextViewTimeMark3?.text.toString().trim())) {
//                    setErrorOnField(mAutoComTextViewTimeMark3!!,mAppComTextViewTimeMark3)
//                    return false
//                }
//                if (TextUtils.isEmpty(mAutoComTextViewTimeMark4?.text.toString().trim())) {
//                    setErrorOnField(mAutoComTextViewTimeMark4!!,mAppComTextViewTimeMark4)
//                    return false
//                }
                if (TextUtils.isEmpty(mAutoComTextViewAssignedArea?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewAssignedArea!!,mAppComTextViewAssignedArea)
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewViolationDescription?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewViolationDescription!!,mAppComTextViewViolationDescription)
                    return false
                }

               /* if (TextUtils.isEmpty(mAutoComTextViewComments?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewComments!!,mAppComTextViewComments)
                    return false
                }*/
            }else if(reportType.equals("72hrs Notice To Tow Report",ignoreCase = true))
            {
                if (TextUtils.isEmpty(mAutoComTextViewPBCZone?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewPBCZone!!,mAppComTextViewPBCZone)
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewMeterNo?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewMeterNo!!,mAppComTextViewMeterNo)
                    return false
                }
//                if (TextUtils.isEmpty(mAutoComTextViewLotArea?.text.toString().trim())) {
//                    setErrorOnField(mAutoComTextViewLotArea!!,mAppComTextViewLotArea)
//                    return false
//                }

                if (TextUtils.isEmpty(mAutoComTextViewLicensePlate?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewLicensePlate!!,mAppComTextViewLicensePlate)
                    return false
                }

                if (TextUtils.isEmpty(mAutoComTextViewVin?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewVin!!,mAppComTextViewVin)
                    return false
                }

                if (TextUtils.isEmpty(mAutoComTextViewState?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewState!!,mAppComTextViewState)
                    return false
                }

//                if (TextUtils.isEmpty(mAutoComTextViewLunchTaken?.text.toString().trim())) {
//                    setErrorOnField(mAutoComTextViewLunchTaken!!,mAppComTextViewLunchTaken)
//                    return false
//                }

                if (TextUtils.isEmpty(mAutoComTextViewMake?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewMake!!,mAppComTextViewMake)
                    return false
                }

                if (TextUtils.isEmpty(mAutoComTextViewModel?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewModel!!,mAppComTextViewModel)
                    return false
                }

                if (TextUtils.isEmpty(mAutoComTextViewColor?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewColor!!,mAppComTextViewColor)
                    return false
                }

                if (TextUtils.isEmpty(mAutoComTextViewTimeMark?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewTimeMark!!,mAppComTextViewTimeMark)
                    return false
                }
                if(mAutoComTextViewTimeMark!!.text!!.isNotEmpty()&&mAutoComTextViewTimeMark!!.text.contains("/")) {
                    val stringArrya = mAutoComTextViewTimeMark!!.text.split("/")
                    if (stringArrya.size > 0) {
                        if (stringArrya[0].toInt()>31) {
                            mAutoComTextViewTimeMark?.error = getString(R.string.val_msg_please_date_invalidate)
                            return false
                        }

                        if (stringArrya[1].toInt()>12) {
                            mAutoComTextViewTimeMark?.error = getString(R.string.val_msg_please_date_invalidate)
                            return false
                        }
                    }
                }

                if (TextUtils.isEmpty(mAutoComTextViewCitationsIssued?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewCitationsIssued!!,mAppComTextViewCitationsIssued)
                    return false
                }
                if(mAutoComTextViewCitationsIssued!!.text!!.isNotEmpty()&&mAutoComTextViewCitationsIssued!!.text.contains("/")) {
                    val stringArrya = mAutoComTextViewCitationsIssued!!.text.split("/")
                    if (stringArrya.size > 0) {
                        if (stringArrya[0].toInt()>31) {
                            mAutoComTextViewCitationsIssued?.error = getString(R.string.val_msg_please_date_invalidate)
                            return false
                        }

                        if (stringArrya[1].toInt()>12) {
                            mAutoComTextViewCitationsIssued?.error = getString(R.string.val_msg_please_date_invalidate)
                            return false
                        }
                    }
                }
                if (TextUtils.isEmpty(mAutoComTextViewDate?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewDate!!,mAppComTextViewDate)
                    return false
                }

                if(mAutoComTextViewDate!!.text!!.isNotEmpty()&&mAutoComTextViewDate!!.text.contains("/")) {
                    val stringArrya = mAutoComTextViewDate!!.text.split("/")
                    if (stringArrya.size > 0) {
                        if (stringArrya[0].toInt()>31) {
                            mAutoComTextViewDate?.error = getString(R.string.val_msg_please_date_invalidate)
                            return false
                        }

                        if (stringArrya[1].toInt()>12) {
                            mAutoComTextViewDate?.error = getString(R.string.val_msg_please_date_invalidate)
                            return false
                        }
                    }
                }
            }else if(reportType.equals("Sign Off Report",ignoreCase = true))
            {
                if (TextUtils.isEmpty(mAutoComTextViewVehicleStoredAt?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewVehicleStoredAt!!,mAppComTextViewVehicleStoredAt)
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewTowFileNumber?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewTowFileNumber!!,mAppComTextViewTowFileNumber)
                    return false
                }

                if (TextUtils.isEmpty(mAutoComTextViewVehicleRelocatedToDifferentStallWithinTheLot?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewVehicleRelocatedToDifferentStallWithinTheLot!!,mAppComTextViewVehicleRelocatedToDifferentStallWithinTheLot)
                    return false
                }

            }else if(reportType.equals("Tow Report",ignoreCase = true))
            {

                if (TextUtils.isEmpty(mAutoComTextViewPBCZone?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewPBCZone!!,mAppComTextViewPBCZone)
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewMeterNo?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewMeterNo!!,mAppComTextViewMeterNo)
                    return false
                }
//                if (TextUtils.isEmpty(mAutoComTextViewLotArea?.text.toString().trim())) {
//                    setErrorOnField(mAutoComTextViewLotArea!!,mAppComTextViewLotArea)
//                    return false
//                }
                if (TextUtils.isEmpty(mAutoComTextViewLicensePlate?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewLicensePlate!!,mAppComTextViewLicensePlate)
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewState?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewState!!,mAppComTextViewState)
                    return false
                }

                if (TextUtils.isEmpty(mAutoComTextViewVin?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewVin!!,mAppComTextViewVin)
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewMake?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewMake!!,mAppComTextViewMake)
                    return false
                }

                if (TextUtils.isEmpty(mAutoComTextViewModel?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewModel!!,mAppComTextViewModel)
                    return false
                }

                if (TextUtils.isEmpty(mAutoComTextViewColor?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewColor!!,mAppComTextViewColor)
                    return false
                }

                if (TextUtils.isEmpty(mAutoComTextViewReasonForTow?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewReasonForTow!!,mAppComTextViewTowFileNumber)
                    return false
                }

                if (TextUtils.isEmpty(mAutoComTextViewDate?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewDate!!,mAppComTextViewDate)
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewInteriorCleanliness?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewInteriorCleanliness!!,mAppComTextViewInteriorCleanliness)
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewOverHeating?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewOverHeating!!,mAppComTextViewOverHeating)
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewSpecialEnforcementRequest?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewSpecialEnforcementRequest!!,mAppComTextViewSpecialEnforcementRequest)
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewGarageClearance?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewGarageClearance!!,mAppComTextViewGarageClearance)
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewFileNumber?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewFileNumber!!,mAppComTextViewFileNumber)
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewVehicleCondition?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewVehicleCondition!!,mAppComTextViewVehicleCondition)
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewOfficerPhoneNumber?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewOfficerPhoneNumber!!,mAppComTextViewOfficerPhoneNumber)
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewRo?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewRo!!,mAppComTextViewRo)
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewVehicleWithInLot?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewVehicleWithInLot!!,mAppComTextViewVehicleWithInLot)
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewTowingOfficer?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewTowingOfficer!!,mAppComTextViewTowingOfficer)
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewDriverSideComment?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewDriverSideComment!!,mAppComTextViewDriverSideComment)
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewPassengerSideComment?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewPassengerSideComment!!,mAppComTextViewPassengerSideComment)
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewFrontSideComment?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewFrontSideComment!!,mAppComTextViewFrontSideComment)
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewRearSideComment?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewRearSideComment!!,mAppComTextViewRearSideComment)
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewTrailerComment?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewTrailerComment!!,mAppComTextViewTrailerComment)
                    return false
                }
//                if (TextUtils.isEmpty(mAutoComTextViewComments?.text.toString().trim())) {
//                    setErrorOnField(mAutoComTextViewComments!!,mAppComTextViewComments)
//                    return false
//                }

            }else if(reportType.equals("NFL Special Assignment Report",ignoreCase = true)||
                reportType.equals("SPECIAL EVENT REPORT",ignoreCase = true)||
                reportType.equals("Lot Count Vio Rate Report",ignoreCase = true)) {

//                if (TextUtils.isEmpty(mAutoComTextViewBlock?.text.toString().trim())) {
//                    setErrorOnField(mAutoComTextViewBlock!!, mAppComTextViewBlock)
//                    return false
//                }
//                if (TextUtils.isEmpty(mAutoComTextViewStreet?.text.toString().trim())) {
//                    setErrorOnField(mAutoComTextViewStreet!!, mAppComTextViewStreet)
//                    return false
//                }
//                if (TextUtils.isEmpty(mAutoComTextViewDirection?.text.toString().trim())) {
//                    setErrorOnField(mAutoComTextViewDirection!!, mAppComTextViewDirection)
//                    return false
//                }
                if (TextUtils.isEmpty(mAutoComTextViewPBCZone?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewPBCZone!!, mAppComTextViewPBCZone)
                    return false
                }
                if (mAutoComTextViewMeterNo!=null && TextUtils.isEmpty(mAutoComTextViewMeterNo?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewMeterNo!!,mAppComTextViewMeterNo)
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewField1?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewField1!!, mAppComTextViewField1)
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewCarCount?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewCarCount!!, mAppComTextViewCarCount)
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewEmptySpace?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewEmptySpace!!, mAppComTextViewEmptySpace)
                    return false
                }
                if (TextUtils.isEmpty(
                        mAutoComTextViewNumberOfViolatingVehicles?.text.toString().trim()
                    )
                ) {
                    setErrorOnField(
                        mAutoComTextViewNumberOfViolatingVehicles!!,
                        mAppComTextViewNumberOfViolatingVehicles
                    )
                    return false
                }
            }else if(reportType.equals("PCH Daily Updates",ignoreCase = true))
            {

//                if (TextUtils.isEmpty(mAutoComTextViewBlock?.text.toString().trim())) {
//                    setErrorOnField(mAutoComTextViewBlock!!,mAppComTextViewBlock)
//                    return false
//                }
//                if (TextUtils.isEmpty(mAutoComTextViewStreet?.text.toString().trim())) {
//                    setErrorOnField(mAutoComTextViewStreet!!,mAppComTextViewStreet)
//                    return false
//                }
//                if (TextUtils.isEmpty(mAutoComTextViewDirection?.text.toString().trim())) {
//                    setErrorOnField(mAutoComTextViewDirection!!,mAppComTextViewDirection)
//                    return false
//                }
                if (TextUtils.isEmpty(mAutoComTextViewPBCZone?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewPBCZone!!,mAppComTextViewPBCZone)
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewCarCount?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewCarCount!!,mAppComTextViewCarCount)
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewLotArea?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewLotArea!!,mAppComTextViewLotArea)
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewSecurityObservation?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewSecurityObservation!!,mAppComTextViewNumberOfViolatingVehicles)
                    return false
                }
            }else if(reportType.equals("Pay Station Report",ignoreCase = true))
            {

//                if (TextUtils.isEmpty(mAutoComTextViewBlock?.text.toString().trim())) {
//                    setErrorOnField(mAutoComTextViewBlock!!,mAppComTextViewBlock)
//                    return false
//                }
//                if (TextUtils.isEmpty(mAutoComTextViewStreet?.text.toString().trim())) {
//                    setErrorOnField(mAutoComTextViewStreet!!,mAppComTextViewStreet)
//                    return false
//                }
//                if (TextUtils.isEmpty(mAutoComTextViewDirection?.text.toString().trim())) {
//                    setErrorOnField(mAutoComTextViewDirection!!,mAppComTextViewDirection)
//                    return false
//                }
                if (TextUtils.isEmpty(mAutoComTextViewPBCZone?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewPBCZone!!,mAppComTextViewPBCZone)
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewMeterNo?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewMeterNo!!,mAppComTextViewMeterNo)
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewField1?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewField1!!,mAppComTextViewField1)
                    return false
                }
//                if (TextUtils.isEmpty(mAutoComTextViewField2?.text.toString().trim())) {
//                    setErrorOnField(mAutoComTextViewField2!!,mAppComTextViewField2)
//                    return false
//                }
//                if (TextUtils.isEmpty(mAutoComTextViewField3?.text.toString().trim())) {
//                    setErrorOnField(mAutoComTextViewField3!!,mAppComTextViewField3)
//                    return false
//                }
                if (TextUtils.isEmpty(mAutoComTextViewField4?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewField4!!,mAppComTextViewField4)
                    return false
                }
//                if (TextUtils.isEmpty(mAutoComTextViewField5?.text.toString().trim())) {
//                    setErrorOnField(mAutoComTextViewField5!!,mAppComTextViewField5)
//                    return false
//                }
//                if (TextUtils.isEmpty(mAutoComTextViewField6?.text.toString().trim())) {
//                    setErrorOnField(mAutoComTextViewField6!!,mAppComTextViewField6)
//                    return false
//                }
            }else if(reportType.equals("Signage Report",ignoreCase = true))
            {

                if (TextUtils.isEmpty(mAutoComTextViewPBCZone?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewPBCZone!!,mAppComTextViewPBCZone)
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewMeterNo?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewMeterNo!!,mAppComTextViewMeterNo)
                    return false
                }
//                if (TextUtils.isEmpty(mAutoComTextViewField1?.text.toString().trim())) {
//                    setErrorOnField(mAutoComTextViewField1!!,mAppComTextViewField1)
//                    return false
//                }
//                if (TextUtils.isEmpty(mAutoComTextViewField2?.text.toString().trim())) {
//                    setErrorOnField(mAutoComTextViewField2!!,mAppComTextViewField2)
//                    return false
//                }
//                if (TextUtils.isEmpty(mAutoComTextViewField3?.text.toString().trim())) {
//                    setErrorOnField(mAutoComTextViewField3!!,mAppComTextViewField3)
//                    return false
//                }
            }else if(reportType.equals("Homeless Report",ignoreCase = true))
            {

                if (TextUtils.isEmpty(mAutoComTextViewPBCZone?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewPBCZone!!,mAppComTextViewPBCZone)
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewMeterNo?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewMeterNo!!,mAppComTextViewMeterNo)
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewField1?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewField1!!,mAppComTextViewField1)
                    return false
                }
                
            }else if(reportType.equals("Lot Inspection Report",ignoreCase = true))
            {

                if (TextUtils.isEmpty(mAutoComTextViewPBCZone?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewPBCZone!!,mAppComTextViewPBCZone)
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewInternetConnectivity?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewInternetConnectivity!!,mAppComTextViewInternetConnectivity)
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewMeterLabelVisible?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewMeterLabelVisible!!,mAppComTextViewMeterLabelVisible)
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewDigitalDisplayVisible?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewDigitalDisplayVisible!!,mAppComTextViewDigitalDisplayVisible)
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewCoinJam?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewCoinJam!!,mAppComTextViewCoinJam)
                    return false
                }

            }else if(reportType.equals("Work Order Report",ignoreCase = true))
            {

                if (TextUtils.isEmpty(mAutoComTextViewPBCZone?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewPBCZone!!,mAppComTextViewPBCZone)
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewField1?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewField1!!,mAppComTextViewField1)
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewField2?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewField2!!,mAppComTextViewField2)
                    return false
                }

            }else if(reportType.equals("Trash Lot Maintenance Report",ignoreCase = true))
            {

                if (TextUtils.isEmpty(mAutoComTextViewPBCZone?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewPBCZone!!,mAppComTextViewPBCZone)
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewRequiredServices?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewRequiredServices!!,mAppComTextViewRequiredServices)
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewField2?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewField2!!,mAppComTextViewField2)
                    return false
                }

            }else if(reportType.equals("Safety Report Immediate Attention Required",ignoreCase = true))
            {

                if (TextUtils.isEmpty(mAutoComTextViewPBCZone?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewPBCZone!!,mAppComTextViewPBCZone)
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewMeterNo?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewMeterNo!!,mAppComTextViewMeterNo)
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewSafetyIssue?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewSafetyIssue!!,mAppComTextViewSafetyIssue)
                    return false
                }
                if (TextUtils.isEmpty(mAutoComTextViewField2?.text.toString().trim())) {
                    setErrorOnField(mAutoComTextViewField2!!,mAppComTextViewField2)
                    return false
                }

            }

            if(cameraCount < imageValidationCount && cameraCount<minimumImageRequired) {
                LogUtil.printToastMSGForErrorWarning(applicationContext,
                        getString(R.string.msg_min_image).replace("#",
                            minimumImageRequired.nullSafety().toString() + ""))
                return false
            }

        }catch (e:Exception)
        {
            e.printStackTrace()
        }
        return true
    }

    private fun setErrorOnField(appCompatAutoCompleteTextView: AppCompatAutoCompleteTextView,
                                mAppComTextViewBlock: AppCompatTextView?)
    {
        try {
            appCompatAutoCompleteTextView?.requestFocus()
            appCompatAutoCompleteTextView?.isFocusable = true
            appCompatAutoCompleteTextView?.error = getString(R.string.val_msg_please_enter)+" "+mAppComTextViewBlock!!.text!!.toString()
            AppUtils.showKeyboard(this@AllReportActivity)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

private fun setErrorOnField(appCompatAutoCompleteTextView: TextInputEditText,
                            mAppComTextViewBlock: AppCompatTextView?)
    {
        try {
            appCompatAutoCompleteTextView?.requestFocus()
            appCompatAutoCompleteTextView?.isFocusable = true
            appCompatAutoCompleteTextView?.error = getString(R.string.val_msg_please_enter)+" "+mAppComTextViewBlock!!.text!!.toString()
            AppUtils.showKeyboard(this@AllReportActivity)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadUIForm(reportType : String)
    {
        if(reportType.equals("Broken Meter",ignoreCase = true))
        {
            mCameraImagesLayout!!.visibility = View.GONE
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)

            // Block
            mAppComTextViewBlock = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewBlock = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatBlock = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutBlock = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutBlock,
                appCompatAutoCompleteTextView = mAutoComTextViewBlock
            )
            mAppComTextViewBlock!!.setText(R.string.scr_lbl_block)
            mLocationDetails!!.addView(mLayoutCompatBlock)

            // Street
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewStreet = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewStreet = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatStreet = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutStreet = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutStreet,
                appCompatAutoCompleteTextView = mAutoComTextViewStreet
            )

            mAppComTextViewStreet!!.setText(R.string.scr_lbl_street)
            mLocationDetails!!.addView(mLayoutCompatStreet)

            // direction
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewDirection = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewDirection = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatDirection = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutDirection = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutDirection,
                appCompatAutoCompleteTextView = mAutoComTextViewDirection
            )
            mAppComTextViewDirection!!.setText(R.string.scr_lbl_direction)
//            mAutoComTextViewDirection!!.setText("S")
            mLocationDetails!!.addView(mLayoutCompatDirection)

            // meter_no
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewMeterNo = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewMeterNo = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatMeterNo = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutMeterNo = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutMeterNo,
                appCompatAutoCompleteTextView = mAutoComTextViewMeterNo
            )
            mAppComTextViewMeterNo!!.setText(R.string.scr_lbl_meter_no)
            mOtherDetails!!.addView(mLayoutCompatMeterNo)

            // meter_type
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewMeterType = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewMeterType = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatMeterType = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutMeterType = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutMeterType,
                appCompatAutoCompleteTextView = mAutoComTextViewMeterType
            )
            mAppComTextViewMeterType!!.setText(R.string.scr_lbl_meter_type)
            mOtherDetails!!.addView(mLayoutCompatMeterType)

            // meter_label_visible
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewMeterLabelVisible = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewMeterLabelVisible = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatMeterLabelVisible = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutMeterLableVisible = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutMeterLableVisible,
                appCompatAutoCompleteTextView = mAutoComTextViewMeterLabelVisible
            )
            mAppComTextViewMeterLabelVisible!!.setText(R.string.scr_lbl_meter_label)
            mOtherDetails!!.addView(mLayoutCompatMeterLabelVisible)

            // meter_label_visible
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewDigitalDisplayVisible = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewDigitalDisplayVisible = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatDigitalDisplayVisible = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutDigitalDisplayVisible = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutDigitalDisplayVisible,
                appCompatAutoCompleteTextView = mAutoComTextViewDigitalDisplayVisible
            )
            mAppComTextViewDigitalDisplayVisible!!.setText(R.string.scr_lbl_digital_display_visible)
            mOtherDetails!!.addView(mLayoutCompatDigitalDisplayVisible)

            // meter_label_visible
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewCoinJam = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewCoinJam = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatCoinJam = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutCoinJam = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutCoinJam,
                appCompatAutoCompleteTextView = mAutoComTextViewCoinJam
            )
            mAppComTextViewCoinJam!!.setText(R.string.scr_lbl_coin_jam)
            mOtherDetails!!.addView(mLayoutCompatCoinJam)

            // meter_label_visible
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewCreditCardJam = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewCreditCardJam = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatCreditCardJam = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutCreditCardJam = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutCreditCardJam,
                appCompatAutoCompleteTextView = mAutoComTextViewCreditCardJam
            )
            mAppComTextViewCreditCardJam!!.setText(R.string.scr_lbl_credit_card_jam)
            mOtherDetails!!.addView(mLayoutCompatCreditCardJam)

            // comment
            rootView = View.inflate(this@AllReportActivity, R.layout.content_comment, null)
            mAppComTextViewComments = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewComments = rootView!!.findViewById<TextInputEditText>(R.id.AutoComTextView)
            mLayoutCompatComments = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutComments = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutComments,
                textInputEditText = mAutoComTextViewComments
            )
            mAppComTextViewComments!!.setText(R.string.scr_lbl_comments)
            mOtherDetails!!.addView(mLayoutCompatComments)

           /* // status
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewStaus = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewStaus = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatStaus = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            mAppComTextViewStaus!!.setText(R.string.scr_lbl_status)
            mOtherDetails!!.addView(mLayoutCompatStaus)*/

        }else if(reportType.equals("Curb",ignoreCase = true))
        {
//            Front Photo, Back Photo, Passenger Side Photo, Driver Side Photo.
            mCameraImagesLayout!!.visibility = View.VISIBLE
            imageValidationCount = 1

            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)

            // Block
            mAppComTextViewBlock = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewBlock = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatBlock = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutBlock = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutBlock,
                textInputEditText = mAutoComTextViewComments
            )
            mAppComTextViewBlock!!.setText(R.string.scr_lbl_block)
            mLocationDetails!!.addView(mLayoutCompatBlock)

            // Street
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewStreet = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewStreet = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatStreet = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutStreet = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutStreet,
                textInputEditText = mAutoComTextViewComments
            )
            mAppComTextViewStreet!!.setText(R.string.scr_lbl_street)
            mLocationDetails!!.addView(mLayoutCompatStreet)

            // direction
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewDirection = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewDirection = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatDirection = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutDirection = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutDirection,
                appCompatAutoCompleteTextView = mAutoComTextViewDirection
            )
            mAppComTextViewDirection!!.setText(R.string.scr_lbl_direction)
//            mAutoComTextViewDirection!!.setText("S")
            mLocationDetails!!.addView(mLayoutCompatDirection)

            /*// device_id
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewDeviceId = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewDeviceId = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatDeviceId = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            mAppComTextViewDeviceId!!.setText(R.string.scr_lbl_device)
            mOtherDetails!!.addView(mLayoutCompatDeviceId)*/

            // enforceable
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewEnforceable = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewEnforceable = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatEnforceable = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutEnforceable = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutEnforceable,
                appCompatAutoCompleteTextView = mAutoComTextViewEnforceable
            )
            mAppComTextViewEnforceable!!.setText(R.string.scr_lbl_enforceable)
            mOtherDetails!!.addView(mLayoutCompatEnforceable)

            // CurbColor
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewCurbColor = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewCurbColor = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatCurbColor = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutCurbColor = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutCurbColor,
                appCompatAutoCompleteTextView = mAutoComTextViewCurbColor
            )
            mAppComTextViewCurbColor!!.setText(R.string.scr_lbl_curb_color)
            mOtherDetails!!.addView(mLayoutCompatCurbColor)


            // comment
            rootView = View.inflate(this@AllReportActivity, R.layout.content_comment, null)
            mAppComTextViewComments = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewComments = rootView!!.findViewById<TextInputEditText>(R.id.AutoComTextView)
            mLayoutCompatComments = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutComments = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutComments,
                textInputEditText = mAutoComTextViewComments
            )
            mAppComTextViewComments!!.setText(R.string.scr_lbl_comments)
            mOtherDetails!!.addView(mLayoutCompatComments)

           /* // status
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewStaus = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewStaus = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatStaus = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            mAppComTextViewStaus!!.setText(R.string.scr_lbl_status)
            mOtherDetails!!.addView(mLayoutCompatStaus)*/

        }else if(reportType.equals("Full Time Report",ignoreCase = true))
        {
            mCameraImagesLayout!!.visibility = View.GONE
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)

           /* // Block
            mAppComTextViewBlock = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewBlock = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatBlock = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            mAppComTextViewBlock!!.setText(R.string.scr_lbl_block)
            mLocationDetails!!.addView(mLayoutCompatBlock)

            // Street
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewStreet = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewStreet = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatStreet = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            mAppComTextViewStreet!!.setText(R.string.scr_lbl_street)
            mLocationDetails!!.addView(mLayoutCompatStreet)*/

            // device_id
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewDeviceId = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewDeviceId = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatDeviceId = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutDeviceId = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutDeviceId,
                appCompatAutoCompleteTextView = mAutoComTextViewDeviceId
            )
            mAppComTextViewDeviceId!!.setText(R.string.scr_lbl_device)
            mOtherDetails!!.addView(mLayoutCompatDeviceId)

//            // Date
//            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
//            mAppComTextViewDate = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
//            mAutoComTextViewDate = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
//            mLayoutCompatDate = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
//            mAppComTextViewDate!!.setText(R.string.scr_lbl_date_report)
//            mOtherDetails!!.addView(mLayoutCompatDate)

            // DutyHours
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewDutyHours = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewDutyHours = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatDutyHours = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            mInputLayoutDutyHours = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = mInputLayoutDutyHours,
                appCompatAutoCompleteTextView = mAutoComTextViewDutyHours
            )
            mAppComTextViewDutyHours!!.setText(R.string.scr_lbl_duty_hours)
            mOtherDetails!!.addView(mLayoutCompatDutyHours)

//            mInputLayoutDutyHours = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
//            mInputLayoutDutyHours!!.endIconMode = END_ICON_NONE

           /* // Officer
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewOfficer = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewOfficer = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatOfficer = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            mAppComTextViewOfficer!!.setText(R.string.scr_lbl_officer)
            mOtherDetails!!.addView(mLayoutCompatOfficer)*/

            // lunch taken
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewLunchTaken = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewLunchTaken = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatLunchTaken = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutLunchTaken = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutLunchTaken,
                appCompatAutoCompleteTextView = mAutoComTextViewLunchTaken
            )
            mAppComTextViewLunchTaken!!.setText(R.string.scr_lbl_lunch_taken)
            mOtherDetails!!.addView(mLayoutCompatLunchTaken)

            // first 10 min break
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewFirst10MinBreak = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewFirst10MinBreak = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatFirst10MinBreak = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutFirst10MinBreak = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutFirst10MinBreak,
                appCompatAutoCompleteTextView = mAutoComTextViewFirst10MinBreak
            )
            mAppComTextViewFirst10MinBreak!!.setText(R.string.scr_lbl_first_10_mins_break)
            mOtherDetails!!.addView(mLayoutCompatFirst10MinBreak)

            // second 10 min break
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewSecond10MinBreak = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewSecond10MinBreak = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatSecond10MinBreak = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutSecond10MinBreak = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutSecond10MinBreak,
                appCompatAutoCompleteTextView = mAutoComTextViewSecond10MinBreak
            )
            mAppComTextViewSecond10MinBreak!!.setText(R.string.scr_lbl_second_10_mins_break)
            mOtherDetails!!.addView(mLayoutCompatSecond10MinBreak)

            // unit_no
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewUnitNo = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewUnitNo = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatUnitNo = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutUnitNo = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutUnitNo,
                appCompatAutoCompleteTextView = mAutoComTextViewUnitNo
            )
            mAppComTextViewUnitNo!!.setText(R.string.scr_lbl_unit_no)
            mOtherDetails!!.addView(mLayoutCompatUnitNo)

            // Assigned Area
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewAssignedArea = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewAssignedArea = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatAssignedArea = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutAssignedArea = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutAssignedArea,
                appCompatAutoCompleteTextView = mAutoComTextViewAssignedArea
            )
            mAppComTextViewAssignedArea!!.setText(R.string.scr_lbl_assigned_area)
            mOtherDetails!!.addView(mLayoutCompatAssignedArea)

            // Ossi DeviceNo
          /*  rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewOssiDeviceNo = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewOssiDeviceNo = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatOssiDeviceNo = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            mAppComTextViewOssiDeviceNo!!.setText(R.string.scr_lbl_ossi_device_no)
            mOtherDetails!!.addView(mLayoutCompatOssiDeviceNo)*/


           /* // handheld_unit_no
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewHandheldUnitNo = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewHandheldUnitNo = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatHandheldUnitNo = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            mAppComTextViewHandheldUnitNo!!.setText(R.string.scr_lbl_handheld_unit_no)
            mOtherDetails!!.addView(mLayoutCompatHandheldUnitNo)*/

            // citation issue
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewCitationsIssued = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewCitationsIssued = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatCitationsIssued = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            mInputLayoutCitationsIssued = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = mInputLayoutCitationsIssued,
                appCompatAutoCompleteTextView = mAutoComTextViewCitationsIssued
            )
            mAppComTextViewCitationsIssued!!.setText(R.string.scr_lbl_citation_issue)
            mOtherDetails!!.addView(mLayoutCompatCitationsIssued)

            mInputLayoutCitationsIssued = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            mInputLayoutCitationsIssued!!.endIconMode = END_ICON_NONE

            // special_enforcement_request
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewSpecialEnforcementRequest = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewSpecialEnforcementRequest = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatSpecialEnforcementRequest = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            mInputLayoutSpecialEnforcementRequest = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = mInputLayoutSpecialEnforcementRequest,
                appCompatAutoCompleteTextView = mAutoComTextViewSpecialEnforcementRequest
            )
            mAppComTextViewSpecialEnforcementRequest!!.setText(R.string.scr_lbl_special_enforcement_request)
            mOtherDetails!!.addView(mLayoutCompatSpecialEnforcementRequest)

            mInputLayoutSpecialEnforcementRequest = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            mInputLayoutSpecialEnforcementRequest!!.endIconMode = END_ICON_NONE

            /*// status
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewStaus = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewStaus = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatStaus = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            mAppComTextViewStaus!!.setText(R.string.scr_lbl_status)
            mOtherDetails!!.addView(mLayoutCompatStaus)*/

        }else if(reportType.equals("Part Time Report",ignoreCase = true))
        {
            mCameraImagesLayout!!.visibility = View.GONE
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)

           /* // Block
            mAppComTextViewBlock = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewBlock = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatBlock = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            mAppComTextViewBlock!!.setText(R.string.scr_lbl_block)
            mLocationDetails!!.addView(mLayoutCompatBlock)

            // Street
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewStreet = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewStreet = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatStreet = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            mAppComTextViewStreet!!.setText(R.string.scr_lbl_street)
            mLocationDetails!!.addView(mLayoutCompatStreet)*/

            // device_id
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewDeviceId = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewDeviceId = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatDeviceId = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutDeviceId = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutDeviceId,
                appCompatAutoCompleteTextView = mAutoComTextViewDeviceId
            )
            mAppComTextViewDeviceId!!.setText(R.string.scr_lbl_device)
            mOtherDetails!!.addView(mLayoutCompatDeviceId)

//            // Date
//            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
//            mAppComTextViewDate = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
//            mAutoComTextViewDate = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
//            mLayoutCompatDate = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
//            mAppComTextViewDate!!.setText(R.string.scr_lbl_date_report)
//            mOtherDetails!!.addView(mLayoutCompatDate)

            // DutyHours
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewDutyHours = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewDutyHours = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatDutyHours = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            mInputLayoutDutyHours = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = mInputLayoutDutyHours,
                appCompatAutoCompleteTextView = mAutoComTextViewDutyHours
            )
            mAppComTextViewDutyHours!!.setText(R.string.scr_lbl_duty_hours)
            mOtherDetails!!.addView(mLayoutCompatDutyHours)

//            mInputLayoutDutyHours = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
//            mInputLayoutDutyHours!!.endIconMode = END_ICON_NONE

           /* // Officer
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewOfficer = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewOfficer = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatOfficer = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            mAppComTextViewOfficer!!.setText(R.string.scr_lbl_officer)
            mOtherDetails!!.addView(mLayoutCompatOfficer)*/

            /*// lunch taken
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewLunchTaken = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewLunchTaken = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatLunchTaken = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            mAppComTextViewLunchTaken!!.setText(R.string.scr_lbl_lunch_taken)
            mOtherDetails!!.addView(mLayoutCompatLunchTaken)*/

            // first 10 min break
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewFirst10MinBreak = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewFirst10MinBreak = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatFirst10MinBreak = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutFirst10MinBreak = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutFirst10MinBreak,
                appCompatAutoCompleteTextView = mAutoComTextViewFirst10MinBreak
            )
            mAppComTextViewFirst10MinBreak!!.setText(R.string.scr_lbl_first_10_mins_break)
            mOtherDetails!!.addView(mLayoutCompatFirst10MinBreak)

            // second 10 min break
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewSecond10MinBreak = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewSecond10MinBreak = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatSecond10MinBreak = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutSecond10MinBreak = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutSecond10MinBreak,
                appCompatAutoCompleteTextView = mAutoComTextViewSecond10MinBreak
            )
            mAppComTextViewSecond10MinBreak!!.setText(R.string.scr_lbl_second_10_mins_break)
            mOtherDetails!!.addView(mLayoutCompatSecond10MinBreak)

            // unit_no
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewUnitNo = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewUnitNo = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatUnitNo = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutUnitNo = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutUnitNo,
                appCompatAutoCompleteTextView = mAutoComTextViewUnitNo
            )
            mAppComTextViewUnitNo!!.setText(R.string.scr_lbl_unit_no)
            mOtherDetails!!.addView(mLayoutCompatUnitNo)

            // Assigned Area
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewAssignedArea = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewAssignedArea = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatAssignedArea = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutAssignedArea = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutAssignedArea,
                appCompatAutoCompleteTextView = mAutoComTextViewAssignedArea
            )
            mAppComTextViewAssignedArea!!.setText(R.string.scr_lbl_assigned_area)
            mOtherDetails!!.addView(mLayoutCompatAssignedArea)

            // Ossi DeviceNo
          /*  rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewOssiDeviceNo = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewOssiDeviceNo = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatOssiDeviceNo = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            mAppComTextViewOssiDeviceNo!!.setText(R.string.scr_lbl_ossi_device_no)
            mOtherDetails!!.addView(mLayoutCompatOssiDeviceNo)*/


           /* // handheld_unit_no
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewHandheldUnitNo = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewHandheldUnitNo = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatHandheldUnitNo = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            mAppComTextViewHandheldUnitNo!!.setText(R.string.scr_lbl_handheld_unit_no)
            mOtherDetails!!.addView(mLayoutCompatHandheldUnitNo)*/

            // citation issue
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewCitationsIssued = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewCitationsIssued = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatCitationsIssued = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            mInputLayoutCitationsIssued = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = mInputLayoutCitationsIssued,
                appCompatAutoCompleteTextView = mAutoComTextViewCitationsIssued
            )
            mAppComTextViewCitationsIssued!!.setText(R.string.scr_lbl_citation_issue)
            mOtherDetails!!.addView(mLayoutCompatCitationsIssued)

            mInputLayoutCitationsIssued = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            mInputLayoutCitationsIssued!!.endIconMode = END_ICON_NONE

            // special_enforcement_request
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewSpecialEnforcementRequest = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewSpecialEnforcementRequest = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatSpecialEnforcementRequest = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            mAppComTextViewSpecialEnforcementRequest!!.setText(R.string.scr_lbl_special_enforcement_request)
            mOtherDetails!!.addView(mLayoutCompatSpecialEnforcementRequest)

            mInputLayoutSpecialEnforcementRequest = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            mInputLayoutSpecialEnforcementRequest!!.endIconMode = END_ICON_NONE

            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = mInputLayoutSpecialEnforcementRequest,
                appCompatAutoCompleteTextView = mAutoComTextViewSpecialEnforcementRequest
            )

            /*// status
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewStaus = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewStaus = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatStaus = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            mAppComTextViewStaus!!.setText(R.string.scr_lbl_status)
            mOtherDetails!!.addView(mLayoutCompatStaus)*/

        }else if(reportType.equals("Hand Held Malfunction",ignoreCase = true))
        {
            mCameraImagesLayout!!.visibility = View.GONE
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)

            /*// Block
            mAppComTextViewBlock = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewBlock = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatBlock = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            mAppComTextViewBlock!!.setText(R.string.scr_lbl_block)
            mLocationDetails!!.addView(mLayoutCompatBlock)

            // Street
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewStreet = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewStreet = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatStreet = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            mAppComTextViewStreet!!.setText(R.string.scr_lbl_street)
            mLocationDetails!!.addView(mLayoutCompatStreet)*/

            // device_id
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewDeviceId = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewDeviceId = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatDeviceId = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutDeviceId = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutDeviceId,
                appCompatAutoCompleteTextView = mAutoComTextViewDeviceId
            )
            mAppComTextViewDeviceId!!.setText(R.string.scr_lbl_device)
            mOtherDetails!!.addView(mLayoutCompatDeviceId)

           /* // handheld_unit_no
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewHandheldUnitNo = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewHandheldUnitNo = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatHandheldUnitNo = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            mAppComTextViewHandheldUnitNo!!.setText(R.string.scr_lbl_handheld_unit_no)
            mOtherDetails!!.addView(mLayoutCompatHandheldUnitNo)*/

            // TriedToRestartHandHeld
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewTriedToRestartHandHeld = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewTriedToRestartHandHeld = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatTriedToRestartHandHeld = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutTriedToRestartHandheld = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutTriedToRestartHandheld,
                appCompatAutoCompleteTextView = mAutoComTextViewTriedToRestartHandHeld
            )
            mAppComTextViewTriedToRestartHandHeld!!.setText(R.string.scr_lbl_tried_to_restart_hand_held)
            mOtherDetails!!.addView(mLayoutCompatTriedToRestartHandHeld)

            // PrintingCorrectly
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewPrintingCorrectly = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewPrintingCorrectly = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatPrintingCorrectly = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutPrintingCorrectly = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutPrintingCorrectly,
                appCompatAutoCompleteTextView = mAutoComTextViewPrintingCorrectly
            )
            mAppComTextViewPrintingCorrectly!!.setText(R.string.scr_lbl_printing_correctly)
            mOtherDetails!!.addView(mLayoutCompatPrintingCorrectly)

            // OverHeating
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewOverHeating = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewOverHeating = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatOverHeating = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutOverHeating = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutOverHeating,
                appCompatAutoCompleteTextView = mAutoComTextViewOverHeating
            )
            mAppComTextViewOverHeating!!.setText(R.string.scr_lbl_over_heating)
            mOtherDetails!!.addView(mLayoutCompatOverHeating)

            // Battery Hold Charge
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewBatteryHoldCharge = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewBatteryHoldCharge = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatBatteryHoldCharge = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutBatteryHoldCharge = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutBatteryHoldCharge,
                appCompatAutoCompleteTextView = mAutoComTextViewBatteryHoldCharge
            )
            mAppComTextViewBatteryHoldCharge!!.setText(R.string.scr_lbl_battery_hold_charge)
            mOtherDetails!!.addView(mLayoutCompatBatteryHoldCharge)

            // Internet Connectivity
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewInternetConnectivity = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewInternetConnectivity = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatInternetConnectivity = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutInternetConnectivity = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutInternetConnectivity,
                appCompatAutoCompleteTextView = mAutoComTextViewInternetConnectivity
            )
            mAppComTextViewInternetConnectivity!!.setText(R.string.scr_lbl_internet_connectivity)
            mOtherDetails!!.addView(mLayoutCompatInternetConnectivity)

            // Describe Hand Held Malfunction In Detail
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewDescribeHandHeldMalfunctionInDetail = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewDescribeHandHeldMalfunctionInDetail = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatDescribeHandHeldMalfunctionInDetail = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            mInputLayoutDescribeHandHeldMalfunctionInDetail = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = mInputLayoutDescribeHandHeldMalfunctionInDetail,
                appCompatAutoCompleteTextView = mAutoComTextViewDescribeHandHeldMalfunctionInDetail
            )
            mAppComTextViewDescribeHandHeldMalfunctionInDetail!!.setText(R.string.scr_lbl_describe_hand_held_malfunction_in_detail)
            mOtherDetails!!.addView(mLayoutCompatDescribeHandHeldMalfunctionInDetail)

            mInputLayoutDescribeHandHeldMalfunctionInDetail = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            mInputLayoutDescribeHandHeldMalfunctionInDetail!!.endIconMode = END_ICON_NONE


            // status
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewStaus = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewStaus = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatStaus = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutStatus = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutStatus,
                appCompatAutoCompleteTextView = mAutoComTextViewStaus
            )
            mAppComTextViewStaus!!.setText(R.string.scr_lbl_status)
            mOtherDetails!!.addView(mLayoutCompatStaus)
        }
        else if(reportType.equals("Sign Report",ignoreCase = true))
        {
            mCameraImagesLayout!!.visibility = View.VISIBLE
            imageValidationCount = 1
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)

            // Block
            mAppComTextViewBlock = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewBlock = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatBlock = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutBlock = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutBlock,
                appCompatAutoCompleteTextView = mAutoComTextViewBlock
            )
            mAppComTextViewBlock!!.setText(R.string.scr_lbl_block)
            mLocationDetails!!.addView(mLayoutCompatBlock)

            // Street
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewStreet = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewStreet = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatStreet = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutStreet = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutStreet,
                appCompatAutoCompleteTextView = mAutoComTextViewStreet
            )
            mAppComTextViewStreet!!.setText(R.string.scr_lbl_street)
            mLocationDetails!!.addView(mLayoutCompatStreet)

            // direction
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewDirection = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewDirection = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatDirection = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutDirection = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutDirection,
                appCompatAutoCompleteTextView = mAutoComTextViewDirection
            )
            mAppComTextViewDirection!!.setText(R.string.scr_lbl_direction)
//            mAutoComTextViewDirection!!.setText("S")
            mLocationDetails!!.addView(mLayoutCompatDirection)

           /* // device_id
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewDeviceId = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewDeviceId = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatDeviceId = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            mAppComTextViewDeviceId!!.setText(R.string.scr_lbl_device)
            mOtherDetails!!.addView(mLayoutCompatDeviceId)*/

            // enforceable
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewEnforceable = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewEnforceable = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatEnforceable = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutEnforceable = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutEnforceable,
                appCompatAutoCompleteTextView = mAutoComTextViewEnforceable
            )
            mAppComTextViewEnforceable!!.setText(R.string.scr_lbl_enforceable)
            mOtherDetails!!.addView(mLayoutCompatEnforceable)

            // Graffiti
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewGraffiti = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewGraffiti = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatGraffiti = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutGraffiti = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutGraffiti,
                appCompatAutoCompleteTextView = mAutoComTextViewGraffiti
            )
            mAppComTextViewGraffiti!!.setText(R.string.scr_lbl_graffiti)
            mOtherDetails!!.addView(mLayoutCompatGraffiti)

            // missing_sign
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewMissingSign = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewMissingSign = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatMissingSign = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutMissingSign = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutMissingSign,
                appCompatAutoCompleteTextView = mAutoComTextViewMissingSign
            )
            mAppComTextViewMissingSign!!.setText(R.string.scr_lbl_missing_sign)
            mOtherDetails!!.addView(mLayoutCompatMissingSign)

            // comment
            rootView = View.inflate(this@AllReportActivity, R.layout.content_comment, null)
            mAppComTextViewComments = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewComments = rootView!!.findViewById<TextInputEditText>(R.id.AutoComTextView)
            mLayoutCompatComments = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutComments = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutComments,
                textInputEditText = mAutoComTextViewComments
            )
            mAppComTextViewComments!!.setText(R.string.scr_lbl_comments)
            mOtherDetails!!.addView(mLayoutCompatComments)

           /* // status
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewStaus = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewStaus = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatStaus = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            mAppComTextViewStaus!!.setText(R.string.scr_lbl_status)
            mOtherDetails!!.addView(mLayoutCompatStaus)*/

            tvCameraTitle.setText("Image Section")

        }
       else if(reportType.equals("Vehicle Inspection",ignoreCase = true))
        {
            mCameraImagesLayout!!.visibility = View.VISIBLE
            if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAMETRO, ignoreCase = true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CORPUSCHRISTI, ignoreCase = true)) {
                imageValidationCount = 7
            }else {
                imageValidationCount = 4
            }
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)

            /*// Block
            mAppComTextViewBlock = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewBlock = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatBlock = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            mAppComTextViewBlock!!.setText(R.string.scr_lbl_block)
            mLocationDetails!!.addView(mLayoutCompatBlock)

            // Street
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewStreet = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewStreet = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatStreet = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            mAppComTextViewStreet!!.setText(R.string.scr_lbl_street)
            mLocationDetails!!.addView(mLayoutCompatStreet)

            // device_id
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewDeviceId = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewDeviceId = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatDeviceId = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            mAppComTextViewDeviceId!!.setText(R.string.scr_lbl_device)
            mOtherDetails!!.addView(mLayoutCompatDeviceId)*/

           /* // Officer
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewOfficer = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewOfficer = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatOfficer = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            mAppComTextViewOfficer!!.setText(R.string.scr_lbl_officer)
            mOtherDetails!!.addView(mLayoutCompatOfficer)*/

            // Vehicle
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewVehicle = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewVehicle = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatVehicle = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutVehicle = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutVehicle,
                appCompatAutoCompleteTextView = mAutoComTextViewVehicle
            )
            mAppComTextViewVehicle!!.setText(R.string.scr_lbl_vehicle)
            mOtherDetails!!.addView(mLayoutCompatVehicle)

            // StartingMileage
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewStartingMileage = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewStartingMileage = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatStartingMileage = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutStartingMileage = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutStartingMileage,
                appCompatAutoCompleteTextView = mAutoComTextViewStartingMileage
            )
            mAppComTextViewStartingMileage!!.setText(R.string.scr_lbl_starting_mileage)
            mOtherDetails!!.addView(mLayoutCompatStartingMileage)

            // GasLevel
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewGasLevel = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewGasLevel = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatGasLevel = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutGasLevel = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutGasLevel,
                appCompatAutoCompleteTextView = mAutoComTextViewGasLevel
            )
            mAppComTextViewGasLevel!!.setText(R.string.scr_lbl_gas_level)
            mOtherDetails!!.addView(mLayoutCompatGasLevel)

            // LightBar
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewLightBar = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewLightBar = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatLightBar = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutLightBar = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutLightBar,
                appCompatAutoCompleteTextView = mAutoComTextViewLightBar
            )
            mAppComTextViewLightBar!!.setText(R.string.scr_lbl_light_bar)
            mOtherDetails!!.addView(mLayoutCompatLightBar)

            // DashBoardIndications
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewDashBoardIndications = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewDashBoardIndications = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatDashBoardIndications = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutDashBoardIndications = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutDashBoardIndications,
                appCompatAutoCompleteTextView = mAutoComTextViewDashBoardIndications
            )
            mAppComTextViewDashBoardIndications!!.setText(R.string.scr_lbl_dashboard_indications)
            mOtherDetails!!.addView(mLayoutCompatDashBoardIndications)

            // SeatBeltOperational
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewSeatBeltOperational = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewSeatBeltOperational = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatSeatBeltOperational = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutSeatBeltOperational = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutSeatBeltOperational,
                appCompatAutoCompleteTextView = mAutoComTextViewSeatBeltOperational
            )
            mAppComTextViewSeatBeltOperational!!.setText(R.string.scr_lbl_seat_belt_operational)
            mOtherDetails!!.addView(mLayoutCompatSeatBeltOperational)

            if(!BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAMETRO,ignoreCase = true)&&
                !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CORPUSCHRISTI,ignoreCase = true)) {
                // brakes
                rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
                mAppComTextViewBrakes = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
                mAutoComTextViewBrakes =
                    rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
                mLayoutCompatBrakes =
                    rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
                textInputLayoutBrakes = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
                setCrossClearButton(
                context = this@AllReportActivity,
                    textInputLayout = textInputLayoutBrakes,
                    appCompatAutoCompleteTextView = mAutoComTextViewBrakes
                )
                mAppComTextViewBrakes!!.setText(R.string.scr_lbl_brakes)
                mOtherDetails!!.addView(mLayoutCompatBrakes)


                // BrakeLights
                rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
                mAppComTextViewBrakeLights =
                    rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
                mAutoComTextViewBrakeLights =
                    rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
                mLayoutCompatBrakeLights =
                    rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
                textInputLayoutBrakeLights = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
                setCrossClearButton(
                context = this@AllReportActivity,
                    textInputLayout = textInputLayoutBrakeLights,
                    appCompatAutoCompleteTextView = mAutoComTextViewBrakeLights
                )
                mAppComTextViewBrakeLights!!.setText(R.string.scr_lbl_brake_lights)
                mOtherDetails!!.addView(mLayoutCompatBrakeLights)
            }

            // HeadLights
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewHeadLights = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewHeadLights = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatHeadLights = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutHeadLights = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutHeadLights,
                appCompatAutoCompleteTextView = mAutoComTextViewHeadLights
            )
            mAppComTextViewHeadLights!!.setText(R.string.scr_lbl_head_lights)
            mOtherDetails!!.addView(mLayoutCompatHeadLights)

            // TurnSignals
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewTurnSignals = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewTurnSignals = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatTurnSignals = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutSignals = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutSignals,
                appCompatAutoCompleteTextView = mAutoComTextViewTurnSignals
            )
            mAppComTextViewTurnSignals!!.setText(R.string.scr_lbl_turn_signals)
            mOtherDetails!!.addView(mLayoutCompatTurnSignals)

            if(!BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAMETRO,ignoreCase = true)&&
                !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CORPUSCHRISTI,ignoreCase = true)) {
                // SteeringWheelOperational
                rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
                mAppComTextViewSteeringWheelOperational =
                    rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
                mAutoComTextViewSteeringWheelOperational =
                    rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
                mLayoutCompatSteeringWheelOperational =
                    rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
                textInputLayoutSteeringWheelOperational = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
                setCrossClearButton(
                context = this@AllReportActivity,
                    textInputLayout = textInputLayoutSteeringWheelOperational,
                    appCompatAutoCompleteTextView = mAutoComTextViewSteeringWheelOperational
                )
                mAppComTextViewSteeringWheelOperational!!.setText(R.string.scr_lbl_steering_wheel_operational)
                mOtherDetails!!.addView(mLayoutCompatSteeringWheelOperational)
            }

            // windshield_visibility
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewWindshieldVisibility = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewWindshieldVisibility = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatWindshieldVisibility = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutWindshieldVisibility = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutWindshieldVisibility,
                appCompatAutoCompleteTextView = mAutoComTextViewWindshieldVisibility
            )
            mAppComTextViewWindshieldVisibility!!.setText(R.string.scr_lbl_windshield_visibility)
            mOtherDetails!!.addView(mLayoutCompatWindshieldVisibility)

            // side_and_rear_view_mirrors_operational
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewSideAndRearViewMirrorsOperational = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewSideAndRearViewMirrorsOperational = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatSideAndRearViewMirrorsOperational = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutSideAndRearViewMirrorsOperational = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutSideAndRearViewMirrorsOperational,
                appCompatAutoCompleteTextView = mAutoComTextViewSideAndRearViewMirrorsOperational
            )
            mAppComTextViewSideAndRearViewMirrorsOperational!!.setText(R.string.scr_lbl_side_and_rear_view_mirrors_operational)
            mOtherDetails!!.addView(mLayoutCompatSideAndRearViewMirrorsOperational)

            // windshield_wipers_operational
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewWindshieldWipersOperational = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewWindshieldWipersOperational = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatWindshieldWipersOperational = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutWindshieldWipersOperational = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutWindshieldWipersOperational,
                appCompatAutoCompleteTextView = mAutoComTextViewWindshieldWipersOperational
            )
            mAppComTextViewWindshieldWipersOperational!!.setText(R.string.scr_lbl_windshield_wipers_operational)
            mOtherDetails!!.addView(mLayoutCompatWindshieldWipersOperational)

            if(!BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAMETRO,ignoreCase = true)&&
                !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CORPUSCHRISTI,ignoreCase = true)) {
                // scr_lbl_vehicle_registration_and_insurance
                rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
                mAppComTextViewVehicleRegistrationAndInsurance =
                    rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
                mAutoComTextViewVehicleRegistrationAndInsurance =
                    rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
                mLayoutCompatVehicleRegistrationAndInsurance =
                    rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
                textInputLayoutVehicleRegistrationAndInsurance = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
                setCrossClearButton(
                context = this@AllReportActivity,
                    textInputLayout = textInputLayoutVehicleRegistrationAndInsurance,
                    appCompatAutoCompleteTextView = mAutoComTextViewVehicleRegistrationAndInsurance
                )
                mAppComTextViewVehicleRegistrationAndInsurance!!.setText(R.string.scr_lbl_vehicle_registration_and_insurance)
                mOtherDetails!!.addView(mLayoutCompatVehicleRegistrationAndInsurance)
            }

            if(!BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAMETRO,ignoreCase = true)&&
                !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CORPUSCHRISTI,ignoreCase = true)) {
                // cones_six_per_vehicle
                rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
                mAppComTextViewConesSixPerVehicle =
                    rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
                mAutoComTextViewConesSixPerVehicle =
                    rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
                mLayoutCompatConesSixPerVehicle =
                    rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
                textInputLayoutConesSixPerVehicle = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
                setCrossClearButton(
                context = this@AllReportActivity,
                    textInputLayout = textInputLayoutConesSixPerVehicle,
                    appCompatAutoCompleteTextView = mAutoComTextViewConesSixPerVehicle
                )
                mAppComTextViewConesSixPerVehicle!!.setText(R.string.scr_lbl_cones_six_per_vehicle)
                mOtherDetails!!.addView(mLayoutCompatConesSixPerVehicle)

                // scr_lbl_first_aid_kit
                rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
                mAppComTextViewFirstAidKit =
                    rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
                mAutoComTextViewFirstAidKit =
                    rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
                mLayoutCompatFirstAidKit =
                    rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
                textInputLayoutFirstAidKit = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
                setCrossClearButton(
                context = this@AllReportActivity,
                    textInputLayout = textInputLayoutFirstAidKit,
                    appCompatAutoCompleteTextView = mAutoComTextViewFirstAidKit
                )
                mAppComTextViewFirstAidKit!!.setText(R.string.scr_lbl_first_aid_kit)
                mOtherDetails!!.addView(mLayoutCompatFirstAidKit)
            }

            // horn
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewHorn = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewHorn = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatHorn = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutHorn = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutHorn,
                appCompatAutoCompleteTextView = mAutoComTextViewHorn
            )
            mAppComTextViewHorn!!.setText(R.string.scr_lbl_horn)
            mOtherDetails!!.addView(mLayoutCompatHorn)

            // scr_lbl_interior_cleanliness
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewInteriorCleanliness = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewInteriorCleanliness = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatInteriorCleanliness = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            mInputLayoutInteriorCleanliness = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = mInputLayoutInteriorCleanliness,
                appCompatAutoCompleteTextView = mAutoComTextViewInteriorCleanliness
            )
            mAppComTextViewInteriorCleanliness!!.setText(R.string.scr_lbl_interior_cleanliness)
            mOtherDetails!!.addView(mLayoutCompatInteriorCleanliness)

            // exterior_cleanliness
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewExteriorCleanliness = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewExteriorCleanliness = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatExteriorCleanliness = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutExteriorCleanliness = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutExteriorCleanliness,
                appCompatAutoCompleteTextView = mAutoComTextViewExteriorCleanliness
            )
            mAppComTextViewExteriorCleanliness!!.setText(R.string.scr_lbl_exterior_cleanliness)
            mOtherDetails!!.addView(mLayoutCompatExteriorCleanliness)

            // lpr_lens_free_of_debris
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewLprLensFreeOfDebris = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewLprLensFreeOfDebris = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatLprLensFreeOfDebris = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutLprLensFreeOfDebris = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutLprLensFreeOfDebris,
                appCompatAutoCompleteTextView = mAutoComTextViewLprLensFreeOfDebris
            )
            mAppComTextViewLprLensFreeOfDebris!!.setText(R.string.scr_lbl_lpr_lens_free_of_debris)
            mOtherDetails!!.addView(mLayoutCompatLprLensFreeOfDebris)

            // visible_leaks
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewVisibleLeaks = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewVisibleLeaks = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatVisibleLeaks = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutVisibleLeaks = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutVisibleLeaks,
                appCompatAutoCompleteTextView = mAutoComTextViewVisibleLeaks
            )
            mAppComTextViewVisibleLeaks!!.setText(R.string.scr_lbl_visible_leaks)
            mOtherDetails!!.addView(mLayoutCompatVisibleLeaks)

            // scr_lbl_tires_visual_inspection
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewTiresVisualInspection = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewTiresVisualInspection = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatTiresVisualInspection = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutTiresVisualInspection = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutTiresVisualInspection,
                appCompatAutoCompleteTextView = mAutoComTextViewTiresVisualInspection
            )
            mAppComTextViewTiresVisualInspection!!.setText(R.string.scr_lbl_tires_visual_inspection)
            mOtherDetails!!.addView(mLayoutCompatTiresVisualInspection)


            // comment
            rootView = View.inflate(this@AllReportActivity, R.layout.content_comment, null)
            mAppComTextViewComments = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewComments = rootView!!.findViewById<TextInputEditText>(R.id.AutoComTextView)
            mLayoutCompatComments = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutComments = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutComments,
                textInputEditText = mAutoComTextViewComments
            )
            mAppComTextViewComments!!.setText(R.string.scr_lbl_comments)
            mOtherDetails!!.addView(mLayoutCompatComments)

           /* // status
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewStaus = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewStaus = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatStaus = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            mAppComTextViewStaus!!.setText(R.string.scr_lbl_status)
            mOtherDetails!!.addView(mLayoutCompatStaus)*/
            tvCameraTitle.setText("Image Section")
        }
        else if(reportType.equals("72 Hour Marked Vehicles",ignoreCase = true))
        {
            mCameraImagesLayout!!.visibility = View.VISIBLE
            imageValidationCount = 2

            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)

            // Block
            mAppComTextViewBlock = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewBlock = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatBlock = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutBlock = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutBlock,
                appCompatAutoCompleteTextView = mAutoComTextViewBlock
            )
            mAppComTextViewBlock!!.setText(R.string.scr_lbl_block)
            mLocationDetails!!.addView(mLayoutCompatBlock)

            // Street
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewStreet = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewStreet = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatStreet = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutStreet = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutStreet,
                appCompatAutoCompleteTextView = mAutoComTextViewStreet
            )
            mAppComTextViewStreet!!.setText(R.string.scr_lbl_street)
            mLocationDetails!!.addView(mLayoutCompatStreet)

            // device_id
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewDeviceId = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewDeviceId = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatDeviceId = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutDeviceId = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutDeviceId,
                appCompatAutoCompleteTextView = mAutoComTextViewDeviceId
            )
            mAppComTextViewDeviceId!!.setText(R.string.scr_lbl_device)
            mOtherDetails!!.addView(mLayoutCompatDeviceId)

            // scr_lbl_line
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewLine = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewLine = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatLine = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            mInputLayoutLine = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = mInputLayoutLine,
                appCompatAutoCompleteTextView = mAutoComTextViewLine
            )
            mAppComTextViewLine!!.setText(R.string.scr_lbl_line)
            mOtherDetails!!.addView(mLayoutCompatLine)

            mInputLayoutLine!!.endIconMode = END_ICON_NONE
            if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLENDALE,ignoreCase = true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLENDALE_POLICE,ignoreCase = true)){
                mLayoutCompatLine!!.visibility = View.GONE
            }

            // comment
            rootView = View.inflate(this@AllReportActivity, R.layout.content_comment, null)
            mAppComTextViewComments = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewComments = rootView!!.findViewById<TextInputEditText>(R.id.AutoComTextView)
            mLayoutCompatComments = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutComments = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutComments,
                textInputEditText = mAutoComTextViewComments
            )
            mAppComTextViewComments!!.setText(R.string.scr_lbl_comments)
            mOtherDetails!!.addView(mLayoutCompatComments)

            // status
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewStaus = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewStaus = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatStaus = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutStatus = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutStatus,
                appCompatAutoCompleteTextView = mAutoComTextViewStaus
            )
            mAppComTextViewStaus!!.setText(R.string.scr_lbl_status)
            mOtherDetails!!.addView(mLayoutCompatStaus)
        }
        else if(reportType.equals("Bike Inspection",ignoreCase = true))
        {

            mCameraImagesLayout!!.visibility = View.VISIBLE
            imageValidationCount = 4

            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            // scr_lbl_tire_pressure
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewTirePressure = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewTirePressure = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatTirePressure = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutTirePressure = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutTirePressure,
                appCompatAutoCompleteTextView = mAutoComTextViewTirePressure
            )
            mAppComTextViewTirePressure!!.setText(R.string.scr_lbl_tire_pressure)
            mOtherDetails!!.addView(mLayoutCompatTirePressure)

            // assigned_bike
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewAssignedBike = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewAssignedBike = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatAssignedBike = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutAssignedBike = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutAssignedBike,
                appCompatAutoCompleteTextView = mAutoComTextViewAssignedBike
            )
            mAppComTextViewAssignedBike!!.setText(R.string.scr_lbl_assigned_bike)
            mOtherDetails!!.addView(mLayoutCompatAssignedBike)

            // BreaksRotors
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewBreaksRotors = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewBreaksRotors = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatBreaksRotors = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutBreakRotors = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutBreakRotors,
                appCompatAutoCompleteTextView = mAutoComTextViewBreaksRotors
            )
            mAppComTextViewBreaksRotors!!.setText(R.string.scr_lbl_breaks_rotors)
            mOtherDetails!!.addView(mLayoutCompatBreaksRotors)

            // scr_lbl_lights_and_reflectors
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewLightsAndReflectors = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewLightsAndReflectors = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatLightsAndReflectors = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutLightsAndReflectors = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutLightsAndReflectors,
                appCompatAutoCompleteTextView = mAutoComTextViewLightsAndReflectors
            )
            mAppComTextViewLightsAndReflectors!!.setText(R.string.scr_lbl_lights_and_reflectors)
            mOtherDetails!!.addView(mLayoutCompatLightsAndReflectors)

            // scr_lbl_chain_crank
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewChainCrank = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewChainCrank = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatChainCrank = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutChainCrank = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutChainCrank,
                appCompatAutoCompleteTextView = mAutoComTextViewChainCrank
            )
            mAppComTextViewChainCrank!!.setText(R.string.scr_lbl_chain_crank)
            mOtherDetails!!.addView(mLayoutCompatChainCrank)

           // BatteryFreeOfDebris
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewBatteryFreeOfDebris = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewBatteryFreeOfDebris = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatBatteryFreeOfDebris = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutBatteryFreeDebris = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutBatteryFreeDebris,
                appCompatAutoCompleteTextView = mAutoComTextViewBatteryFreeOfDebris
            )
            mAppComTextViewBatteryFreeOfDebris!!.setText(R.string.scr_lbl_battery_free_of_debris)
            mOtherDetails!!.addView(mLayoutCompatBatteryFreeOfDebris)

            // scr_lbl_flat_pack
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewFlatPack = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewFlatPack = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatFlatPack = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutFlatPack = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutFlatPack,
                appCompatAutoCompleteTextView = mAutoComTextViewFlatPack
            )
            mAppComTextViewFlatPack!!.setText(R.string.scr_lbl_flat_pack)
            mOtherDetails!!.addView(mLayoutCompatFlatPack)

            // scr_lbl_first_aid_kit
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewFirstAidKit= rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewFirstAidKit = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatFirstAidKit = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutFirstAidKit = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutFirstAidKit,
                appCompatAutoCompleteTextView = mAutoComTextViewFirstAidKit
            )
            mAppComTextViewFirstAidKit!!.setText(R.string.scr_lbl_first_aid_kit)
            mOtherDetails!!.addView(mLayoutCompatFirstAidKit)

            // scr_lbl_helmel_visual_inspection
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewTiresVisualInspection= rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewTiresVisualInspection = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatTiresVisualInspection = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutTiresVisualInspection = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutTiresVisualInspection,
                appCompatAutoCompleteTextView = mAutoComTextViewTiresVisualInspection
            )
            mAppComTextViewTiresVisualInspection!!.setText(R.string.scr_lbl_helmet_visual_inspection)
            mOtherDetails!!.addView(mLayoutCompatTiresVisualInspection)

            // scr_lbl_Bike_Glassess
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewBikeGlassess= rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewBikeGlassess = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatBikeGlassess = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutGlasses = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutGlasses,
                appCompatAutoCompleteTextView = mAutoComTextViewBikeGlassess
            )
            mAppComTextViewBikeGlassess!!.setText(R.string.scr_lbl_bike_glassess)
            mOtherDetails!!.addView(mLayoutCompatBikeGlassess)

            // scr_lbl_BatterCharge
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewBatteryCharge= rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewBatteryCharge = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatBatteryCharge = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutBatteryCharge = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutBatteryCharge,
                appCompatAutoCompleteTextView = mAutoComTextViewBatteryCharge
            )
            mAppComTextViewBatteryCharge!!.setText(R.string.scr_lbl_battery_charge)
            mOtherDetails!!.addView(mLayoutCompatBatteryCharge)

            // scr_lbl_BatterCharge
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewGloveVisualInspection= rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewGloveVisualInspection = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatGloveVisualInspection = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutGloveVisualInspection = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutGloveVisualInspection,
                appCompatAutoCompleteTextView = mAutoComTextViewGloveVisualInspection
            )
            mAppComTextViewGloveVisualInspection!!.setText(R.string.scr_lbl_glove_visual_inspection)
            mOtherDetails!!.addView(mLayoutCompatGloveVisualInspection)


            tvCameraTitle.setText("Image Section")

        }else if(reportType.equals("EOW Supervisor Shift Report",ignoreCase = true))
        {
            mCameraImagesLayout!!.visibility = View.GONE

            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)

            // Total Enforcement Personnel
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewTotalEnforcementPersonnel = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewTotalEnforcementPersonnel = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatTotalEnforcementPersonnel = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutTotalEnforcementPersonnel = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutTotalEnforcementPersonnel,
                appCompatAutoCompleteTextView = mAutoComTextViewTotalEnforcementPersonnel
            )
            mAppComTextViewTotalEnforcementPersonnel!!.setText(R.string.scr_lbl_total_enforcement_personnel)
            mOtherDetails!!.addView(mLayoutCompatTotalEnforcementPersonnel)

            // scr_lbl_first_10_mins_break
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewFirst10MinBreak = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewFirst10MinBreak = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatFirst10MinBreak = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutFirst10MinBreak = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutFirst10MinBreak,
                appCompatAutoCompleteTextView = mAutoComTextViewFirst10MinBreak
            )
            mAppComTextViewFirst10MinBreak!!.setText(R.string.scr_lbl_first_10_mins_break)
            mOtherDetails!!.addView(mLayoutCompatFirst10MinBreak)

            // scr_lbl_lunch
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewLunchTaken = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewLunchTaken = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatLunchTaken = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutLunchTaken = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutLunchTaken,
                appCompatAutoCompleteTextView = mAutoComTextViewLunchTaken
            )
            mAppComTextViewLunchTaken!!.setText(R.string.scr_lbl_lunch)
            mOtherDetails!!.addView(mLayoutCompatLunchTaken)

            // scr_lbl_second_10_mins_break
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewSecond10MinBreak = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewSecond10MinBreak = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatSecond10MinBreak = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutSecond10MinBreak = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutSecond10MinBreak,
                appCompatAutoCompleteTextView = mAutoComTextViewSecond10MinBreak
            )
            mAppComTextViewSecond10MinBreak!!.setText(R.string.scr_lbl_second_10_mins_break)
            mOtherDetails!!.addView(mLayoutCompatSecond10MinBreak)

            // scr_lbl_complaints_towards_officer
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewComplaintsTowardsOfficer = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewComplaintsTowardsOfficer = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatComplaintsTowardsOfficer = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            mInputLayoutComplaintsTowardsOfficer = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = mInputLayoutComplaintsTowardsOfficer,
                appCompatAutoCompleteTextView = mAutoComTextViewComplaintsTowardsOfficer
            )
            mAppComTextViewComplaintsTowardsOfficer!!.setText(R.string.scr_lbl_complaints_towards_officer)
            mOtherDetails!!.addView(mLayoutCompatComplaintsTowardsOfficer)

            mInputLayoutComplaintsTowardsOfficer!!.endIconMode = END_ICON_NONE

            // scr_lbl_resident_complaints
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewResidentComplaints = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewResidentComplaints = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatResidentComplaints = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            mInputLayoutResidentComplaints = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = mInputLayoutResidentComplaints,
                appCompatAutoCompleteTextView = mAutoComTextViewResidentComplaints
            )
            mAppComTextViewResidentComplaints!!.setText(R.string.scr_lbl_resident_complaints)
            mOtherDetails!!.addView(mLayoutCompatResidentComplaints)

            mInputLayoutResidentComplaints!!.endIconMode = END_ICON_NONE

            // scr_lbl_warnings_issued
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewWarningsIssued = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewWarningsIssued = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatWarningsIssued = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            mInputLayoutWarningsIssued = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = mInputLayoutWarningsIssued,
                appCompatAutoCompleteTextView = mAutoComTextViewWarningsIssued
            )
            mAppComTextViewWarningsIssued!!.setText(R.string.scr_lbl_warnings_issued)
            mOtherDetails!!.addView(mLayoutCompatWarningsIssued)

            mInputLayoutWarningsIssued!!.endIconMode = END_ICON_NONE

            // scr_lbl_warnings_issued
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewCitationsIssued = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewCitationsIssued = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatCitationsIssued = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            mInputLayoutCitationsIssued = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = mInputLayoutCitationsIssued,
                appCompatAutoCompleteTextView = mAutoComTextViewCitationsIssued
            )
            mAppComTextViewCitationsIssued!!.setText(R.string.scr_lbl_citation_issued)
            mOtherDetails!!.addView(mLayoutCompatCitationsIssued)

            mInputLayoutCitationsIssued!!.endIconMode = END_ICON_NONE

            // comment
            rootView = View.inflate(this@AllReportActivity, R.layout.content_comment, null)
            mAppComTextViewComments = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewComments = rootView!!.findViewById<TextInputEditText>(R.id.AutoComTextView)
            mLayoutCompatComments = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutComments = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutComments,
                textInputEditText = mAutoComTextViewComments
            )
            mAppComTextViewComments!!.setText(R.string.scr_lbl_comments)
            mOtherDetails!!.addView(mLayoutCompatComments)

        }else if(reportType.equals("Special Assignment Report",ignoreCase = true)) {
            mCameraImagesLayout!!.visibility = View.GONE

            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)

            // Block
            mAppComTextViewBlock = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewBlock =
                rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatBlock = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutBlock = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutBlock,
                appCompatAutoCompleteTextView = mAutoComTextViewBlock
            )
            mAppComTextViewBlock!!.setText(R.string.scr_lbl_block)
            mLocationDetails!!.addView(mLayoutCompatBlock)

            // Street
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewStreet = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewStreet =
                rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatStreet = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutStreet = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutStreet,
                appCompatAutoCompleteTextView = mAutoComTextViewStreet
            )
            mAppComTextViewStreet!!.setText(R.string.scr_lbl_street)
            mLocationDetails!!.addView(mLayoutCompatStreet)

//            // Street
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewDirection = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewDirection =
                rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatDirection = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutDirection = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutDirection,
                appCompatAutoCompleteTextView = mAutoComTextViewDirection
            )
            mAppComTextViewDirection!!.setText(R.string.scr_lbl_direction)
            mLocationDetails!!.addView(mLayoutCompatDirection)

// scr_lbl_warnings_issued
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewWarningsIssued =
                rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewWarningsIssued =
                rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatWarningsIssued =
                rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            mInputLayoutWarningsIssued = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = mInputLayoutWarningsIssued,
                appCompatAutoCompleteTextView = mAutoComTextViewWarningsIssued
            )
            mAppComTextViewWarningsIssued!!.setText(R.string.scr_lbl_warnings_issued)
            mOtherDetails!!.addView(mLayoutCompatWarningsIssued)

            mInputLayoutWarningsIssued!!.endIconMode = END_ICON_NONE

// scr_lbl_warnings_issued
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewCitationsIssued =
                rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewCitationsIssued =
                rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatCitationsIssued =
                rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            mInputLayoutCitationsIssued = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = mInputLayoutCitationsIssued,
                appCompatAutoCompleteTextView = mAutoComTextViewCitationsIssued
            )
            mAppComTextViewCitationsIssued!!.setText(R.string.scr_lbl_citation_issued)
            mOtherDetails!!.addView(mLayoutCompatCitationsIssued)

            mInputLayoutCitationsIssued!!.endIconMode = END_ICON_NONE

// scr_lbl_vehicles_marked
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewVehicleMark =
                rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewVehicleMark =
                rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatVehicleMark =
                rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            mInputLayoutVehicleMark = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = mInputLayoutVehicleMark,
                appCompatAutoCompleteTextView = mAutoComTextViewVehicleMark
            )
            mAppComTextViewVehicleMark!!.setText(R.string.scr_lbl_vehicles_marked)
            mOtherDetails!!.addView(mLayoutCompatVehicleMark)

            mInputLayoutVehicleMark!!.endIconMode = END_ICON_NONE

// scr_lbl_time_marked
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewTimeMark = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewTimeMark =
                rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatTimeMark = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            mInputLayoutTimeMark = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = mInputLayoutTimeMark,
                appCompatAutoCompleteTextView = mAutoComTextViewTimeMark
            )
            mAppComTextViewTimeMark!!.setText(R.string.scr_lbl_time_marked)
            mOtherDetails!!.addView(mLayoutCompatTimeMark)
//            mAutoComTextViewTimeMark!!.setHint("dd/mm/yyyy HH:mm")
            mAutoComTextViewTimeMark?.setOnClickListener {
                AppUtils.hideSoftKeyboard(this@AllReportActivity)
                openTimePicker(mAutoComTextViewTimeMark)
            }

//            mInputLayoutTimeMark = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
//            mInputLayoutTimeMark!!.endIconMode = END_ICON_NONE
//            mAutoComTextViewTimeMark!!.setHint("10PM to 6PM")

// scr_lbl_time_marked2
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewTimeMark2 = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewTimeMark2 =
                rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatTimeMark2 = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            mInputLayoutTimeMark2 = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = mInputLayoutTimeMark2,
                appCompatAutoCompleteTextView = mAutoComTextViewTimeMark2
            )
            mAppComTextViewTimeMark2!!.setText(R.string.scr_lbl_time_marked_2)
            mOtherDetails!!.addView(mLayoutCompatTimeMark2)

//            mInputLayoutTimeMark2 = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
//            mInputLayoutTimeMark2!!.endIconMode = END_ICON_NONE
//            mAutoComTextViewTimeMark!!.setHint("10PM to 6PM")

// scr_lbl_time_marked3
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewTimeMark3 = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewTimeMark3 =
                rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatTimeMark3 = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            mInputLayoutTimeMark3 = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = mInputLayoutTimeMark3,
                appCompatAutoCompleteTextView = mAutoComTextViewTimeMark3
            )
            mAppComTextViewTimeMark3!!.setText(R.string.scr_lbl_time_marked_3)
            mOtherDetails!!.addView(mLayoutCompatTimeMark3)

//            mInputLayoutTimeMark3 = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
//            mInputLayoutTimeMark3!!.endIconMode = END_ICON_NONE
//            mAutoComTextViewTimeMark!!.setHint("10PM to 6PM")

// scr_lbl_time_marked4
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewTimeMark4 = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewTimeMark4 =
                rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatTimeMark4 = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            mInputLayoutTimeMark4 = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = mInputLayoutTimeMark4,
                appCompatAutoCompleteTextView = mAutoComTextViewTimeMark4
            )
            mAppComTextViewTimeMark4!!.setText(R.string.scr_lbl_time_marked_4)
            mOtherDetails!!.addView(mLayoutCompatTimeMark4)

//            mInputLayoutTimeMark4 = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
//            mInputLayoutTimeMark4!!.endIconMode = END_ICON_NONE
//            mAutoComTextViewTimeMark!!.setHint("10PM to 6PM")

//            scr_lbl_assigned_area
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewAssignedArea =
                rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewAssignedArea =
                rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatAssignedArea =
                rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutAssignedArea = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutAssignedArea,
                appCompatAutoCompleteTextView = mAutoComTextViewAssignedArea
            )
            mAppComTextViewAssignedArea!!.setText(R.string.scr_lbl_assigned_area)
            mOtherDetails!!.addView(mLayoutCompatAssignedArea)

//            scr_lbl_violation_descriptions
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewViolationDescription =
                rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewViolationDescription =
                rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatViolationDescription =
                rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutViolationDescription = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutViolationDescription,
                appCompatAutoCompleteTextView = mAutoComTextViewViolationDescription
            )
            mAppComTextViewViolationDescription!!.setText(R.string.scr_lbl_violation_descriptions)
            mOtherDetails!!.addView(mLayoutCompatViolationDescription)

// comment
            rootView = View.inflate(this@AllReportActivity, R.layout.content_comment, null)
            mAppComTextViewComments = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewComments =
                rootView!!.findViewById<TextInputEditText>(R.id.AutoComTextView)
            mLayoutCompatComments = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutComments = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutComments,
                textInputEditText = mAutoComTextViewComments
            )
            mAppComTextViewComments!!.setText(R.string.scr_lbl_comments)
            mOtherDetails!!.addView(mLayoutCompatComments)

        }else if(reportType.equals("72hrs Notice To Tow Report",ignoreCase = true)) {
//            ioScope.launch {
                rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
                // scr_lbl_zone
                mAppComTextViewPBCZone = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
                mAutoComTextViewPBCZone =
                    rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
                mLayoutCompatPBCZone = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutPBCZone = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutPBCZone,
                appCompatAutoCompleteTextView = mAutoComTextViewPBCZone
            )


                mCameraImagesLayout!!.visibility = View.GONE
                mCameraImagesLayout!!.visibility = View.VISIBLE
                imageValidationCount = 2
                mAppComTextViewPBCZone!!.post{mAppComTextViewPBCZone!!.setText(R.string.scr_lbl_zone)}
                mLocationDetails!!.addView(mLayoutCompatPBCZone)

            //Meter Number
                rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
                // scr_lbl_zone
                mAppComTextViewMeterNo = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
                mAutoComTextViewMeterNo =
                    rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
                mLayoutCompatMeterNo = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutMeterNo = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutMeterNo,
                appCompatAutoCompleteTextView = mAutoComTextViewMeterNo
            )

                mAppComTextViewMeterNo!!.post{mAppComTextViewMeterNo!!.setText(R.string.scr_lbl_station)}
                mLocationDetails!!.addView(mLayoutCompatMeterNo)

                // scr_lbl_vin_number
                rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
                mAppComTextViewLicensePlate =
                    rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
                mAutoComTextViewLicensePlate =
                    rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
                mLayoutCompatLicensePlate =
                    rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
                mAppComTextViewLicensePlate!!.post{mAppComTextViewLicensePlate!!.setText(R.string.scr_lbl_license_plate)}
            mInputLayoutLicensePlate = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = mInputLayoutLicensePlate,
                appCompatAutoCompleteTextView = mAutoComTextViewLicensePlate
            )

                mOtherDetails!!.addView(mLayoutCompatLicensePlate)

                mInputLayoutLicensePlate = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
                mInputLayoutLicensePlate!!.endIconMode = END_ICON_NONE
                mAutoComTextViewLicensePlate?.setAllCaps(true)
                mAutoComTextViewLicensePlate?.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS)

                // scr_lbl_vin_number
                rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
                mAppComTextViewVin =
                    rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
                mAutoComTextViewVin =
                    rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
                mLayoutCompatVin =
                    rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutVin = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutVin,
                appCompatAutoCompleteTextView = mAutoComTextViewVin
            )
                mAppComTextViewVin!!.post{mAppComTextViewVin!!.setText(R.string.scr_lbl_vin_number)}
                mOtherDetails!!.addView(mLayoutCompatVin)

                mInputLayoutWarningsIssued =
                    rootView!!.findViewById<TextInputLayout>(R.id.input_text)
                mInputLayoutWarningsIssued!!.endIconMode = END_ICON_NONE
                val maxL = 17;
                mAutoComTextViewVin?.filters =
                     arrayOf<InputFilter>(InputFilter.LengthFilter(maxL))

                // scr_lbl_State
                rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
                mAppComTextViewState =
                    rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
                mAutoComTextViewState =
                    rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
                mLayoutCompatState =
                    rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutState = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutState,
                appCompatAutoCompleteTextView = mAutoComTextViewState
            )
                mAppComTextViewState!!.post{mAppComTextViewState!!.setText(R.string.scr_lbl_state)}
                mOtherDetails!!.addView(mLayoutCompatState)

//            mInputLayoutVehicleMark = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
//            mInputLayoutVehicleMark!!.endIconMode = END_ICON_NONE

                // scr_lbl_make
                rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
                mAppComTextViewMake = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
                mAutoComTextViewMake =
                    rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
                mLayoutCompatMake = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutMake = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutMake,
                appCompatAutoCompleteTextView = mAutoComTextViewMake
            )
                mAppComTextViewMake!!.post{mAppComTextViewMake!!.setText(R.string.scr_lbl_make)}
                mOtherDetails!!.addView(mLayoutCompatMake)

                // scr_lbl_mode
                rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
                mAppComTextViewModel = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
                mAutoComTextViewModel =
                    rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
                mLayoutCompatModel = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutModel = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutModel,
                appCompatAutoCompleteTextView = mAutoComTextViewModel
            )
                mAppComTextViewModel!!.post{mAppComTextViewModel!!.setText(R.string.scr_lbl_model)}
                mOtherDetails!!.addView(mLayoutCompatModel)

                // scr_lbl_color
                rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
                mAppComTextViewColor = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
                mAutoComTextViewColor =
                    rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
                mLayoutCompatColor = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutColor = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutColor,
                appCompatAutoCompleteTextView = mAutoComTextViewColor
            )
                mAppComTextViewColor!!.post{mAppComTextViewColor!!.setText(R.string.scr_lbl_color)}
                mOtherDetails!!.addView(mLayoutCompatColor)

                // scr_lbl_time_marked
                rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
                mAppComTextViewTimeMark =
                    rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
                mAutoComTextViewTimeMark =
                    rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
                mLayoutCompatTimeMark =
                    rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            mInputLayoutTimeMark = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = mInputLayoutTimeMark,
                appCompatAutoCompleteTextView = mAutoComTextViewTimeMark
            )
                mAppComTextViewTimeMark!!.setText(R.string.scr_lbl_time_marked)
                mOtherDetails!!.addView(mLayoutCompatTimeMark)
            mAutoComTextViewTimeMark!!.setHint("dd/mm/yyyy HH:mm")

            // scr_lbl_citation_issued
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewCitationsIssued =
                rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewCitationsIssued =
                rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatCitationsIssued =
                rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            mInputLayoutCitationsIssued = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = mInputLayoutCitationsIssued,
                appCompatAutoCompleteTextView = mAutoComTextViewCitationsIssued
            )
            mAppComTextViewCitationsIssued!!.post{mAppComTextViewCitationsIssued!!.setText(R.string.scr_lbl_citation_issued)}
            mOtherDetails!!.addView(mLayoutCompatCitationsIssued)
            mAutoComTextViewCitationsIssued!!.setHint("dd/mm/yyyy HH:mm")

//            mInputLayoutCitationsIssued =
//                rootView!!.findViewById<TextInputLayout>(R.id.input_text)
//            mInputLayoutCitationsIssued!!.endIconMode = END_ICON_NONE


//            scr_lbl_schedule_tow_date_time
                rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
                mAppComTextViewDate =
                    rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
                mAutoComTextViewDate =
                    rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
                mLayoutCompatDate =
                    rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutDate = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutDate,
                appCompatAutoCompleteTextView = mAutoComTextViewDate
            )
                mAppComTextViewDate!!.setText(R.string.scr_lbl_schedule_tow_date_time)
                mOtherDetails!!.addView(mLayoutCompatDate)
            mAutoComTextViewDate!!.setHint("dd/mm/yyyy HH:mm")

            // scr_lbl_line
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewLine = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewLine = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatLine = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            mInputLayoutLine = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = mInputLayoutLine,
                appCompatAutoCompleteTextView = mAutoComTextViewLine
            )
            mAppComTextViewLine!!.setText(R.string.scr_lbl_line)
            mOtherDetails!!.addView(mLayoutCompatLine)

            mInputLayoutLine!!.endIconMode = END_ICON_NONE
//            mAutoComTextViewLine!!.setHint("Enter")

        }else if(reportType.equals("Sign Off Report",ignoreCase = true)) {
            mCameraImagesLayout!!.visibility = View.VISIBLE
            imageValidationCount = 2
            mCameraImagesLayout!!.visibility = View.GONE
            tvCameraTitle.setText("Signature Section")

            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)

            // scr_lbl_vehicle_stored_at
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewVehicleStoredAt = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewVehicleStoredAt =
                rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatVehicleStoredAt = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            mInputLayoutVehicleStoredAt = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = mInputLayoutVehicleStoredAt,
                appCompatAutoCompleteTextView = mAutoComTextViewVehicleStoredAt
            )
            mAppComTextViewVehicleStoredAt!!.setText(R.string.scr_lbl_vehicle_stored_at)
            mLocationDetails!!.addView(mLayoutCompatVehicleStoredAt)

//            mInputLayoutVehicleStoredAt = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
//            mInputLayoutVehicleStoredAt!!.endIconMode = END_ICON_NONE

            // scr_lbl_tow_file_number
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewTowFileNumber =
                rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewTowFileNumber =
                rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatTowFileNumber =
                rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            mInputLayoutTowFileNumber = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = mInputLayoutTowFileNumber,
                appCompatAutoCompleteTextView = mAutoComTextViewTowFileNumber
            )
            mAppComTextViewTowFileNumber!!.setText(R.string.scr_lbl_tow_file_number)
            mOtherDetails!!.addView(mLayoutCompatTowFileNumber)

            mInputLayoutTowFileNumber!!.endIconMode = END_ICON_NONE

//            scr_lbl_assigned_area
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewVehicleRelocatedToDifferentStallWithinTheLot =
                rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewVehicleRelocatedToDifferentStallWithinTheLot =
                rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatVehicleRelocatedToDifferentStallWithinTheLot =
                rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutVehicleRelocatedToDifferentStallWithinTheLot = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutVehicleRelocatedToDifferentStallWithinTheLot,
                appCompatAutoCompleteTextView = mAutoComTextViewVehicleRelocatedToDifferentStallWithinTheLot
            )
            mAppComTextViewVehicleRelocatedToDifferentStallWithinTheLot!!.setText(R.string.scr_lbl_vehicle_relocated_to_different_stall_within_the_lot)
            mOtherDetails!!.addView(mLayoutCompatVehicleRelocatedToDifferentStallWithinTheLot)

            // scr_lbl_line
//            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
//            mAppComTextViewLine = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
//            mAutoComTextViewLine = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
//            mLayoutCompatLine = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
//            mAppComTextViewLine!!.setText(R.string.scr_lbl_line)
//            mOtherDetails!!.addView(mLayoutCompatLine)
//
//            mInputLayoutLine = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
//            mInputLayoutLine!!.endIconMode = END_ICON_NONE

        }else if(reportType.equals("Tow Report",ignoreCase = true)) {
            mCameraImagesLayout!!.visibility = View.VISIBLE
            imageValidationCount = 8
            mCameraImagesLayout!!.visibility = View.GONE

            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)

//            scr_lbl_zone
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewPBCZone =
                rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewPBCZone =
                rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatPBCZone =
                rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutPBCZone = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutPBCZone,
                appCompatAutoCompleteTextView = mAutoComTextViewPBCZone
            )
            mAppComTextViewPBCZone!!.post { mAppComTextViewPBCZone!!.setText(R.string.scr_lbl_zone) }
            mLocationDetails!!.addView(mLayoutCompatPBCZone)

            //Meter Number
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            // scr_lbl_zone
            mAppComTextViewMeterNo = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewMeterNo =
                rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatMeterNo = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutMeterNo = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutMeterNo,
                appCompatAutoCompleteTextView = mAutoComTextViewMeterNo
            )

            mAppComTextViewMeterNo!!.post{mAppComTextViewMeterNo!!.setText(R.string.scr_lbl_station)}
            mLocationDetails!!.addView(mLayoutCompatMeterNo)

            // scr_lbl_license_plate
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewLicensePlate = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewLicensePlate =
                rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatLicensePlate = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            mInputLayoutLicensePlate = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = mInputLayoutLicensePlate,
                appCompatAutoCompleteTextView = mAutoComTextViewLicensePlate
            )
            mAppComTextViewLicensePlate!!.post{mAppComTextViewLicensePlate!!.setText(R.string.scr_lbl_license_plate)}
            mOtherDetails!!.addView(mLayoutCompatLicensePlate)

            mInputLayoutVehicleStoredAt = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            mInputLayoutVehicleStoredAt!!.endIconMode = END_ICON_NONE
            mAutoComTextViewLicensePlate?.setAllCaps(true)
            mAutoComTextViewLicensePlate?.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS)


            // scr_lbl_state
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewState =
                rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewState =
                rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatState =
                rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutState = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutState,
                appCompatAutoCompleteTextView = mAutoComTextViewState
            )
            mAppComTextViewState!!.post{mAppComTextViewState!!.setText(R.string.scr_lbl_state)}
            mOtherDetails!!.addView(mLayoutCompatState)

//            mInputLayoutTowFileNumber = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
//            mInputLayoutTowFileNumber!!.endIconMode = END_ICON_NONE

//            scr_lbl_vin
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewVin =
                rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewVin =
                rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatVin =
                rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutVin = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutVin,
                appCompatAutoCompleteTextView = mAutoComTextViewVin
            )
            mAppComTextViewVin!!.post{mAppComTextViewVin!!.setText(R.string.scr_lbl_vin)}
            mOtherDetails!!.addView(mLayoutCompatVin)
            val maxL = 17;
            mAutoComTextViewVin?.filters =
                arrayOf<InputFilter>(InputFilter.LengthFilter(maxL))

//            scr_lbl_make
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewMake =
                rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewMake =
                rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatMake =
                rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutMake = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutMake,
                appCompatAutoCompleteTextView = mAutoComTextViewMake
            )
            mAppComTextViewMake!!.setText(R.string.scr_lbl_make)
            mOtherDetails!!.addView(mLayoutCompatMake)

//            scr_lbl_model
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewModel =
                rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewModel =
                rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatModel =
                rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutModel = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutModel,
                appCompatAutoCompleteTextView = mAutoComTextViewModel
            )
            mAppComTextViewModel!!.post{mAppComTextViewModel!!.setText(R.string.scr_lbl_model)}
            mOtherDetails!!.addView(mLayoutCompatModel)

//            scr_lbl_color
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewColor =
                rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewColor =
                rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatColor =
                rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutColor = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutColor,
                appCompatAutoCompleteTextView = mAutoComTextViewColor
            )
            mAppComTextViewColor!!.post{mAppComTextViewColor!!.setText(R.string.scr_lbl_color)}
            mOtherDetails!!.addView(mLayoutCompatColor)

//            scr_lbl_reason_for_tow
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewReasonForTow =
                rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewReasonForTow =
                rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatReasonForTow =
                rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutReasonForTow = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutReasonForTow,
                appCompatAutoCompleteTextView = mAutoComTextViewReasonForTow
            )
            mAppComTextViewReasonForTow!!.post{mAppComTextViewReasonForTow!!.setText(R.string.scr_lbl_reason_for_tow)}
            mOtherDetails!!.addView(mLayoutCompatReasonForTow)

//            scr_lbl_notice_to_tow_posted_on
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewDate =
                rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewDate =
                rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatDate =
                rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutDate = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutDate,
                appCompatAutoCompleteTextView = mAutoComTextViewDate
            )
            mAppComTextViewDate!!.post{mAppComTextViewDate!!.setText(R.string.scr_lbl_notice_to_tow_posted_on)}
            mOtherDetails!!.addView(mLayoutCompatDate)

            val mInputLayoutDate = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            mInputLayoutDate!!.endIconMode = END_ICON_NONE
            mAutoComTextViewDate!!.setHint("dd/mm/yyyy")


//            scr_lbl_trailer_attached_to_vehicle
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewOverHeating =
                rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewOverHeating =
                rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatOverHeating =
                rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutOverHeating = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutOverHeating,
                appCompatAutoCompleteTextView = mAutoComTextViewOverHeating
            )
            mAppComTextViewOverHeating!!.post{mAppComTextViewOverHeating!!.setText(R.string.scr_lbl_trailer_attached_to_vehicle)}
            mOtherDetails!!.addView(mLayoutCompatOverHeating)

//            scr_lbl_officer_phone_number
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewOfficerPhoneNumber =
                rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewOfficerPhoneNumber =
                rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatOfficerPhoneNumber =
                rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            mInputLayoutOfficerPhoneNumber = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = mInputLayoutOfficerPhoneNumber,
                appCompatAutoCompleteTextView = mAutoComTextViewOfficerPhoneNumber
            )
            mAppComTextViewOfficerPhoneNumber!!.post{mAppComTextViewOfficerPhoneNumber!!.setText(R.string.scr_lbl_officer_phone_number)}
            mOtherDetails!!.addView(mLayoutCompatOfficerPhoneNumber)

            val filterArray = arrayOfNulls<InputFilter>(1)
            filterArray[0] = LengthFilter(10)
            mAutoComTextViewOfficerPhoneNumber!!.setFilters(filterArray)

            mInputLayoutOfficerPhoneNumber!!.endIconMode = END_ICON_NONE

//            scr_lbl_vehicle_condition
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewVehicleCondition =
                rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewVehicleCondition =
                rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatVehicleCondition =
                rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            mInputLayoutVehicleCondition = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = mInputLayoutVehicleCondition,
                appCompatAutoCompleteTextView = mAutoComTextViewVehicleCondition
            )
            mAppComTextViewVehicleCondition!!.post{mAppComTextViewVehicleCondition!!.setText(R.string.scr_lbl_vehicle_condition)}
            mOtherDetails!!.addView(mLayoutCompatVehicleCondition)

            mInputLayoutVehicleCondition!!.endIconMode = END_ICON_NONE

//            scr_lbl_request_to
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewSpecialEnforcementRequest =
                rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewSpecialEnforcementRequest =
                rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatSpecialEnforcementRequest =
                rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            mInputLayoutSpecialEnforcementRequest = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = mInputLayoutSpecialEnforcementRequest,
                appCompatAutoCompleteTextView = mAutoComTextViewSpecialEnforcementRequest
            )
            mAppComTextViewSpecialEnforcementRequest!!.post{mAppComTextViewSpecialEnforcementRequest!!.setText(R.string.scr_lbl_request_to)}
            mOtherDetails!!.addView(mLayoutCompatSpecialEnforcementRequest)

//            scr_lbl_garage_clearance
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewGarageClearance =
                rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewGarageClearance =
                rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatGarageClearance =
                rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            mInputLayoutGarageClearance = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = mInputLayoutGarageClearance,
                appCompatAutoCompleteTextView = mAutoComTextViewGarageClearance
            )
            mAppComTextViewGarageClearance!!.post{mAppComTextViewGarageClearance!!.setText(R.string.scr_lbl_garage_clearance)}
            mOtherDetails!!.addView(mLayoutCompatGarageClearance)

            mInputLayoutGarageClearance!!.endIconMode = END_ICON_NONE

//            scr_lbl_file_number
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewFileNumber =
                rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewFileNumber =
                rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatFileNumber =
                rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            mInputLayoutFileNumber = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = mInputLayoutFileNumber,
                appCompatAutoCompleteTextView = mAutoComTextViewFileNumber
            )
            mAppComTextViewFileNumber!!.post{mAppComTextViewFileNumber!!.setText(R.string.scr_lbl_file_number)}
            mOtherDetails!!.addView(mLayoutCompatFileNumber)

            mInputLayoutFileNumber!!.endIconMode = END_ICON_NONE

            // scr_lbl_ro_name_address
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewRo =
                rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewRo =
                rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatRo =
                rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            mInputLayoutRo = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = mInputLayoutRo,
                appCompatAutoCompleteTextView = mAutoComTextViewRo
            )
            mAppComTextViewRo!!.post{mAppComTextViewRo!!.setText(R.string.scr_lbl_ro_name_address)}
            mOtherDetails!!.addView(mLayoutCompatRo)

            mInputLayoutRo!!.endIconMode = END_ICON_NONE

            // scr_lbl_vehicle_within_the_lot
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewVehicleWithInLot =
                rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewVehicleWithInLot =
                rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatVehicleWithInLot =
                rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            mInputLayoutVehicleWithInLot = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = mInputLayoutVehicleWithInLot,
                appCompatAutoCompleteTextView = mAutoComTextViewVehicleWithInLot
            )
            mAppComTextViewVehicleWithInLot!!.post{mAppComTextViewVehicleWithInLot!!.setText(R.string.scr_lbl_vehicle_within_the_lot)}
            mOtherDetails!!.addView(mLayoutCompatVehicleWithInLot)

            mInputLayoutVehicleWithInLot!!.endIconMode = END_ICON_NONE


            // scr_lbl_comment_driver_side
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewDriverSideComment =
                rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewDriverSideComment =
                rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatDriverSideComment =
                rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            mInputLayoutDriverSideComment = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = mInputLayoutDriverSideComment,
                appCompatAutoCompleteTextView = mAutoComTextViewDriverSideComment
            )
            mAppComTextViewDriverSideComment!!.post{mAppComTextViewDriverSideComment!!.setText(R.string.scr_lbl_comment_driver_side)}
            mOtherDetails!!.addView(mLayoutCompatDriverSideComment)

            mInputLayoutDriverSideComment!!.endIconMode = END_ICON_NONE

            // scr_lbl_comment_passenger_side
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewPassengerSideComment =
                rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewPassengerSideComment =
                rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatPassengerSideComment =
                rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            mInputLayoutPassengerSideComment = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = mInputLayoutPassengerSideComment,
                appCompatAutoCompleteTextView = mAutoComTextViewPassengerSideComment
            )
            mAppComTextViewPassengerSideComment!!.post{mAppComTextViewPassengerSideComment!!.setText(R.string.scr_lbl_comment_passenger_side)}
            mOtherDetails!!.addView(mLayoutCompatPassengerSideComment)

            mInputLayoutPassengerSideComment!!.endIconMode = END_ICON_NONE

            // scr_lbl_comment_front_side
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewFrontSideComment =
                rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewFrontSideComment =
                rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatFrontSideComment =
                rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            mInputLayoutFrontSideComment = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = mInputLayoutFrontSideComment,
                appCompatAutoCompleteTextView = mAutoComTextViewFrontSideComment
            )
            mAppComTextViewFrontSideComment!!.post{mAppComTextViewFrontSideComment!!.setText(R.string.scr_lbl_comment_front_side)}
            mOtherDetails!!.addView(mLayoutCompatFrontSideComment)

            mInputLayoutFrontSideComment!!.endIconMode = END_ICON_NONE

            // scr_lbl_comment_rear_side
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewRearSideComment =
                rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewRearSideComment =
                rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatRearSideComment =
                rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            mInputLayoutRearSideComment = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = mInputLayoutRearSideComment,
                appCompatAutoCompleteTextView = mAutoComTextViewRearSideComment
            )
            mAppComTextViewRearSideComment!!.post{mAppComTextViewRearSideComment!!.setText(R.string.scr_lbl_comment_rear_side)}
            mOtherDetails!!.addView(mLayoutCompatRearSideComment)

            mInputLayoutRearSideComment!!.endIconMode = END_ICON_NONE

            // scr_lbl_comment_rear_side
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewTrailerComment =
                rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewTrailerComment =
                rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatTrailerComment =
                rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            mInputLayoutTrailerComment = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = mInputLayoutTrailerComment,
                appCompatAutoCompleteTextView = mAutoComTextViewTrailerComment
            )
            mAppComTextViewTrailerComment!!.post{mAppComTextViewTrailerComment!!.setText(R.string.scr_lbl_comment_trailer)}
            mOtherDetails!!.addView(mLayoutCompatTrailerComment)

            mInputLayoutTrailerComment!!.endIconMode = END_ICON_NONE


//            scr_lbl_visible_interior_item
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewInteriorCleanliness =
                rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewInteriorCleanliness =
                rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatInteriorCleanliness =
                rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            mInputLayoutInteriorCleanliness = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = mInputLayoutInteriorCleanliness,
                appCompatAutoCompleteTextView = mAutoComTextViewInteriorCleanliness
            )
            mAppComTextViewInteriorCleanliness!!.post{mAppComTextViewInteriorCleanliness!!.setText(R.string.scr_lbl_visible_interior_item)}
            mOtherDetails!!.addView(mLayoutCompatInteriorCleanliness)

            mInputLayoutInteriorCleanliness!!.endIconMode = END_ICON_NONE

            // scr_lbl_line
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewLine = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewLine = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatLine = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            mInputLayoutLine = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = mInputLayoutLine,
                appCompatAutoCompleteTextView = mAutoComTextViewLine
            )
            mAppComTextViewLine!!.setText(R.string.scr_lbl_line)
            mOtherDetails!!.addView(mLayoutCompatLine)

            mInputLayoutLine!!.endIconMode = END_ICON_NONE

            // scr_lbl_towing_officer
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewTowingOfficer = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewTowingOfficer = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatTowingOfficer = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutTowingOfficer = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutTowingOfficer,
                appCompatAutoCompleteTextView = mAutoComTextViewTowingOfficer
            )
            mAppComTextViewTowingOfficer!!.setText(R.string.scr_lbl_towing_officer)
            mOtherDetails!!.addView(mLayoutCompatTowingOfficer)

            // comment
            rootView = View.inflate(this@AllReportActivity, R.layout.content_comment, null)
            mAppComTextViewComments = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewComments = rootView!!.findViewById<TextInputEditText>(R.id.AutoComTextView)
            mLayoutCompatComments = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutComments = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutComments,
                textInputEditText = mAutoComTextViewComments
            )
            mAppComTextViewComments!!.post{mAppComTextViewComments!!.setText(R.string.scr_lbl_comments)}
            mOtherDetails!!.addView(mLayoutCompatComments)

        }else if(reportType.equals("NFL Special Assignment Report",ignoreCase = true)) {
            mCameraImagesLayout!!.visibility = View.VISIBLE
            imageValidationCount = 3
            mCameraImagesLayout!!.visibility = View.GONE

            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)

//            scr_lbl_Zone
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewPBCZone =
                rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewPBCZone =
                rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatPBCZone =
                rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutPBCZone = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutPBCZone,
                appCompatAutoCompleteTextView = mAutoComTextViewPBCZone
            )
            mAppComTextViewPBCZone!!.post{mAppComTextViewPBCZone!!.setText(R.string.scr_lbl_zone)}
            mLocationDetails!!.addView(mLayoutCompatPBCZone)

            // meter_no
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewMeterNo = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewMeterNo = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatMeterNo = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutMeterNo = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutMeterNo,
                appCompatAutoCompleteTextView = mAutoComTextViewMeterNo
            )
            mAppComTextViewMeterNo!!.setText(R.string.scr_lbl_station)
            mLocationDetails!!.addView(mLayoutCompatMeterNo)


//            scr_lbl_car_count
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewCarCount =
                rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewCarCount =
                rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatCarCount =
                rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            mInputLayoutCarCount = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = mInputLayoutCarCount,
                appCompatAutoCompleteTextView = mAutoComTextViewCarCount
            )
            mAppComTextViewCarCount!!.post{mAppComTextViewCarCount!!.setText(R.string.scr_lbl_car_counted)}
            mOtherDetails!!.addView(mLayoutCompatCarCount)

            mInputLayoutCarCount!!.endIconMode = END_ICON_NONE
            mAutoComTextViewCarCount!!.setInputType(
                InputType.TYPE_CLASS_NUMBER or
                        InputType.TYPE_NUMBER_FLAG_DECIMAL or
                        InputType.TYPE_NUMBER_FLAG_SIGNED)

//            scr_lbl_empty_space
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewEmptySpace =
                rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewEmptySpace =
                rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatEmptySpace =
                rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            mInputLayoutEmptySpace = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = mInputLayoutEmptySpace,
                appCompatAutoCompleteTextView = mAutoComTextViewEmptySpace
            )

            mAppComTextViewEmptySpace!!.post{mAppComTextViewEmptySpace!!.setText(R.string.scr_lbl_empty_space)}
            mOtherDetails!!.addView(mLayoutCompatEmptySpace)

            mInputLayoutEmptySpace!!.endIconMode = END_ICON_NONE
            mAutoComTextViewEmptySpace!!.setInputType(
                InputType.TYPE_CLASS_NUMBER or
                        InputType.TYPE_NUMBER_FLAG_DECIMAL or
                        InputType.TYPE_NUMBER_FLAG_SIGNED)

//            scr_lbl_number_of_violating_vehicles
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewNumberOfViolatingVehicles =
                rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewNumberOfViolatingVehicles =
                rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatNumberOfViolatingVehicles =
                rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            mInputLayoutNumberOfViolatingVehicles = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = mInputLayoutNumberOfViolatingVehicles,
                appCompatAutoCompleteTextView = mAutoComTextViewNumberOfViolatingVehicles
            )

            mAppComTextViewNumberOfViolatingVehicles!!.post{mAppComTextViewNumberOfViolatingVehicles!!.setText(R.string.scr_lbl_number_of_violating_vehicles)}
            mOtherDetails!!.addView(mLayoutCompatNumberOfViolatingVehicles)

            mInputLayoutNumberOfViolatingVehicles!!.endIconMode = END_ICON_NONE
            mAutoComTextViewNumberOfViolatingVehicles!!.setInputType(
                InputType.TYPE_CLASS_NUMBER or
                        InputType.TYPE_NUMBER_FLAG_DECIMAL or
                        InputType.TYPE_NUMBER_FLAG_SIGNED)

               mAutoComTextViewNumberOfViolatingVehicles?.onFocusChangeListener =
                   View.OnFocusChangeListener { v, hasFocus ->
                       if (!hasFocus && mAutoComTextViewNumberOfViolatingVehicles!!.text!!.isNotEmpty()) {
                           val carCount :Double= (mAutoComTextViewCarCount!!.text!!.toString()).toDouble()
                           val violationCount :Double= (mAutoComTextViewNumberOfViolatingVehicles!!.text!!.toString()).toDouble()
                           val result : Double = (violationCount/(carCount*100)).toDouble()
                           try {
                               mAutoComTextViewViolationRate!!.setText(" "+ String.format("%.2f", result))
//                               mAutoComTextViewViolationRate!!.isFocusable = false
//                               mAppComTextViewComments!!.isFocusable = true
                           } catch (e: Exception) {
                               e.printStackTrace()
                           }
                       }
                   }

//            scr_lbl_violation_rate
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewViolationRate =
                rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewViolationRate =
                rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatViolationRate =
                rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            mInputLayoutViolationRate = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = mInputLayoutViolationRate,
                appCompatAutoCompleteTextView = mAutoComTextViewViolationRate
            )

            mAppComTextViewViolationRate!!.post{mAppComTextViewViolationRate!!.setText(R.string.scr_lbl_number_of_violating_rate)}
            mOtherDetails!!.addView(mLayoutCompatViolationRate)

            mInputLayoutViolationRate!!.endIconMode = END_ICON_NONE

            // scr_lbl_line
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewLine = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewLine = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatLine = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            mInputLayoutLine = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = mInputLayoutLine,
                appCompatAutoCompleteTextView = mAutoComTextViewLine
            )
            mAppComTextViewLine!!.setText(R.string.scr_lbl_line)
            mOtherDetails!!.addView(mLayoutCompatLine)

            mInputLayoutLine = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            mInputLayoutLine!!.endIconMode = END_ICON_NONE

            // comment
            rootView = View.inflate(this@AllReportActivity, R.layout.content_comment, null)
            mAppComTextViewComments = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewComments = rootView!!.findViewById<TextInputEditText>(R.id.AutoComTextView)
            mLayoutCompatComments = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutComments = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutComments,
                textInputEditText = mAutoComTextViewComments
            )
            mAppComTextViewComments!!.post{mAppComTextViewComments!!.setText(R.string.scr_lbl_comments)}
            mOtherDetails!!.addView(mLayoutCompatComments)

        }else if(reportType.equals("SPECIAL EVENT REPORT",ignoreCase = true)) {
            mCameraImagesLayout!!.visibility = View.VISIBLE
            imageValidationCount = 3
            mCameraImagesLayout!!.visibility = View.GONE

            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
//
//            scr_lbl_Zone
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewPBCZone =
                rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewPBCZone =
                rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatPBCZone =
                rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutPBCZone = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutPBCZone,
                appCompatAutoCompleteTextView = mAutoComTextViewPBCZone
            )
            mAppComTextViewPBCZone!!.post{mAppComTextViewPBCZone!!.setText(R.string.scr_lbl_zone)}
            mLocationDetails!!.addView(mLayoutCompatPBCZone)

//            scr_lbl_car_count
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewCarCount =
                rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewCarCount =
                rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatCarCount =
                rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            mInputLayoutCarCount = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = mInputLayoutCarCount,
                appCompatAutoCompleteTextView = mAutoComTextViewCarCount
            )
            mAppComTextViewCarCount!!.post{mAppComTextViewCarCount!!.setText(R.string.scr_lbl_car_counted)}
            mOtherDetails!!.addView(mLayoutCompatCarCount)
            mAutoComTextViewCarCount!!.setInputType(
                InputType.TYPE_CLASS_NUMBER or
                        InputType.TYPE_NUMBER_FLAG_DECIMAL or
                        InputType.TYPE_NUMBER_FLAG_SIGNED)

            mInputLayoutCarCount!!.endIconMode = END_ICON_NONE

//            scr_lbl_empty_space
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewEmptySpace =
                rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewEmptySpace =
                rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatEmptySpace =
                rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            mInputLayoutEmptySpace = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = mInputLayoutEmptySpace,
                appCompatAutoCompleteTextView = mAutoComTextViewEmptySpace
            )
            mAppComTextViewEmptySpace!!.post{mAppComTextViewEmptySpace!!.setText(R.string.scr_lbl_empty_space)}
            mOtherDetails!!.addView(mLayoutCompatEmptySpace)
            mAutoComTextViewEmptySpace!!.setInputType(
                InputType.TYPE_CLASS_NUMBER or
                        InputType.TYPE_NUMBER_FLAG_DECIMAL or
                        InputType.TYPE_NUMBER_FLAG_SIGNED)

            mInputLayoutEmptySpace!!.endIconMode = END_ICON_NONE

//            scr_lbl_number_of_violating_vehicles
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewNumberOfViolatingVehicles =
                rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewNumberOfViolatingVehicles =
                rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatNumberOfViolatingVehicles =
                rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            mInputLayoutNumberOfViolatingVehicles = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = mInputLayoutNumberOfViolatingVehicles,
                appCompatAutoCompleteTextView = mAutoComTextViewNumberOfViolatingVehicles
            )
            mAppComTextViewNumberOfViolatingVehicles!!.post{mAppComTextViewNumberOfViolatingVehicles!!.setText(R.string.scr_lbl_number_of_violating_vehicles)}
            mOtherDetails!!.addView(mLayoutCompatNumberOfViolatingVehicles)
            mAutoComTextViewNumberOfViolatingVehicles!!.setInputType(
                InputType.TYPE_CLASS_NUMBER or
                        InputType.TYPE_NUMBER_FLAG_DECIMAL or
                        InputType.TYPE_NUMBER_FLAG_SIGNED)

            mInputLayoutNumberOfViolatingVehicles!!.endIconMode = END_ICON_NONE

            mAutoComTextViewNumberOfViolatingVehicles?.onFocusChangeListener =
                View.OnFocusChangeListener { v, hasFocus ->
                    if (!hasFocus && mAutoComTextViewNumberOfViolatingVehicles!!.text!!.isNotEmpty()) {
                        val carCount :Double= (mAutoComTextViewCarCount!!.text!!.toString()).toDouble()
                        val violationCount :Double= (mAutoComTextViewNumberOfViolatingVehicles!!.text!!.toString()).toDouble()
                        val result : Double = (violationCount/(carCount*100)).toDouble()
                        try {
                            mAutoComTextViewViolationRate!!.setText(" "+ String.format("%.2f", result))
//                            mAutoComTextViewViolationRate!!.isFocusable = false
//                            mAppComTextViewComments!!.isFocusable = true
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }

//            scr_lbl_violation_rate
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewViolationRate =
                rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewViolationRate =
                rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatViolationRate =
                rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            mInputLayoutViolationRate = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = mInputLayoutViolationRate,
                appCompatAutoCompleteTextView = mAutoComTextViewViolationRate
            )
            mAppComTextViewViolationRate!!.post{mAppComTextViewViolationRate!!.setText(R.string.scr_lbl_number_of_violating_rate)}
            mOtherDetails!!.addView(mLayoutCompatViolationRate)

            mInputLayoutViolationRate!!.endIconMode = END_ICON_NONE

//            scr_lbl_event_name
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewField1 =
                rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewField1 =
                rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatField1 =
                rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            mInputLayoutField1 = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = mInputLayoutField1,
                appCompatAutoCompleteTextView = mAutoComTextViewField1
            )
            mAppComTextViewField1!!.post{mAppComTextViewField1!!.setText(R.string.scr_lbl_event_name)}
            mOtherDetails!!.addView(mLayoutCompatField1)

            mInputLayoutField1!!.endIconMode = END_ICON_NONE

            // scr_lbl_line
//            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
//            mAppComTextViewLine = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
//            mAutoComTextViewLine = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
//            mLayoutCompatLine = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
//            mAppComTextViewLine!!.setText(R.string.scr_lbl_line)
//            mOtherDetails!!.addView(mLayoutCompatLine)
//
//            mInputLayoutLine = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
//            mInputLayoutLine!!.endIconMode = END_ICON_NONE

            // comment
            rootView = View.inflate(this@AllReportActivity, R.layout.content_comment, null)
            mAppComTextViewComments = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewComments = rootView!!.findViewById<TextInputEditText>(R.id.AutoComTextView)
            mLayoutCompatComments = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutComments = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutComments,
                textInputEditText = mAutoComTextViewComments
            )
            mAppComTextViewComments!!.post{mAppComTextViewComments!!.setText(R.string.scr_lbl_comments)}
            mOtherDetails!!.addView(mLayoutCompatComments)

        }else if(reportType.equals("PCH Daily Updates",ignoreCase = true)) {
            mCameraImagesLayout!!.visibility = View.VISIBLE
            imageValidationCount = 8
            mCameraImagesLayout!!.visibility = View.GONE

            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)

//            scr_lbl_Zone
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewPBCZone =
                rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewPBCZone =
                rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatPBCZone =
                rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutPBCZone = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutPBCZone,
                appCompatAutoCompleteTextView = mAutoComTextViewPBCZone
            )
            mAppComTextViewPBCZone!!.post{mAppComTextViewPBCZone!!.setText(R.string.scr_lbl_zone)}
            mLocationDetails!!.addView(mLayoutCompatPBCZone)

//            scr_lbl_vendor_count
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewCarCount =
                rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewCarCount =
                rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatCarCount =
                rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            mInputLayoutCarCount = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = mInputLayoutCarCount,
                appCompatAutoCompleteTextView = mAutoComTextViewCarCount
            )
            mAppComTextViewCarCount!!.post{mAppComTextViewCarCount!!.setText(R.string.scr_lbl_vendor_counted)}
            mOtherDetails!!.addView(mLayoutCompatCarCount)

            mInputLayoutCarCount!!.endIconMode = END_ICON_NONE
            mAutoComTextViewCarCount!!.setInputType(
                InputType.TYPE_CLASS_NUMBER or
                        InputType.TYPE_NUMBER_FLAG_DECIMAL or
                        InputType.TYPE_NUMBER_FLAG_SIGNED)

//            scr_lbl_vendor_locations
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewLotArea =
                rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewLotArea =
                rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatLotArea =
                rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            mInputLayoutLotArea = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = mInputLayoutLotArea,
                appCompatAutoCompleteTextView = mAutoComTextViewLotArea
            )
            mAppComTextViewLotArea!!.post{mAppComTextViewLotArea!!.setText(R.string.scr_lbl_vendor_locations)}
            mOtherDetails!!.addView(mLayoutCompatLotArea)

            mInputLayoutLotArea!!.endIconMode = END_ICON_NONE

//            scr_lbl_number_of_security_observation
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewSecurityObservation =
                rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewSecurityObservation =
                rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatSecurityObservation =
                rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            mInputLayoutSecurityObservation = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = mInputLayoutSecurityObservation,
                appCompatAutoCompleteTextView = mAutoComTextViewSecurityObservation
            )
            mAppComTextViewSecurityObservation!!.post{mAppComTextViewSecurityObservation!!.setText(R.string.scr_lbl_number_of_security_observation)}
            mOtherDetails!!.addView(mLayoutCompatSecurityObservation)

            mInputLayoutSecurityObservation!!.endIconMode = END_ICON_NONE

//            // scr_lbl_line
//            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
//            mAppComTextViewLine = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
//            mAutoComTextViewLine = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
//            mLayoutCompatLine = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
//            mAppComTextViewLine!!.setText(R.string.scr_lbl_line)
//            mOtherDetails!!.addView(mLayoutCompatLine)
//
//            mInputLayoutLine = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
//            mInputLayoutLine!!.endIconMode = END_ICON_NONE

            // comment
            rootView = View.inflate(this@AllReportActivity, R.layout.content_comment, null)
            mAppComTextViewComments = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewComments = rootView!!.findViewById<TextInputEditText>(R.id.AutoComTextView)
            mLayoutCompatComments = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutComments = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutComments,
                textInputEditText = mAutoComTextViewComments
            )
            mAppComTextViewComments!!.post{mAppComTextViewComments!!.setText(R.string.scr_lbl_comments)}
            mOtherDetails!!.addView(mLayoutCompatComments)

        }else if(reportType.equals("Pay Station Report",ignoreCase = true)) {
            mCameraImagesLayout!!.visibility = View.VISIBLE
            imageValidationCount = 1
            mCameraImagesLayout!!.visibility = View.GONE

            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)

//            scr_lbl_Zone
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewPBCZone =
                rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewPBCZone =
                rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatPBCZone =
                rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutPBCZone = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutPBCZone,
                appCompatAutoCompleteTextView = mAutoComTextViewPBCZone
            )
            mAppComTextViewPBCZone!!.post{mAppComTextViewPBCZone!!.setText(R.string.scr_lbl_zone)}
            mLocationDetails!!.addView(mLayoutCompatPBCZone)

            // meter_no
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewMeterNo = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewMeterNo = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatMeterNo = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutMeterNo = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutMeterNo,
                appCompatAutoCompleteTextView = mAutoComTextViewMeterNo
            )
            mAppComTextViewMeterNo!!.setText(R.string.scr_lbl_station)
            mLocationDetails!!.addView(mLayoutCompatMeterNo)

//            scr_lbl_machine_number1
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewField1 =
                rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewField1 =
                rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatField1 =
                rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            mInputLayoutField1 = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = mInputLayoutField1,
                appCompatAutoCompleteTextView = mAutoComTextViewField1
            )
            mAppComTextViewField1!!.post{mAppComTextViewField1!!.setText(R.string.scr_lbl_machine_number1)}
            mOtherDetails!!.addView(mLayoutCompatField1)


            mInputLayoutField1!!.endIconMode = END_ICON_NONE
            mAutoComTextViewField1!!.setInputType(
                InputType.TYPE_CLASS_NUMBER or
                        InputType.TYPE_NUMBER_FLAG_DECIMAL or
                        InputType.TYPE_NUMBER_FLAG_SIGNED)

//            scr_lbl_machine_number2
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewField2 =
                rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewField2 =
                rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatField2 =
                rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            mInputLayoutField2 = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = mInputLayoutField2,
                appCompatAutoCompleteTextView = mAutoComTextViewField2
            )
            mAppComTextViewField2!!.post{mAppComTextViewField2!!.setText(R.string.scr_lbl_machine_number2)}
            mOtherDetails!!.addView(mLayoutCompatField2)

            mInputLayoutField2!!.endIconMode = END_ICON_NONE
            mAutoComTextViewField2!!.setInputType(
                InputType.TYPE_CLASS_NUMBER or
                        InputType.TYPE_NUMBER_FLAG_DECIMAL or
                        InputType.TYPE_NUMBER_FLAG_SIGNED)

//            scr_lbl_machine3
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewField3 =
                rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewField3 =
                rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatField3 =
                rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            mInputLayoutField3 = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = mInputLayoutField3,
                appCompatAutoCompleteTextView = mAutoComTextViewField3
            )
            mAppComTextViewField3!!.post{mAppComTextViewField3!!.setText(R.string.scr_lbl_machine_number3)}
            mOtherDetails!!.addView(mLayoutCompatField3)

            mInputLayoutField3!!.endIconMode = END_ICON_NONE
            mAutoComTextViewField3!!.setInputType(
                InputType.TYPE_CLASS_NUMBER or
                        InputType.TYPE_NUMBER_FLAG_DECIMAL or
                        InputType.TYPE_NUMBER_FLAG_SIGNED)

//            scr_lbl_description1
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewField4 =
                rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewField4 =
                rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatField4 =
                rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            mInputLayoutField4 = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = mInputLayoutField4,
                appCompatAutoCompleteTextView = mAutoComTextViewField4
            )
            mAppComTextViewField4!!.post{mAppComTextViewField4!!.setText(R.string.scr_lbl_description1)}
            mOtherDetails!!.addView(mLayoutCompatField4)

            mInputLayoutField4!!.endIconMode = END_ICON_NONE

//            scr_lbl_description2
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewField5 =
                rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewField5 =
                rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatField5 =
                rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            mInputLayoutField5 = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = mInputLayoutField5,
                appCompatAutoCompleteTextView = mAutoComTextViewField5
            )
            mAppComTextViewField5!!.post{mAppComTextViewField5!!.setText(R.string.scr_lbl_description2)}
            mOtherDetails!!.addView(mLayoutCompatField5)

            mInputLayoutField5!!.endIconMode = END_ICON_NONE

//            scr_lbl_description3
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewField6 =
                rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewField6 =
                rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatField6 =
                rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            mInputLayoutField6 = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = mInputLayoutField6,
                appCompatAutoCompleteTextView = mAutoComTextViewField6
            )
            mAppComTextViewField6!!.post{mAppComTextViewField6!!.setText(R.string.scr_lbl_description3)}
            mOtherDetails!!.addView(mLayoutCompatField6)

            mInputLayoutField6!!.endIconMode = END_ICON_NONE

            // scr_lbl_line
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewLine = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewLine = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatLine = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            mInputLayoutLine = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = mInputLayoutLine,
                appCompatAutoCompleteTextView = mAutoComTextViewLine
            )
            mAppComTextViewLine!!.setText(R.string.scr_lbl_line)
            mOtherDetails!!.addView(mLayoutCompatLine)

            mInputLayoutLine!!.endIconMode = END_ICON_NONE

            // comment1
            rootView = View.inflate(this@AllReportActivity, R.layout.content_comment, null)
            mAppComTextViewComments = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewComments = rootView!!.findViewById<TextInputEditText>(R.id.AutoComTextView)
            mLayoutCompatComments = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutComments = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutComments,
                textInputEditText = mAutoComTextViewComments
            )
            mAppComTextViewComments!!.post{mAppComTextViewComments!!.setText(R.string.scr_lbl_comment1)}
            mOtherDetails!!.addView(mLayoutCompatComments)


            // comment2
            rootView = View.inflate(this@AllReportActivity, R.layout.content_comment, null)
            mAppComTextViewComment2 = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewComment2 = rootView!!.findViewById<TextInputEditText>(R.id.AutoComTextView)
            mLayoutCompatComment2 = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutComment2 = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutComment2,
                textInputEditText = mAutoComTextViewComment2
            )
            mAppComTextViewComment2!!.post{mAppComTextViewComment2!!.setText(R.string.scr_lbl_comment2)}
            mOtherDetails!!.addView(mLayoutCompatComment2)

            // comment3
            rootView = View.inflate(this@AllReportActivity, R.layout.content_comment, null)
            mAppComTextViewComment3 = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewComment3 = rootView!!.findViewById<TextInputEditText>(R.id.AutoComTextView)
            mLayoutCompatComment3 = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutComment3 = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutComment3,
                textInputEditText = mAutoComTextViewComment3
            )
            mAppComTextViewComment3!!.post{mAppComTextViewComment3!!.setText(R.string.scr_lbl_comment3)}
            mOtherDetails!!.addView(mLayoutCompatComment3)

        } else if(reportType.equals("Signage Report",ignoreCase = true)) {
            mCameraImagesLayout!!.visibility = View.VISIBLE
            imageValidationCount = 1
            mCameraImagesLayout!!.visibility = View.GONE

            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)


//            scr_lbl_Zone
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewPBCZone =
                rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewPBCZone =
                rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatPBCZone =
                rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutPBCZone = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutPBCZone,
                appCompatAutoCompleteTextView = mAutoComTextViewPBCZone
            )
            mAppComTextViewPBCZone!!.post{mAppComTextViewPBCZone!!.setText(R.string.scr_lbl_zone)}
            mLocationDetails!!.addView(mLayoutCompatPBCZone)

            // meter_no
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewMeterNo = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewMeterNo = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatMeterNo = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutMeterNo = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutMeterNo,
                appCompatAutoCompleteTextView = mAutoComTextViewMeterNo
            )
            mAppComTextViewMeterNo!!.setText(R.string.scr_lbl_station)
            mLocationDetails!!.addView(mLayoutCompatMeterNo)

//            scr_lbl_description1
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewField1 =
                rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewField1 =
                rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatField1 =
                rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            mInputLayoutField1 = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = mInputLayoutField1,
                appCompatAutoCompleteTextView = mAutoComTextViewField1
            )
            mAppComTextViewField1!!.post{mAppComTextViewField1!!.setText(R.string.scr_lbl_description1)}
            mOtherDetails!!.addView(mLayoutCompatField1)

            mInputLayoutField1!!.endIconMode = END_ICON_NONE

//            scr_lbl_description2
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewField2 =
                rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewField2 =
                rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatField2 =
                rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            mInputLayoutField2 = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = mInputLayoutField2,
                appCompatAutoCompleteTextView = mAutoComTextViewField2
            )
            mAppComTextViewField2!!.post{mAppComTextViewField2!!.setText(R.string.scr_lbl_description2)}
            mOtherDetails!!.addView(mLayoutCompatField2)

            mInputLayoutField2!!.endIconMode = END_ICON_NONE

//            scr_lbl_description3
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewField3 =
                rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewField3 =
                rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatField3 =
                rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            mInputLayoutField3 = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = mInputLayoutField3,
                appCompatAutoCompleteTextView = mAutoComTextViewField3
            )
            mAppComTextViewField3!!.post{mAppComTextViewField3!!.setText(R.string.scr_lbl_description3)}
            mOtherDetails!!.addView(mLayoutCompatField3)

            mInputLayoutField3!!.endIconMode = END_ICON_NONE

            // scr_lbl_line
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewLine = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewLine = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatLine = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            mInputLayoutLine = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = mInputLayoutLine,
                appCompatAutoCompleteTextView = mAutoComTextViewLine
            )
            mAppComTextViewLine!!.setText(R.string.scr_lbl_line)
            mOtherDetails!!.addView(mLayoutCompatLine)

            mInputLayoutLine!!.endIconMode = END_ICON_NONE

            // comment1
            rootView = View.inflate(this@AllReportActivity, R.layout.content_comment, null)
            mAppComTextViewComments = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewComments = rootView!!.findViewById<TextInputEditText>(R.id.AutoComTextView)
            mLayoutCompatComments = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutComments = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutComments,
                textInputEditText = mAutoComTextViewComments
            )
            mAppComTextViewComments!!.post{mAppComTextViewComments!!.setText(R.string.scr_lbl_comment1)}
            mOtherDetails!!.addView(mLayoutCompatComments)

        }else if(reportType.equals("Homeless Report",ignoreCase = true)) {
            mCameraImagesLayout!!.visibility = View.VISIBLE
            imageValidationCount = 4
            mCameraImagesLayout!!.visibility = View.GONE

            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)

//            scr_lbl_Zone
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewPBCZone =
                rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewPBCZone =
                rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatPBCZone =
                rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutPBCZone = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutPBCZone,
                appCompatAutoCompleteTextView = mAutoComTextViewPBCZone
            )
            mAppComTextViewPBCZone!!.post{mAppComTextViewPBCZone!!.setText(R.string.scr_lbl_zone)}
            mLocationDetails!!.addView(mLayoutCompatPBCZone)

            // meter_no
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewMeterNo = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewMeterNo = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatMeterNo = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutMeterNo = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutMeterNo,
                appCompatAutoCompleteTextView = mAutoComTextViewMeterNo
            )
            mAppComTextViewMeterNo!!.setText(R.string.scr_lbl_station)
            mLocationDetails!!.addView(mLayoutCompatMeterNo)

//            scr_lbl_description1
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewField1 =
                rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewField1 =
                rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatField1 =
                rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            mInputLayoutField1 = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = mInputLayoutField1,
                appCompatAutoCompleteTextView = mAutoComTextViewField1
            )
            mAppComTextViewField1!!.post{mAppComTextViewField1!!.setText(R.string.scr_lbl_description1)}
            mOtherDetails!!.addView(mLayoutCompatField1)

            mInputLayoutField1!!.endIconMode = END_ICON_NONE

            // scr_lbl_line
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewLine = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewLine = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatLine = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            mInputLayoutLine = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = mInputLayoutLine,
                appCompatAutoCompleteTextView = mAutoComTextViewLine
            )
            mAppComTextViewLine!!.setText(R.string.scr_lbl_line)
            mOtherDetails!!.addView(mLayoutCompatLine)

            mInputLayoutLine!!.endIconMode = END_ICON_NONE

            // comment1
            rootView = View.inflate(this@AllReportActivity, R.layout.content_comment, null)
            mAppComTextViewComments = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewComments = rootView!!.findViewById<TextInputEditText>(R.id.AutoComTextView)
            mLayoutCompatComments = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutComments = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutComments,
                textInputEditText = mAutoComTextViewComments
            )
            mAppComTextViewComments!!.post{mAppComTextViewComments!!.setText(R.string.scr_lbl_comment1)}
            mOtherDetails!!.addView(mLayoutCompatComments)

        }else if(reportType.equals("Lot Inspection Report",ignoreCase = true)) {
            mCameraImagesLayout!!.visibility = View.GONE
            imageValidationCount = 0
            mCameraImagesLayout!!.visibility = View.GONE

            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)

//            scr_lbl_Zone
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewPBCZone =
                rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewPBCZone =
                rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatPBCZone =
                rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutPBCZone = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutPBCZone,
                appCompatAutoCompleteTextView = mAutoComTextViewPBCZone
            )
            mAppComTextViewPBCZone!!.post{mAppComTextViewPBCZone!!.setText(R.string.scr_lbl_zone)}
            mLocationDetails!!.addView(mLayoutCompatPBCZone)

            // scr_lbl_all_signs_reported
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewInternetConnectivity = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewInternetConnectivity = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatInternetConnectivity = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutInternetConnectivity = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutInternetConnectivity,
                appCompatAutoCompleteTextView = mAutoComTextViewInternetConnectivity
            )
            mAppComTextViewInternetConnectivity!!.setText(R.string.scr_lbl_all_signs_reported)
            mOtherDetails!!.addView(mLayoutCompatInternetConnectivity)

            // all_paystations_reported
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewMeterLabelVisible = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewMeterLabelVisible = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatMeterLabelVisible = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutMeterLableVisible = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutMeterLableVisible,
                appCompatAutoCompleteTextView = mAutoComTextViewMeterLabelVisible
            )
            mAppComTextViewMeterLabelVisible!!.setText(R.string.scr_lbl_all_paystations_reported)
            mOtherDetails!!.addView(mLayoutCompatMeterLabelVisible)

            // any_homeless_reported
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewDigitalDisplayVisible = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewDigitalDisplayVisible = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatDigitalDisplayVisible = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutDigitalDisplayVisible = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutDigitalDisplayVisible,
                appCompatAutoCompleteTextView = mAutoComTextViewDigitalDisplayVisible
            )
            mAppComTextViewDigitalDisplayVisible!!.setText(R.string.scr_lbl_any_homeless_reported)
            mOtherDetails!!.addView(mLayoutCompatDigitalDisplayVisible)

            // meter_label_visible
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewCoinJam = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewCoinJam = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatCoinJam = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutCoinJam = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutCoinJam,
                appCompatAutoCompleteTextView = mAutoComTextViewCoinJam
            )
            mAppComTextViewCoinJam!!.setText(R.string.scr_lbl_any_safety_issues_reported)
            mOtherDetails!!.addView(mLayoutCompatCoinJam)

            // scr_lbl_line
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewLine = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewLine = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatLine = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            mInputLayoutLine = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = mInputLayoutLine,
                appCompatAutoCompleteTextView = mAutoComTextViewLine
            )
            mAppComTextViewLine!!.setText(R.string.scr_lbl_line)
            mOtherDetails!!.addView(mLayoutCompatLine)

            mInputLayoutLine!!.endIconMode = END_ICON_NONE

//            // comment1
//            rootView = View.inflate(this@AllReportActivity, R.layout.content_comment, null)
//            mAppComTextViewComments = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
//            mAutoComTextViewComments = rootView!!.findViewById<TextInputEditText>(R.id.AutoComTextView)
//            mLayoutCompatComments = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
//            mAppComTextViewComments!!.post{mAppComTextViewComments!!.setText(R.string.scr_lbl_comment1)}
//            mOtherDetails!!.addView(mLayoutCompatComments)

        }else if(reportType.equals("Lot Count Vio Rate Report",ignoreCase = true)) {
            mCameraImagesLayout!!.visibility = View.GONE
            imageValidationCount = 0
            mCameraImagesLayout!!.visibility = View.GONE

            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)

//            scr_lbl_Zone
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewPBCZone =
                rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewPBCZone =
                rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatPBCZone =
                rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutPBCZone = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutPBCZone,
                appCompatAutoCompleteTextView = mAutoComTextViewPBCZone
            )
            mAppComTextViewPBCZone!!.post{mAppComTextViewPBCZone!!.setText(R.string.scr_lbl_zone)}
            mLocationDetails!!.addView(mLayoutCompatPBCZone)

            // meter_no
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewMeterNo = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewMeterNo = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatMeterNo = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutMeterNo = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutMeterNo,
                appCompatAutoCompleteTextView = mAutoComTextViewMeterNo
            )
            mAppComTextViewMeterNo!!.setText(R.string.scr_lbl_station)
            mLocationDetails!!.addView(mLayoutCompatMeterNo)

//            scr_lbl_car_count
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewCarCount =
                rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewCarCount =
                rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatCarCount =
                rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            mInputLayoutCarCount = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = mInputLayoutCarCount,
                appCompatAutoCompleteTextView = mAutoComTextViewCarCount
            )
            mAppComTextViewCarCount!!.post{mAppComTextViewCarCount!!.setText(R.string.scr_lbl_car_counted)}
            mOtherDetails!!.addView(mLayoutCompatCarCount)
            mAutoComTextViewCarCount!!.setInputType(
                InputType.TYPE_CLASS_NUMBER or
                        InputType.TYPE_NUMBER_FLAG_DECIMAL or
                        InputType.TYPE_NUMBER_FLAG_SIGNED)

            mInputLayoutCarCount!!.endIconMode = END_ICON_NONE

//            scr_lbl_car_space
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewField1 =
                rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewField1 =
                rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatField1 =
                rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            mInputLayoutField1 = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = mInputLayoutField1,
                appCompatAutoCompleteTextView = mAutoComTextViewField1
            )
            mAppComTextViewField1!!.post{mAppComTextViewField1!!.setText(R.string.scr_lbl_car_space)}
            mOtherDetails!!.addView(mLayoutCompatField1)
            mAutoComTextViewField1!!.setInputType(
                InputType.TYPE_CLASS_NUMBER or
                        InputType.TYPE_NUMBER_FLAG_DECIMAL or
                        InputType.TYPE_NUMBER_FLAG_SIGNED)

            mInputLayoutField1!!.endIconMode = END_ICON_NONE

//            scr_lbl_empty_space
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewEmptySpace =
                rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewEmptySpace =
                rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatEmptySpace =
                rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            mInputLayoutEmptySpace = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = mInputLayoutEmptySpace,
                appCompatAutoCompleteTextView = mAutoComTextViewEmptySpace
            )
            mAppComTextViewEmptySpace!!.post{mAppComTextViewEmptySpace!!.setText(R.string.scr_lbl_empty_space)}
            mOtherDetails!!.addView(mLayoutCompatEmptySpace)
            mAutoComTextViewEmptySpace!!.setInputType(
                InputType.TYPE_CLASS_NUMBER or
                        InputType.TYPE_NUMBER_FLAG_DECIMAL or
                        InputType.TYPE_NUMBER_FLAG_SIGNED)

            mInputLayoutEmptySpace!!.endIconMode = END_ICON_NONE

//            scr_lbl_number_of_violating_vehicles
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewNumberOfViolatingVehicles =
                rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewNumberOfViolatingVehicles =
                rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatNumberOfViolatingVehicles =
                rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            mInputLayoutNumberOfViolatingVehicles = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = mInputLayoutNumberOfViolatingVehicles,
                appCompatAutoCompleteTextView = mAutoComTextViewNumberOfViolatingVehicles
            )
            mAppComTextViewNumberOfViolatingVehicles!!.post{mAppComTextViewNumberOfViolatingVehicles!!.setText(R.string.scr_lbl_number_of_violating_vehicles)}
            mOtherDetails!!.addView(mLayoutCompatNumberOfViolatingVehicles)
            mAutoComTextViewNumberOfViolatingVehicles!!.setInputType(
                InputType.TYPE_CLASS_NUMBER or
                        InputType.TYPE_NUMBER_FLAG_DECIMAL or
                        InputType.TYPE_NUMBER_FLAG_SIGNED)

            mInputLayoutNumberOfViolatingVehicles!!.endIconMode = END_ICON_NONE

            mAutoComTextViewNumberOfViolatingVehicles?.onFocusChangeListener =
                View.OnFocusChangeListener { v, hasFocus ->
                    if (!hasFocus && mAutoComTextViewNumberOfViolatingVehicles!!.text!!.isNotEmpty()) {
                        val carCount :Double= (mAutoComTextViewCarCount!!.text!!.toString()).toDouble()
                        val violationCount :Double= (mAutoComTextViewNumberOfViolatingVehicles!!.text!!.toString()).toDouble()
                        val result : Double = (violationCount/(carCount*100)).toDouble()
                        try {
                            mAutoComTextViewViolationRate!!.setText(" "+ String.format("%.2f", result))
//                            mAutoComTextViewViolationRate!!.isFocusable = false
//                            mAppComTextViewComments!!.isFocusable = true
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }

//            scr_lbl_violation_rate
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewViolationRate =
                rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewViolationRate =
                rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatViolationRate =
                rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            mInputLayoutViolationRate = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = mInputLayoutViolationRate,
                appCompatAutoCompleteTextView = mAutoComTextViewViolationRate
            )
            mAppComTextViewViolationRate!!.post{mAppComTextViewViolationRate!!.setText(R.string.scr_lbl_number_of_violating_rate)}
            mOtherDetails!!.addView(mLayoutCompatViolationRate)

            mInputLayoutViolationRate!!.endIconMode = END_ICON_NONE

            // scr_lbl_line
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewLine = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewLine = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatLine = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            mInputLayoutLine = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = mInputLayoutLine,
                appCompatAutoCompleteTextView = mAutoComTextViewLine
            )
            mAppComTextViewLine!!.setText(R.string.scr_lbl_line)
            mOtherDetails!!.addView(mLayoutCompatLine)

            mInputLayoutLine!!.endIconMode = END_ICON_NONE

//            // comment1
            rootView = View.inflate(this@AllReportActivity, R.layout.content_comment, null)
            mAppComTextViewComments = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewComments = rootView!!.findViewById<TextInputEditText>(R.id.AutoComTextView)
            mLayoutCompatComments = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutComments = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutComments,
                textInputEditText = mAutoComTextViewComments
            )
            mAppComTextViewComments!!.post{mAppComTextViewComments!!.setText(R.string.scr_lbl_comment1)}
            mOtherDetails!!.addView(mLayoutCompatComments)

        }else if(reportType.equals("Work Order Report",ignoreCase = true)) {
            mCameraImagesLayout!!.visibility = View.GONE
            imageValidationCount = 1
            mCameraImagesLayout!!.visibility = View.GONE

            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)

//          scr_lbl_block
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewBlock =
                rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewBlock =
                rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatBlock =
                rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutBlock = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutBlock,
                appCompatAutoCompleteTextView = mAutoComTextViewBlock
            )
            mAppComTextViewBlock!!.post { mAppComTextViewBlock!!.setText(R.string.scr_lbl_block) }
            mLocationDetails!!.addView(mLayoutCompatBlock)

//          scr_lbl_street
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewStreet =
                rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewStreet =
                rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatStreet =
                rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutStreet = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutStreet,
                appCompatAutoCompleteTextView = mAutoComTextViewStreet
            )
            mAppComTextViewStreet!!.post { mAppComTextViewStreet!!.setText(R.string.scr_lbl_street) }
            mLocationDetails!!.addView(mLayoutCompatStreet)

//          scr_lbl_side
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewDirection =
                rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewDirection =
                rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatDirection =
                rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutDirection = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutDirection,
                appCompatAutoCompleteTextView = mAutoComTextViewDirection
            )
            mAppComTextViewDirection!!.post { mAppComTextViewDirection!!.setText(R.string.scr_lbl_side) }
            mLocationDetails!!.addView(mLayoutCompatDirection)

//            scr_lbl_sign
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewField1 =
                rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewField1 =
                rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatField1 =
                rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            mInputLayoutField1 = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = mInputLayoutField1,
                appCompatAutoCompleteTextView = mAutoComTextViewField1
            )
            mAppComTextViewField1!!.post{mAppComTextViewField1!!.setText(R.string.scr_lbl_sign)}
            mOtherDetails!!.addView(mLayoutCompatField1)

            mInputLayoutField1!!.endIconMode = END_ICON_NONE

//            scr_lbl_actions_required
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewField2 =
                rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewField2 =
                rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatField2 =
                rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            mInputLayoutField2 = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = mInputLayoutField2,
                appCompatAutoCompleteTextView = mAutoComTextViewField2
            )
            mAppComTextViewField2!!.post{mAppComTextViewField2!!.setText(R.string.scr_lbl_actions_required)}
            mOtherDetails!!.addView(mLayoutCompatField2)

            mInputLayoutField2!!.endIconMode = END_ICON_NONE

//            // scr_lbl_line
//            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
//            mAppComTextViewLine = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
//            mAutoComTextViewLine = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
//            mLayoutCompatLine = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
//            mAppComTextViewLine!!.setText(R.string.scr_lbl_line)
//            mOtherDetails!!.addView(mLayoutCompatLine)
//
//            mInputLayoutLine = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
//            mInputLayoutLine!!.endIconMode = END_ICON_NONE

//            // comment1
            rootView = View.inflate(this@AllReportActivity, R.layout.content_comment, null)
            mAppComTextViewComments = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewComments = rootView!!.findViewById<TextInputEditText>(R.id.AutoComTextView)
            mLayoutCompatComments = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutComments = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutComments,
                textInputEditText = mAutoComTextViewComments
            )
            mAppComTextViewComments!!.post{mAppComTextViewComments!!.setText(R.string.scr_lbl_comment1)}
            mOtherDetails!!.addView(mLayoutCompatComments)

        }else if(reportType.equals("Safety Report Immediate Attention Required",ignoreCase = true)) {
            mCameraImagesLayout!!.visibility = View.GONE
            imageValidationCount = 3
            mCameraImagesLayout!!.visibility = View.GONE

            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)

//            scr_lbl_Zone
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewPBCZone =
                rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewPBCZone =
                rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatPBCZone =
                rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutPBCZone = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutPBCZone,
                appCompatAutoCompleteTextView = mAutoComTextViewPBCZone
            )
            mAppComTextViewPBCZone!!.post{mAppComTextViewPBCZone!!.setText(R.string.scr_lbl_zone)}
            mLocationDetails!!.addView(mLayoutCompatPBCZone)

            // meter_no
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewMeterNo = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewMeterNo = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatMeterNo = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutMeterNo = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutMeterNo,
                appCompatAutoCompleteTextView = mAutoComTextViewMeterNo
            )
            mAppComTextViewMeterNo!!.setText(R.string.scr_lbl_station)
            mLocationDetails!!.addView(mLayoutCompatMeterNo)

//            scr_lbl_safety_issue
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewSafetyIssue =
                rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewSafetyIssue =
                rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatSafetyIssue =
                rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutSafetyIssue = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutSafetyIssue,
                appCompatAutoCompleteTextView = mAutoComTextViewSafetyIssue
            )
            mAppComTextViewSafetyIssue!!.post{mAppComTextViewSafetyIssue!!.setText(R.string.scr_lbl_safety_issue)}
            mOtherDetails!!.addView(mLayoutCompatSafetyIssue)

//            scr_lbl_description_level_quadrant
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewField2 =
                rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewField2 =
                rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatField2 =
                rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            mInputLayoutField2 = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = mInputLayoutField2,
                appCompatAutoCompleteTextView = mAutoComTextViewField2
            )
            mAppComTextViewField2!!.post{mAppComTextViewField2!!.setText(R.string.scr_lbl_description_level_quadrant)}
            mOtherDetails!!.addView(mLayoutCompatField2)

            mInputLayoutField2!!.endIconMode = END_ICON_NONE

            // scr_lbl_line
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewLine = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewLine = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatLine = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            mInputLayoutLine = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = mInputLayoutLine,
                appCompatAutoCompleteTextView = mAutoComTextViewLine
            )
            mAppComTextViewLine!!.setText(R.string.scr_lbl_line)
            mOtherDetails!!.addView(mLayoutCompatLine)

            mInputLayoutLine!!.endIconMode = END_ICON_NONE


        }else if(reportType.equals("Trash Lot Maintenance Report",ignoreCase = true)) {
            mCameraImagesLayout!!.visibility = View.GONE
            imageValidationCount = 1
            mCameraImagesLayout!!.visibility = View.GONE

            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)

//            scr_lbl_Zone
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewPBCZone =
                rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewPBCZone =
                rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatPBCZone =
                rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutPBCZone = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutPBCZone,
                appCompatAutoCompleteTextView = mAutoComTextViewPBCZone
            )
            mAppComTextViewPBCZone!!.post{mAppComTextViewPBCZone!!.setText(R.string.scr_lbl_zone)}

            mLocationDetails!!.addView(mLayoutCompatPBCZone)

//            scr_lbl_safety_issue
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewRequiredServices =
                rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewRequiredServices =
                rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatRequiredServices =
                rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            textInputLayoutRequiredServices = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = textInputLayoutRequiredServices,
                appCompatAutoCompleteTextView = mAutoComTextViewRequiredServices
            )

            mAppComTextViewRequiredServices!!.post{mAppComTextViewRequiredServices!!.setText(R.string.scr_lbl_safety_issue)}
            mOtherDetails!!.addView(mLayoutCompatRequiredServices)



//            scr_lbl_description_level_quadrant
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewField2 =
                rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewField2 =
                rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatField2 =
                rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            mInputLayoutField2 = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = mInputLayoutField2,
                appCompatAutoCompleteTextView = mAutoComTextViewField2
            )
            mAppComTextViewField2!!.post{mAppComTextViewField2!!.setText(R.string.scr_lbl_description_level_quadrant)}

            mInputLayoutField2!!.endIconMode = END_ICON_NONE

            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = mInputLayoutField2,
                appCompatAutoCompleteTextView = mAutoComTextViewField2
            )

            mOtherDetails!!.addView(mLayoutCompatField2)


            // scr_lbl_line
            rootView = View.inflate(this@AllReportActivity, R.layout.content_dropdown, null)
            mAppComTextViewLine = rootView!!.findViewById<AppCompatTextView>(R.id.appcomptext)
            mAutoComTextViewLine = rootView!!.findViewById<AppCompatAutoCompleteTextView>(R.id.AutoComTextView)
            mLayoutCompatLine = rootView!!.findViewById<LinearLayoutCompat>(R.id.linear_layout)
            mInputLayoutLine = rootView!!.findViewById<TextInputLayout>(R.id.input_text)
            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = mInputLayoutLine,
                appCompatAutoCompleteTextView = mAutoComTextViewLine
            )
            mInputLayoutLine!!.endIconMode = END_ICON_NONE

            setCrossClearButton(
                context = this@AllReportActivity,
                textInputLayout = mInputLayoutLine,
                appCompatAutoCompleteTextView = mAutoComTextViewLine
            )

            mAppComTextViewLine!!.setText(R.string.scr_lbl_line)
            mOtherDetails!!.addView(mLayoutCompatLine)
        }
        dismissLoader()
}



class MonthYearPickerDialog : DialogFragment() {
private var listener: DatePickerDialog.OnDateSetListener? = null
fun setListener(listener: DatePickerDialog.OnDateSetListener?) {
this.listener = listener
}

override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    val builder = AlertDialog.Builder(requireActivity())
// Get the layout inflater
    val inflater = requireActivity().layoutInflater
    val cal = Calendar.getInstance()
    val dialog: View = inflater.inflate(R.layout.date_picker_dialog, null)
    val monthPicker = dialog.findViewById<View>(R.id.picker_month) as NumberPicker
    val yearPicker = dialog.findViewById<View>(R.id.picker_year) as NumberPicker
    monthPicker.minValue = 0
    monthPicker.maxValue = 12
    monthPicker.value = cal[Calendar.MONTH]
    val year = cal[Calendar.YEAR]
    yearPicker.minValue = year - 7
    yearPicker.maxValue = MAX_YEAR
    yearPicker.value = year
    builder.setView(dialog) // Add action buttons
        .setPositiveButton(
            R.string.alt_lbl_OK,
            DialogInterface.OnClickListener { dialog, id ->
                listener!!.onDateSet(
                    null,
                    yearPicker.value,
                    monthPicker.value,
                    0
                )
            })
        .setNegativeButton(
            R.string.scr_btn_cancel,
            DialogInterface.OnClickListener { dialog, id -> this@MonthYearPickerDialog.dialog!!.cancel() })
    return builder.create()
}

companion object {
private const val MAX_YEAR = 2025
}
}

private fun openTimePicker(timePickerField: AppCompatAutoCompleteTextView?) {
    val mTimePicker: TimePickerDialog
    val mcurrentTime = Calendar.getInstance()
    val hour = mcurrentTime.get(Calendar.HOUR_OF_DAY)
    val minute = mcurrentTime.get(Calendar.MINUTE)
//    val df1 = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
    val df1 = SimpleDateFormat("yyyy-MM-dd")
    var specialReportTimeFormat = ""
     if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val current = LocalDateTime.now()
        specialReportTimeFormat = current.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    } else {
         specialReportTimeFormat = df1.format(mcurrentTime.time)
    }
//    val specialReportTimeFormat = current.format(DateTimeFormatter.ofPattern("dd-MM-yy'T'HH:mm:ss'Z'"))

    mTimePicker = TimePickerDialog(this, object : TimePickerDialog.OnTimeSetListener {
        override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
            if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAMETRO, true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CORPUSCHRISTI, true)) {
                mainScope.launch {
                    var am_pm = ""
                    val datetime = Calendar.getInstance()
                    datetime[Calendar.HOUR_OF_DAY] = hourOfDay
                    datetime[Calendar.MINUTE] = minute

                    if (datetime[Calendar.AM_PM] === Calendar.AM) am_pm =
                        "AM" else if (datetime[Calendar.AM_PM] === Calendar.PM) am_pm = "PM"

                    val strHrsToShow =
                        if (datetime[Calendar.HOUR] === 0) "12" else datetime[Calendar.HOUR]

                    timePickerField!!.post {
                        timePickerField!!.setText(
                            "" + strHrsToShow + ":" + String.format("%02d", datetime[Calendar.MINUTE]) + " " + am_pm
                        )
                    }
                }
            } else {
                timePickerField!!.setText(String.format("%02d%02d", hourOfDay, minute))
            }
            timePickerField!!.setTag(specialReportTimeFormat + "T" + hourOfDay + ":" + minute + ":11Z")
            timePickerField!!.setError(null)
        }
    }, hour, minute, false)
    mTimePicker.show()
}

    //set value to make vehicle dropdown
    private fun setDropdownMakeVehicle() {
        try {
            var makeValue: String = ""
            ioScope.launch {
                val mApplicationList = Singleton.getDataSetList(DATASET_CAR_MAKE_LIST, mDb)
                val uniqueDataSet: MutableSet<String> = HashSet()
                if (uniqueDataSet == null || uniqueDataSet.size < 1) {
                    if (mApplicationList != null && mApplicationList.isNotEmpty()) {
                        for (i in mApplicationList.indices) {
                            uniqueDataSet.add(mApplicationList[i].make.toString() + "#" + mApplicationList[i].makeText.toString())
                        }
                    }
                }
                val Geeks = uniqueDataSet.toTypedArray()
                Arrays.sort(Geeks)
                if (uniqueDataSet != null && uniqueDataSet.size > 0) {
                    val mDropdownList = arrayOfNulls<String>(uniqueDataSet.size)
                    for (i in Geeks.indices) {
                        mDropdownList[i] = Geeks[i].split("#").toTypedArray()[1]
                    }
                    mAutoComTextViewMake?.post {
                        val adapter = ArrayAdapter(
                            this@AllReportActivity,
                            R.layout.row_dropdown_lpr_details_item,
                            mDropdownList
                        )
                        try {
                            mAutoComTextViewMake?.threshold = 1
                            mAutoComTextViewMake?.setAdapter<ArrayAdapter<String?>>(adapter)
                            mAutoComTextViewMake?.onItemClickListener =
                                OnItemClickListener { parent, view, position, id -> //
                                    AppUtils.hideSoftKeyboard(this@AllReportActivity)
                                    val index = getIndexOf(
                                        mApplicationList,
                                        parent.getItemAtPosition(position).toString()
                                    )
                                    mSelectedMake = mApplicationList!![index].make
                                    mSelectedMakeValue = mApplicationList[index].makeText
                                    if (mSelectedMakeValue != null) {
                                        setDropdownVehicleModel(mSelectedMake,false)
                                    } else {
                                        setDropdownVehicleModel("",false)
                                    }
                                }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getIndexOf(list: List<DatasetResponse>?, name: String): Int {
        var pos = 0
        for (myObj in list!!) {
            if (name.equals(myObj.makeText, ignoreCase = true)) return pos
            pos++
        }
        return -1
    }

     //set value to vehicle model dropdown
    private fun setDropdownVehicleModel(value: String?,isAutoSelect: Boolean?) {
        try {
            var adapterModel: ArrayAdapter<String?>? = null
            if (mAutoComTextViewModel != null) {
                mModelList?.clear()
                ioScope.launch {
                    val mApplicationList =
                        Singleton.getDataSetList(DATASET_CAR_MODEL_LIST, mDb)
                    if (mApplicationList != null && mApplicationList.size > 0) {
                        try {
                            Collections.sort(
                                mApplicationList,
                                object : Comparator<DatasetResponse?> {
                                    override fun compare(
                                        lhs: DatasetResponse?,
                                        rhs: DatasetResponse?
                                    ): Int {
                                        return (if (lhs?.model != null) lhs.model else "")!!.compareTo(
                                            (if (rhs?.model != null) rhs.model else "")!!
                                        )
                                    }
                                })
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        for (i in mApplicationList.indices) {
                            try {
                                if (mApplicationList[i].make != null && mApplicationList[i].make == mSelectedMake) {
                                    val mDatasetResponse = DatasetResponse()
                                    mDatasetResponse.model = mApplicationList[i].model
                                    mDatasetResponse.model_lookup_code = mApplicationList[i].model_lookup_code.nullSafety("")
                                    mDatasetResponse.make = mApplicationList[i].make
                                    mDatasetResponse.makeText = mApplicationList[i].makeText
                                    mModelList!!.add(mDatasetResponse)
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                        var pos = -1

                        if (mModelList != null && mModelList.size > 0) {
                            val mDropdownList = arrayOfNulls<String>(mModelList.size)
                            for (i in mModelList.indices) {
                                mDropdownList[i] = mModelList[i].model.toString()
                                if (value != "") {
                                    if (mModelList[i].make == value || mModelList[i].model == value) {
                                        pos = i

                                    }
                                }
                            }
                            mAutoComTextViewModel?.post {
                                if (pos >= 0 && isAutoSelect == true) {
                                    try {
                                        mAutoComTextViewModel?.setText(mDropdownList[pos])
                                        mSelectedModel = mModelList[pos].model
                                        mSelecteModelLookupCode = mModelList[pos].model_lookup_code
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                }else{
                                    mAutoComTextViewModel?.setText("")
                                }
                                adapterModel = ArrayAdapter(
                                    this@AllReportActivity,
                                    R.layout.row_dropdown_lpr_details_item,
                                    mDropdownList
                                )
                                try {
                                    mAutoComTextViewModel?.threshold = 1
                                    mAutoComTextViewModel?.setAdapter<ArrayAdapter<String?>>(adapterModel)
                                    mAutoComTextViewModel?.onItemClickListener =
                                        OnItemClickListener { parent, view, position, id ->
                                            AppUtils.hideSoftKeyboard(this@AllReportActivity)
                                            val index = getIndexOfModel(
                                                mDropdownList!!,
                                                parent.getItemAtPosition(position).toString()
                                            )
                                            mSelectedModel = mModelList[index].model
                                            mSelecteModelLookupCode = mModelList[index].model_lookup_code
                                            AppUtils.hideSoftKeyboard(this@AllReportActivity)
                                        }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        } else {
                            try {
                                mAutoComTextViewModel?.setText("")
                                mAutoComTextViewModel?.setAdapter(null)
                                adapterModel?.notifyDataSetChanged()
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    private fun getIndexOfModel(list: Array<String?>, name: String): Int {
        var pos = 0
        for (myObj in list!!) {
            if (name.equals(myObj, ignoreCase = true)) return pos
            pos++
        }
        return -1
    }

    //set value to vehicle colour dropdown
    private fun setDropdownVehicleColour() {
        ioScope.launch {
            val mApplicationList = Singleton.getDataSetList(DATASET_CAR_COLOR_LIST, mDb)
            var pos = -1
            if (mApplicationList != null && mApplicationList.size > 0) {
                Collections.sort(mApplicationList, object : Comparator<DatasetResponse?> {
                    override fun compare(lhs: DatasetResponse?, rhs: DatasetResponse?): Int {
                        return (if (lhs?.description != null) lhs.description else "")!!.compareTo((if (rhs?.description != null) rhs.description else "")!!)
                    }
                })
                val mDropdownList = arrayOfNulls<String>(mApplicationList.size)
                for (i in mApplicationList.indices) {
                    mDropdownList[i] = mApplicationList[i].description.toString()
                }
                mAutoComTextViewColor?.post {
                    val adapter = ArrayAdapter(
                        this@AllReportActivity,
                        R.layout.row_dropdown_lpr_details_item,
                        mDropdownList
                    )
                    try {
                        mAutoComTextViewColor?.threshold = 1
                        mAutoComTextViewColor?.setAdapter<ArrayAdapter<String?>>(adapter)
                        mAutoComTextViewColor?.onItemClickListener =
                            OnItemClickListener { parent, view, position, id ->
                                val index: Int =
                                    getIndexOcolor(
                                        mApplicationList,
                                        mAutoComTextViewColor?.text.toString()
                                    )
                                mSelectedColor = mApplicationList[index].color_code
                                AppUtils.hideSoftKeyboard(this@AllReportActivity)
                            }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    private fun getIndexOcolor(list: List<DatasetResponse>, name: String): Int {
        var pos = 0
        for (myObj in list) {
            if (name.equals(myObj.description, ignoreCase = true)) return pos
            pos++
        }
        return 0
    }

    //set value to Lot dropdown.
    private fun setDropdownLot() {
        var pos = -1
        if (mAutoComTextViewLotArea != null) {
            ioScope.launch {
                val mApplicationList = Singleton.getDataSetList(DATASET_LOT_LIST, mDb)

                if (mApplicationList != null && mApplicationList.size > 0) {
                    try {
                        Collections.sort(mApplicationList, object : Comparator<DatasetResponse?> {
                            override fun compare(lhs: DatasetResponse?, rhs: DatasetResponse?): Int {
                                return lhs?.location!!.nullSafety().compareTo(rhs?.location!!.nullSafety())
                            }
                        })
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    val mDropdownList = arrayOfNulls<String>(mApplicationList.size)
                    for (i in mApplicationList.indices) {
                        mDropdownList[i] = mApplicationList[i].location.toString()

                    }
                    mAutoComTextViewLotArea?.post {
                        val adapter = ArrayAdapter(
                            this@AllReportActivity,
                            R.layout.row_dropdown_menu_popup_item,
                            mDropdownList
                        )
                        try {
                            mAutoComTextViewLotArea?.threshold = 1
                            mAutoComTextViewLotArea?.setAdapter<ArrayAdapter<String?>>(adapter)
                            mAutoComTextViewLotArea?.onItemClickListener =
                                OnItemClickListener { parent, view, position, id -> // mSelectedShiftStat = mApplicationList.get(position);
                                    AppUtils.hideSoftKeyboard(this@AllReportActivity)
                                }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }
    }

    //set value to State dropdown
    private fun setDropdownState() {

        if (mAutoComTextViewState != null) {
            ioScope.launch {
                val mApplicationList = Singleton.getDataSetList(DATASET_STATE_LIST, mDb)

                if (mApplicationList != null && mApplicationList.size > 0) {
                    try {
                        Collections.sort(mApplicationList, object : Comparator<DatasetResponse?> {
                            override fun compare(lhs: DatasetResponse?, rhs: DatasetResponse?): Int {
                                return lhs?.state_name.nullSafety()
                                    .compareTo(rhs?.state_name.nullSafety())
                            }
                        })
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    val mDropdownList = arrayOfNulls<String>(mApplicationList.size)

                    var pos = -1
                    for (i in mApplicationList.indices) {
                        mDropdownList[i] = mApplicationList[i].state_name.nullSafety().toString()
                    }
                    mAutoComTextViewState?.post {
                        val adapter = ArrayAdapter(mContext!!,
                            R.layout.row_dropdown_menu_popup_item,
                            mDropdownList)
                        try {
                            mAutoComTextViewState?.threshold = 1
                            mAutoComTextViewState?.setAdapter<ArrayAdapter<String?>>(adapter)
                            //mSelectedShiftStat = mApplicationList.get(pos);
                            mAutoComTextViewState?.onItemClickListener =
                                OnItemClickListener { parent, view, position, id -> // mSelectedShiftStat = mApplicationList.get(position);
                                    AppUtils.hideSoftKeyboard(this@AllReportActivity)
                                    val index = getIndexOfState(
                                        mApplicationList,
                                        parent.getItemAtPosition(position).toString()
                                    )
                                    mState2DigitCode =
                                        mApplicationList[index].state_abbreviated
                                }

                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }
    }

    private fun getIndexOfState(list: List<DatasetResponse>, name: String): Int {
        var pos = 0
        for (myObj in list) {
            if(name.equals(myObj.state_name, ignoreCase = true)) return pos
            pos++
        }
        return -1
    }
    //set value to zone dropdown
    private fun setDropdownZone() {
        try {
            ioScope.launch {
//                val mWelcomeList = Singleton.getWelcomeDbObject(mDb)
//                   val mApplicationListZone =
//                        if (mWelcomeList != null && mWelcomeList.welcomeList != null) mWelcomeList.welcomeList!!.zoneStats else null
//                if (mApplicationListZone != null && mApplicationListZone!!.size > 0) {
//                    val mDropdownList = arrayOfNulls<String>(mApplicationListZone!!.size)
//                    for (i in mApplicationListZone!!.indices) {
//                        mDropdownList[i] = mApplicationListZone!![i].zoneName.toString()
//                    }
                var mDropdownList = ReportArrayClass.mZone2ndForAllReport
                if (fromForm.equals("72hrs Notice To Tow Report", ignoreCase = true)||
                    fromForm.equals("Tow Report", ignoreCase = true)) {
                    mDropdownList = ReportArrayClass.mZone2ndForAllReport
                }else {
                    mDropdownList = ReportArrayClass.mZone2ndForAllReport
                }
                mAutoComTextViewZone?.post {

                    val adapter = ArrayAdapter(
                        this@AllReportActivity,
                        R.layout.row_dropdown_menu_popup_item,
                        mDropdownList
                    )
                    try {
                        mAutoComTextViewZone?.threshold = 1
                        mAutoComTextViewZone?.setAdapter<ArrayAdapter<String?>>(adapter)
                        //mSelectedShiftStat = mApplicationList.get(pos);
                        mAutoComTextViewZone?.onItemClickListener =
                            OnItemClickListener { parent, view, position, id ->
                                AppUtils.hideSoftKeyboard(this@AllReportActivity)

                            }

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
//                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setDropdownPBCZone() {
        ioScope.launch {
            if (mAutoComTextViewPBCZone != null) {
//                var mApplicationList: List<ZoneStat>? = null
//                val mWelcomeList = Singleton.getWelcomeDbObject(mDb)
//                if (mWelcomeList != null) {
//                    mApplicationList = mWelcomeList.welcomeList!!.pbcZoneStats
//                }
//                var pos = -1
//                if (mApplicationList != null && mApplicationList.size > 0) {
//                    val mDropdownList = arrayOfNulls<String>(mApplicationList.size)
//                    for (i in mApplicationList.indices) {
//                        mDropdownList[i] = mApplicationList[i].zoneName.toString()
//                    }

                    var mDropdownList = ReportArrayClass.mZone2ndForAllReport
                if (fromForm.equals("72hrs Notice To Tow Report", ignoreCase = true)||
                    fromForm.equals("Tow Report", ignoreCase = true)) {
                        mDropdownList = ReportArrayClass.mZoneForTowAnd72Hour
                    }else {
                        mDropdownList = ReportArrayClass.mZone2ndForAllReport
                    }
                    //insertFormToDb(true, false, null);
//                Arrays.sort(mDropdownList)
                    mAutoComTextViewPBCZone?.post {
                        val adapter = ArrayAdapter(
                            this@AllReportActivity,
                            R.layout.row_dropdown_menu_popup_item,
                            mDropdownList
                        )
                        try {
                            mAutoComTextViewPBCZone?.threshold = 1
                            mAutoComTextViewPBCZone?.setAdapter<ArrayAdapter<String?>>(adapter)
                            mAutoComTextViewPBCZone?.onItemClickListener =
                                OnItemClickListener { parent, view, position, id ->
                                    AppUtils.hideSoftKeyboard(this@AllReportActivity)
//                                mAutoComTextViewMeterName!!.setText(mAutoComTextViewPBCZone!!.text.toString())
                                }
                            // listonly
                            if (mAutoComTextViewPBCZone?.tag != null && mAutoComTextViewPBCZone?.tag == "listonly") {
                                AppUtils.setListOnly(
                                    this@AllReportActivity,
                                    mAutoComTextViewPBCZone!!
                                )
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
//                }
            }
        }
    }

//    private fun getIndexOfCityZone(list: List<ZoneStat>, name: String): Int {
//        var pos = 0
//        for (myObj in list) {
//            if (name.equals(myObj.zoneName, ignoreCase = true)) return pos
//            pos++
//        }
//        return -1
//    }

    private fun setSignatureView() {
        //Util.hideKeyBoard(getActivity());
        // custom dialog
        val dialog = Dialog(this@AllReportActivity)
        dialog.setContentView(R.layout.dialog_signature)
        dialog.setTitle("Title...")

        // set the custom dialog components - text, image and button
        val signatureView = dialog.findViewById<View>(R.id.signature_view) as SignaturePad
        val clear = dialog.findViewById<View>(R.id.clear) as Button
        val save = dialog.findViewById<View>(R.id.save) as Button
        val imgCancel = dialog.findViewById<View>(R.id.imgCancel) as ImageView
        clear.setOnClickListener { signatureView.clear() }
        save.setOnClickListener {
            dialog.dismiss()
//            imageViewSignature.setImageBitmap(signatureView.signatureBitmap)
//            SaveSignature(signatureView.signatureBitmap)
            SaveImageMM(signatureView.getSignatureBitmap())
        }
        imgCancel.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }
}