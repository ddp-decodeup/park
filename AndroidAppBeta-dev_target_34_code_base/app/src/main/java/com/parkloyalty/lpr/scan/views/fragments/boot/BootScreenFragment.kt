package com.parkloyalty.lpr.scan.views.fragments.boot

import DialogUtil
import android.Manifest
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.JsonNode
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.parkloyalty.lpr.scan.BuildConfig
import com.parkloyalty.lpr.scan.R
import com.parkloyalty.lpr.scan.databinding.FragmentBootScreenBinding
import com.parkloyalty.lpr.scan.extensions.DuncanBrandingApp13
import com.parkloyalty.lpr.scan.extensions.getBoxStrokeColor
import com.parkloyalty.lpr.scan.extensions.getErrorMessage
import com.parkloyalty.lpr.scan.extensions.getIndexOfLocation
import com.parkloyalty.lpr.scan.extensions.getIndexOfMakeText
import com.parkloyalty.lpr.scan.extensions.getIndexOfStateName
import com.parkloyalty.lpr.scan.extensions.hideSoftKeyboard
import com.parkloyalty.lpr.scan.extensions.hideView
import com.parkloyalty.lpr.scan.extensions.isInternetAvailable
import com.parkloyalty.lpr.scan.extensions.logE
import com.parkloyalty.lpr.scan.extensions.nav
import com.parkloyalty.lpr.scan.extensions.nullOrEmptySafety
import com.parkloyalty.lpr.scan.extensions.nullSafety
import com.parkloyalty.lpr.scan.extensions.safeNavigate
import com.parkloyalty.lpr.scan.extensions.setCrossClearButton
import com.parkloyalty.lpr.scan.extensions.setListOnlyDropDown
import com.parkloyalty.lpr.scan.extensions.setupTextInputLayout
import com.parkloyalty.lpr.scan.extensions.showErrorWithShake
import com.parkloyalty.lpr.scan.extensions.showView
import com.parkloyalty.lpr.scan.extensions.toast
import com.parkloyalty.lpr.scan.interfaces.Constants
import com.parkloyalty.lpr.scan.interfaces.PrintInterface
import com.parkloyalty.lpr.scan.interfaces.SharedPrefKey
import com.parkloyalty.lpr.scan.locationservice.LocationUtils.getAddressFromLatLng
import com.parkloyalty.lpr.scan.network.response_handler.NewApiResponse
import com.parkloyalty.lpr.scan.ui.boot.model.BootInstanceTicketErrorResponse
import com.parkloyalty.lpr.scan.ui.boot.model.BootInstanceTicketRequest
import com.parkloyalty.lpr.scan.ui.boot.model.BootInstanceTicketResponse
import com.parkloyalty.lpr.scan.ui.boot.model.BootMetadataRequest
import com.parkloyalty.lpr.scan.ui.boot.model.BootRequest
import com.parkloyalty.lpr.scan.ui.boot.model.LocationDetails
import com.parkloyalty.lpr.scan.ui.boot.model.OfficerDetails
import com.parkloyalty.lpr.scan.ui.boot.model.PrintBootNoticeModel
import com.parkloyalty.lpr.scan.ui.boot.model.ResponseBoot
import com.parkloyalty.lpr.scan.ui.boot.model.ResponseBootError
import com.parkloyalty.lpr.scan.ui.boot.model.VehicleDetails
import com.parkloyalty.lpr.scan.ui.boot.model.ViolationDetails
import com.parkloyalty.lpr.scan.ui.check_setup.model.lpr_model.ScofflawDataResponse
import com.parkloyalty.lpr.scan.ui.check_setup.model.timing.AddTimingRequest
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.CitationBookletModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.CitationNumberData
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.CitationNumberRequest
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.CitationNumberResponse
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DatasetResponse
import com.parkloyalty.lpr.scan.ui.login.model.WelcomeForm
import com.parkloyalty.lpr.scan.ui.printer.PrinterType
import com.parkloyalty.lpr.scan.ui.printer.ZebraPrinterUseCase
import com.parkloyalty.lpr.scan.util.API_CONSTANT_BOOT_TYPE_HANDHELD_INITIATED
import com.parkloyalty.lpr.scan.util.API_CONSTANT_PLATE_TYPE_PERSONAL
import com.parkloyalty.lpr.scan.util.AppUtils
import com.parkloyalty.lpr.scan.util.INTENT_KEY_CITATION_NUMBER
import com.parkloyalty.lpr.scan.util.INTENT_KEY_COLOR
import com.parkloyalty.lpr.scan.util.INTENT_KEY_LPNUMBER
import com.parkloyalty.lpr.scan.util.INTENT_KEY_MAKE
import com.parkloyalty.lpr.scan.util.INTENT_KEY_MODEL
import com.parkloyalty.lpr.scan.util.INTENT_KEY_SCCOFFLAW
import com.parkloyalty.lpr.scan.util.INTENT_KEY_VIOLATION_DATE
import com.parkloyalty.lpr.scan.util.LogUtil
import com.parkloyalty.lpr.scan.util.SDF_MM_DD_YYYY
import com.parkloyalty.lpr.scan.util.SharedPref
import com.parkloyalty.lpr.scan.util.Util.getIndexOfLot
import com.parkloyalty.lpr.scan.util.Util.getIndexOfSpaceName
import com.parkloyalty.lpr.scan.util.ZebraCommandPrintUtils
import com.parkloyalty.lpr.scan.util.permissions.BluetoothPermissionUtil
import com.parkloyalty.lpr.scan.util.setAccessibilityForTextInputLayoutDropdownButtons
import com.parkloyalty.lpr.scan.util.setAccessibilityForTextInputLayoutWithHintOnly
import com.parkloyalty.lpr.scan.util.setDoNothingAccessibilityForTextInputLayoutDropdownButtons
import com.parkloyalty.lpr.scan.utils.AlertDialogListener
import com.parkloyalty.lpr.scan.utils.AlertDialogUtils
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_BOOT_INSTANCE_TICKET
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_BOOT_SUBMIT
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_GET_CITATION_NUMBER
import com.parkloyalty.lpr.scan.utils.AppConstants.DEFAULT_VALUE_ZERO_DOT_ZERO_STR
import com.parkloyalty.lpr.scan.utils.CitationFormUtils.CITATION_FORM_FIELD_TYPE_LIST_ONLY
import com.parkloyalty.lpr.scan.utils.ObjectMapperProvider
import com.parkloyalty.lpr.scan.utils.permissions.PermissionManager
import com.parkloyalty.lpr.scan.views.MainActivityAction
import com.parkloyalty.lpr.scan.views.MainActivityViewModel
import com.parkloyalty.lpr.scan.views.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Arrays
import java.util.Calendar
import javax.inject.Inject

@AndroidEntryPoint
class BootScreenFragment : BaseFragment<FragmentBootScreenBinding>(), PrintInterface {
    private val mainActivityViewModel: MainActivityViewModel by activityViewModels()

    //Can be used later for settings specific logic
    private val bootScreenViewModel: BootScreenViewModel by viewModels()

    @Inject
    lateinit var permissionFactory: PermissionManager.Factory

    private lateinit var permissionManager: PermissionManager

    @Inject
    lateinit var sharedPreference: SharedPref

