package com.parkloyalty.lpr.scan.util

import android.content.Context
import android.os.Vibrator
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils

/**
 * Created by neha on 26/9/18.
 */
class CustomAnimationUtil(private val context: Context) {
    private val vib: Vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    var animShake: Animation? = null
    fun showErrorEditTextAnimation(view: View, resID: Int) {
        val shake = AnimationUtils.loadAnimation(context, resID)
        view.startAnimation(shake)
        vib.vibrate(120)
    }

}