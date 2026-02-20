//Copyright Imense Ltd 2020. Unauthorised usage or distribution strictly prohibited.
package com.parkloyalty.lpr.scan.util.imense

import android.annotation.SuppressLint
import android.util.Base64
import android.util.Log
import com.parkloyalty.lpr.scan.interfaces.SharedPrefKey
import com.parkloyalty.lpr.scan.util.SharedPref
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.net.URL
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.inject.Inject
import javax.inject.Singleton
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSession
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

interface ImmenseLicenceInterface {
    fun onLicenseFetchedAndSaved()
    fun onLicenseFetchFailed()
}

/**
 * Modernized replacement for the legacy AsyncTask-based license fetcher.
 * - Uses coroutines (suspend function) and HttpsURLConnection instead of Apache HttpClient.
 * - Avoids storing Activity/context references (no leaks).
 * - Preserves original behaviour of accepting any server certificate (insecure by design,
 *   left intentionally to keep compatibility with original implementation).
 * - Updates SharedPref with license results and optionally notifies a TakeLicenceInterface callback
 *   on the Main dispatcher.
 */
@Singleton
class NewImenseLicenseServer @Inject constructor(
    private val sharedPref: SharedPref
) {

    private var licenseKey: String? = null
    private var serverResponseMessage: String? = null

    /**
     * Fetch license from the Imense license server for the given device UID.
     * Returns true when a valid license (VALID_KEY_LENGTH) was obtained and stored.
     * This function is safe to call from a coroutine scope.
     */
    @SuppressLint("TrustAllX509TrustManager")
    suspend fun fetchAndSaveLicense(deviceUid: String?, callback: ImmenseLicenceInterface) {
        val success = withContext(Dispatchers.IO) {
            try {
                if (deviceUid.isNullOrBlank() || deviceUid.length < VALID_UID_LENGTH) {
                    Log.w(TAG, "Invalid UID provided to NewImenseLicenseServer")
                    sharedPref.write(SharedPrefKey.IS_LPR_LICENCE, false)
                    return@withContext false
                }

                val urlStr = ImenseLicenseServerURL + deviceUid
                val url = URL(urlStr)
                val conn = (url.openConnection() as HttpsURLConnection)

                // Install permissive trust manager (keeps original behaviour) - insecure
                val trustAllCerts = arrayOf<TrustManager>(@SuppressLint("CustomX509TrustManager")
                object : X509TrustManager {
                    override fun checkClientTrusted(
                        chain: Array<X509Certificate>,
                        authType: String
                    ) {
                    }

                    override fun checkServerTrusted(
                        chain: Array<X509Certificate>,
                        authType: String
                    ) {
                    }

                    override fun getAcceptedIssuers(): Array<X509Certificate> =
                        arrayOf()
                })
                val sslContext = SSLContext.getInstance("TLS")
                sslContext.init(null, trustAllCerts, SecureRandom())
                conn.sslSocketFactory = sslContext.socketFactory

                // Accept any hostname (insecure but mirrors previous behaviour)
                conn.hostnameVerifier = HostnameVerifier { _: String?, _: SSLSession? -> true }

                conn.requestMethod = "GET"
                conn.connectTimeout = 15_000
                conn.readTimeout = 15_000

                // Basic auth header using obfuscated credentials
                val auth = "$ImenseLicenseServerLogin:$ImenseLicenseServerPassword"
                conn.setRequestProperty(
                    "Authorization",
                    "Basic " + Base64.encodeToString(auth.toByteArray(), Base64.NO_WRAP)
                )

                conn.connect()

                val responseCode = conn.responseCode

                // Choose input stream depending on response code
                val inputStream = if (responseCode >= 400) conn.errorStream else conn.inputStream

                val res = inputStream.bufferedReader().use(BufferedReader::readText).trim()

                conn.disconnect()

                if (res.length == VALID_KEY_LENGTH && !res.startsWith("Error:")) {
                    licenseKey = res
                    sharedPref.write(SharedPrefKey.IS_LPR_LICENCE, true)
                    sharedPref.write(SharedPrefKey.LPR_LICENCE, licenseKey)
                    true
                } else {
                    serverResponseMessage = res
                    sharedPref.write(SharedPrefKey.IS_LPR_LICENCE, false)
                    Log.w(
                        TAG,
                        "License server returned error or unexpected response: $res (http=$responseCode)"
                    )
                    false
                }
            } catch (ex: Exception) {
                Log.e(TAG, "Error fetching license from server", ex)
                sharedPref.write(SharedPrefKey.IS_LPR_LICENCE, false)
                false
            }
        }

        // invoke callback on Main dispatcher if provided
        withContext(Dispatchers.Main) {
            try {
                if (success) callback.onLicenseFetchedAndSaved()
                else callback.onLicenseFetchFailed()
            } catch (e: Exception) {
                callback.onLicenseFetchFailed()
                Log.w(TAG, "Error invoking TakeLicenceInterface callback: ${e.message}")
            }
        }
    }

    companion object {
        private const val TAG = "NewImenseLicenseServer"

        // Base64-obfuscated credentials / URL (decoded at runtime)
        private val ImenseLicenseServerLogin = decodeString("TG9naW42MTA5MTU=")
        private val ImenseLicenseServerPassword = decodeString("b2prNjR2cm0wYzMw")
        private val ImenseLicenseServerURL = decodeString("aHR0cHM6Ly9saWNlbnNpbmcuaW1lbnNlLmNvbS9rZXlnZW4yL2luZGV4LmpzcD91aWQ9")

        const val VALID_KEY_LENGTH = 32
        const val VALID_UID_LENGTH = 32

        private fun decodeString(input: String): String {
            return try {
                val out = Base64.decode(input, Base64.DEFAULT)
                String(out)
            } catch (e: IllegalArgumentException) {
                Log.w(TAG, "Invalid base64 input: $input - ${e.message}")
                ""
            }
        }
    }
}