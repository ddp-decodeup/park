package com.parkloyalty.lpr.scan.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.parkloyalty.lpr.scan.R
import com.parkloyalty.lpr.scan.extensions.nullSafety
import com.parkloyalty.lpr.scan.interfaces.Constants
import com.parkloyalty.lpr.scan.interfaces.SharedPrefKey
import com.parkloyalty.lpr.scan.ui.login.LoginActivity
import com.parkloyalty.lpr.scan.ui.login.activity.WelcomeActivity
import com.parkloyalty.lpr.scan.util.SharedPref
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.*

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    @Inject
    lateinit var sharedPreference: SharedPref
    private val ioScope = CoroutineScope(
        Job() + Dispatchers.IO
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        setTimeStamp()
    }

    /*wait for few second than launch new screen*/
    private fun setTimeStamp() {
        ioScope.launch {
            delay(Constants.SPLAHS_TIME_OUT.toLong())
            try {
                //get login status
                val iSLoggedIn = sharedPreference.read(SharedPrefKey.IS_LOGGED_IN, false)
                if (iSLoggedIn.nullSafety()) {
                    launchScreen(WelcomeActivity::class.java)
                    //launchScreen(BootActivity.class);
                    finish()
                } else {
                    launchScreen(LoginActivity::class.java)
                    //launchScreen(BootActivity.class);
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /*navigate to new screen*/
    private fun launchScreen(activity: Class<*>) {
        startActivity(Intent(this@SplashActivity, activity))
        finish()
    }

    override fun onDestroy() {
        ioScope.cancel()
        super.onDestroy()
    }
}