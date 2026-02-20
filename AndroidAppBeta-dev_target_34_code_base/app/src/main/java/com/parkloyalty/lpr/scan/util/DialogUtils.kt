package com.parkloyalty.lpr.scan.util

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.parkloyalty.lpr.scan.R
import com.parkloyalty.lpr.scan.extensions.hideView
import com.parkloyalty.lpr.scan.extensions.nullSafety
import com.parkloyalty.lpr.scan.extensions.showView


object DialogUtils {
    fun showConfirmationDialog(
        context: Context,
        title: String? = null,
        message: String = context.getString(R.string.lbl_something_went_wrong),
        positiveText: String = context.getString(R.string.btn_text_yes),
        negativeText: String = context.getString(R.string.btn_text_no),
        isCancelable: Boolean? = true,
        icon: Int? = null,
        callback: DialogInterface.OnClickListener? = null,
    ) {
        val confirmationAlertDialog = Dialog(context)
        confirmationAlertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        confirmationAlertDialog.setContentView(R.layout.dialog_two_button)
        confirmationAlertDialog.setCancelable(isCancelable.nullSafety(true))

        val tvTitle = confirmationAlertDialog.findViewById<AppCompatTextView>(R.id.tvTitle)
        val tvMessage = confirmationAlertDialog.findViewById<AppCompatTextView>(R.id.tvMessage)
        val btNegative = confirmationAlertDialog.findViewById<AppCompatButton>(R.id.btNegative)
        val btPositive = confirmationAlertDialog.findViewById<AppCompatButton>(R.id.btPositive)
        val ivIcon = confirmationAlertDialog.findViewById<AppCompatImageView>(R.id.ivIcon)
        val ivClose = confirmationAlertDialog.findViewById<AppCompatImageView>(R.id.ivClose)

        if (title != null) {
            tvTitle.showView()
            tvTitle.text = title
        }

        if (icon != null) {
            ivIcon.showView()
            ivIcon.setImageResource(icon)
        }

        tvMessage.text = message

        btPositive.text = positiveText
        btNegative.text = negativeText

        ivClose.setOnClickListener {
            confirmationAlertDialog.dismiss()
        }

        btNegative.setOnClickListener {
            confirmationAlertDialog.dismiss()
            callback?.onClick(confirmationAlertDialog, DialogInterface.BUTTON_NEGATIVE)
        }

        btPositive.setOnClickListener {
            confirmationAlertDialog.dismiss()
            callback?.onClick(confirmationAlertDialog, DialogInterface.BUTTON_POSITIVE)
        }

        confirmationAlertDialog.show()
        confirmationAlertDialog.window!!.setLayout(
            context.resources.getDimension(R.dimen.confirmation_dialog_width).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    fun showInfoDialog(
        context: Context,
        message: String = context.getString(R.string.lbl_something_went_wrong),
        positiveText: String = context.getString(R.string.btn_text_ok),
        icon: Int? = R.drawable.ic_report,
        callback: DialogInterface.OnClickListener? = null
    ) {
        val errorAlertDialog = Dialog(context)
        errorAlertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        errorAlertDialog.setContentView(R.layout.dialog_one_button)

        val ivClose = errorAlertDialog.findViewById<AppCompatImageView>(R.id.ivClose)


        if (icon != null) {
            errorAlertDialog.findViewById<AppCompatImageView>(R.id.ivIcon).showView()
            errorAlertDialog.findViewById<AppCompatImageView>(R.id.ivIcon).setImageResource(icon)
        } else {
            errorAlertDialog.findViewById<AppCompatImageView>(R.id.ivIcon).hideView()
        }

        errorAlertDialog.findViewById<AppCompatTextView>(R.id.tvMessage).text = message
        errorAlertDialog.findViewById<AppCompatButton>(R.id.btPositive).text = positiveText

        ivClose.setOnClickListener {
            errorAlertDialog.dismiss()
        }

        errorAlertDialog.findViewById<AppCompatButton>(R.id.btPositive).setOnClickListener {
            errorAlertDialog.dismiss()
            callback?.onClick(errorAlertDialog, DialogInterface.BUTTON_POSITIVE)
        }

        errorAlertDialog.show()
        errorAlertDialog.window!!.setLayout(
            context.resources.getDimension(R.dimen.confirmation_dialog_width).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }


}