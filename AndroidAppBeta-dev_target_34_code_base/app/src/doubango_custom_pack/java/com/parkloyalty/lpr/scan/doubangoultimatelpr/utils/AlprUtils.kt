package com.parkloyalty.lpr.scan.doubangoultimatelpr.utils

import android.content.res.AssetFileDescriptor
import android.content.res.AssetManager
import android.graphics.PointF
import androidx.annotation.NonNull
import com.parkloyalty.lpr.scan.extensions.nullSafety

import com.parkloyalty.lpr.scan.util.LogUtil.printLog
import org.doubango.ultimateAlpr.Sdk.UltAlprSdkResult
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.FileInputStream
import java.io.IOException
import java.nio.channels.FileChannel
import java.util.*

/**
 * Utility class
 */
object AlprUtils {
    private val TAG = AlprUtils::class.java.canonicalName

    class AlprTransformationInfo(
        imageWidth: Int,
        imageHeight: Int,
        canvasWidth: Int,
        canvasHeight: Int
    ) {
        val xOffset: Int
        val yOffset: Int
        val ratio: Float
        val width: Int
        val height: Int

        fun transformX(x: Float): Float {
            return x * ratio + xOffset
        }

        fun transformY(y: Float): Float {
            return y * ratio + yOffset
        }

        fun transform(p: PointF): PointF {
            return PointF(transformX(p.x), transformY(p.y))
        }

        init {
            val xRatio = canvasWidth.toFloat() / imageWidth.toFloat()
            val yRatio = canvasHeight.toFloat() / imageHeight.toFloat()
            ratio = Math.min(xRatio, yRatio)
            width = (imageWidth * ratio).toInt()
            height = (imageHeight * ratio).toInt()
            xOffset = canvasWidth - width shr 1
            yOffset = canvasHeight - height shr 1
        }
    }


    internal class Car {
        internal class Attribute {
            var klass = 0
            var name: String? = null
            var confidence = 0f
        }

        internal class MakeModelYear {
            var klass = 0
            var make: String? = null
            var model: String? = null
            var year: String? = null
            var confidence = 0f
        }

        var confidence = 0f
        var warpedBox: FloatArray? = null
        var colors: List<Attribute>? = null
        var bodyStyles: List<Attribute>? = null
        var makesModelsYears: List<MakeModelYear>? = null
    }

    internal class Country {
        var klass = 0
        var code: String? = null
        var name: String? = null
        var state: String? = null
        var other: String? = null
        var confidence = 0f
    }


    internal class Plate {
        var number: String? = null
        var detectionConfidence: Double = 0.0
        var recognitionConfidence: Double = 0.0
        var warpedBox: FloatArray? = null
        var countries: List<Country>? = null
        var car: Car? = null
    }

    fun extractFrameId(result: UltAlprSdkResult): Long {
        val jsonString = result.json()
        if (jsonString != null) {
            try {
                val jObject = JSONObject(jsonString)
                return jObject.getLong("frame_id")
            } catch (e: JSONException) {
            }
        }
        return 0
    }


