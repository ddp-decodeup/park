package com.parkloyalty.lpr.scan.util

import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.text.SpannableString
import android.text.Spanned
import android.text.style.TtsSpan
import android.view.MotionEvent
import android.view.View
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityManager
import android.widget.AutoCompleteTextView
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.AccessibilityDelegateCompat
import androidx.core.view.ViewCompat
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat.AccessibilityActionCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.parkloyalty.lpr.scan.R
import com.parkloyalty.lpr.scan.extensions.nullSafety

fun setAccessibilityForTextInputLayoutCrossButtons(
    context: Context,
    textInputLayout: TextInputLayout?
) {
    val iconView = textInputLayout?.findViewById<ImageButton>(
        com.google.android.material.R.id.text_input_start_icon
    )

    iconView?.let { button ->
        ViewCompat.setAccessibilityDelegate(button, object : AccessibilityDelegateCompat() {
            override fun onInitializeAccessibilityNodeInfo(
                host: View,
                info: AccessibilityNodeInfoCompat
            ) {
                super.onInitializeAccessibilityNodeInfo(host, info)

                // Force properties to suppress "checked/ticked"
                info.isCheckable = false
                info.isChecked = false

                // Replace announcement text
                info.contentDescription =
                    context.getString(R.string.ada_content_description_clear_text_double_tap_to_clear)
                info.className = context.getString(R.string.ada_class_name_image_button)
                info.roleDescription = context.getString(R.string.ada_role_empty)
            }
        })
    }
}

fun setAccessibilityForTextInputLayoutDropdownButtons(
    context: Context,
    textInputLayout: TextInputLayout?
) {
    val iconView = textInputLayout?.findViewById<ImageButton>(
        com.google.android.material.R.id.text_input_end_icon
    )

    val autoCompleteTextView = textInputLayout?.editText as? AutoCompleteTextView

    if (iconView != null && autoCompleteTextView != null) {
        ViewCompat.setAccessibilityDelegate(iconView, object : AccessibilityDelegateCompat() {
            override fun onInitializeAccessibilityNodeInfo(
                host: View,
                info: AccessibilityNodeInfoCompat
            ) {
                super.onInitializeAccessibilityNodeInfo(host, info)

                info.isCheckable = false
                info.isChecked = false

                val isDropdownShowing = autoCompleteTextView.isPopupShowing

                info.contentDescription = if (isDropdownShowing) {
                    context.getString(R.string.ada_content_description_dropdown_button_double_tap_to_close_dropdown)
                } else {
                    context.getString(R.string.ada_content_description_dropdown_button_double_tap_to_open_dropdown)
                }

                info.className = context.getString(R.string.ada_class_name_image_button)
                info.roleDescription = context.getString(R.string.ada_role_empty)
            }
        })
    }
}

fun setDoNothingAccessibilityForTextInputLayoutDropdownButtons(
    context: Context,
    textInputLayout: TextInputLayout?
) {
    val iconView = textInputLayout?.findViewById<ImageButton>(
        com.google.android.material.R.id.text_input_end_icon
    )

    val autoCompleteTextView = textInputLayout?.editText as? AutoCompleteTextView

    if (iconView != null && autoCompleteTextView != null) {
        ViewCompat.setAccessibilityDelegate(iconView, object : AccessibilityDelegateCompat() {
            override fun onInitializeAccessibilityNodeInfo(
                host: View,
                info: AccessibilityNodeInfoCompat
            ) {
                super.onInitializeAccessibilityNodeInfo(host, info)

                info.isCheckable = false
                info.isChecked = false

                val isDropdownShowing = autoCompleteTextView.isPopupShowing

                info.contentDescription = ""

                info.className = ""
                info.roleDescription = ""
            }
        })
    }
}


/**
 * Marks a view as heading for screen readers like TalkBack.
 */
fun setAsAccessibilityHeading(view: View) {
    ViewCompat.setAccessibilityHeading(view, true)
}


