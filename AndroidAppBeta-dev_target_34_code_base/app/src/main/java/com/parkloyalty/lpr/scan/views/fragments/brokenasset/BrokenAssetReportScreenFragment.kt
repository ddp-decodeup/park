package com.parkloyalty.lpr.scan.views.fragments.brokenasset

import DialogUtil
import android.annotation.SuppressLint
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.JsonNode
import com.google.android.material.textfield.TextInputLayout
import com.parkloyalty.lpr.scan.BuildConfig
import com.parkloyalty.lpr.scan.R
import com.parkloyalty.lpr.scan.databinding.FragmentBrokenAssetReportScreenBinding
import com.parkloyalty.lpr.scan.extensions.DuncanBrandingApp13
import com.parkloyalty.lpr.scan.extensions.getErrorMessage
import com.parkloyalty.lpr.scan.extensions.getIndexOfName
import com.parkloyalty.lpr.scan.extensions.hideSoftKeyboard
import com.parkloyalty.lpr.scan.extensions.isInternetAvailable
import com.parkloyalty.lpr.scan.extensions.nullSafety
import com.parkloyalty.lpr.scan.extensions.setCrossClearButton
import com.parkloyalty.lpr.scan.extensions.setListOnlyDropDown
import com.parkloyalty.lpr.scan.extensions.showErrorWithShake
import com.parkloyalty.lpr.scan.extensions.toast
import com.parkloyalty.lpr.scan.interfaces.Constants
import com.parkloyalty.lpr.scan.interfaces.SharedPrefKey
import com.parkloyalty.lpr.scan.locationservice.LocationUtils.getAddressFromLatLng
import com.parkloyalty.lpr.scan.network.response_handler.NewApiResponse
import com.parkloyalty.lpr.scan.ui.brokenmeter.model.BrokenMeterRequest
import com.parkloyalty.lpr.scan.ui.brokenmeter.model.BrokenMeterResponse
import com.parkloyalty.lpr.scan.ui.brokenmeter.model.Details
import com.parkloyalty.lpr.scan.ui.brokenmeter.model.LocationDetails
import com.parkloyalty.lpr.scan.ui.brokenmeter.model.OfficerDetails
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DatasetResponse
import com.parkloyalty.lpr.scan.ui.login.model.WelcomeForm
import com.parkloyalty.lpr.scan.util.AppUtils
import com.parkloyalty.lpr.scan.util.SharedPref
import com.parkloyalty.lpr.scan.utils.AlertDialogListener
import com.parkloyalty.lpr.scan.utils.AlertDialogUtils
import com.parkloyalty.lpr.scan.utils.AppConstants.DEFAULT_VALUE_ZERO_DOT_ZERO_STR
import com.parkloyalty.lpr.scan.utils.CitationFormUtils.CITATION_FORM_FIELD_TYPE_LIST_ONLY
import com.parkloyalty.lpr.scan.utils.NoInternetDialogListener
import com.parkloyalty.lpr.scan.utils.NoInternetDialogUtil
import com.parkloyalty.lpr.scan.utils.ObjectMapperProvider
import com.parkloyalty.lpr.scan.views.MainActivityAction
import com.parkloyalty.lpr.scan.views.MainActivityViewModel
import com.parkloyalty.lpr.scan.views.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Arrays
import java.util.Collections
import java.util.regex.Pattern
import javax.inject.Inject

@AndroidEntryPoint
class BrokenAssetReportScreenFragment : BaseFragment<FragmentBrokenAssetReportScreenBinding>() {
    private val mainActivityViewModel: MainActivityViewModel by activityViewModels()

    //Can be used later for settings specific logic
    private val brokenAssetReportScreenViewModel: BrokenAssetReportScreenViewModel by viewModels()

    @Inject
    lateinit var sharedPreference: SharedPref

