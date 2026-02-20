package com.parkloyalty.lpr.scan.extensions

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.os.Build
import android.os.Vibrator
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.View
import android.view.View.OnFocusChangeListener
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.appcompat.widget.AppCompatButton
import com.parkloyalty.lpr.scan.R
import com.google.android.material.textfield.TextInputLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
import kotlin.text.compareTo
import kotlin.toString

fun AppCompatAutoCompleteTextView?.setAmount(currencySymbol : String, amount : String){
    this?.setText("$currencySymbol $amount")
}

fun View.showView() {
    this.visibility = View.VISIBLE
}

fun View.hideView() {
    this.visibility = View.GONE
}

fun View.invisibleView() {
    this.visibility = View.INVISIBLE
}

fun View?.setButtonForShowHideOnKeyboard(button : AppCompatButton?){
    val defaultKeyboardDP = 100
    val estimatedKeyboardDP =
        defaultKeyboardDP + if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) 48 else 0

    val r = Rect()
    this?.viewTreeObserver?.addOnGlobalLayoutListener {
        val estimatedKeyboardHeight = TypedValue
            .applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                estimatedKeyboardDP.toFloat(),
                this@setButtonForShowHideOnKeyboard.resources?.displayMetrics
            )
            .toInt()

        // Conclude whether the keyboard is shown or not.
        this@setButtonForShowHideOnKeyboard.getWindowVisibleDisplayFrame(r)
        val heightDiff: Int =
            this@setButtonForShowHideOnKeyboard.rootView?.height.nullSafety() - (r.bottom - r.top)
        val isShown = heightDiff >= estimatedKeyboardHeight

        if (isShown) {
            button?.hideView()
        } else {
            button?.showView()
        }
    }
}

fun AppCompatAutoCompleteTextView?.setViewForProperDrowDown(){
    this?.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
        if (hasFocus){
            this@setViewForProperDrowDown?.showDropDown()
        }
    }
}

fun View.enableView() {
    this.alpha = 1.0f
    this.isEnabled = true
}

fun View.disableView() {
    this.alpha = 0.5f
    this.isEnabled = false
}

fun AppCompatAutoCompleteTextView.textWatcherForAutoCompleteTextView(onTextChanged: (String) -> Unit)  {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            onTextChanged(s.toString())
        }
        override fun afterTextChanged(s: Editable) {

        }
    })
}

fun AppCompatAutoCompleteTextView.setListOnlyDropDown(
    context: Context,
    textInputLayout: TextInputLayout? = null
) {
//    this.addTextChangedListener(
//        beforeTextChanged = { charSequence, start, count, after ->
//
//        },
//        onTextChanged = { charSequence, start, before, count ->
//
//        },
//        afterTextChanged = { editable ->
//
//        }
//    )
    this.addTextChangedListener { editable ->
//        val input = editable?.toString().orEmpty()
//        val adapter = this.adapter ?: return@addTextChangedListener
//
//        val matched = (0 until adapter.count).asSequence()
//            .mapNotNull { adapter.getItem(it)?.toString() }
//            .any { input.contains(it, ignoreCase = true) }

        if (isItemFromTheList(editable.toString())) {
            //this.background = ContextCompat.getDrawable(context, R.drawable.round_corner_shape_without_fill_thin_grey)
            if (textInputLayout != null){
                textInputLayout.error = null
            }
        } else {
            //this.background = ContextCompat.getDrawable(context, R.drawable.round_corner_shape_without_fill_thin_red)
            //this.error = this.context.getString(R.string.error_desc_please_select_or_enter_a_valid_option_from_the_list)

            if (textInputLayout != null){
                textInputLayout.error = this.context.getString(R.string.error_desc_please_select_or_enter_a_valid_option_from_the_list)
            }

            // Clear text after the current change to avoid triggering this listener recursively
            //this.post { this.setText("") }

            // Vibrate safely (use VibrationEffect when available)
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            vibrator?.vibrate(android.os.VibrationEffect.createOneShot(200, android.os.VibrationEffect.DEFAULT_AMPLITUDE))
        }
    }


//    this.onFocusChangeListener = OnFocusChangeListener { view, b ->
//        if (!b) {
//            // on focus off
//            val str = this.text.toString()
//            val listAdapter = this.adapter
//            for (i in 0 until listAdapter.count) {
//                val temp = listAdapter.getItem(i).toString()
//                if (str.contains(temp, ignoreCase = true)) {
//                    this.background =
//                        context.getDrawable(R.drawable.round_corner_shape_without_fill_thin_grey)
//                    return@OnFocusChangeListener
//                }
//            }
//            this.background =
//                context.getDrawable(R.drawable.round_corner_shape_without_fill_thin_red)
//            this.setText("")
//            val vibe = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
//            vibe.vibrate(200) // 50 is time in ms
//        }
//    }
}

