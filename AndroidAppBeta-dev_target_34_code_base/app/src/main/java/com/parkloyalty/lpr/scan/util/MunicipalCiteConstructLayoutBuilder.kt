package com.parkloyalty.lpr.scan.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.text.InputFilter.LengthFilter
import android.text.InputType
import android.text.method.DigitsKeyListener
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.parkloyalty.lpr.scan.R
import com.parkloyalty.lpr.scan.basecontrol.BaseActivity
import com.parkloyalty.lpr.scan.extensions.nullSafety
import com.parkloyalty.lpr.scan.ui.check_setup.model.municipalcitation.MunicipalCitationLayoutField
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.CitationLayoutField


@SuppressLint("StaticFieldLeak")
object MunicipalCiteConstructLayoutBuilder : BaseActivity() {
    private var myLayout: LinearLayoutCompat? = null
    private var rootView: View? = null

    @JvmStatic
    fun CheckTypeOfField(
        `object`: MunicipalCitationLayoutField, layout: LinearLayoutCompat?,
        component: String, mContext: Context?
    ): AppCompatAutoCompleteTextView? {
        myLayout = layout
        var appCompatAutoCompleteTextView: AppCompatAutoCompleteTextView? = null
//        MainScope().launch {

            if(`object`.tag.equals("dropdown", ignoreCase = true)) {
                rootView = View.inflate(mContext, R.layout.content_dropdown, null)
                appCompatAutoCompleteTextView =
                    SetPropertyWithInputLayout(mContext, `object`, component, true)
            } else if (`object`.tag.equals("editview", ignoreCase = true)) {
                rootView = View.inflate(mContext, R.layout.content_textview, null)
                appCompatAutoCompleteTextView =
                    SetPropertyWithInputLayout(mContext, `object`, component, true)
            }else if (`object`.tag.equals("editviewcamera", ignoreCase = true)) {
                rootView = View.inflate(mContext, R.layout.content_textview_camera_icon, null)
                appCompatAutoCompleteTextView =
                    SetPropertyWithInputLayout(mContext, `object`, component, true)
            } else if (`object`.tag.equals("datepicker", ignoreCase = true)) {
                rootView = View.inflate(mContext, R.layout.content_dropdown, null)
                appCompatAutoCompleteTextView =
                    SetPropertyWithInputLayout(mContext, `object`, component, true)
            } else if (`object`.tag.equals("textview", ignoreCase = true)) //
            {
                rootView = View.inflate(mContext, R.layout.content_text_editable, null)
                appCompatAutoCompleteTextView =
                    SetPropertyWithTextViewEdit(mContext, `object`, component, true)
            } else if (`object`.tag.equals("textviewnomargin", ignoreCase = true)) //
            {
                rootView = View.inflate(mContext, R.layout.content_text_no_margin, null)
                appCompatAutoCompleteTextView =
                    SetPropertyWithTextViewEdit(mContext, `object`, component, true)
            } else if (`object`.tag.equals("textviewprint", ignoreCase = true)) //
            {
                rootView = View.inflate(mContext, R.layout.content_text_print, null)
                appCompatAutoCompleteTextView =
                    SetPropertyWithTextViewEdit(mContext, `object`, component, true)
            } else if (`object`.tag.equals("textviewviolation", ignoreCase = true)) //
            {
                rootView = View.inflate(mContext, R.layout.content_text_voilation, null)
                appCompatAutoCompleteTextView =
                    SetPropertyWithTextViewEdit(mContext, `object`, component, true)
            } else if (`object`.tag.equals("textarea", ignoreCase = true)) {
                rootView = View.inflate(mContext, R.layout.content_text_box, null)
                appCompatAutoCompleteTextView =
                    SetPropertyWithEditText(mContext, `object`, component, true)
            }
//        }
        return appCompatAutoCompleteTextView
    }
@JvmStatic
    fun CheckTypeOfFieldAbandoned(
        `object`: CitationLayoutField, layout: LinearLayoutCompat?,
        component: String, mContext: Context?
    ): TextInputEditText? {
        //mContext = context
        myLayout = layout
        var appCompatAutoCompleteTextView: TextInputEditText? = null
        if (`object`.tag.equals("dropdown", ignoreCase = true)) {
            rootView = View.inflate(mContext, R.layout.content_edittext_view_material, null)
            appCompatAutoCompleteTextView = SetPropertyWithInputLayoutAbandoned(mContext, `object`, component, true)
        } else if (`object`.tag.equals("editview", ignoreCase = true)) {
            rootView = View.inflate(mContext, R.layout.content_edittext_view_material, null)
            appCompatAutoCompleteTextView = SetPropertyWithInputLayoutAbandoned(mContext, `object`, component, true)
        } else if (`object`.tag.equals("datepicker", ignoreCase = true)) {
            rootView = View.inflate(mContext, R.layout.content_edittext_view_material, null)
            appCompatAutoCompleteTextView = SetPropertyWithInputLayoutAbandoned(mContext, `object`, component, true)
        } else if (`object`.tag.equals("textview", ignoreCase = true)) //
        {
            rootView = View.inflate(mContext, R.layout.content_edittext_view_material, null)
            appCompatAutoCompleteTextView = SetPropertyWithInputLayoutAbandoned(mContext, `object`, component, true)
        } else if (`object`.tag.equals("textviewnomargin", ignoreCase = true)) //
        {
            rootView = View.inflate(mContext, R.layout.content_edittext_view_material, null)
            appCompatAutoCompleteTextView = SetPropertyWithInputLayoutAbandoned(mContext, `object`, component, true)
        } else if (`object`.tag.equals("textviewprint", ignoreCase = true)) //
        {
            rootView = View.inflate(mContext, R.layout.content_edittext_view_material, null)
            appCompatAutoCompleteTextView = SetPropertyWithInputLayoutAbandoned(mContext, `object`, component, true)
        } else if (`object`.tag.equals("textviewviolation", ignoreCase = true)) //
        {
            rootView = View.inflate(mContext, R.layout.content_edittext_view_material, null)
            appCompatAutoCompleteTextView = SetPropertyWithInputLayoutAbandoned(mContext, `object`, component, true)
        } else if (`object`.tag.equals("textarea", ignoreCase = true)) {
            rootView = View.inflate(mContext, R.layout.content_edittext_view_material, null)
            appCompatAutoCompleteTextView =  SetPropertyWithInputLayoutAbandoned(mContext, `object`, component, true)
        }
        return appCompatAutoCompleteTextView
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private fun SetPropertyWithInputLayout(
        mContext: Context?,
        `object`: MunicipalCitationLayoutField,
        component: String,
        isEditable: Boolean
    ): AppCompatAutoCompleteTextView? {

        var appCompatTextView: AppCompatTextView? = null
        var textInputLayout: TextInputLayout? = null
        var appCompatAutoCompleteTextView: AppCompatAutoCompleteTextView? = null

//        // textview
        appCompatTextView = rootView?.findViewById(R.id.appcomptext)
        val typeface = ResourcesCompat.getFont(mContext!!, R.font.poppins_regular)
        appCompatTextView?.setTypeface(typeface)
        if (`object`.isRequired.nullSafety()) {
            appCompatTextView?.setText(`object`.repr + "*")
        } else {
            appCompatTextView?.setText(`object`.repr)
        }
        textInputLayout = rootView?.findViewById(R.id.input_text)
        //Testing------
        appCompatAutoCompleteTextView = rootView!!.findViewById(R.id.AutoComTextView)
        if (`object`.type.equals("datepicker", ignoreCase = true)) {
            textInputLayout?.setEndIconDrawable(mContext!!.getDrawable(R.drawable.ic_date_picker))
            textInputLayout?.setClickable(true)
            appCompatAutoCompleteTextView?.setFocusable(false)
            appCompatAutoCompleteTextView?.setFocusableInTouchMode(false)
            appCompatAutoCompleteTextView?.setInputType(EditorInfo.TYPE_DATETIME_VARIATION_DATE)
            appCompatAutoCompleteTextView?.setHint("dd/mm/yyyy")
        }

        if(`object`.mCalculatedField.equals("save",ignoreCase = false)) {
            appCompatAutoCompleteTextView.setEms(1)
        }else{
            appCompatAutoCompleteTextView.setEms(0)
        }
        if (`object`.collectionName != null) {
            if (`object`.collectionName.equals("ViolationCodeList", ignoreCase = true)) {
                appCompatAutoCompleteTextView?.setBackground(
                    mContext!!.getDrawable(R.drawable.round_corner_shape_without_fill_thin_yellow)
                )
            }
        }
        if (`object`.isRequired != null) {
            if (`object`.type == "number") {
                appCompatAutoCompleteTextView?.setInputType(InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL)
            }
        }

        if (`object`.isEditable != null) {
            if (!`object`.isEditable.nullSafety()) {
                appCompatAutoCompleteTextView?.setEnabled(false)
                appCompatAutoCompleteTextView?.setFocusable(false)
                appCompatAutoCompleteTextView?.setFocusableInTouchMode(false)
                textInputLayout!!.endIconMode = TextInputLayout.END_ICON_NONE

//                appCompatAutoCompleteTextView!!.setSingleLine(false)
//                appCompatAutoCompleteTextView!!.setLines(2)
//                appCompatAutoCompleteTextView.setFilters(arrayOf<InputFilter>(LengthFilter(40)))
            }
        }
        if (`object`.name == "lp_number") {
            appCompatAutoCompleteTextView?.setAllCaps(true)
            appCompatAutoCompleteTextView?.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS)
        }
        if (`object`.name == "expiry_year") {
//            appCompatAutoCompleteTextView?.setInputType(InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_NUMBER_FLAG_SIGNED)
            appCompatAutoCompleteTextView?.setKeyListener(DigitsKeyListener.getInstance("0123456789/"));

        }
        if (`object`.name == "lpr_no") {
            appCompatAutoCompleteTextView?.setAllCaps(true)
            appCompatAutoCompleteTextView?.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS)
        }
//        if (`object`.mMaxLength?.isEmpty() ==false) {
//            appCompatAutoCompleteTextView?.setFilters(arrayOf(filter, LengthFilter(17)))
//        }

