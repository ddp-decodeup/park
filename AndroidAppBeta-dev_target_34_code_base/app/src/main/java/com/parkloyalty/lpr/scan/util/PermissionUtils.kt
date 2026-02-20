package com.parkloyalty.lpr.scan.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat


object PermissionUtils {

    private const val REQUEST_CAMERA = 0
    private const val PERMISSION_REQUEST_CODE = 2

    var storage_permissions_login: Array<String> = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.BLUETOOTH_CONNECT,
        Manifest.permission.BLUETOOTH_SCAN,
        Manifest.permission.READ_PHONE_STATE
    )

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    var storage_permissions_login_33: Array<String> = arrayOf(
        Manifest.permission.READ_MEDIA_IMAGES,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.BLUETOOTH_CONNECT,
        Manifest.permission.BLUETOOTH_SCAN,
        Manifest.permission.READ_PHONE_STATE
    )

    @RequiresApi(api = Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    var storage_permissions_login_34: Array<String> = arrayOf(
//        Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED,
        Manifest.permission.POST_NOTIFICATIONS,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.BLUETOOTH_CONNECT,
        Manifest.permission.BLUETOOTH_SCAN,
        Manifest.permission.READ_PHONE_STATE
    )


    var camera_permissions: Array<String> = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE

    )

    @RequiresApi(api = Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    var camera_permissions_34: Array<String> = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.READ_MEDIA_IMAGES
    )

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    var camera_permissions_33: Array<String> = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.READ_MEDIA_IMAGES
    )

    fun cameraPermissions(): Array<String> {
        val p: Array<String> = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            camera_permissions_34
        }
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            camera_permissions_33
        }
        else {
            camera_permissions
        }
        return p
    }

    fun requestCameraAndStoragePermission(mContext : Activity) : Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE||
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (mContext.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                mContext.requestPermissions(
                    cameraPermissions(),
                    PERMISSION_REQUEST_CODE
                )
                return false
            } else {
                return true
            }
        }
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (mContext.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                mContext.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                mContext.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                mContext.requestPermissions(
                    cameraPermissions(),
                    PERMISSION_REQUEST_CODE
                )
                return false
            } else {
                return true
            }
        } else {
            return true
        }
    }

    fun storageLoginPermissions(): Array<String> {
        val p: Array<String> = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            camera_permissions_34
        }
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            camera_permissions_33
        }
        else {
            camera_permissions
        }
        return p
    }

    fun requestStorageLoginPermission(mContext : Activity) : Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE||
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (mContext.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                mContext.requestPermissions(
                    cameraPermissions(),
                    PERMISSION_REQUEST_CODE
                )
                return false
            } else {
                return true
            }
        }
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (mContext.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                mContext.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                mContext.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                mContext.requestPermissions(
                    cameraPermissions(),
                    PERMISSION_REQUEST_CODE
                )
                return false
            } else {
                return true
            }
        } else {
            return true
        }
    }

    fun isPermissionGranted(context: Context, permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            context, permission
        ) == PackageManager.PERMISSION_GRANTED
    }

}