package com.parkloyalty.lpr.scan.extensions

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.appcompat.widget.AppCompatEditText
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.parkloyalty.lpr.scan.R
import com.parkloyalty.lpr.scan.util.AccessibilityUtil.announceForAccessibility
import com.parkloyalty.lpr.scan.util.LogUtil
import com.parkloyalty.lpr.scan.util.setAccessibilityForTextInputLayoutCrossButtons

fun setCrossClearButton(
    context: Context,
    textInputLayout: TextInputLayout?,
    appCompatAutoCompleteTextView: AppCompatAutoCompleteTextView?,
    isEditable: Boolean? = true
) {
    if (LogUtil.isEnableCrossClearButton() && isEditable.nullSafety()) {
        textInputLayout?.setStartIconDrawable(R.drawable.ic_cross_black)
        textInputLayout?.setStartIconTintList(ColorStateList.valueOf(Color.BLACK))

        textInputLayout?.setStartIconOnClickListener {
            appCompatAutoCompleteTextView?.text = null
            announceForAccessibility(
                textInputLayout,
                context.getString(R.string.ada_announcement_text_cleared).nullSafety()
            )
        }

        setAccessibilityForTextInputLayoutCrossButtons(context, textInputLayout)
    }
}

fun setCrossClearButton(
    context: Context,
    textInputLayout: TextInputLayout?,
    appCompatEditText: AppCompatEditText?,
    isEditable: Boolean? = true
) {
    if (LogUtil.isEnableCrossClearButton() && isEditable.nullSafety()) {
        textInputLayout?.setStartIconDrawable(R.drawable.ic_cross_black)
        textInputLayout?.setStartIconTintList(ColorStateList.valueOf(Color.BLACK))

        textInputLayout?.setStartIconOnClickListener {
            appCompatEditText?.text = null
            announceForAccessibility(
                textInputLayout,
                context.getString(R.string.ada_announcement_text_cleared).nullSafety()
            )
        }

        setAccessibilityForTextInputLayoutCrossButtons(context, textInputLayout)
    }
}

fun setCrossClearButton(
    context: Context,
    textInputLayout: TextInputLayout?,
    textInputEditText: TextInputEditText?,
    isEditable: Boolean? = true
) {
    if (LogUtil.isEnableCrossClearButton() && isEditable.nullSafety()) {
        textInputLayout?.setStartIconDrawable(R.drawable.ic_cross_black)
        textInputLayout?.setStartIconTintList(ColorStateList.valueOf(Color.BLACK))

        textInputLayout?.setStartIconOnClickListener {
            textInputEditText?.text = null
            announceForAccessibility(
                textInputLayout,
                context.getString(R.string.ada_announcement_text_cleared).nullSafety()
            )
        }

        setAccessibilityForTextInputLayoutCrossButtons(context, textInputLayout)
    }
}