        if (`object`.name == "vin_number") {
            appCompatAutoCompleteTextView?.setFilters(arrayOf(filter, LengthFilter(17)))
        }

        if (`object`.mDefaultValue!=null&&`object`.mDefaultValue!!.isNotEmpty()) {
            appCompatAutoCompleteTextView?.setText(`object`.mDefaultValue)
        }
//        if (`object`.tag.equals("editview", ignoreCase = true)) {
//            textInputLayout!!.endIconMode = TextInputLayout.END_ICON_CLEAR_TEXT
//        }

//        appCompatAutoCompleteTextView?.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_CLASS_NUMBER
//        if (`object`.tag.equals(
//                "dropdown",
//                ignoreCase = true
//            ) && `object`.mDataTypeValidation != null && `object`.mDataTypeValidation == "listonly"
//        ) {
//            appCompatAutoCompleteTextView?.setTag("listonly")
//
//        }
        appCompatAutoCompleteTextView?.setTag(`object`.mDataTypeValidation)

        if(AppUtils.isSunLightMode) {
            appCompatAutoCompleteTextView.setBackgroundColor(
                ContextCompat.getColor(
                    mContext,
                    R.color._013220
                )
            );
            appCompatAutoCompleteTextView.setTextColor(
                ContextCompat.getColor(
                    mContext,
                    R.color.white
                )
            );
            appCompatAutoCompleteTextView.setHintTextColor(
                ContextCompat.getColor(
                    mContext,
                    R.color.white
                )
            );
            textInputLayout!!.setStartIconTintList(ColorStateList.valueOf(Color.BLUE))
            textInputLayout!!.setEndIconTintList(ColorStateList.valueOf(Color.WHITE))
        }

