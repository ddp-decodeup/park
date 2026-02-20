package com.parkloyalty.lpr.scan.util


sealed class PermissionTypes(val permissionTypeValue: String) {

    object CameraPermission : PermissionTypes("android.permission.CAMERA")

    sealed class Location(value: String) : PermissionTypes(value) {
        object FineLocationPermission : Location("android.permission.ACCESS_FINE_LOCATION")
        object CoarseLocationPermission : Location("android.permission.ACCESS_COARSE_LOCATION")
    }

    sealed class Storage(value: String) : PermissionTypes(value) {
        object ManageExternalStoragePermission : Storage("android.permission.MANAGE_EXTERNAL_STORAGE")
        object ReadExternalPermission : Storage("android.permission.READ_EXTERNAL_STORAGE")
        object WriteExternalPermission : Storage("android.permission.WRITE_EXTERNAL_STORAGE")
    }

}
