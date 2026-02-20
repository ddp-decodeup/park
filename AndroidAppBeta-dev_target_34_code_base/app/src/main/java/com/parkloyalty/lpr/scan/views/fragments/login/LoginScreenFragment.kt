package com.parkloyalty.lpr.scan.views.fragments.login

import DialogUtil
import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.SwitchCompat
import androidx.core.net.toUri
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.JsonNode
import com.google.android.gms.safetynet.SafetyNet
import com.google.android.material.textfield.TextInputLayout
import com.parkloyalty.lpr.scan.BuildConfig
import com.parkloyalty.lpr.scan.R
import com.parkloyalty.lpr.scan.common.model.LocUpdateRequest
import com.parkloyalty.lpr.scan.database.AppDatabase
import com.parkloyalty.lpr.scan.database.DataBaseUtil.BOOKLET_NOT_ISSUED
import com.parkloyalty.lpr.scan.database.DataBaseUtil.CITATION_UNUPLOADED_API_FAILED
import com.parkloyalty.lpr.scan.database.DataBaseUtil.CITATION_UPLOADED
import com.parkloyalty.lpr.scan.database.DbOperationStatus
import com.parkloyalty.lpr.scan.databinding.FragmentLoginScreenBinding
import com.parkloyalty.lpr.scan.extensions.activateMoonLightMode
import com.parkloyalty.lpr.scan.extensions.activateSunLightMode
import com.parkloyalty.lpr.scan.extensions.clearError
import com.parkloyalty.lpr.scan.extensions.disableView
import com.parkloyalty.lpr.scan.extensions.enableButton
import com.parkloyalty.lpr.scan.extensions.getAndroidID
import com.parkloyalty.lpr.scan.extensions.getAppVersionName
import com.parkloyalty.lpr.scan.extensions.getDeviceIdForAPI
import com.parkloyalty.lpr.scan.extensions.getErrorMessage
import com.parkloyalty.lpr.scan.extensions.hideCompanyTitleOnLoginScreen
import com.parkloyalty.lpr.scan.extensions.hideShiftTimeDropdownOnLoginScreen
import com.parkloyalty.lpr.scan.extensions.hideSoftKeyboard
import com.parkloyalty.lpr.scan.extensions.hideView
import com.parkloyalty.lpr.scan.extensions.invisibleView
import com.parkloyalty.lpr.scan.extensions.isInternetAvailable
import com.parkloyalty.lpr.scan.extensions.isItemFromTheList
import com.parkloyalty.lpr.scan.extensions.logD
import com.parkloyalty.lpr.scan.extensions.nav
import com.parkloyalty.lpr.scan.extensions.nullSafety
import com.parkloyalty.lpr.scan.extensions.safeNavigate
import com.parkloyalty.lpr.scan.extensions.setCrossClearButton
import com.parkloyalty.lpr.scan.extensions.setListOnlyDropDown
import com.parkloyalty.lpr.scan.extensions.setupTextInputLayout
import com.parkloyalty.lpr.scan.extensions.showErrorWithShake
import com.parkloyalty.lpr.scan.extensions.showHearingDateTimeDropdownOnLoginScreen
import com.parkloyalty.lpr.scan.extensions.showView
import com.parkloyalty.lpr.scan.extensions.toast
import com.parkloyalty.lpr.scan.extensions.writeDefaultShiftTimeToSharedPreference
import com.parkloyalty.lpr.scan.interfaces.Constants
import com.parkloyalty.lpr.scan.interfaces.SharedPrefKey
import com.parkloyalty.lpr.scan.network.ApiLogsClass
import com.parkloyalty.lpr.scan.network.api.RequestHandler
import com.parkloyalty.lpr.scan.network.response_handler.NewApiResponse
import com.parkloyalty.lpr.scan.ui.check_setup.model.lpr_model.UploadImagesResponse
import com.parkloyalty.lpr.scan.ui.check_setup.model.municipalcitation.CreateMunicipalCitationTicketResponse
import com.parkloyalty.lpr.scan.ui.check_setup.model.municipalcitation.MunicipalCitationLayoutResponse
import com.parkloyalty.lpr.scan.ui.check_setup.model.ticket.CreateTicketResponse
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.ActivityLayoutResponse
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.AuthRefreshResponse
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.CitationBookletModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.CitationImageModelOffline
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.CitationInsurranceDatabaseModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.CitationLayoutResponse
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.CitationNumberRequest
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.CitationNumberResponse
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.TimingLayoutResponse
import com.parkloyalty.lpr.scan.ui.login.model.CommonLoginResponse
import com.parkloyalty.lpr.scan.ui.login.model.HearingTimeResponse
import com.parkloyalty.lpr.scan.ui.login.model.ResponseItemHearingTime
import com.parkloyalty.lpr.scan.ui.login.model.ShiftResponse
import com.parkloyalty.lpr.scan.ui.login.model.ShiftStat
import com.parkloyalty.lpr.scan.ui.login.model.SiteOfficerLoginRequest
import com.parkloyalty.lpr.scan.ui.login.model.SiteVerifyResponse
import com.parkloyalty.lpr.scan.ui.login.model.TimestampDatatbase
import com.parkloyalty.lpr.scan.ui.login.model.UpdateTimeData
import com.parkloyalty.lpr.scan.ui.login.model.UpdateTimeDataList
import com.parkloyalty.lpr.scan.ui.login.model.UpdateTimeDb
import com.parkloyalty.lpr.scan.ui.login.model.UpdateTimeResponse
import com.parkloyalty.lpr.scan.ui.login.model.WelcomeForm
import com.parkloyalty.lpr.scan.ui.ticket.model.TicketUploadStatusRequest
import com.parkloyalty.lpr.scan.util.API_CONSTANT_TICKET_CATEGORY_MUNICIPAL_TICKET
import com.parkloyalty.lpr.scan.util.AppUtils
import com.parkloyalty.lpr.scan.util.AppUtils.deleteLprContinousModeFolder
import com.parkloyalty.lpr.scan.util.AppUtils.getSiteId
import com.parkloyalty.lpr.scan.util.FileUtil.getHeaderFooterImageFileFullPath
import com.parkloyalty.lpr.scan.util.FileUtil.getSignatureFileNameWithExt
import com.parkloyalty.lpr.scan.util.FileUtil.takeDatabaseBackUpAndSave
import com.parkloyalty.lpr.scan.util.LockLprModel
import com.parkloyalty.lpr.scan.util.LogUtil
import com.parkloyalty.lpr.scan.util.LogUtil.printLog
import com.parkloyalty.lpr.scan.util.SDF_EEEE
import com.parkloyalty.lpr.scan.util.SDF_MM_DD_YYYY
import com.parkloyalty.lpr.scan.util.SharedPref
import com.parkloyalty.lpr.scan.util.SystemUtils
import com.parkloyalty.lpr.scan.util.SystemUtils.whitelistAppFromBatteryOptimization
import com.parkloyalty.lpr.scan.util.Util.setTimeDataList
import com.parkloyalty.lpr.scan.util.setAccessibilityForTextInputLayoutDropdownButtons
import com.parkloyalty.lpr.scan.util.setAccessibilityRoleAsAction
import com.parkloyalty.lpr.scan.util.setCustomAccessibility
import com.parkloyalty.lpr.scan.util.setDoNothingAccessibilityForTextInputLayoutDropdownButtons
import com.parkloyalty.lpr.scan.utils.AlertDialogListener
import com.parkloyalty.lpr.scan.utils.AlertDialogUtils
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_CREATE_MUNICIPAL_CITATION_TICKET
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_CREATE_TICKET
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_GET_ACTIVITY_LAYOUT
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_GET_CITATION_LAYOUT
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_GET_CITATION_NUMBER
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_GET_HEARING_TIME_LIST
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_GET_MUNICIPAL_CITATION_LAYOUT
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_GET_REFRESH_TOKEN
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_GET_SHIFT_LIST
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_GET_TIMING_LAYOUT
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_LOGIN
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_UPDATE_TIME
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_UPLOAD_IMAGES
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_VERIFY_SITE
import com.parkloyalty.lpr.scan.utils.ApiConstants.MULTIPART_CONTENT_TYPE_CITATION_IMAGES
import com.parkloyalty.lpr.scan.utils.ApiConstants.TYPE_ACTIVITY_LAYOUT
import com.parkloyalty.lpr.scan.utils.ApiConstants.TYPE_HEARING_TIME_LIST
import com.parkloyalty.lpr.scan.utils.ApiConstants.TYPE_SHIFT_LIST
import com.parkloyalty.lpr.scan.utils.ApiConstants.TYPE_TIME_RECORD_LAYOUT
import com.parkloyalty.lpr.scan.utils.AppConstants.DAY_FRIDAY
import com.parkloyalty.lpr.scan.utils.AppConstants.TIME_INTERVAL_2_SECONDS
import com.parkloyalty.lpr.scan.utils.CitationFormUtils.CITATION_FORM_FIELD_TYPE_LIST_ONLY
import com.parkloyalty.lpr.scan.utils.DateTimeUtils
import com.parkloyalty.lpr.scan.utils.MultipartUtils
import com.parkloyalty.lpr.scan.utils.NoInternetDialogListener
import com.parkloyalty.lpr.scan.utils.NoInternetDialogUtil
import com.parkloyalty.lpr.scan.utils.ObjectMapperProvider
import com.parkloyalty.lpr.scan.utils.constants.ValidationConstants.PASSWORD_LENGTH
import com.parkloyalty.lpr.scan.utils.permissions.PermissionManager
import com.parkloyalty.lpr.scan.utils.permissions.PermissionsViewModel
import com.parkloyalty.lpr.scan.views.MainActivityViewModel
import com.parkloyalty.lpr.scan.views.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.Date
import javax.inject.Inject

@AndroidEntryPoint
class LoginScreenFragment : BaseFragment<FragmentLoginScreenBinding>() {
    private val mainActivityViewModel: MainActivityViewModel by activityViewModels()
    private val loginScreenViewModel: LoginScreenViewModel by viewModels()

    private val permissionViewModel: PermissionsViewModel by viewModels()

    @Inject
    lateinit var permissionFactory: PermissionManager.Factory

    private lateinit var permissionManager: PermissionManager

    private val permissions = mutableListOf<String>()

    @Inject
    lateinit var sharedPreference: SharedPref

    @Inject
    lateinit var appDatabase: AppDatabase