    private var mTextInputOfficerName: TextInputLayout? = null
    private var mEditTextOfficerName: TextInputEditText? = null
    private var mTextInputDateTime: TextInputLayout? = null
    private var mEditTextDateTime: TextInputEditText? = null
    private var mTextInputCitation: TextInputLayout? = null
    private var mEditTextCitation: TextInputEditText? = null
    private var mAutoComTextViewState: AppCompatAutoCompleteTextView? = null
    private var mAutoComTextViewBlock: AppCompatAutoCompleteTextView? = null
    private var mEditTextViewTextViewStreet: AppCompatAutoCompleteTextView? = null
    private var mEditTextViewTextViewSideOfState: AppCompatAutoCompleteTextView? = null
    private var mEditTextViewTextViewMake: AppCompatAutoCompleteTextView? = null
    private var mEditTextViewTextViewModel: AppCompatAutoCompleteTextView? = null
    private var mEditTextViewTextViewColor: AppCompatAutoCompleteTextView? = null
    private var mEditTextViewTextViewRemark: AppCompatAutoCompleteTextView? = null
    private var textInputLayoutLot: TextInputLayout? = null
    private var textInputLayoutSpace: TextInputLayout? = null
    private var textInputLayoutVehBlock: TextInputLayout? = null
    private var textInputLayoutVehState: TextInputLayout? = null
    private var textInputLayoutVehStreet: TextInputLayout? = null
    private var textInputLayoutVehSideOfStreet: TextInputLayout? = null
    private var textInputLayoutVehMake: TextInputLayout? = null
    private var textInputLayoutVehModel: TextInputLayout? = null
    private var textInputLayoutVehColor: TextInputLayout? = null
    private var textInputLayoutVehRemark: TextInputLayout? = null
    private var textInputLayoutNote: TextInputLayout? = null
    private var mEditTextViewTextViewNote: AppCompatAutoCompleteTextView? = null
    private var appCompatCheckBoxRegular: AppCompatCheckBox? = null
    private var appCompatCheckBoxHeavy: AppCompatCheckBox? = null
    private var appCompatCheckBoxMedium: AppCompatCheckBox? = null
    private var linearLayoutCompatCheckBox: LinearLayoutCompat? = null
    private var mTextViewLot: AppCompatAutoCompleteTextView? = null
    private var mTextViewSpace: AppCompatAutoCompleteTextView? = null
    private var btnSubmitTime: AppCompatButton? = null
    private var btnSubmit: AppCompatButton? = null
    private var btnSubmitIssue: AppCompatButton? = null
    private var viewPrintDivider: View? = null


    private val mModelList: MutableList<DatasetResponse> = ArrayList()
    private var mSelectedMake: String? = ""
    private var mSelectedModel: String? = ""
    private var mSelectedMakeValue: String? = ""
    private var mSelectedColor: String? = ""
    private var mSelectedLpNumber: String? = ""
    private val mSideItem = ""
    private var clientTime: String? = null
    private var apiResultTag = ""
    private var mStateItem = "Pennsylvania"
    private var mState2DigitCode = ""
    private var btnClickedEvent = "Submit"

    private var mWelcomeFormData: WelcomeForm? = null
    private var scofflawDataResponse: ScofflawDataResponse? = null

    //Start for field specific to Septa
    private var citationNumber: String? = null
    private var violationDate: String? = null
    private var lotNumber: String? = null
    private var spaceName: String? = null

    //End for field specific to Septa
    private var mBackgroundWhiteBitmap: Bitmap? = null

    private var mStreetItem: String? = ""
    private var mCitationNumberId: String? = ""

    val printBootNoticeModel = PrintBootNoticeModel()

    private var zebraPrinterUseCase: ZebraPrinterUseCase? = null

    private val bluetoothPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val granted = permissions.entries.all { it.value }
            if (granted) {
                // Nothing to implement here
            } else {
                BluetoothPermissionUtil.requestBluetoothSetting(requireContext())
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        permissionManager =
            permissionFactory.create(caller = this, context = requireContext(), fragment = this)
    }

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentBootScreenBinding.inflate(inflater, container, false)