    var mEditTextOfficerName: AppCompatTextView? = null
    var mEditTextDateTime: AppCompatTextView? = null
    var mAutoComTextViewBlock: AppCompatAutoCompleteTextView? = null
    var mEditTextViewTextViewStreet: AppCompatAutoCompleteTextView? = null
    var mEditTextViewTextViewSideOfState: AppCompatAutoCompleteTextView? = null
    var mEditTextViewTextViewMeterOutage: AppCompatAutoCompleteTextView? = null
    var mEditTextViewTextViewRemark: AppCompatAutoCompleteTextView? = null
    var mEditTextViewTextViewMeterNo: AppCompatAutoCompleteTextView? = null
    var textInputLayoutVehMeterNo: TextInputLayout? = null
    var textInputLayoutVehBlock: TextInputLayout? = null
    var textInputLayoutVehStreet: TextInputLayout? = null
    var textInputLayoutVehSideOfState: TextInputLayout? = null
    var textInputLayoutMeterOutage: TextInputLayout? = null
    var textInputLayoutVehRemark: TextInputLayout? = null

    private val mSideItem = ""
    private var clientTime = ""
    private val meterOutageList = arrayOf("Sign outage", "Meter outage")
    private val meterOutageListValue = arrayOf("sign_outage", "meter_outage")
    private val meterOutageListTypeValue = arrayOf("broken_sign", "broken_meter")
    private var outageValue = "sign_outage"
    private var outageTypeValue = "broken_sign"
    private var mWelcomeFormData: WelcomeForm? = null

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentBrokenAssetReportScreenBinding.inflate(inflater, container, false)