    internal fun extractPlates(result: UltAlprSdkResult): List<Plate> {
        val plates: MutableList<Plate> = LinkedList()
        if (!result.isOK || result.numPlates() == 0L && result.numCars() == 0L) {
            return plates
        }
        val jsonString = result.json()
            ?: // No plate
            return plates
        //final String jsonString = "{\"frame_id\":178,\"lantency\":0,\"plates\":[{\"car\":{\"color\":[{\"confidence\":59.76562,\"klass\":11,\"name\":\"white\"},{\"confidence\":27.73438,\"klass\":0,\"name\":\"black\"},{\"confidence\":11.32812,\"klass\":9,\"name\":\"silver\"},{\"confidence\":0.390625,\"klass\":4,\"name\":\"gray\"},{\"confidence\":0.390625,\"klass\":5,\"name\":\"green\"}],\"confidence\":89.45312,\"makeModelYear\":[{\"confidence\":5.46875,\"klass\":8072,\"make\":\"nissan\",\"model\":\"nv\",\"year\":2012},{\"confidence\":3.90625,\"klass\":4885,\"make\":\"gmc\",\"model\":\"yukon 1500\",\"year\":2007},{\"confidence\":1.953125,\"klass\":3950,\"make\":\"ford\",\"model\":\"f150\",\"year\":2001},{\"confidence\":1.953125,\"klass\":4401,\"make\":\"ford\",\"model\":\"ranger\",\"year\":2008},{\"confidence\":1.953125,\"klass\":3954,\"make\":\"ford\",\"model\":\"f150\",\"year\":2005}],\"warpedBox\":[37.26704,655.171,253.8487,655.171,253.8487,897.6935,37.26704,897.6935]},\"confidences\":[86.99596,99.60938],\"country\":[{\"code\":\"RUS\",\"confidence\":99.60938,\"klass\":65,\"name\":\"Russian Federation\",\"other\":\"Private vehicle\",\"state\":\"Republic of Karelia\"},{\"code\":\"USA\",\"confidence\":0.0,\"klass\":88,\"name\":\"United States of America\",\"state\":\"Iowa\"},{\"code\":\"USA\",\"confidence\":0.0,\"klass\":80,\"name\":\"United States of America\",\"state\":\"Connecticut\"},{\"code\":\"USA\",\"confidence\":0.0,\"klass\":81,\"name\":\"United States of America\",\"state\":\"Delaware\"},{\"code\":\"USA\",\"confidence\":0.0,\"klass\":82,\"name\":\"United States of America\",\"state\":\"Florida\"}],\"text\":\"K643ET10\",\"warpedBox\":[61.73531,819.796,145.57,819.796,145.57,881.916,61.73531,881.916]}]}";
        try {
            val jObject = JSONObject(jsonString)
            if (jObject.has("plates")) {
                val jPlates: JSONArray = jObject.getJSONArray("plates")
                for (i in 0 until jPlates.length()) {
                    val jPlate: JSONObject = jPlates.getJSONObject(i)
                    printLog("==>LPR:", jPlate.toString())

                    // The plate itself (backward-compatible with 2.0.0)
                    val plate = Plate()
                    plate.warpedBox = FloatArray(8)
                    if (jPlate.has("text")) { // Starting 3.2 it's possible to have cars without plates when enabled
                        val jConfidences: JSONArray = jPlate.getJSONArray("confidences")
                        val jWarpedBox: JSONArray = jPlate.getJSONArray("warpedBox")
                        plate.number = jPlate.getString("text")
                        for (j in 0..7) {
                            plate.warpedBox?.set(j, jWarpedBox.getDouble(j).nullSafety().toFloat())
                        }
                        plate.recognitionConfidence = jConfidences.getDouble(0)
                        plate.detectionConfidence = jConfidences.getDouble(1)
                    } else {
                        plate.number = ""
                        plate.recognitionConfidence = 0.0
                        plate.detectionConfidence = 0.0
                    }

                    // License Plate Country Identification [LPCI] (Added in 3.0.0): https://www.doubango.org/SDKs/anpr/docs/Features.html#license-plate-country-identification-lpci
                    if (jPlate.has("country")) {
                        plate.countries = LinkedList()
                        val jCountries: JSONArray = jPlate.getJSONArray("country")
                        for (k in 0 until jCountries.length()) {
                            val jCountry: JSONObject = jCountries.getJSONObject(k)
                            val country = Country()
                            country.klass = jCountry.getInt("klass")
                            country.confidence = jCountry.getDouble("confidence").toFloat()
                            country.code =
                                jCountry.getString("code") // ISO-code: https://en.wikipedia.org/wiki/ISO_3166-1_alpha-3
                            country.name = jCountry.getString("name") // Name in English
                            if (jCountry.has("state")) { // optional
                                country.state = jCountry.getString("state")
                            }
                            if (jCountry.has("other")) { // optional
                                country.other = jCountry.getString("other")
                            }
                            (plate.countries as LinkedList<Country>).add(country)
                        }
                    }

                    // Car (Added in 3.0.0)
                    if (jPlate.has("car")) {
                        val jCar = jPlate.getJSONObject("car")
                        val jCarWarpedBox: JSONArray = jCar.getJSONArray("warpedBox")
                        plate.car = Car()
                        plate.car?.confidence = jCar.getDouble("confidence").toFloat()
                        plate.car?.warpedBox = FloatArray(8)
                        for (j in 0..7) {
                            plate.car?.warpedBox?.set(
                                j,
                                jCarWarpedBox.getDouble(j).nullSafety().toFloat()
                            )
                        }

                        // Vehicle Color Recognition [VCR] (added in 3.0.0) : https://www.doubango.org/SDKs/anpr/docs/Features.html#vehicle-color-recognition-vcr
                        if (jCar.has("color")) {
                            plate.car?.colors = LinkedList()
                            val jColors: JSONArray = jCar.getJSONArray("color")
                            for (k in 0 until jColors.length()) {
                                val jColor: JSONObject = jColors.getJSONObject(k)
                                val color: Car.Attribute = Car.Attribute()
                                color.klass = jColor.getInt("klass")
                                color.confidence = jColor.getDouble("confidence").toFloat()
                                color.name = jColor.getString("name") // Name in English
                                (plate.car?.colors as LinkedList<Car.Attribute>).add(color)
                            }
                        }

                        // Vehicle Make Model Recognition [VMMR] (added in 3.0.0): https://www.doubango.org/SDKs/anpr/docs/Features.html#vehicle-make-model-recognition-vmmr
                        if (jCar.has("makeModelYear")) {
                            plate.car?.makesModelsYears = LinkedList()
                            val jMMYs: JSONArray = jCar.getJSONArray("makeModelYear")
                            for (k in 0 until jMMYs.length()) {
                                val jMMY: JSONObject = jMMYs.getJSONObject(k)
                                val mmy = Car.MakeModelYear()
                                mmy.klass = jMMY.getInt("klass")
                                mmy.confidence = jMMY.getDouble("confidence").toFloat()
                                mmy.make = jMMY.getString("make")
                                mmy.model = jMMY.getString("model")
                                mmy.year =
                                    jMMY["year"].toString() // Maybe Integer or String or whatever
                                (plate.car?.makesModelsYears as LinkedList<Car.MakeModelYear>).add(
                                    mmy
                                )
                            }
                        }

                        // Vehicle Body Style Recognition [VBSR] (added in 3.2.0): https://www.doubango.org/SDKs/anpr/docs/Features.html#features-vehiclebodystylerecognition
                        if (jCar.has("bodyStyle")) {
                            plate.car?.bodyStyles = LinkedList()
                            val jBodyStyles: JSONArray = jCar.getJSONArray("bodyStyle")
                            for (k in 0 until jBodyStyles.length()) {
                                val jBodyStyle: JSONObject = jBodyStyles.getJSONObject(k)
                                val bodyStyle: Car.Attribute = Car.Attribute()
                                bodyStyle.klass = jBodyStyle.getInt("klass")
                                bodyStyle.confidence = jBodyStyle.getDouble("confidence").toFloat()
                                bodyStyle.name = jBodyStyle.getString("name") // Name in English
                                (plate.car?.bodyStyles as LinkedList<Car.Attribute>).add(bodyStyle)
                            }
                        }
                    }
                    plates.add(plate)
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
            printLog(TAG, e.toString())
        }
        return plates
    }

    fun <K, V> getOrDefault(@NonNull map: Map<K, V>, key: K, defaultValue: V): V {
        var v: V?
        return if (map[key].also { v = it } != null || map.containsKey(key)) v!! else defaultValue
    }


    /**
     * Checks if the returned result is success. An assertion will be raised if it's not the case.
     * In production you should catch the exception and perform the appropriate action.
     * @param result The result to check
     * @return The same result
     */
    fun assertIsOk(result: UltAlprSdkResult): UltAlprSdkResult {
        if (!result.isOK) {
            throw java.lang.AssertionError("Operation failed: " + result.phrase())
        }
        return result
    }

    /**
     * Converts the result to String.
     * @param result
     * @return
     */
    fun resultToString(result: UltAlprSdkResult): String {
        return "code: " + result.code() + ", phrase: " + result.phrase() + ", numPlates: " + result.numPlates() + ", json: " + result.json()
    }

    /**
     *
     * @param fileName
     * @return Must close the returned object
     */
    fun readFileFromAssets(assets: AssetManager, fileName: String?): FileChannel? {
        var inputStream: FileInputStream? = null
        return try {
            val fileDescriptor: AssetFileDescriptor = assets.openFd(fileName!!)
            inputStream = FileInputStream(fileDescriptor.fileDescriptor)
            inputStream.channel
            // To return DirectByteBuffer: fileChannel.map(FileChannel.MapMode.READ_ONLY, fileDescriptor.getStartOffset(), fileDescriptor.getDeclaredLength());
        } catch (e: IOException) {
            e.printStackTrace()
            printLog(TAG, e.toString())
            null
        }
    }
}