package com.parkloyalty.lpr.scan.locationservice

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Build
import com.parkloyalty.lpr.scan.extensions.logD
import com.parkloyalty.lpr.scan.extensions.nullSafety
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.Locale

object LocationUtils {
    suspend fun getAddressFromLatLng(context: Context, lat: Double, lng: Double): String? =
        //List<Address>?
        withContext(Dispatchers.IO) {
            val geocoder = Geocoder(context, Locale.getDefault())
            return@withContext try {
                suspendCancellableCoroutine { continuation ->
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        geocoder.getFromLocation(lat, lng, 1, object : Geocoder.GeocodeListener {
                            override fun onGeocode(addresses: List<Address>) {
                                val firstAddress =
                                    addresses.firstOrNull()?.getAddressLine(0).nullSafety()
                                logD("LocationUtils", "Addresses: $addresses")
                                logD("LocationUtils", "Addresses: $firstAddress")
                                continuation.resume(firstAddress) { cause, _, _ -> (cause) }
                            }

                            override fun onError(errorMessage: String?) {
                                logD("LocationUtils", "Error: $errorMessage")
                                continuation.resume(null) { cause, _, _ -> (cause) }
                            }
                        })
                    } else {
                        try {
                            val addresses = geocoder.getFromLocation(lat, lng, 1)
                            val firstAddress =
                                addresses?.firstOrNull()?.getAddressLine(0).nullSafety()

                            continuation.resume(firstAddress) { cause, _, _ -> (cause) }
                        } catch (e: IOException) {
                            e.printStackTrace()
                            continuation.resume(null) { cause, _, _ -> (cause) }
                        }
                    }
                }
            } catch (e: IOException) {
                logD("LocationUtils", "IOException: ${e.message}")
                e.printStackTrace()
                return@withContext null
            }
        }

//    suspend fun getAddressFromLatLng(context: Context, lat: Double, lng: Double): String? {
//        //List<Address>?
//        val geocoder = Geocoder(context, Locale.getDefault())
//        try {
//            val addresses = geocoder.getFromLocation(lat, lng, 1)
//            val firstAddress = addresses?.firstOrNull()?.getAddressLine(0).nullSafety()
//            return firstAddress
//        } catch (e: IOException) {
//            logD("LocationUtils", "IOException: ${e.message}")
//            e.printStackTrace()
//            return null
//        }
//    }

}

//How to use:
//lifecycleScope.launch {
//    showLoadingDialog()
//    val addresses = getAddressFromLatLng(context, lat, lng)
//    hideLoadingDialog()
//
//    if (!addresses.isNullOrEmpty()) {
//        val address = addresses[0].getAddressLine(0)
//        // Continue with address
//    } else {
//        showToast("Failed to fetch address")
//    }
//}