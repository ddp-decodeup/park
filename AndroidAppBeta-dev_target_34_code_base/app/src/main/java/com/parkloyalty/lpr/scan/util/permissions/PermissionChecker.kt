package com.parkloyalty.lpr.scan.util.permissions

import android.content.Context
import android.content.pm.PackageManager

interface PermissionChecker {
    class Result(m: Map<String, State>) : Map<String, State> by HashMap(m) {
        operator fun component1(): Set<String> = granted()
        operator fun component2(): Set<String> = denied()

        private fun denied() = filterValues(State::isDenied).keys
        private fun granted() = filterValues(State::isGranted).keys
    }

    sealed interface State {
        val shouldShowRequestPermissionRationale: Boolean

        fun isGranted() = this is Granted
        fun isDenied() = this is Denied

        data object Granted : State {
            override val shouldShowRequestPermissionRationale: Boolean = false
        }

        data class Denied(
            override val shouldShowRequestPermissionRationale: Boolean
        ) : State
    }

    fun Context.checkSelfMultiplePermissions(permissions: Array<out String>): Boolean {
        return permissions.all {
            checkSelfPermission(it) == PackageManager.PERMISSION_GRANTED
        }
    }

    fun Context.checkSelfSinglePermission(permission: String): Boolean {
        return checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
    }
}