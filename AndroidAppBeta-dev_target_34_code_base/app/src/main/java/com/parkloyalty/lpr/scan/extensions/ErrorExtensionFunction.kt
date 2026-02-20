package com.parkloyalty.lpr.scan.extensions


import android.animation.ObjectAnimator
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.animation.CycleInterpolator
import com.google.android.material.textfield.TextInputLayout
import com.parkloyalty.lpr.scan.util.announceTextInputLayoutError

// Keys for view tags (avoid collisions)
private const val TAG_SHAKE_ANIM = -1001
private const val TAG_ERROR_CLEAR_WATCHER = -1002

/**
 * Show error text on TextInputLayout and play a shake + vibration effect.
 * Very cheap to call repeatedly — animator is cached on the view.
 */
fun TextInputLayout.showErrorWithShake(
    errorText: String,
    shakeAmplitudeDp: Float = 8f,   // how far to shake (dp)
    shakeCycles: Float = 6f,        // how many back-and-forth cycles
    duration: Long = 350L,          // total duration ms
    vibrateMs: Long = 30L           // vibration duration
) {
    // Set error text (Material handles content and accessibility)
    this.error = errorText
    this.isErrorEnabled = true
    this.requestFocus()

    // Convert dp to pixels
    val density = context.resources.displayMetrics.density
    val amplitudePx = shakeAmplitudeDp * density

    // Cancel and reuse existing animator if any
    val existing = this.getTag(TAG_SHAKE_ANIM) as? ObjectAnimator
    existing?.let {
        if (it.isRunning) it.cancel()
    }

    // Build animator (translationX) and cache it
    val animator =
        ObjectAnimator.ofFloat(this, View.TRANSLATION_X, 0f, amplitudePx, -amplitudePx, 0f)
    animator.duration = duration
    animator.interpolator = CycleInterpolator(shakeCycles)
    // Optional: small listener to clear translation after animation
    animator.addUpdateListener {
        // nothing heavy here
    }

    // Cache animator for reuse
    this.setTag(TAG_SHAKE_ANIM, animator)
    animator.start()

    // Trigger vibration / haptic feedback
    playHaptic(this.context, vibrateMs)

    autoClearErrorOnInput()

    // Announce for accessibility
    announceTextInputLayoutError(this, errorText.nullSafety())
}

/** Clear error and any animations cleanly */
fun TextInputLayout.clearError() {
    this.error = null
    this.isErrorEnabled = false

    val existing = this.getTag(TAG_SHAKE_ANIM) as? ObjectAnimator
    existing?.let {
        if (it.isRunning) it.cancel()
    }
    // Reset translation in case
    this.translationX = 0f
}

fun TextInputLayout.autoClearErrorOnInput() {
    val editText = this.editText ?: return
    // Avoid adding multiple watchers
    val watcherKey = TAG_ERROR_CLEAR_WATCHER
    val existing = editText.getTag(watcherKey) as? TextWatcher
    if (existing != null) return

    val watcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            this@autoClearErrorOnInput.clearError()
        }
        override fun afterTextChanged(s: Editable?) {}
    }
    editText.addTextChangedListener(watcher)
    editText.setTag(watcherKey, watcher)
}

/** Play haptic vibration in the most compatible + efficient way */
private fun playHaptic(context: Context, vibrateMs: Long) {
    // Prefer Vibrator API when available for more control
    val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
    val vib: Vibrator? = vibratorManager?.defaultVibrator

    try {
        if (vib != null && vib.hasVibrator()) {
            vib.vibrate(
                VibrationEffect.createOneShot(
                    vibrateMs,
                    VibrationEffect.DEFAULT_AMPLITUDE
                )
            )
            return
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}