fun AppCompatAutoCompleteTextView.isItemFromTheList(value: String?): Boolean {
    val input = value.orEmpty()
    val adapter = this.adapter ?: return false

    return (0 until adapter.count).asSequence().mapNotNull { adapter.getItem(it)?.toString() }
        .any { input.contains(it, ignoreCase = true) }
}

//Function that can disable the ability of typing in AppCompatAutoCompleteTextView
//fun AppCompatAutoCompleteTextView.setDropDownListOnly() {
//    this.setOnKeyListener { v, keyCode, event -> true }
//    this.isLongClickable = false
//    this.setTextIsSelectable(false)
//}

fun AppCompatAutoCompleteTextView.setDropDownListOnly() {
    // Make it non-editable but still clickable
    keyListener = null
    isFocusable = false
    isFocusableInTouchMode = false
    isCursorVisible = false
    isLongClickable = false
    isClickable = true // still allow showing dropdown
}


fun TextInputLayout.setupHintAndPlaceholder(
    hintText: String,
    placeholderText: String,
    hintColorRes: Int = R.color.black_heading,
    placeholderColorRes: Int = R.color.black_heading,
    hintFontRes: Int = R.font.poppins_regular,
    placeholderFontRes: Int = R.font.poppins_regular
) {
    this.hint = hintText
    this.placeholderText = placeholderText
    this.isHintEnabled = true
    this.isHintAnimationEnabled = false // show both immediately

    // ---- Apply hint text appearance ----
//    this.setHintTextAppearance(com.google.android.material.R.style.TextAppearance_MaterialComponents_Caption)
//    this.findViewById<TextView?>(com.google.android.material.R.id.textinput_placeholder)?.apply {
//        setTextColor(ContextCompat.getColor(context, placeholderColorRes))
//        typeface = resources.getFont(placeholderFontRes)
//    }
//
//    // ---- Apply placeholder text appearance ----
//    this.setPlaceholderTextAppearance(com.google.android.material.R.style.TextAppearance_MaterialComponents_Body2)
//    this.findViewById<TextView?>(com.google.android.material.R.id.textinput_label)?.apply {
//        setTextColor(ContextCompat.getColor(context, hintColorRes))
//        typeface = resources.getFont(hintFontRes)
//    }
}

fun RecyclerView.addGrayDivider() {
    val divider = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
    divider.setDrawable(ContextCompat.getDrawable(this.context, R.drawable.recycler_divider)!!)
    this.addItemDecoration(divider)
}

/**
 * Extension to dynamically set floating hint, placeholder, colors, and font styles
 * for a TextInputLayout with AppCompatAutoCompleteTextView.
 *
 * @param floatingHint Text shown permanently as floating label (null = disabled)
 * @param placeholder Text shown inside field when empty (null = unchanged)
 * @param hintColorRes Optional color resource for floating hint
 * @param placeholderColorRes Optional color resource for placeholder text
 * @param hintTypeface Optional custom font for floating hint
 * @param placeholderTypeface Optional custom font for placeholder text
 */
fun TextInputLayout.setFloatingHintAndPlaceholder(
    floatingHint: String? = null,
    placeholder: String? = null,
    hintColorRes: Int? = null,
    placeholderColorRes: Int? = null,
    hintTypeface: Typeface? = null,
    placeholderTypeface: Typeface? = null
) {
    // ----- Setup Floating Hint -----
    val editTextView = editText as? AppCompatAutoCompleteTextView ?: return

    val hasFloating = !floatingHint.isNullOrBlank()

    // Always enable hint if floatingHint is provided so TextInputLayout can show a floating label
    isHintEnabled = hasFloating
    if (hasFloating) hint = floatingHint

    // Apply floating hint color (if provided)
    hintColorRes?.let {
        defaultHintTextColor = ContextCompat.getColorStateList(context, it)
    }

    // Apply floating hint font style (if provided)
    hintTypeface?.let { typeface = it }

    // ----- Setup Placeholder: use TextInputLayout.placeholderText -----
    this.placeholderText = placeholder ?: null

    // Placeholder color via inner EditText (best-effort)
    placeholderColorRes?.let {
        editTextView.setHintTextColor(ContextCompat.getColor(context, it))
    }

    placeholderTypeface?.let {
        editTextView.typeface = it
    }

    // Clear inner hint to avoid duplicate text; we'll manage it from the layout
    editTextView.hint = null

    // Helper to update floating state. If there's text, ensure the floating label is visible.
    fun updateFloatingState() {
        val hasText = !editTextView.text.isNullOrEmpty() && editTextView.text.toString().trim().isNotEmpty()

        if (hasFloating) {
            // Force the TextInputLayout to show the floating label when there's text
            if (hasText) {
                // force a re-evaluation of the hint so the collapsed (floating) label shows
                isHintEnabled = false
                hint = floatingHint
                isHintEnabled = true

                // clear inner hint so only floating label is visible
                editTextView.hint = null

                // ensure the layout updates UI immediately
                this.requestLayout()
                this.post { this.requestLayout() }

                // Some platform/device combinations need a short delayed toggle to force the
                // collapsing text to render as floating even when not focused. Do a small
                // postDelayed to re-toggle hint enabled.
                this.postDelayed({
                    try {
                        isHintEnabled = false
                        hint = floatingHint
                        isHintEnabled = true
                    } catch (_: Throwable) {
                    }
                }, 40L)
            } else {
                // No text: show placeholder inline by keeping the TextInputLayout hint enabled but
                // setting the inner EditText hint to the placeholder for inline text
                editTextView.hint = placeholder

                // Keep hint enabled so when user types the label will float automatically
                isHintEnabled = true
                hint = floatingHint

                this.requestLayout()
            }
        } else {
            // No floating hint requested: ensure placeholder is shown in editText
            isHintEnabled = false
            editTextView.hint = placeholder
            this.requestLayout()
        }
    }

    // initial state
    updateFloatingState()
    // Some platforms require a posted re-evaluation to pick up the floating state if text was
    // already set on the EditText before this helper was called.
    this.post { updateFloatingState() }

    // react to focus changes
    editTextView.setOnFocusChangeListener { _, _ -> updateFloatingState() }

    // react to text changes
    editTextView.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            updateFloatingState()
        }
        override fun afterTextChanged(s: Editable?) {}
    })
}