fun View.setCustomAccessibility(
    contentDescription: String? = null,
    role: String? = null,
    actionLabel: String? = null
) {
    ViewCompat.setAccessibilityDelegate(this, object :
        AccessibilityDelegateCompat() {
        override fun onInitializeAccessibilityNodeInfo(
            host: View,
            info: AccessibilityNodeInfoCompat
        ) {
            super.onInitializeAccessibilityNodeInfo(host, info)
            if (!contentDescription.isNullOrEmpty()) {
                info.contentDescription = contentDescription
            }
            if (!role.isNullOrEmpty()) {
                info.roleDescription = role
                info.className = role
            }

            if (!actionLabel.isNullOrEmpty()) {
                val customAction = AccessibilityNodeInfoCompat.AccessibilityActionCompat(
                    AccessibilityNodeInfoCompat.ACTION_CLICK,
                    actionLabel
                )
                info.addAction(customAction)
            }
        }
    })
}

fun TextView.setAccessibilityRoleAsAction(role: String?) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        ViewCompat.setAccessibilityDelegate(
            this@setAccessibilityRoleAsAction,
            object : AccessibilityDelegateCompat() {
                override fun onInitializeAccessibilityNodeInfo(
                    host: View,
                    info: AccessibilityNodeInfoCompat
                ) {
                    super.onInitializeAccessibilityNodeInfo(host, info)
                    info.roleDescription = role // Optional: For older screen readers

                    // Set role as link
                    info.className = android.widget.TextView::class.java.name
                    info.isClickable = true
                }
            })
    } else {
        // Fallback for older versions
        this@setAccessibilityRoleAsAction.contentDescription =
            "${this@setAccessibilityRoleAsAction.text}, $role"
    }
}

fun announceTextInputLayoutError(
    textInputLayout: TextInputLayout,
    errorMessage: String
) {
    textInputLayout.error = errorMessage

    // Get the EditText inside
    val editText = textInputLayout.editText ?: return

    // Delay required to ensure error is registered in layout and focus settles
    editText.postDelayed({
        editText.requestFocus()

        // This ensures TalkBack announces the error message
        editText.announceForAccessibility(errorMessage)
    }, 100)
}

fun TextInputLayout.setAccessibilityForTextInputLayoutWithHintOnly() {
    val editText = this.editText as? TextInputEditText ?: return

    ViewCompat.setAccessibilityDelegate(editText, object : AccessibilityDelegateCompat() {
        override fun onInitializeAccessibilityNodeInfo(
            host: View,
            info: AccessibilityNodeInfoCompat
        ) {
            super.onInitializeAccessibilityNodeInfo(host, info)

            val hint = this@setAccessibilityForTextInputLayoutWithHintOnly.hint?.toString() ?: ""
            val currentText = editText.text?.toString() ?: ""

            // Tell TalkBack what to read
            info.text = if (currentText.isEmpty()) {
                hint
            } else {
                "$hint, $currentText"
            }

            info.hintText = hint
            info.isShowingHintText = currentText.isEmpty()
        }
    })
}

object AccessibilityUtil {
    /**
     * Announces a message using accessibility services like TalkBack.
     * Helpful for visually impaired users to get real-time feedback.
     */
    fun announceForAccessibility(view: View, message: String) {
        view.announceForAccessibility(message)
    }

    /**
     * Checks if any accessibility service (like TalkBack) is enabled.
     */
    fun isAccessibilityEnabled(context: Context): Boolean {
        val am = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        return am.isEnabled
    }

    /**
     * Checks if touch exploration (like TalkBack gestures) is enabled.
     */
    fun isTouchExplorationEnabled(context: Context): Boolean {
        val am = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        return am.isTouchExplorationEnabled
    }

    /**
     * Sends a custom accessibility event like TYPE_VIEW_FOCUSED, TYPE_ANNOUNCEMENT, etc.
     */
    fun sendAccessibilityEvent(view: View, eventType: Int) {
        val event = AccessibilityEvent.obtain(eventType)
        view.onInitializeAccessibilityEvent(event)
        view.dispatchPopulateAccessibilityEvent(event)
        view.parent?.requestSendAccessibilityEvent(view, event)
    }

    /**
     * Sets an accessibility label (content description) for assistive tech users.
     */
    fun setAccessibilityLabel(view: View, label: String) {
        view.contentDescription = label
    }