    override fun findViewsByViewBinding() {
        mTextInputOfficerName = binding.inputOfficerName
        mEditTextOfficerName = binding.etOfficerName
        mTextInputDateTime = binding.inputDateTime
        mEditTextDateTime = binding.etDateTime
        mTextInputCitation = binding.inputCitation
        mEditTextCitation = binding.etCitation
        mAutoComTextViewState = binding.AutoComTextViewVehState
        mAutoComTextViewBlock = binding.AutoComTextViewVehblock
        mEditTextViewTextViewStreet = binding.AutoComTextViewVehStreet
        mEditTextViewTextViewSideOfState = binding.AutoComTextViewVehSideOfState
        mEditTextViewTextViewMake = binding.AutoComTextViewMakeVeh
        mEditTextViewTextViewModel = binding.AutoComTextViewVehModel
        mEditTextViewTextViewColor = binding.AutoComTextViewVehColor
        mEditTextViewTextViewRemark = binding.AutoComTextViewVehRemark
        textInputLayoutLot = binding.textInputLayoutLot
        textInputLayoutSpace = binding.inputTextSpace
        textInputLayoutVehBlock = binding.textInputLayoutVehBlock
        textInputLayoutVehState = binding.textInputLayoutVehState
        textInputLayoutVehStreet = binding.textInputLayoutVehStreet
        textInputLayoutVehSideOfStreet = binding.textInputLayoutVehSideOfStreet
        textInputLayoutVehMake = binding.textInputLayoutVehMake
        textInputLayoutVehModel = binding.textInputLayoutVehModel
        textInputLayoutVehColor = binding.textInputLayoutVehColor
        textInputLayoutVehRemark = binding.textInputLayoutVehRemark
        textInputLayoutNote = binding.inputTextNote
        mEditTextViewTextViewNote = binding.etNote
        appCompatCheckBoxRegular = binding.checkRegular
        appCompatCheckBoxHeavy = binding.checkHeavy
        appCompatCheckBoxMedium = binding.checkMedium
        linearLayoutCompatCheckBox = binding.llCheckbox
        mTextViewLot = binding.etLot
        mTextViewSpace = binding.etSpace
        btnSubmitTime = binding.btnSubmitTime
        btnSubmit = binding.btnSubmit
        btnSubmitIssue = binding.btnSubmitIssue
        viewPrintDivider = binding.viewPrintDivider
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
                    bootScreenViewModel.bootSubmitResponse.collect(::consumeResponse)
                }
                launch {
                    bootScreenViewModel.bootInstanceTicketResponse.collect(::consumeResponse)
                }
                launch {
                    bootScreenViewModel.citationNumberResponse.collect(::consumeResponse)
                }
            }
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    override fun initialiseData() {
        init()

        if (LogUtil.getPrinterTypeForPrint() == PrinterType.ZEBRA_PRINTER) {
            LogUtil.printLog("==>Printer", "Inside")
            zebraPrinterUseCase = ZebraPrinterUseCase()
            zebraPrinterUseCase?.setPrintInterfaceCallback(this)
            zebraPrinterUseCase?.initialize(
                activity = requireActivity(),
                contentResolver = requireContext().contentResolver,
                sharedPreference = sharedPreference
            )
        }

        BluetoothPermissionUtil.checkBluetoothPermissions(
            activity = requireActivity(), permissionLauncher = bluetoothPermissionLauncher
        ) {
            //Nothing to implement here in onCreate, requireContext() will check bluetooth permission and redirect user to allow bluetooth permission, in case misssed
        }

        setAccessibilityForComponents()
    }

    override fun setupClickListeners() {
        btnSubmit?.setOnClickListener {
            if (isFormValid()) {
                BluetoothPermissionUtil.checkBluetoothPermissions(
                    activity = requireActivity(), permissionLauncher = bluetoothPermissionLauncher
                ) {
                    btnClickedEvent = "Submit"
                    setRequest(btnClickedEvent)
                }
            }
        }

        btnSubmitIssue?.setOnClickListener {
            if (isFormValid()) {
                BluetoothPermissionUtil.checkBluetoothPermissions(
                    activity = requireActivity(), permissionLauncher = bluetoothPermissionLauncher
                ) {
                    btnClickedEvent = "Issue"
                    setRequest(btnClickedEvent)
                }
            }
        }

        btnSubmitTime?.setOnClickListener {
            if (isFormValid()) {
                BluetoothPermissionUtil.checkBluetoothPermissions(
                    activity = requireActivity(), permissionLauncher = bluetoothPermissionLauncher
                ) {
                    btnClickedEvent = "Time"
                    setRequest(btnClickedEvent)
                }
            }
        }
    }

    private fun setAccessibilityForComponents() {
        mTextInputOfficerName?.setAccessibilityForTextInputLayoutWithHintOnly()
        mTextInputDateTime?.setAccessibilityForTextInputLayoutWithHintOnly()
        mTextInputCitation?.setAccessibilityForTextInputLayoutWithHintOnly()

        setAccessibilityForTextInputLayoutDropdownButtons(
            context = requireContext(), textInputLayout = textInputLayoutLot
        )

        setAccessibilityForTextInputLayoutDropdownButtons(
            context = requireContext(), textInputLayout = textInputLayoutSpace
        )

        setAccessibilityForTextInputLayoutDropdownButtons(
            context = requireContext(), textInputLayout = textInputLayoutVehBlock
        )

        setAccessibilityForTextInputLayoutDropdownButtons(
            context = requireContext(), textInputLayout = textInputLayoutVehState
        )

        setAccessibilityForTextInputLayoutDropdownButtons(
            context = requireContext(), textInputLayout = textInputLayoutVehStreet
        )

        setAccessibilityForTextInputLayoutDropdownButtons(
            context = requireContext(), textInputLayout = textInputLayoutVehSideOfStreet
        )

        setAccessibilityForTextInputLayoutDropdownButtons(
            context = requireContext(), textInputLayout = textInputLayoutVehMake
        )

        setAccessibilityForTextInputLayoutDropdownButtons(
            context = requireContext(), textInputLayout = textInputLayoutVehModel
        )

        setAccessibilityForTextInputLayoutDropdownButtons(
            context = requireContext(), textInputLayout = textInputLayoutVehColor
        )

        setDoNothingAccessibilityForTextInputLayoutDropdownButtons(
            context = requireContext(), textInputLayout = textInputLayoutVehRemark
        )
    }


    private fun init() {
        viewLifecycleOwner.lifecycleScope.launch {
            mWelcomeFormData = bootScreenViewModel.getWelcomeForm()

            textInputLayoutNote?.setBoxStrokeColorStateList(requireContext().getBoxStrokeColor(R.color.light_yellow_30))
            textInputLayoutNote?.setupTextInputLayout(
                hintText = getString(R.string.scr_lbl_comments),
                placeholder = getString(
                    R.string.hint_start_typing_your_value, getString(R.string.scr_lbl_comments)
                ),
                endIconModeType = null,
            )

            getBundleData()
            setLayoutVisibilityBasedOnSettingResponse()
            setLayoutVisibilityBasedOnSite()


            if (BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_DUNCAN, true
                ) || BuildConfig.FLAVOR.equals(DuncanBrandingApp13())
            ) {
                linearLayoutCompatCheckBox?.visibility = View.GONE
            } else if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SEPTA, true)) {
                val drawableBitmap = BitmapFactory.decodeResource(resources, R.drawable.white_print)
                mBackgroundWhiteBitmap = drawableBitmap.copy(Bitmap.Config.ARGB_8888, true)
                Canvas(mBackgroundWhiteBitmap!!)
            }

            setCrossClearButtonForAllFields()

            if (BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_SEPTA, true
                )
            ) {
                getLatestCitationNumber()
            }
        }
    }

    private fun setCrossClearButtonForAllFields() {
        setCrossClearButton(
            context = requireContext(),
            textInputLayout = mTextInputCitation,
            textInputEditText = mEditTextCitation
        )

        setCrossClearButton(
            context = requireContext(),
            textInputLayout = textInputLayoutLot,
            appCompatAutoCompleteTextView = mTextViewLot
        )

        setCrossClearButton(
            context = requireContext(),
            textInputLayout = textInputLayoutSpace,
            appCompatAutoCompleteTextView = mTextViewSpace
        )

        setCrossClearButton(
            context = requireContext(),
            textInputLayout = textInputLayoutVehBlock,
            appCompatAutoCompleteTextView = mAutoComTextViewBlock
        )

        setCrossClearButton(
            context = requireContext(),
            textInputLayout = textInputLayoutVehState,
            appCompatAutoCompleteTextView = mAutoComTextViewState
        )

        setCrossClearButton(
            context = requireContext(),
            textInputLayout = textInputLayoutVehStreet,
            appCompatAutoCompleteTextView = mEditTextViewTextViewStreet
        )

        setCrossClearButton(
            context = requireContext(),
            textInputLayout = textInputLayoutVehSideOfStreet,
            appCompatAutoCompleteTextView = mEditTextViewTextViewSideOfState
        )

        setCrossClearButton(
            context = requireContext(),
            textInputLayout = textInputLayoutVehMake,
            appCompatAutoCompleteTextView = mEditTextViewTextViewMake
        )

        setCrossClearButton(
            context = requireContext(),
            textInputLayout = textInputLayoutVehModel,
            appCompatAutoCompleteTextView = mEditTextViewTextViewModel
        )

        setCrossClearButton(
            context = requireContext(),
            textInputLayout = textInputLayoutVehColor,
            appCompatAutoCompleteTextView = mEditTextViewTextViewColor
        )

        setCrossClearButton(
            context = requireContext(),
            textInputLayout = textInputLayoutVehRemark,
            appCompatAutoCompleteTextView = mEditTextViewTextViewRemark
        )

        setCrossClearButton(
            context = requireContext(),
            textInputLayout = textInputLayoutNote,
            appCompatAutoCompleteTextView = mEditTextViewTextViewNote
        )
    }

    /**
     * As we need fresh citation number for notice print & ticket creation from here, we are using below function and its associates
     */
    private fun getLatestCitationNumber() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val fetched = withContext(Dispatchers.IO) {
                    bootScreenViewModel.getCitationBooklet(0)
                } ?: emptyList()

                mCitationNumberId = fetched.firstOrNull()?.citationBooklet

                if (fetched.size < 3) {
                    callCitationNumberApi()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    private suspend fun callCitationNumberApi() {
        if (requireActivity().isInternetAvailable()) {
            val mCitationNumberRequest = CitationNumberRequest()
            val uniqueID = AppUtils.getDeviceId(requireContext())

            val welcomeForm: WelcomeForm? = withContext(Dispatchers.IO) {
                bootScreenViewModel.getWelcomeForm()
            }

            if (welcomeForm != null && !welcomeForm.officerDeviceName.isNullOrEmpty()) {
                mCitationNumberRequest.deviceId = uniqueID + "-" + welcomeForm.officerDeviceName
            } else {
                mCitationNumberRequest.deviceId = "$uniqueID-Device"
            }
            bootScreenViewModel.callGetCitationNumberAPI(mCitationNumberRequest)
        } else {
            requireContext().toast(getString(R.string.err_msg_connection_was_refused))
        }
    }

    private fun getBundleData() {
        try {
            mSelectedMake = arguments?.getString(INTENT_KEY_MAKE)
            mSelectedModel = arguments?.getString(INTENT_KEY_MODEL)
            mSelectedColor = arguments?.getString(INTENT_KEY_COLOR)
            mSelectedLpNumber = arguments?.getString(INTENT_KEY_LPNUMBER)
            scofflawDataResponse = arguments?.getParcelable(INTENT_KEY_SCCOFFLAW)
            mStateItem = if (scofflawDataResponse != null) scofflawDataResponse!!.state!! else ""

            //We only have to get citation number & violation date when we are in SEPTA
            if (BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_SEPTA, true
                )
            ) {
                citationNumber = arguments?.getString(INTENT_KEY_CITATION_NUMBER).nullSafety()
                violationDate = arguments?.getString(INTENT_KEY_VIOLATION_DATE).nullSafety()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setDropDowns() {
        try {
            setDropdownMakeVehicle(mSelectedMake)
            setDropdownVehicleModel(mSelectedModel)
            setDropdownVehicleColour(mSelectedColor)


            viewLifecycleOwner.lifecycleScope.launch {
                val remarkList = withContext(Dispatchers.IO) {
                    mainActivityViewModel.getRemarkListFromDataSet()
                } ?: return@launch

                setDropdownRemark(remarkList)
            }

            viewLifecycleOwner.lifecycleScope.launch {
                val sideList = withContext(Dispatchers.IO) {
                    mainActivityViewModel.getSideListFromDataSet()
                } ?: return@launch

                if (sideList.isNotEmpty()) {
                    setDropdownSide(sideList)
                } else {
                    val mApplicationList: MutableList<DatasetResponse> = ArrayList()
                    mApplicationList.add(DatasetResponse.setSideName("E", 0))
                    mApplicationList.add(DatasetResponse.setSideName("N", 0))
                    mApplicationList.add(DatasetResponse.setSideName("S", 0))
                    mApplicationList.add(DatasetResponse.setSideName("W", 0))
                    setDropdownSide(mApplicationList)
                }
            }

            viewLifecycleOwner.lifecycleScope.launch {
                val streetList = withContext(Dispatchers.IO) {
                    mainActivityViewModel.getStreetListFromDataSet()
                } ?: return@launch

                setDropdownStreet(streetList)
            }

            viewLifecycleOwner.lifecycleScope.launch {
                val stateList = withContext(Dispatchers.IO) {
                    mainActivityViewModel.getStateListFromDataSet()
                } ?: return@launch

                setDropdownState(stateList)
            }

            viewLifecycleOwner.lifecycleScope.launch {
                val blockList = withContext(Dispatchers.IO) {
                    mainActivityViewModel.getBlockListFromDataSet()
                } ?: return@launch

                setDropdownBlock(blockList)
            }

            getGeoAddress()
            mEditTextCitation?.setText(mSelectedLpNumber)
            mEditTextOfficerName?.setText(
                mWelcomeFormData?.officerFirstName + " " + mWelcomeFormData?.officerLastName
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getGeoAddress() {
        mEditTextDateTime?.setText(AppUtils.getCurrentDateTimeforBoot("UI"))
        clientTime = AppUtils.getCurrentDateTimeforBoot("Normal").trim().replace(" ", "T")

        viewLifecycleOwner.lifecycleScope.launch {
            val mLat = sharedPreference.read(SharedPrefKey.LAT, "0.0")
                .nullSafety(defaultValue = DEFAULT_VALUE_ZERO_DOT_ZERO_STR).toDouble()
            val mLong = sharedPreference.read(SharedPrefKey.LONG, "0.0")
                .nullSafety(defaultValue = DEFAULT_VALUE_ZERO_DOT_ZERO_STR).toDouble()

            val ctx = requireContext()
            val addressLine = try {
                withContext(Dispatchers.IO) { getAddressFromLatLng(ctx, mLat, mLong) }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            } ?: return@launch

            val printAddress = addressLine.substringBefore(",").trim()
            val tokens = printAddress.split(Regex("\\s+")).filter { it.isNotEmpty() }

            val block = tokens.firstOrNull() ?: printAddress
            val street = if (tokens.size > 1) tokens.drop(1).joinToString(" ") else printAddress

            mAutoComTextViewBlock?.setText(block)
            mEditTextViewTextViewStreet?.setText(street)
        }
    }

    //set value to State dropdown
    private fun setDropdownState(mApplicationList: List<DatasetResponse>?) {
        val states =
            mApplicationList?.mapNotNull { it.state_name?.nullSafety() }?.distinct()?.sorted()
                ?: emptyList()

        if (states.isEmpty()) {
            mAutoComTextViewState?.apply {
                setText("")
                setAdapter(null)
            }
            return
        }

        val selectedEntry = mApplicationList?.firstOrNull {
            it.state_name.equals(mStateItem, ignoreCase = true) || it.state_abbreviated.equals(
                mStateItem,
                ignoreCase = true
            )
        }
        val initialName = selectedEntry?.state_name.nullSafety().ifEmpty { states.first() }
        if (selectedEntry != null) mState2DigitCode = selectedEntry.state_abbreviated.nullSafety()

        val adapter = ArrayAdapter(requireContext(), R.layout.row_dropdown_menu_popup_item, states)

        mAutoComTextViewState?.apply {
            threshold = 1
            setAdapter(adapter)
            setText(initialName)
            onItemClickListener = OnItemClickListener { parent, _, position, _ ->
                requireActivity().hideSoftKeyboard()
                val selectedName = parent.getItemAtPosition(position).toString()
                val index = mApplicationList?.getIndexOfStateName(selectedName) ?: -1
                mState2DigitCode =
                    if (index >= 0) mApplicationList?.getOrNull(index)?.state_abbreviated.nullSafety() else ""
            }

            if (tag != null && tag == CITATION_FORM_FIELD_TYPE_LIST_ONLY) {
                setListOnlyDropDown(
                    context = requireContext(),
                    textInputLayout = textInputLayoutVehState
                )
            }
        }
    }

    //set value to Street dropdown
    private fun setDropdownStreet(mApplicationList: List<DatasetResponse>?) {
        val streets =
            mApplicationList?.mapNotNull { it.street_name?.nullSafety() }?.distinct()?.sorted()
                ?: emptyList()

        if (streets.isEmpty()) {
            mEditTextViewTextViewStreet?.apply {
                setText("")
                setAdapter(null)
            }
            return
        }

        val initialPos =
            streets.indexOfFirst { it.equals(mStreetItem, ignoreCase = true) }.takeIf { it >= 0 }
                ?: 0

        val adapter = ArrayAdapter(requireContext(), R.layout.row_dropdown_menu_popup_item, streets)

        mEditTextViewTextViewStreet?.apply {
            threshold = 1
            setAdapter(adapter)
            setText(streets[initialPos])
            onItemClickListener = OnItemClickListener { _, _, _, _ ->
                requireActivity().hideSoftKeyboard()
            }

            // If this field should be list-only (matching other dropdowns in the file)
            if (tag != null && tag == CITATION_FORM_FIELD_TYPE_LIST_ONLY) {
                setListOnlyDropDown(
                    context = requireContext(),
                    textInputLayout = textInputLayoutVehStreet
                )
            }
        }
    }


    //set value to vehicle model dropdown
    private fun setDropdownVehicleModel(value: String?) {
        viewLifecycleOwner.lifecycleScope.launch {
            val allMakes = withContext(Dispatchers.IO) {
                mainActivityViewModel.getCarMakeListFromDataSet()
            } ?: return@launch

            // Build mModelList from the selected make
            mModelList.clear()
            mModelList.addAll(allMakes.asSequence().filter { it.make == mSelectedMake }.map {
                    DatasetResponse().apply {
                        model = it.model
                        make = it.make
                        makeText = it.makeText
                    }
                }.toList())

            // Prepare display list: distinct, non-null, sorted model strings
            val models = mModelList.mapNotNull { it.model?.nullSafety() }.distinct().sorted()

            // If no models, clear field and adapter
            if (models.isEmpty()) {
                mEditTextViewTextViewModel?.apply {
                    setText("")
                    setAdapter(null)
                }
                return@launch
            }

            // Determine initial selection
            val initialPos =
                models.indexOfFirst { it.equals(value, ignoreCase = true) }.takeIf { it >= 0 } ?: 0
            mSelectedModel = models.getOrNull(initialPos)

            // Set adapter and listener on main thread
            mEditTextViewTextViewModel?.apply {
                threshold = 1
                val adapter =
                    ArrayAdapter(requireContext(), R.layout.row_dropdown_lpr_details_item, models)
                setAdapter(adapter)
                setText(models[initialPos])
                onItemClickListener = OnItemClickListener { parent, _, position, _ ->
                    mSelectedModel = parent.getItemAtPosition(position)?.toString()
                    requireActivity().hideSoftKeyboard()
                }
            }
        }
    }

    //set value to make vehicle dropdown
    private fun setDropdownMakeVehicle(value: String?) {
        viewLifecycleOwner.lifecycleScope.launch {
            val mApplicationList = withContext(Dispatchers.IO) {
                mainActivityViewModel.getCarMakeListFromDataSet()
            } ?: return@launch

            val pairs = mApplicationList.mapNotNull { dataset ->
                    val make = dataset.make
                    if (make.isNullOrBlank()) null else make to dataset.makeText.nullSafety()
                }.distinctBy { it.first }.sortedBy { it.second }

            if (pairs.isEmpty()) {
                mEditTextViewTextViewMake?.apply {
                    setText("")
                    setAdapter(null)
                }
                return@launch
            }

            val displayList = pairs.map { it.second }
            val adapter =
                ArrayAdapter(requireContext(), R.layout.row_dropdown_lpr_details_item, displayList)

            mEditTextViewTextViewMake?.apply {
                threshold = 1
                setAdapter(adapter)

                val initialPos = if (!value.isNullOrBlank()) {
                    pairs.indexOfFirst {
                        it.first.equals(value, true) || it.second.equals(
                            value,
                            true
                        )
                    }.takeIf { it >= 0 } ?: 0
                } else 0

                setText(displayList[initialPos])
                mSelectedMake = pairs[initialPos].first
                mSelectedMakeValue = pairs[initialPos].second

                onItemClickListener = OnItemClickListener { parent, _, position, _ ->
                    val selectedText = parent.getItemAtPosition(position).toString()
                    val index = mApplicationList.getIndexOfMakeText(selectedText)
                    if (index >= 0) {
                        mSelectedMake = mApplicationList[index].make
                        mSelectedMakeValue = mApplicationList[index].makeText
                    } else {
                        val p = pairs[position]
                        mSelectedMake = p.first
                        mSelectedMakeValue = p.second
                    }
                    setDropdownVehicleModel(mSelectedMake)
                    requireActivity().hideSoftKeyboard()
                }
            }
        }
    }

    //set value to vehicle colour dropdown
    private fun setDropdownVehicleColour(value: String?) {
        viewLifecycleOwner.lifecycleScope.launch {
            val mApplicationList = withContext(Dispatchers.IO) {
                mainActivityViewModel.getCarColorListFromDataSet()
            } ?: return@launch

            //init array list
            var pos = 0
            if (mApplicationList.isNotEmpty()) {
                val mDropdownList = arrayOfNulls<String>(mApplicationList.size)
                for (i in mApplicationList.indices) {
                    mDropdownList[i] = mApplicationList[i].description.toString()
                    if (value != "") {
                        if (mDropdownList[i] == value) {
                            pos = i
                            try {
                                mEditTextViewTextViewColor?.setText(mDropdownList[pos])
                            } catch (e: Exception) {
                            }
                        }
                    }
                }
                Arrays.sort(mDropdownList)
                val adapter = ArrayAdapter(
                    requireContext(), R.layout.row_dropdown_lpr_details_item, mDropdownList
                )
                try {
                    mEditTextViewTextViewColor?.threshold = 1
                    mEditTextViewTextViewColor?.setAdapter<ArrayAdapter<String?>>(adapter)
                    mSelectedColor = mDropdownList[pos]
                    mEditTextViewTextViewColor?.onItemClickListener =
                        OnItemClickListener { parent, view, position, id ->
                            mSelectedColor = mDropdownList[position]
                            requireActivity().hideSoftKeyboard()
                        }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    //set value to Body Style dropdown
    private fun setDropdownRemark(mApplicationList: List<DatasetResponse>?) {
        val remarks =
            mApplicationList?.map { it.remark.nullSafety() }?.sorted()?.takeIf { it.isNotEmpty() }
                ?: return

        val adapter = ArrayAdapter(requireContext(), R.layout.row_dropdown_menu_popup_item, remarks)

        try {
            mEditTextViewTextViewRemark?.apply {
                threshold = 1
                setAdapter(adapter)
                onItemClickListener = OnItemClickListener { _, _, _, _ ->
                    requireActivity().hideSoftKeyboard()
                    error = null
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    //set value to side dropdown
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
                requireContext(), R.layout.row_dropdown_menu_popup_item, mDropdownList
            )
            try {
                mEditTextViewTextViewSideOfState?.threshold = 1
                mEditTextViewTextViewSideOfState?.setAdapter<ArrayAdapter<String?>>(adapter)
                mEditTextViewTextViewSideOfState?.onItemClickListener =
                    OnItemClickListener { parent, view, position, id -> // mSelectedShiftStat = mApplicationList.get(position);
                        requireActivity().hideSoftKeyboard()
                        mEditTextViewTextViewSideOfState?.error = null
                    }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    //set value to Meter Name dropdown
    private fun setDropdownBlock(mApplicationList: List<DatasetResponse>?) {
        if (mApplicationList != null && mApplicationList.isNotEmpty()) {
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
                    }

                if (mAutoComTextViewBlock?.tag != null && mAutoComTextViewBlock?.tag == CITATION_FORM_FIELD_TYPE_LIST_ONLY) {
                    mAutoComTextViewBlock?.setListOnlyDropDown(
                        context = requireContext(), textInputLayout = textInputLayoutVehBlock
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun setLayoutVisibilityBasedOnSettingResponse() {
        viewLifecycleOwner.lifecycleScope.launch {
            val settingsList = withContext(Dispatchers.IO) {
                mainActivityViewModel.getSettingsListFromDataSet()
            } ?: return@launch

            try {
                if (settingsList.isNotEmpty()) {
                    for (i in settingsList.indices) {
                        if (settingsList[i].type.equals(
                                "HAS_MODEL", ignoreCase = true
                            ) && settingsList[i].mValue.equals("NO", ignoreCase = true)
                        ) {
                            binding.viewSeparatorForMakeModel.hideView()
                            textInputLayoutVehModel?.hideView()
                        }
                        if (settingsList[i].type.equals(
                                "DEFAULT_STATE", ignoreCase = true
                            ) && mStateItem.isEmpty()
                        ) {
                            mStateItem = settingsList[i].mValue!!
                        }
                    }
                }
                setDropDowns()
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
                setDropDowns()
            }
        }
    }

    //requireContext() function is used to show hide some view based on sites
    private fun setLayoutVisibilityBasedOnSite() {
        if (BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_SEPTA, true
            )
        ) {
            textInputLayoutLot?.showView()

            setDropdownForLotNumber()

            textInputLayoutSpace?.hideView()
            //setDropdownForSpace()

            textInputLayoutVehSideOfStreet?.hideView()
            linearLayoutCompatCheckBox?.hideView()
            //Need to hide these two buttons
            btnSubmitTime?.hideView()
            viewPrintDivider?.hideView()
            btnSubmit?.hideView()

            //Text Changes Needed
            btnSubmit?.setText(R.string.scr_btn_print)
            btnSubmitIssue?.setText(R.string.scr_btn_print_and_cite)
        } else if (BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_DUNCAN, true
            ) || BuildConfig.FLAVOR.equals(DuncanBrandingApp13())
        ) {
            linearLayoutCompatCheckBox?.visibility = View.GONE
        }
    }


    /**
     * Function used to set dropdown for lot number
     */
    private fun setDropdownForLotNumber() {
        if (mTextViewLot != null) {
            viewLifecycleOwner.lifecycleScope.launch {
                val mApplicationList = withContext(Dispatchers.IO) {
                    mainActivityViewModel.getLotListFromDataSet()
                } ?: return@launch
                if (mApplicationList.isNotEmpty()) {
                    val mDropdownList = arrayOfNulls<String>(mApplicationList.size)
                    for (i in mApplicationList.indices) {
                        mDropdownList[i] = mApplicationList[i].lot.toString()
                    }

                    mTextViewLot?.post {
                        val adapter = ArrayAdapter(
                            requireContext(), R.layout.row_dropdown_menu_popup_item, mDropdownList
                        )
                        try {
                            mTextViewLot?.threshold = 1
                            mTextViewLot?.setAdapter<ArrayAdapter<String?>>(adapter)
                            mTextViewLot?.onItemClickListener =
                                OnItemClickListener { parent, view, position, id -> // mSelectedShiftStat = mApplicationList.get(position);
                                    requireActivity().hideSoftKeyboard()

                                    mTextViewSpace?.error = null

                                    val indexOfLot = getIndexOfLot(
                                        mApplicationList,
                                        parent.getItemAtPosition(position).toString()
                                    )
                                    lotNumber = mApplicationList[indexOfLot].lot.toString()

                                    val index = mApplicationList.getIndexOfLocation(
                                        parent.getItemAtPosition(position).toString()
                                    )

                                    try {
                                        mAutoComTextViewBlock?.setText(mApplicationList[index].block.nullSafety())
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                    val currentString = mApplicationList[index].street.toString()
                                    val separated = currentString.split(" ").toTypedArray()
                                    mStreetItem =
                                        mApplicationList[index].street.toString()//separated[0]
                                    //mStreetItem = String.valueOf(mApplicationList.get(position).getStreet());
                                    viewLifecycleOwner.lifecycleScope.launch {
                                        val streetList = withContext(Dispatchers.IO) {
                                            mainActivityViewModel.getStreetListFromDataSet()
                                        } ?: return@launch

                                        setDropdownStreet(streetList)
                                    }

//                                    mDirectionItem = mApplicationList[index].direction.toString()
//                                    try {
//                                        mAutoComTextViewDirection?.setText(mApplicationList[index].direction.toString())
//                                    } catch (e: Exception) {
//                                        e.printStackTrace()
//                                    }
//                                    setDropdownDirection()
//                                    setDropdownZone(mAutoComTextViewLot?.text.toString())
//                                    try {
//                                        mViolationExtraAmount = mApplicationList[index].violation!!.toInt()
//                                        if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CARTA, ignoreCase = true)
//                                            && mViolationListSelectedItem!=null) {
//                                            setViolationBaseData(mViolationListSelectedItem, pos)
//                                        }
//                                    } catch (e: Exception) {
//                                        e.printStackTrace()
//                                    }


                                }
                            if (mTextViewLot?.tag != null && mTextViewLot?.tag == CITATION_FORM_FIELD_TYPE_LIST_ONLY) {
                                mTextViewLot?.setListOnlyDropDown(
                                    context = requireContext(), textInputLayout = textInputLayoutLot
                                )
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
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


    /**
     * Function used to set dropdown for space number
     */
    private fun setDropdownForSpace() {
        if (mTextViewSpace != null) {
            viewLifecycleOwner.lifecycleScope.launch {
                val mApplicationList = withContext(Dispatchers.IO) {
                    mainActivityViewModel.getSpaceListFromDataSet()
                } ?: return@launch

                if (mApplicationList.isNotEmpty()) {
                    val mDropdownList = arrayOfNulls<String>(mApplicationList.size)
                    for (i in mApplicationList.indices) {
                        mDropdownList[i] = mApplicationList[i].spaceName.toString()
                    }
                    mTextViewSpace?.post {

                        val adapter = ArrayAdapter(
                            requireContext(), R.layout.row_dropdown_menu_popup_item, mDropdownList
                        )
                        try {
                            mTextViewSpace?.threshold = 1
                            mTextViewSpace?.setAdapter<ArrayAdapter<String?>>(adapter)
                            mTextViewSpace?.onItemClickListener =
                                OnItemClickListener { parent, view, position, id ->

                                    requireActivity().hideSoftKeyboard()

                                    val index = getIndexOfSpaceName(
                                        mApplicationList,
                                        parent.getItemAtPosition(position).toString()
                                    )
                                    spaceName = mApplicationList[index].spaceName.toString()
                                }
                            if (mTextViewSpace?.tag != null && mTextViewSpace?.tag == CITATION_FORM_FIELD_TYPE_LIST_ONLY) {
                                mTextViewSpace?.setListOnlyDropDown(
                                    context = requireContext(),
                                    textInputLayout = textInputLayoutSpace
                                )
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }
    }

    private fun printFinalNotice(printBootNoticeModel: PrintBootNoticeModel) {
        val (printerCommand, printHeight) = ZebraCommandPrintUtils.getPrintCommandForBootTow(
            context = requireContext(),
            printBootNoticeModel = printBootNoticeModel,
            citationNumber = mCitationNumberId,
            violationDate = SDF_MM_DD_YYYY.format(Calendar.getInstance().time),
            lotName = lotNumber,
            spaceNumber = spaceName
        )

        zebraPrinterUseCase?.printFinalTowNoticeWithWhiteBackgroundBitmap(
            mBackgroundWhiteBitmap, printerCommand, printHeight
        )

    }

    private fun isFormValid(): Boolean {
        if (TextUtils.isEmpty(
                mTextViewLot?.text.toString().trim()
            ) && textInputLayoutLot?.isVisible.nullSafety()
        ) {
            textInputLayoutLot?.showErrorWithShake(getString(R.string.val_msg_please_enter_lot))
            return false
        }

        if (TextUtils.isEmpty(
                mTextViewSpace?.text.toString().trim()
            ) && textInputLayoutSpace?.isVisible.nullSafety()
        ) {
            textInputLayoutSpace?.showErrorWithShake(getString(R.string.val_msg_please_enter_space))
            return false
        }

        if (TextUtils.isEmpty(mAutoComTextViewState?.text.toString().trim())) {
            textInputLayoutVehState?.showErrorWithShake(getString(R.string.val_msg_please_enter_state))
            return false
        }
        if (TextUtils.isEmpty(
                mEditTextViewTextViewSideOfState?.text.toString().trim()
            ) && textInputLayoutVehSideOfStreet?.isVisible.nullSafety()
        ) {
            textInputLayoutVehSideOfStreet?.showErrorWithShake(getString(R.string.err_lbl_side_of_street))
            return false
        }
        if (TextUtils.isEmpty(
                mEditTextViewTextViewStreet?.text.toString().trim()
            )
        ) {
            textInputLayoutVehStreet?.showErrorWithShake(getString(R.string.val_msg_please_enter_street))
            return false
        }
        if (!appCompatCheckBoxHeavy?.isChecked.nullSafety() && !appCompatCheckBoxRegular?.isChecked.nullSafety() && !appCompatCheckBoxMedium?.isChecked.nullSafety() && !BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_DUNCAN, true
            ) && !BuildConfig.FLAVOR.equals(
                DuncanBrandingApp13()
            ) && linearLayoutCompatCheckBox?.isVisible.nullSafety()
        ) {
            requireContext().toast(
                getString(R.string.err_lbl_select_any_one)
            )
            return false
        }
        return true
    }

    private fun setRequest(clickEvent: String) {
        if (requireContext().isInternetAvailable()) {
            val mLat = sharedPreference.read(SharedPrefKey.LAT, "0.0")?.toDouble().nullSafety()
            val mLong = sharedPreference.read(SharedPrefKey.LONG, "0.0")?.toDouble().nullSafety()
            val bootRequest = BootRequest()
            bootRequest.dispatchType = "boot"
            bootRequest.isOnGroundDispatch = false
            bootRequest.citationNumber = ""
            bootRequest.clientTimestamp = clientTime
            bootRequest.bootTowReason = "tow away zone"
            bootRequest.remarks = mEditTextViewTextViewRemark?.text.toString()
            if (appCompatCheckBoxRegular?.isChecked.nullSafety()) {
                bootRequest.bootTowType = "Regular"
            } else if (appCompatCheckBoxMedium?.isChecked.nullSafety()) {
                bootRequest.bootTowType = "Medium"
            } else if (appCompatCheckBoxHeavy?.isChecked.nullSafety()) {
                bootRequest.bootTowType = "Heavy"
            } else {
                bootRequest.bootTowType = "Regular"
            }
            val officerDetails = OfficerDetails()
            officerDetails.officerName =
                mWelcomeFormData?.officerFirstName + " " + mWelcomeFormData?.officerLastName
            officerDetails.siteOfficerId = mWelcomeFormData?.siteOfficerId
            officerDetails.badgeId = mWelcomeFormData?.officerBadgeId
            officerDetails.beat = mWelcomeFormData?.officerBeat
            officerDetails.shift = sharedPreference.read(SharedPrefKey.LOGIN_SHIFT, "")
            officerDetails.squad = mWelcomeFormData?.officerSquad
            bootRequest.officerDetails = officerDetails

            val vehicleDetails = VehicleDetails()
            vehicleDetails.color = mEditTextViewTextViewColor?.text.toString()
            vehicleDetails.lpNumber = mEditTextCitation?.text.toString()
            vehicleDetails.make = mEditTextViewTextViewMake?.text.toString()
            vehicleDetails.model = mEditTextViewTextViewModel?.text.toString()
            //vehicleDetails.state = if (scofflawDataResponse != null) scofflawDataResponse?.state else ""
            //vehicleDetails.state = mStateItem
            vehicleDetails.state = mState2DigitCode
            bootRequest.vehicleDetails = vehicleDetails

            val violationDetails = ViolationDetails()
            violationDetails.violationCode = ""
            violationDetails.violationFine = 0.0
            violationDetails.violationDescription = ""
            bootRequest.violationDetails = violationDetails

            val locationDetails = LocationDetails()
            locationDetails.block = mAutoComTextViewBlock?.text.toString()
            locationDetails.latitude = mLat
            locationDetails.longitude = mLong
            locationDetails.side = mEditTextViewTextViewSideOfState?.text.toString()
            locationDetails.street = mEditTextViewTextViewStreet?.text.toString()
            bootRequest.locationDetails = locationDetails

            if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SEPTA, ignoreCase = true)) {
                //Setting Print Layout Data for Notice print
                printBootNoticeModel.officerDetails = officerDetails
                printBootNoticeModel.vehicleDetails = vehicleDetails
                printBootNoticeModel.remarks = bootRequest.remarks

                //Setting up boot metadata request
                val bootMetadataRequest = BootMetadataRequest()
                val bootInstanceTicketRequest = BootInstanceTicketRequest()
                bootInstanceTicketRequest.bootType = API_CONSTANT_BOOT_TYPE_HANDHELD_INITIATED
                bootInstanceTicketRequest.licensePlateNumber = mEditTextCitation?.text.toString()
                bootInstanceTicketRequest.plateType = API_CONSTANT_PLATE_TYPE_PERSONAL
                bootInstanceTicketRequest.licensePlateState = mState2DigitCode
                bootInstanceTicketRequest.notes = mEditTextViewTextViewRemark?.text.toString()
                bootInstanceTicketRequest.ticketNo = mCitationNumberId
                bootMetadataRequest.bootMetadata = bootInstanceTicketRequest
                bootScreenViewModel.callBootInstanceTicketAPI(bootMetadataRequest)
            } else {
                bootScreenViewModel.callBootSubmitAPI(bootRequest)
            }
        } else {
            requireContext().toast(
                getString(R.string.err_msg_connection_was_refused)
            )
        }
    }

    fun handleDialogButtonClick() {
        if (apiResultTag.equals("success", ignoreCase = true)) {
            if (btnClickedEvent.equals("Submit", ignoreCase = true)) {
                viewLifecycleOwner.lifecycleScope.launch {
                    mainActivityViewModel.backButtonPressed()
                }
            } else {
                val bundle = Bundle()
                if (btnClickedEvent.equals("Issue", ignoreCase = true)) {
                    sharedPreference.writeOverTimeParkingTicketDetails(
                        SharedPrefKey.TIMING_DATA_OVERTIME_PARKING_CHARLESTION, AddTimingRequest()!!
                    )

                    bundle.putString(
                        "make", mEditTextViewTextViewMake?.editableText.toString().trim()
                    )
                    bundle.putString("from_scr", "BOOTACTIVITY")
                    if (mSelectedModel != null) {
                        bundle.putString(
                            "model", mEditTextViewTextViewModel?.editableText.toString().trim()
                        )
                    }
                    bundle.putString(
                        "color", mEditTextViewTextViewColor?.editableText.toString().trim()
                    )
                    bundle.putString(
                        "lpr_number", mEditTextCitation?.text.toString().trim()
                    )
                    bundle.putString(
                        "Street", mEditTextViewTextViewStreet?.text.toString().trim()
                    )
                    bundle.putString("SideItem", mEditTextViewTextViewSideOfState?.text.toString())
                    bundle.putString("Block", mAutoComTextViewBlock?.text.toString())
                    bundle.putString("State", mAutoComTextViewState?.text.toString())
                    bundle.putString(
                        "address",
                        mAutoComTextViewBlock?.text.toString() + "#" + mEditTextViewTextViewStreet?.text.toString()
                            .trim()
                    )
                    if (BuildConfig.FLAVOR.equals(
                            Constants.FLAVOR_TYPE_GLENDALE, ignoreCase = true
                        ) || BuildConfig.FLAVOR.equals(
                            Constants.FLAVOR_TYPE_GLENDALE_POLICE, ignoreCase = true
                        ) || BuildConfig.FLAVOR.equals(
                            Constants.FLAVOR_TYPE_LAMETRO, ignoreCase = true
                        ) || BuildConfig.FLAVOR.equals(
                            Constants.FLAVOR_TYPE_CORPUSCHRISTI, ignoreCase = true
                        ) || BuildConfig.FLAVOR.equals(
                            Constants.FLAVOR_TYPE_BURBANK, ignoreCase = true
                        ) || BuildConfig.FLAVOR.equals(
                            Constants.FLAVOR_TYPE_WESTCHESTER, ignoreCase = true
                        ) || BuildConfig.FLAVOR.equals(
                            Constants.FLAVOR_TYPE_LAZLB, ignoreCase = true
                        ) || BuildConfig.FLAVOR.equals(
                            Constants.FLAVOR_TYPE_VALLEJOL, ignoreCase = true
                        ) || BuildConfig.FLAVOR.equals(
                            Constants.FLAVOR_TYPE_ENCINITAS, ignoreCase = true
                        ) || BuildConfig.FLAVOR.equals(
                            Constants.FLAVOR_TYPE_SANDIEGO, ignoreCase = true
                        ) || BuildConfig.FLAVOR.equals(
                            Constants.FLAVOR_TYPE_MILLBRAE, ignoreCase = true
                        ) || BuildConfig.FLAVOR.equals(
                            Constants.FLAVOR_TYPE_SANMATEO_REDWOOD, ignoreCase = true
                        ) || BuildConfig.FLAVOR.equals(
                            Constants.FLAVOR_TYPE_CITYOFSANDIEGO, ignoreCase = true
                        ) || BuildConfig.FLAVOR.equals(
                            Constants.FLAVOR_TYPE_STORMWATER_DIVISION, ignoreCase = true
                        ) || BuildConfig.FLAVOR.equals(
                            Constants.FLAVOR_TYPE_CHULAVISTA, ignoreCase = true
                        ) || BuildConfig.FLAVOR.equals(
                            Constants.FLAVOR_TYPE_ROSEBURG, ignoreCase = true
                        ) || BuildConfig.FLAVOR.equals(
                            Constants.FLAVOR_TYPE_COHASSET, ignoreCase = true
                        ) || BuildConfig.FLAVOR.equals(
                            Constants.FLAVOR_TYPE_MARTIN, ignoreCase = true
                        ) || BuildConfig.FLAVOR.equals(
                            Constants.FLAVOR_TYPE_NORTHERN_CALIFORNIA, ignoreCase = true
                        ) || BuildConfig.FLAVOR.equals(
                            Constants.FLAVOR_TYPE_ACE_SAN_DIEGO_ZOO, ignoreCase = true
                        ) || BuildConfig.FLAVOR.equals(
                            Constants.FLAVOR_TYPE_SANFRANCISCO, ignoreCase = true
                        ) || BuildConfig.FLAVOR.equals(
                            Constants.FLAVOR_TYPE_ACE_FRESNO, ignoreCase = true
                        ) || BuildConfig.FLAVOR.equals(
                            Constants.FLAVOR_TYPE_ACE_HGI_MANHATTAN, ignoreCase = true
                        ) || BuildConfig.FLAVOR.equals(
                            Constants.FLAVOR_TYPE_ACE_LAKE_TAHOE, ignoreCase = true
                        ) || BuildConfig.FLAVOR.equals(
                            Constants.FLAVOR_TYPE_ACE_VALET_DIVISION, ignoreCase = true
                        ) || BuildConfig.FLAVOR.equals(
                            Constants.FLAVOR_TYPE_ACE_SAN_ANTONIO, ignoreCase = true
                        ) || BuildConfig.FLAVOR.equals(
                            Constants.FLAVOR_TYPE_DALLAS, ignoreCase = true
                        ) || BuildConfig.FLAVOR.equals(
                            Constants.FLAVOR_TYPE_SMYRNABEACH, ignoreCase = true
                        ) || BuildConfig.FLAVOR.equals(
                            Constants.FLAVOR_TYPE_VOLUSIA, ignoreCase = true
                        ) || BuildConfig.FLAVOR.equals(
                            Constants.FLAVOR_TYPE_ADOBE, ignoreCase = true
                        ) || BuildConfig.FLAVOR.equals(
                            Constants.FLAVOR_TYPE_FASHION_CITY, ignoreCase = true
                        ) || BuildConfig.FLAVOR.equals(
                            Constants.FLAVOR_TYPE_PRRS, ignoreCase = true
                        ) || BuildConfig.FLAVOR.equals(
                            Constants.FLAVOR_TYPE_UPTOWN_ATLANTA, ignoreCase = true
                        ) || BuildConfig.FLAVOR.equals(
                            Constants.FLAVOR_TYPE_GREENBURGH_NY, ignoreCase = true
                        ) || BuildConfig.FLAVOR.equals(
                            Constants.FLAVOR_TYPE_DEER_FIELD_BEACH_FL, ignoreCase = true
                        ) || BuildConfig.FLAVOR.equals(
                            Constants.FLAVOR_TYPE_ACEAMAZON, ignoreCase = true
                        ) || BuildConfig.FLAVOR.equals(
                            Constants.FLAVOR_TYPE_IRVINE, ignoreCase = true
                        ) || BuildConfig.FLAVOR.equals(
                            Constants.FLAVOR_TYPE_PHOENIX, ignoreCase = true
                        )
                    ) {
                        bundle.putString("Remark", "")
                    } else {
                        bundle.putString("Remark", mEditTextViewTextViewRemark?.text.toString())
                    }

                    nav.safeNavigate(
                        R.id.action_bootScreenFragment_to_citationFormScreenFragment, bundle
                    )
                } else {
                    viewLifecycleOwner.lifecycleScope.launch {
                        mainActivityViewModel.backButtonPressed()
                    }
                }
            }
        }
    }

    private fun saveBookletWithStatus(mResponse: CitationNumberData) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val citationBookletModelList = mResponse.response?.citationBooklet
                    // mapNotNull handles null entries and creates models
                    ?.map { booklet ->
                        booklet.let {
                            CitationBookletModel().apply {
                                citationBooklet = it
                                mStatus = 0
                            }
                        }
                    } ?: emptyList()

                if (citationBookletModelList.isEmpty()) return@launch

                // Insert on IO
                withContext(Dispatchers.IO) {
                    bootScreenViewModel.insertCitationBooklet(citationBookletModelList)
                }

                // Read back on IO and then update UI on main
                val isBookletExists = withContext(Dispatchers.IO) {
                    bootScreenViewModel.getCitationBooklet(0)
                } ?: emptyList()

                requireContext().toast("Booklet saved - ${isBookletExists.size}")
                logE("Booklet saved -", isBookletExists.size.nullSafety().toString())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onStop() {
        super.onStop()
        if (LogUtil.getPrinterTypeForPrint() == PrinterType.ZEBRA_PRINTER) {
            zebraPrinterUseCase?.disconnect()
        }
    }

    override fun onActionSuccess(value: String) {
        //Nothing to do here
    }

    private fun consumeResponse(newApiResponse: NewApiResponse<Any>) {
        requireActivity().hideSoftKeyboard()

        when (newApiResponse) {
            is NewApiResponse.Idle -> {
                DialogUtil.hideLoader()
            }

            is NewApiResponse.Loading -> {
                DialogUtil.showLoader(
                    context = requireContext(),
                    message = getString(R.string.loader_text_please_wait_we_are_loading_data)
                )
            }

            is NewApiResponse.Success -> {
                try {
                    when (newApiResponse.apiNameTag) {
                        API_TAG_NAME_BOOT_SUBMIT -> {
                            handleBootSubmitResponse(newApiResponse.data as JsonNode)
                        }

                        API_TAG_NAME_BOOT_INSTANCE_TICKET -> {
                            handleBootInstanceTicketResponse(newApiResponse.data as JsonNode)
                        }

                        API_TAG_NAME_GET_CITATION_NUMBER -> {
                            handleGetCitationNumberResponse(newApiResponse.data as JsonNode)
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
    private fun handleBootSubmitResponse(jsonNodeValue: JsonNode) {
        val responseModel = runCatching {
            ObjectMapperProvider.fromJson(
                jsonNodeValue.toString(), ResponseBoot::class.java
            )
        }.getOrNull()

        if (responseModel != null && responseModel.isSuccess) {
            try {
                apiResultTag = "success"

                AlertDialogUtils.showDialog(
                    context = requireContext(),
                    icon = R.drawable.icon_success,
                    title = "Boot",
                    message = "Submit boot successfully",
                    positiveButtonText = getString(R.string.button_text_ok),
                    listener = object : AlertDialogListener {
                        override fun onPositiveButtonClicked() {
                            handleDialogButtonClick()
                        }
                    })
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            val responseModel = runCatching {
                ObjectMapperProvider.fromJson(
                    jsonNodeValue.toString(), ResponseBootError::class.java
                )
            }.getOrNull()

            apiResultTag = "fail"

            AlertDialogUtils.showDialog(
                context = requireContext(),
                title = getString(R.string.error_title_boot_submit_api_response),
                message = responseModel?.description.nullSafety(getString(R.string.err_msg_something_went_wrong)),
                positiveButtonText = getString(R.string.button_text_ok),
                listener = object : AlertDialogListener {
                    override fun onPositiveButtonClicked() {
                        handleDialogButtonClick()
                    }
                })
        }
    }

    private fun handleBootInstanceTicketResponse(jsonNodeValue: JsonNode) {
        val responseModel = runCatching {
            ObjectMapperProvider.fromJson(
                jsonNodeValue.toString(), BootInstanceTicketResponse::class.java
            )
        }.getOrNull()

        if (responseModel?.data != null && responseModel.status.nullSafety()) {
            try {
                viewLifecycleOwner.lifecycleScope.launch {
                    bootScreenViewModel.updateCitationBooklet(1, mCitationNumberId)
                }

                //Printing the ticket on API success only
                printFinalNotice(printBootNoticeModel)

                apiResultTag = "success_for_boot_instance"

                AlertDialogUtils.showDialog(
                    context = requireContext(),
                    icon = R.drawable.icon_success,
                    title = getString(R.string.err_title_api_successful),
                    message = responseModel.message.nullSafety("Ticket Created Successfully"),
                    positiveButtonText = getString(R.string.button_text_ok),
                    listener = object : AlertDialogListener {
                        override fun onPositiveButtonClicked() {
                            handleDialogButtonClick()
                        }
                    })
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {

            val responseErrorModel = runCatching {
                ObjectMapperProvider.fromJson(
                    jsonNodeValue.toString(), BootInstanceTicketErrorResponse::class.java
                )
            }.getOrNull()

            apiResultTag = "fail_for_boot_instance"

            AlertDialogUtils.showDialog(
                context = requireContext(),
                title = getString(R.string.error_title_boot_instance_ticket_api_response),
                message = (responseErrorModel?.data as String).nullOrEmptySafety(
                    getString(R.string.err_msg_something_went_wrong)
                ),
                positiveButtonText = getString(R.string.button_text_ok),
                listener = object : AlertDialogListener {
                    override fun onPositiveButtonClicked() {
                        handleDialogButtonClick()
                    }
                })
        }
    }

    private fun handleGetCitationNumberResponse(jsonNodeValue: JsonNode) {
        val responseModel = runCatching {
            ObjectMapperProvider.fromJson(
                jsonNodeValue.toString(), CitationNumberResponse::class.java
            )
        }.getOrNull()

        if (responseModel != null && responseModel.status.nullSafety()) {
            responseModel.data?.first()?.let { saveBookletWithStatus(it) }
        } else {
            AlertDialogUtils.showDialog(
                context = requireContext(),
                title = getString(R.string.error_title_get_citation_number_api_response),
                message = responseModel?.data?.firstOrNull()?.metadata.toString(),
                positiveButtonText = getString(R.string.button_text_ok)
            )
        }
    }
    //End of API Response Handling
}