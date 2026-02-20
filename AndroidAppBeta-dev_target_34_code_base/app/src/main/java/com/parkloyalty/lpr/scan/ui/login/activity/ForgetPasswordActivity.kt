package com.parkloyalty.lpr.scan.ui.login.activity

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import androidx.activity.viewModels
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.lifecycle.Observer
import com.google.android.material.textfield.TextInputLayout
import com.parkloyalty.lpr.scan.R
import com.parkloyalty.lpr.scan.basecontrol.BaseActivity
import com.parkloyalty.lpr.scan.databinding.ActivityForgetPasswordBinding
import com.parkloyalty.lpr.scan.extensions.nullSafety
import com.parkloyalty.lpr.scan.interfaces.CustomDialogHelper
import com.parkloyalty.lpr.scan.network.ApiResponse
import com.parkloyalty.lpr.scan.network.DynamicAPIPath
import com.parkloyalty.lpr.scan.network.NetworkCheck.isInternetAvailable
import com.parkloyalty.lpr.scan.network.Status
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DropdownDatasetResponse
import com.parkloyalty.lpr.scan.ui.login.LoginActivity
import com.parkloyalty.lpr.scan.ui.login.model.CommonLoginResponse
import com.parkloyalty.lpr.scan.ui.login.model.ForgetPasswordRequest
import com.parkloyalty.lpr.scan.ui.login.model.ForgetPasswordViewModel
import com.parkloyalty.lpr.scan.util.AppUtils