    /**
     * Adds a custom accessibility action to a view.
     *
     * @param view The target view.
     * @param label The description of the custom action (e.g., "Double tap to retry").
     * @param actionId A unique ID for the action (you can use custom int > 0).
     * @param action Lambda to run when the action is triggered via screen reader.
     */
    fun addCustomAccessibilityAction(
        view: View,
        label: String,
        action: () -> Boolean
    ) {
        val actionCompat = AccessibilityActionCompat(
            AccessibilityNodeInfoCompat.ACTION_CLICK, // or use a custom ID > 0
            label
        )

        ViewCompat.replaceAccessibilityAction(
            view,
            actionCompat, label
        ) { _, _ ->
            action()
        }
    }


    /**
     * Sets both a label (contentDescription) and a custom accessibility action for a view.
     *
     * @param view The target view.
     * @param label Spoken description for screen readers (e.g., "Retry button").
     * @param actionLabel Label for the custom action (e.g., "Double tap to retry").
     * @param onAction Lambda to run when custom action is triggered via accessibility (returns true if handled).
     */
    fun addAccessibilityLabelAndAction(
        view: View,
        label: String,
        actionLabel: String,
        onAction: () -> Boolean
    ) {
        // Set spoken label
        view.contentDescription = label
        view.importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_YES

        // Create and bind custom accessibility action
        val customAction = AccessibilityNodeInfoCompat.AccessibilityActionCompat(
            AccessibilityNodeInfoCompat.ACTION_CLICK,
            actionLabel
        )

        ViewCompat.replaceAccessibilityAction(view, customAction, null) { _, _ ->
            onAction()
        }
    }


    /**
     * Sends a live region update (used when dynamic content changes).
     */
    fun notifyLiveRegionChanged(view: View) {
        ViewCompat.setAccessibilityLiveRegion(view, ViewCompat.ACCESSIBILITY_LIVE_REGION_POLITE)
        view.invalidate()
    }

