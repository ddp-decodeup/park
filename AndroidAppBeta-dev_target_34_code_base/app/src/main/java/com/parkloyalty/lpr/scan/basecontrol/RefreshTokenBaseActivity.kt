package com.parkloyalty.lpr.scan.basecontrol

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.parkloyalty.lpr.scan.R
import com.parkloyalty.lpr.scan.common.model.PushEventViewModel
import com.parkloyalty.lpr.scan.database.AppDatabase
import com.parkloyalty.lpr.scan.extensions.nullSafety
import com.parkloyalty.lpr.scan.interfaces.SharedPrefKey
import com.parkloyalty.lpr.scan.network.*
import com.parkloyalty.lpr.scan.ui.boot.model.ResponseBoot
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.AuthRefreshResponse
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.AuthTokenRefreshViewModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DropdownDatasetResponse
import com.parkloyalty.lpr.scan.ui.login.activity.WelcomeActivity
import com.parkloyalty.lpr.scan.util.AppUtils
import com.parkloyalty.lpr.scan.util.LogUtil
import com.parkloyalty.lpr.scan.util.SharedPref
import com.parkloyalty.lpr.scan.utils.ObjectMapperProvider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.cancel
import javax.inject.Inject
import kotlin.getValue

@AndroidEntryPoint
class RefreshTokenBaseActivity : BaseActivity(){

    private var mDb: AppDatabase? = null
    private var mContext: Context? = null
    private val mAuthTokenRefreshViewModel: AuthTokenRefreshViewModel? by viewModels()
    private var apiResultTag = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContext = this
        mDb = BaseApplication.instance?.getAppDatabase()
        addObservers()
        callRefreshTokenAPI()
    }

    private fun addObservers() {
        mAuthTokenRefreshViewModel?.response?.observe(this, RefreshTokenResponseObserver)
    }


    override fun onDestroy() {
        mAuthTokenRefreshViewModel?.response?.removeObserver(RefreshTokenResponseObserver)
        super.onDestroy()
    }


    private val RefreshTokenResponseObserver = Observer { apiResponse: ApiResponse? ->
        consumeResponse(
            apiResponse,
            DynamicAPIPath.GET_REFRESH_AUTH_TOKEN
        )
    }

    fun callRefreshTokenAPI()
    {
        mAuthTokenRefreshViewModel!!.hitAuthTokenRefreshApi()
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
                        if (tag.equals(DynamicAPIPath.GET_REFRESH_AUTH_TOKEN, ignoreCase = true)) {
                            try {

                                val responseModel = ObjectMapperProvider.fromJson(apiResponse.data.toString(), AuthRefreshResponse::class.java)

                                dismissLoader()
                                if (responseModel != null) {
                                    sharedPreference.write(
                                        SharedPrefKey.ACCESS_TOKEN,
                                        responseModel!!.response.nullSafety())
                                    Handler(Looper.getMainLooper()).postDelayed({
                                        dismissLoader()
                                        sharedPreference.write(SharedPrefKey.IS_LOGIN_ACTIVITY_LOGER_API, true)
                                        launchScreenLogin(mContext, WelcomeActivity::class.java)
                                    }, 300)
                                }
                            } catch (e: Exception) {
                                dismissLoader()
                                e.printStackTrace()
                                launchScreenLogin(mContext, WelcomeActivity::class.java)
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        launchScreenLogin(mContext, WelcomeActivity::class.java)
//                        apiResultTag = "fail"
//                        AppUtils.showCustomAlertDialog(
//                            mContext,
//                            "Boot",
//                            getString(R.string.err_msg_something_went_wrong),
//                            getString(R.string.alt_lbl_OK),
//                            getString(R.string.scr_btn_cancel),
//                            this
//                        )
                    }
                }
            }
            Status.ERROR -> {
                dismissLoader()
                apiResultTag = "fail"
                AppUtils.showCustomAlertDialog(
                    mContext,
                    "Boot",
                    getString(R.string.err_msg_something_went_wrong),
                    getString(R.string.alt_lbl_OK),
                    getString(R.string.scr_btn_cancel),
                    this
                )
            }
            else -> {
            }
        }
    }

}