import com.parkloyalty.lpr.scan.util.AppUtils.isValidEmail
import com.parkloyalty.lpr.scan.util.AppUtils.showCustomAlertDialog
import com.parkloyalty.lpr.scan.util.LogUtil.printLog
import com.parkloyalty.lpr.scan.util.LogUtil.printToastMSG
import com.parkloyalty.lpr.scan.util.LogUtil.printToastMSGForErrorWarning
import com.parkloyalty.lpr.scan.utils.ObjectMapperProvider
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class ForgetPasswordActivity : BaseActivity(), CustomDialogHelper {
    private lateinit var mBtnCancel: AppCompatButton
    private lateinit var mTextInputEmail: TextInputLayout
    private lateinit var mEditTextEmail: AppCompatEditText
    private lateinit var mTextInputUserId: TextInputLayout
    private lateinit var mEditTextUserId: AppCompatEditText

    private val mForgetPasswordViewModel: ForgetPasswordViewModel? by viewModels()
    private var mContext: Context? = null

    private lateinit var binding: ActivityForgetPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        findViewsByViewBinding()
        setupClickListeners()

        setFullScreenUI()
        mContext = this@ForgetPasswordActivity
        addObservers()
        init()

        setCrossClearButton(
            context = this@ForgetPasswordActivity,
            textInputLayout = mTextInputEmail,
            appCompatEditText = mEditTextEmail
        )

        setCrossClearButton(
            context = this@ForgetPasswordActivity,
            textInputLayout = mTextInputUserId,
            appCompatEditText = mEditTextUserId
        )
    }

    private fun findViewsByViewBinding(){
        mBtnCancel = binding.btnCancel
        mTextInputEmail = binding.layoutContentForgotPassword.inputEmail
        mEditTextEmail = binding.layoutContentForgotPassword.etEmail
        mTextInputUserId = binding.layoutContentForgotPassword.inputUserId
        mEditTextUserId = binding.layoutContentForgotPassword.etUserId
    }

    private fun setupClickListeners(){
        mBtnCancel.setOnClickListener {
            finish()
        }

        binding.btnContinue.setOnClickListener {
            if (isDetailsValid()) {
                callForgetPasswordApi()
            }
        }
    }

    private val forgetPasswordResponseObserver = Observer { apiResponse: ApiResponse ->
        consumeResponse(
            apiResponse,
            DynamicAPIPath.POST_FORGOT_PASSWORD
        )
    }

    private fun addObservers() {
        mForgetPasswordViewModel!!.response.observe(this, forgetPasswordResponseObserver)
    }

    override fun removeObservers() {
        super.removeObservers()
        mForgetPasswordViewModel!!.response.removeObserver(forgetPasswordResponseObserver)
    }

    private fun init() {
        mEventStartTimeStamp = AppUtils.getDateTime()
        setErrorMSG()
    }

    // set error if input field is blank
    private fun setErrorMSG() {
        setErrorMessage(
            mTextInputEmail,
            mEditTextEmail,
            getString(R.string.val_msg_please_enter_email_)
        )
        setErrorMessage(
            mTextInputUserId,
            mEditTextUserId,
            getString(R.string.val_msg_please_enter_user_id)
        )
    }

    /*check validations on field*/
    private fun isDetailsValid(): Boolean {
            if (TextUtils.isEmpty(mEditTextEmail.text.toString().trim())) {
                setError(mTextInputEmail, getString(R.string.val_msg_please_enter_email_))
                return false
            } else if (!isValidEmail(mEditTextEmail.text.toString())) {
                setError(mTextInputEmail, getString(R.string.val_msg_please_enter_valid_email))
                return false
            } else if (TextUtils.isEmpty(mEditTextUserId.text.toString().trim())) {
                setError(mTextInputUserId, getString(R.string.val_msg_please_enter_user_id))
                return false
            } else if (mEditTextUserId.text.toString()
                    .trim() != "lprscan_user" || mEditTextEmail.text.toString()
                    .trim() != "lprscan@gmail.com"
            ) {
                printToastMSGForErrorWarning(applicationContext, getString(R.string.err_msg_forget_password))
                return false
            }
            return true
        }

    /* Call Api For ForgetPassword */
    private fun callForgetPasswordApi() {
        if (isInternetAvailable(this@ForgetPasswordActivity)) {
            val mForgetPassRequest = ForgetPasswordRequest()
            mForgetPassRequest.mSiteOfficerEmail=
                mEditTextEmail.editableText.toString().trim()
            mForgetPassRequest.siteOfficerUserName =
                mEditTextUserId.editableText.toString().trim()
            mForgetPasswordViewModel!!.hitForgetPasswordApi(mForgetPassRequest)
        } else {
            printToastMSGForErrorWarning(
                applicationContext,
                getString(R.string.err_msg_connection_was_refused)
            )
        }
    }

    /*Api response */
    private fun consumeResponse(apiResponse: ApiResponse, tag: String) {
        when (apiResponse.status) {
            Status.LOADING -> showProgressLoader(getString(R.string.scr_message_please_wait))
            Status.SUCCESS -> {
                dismissLoader()
                if (!apiResponse.data!!.isNull) {
                    printLog(tag, apiResponse.data.toString())
                    if (tag.equals(DynamicAPIPath.POST_FORGOT_PASSWORD, ignoreCase = true)) {

                        val responseModel = ObjectMapperProvider.fromJson(apiResponse.data.toString(), CommonLoginResponse::class.java)

                        if (responseModel != null && responseModel.status!!) {
                            printToastMSG(
                                this,
                                getString(R.string.success_msg_pass_will_be_emailed)
                            )
                            launchScreen(this, LoginActivity::class.java)
                            //user event logging
                            //callPushEventLogin(Constants.FROM_SCREEN_FORGET_PASS,mEventStartTimeStamp);
                            finish()
                        } else if (responseModel != null && !responseModel.status!!) {
                            val message: String
                            if (responseModel.response != null && responseModel.response != "") {
                                message = responseModel.response.nullSafety()
                                showCustomAlertDialog(
                                    mContext, "POST_FORGOT_PASSWORD",
                                    message, "Ok", "Cancel", this
                                )
                            } else {
                                responseModel.response = "Not getting response from server..!!"
                                message = responseModel.response.nullSafety()
                                showCustomAlertDialog(
                                    mContext, "POST_FORGOT_PASSWORD",
                                    message, "Ok", "Cancel", this
                                )
                            }
                        } else {
                            showCustomAlertDialog(
                                mContext, "POST_FORGOT_PASSWORD",
                                "Something wen't wrong..!!", "Ok", "Cancel",
                                this
                            )
                            dismissLoader()

                            // LogUtil.printToastMSG(LprDetails2Activity.this, responseModel.getMessage());
                        }
                    }
                }
            }
            Status.ERROR -> {
                dismissLoader()
                printToastMSGForErrorWarning(
                    applicationContext,
                    getString(R.string.err_msg_connection_was_refused)
                )
            }

            else -> {}
        }
    }

    override fun onYesButtonClick() {}
    override fun onNoButtonClick() {}
    override fun onYesButtonClickParam(msg: String?) {}

    companion object {
        private val TAG = ForgetPasswordActivity::class.java.simpleName
    }
}