    /**
     * Programmatically focuses a view for accessibility (spoken focus).
     */
    fun requestAccessibilityFocus(view: View) {
        view.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED)
    }

    /**
     * Sets whether the view should be ignored by accessibility services.
     */
    fun setImportantForAccessibility(view: View, isImportant: Boolean) {
        ViewCompat.setImportantForAccessibility(
            view,
            if (isImportant)
                ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_YES
            else
                ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_NO
        )
    }

    /**
     * Sets a view to be a live region with assertive (interruptive) announcement.
     */
    fun setAssertiveLiveRegion(view: View) {
        ViewCompat.setAccessibilityLiveRegion(view, ViewCompat.ACCESSIBILITY_LIVE_REGION_ASSERTIVE)
    }

    /**
     * Sets accessibility state description (introduced in API 30)
     * e.g., “checked” or “unchecked” for custom switches
     */
    fun setStateDescription(view: View, stateDescription: String) {
        ViewCompat.setStateDescription(view, stateDescription)
    }

    //AUTO


    //Working
    fun setupAccessibleDropdown(autoComplete: AppCompatAutoCompleteTextView) {
        autoComplete.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                if (!autoComplete.isPopupShowing) {
                    autoComplete.showDropDown()

                    // Delay and then send accessibility focus event
                    Handler(Looper.getMainLooper()).postDelayed({
                        focusFirstDropdownItem(autoComplete)
                    }, 300) // enough time for dropdown to render
                }
            }
            false
        }
    }

    private fun focusFirstDropdownItem(autoComplete: AppCompatAutoCompleteTextView) {
        try {
            val popupField = AutoCompleteTextView::class.java.getDeclaredField("mPopup")
            popupField.isAccessible = true
            val listPopup = popupField.get(autoComplete)
            val listViewField = listPopup.javaClass.getDeclaredField("mDropDownList")
            listViewField.isAccessible = true
            val listView = listViewField.get(listPopup) as? ListView

            listView?.post {
                if (listView.childCount > 0) {
                    listView.setSelection(0)
                    listView.requestFocus()

                    // Send accessibility focus to the first child
                    val am =
                        autoComplete.context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
                    if (am.isEnabled) {
//                        val event = AccessibilityEvent.obtain(AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUSED)
//                        event.source = listView.getChildAt(0)
                        listView.getChildAt(0)
                            ?.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED)
                    }
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun setupAutoCompleteAccessibility(
        view: AppCompatAutoCompleteTextView,
        label: String = ""
    ) {
        view.contentDescription = label
        view.importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_YES
        //view.setHint("Type or select")

        // Live region to announce dropdown changes
        //ViewCompat.setAccessibilityLiveRegion(view, ViewCompat.ACCESSIBILITY_LIVE_REGION_POLITE)

        // Add custom accessibility action (double tap to expand/collapse)
        ViewCompat.replaceAccessibilityAction(
            view,
            AccessibilityActionCompat(
                AccessibilityNodeInfoCompat.ACTION_CLICK,
                "Double tap to expand or collapse list"
            ), label
        ) { _, _ ->
            toggleDropdown(view)
            true
        }

        // Add drawable click handling (manually handle touch)
        view.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawable = view.compoundDrawablesRelative[2] ?: return@setOnTouchListener false
                if (event.x >= (view.width - view.paddingEnd - drawable.bounds.width())) {
                    toggleDropdown(view)
                    announceDropdownState(view)
                    return@setOnTouchListener true
                }
            }
            false
        }
    }

    private fun toggleDropdown(view: AppCompatAutoCompleteTextView) {
        if (view.isPopupShowing) {
            view.dismissDropDown()
        } else {
            view.showDropDown()
        }
    }

    fun announceDropdownState(view: View) {
        val am =
            view.context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        if (am.isEnabled) {
            val event = AccessibilityEvent.obtain(AccessibilityEvent.TYPE_ANNOUNCEMENT)
            event.text.add(
                if ((view as? AutoCompleteTextView)?.isPopupShowing == true)
                    "Options expanded"
                else
                    "Options collapsed"
            )
            event.className = view.javaClass.name
            event.packageName = view.context.packageName
            am.sendAccessibilityEvent(event)
        }
    }

    fun AppCompatTextView.setCustomContentDescription() {
        if (this.text.toString().nullSafety().trim().isEmpty()) {
            this.text = " "
            this.contentDescription = this.context.getString(R.string.ada_content_description_not_available)
        } else {
            this.contentDescription = this.text.toString()
        }
    }

    fun AppCompatAutoCompleteTextView.setCustomContentDescription() {
        if (this.text.toString().nullSafety().trim().isEmpty()) {
            //this.text = " "
            this.contentDescription = this.context.getString(R.string.ada_content_description_not_available)
        } else {
            this.contentDescription = this.text.toString()
        }
    }

    fun AppCompatTextView.setCustomAbbreviatedContentDescription() {
        val rawText = this.text.toString()
        val spelledOut = rawText.toCharArray().joinToString(" ")  // e.g. "N Y"

        val spannable = SpannableString(rawText)
        val ttsSpan = TtsSpan.VerbatimBuilder(spelledOut).build()
        spannable.setSpan(ttsSpan, 0, rawText.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        // Re-apply the styled text to trigger TalkBack override
        this.setText(spannable, TextView.BufferType.SPANNABLE)
    }

    fun LinearLayoutCompat.setAccessibilityForCheckboxUnderLinearLayout(
        checkBox: AppCompatCheckBox,
        labelView: TextView
    ) {
        ViewCompat.setAccessibilityDelegate(this, object : AccessibilityDelegateCompat() {
            override fun onInitializeAccessibilityNodeInfo(
                host: View,
                info: AccessibilityNodeInfoCompat
            ) {
                super.onInitializeAccessibilityNodeInfo(host, info)

                val label = labelView.text?.toString() ?: ""
                val checked = checkBox.isChecked

                info.className = CheckBox::class.java.name // announce as checkbox
                info.text = label
                info.isCheckable = true
                info.isChecked = checked
            }
        })

        // Ensure toggle works when user clicks anywhere in the layout
        this.setOnClickListener {
            checkBox.isChecked = !checkBox.isChecked
            this.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_CLICKED)
        }
    }
}
