package com.parkloyalty.lpr.scan.doubangoultimatelpr.license

import android.content.Context
import com.parkloyalty.lpr.scan.util.DoubangoConstants.LICENSE_TOKEN_FILE_NAME
import com.parkloyalty.lpr.scan.util.DoubangoConstants.TIMEOUT_CONNECT
import com.parkloyalty.lpr.scan.util.DoubangoConstants.TIMEOUT_READ
import com.parkloyalty.lpr.scan.util.LogUtil.printLog
import org.doubango.ultimateAlpr.Sdk.UltAlprSdkEngine
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Callable
import java.util.concurrent.Executors

/**
 * License activator.
 */
object AlprLicenseActivator {
    /**
     * TAG used for the debug logs.
     */
    private val TAG = AlprLicenseActivator::class.java.toString()


    /**
     * Read data from the file.
     * @return Returns data in file at \ref path if succeed, otherwise empty string.
     */
    fun tokenData(path: String?): String {
        val text = StringBuilder()
        try {
            val reader = BufferedReader(FileReader(File(path)))
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                text.append(line)
            }
            reader.close()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
        }
        return text.toString()
    }

    /**
     * Returns path to the file containing the license token.
     * @param context Activity context.
     * @return Path to the file containing the license token if exist, otherwise empty string.
     */
    fun tokenFile(context: Context): String {
        val tokenFile = File(context.filesDir, LICENSE_TOKEN_FILE_NAME)
        return if (tokenFile.exists()) tokenFile.absolutePath else ""
    }

    /**
     * Function used to automatically activate the license on the current device. You must initialize
     * the engine before calling this function. After activation you must initialized the engine
     * again to provide the token info.
     * More information about the activation process can be found at https://www.doubango.org/SDKs/LicenseManager/docs/Jargon.html#activation.
     * @param context Activity context.
     * @param activationUrl Activation HTTPS URL. e.g. https://localhost:3600
     * @param masterOrSlaveKey Master or slave key. You must never ever share your master key
     * or include it in your application. Slaves are one-time activation keys
     * to be included in your application or sent to the end user for activation.
     * @param force Whether to force the activation even if the license file exists. Should be false to
     * avoid connecting to the server every time this function is called.
     * @return True if success, otherwise false.
     */
    fun activate(
        context: Context,
        activationUrl: String?,
        masterOrSlaveKey: String?,
        force: Boolean
    ): String? {
        val tokenFile = File(context.filesDir, LICENSE_TOKEN_FILE_NAME)
        if (tokenFile.exists()) {
            if (!force) {
                return tokenFile.absolutePath
            }
            printLog(
                TAG,
                "You should not force the activation. You'll be in big trouble if the server is down or the device offline."
            )
        }
        return try {
            Executors.newSingleThreadExecutor().submit(
                Callable {
                    if (activationUrl == null || activationUrl.isEmpty() || masterOrSlaveKey == null || masterOrSlaveKey.isEmpty()) {
                        printLog(TAG, "Activation url and master/slave key must not be null")
                        return@Callable ""
                    }
                    var pathToLicenseFile = ""
                    var url: URL? = null
                    var urlConnection: HttpURLConnection? = null
                    try {
                        url =
                            URL(activationUrl + (if (activationUrl[activationUrl.length - 1] == '/') "" else "/") + "activate")
                        urlConnection = url.openConnection() as HttpURLConnection
                        urlConnection.readTimeout = TIMEOUT_READ
                        urlConnection.connectTimeout = TIMEOUT_CONNECT
                        urlConnection.requestMethod = "POST"
                        urlConnection.doInput = true
                        urlConnection.doOutput = true
                        urlConnection.setRequestProperty(
                            "Content-Type",
                            "application/json; charset=UTF-8"
                        )
                        val outStream = urlConnection.outputStream
                        val writer = BufferedWriter(OutputStreamWriter(outStream, "UTF-8"))
                        val data = JSONObject()
                        data.put("masterOrSlaveKey", masterOrSlaveKey)
                        val result = UltAlprSdkEngine.requestRuntimeLicenseKey(true)
                        if (!result.isOK) {
                            throw AssertionError("Failed to request runtime key: " + result.phrase())
                        }
                        data.put("runtimeKey", result.json())
                        writer.write(data.toString())
                        writer.flush()
                        writer.close()
                        outStream.close()
                        val responseCode = urlConnection.responseCode
                        val responseCodeIsSuccess = responseCode in 200..299
                        var responseContent = ""
                        val br =
                            BufferedReader(InputStreamReader(if (responseCodeIsSuccess) urlConnection.inputStream else urlConnection.errorStream))

                        br.forEachLine {
                            responseContent += it
                        }

                        if (responseCodeIsSuccess) {
                            val responseJSON = JSONObject(responseContent)
                            val stream = FileOutputStream(tokenFile)
                            try {
                                stream.write(responseJSON.getString("token").toByteArray())
                                pathToLicenseFile = tokenFile.absolutePath
                            } catch (e: Exception) {
                                printLog(TAG, "Failed to write token: $e")
                                e.printStackTrace()
                            } finally {
                                stream.close()
                            }
                        } else {
                            printLog(
                                TAG, """
     POST request failed: ${urlConnection.responseMessage}
     $responseContent
     """.trimIndent()
                            )
                        }
                    } catch (e: Exception) {
                        printLog(TAG, "Activate failed:$e")
                        e.printStackTrace()
                    } finally {
                        urlConnection?.disconnect()
                    }
                    pathToLicenseFile
                }).get()
        } catch (e: Exception) {
            printLog(TAG, "Activate failed:$e")
            e.printStackTrace()
            null
        }
    }
}