    private var mTextInputEmail: TextInputLayout? = null
    private var mEditTextEmail: AppCompatEditText? = null
    private var mTextInputPassword: TextInputLayout? = null
    private var mEditTextPassword: AppCompatEditText? = null
    private var appCompatEditTextVersionName: AppCompatTextView? = null
    private var appCompatEditTextSiteName: AppCompatTextView? = null
    private var appCompatEditTextCompanyName: AppCompatTextView? = null
    private var mTextInputShift: TextInputLayout? = null
    private var mAutoComTextViewShift: AppCompatAutoCompleteTextView? = null
    private var mAutoComTextViewHearingDate: AppCompatAutoCompleteTextView? = null
    private var mAutoComTextViewHearingTime: AppCompatAutoCompleteTextView? = null
    private var mTextInputLayoutDate: TextInputLayout? = null
    private var mTextInputLayoutTime: TextInputLayout? = null
    private var mLoginButton: AppCompatButton? = null
    private var mSwitchCompatSunMode: SwitchCompat? = null

    private var androidID = ""
    private var mCitationNumberId: String? = null
    private var imageUploadSuccessCount = 0
    private var responseModelLogin: CommonLoginResponse? = null

    private var mResponseEntity: UpdateTimeData? = null
    private var timeDataList: UpdateTimeDataList? = null
    private var mTimeResponse: UpdateTimeResponse? = null
    private var mTimingDatabaseList: TimestampDatatbase? = null
    private var numberOfAPI = 0
    private var offlineCitationImagesList: List<CitationImageModelOffline>? = null
    private var offlineCitationData: CitationInsurranceDatabaseModel? = null
    private val mImages: MutableList<String> = ArrayList()

