package com.parkloyalty.lpr.scan.utils

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.text.InputFilter.LengthFilter
import android.text.InputType
import android.text.method.DigitsKeyListener
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.parkloyalty.lpr.scan.R
import com.parkloyalty.lpr.scan.extensions.filter
import com.parkloyalty.lpr.scan.extensions.getBoxStrokeColor
import com.parkloyalty.lpr.scan.extensions.nullSafety
import com.parkloyalty.lpr.scan.extensions.setCrossClearButton
import com.parkloyalty.lpr.scan.extensions.setupTextInputLayout
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.CitationLayoutField
import com.parkloyalty.lpr.scan.util.AppUtils
import com.parkloyalty.lpr.scan.util.setAccessibilityForTextInputLayoutDropdownButtons
import javax.inject.Singleton

@Singleton
class NewConstructLayoutBuilder {
    // removed mutable class-level state (myLayout, rootView) to make methods pure and safer

    interface OnClickListener {
        fun onClick() {}
        fun onCameraClick() {}
    }

    fun checkTypeOfField(
        citationLayoutField: CitationLayoutField,
        layout: LinearLayoutCompat?,
        component: String,
        mContext: Context?,
        onClickListener : OnClickListener? = null
    ): Triple<AppCompatAutoCompleteTextView?, TextInputLayout?, OnClickListener?>? {
        if (mContext == null) return null

        val tag = citationLayoutField.tag.orEmpty()
        val rootView: View? = when {
            //Input
            tag.equals("dropdown", ignoreCase = true) -> View.inflate(mContext, R.layout.construct_layout_content_dropdown, null)
            tag.equals("editview", ignoreCase = true) -> View.inflate(mContext, R.layout.construct_layout_content_dropdown, null)
            tag.equals("editviewcamera", ignoreCase = true) -> View.inflate(mContext, R.layout.construct_layout_content_dropdown, null)
            tag.equals("datepicker", ignoreCase = true) -> View.inflate(mContext, R.layout.construct_layout_content_dropdown, null)

            //Another
            tag.equals("textview", ignoreCase = true) -> View.inflate(mContext, R.layout.content_text_editable, null)
            tag.equals("textviewviolation", ignoreCase = true) -> View.inflate(mContext, R.layout.content_text_voilation, null)
            tag.equals("textviewnomargin", ignoreCase = true) -> View.inflate(mContext, R.layout.content_text_no_margin, null)
            tag.equals("textviewprint", ignoreCase = true) -> View.inflate(mContext, R.layout.content_text_print, null)

            //Another
            tag.equals("textarea", ignoreCase = true) -> View.inflate(mContext, R.layout.construct_layout_content_text_box, null)
            else -> null
        }

        return when {
            tag.equals("dropdown", ignoreCase = true) || tag.equals(
                "editview",
                ignoreCase = true
            ) || tag.equals("editviewcamera", ignoreCase = true) || tag.equals(
                "datepicker",
                ignoreCase = true
            ) -> SetPropertyWithInputLayout(
                mContext,
                citationLayoutField,
                component,
                rootView,
                layout,
                onClickListener
            )

            tag.equals("textview", true) || tag.equals(
                "textviewviolation",
                true
            ) || tag.equals("textviewnomargin", true) || tag.equals(
                "textviewprint",
                ignoreCase = true
            ) -> SetPropertyWithTextViewEdit(
                mContext,
                citationLayoutField,
                component,
                rootView,
                layout,
                onClickListener
            )

            tag.equals("textarea", ignoreCase = true) -> SetPropertyWithEditText(
                mContext,
                citationLayoutField,
                component,
                rootView,
                layout,
                onClickListener
            )

            else -> null
        }
    }


    fun CheckTypeOfFieldAbandoned(
        citationLayoutField: CitationLayoutField,
        layout: LinearLayoutCompat?,
        component: String,
        mContext: Context?
    ): TextInputEditText? {
        if (mContext == null) return null

        val tag = citationLayoutField.tag.orEmpty()
        val rootView: View? = when {
            tag.equals("dropdown", ignoreCase = true) -> View.inflate(mContext, R.layout.content_edittext_view_material, null)
            tag.equals("editview", ignoreCase = true) -> View.inflate(mContext, R.layout.content_edittext_view_material, null)
            tag.equals("datepicker", ignoreCase = true) -> View.inflate(mContext, R.layout.content_edittext_view_material, null)
            tag.startsWith("textview", true) -> View.inflate(mContext, R.layout.content_edittext_view_material, null)
            tag.equals("textarea", ignoreCase = true) -> View.inflate(mContext, R.layout.content_edittext_view_material, null)
            else -> null
        }

        return SetPropertyWithInputLayoutAbandoned(mContext, citationLayoutField, component, rootView, layout)
    }

