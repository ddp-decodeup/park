package com.parkloyalty.lpr.scan.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo

object NetworkCheck {
    /**
     * this method check internet available or not in app
     *
     * @param context context
     * @return true if internet available else return false
     */
    @JvmStatic
    fun isInternetAvailable(context: Context): Boolean {
        val connectivity =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        var isActiveNetworkConnected = false
        if (connectivity != null) {
            val info = connectivity.activeNetworkInfo
            if (info != null) {
                isActiveNetworkConnected =
                    info.state == NetworkInfo.State.CONNECTED || info.state == NetworkInfo.State.CONNECTING
            }
        } else {
            isActiveNetworkConnected = false
        }
        return isActiveNetworkConnected
    }
}