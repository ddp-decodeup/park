package com.parkloyalty.lpr.scan.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.parkloyalty.lpr.scan.R
import com.parkloyalty.lpr.scan.extensions.hideView
import com.parkloyalty.lpr.scan.extensions.showView
import androidx.core.graphics.drawable.toDrawable

interface NoInternetDialogListener {
    fun onPositiveButtonClicked() {}
    fun onNegativeButtonClicked() {}
}

object NoInternetDialogUtil {
    private var currentDialog: Dialog? = null

    fun showDialog(
        context: Context,
        positiveButtonText: String = context.getString(R.string.button_text_ok),
        negativeButtonText: String? = null,
        cancelable: Boolean = true,
        listener: NoInternetDialogListener? = null,
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

        // Icon
        ivIcon?.showView()
        ivIcon?.setImageResource(R.drawable.icon_no_internet_connection)

        // Title
        tvTitle.text = context.getString(R.string.error_title_no_internet)

        // Message
        tvMessage?.text = context.getString(R.string.error_desc_no_internet_connection)

        // Positive Button
        btPositive?.text = positiveButtonText
        btPositive?.setOnClickListener {
            errorAlertDialog.dismiss()
            listener?.onPositiveButtonClicked()
        }

        // Negative Button
        if (!negativeButtonText.isNullOrBlank() && btNegative != null) {
            btNegative.showView()
            btNegative.text = negativeButtonText
            btNegative.setOnClickListener {
                errorAlertDialog.dismiss()
                listener?.onNegativeButtonClicked()
            }
        } else {
            btNegative?.hideView()
        }

        // Optionally handle dialog close (if you have a close icon)
//        val ivClose = errorAlertDialog.findViewById<AppCompatImageView>(R.id.ivClose)
//        ivClose?.setOnClickListener {
//            errorAlertDialog.dismiss()
//            listener?.onClose(errorAlertDialog)
//        }

        errorAlertDialog.show()
//        errorAlertDialog.window?.setLayout(
//            context.resources.getDimension(R.dimen.confirmation_dialog_width).toInt(),
//            ViewGroup.LayoutParams.WRAP_CONTENT
//        )
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