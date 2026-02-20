package com.parkloyalty.lpr.scan.extensions

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.Intent
import android.os.Bundle
import android.content.res.ColorStateList
import android.graphics.Color
import android.location.LocationManager
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputLayout
import com.parkloyalty.lpr.scan.R

//Hide Soft Keyboard from any context
fun Activity.hideSoftKeyboard() {
    val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as? InputMethodManager
    val windowToken = currentFocus?.windowToken ?: window.decorView.windowToken
    imm?.hideSoftInputFromWindow(windowToken, 0)
}

@SuppressLint("HardwareIds")
fun Context.getAndroidID(): String {
    return Settings.Secure.getString(
        this.contentResolver,
        Settings.Secure.ANDROID_ID
    ) ?: ""
}

//Add function to get version name from Context
fun Context.getAppVersionName(): String {
    return try {
        val packageInfo = this.packageManager.getPackageInfo(this.packageName, 0)
        packageInfo.versionName ?: "NA"
    } catch (e: Exception) {
        "NA"
    }
}

//Add Internet check functionality to Context
fun Context.isInternetAvailable(): Boolean {
    val connectivity = getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
    if (connectivity != null) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivity.activeNetwork ?: return false
            val capabilities = connectivity.getNetworkCapabilities(network) ?: return false
            return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                    capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        } else {
            @Suppress("DEPRECATION")
            val networkInfo = connectivity.activeNetworkInfo
            return networkInfo != null && networkInfo.isConnected
        }
    }
    return false
}

//check if gps is enabled
fun Context.isGPSEnabled(): Boolean {
    val locationManager = this.getSystemService(LOCATION_SERVICE) as LocationManager
    var gpsEnabled = false
    try {
        gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return gpsEnabled
}

fun Context.activateSunLightMode(
    mTextInputLayout: TextInputLayout?,
    mTextInputEditText: AppCompatEditText?,
    mAppCompatAutoCompleteTextView: AppCompatAutoCompleteTextView?
) {
    if (mAppCompatAutoCompleteTextView != null) {
        mAppCompatAutoCompleteTextView.setBackgroundResource(R.drawable.round_corner_shap_sunlight)
//            mAppCompatAutoCompleteTextView.setBackgroundColor(
//                ContextCompat.getColor(
//                    mContext,
//                    R.color._013220
//                )
//            )
        mAppCompatAutoCompleteTextView.setTextColor(
            ContextCompat.getColor(
                this,
                R.color.white
            )
        )
        mAppCompatAutoCompleteTextView.setHintTextColor(
            ContextCompat.getColor(
                this,
                R.color.white
            )
        )
    }
    if (mTextInputEditText != null) {
        mTextInputEditText.setBackgroundResource(R.drawable.round_corner_shap_sunlight)
        mTextInputEditText.setTextColor(
            ContextCompat.getColor(
                this,
                R.color.white
            )
        )
        mTextInputEditText.setHintTextColor(
            ContextCompat.getColor(
                this,
                R.color.white
            )
        )
    }
    mTextInputLayout!!.defaultHintTextColor = ColorStateList.valueOf(
        ContextCompat.getColor(
            this,
            R.color.deep_yellow
        )
    )
//        mTextInputLayout!!.stysetTextA(@style/Widget.MaterialComponents.TextInputLayout.FilledBox.Dense)
    mTextInputLayout!!.setStartIconTintList(ColorStateList.valueOf(Color.BLUE))
    mTextInputLayout!!.setEndIconTintList(ColorStateList.valueOf(Color.WHITE))
}

fun Context.activateMoonLightMode(
    mTextInputLayout: TextInputLayout?,
    mTextInputEditText: AppCompatEditText?,
    mAppCompatAutoCompleteTextView: AppCompatAutoCompleteTextView?
) {
    if (mAppCompatAutoCompleteTextView != null) {
        mAppCompatAutoCompleteTextView.setBackgroundColor(
            ContextCompat.getColor(
                this,
                R.color.white
            )
        )
//            mAppCompatAutoCompleteTextView.setBackgroundColor(
//                ContextCompat.getColor(
//                    mContext,
//                    R.color._013220
//                )
//            )
        mAppCompatAutoCompleteTextView.setTextColor(
            ContextCompat.getColor(
                this,
                R.color.white
            )
        )
        mAppCompatAutoCompleteTextView.setHintTextColor(
            ContextCompat.getColor(
                this,
                R.color.white
            )
        )
    }
    if (mTextInputEditText != null) {
        mTextInputEditText.setBackgroundColor(
            ContextCompat.getColor(
                this,
                R.color.white
            )
        )
        mTextInputEditText.setTextColor(
            ContextCompat.getColor(
                this,
                R.color.white
            )
        )
        mTextInputEditText.setHintTextColor(
            ContextCompat.getColor(
                this,
                R.color.white
            )
        )
    }
    mTextInputLayout!!.defaultHintTextColor = ColorStateList.valueOf(Color.BLACK)
    mTextInputLayout!!.setStartIconTintList(ColorStateList.valueOf(Color.BLUE))
    mTextInputLayout!!.setEndIconTintList(ColorStateList.valueOf(Color.WHITE))
}


/**
 * Start the given Activity and finish the current task (affinity) if the caller is an Activity.
 *
 * Usage:
 *   launchScreen(this@MainActivity, MainActivity::class.java)
 */
fun launchActivityScreen(
    context: Context,
    targetActivity: Class<*>,
    extras: Bundle? = null,
    addNewTaskIfNeeded: Boolean = true,
    finishAffinityOnCaller: Boolean = true
) {
    val intent = Intent(context, targetActivity)
    extras?.let { intent.putExtras(it) }

    if (addNewTaskIfNeeded && context !is Activity) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    } else {
        // ensure a fresh start when calling from an Activity
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
    }

    context.startActivity(intent)

    if (finishAffinityOnCaller && context is Activity) {
        context.finishAffinity()
    }
}

