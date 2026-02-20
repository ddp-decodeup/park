package com.parkloyalty.lpr.scan.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.fingerprint.FingerprintManager
import android.os.Build
import android.os.CancellationSignal
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.parkloyalty.lpr.scan.R
import com.parkloyalty.lpr.scan.interfaces.FingerDetector

@RequiresApi(api = Build.VERSION_CODES.M)
class FingerprintHandler     // Constructor
    (private val context: Context, private val fingerDetector: FingerDetector) :
    FingerprintManager.AuthenticationCallback() {
    @RequiresApi(api = Build.VERSION_CODES.M)
    fun startAuth(manager: FingerprintManager, cryptoObject: FingerprintManager.CryptoObject?) {
        try {
            val cancellationSignal = CancellationSignal()
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.USE_FINGERPRINT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            manager.authenticate(cryptoObject, cancellationSignal, 0, this, null)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onAuthenticationError(errMsgId: Int, errString: CharSequence) {
        update(context.getString(R.string.finger_print_failed), false)
    }

    override fun onAuthenticationHelp(helpMsgId: Int, helpString: CharSequence) {
        update(context.getString(R.string.finger_print_failed), false)
    }

    override fun onAuthenticationFailed() {
        update(context.getString(R.string.finger_print_failed), false)
    }

    override fun onAuthenticationSucceeded(result: FingerprintManager.AuthenticationResult) {
        update(context.getString(R.string.finger_print_succeed), true)
    }

    private fun update(e: String, success: Boolean) {
        fingerDetector.fingerDetected(e, success)
    }
}