    private var mUsername = ""
    private var mPassword = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        permissionManager =
            permissionFactory.create(caller = this, context = requireContext(), fragment = this)
    }

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentLoginScreenBinding.inflate(inflater, container, false)

    override fun findViewsByViewBinding() {
        mTextInputEmail = binding.layoutContentLogin.inputEmail
        mEditTextEmail = binding.layoutContentLogin.etEmail
        mTextInputPassword = binding.layoutContentLogin.inputPassword
        mEditTextPassword = binding.layoutContentLogin.etPassword
        appCompatEditTextVersionName = binding.layoutContentLogin.textviewVersionName
        appCompatEditTextSiteName = binding.layoutContentLogin.textviewSiteName
        appCompatEditTextCompanyName = binding.layoutHeaderLogin.textviewCmpName
        mTextInputShift = binding.layoutContentLogin.inputShift
        mAutoComTextViewShift = binding.layoutContentLogin.AutoComTextViewVehShift
        mAutoComTextViewHearingDate = binding.layoutContentLogin.AutoComTextViewHearingDate
        mAutoComTextViewHearingTime = binding.layoutContentLogin.AutoComTextViewHearingTime
        mTextInputLayoutDate = binding.layoutContentLogin.inputHearingDate
        mTextInputLayoutTime = binding.layoutContentLogin.inputHearingTime
        mLoginButton = binding.layoutContentLogin.btnLogin
        mSwitchCompatSunMode = binding.layoutContentLogin.toggleNight
    }

    @SuppressLint("ObsoleteSdkInt")
    override fun initViewLifecycleScope() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    loginScreenViewModel.loginResponse.collect(::consumeResponse)
                }

                launch {
                    mainActivityViewModel.deleteDatasetAndActivityTablesState.collect { status ->
                        when (status) {
                            is DbOperationStatus.Idle -> {
                                DialogUtil.hideLoader()
                            }

                            is DbOperationStatus.Loading -> {
                                DialogUtil.showLoader(
                                    context = requireContext(),
                                    message = getString(R.string.loader_text_clearing_existing_data_and_setting_up_new_data)
                                )
                            }

                            is DbOperationStatus.Success -> {
                                DialogUtil.hideLoader()
                                AlertDialogUtils.showDialog(
                                    context = requireContext(),
                                    icon = R.drawable.icon_success,
                                    title = getString(R.string.success_title_data_reset_and_reloaded),
                                    message = getString(R.string.success_desc_data_reset_and_reloaded),
                                    positiveButtonText = getString(R.string.button_text_ok),
                                    listener = object : AlertDialogListener {
                                        override fun onPositiveButtonClicked() {

                                        }
                                    })
                            }

                            is DbOperationStatus.Error -> {
                                AlertDialogUtils.showDialog(
                                    context = requireContext(),
                                    title = getString(R.string.error_title_database_error),
                                    message = getString(
                                        R.string.error_desc_database_error,
                                        status.throwable.localizedMessage.nullSafety(getString(R.string.error_desc_something_went_wrong))
                                    ),
                                    positiveButtonText = getString(R.string.button_text_ok)
                                )
                            }
                        }
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                launch {
                    permissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
                    permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION)
                    permissions.add(Manifest.permission.BLUETOOTH_CONNECT)
                    permissions.add(Manifest.permission.BLUETOOTH_SCAN)
                    permissions.add(Manifest.permission.READ_PHONE_STATE)

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                        permissions.add(Manifest.permission.POST_NOTIFICATIONS)
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        permissions.add(Manifest.permission.READ_MEDIA_IMAGES)
                    } else {
                        permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                launch {
                    loginScreenViewModel.callGetShiftListAPI()
                }

                launch {
                    loginScreenViewModel.callGetHearingListAPI()
                }

                launch {
                    mainActivityViewModel.setToolbarVisibility(false)
                }

                launch {
                    permissionViewModel.state.collectLatest { state ->
                        when (state) {
                            is PermissionsViewModel.UiState.Idle -> {
                                //Nothing to implement here
                            }

                            is PermissionsViewModel.UiState.Requesting -> {
                                //Nothing to implement here
                            }

                            is PermissionsViewModel.UiState.Result -> {
                                val result = state.result
                                if (result.granted.size == permissions.size) {
                                    //We need to show this dialog after all the permission done
                                    //We have to show this dialog only once for application lifetime
                                    val isNeedToShowBatteryOptimisationDialog =
                                        sharedPreference.read(
                                            SharedPrefKey.SHOW_BATTERY_OPTIMISATION_DIALOG, true
                                        )
                                    val isAppExcludedFromBatteryOptimization =
                                        SystemUtils.isAppExcludedFromBatteryOptimization(
                                            requireContext()
                                        )

                                    if (isNeedToShowBatteryOptimisationDialog && !isAppExcludedFromBatteryOptimization) {
                                        whitelistAppFromBatteryOptimization(
                                            requireContext(), sharedPreference
                                        )
                                    } else {
                                        sharedPreference.write(
                                            SharedPrefKey.SHOW_BATTERY_OPTIMISATION_DIALOG, false
                                        )
                                    }
                                }

                                if (result.denied.isNotEmpty()) {
                                    // show rationale dialog (suspend) then re-request
                                    lifecycleScope.launch {
                                        val allow =
                                            permissionManager.dialogPresenter.showRationaleDialogSuspend(
                                                requireContext(), result.denied
                                            )
                                        if (allow) {
                                            //Need this placeholder
                                            val retry =
                                                permissionManager.requestPermissions(result.denied.toTypedArray())
                                        } else {
                                            //Need this placeholder
                                            val retry =
                                                permissionManager.requestPermissions(result.denied.toTypedArray())
                                        }
                                    }
                                }
                                if (result.permanentlyDenied.isNotEmpty()) {
                                    // show settings dialog and open settings
                                    lifecycleScope.launch {
                                        val openSettings =
                                            permissionManager.dialogPresenter.showRationaleDialogSuspend(
                                                requireContext(),
                                                result.permanentlyDenied,
                                                getString(R.string.permission_message_permissions_are_denied_open_setting)
                                            )
                                        if (openSettings) {
                                            permissionManager.openAppSettings(getString(R.string.permission_reason_user_chose_to_open_settings))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }


                launch {
                    loginScreenViewModel.shiftListResponse.collect(::consumeResponse)
                }
                launch {
                    loginScreenViewModel.hearingListResponse.collect(::consumeResponse)
                }
                launch {
                    loginScreenViewModel.createTicketResponse.collect(::consumeResponse)
                }
                launch {
                    loginScreenViewModel.createMunicipalCitationTicketResponse.collect(::consumeResponse)
                }
                launch {
                    loginScreenViewModel.uploadImageResponse.collect(::consumeResponse)
                }
                launch {
                    loginScreenViewModel.citationNumberResponse.collect(::consumeResponse)
                }
                launch {
                    loginScreenViewModel.citationLayoutResponse.collect(::consumeResponse)
                }
                launch {
                    loginScreenViewModel.municipalCitationLayoutResponse.collect(::consumeResponse)
                }
                launch {
                    loginScreenViewModel.activityLayoutResponse.collect(::consumeResponse)
                }
                launch {
                    loginScreenViewModel.timingLayoutResponse.collect(::consumeResponse)
                }
                launch {
                    loginScreenViewModel.updateTimeResponse.collect(::consumeResponse)
                }
                launch {
                    loginScreenViewModel.authTokenRefreshResponse.collect(::consumeResponse)
                }
                launch {
                    loginScreenViewModel.siteVerifyResponse.collect(::consumeResponse)
                }
            }
        }
    }

    override fun initialiseData() {
        androidID = requireContext().getAndroidID()
        mainActivityViewModel.eventStartTimeStamp = AppUtils.getDateTime()
        initUI()

        setCrossClearButtonForAllFields()
        setAccessibilityForComponents()

        mainActivityViewModel.resetSingletonData()

        // Call deleteInventoryTables when needed (e.g., on login button click or as required)
        lifecycleScope.launch {
            loginScreenViewModel.deleteInventoryTables()
        }
        loginScreenViewModel.clearSharedPreferenceValues()

        /**
         * this code only for ppa build release in emergency purpose need to remove
         */
        try {
            clearDatasetAndActivityTables(isManual = false)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initUI() {
        setFooterText()
        appCompatEditTextVersionName?.text = "${requireContext().getAppVersionName()}\n$androidID"

        if (hideCompanyTitleOnLoginScreen()){
            appCompatEditTextCompanyName?.invisibleView()
        } else {
            appCompatEditTextCompanyName?.showView()
        }

        if (hideShiftTimeDropdownOnLoginScreen()) {
            mTextInputShift?.hideView()
        }

        if (showHearingDateTimeDropdownOnLoginScreen()) {
            mTextInputLayoutTime?.showView()
            mTextInputLayoutDate?.showView()
        }

        if (BuildConfig.DEBUG && LogUtil.isEnableAPILogs) {
            mUsername = "sbreyer"
            mPassword = "breyer123"
//            mUsername = "nicklat"; //rise tek
//            mPassword .= "password987";// rise tek
//            mUsername = "ssotomayor"
//            mPassword = "sotomayor123"

            mEditTextEmail?.setText(mUsername)
            mEditTextPassword?.setText(mPassword)
        }

        mTextInputEmail?.setupTextInputLayout(
            isEditTextInside = true,
            hintText = getString(R.string.hint_user_id),
            placeholder = getString(R.string.hint_enter_user_id)
        )

        mTextInputPassword?.setupTextInputLayout(
            isEditTextInside = true,
            hintText = getString(R.string.hint_password),
            placeholder = getString(R.string.hint_enter_password)
        )

        mTextInputShift?.setupTextInputLayout(
            hintText = getString(R.string.hint_shift),
            placeholder = getString(R.string.hint_select_or_type)
        )

        mTextInputLayoutTime?.setupTextInputLayout(
            hintText = getString(R.string.hint_hearing_time),
            placeholder = getString(R.string.hint_select_or_type)
        )

        mTextInputLayoutDate?.setupTextInputLayout(
            hintText = getString(R.string.hint_hearing_date),
            placeholder = getString(R.string.hint_select)
        )
    }

    override fun setupClickListeners() {
        mAutoComTextViewHearingDate?.setOnClickListener {
            requireActivity().hideSoftKeyboard()

            DateTimeUtils.openDataPicker(
                context = requireContext(),
                dateFormat = SDF_MM_DD_YYYY,
                minDate = System.currentTimeMillis(),
                onDateSelected = { selectedDate ->
                    mAutoComTextViewHearingDate?.setText(selectedDate)
                    sharedPreference.write(
                        SharedPrefKey.LOGIN_HEARING_DATE, selectedDate
                    )
                })
        }

        mLoginButton?.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                permissionManager.ensurePermissionsThen(
                    permissions = permissions.toTypedArray(),
                    rationaleMessage = getString(R.string.permission_message_all_permission_required_to_login)
                ) {
                    //Battery Dialog
                    if (isValidLoginDetails() && mLoginButton?.isEnabled.nullSafety()) {
                        if (requireContext().isInternetAvailable()) {
                            mLoginButton?.disableView()
                            loadCaptchaVerification()
                        } else {
                            NoInternetDialogUtil.showDialog(
                                context = requireContext(),
                                positiveButtonText = getString(R.string.button_text_ok),
                                negativeButtonText = getString(R.string.button_text_retry),
                                listener = object : NoInternetDialogListener {
                                    override fun onNegativeButtonClicked() {
                                        mLoginButton?.disableView()
                                        loadCaptchaVerification()
                                    }
                                })
                        }

                    }
                }
            }
        }

        binding.layoutContentLogin.tvForgotPass.setOnClickListener {
            nav.safeNavigate(R.id.action_loginScreenFragment_to_forgotPasswordScreenFragment)
        }

        binding.layoutContentLogin.ivResetAndReload.setOnClickListener {
            AlertDialogUtils.showDialog(
                context = requireContext(),
                icon = R.drawable.icon_warning,
                title = getString(R.string.confirmation_text_title_reset_and_reload),
                message = getString(R.string.confirmation_text_desc_reset_and_reload),
                positiveButtonText = getString(R.string.button_text_yes),
                negativeButtonText = getString(R.string.button_text_no),
                listener = object : AlertDialogListener {
                    override fun onPositiveButtonClicked() {
                        removeCachedHeaderFooterImage()
                        clearDatasetAndActivityTables(isManual = true)
                    }
                })

//            val intent = Intent(requireContext(), LoginActivity::class.java)
//            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
//            startActivity(intent)
//
//            android.os.Process.killProcess(android.os.Process.myPid())
        }

        mSwitchCompatSunMode?.setOnCheckedChangeListener { _, isChecked ->
            sharedPreference.write(
                SharedPrefKey.IS_SUN_LIGHT_MODE_ACTIVE, isChecked
            )
            if (isChecked) {
                AppUtils.isSunLightMode = true
                requireContext().activateSunLightMode(
                    mTextInputShift, null, mAutoComTextViewShift
                )
                requireContext().activateSunLightMode(mTextInputEmail, mEditTextEmail, null)
                requireContext().toast(getString(R.string.button_text_on))
            } else {
                AppUtils.isSunLightMode = false
                requireContext().activateMoonLightMode(
                    mTextInputShift, null, mAutoComTextViewShift
                )
                requireContext().toast(getString(R.string.button_text_off))
            }
        }
    }

    override fun onResume() {
        super.onResume()

        numberOfAPI = 0

        try {
            deleteLprContinousModeFolder(requireContext(), sharedPreference)
            deleteAllPhotosEveryWeek()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

        requestAllFilesAccessPermission()

        permissionViewModel.requestPermissionsUsingManager(
            permissionManager, permissions.toTypedArray()
        )
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun requestAllFilesAccessPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !Environment.isExternalStorageManager()) {
            val uri = "package:${BuildConfig.APPLICATION_ID}".toUri()
            startActivity(
                Intent(
                    Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, uri
                )
            )
        }
    }

    private fun isValidLoginDetails(): Boolean {
        if (mEditTextEmail?.text.isNullOrBlank()) {
            mTextInputEmail?.showErrorWithShake(getString(R.string.val_msg_please_enter_email))
            return false
        } else if (mEditTextPassword?.text.isNullOrBlank()) {
            mTextInputPassword?.showErrorWithShake(getString(R.string.val_msg_please_enter_password))
            return false
        } else if (mEditTextPassword?.text?.trim().toString().length < PASSWORD_LENGTH) {
            mTextInputPassword?.showErrorWithShake(getString(R.string.val_msg_minimum_password))
            return false
        } else if (!hideShiftTimeDropdownOnLoginScreen() || sharedPreference.read(
                SharedPrefKey.LOGIN_SHIFT, ""
            ).isNullOrBlank()
        ) {
            if (mAutoComTextViewShift?.text.isNullOrBlank()) {
                mTextInputShift?.showErrorWithShake(getString(R.string.val_msg_please_enter_shift))
                return false
            } else if (!mAutoComTextViewShift?.isItemFromTheList(mAutoComTextViewShift?.text.toString())
                    .nullSafety()
            ) {
                mTextInputShift?.showErrorWithShake(getString(R.string.error_desc_please_select_or_enter_a_valid_option_from_the_list))
                return false
            }

        }
        return true
    }

    private fun setCrossClearButtonForAllFields() {
        setCrossClearButton(
            context = requireContext(),
            textInputLayout = mTextInputEmail,
            appCompatEditText = mEditTextEmail
        )

        setCrossClearButton(
            context = requireContext(),
            textInputLayout = mTextInputPassword,
            appCompatEditText = mEditTextPassword
        )

        setCrossClearButton(
            context = requireContext(),
            textInputLayout = mTextInputShift,
            appCompatAutoCompleteTextView = mAutoComTextViewShift
        )

        setCrossClearButton(
            context = requireContext(),
            textInputLayout = mTextInputLayoutDate,
            appCompatAutoCompleteTextView = mAutoComTextViewHearingDate
        )

        setCrossClearButton(
            context = requireContext(),
            textInputLayout = mTextInputLayoutTime,
            appCompatAutoCompleteTextView = mAutoComTextViewHearingTime
        )
    }

    private fun setAccessibilityForComponents() {
        setAccessibilityForTextInputLayoutDropdownButtons(requireContext(), mTextInputShift)
        setAccessibilityForTextInputLayoutDropdownButtons(requireContext(), mTextInputLayoutTime)
        setDoNothingAccessibilityForTextInputLayoutDropdownButtons(
            requireContext(), mTextInputLayoutDate
        )

        binding.layoutContentLogin.ivResetAndReload.setCustomAccessibility(
            contentDescription = getString(R.string.ada_content_description_sync),
            role = getString(R.string.ada_role_button),
            actionLabel = getString(R.string.ada_action_reset_and_reload)
        )
        appCompatEditTextCompanyName?.setCustomAccessibility(role = getString(R.string.ada_role_label))
        binding.layoutContentLogin.tvForgotPass.setAccessibilityRoleAsAction(role = getString(R.string.ada_role_link))
    }

    //API Response Consumer function for all APIs
    private fun consumeResponse(newApiResponse: NewApiResponse<Any>) {
        requireActivity().hideSoftKeyboard()

        when (newApiResponse) {
            is NewApiResponse.Idle -> {
                //Can Hide Loader But Not Needed
                DialogUtil.hideLoader()
            }

            is NewApiResponse.Loading -> {
                DialogUtil.showLoader(context = requireContext())
            }

            is NewApiResponse.Success -> {
//                if (newApiResponse.apiNameTag == API_TAG_NAME_GET_ACTIVITY_LAYOUT || newApiResponse.apiNameTag == API_TAG_NAME_GET_TIMING_LAYOUT || newApiResponse.apiNameTag == API_TAG_NAME_GET_CITATION_LAYOUT || newApiResponse.apiNameTag == API_TAG_NAME_GET_MUNICIPAL_CITATION_LAYOUT || newApiResponse.apiNameTag == API_TAG_NAME_UPDATE_TIME || newApiResponse.apiNameTag == API_TAG_NAME_GET_CITATION_NUMBER) {
//                    numberOfAPI++
//                }

                if (newApiResponse.apiNameTag != API_TAG_NAME_UPLOAD_IMAGES && newApiResponse.apiNameTag != API_TAG_NAME_CREATE_TICKET && newApiResponse.apiNameTag != API_TAG_NAME_CREATE_MUNICIPAL_CITATION_TICKET) {
                    numberOfAPI++
                }

                try {
                    when (newApiResponse.apiNameTag) {
                        API_TAG_NAME_GET_SHIFT_LIST -> {
                            //DialogUtil.hideLoader()

                            val shiftResponse = ObjectMapperProvider.fromJson(
                                (newApiResponse.data as JsonNode).toString(),
                                ShiftResponse::class.java
                            )
                            handleShiftListResponse(shiftResponse)
                        }

                        API_TAG_NAME_GET_HEARING_TIME_LIST -> {
                            //DialogUtil.hideLoader()
                            val hearingTimeResponse = ObjectMapperProvider.fromJson(
                                (newApiResponse.data as JsonNode).toString(),
                                HearingTimeResponse::class.java
                            )
                            handleHearingTimeListResponse(hearingTimeResponse)
                        }

                        API_TAG_NAME_LOGIN -> {
                            val commonLoginResponse = ObjectMapperProvider.fromJson(
                                (newApiResponse.data as JsonNode).toString(),
                                CommonLoginResponse::class.java
                            )
                            handleLoginResponse(commonLoginResponse)
                        }

                        API_TAG_NAME_UPDATE_TIME -> {
                            val updateTimeResponse = ObjectMapperProvider.fromJson(
                                (newApiResponse.data as JsonNode).toString(),
                                UpdateTimeResponse::class.java
                            )
                            handleUpdateTimeResponse(updateTimeResponse)
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

                        API_TAG_NAME_UPLOAD_IMAGES -> {
                            val uploadImagesResponse = ObjectMapperProvider.fromJson(
                                (newApiResponse.data as JsonNode).toString(),
                                UploadImagesResponse::class.java
                            )

                            handleUploadImageResponse(uploadImagesResponse)
                        }

                        API_TAG_NAME_GET_CITATION_NUMBER -> {
                            val citationNumberResponse = ObjectMapperProvider.fromJson(
                                (newApiResponse.data as JsonNode).toString(),
                                CitationNumberResponse::class.java
                            )

                            handleCitationNumberResponse(citationNumberResponse)

                        }

                        API_TAG_NAME_GET_CITATION_LAYOUT -> {
                            val citationLayoutResponse = ObjectMapperProvider.fromJson(
                                (newApiResponse.data as JsonNode).toString(),
                                CitationLayoutResponse::class.java
                            )

                            handleCitationLayoutResponse(citationLayoutResponse)
                        }

                        API_TAG_NAME_GET_MUNICIPAL_CITATION_LAYOUT -> {
                            val municipalCitationLayoutResponse = ObjectMapperProvider.fromJson(
                                (newApiResponse.data as JsonNode).toString(),
                                MunicipalCitationLayoutResponse::class.java
                            )

                            handleMunicipalCitationLayoutResponse(municipalCitationLayoutResponse)
                        }

                        API_TAG_NAME_GET_ACTIVITY_LAYOUT -> {
                            val activityLayoutResponse = ObjectMapperProvider.fromJson(
                                (newApiResponse.data as JsonNode).toString(),
                                ActivityLayoutResponse::class.java
                            )
                            handleActivityLayoutResponse(activityLayoutResponse)

                        }

                        API_TAG_NAME_GET_TIMING_LAYOUT -> {
                            val timingLayoutResponse = ObjectMapperProvider.fromJson(
                                (newApiResponse.data as JsonNode).toString(),
                                TimingLayoutResponse::class.java
                            )

                            handleTimingLayoutResponse(timingLayoutResponse)
                        }

                        API_TAG_NAME_GET_REFRESH_TOKEN -> {
                            val authRefreshResponse = ObjectMapperProvider.fromJson(
                                (newApiResponse.data as JsonNode).toString(),
                                AuthRefreshResponse::class.java
                            )

                            handleAuthTokenRefreshResponse(authRefreshResponse)

                        }

                        API_TAG_NAME_VERIFY_SITE -> {
                            val siteVerifyResponse = ObjectMapperProvider.fromJson(
                                (newApiResponse.data as JsonNode).toString(),
                                SiteVerifyResponse::class.java
                            )
                            handleSiteVerifyResponse(siteVerifyResponse)
                        }
                    }
                } catch (e: JsonMappingException) {
                    requireContext().toast(message = getString(R.string.error_desc_please_login_again_to_use_the_application))
                    e.printStackTrace()
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                if (numberOfAPI >= 5) {
                    viewLifecycleOwner.lifecycleScope.launch {
                        sharedPreference.write(SharedPrefKey.IS_LOGIN_ACTIVITY_LOGER_API, true)
                        delay(TIME_INTERVAL_2_SECONDS)
                        DialogUtil.hideLoader()

                        // Use safe navigation helper to avoid IllegalArgumentException when
                        // currentDestination doesn't have the requested action
                        nav.safeNavigate(R.id.action_loginScreenFragment_to_welcomeScreenFragment)
                    }
                }
            }

            is NewApiResponse.ApiError -> {
                numberOfAPI++
                if (newApiResponse.apiNameTag == API_TAG_NAME_CREATE_TICKET || newApiResponse.apiNameTag == API_TAG_NAME_CREATE_MUNICIPAL_CITATION_TICKET || newApiResponse.apiNameTag == API_TAG_NAME_UPLOAD_IMAGES) {
                    numberOfAPI = 0
                    callRestOfAPIs()
                } else {
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

    //Start of API Response handling
    private fun handleShiftListResponse(shiftListResponse: ShiftResponse) {
        if (!shiftListResponse.status.nullSafety()) return
        val firstData = shiftListResponse.data?.firstOrNull() ?: return
        if (firstData.metadata?.type != TYPE_SHIFT_LIST) return
        setDropdownShift(firstData.response)
        callAuthToken()
    }

    private fun handleHearingTimeListResponse(hearingTimeListResponse: HearingTimeResponse) {
        if (!hearingTimeListResponse.status.nullSafety()) return
        val firstData = hearingTimeListResponse.data?.firstOrNull() ?: return
        if (firstData.metadata?.type != TYPE_HEARING_TIME_LIST) return
        setDropdownHearingTime(firstData.response)
    }

    private fun handleLoginResponse(loginResponse: CommonLoginResponse) {
        responseModelLogin = loginResponse

        when (responseModelLogin?.status) {
            true -> {
                if (mEditTextEmail?.text?.trim().toString().equals(
                        sharedPreference.read(SharedPrefKey.USER_NAME, ""), ignoreCase = true
                    )
                ) {
                    sharedPreference.write(
                        SharedPrefKey.IS_NEW_USER_LOGIN_DELETED_DB, false
                    )
                } else if (!sharedPreference.read(SharedPrefKey.USER_NAME, "").isNullOrBlank()) {
                    sharedPreference.write(
                        SharedPrefKey.IS_NEW_USER_LOGIN_DELETED_DB, true
                    )
                }
                deleteSignatureIfExist()
                callRestOfAPIs()
            }

            false -> {
                AlertDialogUtils.showDialog(
                    context = requireContext(),
                    title = getString(R.string.error_title_login_api_response),
                    message = responseModelLogin?.response.nullSafety(getString(R.string.error_desc_unable_to_receive_data_from_service)),
                    positiveButtonText = getString(R.string.button_text_ok)
                )
            }

            else -> {
                AlertDialogUtils.showDialog(
                    context = requireContext(),
                    title = getString(R.string.error_title_login_api_response),
                    message = getString(R.string.error_desc_something_went_wrong_please_try_again),
                    positiveButtonText = getString(R.string.button_text_ok)
                )
            }
        }
    }

    private fun handleUpdateTimeResponse(updateTimeResponse: UpdateTimeResponse) {
        when (updateTimeResponse.status) {
            true -> {
                saveUpdatedTime(updateTimeResponse)
            }

            false -> {
                AlertDialogUtils.showDialog(
                    context = requireContext(),
                    title = getString(R.string.error_title_update_time_api_response),
                    message = updateTimeResponse.response.nullSafety(getString(R.string.error_desc_unable_to_receive_data_from_service)),
                    positiveButtonText = getString(R.string.button_text_ok)
                )
            }

            else -> {
                AlertDialogUtils.showDialog(
                    context = requireContext(),
                    title = getString(R.string.error_title_update_time_api_response),
                    message = getString(R.string.error_desc_something_went_wrong_please_try_again),
                    positiveButtonText = getString(R.string.button_text_ok)
                )
            }
        }
    }

    private fun handleCreateCitationTicketResponse(createTicketResponse: CreateTicketResponse) {
        try {
            if (LogUtil.WRITE_ALL_API_IN_LOGS_FILE) {
                ApiLogsClass.writeApiPayloadTex(
                    requireContext(),
                    "RESPONSE: " + ObjectMapperProvider.instance.writeValueAsString(
                        createTicketResponse
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        when (createTicketResponse.success) {
            true -> {
                lifecycleScope.launch {
                    loginScreenViewModel.updateCitationUploadStatus(
                        CITATION_UPLOADED, mCitationNumberId.nullSafety()
                    )
                }
                uploadOfflineCitation()
            }

            false -> {
                AlertDialogUtils.showDialog(
                    context = requireContext(),
                    title = getString(R.string.error_title_citation_api_response),
                    message = createTicketResponse.response.nullSafety(getString(R.string.error_desc_unable_to_receive_data_from_service)),
                    positiveButtonText = getString(R.string.button_text_ok)
                )
            }

            else -> {
                AlertDialogUtils.showDialog(
                    context = requireContext(),
                    title = getString(R.string.error_title_citation_api_response),
                    message = getString(R.string.error_desc_something_went_wrong_please_try_again),
                    positiveButtonText = getString(R.string.button_text_ok)
                )
            }
        }
    }

    private fun handleCreateMunicipalCitationTicketResponse(createMunicipalCitationTicketResponse: CreateMunicipalCitationTicketResponse) {
        try {
            if (LogUtil.WRITE_ALL_API_IN_LOGS_FILE) {
                ApiLogsClass.writeApiPayloadTex(
                    requireContext(),
                    "RESPONSE: " + ObjectMapperProvider.instance.writeValueAsString(
                        createMunicipalCitationTicketResponse
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        when (createMunicipalCitationTicketResponse.success) {
            true -> {
                lifecycleScope.launch {
                    loginScreenViewModel.updateCitationUploadStatus(
                        CITATION_UPLOADED, mCitationNumberId.nullSafety()
                    )
                }
                uploadOfflineCitation()
            }

            false -> {
                AlertDialogUtils.showDialog(
                    context = requireContext(),
                    title = getString(R.string.error_title_municipal_citation_api_response),
                    message = createMunicipalCitationTicketResponse.response.nullSafety(getString(R.string.error_desc_unable_to_receive_data_from_service)),
                    positiveButtonText = getString(R.string.button_text_ok)
                )
            }

            else -> {
                AlertDialogUtils.showDialog(
                    context = requireContext(),
                    title = getString(R.string.error_title_municipal_citation_api_response),
                    message = getString(R.string.error_desc_something_went_wrong_please_try_again),
                    positiveButtonText = getString(R.string.button_text_ok)
                )
            }
        }
    }

    private fun handleUploadImageResponse(uploadImagesResponse: UploadImagesResponse) {
        try {
            when (uploadImagesResponse.status) {
                true -> {
                    val data = uploadImagesResponse.data
                    val imageItem = data?.firstOrNull()
                    val links = imageItem?.response?.links

                    if (!data.isNullOrEmpty() && !links.isNullOrEmpty()) {
                        try {
                            offlineCitationImagesList?.getOrNull(imageUploadSuccessCount)?.id?.let { id ->
                                lifecycleScope.launch {
                                    loginScreenViewModel.deleteTempImagesOfflineWithId(id.toString())
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        imageUploadSuccessCount++
                        mImages.add(links.first())
                        if (imageUploadSuccessCount == (offlineCitationImagesList?.size ?: 0)) {
                            offlineCitationData?.let {
                                val ticketUploadStatusRequest = TicketUploadStatusRequest().apply {
                                    citationNumber = it.citationNumber
                                }
                                callCreateTicketApi(it)
                            }
                        }
                    } else {
                        AlertDialogUtils.showDialog(
                            context = requireContext(),
                            title = getString(R.string.error_title_upload_image_api_response),
                            message = getString(R.string.error_desc_getting_empty_image_array_from_server),
                            positiveButtonText = getString(R.string.button_text_ok)
                        )
                    }
                }

                false -> {
                    AlertDialogUtils.showDialog(
                        context = requireContext(),
                        title = getString(R.string.error_title_upload_image_api_response),
                        message = uploadImagesResponse.message.nullSafety(getString(R.string.error_desc_unable_to_receive_data_from_service)),
                        positiveButtonText = getString(R.string.button_text_ok)
                    )
                }

                else -> {
                    AlertDialogUtils.showDialog(
                        context = requireContext(),
                        title = getString(R.string.error_title_upload_image_api_response),
                        message = getString(R.string.error_desc_something_went_wrong_please_try_again),
                        positiveButtonText = getString(R.string.button_text_ok)
                    )
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun handleCitationNumberResponse(citationNumberResponse: CitationNumberResponse) {
        try {
            when (citationNumberResponse.status) {
                true -> {
                    lifecycleScope.launch {
                        loginScreenViewModel.saveBookletWithStatus(citationNumberResponse.data?.firstOrNull())
                    }
                }

                false -> {
                    AlertDialogUtils.showDialog(
                        context = requireContext(),
                        title = getString(R.string.error_title_citation_number_api_response),
                        message = citationNumberResponse.response.nullSafety(getString(R.string.error_desc_unable_to_receive_data_from_service)),
                        positiveButtonText = getString(R.string.button_text_ok)
                    )
                }

                else -> {
                    AlertDialogUtils.showDialog(
                        context = requireContext(),
                        title = getString(R.string.error_title_citation_number_api_response),
                        message = getString(R.string.error_desc_something_went_wrong_please_try_again),
                        positiveButtonText = getString(R.string.button_text_ok)
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun handleCitationLayoutResponse(citationLayoutResponse: CitationLayoutResponse) {
        try {
            when (citationLayoutResponse.success) {
                true -> {
                    lifecycleScope.launch {
                        loginScreenViewModel.saveCitationLayout(citationLayoutResponse)
                    }
                }

                false -> {
                    AlertDialogUtils.showDialog(
                        context = requireContext(),
                        title = getString(R.string.error_title_citation_layout_api_response),
                        message = citationLayoutResponse.response.nullSafety(getString(R.string.error_desc_unable_to_receive_data_from_service)),
                        positiveButtonText = getString(R.string.button_text_ok)
                    )
                }

                else -> {
                    AlertDialogUtils.showDialog(
                        context = requireContext(),
                        title = getString(R.string.error_title_citation_layout_api_response),
                        message = getString(R.string.error_desc_something_went_wrong_please_try_again),
                        positiveButtonText = getString(R.string.button_text_ok)
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun handleMunicipalCitationLayoutResponse(municipalCitationLayoutResponse: MunicipalCitationLayoutResponse) {
        try {
            when (municipalCitationLayoutResponse.success) {
                true -> {
                    lifecycleScope.launch {
                        loginScreenViewModel.saveMunicipalCitationLayout(
                            municipalCitationLayoutResponse
                        )
                    }
                }

                false -> {
                    AlertDialogUtils.showDialog(
                        context = requireContext(),
                        title = getString(R.string.error_title_municipal_citation_layout_api_response),
                        message = municipalCitationLayoutResponse.response.nullSafety(getString(R.string.error_desc_unable_to_receive_data_from_service)),
                        positiveButtonText = getString(R.string.button_text_ok)
                    )
                }


                else -> {
                    AlertDialogUtils.showDialog(
                        context = requireContext(),
                        title = getString(R.string.error_title_municipal_citation_layout_api_response),
                        message = getString(R.string.error_desc_something_went_wrong_please_try_again),
                        positiveButtonText = getString(R.string.button_text_ok)
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun handleActivityLayoutResponse(activityLayoutResponse: ActivityLayoutResponse) {
        try {
            when (activityLayoutResponse.status) {
                true -> {
                    lifecycleScope.launch {
                        loginScreenViewModel.saveActivityLayout(activityLayoutResponse)
                    }
                }

                false -> {
                    AlertDialogUtils.showDialog(
                        context = requireContext(),
                        title = getString(R.string.error_title_activity_layout_api_response),
                        message = activityLayoutResponse.response.nullSafety(getString(R.string.error_desc_unable_to_receive_data_from_service)),
                        positiveButtonText = getString(R.string.button_text_ok)
                    )
                }

                else -> {
                    AlertDialogUtils.showDialog(
                        context = requireContext(),
                        title = getString(R.string.error_title_activity_layout_api_response),
                        message = getString(R.string.error_desc_something_went_wrong_please_try_again),
                        positiveButtonText = getString(R.string.button_text_ok)
                    )
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun handleTimingLayoutResponse(timingLayoutResponse: TimingLayoutResponse) {
        try {
            when (timingLayoutResponse.status) {
                true -> {
                    lifecycleScope.launch {
                        loginScreenViewModel.saveTimingLayout(timingLayoutResponse)
                    }
                }

                false -> {
                    AlertDialogUtils.showDialog(
                        context = requireContext(),
                        title = getString(R.string.error_title_timing_layout_api_response),
                        message = timingLayoutResponse.response.nullSafety(getString(R.string.error_desc_unable_to_receive_data_from_service)),
                        positiveButtonText = getString(R.string.button_text_ok)
                    )
                }

                else -> {
                    AlertDialogUtils.showDialog(
                        context = requireContext(),
                        title = getString(R.string.error_title_timing_layout_api_response),
                        message = getString(R.string.error_desc_something_went_wrong_please_try_again),
                        positiveButtonText = getString(R.string.button_text_ok)
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun handleAuthTokenRefreshResponse(authRefreshResponse: AuthRefreshResponse) {
        try {
            sharedPreference.write(
                SharedPrefKey.ACCESS_TOKEN, authRefreshResponse.response.nullSafety()
            )
            uploadOfflineCitation()
        } catch (e: Exception) {
            e.printStackTrace()
            uploadOfflineCitation()
        }
    }

    private fun handleSiteVerifyResponse(siteVerifyResponse: SiteVerifyResponse) {
        if (siteVerifyResponse.status.nullSafety()) {
            callLoginUserAPI()
        } else {
            AlertDialogUtils.showDialog(
                context = requireContext(),
                title = getString(R.string.error_title_site_verify_error),
                message = "${siteVerifyResponse.message}",
                positiveButtonText = getString(R.string.button_text_ok)
            )
        }
    }
    //End of API Response handling

    private fun setDropdownShift(shiftList: List<ShiftStat>?) {
        if (shiftList.isNullOrEmpty()) return

        val shiftNames = shiftList.mapNotNull { it.shiftName }
        if (shiftNames.isEmpty()) return

        if (writeDefaultShiftTimeToSharedPreference()) {
            sharedPreference.write(SharedPrefKey.LOGIN_SHIFT, shiftNames[0])
        }

        val adapter = ArrayAdapter(
            requireContext(), R.layout.row_dropdown_menu_popup_item, shiftNames
        )
        mAutoComTextViewShift?.apply {
            threshold = 1
            setAdapter(adapter)
            setOnItemClickListener { _, _, position, _ ->
                mTextInputShift?.clearError()
                sharedPreference.write(SharedPrefKey.LOGIN_SHIFT, shiftNames[position])
            }

            //setDropDownListOnly()
            setListOnlyDropDown(context = requireContext(), textInputLayout = mTextInputShift)
        }
    }

    private fun setDropdownHearingTime(hearingTimeList: List<ResponseItemHearingTime?>?) {
        if (hearingTimeList.isNullOrEmpty()) return

        val hearingTimes = hearingTimeList.mapNotNull { it?.hearingTime }
        if (hearingTimes.isEmpty()) return

        val adapter = ArrayAdapter(
            requireContext(), R.layout.row_dropdown_menu_popup_item, hearingTimes
        )
        mAutoComTextViewHearingTime?.apply {
            threshold = 1
            setAdapter(adapter)
            setOnItemClickListener { _, _, position, _ ->
                requireActivity().hideSoftKeyboard()
                sharedPreference.write(
                    SharedPrefKey.LOGIN_HEARING_TIME, hearingTimes[position]
                )
            }
            // If tag is set to "listonly", apply setListOnly
            if (tag == CITATION_FORM_FIELD_TYPE_LIST_ONLY) {
                //setDropDownListOnly()
                setListOnlyDropDown(
                    context = requireContext(), textInputLayout = mTextInputLayoutTime
                )
            }
        }
    }


    private fun uploadOfflineCitation() {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            try {
                val result = loginScreenViewModel.getCitationInsurance()
                    ?.firstOrNull { it?.formStatus == 1 && it.citationNumber.isNotEmpty() }

                if (result != null) {
                    offlineCitationData = result
                    uploadOfflineImages(result)
                } else {
                    callRestOfAPIs()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                callRestOfAPIs()
            }
        }
    }

    private fun uploadOfflineImages(result: CitationInsurranceDatabaseModel) {
        lifecycleScope.launch {
            try {
                offlineCitationImagesList = mutableListOf()
                val imagesList = loginScreenViewModel.getCitationImageOffline(result.citationNumber)
                offlineCitationImagesList = imagesList
                if (imagesList.isNullOrEmpty()) {
                    callCreateTicketApi(result)
                } else {
                    imagesList.forEachIndexed { index, imageModel ->
                        callUploadImages(File(imageModel.citationImage.nullSafety()), index)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun callRestOfAPIs() {
        lifecycleScope.launch {
            if (sharedPreference.read(SharedPrefKey.IS_NEW_USER_LOGIN_DELETED_DB, false)) {
                val remainingBooklet: List<CitationBookletModel>? =
                    loginScreenViewModel.getCitationBooklet(BOOKLET_NOT_ISSUED)

                withContext(Dispatchers.Main) {
                    appDatabase.clearAllTables()
                }

                loginScreenViewModel.insertCitationBooklet(remainingBooklet ?: emptyList())

                sharedPreference.write(SharedPrefKey.IS_LOGGED_IN, true)
                try {
                    val lockLprModel = LockLprModel()
                    lockLprModel.mLprNumber = ""
                    lockLprModel.mMake = ""
                    lockLprModel.mModel = ""
                    lockLprModel.mColor = ""
                    lockLprModel.mAddress = ""
                    lockLprModel.mViolationCode = ""
                    lockLprModel.ticketCategory = ""
                    AppUtils.setLprLock(lockLprModel, requireContext(), sharedPreference)
                    sharedPreference.write(SharedPrefKey.LOCKED_LPR_BOOL, false)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            try {
                sharedPreference.write(SharedPrefKey.IS_LOGGED_IN, true)
                sharedPreference.write(SharedPrefKey.IS_LOGIN_LOGGED, true)
                sharedPreference.write(
                    SharedPrefKey.USER_NAME, mEditTextEmail?.text.toString().trim()
                )
                sharedPreference.write(
                    SharedPrefKey.PRE_TIME, responseModelLogin?.metadata?.lastLogin
                )
                sharedPreference.write(
                    SharedPrefKey.CURRENT_TIME, responseModelLogin?.metadata?.currentLogin
                )
                sharedPreference.write(
                    SharedPrefKey.ACCESS_TOKEN, responseModelLogin?.response.nullSafety()
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }

            //call api for activity layout
            callCitationDatasetAPI(TYPE_ACTIVITY_LAYOUT)
            //call api for timing layout
            callCitationDatasetAPI(TYPE_TIME_RECORD_LAYOUT)
            //call api for citation layout
            callCitationLayoutAPI()

            if (LogUtil.isMunicipalCitationEnabled()) {
                //call api for municipal citation layout
                callMunicipalCitationLayoutAPI()
            }

            try {
                val isBookletExits = loginScreenViewModel.getCitationBooklet(0)
                if (isBookletExits?.isEmpty().nullSafety()) {
                    //call api for citation booklet
                    callCitationNumberAPI()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            //call api to get updated time
            callUpdatedTimeAPI()

            val mLat =
                sharedPreference.read(SharedPrefKey.LAT, "0.0").nullSafety(defaultValue = "0.0")
                    .toDouble()
            val mLong =
                sharedPreference.read(SharedPrefKey.LONG, "0.0").nullSafety(defaultValue = "0.0")
                    .toDouble()
            val mLocationUpdateRequest = LocUpdateRequest()
            mLocationUpdateRequest.activityType = Constants.ACTIVITY_TYPE_LOCATION_UPDATE
            mLocationUpdateRequest.logType = Constants.LOG_TYPE_NODE_PORT
            mLocationUpdateRequest.locationUpdateType = "login"
            mLocationUpdateRequest.latitude = mLat
            mLocationUpdateRequest.longitude = mLong
            mLocationUpdateRequest.clientTimestamp = DateTimeUtils.getClientTimestamp(true)
            mainActivityViewModel.callLocationUpdateAPI(mLocationUpdateRequest)
        }
    }

    //save Booklet with status in Database
    private fun saveUpdatedTime(mGetList: UpdateTimeResponse) {
        lifecycleScope.launch {
            mTimeResponse = mGetList
            mResponseEntity = mTimeResponse?.data?.firstOrNull()
            try {
                val mSaveToDb = TimestampDatatbase().apply { id = 1 }
                timeDataList = UpdateTimeDataList()
                mTimingDatabaseList = loginScreenViewModel.getUpdateTimeResponse()
                if (mTimingDatabaseList != null && mTimeResponse != null && mResponseEntity != null) {
                    setTimingStatus(false)
                    timeDataList = setTimeDataList(
                        timeDataList!!, mTimingDatabaseList!!, mTimeResponse!!, mResponseEntity!!
                    )
                } else {
                    setTimingStatus(true)
                }
                mSaveToDb.timeList = timeDataList
                loginScreenViewModel.insertUpdatedTime(mSaveToDb)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    //set status for all dataset
    private fun setTimingStatus(status: Boolean) {
        try {
            if (status) {
                mResponseEntity?.activityLayout.nullSafety().takeIf { it.isNotEmpty() }?.let {
                    timeDataList?.activityLayout = UpdateTimeDb(it, true)
                }

                mResponseEntity?.activityList.nullSafety().takeIf { it.isNotEmpty() }?.let {
                    timeDataList?.activityList = UpdateTimeDb(it, true)
                }

                mResponseEntity?.agencyList.nullSafety().takeIf { it.isNotEmpty() }?.let {
                    timeDataList?.agencyList = UpdateTimeDb(it, true)
                }

                mResponseEntity?.beatList.nullSafety().takeIf { it.isNotEmpty() }?.let {
                    timeDataList?.beatList = UpdateTimeDb(it, true)
                }

                mResponseEntity?.cancelReasonList.nullSafety().takeIf { it.isNotEmpty() }?.let {
                    timeDataList?.cancelReasonList = UpdateTimeDb(it, true)
                }

                mResponseEntity?.carBodyStyleList.nullSafety().takeIf { it.isNotEmpty() }?.let {
                    timeDataList?.carBodyStyleList = UpdateTimeDb(it, true)
                }

                mResponseEntity?.carColorList.nullSafety().takeIf { it.isNotEmpty() }?.let {
                    timeDataList?.carColorList = UpdateTimeDb(it, true)
                }

                mResponseEntity?.carModelList.nullSafety().takeIf { it.isNotEmpty() }?.let {
                    timeDataList?.carModelList = UpdateTimeDb(it, true)
                }

                mResponseEntity?.carMakeList.nullSafety().takeIf { it.isNotEmpty() }?.let {
                    timeDataList?.carMakeList = UpdateTimeDb(it, true)
                }

                mResponseEntity?.citationData.nullSafety().takeIf { it.isNotEmpty() }?.let {
                    timeDataList?.citationData = UpdateTimeDb(it, true)
                }

                mResponseEntity?.citationLayout.nullSafety().takeIf { it.isNotEmpty() }?.let {
                    timeDataList?.citationLayout = UpdateTimeDb(it, true)
                }

                mResponseEntity?.commentsList.nullSafety().takeIf { it.isNotEmpty() }?.let {
                    timeDataList?.commentsList = UpdateTimeDb(it, true)
                }

                mResponseEntity?.decalYearList.nullSafety().takeIf { it.isNotEmpty() }?.let {
                    timeDataList?.decalYearList = UpdateTimeDb(it, true)
                }

                mResponseEntity?.directionList.nullSafety().takeIf { it.isNotEmpty() }?.let {
                    timeDataList?.directionList = UpdateTimeDb(it, true)
                }

                mResponseEntity?.exemptData.nullSafety().takeIf { it.isNotEmpty() }?.let {
                    timeDataList?.exemptData = UpdateTimeDb(it, true)
                }

                mResponseEntity?.lotList.nullSafety().takeIf { it.isNotEmpty() }?.let {
                    timeDataList?.lotList = UpdateTimeDb(it, true)
                }

                mResponseEntity?.makeModelColorData.nullSafety().takeIf { it.isNotEmpty() }?.let {
                    timeDataList?.makeModelColorData = UpdateTimeDb(it, true)
                }

                mResponseEntity?.meterList.nullSafety().takeIf { it.isNotEmpty() }?.let {
                    timeDataList?.meterList = UpdateTimeDb(it, true)
                }

                mResponseEntity?.notesList.nullSafety().takeIf { it.isNotEmpty() }?.let {
                    timeDataList?.notesList = UpdateTimeDb(it, true)
                }

                mResponseEntity?.paymentData.nullSafety().takeIf { it.isNotEmpty() }?.let {
                    timeDataList?.paymentData = UpdateTimeDb(it, true)
                }

                mResponseEntity?.permitData.nullSafety().takeIf { it.isNotEmpty() }?.let {
                    timeDataList?.permitData = UpdateTimeDb(it, true)
                }

                mResponseEntity?.radioList.nullSafety().takeIf { it.isNotEmpty() }?.let {
                    timeDataList?.radioList = UpdateTimeDb(it, true)
                }

                mResponseEntity?.regulationTimeList.nullSafety().takeIf { it.isNotEmpty() }?.let {
                    timeDataList?.regulationTimeList = UpdateTimeDb(it, true)
                }

                mResponseEntity?.remarksList.nullSafety().takeIf { it.isNotEmpty() }?.let {
                    timeDataList?.remarksList = UpdateTimeDb(it, true)
                }

                mResponseEntity?.scofflawData.nullSafety().takeIf { it.isNotEmpty() }?.let {
                    timeDataList?.scofflawData = UpdateTimeDb(it, true)
                }

                mResponseEntity?.shiftList.nullSafety().takeIf { it.isNotEmpty() }?.let {
                    timeDataList?.shiftList = UpdateTimeDb(it, true)
                }

                mResponseEntity?.sideList.nullSafety().takeIf { it.isNotEmpty() }?.let {
                    timeDataList?.sideList = UpdateTimeDb(it, true)
                }

                mResponseEntity?.stateList.nullSafety().takeIf { it.isNotEmpty() }?.let {
                    timeDataList?.stateList = UpdateTimeDb(it, true)
                }

                mResponseEntity?.stolenData.nullSafety().takeIf { it.isNotEmpty() }?.let {
                    timeDataList?.stolenData = UpdateTimeDb(it, true)
                }

                mResponseEntity?.streetList.nullSafety().takeIf { it.isNotEmpty() }?.let {
                    timeDataList?.streetList = UpdateTimeDb(it, true)
                }

                mResponseEntity?.supervisorList.nullSafety().takeIf { it.isNotEmpty() }?.let {
                    timeDataList?.supervisorList = UpdateTimeDb(it, true)
                }

                mResponseEntity?.tierStemList.nullSafety().takeIf { it.isNotEmpty() }?.let {
                    timeDataList?.tierStemList = UpdateTimeDb(it, true)
                }

                mResponseEntity?.timingData.nullSafety().takeIf { it.isNotEmpty() }?.let {
                    timeDataList?.timingData = UpdateTimeDb(it, true)
                }

                mResponseEntity?.timingRecordLayout.nullSafety().takeIf { it.isNotEmpty() }?.let {
                    timeDataList?.timingRecordLayout = UpdateTimeDb(it, true)
                }

                mResponseEntity?.vehiclePlateTypeList.nullSafety().takeIf { it.isNotEmpty() }?.let {
                    timeDataList?.vehiclePlateTypeList = UpdateTimeDb(it, true)
                }

                mResponseEntity?.violationList.nullSafety().takeIf { it.isNotEmpty() }?.let {
                    timeDataList?.violationList = UpdateTimeDb(it, true)
                }

                mResponseEntity?.vioList.nullSafety().takeIf { it.isNotEmpty() }?.let {
                    timeDataList?.vioList = UpdateTimeDb(it, true)
                }

                mResponseEntity?.holidayCalendarList.nullSafety().takeIf { it.isNotEmpty() }?.let {
                    timeDataList?.holidayCalendarList = UpdateTimeDb(it, true)
                }

                mResponseEntity?.zoneList.nullSafety().takeIf { it.isNotEmpty() }?.let {
                    timeDataList?.zoneList = UpdateTimeDb(it, true)
                }

                mResponseEntity?.mCityZoneList.nullSafety().takeIf { it.isNotEmpty() }?.let {
                    timeDataList?.mCityZoneList = UpdateTimeDb(it, true)
                }

                mResponseEntity?.mVoidAndReissueList.nullSafety().takeIf { it.isNotEmpty() }?.let {
                    timeDataList?.mVoidAndReissueList = UpdateTimeDb(it, true)
                }

                mResponseEntity?.mDeviceList.nullSafety().takeIf { it.isNotEmpty() }?.let {
                    timeDataList?.mDeviceList = UpdateTimeDb(it, true)
                }

                mResponseEntity?.mEquipmentList.nullSafety().takeIf { it.isNotEmpty() }?.let {
                    timeDataList?.mEquipmentList = UpdateTimeDb(it, true)
                }

                mResponseEntity?.mBlockList.nullSafety().takeIf { it.isNotEmpty() }?.let {
                    timeDataList?.mBlockList = UpdateTimeDb(it, true)
                }

                mResponseEntity?.mSpaceList.nullSafety().takeIf { it.isNotEmpty() }?.let {
                    timeDataList?.mSpaceList = UpdateTimeDb(it, true)
                }

                mResponseEntity?.mSquadList.nullSafety().takeIf { it.isNotEmpty() }?.let {
                    timeDataList?.mSquadList = UpdateTimeDb(it, true)
                }

                if (LogUtil.isMunicipalCitationEnabled()) {
                    mResponseEntity?.municipalViolationList.nullSafety().takeIf { it.isNotEmpty() }
                        ?.let {
                            timeDataList?.municipalViolationList = UpdateTimeDb(it, true)
                        }

                    mResponseEntity?.municipalBlockList.nullSafety().takeIf { it.isNotEmpty() }
                        ?.let {
                            timeDataList?.municipalBlockList = UpdateTimeDb(it, true)
                        }

                    mResponseEntity?.municipalStreetList.nullSafety().takeIf { it.isNotEmpty() }
                        ?.let {
                            timeDataList?.municipalStreetList = UpdateTimeDb(it, true)
                        }

                    mResponseEntity?.municipalCityList.nullSafety().takeIf { it.isNotEmpty() }
                        ?.let {
                            timeDataList?.municipalCityList = UpdateTimeDb(it, true)
                        }

                    mResponseEntity?.municipalStateList.nullSafety().takeIf { it.isNotEmpty() }
                        ?.let {
                            timeDataList?.municipalStateList = UpdateTimeDb(it, true)
                        }
                }
            } else {
                mTimingDatabaseList?.timeList?.activityLayout?.name?.let { name ->
                    timeDataList?.activityLayout = UpdateTimeDb(name, false)
                }

                mTimingDatabaseList?.timeList?.activityList?.name?.let { name ->
                    timeDataList?.activityList = UpdateTimeDb(name, false)
                }

                mTimingDatabaseList?.timeList?.agencyList?.name?.let { name ->
                    timeDataList?.agencyList = UpdateTimeDb(name, false)
                }

                mTimingDatabaseList?.timeList?.beatList?.name?.let { name ->
                    timeDataList?.beatList = UpdateTimeDb(name, false)
                }

                mTimingDatabaseList?.timeList?.cancelReasonList?.name?.let { name ->
                    timeDataList?.cancelReasonList = UpdateTimeDb(name, false)
                }

                mTimingDatabaseList?.timeList?.carBodyStyleList?.name?.let { name ->
                    timeDataList?.carBodyStyleList = UpdateTimeDb(name, false)
                }

                mTimingDatabaseList?.timeList?.carColorList?.name?.let { name ->
                    timeDataList?.carColorList = UpdateTimeDb(name, false)
                }

                mTimingDatabaseList?.timeList?.carModelList?.name?.let { name ->
                    timeDataList?.carModelList = UpdateTimeDb(name, false)
                }

                mTimingDatabaseList?.timeList?.carMakeList?.name?.let { name ->
                    timeDataList?.carMakeList = UpdateTimeDb(name, false)
                }

                mTimingDatabaseList?.timeList?.citationData?.name?.let { name ->
                    timeDataList?.citationData = UpdateTimeDb(name, false)
                }

                mTimingDatabaseList?.timeList?.citationLayout?.name?.let { name ->
                    timeDataList?.citationLayout = UpdateTimeDb(name, false)
                }

                mTimingDatabaseList?.timeList?.commentsList?.name?.let { name ->
                    timeDataList?.commentsList = UpdateTimeDb(name, false)
                }

                mTimingDatabaseList?.timeList?.decalYearList?.name?.let { name ->
                    timeDataList?.decalYearList = UpdateTimeDb(name, false)
                }

                mTimingDatabaseList?.timeList?.directionList?.name?.let { name ->
                    timeDataList?.directionList = UpdateTimeDb(name, false)
                }

                mTimingDatabaseList?.timeList?.exemptData?.name?.let { name ->
                    timeDataList?.exemptData = UpdateTimeDb(name, false)
                }

                mTimingDatabaseList?.timeList?.lotList?.name?.let { name ->
                    timeDataList?.lotList = UpdateTimeDb(name, false)
                }

                mTimingDatabaseList?.timeList?.makeModelColorData?.name?.let { name ->
                    timeDataList?.makeModelColorData = UpdateTimeDb(name, false)
                }

                mTimingDatabaseList?.timeList?.meterList?.name?.let { name ->
                    timeDataList?.meterList = UpdateTimeDb(name, false)
                }

                mTimingDatabaseList?.timeList?.notesList?.name?.let { name ->
                    timeDataList?.notesList = UpdateTimeDb(name, false)
                }

                mTimingDatabaseList?.timeList?.paymentData?.name?.let { name ->
                    timeDataList?.paymentData = UpdateTimeDb(name, false)
                }

                mTimingDatabaseList?.timeList?.permitData?.name?.let { name ->
                    timeDataList?.permitData = UpdateTimeDb(name, false)
                }

                mTimingDatabaseList?.timeList?.radioList?.name?.let { name ->
                    timeDataList?.radioList = UpdateTimeDb(name, false)
                }

                mTimingDatabaseList?.timeList?.regulationTimeList?.name?.let { name ->
                    timeDataList?.regulationTimeList = UpdateTimeDb(name, false)
                }

                mTimingDatabaseList?.timeList?.remarksList?.name?.let { name ->
                    timeDataList?.remarksList = UpdateTimeDb(name, false)
                }

                mTimingDatabaseList?.timeList?.scofflawData?.name?.let { name ->
                    timeDataList?.scofflawData = UpdateTimeDb(name, false)
                }

                mTimingDatabaseList?.timeList?.shiftList?.name?.let { name ->
                    timeDataList?.shiftList = UpdateTimeDb(name, false)
                }

                mTimingDatabaseList?.timeList?.sideList?.name?.let { name ->
                    timeDataList?.sideList = UpdateTimeDb(name, false)
                }

                mTimingDatabaseList?.timeList?.stateList?.name?.let { name ->
                    timeDataList?.stateList = UpdateTimeDb(name, false)
                }

                mTimingDatabaseList?.timeList?.stolenData?.name?.let { name ->
                    timeDataList?.stolenData = UpdateTimeDb(name, false)
                }

                mTimingDatabaseList?.timeList?.streetList?.name?.let { name ->
                    timeDataList?.streetList = UpdateTimeDb(name, false)
                }

                mTimingDatabaseList?.timeList?.supervisorList?.name?.let { name ->
                    timeDataList?.supervisorList = UpdateTimeDb(name, false)
                }

                mTimingDatabaseList?.timeList?.tierStemList?.name?.let { name ->
                    timeDataList?.tierStemList = UpdateTimeDb(name, false)
                }

                mTimingDatabaseList?.timeList?.timingData?.name?.let { name ->
                    timeDataList?.timingData = UpdateTimeDb(name, false)
                }

                mTimingDatabaseList?.timeList?.timingRecordLayout?.name?.let { name ->
                    timeDataList?.timingRecordLayout = UpdateTimeDb(name, false)
                }

                mTimingDatabaseList?.timeList?.vehiclePlateTypeList?.name?.let { name ->
                    timeDataList?.vehiclePlateTypeList = UpdateTimeDb(name, false)
                }

                mTimingDatabaseList?.timeList?.violationList?.name?.let { name ->
                    timeDataList?.violationList = UpdateTimeDb(name, false)
                }

                mTimingDatabaseList?.timeList?.vioList?.name?.let { name ->
                    timeDataList?.vioList = UpdateTimeDb(name, false)
                }

                mTimingDatabaseList?.timeList?.holidayCalendarList?.name?.let { name ->
                    timeDataList?.holidayCalendarList = UpdateTimeDb(name, false)
                }

                mTimingDatabaseList?.timeList?.zoneList?.name?.let { name ->
                    timeDataList?.zoneList = UpdateTimeDb(name, false)
                }

                mTimingDatabaseList?.timeList?.mCityZoneList?.name?.let { name ->
                    timeDataList?.mCityZoneList = UpdateTimeDb(name, false)
                }

                mTimingDatabaseList?.timeList?.mVoidAndReissueList?.name?.let { name ->
                    timeDataList?.mVoidAndReissueList = UpdateTimeDb(name, false)
                }

                mTimingDatabaseList?.timeList?.mDeviceList?.name?.let { name ->
                    timeDataList?.mDeviceList = UpdateTimeDb(name, false)
                }

                mTimingDatabaseList?.timeList?.mEquipmentList?.name?.let { name ->
                    timeDataList?.mEquipmentList = UpdateTimeDb(name, false)
                }

                mTimingDatabaseList?.timeList?.mBlockList?.name?.let { name ->
                    timeDataList?.mBlockList = UpdateTimeDb(name, false)
                }

                mTimingDatabaseList?.timeList?.mSpaceList?.name?.let { name ->
                    timeDataList?.mSpaceList = UpdateTimeDb(name, false)
                }

                mTimingDatabaseList?.timeList?.mSquadList?.name?.let { name ->
                    timeDataList?.mSquadList = UpdateTimeDb(name, false)
                }

                if (LogUtil.isMunicipalCitationEnabled()) {
                    mTimingDatabaseList?.timeList?.municipalViolationList?.name?.let { name ->
                        timeDataList?.municipalViolationList = UpdateTimeDb(name, false)
                    }

                    mTimingDatabaseList?.timeList?.municipalBlockList?.name?.let { name ->
                        timeDataList?.municipalBlockList = UpdateTimeDb(name, false)
                    }

                    mTimingDatabaseList?.timeList?.municipalStreetList?.name?.let { name ->
                        timeDataList?.municipalStreetList = UpdateTimeDb(name, false)
                    }

                    mTimingDatabaseList?.timeList?.municipalCityList?.name?.let { name ->
                        timeDataList?.municipalCityList = UpdateTimeDb(name, false)
                    }

                    mTimingDatabaseList?.timeList?.municipalStateList?.name?.let { name ->
                        timeDataList?.municipalStateList = UpdateTimeDb(name, false)
                    }


                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /* Call Api For update profile */
    private fun callUploadImages(file: File?, num: Int) {
        if (file == null) return
        if (requireContext().isInternetAvailable()) {
            val (mDropdownList, mRequestBodyType, files) = MultipartUtils.getImageUploadRequestData(
                file, mCitationNumberId.nullSafety(), num, MULTIPART_CONTENT_TYPE_CITATION_IMAGES
            )
            loginScreenViewModel.callUploadImageAPI(mDropdownList, mRequestBodyType, files)
        } else {
            NoInternetDialogUtil.showDialog(
                context = requireContext(),
                positiveButtonText = getString(R.string.button_text_ok),
                negativeButtonText = getString(R.string.button_text_retry),
                listener = object : NoInternetDialogListener {
                    override fun onNegativeButtonClicked() {
                        callUploadImages(file, num)
                    }
                })
        }
    }


    /* Call Api For citation layout details */
    private fun callCreateTicketApi(mIssuranceModel: CitationInsurranceDatabaseModel) {
        if (requireContext().isInternetAvailable()) {
            lifecycleScope.launch {
                val welcomeForm: WelcomeForm? = loginScreenViewModel.getWelcomeForm()
                if (mIssuranceModel.citationData?.municipalCitationMotoristDetailsModel == null) {
                    val createTicketRequest = RequestHandler.getCreateCitationTicketRequest(
                        welcomeForm = welcomeForm,
                        insuranceModel = mIssuranceModel,
                        mImages = mImages,
                        isReissue = false,
                        timeLimitEnforcementObservedTime = mIssuranceModel.citationData?.officer?.observationTime.nullSafety(),
                    )

                    loginScreenViewModel.callCreateCitationTicketAPI(createTicketRequest)

                    try {
                        if (LogUtil.WRITE_ALL_API_IN_LOGS_FILE) {
                            ApiLogsClass.writeApiPayloadTex(
                                requireContext(), "------------LOGIN Create API-----------------"
                            )
                            ApiLogsClass.writeApiPayloadTex(
                                requireContext(),
                                "REQUEST: " + ObjectMapperProvider.instance.writeValueAsString(
                                    createTicketRequest
                                )
                            )
                        }
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

                    loginScreenViewModel.callCreateMunicipalCitationTicketAPI(createTicketRequest)
                    try {
                        if (LogUtil.WRITE_ALL_API_IN_LOGS_FILE) {
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
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        } else {
            lifecycleScope.launch {
                loginScreenViewModel.updateCitationUploadStatus(
                    CITATION_UNUPLOADED_API_FAILED, mIssuranceModel.citationNumber
                )
            }
        }
    }

    // Set Footer Text
    private fun setFooterText() {
        if (BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_RISE_TEK_INNOVA, ignoreCase = true
            )
        ) {
            appCompatEditTextSiteName?.text = getString(R.string.cmp_name_title_innova)
        } else {
            appCompatEditTextSiteName?.text = getString(R.string.cmp_name_title)
        }

        appCompatEditTextCompanyName?.text = getString(R.string.app_name_title)
    }

    private fun deleteAllPhotosEveryWeek() {
        try {
            val isFriday = SDF_EEEE.format(Date()).equals(DAY_FRIDAY, ignoreCase = true)
            val alreadyDeleted = sharedPreference.read(SharedPrefKey.DELETE_IMAGE_FOLDER, false)

            if (isFriday && !alreadyDeleted) {
                takeDatabaseBackUpAndSave(requireContext())
                listOf(Constants.CAMERA, Constants.SCANNER).forEach { folder ->
                    val dir = File(
                        Environment.getExternalStorageDirectory().absolutePath,
                        Constants.FILE_NAME + folder
                    )
                    deleteRecursive(dir)
                }
                sharedPreference.write(SharedPrefKey.DELETE_IMAGE_FOLDER, true)
            } else if (!isFriday) {
                sharedPreference.write(SharedPrefKey.DELETE_IMAGE_FOLDER, false)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun deleteRecursive(directory: File) {
        try {
            if (directory.exists() && directory.isDirectory) {
                directory.listFiles()?.forEach { child ->
                    deleteRecursive(child)
                }
            }
            directory.delete()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    // Call Auth Token Refresh API
    private fun callAuthToken() {
        lifecycleScope.launch {
            try {
                val insurranceModel = loginScreenViewModel.getCitationInsurance()

                val result = insurranceModel?.firstOrNull { it?.formStatus == 1 }
                result?.let {
                    mCitationNumberId = it.citationNumber
                    if (it.citationNumber.isNotEmpty()) {
                        loginScreenViewModel.callAuthTokenRefreshAPI()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /* Clear Dataset & Activity tables */
    private fun clearDatasetAndActivityTables(isManual: Boolean) {
        mainActivityViewModel.deleteDatasetAndActivityTables(isManual = isManual)
    }

    /* Load captcha for verification*/
    private fun loadCaptchaVerification() {
        SafetyNet.getClient(requireContext()).verifyWithRecaptcha(BuildConfig.CAPTCHA_SITE_KEY)
            .addOnSuccessListener(
                requireActivity() as AppCompatActivity
            ) { response ->
                val userResponseToken = response.tokenResult
                if (userResponseToken?.isNotEmpty().nullSafety()) {
                    loginScreenViewModel.callVerifySite(
                        secretKey = BuildConfig.CAPTCHA_SECRET_KEY,
                        userResponseToken = userResponseToken.nullSafety()
                    )
                }
            }.addOnFailureListener(requireActivity()) { exception ->
                //THIS WAS NEVER WORKED : JANAK
//                if (exception is ApiException) {
//                    AlertDialogUtils.showDialog(
//                        context = requireContext(),
//                        title = "API Error",
//                        message = "API ${exception.statusCode}: ${exception.message}",
//                        positiveButtonText = "Yes"
//                    )
//                } else {
//                    AlertDialogUtils.showDialog(
//                        context = requireContext(),
//                        title = "API Error",
//                        message = "API ${exception.message}",
//                        positiveButtonText = "Yes"
//                    )
//                }
                callLoginUserAPI()
            }
    }

    /* Call Api For Citation issue number*/
    private fun callCitationNumberAPI() {
        if (requireContext().isInternetAvailable()) {
            lifecycleScope.launch {
                val uniqueID = requireContext().getAndroidID()
                val deviceName =
                    loginScreenViewModel.getWelcomeForm()?.officerDeviceName.nullSafety()

                val mCitationNumberRequest = CitationNumberRequest().apply {
                    this.deviceId = getDeviceIdForAPI(uniqueID = uniqueID, deviceName = deviceName)
                }
                loginScreenViewModel.callGetCitationNumberAPI(mCitationNumberRequest)
            }

        } else {
            NoInternetDialogUtil.showDialog(
                context = requireContext(),
                positiveButtonText = getString(R.string.button_text_ok),
                negativeButtonText = getString(R.string.button_text_retry),
                listener = object : NoInternetDialogListener {
                    override fun onNegativeButtonClicked() {
                        callCitationNumberAPI()
                    }
                })
        }
    }

    /* Call Api For Citation Dataset Type */
    private fun callCitationDatasetAPI(mType: String) {
        if (requireContext().isInternetAvailable()) {
            if (mType == TYPE_ACTIVITY_LAYOUT) {
                loginScreenViewModel.callGetActivityLayoutAPI()
            } else if (mType == TYPE_TIME_RECORD_LAYOUT) {
                loginScreenViewModel.callGetTimingLayoutAPI()
            }
        } else {
            NoInternetDialogUtil.showDialog(
                context = requireContext(),
                positiveButtonText = getString(R.string.button_text_ok),
                negativeButtonText = getString(R.string.button_text_retry),
                listener = object : NoInternetDialogListener {
                    override fun onNegativeButtonClicked() {
                        callCitationDatasetAPI(mType = mType)
                    }
                })
        }
    }

    /* Call Api For citation layout details */
    private fun callCitationLayoutAPI() {
        if (requireContext().isInternetAvailable()) {
            loginScreenViewModel.callGetCitationLayoutAPI()
        } else {
            NoInternetDialogUtil.showDialog(
                context = requireContext(),
                positiveButtonText = getString(R.string.button_text_ok),
                negativeButtonText = getString(R.string.button_text_retry),
                listener = object : NoInternetDialogListener {
                    override fun onNegativeButtonClicked() {
                        callCitationLayoutAPI()
                    }
                })
        }
    }

    /* Call Api For Municipal Citation Layout details */
    private fun callMunicipalCitationLayoutAPI() {
        if (requireContext().isInternetAvailable()) {
            loginScreenViewModel.callGetMunicipalCitationLayoutAPI()
        } else {
            NoInternetDialogUtil.showDialog(
                context = requireContext(),
                positiveButtonText = getString(R.string.button_text_ok),
                negativeButtonText = getString(R.string.button_text_retry),
                listener = object : NoInternetDialogListener {
                    override fun onNegativeButtonClicked() {
                        callMunicipalCitationLayoutAPI()
                    }
                })
        }
    }

    /* Call Api For Updated Time */
    private fun callUpdatedTimeAPI() {
        if (requireContext().isInternetAvailable()) {
            loginScreenViewModel.callUpdateTimeAPI()
        } else {
            NoInternetDialogUtil.showDialog(
                context = requireContext(),
                positiveButtonText = getString(R.string.button_text_ok),
                negativeButtonText = getString(R.string.button_text_retry),
                listener = object : NoInternetDialogListener {
                    override fun onNegativeButtonClicked() {
                        callUpdatedTimeAPI()
                    }
                })
        }
    }

    /* Call Api For Login user and get user details */
    private fun callLoginUserAPI() {
        if (requireContext().isInternetAvailable()) {
            val mLoginRequest = SiteOfficerLoginRequest()
            mLoginRequest.siteId = getSiteId(requireContext())
            mLoginRequest.siteOfficerUserName = mEditTextEmail?.text?.trim().toString()
            mLoginRequest.siteOfficerPassword = mEditTextPassword?.text?.trim().toString()

            loginScreenViewModel.callLoginAPI(siteOfficerLoginRequest = mLoginRequest)
            mLoginButton?.enableButton()
        } else {
            NoInternetDialogUtil.showDialog(
                context = requireContext(),
                positiveButtonText = getString(R.string.button_text_ok),
                negativeButtonText = getString(R.string.button_text_retry),
                listener = object : NoInternetDialogListener {
                    override fun onNegativeButtonClicked() {
                        callLoginUserAPI()
                    }
                })
        }
    }

    // Delete signature file if exist
    private fun deleteSignatureIfExist() {
        try {
            var mPath = sharedPreference.read(SharedPrefKey.FILE_PATH, "")
            mPath = mPath + Constants.CAMERA + "/" + getSignatureFileNameWithExt()
            val file = File(mPath)
            if (file.exists()) file.delete()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Function used to delete stored header & footer file forcefully
     */
    private fun removeCachedHeaderFooterImage() {
        try {
            val headerFile = File(getHeaderFooterImageFileFullPath(true))
            val footerFile = File(getHeaderFooterImageFileFullPath(false))

            if (headerFile.exists()) headerFile.delete()

            if (footerFile.exists()) footerFile.delete()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}