    private fun SetPropertyWithInputLayout(
        mContext: Context?, citationLayoutField: CitationLayoutField, component: String,
        rootView: View?, parentLayout: LinearLayoutCompat?,onClickListener : OnClickListener? = null
    ): Triple<AppCompatAutoCompleteTextView?, TextInputLayout?, OnClickListener?>? {

        if (mContext == null || rootView == null) return null

//        val appCompatTextView: AppCompatTextView? = rootView.findViewById(R.id.appcomptext)
//        val typeface = ResourcesCompat.getFont(mContext, R.font.poppins_regular)
//        appCompatTextView?.setTypeface(typeface)
//        appCompatTextView?.text = if (citationLayoutField.isRequired.nullSafety()) citationLayoutField.repr + "*" else citationLayoutField.repr

        val hintText = if (citationLayoutField.isRequired.nullSafety()) citationLayoutField.repr + "*" else citationLayoutField.repr

        val textInputLayout: TextInputLayout? = rootView.findViewById(R.id.inputLayout)
        val appCompatAutoCompleteTextView: AppCompatAutoCompleteTextView? = rootView.findViewById(R.id.autoCompleteTextView)

        if (citationLayoutField.tag == "editviewcamera"){
            textInputLayout?.setupTextInputLayout(
                hintText = hintText,
                placeholder = "${mContext.getString(R.string.hint_select_or_type)} ${citationLayoutField.repr}",
                endIconModeType = TextInputLayout.END_ICON_CUSTOM,
                endIcon = R.drawable.baseline_camera_alt_24
            )

            textInputLayout?.setEndIconOnClickListener {
                onClickListener?.onCameraClick()
            }
        }else{
            textInputLayout?.setupTextInputLayout(
                hintText = hintText,
                placeholder = "${mContext.getString(R.string.hint_select_or_type)} ${citationLayoutField.repr}"
            )
        }

        if (citationLayoutField.type.equals("datepicker", ignoreCase = true)) {
            textInputLayout?.endIconDrawable = ContextCompat.getDrawable(mContext, R.drawable.ic_date_picker)
            textInputLayout?.isClickable = true
            appCompatAutoCompleteTextView?.isFocusable = false
            appCompatAutoCompleteTextView?.isFocusableInTouchMode = false
            appCompatAutoCompleteTextView?.inputType = EditorInfo.TYPE_DATETIME_VARIATION_DATE
            appCompatAutoCompleteTextView?.hint = "dd/mm/yyyy"
        }

        if (citationLayoutField.mCalculatedField.equals("save", ignoreCase = false)) {
            appCompatAutoCompleteTextView?.setEms(1)
        } else {
            appCompatAutoCompleteTextView?.setEms(0)
        }

        if (!citationLayoutField.collectionName.isNullOrEmpty() && citationLayoutField.collectionName.equals("ViolationCodeList", ignoreCase = true)) {
            //appCompatAutoCompleteTextView?.background = ContextCompat.getDrawable(mContext, R.drawable.round_corner_shape_without_fill_thin_yellow)
            // Apply to outline (box stroke)
            textInputLayout?.setBoxStrokeColorStateList(mContext.getBoxStrokeColor(R.color.light_yellow_30))
        }

        if (citationLayoutField.isRequired != null && citationLayoutField.type == "number") {
            appCompatAutoCompleteTextView?.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        }

        if (citationLayoutField.isEditable != null && !citationLayoutField.isEditable.nullSafety()) {
            appCompatAutoCompleteTextView?.isEnabled = false
            appCompatAutoCompleteTextView?.isFocusable = false
            appCompatAutoCompleteTextView?.isFocusableInTouchMode = false
            textInputLayout?.endIconMode = TextInputLayout.END_ICON_NONE
        }

        when (citationLayoutField.name) {
            "lp_number", "lpr_no" -> {
                appCompatAutoCompleteTextView?.isAllCaps = true
                appCompatAutoCompleteTextView?.inputType = InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS
            }
            "expiry_year" -> {
                appCompatAutoCompleteTextView?.keyListener = DigitsKeyListener.getInstance("0123456789/")
            }
        }

        if (citationLayoutField.name == "vin_number") {
            appCompatAutoCompleteTextView?.filters = arrayOf(filter, LengthFilter(17))
        }

        if (!citationLayoutField.mDefaultValue.isNullOrEmpty()) {
            appCompatAutoCompleteTextView?.setText(citationLayoutField.mDefaultValue)
        }

        appCompatAutoCompleteTextView?.tag = citationLayoutField.mDataTypeValidation

        if (AppUtils.isSunLightMode) {
            appCompatAutoCompleteTextView?.setBackgroundColor(ContextCompat.getColor(mContext, R.color._013220))
            appCompatAutoCompleteTextView?.setTextColor(ContextCompat.getColor(mContext, R.color.white))
            appCompatAutoCompleteTextView?.setHintTextColor(ContextCompat.getColor(mContext, R.color.white))
            textInputLayout?.setStartIconTintList(ColorStateList.valueOf(Color.BLUE))
            textInputLayout?.setEndIconTintList(ColorStateList.valueOf(Color.WHITE))
        }

        setCrossClearButton(
            context = mContext,
            textInputLayout = textInputLayout,
            appCompatAutoCompleteTextView = appCompatAutoCompleteTextView,
            isEditable = (citationLayoutField.isEditable.nullSafety())
        )

        setAccessibilityForTextInputLayoutDropdownButtons(mContext, textInputLayout)

        parentLayout?.addView(rootView)
        return Triple(appCompatAutoCompleteTextView, textInputLayout, null)
    }

