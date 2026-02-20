package com.parkloyalty.lpr.scan.utils.permissions
// PermissionManager.kt

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject

class PermissionManager private constructor(
    private val caller: ActivityResultCaller,
    private val context: Context,
    private val activity: ComponentActivity?,
    private val fragment: Fragment?,
    val dialogPresenter: DialogPresenter
) {
    private val _events = MutableSharedFlow<PermissionEvent>(replay = 0)
    val events = _events.asSharedFlow()

    private val launcher: ActivityResultLauncher<Array<String>>
    private var pending: CompletableDeferred<PermissionResult>? = null

    init {
        launcher = caller.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            val granted = result.filterValues { it }.keys.toList()
            val denied = result.filterValues { !it }.keys.toList()
            val permanentlyDenied = denied.filter { perm -> !shouldShowRationaleFor(perm) }
            val finalDenied = denied.filter { it !in permanentlyDenied }
            val res = PermissionResult(granted = granted, denied = finalDenied, permanentlyDenied = permanentlyDenied)
            pending?.complete(res)
            pending = null
        }
    }

    class Factory @Inject constructor(private val dialogPresenter: DialogPresenter) {
        fun create(caller: ActivityResultCaller, context: Context, activity: ComponentActivity? = null, fragment: Fragment? = null): PermissionManager {
            return PermissionManager(caller, context, activity, fragment, dialogPresenter)
        }
    }

    data class PermissionResult(val granted: List<String>, val denied: List<String>, val permanentlyDenied: List<String>)
    sealed class PermissionEvent {
        data class Starting(val permissions: List<String>) : PermissionEvent()
        data class RationaleShown(val permissions: List<String>) : PermissionEvent()
        data class OpenedSettings(val reason: String) : PermissionEvent()
        data class Finished(val result: PermissionResult) : PermissionEvent()
    }

    companion object {
        const val MANAGE_EXTERNAL_STORAGE = "android:manage_external_storage"
    }

    suspend fun requestPermissions(permissions: Array<String>): PermissionResult {
        _events.tryEmit(PermissionEvent.Starting(permissions.toList()))

        if (permissions.contains(MANAGE_EXTERNAL_STORAGE) && Build.VERSION.SDK_INT >= 30) {
            if (Environment.isExternalStorageManager()) {
                val remaining = permissions.filter { it != MANAGE_EXTERNAL_STORAGE }.toTypedArray()
                if (remaining.isEmpty()) {
                    val res = PermissionResult(listOf(MANAGE_EXTERNAL_STORAGE), emptyList(), emptyList())
                    _events.tryEmit(PermissionEvent.Finished(res))
                    return res
                } else {
                    return requestWithStaging(remaining, preGranted = listOf(MANAGE_EXTERNAL_STORAGE))
                }
            } else {
                val open = dialogPresenter.showAllFilesAccessDialogSuspend(context)
                if (open) {
                    try {
                        val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                        intent.data = Uri.fromParts("package", context.packageName, null)
                        startActivityForSettings(intent)
                        _events.tryEmit(PermissionEvent.OpenedSettings("All files access"))
                    } catch (e: Exception) {
                        openAppSettings("All files access fallback")
                    }
                }
                val res = PermissionResult(emptyList(), permissions.toList(), emptyList())
                _events.tryEmit(PermissionEvent.Finished(res))
                return res
            }
        }

        return requestWithStaging(permissions, preGranted = emptyList())
    }

    private suspend fun requestWithStaging(permissions: Array<String>, preGranted: List<String>): PermissionResult {
        val bg = android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
        val hasBg = permissions.contains(bg)
        val hasForeground = permissions.any { it == android.Manifest.permission.ACCESS_FINE_LOCATION || it == android.Manifest.permission.ACCESS_COARSE_LOCATION }

        if (hasBg && !hasForeground) {
            val fg = arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION)
            val fgRes = requestDirect(fg)
            if (fgRes.granted.isNotEmpty()) {
                val bgRes = requestDirect(arrayOf(bg))
                val combined = combine(preGranted, fgRes, bgRes)
                _events.tryEmit(PermissionEvent.Finished(combined))
                return combined
            } else {
                val combined = combine(preGranted, fgRes)
                _events.tryEmit(PermissionEvent.Finished(combined))
                return combined
            }
        } else {
            val res = requestDirect(permissions)
            val combined = combine(preGranted, res)
            _events.tryEmit(PermissionEvent.Finished(combined))
            return combined
        }
    }

    private fun combine(preGranted: List<String>, vararg results: PermissionResult): PermissionResult {
        val granted = mutableListOf<String>(); val denied = mutableListOf<String>(); val permDenied = mutableListOf<String>()
        granted += preGranted
        results.forEach { r ->
            granted += r.granted
            denied += r.denied
            permDenied += r.permanentlyDenied
        }
        val g = granted.distinct()
        val d = denied.distinct().filter { it !in g }
        val p = permDenied.distinct().filter { it !in g }
        return PermissionResult(g, d, p)
    }

    private suspend fun requestDirect(permissions: Array<String>): PermissionResult {
        if (permissions.isEmpty()) return PermissionResult(emptyList(), emptyList(), emptyList())
        val def = CompletableDeferred<PermissionResult>().also { pending = it }
        launcher.launch(permissions)
        try { return def.await() } finally { pending = null }
    }

    fun shouldShowRationaleFor(permission: String): Boolean {
        return try {
            when {
                fragment != null -> fragment.shouldShowRequestPermissionRationale(permission)
                activity != null -> activity.shouldShowRequestPermissionRationale(permission)
                else -> false
            }
        } catch (e: Exception) { false }
    }

    private fun startActivityForSettings(intent: Intent) {
        when {
            activity != null -> activity.startActivity(intent)
            fragment != null -> fragment.startActivity(intent)
            else -> {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            }
        }
    }

    fun openAppSettings(reason: String = "Open app settings (permission)") {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", context.packageName, null))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivityForSettings(intent)
        _events.tryEmit(PermissionEvent.OpenedSettings(reason))
    }

    fun isPermissionGranted(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(context, permission) == android.content.pm.PackageManager.PERMISSION_GRANTED
    }

    // -----------------------------
    // NEW: Generic helper for any permissions
    // -----------------------------
    /**
     * Generic helper: ensure requested [permissions] are granted and then run [onGranted].
     *
     * - [rationaleMessage] optional message shown in rationale dialog when non-permanent denials occur.
     * - Handles background-location staging & MANAGE_EXTERNAL_STORAGE special case.
     *
     * Usage:
     * permissionManager.ensurePermissionsThen(arrayOf(Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION), "We need camera+location") {
     *     // open camera or perform action
     * }
     */
    suspend fun ensurePermissionsThen(
        permissions: Array<String>,
        rationaleMessage: String? = null,
        onGranted: suspend () -> Unit
    ) {
        // 1) Special: MANAGE_EXTERNAL_STORAGE on API30+
        if (permissions.contains(MANAGE_EXTERNAL_STORAGE) && Build.VERSION.SDK_INT >= 30) {
            if (Environment.isExternalStorageManager()) {
                // continue with rest
                val remaining = permissions.filter { it != MANAGE_EXTERNAL_STORAGE }.toTypedArray()
                if (remaining.isEmpty()) {
                    onGranted(); return
                } else {
                    ensurePermissionsThen(remaining, rationaleMessage, onGranted); return
                }
            } else {
                val open = dialogPresenter.showAllFilesAccessDialogSuspend(context)
                if (open) {
                    try {
                        val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                        intent.data = Uri.fromParts("package", context.packageName, null)
                        startActivityForSettings(intent)
                        _events.tryEmit(PermissionEvent.OpenedSettings("All files access"))
                    } catch (e: Exception) {
                        openAppSettings("All files access fallback")
                    }
                }
                // Caller should re-invoke after user returns from settings.
                return
            }
        }

        // 2) Quick pre-check: are all already granted?
        val notGranted = permissions.filter { !isPermissionGranted(it) }
        if (notGranted.isEmpty()) {
            onGranted(); return
        }

        // 3) Request them
        val result = requestPermissions(permissions)

        // If everything granted -> run onGranted
        val allGrantedNow = permissions.all { it in result.granted || isPermissionGranted(it) }
        if (allGrantedNow) {
            onGranted(); return
        }

        // 4) If there are non-permanent denied -> show rationale (suspend) and retry once
        if (result.denied.isNotEmpty()) {
            val proceed = dialogPresenter.showRationaleDialogSuspend(context, result.denied, rationaleMessage)
            if (proceed) {
                val retry = requestPermissions(result.denied.toTypedArray())
                val allGrantedRetry = permissions.all { it in retry.granted || isPermissionGranted(it) }
                if (allGrantedRetry) {
                    onGranted(); return
                }
            } else {
                return
            }
        }

        // 5) If any permanently denied -> ask to open settings (suspend) and open settings if user agrees
        if (result.permanentlyDenied.isNotEmpty()) {
            val openSettings = dialogPresenter.showRationaleDialogSuspend(context, result.permanentlyDenied, "These are permanently denied. Open settings?")
            if (openSettings) {
                openAppSettings("User opened settings for permanently denied permissions")
            }
            return
        }

        // If we reach here nothing more to do; return without calling onGranted
    }
}
