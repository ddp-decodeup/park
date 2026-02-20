package com.parkloyalty.lpr.scan.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.os.PowerManager
import android.provider.Settings
import androidx.core.net.toUri
import com.parkloyalty.lpr.scan.R
import com.parkloyalty.lpr.scan.interfaces.CustomDialogHelper
import com.parkloyalty.lpr.scan.interfaces.SharedPrefKey
import com.parkloyalty.lpr.scan.util.AppUtils.showCustomAlertDialogOnce

object SystemUtils {

    /**
     * Function used to remove app from Battery Optimization
     */
    fun whitelistAppFromBatteryOptimization(context: Context, sharedPreference: SharedPref) {
        try {
            if (!isAppExcludedFromBatteryOptimization(context)) {
                //Old Code Working
                //Don't need manifest permission for this code
//                val intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
//                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//                context.startActivity(intent)

                //New Implementation
                requestIgnoreBatteryOptimization(context, sharedPreference)
            } else {
                LogUtil.printLog("Battery", "Already ignoring battery optimizations")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            sharedPreference.write(SharedPrefKey.SHOW_BATTERY_OPTIMISATION_DIALOG, false)
            //Toast.makeText(context, "Failed to open battery settings", Toast.LENGTH_SHORT).show()
        }
    }

    fun requestIgnoreBatteryOptimization(context: Context, sharedPreference: SharedPref) {
        val packageName = context.packageName

        try {
            showCustomAlertDialogOnce(
                context,
                context.getString(R.string.title_battery_optimisation),
                context.getString(
                    R.string.desc_battery_optimisation,
                    context.resources.getString(R.string.app_name)
                ),
                context.getString(R.string.dialog_button_yes),
                context.getString(R.string.dialog_button_i_will_do_this_later),
                object : CustomDialogHelper {
                    override fun onYesButtonClick() {
                        //No Implementation Required
                    }

                    override fun onNoButtonClick() {
                        sharedPreference.write(
                            SharedPrefKey.SHOW_BATTERY_OPTIMISATION_DIALOG,
                            false
                        )
                    }

                    override fun onYesButtonClickParam(msg: String?) {
//                        val intent =
//                            Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
//                                data = "package:$packageName".toUri()
//                                flags = Intent.FLAG_ACTIVITY_NEW_TASK
//                            }
//                        context.startActivity(intent)

                        //Below statement is to show the battery optimization dialog only once
                        sharedPreference.write(SharedPrefKey.SHOW_BATTERY_OPTIMISATION_DIALOG, false)

                        Handler(Looper.getMainLooper()).post {
                            try {
                                val intent =
                                    Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                                        data = "package:$packageName".toUri()
                                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                    }
                                context.startActivity(intent)
                            } catch (e: ActivityNotFoundException) {
                                LogUtil.printLog("BatteryOpt", "Intent failed to launch", e)

                                sharedPreference.write(SharedPrefKey.SHOW_BATTERY_OPTIMISATION_DIALOG, false)
                            }
                        }
                    }
                })

        } catch (e: Exception) {
            // Samsung or other OEMs might restrict this, fallback to app settings
            showCustomAlertDialogOnce(
                context,
                context.getString(R.string.title_battery_optimisation),
                context.getString(
                    R.string.desc_battery_optimisation_fallback,
                    context.resources.getString(R.string.app_name)
                ),
                context.getString(R.string.dialog_button_yes),
                context.getString(R.string.dialog_button_i_will_do_this_later),
                object : CustomDialogHelper {
                    override fun onYesButtonClick() {
                        //No Implementation Required
                    }

                    override fun onNoButtonClick() {
                        sharedPreference.write(
                            SharedPrefKey.SHOW_BATTERY_OPTIMISATION_DIALOG,
                            false
                        )
                    }

                    override fun onYesButtonClickParam(msg: String?) {
                        // This fallback works
//                        val fallbackIntent =
//                            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
//                                data = "package:$packageName".toUri()
//                                flags = Intent.FLAG_ACTIVITY_NEW_TASK
//                            }
//                        context.startActivity(fallbackIntent)

                        //Below statement is to show the battery optimization dialog only once
                        sharedPreference.write(SharedPrefKey.SHOW_BATTERY_OPTIMISATION_DIALOG, false)

                        Handler(Looper.getMainLooper()).post {
                            try {
                                val fallbackIntent =
                                    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                        data = "package:$packageName".toUri()
                                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                    }
                                context.startActivity(fallbackIntent)
                            } catch (e: ActivityNotFoundException) {
                                LogUtil.printLog("BatteryOpt", "Intent failed to launch", e)

                                sharedPreference.write(SharedPrefKey.SHOW_BATTERY_OPTIMISATION_DIALOG, false)
                            }
                        }
                    }
                })
        }
    }

    /**
     * Function to check if app is excluded from battery optimisation or not
     */
    fun isAppExcludedFromBatteryOptimization(context: Context): Boolean {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val packageName = context.packageName

        return powerManager.isIgnoringBatteryOptimizations(packageName)
    }
}