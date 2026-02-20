//package com.parkloyalty.lpr.scan.ui.login
//
//import android.Manifest
//import android.annotation.SuppressLint
//import android.app.Activity
//import android.app.ActivityManager
//import android.app.DatePickerDialog
//import android.content.Context
//import android.content.Intent
//import android.content.pm.PackageManager
//import android.content.pm.Signature
//import android.net.Uri
//import android.os.AsyncTask
//import android.os.Build
//import android.os.Bundle
//import android.os.Environment
//import android.os.Handler
//import android.os.Looper
//import android.os.Process
//import android.provider.Settings
//import android.telephony.TelephonyManager
//import android.text.TextUtils
//import android.util.Base64
//import android.util.Log
//import android.view.View
//import android.view.animation.AlphaAnimation
//import android.widget.AdapterView.OnItemClickListener
//import android.widget.ArrayAdapter
//import android.widget.TextView
//import android.widget.Toast
//import androidx.activity.viewModels
//import androidx.annotation.RequiresApi
//import androidx.appcompat.app.AppCompatActivity
//import androidx.appcompat.widget.AppCompatAutoCompleteTextView
//import androidx.appcompat.widget.AppCompatButton
//import androidx.appcompat.widget.AppCompatEditText
//import androidx.appcompat.widget.AppCompatImageView
//import androidx.appcompat.widget.AppCompatTextView
//import androidx.appcompat.widget.SwitchCompat
//import androidx.core.app.ActivityCompat
//import androidx.core.view.WindowCompat
//import androidx.core.view.WindowInsetsCompat
//import androidx.core.view.WindowInsetsControllerCompat
//import androidx.lifecycle.Observer
//import androidx.lifecycle.lifecycleScope
//import com.google.android.gms.common.api.ApiException
//import com.google.android.gms.safetynet.SafetyNet
//import com.google.android.material.textfield.TextInputLayout
//import com.parkloyalty.lpr.scan.BuildConfig
//import com.parkloyalty.lpr.scan.R
//import com.parkloyalty.lpr.scan.applanguage.LanguageHelper
//import com.parkloyalty.lpr.scan.basecontrol.BaseActivity
//import com.parkloyalty.lpr.scan.basecontrol.BaseApplication
//import com.parkloyalty.lpr.scan.common.model.LocUpdateRequest
//import com.parkloyalty.lpr.scan.database.AppDatabase
//import com.parkloyalty.lpr.scan.database.Singleton
//import com.parkloyalty.lpr.scan.databinding.ActivityLoginBinding
//import com.parkloyalty.lpr.scan.extensions.DuncanBrandingApp13
//import com.parkloyalty.lpr.scan.extensions.nullSafety
//import com.parkloyalty.lpr.scan.interfaces.APIConstant
//import com.parkloyalty.lpr.scan.interfaces.Constants
//import com.parkloyalty.lpr.scan.interfaces.Constants.Companion.FILE_NAME_FACSIMILE_PRINT_BITMAP
//import com.parkloyalty.lpr.scan.interfaces.CustomDialogHelper
//import com.parkloyalty.lpr.scan.interfaces.SharedPrefKey
//import com.parkloyalty.lpr.scan.network.ApiLogsClass
//import com.parkloyalty.lpr.scan.network.ApiResponse
//import com.parkloyalty.lpr.scan.network.DynamicAPIPath
//import com.parkloyalty.lpr.scan.network.NetworkCheck.isInternetAvailable
//import com.parkloyalty.lpr.scan.network.Status
//import com.parkloyalty.lpr.scan.ui.check_setup.model.lpr_model.UploadImageViewModel
//import com.parkloyalty.lpr.scan.ui.check_setup.model.lpr_model.UploadImagesResponse
//import com.parkloyalty.lpr.scan.ui.check_setup.model.municipalcitation.CreateMunicipalCitationTicketRequest
//import com.parkloyalty.lpr.scan.ui.check_setup.model.municipalcitation.CreateMunicipalCitationTicketResponse
//import com.parkloyalty.lpr.scan.ui.check_setup.model.municipalcitation.CreateMunicipalCitationTicketViewModel
//import com.parkloyalty.lpr.scan.ui.check_setup.model.municipalcitation.MunicipalCitationCommentsDetails
//import com.parkloyalty.lpr.scan.ui.check_setup.model.municipalcitation.MunicipalCitationHeaderDetails
//import com.parkloyalty.lpr.scan.ui.check_setup.model.municipalcitation.MunicipalCitationInvoiceFeeStructure
//import com.parkloyalty.lpr.scan.ui.check_setup.model.municipalcitation.MunicipalCitationLayoutResponse
//import com.parkloyalty.lpr.scan.ui.check_setup.model.municipalcitation.MunicipalCitationLocationDetails
//import com.parkloyalty.lpr.scan.ui.check_setup.model.municipalcitation.MunicipalCitationOfficerDetails
//import com.parkloyalty.lpr.scan.ui.check_setup.model.municipalcitation.MunicipalCitationVehicleDetails
//import com.parkloyalty.lpr.scan.ui.check_setup.model.municipalcitation.MunicipalCitationViolationDetails
//import com.parkloyalty.lpr.scan.ui.check_setup.model.ticket.CommentsDetails
//import com.parkloyalty.lpr.scan.ui.check_setup.model.ticket.CreateTicketRequest
//import com.parkloyalty.lpr.scan.ui.check_setup.model.ticket.CreateTicketResponse
//import com.parkloyalty.lpr.scan.ui.check_setup.model.ticket.CreateTicketViewModel
//import com.parkloyalty.lpr.scan.ui.check_setup.model.ticket.HeaderDetails
//import com.parkloyalty.lpr.scan.ui.check_setup.model.ticket.InvoiceFeeStructure
//import com.parkloyalty.lpr.scan.ui.check_setup.model.ticket.LocationDetails
//import com.parkloyalty.lpr.scan.ui.check_setup.model.ticket.MotoristDetailsModel
//import com.parkloyalty.lpr.scan.ui.check_setup.model.ticket.OfficerDetails
//import com.parkloyalty.lpr.scan.ui.check_setup.model.ticket.VehicleDetails
//import com.parkloyalty.lpr.scan.ui.check_setup.model.ticket.ViolationDetails
//import com.parkloyalty.lpr.scan.ui.login.DatasetModel.ActivityLayoutModel
//import com.parkloyalty.lpr.scan.ui.login.DatasetModel.ActivityLayoutResponse
//import com.parkloyalty.lpr.scan.ui.login.DatasetModel.AuthRefreshResponse
//import com.parkloyalty.lpr.scan.ui.login.DatasetModel.AuthTokenRefreshViewModel
//import com.parkloyalty.lpr.scan.ui.login.DatasetModel.CitationBookletDatabaseModel
//import com.parkloyalty.lpr.scan.ui.login.DatasetModel.CitationBookletModel
//import com.parkloyalty.lpr.scan.ui.login.DatasetModel.CitationDatasetModel
//import com.parkloyalty.lpr.scan.ui.login.DatasetModel.CitationImageModelOffline
//import com.parkloyalty.lpr.scan.ui.login.DatasetModel.CitationInsurranceDatabaseModel
//import com.parkloyalty.lpr.scan.ui.login.DatasetModel.CitationLayoutResponse
//import com.parkloyalty.lpr.scan.ui.login.DatasetModel.CitationLayoutViewModel
//import com.parkloyalty.lpr.scan.ui.login.DatasetModel.CitationNumberData
//import com.parkloyalty.lpr.scan.ui.login.DatasetModel.CitationNumberDatabaseModel
//import com.parkloyalty.lpr.scan.ui.login.DatasetModel.CitationNumberModel
//import com.parkloyalty.lpr.scan.ui.login.DatasetModel.CitationNumberRequest
//import com.parkloyalty.lpr.scan.ui.login.DatasetModel.CitationNumberResponse
//import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DropdownDatasetRequest
//import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DropdownDatasetResponse
//import com.parkloyalty.lpr.scan.ui.login.DatasetModel.TimingLayoutResponse
//import com.parkloyalty.lpr.scan.ui.login.activity.ForgetPasswordActivity
//import com.parkloyalty.lpr.scan.ui.login.activity.WelcomeActivity
//import com.parkloyalty.lpr.scan.ui.login.model.CommonLoginResponse
//import com.parkloyalty.lpr.scan.ui.login.model.HearingTimeResponse
//import com.parkloyalty.lpr.scan.ui.login.model.LoginViewModel
//import com.parkloyalty.lpr.scan.ui.login.model.ResponseItemHearingTime
//import com.parkloyalty.lpr.scan.ui.login.model.ShiftResponse
//import com.parkloyalty.lpr.scan.ui.login.model.ShiftStat
//import com.parkloyalty.lpr.scan.ui.login.model.SiteOfficerLoginRequest
//import com.parkloyalty.lpr.scan.ui.login.model.TimestampDatatbase
//import com.parkloyalty.lpr.scan.ui.login.model.UpdateTimeData
//import com.parkloyalty.lpr.scan.ui.login.model.UpdateTimeDataList
//import com.parkloyalty.lpr.scan.ui.login.model.UpdateTimeDb
//import com.parkloyalty.lpr.scan.ui.login.model.UpdateTimeResponse
//import com.parkloyalty.lpr.scan.ui.login.model.UpdateTimeViewModel
//import com.parkloyalty.lpr.scan.ui.login.model.WelcomeForm
//import com.parkloyalty.lpr.scan.ui.printer.SettingsHelper
//import com.parkloyalty.lpr.scan.ui.ticket.model.TicketUploadStatusRequest
//import com.parkloyalty.lpr.scan.ui.ticket.model.TicketUploadStatusViewModel
//import com.parkloyalty.lpr.scan.util.AppUtils
//import com.parkloyalty.lpr.scan.util.AppUtils.deleteLprContinousModeFolder
//import com.parkloyalty.lpr.scan.util.AppUtils.getClientTimeStamp
//import com.parkloyalty.lpr.scan.util.AppUtils.getDeviceId
//import com.parkloyalty.lpr.scan.util.AppUtils.getSiteId
//import com.parkloyalty.lpr.scan.util.AppUtils.hideSoftKeyboard
//import com.parkloyalty.lpr.scan.util.AppUtils.setListOnly
//import com.parkloyalty.lpr.scan.util.AppUtils.showCustomAlertDialog
//import com.parkloyalty.lpr.scan.util.AppUtils.splitDateLpr
//import com.parkloyalty.lpr.scan.util.AppUtils.splitDoller
//import com.parkloyalty.lpr.scan.util.DATASET_SETTINGS_LIST
//import com.parkloyalty.lpr.scan.util.Device
//import com.parkloyalty.lpr.scan.util.FileUtil.getHeaderFooterImageFileFullPath
//import com.parkloyalty.lpr.scan.util.LockLprModel
//import com.parkloyalty.lpr.scan.util.LogUtil
//import com.parkloyalty.lpr.scan.util.LogUtil.printLog
//import com.parkloyalty.lpr.scan.util.LogUtil.printToastMSG
//import com.parkloyalty.lpr.scan.util.PermissionUtils
//import com.parkloyalty.lpr.scan.util.SystemUtils
//import com.parkloyalty.lpr.scan.util.SystemUtils.whitelistAppFromBatteryOptimization
//import com.parkloyalty.lpr.scan.util.Util
//import com.parkloyalty.lpr.scan.util.Util.setTimeDataList
//import com.parkloyalty.lpr.scan.util.setAccessibilityForTextInputLayoutDropdownButtons
//import com.parkloyalty.lpr.scan.util.setAccessibilityRoleAsAction
//import com.parkloyalty.lpr.scan.util.setCustomAccessibility
//import com.parkloyalty.lpr.scan.util.setDoNothingAccessibilityForTextInputLayoutDropdownButtons
//import com.parkloyalty.lpr.scan.utils.ObjectMapperProvider
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.withContext
//import okhttp3.MediaType.Companion.toMediaTypeOrNull
//import okhttp3.MultipartBody
//import okhttp3.RequestBody
//import org.json.JSONObject
//import java.io.BufferedReader
//import java.io.File
//import java.io.FileInputStream
//import java.io.FileNotFoundException
//import java.io.FileOutputStream
//import java.io.FileWriter
//import java.io.IOException
//import java.io.InputStream
//import java.io.InputStreamReader
//import java.io.OutputStream
//import java.net.HttpURLConnection
//import java.net.URL
//import java.security.MessageDigest
//import java.security.NoSuchAlgorithmException
//import java.text.SimpleDateFormat
//import java.util.Calendar
//import java.util.Date
//import java.util.Locale
//import java.util.UUID
//import dagger.hilt.android.AndroidEntryPoint
//
//@AndroidEntryPoint
//class LoginActivity : BaseActivity(), CustomDialogHelper {
//
//    private var mTextInputEmail: TextInputLayout? = null
//    private var mEditTextEmail: AppCompatEditText? = null
//    private var mTextInputPassword: TextInputLayout? = null
//    private var mEditTextPassword: AppCompatEditText? = null
//    private var appCompatEditTextVersionName: AppCompatTextView? = null
//    private var appCompatEditTextSiteName: AppCompatTextView? = null
//    private var appCompatEditTextCompanyName: AppCompatTextView? = null
//    private var mTextInputShift: TextInputLayout? = null
//    private var mAutoComTextViewShift: AppCompatAutoCompleteTextView? = null
//    private var mAutoComTextViewHearingDate: AppCompatAutoCompleteTextView? = null
//    private var mAutoComTextViewHearingTime: AppCompatAutoCompleteTextView? = null
//    private var mTextInputLayoutDate: TextInputLayout? = null
//    private var mTextInputLayoutTime: TextInputLayout? = null
//    private var mLoginButton: AppCompatButton? = null
//    private var mDataSetSyncButton: AppCompatImageView? = null
//    private var mCitationSyncButton: AppCompatImageView? = null
//    private var mSwitchCompatSunMode: SwitchCompat? = null
//
//    private lateinit var binding: ActivityLoginBinding
//
//
//    private var mUsername = ""
//    private var mPassword = ""
//    private var mUUID = ""
//    private var mInvalidPassCount = 0
//    private var mCitationNumberId: String? = null
//    private var mContext: Context? = null
//    private var imageUploadSuccessCount = 0
//    private var responseModelLogin:CommonLoginResponse? = null
//
//    //KEYS for Captcha
//    //    private String SITE_KEY = "6Lfv-tIaAAAAAIPj_da2nHPpuet6_MwY5-3CWGK1"; // "6LcW0UwUAAAAAFQfFY1a-7AxSZxNSu0usk2JwhLC";
//    //    private String SECRET_KEY = "6Lfv-tIaAAAAAKPQuhc9ZYe53ZajBgTo16hkT5Oe";//"6LcW0UwUAAAAAL4zYZyigh97pa73rz6jxmrSJR-a";//
//    //   najib private String SITE_KEY = "6LejD5QcAAAAAJ3ARLYK8HTuLhv_XvaTogxE7mTf";  // "6LcW0UwUAAAAAFQfFY1a-7AxSZxNSu0usk2JwhLC";//6Lfv-tIaAAAAAIPj_da2nHPpuet6_MwY5-3CWGK1
//    //   najib private String SECRET_KEY = "6LejD5QcAAAAAPnugITVcMzST-XjLhe2iQIJ8yZ4"; //"6LcW0UwUAAAAAL4zYZyigh97pa73rz6jxmrSJR-a";//6Lfv-tIaAAAAAKPQuhc9ZYe53ZajBgTo16hkT5Oe
//    //Last Used
//    //    private String SITE_KEY = "6LehlLAcAAAAAOWuW3qLLbUKE53Wqe2oCmvtFHRk";
//    //    private String SECRET_KEY = "6LehlLAcAAAAABpg0xRNC7R1Hil-qGAS1vK0OLuE";
//    var userResponseToken: String? = null
//    private var mDb: AppDatabase? = null
//    private var mResponseEntity: UpdateTimeData? = null
//    private var timeDataList: UpdateTimeDataList? = null
//    private var mTimeResponse: UpdateTimeResponse? = null
//    private var mTimingDatabaseList: TimestampDatatbase? = null
//    private var numberOfAPI = 0
//    private var offlineCitationImagesList: List<CitationImageModelOffline>? = null
//    private var offlineCitationData: CitationInsurranceDatabaseModel? = null
//    private val mImages: MutableList<String> = ArrayList()
//
//    private val mCreateTicketViewModel: CreateTicketViewModel? by viewModels()
//    private val mCreateMunicipalCitationTicketViewModel: CreateMunicipalCitationTicketViewModel? by viewModels()
//    private val mLoginViewModel: LoginViewModel? by viewModels()
//    private val mUpdateTimeViewModel: UpdateTimeViewModel? by viewModels()
//    private val mCitationNumberModel: CitationNumberModel? by viewModels()
//    private val mCitationLayoutViewModel: CitationLayoutViewModel? by viewModels()
//    private val mCitationDatasetModel: CitationDatasetModel? by viewModels()
//    private val mAuthTokenRefreshViewModel: AuthTokenRefreshViewModel? by viewModels()
//    private val activityLayoutModel: ActivityLayoutModel? by viewModels()
//    private val mUploadImageViewModel: UploadImageViewModel? by viewModels()
//    private val mTicketStatusViewModel: TicketUploadStatusViewModel? by viewModels()
//
//    var currentLan = "en"
//    val languageHelper by lazy {
//        LanguageHelper()
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityLoginBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//        findViewsByViewBinding()
//        setupClickListeners()
//
//        Singleton.reset()
//        setFullScreenUI()
//        mContext = this
//        mDb = BaseApplication.instance?.getAppDatabase()
//        currentLan = languageHelper.getLanguageCode(applicationContext)
////        languageHelper.changeLanguage(applicationContext,"EN")
//        addObservers()
//        init()
//
//        if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CHARLESTON, true) ||
//            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MEMORIALHERMAN, true) ||
//            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKTEXAS, true) ||
//            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOTTLEWORKS_IN, true) ||
//            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKPARKING, true) ||
//            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PARKX, true) ||
//            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RUTGERS, true) ||
//            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SEPTA, true) ||
//            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_COHASSET, true) ||
//            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITYOFSANDIEGO, true)
//        ) {
//            mTextInputShift!!.visibility = View.GONE
//        }
//
//        try {
//            SettingsHelper.saveBluetoothAddress(this@LoginActivity, "")
//            SettingsHelper.saveIp(this@LoginActivity, "")
//            SettingsHelper.savePort(this@LoginActivity, "")
////            sharedPreference.writeInt(SharedPrefKey.QRCODE_SCAN_COUNT,0)
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//
//        mDb?.dbDAO?.deleteQrCodeInventoryTable()
//        mDb?.dbDAO?.deleteInventoryToShowTable()
//        clearShardValue()
//        if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ORLEANS, ignoreCase = true)) {
//            mTextInputLayoutTime!!.visibility = View.VISIBLE
//            mTextInputLayoutDate!!.visibility = View.VISIBLE
//            mAutoComTextViewHearingDate?.setOnClickListener {
//                hideSoftKeyboard(this@LoginActivity)
//                openDataPicker(mAutoComTextViewHearingDate)
//            }
//        }
//
//        try {
//            mUUID = getDeviceId(this)
//            printLog("UUID", mUUID)
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//
//        setCrossClearButtonForAllFields()
//        setAccessibilityForComponents()
//
//        /**
//         * this code only for ppa build release in emergency purpose need to remove
//         */
//        try {
//            mDb!!.dbDAO!!.deleteTimeStampTable()
//            mDb?.dbDAO?.deleteAllDataSet()
//            mDb?.dbDAO?.deleteActivityList()
//
//        } catch(e: Exception) {
//            e.printStackTrace()
//        }
//    }
//    private fun findViewsByViewBinding() {
//        mTextInputEmail = binding.layoutContentLogin.inputEmail
//        mEditTextEmail = binding.layoutContentLogin.etEmail
//        mTextInputPassword = binding.layoutContentLogin.inputPassword
//        mEditTextPassword = binding.layoutContentLogin.etPassword
//        appCompatEditTextVersionName = binding.layoutContentLogin.textviewVersionName
//        appCompatEditTextSiteName = binding.layoutContentLogin.textviewSiteName
//        appCompatEditTextCompanyName = binding.layoutHeaderLogin.textviewCmpName
//        mTextInputShift = binding.layoutContentLogin.inputShift
//        mAutoComTextViewShift = binding.layoutContentLogin.AutoComTextViewVehShift
//        mAutoComTextViewHearingDate = binding.layoutContentLogin.AutoComTextViewHearingDate
//        mAutoComTextViewHearingTime = binding.layoutContentLogin.AutoComTextViewHearingTime
//        mTextInputLayoutDate = binding.layoutContentLogin.inputHearingDate
//        mTextInputLayoutTime = binding.layoutContentLogin.inputHearingTime
//        mLoginButton = binding.layoutContentLogin.btnLogin
//        mDataSetSyncButton = binding.layoutContentLogin.acivSyncDataset
//        mCitationSyncButton = binding.layoutContentLogin.acivSyncCitation
//        mSwitchCompatSunMode = binding.layoutContentLogin.toggleNight
//    }
//
//    private fun setupClickListeners() {
//        mLoginButton?.setOnClickListener {
//            if (isDetailsValid()) {
//                if (mLoginButton!!.isEnabled) {
//                    mLoginButton!!.isEnabled = false
//                    val alpha = 0.45f
//                    val alphaUp = AlphaAnimation(alpha, alpha)
//                    alphaUp.fillAfter = true
//                    mLoginButton!!.startAnimation(alphaUp)
//                    showCaptcha()
//                }
//            }
//        }
//
//        binding.layoutContentLogin.tvForgotPass.setOnClickListener {
//            launchScreen(
//                this@LoginActivity,
//                ForgetPasswordActivity::class.java
//            )
//        }
//
//        mDataSetSyncButton?.setOnClickListener {
//            removeCachedHeaderFooterImage()
//
//            val alpha = 0.45f
//            val alphaUp = AlphaAnimation(alpha, alpha)
//            alphaUp.fillAfter = true
//            mDataSetSyncButton!!.startAnimation(alphaUp)
//            mDb!!.dbDAO!!.deleteTimeStampTable()
//            mDb?.dbDAO?.deleteAllDataSet()
//            mDb?.dbDAO?.deleteActivityList()
//
//
//            val intent = Intent(this@LoginActivity, LoginActivity::class.java)
//            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
//            startActivity(intent)
//
//            Process.killProcess(Process.myPid())
//        }
//
//        mCitationSyncButton?.setOnClickListener {
//            val alpha = 0.45f
//            val alphaUp = AlphaAnimation(alpha, alpha)
//            alphaUp.fillAfter = true
//            mCitationSyncButton!!.startAnimation(alphaUp)
//        }
//
//        mSwitchCompatSunMode?.setOnCheckedChangeListener { _, isChecked ->
//            sharedPreference.write(
//                SharedPrefKey.IS_SUN_LIGHT_MODE_ACTIVE, isChecked
//            )
//            if (isChecked) {
//                AppUtils.isSunLightMode = true
//                sunLightMode(this@LoginActivity, mTextInputShift, null, mAutoComTextViewShift!!)
//                sunLightMode(this@LoginActivity, mTextInputEmail, mEditTextEmail!!, null)
////                sunLightMode(this@LoginActivity,mTextInputPassword!!,mEditTextPassword!!,null!!)
//                Toast.makeText(this@LoginActivity, "ON", Toast.LENGTH_SHORT).show()
//            } else {
//                AppUtils.isSunLightMode = false
//                MoonLightMode(this@LoginActivity, mTextInputShift, null, mAutoComTextViewShift!!)
//                Toast.makeText(this@LoginActivity, "OFF", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//
//    private fun setCrossClearButtonForAllFields(){
//        setCrossClearButton(
//            context = this@LoginActivity,
//            textInputLayout = mTextInputEmail,
//            appCompatEditText = mEditTextEmail
//        )
//
//        setCrossClearButton(
//            context = this@LoginActivity,
//            textInputLayout = mTextInputPassword,
//            appCompatEditText = mEditTextPassword
//        )
//
//        setCrossClearButton(
//            context = this@LoginActivity,
//            textInputLayout = mTextInputShift,
//            appCompatAutoCompleteTextView = mAutoComTextViewShift
//        )
//
//        setCrossClearButton(
//            context = this@LoginActivity,
//            textInputLayout = mTextInputLayoutDate,
//            appCompatAutoCompleteTextView = mAutoComTextViewHearingDate
//        )
//
//        setCrossClearButton(
//            context = this@LoginActivity,
//            textInputLayout = mTextInputLayoutTime,
//            appCompatAutoCompleteTextView = mAutoComTextViewHearingTime
//        )
//    }
//
//    private fun setAccessibilityForComponents(){
//        setAccessibilityForTextInputLayoutDropdownButtons(this@LoginActivity, mTextInputShift)
//        setAccessibilityForTextInputLayoutDropdownButtons(this@LoginActivity, mTextInputLayoutTime)
//        setDoNothingAccessibilityForTextInputLayoutDropdownButtons(this@LoginActivity, mTextInputLayoutDate)
//
//        mDataSetSyncButton?.setCustomAccessibility(contentDescription = getString(R.string.ada_content_description_sync), role = getString(R.string.ada_role_button), actionLabel = getString(R.string.ada_action_reset_and_reload))
//        appCompatEditTextCompanyName?.setCustomAccessibility( role = getString(R.string.ada_role_label))
//        binding.layoutContentLogin.tvForgotPass.setAccessibilityRoleAsAction(role = getString(R.string.ada_role_link))
//    }
//
//    override fun onResume() {
//        onResumeItems()
//        super.onResume()
//    }
//
//    private fun onResumeItems(){
//        numberOfAPI = 0
//        setBuildVersion()
//
////        writeFileOnInternalStorage(this, "Login Screen")
//        callCitationDatasetApiShift("ShiftList")
//        callCitationDatasetApiShift("HearingTimeList")
//        try {
//            deleteLprContinousModeFolder(this)
//        } catch (e: java.lang.Exception) {
//            e.printStackTrace()
//        }
//        try {
//            deleteAllPhotosByModayLogin()
//        } catch (e: java.lang.Exception) {
//            e.printStackTrace()
//        }
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !Environment.isExternalStorageManager()) {
//            val uri = Uri.parse("package:${BuildConfig.APPLICATION_ID}")
//            startActivity(
//                Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
//                    uri))
//
//        } else {
////                TODO("VERSION.SDK_INT < R")
//        }
//
//        requestPermission()
//    }
//
//    private val createTicketResponseObserver = Observer { apiResponse: ApiResponse ->
//        consumeResponse(apiResponse,DynamicAPIPath.POST_CREATE_TICKET)
//    }
//
//    private val createMunicipalCitationTicketResponseObserver = Observer { apiResponse: ApiResponse ->
//        consumeResponse(apiResponse,DynamicAPIPath.POST_CREATE_MUNICIPAL_CITATION_TICKET)
//    }
//
//
//    private val uploadImageResponseObserver = Observer { apiResponse: ApiResponse ->
//        consumeResponse(apiResponse,DynamicAPIPath.POST_IMAGE)
//    }
//
//
//    private val loginResponseObserver =
//        Observer { apiResponse: ApiResponse -> consumeResponse(apiResponse, TAG) }
//
//    private val citationNumberResponseObserver = Observer { apiResponse: ApiResponse ->
//        consumeResponse(apiResponse,DynamicAPIPath.POST_CITATION_NUMBER)
//    }
//    private val citationLayoutResponseObserver = Observer { apiResponse: ApiResponse ->
//        consumeResponse(apiResponse,DynamicAPIPath.GET_CITATION_LAYOUT)
//    }
//
//    private val municipalCitationLayoutResponseObserver = Observer { apiResponse: ApiResponse ->
//        consumeResponse(apiResponse,DynamicAPIPath.GET_MUNICIPAL_CITATION_LAYOUT)
//    }
//    private val activityLayoutResponseObserver = Observer { apiResponse: ApiResponse ->
//        consumeResponse(apiResponse,DynamicAPIPath.GET_CITATION_LAYOUT + "TIMEACTIVITY")
//    }
//    private val updateTimeResponseObserver = Observer { apiResponse: ApiResponse ->
//        consumeResponse(apiResponse,DynamicAPIPath.GET_UPDATE_TIME)
//    }
//    private val citationDatasetResponseObserver = Observer { apiResponse: ApiResponse ->
//        consumeResponse(apiResponse,DynamicAPIPath.POST_CITATION_DATASET)
//    }
//    private val authTokenRefreshResponseObserver = Observer { apiResponse: ApiResponse ->
//        consumeResponse(apiResponse,DynamicAPIPath.GET_REFRESH_AUTH_TOKEN)
//    }
//
//    private val ticketStatusResponseObserver = Observer { apiResponse: ApiResponse ->
//        consumeResponse(apiResponse,DynamicAPIPath.POST_TICKET_UPLOADE_STATUS_META)
//    }
//
//    private fun addObservers() {
//        mCreateTicketViewModel!!.response.observe(this, createTicketResponseObserver)
//        mCreateMunicipalCitationTicketViewModel!!.response.observe(this, createMunicipalCitationTicketResponseObserver)
//        mUploadImageViewModel?.response?.observe(this, uploadImageResponseObserver)
//        mLoginViewModel!!.response.observe(this, loginResponseObserver)
//        mCitationNumberModel!!.response.observe(this, citationNumberResponseObserver)
//        mCitationLayoutViewModel!!.response.observe(this, citationLayoutResponseObserver)
//        mCitationLayoutViewModel!!.municipalCitationResponse.observe(this, municipalCitationLayoutResponseObserver)
//        activityLayoutModel!!.response.observe(this, activityLayoutResponseObserver)
//        mUpdateTimeViewModel!!.response.observe(this, updateTimeResponseObserver)
//        mCitationDatasetModel!!.response.observe(this, citationDatasetResponseObserver)
//        mAuthTokenRefreshViewModel!!.response.observe(this, authTokenRefreshResponseObserver)
//        mTicketStatusViewModel?.response?.observe(this, ticketStatusResponseObserver)
//    }
//
//    override fun removeObservers() {
//        super.removeObservers()
//        mCreateTicketViewModel!!.response.removeObserver(createTicketResponseObserver)
//        mCreateMunicipalCitationTicketViewModel!!.response.removeObserver(createMunicipalCitationTicketResponseObserver)
//        mUploadImageViewModel?.response?.observe(this, uploadImageResponseObserver)
//        mLoginViewModel!!.response.removeObserver(loginResponseObserver)
//        mCitationNumberModel!!.response.removeObserver(citationNumberResponseObserver)
//        mCitationLayoutViewModel!!.response.removeObserver(citationLayoutResponseObserver)
//        mCitationLayoutViewModel!!.municipalCitationResponse.removeObserver(municipalCitationLayoutResponseObserver)
//        activityLayoutModel!!.response.removeObserver(activityLayoutResponseObserver)
//        mUpdateTimeViewModel!!.response.removeObserver(updateTimeResponseObserver)
//        mCitationDatasetModel!!.response.removeObserver(citationDatasetResponseObserver)
//        mAuthTokenRefreshViewModel!!.response.removeObserver(authTokenRefreshResponseObserver)
//        mTicketStatusViewModel?.response?.removeObserver(ticketStatusResponseObserver)
//    }
//
//    private fun init() {
//        mEventStartTimeStamp = AppUtils.getDateTime()
//
//        setErrorMSG()
//        //init user credential
//        //TODO fetch valued from api or database
//        if (BuildConfig.DEBUG && LogUtil.isEnableAPILogs) {
//            mUsername = "sbreyer" //phili
//            mPassword = "breyer123" //phili
////            mUsername = "nicklat"; //rise tek
////            mPassword .= "password987";// rise tek
////            mUsername = "ssotomayor"
////            mPassword = "sotomayor123"
//
//            mEditTextEmail!!.setText(mUsername)
//            mEditTextPassword!!.setText(mPassword)
//        }
//        //get invalid credential coun
//        mInvalidPassCount = sharedPreference.read(SharedPrefKey.IS_INVALID_PASSWORD, "0")!!.toInt()
//    }
//
//    //configuring captch
//    fun showCaptcha() {
//        SafetyNet.getClient(this@LoginActivity).verifyWithRecaptcha(BuildConfig.CAPTCHA_SITE_KEY)
//            .addOnSuccessListener(
//                this@LoginActivity as AppCompatActivity
//            ) { response -> // Indicates communication with reCAPTCHA service was
//                // successful.
//                userResponseToken = response.tokenResult
//                if (userResponseToken?.isNotEmpty()!!) {
//                    handleSiteVerify().execute()
//                }
//            }
//            .addOnFailureListener(this@LoginActivity) { e ->
//                if (e is ApiException) {
//                    // An error occurred when communicating with the
//                    // reCAPTCHA service. Refer to the status code to
//                    // handle the error appropriately.
//                    val statusCode = e.statusCode
//                } else {
//                    // A different, unknown type of error occurred.
//                    Log.d(TAG, "Error: " + e.message)
//                }
//                callLoginUserApi()
//            }
//    }
//
//    override fun onYesButtonClick() {}
//    override fun onNoButtonClick() {}
//    override fun onYesButtonClickParam(msg: String?) {}
//
//    //configuring captch
//    inner class handleSiteVerify : AsyncTask<String?, Void?, String?>() {
//        var mContext: Context = this@LoginActivity
//        override fun onPreExecute() {
//            super.onPreExecute()
//            showProgressLoader(getString(R.string.scr_message_please_wait))
//        }
//
//        override fun doInBackground(vararg strings: String?): String? {
//            var isSuccess = ""
//            var `is`: InputStream? = null
//            val API = "https://www.google.com/recaptcha/api/siteverify?"
//            val newAPI =
//                API + "secret=" + BuildConfig.CAPTCHA_SECRET_KEY + "&response=" + userResponseToken
//            Log.d(TAG, " API  $newAPI")
//            try {
//                val url = URL(newAPI)
//                val httpURLConnection = url.openConnection() as HttpURLConnection
//                /*httpURLConnection.setReadTimeout(18000 */ /* milliseconds */ /*);
//                httpURLConnection.setConnectTimeout(14000 */
//                /* milliseconds */ /*);
//                 */httpURLConnection.requestMethod = "GET"
//                httpURLConnection.doInput = true
//                // Starts the query
//                httpURLConnection.connect()
//                val response = httpURLConnection.responseCode
//                dismissLoader()
//                println(response)
//                `is` = httpURLConnection.inputStream
//                val bufferedReader = BufferedReader(InputStreamReader(`is`))
//                val stringBuilder = StringBuilder()
//                var line: String?
//                while (bufferedReader.readLine().also { line = it } != null) {
//                    stringBuilder.append(line).append("\n")
//                }
//                val result = stringBuilder.toString()
//                Log.d("Api", result)
//                try {
//                    val jsonObject = JSONObject(result)
//                    println("Result Object :  $jsonObject")
//                    isSuccess = jsonObject.getString("success")
//                    println("obj $isSuccess")
//                } catch (e: Exception) {
//                    isSuccess = "exception"
//                    Log.d("Exception: ", e.message!!)
//                    e.printStackTrace()
//                    dismissLoader()
//                }
//            } catch (e: Exception) {
//                isSuccess = "exception"
//                e.printStackTrace()
//                dismissLoader()
//            }
//            return isSuccess
//        }
//
//        override fun onPostExecute(s: String?) {
//            super.onPostExecute(s)
//            if (s.equals("exception", ignoreCase = true)) {
//            }
//            try {
//                if (s != null) {
//                    when (s) {
//                        "true" -> {
//                            callLoginUserApi()
//                            return
//                        }
//                        "exception" -> {
//                            printToastMSG(mContext, "Something went wrong, Please Try again.")
//                            return
//                        }
//                        "socketexception" -> {
//                            printToastMSG(mContext, "Something went wrong, Please Try again.")
//                            return
//                        }
//                    }
//                }
//            } catch (ex: Exception) {
//                ex.printStackTrace()
//            }
//        }
//    }
//
//    // set error if input field is blank
//    private fun setErrorMSG() {
//        setErrorMessage(mTextInputEmail!!,mEditTextEmail!!,
//            getString(R.string.val_msg_please_enter_email))
//        setErrorMessage(mTextInputPassword!!,mEditTextPassword!!,
//            getString(R.string.val_msg_please_enter_password))
//        //        setErrorMessage(mTextInputShift, mEditTextShift, getString(R.string.val_msg_please_enter_shift));
//    }
//
//    /* Call Api For Citation issue number*/
//    private fun callCitationNumberApi() {
//        if (isInternetAvailable(this@LoginActivity)) {
//            val mCitationNumberRequest = CitationNumberRequest()
//            val uniqueID = getDeviceId(mContext!!)
//            val welcomeForm : WelcomeForm? = mDb!!.dbDAO!!.getWelcomeForm()
//            if(welcomeForm!=null && !welcomeForm.officerDeviceName.isNullOrEmpty()){
//                mCitationNumberRequest.deviceId = uniqueID+"-"+welcomeForm.officerDeviceName
//            }else{
//                mCitationNumberRequest.deviceId = uniqueID+"-"+"Device"
//            }
//            mCitationNumberModel!!.hitGetCitationNumberApi(mCitationNumberRequest)
//        } else {
//            printToastMSG(this@LoginActivity, getString(R.string.err_msg_connection_was_refused))
//        }
//    }
//
//    /* Call Api For Citation Dataset Type */
//    private fun callCitationDatasetApi(mType: String) {
//        if (isInternetAvailable(this@LoginActivity)) {
////            DropdownDatasetRequest mDropdownDatasetRequest = new DropdownDatasetRequest();
////            mDropdownDatasetRequest.setType(mType);
////            mDropdownDatasetRequest.setShard((long) 1);
//            if (mType == "ActivityLayout") {
//                activityLayoutModel!!.hitGetActivityLayoutApi()
//            } else if (mType == "TimingRecordLayout") {
//                activityLayoutModel!!.hitGetTimingLayoutApi()
//            }
//        } else {
//            printToastMSG(this@LoginActivity, getString(R.string.err_msg_connection_was_refused))
//        }
//    }
//
//    /* Call Api For citation layout details */
//    private fun callCitationLayoutApi() {
//        if (isInternetAvailable(this@LoginActivity)) {
//            mCitationLayoutViewModel!!.hitGetCitationLayoutApi()
//        } else {
//            printToastMSG(this@LoginActivity, getString(R.string.err_msg_connection_was_refused))
//        }
//    }
//
//    /* Call Api For Municipal Citation Layout details */
//    private fun callMunicipalCitationLayoutApi() {
//        if (isInternetAvailable(this@LoginActivity)) {
//            mCitationLayoutViewModel?.hitGetMunicipalCitationLayoutApi()
//        } else {
//            printToastMSG(this@LoginActivity, getString(R.string.err_msg_connection_was_refused))
//        }
//    }
//
//    /* Call Api For Updated Time */
//    private fun callUpdatedTimeApi() {
//        if (isInternetAvailable(this@LoginActivity)) {
//            mUpdateTimeViewModel!!.hitUpdateTimeApi()
//        } else {
//            printToastMSG(this@LoginActivity, getString(R.string.err_msg_connection_was_refused))
//        }
//    }
//
//    /* Call Api For Login user and get user details */
//    private fun callLoginUserApi() {
//        printLog("BuildConfig.SITE_ID", getString(R.string.SITE_ID))
//        if (isInternetAvailable(this@LoginActivity)) {
//            val mLoginRequest = SiteOfficerLoginRequest()
//            mLoginRequest.siteId = getSiteId(this@LoginActivity)
//            //            mLoginRequest.setSiteId(Constants.SITE_ID); //6b07b768-926c-49b6-ac1c-89a9d03d4c3b
////            mLoginRequest.setSiteId("d57ca4fd-53f2-4894-8cb4-51061b569670"); //6b07b768-926c-49b6-ac1c-89a9d03d4c3b
//            mLoginRequest.siteOfficerUserName = mEditTextEmail!!.editableText.toString()
//            mLoginRequest.siteOfficerPassword = mEditTextPassword!!.editableText.toString()
//            mLoginViewModel!!.hitLoginApi(mLoginRequest)
//            enableLoginButton()
//        } else {
//
//            printToastMSG(this@LoginActivity, getString(R.string.err_msg_connection_was_refused))
//        }
//    }
//
//    private fun deleteSignature() {
//        try {
//            //getting signature path
//            var mPath = sharedPreference.read(SharedPrefKey.FILE_PATH, "")
//            val imageName = getSignatureFileNameWithExt()
//            mPath = mPath + Constants.CAMERA + "/" + imageName
//            //delete signature from path directory
//            val file = File(mPath)
//            if (file.exists()) file.delete()
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }
//
//    /*Api response */
//    private fun consumeResponse(apiResponse: ApiResponse, tag: String) {
//        when (apiResponse.status) {
//            Status.LOADING -> showProgressLoader(getString(R.string.scr_message_please_wait))
//            Status.SUCCESS -> {
//                printLog("API COUNT", numberOfAPI.toString() + "")
//                if (!tag.equals(DynamicAPIPath.POST_IMAGE, true)
//                    && !tag.equals(DynamicAPIPath.POST_CREATE_TICKET, true)
//                    && !tag.equals(DynamicAPIPath.POST_CREATE_MUNICIPAL_CITATION_TICKET, true)) {
//                    numberOfAPI++
//                }
//                if (!apiResponse.data!!.isNull) {
//                    printLog(tag, apiResponse.data.toString())
//
//                    if (tag.equals(DynamicAPIPath.POST_IMAGE, true)) {
//                        try {
//                            val responseModel = ObjectMapperProvider.fromJson(apiResponse.data.toString(), UploadImagesResponse::class.java)
//
//
//                            if (responseModel != null && responseModel.status!!) {
//                                if (responseModel.data != null && responseModel.data!!.size > 0 && responseModel.data!![0].response != null && responseModel.data!![0].response!!.links != null && responseModel.data!![0].response!!.links!!.size > 0) {
//                                    try {
//                                        mDb?.dbDAO?.deleteTempImagesOfflineWithId(
//                                            offlineCitationImagesList?.get(
//                                                imageUploadSuccessCount
//                                            )?.id.toString()
//                                        )
//                                    } catch (e: java.lang.Exception) {
//                                        e.printStackTrace()
//                                    }
//                                    imageUploadSuccessCount++
//                                    mImages.add(responseModel.data!![0].response!!.links!![0])
//                                    if (imageUploadSuccessCount == offlineCitationImagesList!!.size) {
//                                        val ticketUploadStatusRequest = TicketUploadStatusRequest()
//                                        ticketUploadStatusRequest.citationNumber = offlineCitationData!!.citationNumber
////                                        mTicketStatusViewModel?.getTicketStatusApi(
////                                            ticketUploadStatusRequest,
////                                            offlineCitationData!!.citationNumber
////                                        )
//                                        callCreateTicketApi(
//                                            offlineCitationData!!
//                                        )
//                                    }
//                                } else {
//                                    showCustomAlertDialog(
//                                        mContext,
//                                        APIConstant.POST_IMAGE,
//                                        getString(R.string.err_msg_something_went_wrong_imagearray),
//                                        getString(R.string.alt_lbl_OK),
//                                        getString(R.string.scr_btn_cancel),
//                                        this
//                                    )
//                                }
//                            } else {
//                                dismissLoader()
//                                showCustomAlertDialog(
//                                    mContext,
//                                    APIConstant.POST_IMAGE,
//                                    getString(R.string.err_msg_something_went_wrong),
//                                    getString(R.string.alt_lbl_OK),
//                                    getString(R.string.scr_btn_cancel),
//                                    this
//                                )
//                            }
//                        } catch (e: java.lang.Exception) {
//                            e.printStackTrace()
//                        }
//                    }
//
//                    if (tag.equals(DynamicAPIPath.POST_CREATE_TICKET, ignoreCase = true)) {
//                        val responseModel = ObjectMapperProvider.fromJson(apiResponse.data.toString(), CreateTicketResponse::class.java)
//
//                        try {
//                            ApiLogsClass.writeApiPayloadTex(BaseApplication.instance?.applicationContext!!,"RESPONSE: "+ObjectMapperProvider.instance.writeValueAsString(responseModel))
//                        } catch (e: Exception) {
//                            e.printStackTrace()
//                        }
////                        callRestOfAPIs()
//                        if (responseModel != null && responseModel.success.nullSafety()) {
//                            /**
//                             * differnt user then clear DB else only update statuc of citation
//                             */
////                            if (sharedPreference.read(
////                                    SharedPrefKey.IS_NEW_USER_LOGIN_DELETED_DB,
////                                    false
////                                )) {
//////                                mDb.clearAllTables();
////                                printLog("Newuserlogin", "clear DB")
////                            } else {
//                            mDb!!.dbDAO!!.updateCitationUploadStatus(0, mCitationNumberId)
//                            uploadOfflineCitation()
////                            }
//                        } else {
//                            showCustomAlertDialog(
//                                mContext,
//                                APIConstant.POST_CREATE_TICKET,
//                                getString(R.string.err_msg_something_went_wrong),
//                                getString(R.string.alt_lbl_OK),
//                                getString(R.string.scr_btn_cancel),
//                                this@LoginActivity
//                            )
//                        }
//                    }
//                    else if (tag.equals(DynamicAPIPath.POST_CREATE_MUNICIPAL_CITATION_TICKET, ignoreCase = true)) {
//
//                        val responseModel = ObjectMapperProvider.fromJson(apiResponse.data.toString(), CreateMunicipalCitationTicketResponse::class.java)
//
//                        try {
//                            ApiLogsClass.writeApiPayloadTex(BaseApplication.instance?.applicationContext!!,"RESPONSE: "+ObjectMapperProvider.instance.writeValueAsString(responseModel))
//                        } catch (e: Exception) {
//                            e.printStackTrace()
//                        }
////                        callRestOfAPIs()
//                        if (responseModel != null && responseModel.success.nullSafety()) {
//                            /**
//                             * differnt user then clear DB else only update statuc of citation
//                             */
////                            if (sharedPreference.read(
////                                    SharedPrefKey.IS_NEW_USER_LOGIN_DELETED_DB,
////                                    false
////                                )) {
//////                                mDb.clearAllTables();
////                                printLog("Newuserlogin", "clear DB")
////                            } else {
//                            mDb!!.dbDAO!!.updateCitationUploadStatus(0, mCitationNumberId)
//                            uploadOfflineCitation()
////                            }
//                        } else {
//                            showCustomAlertDialog(
//                                mContext,
//                                APIConstant.POST_CREATE_MUNICIPAL_CITATION_TICKET,
//                                getString(R.string.err_msg_something_went_wrong),
//                                getString(R.string.alt_lbl_OK),
//                                getString(R.string.scr_btn_cancel),
//                                this@LoginActivity
//                            )
//                        }
//                    }
//                    else if (tag.equals(
//                            DynamicAPIPath.POST_CITATION_DATASET,
//                            ignoreCase = true)) {
//                        dismissLoader()
//                        try {
//                            val responseModelShift = ObjectMapperProvider.fromJson(apiResponse.data.toString(), ShiftResponse::class.java)
//
//
//                            if (responseModelShift != null && responseModelShift.status.nullSafety()) {
//                                if (responseModelShift.data?.get(0)?.metadata?.type == "ShiftList") {
//                                    setDropdownShift(responseModelShift.data?.get(0)?.response)
//                                    callAuthToken()
//                                }
//                                if (responseModelShift.data?.get(0)?.metadata?.type == "HearingTimeList") {
//
//                                    val responseModelHearing = ObjectMapperProvider.fromJson(apiResponse.data.toString(), HearingTimeResponse::class.java)
//
//                                    setDropdownHearingTime(responseModelHearing.data?.get(0)?.response)
////                                    callAuthToken()
//                                }
//                            }
//                        } catch (e: Exception) {
//                            dismissLoader()
//                            e.printStackTrace()
//                        }
//                    } else if (tag.equals(TAG, ignoreCase = true)) {
//                        responseModelLogin = ObjectMapperProvider.fromJson(apiResponse.data.toString(), CommonLoginResponse::class.java)
//
//                        if (responseModelLogin != null && responseModelLogin!!.status.nullSafety()) {
//                            if (mEditTextEmail!!.text.toString()
//                                    .equals(
//                                        sharedPreference.read(SharedPrefKey.USER_NAME, ""),
//                                        ignoreCase = true)) {
//                                sharedPreference.write(
//                                    SharedPrefKey.IS_NEW_USER_LOGIN_DELETED_DB,false )
//                            } else if (!sharedPreference.read(SharedPrefKey.USER_NAME, "")!!
//                                    .isEmpty()) {
//                                sharedPreference.write(
//                                    SharedPrefKey.IS_NEW_USER_LOGIN_DELETED_DB,true)
//                            }
//                            deleteSignature()
//
//                            //mDb.getDbDAO().insertLogin(responseModel);
//                        } else if (responseModelLogin != null && responseModelLogin!!.response?.isNotEmpty()
//                                .nullSafety()) {
//                            dismissLoader()
//                            showCustomAlertDialog(
//                                mContext,APIConstant.LOGIN,responseModelLogin!!.response.toString(),
//                                getString(R.string.alt_lbl_OK), getString(R.string.scr_btn_cancel),this@LoginActivity)
//                        } else {
//                            dismissLoader()
//                            showCustomAlertDialog(mContext, APIConstant.LOGIN,
//                                getString(R.string.err_msg_something_went_wrong),
//                                getString(R.string.alt_lbl_OK), getString(R.string.scr_btn_cancel), this@LoginActivity)
//                        }
//                        if (responseModelLogin!!.status.nullSafety()) {
////                            uploadOfflineCitation()
//                            callRestOfAPIs()
//                        }
//                    }
//                    if (tag.equals(DynamicAPIPath.POST_CITATION_NUMBER, ignoreCase = true)) {
//                        try {
//
//
//                            val responseModel = ObjectMapperProvider.fromJson(apiResponse.data.toString(), CitationNumberResponse::class.java)
//
//
//                            if (responseModel != null && responseModel.status.nullSafety()) {
//                                saveBookletWithStatus(responseModel.data?.get(0))
//                                //                            printToastMSG(
//                                //                                this@LoginActivity,
//                                //                                responseModel.message + responseModel.data?.get(0)?.metadata.toString()
//                                //                            )
//                            } else {
//                                showCustomAlertDialog(
//                                    mContext,
//                                    APIConstant.POST_CITATION_NUMBER,
//                                    getString(R.string.err_msg_something_went_wrong),
//                                    getString(R.string.alt_lbl_OK),
//                                    getString(R.string.scr_btn_cancel),
//                                    this@LoginActivity
//                                )
//                            }
//                        } catch (e: Exception) {
//                            e.printStackTrace()
//                        }
//                    }
//                    if (tag.equals(DynamicAPIPath.GET_UPDATE_TIME, ignoreCase = true)) {
//
//                        val responseModel = ObjectMapperProvider.fromJson(apiResponse.data.toString(), UpdateTimeResponse::class.java)
//
//                        if (responseModel != null && responseModel.status.nullSafety()) {
//                            saveUpdatedTime(responseModel)
//                            //LogUtil.printToastMSG(LoginActivity.this, responseModel.getMessage() + responseModel.getData().get(0).getMetadata().toString());
//                        } else {
//                            showCustomAlertDialog(
//                                mContext,
//                                APIConstant.GET_UPDATE_TIME,
//                                getString(R.string.err_msg_something_went_wrong),
//                                getString(R.string.alt_lbl_OK),
//                                getString(R.string.scr_btn_cancel),
//                                this@LoginActivity
//                            )
//                        }
//                    }
//                    if (tag.equals(DynamicAPIPath.GET_CITATION_LAYOUT + "TIMEACTIVITY",
//                            ignoreCase = true)) {
//                        //TODO create new response model
//                        try {
//                            val responseModel = ObjectMapperProvider.fromJson(apiResponse.data.toString(), ActivityLayoutResponse::class.java)
//
//                            if (responseModel != null && responseModel.status.nullSafety()) {
//                                if (responseModel.message.equals("activity", ignoreCase = true)) {
//                                    saveActivityLayout(responseModel)
//                                }
//                            } else if (responseModel != null && !responseModel.status.nullSafety()) {
//                                val message: String
//                                if (responseModel.response != null && responseModel.response != "") {
//                                    message = responseModel.response.nullSafety()
//                                    showCustomAlertDialog(
//                                        mContext, "POST_CITATION_DATASET",
//                                        message, "Ok", "Cancel", this
//                                    )
//                                } else {
//                                    responseModel.response = "Not getting response from server..!!"
//                                    message = responseModel.response.nullSafety()
//                                    showCustomAlertDialog(
//                                        mContext, "POST_CITATION_DATASET",
//                                        message, "Ok", "Cancel", this
//                                    )
//                                }
//                            } else {
//                                showCustomAlertDialog(
//                                    mContext, "POST_CITATION_DATASET",
//                                    "Something wen't wrong..!!", "Ok", "Cancel", this
//                                )
//                                printToastMSG(this@LoginActivity, responseModel!!.message)
//                            }
//                        } catch (e: Exception) {
//                            e.printStackTrace()
//                        }
//
//
//                        ////TIMING
//                        try {
//
//
//                            val responseModel = ObjectMapperProvider.fromJson(apiResponse.data.toString(), TimingLayoutResponse::class.java)
//
//
//                            if (responseModel != null && responseModel.status.nullSafety()) {
//                                if (responseModel.message == "timing") {
//                                    saveTimingLayout(responseModel)
//                                }
//                            } else if (responseModel != null && !responseModel.status.nullSafety()) {
//                                val message: String
//                                if (responseModel.response != null && responseModel.response != "") {
//                                    message = responseModel.response.nullSafety()
//                                    showCustomAlertDialog(
//                                        mContext, "TimingRecordLayout",
//                                        message, "Ok", "Cancel", this
//                                    )
//                                } else {
//                                    responseModel.response = "Not getting response from server..!!"
//                                    message = responseModel.response.nullSafety()
//                                    showCustomAlertDialog(
//                                        mContext, "TimingRecordLayout",
//                                        message, "Ok", "Cancel", this
//                                    )
//                                }
//                            } else {
//                                showCustomAlertDialog(
//                                    mContext, "TimingRecordLayout",
//                                    "Something wen't wrong..!!", "Ok", "Cancel", this
//                                )
//                                printToastMSG(this@LoginActivity, responseModel!!.message)
//                            }
//                        } catch (e: Exception) {
//                            e.printStackTrace()
//                            dismissLoader()
//                        }
//                    }
//                    if (tag.equals(DynamicAPIPath.GET_CITATION_LAYOUT, ignoreCase = true)) {
//                        try {
//
//                            val responseModel = ObjectMapperProvider.fromJson(apiResponse.data.toString(), CitationLayoutResponse::class.java)
//
//
//                            if (responseModel != null && responseModel.success.nullSafety()) {
//                                saveCitationLayout(responseModel)
//                            } else if (responseModel != null && !responseModel.success.nullSafety()) {
//                                val message: String
//                                if (responseModel.response != null && responseModel.response != "") {
//                                    message = responseModel.response.nullSafety()
//                                    showCustomAlertDialog(
//                                        mContext, "CitationLayou",
//                                        message, "Ok", "Cancel", this
//                                    )
//                                } else {
//                                    responseModel.response = "Not getting response from server..!!"
//                                    message = responseModel.response.nullSafety()
//                                    showCustomAlertDialog(
//                                        mContext, "CitationLayou",
//                                        message, "Ok", "Cancel", this
//                                    )
//                                }
//                            } else {
//                                showCustomAlertDialog(
//                                    mContext, "Citation-Layout",
//                                    "Something wen't wrong..!!", "Ok", "Cancel", this
//                                )
//
//                                // LogUtil.printToastMSG(LoginActivity.this, responseModel.getMessage());
//                            }
//                        } catch (e: Exception) {
//                            dismissLoader()
//                            e.printStackTrace()
//                        }
//                    }
//                    if (tag.equals(DynamicAPIPath.GET_MUNICIPAL_CITATION_LAYOUT, ignoreCase = true)) {
//                        try {
//                            val responseModel = ObjectMapperProvider.fromJson(apiResponse.data.toString(), MunicipalCitationLayoutResponse::class.java)
//
//                            if (responseModel != null && responseModel.success.nullSafety()) {
//                                saveMunicipalCitationLayout(responseModel)
//                            } else if (responseModel != null && !responseModel.success.nullSafety()) {
//                                val message: String
//                                if (responseModel.response != null && responseModel.response != "") {
//                                    message = responseModel.response.nullSafety()
//                                    showCustomAlertDialog(
//                                        mContext, "CitationLayou",
//                                        message, "Ok", "Cancel", this
//                                    )
//                                } else {
//                                    responseModel.response = "Not getting response from server..!!"
//                                    message = responseModel.response.nullSafety()
//                                    showCustomAlertDialog(
//                                        mContext, "CitationLayou",
//                                        message, "Ok", "Cancel", this
//                                    )
//                                }
//                            } else {
//                                showCustomAlertDialog(
//                                    mContext, "Citation-Layout",
//                                    "Something wen't wrong..!!", "Ok", "Cancel", this
//                                )
//
//                                // LogUtil.printToastMSG(LoginActivity.this, responseModel.getMessage());
//                            }
//                        } catch (e: Exception) {
//                            dismissLoader()
//                            e.printStackTrace()
//                        }
//                    }
//
//                    if (tag.equals(DynamicAPIPath.GET_REFRESH_AUTH_TOKEN, ignoreCase = true)) {
//                        try {
//
//                            val responseModel = ObjectMapperProvider.fromJson(apiResponse.data.toString(), AuthRefreshResponse::class.java)
//
//                            dismissLoader()
//                            if (responseModel != null) {
//                                sharedPreference.write(SharedPrefKey.ACCESS_TOKEN,
//                                    responseModel!!.response.nullSafety())
//                                uploadOfflineCitation()
//                            }
//                        } catch (e: Exception) {
//                            dismissLoader()
//                            e.printStackTrace()
//                            uploadOfflineCitation()
////                            if(apiResponse.data.asJsonObject.get("metadata").equals("old token is valid"))
//                        }
//                    }
//                    printLog("API COUNT", "$numberOfAPI  b")
//                    if (numberOfAPI >= 6) {
//                        Handler(Looper.getMainLooper()).postDelayed({
//                            dismissLoader()
//                            sharedPreference.write(SharedPrefKey.IS_LOGIN_ACTIVITY_LOGER_API, true)
//                            launchScreenLogin(mContext, WelcomeActivity::class.java)
//                        }, 300)
//                    }
//                }
//            }
//            Status.ERROR -> {
//                printLog("API COUNT", "$numberOfAPI  error")
//                numberOfAPI++
//                if (tag.equals(DynamicAPIPath.POST_CREATE_TICKET)||
//                    tag.equals(DynamicAPIPath.POST_CREATE_MUNICIPAL_CITATION_TICKET)||
//                    tag.equals(DynamicAPIPath.POST_IMAGE)) {
//                    dismissLoader()
//                    callRestOfAPIs()
////                   mAuthTokenRefreshViewModel!!.hitAuthTokenRefreshApi()
////                   uploadOfflineCitation()
//                }
//                dismissLoader()
//                printToastMSG(
//                    this@LoginActivity,
//                    tag+" "+getString(R.string.err_msg_connection_was_refused)
//                )
//            }
//            else -> {}
//        }
//    }
//
//    var lanInt = 0
//    /*check validations on field*/
//    private fun isDetailsValid(): Boolean {
//        mEditTextEmail!!.requestFocus()
//        if (TextUtils.isEmpty(mEditTextEmail!!.text.toString().trim())) {
//            setError(mTextInputEmail!!, getString(R.string.val_msg_please_enter_email))
//            return false
//        } else if (TextUtils.isEmpty(mEditTextPassword!!.text.toString().trim())) {
//            setError(mTextInputPassword!!, getString(R.string.val_msg_please_enter_password))
//            return false
//        } else if (mEditTextPassword!!.editableText.toString()
//                .trim().length < 6
//        ) { //6
//            setError(mTextInputPassword!!, getString(R.string.val_msg_minimum_password))
//            return false
//        } else if ((!BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CHARLESTON, true))
//            &&(!BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MEMORIALHERMAN, true))
//            &&(!BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKTEXAS, true))
//            &&(!BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOTTLEWORKS_IN, true))
//            &&(!BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKPARKING, true))
//            &&(!BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PARKX, true))
//            &&(!BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RUTGERS, true))
//            &&(!BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SEPTA, true))
//            &&(!BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_COHASSET, true))
//            &&(!BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITYOFSANDIEGO, true))
//            && TextUtils.isEmpty(
//                mAutoComTextViewShift!!.text.toString().trim())
//            ||  sharedPreference.read(SharedPrefKey.LOGIN_SHIFT, "")!!.toString().isEmpty()
//        ) {
//            setError(mTextInputShift!!, getString(R.string.val_msg_please_enter_shift))
//            return false
//        }
//        return true
//    }
//
//    /*check validations on field*/
//    private fun isCredentialValid(mstatus: Boolean): Boolean {
//        return if (mInvalidPassCount <= 4) {
//            checkCredCombination(mstatus)
//        } else true
//    }
//
//    /*validate username and password combination*/
//    private fun checkCredCombination(mstatus: Boolean): Boolean {
//        //get invalid credential count
//        mInvalidPassCount = sharedPreference.read(SharedPrefKey.IS_INVALID_PASSWORD, "0")!!.toInt()
//        //check for verified credential
//        //if username matches but password doesn't
//        if (!mstatus) {
//            //there are only five attempts to enter correct credential
//            if (mInvalidPassCount < 4) {
//                //increment count
//                val mInvalidPassFinalCount = (mInvalidPassCount + 1).toString()
//                //store to shared-pref
//                sharedPreference.write(SharedPrefKey.IS_INVALID_PASSWORD, mInvalidPassFinalCount)
//                sharedPreference.write(
//                    SharedPrefKey.USER_NAME,
//                    mEditTextEmail!!.text.toString().trim())
//                printToastMSG(
//                    this,
//                    getString(R.string.val_msg_username_password_combination_incorrect) + " " + (4 - mInvalidPassCount) + " attempts left."
//                )
//            } else {
//                printToastMSG(this, getString(R.string.err_msg_your_account_is_locked))
//            }
//            return false
//            //if credential are matches
//        } else if (mstatus) {
//            return if (mEditTextEmail!!.text.toString()
//                    .trim() == sharedPreference.read(SharedPrefKey.USER_NAME, "")
//            ) {
//                if (mInvalidPassCount == 4) {
//                    printToastMSG(this, getString(R.string.err_msg_your_account_is_locked))
//                    false
//                } else {
//                    true
//                }
//            } else {
//                true
//            }
//        }
//        return false
//    }
//
//
//
//    fun permissions(): Array<String> {
//        val p: Array<String> = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
//            PermissionUtils.storage_permissions_login_34
//        }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            PermissionUtils.storage_permissions_login_33
//        } else {
//            PermissionUtils.storage_permissions_login
//        }
//        return p
//    }
//    //request camera and storage permission
//    private fun requestPermission() {
//        var request = false
//        for (permission in permissions())
//            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
//                request = true
//                break
//            }
//        if (request) {
//            requestPermissions(
//                permissions(),
//                1000
//            )
//        } else {
//            //We need to show this dialog after all the permission done
//            //We have to show this dialog only once for application lifetime
//            val isNeedToShowBatteryOptimisationDialog =
//                sharedPreference.read(SharedPrefKey.SHOW_BATTERY_OPTIMISATION_DIALOG, true)
//            val isAppExcludedFromBatteryOptimization =
//                SystemUtils.isAppExcludedFromBatteryOptimization(this@LoginActivity)
//
//            if (isNeedToShowBatteryOptimisationDialog && !isAppExcludedFromBatteryOptimization
//            ) {
//                whitelistAppFromBatteryOptimization(this@LoginActivity, sharedPreference)
//            } else {
//                sharedPreference.write(SharedPrefKey.SHOW_BATTERY_OPTIMISATION_DIALOG, false)
//            }
//        }
//    }
//
//    /*
//     * ACCESS_FINE_LOCATION permission result
//     * */
//    @SuppressLint("MissingSuperCall")
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<String>,
//        grantResults: IntArray
//    ) {
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
//            when (requestCode) {
//                1000 -> if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
//                    grantResults[1] == PackageManager.PERMISSION_GRANTED &&
//                    grantResults[2] == PackageManager.PERMISSION_GRANTED &&
//                    grantResults[3] == PackageManager.PERMISSION_GRANTED &&
//                    grantResults[4] == PackageManager.PERMISSION_GRANTED
//                ) {
//
//                }else {
//                    requestPermission()
//                }
//            }
////            requestPermissions.launch(arrayOf(READ_MEDIA_IMAGES, READ_MEDIA_VIDEO, READ_MEDIA_VISUAL_USER_SELECTED))
//        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            when (requestCode) {
//                1000 -> if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
//                    grantResults[1] == PackageManager.PERMISSION_GRANTED &&
//                    grantResults[2] == PackageManager.PERMISSION_GRANTED &&
//                    grantResults[3] == PackageManager.PERMISSION_GRANTED &&
//                    grantResults[4] == PackageManager.PERMISSION_GRANTED &&
//                    grantResults[5] == PackageManager.PERMISSION_GRANTED
//                ) {
//
//                }else {
//                    requestPermission()
//                }
//            }
//        } else {
//            when (requestCode) {
//                1000 -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
//                    && grantResults[1] == PackageManager.PERMISSION_GRANTED &&
//                    grantResults[2] == PackageManager.PERMISSION_GRANTED &&
//                    grantResults[3] == PackageManager.PERMISSION_GRANTED &&
//                    grantResults[4] == PackageManager.PERMISSION_GRANTED &&
//                    grantResults[5] == PackageManager.PERMISSION_GRANTED &&
//                    grantResults[6] == PackageManager.PERMISSION_GRANTED) {
//                    //BaseApplication.getInstance().mService.requestLocationUpdates();
//
//
//                } else {
//                    requestPermission()
//                    //Toast.makeText(mContext, getString(R.string.somthing_went_wrong), Toast.LENGTH_SHORT).show();
//                }
//                else ->{}
//            }
//        }
//
//
//    }
//
//    //save Booklet with status in Databse
//    private fun saveBookletWithStatus(mResponse: CitationNumberData?) {
//        mContext = this
//        val citationBookletModelList: MutableList<CitationBookletModel> = ArrayList()
//        val mData = CitationNumberDatabaseModel()
//        try {
//            val isBookletExits = mDb!!.dbDAO!!.getCitationBooklet(0)
//            if (isBookletExits!!.size == 0) {
//                for (i in mResponse?.response?.citationBooklet?.indices!!) {
//                    if (mResponse.response?.citationBooklet!![i] != null) {
//                        val bookletModel = CitationBookletModel()
//                        bookletModel.citationBooklet =
//                            mResponse.response?.citationBooklet!![i].nullSafety()
//                        bookletModel.mStatus = 0
//                        if (bookletModel != null) {
//                            citationBookletModelList.add(bookletModel)
//                        }
//                    }
//                }
//                mDb!!.dbDAO!!.insertCitationBooklet(citationBookletModelList)
//                if (mResponse.metadata != null) {
//                    mData.metadata = mResponse.metadata
//                }
//                val mBooklet = CitationBookletDatabaseModel()
//                mBooklet.latestCitationNumber =
//                    mResponse.response?.latestCitationNumber.nullSafety()
//                mData.response = mBooklet
//                mDb!!.dbDAO!!.insertCitationNumberResponse(mData)
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }
//
//    //save Activity layout in Databse
//    private fun saveActivityLayout(mResponse: ActivityLayoutResponse) {
//        mContext = this
//        mResponse.id = 1
//        mResponse.uploadStatus = 0
//        mDb!!.dbDAO!!.insertActivityLayout(mResponse)
//    }
//
//    //save Timing layout in Databse
//    private fun saveTimingLayout(mResponse: TimingLayoutResponse) {
//        mContext = this
//        mResponse.id = 1
//        mResponse.uploadStatus = 0
//        mDb!!.dbDAO!!.insertTimingLayout(mResponse)
//    }
//
//    //save Citation Layout Databse
//    private fun saveCitationLayout(mResponse: CitationLayoutResponse) {
//        mContext = this
//        try {
//            mResponse.id = 1
//            mDb!!.dbDAO!!.insertCitationLayout(mResponse)
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }
//
//    //save Citation Layout Database
//    private fun saveMunicipalCitationLayout(mResponse: MunicipalCitationLayoutResponse) {
//        mContext = this
//        try {
//            mResponse.id = 1
//            mDb!!.dbDAO!!.insertMunicipalCitationLayout(mResponse)
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }
//
//    //save Citation Layout Databse
//    private fun uploadOfflineCitation() {
//        mContext = this
//        class SaveTask : AsyncTask<Void?, Int?, CitationInsurranceDatabaseModel?>() {
//            override fun doInBackground(vararg voids: Void?): CitationInsurranceDatabaseModel? {
//                try {
//                    var mIssuranceModel: List<CitationInsurranceDatabaseModel?>? = ArrayList()
//                    mIssuranceModel = mDb?.dbDAO?.getCitationInsurrance()
//                    for (i in mIssuranceModel!!.indices) {
//                        if (mIssuranceModel[i]!!.formStatus == 1) {
//                            mCitationNumberId = mIssuranceModel[i]!!.citationNumber
//                            return mIssuranceModel[i]
//                        }
//                    }
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                }
//                return null
//            }
//
//            @SuppressLint("WrongThread")
//            override fun onPostExecute(result: CitationInsurranceDatabaseModel?) {
//                try {
//                    if(result!=null && result.citationNumber!!.isNotEmpty()) {
////                        result?.let { callCreateTicketApi(it) }
//                        result?.let {
//                            offlineCitationData = result
//                            uploadOfflineImages(result)
//                            //callCreateTicketApi(it)
//                        }
//                    }else
//                    {
//                        callRestOfAPIs()
//                    }
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                }
//                //LogUtil.printToastMSG(mContext,"Activity Layout saved!");
//            }
//        }
//        SaveTask().execute()
//    }
//
//    private fun uploadOfflineImages(result: CitationInsurranceDatabaseModel) {
//        try {
//            offlineCitationImagesList = ArrayList()
//            offlineCitationImagesList =
//                mDb?.dbDAO?.getCitationImageOffline(result.citationNumber!!.toString()) as List<CitationImageModelOffline>?
//            if (offlineCitationImagesList!!.size == 0) {
//                callCreateTicketApi(result)
//            } else {
//                for (i in offlineCitationImagesList!!.indices) {
//                    callUploadImages(File(offlineCitationImagesList!![i].citationImage), i)
//                }
//            }
//        } catch (e: java.lang.Exception) {
//            e.printStackTrace()
//        }
//    }
//
//
//    private fun callRestOfAPIs()
//    {
//        if (sharedPreference.read(SharedPrefKey.IS_NEW_USER_LOGIN_DELETED_DB, false)) {
//            val remainingBooklet: List<CitationBookletModel>? = mDb?.dbDAO?.getCitationBooklet(0)
//            lifecycleScope.launch {
//                withContext(Dispatchers.Main){
//                    mDb?.clearAllTables()
//                }
//            }
//            //mDb?.clearAllTables()
//            printLog("Newuserlogin", "clear DB")
//            mDb?.dbDAO?.insertCitationBooklet(remainingBooklet?: emptyList())
//            sharedPreference.write(SharedPrefKey.IS_LOGGED_IN, true)
//            try {
//                val lockLprModel = LockLprModel()
//                lockLprModel.mLprNumber = ""
//                lockLprModel.mMake = ""
//                lockLprModel.mModel = ""
//                lockLprModel.mColor = ""
//                lockLprModel.mAddress = ""
//                lockLprModel.mViolationCode = ""
//                lockLprModel.ticketCategory = ""
//                AppUtils.setLprLock(lockLprModel, this@LoginActivity)
//                sharedPreference.write(SharedPrefKey.LOCKED_LPR_BOOL, false)
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
//        try {
//            sharedPreference.write(SharedPrefKey.IS_LOGGED_IN, true)
//            sharedPreference.write(SharedPrefKey.IS_LOGIN_LOGGED, true)
//            sharedPreference.write(SharedPrefKey.USER_NAME,
//                mEditTextEmail!!.text.toString().trim())
//            sharedPreference.write(SharedPrefKey.PRE_TIME,
//                responseModelLogin!!.metadata?.lastLogin)
//            sharedPreference.write(
//                SharedPrefKey.CURRENT_TIME,responseModelLogin!!.metadata?.currentLogin)
//            sharedPreference.write(SharedPrefKey.ACCESS_TOKEN,
//                responseModelLogin!!.response.nullSafety())
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//
//        //LogUtil.printToastMSG(mContext, getString(R.string.success_msg_new_device));
//        //call api for activity layout
//        callCitationDatasetApi("ActivityLayout")
//        //call api for timing layout
//        callCitationDatasetApi("TimingRecordLayout")
//        //call api for citation layout
//        callCitationLayoutApi()
//
//        if(LogUtil.isMunicipalCitationEnabled()){
//            //call api for municipal citation layout
//            callMunicipalCitationLayoutApi()
//        }
//
//        try {
//            val isBookletExits = mDb?.dbDAO?.getCitationBooklet(0)
//            if (isBookletExits!!.size < 1) {
//                //call api for citation booklet
//                callCitationNumberApi()
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//        //call api to get updated time
//        callUpdatedTimeApi()
//        val mLat = sharedPreference.read(SharedPrefKey.LAT, "0.0")!!
//            .toDouble()
//        val mLong = sharedPreference.read(SharedPrefKey.LONG, "0.0")!!
//            .toDouble()
//        val mLocationUpdateRequest = LocUpdateRequest()
//        mLocationUpdateRequest.activityType = Constants.ACTIVITY_TYPE_LOCATION_UPDATE
//        mLocationUpdateRequest.logType = Constants.LOG_TYPE_NODE_PORT
//        mLocationUpdateRequest.locationUpdateType = "login"
//        mLocationUpdateRequest.latitude = mLat
//        mLocationUpdateRequest.longitude = mLong
//        var Zone: String? = "CST"
//        if (Singleton.getDataSetList(DATASET_SETTINGS_LIST, getMyDatabase()) != null) {
//            Zone = Singleton.getDataSetList(DATASET_SETTINGS_LIST, getMyDatabase())!![0].mValue
//                .nullSafety()
//        }
//        val timeMilli1 = getClientTimeStamp(Zone)
//        //                        mLocationUpdateRequest.setClientTimestamp(timeMilli1);
//        mLocationUpdateRequest.clientTimestamp = splitDateLpr(Zone)
//        callLocationApi(mLocationUpdateRequest)
//    }
//    //save Booklet with status in Databse
//    private fun saveUpdatedTime(mGetList: UpdateTimeResponse) {
//        mTimeResponse = mGetList
//        mContext = this
//        mResponseEntity = mTimeResponse?.data?.get(0)
//        try {
//            val mSaveToDb = TimestampDatatbase()
//            timeDataList = UpdateTimeDataList()
//            //get data from Db
//            mTimingDatabaseList = mDb?.dbDAO?.getUpdateTimeResponse()
//            if (mTimingDatabaseList != null) {
//                //set status initally false
//                setTimingStatus(false)
//                timeDataList = setTimeDataList(
//                    timeDataList!!,
//                    mTimingDatabaseList!!,
//                    mTimeResponse!!,
//                    mResponseEntity!!
//                )
//            } else {
//                //if no data exits in db then set all status true
//                setTimingStatus(true)
//            }
//            mSaveToDb.id = 1
//            mSaveToDb.timeList = timeDataList
//            mDb!!.dbDAO!!.insertUpdatedTime(mSaveToDb)
////            /**
////             * In case data set get null then login and logout get all dataset again
////             */
////            if(mDb?.dbDAO?.getDataset()?.dataset!! == null || mDb?.dbDAO?.getDataset()?.dataset!!.settingsList == null) {
////                mDb!!.dbDAO!!.deleteTimeStampTable()
////            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }
//
//    //set status for all dataset
//    private fun setTimingStatus(mStatus: Boolean) {
//        try {
//            if (mStatus) {
//                if (mResponseEntity!!.activityLayout != null) {
//                    timeDataList!!.activityLayout =
//                        UpdateTimeDb(mResponseEntity!!.activityLayout, mStatus)
//                }
//                if (mResponseEntity!!.activityList != null) {
//                    timeDataList!!.activityList =
//                        UpdateTimeDb(mResponseEntity!!.activityList, mStatus)
//                }
//                if (mResponseEntity!!.agencyList != null) {
//                    timeDataList!!.agencyList = UpdateTimeDb(mResponseEntity!!.agencyList, mStatus)
//                }
//                if (mResponseEntity!!.beatList != null) {
//                    timeDataList!!.beatList = UpdateTimeDb(mResponseEntity!!.beatList, mStatus)
//                }
//                if (mResponseEntity!!.cancelReasonList != null) {
//                    timeDataList!!.cancelReasonList =
//                        UpdateTimeDb(mResponseEntity!!.cancelReasonList, mStatus)
//                }
//                if (mResponseEntity!!.carBodyStyleList != null) {
//                    timeDataList!!.carBodyStyleList =
//                        UpdateTimeDb(mResponseEntity!!.carBodyStyleList, mStatus)
//                }
//                if (mResponseEntity!!.carColorList != null) {
//                    timeDataList!!.carColorList =
//                        UpdateTimeDb(mResponseEntity!!.carColorList, mStatus)
//                }
//                if (mResponseEntity!!.carModelList != null) {
//                    timeDataList!!.carModelList =
//                        UpdateTimeDb(mResponseEntity!!.carModelList, mStatus)
//                }
//                if (mResponseEntity!!.carMakeList != null) {
//                    timeDataList!!.carMakeList =
//                        UpdateTimeDb(mResponseEntity!!.carMakeList, mStatus)
//                }
//                if (mResponseEntity!!.citationData != null) {
//                    timeDataList!!.citationData =
//                        UpdateTimeDb(mResponseEntity!!.citationData, mStatus)
//                }
//                if (mResponseEntity!!.citationLayout != null) {
//                    timeDataList!!.citationLayout =
//                        UpdateTimeDb(mResponseEntity!!.citationLayout, mStatus)
//                }
//                if (mResponseEntity!!.commentsList != null) {
//                    timeDataList!!.commentsList =
//                        UpdateTimeDb(mResponseEntity!!.commentsList, mStatus)
//                }
//                if (mResponseEntity!!.decalYearList != null) {
//                    timeDataList!!.decalYearList =
//                        UpdateTimeDb(mResponseEntity!!.decalYearList, mStatus)
//                }
//                if (mResponseEntity!!.directionList != null) {
//                    timeDataList!!.directionList =
//                        UpdateTimeDb(mResponseEntity!!.directionList, mStatus)
//                }
//                if (mResponseEntity!!.exemptData != null) {
//                    timeDataList!!.exemptData = UpdateTimeDb(mResponseEntity!!.exemptData, mStatus)
//                }
//                if (mResponseEntity!!.lotList != null) {
//                    timeDataList!!.lotList = UpdateTimeDb(mResponseEntity!!.lotList, mStatus)
//                }
//                if (mResponseEntity!!.makeModelColorData != null) {
//                    timeDataList!!.makeModelColorData =
//                        UpdateTimeDb(mResponseEntity!!.makeModelColorData, mStatus)
//                }
//                if (mResponseEntity!!.meterList != null) {
//                    timeDataList!!.meterList = UpdateTimeDb(mResponseEntity!!.meterList, mStatus)
//                }
//                if (mResponseEntity!!.notesList != null) {
//                    timeDataList!!.notesList = UpdateTimeDb(mResponseEntity!!.notesList, mStatus)
//                }
//                if (mResponseEntity!!.paymentData != null) {
//                    timeDataList!!.paymentData =
//                        UpdateTimeDb(mResponseEntity!!.paymentData, mStatus)
//                }
//                if (mResponseEntity!!.permitData != null) {
//                    timeDataList!!.permitData = UpdateTimeDb(mResponseEntity!!.permitData, mStatus)
//                }
//                if (mResponseEntity!!.radioList != null) {
//                    timeDataList!!.radioList = UpdateTimeDb(mResponseEntity!!.radioList, mStatus)
//                }
//                if (mResponseEntity!!.regulationTimeList != null) {
//                    timeDataList!!.regulationTimeList =
//                        UpdateTimeDb(mResponseEntity!!.regulationTimeList, mStatus)
//                }
//                if (mResponseEntity!!.remarksList != null) {
//                    timeDataList!!.remarksList =
//                        UpdateTimeDb(mResponseEntity!!.remarksList, mStatus)
//                }
//                if (mResponseEntity!!.scofflawData != null) {
//                    timeDataList!!.scofflawData =
//                        UpdateTimeDb(mResponseEntity!!.scofflawData, mStatus)
//                }
//                if (mResponseEntity!!.shiftList != null) {
//                    timeDataList!!.shiftList = UpdateTimeDb(mResponseEntity!!.shiftList, mStatus)
//                }
//                if (mResponseEntity!!.sideList != null) {
//                    timeDataList!!.sideList = UpdateTimeDb(mResponseEntity!!.sideList, mStatus)
//                }
//                if (mResponseEntity!!.stateList != null) {
//                    timeDataList!!.stateList = UpdateTimeDb(mResponseEntity!!.stateList, mStatus)
//                }
//                if (mResponseEntity!!.stolenData != null) {
//                    timeDataList!!.stolenData = UpdateTimeDb(mResponseEntity!!.stolenData, mStatus)
//                }
//                if (mResponseEntity!!.streetList != null) {
//                    timeDataList!!.streetList = UpdateTimeDb(mResponseEntity!!.streetList, mStatus)
//                }
//                if (mResponseEntity!!.supervisorList != null) {
//                    timeDataList!!.supervisorList =
//                        UpdateTimeDb(mResponseEntity!!.supervisorList, mStatus)
//                }
//                if (mResponseEntity!!.tierStemList != null) {
//                    timeDataList!!.tierStemList =
//                        UpdateTimeDb(mResponseEntity!!.tierStemList, mStatus)
//                }
//                if (mResponseEntity!!.timingData != null) {
//                    timeDataList!!.timingData = UpdateTimeDb(mResponseEntity!!.timingData, mStatus)
//                }
//                if (mResponseEntity!!.timingRecordLayout != null) {
//                    timeDataList!!.timingRecordLayout =
//                        UpdateTimeDb(mResponseEntity!!.timingRecordLayout, mStatus)
//                }
//                if (mResponseEntity!!.vehiclePlateTypeList != null) {
//                    timeDataList!!.vehiclePlateTypeList =
//                        UpdateTimeDb(mResponseEntity!!.vehiclePlateTypeList, mStatus)
//                }
//                if (mResponseEntity!!.violationList != null) {
//                    timeDataList!!.violationList =
//                        UpdateTimeDb(mResponseEntity?.violationList, mStatus)
//                }
//                if (mResponseEntity?.zoneList != null) {
//                    timeDataList?.zoneList = UpdateTimeDb(mResponseEntity?.zoneList, mStatus)
//                }
//                if (mResponseEntity?.mCityZoneList != null) {
//                    timeDataList?.mCityZoneList =
//                        UpdateTimeDb(
//                            mResponseEntity?.mCityZoneList.nullSafety(),
//                            mStatus
//                        )
//
//                }
//                if (mResponseEntity?.mVoidAndReissueList != null) {
//                    timeDataList?.mVoidAndReissueList =
//                        UpdateTimeDb(
//                            mResponseEntity?.mVoidAndReissueList,
//                            mStatus
//                        )
//                }
//                if (mResponseEntity?.mDeviceList != null) {
//                    timeDataList?.mDeviceList =
//                        UpdateTimeDb(
//                            mResponseEntity?.mDeviceList.nullSafety(),
//                            mStatus
//                        )
//
//                }
//                if (mResponseEntity?.mEquipmentList != null) {
//                    timeDataList?.mEquipmentList =
//                        UpdateTimeDb(
//                            mResponseEntity?.mEquipmentList,
//                            mStatus
//                        )
//                }
//                if (mResponseEntity?.mBlockList != null) {
//                    timeDataList?.mBlockList =
//                        UpdateTimeDb(
//                            mResponseEntity?.mBlockList,
//                            mStatus
//                        )
//                }
//
//                if (mResponseEntity?.mSpaceList != null) {
//                    timeDataList?.mSpaceList =
//                        UpdateTimeDb(
//                            mResponseEntity?.mSpaceList,
//                            mStatus
//                        )
//                }
//
//                if (mResponseEntity?.mSquadList != null) {
//                    timeDataList?.mSquadList =
//                        UpdateTimeDb(
//                            mResponseEntity?.mSquadList,
//                            mStatus
//                        )
//                }
//
//                if (LogUtil.isMunicipalCitationEnabled()){
//                    if (mResponseEntity!!.municipalViolationList != null) {
//                        timeDataList!!.municipalViolationList =
//                            UpdateTimeDb(mResponseEntity?.municipalViolationList, mStatus)
//                    }
//
//                    if (mResponseEntity!!.municipalBlockList != null) {
//                        timeDataList!!.municipalBlockList =
//                            UpdateTimeDb(mResponseEntity?.municipalBlockList, mStatus)
//                    }
//
//                    if (mResponseEntity!!.municipalStreetList != null) {
//                        timeDataList!!.municipalStreetList =
//                            UpdateTimeDb(mResponseEntity?.municipalStreetList, mStatus)
//                    }
//
//                    if (mResponseEntity!!.municipalCityList != null) {
//                        timeDataList!!.municipalCityList =
//                            UpdateTimeDb(mResponseEntity?.municipalCityList, mStatus)
//                    }
//
//                    if (mResponseEntity!!.municipalStateList != null) {
//                        timeDataList!!.municipalStateList =
//                            UpdateTimeDb(mResponseEntity?.municipalStateList, mStatus)
//                    }
//                }
//                //                if(mResponseEntity.getmPrinterList()!=null) {
////                    timeDataList.setmPrinterList(new UpdateTimeDb(mResponseEntity.getmPrinterList(), mStatus));
////                }
//            } else {
//                if (mTimingDatabaseList?.timeList?.activityLayout != null &&
//                    mTimingDatabaseList?.timeList?.activityLayout?.name != null
//                ) {
//                    timeDataList?.activityLayout =
//                        UpdateTimeDb(mTimingDatabaseList?.timeList?.activityLayout?.name, mStatus)
//                }
//                if (mTimingDatabaseList?.timeList?.activityLayout != null &&
//                    mTimingDatabaseList?.timeList?.activityLayout?.name != null
//                ) {
//                    timeDataList?.activityList =
//                        UpdateTimeDb(mTimingDatabaseList?.timeList?.activityList?.name, mStatus)
//                }
//                if (mTimingDatabaseList?.timeList?.agencyList != null &&
//                    mTimingDatabaseList?.timeList?.agencyList?.name != null
//                ) {
//                    timeDataList?.agencyList =
//                        UpdateTimeDb(mTimingDatabaseList?.timeList?.agencyList?.name, mStatus)
//                }
//                if (mTimingDatabaseList?.timeList?.beatList != null &&
//                    mTimingDatabaseList?.timeList?.beatList?.name != null
//                ) {
//                    timeDataList?.beatList =
//                        UpdateTimeDb(mTimingDatabaseList?.timeList?.beatList?.name, mStatus)
//                }
//                if (mTimingDatabaseList?.timeList?.cancelReasonList != null &&
//                    mTimingDatabaseList?.timeList?.cancelReasonList?.name != null
//                ) {
//                    timeDataList?.cancelReasonList =
//                        UpdateTimeDb(
//                            mTimingDatabaseList?.timeList?.cancelReasonList?.name,
//                            mStatus
//                        )
//                }
//                if (mTimingDatabaseList?.timeList?.carBodyStyleList != null &&
//                    mTimingDatabaseList?.timeList?.carBodyStyleList?.name != null
//                ) {
//                    timeDataList?.carBodyStyleList =
//                        UpdateTimeDb(
//                            mTimingDatabaseList?.timeList?.carBodyStyleList?.name,
//                            mStatus
//                        )
//                }
//                if (mTimingDatabaseList?.timeList?.carColorList != null &&
//                    mTimingDatabaseList?.timeList?.carColorList?.name != null
//                ) {
//                    timeDataList?.carColorList =
//                        UpdateTimeDb(mTimingDatabaseList?.timeList?.carColorList?.name, mStatus)
//                }
//                if (mTimingDatabaseList?.timeList?.carModelList != null &&
//                    mTimingDatabaseList?.timeList?.carModelList?.name != null
//                ) {
//                    timeDataList?.carModelList =
//                        UpdateTimeDb(mTimingDatabaseList?.timeList?.carModelList?.name, mStatus)
//                }
//                if (mTimingDatabaseList?.timeList?.carMakeList != null &&
//                    mTimingDatabaseList?.timeList?.carMakeList?.name != null
//                ) {
//                    timeDataList?.carMakeList =
//                        UpdateTimeDb(mTimingDatabaseList?.timeList?.carMakeList?.name, mStatus)
//                }
//                if (mTimingDatabaseList!!.timeList?.citationData != null &&
//                    mTimingDatabaseList!!.timeList?.citationData?.name != null
//                ) {
//                    timeDataList!!.citationData =
//                        UpdateTimeDb(mTimingDatabaseList!!.timeList?.citationData?.name, mStatus)
//                }
//                if (mTimingDatabaseList!!.timeList?.citationLayout != null &&
//                    mTimingDatabaseList!!.timeList?.citationLayout?.name != null
//                ) {
//                    timeDataList!!.citationLayout =
//                        UpdateTimeDb(mTimingDatabaseList!!.timeList?.citationLayout?.name, mStatus)
//                }
//                if (mTimingDatabaseList!!.timeList?.commentsList != null &&
//                    mTimingDatabaseList!!.timeList?.commentsList?.name != null
//                ) {
//                    timeDataList!!.commentsList =
//                        UpdateTimeDb(mTimingDatabaseList!!.timeList?.commentsList?.name, mStatus)
//                }
//                if (mTimingDatabaseList!!.timeList?.decalYearList != null &&
//                    mTimingDatabaseList!!.timeList?.decalYearList?.name != null
//                ) {
//                    timeDataList!!.decalYearList =
//                        UpdateTimeDb(mTimingDatabaseList!!.timeList?.decalYearList?.name, mStatus)
//                }
//                if (mTimingDatabaseList!!.timeList?.directionList != null &&
//                    mTimingDatabaseList!!.timeList?.directionList?.name != null
//                ) {
//                    timeDataList!!.directionList =
//                        UpdateTimeDb(mTimingDatabaseList!!.timeList?.directionList?.name, mStatus)
//                }
//                if (mTimingDatabaseList!!.timeList?.exemptData != null &&
//                    mTimingDatabaseList!!.timeList?.exemptData?.name != null
//                ) {
//                    timeDataList!!.exemptData =
//                        UpdateTimeDb(mTimingDatabaseList!!.timeList?.exemptData?.name, mStatus)
//                }
//                if (mTimingDatabaseList!!.timeList?.lotList != null &&
//                    mTimingDatabaseList!!.timeList?.lotList?.name != null
//                ) {
//                    timeDataList!!.lotList =
//                        UpdateTimeDb(mTimingDatabaseList!!.timeList?.lotList?.name, mStatus)
//                }
//                if (mTimingDatabaseList!!.timeList?.makeModelColorData != null &&
//                    mTimingDatabaseList!!.timeList?.makeModelColorData?.name != null
//                ) {
//                    timeDataList!!.makeModelColorData = UpdateTimeDb(
//                        mTimingDatabaseList!!.timeList?.makeModelColorData?.name,
//                        mStatus
//                    )
//                }
//                if (mTimingDatabaseList!!.timeList?.meterList != null &&
//                    mTimingDatabaseList!!.timeList?.meterList?.name != null
//                ) {
//                    timeDataList!!.meterList =
//                        UpdateTimeDb(mTimingDatabaseList!!.timeList?.meterList?.name, mStatus)
//                }
//                if (mTimingDatabaseList!!.timeList?.notesList != null &&
//                    mTimingDatabaseList!!.timeList?.notesList?.name != null
//                ) {
//                    timeDataList!!.notesList =
//                        UpdateTimeDb(mTimingDatabaseList!!.timeList?.notesList?.name, mStatus)
//                }
//                if (mTimingDatabaseList!!.timeList?.paymentData != null &&
//                    mTimingDatabaseList!!.timeList?.paymentData?.name != null
//                ) {
//                    timeDataList!!.paymentData =
//                        UpdateTimeDb(mTimingDatabaseList!!.timeList?.paymentData?.name, mStatus)
//                }
//                if (mTimingDatabaseList!!.timeList?.permitData != null &&
//                    mTimingDatabaseList!!.timeList?.permitData?.name != null
//                ) {
//                    timeDataList!!.permitData =
//                        UpdateTimeDb(mTimingDatabaseList!!.timeList?.permitData?.name, mStatus)
//                }
//                if (mTimingDatabaseList!!.timeList?.radioList != null &&
//                    mTimingDatabaseList!!.timeList?.radioList?.name != null
//                ) {
//                    timeDataList!!.radioList =
//                        UpdateTimeDb(mTimingDatabaseList!!.timeList?.radioList?.name, mStatus)
//                }
//                if (mTimingDatabaseList!!.timeList?.regulationTimeList != null &&
//                    mTimingDatabaseList!!.timeList?.regulationTimeList?.name != null
//                ) {
//                    timeDataList!!.regulationTimeList = UpdateTimeDb(
//                        mTimingDatabaseList!!.timeList?.regulationTimeList?.name,
//                        mStatus
//                    )
//                }
//                if (mTimingDatabaseList!!.timeList?.remarksList != null &&
//                    mTimingDatabaseList!!.timeList?.remarksList?.name != null
//                ) {
//                    timeDataList!!.remarksList =
//                        UpdateTimeDb(mTimingDatabaseList!!.timeList?.remarksList?.name, mStatus)
//                }
//                if (mTimingDatabaseList!!.timeList?.scofflawData != null &&
//                    mTimingDatabaseList!!.timeList?.scofflawData?.name != null
//                ) {
//                    timeDataList!!.scofflawData =
//                        UpdateTimeDb(mTimingDatabaseList!!.timeList?.scofflawData?.name, mStatus)
//                }
//                if (mTimingDatabaseList!!.timeList?.shiftList != null &&
//                    mTimingDatabaseList!!.timeList?.shiftList?.name != null
//                ) {
//                    timeDataList!!.shiftList =
//                        UpdateTimeDb(mTimingDatabaseList!!.timeList?.shiftList?.name, mStatus)
//                }
//                if (mTimingDatabaseList!!.timeList?.sideList != null &&
//                    mTimingDatabaseList!!.timeList?.sideList?.name != null
//                ) {
//                    timeDataList!!.sideList =
//                        UpdateTimeDb(mTimingDatabaseList!!.timeList?.sideList?.name, mStatus)
//                }
//                if (mTimingDatabaseList!!.timeList?.stateList != null &&
//                    mTimingDatabaseList!!.timeList?.stateList?.name != null
//                ) {
//                    timeDataList!!.stateList =
//                        UpdateTimeDb(mTimingDatabaseList!!.timeList?.stateList?.name, mStatus)
//                }
//                if (mTimingDatabaseList!!.timeList?.stolenData != null &&
//                    mTimingDatabaseList!!.timeList?.stolenData?.name != null
//                ) {
//                    timeDataList!!.stolenData =
//                        UpdateTimeDb(mTimingDatabaseList!!.timeList?.stolenData?.name, mStatus)
//                }
//                if (mTimingDatabaseList!!.timeList?.streetList != null &&
//                    mTimingDatabaseList!!.timeList?.streetList?.name != null
//                ) {
//                    timeDataList!!.streetList =
//                        UpdateTimeDb(mTimingDatabaseList!!.timeList?.streetList?.name, mStatus)
//                }
//                if (mTimingDatabaseList!!.timeList?.supervisorList != null &&
//                    mTimingDatabaseList!!.timeList?.supervisorList?.name != null
//                ) {
//                    timeDataList!!.supervisorList =
//                        UpdateTimeDb(mTimingDatabaseList!!.timeList?.supervisorList?.name, mStatus)
//                }
//                if (mTimingDatabaseList!!.timeList?.tierStemList != null &&
//                    mTimingDatabaseList!!.timeList?.tierStemList?.name != null
//                ) {
//                    timeDataList!!.tierStemList =
//                        UpdateTimeDb(mTimingDatabaseList!!.timeList?.tierStemList?.name, mStatus)
//                }
//                if (mTimingDatabaseList!!.timeList?.timingData != null &&
//                    mTimingDatabaseList!!.timeList?.timingData?.name != null
//                ) {
//                    timeDataList!!.timingData =
//                        UpdateTimeDb(mTimingDatabaseList!!.timeList?.timingData?.name, mStatus)
//                }
//                if (mTimingDatabaseList!!.timeList?.timingRecordLayout != null &&
//                    mTimingDatabaseList!!.timeList?.timingRecordLayout?.name != null
//                ) {
//                    timeDataList!!.timingRecordLayout = UpdateTimeDb(
//                        mTimingDatabaseList!!.timeList?.timingRecordLayout?.name,
//                        mStatus
//                    )
//                }
//                if (mTimingDatabaseList!!.timeList?.vehiclePlateTypeList != null &&
//                    mTimingDatabaseList!!.timeList?.vehiclePlateTypeList?.name != null
//                ) {
//                    timeDataList!!.vehiclePlateTypeList = UpdateTimeDb(
//                        mTimingDatabaseList!!.timeList?.vehiclePlateTypeList?.name,
//                        mStatus
//                    )
//                }
//                if (mTimingDatabaseList?.timeList?.violationList != null &&
//                    mTimingDatabaseList?.timeList?.violationList?.name != null
//                ) {
//                    timeDataList?.violationList =
//                        UpdateTimeDb(mTimingDatabaseList?.timeList?.violationList?.name, mStatus)
//                }
//                if (mTimingDatabaseList?.timeList?.zoneList != null &&
//                    mTimingDatabaseList?.timeList?.zoneList?.name != null
//                ) {
//                    timeDataList?.zoneList =
//                        UpdateTimeDb(mTimingDatabaseList?.timeList?.zoneList?.name, mStatus)
//                }
//                if (mTimingDatabaseList?.timeList?.mCityZoneList != null &&
//                    mTimingDatabaseList?.timeList?.mCityZoneList?.name != null
//                ) {
//                    timeDataList?.mCityZoneList =
//                        UpdateTimeDb(
//                            mTimingDatabaseList?.timeList?.mCityZoneList?.name,
//                            mStatus
//                        )
//                }
//                if (mTimingDatabaseList?.timeList?.mVoidAndReissueList != null &&
//                    mTimingDatabaseList?.timeList?.mVoidAndReissueList?.name != null
//                ) {
//                    timeDataList?.mVoidAndReissueList =
//                        UpdateTimeDb(
//                            mTimingDatabaseList?.timeList?.mVoidAndReissueList?.name,
//                            mStatus
//                        )
//                }
//                if (mTimingDatabaseList?.timeList?.mDeviceList != null &&
//                    mTimingDatabaseList?.timeList?.mDeviceList?.name != null
//                ) {
//                    timeDataList?.mDeviceList =
//                        UpdateTimeDb(
//                            mTimingDatabaseList?.timeList?.mDeviceList?.name,
//                            mStatus
//                        )
//                }
//                if (mTimingDatabaseList?.timeList?.mEquipmentList != null &&
//                    mTimingDatabaseList?.timeList?.mEquipmentList?.name != null
//                ) {
//                    timeDataList?.mEquipmentList =
//                        UpdateTimeDb(
//                            mTimingDatabaseList?.timeList?.mEquipmentList?.name,
//                            mStatus
//                        )
//                }
//                if (mTimingDatabaseList?.timeList?.mBlockList != null &&
//                    mTimingDatabaseList?.timeList?.mBlockList?.name != null
//                ) {
//                    timeDataList?.mBlockList =
//                        UpdateTimeDb(
//                            mTimingDatabaseList?.timeList?.mBlockList?.name,
//                            mStatus
//                        )
//                }
//
//                if (mTimingDatabaseList?.timeList?.mSpaceList != null &&
//                    mTimingDatabaseList?.timeList?.mSpaceList?.name != null
//                ) {
//                    timeDataList?.mSpaceList =
//                        UpdateTimeDb(
//                            mTimingDatabaseList?.timeList?.mSpaceList?.name,
//                            mStatus
//                        )
//                }
//
//                if (mTimingDatabaseList?.timeList?.mSquadList != null &&
//                    mTimingDatabaseList?.timeList?.mSquadList?.name != null
//                ) {
//                    timeDataList?.mSquadList =
//                        UpdateTimeDb(
//                            mTimingDatabaseList?.timeList?.mSquadList?.name, mStatus
//                        )
//                }
//
//                if (LogUtil.isMunicipalCitationEnabled()){
//                    if (mTimingDatabaseList?.timeList?.municipalViolationList != null &&
//                        mTimingDatabaseList?.timeList?.municipalViolationList?.name != null
//                    ) {
//                        timeDataList?.municipalViolationList =
//                            UpdateTimeDb(mTimingDatabaseList?.timeList?.municipalViolationList?.name, mStatus)
//                    }
//
//                    if (mTimingDatabaseList?.timeList?.municipalBlockList != null &&
//                        mTimingDatabaseList?.timeList?.municipalBlockList?.name != null
//                    ) {
//                        timeDataList?.municipalBlockList =
//                            UpdateTimeDb(mTimingDatabaseList?.timeList?.municipalBlockList?.name, mStatus)
//                    }
//
//                    if (mTimingDatabaseList?.timeList?.municipalStreetList != null &&
//                        mTimingDatabaseList?.timeList?.municipalStreetList?.name != null
//                    ) {
//                        timeDataList?.municipalStreetList =
//                            UpdateTimeDb(mTimingDatabaseList?.timeList?.municipalStreetList?.name, mStatus)
//                    }
//
//                    if (mTimingDatabaseList?.timeList?.municipalCityList != null &&
//                        mTimingDatabaseList?.timeList?.municipalCityList?.name != null
//                    ) {
//                        timeDataList?.municipalCityList =
//                            UpdateTimeDb(mTimingDatabaseList?.timeList?.municipalCityList?.name, mStatus)
//                    }
//
//                    if (mTimingDatabaseList?.timeList?.municipalStateList != null &&
//                        mTimingDatabaseList?.timeList?.municipalStateList?.name != null
//                    ) {
//                        timeDataList?.municipalStateList =
//                            UpdateTimeDb(mTimingDatabaseList?.timeList?.municipalStateList?.name, mStatus)
//                    }
//                }
//
//                //                if(mTimingDatabaseList.getTimeList().getmPrinterList()!=null &&
////                        mTimingDatabaseList.getTimeList().getmPrinterList().getName()!=null) {
////                    timeDataList.setmPrinterList(new UpdateTimeDb(mTimingDatabaseList.getTimeList().getmPrinterList().getName(), mStatus));
////                }
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }
//
//    /* Call Api For update profile */
//    private fun callUploadImages(file: File?, num: Int) {
//        if (isInternetAvailable(this@LoginActivity)) {
//            val requestFile = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file!!)
//            val files = MultipartBody.Part.createFormData(
//                "files",
//                if (file != null) file.name else "",
//                requestFile
//            )
//            val mDropdownList: Array<String>
//            mDropdownList = if (file!!.name.contains("_"+FILE_NAME_FACSIMILE_PRINT_BITMAP)) {
//                arrayOf(mCitationNumberId + "_" + num + "_" + FILE_NAME_FACSIMILE_PRINT_BITMAP)
//            } else {
//                arrayOf(mCitationNumberId + "_" + num)
//            }
//            val mRequestBodyType =
//                RequestBody.create("text/plain".toMediaTypeOrNull(), "CitationImages")
//            mUploadImageViewModel!!.hitUploadImagesApi(mDropdownList, mRequestBodyType, files)
//        } else {
//            LogUtil.printToastMSG(
//                this@LoginActivity,
//                getString(R.string.err_msg_connection_was_refused)
//            )
//        }
//
//        //SaveTask().execute()
//    }
//
//    /* Call Api For citation layout details */
//    private fun callCreateTicketApi(mIssuranceModel: CitationInsurranceDatabaseModel) {
//        if (isInternetAvailable(this@LoginActivity)) {
//            if (mIssuranceModel?.citationData?.municipalCitationMotoristDetailsModel == null) {
//                val createTicketRequest = CreateTicketRequest()
//                val locationDetails = LocationDetails()
//                locationDetails.street = mIssuranceModel.citationData?.location?.street
//                locationDetails.street_lookup_code =
//                    mIssuranceModel?.citationData?.location?.mStreetLookupCode.nullSafety()
//                locationDetails.block = mIssuranceModel.citationData?.location?.block
//                locationDetails.side = mIssuranceModel.citationData?.location?.side
//                locationDetails.meter = mIssuranceModel.citationData?.location?.meterName
//                locationDetails.direction = mIssuranceModel.citationData?.location?.direction
//                locationDetails.lot = mIssuranceModel.citationData?.location?.lot
//                createTicketRequest.locationDetails = locationDetails
//                val vehicleDetails = VehicleDetails()
//                vehicleDetails.body_style = mIssuranceModel.citationData?.vehicle?.bodyStyle
//                vehicleDetails.body_style_lookup_code =
//                    mIssuranceModel?.citationData?.vehicle?.body_style_lookup_code.nullSafety()
//                vehicleDetails.decal_year = mIssuranceModel.citationData?.vehicle?.decalYear
//                vehicleDetails.decal_number = mIssuranceModel.citationData?.vehicle?.decalNumber
//                vehicleDetails.vin_number = mIssuranceModel.citationData?.vehicle?.vinNumber
//                vehicleDetails.make = mIssuranceModel.citationData?.vehicle?.make
//                vehicleDetails.model_lookup_code =
//                    mIssuranceModel?.citationData!!.vehicle!!.model_lookup_code.nullSafety("")
//                vehicleDetails.color = mIssuranceModel.citationData?.vehicle?.color
//                vehicleDetails.model = mIssuranceModel.citationData?.vehicle?.model
//                vehicleDetails.lprNo = mIssuranceModel.citationData?.vehicle?.licensePlate
//                vehicleDetails.state = mIssuranceModel.citationData?.vehicle?.state
//                vehicleDetails.mLicenseExpiry =
//                    if (mIssuranceModel.citationData?.vehicle?.expiration != null) mIssuranceModel.citationData?.vehicle?.expiration else ""
//
//                createTicketRequest.vehicleDetails = vehicleDetails
//                val violationDetails = ViolationDetails()
//                violationDetails.code =
//                    mIssuranceModel.citationData?.voilation?.code //mAutoComTextViewCode.getEditableText().toString().trim());
//                violationDetails.description =
//                    mIssuranceModel.citationData?.voilation?.locationDescr
//                try {
//                    violationDetails.fine =
//                        splitDoller(mIssuranceModel.citationData?.voilation?.amount.nullSafety())
//                    violationDetails.late_fine =
//                        splitDoller(mIssuranceModel.citationData?.voilation?.amountDueDate.nullSafety())
//                    violationDetails.due_15_days =
//                        splitDoller(mIssuranceModel.citationData?.voilation?.dueDate.nullSafety())
//                    violationDetails.due_30_days =
//                        splitDoller(mIssuranceModel.citationData?.voilation?.dueDate30.nullSafety())
//                    violationDetails.due_45_days =
//                        splitDoller(mIssuranceModel.citationData?.voilation?.dueDate45.nullSafety())
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                }
//                createTicketRequest.violationDetails = violationDetails
//
//                try {
//                    val invoiceFeeStructure = InvoiceFeeStructure()
//                    invoiceFeeStructure.mSaleTax =
//                        if (mIssuranceModel?.citationData?.voilation?.dueDateCost != null && !mIssuranceModel?.citationData?.voilation?.dueDateCost.equals(
//                                "null"
//                            )
//                        ) mIssuranceModel?.citationData?.voilation?.dueDateCost.nullSafety("0")
//                            .toDouble() else 0.0
//                    invoiceFeeStructure.mCitationFee =
//                        if (mIssuranceModel?.citationData?.voilation?.dueDateCitationFee != null && !mIssuranceModel?.citationData?.voilation?.dueDateCitationFee.equals(
//                                "null"
//                            )
//                        ) mIssuranceModel?.citationData?.voilation?.dueDateCitationFee.nullSafety("0")
//                            .toDouble() else 0.0
//                    invoiceFeeStructure.mParkingFee =
//                        if (mIssuranceModel?.citationData?.voilation?.dueDateParkingFee != null && !mIssuranceModel?.citationData?.voilation?.dueDateParkingFee.equals(
//                                "null"
//                            )
//                        ) mIssuranceModel?.citationData?.voilation?.dueDateParkingFee.nullSafety("0")
//                            .toDouble() else 0.0
//                    createTicketRequest.invoiceFeeStructure = invoiceFeeStructure
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                }
//
//                val officerDetails = OfficerDetails()
//                //officerDetails.setOfficerId(mData.getSiteOfficerId());
//                officerDetails.badgeId = mIssuranceModel.citationData?.officer?.badgeId
//                officerDetails.officer_lookup_code =
//                    mIssuranceModel?.citationData?.officer?.officer_lookup_code.nullSafety()
//                if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DURANGO, true) ||
//                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LITTLEROCK, true) ||
//                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKTEXAS, true) ||
//                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOTTLEWORKS_IN, true) ||
//                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RUTGERS, true) ||
//                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PARKX, true) ||
//                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKPARKING, true)
//                ) {
//                    officerDetails.officer_name =
//                        Util.officerNameForBurbank(mIssuranceModel?.citationData?.officer?.officerDetails.nullSafety())
//                } else {
//                    officerDetails.officer_name =
//                        AppUtils.getOfficerName(
//                            mIssuranceModel?.citationData?.officer?.officerDetails.toString().trim()
//                                .nullSafety()
//                        )
//                }
////            officerDetails.officer_name =  AppUtils.getOfficerName(mIssuranceModel?.citationData?.officer?.officerDetails.toString().trim().nullSafety())
//                if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DUNCAN, true) ||
//                    BuildConfig.FLAVOR.equals(DuncanBrandingApp13()) ||
//                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PILOTPITTSBURGPA, true)
//                ) {
//                    officerDetails.peo_fname = AppUtils.getPOEName(
//                        mIssuranceModel?.citationData?.officer?.officerDetails.toString().trim()
//                            .nullSafety(), 0
//                    )
//                    officerDetails.peo_lname = AppUtils.getPOEName(
//                        mIssuranceModel?.citationData?.officer?.officerDetails.toString().trim()
//                            .nullSafety(), 1
//                    )
//                    officerDetails.peo_name =
//                        officerDetails.peo_fname + ", " + officerDetails.peo_lname
//                }
//                officerDetails.signature = ""
//                try {
//                    officerDetails.squad = mIssuranceModel.citationData?.officer?.squad
//                    officerDetails.beat = mIssuranceModel.citationData?.officer?.beat
//                    officerDetails.agency = mIssuranceModel.citationData?.officer?.agency
//                    officerDetails.mShift = mIssuranceModel.citationData?.officer?.shift
//                    officerDetails.zone =
//                        if (mIssuranceModel.citationData?.location?.pcbZone != null) mIssuranceModel.citationData?.location?.pcbZone else if (mIssuranceModel.citationData?.officer?.zone != null) mIssuranceModel.citationData?.officer?.zone else ""
//
//                    val welcomeForm: WelcomeForm? = mDb!!.dbDAO!!.getWelcomeForm()
////                officerDetails.mDdeviceId = welcomeForm!!.officerDeviceId
//                    officerDetails.mDdeviceId = welcomeForm!!.officerDeviceName
//                    officerDetails.mDdeviceFriendlyName = welcomeForm!!.officerDeviceName
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                }
//                val commentsDetails = CommentsDetails()
//                commentsDetails.note1 = mIssuranceModel.citationData?.locationNotes
//                commentsDetails.note2 = mIssuranceModel.citationData?.locationNotes1
//                commentsDetails.remark1 = mIssuranceModel.citationData?.locationRemarks
//                commentsDetails.remark2 = mIssuranceModel.citationData?.locationRemarks1
//                createTicketRequest.commentsDetails = commentsDetails
//                createTicketRequest.officerDetails = officerDetails
//                val headerDetails = HeaderDetails()
//                headerDetails.citationNumber = mIssuranceModel.citationData?.ticketNumber
//                headerDetails.timestamp = mIssuranceModel.citationData?.ticketDate
//                createTicketRequest.headerDetails = headerDetails
//                createTicketRequest.lprNumber = mIssuranceModel.citationData?.vehicle?.licensePlate
//                createTicketRequest.code =
//                    mIssuranceModel.citationData?.voilation?.code //mIssuranceModel.getCitationData().getCode());
//                createTicketRequest.hearingDate =
//                    mIssuranceModel?.citationData?.hearingDate
//                createTicketRequest.ticketNo = mIssuranceModel.citationData?.ticketNumber
//                createTicketRequest.type = mIssuranceModel.citationData?.ticketType
//                /*List<String> mImageList = new ArrayList<>();
//                mImageList.add("https://pkg.go.dev/static/legacy/img/go-logo-blue.svg");*/
////            val mImage: MutableList<String> = ArrayList()
////            if (mIssuranceModel.citationData?.imagesList != null) {
////                for (i in mIssuranceModel.citationData?.imagesList!!.indices) {
////                    mImage.add(mIssuranceModel.citationData?.imagesList!![i].citationImage.nullSafety())
////                }
////            }
////            if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITYOFSANDIEGO,true)){
//                createTicketRequest.timeLimitEnforcementObservedTime =
//                    mIssuranceModel?.citationData?.officer?.observationTime
////            }else {
////                createTicketRequest.timeLimitEnforcementObservedTime =  sharedPreference.read(SharedPrefKey.TIMING_RECORD_FOR_OBSERVE_TIME, "")
////            }
//                createTicketRequest.imageUrls = mImages
////            createTicketRequest.imageUrls = mImage
//                createTicketRequest.notes = mIssuranceModel.citationData?.locationNotes
//                createTicketRequest.status = "Valid"
//                createTicketRequest.citationStartTimestamp = mIssuranceModel.citationData?.startTime
//                createTicketRequest.citationIssueTimestamp = mIssuranceModel.citationData?.issueTime
//                createTicketRequest.isReissue = false
//                mCreateTicketViewModel!!.hitCreateTicketApi(createTicketRequest)
//
//                try {
//                    ApiLogsClass.writeApiPayloadTex(
//                        BaseApplication.instance?.applicationContext!!,
//                        "------------LOGIN Create API-----------------"
//                    )
//                    ApiLogsClass.writeApiPayloadTex(
//                        BaseApplication.instance?.applicationContext!!,
//                        "REQUEST: " + ObjectMapperProvider.instance.writeValueAsString(createTicketRequest)
//                    )
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                }
//            } else {
//                val createTicketRequest = CreateMunicipalCitationTicketRequest()
//                val locationDetails = MunicipalCitationLocationDetails()
//                locationDetails.street = mIssuranceModel.citationData?.location?.street
//                locationDetails.street_lookup_code =
//                    mIssuranceModel?.citationData?.location?.mStreetLookupCode.nullSafety()
//                locationDetails.block = mIssuranceModel.citationData?.location?.block
//                locationDetails.side = mIssuranceModel.citationData?.location?.side
//                locationDetails.meter = mIssuranceModel.citationData?.location?.meterName
//                locationDetails.direction = mIssuranceModel.citationData?.location?.direction
//                locationDetails.lot = mIssuranceModel.citationData?.location?.lot
//                createTicketRequest.locationDetails = locationDetails
//                val vehicleDetails = MunicipalCitationVehicleDetails()
//                vehicleDetails.body_style = mIssuranceModel.citationData?.vehicle?.bodyStyle
//                vehicleDetails.body_style_lookup_code =
//                    mIssuranceModel?.citationData?.vehicle?.body_style_lookup_code.nullSafety()
//                vehicleDetails.decal_year = mIssuranceModel.citationData?.vehicle?.decalYear
//                vehicleDetails.decal_number = mIssuranceModel.citationData?.vehicle?.decalNumber
//                vehicleDetails.vin_number = mIssuranceModel.citationData?.vehicle?.vinNumber
//                vehicleDetails.make = mIssuranceModel.citationData?.vehicle?.make
//                vehicleDetails.model_lookup_code =
//                    mIssuranceModel?.citationData!!.vehicle!!.model_lookup_code.nullSafety("")
//                vehicleDetails.color = mIssuranceModel.citationData?.vehicle?.color
//                vehicleDetails.model = mIssuranceModel.citationData?.vehicle?.model
//                vehicleDetails.lprNo = mIssuranceModel.citationData?.vehicle?.licensePlate
//                vehicleDetails.state = mIssuranceModel.citationData?.vehicle?.state
//                vehicleDetails.mLicenseExpiry =
//                    if (mIssuranceModel.citationData?.vehicle?.expiration != null) mIssuranceModel.citationData?.vehicle?.expiration else ""
//
//                createTicketRequest.vehicleDetails = vehicleDetails
//                val violationDetails = MunicipalCitationViolationDetails()
//                violationDetails.code =
//                    mIssuranceModel.citationData?.voilation?.code //mAutoComTextViewCode.getEditableText().toString().trim());
//                violationDetails.description =
//                    mIssuranceModel.citationData?.voilation?.locationDescr
//                try {
//                    violationDetails.fine =
//                        splitDoller(mIssuranceModel.citationData?.voilation?.amount.nullSafety())
//                    violationDetails.late_fine =
//                        splitDoller(mIssuranceModel.citationData?.voilation?.amountDueDate.nullSafety())
//                    violationDetails.due_15_days =
//                        splitDoller(mIssuranceModel.citationData?.voilation?.dueDate.nullSafety())
//                    violationDetails.due_30_days =
//                        splitDoller(mIssuranceModel.citationData?.voilation?.dueDate30.nullSafety())
//                    violationDetails.due_45_days =
//                        splitDoller(mIssuranceModel.citationData?.voilation?.dueDate45.nullSafety())
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                }
//                createTicketRequest.violationDetails = violationDetails
//
//                val motoristDetails = MotoristDetailsModel()
//                motoristDetails.motoristFirstName = mIssuranceModel.citationData?.municipalCitationMotoristDetailsModel?.motoristFirstName.nullSafety().uppercase()
//                motoristDetails.motoristMiddleName = mIssuranceModel.citationData?.municipalCitationMotoristDetailsModel?.motoristMiddleName.nullSafety().uppercase()
//                motoristDetails.motoristLastName = mIssuranceModel.citationData?.municipalCitationMotoristDetailsModel?.motoristLastName.nullSafety().uppercase()
//                motoristDetails.motoristDateOfBirth = mIssuranceModel.citationData?.municipalCitationMotoristDetailsModel?.motoristDateOfBirth.nullSafety().uppercase()
//                motoristDetails.motoristDlNumber = mIssuranceModel.citationData?.municipalCitationMotoristDetailsModel?.motoristDlNumber.nullSafety().uppercase()
//                motoristDetails.motoristAddressBlock = mIssuranceModel.citationData?.municipalCitationMotoristDetailsModel?.motoristAddressBlock.nullSafety().uppercase()
//                motoristDetails.motoristAddressStreet = mIssuranceModel.citationData?.municipalCitationMotoristDetailsModel?.motoristAddressStreet.nullSafety().uppercase()
//                motoristDetails.motoristAddressCity = mIssuranceModel.citationData?.municipalCitationMotoristDetailsModel?.motoristAddressCity.nullSafety().uppercase()
//                motoristDetails.motoristAddressState = mIssuranceModel.citationData?.municipalCitationMotoristDetailsModel?.motoristAddressState.nullSafety().uppercase()
//                motoristDetails.motoristAddressZip = mIssuranceModel.citationData?.municipalCitationMotoristDetailsModel?.motoristAddressZip.nullSafety().uppercase()
//
//                createTicketRequest.motoristDetails = motoristDetails
//
//                try {
//                    val invoiceFeeStructure = MunicipalCitationInvoiceFeeStructure()
//                    invoiceFeeStructure.mSaleTax =
//                        if (mIssuranceModel?.citationData?.voilation?.dueDateCost != null && !mIssuranceModel?.citationData?.voilation?.dueDateCost.equals(
//                                "null"
//                            )
//                        ) mIssuranceModel?.citationData?.voilation?.dueDateCost.nullSafety("0")
//                            .toDouble() else 0.0
//                    invoiceFeeStructure.mCitationFee =
//                        if (mIssuranceModel?.citationData?.voilation?.dueDateCitationFee != null && !mIssuranceModel?.citationData?.voilation?.dueDateCitationFee.equals(
//                                "null"
//                            )
//                        ) mIssuranceModel?.citationData?.voilation?.dueDateCitationFee.nullSafety("0")
//                            .toDouble() else 0.0
//                    invoiceFeeStructure.mParkingFee =
//                        if (mIssuranceModel?.citationData?.voilation?.dueDateParkingFee != null && !mIssuranceModel?.citationData?.voilation?.dueDateParkingFee.equals(
//                                "null"
//                            )
//                        ) mIssuranceModel?.citationData?.voilation?.dueDateParkingFee.nullSafety("0")
//                            .toDouble() else 0.0
//                    createTicketRequest.invoiceFeeStructure = invoiceFeeStructure
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                }
//
//                val officerDetails = MunicipalCitationOfficerDetails()
//                //officerDetails.setOfficerId(mData.getSiteOfficerId());
//                officerDetails.badgeId = mIssuranceModel.citationData?.officer?.badgeId
//                officerDetails.officer_lookup_code =
//                    mIssuranceModel?.citationData?.officer?.officer_lookup_code.nullSafety()
//                if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DURANGO, true) ||
//                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LITTLEROCK, true) ||
//                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKTEXAS, true) ||
//                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOTTLEWORKS_IN, true) ||
//                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RUTGERS, true) ||
//                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PARKX, true) ||
//                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKPARKING, true)
//                ) {
//                    officerDetails.officer_name =
//                        Util.officerNameForBurbank(mIssuranceModel?.citationData?.officer?.officerDetails.nullSafety())
//                } else {
//                    officerDetails.officer_name =
//                        AppUtils.getOfficerName(
//                            mIssuranceModel?.citationData?.officer?.officerDetails.toString().trim()
//                                .nullSafety()
//                        )
//                }
////            officerDetails.officer_name =  AppUtils.getOfficerName(mIssuranceModel?.citationData?.officer?.officerDetails.toString().trim().nullSafety())
//                if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DUNCAN, true) ||
//                    BuildConfig.FLAVOR.equals(DuncanBrandingApp13()) ||
//                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PILOTPITTSBURGPA, true)
//                ) {
//                    officerDetails.peo_fname = AppUtils.getPOEName(
//                        mIssuranceModel?.citationData?.officer?.officerDetails.toString().trim()
//                            .nullSafety(), 0
//                    )
//                    officerDetails.peo_lname = AppUtils.getPOEName(
//                        mIssuranceModel?.citationData?.officer?.officerDetails.toString().trim()
//                            .nullSafety(), 1
//                    )
//                    officerDetails.peo_name =
//                        officerDetails.peo_fname + ", " + officerDetails.peo_lname
//                }
//                officerDetails.signature = ""
//                try {
//                    officerDetails.squad = mIssuranceModel.citationData?.officer?.squad
//                    officerDetails.beat = mIssuranceModel.citationData?.officer?.beat
//                    officerDetails.agency = mIssuranceModel.citationData?.officer?.agency
//                    officerDetails.mShift = mIssuranceModel.citationData?.officer?.shift
//                    officerDetails.zone =
//                        if (mIssuranceModel.citationData?.location?.pcbZone != null) mIssuranceModel.citationData?.location?.pcbZone else if (mIssuranceModel.citationData?.officer?.zone != null) mIssuranceModel.citationData?.officer?.zone else ""
//
//                    val welcomeForm: WelcomeForm? = mDb!!.dbDAO!!.getWelcomeForm()
////                officerDetails.mDdeviceId = welcomeForm!!.officerDeviceId
//                    officerDetails.mDdeviceId = welcomeForm!!.officerDeviceName
//                    officerDetails.mDdeviceFriendlyName = welcomeForm!!.officerDeviceName
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                }
//                val commentsDetails = MunicipalCitationCommentsDetails()
//                commentsDetails.note1 = mIssuranceModel.citationData?.locationNotes
//                commentsDetails.note2 = mIssuranceModel.citationData?.locationNotes1
//                commentsDetails.remark1 = mIssuranceModel.citationData?.locationRemarks
//                commentsDetails.remark2 = mIssuranceModel.citationData?.locationRemarks1
//                createTicketRequest.commentsDetails = commentsDetails
//                createTicketRequest.officerDetails = officerDetails
//                val headerDetails = MunicipalCitationHeaderDetails()
//                headerDetails.citationNumber = mIssuranceModel.citationData?.ticketNumber
//                headerDetails.timestamp = mIssuranceModel.citationData?.ticketDate
//                createTicketRequest.headerDetails = headerDetails
//                createTicketRequest.lprNumber = mIssuranceModel.citationData?.vehicle?.licensePlate
//                createTicketRequest.code =
//                    mIssuranceModel.citationData?.voilation?.code //mIssuranceModel.getCitationData().getCode());
//                createTicketRequest.hearingDate =
//                    mIssuranceModel?.citationData?.hearingDate
//                createTicketRequest.ticketNo = mIssuranceModel.citationData?.ticketNumber
//                createTicketRequest.type = mIssuranceModel.citationData?.ticketType
//                /*List<String> mImageList = new ArrayList<>();
//                mImageList.add("https://pkg.go.dev/static/legacy/img/go-logo-blue.svg");*/
////            val mImage: MutableList<String> = ArrayList()
////            if (mIssuranceModel.citationData?.imagesList != null) {
////                for (i in mIssuranceModel.citationData?.imagesList!!.indices) {
////                    mImage.add(mIssuranceModel.citationData?.imagesList!![i].citationImage.nullSafety())
////                }
////            }
////            if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITYOFSANDIEGO,true)){
//                createTicketRequest.timeLimitEnforcementObservedTime =
//                    mIssuranceModel?.citationData?.officer?.observationTime
////            }else {
////                createTicketRequest.timeLimitEnforcementObservedTime =  sharedPreference.read(SharedPrefKey.TIMING_RECORD_FOR_OBSERVE_TIME, "")
////            }
//                createTicketRequest.imageUrls = mImages
////            createTicketRequest.imageUrls = mImage
//                createTicketRequest.notes = mIssuranceModel.citationData?.locationNotes
//                createTicketRequest.status = "Valid"
//                createTicketRequest.citationStartTimestamp = mIssuranceModel.citationData?.startTime
//                createTicketRequest.citationIssueTimestamp = mIssuranceModel.citationData?.issueTime
//                createTicketRequest.isReissue = false
//                mCreateMunicipalCitationTicketViewModel?.hitCreateMunicipalCitationTicketApi(createTicketRequest)
//                try {
//                    ApiLogsClass.writeApiPayloadTex(
//                        BaseApplication.instance?.applicationContext!!,
//                        "------------LOGIN Create Municipal Citation API-----------------"
//                    )
//                    ApiLogsClass.writeApiPayloadTex(
//                        BaseApplication.instance?.applicationContext!!,
//                        "REQUEST: " + ObjectMapperProvider.instance.writeValueAsString(createTicketRequest)
//                    )
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                }
//            }
//        } else {
//            mDb!!.dbDAO!!.updateCitationUploadStatus(1, mIssuranceModel.citationNumber)
//            //LogUtil.printToastMSG(LprPreviewActivity.this, getString(R.string.err_msg_connection_was_refused));
//        }
//    }
//
//    override val locationEventCallBack: Unit
//        get() {}
//
//    private fun setBuildVersion() {
//        try {
//            val pInfo = packageManager.getPackageInfo(packageName, 0)
//            val versionName = pInfo.versionName //Version Name
//            val verCode = pInfo.versionCode //Version Code
//
////            int versionCode = BuildConfig.VERSION_CODE;
////            String versionName = BuildConfig.VERSION_NAME;
////            if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SEPTA, true)) {
////                appCompatEditTextVersionName!!.text = "$versionName" + "\n" + mUUID
////            } else {
////                appCompatEditTextVersionName!!.text = "$versionName "
////            }
//
//            appCompatEditTextVersionName?.text = "$versionName\n$mUUID"
//
//            if (BuildConfig.FLAVOR.equals(
//                    Constants.FLAVOR_TYPE_RISE_TEK_INNOVA,
//                    ignoreCase = true
//                )
//            ) {
//                appCompatEditTextSiteName?.text = getString(R.string.cmp_name_title_innova)
//            } else {
//                appCompatEditTextSiteName?.text = getString(R.string.cmp_name_title)
//            }
//            appCompatEditTextCompanyName?.text = getString(R.string.app_name_title)
//
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }
//
//    private fun callCitationDatasetApiShift(mType: String) {
//        if (isInternetAvailable(this@LoginActivity)) {
//            val mDropdownDatasetRequest = DropdownDatasetRequest()
//            mDropdownDatasetRequest.type = mType
//            mDropdownDatasetRequest.shard = 1.toLong()
//            mDropdownDatasetRequest.mSiteId=getSiteId(this@LoginActivity)
//            mCitationDatasetModel?.hitCitationDatasetApiLoginPage(mDropdownDatasetRequest)
//        } else {
//            dismissLoader()
//            printToastMSG(this@LoginActivity, getString(R.string.err_msg_connection_was_refused))
//        }
//    }
//
//    //set value to shift dropdown
//    private fun setDropdownShift(mApplicationList: List<ShiftStat>?) {
//        //init array list
//        hideSoftKeyboard(this@LoginActivity)
//        if (mApplicationList != null && mApplicationList.size > 0) {
//            val mDropdownList = arrayOfNulls<String>(mApplicationList.size)
//            for (i in mApplicationList.indices) {
//                mDropdownList[i] = mApplicationList[i].shiftName.toString()
//            }
//            if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CHARLESTON, true)||
//                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_COHASSET, true)||
//                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKPARKING, true)||
//                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PARKX, true)||
//                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RUTGERS, true)||
//                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SEPTA, true)||
//                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKTEXAS, true)||
//                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOTTLEWORKS_IN, true)||
//                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MEMORIALHERMAN, true) ||
//                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITYOFSANDIEGO, true)
//            ) {
//                sharedPreference.write(
//                    SharedPrefKey.LOGIN_SHIFT,
//                    mDropdownList[0]
//                )
//            }
//            val adapter = ArrayAdapter(
//                this,
//                R.layout.row_dropdown_menu_popup_item,
//                mDropdownList
//            )
//            try {
//
////                mAutoComTextViewShift!!.setText(mAutoComTextViewShift!!.getAdapter().getItem(0).toString(), false);
//
//                mAutoComTextViewShift!!.threshold = 1
//                mAutoComTextViewShift!!.setAdapter<ArrayAdapter<String?>>(adapter)
//                //                mSelectedShiftStat = mApplicationList.get(pos);
//                mAutoComTextViewShift!!.onItemClickListener =
//                    OnItemClickListener { parent, view, position, id -> //                        mSelectedShiftStat = mApplicationList.get(position);
//                        sharedPreference.write(
//                            SharedPrefKey.LOGIN_SHIFT,
//                            mAutoComTextViewShift!!.text.toString()
//                        )
//                        hideSoftKeyboard(this@LoginActivity)
//                        hideSoftKeyboard(this@LoginActivity)
//                    }
//                //                if(mAutoComTextViewShift.getTag()!=null && mAutoComTextViewShift.getTag().equals("listonly")) {
//                setListOnly(this@LoginActivity, mAutoComTextViewShift!!)
//                //                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        } else {
//        }
//    }
//
//    private fun deleteAllPhotosByModayLogin() {
//        try {
//            val sdf = SimpleDateFormat("EEEE")
//            val d = Date()
//            val dayOfTheWeek: String = sdf.format(d)
//            LogUtil.printLog("day of today", dayOfTheWeek)
//            //            if(dayOfTheWeek.equalsIgnoreCase("Monday") && !SharedPref.getInstance(this).read(SharedPrefKey.DELETE_IMAGE_FOLDER, false))
//            if (dayOfTheWeek.equals("Friday", ignoreCase = true)) {
//                if (!sharedPreference.read(SharedPrefKey.DELETE_IMAGE_FOLDER, false)) {
//                    try {
//                        takeDataBaseBackup()
//                    } catch (e: Exception) {
//                        e.printStackTrace()
//                    }
//                    val directoryCamera = File(
//                        Environment.getExternalStorageDirectory().absolutePath,
//                        Constants.FILE_NAME + Constants.CAMERA
//                    )
//                    deleteRecursive(directoryCamera)
//                    val directoryScan = File(
//                        Environment.getExternalStorageDirectory().absolutePath,
//                        Constants.FILE_NAME + Constants.SCANNER
//                    )
//                    deleteRecursive(directoryScan)
//                    sharedPreference.write(SharedPrefKey.DELETE_IMAGE_FOLDER, true)
//                }
//            } else {
//                sharedPreference.write(SharedPrefKey.DELETE_IMAGE_FOLDER, false)
//            }
//        } catch (e: java.lang.Exception) {
//            e.printStackTrace()
//        }
//    }
//
//    private fun deleteRecursive(directory: File) {
//        try {
//            if (directory.exists()) {
//                if (directory.isDirectory)
//                    for (child in directory.listFiles())
//                        deleteRecursive(child)
//
//                directory.delete()
//            }
//        } catch (e: java.lang.Exception) {
//            e.printStackTrace()
//        }
//    }
//
//    private fun enableLoginButton(){
//        mLoginButton!!.isEnabled = true
//        val alpha = 1.00f
//        val alphaUp = AlphaAnimation(alpha, alpha)
//        alphaUp.fillAfter = true
//        mLoginButton!!.startAnimation(alphaUp)
//
//    }
//
//    companion object {
//        private val TAG = LoginActivity::class.java.simpleName
//    }
//
//
//    private fun takeDataBaseBackup() {
////        val db = AppDatabase.getInstance(this@LoginActivity)
////        db.close()
//        val dbFile: File = getDatabasePath("park_loyalty")
////       val sDir = File(Environment.getExternalStorageDirectory(), "Backup")
//        val sDir = File(
//            Environment.getExternalStorageDirectory().absolutePath,
//            Constants.FILE_NAME + Constants.DATABASEBACKUP
//        )
//        val fileName = "park_loyalty"
//        val sfPath = sDir.path + File.separator + fileName
//        if (!sDir.exists()) {
//            sDir.mkdirs()
//        }
//        val saveFile = File(sfPath)
//        if (saveFile.exists()) {
//            Log.d("LOGGER ", "File exists. Deleting it and then creating new file.")
//            saveFile.delete()
//        }
//        try {
//            if (saveFile.createNewFile()) {
//                val bufferSize = 8 * 1024
//                val buffer = ByteArray(bufferSize)
//                var bytesRead: Int
//                val saveDb: OutputStream = FileOutputStream(sfPath)
//                val indDb: InputStream = FileInputStream(dbFile)
//                do {
//                    bytesRead = indDb.read(buffer, 0, bufferSize)
//                    if (bytesRead < 0)
//                        break
//                    saveDb.write(buffer, 0, bytesRead)
//                } while (true)
//                saveDb.flush()
//                indDb.close()
//                saveDb.close()
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//
//        }
//    }
//
//    fun getDateTimeFromMillis(millis: Long, pattern: String): String {
//        val simpleDateFormat = SimpleDateFormat(pattern, Locale.getDefault()).format(Date())
//        return simpleDateFormat.format(millis)
//    }
//
//    var mCombinationId:String? = ""
//    @RequiresApi(Build.VERSION_CODES.M)
//    private fun getDeviceIdWithSerial()
//    {
//        try {
//            val tm: TelephonyManager =
//                baseContext.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
//
//            var tmDevice: String
//            val tmDevice0: String
//            val tmSerial: String
//            val androidId: String
//            tmDevice = "" + tm.getDeviceId()
//            tmDevice0 = "" + tm.getDeviceId(0)
//            tmSerial = "" + tm.getSimSerialNumber()
//            androidId = "" + Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
//
//            mCombinationId = UUID(
//                androidId.hashCode().toLong(),
//                tmDevice.hashCode().toLong() shl 32 or tmSerial.hashCode().toLong()
//            ).toString()
//
//
//            LogUtil.printLog("VINOD",mCombinationId.toString() +"   ")
//            val telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
//
////            if (Build.VERSION.SDK_INT >= 26) {
////                tmDevice = telephonyManager.getImei()
////            } else {
////                tmDevice = telephonyManager.getDeviceId()
////            }
//
//            appCompatEditTextSiteName?.text = tmDevice
//
//            Log.e("unique id   ", Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID))
//            //        val uniqueID = getDeviceId(mContext!!)
//            getUniquePsuedoID()
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                getserialNumber()
//            }
//            Log.e("unique id   ", Device.getSerialNumber())
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }
//
//    var m_szDevIDShort =
//        "35" + Build.BOARD.length % 10 + Build.BRAND.length % 10 + Build.CPU_ABI.length % 10 + Build.DEVICE.length % 10 + Build.MANUFACTURER.length % 10 + Build.MODEL.length % 10 + Build.PRODUCT.length % 10
//    fun getUniquePsuedoID():String {
//        val m_szDevIDShort =
//            "35" + Build.BOARD.length % 10 + Build.BRAND.length % 10 + Build.CPU_ABI.length % 10 + Build.DEVICE.length % 10 + Build.MANUFACTURER.length % 10 + Build.MODEL.length % 10 + Build.PRODUCT.length % 10
//        var serial: String? = null
//        try {
//            serial = Build::class.java.getField("SERIAL")[null]
//                .toString()
//            return UUID(m_szDevIDShort.hashCode().toLong(), serial.hashCode().toLong())
//                .toString()
//        } catch (exception: java.lang.Exception) {
//            // String needs to be initialized
//            serial = "serial" // some value
//        }
//        Log.e("unique id   ",serial.toString())
//        return UUID(m_szDevIDShort.hashCode().toLong(), serial.hashCode().toLong())
//            .toString()
//    }
//
//
//
//    @RequiresApi(Build.VERSION_CODES.O)
//    fun getserialNumber() {
//        displayIMEI(mContext!!, this@LoginActivity)
//    }
//
//
//    @RequiresApi(Build.VERSION_CODES.O)
//    fun displayIMEI(context: Context, activity: Activity) {
//
//        // on below line we are creating a column
//
//
//        // on below line we are checking for permissions
//        if (ActivityCompat.checkSelfPermission(
//                context,
//                Manifest.permission.READ_PHONE_STATE
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            // if permissions are not provided we
//            // are requesting for permissions.
//            ActivityCompat.requestPermissions(
//                activity,
//                arrayOf(Manifest.permission.READ_PHONE_STATE), 101
//            )
//        }
//
//        // on below line we are creating a simple
//        // text view for displaying heading.
//
//        // on below line we are initializing our variables.
//        val telephonyManager = context.getSystemService(TELEPHONY_SERVICE) as TelephonyManager
//
//        // on below line we are getting
//        // imei using Telephony Manager.
//        val imei = telephonyManager.imei
//        Log.e("unique id   ",imei)
//        // on below line we are
//
//    }
//
//
//    private fun callAuthToken() {
//        mContext = this
//        class SaveTask : AsyncTask<Void?, Int?, CitationInsurranceDatabaseModel?>() {
//            override fun doInBackground(vararg voids: Void?): CitationInsurranceDatabaseModel? {
//                try {
//                    var mIssuranceModel: List<CitationInsurranceDatabaseModel?>? = ArrayList()
//                    mIssuranceModel = mDb?.dbDAO?.getCitationInsurrance()
//                    for (i in mIssuranceModel!!.indices) {
//                        if (mIssuranceModel[i]!!.formStatus == 1) {
//                            mCitationNumberId = mIssuranceModel[i]!!.citationNumber
//                            return mIssuranceModel[i]
//                        }
//                    }
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                }
//                return null
//            }
//
//            @SuppressLint("WrongThread")
//            override fun onPostExecute(result: CitationInsurranceDatabaseModel?) {
//                try {
//                    if(result!=null && result.citationNumber!!.isNotEmpty()) {
//                        mAuthTokenRefreshViewModel!!.hitAuthTokenRefreshApi()
//                    }
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                }
//            }
//        }
//        SaveTask().execute()
//    }
//
//    private fun savefile(){
//        try
//        {
//            val localFolder =
//                File(Environment.getExternalStorageDirectory().absolutePath, Constants.FILE_NAME)
//            if (!localFolder.exists()) {
//                localFolder.mkdirs()
//            }
//            /* SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd");
//            Date now = new Date();*/
//            val fileName1 = "printer_command" + ".txt" //like 2016_01_12.txt
//            val file = File(localFolder, fileName1)
//            val writer = FileWriter(file, true)
//            writer.append("VINOD code").append("\n\n")
//            writer.flush()
//            writer.close()
//            if (!file.exists()) {
//                try {
//                    file.createNewFile()
//                } catch (e: IOException) {
//                    e.printStackTrace()
//                    return
//                }
//            }
//            //if file not exists the download
//            val mFilepath = file.absolutePath
//            sharedPreference.write(SharedPrefKey.FILE_PATH, localFolder.absolutePath)
//            printLog("filename", mFilepath)
//
////            val file = "printercode.text"
////            val fileOutputStream: FileOutputStream
////            //Creating the python file
////            fileOutputStream = openFileOutput(file, Context.MODE_PRIVATE)
////            //Incrementing through the list and writing to the file
////
////                //Writing python code to file
////                fileOutputStream.write("VINOD CODE".toByteArray())
//        }
//        //Catching any file errors that could occur
//        catch(e: FileNotFoundException)
//        {
//            e.printStackTrace()
//        }
//        catch(e:NumberFormatException)
//        {
//            e.printStackTrace()
//        }
//        catch(e: IOException)
//        {
//            e.printStackTrace()
//        }
//        catch(e:Exception)
//        {
//            e.printStackTrace()
//        }
//    }
//
//    private fun getHash()
//    {
//        try {
//            val info = packageManager.getPackageInfo(
//                "com.septa.lpr.scan",
//                PackageManager.GET_SIGNATURES
//            )
//            for (signature in info.signatures) {
//                val md = MessageDigest.getInstance("SHA")
//                md.update(signature.toByteArray())
//                Log.d(
//                    "KeyHash", "KeyHash:" + Base64.encodeToString(
//                        md.digest(),
//                        Base64.DEFAULT
//                    )
//                )
//                val mHash = mContext?.let { hashT(it,"com.septa.lpr.scan") }
//                Log.d(
//                    "KeyHash", "KeyHash: vinod"  +mHash
//                )
//            }
//        } catch (e: PackageManager.NameNotFoundException) {
//        } catch (e: NoSuchAlgorithmException) {
//        }
//    }
//
//
//    fun hashT(context: Context, packag: String?): String? {
//        return try {
//            val info =
//                context.packageManager.getPackageInfo(packag!!, PackageManager.GET_SIGNATURES)
//            val signature: Signature = info.signatures[0]
//            val digest = MessageDigest.getInstance("SHA")
//            digest.update(signature.toByteArray())
//            Base64.encodeToString(digest.digest(), Base64.DEFAULT)
//
//        } catch (exception: java.lang.Exception) {
//            exception.printStackTrace()
//            null
//        }
//    }
//
//    private fun clearShardValue()
//    {
//        try {
//            sharedPreference.write(
//                SharedPrefKey.QRCODE_LABEL_FOR_PRINT_X, ""
//            ).toString()
//            sharedPreference.write(
//                SharedPrefKey.QRCODE_LABEL_FOR_PRINT_Y, ""
//            ).toString()
//            sharedPreference.write(
//                SharedPrefKey.QRCODE_LABEL_FOR_PRINT, ""
//            ).toString()
//            sharedPreference.write(
//                SharedPrefKey.QRCODE_LABEL_FOR_PRINT_FONT, ""
//            ).toString()
//
//            sharedPreference.write(
//                SharedPrefKey.FOOTER_LABEL_FOR_PRINT_X, ""
//            ).toString()
//            sharedPreference.write(
//                SharedPrefKey.FOOTER_LABEL_FOR_PRINT_Y, ""
//            ).toString()
//            sharedPreference.write(
//                SharedPrefKey.FOOTER_LABEL_FOR_PRINT, ""
//            ).toString()
//            sharedPreference.write(
//                SharedPrefKey.FOOTER_LABEL_FOR_PRINT_FONT, ""
//            ).toString()
//
//            sharedPreference.write(
//                SharedPrefKey.FOOTER2_LABEL_FOR_PRINT_X, ""
//            ).toString()
//            sharedPreference.write(
//                SharedPrefKey.FOOTER2_LABEL_FOR_PRINT_Y, ""
//            ).toString()
//            sharedPreference.write(
//                SharedPrefKey.FOOTER2_LABEL_FOR_PRINT, ""
//            ).toString()
//            sharedPreference.write(
//                SharedPrefKey.FOOTER2_LABEL_FOR_PRINT_FONT, ""
//            ).toString()
//
//            sharedPreference.write(
//                SharedPrefKey.FOOTER3_LABEL_FOR_PRINT_X, ""
//            ).toString()
//            sharedPreference.write(
//                SharedPrefKey.FOOTER3_LABEL_FOR_PRINT_Y, ""
//            ).toString()
//            sharedPreference.write(
//                SharedPrefKey.FOOTER3_LABEL_FOR_PRINT, ""
//            ).toString()
//            sharedPreference.write(
//                SharedPrefKey.FOOTER3_LABEL_FOR_PRINT_FONT, ""
//            ).toString()
//
//            sharedPreference.write(
//                SharedPrefKey.FOOTER4_LABEL_FOR_PRINT_X, ""
//            ).toString()
//            sharedPreference.write(
//                SharedPrefKey.FOOTER4_LABEL_FOR_PRINT_Y, ""
//            ).toString()
//            sharedPreference.write(
//                SharedPrefKey.FOOTER4_LABEL_FOR_PRINT, ""
//            ).toString()
//            sharedPreference.write(
//                SharedPrefKey.FOOTER4_LABEL_FOR_PRINT_FONT, ""
//            ).toString()
//
//            //Clearing Footer 5 Value
//            sharedPreference.write(
//                SharedPrefKey.FOOTER5_LABEL_FOR_PRINT_X, ""
//            ).toString()
//            sharedPreference.write(
//                SharedPrefKey.FOOTER5_LABEL_FOR_PRINT_Y, ""
//            ).toString()
//            sharedPreference.write(
//                SharedPrefKey.FOOTER5_LABEL_FOR_PRINT, ""
//            ).toString()
//            sharedPreference.write(
//                SharedPrefKey.FOOTER5_LABEL_FOR_PRINT_FONT, ""
//            ).toString()
//
//            sharedPreference.write(
//                SharedPrefKey.FOOTER6_LABEL_FOR_PRINT_X, ""
//            ).toString()
//            sharedPreference.write(
//                SharedPrefKey.FOOTER6_LABEL_FOR_PRINT_Y, ""
//            ).toString()
//            sharedPreference.write(
//                SharedPrefKey.FOOTER6_LABEL_FOR_PRINT, ""
//            ).toString()
//            sharedPreference.write(
//                SharedPrefKey.FOOTER6_LABEL_FOR_PRINT_FONT, ""
//            ).toString()
//
//            sharedPreference.write(
//                SharedPrefKey.FOOTER7_LABEL_FOR_PRINT_X, ""
//            ).toString()
//            sharedPreference.write(
//                SharedPrefKey.FOOTER7_LABEL_FOR_PRINT_Y, ""
//            ).toString()
//            sharedPreference.write(
//                SharedPrefKey.FOOTER7_LABEL_FOR_PRINT, ""
//            ).toString()
//            sharedPreference.write(
//                SharedPrefKey.FOOTER7_LABEL_FOR_PRINT_FONT, ""
//            ).toString()
//
//            sharedPreference.clearSharedPrefData(SharedPrefKey.FOOTER8_LABEL_FOR_PRINT_X)
//            sharedPreference.clearSharedPrefData(SharedPrefKey.FOOTER8_LABEL_FOR_PRINT_Y)
//            sharedPreference.clearSharedPrefData(SharedPrefKey.FOOTER8_LABEL_FOR_PRINT)
//            sharedPreference.clearSharedPrefData(SharedPrefKey.FOOTER8_LABEL_FOR_PRINT_FONT)
//
//            sharedPreference.clearSharedPrefData(SharedPrefKey.FOOTER9_LABEL_FOR_PRINT_X)
//            sharedPreference.clearSharedPrefData(SharedPrefKey.FOOTER9_LABEL_FOR_PRINT_Y)
//            sharedPreference.clearSharedPrefData(SharedPrefKey.FOOTER9_LABEL_FOR_PRINT)
//            sharedPreference.clearSharedPrefData(SharedPrefKey.FOOTER9_LABEL_FOR_PRINT_FONT)
//
//            sharedPreference.clearSharedPrefData(SharedPrefKey.FOOTER10_LABEL_FOR_PRINT_X)
//            sharedPreference.clearSharedPrefData(SharedPrefKey.FOOTER10_LABEL_FOR_PRINT_Y)
//            sharedPreference.clearSharedPrefData(SharedPrefKey.FOOTER10_LABEL_FOR_PRINT)
//            sharedPreference.clearSharedPrefData(SharedPrefKey.FOOTER10_LABEL_FOR_PRINT_FONT)
//
//            //Clearing Shared Pref Value for Lines
//            sharedPreference.clearSharedPrefData(SharedPrefKey.LINE_1_FOR_PRINT_X)
//            sharedPreference.clearSharedPrefData(SharedPrefKey.LINE_1_FOR_PRINT_Y)
//            sharedPreference.clearSharedPrefData(SharedPrefKey.LINE_1_FOR_PRINT)
//            sharedPreference.clearSharedPrefData(SharedPrefKey.LINE_1_FOR_PRINT_HEIGHT)
//
//            sharedPreference.clearSharedPrefData(SharedPrefKey.LINE_2_FOR_PRINT_X)
//            sharedPreference.clearSharedPrefData(SharedPrefKey.LINE_2_FOR_PRINT_Y)
//            sharedPreference.clearSharedPrefData(SharedPrefKey.LINE_2_FOR_PRINT)
//            sharedPreference.clearSharedPrefData(SharedPrefKey.LINE_2_FOR_PRINT_HEIGHT)
//
//            sharedPreference.clearSharedPrefData(SharedPrefKey.LINE_3_FOR_PRINT_X)
//            sharedPreference.clearSharedPrefData(SharedPrefKey.LINE_3_FOR_PRINT_Y)
//            sharedPreference.clearSharedPrefData(SharedPrefKey.LINE_3_FOR_PRINT)
//            sharedPreference.clearSharedPrefData(SharedPrefKey.LINE_3_FOR_PRINT_HEIGHT)
//
//            sharedPreference.clearSharedPrefData(SharedPrefKey.LINE_4_FOR_PRINT_X)
//            sharedPreference.clearSharedPrefData(SharedPrefKey.LINE_4_FOR_PRINT_Y)
//            sharedPreference.clearSharedPrefData(SharedPrefKey.LINE_4_FOR_PRINT)
//            sharedPreference.clearSharedPrefData(SharedPrefKey.LINE_4_FOR_PRINT_HEIGHT)
//
//            sharedPreference.clearSharedPrefData(SharedPrefKey.LINE_5_FOR_PRINT_X)
//            sharedPreference.clearSharedPrefData(SharedPrefKey.LINE_5_FOR_PRINT_Y)
//            sharedPreference.clearSharedPrefData(SharedPrefKey.LINE_5_FOR_PRINT)
//            sharedPreference.clearSharedPrefData(SharedPrefKey.LINE_5_FOR_PRINT_HEIGHT)
//
//            sharedPreference.clearSharedPrefData(SharedPrefKey.LINE_6_FOR_PRINT_X)
//            sharedPreference.clearSharedPrefData(SharedPrefKey.LINE_6_FOR_PRINT_Y)
//            sharedPreference.clearSharedPrefData(SharedPrefKey.LINE_6_FOR_PRINT)
//            sharedPreference.clearSharedPrefData(SharedPrefKey.LINE_6_FOR_PRINT_HEIGHT)
//
//            sharedPreference.clearSharedPrefData(SharedPrefKey.LINE_7_FOR_PRINT_X)
//            sharedPreference.clearSharedPrefData(SharedPrefKey.LINE_7_FOR_PRINT_Y)
//            sharedPreference.clearSharedPrefData(SharedPrefKey.LINE_7_FOR_PRINT)
//            sharedPreference.clearSharedPrefData(SharedPrefKey.LINE_7_FOR_PRINT_HEIGHT)
//
//            sharedPreference.clearSharedPrefData(SharedPrefKey.LINE_8_FOR_PRINT_X)
//            sharedPreference.clearSharedPrefData(SharedPrefKey.LINE_8_FOR_PRINT_Y)
//            sharedPreference.clearSharedPrefData(SharedPrefKey.LINE_8_FOR_PRINT)
//            sharedPreference.clearSharedPrefData(SharedPrefKey.LINE_8_FOR_PRINT_HEIGHT)
//
//            sharedPreference.clearSharedPrefData(SharedPrefKey.LINE_9_FOR_PRINT_X)
//            sharedPreference.clearSharedPrefData(SharedPrefKey.LINE_9_FOR_PRINT_Y)
//            sharedPreference.clearSharedPrefData(SharedPrefKey.LINE_9_FOR_PRINT)
//            sharedPreference.clearSharedPrefData(SharedPrefKey.LINE_9_FOR_PRINT_HEIGHT)
//
//            sharedPreference.clearSharedPrefData(SharedPrefKey.LINE_10_FOR_PRINT_X)
//            sharedPreference.clearSharedPrefData(SharedPrefKey.LINE_10_FOR_PRINT_Y)
//            sharedPreference.clearSharedPrefData(SharedPrefKey.LINE_10_FOR_PRINT)
//            sharedPreference.clearSharedPrefData(SharedPrefKey.LINE_10_FOR_PRINT_HEIGHT)
//
//
//            sharedPreference.write(
//                SharedPrefKey.SCHEDULE_LABEL_FOR_PRINT_X, ""
//            ).toString()
//            sharedPreference.write(
//                SharedPrefKey.SCHEDULE_LABEL_FOR_PRINT_Y, ""
//            ).toString()
//            sharedPreference.write(
//                SharedPrefKey.SCHEDULE_LABEL_FOR_PRINT, ""
//            ).toString()
//            sharedPreference.write(
//                SharedPrefKey.SCHEDULE_LABEL_FOR_PRINT_FONT, ""
//            ).toString()
//
//            sharedPreference.write(
//                SharedPrefKey.QRCODE_FOR_PRINT_X, ""
//            )
//            sharedPreference.write(
//                SharedPrefKey.QRCODE_FOR_PRINT_Y, ""
//            )
//
//            sharedPreference.write(SharedPrefKey.HEADER_1_FOR_PRINT, "")
//            sharedPreference.write(SharedPrefKey.HEADER_2_FOR_PRINT, "")
//
//            sharedPreference.write(SharedPrefKey.HEADER_1_FOR_PRINT_X, "")
//            sharedPreference.write(SharedPrefKey.HEADER_1_FOR_PRINT_Y, "")
//            sharedPreference.write(SharedPrefKey.HEADER_1_FOR_PRINT_FONT, "")
//
//            sharedPreference.write(SharedPrefKey.HEADER_2_FOR_PRINT_X, "")
//            sharedPreference.write(SharedPrefKey.HEADER_2_FOR_PRINT_Y, "")
//            sharedPreference.write(SharedPrefKey.HEADER_2_FOR_PRINT_FONT, "")
//
//            sharedPreference.write(SharedPrefKey.LOGIN_HEARING_TIME,"")
//            sharedPreference.write(SharedPrefKey.LOGIN_HEARING_DATE,"")
//
//            sharedPreference.write(
//                SharedPrefKey.APP_LOGO_FOR_PRINT_X, ""
//            ).toString()
//            sharedPreference.write(
//                SharedPrefKey.APP_LOGO_FOR_PRINT_Y, ""
//            ).toString()
//            sharedPreference.write(
//                SharedPrefKey.APP_LOGO_FOR_PRINT, ""
//            ).toString()
//            sharedPreference.write(
//                SharedPrefKey.APP_LOGO_FOR_PRINT_WIDTH, ""
//            ).toString()
//
//            sharedPreference.write(
//                SharedPrefKey.LPR_IMAGE_FOR_PRINT_X, ""
//            ).toString()
//            sharedPreference.write(
//                SharedPrefKey.LPR_IMAGE_FOR_PRINT_Y, ""
//            ).toString()
//            sharedPreference.write(
//                SharedPrefKey.LPR_IMAGE_FOR_PRINT, ""
//            ).toString()
//            sharedPreference.write(
//                SharedPrefKey.LPR_IMAGE_FOR_PRINT_WIDTH, ""
//            ).toString()
//
//            sharedPreference.write(
//                SharedPrefKey.MOTORIST_INFORMATION_LABEL,
//                ""
//            )
//
//            //Bar code
//            sharedPreference.write(
//                SharedPrefKey.BAR_CODE_FOR_PRINT_X, ""
//            )
//            sharedPreference.write(
//                SharedPrefKey.BAR_CODE_FOR_PRINT_Y, ""
//            )
//            sharedPreference.write(
//                SharedPrefKey.BAR_CODE_FOR_PRINT, ""
//            )
//            sharedPreference.write(
//                SharedPrefKey.BAR_CODE_FOR_PRINT_HEIGHT, ""
//            )
//
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }
//
//    //set value to Body Style dropdown
//    private fun setDropdownHearingTime(mApplicationList: List<ResponseItemHearingTime?>?) {
//        hideSoftKeyboard(this@LoginActivity)
//        try {
//            if (mApplicationList != null && mApplicationList.size > 0) {
//                ioScope.launch {
//                    val mDropdownList = arrayOfNulls<String>(mApplicationList.size)
//                    for (i in mApplicationList.indices) {
//                        mDropdownList[i] = mApplicationList[i]!!.hearingTime.toString()
//                    }
//                    mAutoComTextViewHearingTime?.post {
//                        val adapter = ArrayAdapter(
//                            this@LoginActivity,
//                            R.layout.row_dropdown_menu_popup_item,
//                            mDropdownList
//                        )
//                        try {
//                            mAutoComTextViewHearingTime?.threshold = 1
//                            mAutoComTextViewHearingTime?.setAdapter<ArrayAdapter<String?>>(adapter)
//                            mAutoComTextViewHearingTime?.onItemClickListener =
//                                OnItemClickListener { parent, view, position, id -> // mSelectedShiftStat = mApplicationList.get(position);
//                                    hideSoftKeyboard(this@LoginActivity)
//                                    sharedPreference.write(
//                                        SharedPrefKey.LOGIN_HEARING_TIME,
//                                        mAutoComTextViewHearingTime!!.text.toString()
//                                    )
//                                }
//                        } catch (e: Exception) {
//                            e.printStackTrace()
//                        }
//
//                        if (mAutoComTextViewHearingTime?.tag != null && mAutoComTextViewHearingTime?.tag == "listonly") {
//                            setListOnly(this@LoginActivity, mAutoComTextViewHearingTime!!)
//                        }
//                    }
//                }
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }
//
//    //set date picker view
//    private fun openDataPicker(datePickerField: AppCompatAutoCompleteTextView?) {
//        val myCalendar = Calendar.getInstance()
//        val date =
//            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth -> // TODO Auto-generated method stub
//                myCalendar[Calendar.YEAR] = year
//                myCalendar[Calendar.MONTH] = monthOfYear
//                myCalendar[Calendar.DAY_OF_MONTH] = dayOfMonth
//                var myFormat = "MM/dd/yyyy" //In which you need put here
//
//                val sdf = SimpleDateFormat(myFormat, Locale.US)
//                datePickerField!!.setText(sdf.format(myCalendar.time))
//                sharedPreference.write(
//                    SharedPrefKey.LOGIN_HEARING_DATE,
//                    datePickerField!!.text.toString()
//                )
//            }
//        DatePickerDialog(
//            this, date, myCalendar[Calendar.YEAR], myCalendar[Calendar.MONTH],
//            myCalendar[Calendar.DAY_OF_MONTH]
//        ).show()
//
//    }
//
//    /**
//     * Function used to delete stored header & footer file forcefully
//     */
//    private fun removeCachedHeaderFooterImage() {
//        try {
//            val headerFile = File(getHeaderFooterImageFileFullPath(true))
//            val footerFile = File(getHeaderFooterImageFileFullPath(false))
//
//            if (headerFile.exists())
//                headerFile.delete()
//
//            if (footerFile.exists())
//                footerFile.delete()
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }
//}
//
