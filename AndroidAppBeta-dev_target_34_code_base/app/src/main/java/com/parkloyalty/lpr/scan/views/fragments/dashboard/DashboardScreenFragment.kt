package com.parkloyalty.lpr.scan.views.fragments.dashboard

import DialogUtil
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.JsonNode
import com.parkloyalty.lpr.scan.BuildConfig
import com.parkloyalty.lpr.scan.R
import com.parkloyalty.lpr.scan.databinding.FragmentDashboardScreenBinding
import com.parkloyalty.lpr.scan.extensions.getErrorMessage
import com.parkloyalty.lpr.scan.extensions.hideSoftKeyboard
import com.parkloyalty.lpr.scan.extensions.hideView
import com.parkloyalty.lpr.scan.extensions.isInternetAvailable
import com.parkloyalty.lpr.scan.extensions.nav
import com.parkloyalty.lpr.scan.extensions.nullSafety
import com.parkloyalty.lpr.scan.extensions.safeNavigate
import com.parkloyalty.lpr.scan.extensions.showView
import com.parkloyalty.lpr.scan.extensions.toast
import com.parkloyalty.lpr.scan.interfaces.Constants
import com.parkloyalty.lpr.scan.interfaces.SharedPrefKey
import com.parkloyalty.lpr.scan.network.response_handler.NewApiResponse
import com.parkloyalty.lpr.scan.ui.check_setup.activity.graph.model.Datum
import com.parkloyalty.lpr.scan.ui.check_setup.activity.graph.model.GetBarCountResponse
import com.parkloyalty.lpr.scan.util.AppUtils
import com.parkloyalty.lpr.scan.util.FileUtil
import com.parkloyalty.lpr.scan.util.INTENT_KEY_LPR_BUNDLE
import com.parkloyalty.lpr.scan.util.INTENT_KEY_LPR_NUMBER
import com.parkloyalty.lpr.scan.util.INTENT_KEY_SCANNER_TYPE
import com.parkloyalty.lpr.scan.util.INTENT_KEY_VEHICLE_INFO
import com.parkloyalty.lpr.scan.util.INTENT_KEY_VEHICLE_STICKER_URL
import com.parkloyalty.lpr.scan.util.SharedPref
import com.parkloyalty.lpr.scan.utils.AlertDialogUtils
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_GET_BAR_COUNT
import com.parkloyalty.lpr.scan.utils.NoInternetDialogListener
import com.parkloyalty.lpr.scan.utils.NoInternetDialogUtil
import com.parkloyalty.lpr.scan.utils.ObjectMapperProvider
import com.parkloyalty.lpr.scan.utils.permissions.PermissionManager
import com.parkloyalty.lpr.scan.utils.permissions.PermissionUtils.getBluetoothPermissions
import com.parkloyalty.lpr.scan.views.MainActivityViewModel
import com.parkloyalty.lpr.scan.views.NewLprScanActivity
import com.parkloyalty.lpr.scan.views.NewVehicleStickerScanActivity
import com.parkloyalty.lpr.scan.views.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class DashboardScreenFragment : BaseFragment<FragmentDashboardScreenBinding>() {
    private val mainActivityViewModel: MainActivityViewModel by activityViewModels()
    private val dashboardScreenViewModel: DashboardScreenViewModel by viewModels()

    lateinit var btnCheckSetup: AppCompatButton
    lateinit var tvScanCount: AppCompatTextView
    lateinit var tvEnforcementCount: AppCompatTextView
    lateinit var btnScanSticker: AppCompatButton

    @Inject
    lateinit var permissionFactory: PermissionManager.Factory

    private lateinit var permissionManager: PermissionManager

    @Inject
    lateinit var sharedPreference: SharedPref

    private lateinit var lprScanActivityLauncher: ActivityResultLauncher<Intent>

    private lateinit var vehicleStickerActivityLauncher: ActivityResultLauncher<Intent>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        permissionManager =
            permissionFactory.create(caller = this, context = requireContext(), fragment = this)

        registerActivityResultLauncher()
    }

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentDashboardScreenBinding.inflate(inflater, container, false)

    override fun findViewsByViewBinding() {
        btnCheckSetup = binding.btnCheckSetup
        tvScanCount = binding.tvScanCount
        tvEnforcementCount = binding.tvEnforcementCount
        btnScanSticker = binding.btnScanSticker
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
                    dashboardScreenViewModel.barCountResponse.collect(::consumeResponse)
                }
            }
        }
    }

    override fun initialiseData() {
        viewLifecycleOwner.lifecycleScope.launch {
            permissionManager.ensurePermissionsThen(
                permissions = getBluetoothPermissions(),
                rationaleMessage = getString(R.string.permission_message_all_permission_required_to_login)
            ) {
                //Nothing to do now
            }
        }

        if (mainActivityViewModel.showAndEnableScanVehicleStickerModule) {
            btnScanSticker.showView()
        } else {
            btnScanSticker.hideView()
        }

        FileUtil.createFolderForLprImages()
        mainActivityViewModel.eventStartTimeStamp = AppUtils.getDateTime()
        callDashboardApi()
    }

    override fun setupClickListeners() {
        btnCheckSetup.setOnClickListener {
            nav.safeNavigate(R.id.checkSetupScreenFragment)
        }

        binding.imgScan.setOnClickListener {
            if (BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_BISMARCK, ignoreCase = true
                ) || BuildConfig.FLAVOR.equals(
                    Constants.FLAVOR_TYPE_SEPTA, ignoreCase = true
                )
            ) {
                nav.safeNavigate(R.id.scanResultScreenFragment)
            } else {
                val intent = Intent(requireContext(), NewLprScanActivity::class.java)
                intent.putExtra(INTENT_KEY_SCANNER_TYPE, mainActivityViewModel.getLprScannerType())
                lprScanActivityLauncher.launch(intent)
            }
        }

        binding.cardTotalScan.setOnClickListener {
            nav.safeNavigate(R.id.myActivityScreenFragment)
        }

        binding.llTotalScan.setOnClickListener {
            nav.safeNavigate(R.id.myActivityScreenFragment)
        }

        binding.cardTotalEnforcement.setOnClickListener {
            nav.safeNavigate(R.id.searchScreenFragment)
        }

        binding.llTotalEnforcement.setOnClickListener {
            nav.safeNavigate(R.id.searchScreenFragment)
        }

        btnScanSticker.setOnClickListener {
            val intent = Intent(requireContext(), NewVehicleStickerScanActivity::class.java)
            vehicleStickerActivityLauncher.launch(intent)
        }
    }

    fun registerActivityResultLauncher() {
        lprScanActivityLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                mainActivityViewModel.eventStartTimeStamp = AppUtils.getDateTime()
                if (result.resultCode == RESULT_OK) {
                    val intent = result.data
                    val bundle = intent?.getBundleExtra(INTENT_KEY_LPR_BUNDLE)
                    nav.safeNavigate(R.id.scanResultScreenFragment, bundle)
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

    /* Call Api For Dashboard details */
    private fun callDashboardApi() {
        if (requireContext().isInternetAvailable()) {
            dashboardScreenViewModel.callGetBarCountAPI(
                sharedPreference.read(
                    SharedPrefKey.LOGIN_SHIFT, ""
                ).nullSafety()
            )
        } else {
            NoInternetDialogUtil.showDialog(
                context = requireContext(),
                positiveButtonText = getString(R.string.button_text_ok),
                negativeButtonText = getString(R.string.button_text_retry),
                listener = object : NoInternetDialogListener {
                    override fun onNegativeButtonClicked() {
                        callDashboardApi()
                    }
                })
        }
    }

    private fun setDashboardDetails(dashboardData: Datum?) {
        tvScanCount.text = dashboardData?.scans.nullSafety().toString()
        tvEnforcementCount.text = dashboardData?.tickets.nullSafety().toString()
    }

    //API Response Consumer function for all APIs
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
                    when (newApiResponse.apiNameTag) {
                        API_TAG_NAME_GET_BAR_COUNT -> {
                            val getBarCountResponse = ObjectMapperProvider.fromJson(
                                (newApiResponse.data as JsonNode).toString(),
                                GetBarCountResponse::class.java
                            )

                            handleGetBarCountResponse(getBarCountResponse)
                        }
                    }
                } catch (e: JsonMappingException) {
                    requireContext().toast(message = getString(R.string.error_desc_please_login_again_to_use_the_application))
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

    fun handleGetBarCountResponse(responseModel: GetBarCountResponse) {
        when (responseModel.status) {
            true -> {
                setDashboardDetails(responseModel.data?.firstOrNull())
            }

            false -> {
                AlertDialogUtils.showDialog(
                    context = requireContext(),
                    title = getString(R.string.error_title_get_bar_count_api_response),
                    message = responseModel.response.nullSafety(getString(R.string.error_desc_unable_to_receive_data_from_service)),
                    positiveButtonText = getString(R.string.button_text_ok)
                )
            }

            else -> {
                AlertDialogUtils.showDialog(
                    context = requireContext(),
                    title = getString(R.string.error_title_get_bar_count_api_response),
                    message = getString(R.string.error_desc_something_went_wrong_please_try_again),
                    positiveButtonText = getString(R.string.button_text_ok)
                )
            }
        }
    }
}