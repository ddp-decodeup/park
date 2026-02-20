package com.parkloyalty.lpr.scan.util.permissions

import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions
import kotlinx.coroutines.channels.Channel

class RequestMultiplePermissions(componentActivity: ComponentActivity) : PermissionChecker {
    private val activityResultLauncher = with(componentActivity) {
        registerForActivityResult(RequestMultiplePermissions()) { result ->
            val m = result.mapValues { (key, value) ->
                if (value) {
                    PermissionChecker.State.Granted
                } else {
                    PermissionChecker.State.Denied(shouldShowRequestPermissionRationale(key))
                }
            }

            channel.trySend(PermissionChecker.Result(m))
        }
    }

    private val channel = Channel<PermissionChecker.Result>(1)

    suspend fun request(permissions: Set<String>): PermissionChecker.Result {
        activityResultLauncher.launch(permissions.toTypedArray())

        return channel.receive()
    }
}

//package com.parkloyalty.lpr.scan.util.permissions
//
//import android.content.Intent
//import android.net.Uri
//import android.os.Build
//import android.provider.Settings
//import androidx.activity.ComponentActivity
//import androidx.activity.result.ActivityResultLauncher
//import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions
//import kotlinx.coroutines.suspendCancellableCoroutine
//import kotlin.coroutines.resume
//import kotlin.coroutines.resumeWithException
//
//// Adjust these sealed types to fit your PermissionChecker definitions
//sealed class PermissionState {
//    object Granted : PermissionState()
//    data class Denied(val canShowRationale: Boolean) : PermissionState()
//    object PermanentlyDenied : PermissionState()
//}
//
//data class PermissionResult(val map: Map<String, PermissionState>)
//
//class RequestMultiplePermissions(private val activity: ComponentActivity) : PermissionChecker {
//    // single launcher used to start request; but we will handle per-request continuation separately
//    private val launcher: ActivityResultLauncher<Array<String>>
//
//    // Holder for the current continuation; only one outstanding request supported by this impl.
//    // If you need concurrency, convert to a queue or generate per-request launcher keyed by lifecycle owner.
//    @Volatile
//    private var currentContinuation: ( (PermissionResult) -> Unit)? = null
//
//    init {
//        launcher = activity.registerForActivityResult(RequestMultiplePermissions()) { result ->
//            val cont = currentContinuation ?: return@registerForActivityResult
//            // Map booleans to PermissionState
//            val mapped = result.mapValues { (perm, granted) ->
//                if (granted) {
//                    PermissionState.Granted
//                } else {
//                    val canShow = activity.shouldShowRequestPermissionRationale(perm)
//                    // if denied and cannot show rationale -> treat as permanently denied
//                    if (!canShow) PermissionState.PermanentlyDenied else PermissionState.Denied(canShow)
//                }
//            }
//            // clear continuation before calling to avoid re-entrancy issues
//            currentContinuation = null
//            cont(PermissionResult(mapped))
//        }
//    }
//
//    /**
//     * Requests runtime permissions. This function is cancellable and supports only one concurrent
//     * request at a time. If you need multiple concurrent requests, adapt to queue or give each request
//     * an id and unique continuation.
//     */
//    suspend fun request(permissions: Set<String>): PermissionResult =
//        suspendCancellableCoroutine { cont ->
//            // special-case: check for known special permissions and treat them separately
//            val special = permissions.filter { isSpecialPermission(it) }
//            if (special.isNotEmpty()) {
//                // If the caller included special permissions, you probably want to handle
//                // them with Settings intents. We'll immediately return a map indicating
//                // those are "PermanentlyDenied" (or not-requestable) so caller can open settings.
//                val map = permissions.associateWith { perm ->
//                    if (isSpecialPermission(perm)) PermissionState.PermanentlyDenied
//                    else PermissionState.Denied(activity.shouldShowRequestPermissionRationale(perm))
//                }
//                cont.resume(PermissionResult(map))
//                return@suspendCancellableCoroutine
//            }
//
//            // ensure no concurrent request
//            if (currentContinuation != null) {
//                cont.resumeWithException(IllegalStateException("Another permission request is in progress"))
//                return@suspendCancellableCoroutine
//            }
//
//            currentContinuation = { result ->
//                if (!cont.isCompleted) cont.resume(result)
//            }
//
//            // if coroutine is cancelled, clear continuation to avoid memory leaks
//            cont.invokeOnCancellation {
//                currentContinuation = null
//            }
//
//            // launch actual request
//            launcher.launch(permissions.toTypedArray())
//        }
//
//    private fun isSpecialPermission(permission: String): Boolean {
//        // Expand this as needed
//        return when (permission) {
//            android.Manifest.permission.MANAGE_EXTERNAL_STORAGE -> true
//            android.Manifest.permission.SYSTEM_ALERT_WINDOW -> true
//            android.Manifest.permission.WRITE_SETTINGS -> true
//            // background location is tricky: treat ACCESS_BACKGROUND_LOCATION as special in many flows
//            android.Manifest.permission.ACCESS_BACKGROUND_LOCATION -> true
//            else -> false
//        }
//    }
//
//    /**
//     * Optional helper to open app settings so the user can grant special/permanently-denied perms.
//     */
//    fun openAppSettings() {
//        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
//        intent.data = Uri.fromParts("package", activity.packageName, null)
//        activity.startActivity(intent)
//    }
//}