    override fun findViewsByViewBinding() {
        mEditTextOfficerName = binding.officername
        mEditTextDateTime = binding.txtIssuedate
        mAutoComTextViewBlock = binding.AutoComTextViewVehblock
        mEditTextViewTextViewStreet = binding.AutoComTextViewVehStreet
        mEditTextViewTextViewSideOfState = binding.AutoComTextViewVehSideOfState

        mEditTextViewTextViewMeterOutage = binding.AutoComTextViewMeterOutage
        mEditTextViewTextViewRemark = binding.AutoComTextViewVehRemark
        mEditTextViewTextViewMeterNo = binding.AutoComTextViewVehMeterNo
        textInputLayoutVehMeterNo = binding.textInputLayoutVehMeterNo
        textInputLayoutVehBlock = binding.textInputLayoutVehblock

        textInputLayoutVehStreet = binding.textInputLayoutVehStreet
        textInputLayoutVehSideOfState = binding.textInputLayoutVehSideOfState
        textInputLayoutMeterOutage = binding.textInputLayoutMeterOutage
        textInputLayoutVehRemark = binding.textInputLayoutVehRemark
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
                    brokenAssetReportScreenViewModel.brokenAssetsReportResponse.collect(::consumeResponse)
                }
            }
        }
    }

    override fun initialiseData() {
        viewLifecycleOwner.lifecycleScope.launch {

        }

        viewLifecycleOwner.lifecycleScope.launch {
            mWelcomeFormData = withContext(Dispatchers.IO) {
                brokenAssetReportScreenViewModel.getWelcomeForm()
            } ?: return@launch

            setDropDowns()
            mEditTextDateTime?.text = AppUtils.getCurrentDateTimeforBoot("UI")
            clientTime = AppUtils.getCurrentDateTimeforBoot("Normal").trim().replace(" ", "T")
        }

        setCrossClearButton(
            context = requireContext(),
            textInputLayout = textInputLayoutVehMeterNo,
            appCompatAutoCompleteTextView = mEditTextViewTextViewMeterNo
        )
        setCrossClearButton(
            context = requireContext(),
            textInputLayout = textInputLayoutVehBlock,
            appCompatAutoCompleteTextView = mAutoComTextViewBlock
        )
        setCrossClearButton(
            context = requireContext(),
            textInputLayout = textInputLayoutVehStreet,
            appCompatAutoCompleteTextView = mEditTextViewTextViewStreet
        )
        setCrossClearButton(
            context = requireContext(),
            textInputLayout = textInputLayoutVehSideOfState,
            appCompatAutoCompleteTextView = mEditTextViewTextViewSideOfState
        )
        setCrossClearButton(
            context = requireContext(),
            textInputLayout = textInputLayoutMeterOutage,
            appCompatAutoCompleteTextView = mEditTextViewTextViewMeterOutage
        )
        setCrossClearButton(
            context = requireContext(),
            textInputLayout = textInputLayoutVehRemark,
            appCompatAutoCompleteTextView = mEditTextViewTextViewRemark
        )
    }

    override fun setupClickListeners() {
        binding.btnSubmit.setOnClickListener { view ->
            if (isFormValid()) {
                setRequest()
            }
        }
    }

    private fun isFormValid(): Boolean {
        if (TextUtils.isEmpty(
                mEditTextViewTextViewSideOfState?.text.toString().trim()
            )
        ) {
            mEditTextViewTextViewSideOfState?.requestFocus()
            mEditTextViewTextViewSideOfState?.isFocusable = true
            textInputLayoutVehSideOfState?.showErrorWithShake(getString(R.string.err_lbl_side_of_street))
            return false
        }
        if (TextUtils.isEmpty(
                mEditTextViewTextViewMeterOutage?.text.toString().trim()
            )
        ) {
            mEditTextViewTextViewMeterOutage?.requestFocus()
            mEditTextViewTextViewMeterOutage?.isFocusable = true
            textInputLayoutMeterOutage?.showErrorWithShake(getString(R.string.err_lbl_meter_outage))
            return false
        }
        if (TextUtils.isEmpty(
                mEditTextViewTextViewMeterNo?.text.toString().trim()
            ) && TextUtils.isEmpty(
                mEditTextViewTextViewMeterOutage?.text.toString().trim()
            )
        ) {
            mEditTextViewTextViewMeterNo?.requestFocus()
            mEditTextViewTextViewMeterNo?.isFocusable = true
            textInputLayoutVehMeterNo?.showErrorWithShake(getString(R.string.err_lbl_meter_no))
            return false
        }
        if (TextUtils.isEmpty(
                mEditTextViewTextViewRemark?.text.toString().trim()
            )
        ) {
            mEditTextViewTextViewRemark?.requestFocus()
            mEditTextViewTextViewRemark?.isFocusable = true
            textInputLayoutVehRemark?.showErrorWithShake(getString(R.string.err_lbl_remarks))
            return false
        }
        return true
    }

    private fun setDropDowns() {
        try {
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

                setDropdownSide(sideList)
            }

            viewLifecycleOwner.lifecycleScope.launch {
                val meterList = withContext(Dispatchers.IO) {
                    mainActivityViewModel.getMeterListFromDataSet()
                } ?: return@launch

                setDropdownMeterName(meterList)
            }

            viewLifecycleOwner.lifecycleScope.launch {
                val streetList = withContext(Dispatchers.IO) {
                    mainActivityViewModel.getStreetListFromDataSet()
                } ?: return@launch

                setDropdownStreet(streetList)
            }

            viewLifecycleOwner.lifecycleScope.launch {
                val blockList = withContext(Dispatchers.IO) {
                    mainActivityViewModel.getBlockListFromDataSet()
                } ?: return@launch

                setDropdownBlock(blockList)
            }

            setDropdownMeteroutage(meterOutageList)
            getGeoAddress()
            mEditTextOfficerName?.text =
                mWelcomeFormData?.officerFirstName + " " + mWelcomeFormData?.officerLastName
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Here 1 represent max location result to returned, by documents it recommended 1 to 5
    private fun getGeoAddress() {
        mEditTextDateTime?.text = AppUtils.getCurrentDateTimeforBoot("UI")
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

    //set value to Body Style dropdown
    private fun setDropdownRemark(mApplicationList: List<DatasetResponse>?) {
        val addZoneValueInZeroIndex = DatasetResponse()
        Collections.sort(mApplicationList) { lhs, rhs ->
            lhs.remark.nullSafety().compareTo(rhs.remark.nullSafety())
        }
        val pos = 0
        if (mApplicationList != null && mApplicationList.isNotEmpty()) {
            val mDropdownList = arrayOfNulls<String>(mApplicationList.size)
            for (i in mApplicationList.indices) {
                mDropdownList[i] = mApplicationList[i].remark.toString()
            }

//            Arrays.sort(mDropdownList);
            val adapter = ArrayAdapter(
                requireContext(), R.layout.row_dropdown_menu_popup_item, mDropdownList
            )
            try {
                mEditTextViewTextViewRemark?.threshold = 1
                mEditTextViewTextViewRemark?.setAdapter<ArrayAdapter<String?>>(adapter)
                //mSelectedShiftStat = mApplicationList.get(pos);
                mEditTextViewTextViewRemark?.onItemClickListener =
                    OnItemClickListener { parent, view, position, id -> // mSelectedShiftStat = mApplicationList.get(position);
                        requireActivity().hideSoftKeyboard()
                        mEditTextViewTextViewRemark?.error = null
                    }
            } catch (e: Exception) {
            }
        } else {
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
                //mSelectedShiftStat = mApplicationList.get(pos);
                mEditTextViewTextViewSideOfState?.onItemClickListener =
                    OnItemClickListener { parent, view, position, id -> // mSelectedShiftStat = mApplicationList.get(position);
                        requireActivity().hideSoftKeyboard()
                        mEditTextViewTextViewSideOfState?.error = null
                    }
            } catch (e: Exception) {
            }
        } else {
        }
    }

    //set value to Meter Name dropdown
    private fun setDropdownMeterName(mApplicationList: List<DatasetResponse>?) {
        if (mApplicationList != null && mApplicationList.isNotEmpty()) {
            val mDropdownList = arrayOfNulls<String>(mApplicationList.size)
            for (i in mApplicationList.indices) {
                mDropdownList[i] = mApplicationList[i].name.toString()
            }
            //            Arrays.sort(mDropdownList);
            val adapter = ArrayAdapter(
                requireContext(), R.layout.row_dropdown_menu_popup_item, mDropdownList
            )
            try {
                mEditTextViewTextViewMeterNo?.threshold = 1
                mEditTextViewTextViewMeterNo?.setAdapter<ArrayAdapter<String?>>(adapter)
                mEditTextViewTextViewMeterNo?.onItemClickListener =
                    OnItemClickListener { parent, view, position, id ->
                        requireActivity().hideSoftKeyboard()
                        try {
                            mEditTextViewTextViewMeterNo?.error = null
                            val index: Int =
                                mApplicationList.getIndexOfName(mEditTextViewTextViewMeterNo?.text.toString())

                            if (mApplicationList[index].direction != null && !TextUtils.isEmpty(
                                    mApplicationList[index].direction
                                )
                            ) {
                                mEditTextViewTextViewSideOfState?.setText(mApplicationList[index].direction)
                            }

                            if (mApplicationList[index].block != null && !TextUtils.isEmpty(
                                    mApplicationList[index].block
                                )
                            ) {
                                mAutoComTextViewBlock?.setText(mApplicationList[index].block)
                            }
                            if (mApplicationList[index].street != null && !TextUtils.isEmpty(
                                    mApplicationList[index].street
                                )
                            ) {
                                mEditTextViewTextViewStreet?.setText(mApplicationList[index].street)
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

    //set value to Meter Name dropdown
    private fun setDropdownMeteroutage(mApplicationList: Array<String>) {
        val adapter = ArrayAdapter(
            requireContext(), R.layout.row_dropdown_menu_popup_item, mApplicationList
        )
        try {
            mEditTextViewTextViewMeterOutage?.threshold = 1
            mEditTextViewTextViewMeterOutage?.setAdapter(adapter)
            //mSelectedShiftStat = mApplicationList.get(pos);
            mEditTextViewTextViewMeterOutage?.onItemClickListener =
                OnItemClickListener { parent, view, position, id ->
                    mEditTextViewTextViewMeterOutage?.error = null
                    requireActivity().hideSoftKeyboard()
                    outageValue = meterOutageListValue[position]
                    outageTypeValue = meterOutageListTypeValue[position]
                }
        } catch (e: Exception) {
        }
    }

    //set value to Street dropdown
    private fun setDropdownStreet(mApplicationList: List<DatasetResponse>?) {
        if (mApplicationList != null && mApplicationList.size > 0) {
            val mDropdownList = arrayOfNulls<String>(mApplicationList.size)
            for (i in mApplicationList.indices) {
                mDropdownList[i] = mApplicationList[i].street_name.toString()
            }

            val adapter = ArrayAdapter(
                requireContext(), R.layout.row_dropdown_menu_popup_item, mDropdownList
            )
            try {
                mEditTextViewTextViewStreet?.threshold = 1
                mEditTextViewTextViewStreet?.setAdapter(adapter)
                mEditTextViewTextViewStreet?.onItemClickListener =
                    OnItemClickListener { parent, view, position, id ->
                        mEditTextViewTextViewStreet?.error = null
                        requireActivity().hideSoftKeyboard()
                    }
            } catch (e: java.lang.Exception) {
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
                mAutoComTextViewBlock?.setAdapter(adapter)
                mAutoComTextViewBlock?.onItemClickListener =
                    OnItemClickListener { parent, view, _, id ->
                        mAutoComTextViewBlock?.error = null
                        requireActivity().hideSoftKeyboard()
                    }

                if (mAutoComTextViewBlock?.tag != null && mAutoComTextViewBlock?.tag == CITATION_FORM_FIELD_TYPE_LIST_ONLY) {
                    mAutoComTextViewBlock?.setListOnlyDropDown(
                        context = requireContext(), textInputLayout = textInputLayoutVehBlock
                    )
                }
            } catch (e: Exception) {
            }
        }
    }


    private fun setRequest() {
        if (requireContext().isInternetAvailable()) {
            val mLat = sharedPreference.read(SharedPrefKey.LAT, "0.0")?.toDouble().nullSafety()
            val mLong = sharedPreference.read(SharedPrefKey.LONG, "0.0")?.toDouble().nullSafety()
            val bootRequest = BrokenMeterRequest()
            bootRequest.clientTimestamp = clientTime
            bootRequest.type = outageTypeValue
            val detailsBroken = Details()
            detailsBroken.meterNo = mEditTextViewTextViewMeterNo?.text.toString()
            detailsBroken.remarks = mEditTextViewTextViewRemark?.text.toString()
            detailsBroken.reason = outageValue
            bootRequest.details = detailsBroken
            val locationDetails = LocationDetails()
            locationDetails.block = mAutoComTextViewBlock?.text.toString()
            locationDetails.side = mEditTextViewTextViewSideOfState?.text.toString()
            locationDetails.street = mEditTextViewTextViewStreet?.text.toString()
            locationDetails.latitude = mLat
            locationDetails.longitude = mLong
            bootRequest.locationDetails = locationDetails
            val officerDetails = OfficerDetails()
            officerDetails.officerName =
                (mWelcomeFormData?.officerFirstName + " " + mWelcomeFormData?.officerLastName)
            officerDetails.siteOfficerId = mWelcomeFormData?.siteOfficerId
            officerDetails.shift = sharedPreference.read(SharedPrefKey.LOGIN_SHIFT, "")
            if (BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_DUNCAN, true
                ) || BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_PILOTPITTSBURGPA, true
                ) || BuildConfig.FLAVOR.equals(DuncanBrandingApp13()) || BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_MACKAY_SAMPLE, true
                )
            ) {
                officerDetails.zone = mWelcomeFormData?.cityZoneName
            } else {
                officerDetails.zone = mWelcomeFormData?.officerZone
            }
            bootRequest.officerDetails = officerDetails
            brokenAssetReportScreenViewModel.callBrokenMeterReportAPI(bootRequest)
        } else {
            NoInternetDialogUtil.showDialog(
                context = requireContext(),
                positiveButtonText = getString(R.string.button_text_ok),
                negativeButtonText = getString(R.string.button_text_retry),
                listener = object : NoInternetDialogListener {
                    override fun onNegativeButtonClicked() {
                        setRequest()
                    }
                })
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
                        (newApiResponse.data as JsonNode).toString(),
                        BrokenMeterResponse::class.java
                    )

                    if (responseModel.isSuccess.nullSafety()) {
                        AlertDialogUtils.showDialog(
                            context = requireContext(),
                            icon = R.drawable.icon_success,
                            cancelable = false,
                            title = "Broken Meter",
                            message = "Submit Broken Meter Successfully",
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
                            title = "Broken Meter",
                            cancelable = false,
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
}