/**
 * Force TextInputLayout to re-evaluate and show the floating label if the inner EditText has text.
 * Call this after you programmatically set text on the EditText to ensure the floating label
 * renders even when the field does not have focus.
 */
fun TextInputLayout.forceShowFloating() {
    try {
        val currentHint = hint
        // toggle to force re-evaluation
        isHintEnabled = false
        hint = currentHint
        isHintEnabled = true
        requestLayout()
        post { requestLayout() }
        postDelayed({
            try {
                isHintEnabled = false
                hint = currentHint
                isHintEnabled = true
                requestLayout()
            } catch (_: Throwable) {
            }
        }, 40L)
    } catch (_: Throwable) {
    }
}

/**
 * How to use
 * mTextInputShift?.setup(
 *             hintText = "Shift",
 *             placeholder = "Select or Type"
 *         )
 *
 *         mTextInputLayoutTime?.setup(
 *             isFloating = false,
 *             endIconModeType = null,
 *             placeholder ="Hearing Time"
 *         )
 */
@SuppressLint("UseCompatLoadingForDrawables")
fun TextInputLayout.setupTextInputLayout(
    isEditTextInside: Boolean? = false,
    isFloating : Boolean? = true,
    hintEnabled: Boolean? = true,
    hintText: String? = null,
    placeholder: String? = null,
    endIconModeType: Int? = TextInputLayout.END_ICON_DROPDOWN_MENU,
    endIcon: Int? = null,
) {
    isHintEnabled = hintEnabled.nullSafety()

    if (!hintText.isNullOrBlank()) {
        // If we have a hint, use it (floating label will be available)
        this.hint = hintText
    } else {
        // No hint provided: ensure there is no floating hint and show placeholder only
        // Setting hint = null prevents a floating label
        this.hint = null
    }

    if (!isFloating.nullSafety()){
        this.editText?.hint = placeholder
    }else{
        // placeholder: only set if provided
        if (!placeholder.isNullOrBlank()) {
            this.placeholderText = placeholder
        } else {
            this.placeholderText = null
        }
    }

    if (!isEditTextInside.nullSafety()){
        if (endIconModeType != null) {
            endIconMode = endIconModeType
            setEndIconTintList(ContextCompat.getColorStateList(context, R.color.colorPrimaryDark))
            // ensure the end icon is visible (some Material versions default to hidden)

            if (endIcon != null){
                endIconDrawable = this.context.getDrawable(endIcon)
//                setEndIconOnClickListener {
//
//                }
            }


            try {
                isEndIconVisible = true
            } catch (_: Throwable) { }
        } else {
            // hide the icon completely
            try {
                endIconMode = TextInputLayout.END_ICON_NONE
                endIconDrawable = null
                // explicit visibility toggle helps on some device/library combinations
                isEndIconVisible = false
                // also clear tint to avoid lingering coloring state
                setEndIconTintList(null)
            } catch (_: Throwable) {
                // best-effort: ignore if methods/properties unavailable on older lib versions
                try {
                    endIconMode = TextInputLayout.END_ICON_NONE
                    endIconDrawable = null
                } catch (_: Throwable) { }
            }
        }
    }
}