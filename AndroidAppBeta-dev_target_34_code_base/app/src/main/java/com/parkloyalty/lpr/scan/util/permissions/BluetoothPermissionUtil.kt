package com.parkloyalty.lpr.scan.util.permissions

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.parkloyalty.lpr.scan.R
import com.parkloyalty.lpr.scan.interfaces.CustomDialogHelper
import com.parkloyalty.lpr.scan.util.AppUtils.showCustomAlertDialog

object BluetoothPermissionUtil {

    private const val REQUEST_ENABLE_BT = 1001

    // Permissions required for Android 13+
    @RequiresApi(Build.VERSION_CODES.S)
    private val requiredPermissions = arrayOf(
        Manifest.permission.BLUETOOTH_CONNECT,
        Manifest.permission.BLUETOOTH_SCAN
    )

    /**
     * Check and request Bluetooth permissions and enablement.
     *
     * @param activity The current Activity
     * @param permissionLauncher The launcher to request permissions
     * @param onAllGranted Callback when all permissions are granted and Bluetooth is on
     */
    fun checkBluetoothPermissions(
        activity: Activity,
        permissionLauncher: ActivityResultLauncher<Array<String>>,
        onAllGranted: () -> Unit
    ) {
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        // A.) Check if Bluetooth is supported
        if (bluetoothAdapter == null) {
            // Device doesn't support Bluetooth
            return
        }

        // A.) Check if Bluetooth is enabled
        if (!bluetoothAdapter.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
            return
        }

        // B.) Check permissions (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val notGranted = requiredPermissions.filter {
                ContextCompat.checkSelfPermission(activity, it) != PackageManager.PERMISSION_GRANTED
            }

            if (notGranted.isEmpty()) {
                // C.) All permissions are granted
                onAllGranted()
            } else {
                // C.) Request missing permissions
                permissionLauncher.launch(requiredPermissions)
            }
        } else {
            // No need to request Bluetooth permissions below API 33
            onAllGranted()
        }
    }

    /**
     * Optional helper: Call this inside onRequestPermissionsResult or permissionLauncher callback
     */
    fun arePermissionsGranted(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true
        return requiredPermissions.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    fun hasBluetoothPermissions(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val scanGranted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_SCAN
            ) == PackageManager.PERMISSION_GRANTED

            val connectGranted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_CONNECT
            ) == PackageManager.PERMISSION_GRANTED

            scanGranted && connectGranted
        } else {
            true
        }
    }

    fun requestBluetoothSetting(context: Context) {
        val packageName = context.packageName

        try {
            showCustomAlertDialog(
                context,
                context.getString(R.string.title_bluetooth_settings),
                context.getString(
                    R.string.desc_bluetooth_settings,
                    context.resources.getString(R.string.app_name)
                ),
                context.getString(R.string.dialog_button_yes),
                context.getString(R.string.dialog_button_i_will_do_this_later),
                object : CustomDialogHelper {
                    override fun onYesButtonClick() {
                        //No Implementation Required
                    }

                    override fun onNoButtonClick() {
                        //No Implementation Required
                    }

                    override fun onYesButtonClickParam(msg: String?) {
                        val intent =
                            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                data = "package:$packageName".toUri()
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            }
                        context.startActivity(intent)
                    }
                })
        } catch (e: Exception) {
            e.printStackTrace()
            //No Implementation Required
        }
    }
}
