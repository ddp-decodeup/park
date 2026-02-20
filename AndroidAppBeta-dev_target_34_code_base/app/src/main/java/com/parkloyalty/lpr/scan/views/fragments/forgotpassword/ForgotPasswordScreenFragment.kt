package com.parkloyalty.lpr.scan.views.fragments.forgotpassword

import DialogUtil
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.fasterxml.jackson.databind.JsonNode
import com.google.android.material.textfield.TextInputLayout
import com.parkloyalty.lpr.scan.R
import com.parkloyalty.lpr.scan.databinding.FragmentForgotPasswordScreenBinding
import com.parkloyalty.lpr.scan.extensions.getErrorMessage
import com.parkloyalty.lpr.scan.extensions.hideSoftKeyboard
import com.parkloyalty.lpr.scan.extensions.isInternetAvailable
import com.parkloyalty.lpr.scan.extensions.nullSafety
import com.parkloyalty.lpr.scan.extensions.setCrossClearButton
import com.parkloyalty.lpr.scan.extensions.setupTextInputLayout
import com.parkloyalty.lpr.scan.extensions.showErrorWithShake
import com.parkloyalty.lpr.scan.network.response_handler.NewApiResponse
import com.parkloyalty.lpr.scan.ui.login.model.ForgetPasswordRequest
import com.parkloyalty.lpr.scan.ui.login.model.ForgotPasswordResponse
import com.parkloyalty.lpr.scan.util.AppUtils
import com.parkloyalty.lpr.scan.util.AppUtils.isValidEmail
import com.parkloyalty.lpr.scan.utils.AlertDialogListener
import com.parkloyalty.lpr.scan.utils.AlertDialogUtils
import com.parkloyalty.lpr.scan.utils.ApiConstants.STATIC_EMAIL_ADDRESS_FORGOT_PASSWORD
import com.parkloyalty.lpr.scan.utils.ApiConstants.STATIC_USER_ID_FORGOT_PASSWORD
import com.parkloyalty.lpr.scan.utils.NoInternetDialogListener
import com.parkloyalty.lpr.scan.utils.NoInternetDialogUtil
import com.parkloyalty.lpr.scan.utils.ObjectMapperProvider
import com.parkloyalty.lpr.scan.views.MainActivityViewModel
import com.parkloyalty.lpr.scan.views.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ForgotPasswordScreenFragment : BaseFragment<FragmentForgotPasswordScreenBinding>() {
    private val mainActivityViewModel: MainActivityViewModel by activityViewModels()
    private val forgotPasswordScreenViewModel: ForgotPasswordScreenViewModel by viewModels()

    private lateinit var btnCancel: AppCompatButton
    private lateinit var textInputEmail: TextInputLayout
    private lateinit var editTextEmail: AppCompatEditText
    private lateinit var textInputUserId: TextInputLayout
    private lateinit var editTextUserId: AppCompatEditText

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentForgotPasswordScreenBinding.inflate(inflater, container, false)

    override fun findViewsByViewBinding() {
        btnCancel = binding.btnCancel
        textInputEmail = binding.layoutContentForgotPassword.inputEmail
        editTextEmail = binding.layoutContentForgotPassword.etEmail
        textInputUserId = binding.layoutContentForgotPassword.inputUserId
        editTextUserId = binding.layoutContentForgotPassword.etUserId
    }

    override fun initViewLifecycleScope() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    forgotPasswordScreenViewModel.forgotPasswordResponse.collect(::consumeResponse)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                launch {
                    mainActivityViewModel.setToolbarVisibility(true)
                    mainActivityViewModel.setToolbarComponents(
                        showBackButton = true, showLogo = true, showHemMenu = false
                    )
                }
            }
        }
    }

    override fun initialiseData() {
        setCrossClearButtonForAllFields()
        mainActivityViewModel.eventStartTimeStamp = AppUtils.getDateTime()

        initUI()
    }

    private fun initUI() {
        textInputUserId.setupTextInputLayout(
            isEditTextInside = true,
            hintText = getString(R.string.hint_user_id),
            placeholder = getString(R.string.hint_enter_user_id)
        )

        textInputEmail.setupTextInputLayout(
            isEditTextInside = true,
            hintText = getString(R.string.hint_email),
            placeholder = getString(R.string.hint_enter_email_address)
        )
    }

    override fun setupClickListeners() {
        btnCancel.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                mainActivityViewModel.backButtonPressed()
            }
        }

        binding.btnContinue.setOnClickListener {
            if (isValidForgotPasswordDetails()) {
                callForgetPasswordAPI()
            }
        }
    }

    private fun isValidForgotPasswordDetails(): Boolean {
        val email = editTextEmail.text?.toString()?.trim().nullSafety()
        val userId = editTextUserId.text?.toString()?.trim().nullSafety()

        if (TextUtils.isEmpty(email)) {
            textInputEmail.showErrorWithShake(getString(R.string.val_msg_please_enter_email))
            return false
        } else if (!isValidEmail(email)) {
            textInputEmail.showErrorWithShake(getString(R.string.val_msg_please_enter_valid_email))
            return false
        } else if (TextUtils.isEmpty(userId)) {
            textInputUserId.showErrorWithShake(getString(R.string.val_msg_please_enter_user_id))
            return false
        } else if (userId != STATIC_USER_ID_FORGOT_PASSWORD || email != STATIC_EMAIL_ADDRESS_FORGOT_PASSWORD) {
            AlertDialogUtils.showDialog(
                context = requireContext(),
                title = getString(R.string.error_title_validation_error),
                message = getString(R.string.error_desc_username_password_combination_dont_match),
                positiveButtonText = getString(R.string.button_text_ok)
            )
            return false
        }
        return true
    }


    private fun callForgetPasswordAPI() {
        if (requireContext().isInternetAvailable()) {
            val forgetPassRequest = ForgetPasswordRequest()
            forgetPassRequest.mSiteOfficerEmail = editTextEmail.editableText.toString().trim()
            forgetPassRequest.siteOfficerUserName = editTextUserId.editableText.toString().trim()
            forgotPasswordScreenViewModel.callForgotPasswordAPI(forgetPassRequest)
        } else {
            NoInternetDialogUtil.showDialog(
                context = requireContext(),
                positiveButtonText = getString(R.string.button_text_ok),
                negativeButtonText = getString(R.string.button_text_retry),
                listener = object : NoInternetDialogListener {
                    override fun onPositiveButtonClicked() {

                    }

                    override fun onNegativeButtonClicked() {
                        callForgetPasswordAPI()
                    }
                })
        }
    }

    private fun setCrossClearButtonForAllFields() {
        setCrossClearButton(
            context = requireContext(),
            textInputLayout = textInputEmail,
            appCompatEditText = editTextEmail
        )

        setCrossClearButton(
            context = requireContext(),
            textInputLayout = textInputUserId,
            appCompatEditText = editTextUserId
        )
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

                val forgotPasswordResponse = ObjectMapperProvider.fromJson(
                    (newApiResponse.data as JsonNode).toString(), ForgotPasswordResponse::class.java
                )
                handleForgotPasswordResponse(forgotPasswordResponse)
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

    private fun handleForgotPasswordResponse(forgotPasswordResponse: ForgotPasswordResponse) {

        when (forgotPasswordResponse.status) {
            true -> {
                AlertDialogUtils.showDialog(
                    context = requireContext(),
                    icon = R.drawable.icon_success,
                    title = getString(R.string.success_title_request_taken),
                    message = getString(R.string.success_desc_password_will_be_emailed),
                    positiveButtonText = getString(R.string.button_text_ok),
                    listener = object : AlertDialogListener {
                        override fun onPositiveButtonClicked() {
                            viewLifecycleOwner.lifecycleScope.launch {
                                mainActivityViewModel.backButtonPressed()
                            }
                        }
                    })
            }

            false -> {
                AlertDialogUtils.showDialog(
                    context = requireContext(),
                    title = getString(R.string.error_title_forgot_password_api_response),
                    message = forgotPasswordResponse.response.nullSafety(getString(R.string.error_desc_unable_to_receive_data_from_service)),
                    positiveButtonText = getString(R.string.button_text_ok)
                )
            }

            else -> {
                AlertDialogUtils.showDialog(
                    context = requireContext(),
                    title = getString(R.string.error_title_forgot_password_api_response),
                    message = getString(R.string.error_desc_something_went_wrong_please_try_again),
                    positiveButtonText = getString(R.string.button_text_ok)
                )
            }
        }
    }
}