        setCrossClearButton(context = mContext, textInputLayout = textInputLayout, appCompatAutoCompleteTextView = appCompatAutoCompleteTextView, isEditable = (`object`.isEditable.nullSafety()))

        myLayout!!.addView(rootView)
        return appCompatAutoCompleteTextView
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private fun SetPropertyWithInputLayoutAbandoned(
        mContext: Context?,
        `object`: CitationLayoutField,
        component: String,
        isEditable: Boolean
    ): TextInputEditText? {

        var appCompatTextView: AppCompatTextView? = null
        var textInputLayout: TextInputLayout? = null
        var appCompatAutoCompleteTextView: TextInputEditText? = null

//        // textview
        appCompatTextView = rootView?.findViewById(R.id.appcomptext)
        val typeface = ResourcesCompat.getFont(mContext!!, R.font.poppins_regular)
        appCompatTextView?.setTypeface(typeface)
        if (`object`.isRequired.nullSafety()) {
            appCompatTextView?.setText(`object`.repr + "*")
        } else {
            appCompatTextView?.setText(`object`.repr)
        }
        textInputLayout = rootView?.findViewById(R.id.input_text)
        //Testing------
        appCompatAutoCompleteTextView = rootView!!.findViewById(R.id.AutoComTextView)
        if (`object`.type.equals("datepicker", ignoreCase = true)) {
            textInputLayout?.setEndIconDrawable(mContext!!.getDrawable(R.drawable.ic_date_picker))
            textInputLayout?.setClickable(true)
            appCompatAutoCompleteTextView?.setFocusable(false)
            appCompatAutoCompleteTextView?.setFocusableInTouchMode(false)
            appCompatAutoCompleteTextView?.setInputType(EditorInfo.TYPE_DATETIME_VARIATION_DATE)
            appCompatAutoCompleteTextView?.setHint("dd/mm/yyyy")
        }
        if (`object`.collectionName != null) {
            if (`object`.collectionName.equals("ViolationCodeList", ignoreCase = true)) {
                appCompatAutoCompleteTextView?.setBackground(
                    mContext!!.getDrawable(R.drawable.round_corner_shape_without_fill_thin_yellow)
                )
            }
        }
        if (`object`.isRequired != null) {
            if (`object`.type == "number") {
                appCompatAutoCompleteTextView?.setInputType(InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL)
            }
        }
        if (`object`.isEditable != null) {
            if (!`object`.isEditable.nullSafety()) {
                appCompatAutoCompleteTextView?.setEnabled(false)
                appCompatAutoCompleteTextView?.setFocusable(false)
                appCompatAutoCompleteTextView?.setFocusableInTouchMode(false)
                textInputLayout!!.endIconMode = TextInputLayout.END_ICON_NONE

//                appCompatAutoCompleteTextView!!.setSingleLine(false)
//                appCompatAutoCompleteTextView!!.setLines(2)
//                appCompatAutoCompleteTextView.setFilters(arrayOf<InputFilter>(LengthFilter(40)))
            }
        }
        if (`object`.name == "lp_number" || `object`.name == "lpr_no") {
            appCompatAutoCompleteTextView?.setAllCaps(true)
            appCompatAutoCompleteTextView?.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS or
                    InputType.TYPE_CLASS_NUMBER or InputType.TYPE_CLASS_TEXT)
        }
//        if (`object`.name == "lpr_no") {
//            appCompatAutoCompleteTextView?.setAllCaps(true)
//            appCompatAutoCompleteTextView?.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS)
//        }
        if (`object`.name == "vin_number") {
            appCompatAutoCompleteTextView?.setFilters(arrayOf(filter, LengthFilter(17)))
        }
        if (`object`.tag.equals(
                "dropdown",
                ignoreCase = true
            ) && `object`.mDataTypeValidation != null && `object`.mDataTypeValidation == "listonly"
        ) {
            appCompatAutoCompleteTextView?.setTag("listonly")

        }
//        appCompatAutoCompleteTextView!!.setHint(`object`.repr)
        textInputLayout!!.setHint(`object`.repr)