    private fun SetPropertyWithInputLayoutAbandoned(
        mContext: Context?, citationLayoutField: CitationLayoutField, component: String,
        rootView: View?, parentLayout: LinearLayoutCompat?
    ): TextInputEditText? {

        if (mContext == null || rootView == null) return null

        val appCompatTextView: AppCompatTextView? = rootView.findViewById(R.id.appcomptext)
        val typeface = ResourcesCompat.getFont(mContext, R.font.poppins_regular)
        appCompatTextView?.setTypeface(typeface)
        appCompatTextView?.text = if (citationLayoutField.isRequired.nullSafety()) citationLayoutField.repr + "*" else citationLayoutField.repr

        val textInputLayout: TextInputLayout? = rootView.findViewById(R.id.input_text)
        val appCompatAutoCompleteTextView: TextInputEditText? = rootView.findViewById(R.id.AutoComTextView)

        if (citationLayoutField.type.equals("datepicker", ignoreCase = true)) {
            textInputLayout?.endIconDrawable = ContextCompat.getDrawable(mContext, R.drawable.ic_date_picker)
            textInputLayout?.isClickable = true
            appCompatAutoCompleteTextView?.isFocusable = false
            appCompatAutoCompleteTextView?.isFocusableInTouchMode = false
            appCompatAutoCompleteTextView?.inputType = EditorInfo.TYPE_DATETIME_VARIATION_DATE
            appCompatAutoCompleteTextView?.hint = "dd/mm/yyyy"
        }

        if (!citationLayoutField.collectionName.isNullOrEmpty() && citationLayoutField.collectionName.equals("ViolationCodeList", ignoreCase = true)) {
            appCompatAutoCompleteTextView?.background = ContextCompat.getDrawable(mContext, R.drawable.round_corner_shape_without_fill_thin_yellow)
        }

        if (citationLayoutField.isRequired != null && citationLayoutField.type == "number") {
            appCompatAutoCompleteTextView?.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        }

        if (citationLayoutField.isEditable != null && !citationLayoutField.isEditable.nullSafety()) {
            appCompatAutoCompleteTextView?.isEnabled = false
            appCompatAutoCompleteTextView?.isFocusable = false
            appCompatAutoCompleteTextView?.isFocusableInTouchMode = false
            textInputLayout?.endIconMode = TextInputLayout.END_ICON_NONE
        }

        if (citationLayoutField.name == "lp_number" || citationLayoutField.name == "lpr_no") {
            appCompatAutoCompleteTextView?.isAllCaps = true
            appCompatAutoCompleteTextView?.inputType = InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS or InputType.TYPE_CLASS_NUMBER or InputType.TYPE_CLASS_TEXT
        }

        if (citationLayoutField.name == "vin_number") {
            appCompatAutoCompleteTextView?.filters = arrayOf(filter, LengthFilter(17))
        }

        if (tagEquals(citationLayoutField.tag)) {
            appCompatAutoCompleteTextView?.tag = "listonly"
        }

        textInputLayout?.hint = citationLayoutField.repr

        setCrossClearButton(
            context = mContext,
            textInputLayout = textInputLayout,
            textInputEditText = appCompatAutoCompleteTextView,
            isEditable = (citationLayoutField.isEditable.nullSafety())
        )

        setAccessibilityForTextInputLayoutDropdownButtons(mContext, textInputLayout)

        parentLayout?.addView(rootView)
        return appCompatAutoCompleteTextView
    }

