package com.parkloyalty.lpr.scan.utils.permissions

import android.content.Context
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.parkloyalty.lpr.scan.R
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

/**
 * DialogPresenter: small UI-only helper that shows suspend dialogs.
 * - showRationaleDialogSuspend -> returns true if user chose "Allow" (i.e., proceed)
 * - showAllFilesAccessDialogSuspend -> returns true if user chose "Open settings"
 *
 * NOTE: Context must be a valid UI context (Activity or themed Context). Prefer Activity or Fragment.requireContext().
 */
class DialogPresenter @Inject constructor() {

    suspend fun showRationaleDialogSuspend(context: Context, permissions: List<String>, messageExtra: String? = null): Boolean {
        val title = context.getString(R.string.permission_title_permissions_required)
        val message = buildString {
            append(context.getString(R.string.permission_message_this_app_needs_the_following_permissions))
            permissions.forEach { append("\n• $it") }
            if (!messageExtra.isNullOrBlank()) {
                append("\n\n")
                append(messageExtra)
            }
        }
        return showYesNoDialogSuspend(context, title, message, positive = context.getString(R.string.button_text_allow), negative = context.getString(R.string.button_text_dont_allow))
    }

    suspend fun showAllFilesAccessDialogSuspend(context: Context, messageExtra: String? = null): Boolean {
        val title = context.getString(R.string.permission_title_all_files_access_required)
        val message = buildString {
            append(context.getString(R.string.permission_message_this_app_needs_all_file_access))
            if (!messageExtra.isNullOrBlank()) {
                append("\n\n")
                append(messageExtra)
            }
            append(context.getString(R.string.permission_message_you_will_be_taken_to_system_setting_to_grant_it))
        }
        return showYesNoDialogSuspend(context, title, message, positive = context.getString(R.string.button_text_open_settings), negative = context.getString(R.string.button_text_cancel))
    }

    private suspend fun showYesNoDialogSuspend(context: Context, title: String, message: String, positive: String, negative: String): Boolean =
        suspendCancellableCoroutine { cont ->
            val builder = MaterialAlertDialogBuilder(context)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(positive) { dialog, _ ->
                    if (cont.isActive) cont.resume(true)
                    dialog.dismiss()
                }
                .setNegativeButton(negative) { dialog, _ ->
                    if (cont.isActive) cont.resume(false)
                    dialog.dismiss()
                }

            val dialog = builder.create()
            dialog.setOnCancelListener {
                if (cont.isActive) cont.resume(false)
            }
            dialog.show()

            cont.invokeOnCancellation {
                try { dialog.dismiss() } catch (_: Exception) { }
            }
        }
}