        setCrossClearButton(context = mContext, textInputLayout = textInputLayout, textInputEditText = appCompatAutoCompleteTextView, isEditable = (`object`.isEditable.nullSafety()))

        myLayout!!.addView(rootView)
        return appCompatAutoCompleteTextView
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private fun SetPropertyWithTextViewEdit(
        mContext: Context?,
        `object`: MunicipalCitationLayoutField,
        component: String?,
        isEditable: Boolean
    ): AppCompatAutoCompleteTextView? {

        var appCompatTextView: AppCompatTextView? = null
        var textInputLayout: TextInputLayout? = null
        var appCompatAutoCompleteTextView: AppCompatAutoCompleteTextView? = null

        appCompatTextView = rootView!!.findViewById(R.id.tvField)
        textInputLayout = rootView?.findViewById(R.id.input_text)

        val typeface = ResourcesCompat.getFont(mContext!!, R.font.poppins_regular)
        appCompatTextView?.setTypeface(typeface)
        if (`object`.isRequired != null) {
            if (`object`.isRequired.nullSafety()) {
                appCompatTextView?.setText(`object`.repr + "*")
            } else {
                appCompatTextView?.setText(`object`.repr)
            }
        } else {
            appCompatTextView?.setText(`object`.repr)
        }

        //Testing------
        appCompatAutoCompleteTextView = rootView?.findViewById(R.id.tvFieldValue)
        if (component != null) {
            if (component.equals("other", ignoreCase = true) || component.equals(
                    "Location",
                    ignoreCase = true
                )
                || component.equals("Violation", ignoreCase = true)
            ) {
                appCompatAutoCompleteTextView?.setBackground(
                    mContext!!.getDrawable(R.drawable.round_corner_shape_without_fill_thin_yellow)
                )
            }
        }
        //appCompatAutoCompleteTextView.setText(object.getRepr());
        if (`object`.isEditable != null) {
            if (!`object`.isEditable.nullSafety()) {
                //appCompatAutoCompleteTextView.setEnabled(false);
                appCompatAutoCompleteTextView?.setFocusable(false)
                appCompatAutoCompleteTextView?.setFocusableInTouchMode(false)
            }
        }

        if(AppUtils.isSunLightMode) {
            appCompatAutoCompleteTextView!!.setBackgroundColor(
                ContextCompat.getColor(
                    mContext,
                    R.color._013220
                )
            );
            appCompatAutoCompleteTextView!!.setTextColor(
                ContextCompat.getColor(
                    mContext,
                    R.color.white
                )
            );
            appCompatAutoCompleteTextView!!.setHintTextColor(
                ContextCompat.getColor(
                    mContext,
                    R.color.white
                )
            );
        }


        setCrossClearButton(context = mContext, textInputLayout = textInputLayout, appCompatAutoCompleteTextView = appCompatAutoCompleteTextView, isEditable = (`object`.isEditable.nullSafety()))

        myLayout?.addView(rootView)
        return appCompatAutoCompleteTextView
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private fun SetPropertyWithEditText(
        mContext: Context?,
        `object`: MunicipalCitationLayoutField,
        component: String?,
        isEditable: Boolean
    ): AppCompatAutoCompleteTextView? {

        var appCompatTextView: AppCompatTextView? = null
        var appCompatAutoCompleteTextView: AppCompatAutoCompleteTextView? = null
        var textInputLayout: TextInputLayout? = null

        appCompatTextView = rootView!!.findViewById(R.id.appcomptext)
        appCompatAutoCompleteTextView = rootView!!.findViewById(R.id.AutoComTextView)
        textInputLayout = rootView?.findViewById(R.id.input_text)

        val typeface = ResourcesCompat.getFont(mContext!!, R.font.poppins_regular)
        appCompatTextView?.setTypeface(typeface)
        if (`object`.isRequired.nullSafety()) {
            appCompatTextView?.setText(`object`.repr + "*")
        } else {
            appCompatTextView?.setText(`object`.repr)
        }
        if (component != null) {
            if (component.equals("other", ignoreCase = true) || component.equals(
                    "Location",
                    ignoreCase = true
                )
                || component.equals("Violation", ignoreCase = true)
            ) {
                appCompatAutoCompleteTextView?.setBackground(
                    mContext?.getDrawable(R.drawable.round_corner_shape_without_fill_thin_yellow)
                )
            }
        }
        //textInputEditText  = rootView.findViewById(R.id.etLocationDescr);
        appCompatAutoCompleteTextView?.setImeOptions(EditorInfo.IME_ACTION_DONE)
        appCompatAutoCompleteTextView?.setRawInputType(InputType.TYPE_CLASS_TEXT)
        appCompatAutoCompleteTextView?.setHorizontallyScrolling(false)
        appCompatAutoCompleteTextView?.setLines(6)
        if (`object`.fieldName != null) {
            if (`object`.fieldName.equals("violation_fine_name", ignoreCase = true)) {
                appCompatAutoCompleteTextView?.setBackground(
                    mContext?.getDrawable(R.drawable.round_corner_shape_without_fill_thin_yellow)
                )
            }
        }
        if (`object`.isEditable != null) {
            if (!`object`.isEditable.nullSafety()) {
                appCompatAutoCompleteTextView?.setEnabled(false)
                appCompatAutoCompleteTextView?.setFocusable(false)
                appCompatAutoCompleteTextView?.setFocusableInTouchMode(false)
            }
        }

        if (`object`.tag.equals("textarea", ignoreCase = true)) {
            appCompatAutoCompleteTextView?.maxLines =6
            appCompatAutoCompleteTextView?.setImeOptions(EditorInfo.IME_ACTION_DONE);
            appCompatAutoCompleteTextView?.setRawInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
            appCompatAutoCompleteTextView?.setElegantTextHeight(true);
            appCompatAutoCompleteTextView?.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
            appCompatAutoCompleteTextView?.setSingleLine(false);
        }

        if(AppUtils.isSunLightMode) {
            appCompatAutoCompleteTextView!!.setBackgroundColor(
                ContextCompat.getColor(
                    mContext,
                    R.color._013220
                )
            );
            appCompatAutoCompleteTextView!!.setTextColor(
                ContextCompat.getColor(
                    mContext,
                    R.color.white
                )
            );
            appCompatAutoCompleteTextView!!.setHintTextColor(
                ContextCompat.getColor(
                    mContext,
                    R.color.white
                )
            );
        }

        setCrossClearButton(context = mContext, textInputLayout = textInputLayout, appCompatAutoCompleteTextView = appCompatAutoCompleteTextView, isEditable = (`object`.isEditable.nullSafety()))

        myLayout?.addView(rootView)
        return appCompatAutoCompleteTextView
    }

    @JvmStatic
    fun sunLightMode(mContext:Context, appCompatAutoCompleteTextView: AppCompatAutoCompleteTextView)
    {
        if(AppUtils.isSunLightMode) {
            appCompatAutoCompleteTextView.setBackgroundColor(
                ContextCompat.getColor(
                    mContext,
                    R.color._013220
                )
            );
            appCompatAutoCompleteTextView.setTextColor(
                ContextCompat.getColor(
                    mContext,
                    R.color.white
                )
            );
            appCompatAutoCompleteTextView.setHintTextColor(
                ContextCompat.getColor(
                    mContext,
                    R.color.white
                )
            );
//            textInputLayout!!.setStartIconTintList(ColorStateList.valueOf(Color.BLUE))
//            textInputLayout!!.setEndIconTintList(ColorStateList.valueOf(Color.WHITE))
        }
    }
}