    private fun SetPropertyWithTextViewEdit(
        mContext: Context?, citationLayoutField: CitationLayoutField, component: String?,
        rootView: View?, parentLayout: LinearLayoutCompat?, onClickListener : OnClickListener? = null
    ): Triple<AppCompatAutoCompleteTextView?, TextInputLayout?, OnClickListener?>? {

        if (mContext == null || rootView == null) return null

        val appCompatTextView: AppCompatTextView? = rootView.findViewById(R.id.tvField)
        val textInputLayout: TextInputLayout? = rootView.findViewById(R.id.input_text)
        val typeface = ResourcesCompat.getFont(mContext, R.font.poppins_regular)
        appCompatTextView?.setTypeface(typeface)
        appCompatTextView?.text = if (citationLayoutField.isRequired.nullSafety()) citationLayoutField.repr + "*" else citationLayoutField.repr

        val appCompatAutoCompleteTextView: AppCompatAutoCompleteTextView? = rootView.findViewById(R.id.tvFieldValue)

        if (component != null && (component.equals("other", ignoreCase = true) || component.equals("Location", ignoreCase = true) || component.equals("Violation", ignoreCase = true))) {
            appCompatAutoCompleteTextView?.background = ContextCompat.getDrawable(mContext, R.drawable.round_corner_shape_without_fill_thin_yellow)
        }

        if (citationLayoutField.isEditable != null && !citationLayoutField.isEditable.nullSafety()) {
            appCompatAutoCompleteTextView?.isFocusable = false
            appCompatAutoCompleteTextView?.isFocusableInTouchMode = false
        }

        if (AppUtils.isSunLightMode) {
            appCompatAutoCompleteTextView?.setBackgroundColor(ContextCompat.getColor(mContext, R.color._013220))
            appCompatAutoCompleteTextView?.setTextColor(ContextCompat.getColor(mContext, R.color.white))
            appCompatAutoCompleteTextView?.setHintTextColor(ContextCompat.getColor(mContext, R.color.white))
        }

        setCrossClearButton(
            context = mContext,
            textInputLayout = textInputLayout,
            appCompatAutoCompleteTextView = appCompatAutoCompleteTextView,
            isEditable = (citationLayoutField.isEditable.nullSafety())
        )

        parentLayout?.addView(rootView)
        return Triple(appCompatAutoCompleteTextView, textInputLayout, null)
    }

