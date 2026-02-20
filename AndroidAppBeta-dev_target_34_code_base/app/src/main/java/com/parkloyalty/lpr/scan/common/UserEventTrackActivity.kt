package com.parkloyalty.lpr.scan.common

import android.os.Bundle
import androidx.activity.viewModels
import com.parkloyalty.lpr.scan.R
import com.parkloyalty.lpr.scan.basecontrol.BaseActivity
import com.parkloyalty.lpr.scan.common.model.PushEventRequest
import com.parkloyalty.lpr.scan.common.model.PushEventResponse
import com.parkloyalty.lpr.scan.common.model.PushEventViewModel
import com.parkloyalty.lpr.scan.extensions.nullSafety
import com.parkloyalty.lpr.scan.network.ApiResponse
import com.parkloyalty.lpr.scan.network.DynamicAPIPath
import com.parkloyalty.lpr.scan.network.NetworkCheck
import com.parkloyalty.lpr.scan.network.Status
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DropdownDatasetResponse
import com.parkloyalty.lpr.scan.util.LogUtil
import com.parkloyalty.lpr.scan.utils.ObjectMapperProvider
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class UserEventTrackActivity : BaseActivity() {
    private val mPushEventViewModel: PushEventViewModel? by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addObservers()
    }

    private fun addObservers() {
        mPushEventViewModel?.response?.observe(
            this
        ) { apiResponse: ApiResponse ->
            consumeResponse(
                apiResponse,
                DynamicAPIPath.POST_PUSH_EVENT
            )
        }
    }

    /* Call Api For Lpr scan details */
    fun callPushEventApiTest(mPushEventRequest: PushEventRequest?) {
        if (NetworkCheck.isInternetAvailable(this@UserEventTrackActivity)) {
            mPushEventViewModel?.hitPushEventApi(mPushEventRequest)
        } else {
            LogUtil.printToastMSGForErrorWarning(
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
                    LogUtil.printLog(tag, apiResponse.data.toString())
                    if (tag.equals(DynamicAPIPath.POST_PUSH_EVENT, ignoreCase = true)) {


                        val responseModel = ObjectMapperProvider.fromJson(apiResponse.data.toString(), PushEventResponse::class.java)

                        if (responseModel != null && responseModel.status.nullSafety()) {
                            LogUtil.printToastMSG(
                                applicationContext,
                                responseModel.message
                            )
                        } else {
                            LogUtil.printToastMSG(
                                applicationContext,
                                responseModel.message
                            )
                        }
                    }
                }
            }
            Status.ERROR -> {
                dismissLoader()
                LogUtil.printToastMSGForErrorWarning(
                    applicationContext,
                    getString(R.string.err_msg_connection_was_refused)
                )
            }
            else -> {
            }
        }
    }
}