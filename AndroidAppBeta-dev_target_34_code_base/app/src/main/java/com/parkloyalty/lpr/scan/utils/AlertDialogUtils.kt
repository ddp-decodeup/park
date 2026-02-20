package com.parkloyalty.lpr.scan.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.view.ViewGroup
import android.widget.Space
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.parkloyalty.lpr.scan.R
import com.parkloyalty.lpr.scan.extensions.hideView
import com.parkloyalty.lpr.scan.extensions.showView
import androidx.core.graphics.drawable.toDrawable

interface AlertDialogListener {
    fun onPositiveButtonClicked() {}
    fun onNegativeButtonClicked() {}
    //fun onClose(dialog: Dialog) {}
}

object AlertDialogUtils {
    private var currentDialog: Dialog? = null

    fun showDialog(
        context: Context,
        title: String? = context.getString(R.string.error_title_unknown_error),
        message: String = context.getString(R.string.error_desc_something_went_wrong),
        positiveButtonText: String = context.getString(R.string.button_text_ok),
        negativeButtonText: String? = null,
        icon: Int? = R.drawable.icon_info,
        cancelable: Boolean = true,
        listener: AlertDialogListener? = null,
    ) {
        // Dismiss previous dialog if showing
        currentDialog?.dismiss()

        val errorAlertDialog = Dialog(context)
        currentDialog = errorAlertDialog
        errorAlertDialog.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
        errorAlertDialog.setContentView(R.layout.dialog_custom_alert)
        errorAlertDialog.setCancelable(cancelable)

        val ivIcon = errorAlertDialog.findViewById<AppCompatImageView>(R.id.ivIcon)
        val tvTitle = errorAlertDialog.findViewById<AppCompatTextView>(R.id.tvTitle)
        val tvMessage = errorAlertDialog.findViewById<AppCompatTextView>(R.id.tvMessage)
        val btPositive = errorAlertDialog.findViewById<AppCompatButton>(R.id.btPositive)
        val btNegative = errorAlertDialog.findViewById<AppCompatButton?>(R.id.btNegative)
        val spaceBetweenButtons = errorAlertDialog.findViewById<Space>(R.id.spaceBetweenButtons)

        // Icon
        if (icon != null) {
            ivIcon?.showView()
            ivIcon?.setImageResource(icon)
        } else {
            ivIcon?.hideView()
        }

        // Title
        if (!title.isNullOrBlank() && tvTitle != null) {
            tvTitle.showView()
            tvTitle.text = title
        } else {
            tvTitle?.hideView()
        }

        // Message
        tvMessage?.text = message

        // Positive Button
        btPositive?.text = positiveButtonText
        btPositive?.setOnClickListener {
            errorAlertDialog.dismiss()
            listener?.onPositiveButtonClicked()
        }

        // Negative Button
        if (!negativeButtonText.isNullOrBlank() && btNegative != null) {
            spaceBetweenButtons.showView()
            btNegative.showView()
            btNegative.text = negativeButtonText
            btNegative.setOnClickListener {
                errorAlertDialog.dismiss()
                listener?.onNegativeButtonClicked()
            }
        } else {
            spaceBetweenButtons.hideView()
            btNegative?.hideView()
        }

        // Optionally handle dialog close (if you have a close icon)
//        val ivClose = errorAlertDialog.findViewById<AppCompatImageView>(R.id.ivClose)
//        ivClose?.setOnClickListener {
//            errorAlertDialog.dismiss()
//            listener?.onClose(errorAlertDialog)
//        }

        errorAlertDialog.show()
        errorAlertDialog.window?.setLayout(
            context.resources.getDimension(R.dimen.confirmation_dialog_width).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    //Function to dismiss the dialog if showing manually
    fun dismissDialog() {
        currentDialog?.dismiss()
        currentDialog = null
    }
}

//USAGE

//AlertDialogUtils.showDialog(
//context = requireContext(),
//title = "Confirm",
//message = "Are you sure?",
//positiveButtonText = "Yes",
//listener = object : AlertDialogListener {
//    override fun onPositiveButtonClicked() {
//        // Handle positive action
//    }
//}
//)