    private fun SetPropertyWithEditText(
        mContext: Context?, citationLayoutField: CitationLayoutField, component: String?,
        rootView: View?, parentLayout: LinearLayoutCompat?, onClickListener : OnClickListener? = null
    ): Triple<AppCompatAutoCompleteTextView?, TextInputLayout?, OnClickListener?>? {

        if (mContext == null || rootView == null) return null

        //val appCompatTextView: AppCompatTextView? = rootView.findViewById(R.id.appcomptext)
//        val appCompatAutoCompleteTextView: AppCompatAutoCompleteTextView? = rootView.findViewById(R.id.autoCompleteTextView)
//        val textInputLayout: TextInputLayout? = rootView.findViewById(R.id.inputLayout)
//
//        val typeface = ResourcesCompat.getFont(mContext, R.font.poppins_regular)
//        appCompatTextView?.setTypeface(typeface)
//        appCompatTextView?.text = if (citationLayoutField.isRequired.nullSafety()) citationLayoutField.repr + "*" else citationLayoutField.repr


        val hintText = if (citationLayoutField.isRequired.nullSafety()) citationLayoutField.repr + "*" else citationLayoutField.repr

        val textInputLayout: TextInputLayout? = rootView.findViewById(R.id.inputLayout)
        val appCompatAutoCompleteTextView: AppCompatAutoCompleteTextView? = rootView.findViewById(R.id.autoCompleteTextView)

        if (citationLayoutField.tag == "textarea") {
            textInputLayout?.setupTextInputLayout(
                hintText = hintText,
                placeholder = mContext.getString(R.string.hint_start_typing_your_value,citationLayoutField.repr ),
                endIconModeType = null,
            )
        }

        if (component != null && (component.equals("other", ignoreCase = true) || component.equals("Location", ignoreCase = true) || component.equals("Violation", ignoreCase = true))) {
            //appCompatAutoCompleteTextView?.background = ContextCompat.getDrawable(mContext, R.drawable.round_corner_shape_without_fill_thin_yellow)

            // Apply to outline (box stroke)
            textInputLayout?.setBoxStrokeColorStateList(mContext.getBoxStrokeColor(R.color.light_yellow_30))
        }

        appCompatAutoCompleteTextView?.imeOptions = EditorInfo.IME_ACTION_DONE
        appCompatAutoCompleteTextView?.setRawInputType(InputType.TYPE_CLASS_TEXT)
        appCompatAutoCompleteTextView?.setHorizontallyScrolling(false)
        appCompatAutoCompleteTextView?.setLines(6)

        if (!citationLayoutField.fieldName.isNullOrEmpty() && citationLayoutField.fieldName.equals("violation_fine_name", ignoreCase = true)) {
            // Apply to outline (box stroke)
            textInputLayout?.setBoxStrokeColorStateList(mContext.getBoxStrokeColor(R.color.light_yellow_30))
            //appCompatAutoCompleteTextView?.background = ContextCompat.getDrawable(mContext, R.drawable.round_corner_shape_without_fill_thin_yellow)
        }

        if (citationLayoutField.isEditable != null && !citationLayoutField.isEditable.nullSafety()) {
            appCompatAutoCompleteTextView?.isEnabled = false
            appCompatAutoCompleteTextView?.isFocusable = false
            appCompatAutoCompleteTextView?.isFocusableInTouchMode = false
        }

        if (citationLayoutField.tag.equals("textarea", ignoreCase = true)) {
            appCompatAutoCompleteTextView?.maxLines = 6
            appCompatAutoCompleteTextView?.imeOptions = EditorInfo.IME_ACTION_DONE
            appCompatAutoCompleteTextView?.setRawInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE)
            appCompatAutoCompleteTextView?.isElegantTextHeight = true
            appCompatAutoCompleteTextView?.inputType = InputType.TYPE_TEXT_FLAG_MULTI_LINE
            appCompatAutoCompleteTextView?.isSingleLine = false
        }

        if (AppUtils.isSunLightMode) {
            appCompatAutoCompleteTextView?.setBackgroundColor(ContextCompat.getColor(mContext, R.color._013220))
            appCompatAutoCompleteTextView?.setTextColor(ContextCompat.getColor(mContext, R.color.white))
            appCompatAutoCompleteTextView?.setHintTextColor(ContextCompat.getColor(mContext, R.color.white))
        }

        setCrossClearButton(
            context = mContext,
            textInputLayout = textInputLayout,
            appCompatAutoCompleteTextView = appCompatAutoCompleteTextView,
            isEditable = (citationLayoutField.isEditable.nullSafety())
        )

        parentLayout?.addView(rootView)
        return Triple(appCompatAutoCompleteTextView, textInputLayout, null)
    }

    fun sunLightMode(
        mContext: Context,
        appCompatAutoCompleteTextView: AppCompatAutoCompleteTextView
    ) {
        if (AppUtils.isSunLightMode) {
            appCompatAutoCompleteTextView.setBackgroundColor(
                ContextCompat.getColor(
                    mContext, R.color._013220
                )
            )
            appCompatAutoCompleteTextView.setTextColor(
                ContextCompat.getColor(
                    mContext, R.color.white
                )
            )
            appCompatAutoCompleteTextView.setHintTextColor(
                ContextCompat.getColor(
                    mContext, R.color.white
                )
            )
        }
    }

    private fun tagEquals(tag: String?) = tag?.equals("dropdown", ignoreCase = true) == true
}