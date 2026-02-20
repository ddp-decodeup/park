package com.parkloyalty.lpr.scan.util

import android.app.Activity
import android.app.ActivityManager
import android.app.DatePickerDialog
import android.content.ActivityNotFoundException
import android.content.ContentUris
import android.content.Context
import android.content.Context.ACTIVITY_SERVICE
import android.content.ContextWrapper
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Point
import android.graphics.Rect
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Vibrator
import android.provider.ContactsContract
import android.provider.MediaStore
import android.provider.Settings
import android.telephony.TelephonyManager
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.text.format.DateFormat
import android.util.Base64
import android.util.DisplayMetrics
import android.util.Patterns
import android.view.View.OnFocusChangeListener
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.fasterxml.jackson.core.type.TypeReference
import com.parkloyalty.lpr.scan.BuildConfig
import com.parkloyalty.lpr.scan.R
import com.parkloyalty.lpr.scan.basecontrol.BaseApplication
import com.parkloyalty.lpr.scan.common.HearingDatesOnHoliday
import com.parkloyalty.lpr.scan.database.AppDatabase
import com.parkloyalty.lpr.scan.database.Singleton
import com.parkloyalty.lpr.scan.extensions.DuncanBrandingApp13
import com.parkloyalty.lpr.scan.extensions.convertToInt
import com.parkloyalty.lpr.scan.extensions.nullSafety
import com.parkloyalty.lpr.scan.extensions.toBooleanFromYesNo
import com.parkloyalty.lpr.scan.interfaces.Constants
import com.parkloyalty.lpr.scan.interfaces.Constants.Companion.PRINT_EXTRA_BOTTOM_MARGIN_FOR_CMD
import com.parkloyalty.lpr.scan.interfaces.CustomDialogHelper
import com.parkloyalty.lpr.scan.interfaces.SharedPrefKey
import com.parkloyalty.lpr.scan.ui.allreport.AllReportActivity
import com.parkloyalty.lpr.scan.ui.check_setup.activity.graph.model.check_setup.VehicleListModel
import com.parkloyalty.lpr.scan.ui.check_setup.model.timing.AddTimingRequest
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DatasetResponse
import com.parkloyalty.lpr.scan.util.FileUtil.ASSET_HEARING_DATES_ON_HOLIDAY_JSON
import com.parkloyalty.lpr.scan.util.ZebraCommandPrintUtils.lprImageHeightAdjustment
import com.parkloyalty.lpr.scan.util.commandprint.DrawableElement
import com.parkloyalty.lpr.scan.utils.ObjectMapperProvider
import com.parkloyalty.lpr.scan.vehiclestickerscan.model.VehicleInfoModel
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.security.SecureRandom
import java.sql.Timestamp
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.regex.Pattern
import kotlin.math.max


/*
* AppUtils is used for all common methods
* */
object AppUtils {
    private val TAG = AppUtils::class.java.simpleName
    var mDate: String? = null

    @JvmField
    var mZone: String? = null

    @JvmField
    var Yaxis: Double? = 190.0

    @JvmField
    var Ybox: Double? = 400.0

    @JvmField
    var mSection: Int? = 2

    @JvmField
    var mOrleansBoxInitialYValue: Double? = 135.0

    @JvmField
    var printQueryStringBuilder =java.lang.StringBuilder();

    @JvmField
    var mFinalQRCodeValue:String = ""

    @JvmField
    var isSunLightMode:Boolean = false

    var commentSectionTitle:String = ""

    var Horizontal = 0;
    var isLine = false;

    var isYAxisHeight = 1000;
    val yAxisHeight: MutableSet<Int> = mutableSetOf()
    val drawableElements: ArrayList<DrawableElement> = arrayListOf()

    fun addYAxisToSet(value: Double, componentHeight : Int) {
        if (LogUtil.isPrintHeightDynamicFromTicket){
            yAxisHeight.add(value.convertToInt() + componentHeight)
        }
    }
    fun getMaxYAxisFromCommand(): Int {
        return yAxisHeight.maxOrNull().nullSafety() + PRINT_EXTRA_BOTTOM_MARGIN_FOR_CMD
    }

    /*fun getMaxYAxisFromCommand(from: String, mContext: Context?): Int {
        val maxHeight = yAxisHeight.maxOrNull() ?: 0

        if (from == "PrinterActivity" && mContext != null) {
            val sharedPreference = SharedPref.getInstance(mContext)
            val lastPrintHeight = sharedPreference.readInt(SharedPrefKey.LAST_PRINTOUT_HEIGHT, 1000)

            if (yAxisHeight.isEmpty()) {
                yAxisHeight.add(lastPrintHeight ?: 1000)
                return lastPrintHeight ?:1000
            }
        }

        return maxHeight + PRINT_EXTRA_BOTTOM_MARGIN_FOR_CMD
    }*/

    fun clearYAxisSet() {
        yAxisHeight.clear()
    }

    fun clearDrawableElementList() {
        if (LogUtil.isEnableCommandBasedFacsimile){
            drawableElements.clear()
        }
    }

    fun getYAxisBasedOnQRCodeHeight(qrCodeSize: Int): Int {
        return qrCodeSize * 50
    }


    fun getHeightBasedOnFont(fontSize: Int): Int {
        when (fontSize) {
            0 -> {
                return 30
            }

            1 -> {
                return 40
            }

            2 -> {
                return 60
            }

            3 -> {
                return 80
            }

            else -> {
                return 30
            }
        }
    }

    fun getScaledQRBitmapBasedOnCommandBitmapSize(qrCodeSize: Int, actualBitmap: Bitmap?): Bitmap {
        val widthAndHeight = qrCodeSize * 28
        //return BitmapUtils.scale(actualBitmap!!, widthAndHeight, widthAndHeight)
        return BitmapUtils.scale(actualBitmap!!, 120, 120)
    }

    fun getSectionLineInPrintOut():Boolean{
        if(
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ATLANTIC_BEACH_NC,ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ATLANTIC_BEACH_SC,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HARTFORD,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PORTLAND_RO_REIMAGINED_PARKING,ignoreCase = true)
            ) {
            return true
        }else {
            return false
        }
    }

    /**
     * this method return whole device info
     *
     * @param context context
     * @return device info
     */
    fun getDeviceType(context: Context): String {
        val isTablet = context.resources.getBoolean(R.bool.isTablet)
        val deviceType =
            Constants.ANDROID + "-" + Build.VERSION.RELEASE + "-" + (if (isTablet) Constants.TABLET else Constants.PHONE) + "-" + Build.MANUFACTURER + " " + Build.MODEL.replace(
                "-",
                " "
            )
        return try {
            URLEncoder.encode(deviceType, "UTF-8")
        } catch (e: UnsupportedEncodingException) {
            LogUtil.printLog(TAG, e.toString())
            deviceType.replace(" ", "%20")
        }
    }

    @JvmStatic
    fun getDeviceId(context: Context): String {
        return Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        )
    }

    @JvmStatic
    fun hideSoftKeyboard(activity: Activity) {
        val inputMethodManager = activity.getSystemService(
            Activity.INPUT_METHOD_SERVICE
        ) as InputMethodManager
        if (inputMethodManager.isAcceptingText) {
            inputMethodManager.hideSoftInputFromWindow(
                activity.currentFocus!!.windowToken,
                0
            )
        }
    }

    @JvmStatic
    fun splitDoller(Value: String): Double {
        try {
            val separated1 = Value.split(" ").toTypedArray()
            if (separated1.isNotEmpty() && separated1[1] != "0") {
                return separated1[1].toDouble()
            }
            else{
                return separated1[0].toDouble()
            }
        } catch (e: Exception) {
            return Value.removePrefix("$").toDouble()
        }
        return Value.removePrefix("$").toDouble()
    }

    @JvmStatic
    fun showKeyboard(context: Context) {
        val inputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }

    /**
     * Function used to get local date from UTC date
     * @param utcDate: UTC date you will get from backend
     * @param fromUtcFormat : Date format of UTC date
     * @param toFormat : Date Format of the desire output we need to show
     * @return : converted date to use
     */
    fun getLocalDateFromUTC(utcDate: String?,fromUtcFormat: SimpleDateFormat, toFormat : SimpleDateFormat) : String {
        try {
            var time = ""
            if (utcDate != null) {
                fromUtcFormat.timeZone = TimeZone.getTimeZone("UTC")
                var gpsUTCDate: Date? = null
                try {
                    gpsUTCDate = fromUtcFormat.parse(utcDate)
                } catch (e: ParseException) {
                    e.printStackTrace()
                }

                toFormat.timeZone = TimeZone.getDefault()
                checkNotNull(gpsUTCDate)
                time = toFormat.format(gpsUTCDate.time)
            }
            return time.nullSafety()
        }catch (e:Exception){
            return ""
        }
    }


    fun splitsFormatLPR(Date: String?): String {
        val separated = Date!!.split("-").toTypedArray()
        val YYYY = separated[0]
        val MM = separated[1]
        val DD = separated[2]
        if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAMETRO,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CORPUSCHRISTI,ignoreCase = true))
        {
            return "$MM-$DD"+"-"+YYYY.substring(2,4)
        }else {
            return "$MM-$DD" /*+"-"+YYYY.substring(2,4)*/
        }
    } ////16/06/21 13:26:00
    fun splitsFormatLPRPermit(Date: String?): String {
        val separated = Date!!.split("-").toTypedArray()
        val YYYY = separated[0]
        val MM = separated[1]
        val DD = separated[2]
        return "$MM-$DD" +"-"+"$YYYY"
    } ////16/06/21 13:26:00

    fun splitsFormatPayment(Date: String): String {
        return try {
            val separatedOld = Date.split(" ").toTypedArray()
            val separated = separatedOld[0].split("/").toTypedArray()
            val YY = separated[2]
            val MM = separated[1]
            val DD = separated[0]
            MM + "-" + DD + "-" + YY + " " + dateConvertLPRPayment(separatedOld[1])
        } catch (e: Exception) {
            Date
        }
    }

    fun getHoursLPR(hours: String): String? {
        val separated = hours.split(":").toTypedArray()
        val `val` = separated[0] + ":" + separated[1] // this will contain " they taste good"
        return dateConvertLPR(`val`)
    }
    fun getHoursLPRPermit(hours: String): String? {
        val separated = hours.split(":").toTypedArray()
        val `val` = separated[0] + ":" + separated[1] // this will contain " they taste good"
        return dateConvertLPRPermit(`val`)
    }
    fun getHoursLPRTime(hours: String): String? {
        val separated = hours.split(":").toTypedArray()
        val `val` = separated[0] + ":" + separated[1] // this will contain " they taste good"
        return dateConvertLPR(`val`)
    }

    @JvmStatic
    fun splitDateLPR(date: String): String? {
        return try {
            val separated = date.split("T").toTypedArray()
            mDate = separated[0]
            val `val` = separated[1] // this will contain " they taste good"
            getHoursLPR(`val`)
        } catch (e: Exception) {
            ""
        }
    }
    @JvmStatic
    fun splitDateLPRPermit(date: String): String? {
        return try {
            val separated = date.split("T").toTypedArray()
            mDate = separated[0]
            val `val` = separated[1] // this will contain " they taste good"
            getHoursLPRPermit(`val`)
        } catch (e: Exception) {
            ""
        }
    }

    @JvmStatic
    fun splitDateLPRTime(date: String): String? {
        return try {
            val separated = date.split("T").toTypedArray()
            mDate = separated[0]
            val `val` = separated[1] // this will contain " they taste good"
            dateConvertLPRTime(`val`)
        } catch (e: Exception) {
            ""
        }
    }

    fun dateConvertLPR(`val`: String): String? {
        try {
            val _24HourSDF = SimpleDateFormat("HH:mm")
            val _12HourSDF = SimpleDateFormat("hh:mm a")
            val _24HourDt = _24HourSDF.parse(`val`)
            return splitsFormatLPR(mDate) + " " + _12HourSDF.format(_24HourDt)
            //System.out.println(_24HourDt);
            //System.out.println(_12HourSDF.format(_24HourDt));
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
    fun dateConvertLPRPermit(`val`: String): String? {
        try {
            val _24HourSDF = SimpleDateFormat("HH:mm")
            val _12HourSDF = SimpleDateFormat("hh:mm a")
            val _24HourDt = _24HourSDF.parse(`val`)
            return splitsFormatLPRPermit(mDate) + " " + _12HourSDF.format(_24HourDt)
            //System.out.println(_24HourDt);
            //System.out.println(_12HourSDF.format(_24HourDt));
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
    fun dateConvertLPRTime(`val`: String): String? {
        try {
            val _24HourSDF = SimpleDateFormat("HH:mm")
            val _12HourSDF = SimpleDateFormat("hh:mm a")
            val _24HourDt = _24HourSDF.parse(`val`)
            return  _12HourSDF.format(_24HourDt)
            //System.out.println(_24HourDt);
            //System.out.println(_12HourSDF.format(_24HourDt));
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun dateConvertLPRPayment(`val`: String): String? {
        try {
            val _24HourSDF = SimpleDateFormat("HH:mm")
            val _12HourSDF = SimpleDateFormat("hh:mm a")
            val _24HourDt = _24HourSDF.parse(`val`)
            return _12HourSDF.format(_24HourDt)
            //System.out.println(_24HourDt);
            //System.out.println(_12HourSDF.format(_24HourDt));
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    @JvmStatic
    fun splitID(id: String): String {
        return try {
            val separated = id.split("-").toTypedArray()
            // this will contain " they taste good"
            separated[0]
        } catch (e: Exception) {
            ""
        }
    }

    @JvmStatic
    @Throws(ParseException::class)
    fun compareDates(date: String): Int {
        return try {
            /*val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            inputFormat.timeZone = TimeZone.getTimeZone("UTC") // Set if input is in UTC

            val originalTimeStr = "2025-06-22T10:30:00"
            val date = inputFormat.parse(originalTimeStr)

            val calendar = Calendar.getInstance()
            calendar.time = date!!
            calendar.add(Calendar.MINUTE, 15) // Add 15 minutes

            val newTimeStr = inputFormat.format(calendar.time)

            println("Original time: $originalTimeStr")
            println("Time + 15 min: $newTimeStr")*/


            val isoFormatForOutPut = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            val Caldate = isoFormatForOutPut.parse(date)

            if (Date().after(Caldate)) {
                1
            } else 0


//            if (Date().after(strDate)) {
//                1
//            } else 0
        } catch (e: Exception) {
            e.printStackTrace()
            0
        }
    }

    @JvmStatic
    fun observedTime(markTime: String): String {
        var dateS = ""
        try {
            val markDate = markTime.split("T").toTypedArray() //16/06/21 13:26:00
            val sMarkDATE = markDate[1]
            val sdf = SimpleDateFormat("hh:mm a")
            val sdfStartTime = SimpleDateFormat("HH:mm:ss")
            val mConvertedDate = sdfStartTime.parse(sMarkDATE)
            dateS = sdf.format(mConvertedDate)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return dateS
    }


    @JvmStatic
    fun isTimingExpired(markTime: String, mRegulation: Float): Boolean {
        try {

            //2023-01-07T23:00:30Z
            val inputDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
            val markStartTime = inputDateFormat.parse(markTime.replace("T"," "))

            val currentDeviceTime = Calendar.getInstance().time
            val currentDeviceTimeFormat = inputDateFormat.format(currentDeviceTime)
            val currentDeviceDateTime = inputDateFormat.parse(currentDeviceTimeFormat)

            val calendarInstance =(Calendar.getInstance())
            calendarInstance.time = (markStartTime)

            try {
                val hours = (mRegulation / 60).toInt() //since both are ints, you get an int
                val minutes = (mRegulation % 60).toInt()
                System.out.printf("%d:%02d", hours, minutes)
                LogUtil.printLog("duration ", "$hours   $minutes")
                if (hours > 0) {
                    calendarInstance.add(Calendar.HOUR, hours)
                }
                if (minutes > 0) {
                    calendarInstance.add(Calendar.MINUTE, minutes)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            val markTimeWithRegulationAdded = calendarInstance.time
            val millse = markTimeWithRegulationAdded.time - currentDeviceDateTime.time
            return if (millse > 0) {
                false
            } else {
                true
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    @JvmStatic
    fun isElapsTime(mTime: String, mRegu: Long, context: Context?): String {
        var diff = "00:00:00"
        try {
//            return if (isRemainingTime(mTime, mRegu, context).equals("00:00:00",
//                    ignoreCase = true)) {
//                "00:00:00"
//            } else {


                //2023-01-07T23:00:30Z
                val inputDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
                val inputCalDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
                val inputAPIResponseDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH)

                val markStartTime = inputAPIResponseDateFormat.parse(mTime)

                val calendarInstance =(Calendar.getInstance())
                calendarInstance.time = (markStartTime)
                val markTimeWithRegulationAdded = calendarInstance.time
//                val t = inputDateFormat.parse(markTimeWithRegulationAdded.toString())

                val currentDeviceTime = Calendar.getInstance().time
                val currentDeviceTimeFormat = inputCalDateFormat.format(currentDeviceTime)
                val currentDeviceDateTime = inputDateFormat.parse(currentDeviceTimeFormat)
                val millse =  currentDeviceDateTime.time - markTimeWithRegulationAdded.time

                if (millse > 0) {
                    val mills = Math.abs(millse)
                    val hours = (mills / (1000 * 60 * 60)).toInt()
                    val mins = (mills / (1000 * 60)).toInt() % 60
                    val secs = ((mills / 1000).toInt() % 60).toLong()
                    var mHour = hours.toString()
                    var mMins = mins.toString()
                    var mSec = secs.toString()
                    if (hours < 10) {
                        mHour = "0$mHour"
                    }
                    if (mins < 10) {
                        mMins = "0$mMins"
                    }
                    if (secs < 10) {
                        mSec = "0$mSec"
                    }
                    diff = "$mHour:$mMins:$mSec" // updated value every1 second
                } else {
                    diff = "00:00:00"
                }
                diff
//            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return diff
    }



    @JvmStatic
    fun isRemainingTime(markTime: String, mRegulation: Long, context: Context?): String {
        try {
            //2023-01-07T23:00:30Z
            val inputDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
            val markStartTime = inputDateFormat.parse(markTime.replace("T"," "))

            val currentDeviceTime = Calendar.getInstance().time
            val currentDeviceTimeFormat = inputDateFormat.format(currentDeviceTime)
            val currentDeviceDateTime = inputDateFormat.parse(currentDeviceTimeFormat)

            val calendarInstance =(Calendar.getInstance())
            calendarInstance.time = (markStartTime)

            try {
                val hours = (mRegulation / 60).toInt() //since both are ints, you get an int
                val minutes = (mRegulation % 60).toInt()
                System.out.printf("%d:%02d", hours, minutes)
                LogUtil.printLog("duration ", "$hours   $minutes")
                if (hours > 0) {
                    calendarInstance.add(Calendar.HOUR, hours)
                }
                if (minutes > 0) {
                    calendarInstance.add(Calendar.MINUTE, minutes)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            val markTimeWithRegulationAdded = calendarInstance.time
            val millse = markTimeWithRegulationAdded.time - currentDeviceDateTime.time

            var diff = "00:00:00"
            if (millse > 0) {
                val mills = Math.abs(millse)
                val hours = (mills / (1000 * 60 * 60)).toInt()
                val mins = (mills / (1000 * 60)).toInt() % 60
                val secs = ((mills / 1000).toInt() % 60).toLong()
                var mHour = hours.toString()
                var mMins = mins.toString()
                var mSec = secs.toString()
                if (hours < 10) {
                    mHour = "0$mHour"
                }
                if (mins < 10) {
                    mMins = "0$mMins"
                }
                if (secs < 10) {
                    mSec = "0$mSec"
                }
                diff = "$mHour:$mMins:$mSec"
            } else {
                val mills = Math.abs(millse)
                val hours = (mills / (1000 * 60 * 60)).toInt()
                val mins = (mills / (1000 * 60)).toInt() % 60
                val secs = ((mills / 1000).toInt() % 60).toLong()
                var mHour = hours.toString()
                var mMins = mins.toString()
                var mSec = secs.toString()
                if (hours < 10) {
                    mHour = "0$mHour"
                }
                if (mins < 10) {
                    mMins = "0$mMins"
                }
                if (secs < 10) {
                    mSec = "0$mSec"
                }
                diff = "$mHour:$mMins:$mSec"
//                diff = "00:00:00"
            }
            return diff
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return "00:00:00"
    }

    @JvmStatic
    fun isElapseTime(mRegulation: Long, context: Context?): String {
        var diff = "00:00:00"

        try {
            val hours = (mRegulation / 60).toInt() //since both are ints, you get an int
            val minutes = (mRegulation % 60).toInt()
            diff = hours.toString()+" hour "+minutes.toString()+ " min"
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return diff
    }

    @JvmStatic
    fun isRemainingTimeForUI(markTime: String, mRegulation: Long, context: Context?): String {
        try {
            //2023-01-07T23:00:30Z
            val inputDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
            val markStartTime = inputDateFormat.parse(markTime.replace("T"," "))

            val currentDeviceTime = Calendar.getInstance().time
            val currentDeviceTimeFormat = inputDateFormat.format(currentDeviceTime)
            val currentDeviceDateTime = inputDateFormat.parse(currentDeviceTimeFormat)

            val calendarInstance =(Calendar.getInstance())
            calendarInstance.time = (markStartTime)

            try {
                val hours = (mRegulation / 60).toInt() //since both are ints, you get an int
                val minutes = (mRegulation % 60).toInt()
                System.out.printf("%d:%02d", hours, minutes)
                LogUtil.printLog("duration ", "$hours   $minutes")
                if (hours > 0) {
                    calendarInstance.add(Calendar.HOUR, hours)
                }
                if (minutes > 0) {
                    calendarInstance.add(Calendar.MINUTE, minutes)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            val markTimeWithRegulationAdded = calendarInstance.time
            val millse = markTimeWithRegulationAdded.time - currentDeviceDateTime.time

            var diff = "00:00:00"
            if (millse > 0) {
                val mills = Math.abs(millse)
                val hours = (mills / (1000 * 60 * 60)).toInt()
                val mins = (mills / (1000 * 60)).toInt() % 60
                val secs = ((mills / 1000).toInt() % 60).toLong()
                var mHour = hours.toString()
                var mMins = mins.toString()
                var mSec = secs.toString()
                if (hours < 10) {
                    mHour = "0$mHour"
                }
                if (mins < 10) {
                    mMins = "0$mMins"
                }
                if (secs < 10) {
                    mSec = "0$mSec"
                }
                diff = "$mHour:$mMins:$mSec"
            } else {
                diff = "00:00:00"
            }
            return diff

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return "00:00:00"
    }



    private fun calculateNumbers(Number: Int): Int {
        var count = 0
        val string = Number.toString()
        //Counts each character except space
        for (i in 0 until string.length) {
            if (string[i] != ' ') count++
        }
        return count
    }

    //date formatter for Activity log
    fun splitsFormatWelcome(Date: String?): String {
        val separated = Date!!.split("-").toTypedArray()
        val YYYY = separated[0]
        val MM = separated[1]
        val DD = separated[2]
        return MM + "-" + DD + "-" + YYYY.substring(2, 4)
    }

    //date formatter for Activity log
    fun getHoursWelcome(hours: String): String? { //"2021-07-19T12:01:32",
        val separated = hours.split(":").toTypedArray()
        val `val` = separated[0] + ":" + separated[1] // this will contain " they taste good"
        return dateConvertWelcome(`val`)
    }

    //date formatter for Activity log
    @JvmStatic
    @Throws(ParseException::class)
    fun splitDateWelcome(date: String?, Zone: String?): String? {
        val format = "yyyy-MM-dd'T'HH:mm:ss"
//        val estFormatter = SimpleDateFormat(format)
//        estFormatter.timeZone = TimeZone.getTimeZone("UTC")
//        val date1 = estFormatter.parse(date)
//        val utcFormatter = SimpleDateFormat(format)
//        utcFormatter.timeZone = TimeZone.getTimeZone(Zone)
//
//        val strDate = utcFormatter.format(date1)
//        val separated = strDate.split("T").toTypedArray()

        val separated = date!!.split("T").toTypedArray()
        mDate = separated[0]
        //mZone = Zone;
        val `val` = separated[1] // this will contain " they taste good"
        return getHoursWelcome(`val`)
    }

    //date formatter for Activity log
    @JvmStatic
    @Throws(ParseException::class)
    fun splitDateLpr(mZone: String?): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
        //sdf.setTimeZone(TimeZone.getTimeZone(mZone));
        val mConvertedDate = sdf.parse(sdf.format(Date()))
        return sdf.format(mConvertedDate)
    }

    @Throws(ParseException::class)
    fun getCurrentStamp(zone: String?): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        //sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        val mConvertedDate = sdf.parse(sdf.format(Date()))
        return sdf.format(mConvertedDate)
    }

    @Throws(ParseException::class)
    fun getCurrentTime(zone: String?): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm a")
        //sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        val mConvertedDate = sdf.parse(sdf.format(Date()))
        return sdf.format(mConvertedDate)
    }

 @Throws(ParseException::class)
    fun getCurrentStampWithSetting(zone: String?): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
        //sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        val mConvertedDate = sdf.parse(sdf.format(Date()))
        return sdf.format(mConvertedDate)
    }

    const val HOUR = (3600 * 1000).toLong() // in milli-seconds.

    @Throws(ParseException::class)
    fun getCurrentStampWithAddTime(zone: String?,hour:String?): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
        //sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        val mHour:Int = hour!!.toInt()
        val newDate = Date(Date().getTime() - mHour * HOUR)
        val mConvertedDate = sdf.parse(sdf.format(newDate))
        return sdf.format(mConvertedDate)
    }

    //sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
    @Throws(ParseException::class)
    fun getMinute(): String {
            val sdf = SimpleDateFormat("HH:mm:ss")
            //sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
            val mConvertedDate = sdf.parse(sdf.format(Date()))
            val dateS = sdf.format(mConvertedDate)
            val separated = dateS.split(":").toTypedArray()
            return separated[1]
        }

    @Throws(ParseException::class)
    fun getCurrentStampT(zone: String?): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        sdf.timeZone = TimeZone.getTimeZone(zone)
        val mConvertedDate = sdf.parse(sdf.format(Date()))
        return sdf.format(mConvertedDate)
    }

    @JvmStatic
    fun getStartT(zone: String?): Long {
        //creating Calendar instance
        return try {
            val isoFormat =
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            isoFormat.timeZone = TimeZone.getTimeZone(zone)
            val mCurrent = getCurrentStampT(zone)
            val date = isoFormat.parse(mCurrent + "T00:00:00")
            val date2 = isoFormat.parse(mCurrent + "T23:00:00")
            val s1 = date.time.toString().substring(0, 10)
            s1.toLong()
        } catch (e: Exception) {
            0.toLong()
        }
    }

    @JvmStatic
    fun getClientTimeStamp(zone: String?): String {
        //creating Calendar instance
        return try {
            val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            isoFormat.timeZone = TimeZone.getTimeZone(zone)
            //isoFormat.setTimeZone(TimeZone.getTimeZone(zone));
            val mConvertedDate = isoFormat.parse(isoFormat.format(Date()))
            val mCurrent = isoFormat.format(mConvertedDate)
            //String mCurrent = getCurrentStampT(zone);
            //Date date = isoFormat.parse(mCurrent);
            //Date date2 = isoFormat.parse(mCurrent + "T23:00:00");
            //String s1 = String.valueOf(date.getTime()).substring(0, 10);
            //long timeMilli1 = Long.parseLong(s1);
            //long timeMilli1 = Long.parseLong(dateS);
            mCurrent + "Z"
        } catch (e: Exception) {
            ""
        }
    }

    @JvmStatic
    fun getStartTDate(zone: String?): String {
        //creating Calendar instance
        return try {
            var currentDateTimeWith3AM:Date?=null
            var date:Date?=null

            val isoFormatForOutPut = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
            val isoFormatTime = SimpleDateFormat("yyyy-mm-dd HH:mm a")

            val mCurrentForStatic3AM = getCurrentStamp(zone)
            val mCurrentDateTime =  getCurrentTime(zone)

            if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CHARLESTON, true)){
                 date = isoFormatForOutPut.parse(mCurrentForStatic3AM + "T05:00:00Z")
                 currentDateTimeWith3AM = isoFormatTime.parse(mCurrentForStatic3AM + " 05:00 AM")
            }else
            {
                date = isoFormatForOutPut.parse(mCurrentForStatic3AM + "T03:00:00Z")
                currentDateTimeWith3AM = isoFormatTime.parse(mCurrentForStatic3AM + " 03:00 AM")
            }

            val currentDeviceDateTime = isoFormatTime.parse(mCurrentDateTime )


            if((currentDeviceDateTime).after(currentDateTimeWith3AM))
            {
                isoFormatForOutPut.format(date)
            }else{
                val dateBefore = (date.getTime() - 1 * 24 * 3600 * 1000)
                isoFormatForOutPut.format(dateBefore)
            }
        } catch (e: Exception) {
            ""
        }
    }

    @JvmStatic
    fun getEndTDate(zone: String?): String {
        //creating Calendar instance
        return try {
//            var currentDateTimeWith3AM:Date?=null
//            var date:Date?=null

            val isoFormatForOutPut = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
//            val isoFormatTime = SimpleDateFormat("yyyy-mm-dd HH:mm a")

//            val mCurrentForStatic3AM = getCurrentStamp(zone)
            val mCurrentDateTime =  getCurrentTime(zone)

//            val date = isoFormatForOutPut.parse(mCurrentForStatic3AM + "T03:00:00Z")
//            val currentDateTimeWith3AM = isoFormatTime.parse(mCurrentForStatic3AM + " 03:00 AM")

//            if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CHARLESTON, true)){
//                date = isoFormatForOutPut.parse(mCurrentForStatic3AM + "T05:00:00Z")
//                currentDateTimeWith3AM = isoFormatTime.parse(mCurrentForStatic3AM + " 05:00 AM")
//            }else
//            {
//                date = isoFormatForOutPut.parse(mCurrentForStatic3AM + "T03:00:00Z")
//                currentDateTimeWith3AM = isoFormatTime.parse(mCurrentForStatic3AM + " 03:00 AM")
//            }
//            val currentDeviceDateTime = isoFormatTime.parse(mCurrentDateTime )

           /* if((currentDeviceDateTime).after(currentDateTimeWith3AM))
            {
                val newDate = addDays(mCurrentDateTime)
                val date2 = isoFormatForOutPut.parse(newDate + "T03:00:00Z")
                isoFormatForOutPut.format(date2)
            }else{

                isoFormatForOutPut.format(date)
            }*/

            val newDate = addDays(mCurrentDateTime)
            if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CHARLESTON, true)) {
                val date2 = isoFormatForOutPut.parse(newDate + "T05:00:00Z")
                isoFormatForOutPut.format(date2)
            }else {
                val date2 = isoFormatForOutPut.parse(newDate + "T03:00:00Z")
                isoFormatForOutPut.format(date2)
            }
        } catch (e: Exception) {
            ""
        }
    }

    @JvmStatic
    fun getStartTDateForPhili(zone: String?): String {
        //creating Calendar instance
        return try {
            var date:Date?=null
            var currentDateTimeWith3AM:Date?=null
            val isoFormatForOutPut = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
            val isoFormatTime = SimpleDateFormat("yyyy-mm-dd HH:mm a")

            val mCurrentForStatic3AM = getCurrentStamp(zone)
            val mCurrentDateTime =  getCurrentTime(zone)
            if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CHARLESTON, true)) {
                 date = isoFormatForOutPut.parse(mCurrentForStatic3AM + "T05:00:00Z")
                 currentDateTimeWith3AM = isoFormatTime.parse(mCurrentForStatic3AM + " 05:00 AM")
            }else{
                 date = isoFormatForOutPut.parse(mCurrentForStatic3AM + "T06:00:00Z")
                 currentDateTimeWith3AM = isoFormatTime.parse(mCurrentForStatic3AM + " 06:00 AM")
            }

            val currentDeviceDateTime = isoFormatTime.parse(mCurrentDateTime )

            if((currentDeviceDateTime).after(currentDateTimeWith3AM))
            {
                isoFormatForOutPut.format(date)
            }else{
                val dateBefore = (date.getTime() - 1 * 24 * 3600 * 1000)
                isoFormatForOutPut.format(dateBefore)
            }
        } catch (e: Exception) {
            ""
        }
    }
    fun getStartTDateWithSetting(zone: String?): String {
        //creating Calendar instance
        return try {
//            val isoFormat =
//                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
//            val mCurrent = getCurrentStampWithSetting(zone)
//            val date = isoFormat.parse(mCurrent + "T03:00:00Z")
//            isoFormat.format(date)
            getCurrentStampWithSetting(zone)
        } catch (e: Exception) {
            ""
        }
    }

 @JvmStatic
    fun getStartTDateAddSettingHourValue(zone: String?,hour: String?): String {
        //creating Calendar instance
        return try {
//            val isoFormat =
//                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
//            //isoFormat.setTimeZone(TimeZone.getTimeZone(zone));
//            val mCurrent = getCurrentStampWithAddTime(zone)
//            val date = isoFormat.parse(mCurrent + "T03:00:00Z")
////            val date2 = isoFormat.parse(mCurrent + "T15:00:00Z")
//            isoFormat.format(date)
            getCurrentStampWithAddTime(zone,hour)
        } catch (e: Exception) {
            ""
        }
    }

    @JvmStatic
    fun getEndTDateForPhili(zone: String?): String {
        //creating Calendar instance
        return try {
            val isoFormat =
                    SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
            //isoFormat.setTimeZone(TimeZone.getTimeZone(zone));
            val mCurrent = getCurrentStamp(zone)
            val newDate = addDays(mCurrent)
            val date2 = isoFormat.parse(newDate + "T03:00:00Z")
            isoFormat.format(date2)
        } catch (e: Exception) {
            ""
        }
    }

    fun addDays(dateInString: String): String {
        var dateInString = dateInString
        return try {
            var c = Calendar.getInstance()
            val mYear = c[Calendar.YEAR]
            val mMonth = c[Calendar.MONTH]
            val mDay = c[Calendar.DAY_OF_MONTH]
            var sdf = SimpleDateFormat("yyyy-MM-dd")
            c = Calendar.getInstance()
            try {
                c.time = sdf.parse(dateInString)
            } catch (e: ParseException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            }
            c.add(Calendar.DATE, 1) //14 dats add
            sdf = SimpleDateFormat("yyyy-MM-dd")
            val resultdate = Date(c.timeInMillis)
            dateInString = sdf.format(resultdate)
            dateInString
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    @JvmStatic
    fun getEndT(zone: String?): Long {
        //creating Calendar instance
        return try {
            val isoFormat =
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            isoFormat.timeZone = TimeZone.getTimeZone(zone)
            val mCurrent = getCurrentStampT(zone)
            val newDate = addDays(mCurrent)
            val date = isoFormat.parse(mCurrent + "T01:00:00")
            val date2 = isoFormat.parse(newDate + "T00:00:00")
            val s2 = date2.time.toString().substring(0, 10)
            s2.toLong()
        } catch (e: Exception) {
            0.toLong()
        }
    }

    //date formatter for Activity log
    fun dateConvertWelcome(`val`: String): String? {
        try {
            val _24HourSDF = SimpleDateFormat("HH:mm")
            val _12HourSDF = SimpleDateFormat("hh:mm a")
            //_12HourSDF.setTimeZone(TimeZone.getTimeZone("IST"));
            //_24HourSDF.setTimeZone(TimeZone.getTimeZone("IST"));
            val _24HourDt = _24HourSDF.parse(`val`)
            return splitsFormatWelcome(mDate) + " " + _12HourSDF.format(_24HourDt)
            //System.out.println(_24HourDt);
            //System.out.println(_12HourSDF.format(_24HourDt));
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * this method return device model like Galaxy 9
     *
     * @return device model
     */
    fun getDeviceModel(): String{
        return Build.ID
    }

    /**
     * this method return device type PHONE, TABLET
     *
     * @param context context
     * @return device type
     */
    fun getDeviceTypePhoneOrTablet(context: Context): String {
        val isTablet = context.resources.getBoolean(R.bool.isTablet)
        return (if (isTablet) Constants.TABLET else Constants.PHONE).toUpperCase()
    }

    /**
     * this method return current app version
     *
     * @param context context
     * @return app version
     */
    fun getAppVersion(context: Context?): String {
        /* PackageInfo packageInfo = null;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return packageInfo != null ? packageInfo.versionName : "";*/
        return BuildConfig.VERSION_NAME
    }

    fun isInteger(str: String): Boolean {
        val result: Boolean
        result = try {
            str.toInt()
            true
        } catch (e: Exception) {
            false
        }
        return result
    }
/*
    fun dateFormate(sDate: String?): String {
        var sDateFormate = ""
        try {
            val pattern = "MM:dd:YYYY"
            val inputPattern = "yyyy-MM-dd"

            val fmt = SimpleDateFormat(inputPattern)
            val date = fmt.parse(sDate)
            val fmtOut = SimpleDateFormat(pattern)
            sDateFormate = fmtOut.format(date)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return sDateFormate
    }*/


    fun dateFormateWithSlash(sDate: String?): String {
        var sDateFormate = ""
        try {
            val pattern = "MM/dd/YYYY"
            val inputPattern = "yyyy-MM-dd"

            val fmt = SimpleDateFormat(inputPattern)
            val date = fmt.parse(sDate)
            val fmtOut = SimpleDateFormat(pattern)
            sDateFormate = fmtOut.format(date)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return sDateFormate
    }

    @JvmStatic
    fun dateFormateForSpace(sDate: String?): String {
        var sDateFormate = ""
        try {
            val pattern = "MM/dd HH:mm a"
            val inputPattern = "yyyy-MM-dd'T'HH:mm:ss"
            /*SimpleDateFormat simpleDateFormat = new SimpleDateFormat(inputPattern);
            Date date = simpleDateFormat.parse(sDate);
            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            sDateFormate = sdf.format(date.getTime());
            System.out.println(sDateFormate);*/
            val fmt = SimpleDateFormat(inputPattern)
            val date = fmt.parse(sDate)
            val fmtOut = SimpleDateFormat(pattern)
            sDateFormate = fmtOut.format(date)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return sDateFormate
    }

@JvmStatic
    fun formatDateTimeForCameraViolation(isoDateTime: String): String {
    try {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
        val dateTime = LocalDateTime.parse(isoDateTime, formatter)

        // Output date and time as-is (no timezone conversion)
        val outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val formattedDate = dateTime.format(outputFormatter)
        return formattedDate
    } catch (e: Exception) {
        // Step 1: Split by 'T'
        val parts = isoDateTime.split("T")
        val date = parts[0]                         // "2025-06-22"
        val timeWithZone = parts[1]                // "21:30:35+00:00"

// Step 2: Split time from timezone
        val time = timeWithZone.split("+")[0]
        return date+" "+time
    }
}

@JvmStatic
    fun formatDateTimeForCameraRaw(isoDateTime: String): String {
    try {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
        val dateTime = LocalDateTime.parse(isoDateTime, formatter)

        // Output date and time as-is (no timezone conversion)
        val outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val formattedDate = dateTime.format(outputFormatter)

        return formattedDate
    } catch (e: Exception) {
        // Step 1: Split by 'T'
        val parts = isoDateTime.split("T")
        val date = parts[0]                         // "2025-06-22"
        val timeWithZone = parts[1]                // "21:30:35+00:00"

// Step 2: Split time from timezone
        val time = timeWithZone.split("+")[0]
        return date+" "+time
    }
}
@JvmStatic
    fun formatDateTimeForSanctionType(isoDateTime: String): String {
//    2025-05-30T12:21:45.785000
    try {
        val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
        val outputFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a")

        val dateTime = LocalDateTime.parse(isoDateTime, inputFormatter)
        return dateTime.format(outputFormatter)
    } catch (e: Exception) {

        return isoDateTime
    }
}
@JvmStatic
    fun formatDateCameraViolation(isoDateTime: String): String {
//    2025-05-30T12:21:45.785000
    try {
        val parsedDate = OffsetDateTime.parse(isoDateTime)

        // Example format: 30-May-2025 12:05 PM
        val formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy hh:mm a")

        val formattedDate = parsedDate.format(formatter)
        return formattedDate
    } catch (e: Exception) {

        return isoDateTime
    }
}

    fun getOSVersion(): String {
            var os = ""
            try {
                os = Build.VERSION.RELEASE
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return os
        }

    /**
     * Returns the consumer friendly device name
     */
    fun getDeviceName(): String {
            val manufacturer = Build.MANUFACTURER
            val model = Build.MODEL
            return if (model.startsWith(manufacturer)) {
                makeWordFirstLetterCapital(model)
            } else makeWordFirstLetterCapital(manufacturer) + " " + model
        }

    /**
     * this method make per word first letter capital
     *
     * @param s
     * @return
     */
    fun makeWordFirstLetterCapital(s: String): String {
        val sb: StringBuilder = StringBuilder(s)
        sb.setCharAt(0, Character.toUpperCase(sb[0]))
        return sb.toString()
    }

    fun convertDpToPixel(dp: Float, context: Context): Int {
        val resources = context.resources
        val metrics = resources.displayMetrics
        val px = dp * (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
        return px.toInt()
    }

    fun saveImage(context: Context, uriPath: Uri): String {
        var imagePath = ""
        var bitmap: Bitmap? = null
        try {
            bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, uriPath)
            if (bitmap != null) {
                bitmap = checkExif(bitmap, uriPath.path)
                imagePath = saveToInternalStorage(bitmap, context)
            }

            //imageViewProfileImage.setImageBitmap(bitmap);
        } catch (e: Exception) {
            e.printStackTrace()
            bitmap = BitmapFactory.decodeFile(uriPath.path)
            if (bitmap != null) {
                bitmap = checkExif(bitmap, uriPath.path)
                imagePath = saveToInternalStorage(bitmap, context)
            }
        }
        return imagePath
    }//System.out.println("Current time => "+c.getTime());

    //2021-04-08 16:45:14.084445
    // formattedDate have current date/time
    /*created by kalyani*/ /*get date and time in 2021-04-08 16:45:14.084445 format*/
    @JvmStatic
    fun getDateTime(): String {
            val c = Calendar.getInstance()
            //System.out.println("Current time => "+c.getTime());
            //2021-04-08 16:45:14.084445
            val df = SimpleDateFormat("yyyy-MM-dd")
            val formattedDate = df.format(c.time)
            val dfTime = SimpleDateFormat("HH:mm:ss.SSS")
            // formattedDate have current date/time
            return formattedDate + "T" + dfTime.format(c.time)
        }//System.out.println("Current time => "+c.getTime());

    //2021-04-08 16:45:14.084445
    // formattedDate have current date/time
    /*created by kalyani*/ /*get date and time in 2021-04-08 16:45:14.084445 format*/
    fun getTicketDateTime(): String {
            val c = Calendar.getInstance()
            //System.out.println("Current time => "+c.getTime());
            //2021-04-08 16:45:14.084445
            val df = SimpleDateFormat("dd/MM/yy")
            val formattedDate = df.format(c.time)
            val dfTime = SimpleDateFormat("HH:mm:ss")
            val formattedTime = formattedDate + " " + dfTime.format(c.time)
            // formattedDate have current date/time
            return splitsFormatPayment(formattedTime)
        }

    //System.out.println("Current time => "+c.getTime());
    //2021-04-08 16:45:14.084445
//        2021-11-25T03:30:11.88715
    @JvmStatic
    fun getCurrentDateTime(): String {
            val c = Calendar.getInstance()
            val df = SimpleDateFormat("dd MMM,yyyy hh:mm a")
            return df.format(c.time)
        }
    @JvmStatic
    fun getCurrentDateForObserveTimeFieldForGreenburgh(): String {
            val c = Calendar.getInstance()
            val df = SimpleDateFormat("MM/dd/yyyy")
            return df.format(c.time)
        }

    @JvmStatic
    fun getCurrentDateTimeForCitationForm(sDate: String?): String {
           /* val c = Calendar.getInstance()
            val df = SimpleDateFormat("dd MMM,yyyy hh:mm a")
            Log.e("AAAAAA",c.time.toString())
            return df.format(c.time)*/

        var sDateFormate = ""
        try {
            val outPattern = "dd MMM,yyyy hh:mm a"
//            val inputPattern = "ddd MMM,yyyy hh:mm a"
            val inputPattern = "yyyy-MM-dd'T'HH:mm:ss'Z'"
            val fmt = SimpleDateFormat(inputPattern)
            val date = fmt.parse(sDate)
            val fmtOut = SimpleDateFormat(outPattern)
            sDateFormate = fmtOut.format(date)
//            Log.e("AAAAAA",sDateFormate+" --  " +sDate)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return sDateFormate
        }

    //System.out.println("Current time => "+c.getTime());
    //2021-04-08 16:45:14.084445
//        2021-11-25T03:30:11.88715
    @JvmStatic
    fun getCurrentDateTimeForPrint(sDate: String?): String {
         /*   val c = Calendar.getInstance()
            val df = SimpleDateFormat("dd MM yyyy hh:mm a")
        Log.e("AAAAAA",c.time.toString())
            return df.format(c.time)*/

        var sDateFormate = ""
        try {
            val outPattern = "dd MM yyyy hh:mm a"
//            val inputPattern = "ddd MMM,yyyy hh:mm a"
            val inputPattern = "yyyy-MM-dd'T'HH:mm:ss'Z'"
            val fmt = SimpleDateFormat(inputPattern)
            val date = fmt.parse(sDate)
            val fmtOut = SimpleDateFormat(outPattern)
            sDateFormate = fmtOut.format(date)
//            Log.e("AAAAAA",sDateFormate+"   --  "+sDate)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return sDateFormate
        }



    @JvmStatic
    fun getCurrentDateTimeforBoot(tag: String): String {
        var formatTime = ""
        val c = Calendar.getInstance()
        //        2021-11-25T03:30:11.88715
        var df = SimpleDateFormat("yyyy-MM-dd hh:mm a")
        df = if (tag.equals("Normal", ignoreCase = true)) {
            SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS")
        } else {
            SimpleDateFormat("yyyy-MM-dd hh:mm a")
        }
        // formattedDate have current date/time
        formatTime = df.format(c.time)
        return formatTime
    }

    @JvmStatic
    fun getCurrenTime(tag: String): String {
        val c = Calendar.getInstance()
        //        2021-11-25T03:30:11.88715
        var df = SimpleDateFormat("yyyy-MM-dd hh:mm a")
        df = if (tag.equals("Normal", ignoreCase = true)) {
            SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS")
        } else {
            SimpleDateFormat("dd MMM hh:mm a")
        }
        //        formattedDate = formattedDate.replace("T"," ");
        // formattedDate have current date/time
        return df.format(c.time)
    }

    @JvmStatic
    fun getCurrenTimefile(tag: String): String {
        val c = Calendar.getInstance()
        //        2021-11-25T03:30:11.88715
        var df = SimpleDateFormat("yyyy-MM-dd hh:mm a")
        df = if (tag.equals("Normal", ignoreCase = true)) {
            SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS")
        } else {
            SimpleDateFormat("hhmm")
        }
        //        formattedDate = formattedDate.replace("T"," ");
        // formattedDate have current date/time
        return df.format(c.time)
    }

    @JvmStatic
    fun getFullDate(strCurrentDate: String?): String? {
        var date: String? = null
        val c = Calendar.getInstance()
        val df1 = SimpleDateFormat("dd MM yyyy hh:mm a")
        try {
            //System.out.println("Current time => "+c.getTime());
            //2021-04-08 16:45:14.084445
            val df = SimpleDateFormat("dd MMM,yyyy hh:mm a")
            //String formattedDate = df.format(c.getTime());
            var newDate: Date? = null
            try {
                newDate = df.parse(strCurrentDate)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            date = df1.format(newDate)
        } catch (e: Exception) {
            e.printStackTrace()
            date = df1.format(c.time)
        }
        //        date= df1.format(c.getTime());
        // formattedDate have current date/time
        return date
    }

    @JvmStatic
    fun geLookDateFormat(strCurrentDate: String?): String? {
        var date: String? = null
//        val c = Calendar.getInstance()
        val df = SimpleDateFormat("dd MMM,yyyy hh:mm a")
        try {
            //System.out.println("Current time => "+c.getTime());
            //2021-04-08 16:45:14.084445
            val df1 = SimpleDateFormat("MMM dd, yyyy hh:mm a")
            //String formattedDate = df.format(c.getTime());
            var newDate: Date? = null
            try {
                newDate = df.parse(strCurrentDate)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            date = df1.format(newDate)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            date = strCurrentDate
        }
        //        date= df1.format(c.getTime());
        // formattedDate have current date/time
        return date
    }

    fun getResizedBitmap(image: Bitmap?, maxSize: Int): Bitmap {
        var width = image!!.width
        var height = image.height
        val bitmapRatio = width.toFloat() / height.toFloat()
        if (bitmapRatio > 1) {
            width = maxSize
            height = (width / bitmapRatio).toInt()
        } else {
            height = maxSize
            width = (height * bitmapRatio).toInt()
        }
        return Bitmap.createScaledBitmap(image, width, height, true)
    }

    fun checkExif(scaledBitmap: Bitmap, filePath: String?): Bitmap? {
        //check the rotation of the image and display it properly
        val exif: ExifInterface
        var bitmap: Bitmap? = null
        try {
            exif = ExifInterface(filePath!!)
            val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0)
            val matrix = Matrix()
            if (orientation == 6) {
                matrix.postRotate(90f)
            } else if (orientation == 3) {
                matrix.postRotate(180f)
            } else if (orientation == 8) {
                matrix.postRotate(270f)
            }
            bitmap = Bitmap.createBitmap(
                scaledBitmap, 0, 0,
                scaledBitmap.width, scaledBitmap.height,
                matrix, true
            )
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return bitmap
    }

    fun saveToInternalStorage(bitmapImage: Bitmap?, context: Context?): String {
        val converetdImage = getResizedBitmap(bitmapImage, Constants.DEFAULT_IMAGE_SIZE)
        val cw = ContextWrapper(context)
        // path to /data/data/yourapp/app_data/imageDir
        val directory = cw.getDir(Constants.INTERNAL_FOLDER_NAME, Context.MODE_PRIVATE)
        // Create imageDir
        val imageFileName = "picture_" + System.currentTimeMillis() + ".jpg"
        val mypath = File(directory, imageFileName)
        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(mypath)
            // Use the compress method on the BitMap object to write image to the OutputStream
            converetdImage.compress(Bitmap.CompressFormat.JPEG, 100, fos)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                fos!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return mypath.absolutePath
    }

    // To hide soft keyboard
    fun hideKeyBoard(activity: Activity) {

        // Check if no view has focus:
        val view = activity.currentFocus
        if (view != null) {
            try {
                val imm =
                    activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                imm?.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // To show soft keyboard
    fun showKeyBoard(activity: Activity) {
        try {
            val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm?.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    fun isValidEmail(target: CharSequence?): Boolean {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }

    @Throws(ParseException::class)
    fun isValidDate(pDateString: String): Boolean {
        var pDateString = pDateString
        var date: Date? = null
        try {
            if (pDateString.contains("/")) {
                val spilt = pDateString.split("/").toTypedArray()
                if (spilt.size > 1 && spilt[0].toInt() < 13) {
                    val lastDayOfMonth = getDate(spilt[0], spilt[1])
                    pDateString = lastDayOfMonth + "/" + spilt[0] + "/20" + spilt[1]
                    date = SimpleDateFormat("dd/MM/yyyy").parse(pDateString)
                    return Date().before(date)
                }
            }
        } catch (e: NumberFormatException) {
            e.printStackTrace()
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return false
    }

    private fun getDate(sMonth: String, sYear: String): String {
        var date: Date? = null
        var DATE_FORMAT: java.text.DateFormat? = null
        try {
            val month = sMonth.toInt()
            val year = "20$sYear".toInt()
            val calendar = Calendar.getInstance()
            calendar[Calendar.MONTH] = month - 1
            calendar[Calendar.YEAR] = year
            calendar.add(Calendar.MONTH, 1)
            calendar[Calendar.DAY_OF_MONTH] = 1
            calendar.add(Calendar.DATE, -1)
            calendar[Calendar.DATE] = calendar.getActualMaximum(Calendar.DATE)
            date = calendar.time
            DATE_FORMAT = SimpleDateFormat("dd")
            LogUtil.printLog("last day of month ", DATE_FORMAT.format(date))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return DATE_FORMAT!!.format(date)
    }

    // For formatting amount according country
    fun getFormattedAmount(amount: String, editTextAmount: EditText): String? {
        var amount = amount
        var formattedString: String? = null
        if (amount.length == 0) {
            amount = "0.00"
        }
        try {
            val cleanString = amount.replace("\\D".toRegex(), "")
            val parsed = cleanString.toDouble()
            val numberFormat = NumberFormat.getCurrencyInstance(Locale("en", "US"))
            val formatted =
                numberFormat.format(parsed / 100).replace(numberFormat.currency.symbol, "")
            val formattedsymbol = formatted.replace(
                (numberFormat as DecimalFormat).decimalFormatSymbols.currencySymbol,
                ""
            )
            //  formattedString = formatted.substring(1).trim();
            formattedString = formattedsymbol.replace("\\s+".toRegex(), "")
            if (parsed == 0.0) {
                editTextAmount.setText("")
            } else {
                editTextAmount.setText(formattedString)
                editTextAmount.setSelection(editTextAmount.text.length)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return formattedString
    }

    /**
     * this method format the Amount in decimal and ,
     *
     * @param amount amount
     * @return formatted amount
     */
    fun formatAmountInDecimal(amount: String): String {
        var formattedAmount = amount
        formattedAmount = try {
            String.format(Locale.US, "%,.2f", formattedAmount.toDouble())
        } catch (e: Exception) {
            e.printStackTrace()
            return amount
        }
        return formattedAmount
    }

    // For formatting amount according country
    fun getFormattedAmountTextView(amount: String, editTextAmount: TextView): String? {
        var formattedString: String? = null
        try {
            val cleanString = amount.replace("\\D".toRegex(), "")
            val parsed = cleanString.toDouble()
            val numberFormat = NumberFormat.getCurrencyInstance()
            val formatted =
                numberFormat.format(parsed / 100).replace(numberFormat.currency.symbol, "")
            val formattedsymbol = formatted.replace(
                (numberFormat as DecimalFormat).decimalFormatSymbols.currencySymbol,
                ""
            )
            formattedString = formattedsymbol.replace("\\s+".toRegex(), "")
            if (parsed == 0.0) {
                editTextAmount.text = ""
            } else {
                editTextAmount.text = formattedString
            }
        } catch (e: NumberFormatException) {
            e.printStackTrace()
        }
        return formattedString
    }

    fun getSpaceInAcNumber(data: String?): String {
        var data = data
        val sb = StringBuilder(data)
        var i = sb.length - 4
        while (i > 0) {
            sb.insert(i, ' ')
            i -= 4
        }
        data = sb.toString()
        println(data)
        return data
    }

    /**
     * convert millis to Date
     *
     * @param milliSeconds millis
     * @param dateFormat   date format
     * @return formatted  date
     */
    fun getDateByMillis(milliSeconds: Long, dateFormat: String?): String {
        // Create a DateFormatter object for displaying date in specified format.
        val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())
        // Create a calendar object that will convert the date and time value in milliseconds to date.
        val date = Date(milliSeconds)
        return formatter.format(date.time)
    }

    /**
     * convert date string to time millis
     *
     * @param dateString date t
     * @param dateFormat date format
     * @return millis  time millis
     */
    fun getMillisByDate(dateString: String?, dateFormat: String?): Long {
        // Create a DateFormatter object for displaying date in specified format.
        val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())
        // Create a calendar object that will convert the date and time value in milliseconds to date.
        return try {
            val date = formatter.parse(dateString)
            date.time
        } catch (e: ParseException) {
            e.printStackTrace()
            0
        }
    }

    // Create a DateFormatter object for displaying date in specified format.
    fun getDateByMillis(milliSeconds: String, dateFormat: String?): String {
        return try {
            val lMilliSeconds = milliSeconds.toLong()
            val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())
            // Create a calendar object that will convert the date and time value in milliseconds to date.
            val date = Date(lMilliSeconds)
            formatter.format(date.time)
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    //Days left from cureent datre
    fun finddateDifference(inputDateString: String?): String {
//        String inputDateString = "09/03/2019";
        val calCurr = Calendar.getInstance()
        val day = Calendar.getInstance()
        try {
            day.time = SimpleDateFormat("MM/dd/yyyy").parse(inputDateString)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        if (day.after(calCurr)) {
            println("Days Left: " + (day[Calendar.DAY_OF_MONTH] - calCurr[Calendar.DAY_OF_MONTH]))
        }
        return (day[Calendar.DAY_OF_MONTH] - calCurr[Calendar.DAY_OF_MONTH]).toString()
    }

    fun getCurrentDate(format: String?): String {
        val c = Calendar.getInstance().time
        val df = SimpleDateFormat(format, Locale.getDefault())
        return df.format(c)
    }

    /**
     * @param days 1= last 30 days, 2 = last 60 days 3 = last 90 days
     * @return
     */
    fun getPreviousDateByDay(days: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, -(30 * days)) //
        return calendar.time.time
    }

    fun getCurrentDateMillis(): Long {
            val calendar = Calendar.getInstance()
            return calendar.time.time
        }

    fun takeScreenshot(context: Activity): String {
        var sharePath = ""
        val now = Date()
        DateFormat.format("yyyy-MM-dd_hh:mm:ss", now)
        try {
            // image naming and path  to include sd card  appending name you choose for file
            val mPath = Environment.getExternalStorageDirectory().toString() + "/" + now + ".jpeg"

            // create bitmap screen capture
            val v1 = context.window.decorView.rootView
            v1.isDrawingCacheEnabled = true
            val bitmap = Bitmap.createBitmap(v1.drawingCache)
            v1.isDrawingCacheEnabled = false
            val imageFile = File(mPath)
            val outputStream = FileOutputStream(imageFile)
            val quality = 100
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            outputStream.flush()
            outputStream.close()

            //setting screenshot in imageview
            val filePath = imageFile.path
            val ssbitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
            //            iv.setImageBitmap(ssbitmap);
            sharePath = filePath
        } catch (e: Throwable) {
            // Several error may come out with file handling or DOM
            e.printStackTrace()
        }
        return sharePath
    }

    fun isNumberSequential(str: String): Boolean {
        var result = true
        val list: MutableList<Int> = ArrayList()
        try {
            for (i in 0 until str.length) {
                list.add((str[i].toString() + "").toInt())
            }
            for (i in 0 until list.size - 1) {
                if (list[i + 1] - list[i] == 1) result = true else {
                    result = false
                    break
                }
            }
        } catch (ex: Exception) {
            result = false
        }
        return result
    }

    fun isNumberRepeative(str: String): Boolean {
        val set: MutableSet<String> = HashSet()
        for (i in 0 until str.length) {
            set.add(str[i].toString() + "")
        }
        return if (set.size == 1) true else false
    }

    /**
     * this method hash the account number with XXXX XXXX 1234
     *
     * @param accountNumber to be hash
     * @return hashed account number
     */
    fun getHashAccountNumber(accountNumber: String): String {
        if (TextUtils.isEmpty(accountNumber)) {
            return ""
        }
        val a4: String
        var last: String
        if (accountNumber.length > 4) {
            a4 = accountNumber.substring(accountNumber.length - 4, accountNumber.length)
            last = accountNumber.substring(0, accountNumber.length - 4)
            last = last.replace("\\S".toRegex(), "X")
            last = last.replace("....".toRegex(), "$0 ")
        } else {
            a4 = accountNumber
            last = ""
        }
        return "$last $a4"
    }

    /**
     * this method hash the account number with XXXX XXXX XX34
     *
     * @param accountNumber to be hash
     * @return hashed account number
     */
    fun getHashAccountNumber_two(accountNumber: String): String {
        if (TextUtils.isEmpty(accountNumber)) {
            return ""
        }
        val a4: String
        var last: String
        if (accountNumber.length > 4) {
            a4 = accountNumber.substring(accountNumber.length - 2, accountNumber.length)
            last = accountNumber.substring(0, accountNumber.length - 2)
            last = last.replace("\\S".toRegex(), "X")
            last = last.replace("....".toRegex(), "$0 ")
        } else {
            a4 = accountNumber
            last = ""
        }
        return "$last $a4"
    }

    /**
     * return first last name first character
     *
     * @param name name
     * @return char
     */
    fun getFirstLastNameFirstChar(name: String): String {
        var SinglePrefix = ""
        val splitindex = name.split(" ").toTypedArray()
        try {
            SinglePrefix = if (splitindex.size >= 2) {
                val Prefix = splitindex[0][0]
                val Prefix2 = if (!splitindex[1].isEmpty()) splitindex[1][0] else ' '
                ("" + Prefix + Prefix2).toUpperCase()
            } else {
                "" + splitindex[0][0]
            }
            SinglePrefix = SinglePrefix.toUpperCase()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return SinglePrefix
    }

 fun getOfficerName(name: String): String {
        var SinglePrefix = ""
        val splitindex = name.split(" ").toTypedArray()
        try {
            SinglePrefix = if (splitindex.size >= 2) {
                val Prefix = splitindex[1]
                val Prefix2 = if (!splitindex[0].isEmpty()) splitindex[0][0] else ' '
                ("" + Prefix +", "+ Prefix2+".").toUpperCase()
            } else {
                "" + splitindex[0][0]
            }
            SinglePrefix = SinglePrefix.toUpperCase()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return SinglePrefix
    }

    fun getPOEName(name: String, index: Int): String {
        var SinglePrefix = ""
        val splitindex = name.split(" ").toTypedArray()
        try {
            SinglePrefix = if (splitindex.size >= 2) {
                splitindex[index].toUpperCase()
            } else {
                "" + splitindex[index]
            }
            SinglePrefix = SinglePrefix.toUpperCase()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return SinglePrefix
    }

    fun parseDate(inputDateString: String?, inputDateFormat: SimpleDateFormat,
        outputDateFormat: SimpleDateFormat): String? {
        var date: Date? = null
        var outputDateString: String? = null
        try {
            date = inputDateFormat.parse(inputDateString)
            outputDateString = outputDateFormat.format(date)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return outputDateString
    }

    fun DateToMilise(inputDateString: String?): String {
        var milies = ""
        val sdf = SimpleDateFormat("dd MMM yyyy")
        try {
            val mDate = sdf.parse(inputDateString)
            val timeInMilliseconds = mDate.time
            milies = timeInMilliseconds.toString()
            println("Date in milli :: $timeInMilliseconds")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return milies
    }

    fun getContactName(phoneNumber: String?, context: Context): String {
        val uri = Uri.withAppendedPath(
            ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
            Uri.encode(phoneNumber)
        )
        val projection = arrayOf(ContactsContract.PhoneLookup.DISPLAY_NAME)
        var contactName = ""
        val cursor = context.contentResolver.query(uri, projection, null, null, null)
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                contactName = cursor.getString(0)
            }
            cursor.close()
        }
        return contactName
    }

    fun Base64Encoded(base64String: String): String {
        var base64 = base64String
        try {
            val data = base64String.toByteArray(charset("UTF-8"))
            base64 = Base64.encodeToString(data, Base64.NO_WRAP)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return base64
    }

    fun Base64Decoded(base64String: String?): String {
        var text = ""
        try {
            val data = Base64.decode(base64String, Base64.DEFAULT)
            text = String(data, StandardCharsets.UTF_8)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return text
    }

    fun getLangId(lang: String): String {
        if (lang.equals("en", ignoreCase = true)) {
            return "0"
        } else if (lang.equals("fr", ignoreCase = true)) {
            return "1"
        }
        return "0"
    }

    /**
     * navigate user to app store
     *
     * @param context context
     */
    fun openAppMarketLink(context: Context) {
        val appPackageName = context.packageName
        try {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=$appPackageName")
                )
            )
        } catch (anfe: ActivityNotFoundException) {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
                )
            )
        }
    }

    /**
     * This method generates a unique merchant TransactionID using the
     * UUID/emcertID and the current timestamp. This is required for miggs.
     *
     * @param context context
     * @return a unique merchantTrx ref.
     */
    fun generateMerchantTrxRef(context: Context?, sharedPref: SharedPref): String {
        var UUID = sharedPref.read(SharedPrefKey.DEVICE_ID, "")
        UUID = UUID?.substring(0, 9)
        val timestamp = Timestamp(System.currentTimeMillis())
        return UUID + "-" + timestamp.time
        //  return "12345";
    }

    fun generateRandomString(): String {
        val secureRandom = SecureRandom()

        /** Length of password. @see #generateRandomPassword()  */
        val PASSWORD_LENGTH = 15
        val letters = "abcdefghjkmnpqrstuvwxyzABCDEFGHJKMNPQRSTUVWXYZ23456789+@"
        var pw = ""
        for (i in 0 until PASSWORD_LENGTH) {
            val index = (secureRandom.nextDouble() * letters.length).toInt()
            pw += letters.substring(index, index + 1)
        }
        LogUtil.printLog(TAG, "String str=$pw")
        return pw
    }

    @JvmStatic
    fun showCustomAlertDialog(
        mContext: Context?, title: String?, msg: String?, yesBTNTxt: String?,
        noBTNTxt: String?, customDialogHelper: CustomDialogHelper
    ) {
        try {
            val dialog = AlertDialog.Builder(mContext!!)
                .setTitle(title)
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton(yesBTNTxt) { dialog, which ->
                    dialog.dismiss()
                    customDialogHelper.onYesButtonClickParam(title)
                }
                .setNegativeButton(noBTNTxt) { dialog, which ->
                    dialog.dismiss()
                    customDialogHelper.onNoButtonClick()
                }.create()

            dialog.setOnShowListener {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    ?.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimaryDark))
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                    ?.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimaryDark))
            }

            dialog.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    var alertDialogOnce: AlertDialog? = null

    @JvmStatic
    fun showCustomAlertDialogOnce(
        mContext: Context?, title: String?, msg: String?, yesBTNTxt: String?,
        noBTNTxt: String?, customDialogHelper: CustomDialogHelper
    ) {
        try {
            alertDialogOnce?.dismiss()  // Dismiss previous if showing

            alertDialogOnce = AlertDialog.Builder(mContext!!)
                .setTitle(title)
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton(yesBTNTxt) { dialog, which ->
                    dialog.dismiss()
                    customDialogHelper.onYesButtonClickParam(title)
                }
                .setNegativeButton(noBTNTxt) { dialog, which ->
                    dialog.dismiss()
                    customDialogHelper.onNoButtonClick()
                }.create()

            alertDialogOnce?.setOnShowListener {
                alertDialogOnce?.getButton(AlertDialog.BUTTON_POSITIVE)
                    ?.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimaryDark))
                alertDialogOnce?.getButton(AlertDialog.BUTTON_NEGATIVE)
                    ?.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimaryDark))
            }

            alertDialogOnce?.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    fun showCustomAlertDialogWithPositiveButton(mContext: Context?, title: String?, msg: String?,
        yesBTNTxt: String?, customDialogHelper: CustomDialogHelper) {
        AlertDialog.Builder(mContext!!)
            .setTitle(title)
            .setMessage(msg)
            .setCancelable(false)
            .setPositiveButton(yesBTNTxt) { dialog, which ->
                dialog.dismiss()
                customDialogHelper.onYesButtonClickParam(title)
            }
            .show()
    }

    fun showCustomAlertDialogSessionTimeOut(mContext: Context?, title: String?, msg: String?,
        yesBTNTxt: String?, noBTNTxt: String?, customDialogHelper: CustomDialogHelper) {
        AlertDialog.Builder(mContext!!)
            .setTitle(title)
            .setMessage(msg)
            .setCancelable(false)
            .setPositiveButton(yesBTNTxt) { dialog, which ->
                dialog.dismiss()
                customDialogHelper.onYesButtonClickParam("YES")
            }
            .setNegativeButton(noBTNTxt) { dialog, which ->
                dialog.dismiss()
                customDialogHelper.onYesButtonClickParam("NO")
            }
            .show()
    }

    // For animation of views in list
    fun runLayoutAnimation(context: Context?, recyclerView: RecyclerView) {
        val controller =
            AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_from_bottom)
        recyclerView.layoutAnimation = controller
        recyclerView.adapter!!.notifyDataSetChanged()
        recyclerView.scheduleLayoutAnimation()
    }

    fun getUri(context: Context, bitmap: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path =
            MediaStore.Images.Media.insertImage(context.contentResolver, bitmap, "Title", null)
        return Uri.parse(path)
    }

    @Throws(IOException::class)
    fun getBitmap(context: Context, uri: Uri?): Bitmap {
        return MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
    }

    fun ContactImages(context: Context, number: String?): Bitmap? {
        val contentResolver = context.contentResolver
        var contactId: String? = null
        val uri = Uri.withAppendedPath(
            ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
            Uri.encode(number)
        )
        val projection =
            arrayOf(ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup._ID)
        val cursor = contentResolver.query(
            uri,
            projection,
            null,
            null,
            null
        )
        if (cursor != null) {
            while (cursor.moveToNext()) {
                contactId =
                    cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup._ID))
            }
            cursor.close()
        }
        var photo: Bitmap? = null
        try {
            if (contactId != null) {
                val inputStream = ContactsContract.Contacts.openContactPhotoInputStream(
                    context.contentResolver,
                    ContentUris.withAppendedId(
                        ContactsContract.Contacts.CONTENT_URI,
                        contactId.nullSafety("0").toLong()
                    )
                )
                if (inputStream != null) {
                    photo = BitmapFactory.decodeStream(inputStream)
                }
                assert(inputStream != null)
                inputStream!!.close()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return photo
    }

    //LICENSE_PLATE_FORMAT
    @JvmStatic
    fun getRegexFromSetting(mValue: String, settingsList: List<DatasetResponse>): String {
        var isVisibility = ""
        val pattern = ""
        try {
//            ("[0-9]{1}[A-Z]{3}[0-9]{3}");
            for (i in settingsList.indices) {
//                LogUtil.printLog("setting type", settingsList.get(i).getType());
                if (mValue.equals(settingsList[i].type, ignoreCase = true)) {
                    isVisibility = settingsList[i].mRegex.nullSafety()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return isVisibility
    }

    @JvmStatic
    fun sixActionButtonVisibilityCheck(mValue: String): Boolean {
        var isVisibility = false
        try {
            val mDb: AppDatabase? = BaseApplication.instance?.getAppDatabase()
            val settingsList = Singleton.getDataSetList(DATASET_SETTINGS_LIST, mDb)
            for (i in settingsList!!.indices) {
//                LogUtil.printLog("setting type", settingsList.get(i).getType());
                if (mValue.equals(settingsList[i].type, ignoreCase = true)) {
                    isVisibility =
                        settingsList[i].mValue.equals("YES", ignoreCase = true)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return isVisibility
    }

    //MAX_IMAGES
    @JvmStatic
    fun maxImageCount(mValue: String): Int {
        var isVisibility = 0
        try {
            val mDb: AppDatabase? = BaseApplication.instance?.getAppDatabase()
            val settingsList = Singleton.getDataSetList(DATASET_SETTINGS_LIST, mDb)
            for (i in settingsList!!.indices) {
//                LogUtil.printLog("setting type", settingsList.get(i).getType());
                if (mValue.equals(settingsList[i].type, ignoreCase = true)) {
                    isVisibility = settingsList[i].mValue.nullSafety("0").toInt()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return isVisibility
    }

    fun getDeviceIdA(context: Context): String {
        var deviceId = ""
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            deviceId = Settings.Secure.getString(context.contentResolver,
                    Settings.Secure.ANDROID_ID)
        } else {
            val mTelephony = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            //            if (mTelephony.getDeviceId() != null) {
//                deviceId = mTelephony.getDeviceId();
//            } else {
//                deviceId = Settings.Secure.getString(
//                        context.getContentResolver(),
//                        Settings.Secure.ANDROID_ID);
//            }
        }
        return deviceId
    }

    @JvmStatic
    fun setLprLock(lockLprModel: LockLprModel?, mContext: Context?, sharedPref: SharedPref) {
        try {
            val json = ObjectMapperProvider.toJson(lockLprModel as LockLprModel)
            sharedPref.write(SharedPrefKey.LOCKED_LPR, json)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    fun getLprLock(mContext: Context?,sharedPref: SharedPref): LockLprModel? {
        var lockLprModel: LockLprModel? = null
        try {
            val json = sharedPref.read(SharedPrefKey.LOCKED_LPR, "")
            lockLprModel = ObjectMapperProvider.fromJson(json.nullSafety(), LockLprModel::class.java)
            return lockLprModel
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return lockLprModel
    }

    @JvmStatic
    fun checkPrintLayoutOrder(mViolationList: List<VehicleListModel>, mSection: String?):
            List<VehicleListModel> {
        val mTempList: MutableList<VehicleListModel> = ArrayList()
        if (mViolationList.size > 0) {
            try {
                val printOrderCount = doubleArrayOf(
                    1.0, 1.5, 1.66, 2.0, 2.5, 2.66, 3.0, 3.5, 3.66, 4.0, 4.5, 4.66,
                    5.0, 5.5, 5.66, 6.0, 6.5, 6.66, 7.0, 7.5, 7.66, 8.0, 8.5, 8.66, 9.0, 9.5, 9.66, 10.0
                )
                val secondLoopIndex = mViolationList[mViolationList.size - 1].mPrintOrder.toInt()
                val firstLoopCount = mViolationList.size
                var countIndexRange = 0
                //            if (mSection.equalsIgnoreCase("Comment")) {
//                  countIndexRange = secondLoopIndex * 2 ;
//            } else {
                countIndexRange = secondLoopIndex * 3
                //            }
                for (i in 0 until countIndexRange) {
                    val vehicleListModel = VehicleListModel()
                    vehicleListModel.mPrintOrder = printOrderCount[i]
                    vehicleListModel.offNameFirst = ""
                    vehicleListModel.offTypeFirst = ""
                    mTempList.add(vehicleListModel)
                }
                for (i in 0 until firstLoopCount) {
                    for (j in mTempList.indices) {
                        if (mViolationList[i].mPrintOrder == mTempList[j].mPrintOrder) {
                            mTempList[j] = mViolationList[i]
                            break
                        } else {
//                        VehicleListModel vehicleListModel = new VehicleListModel();
//                        vehicleListModel.segetmPrintOrder(printOrderCount[i]);
//                        vehicleListModel.setOffNameFirst("");
//                        vehicleListModel.setOffTypeFirst("");
//                        mViolationList.add(vehicleListModel);
//                        break;
                        }
                    }
                }
                return mTempList
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return mViolationList
        }
        return mViolationList
    }

    @JvmStatic
    fun checkPrintLayoutOrderForTwoColumn(mViolationList: List<VehicleListModel>, mSection: String?):
            List<VehicleListModel> {
        val mTempList: MutableList<VehicleListModel> = ArrayList()
        if (mViolationList.size > 0) {
            try {
                val printOrderCount = doubleArrayOf(
                    1.0, 1.5, 2.0, 2.5,  3.0, 3.5,  4.0, 4.5,
                    5.0, 5.5,  6.0, 6.5, 7.0, 7.5,  8.0, 8.5, 9.0, 9.5, 10.0, 10.5)
                val secondLoopIndex = mViolationList[mViolationList.size - 1].mPrintOrder.toInt()
                val firstLoopCount = mViolationList.size
                var countIndexRange = 0
                //            if (mSection.equalsIgnoreCase("Comment")) {
//                  countIndexRange = secondLoopIndex * 2 ;
//            } else {
                countIndexRange = secondLoopIndex * 2
                //            }
                for (i in 0 until countIndexRange) {
                    val vehicleListModel = VehicleListModel()
                    vehicleListModel.mPrintOrder = printOrderCount[i]
                    vehicleListModel.offNameFirst = ""
                    vehicleListModel.offTypeFirst = ""
                    mTempList.add(vehicleListModel)
                }
                for (i in 0 until firstLoopCount) {
                    for (j in mTempList.indices) {
                        if (mViolationList[i].mPrintOrder == mTempList[j].mPrintOrder) {
                            mTempList[j] = mViolationList[i]
                            break
                        } else {
//                        VehicleListModel vehicleListModel = new VehicleListModel();
//                        vehicleListModel.segetmPrintOrder(printOrderCount[i]);
//                        vehicleListModel.setOffNameFirst("");
//                        vehicleListModel.setOffTypeFirst("");
//                        mViolationList.add(vehicleListModel);
//                        break;
                        }
                    }
                }
                return mTempList
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return mViolationList
        }
        return mViolationList
    }

    @JvmStatic
    fun checkPrintLayoutOrderComment(mViolationList: List<VehicleListModel>, mSection: String?
    ): List<VehicleListModel> {
        val mTempList: MutableList<VehicleListModel> = ArrayList()
        try {
            val printOrderCount = doubleArrayOf(
                1.0, 2.0, 3.0, 4.0,
                5.0, 6.0, 7.0, 8.0, 9.0
            )
            val secondLoopIndex = mViolationList[mViolationList.size - 1].mPrintOrder.toInt()
            val firstLoopCount = mViolationList.size
            var countIndexRange = 0
            countIndexRange = secondLoopIndex
            for (i in 0 until countIndexRange) {
                val vehicleListModel = VehicleListModel()
                vehicleListModel.mPrintOrder = printOrderCount[i]
                vehicleListModel.offNameFirst = ""
                vehicleListModel.offTypeFirst = ""
                mTempList.add(vehicleListModel)
            }
            for (i in 0 until firstLoopCount) {
                for (j in mTempList.indices) {
                    if (mViolationList[i].mPrintOrder == mTempList[j].mPrintOrder) {
                        mTempList[j] = mViolationList[i]
                        break
                    } else {
//                        VehicleListModel vehicleListModel = new VehicleListModel();
//                        vehicleListModel.setmPrintOrder(printOrderCount[i]);
//                        vehicleListModel.setOffNameFirst("");
//                        vehicleListModel.setOffTypeFirst("");
//                        mViolationList.add(vehicleListModel);
//                        break;
                    }
                }
            }
            return mTempList
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return mTempList
    }

    interface TextWatcherCallBackForAPICall {
        fun callActivityLogAPI()
    }

        private var mRegex = ""
    @JvmStatic
    fun textWatcherForLicensePlate(editText: AppCompatAutoCompleteTextView, settingsList: List<DatasetResponse>,
                                   mContext: Context, textWatcherCallBackForAPICall : TextWatcherCallBackForAPICall)  {
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                textWatcherCallBackForAPICall.callActivityLogAPI()
            }
            override fun afterTextChanged(s: Editable) {
                try {
                    if(mRegex.isEmpty()) {
                        mRegex = getRegexFromSetting("LICENSE_PLATE_FORMAT", settingsList)
                    }
                    val p = Pattern.compile(mRegex)
                    val m = p.matcher(s)
                    val b = m.matches()
                    val p1 = Pattern.compile(mRegex) //EPC8201
                    val m1 = p1.matcher(s)
                    val b1 = m1.matches()
                    if (b) {
                        editText.setCompoundDrawablesWithIntrinsicBounds(
                            0,
                            0,
                            R.drawable.ic_right,
                            0
                        )
                    } else if (b1) {
                        editText.setCompoundDrawablesWithIntrinsicBounds(
                            0,
                            0,
                            R.drawable.ic_right,
                            0
                        )
                    } else {
                        editText.setCompoundDrawablesWithIntrinsicBounds(
                            0,
                            0,
                            R.drawable.ic_cross_lpr,
                            0
                        )
                    }
                    val img = mContext.resources.getDrawable(R.drawable.ic_cross_lpr)
                    img.setBounds(0, 0, 60, 60) // set the image size
                    editText.setCompoundDrawables(img, null, null, null)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })
    }

    @JvmStatic
    fun textWatcherForDateOfBirth(autoCompleteDob: AppCompatAutoCompleteTextView?, onTextChanged : (String) -> Unit)  {
        //MM/dd/yyyy support
        autoCompleteDob?.addTextChangedListener(object : TextWatcher {
            private var isEditing = false
            private var lastText = ""

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (isEditing || s == null) return

                val input = s.toString()
                val digits = input.replace("[^\\d]".toRegex(), "")
                if (digits == lastText) return

                isEditing = true
                val sb = StringBuilder()
                val currentYear = Calendar.getInstance().get(Calendar.YEAR)
                var cursorPos = autoCompleteDob.selectionStart

                var index = 0

                // --- Month ---
                var mm = ""
                if (digits.length >= 1) {
                    val m1 = digits[0].digitToInt()
                    if (m1 in 2..9) {
                        mm = "0$m1"
                        index = 1
                    } else if (digits.length >= 2) {
                        val m = digits.substring(0, 2).toInt()
                        mm = m.coerceAtMost(12).toString().padStart(2, '0')
                        index = 2
                    } else {
                        mm = digits
                        index = digits.length
                    }
                }

                // --- Day ---
                var dd = ""
                if (digits.length > index) {
                    val remaining = digits.substring(index)
                    if (remaining.length >= 1) {
                        val d1 = remaining[0].digitToInt()
                        if (d1 in 4..9) {
                            dd = "0$d1"
                            index += 1
                        } else if (remaining.length >= 2) {
                            val d = remaining.substring(0, 2).toInt()
                            val maxDay = when (mm.toIntOrNull() ?: 1) {
                                2 -> 29
                                4, 6, 9, 11 -> 30
                                else -> 31
                            }
                            dd = d.coerceAtMost(maxDay).toString().padStart(2, '0')
                            index += 2
                        } else {
                            dd = remaining
                            index += remaining.length
                        }
                    }
                }

                // --- Year ---
                var yyyy = ""
                if (digits.length > index) {
                    yyyy = digits.substring(index).take(4)
                    if (yyyy.length == 4 && yyyy.toInt() > currentYear) {
                        yyyy = currentYear.toString()
                    }
                }

                // --- Format ---
                if (mm.isNotEmpty()) sb.append(mm)
                if (mm.length == 2 && dd.isNotEmpty()) sb.append("/").append(dd)
                if (mm.length == 2 && dd.length == 2 && yyyy.isNotEmpty()) sb.append("/").append(yyyy)

                lastText = digits
                autoCompleteDob?.setText(sb.toString())
                autoCompleteDob?.setSelection(sb.length.coerceAtMost(autoCompleteDob.text.length))

                isEditing = false
            }
        })

        //dd/MM/yyyy
//        autoCompleteDob?.addTextChangedListener(object : TextWatcher {
//            private var isEditing = false
//            private var lastText = ""
//
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
//
//            override fun afterTextChanged(s: Editable?) {
//                if (isEditing || s == null) return
//
//                val input = s.toString()
//                val digits = input.replace("[^\\d]".toRegex(), "")
//                if (digits == lastText) return
//
//                isEditing = true
//                val sb = StringBuilder()
//                val currentYear = Calendar.getInstance().get(Calendar.YEAR)
//                var index = 0
//
//                // --- Day ---
//                var dd = ""
//                if (digits.length >= 1) {
//                    val d1 = digits[0].digitToInt()
//                    if (d1 in 4..9) {
//                        dd = "0$d1"
//                        index = 1
//                    } else if (digits.length >= 2) {
//                        val d = digits.substring(0, 2).toInt()
//                        dd = d.toString().padStart(2, '0')
//                        index = 2
//                    } else {
//                        dd = digits
//                        index = digits.length
//                    }
//                }
//
//                // --- Month ---
//                var mm = ""
//                if (digits.length > index) {
//                    val remaining = digits.substring(index)
//                    if (remaining.isNotEmpty()) {
//                        val m1 = remaining[0].digitToInt()
//                        if (m1 in 2..9) {
//                            mm = "0$m1"
//                            index += 1
//                        } else if (remaining.length >= 2) {
//                            val m = remaining.substring(0, 2).toInt()
//                            mm = m.coerceAtMost(12).toString().padStart(2, '0')
//                            index += 2
//                        } else {
//                            mm = remaining
//                            index += remaining.length
//                        }
//                    }
//                }
//
//                // --- Clamp day based on month ---
//                if (dd.length == 2 && mm.length == 2) {
//                    val dayInt = dd.toInt()
//                    val monthInt = mm.toInt()
//                    val maxDay = when (monthInt) {
//                        2 -> 29
//                        4, 6, 9, 11 -> 30
//                        else -> 31
//                    }
//                    if (dayInt > maxDay) {
//                        dd = maxDay.toString().padStart(2, '0')
//                    }
//                }
//
//                // --- Year ---
//                var yyyy = ""
//                if (digits.length > index) {
//                    yyyy = digits.substring(index).take(4)
//                    if (yyyy.length == 4 && yyyy.toInt() > currentYear) {
//                        yyyy = currentYear.toString()
//                    }
//                }
//
//                // If need to limit minimum year--- Year ---
////                var yyyy = ""
////                val minYear = 1900
////                val currentYear = Calendar.getInstance().get(Calendar.YEAR)
////
////                if (digits.length > index) {
////                    yyyy = digits.substring(index).take(4)
////                    if (yyyy.length == 4) {
////                        val yearInt = yyyy.toInt()
////                        yyyy = when {
////                            yearInt < minYear -> minYear.toString()
////                            yearInt > currentYear -> currentYear.toString()
////                            else -> yearInt.toString()
////                        }
////                    }
////                }
//
//                // --- Format ---
//                if (dd.isNotEmpty()) sb.append(dd)
//                if (dd.length == 2 && mm.isNotEmpty()) sb.append("/").append(mm)
//                if (dd.length == 2 && mm.length == 2 && yyyy.isNotEmpty()) sb.append("/").append(yyyy)
//
//                lastText = digits
//                autoCompleteDob?.setText(sb.toString())
//                autoCompleteDob?.setSelection(sb.length.coerceAtMost(autoCompleteDob.text.length))
//                isEditing = false
//
//                //Returning the formatted date string
//                onTextChanged(s.toString())
//            }
//        })
    }

    @JvmStatic
    fun setListOnly(context: Context, appCompatAutoCompleteTextView: AppCompatAutoCompleteTextView
    ): AppCompatAutoCompleteTextView {
        appCompatAutoCompleteTextView.onFocusChangeListener = OnFocusChangeListener { view, b ->
            if (!b) {
                // on focus off
                val str = appCompatAutoCompleteTextView.text.toString()
                val listAdapter = appCompatAutoCompleteTextView.adapter
                for (i in 0 until listAdapter.count) {
                    val temp = listAdapter.getItem(i).toString()
                    if (str.contains(temp,ignoreCase = true)) {
                        appCompatAutoCompleteTextView.background =
                            context.getDrawable(R.drawable.round_corner_shape_without_fill_thin_grey)
                        return@OnFocusChangeListener
                    }
                }
                appCompatAutoCompleteTextView.background =
                    context.getDrawable(R.drawable.round_corner_shape_without_fill_thin_red)
                appCompatAutoCompleteTextView.setText("")
                val vibe = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                vibe.vibrate(200) // 50 is time in ms
            }
        }
        return appCompatAutoCompleteTextView
    }

    fun printLabelHeight(height:Int): Int {
        return if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RISE_TEK,ignoreCase = true) ||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOL,ignoreCase = true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RISE_TEK_INNOVA,ignoreCase = true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_FLOWBIRD,ignoreCase = true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLASGOW,ignoreCase = true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MACKAY,ignoreCase = true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CARTA,ignoreCase = true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BELLINGHAM,ignoreCase = true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_OCEANCITY,ignoreCase = true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EPHRATA,ignoreCase = true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_Easton,ignoreCase = true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CLIFTON,ignoreCase = true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MYSTICCT,ignoreCase = true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZPILOT,ignoreCase = true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ_ATLANTA,ignoreCase = true)||
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ,ignoreCase = true)) {
            (height * 0.07).toInt()
        } else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RISE_TEK_OKC,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_VIRGINIA,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZLB,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WESTCHESTER,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BURBANK,ignoreCase = true)){
            (height * 0.09).toInt()
        } else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DUNCAN,ignoreCase = true)||
            (BuildConfig.FLAVOR.equals(DuncanBrandingApp13())&&
            !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_KENOSHA,ignoreCase = true)&&
            !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_NORWALK,ignoreCase = true))){
            (height * 0.014).toInt()
        }else if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_NORWALK,ignoreCase = true)){
            (height * 0.020).toInt()
        }else if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_KENOSHA,ignoreCase = true)){
            (height * 0.045).toInt()
        }else if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PILOTPITTSBURGPA,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MACKAY_SAMPLE,ignoreCase = true)){
            (height * 0.015).toInt()
        }else if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CHARLESTON,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ_CCP,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PORTLAND_RO_REIMAGINED_PARKING,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CHATTANOOGA_TN_REIMAGINED_PARKING,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_JACKSONVILLE_FL_REIMAGINED_PARKING,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_OF_WILTON_MANORS_REIMAGINED_PARKING,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_REEF_CASPER_WY_REIMAGINED_PARKING,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MOBILE_AL_REIMAGINED_PARKING,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_FAYETTEVILLE_NC_REIMAGINED_PARKING,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_IMPARK_PHSA,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HILLSBORO_OR_REIMAGINED_PARKING,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PHSA_REIMAGINED_PARKING,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MERRICK_PARK_FL_REIMAGINED_PARKING,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ_KCMO_PRIVATE,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_A_BOBS_TOWING,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SEPTA,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_KANSAS_CITY,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EL_CAMINO_COLLEGE,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VEST_SECURITY_SYSTEM,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_JEFFERSON_CITY_MO,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MTA_LONG_ISLAND_RAIL_ROAD,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SEASTREAK,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ATLANTIC_BEACH_NC,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ATLANTIC_BEACH_SC,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ST_JOHNSBURY_VT,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_OXFORD,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRIME_PARKING,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_OF_WATERLOO,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EL_PASO_TX_MACKAY,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DANVILLE_VA,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CAMDEN,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTH_LAKE,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WINPARK_TX,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HAMTRAMCK_MI,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTHMIAMI,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ASHLAND,ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ_CONJUCTIVE_POINT,ignoreCase = true)
            ){
            (height * 0.05).toInt()
        }else if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CLEMENS,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_KALAMAZOO,ignoreCase = true)){
            (height * 0.10).toInt()
        }else if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RIVEROAKS,ignoreCase = true)){
            (height * 0.13).toInt()
        }else if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLENDALE,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLENDALE_POLICE,ignoreCase = true)){
            (height * 0.14).toInt()
        }else if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MANSFIELDCT,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HARTFORD,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_COB,ignoreCase = true)){
            (height * 0.040).toInt()
        }else if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DURANGO,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LITTLEROCK,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKTEXAS,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOTTLEWORKS_IN,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RUTGERS,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PARKX,ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKPARKING,ignoreCase = true)){
            (height * 0.02).toInt()
        }else {
            (height * 0.02).toInt()
        }
    }

    @JvmStatic
    fun getSiteId(mContext: Context): String {
        if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RISE_TEK,ignoreCase = true)) {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(Constants.RELEASE_TYPE_UAT,
                            ignoreCase = true)) {
                mContext.getString(R.string.SITE_ID)
            } else {
                mContext.getString(R.string.SITE_ID_PROD)
            }
        } else if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RISE_TEK_OKC,ignoreCase = true)) {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(Constants.RELEASE_TYPE_UAT,
                            ignoreCase = true)) {
                mContext.getString(R.string.SITE_ID_RISETEK_OKC_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_RISETEK_OKC_PROD)
            }
        } else if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CEDAR, ignoreCase = true)) {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(Constants.RELEASE_TYPE_UAT,
                            ignoreCase = true)) {
                mContext.getString(R.string.SITE_ID_CEDAR_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_CEDAR_PROD)
            }
        } else if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLENDALE, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLENDALE_POLICE, ignoreCase = true)) {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(Constants.RELEASE_TYPE_UAT,
                            ignoreCase = true)) {
                mContext.getString(R.string.SITE_ID_Glendale)
//                mContext.getString(R.string.SITE_ID_Glendale_prod)
            } else {
                mContext.getString(R.string.SITE_ID_Glendale_prod)
            }
        } else if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RISE_TEK_INNOVA,
                        ignoreCase = true)) {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(Constants.RELEASE_TYPE_UAT,
                    ignoreCase = true)) {
                mContext.getString(R.string.SITE_ID_Innova) // RiseTek Innova
            } else {
                mContext.getString(R.string.SITE_ID_Innova_PROD) // RiseTek Innova
            }
        }
        else if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PARK,ignoreCase = true)) {
            if (BuildConfig.BUILD_RELEASE_TYPE.equals(Constants.RELEASE_TYPE_UAT)) {
                return mContext.getString(R.string.SITE_ID_Greenvile);// RiseTek Innova
            } else {
                return mContext.getString(R.string.SITE_ID_Greenvile_PROD);// RiseTek Innova
            }
        }else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DUNCAN,ignoreCase = true))
        {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(Constants.RELEASE_TYPE_UAT,
                            ignoreCase = true)) {
                mContext.getString(R.string.SITE_ID)
//                mContext.getString(R.string.SITE_ID_Phili_TRANINING)
//                mContext.getString(R.string.SITE_ID_Phili_PROD)
            } else {
//                mContext.getString(R.string.SITE_ID_Phili_TRANINING)
                mContext.getString(R.string.SITE_ID_Phili_PROD)
            }

        }else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RIDGEHILL,ignoreCase = true))
        {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(Constants.RELEASE_TYPE_UAT,
                            ignoreCase = true)) {
                mContext.getString(R.string.SITE_ID_RIDGEHILL_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_RIDGEHILL_PROD)
            }
        }else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VALLEJOL,ignoreCase = true))
        {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(Constants.RELEASE_TYPE_UAT,
                            ignoreCase = true)) {
                mContext.getString(R.string.SITE_ID_VALLEJOL_UAT)
//                mContext.getString(R.string.SITE_ID_VALLEJOL_PROD)
            } else {
                mContext.getString(R.string.SITE_ID_VALLEJOL_PROD)
            }
        }else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LEAVENWORTH,ignoreCase = true))
        {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(Constants.RELEASE_TYPE_UAT,
                            ignoreCase = true)) {
                mContext.getString(R.string.SITE_ID_LEAVENWORTH_UAT)
//                mContext.getString(R.string.SITE_ID_LEAVENWORTH_PROD)
            } else {
                mContext.getString(R.string.SITE_ID_LEAVENWORTH_PROD)
            }
        }else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_FLOWBIRD,ignoreCase = true))
        {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(Constants.RELEASE_TYPE_UAT,
                            ignoreCase = true)) {
                mContext.getString(R.string.SITE_ID_FLOWBIRD_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_FLOWBIRD_UAT)
            }
        }else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MACKAY, ignoreCase = true))
        {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(Constants.RELEASE_TYPE_UAT,
                            ignoreCase = true)) {
                mContext.getString(R.string.SITE_ID_MACKAY_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_MACKAY_UAT)
            }
        }else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ, ignoreCase = true))
        {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(Constants.RELEASE_TYPE_UAT,
                            ignoreCase = true)) {
                mContext.getString(R.string.SITE_ID_LAZ_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_LAZ_UAT)
            }
        }else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZPILOT, ignoreCase = true))
        {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(Constants.RELEASE_TYPE_UAT,
                            ignoreCase = true)) {
                mContext.getString(R.string.SITE_ID_LAZ_PILOT_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_LAZ_UAT)
            }
        }else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CARTA, ignoreCase = true))
        {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(Constants.RELEASE_TYPE_UAT,
                            ignoreCase = true)) {
                mContext.getString(R.string.SITE_ID_Chatanooga_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_Chatanooga_PROD)
            }
        }else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ENCINITAS, ignoreCase = true))
        {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(Constants.RELEASE_TYPE_UAT,
                            ignoreCase = true)) {
                mContext.getString(R.string.SITE_ID_Encinitas_UAT)
//                mContext.getString(R.string.SITE_ID_Encinitas_PROD)
            } else {
                mContext.getString(R.string.SITE_ID_Encinitas_PROD)
            }
        }else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SCPM, ignoreCase = true))
        {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(Constants.RELEASE_TYPE_UAT,
                            ignoreCase = true)) {
                mContext.getString(R.string.SITE_ID_SCPM_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_SCPM_UAT)
            }
        }else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PHOENIX, ignoreCase = true))
        {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(Constants.RELEASE_TYPE_UAT,
                            ignoreCase = true)) {
                mContext.getString(R.string.SITE_ID_PHOENIX_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_PHOENIX_PROD)
            }
        }else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANDIEGO, ignoreCase = true))
        {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(Constants.RELEASE_TYPE_UAT,
                            ignoreCase = true)) {
                mContext.getString(R.string.SITE_ID_SAN_DIEGO_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_SAN_DIEGO_PROD)
            }
        }else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITYOFSANDIEGO, ignoreCase = true))
        {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(Constants.RELEASE_TYPE_UAT,
                            ignoreCase = true)) {
                mContext.getString(R.string.SITE_ID_SAN_DIEGO_PPA_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_SAN_DIEGO_PPA_PROD)
            }
        }else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CHULAVISTA, ignoreCase = true))
        {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(Constants.RELEASE_TYPE_UAT,
                            ignoreCase = true)) {
                mContext.getString(R.string.SITE_ID_CHULA_VISTA_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_CHULA_VISTA_PROD)
            }
        }else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ROSEBURG, ignoreCase = true))
        {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(Constants.RELEASE_TYPE_UAT,
                            ignoreCase = true)) {
                mContext.getString(R.string.SITE_ID_ROSEBURG_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_ROSEBURG_PROD)
            }
        }else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_IRVINE, ignoreCase = true)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_NORTHERN_CALIFORNIA, ignoreCase = true))
        {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(Constants.RELEASE_TYPE_UAT,
                            ignoreCase = true)) {
                mContext.getString(R.string.SITE_ID_IRVINE_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_IRVINE_PROD)
            }
        }else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MONKTON, ignoreCase = true))
        {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(Constants.RELEASE_TYPE_UAT,
                            ignoreCase = true)) {
                mContext.getString(R.string.SITE_ID_MONKTON_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_MONKTON_PROD)
            }
        }else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOL, ignoreCase = true))
        {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(Constants.RELEASE_TYPE_UAT,
                            ignoreCase = true)) {
                mContext.getString(R.string.SITE_ID_SOL_UAT)
//                mContext.getString(R.string.SITE_ID_SOL_PROD)
            } else {
                mContext.getString(R.string.SITE_ID_SOL_PROD)
            }
        }else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BELLINGHAM, ignoreCase = true))
        {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(Constants.RELEASE_TYPE_UAT,
                            ignoreCase = true)) {
                mContext.getString(R.string.SITE_ID_BELLINGHAM_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_BELLINGHAM_UAT)
            }
        }else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_OCEANCITY, ignoreCase = true))
        {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(Constants.RELEASE_TYPE_UAT,
                            ignoreCase = true)) {
                mContext.getString(R.string.SITE_ID_OCEANCITY_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_OCEANCITY_PROD)
            }
        }else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DURANGO, ignoreCase = true))
        {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(Constants.RELEASE_TYPE_UAT,
                            ignoreCase = true)) {
                mContext.getString(R.string.SITE_ID_DURANGO_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_DURANGO_PROD)
            }
        }else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CFFB, ignoreCase = true))
        {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(Constants.RELEASE_TYPE_UAT,
                            ignoreCase = true)) {
                mContext.getString(R.string.SITE_ID_CFFB_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_CFFB_PROD)
            }
        }else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HILTONHEAD, ignoreCase = true))
        {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(Constants.RELEASE_TYPE_UAT,
                            ignoreCase = true)) {
                mContext.getString(R.string.SITE_ID_HILTON_HEAD_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_HILTON_HEAD_PROD)
            }
        }else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BURBANK, ignoreCase = true))
        {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(Constants.RELEASE_TYPE_UAT,
                            ignoreCase = true)) {
                mContext.getString(R.string.SITE_ID_BURBANK_CA_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_BURBANK_CA_PROD)
            }
        }else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PILOTPITTSBURGPA, ignoreCase = true))
        {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(Constants.RELEASE_TYPE_UAT,
                            ignoreCase = true)) {
                mContext.getString(R.string.SITE_ID_PILOTPITTSBURGPA_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_PILOTPITTSBURGPA_PROD)
            }
        }else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MANSFIELDCT, ignoreCase = true))
        {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(Constants.RELEASE_TYPE_UAT,
                            ignoreCase = true)) {
                mContext.getString(R.string.SITE_ID_MANSFIELDCT_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_MANSFIELDCT_PROD)
            }
        }else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CLEMENS, ignoreCase = true))
        {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(Constants.RELEASE_TYPE_UAT,
                            ignoreCase = true)) {
                mContext.getString(R.string.SITE_ID_CLEMENSKICK_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_CLEMENSKICK_PROD)
            }
        }else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_VIRGINIA, ignoreCase = true))
        {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(Constants.RELEASE_TYPE_UAT,
                            ignoreCase = true)) {
                mContext.getString(R.string.SITE_ID_VIRGINIA_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_VIRGINIA_PROD)
            }
        }else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MYSTICCT, ignoreCase = true))
        {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(Constants.RELEASE_TYPE_UAT,
                            ignoreCase = true)) {
                mContext.getString(R.string.SITE_ID_MYSTICCT_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_MYSTICCT_PROD)
            }
        }else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CHARLESTON, ignoreCase = true))
        {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(Constants.RELEASE_TYPE_UAT,
                            ignoreCase = true)) {
                mContext.getString(R.string.SITE_ID_CHARLESTON_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_CHARLESTON_PROD)
            }
        }else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BISMARCK, ignoreCase = true))
        {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(Constants.RELEASE_TYPE_UAT,
                            ignoreCase = true)) {
                mContext.getString(R.string.SITE_ID_BISMARCK_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_BISMARCK_PROD)
            }
        }else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SATELLITE, ignoreCase = true))
        {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(Constants.RELEASE_TYPE_UAT,
                            ignoreCase = true)) {
                mContext.getString(R.string.SITE_ID_SATELLITE_UAT)
//                mContext.getString(R.string.SITE_ID_SATELLITE_PROD)
            } else {
                mContext.getString(R.string.SITE_ID_SATELLITE_PROD)
            }
        }else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BURLINGTON, ignoreCase = true))
        {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(Constants.RELEASE_TYPE_UAT,
                            ignoreCase = true)) {
                mContext.getString(R.string.SITE_ID_BURLINGTON_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_BURLINGTON_PROD)
            }
        }else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ADOBE, ignoreCase = true))
        {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(Constants.RELEASE_TYPE_UAT,
                            ignoreCase = true)) {
                mContext.getString(R.string.SITE_ID_ADOBE_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_ADOBE_PROD)
            }
        }else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MACKAY_SAMPLE, ignoreCase = true))
        {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(Constants.RELEASE_TYPE_UAT,
                            ignoreCase = true)) {
                mContext.getString(R.string.SITE_ID_MACKAY_SAMPLE_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_MACKAY_SAMPLE_UAT)
            }
        }else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ_ATLANTA, ignoreCase = true))
        {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(Constants.RELEASE_TYPE_UAT,
                    ignoreCase = true)) {
                mContext.getString(R.string.SITE_ID_LAZ_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_LAZ_UAT)
            }
        }else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ_CCP, ignoreCase = true))
        {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(Constants.RELEASE_TYPE_UAT,
                    ignoreCase = true)) {
                mContext.getString(R.string.SITE_ID_LAZ_CCP_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_LAZ_CCP_PROD)
            }
        }else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MARTIN, ignoreCase = true))
        {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(Constants.RELEASE_TYPE_UAT,
                    ignoreCase = true)) {
                mContext.getString(R.string.SITE_ID_MARTIN_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_MARTIN_PROD)
            }
        }else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANFRANCISCO, ignoreCase = true))
        {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(Constants.RELEASE_TYPE_UAT,
                    ignoreCase = true)) {
                mContext.getString(R.string.SITE_ID_SAN_FRANCISCO_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_SAN_FRANCISCO_PROD)
            }
        }else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_FRESNO, ignoreCase = true))
        {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(Constants.RELEASE_TYPE_UAT,
                    ignoreCase = true)) {
                mContext.getString(R.string.SITE_ID_ACE_FRENSO_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_ACE_FRENSO_PROD)
            }
        }else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_VALET_DIVISION, ignoreCase = true))
        {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(Constants.RELEASE_TYPE_UAT,
                    ignoreCase = true)) {
                mContext.getString(R.string.SITE_ID_ACE_VALET_DIVISION_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_ACE_VALET_DIVISION_PROD)
            }
        }else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_SAN_ANTONIO, ignoreCase = true))
        {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(Constants.RELEASE_TYPE_UAT,
                    ignoreCase = true)) {
                mContext.getString(R.string.SITE_ID_ACE_SAN_ANTONIO_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_ACE_SAN_ANTONIO_PROD)
            }
        }else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_LAKE_TAHOE, ignoreCase = true))
        {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(Constants.RELEASE_TYPE_UAT,
                    ignoreCase = true)) {
                mContext.getString(R.string.SITE_ID_ACE_LAKE_TAHOE_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_ACE_LAKE_TAHOE_PROD)
            }
        }else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_KALAMAZOO, ignoreCase = true))
        {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(Constants.RELEASE_TYPE_UAT,
                    ignoreCase = true)) {
                mContext.getString(R.string.SITE_ID_KALAMAZOO_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_KALAMAZOO_PROD)
            }
        }else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ISLEOFPALMS, ignoreCase = true))
        {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(Constants.RELEASE_TYPE_UAT,
                    ignoreCase = true)) {
                mContext.getString(R.string.SITE_ID_ISLE_PLAMS_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_ISLE_PLAMS_PROD)
            }
        }else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAMETRO, ignoreCase = true))
        {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(Constants.RELEASE_TYPE_UAT,
                    ignoreCase = true)) {
                mContext.getString(R.string.SITE_ID_LA_METRO_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_LA_METRO_PROD)
            }
        }else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAWRENCE, ignoreCase = true))
        {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(Constants.RELEASE_TYPE_UAT,
                    ignoreCase = true)) {
                mContext.getString(R.string.SITE_ID_LAWRENCE_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_LAWRENCE_PROD)
            }
        }else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HARTFORD, ignoreCase = true))
        {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(Constants.RELEASE_TYPE_UAT,
                    ignoreCase = true)) {
                mContext.getString(R.string.SITE_ID_HARTFORD_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_HARTFORD_PROD)
            }
        }else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_COB, ignoreCase = true))
        {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(Constants.RELEASE_TYPE_UAT,
                    ignoreCase = true)) {
                mContext.getString(R.string.SITE_ID_BANGOR_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_BANGOR_PROD)
            }
        }else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZLB, ignoreCase = true))
        {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(Constants.RELEASE_TYPE_UAT,
                    ignoreCase = true)) {
                mContext.getString(R.string.SITE_ID_LAZLB_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_LAZLB_PROD)
            }
        }else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RIVEROAKS, ignoreCase = true))
        {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(Constants.RELEASE_TYPE_UAT,
                    ignoreCase = true)) {
                mContext.getString(R.string.SITE_ID_RIVER_OAKS_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_RIVER_OAKS_PROD)
            }
        }else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ASHLAND, ignoreCase = true))
        {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(Constants.RELEASE_TYPE_UAT,
                    ignoreCase = true)) {
                mContext.getString(R.string.SITE_ID_ASHLAND_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_ASHLAND_PROD)
            }
        }else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACEAMAZON, ignoreCase = true))
        {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(Constants.RELEASE_TYPE_UAT,
                    ignoreCase = true)) {
                mContext.getString(R.string.SITE_ID_ACE_AMAZON_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_ACE_AMAZON_PROD)
            }
        }
        else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BEAUFORT, ignoreCase = true))
        {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(Constants.RELEASE_TYPE_UAT,
                    ignoreCase = true)) {
                mContext.getString(R.string.SITE_ID_BEAUFORT_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_BEAUFORT_PROD)
            }
        }
        else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANIBEL, ignoreCase = true))
        {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(Constants.RELEASE_TYPE_UAT,
                    ignoreCase = true)) {
                mContext.getString(R.string.SITE_ID_SANIBEL_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_SANIBEL_PROD)
            }
        }else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_FASHION_CITY, ignoreCase = true))
        {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(Constants.RELEASE_TYPE_UAT,
                    ignoreCase = true)) {
                mContext.getString(R.string.SITE_ID_ACE_FASHION_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_ACE_FASHION_PROD)
            }
        }else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLASGOW, ignoreCase = true))
        {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(Constants.RELEASE_TYPE_UAT,
                    ignoreCase = true)) {
                mContext.getString(R.string.SITE_ID_GLASGOW_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_GLASGOW_PROD)
            }
        }else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_COHASSET, ignoreCase = true))
        {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(Constants.RELEASE_TYPE_UAT,
                    ignoreCase = true)) {
                mContext.getString(R.string.SITE_ID_COHASSET_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_COHASSET_PROD)
            }
        }else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SMYRNABEACH, ignoreCase = true))
        {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(Constants.RELEASE_TYPE_UAT,
                    ignoreCase = true)) {
                mContext.getString(R.string.SITE_ID_SMYRNA_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_SMYRNA_PROD)
            }
        }else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LITTLEROCK, ignoreCase = true))
        {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(Constants.RELEASE_TYPE_UAT,
                    ignoreCase = true)) {
                mContext.getString(R.string.SITE_ID_LITTLE_ROCK_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_LITTLE_ROCK_PROD)
            }
        }else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MEMORIALHERMAN, ignoreCase = true))
        {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(Constants.RELEASE_TYPE_UAT,
                    ignoreCase = true)) {
                mContext.getString(R.string.SITE_ID_MEMORIAL_HERMAN_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_MEMORIAL_HERMAN_PROD)
            }
        }else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKPARKING, ignoreCase = true))
        {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(Constants.RELEASE_TYPE_UAT,
                    ignoreCase = true)) {
                mContext.getString(R.string.SITE_ID_PEAK_PARKING_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_PEAK_PARKING_PROD)
            }
        }else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKTEXAS, ignoreCase = true))
        {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(Constants.RELEASE_TYPE_UAT,
                    ignoreCase = true)) {
                mContext.getString(R.string.SITE_ID_PEAK_TEXAS_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_PEAK_TEXAS_PROD)
            }
        }else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SEPTA, ignoreCase = true))
        {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(Constants.RELEASE_TYPE_UAT,
                    ignoreCase = true)) {
                mContext.getString(R.string.SITE_ID_SEPTA_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_SEPTA_PROD)
            }
        }else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_KANSAS_CITY, ignoreCase = true))
        {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(Constants.RELEASE_TYPE_UAT,
                    ignoreCase = true)) {
                mContext.getString(R.string.SITE_ID_KANSAS_PROD)
            } else {
                mContext.getString(R.string.SITE_ID_KANSAS_PROD)
            }
        }else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTHMIAMI, ignoreCase = true))
        {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(Constants.RELEASE_TYPE_UAT,
                    ignoreCase = true)) {
                mContext.getString(R.string.SITE_ID_SOUTH_MIAMI_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_SOUTH_MIAMI_PROD)
            }
        }else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EPHRATA
                , ignoreCase = true))
        {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(Constants.RELEASE_TYPE_UAT,
                    ignoreCase = true)) {
                mContext.getString(R.string.SITE_ID_EPHRATA_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_EPHRATA_PROD)
            }
        }else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RUTGERS
                , ignoreCase = true))
        {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(Constants.RELEASE_TYPE_UAT,
                    ignoreCase = true)) {
                mContext.getString(R.string.SITE_ID_RUTGERS_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_RUTGERS_PROD)
            }
        }else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WESTCHESTER
                , ignoreCase = true))
        {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(Constants.RELEASE_TYPE_UAT,
                    ignoreCase = true)) {
                mContext.getString(R.string.SITE_ID_WESTCHESTER_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_WESTCHESTER_PROD)
            }
        }else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DALLAS
                , ignoreCase = true))
        {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(Constants.RELEASE_TYPE_UAT,
                    ignoreCase = true)) {
                mContext.getString(R.string.SITE_ID_DALLAS_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_DALLAS_PROD)
            }
        }else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CLIFTON
                , ignoreCase = true))
        {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(Constants.RELEASE_TYPE_UAT,
                    ignoreCase = true)) {
                mContext.getString(R.string.SITE_ID_CLIFTON_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_CLIFTON_PROD)
            }
        }else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ORLEANS
                , ignoreCase = true))
        {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(Constants.RELEASE_TYPE_UAT,
                    ignoreCase = true)) {
                mContext.getString(R.string.SITE_ID_ORLEANS_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_ORLEANS_PROD)
            }
        }else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VOLUSIA
                , ignoreCase = true))
        {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(Constants.RELEASE_TYPE_UAT,
                    ignoreCase = true)) {
                mContext.getString(R.string.SITE_ID_VOLUSIA_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_VOLUSIA_PROD)
            }
        }else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_Easton
                , ignoreCase = true))
        {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(Constants.RELEASE_TYPE_UAT,
                    ignoreCase = true)) {
                mContext.getString(R.string.SITE_ID_EASTON_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_EASTON_PROD)
            }
        }else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CORPUSCHRISTI
                , ignoreCase = true))
        {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(Constants.RELEASE_TYPE_UAT,
                    ignoreCase = true)) {
                mContext.getString(R.string.SITE_ID_CORPUS_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_CORPUS_PROD)
            }
        }else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRRS
                , ignoreCase = true))
        {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(Constants.RELEASE_TYPE_UAT,
                    ignoreCase = true)) {
                mContext.getString(R.string.SITE_ID_PRRS_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_PRRS_PROD)
            }
        }else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_OXFORD
                , ignoreCase = true))
        {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(Constants.RELEASE_TYPE_UAT,
                    ignoreCase = true)) {
                mContext.getString(R.string.SITE_ID_OXFORD_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_OXFORD_PROD)
            }
        }else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SURF_CITY
                , ignoreCase = true))
        {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(Constants.RELEASE_TYPE_UAT,
                    ignoreCase = true)) {
                mContext.getString(R.string.SITE_ID_SURF_CITY_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_SURF_CITY_PROD)
            }
        }else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MILLBRAE
                , ignoreCase = true))
        {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(Constants.RELEASE_TYPE_UAT,
                    ignoreCase = true)) {
                mContext.getString(R.string.SITE_ID_ACE_MILLBRAE_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_ACE_MILLBRAE_PROD)
            }
        }else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PARK_RIDGE
                , ignoreCase = true))
        {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(Constants.RELEASE_TYPE_UAT,
                    ignoreCase = true)) {
                mContext.getString(R.string.SITE_ID_PARK_RIDGE_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_PARK_RIDGE_PROD)
            }
        }else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ALAMEDA_COUNTY_SHERIFF
                , ignoreCase = true))
        {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(Constants.RELEASE_TYPE_UAT,
                    ignoreCase = true)) {
                mContext.getString(R.string.SITE_ID_ALAMEDA_COUNTY_SHERIFF_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_ALAMEDA_COUNTY_SHERIFF_PROD)
            }
        }else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ALAMEDA_COUNTY_TRANSIT
                , ignoreCase = true))
        {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(Constants.RELEASE_TYPE_UAT,
                    ignoreCase = true)) {
                mContext.getString(R.string.SITE_ID_ALAMEDA_COUNTY_TRANSIT_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_ALAMEDA_COUNTY_TRANSIT_PROD)
            }
        }else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_JACKSONVILLA
                , ignoreCase = true))
        {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(Constants.RELEASE_TYPE_UAT,
                    ignoreCase = true)) {
                mContext.getString(R.string.SITE_ID_JACKSONVILLE_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_JACKSONVILLE_PROD)
            }
        }else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_NORWALK
                , ignoreCase = true))
        {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(Constants.RELEASE_TYPE_UAT,
                    ignoreCase = true)) {
                mContext.getString(R.string.SITE_ID_NORWALK_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_NORWALK_PROD)
            }
        }else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_COVINA
                , ignoreCase = true))
        {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(Constants.RELEASE_TYPE_UAT,
                    ignoreCase = true)) {
                mContext.getString(R.string.SITE_ID_COVINA_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_COVINA_PROD)
            }
        }else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_KENOSHA
                , ignoreCase = true))
        {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(Constants.RELEASE_TYPE_UAT,
                    ignoreCase = true)) {
                mContext.getString(R.string.SITE_ID_KENOSHA_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_KENOSHA_PROD)
            }
        }else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WATSONVILLE
                , ignoreCase = true))
        {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(Constants.RELEASE_TYPE_UAT,
                    ignoreCase = true)) {
                mContext.getString(R.string.SITE_ID_WATSONVILLE_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_WATSONVILLE_PROD)
            }
        }else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PORTHOODRIVER
                , ignoreCase = true))
        {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(Constants.RELEASE_TYPE_UAT,
                    ignoreCase = true)) {
                mContext.getString(R.string.SITE_ID_PORT_HOOD_RIVER_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_PORT_HOOD_RIVER_PROD)
            }
        }else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VISTA_CA
                , ignoreCase = true))
        {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(Constants.RELEASE_TYPE_UAT,
                    ignoreCase = true)) {
                mContext.getString(R.string.SITE_ID_VISTA_CA_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_VISTA_CA_PROD)
            }
        }else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_INDIANA_BOROUGH
                , ignoreCase = true))
        {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(Constants.RELEASE_TYPE_UAT,
                    ignoreCase = true)) {
                mContext.getString(R.string.SITE_ID_INDIANA_BOROUGH_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_INDIANA_BOROUGH_PROD)
            }
        }else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MOUNT_RAINIER
                , ignoreCase = true))
        {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(Constants.RELEASE_TYPE_UAT,
                    ignoreCase = true)) {
                mContext.getString(R.string.SITE_ID_MOUNT_RAINIER_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_MOUNT_RAINIER_PROD)
            }
        }else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRIME_PARKING
                , ignoreCase = true))
        {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(Constants.RELEASE_TYPE_UAT,
                    ignoreCase = true)) {
                mContext.getString(R.string.SITE_ID_PRIME_PARKING_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_PRIME_PARKING_PROD)
            }
        }else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOSTON
                , ignoreCase = true))
        {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(Constants.RELEASE_TYPE_UAT,
                    ignoreCase = true)) {
                mContext.getString(R.string.SITE_ID_BOSTON_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_BOSTON_PROD)
            }
        }
        else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ATLANTIC_BEACH_NC, ignoreCase = true))
        {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(Constants.RELEASE_TYPE_UAT,
                    ignoreCase = true)) {
                mContext.getString(R.string.SITE_ID_ATLANTIC_BEACH_NC_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_ATLANTIC_BEACH_NC_PROD)
            }
        }

        else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ST_JOHNSBURY_VT, ignoreCase = true))
        {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(Constants.RELEASE_TYPE_UAT,
                    ignoreCase = true)) {
                mContext.getString(R.string.SITE_ID_ST_JOHNSBURY_VT_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_ST_JOHNSBURY_VT_PROD)
            }
        }
        else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WOODSTOCK_GA, ignoreCase = true))
        {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(Constants.RELEASE_TYPE_UAT,
                    ignoreCase = true)) {
                mContext.getString(R.string.SITE_ID_WOODSTOCK_GA_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_WOODSTOCK_GA_PROD)
            }
        }

        else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_INDIAN_HARBOUR_BEACH, ignoreCase = true))
        {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(Constants.RELEASE_TYPE_UAT,
                    ignoreCase = true)) {
                mContext.getString(R.string.SITE_ID_INDIAN_HARBOUR_BEACH_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_INDIAN_HARBOUR_BEACH_PROD)
            }
        }
        else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ATLANTIC_BEACH_SC, ignoreCase = true))
        {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(Constants.RELEASE_TYPE_UAT,
                    ignoreCase = true)) {
                mContext.getString(R.string.SITE_ID_ATLANTIC_BEACH_SC_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_ATLANTIC_BEACH_SC_PROD)
            }
        }
        else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_OF_WATERLOO, ignoreCase = true))
        {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(Constants.RELEASE_TYPE_UAT,
                    ignoreCase = true)) {
                mContext.getString(R.string.SITE_ID_CITY_OF_WATERLOO_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_CITY_OF_WATERLOO_PROD)
            }
        }
        else if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PARKX, ignoreCase = true))
        {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(Constants.RELEASE_TYPE_UAT,
                    ignoreCase = true)) {
                mContext.getString(R.string.SITE_ID_PARK_X_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_PARK_X_PROD)
            }
        } else if (BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_SANMATEO_REDWOOD,
                ignoreCase = true
            )
        ) {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(
                    Constants.RELEASE_TYPE_UAT,
                    ignoreCase = true
                )
            ) {
                mContext.getString(R.string.SITE_ID_SANMATEO_REDWOOD_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_SANMATEO_REDWOOD_PROD)
            }
        } else if (BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_SEASTREAK,
                ignoreCase = true
            )
        ) {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(
                    Constants.RELEASE_TYPE_UAT,
                    ignoreCase = true
                )
            ) {
                mContext.getString(R.string.SITE_ID_SEASTREAK_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_SEASTREAK_PROD)
            }
        }
        else if (BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_UPTOWN_ATLANTA,
                ignoreCase = true
            )
        ) {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(
                    Constants.RELEASE_TYPE_UAT,
                    ignoreCase = true
                )
            ) {
                mContext.getString(R.string.SITE_ID_UPTOWN_ATLANTA_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_UPTOWN_ATLANTA_PROD)
            }
        }
        else if (BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_SOUTH_LAKE,
                ignoreCase = true
            )
        ) {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(
                    Constants.RELEASE_TYPE_UAT,
                    ignoreCase = true
                )
            ) {
                mContext.getString(R.string.SITE_ID_SOUTH_LAKE_TAHOE_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_SOUTH_LAKE_TAHOE_PROD)
            }
        }
        else if (BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_DANVILLE_VA,
                ignoreCase = true
            )
        ) {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(
                    Constants.RELEASE_TYPE_UAT,
                    ignoreCase = true
                )
            ) {
                mContext.getString(R.string.SITE_ID_DANVILLE_VA_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_DANVILLE_VA_PROD)
            }
        }
        else if (BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_CAMDEN,
                ignoreCase = true
            )
        ) {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(
                    Constants.RELEASE_TYPE_UAT,
                    ignoreCase = true
                )
            ) {
                mContext.getString(R.string.SITE_ID_CAMDEN_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_CAMDEN_PROD)
            }
        }
        else if (BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_WINPARK_TX,
                ignoreCase = true
            )
        ) {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(
                    Constants.RELEASE_TYPE_UAT,
                    ignoreCase = true
                )
            ) {
                mContext.getString(R.string.SITE_ID_WINPARK_TX_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_WINPARK_TX_PROD)
            }
        }
        else if (BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_LAZ_KCMO_PRIVATE,
                ignoreCase = true
            )
        ) {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(
                    Constants.RELEASE_TYPE_UAT,
                    ignoreCase = true
                )
            ) {
                mContext.getString(R.string.SITE_ID_LAZ_KCMO_PRIVATE_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_LAZ_KCMO_PRIVATE_PROD)
            }
        }
        else if (BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_LAZ_CONJUCTIVE_POINT,
                ignoreCase = true
            )
        ) {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(
                    Constants.RELEASE_TYPE_UAT,
                    ignoreCase = true
                )
            ) {
                mContext.getString(R.string.SITE_ID_LAZ_CONJUCTIVE_POINT_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_LAZ_CONJUCTIVE_POINT_PROD)
            }
        }
        else if (BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_JACKSONVILLE_FL_REIMAGINED_PARKING,
                ignoreCase = true
            )
        ) {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(
                    Constants.RELEASE_TYPE_UAT,
                    ignoreCase = true
                )
            ) {
                mContext.getString(R.string.SITE_ID_RPS_JACKSONVILLE_FL_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_RPS_JACKSONVILLE_FL_PROD)
            }
        }
        else if (BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_CHATTANOOGA_TN_REIMAGINED_PARKING,
                ignoreCase = true
            )
        ) {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(
                    Constants.RELEASE_TYPE_UAT,
                    ignoreCase = true
                )
            ) {
                mContext.getString(R.string.SITE_ID_RPS_CHATTANOOGA_TN_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_RPS_CHATTANOOGA_TN_PROD)
            }
        }
        else if (BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_CITY_OF_WILTON_MANORS_REIMAGINED_PARKING,
                ignoreCase = true
            )
        ) {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(
                    Constants.RELEASE_TYPE_UAT,
                    ignoreCase = true
                )
            ) {
                mContext.getString(R.string.SITE_ID_CITY_OF_WILTON_MANORS_REIMAGINED_PARKING_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_CITY_OF_WILTON_MANORS_REIMAGINED_PARKING_PROD)
            }
        }

        else if (BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_REEF_CASPER_WY_REIMAGINED_PARKING,
                ignoreCase = true
            )
        ) {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(
                    Constants.RELEASE_TYPE_UAT,
                    ignoreCase = true
                )
            ) {
                mContext.getString(R.string.SITE_ID_REEF_CASPER_WY_REIMAGINED_PARKING_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_REEF_CASPER_WY_REIMAGINED_PARKING_PROD)
            }
        }
        else if (BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_MOBILE_AL_REIMAGINED_PARKING,
                ignoreCase = true
            )
        ) {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(
                    Constants.RELEASE_TYPE_UAT,
                    ignoreCase = true
                )
            ) {
                mContext.getString(R.string.SITE_ID_MOBILE_AL_REIMAGINED_PARKING_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_MOBILE_AL_REIMAGINED_PARKING_PROD)
            }
        }
        else if (BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_FAYETTEVILLE_NC_REIMAGINED_PARKING,
                ignoreCase = true
            )
        ) {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(
                    Constants.RELEASE_TYPE_UAT,
                    ignoreCase = true
                )
            ) {
                mContext.getString(R.string.SITE_ID_FAYETTEVILLE_NC_REIMAGINED_PARKING_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_FAYETTEVILLE_NC_REIMAGINED_PARKING_PROD)
            }
        }
        else if (BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_MERRICK_PARK_FL_REIMAGINED_PARKING,
                ignoreCase = true
            )
        ) {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(
                    Constants.RELEASE_TYPE_UAT,
                    ignoreCase = true
                )
            ) {
                mContext.getString(R.string.SITE_ID_MERRICK_PARK_FL_REIMAGINED_PARKING_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_MERRICK_PARK_FL_REIMAGINED_PARKING_PROD)
            }
        }

        else if (BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_HAMTRAMCK_MI,
                ignoreCase = true
            )
        ) {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(
                    Constants.RELEASE_TYPE_UAT,
                    ignoreCase = true
                )
            ) {
                mContext.getString(R.string.SITE_ID_HAMTRAMCK_MI_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_HAMTRAMCK_MI_PROD)
            }
        }

        else if (BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_PORTLAND_RO_REIMAGINED_PARKING,
                ignoreCase = true
            )
        ) {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(
                    Constants.RELEASE_TYPE_UAT,
                    ignoreCase = true
                )
            ) {
                mContext.getString(R.string.SITE_ID_PORTLAND_RO_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_PORTLAND_RO_PROD)
            }
        }
        else if (BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_HILLSBORO_OR_REIMAGINED_PARKING,
                ignoreCase = true
            )
        ) {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(
                    Constants.RELEASE_TYPE_UAT,
                    ignoreCase = true
                )
            ) {
                mContext.getString(R.string.SITE_ID_HILLSBORO_OR_REIMAGINED_PARKING_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_HILLSBORO_OR_REIMAGINED_PARKING_PROD)
            }
        }
        else if (BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_PHSA_REIMAGINED_PARKING,
                ignoreCase = true
            )
        ) {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(
                    Constants.RELEASE_TYPE_UAT,
                    ignoreCase = true
                )
            ) {
                mContext.getString(R.string.SITE_ID_PHSA_REIMAGINED_PARKING_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_PHSA_REIMAGINED_PARKING_PROD)
            }
        }
        else if (BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_MTA_LONG_ISLAND_RAIL_ROAD,
                ignoreCase = true
            )
        ) {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(
                    Constants.RELEASE_TYPE_UAT,
                    ignoreCase = true
                )
            ) {
                mContext.getString(R.string.SITE_ID_MTA_LONG_ISLAND_RAIL_ROAD_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_MTA_LONG_ISLAND_RAIL_ROAD_PROD)
            }
        }
        else if (BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_GREENBURGH_NY
                ,
                ignoreCase = true
            )
        ) {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(
                    Constants.RELEASE_TYPE_UAT,
                    ignoreCase = true
                )
            ) {
                mContext.getString(R.string.SITE_ID_GREENBURGH_NY_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_GREENBURGH_NY_PROD)
            }
        }
        else if (BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_BOTTLEWORKS_IN
                ,
                ignoreCase = true
            )
        ) {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(
                    Constants.RELEASE_TYPE_UAT,
                    ignoreCase = true
                )
            ) {
                mContext.getString(R.string.SITE_ID_BOTTLEWORKS_IN_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_BOTTLEWORKS_IN_PROD)
            }
        }
        else if (BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_ACE_HGI_MANHATTAN
                ,
                ignoreCase = true
            )
        ) {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(
                    Constants.RELEASE_TYPE_UAT,
                    ignoreCase = true
                )
            ) {
                mContext.getString(R.string.SITE_ID_ACE_HGI_MANHATTAN_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_ACE_HGI_MANHATTAN_PROD)
            }
        }
        else if (BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_A_BOBS_TOWING
                ,
                ignoreCase = true
            )
        ) {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(
                    Constants.RELEASE_TYPE_UAT,
                    ignoreCase = true
                )
            ) {
                mContext.getString(R.string.SITE_ID_A_BOBS_TOWING_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_A_BOBS_TOWING_PROD)
            }
        }
        else if (BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_EL_PASO_TX_MACKAY
                ,
                ignoreCase = true
            )
        ) {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(
                    Constants.RELEASE_TYPE_UAT,
                    ignoreCase = true
                )
            ) {
                mContext.getString(R.string.SITE_ID_EL_PASO_TX_MACKAY_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_EL_PASO_TX_MACKAY_PROD)
            }
        }
        else if (BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_IMPARK_PHSA,
                ignoreCase = true
            )
        ) {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(
                    Constants.RELEASE_TYPE_UAT,
                    ignoreCase = true
                )
            ) {
                mContext.getString(R.string.SITE_ID_IMPARK_PHSA_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_IMPARK_PHSA_PROD)
            }
        }
        else if (BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_EL_CAMINO_COLLEGE,
                ignoreCase = true
            )
        ) {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(
                    Constants.RELEASE_TYPE_UAT,
                    ignoreCase = true
                )
            ) {
                mContext.getString(R.string.SITE_ID_EL_CAMINO_COLLEGE_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_EL_CAMINO_COLLEGE_PROD)
            }
        }
        else if (BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_ACE_SAN_DIEGO_ZOO,
                ignoreCase = true
            )
        ) {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(
                    Constants.RELEASE_TYPE_UAT,
                    ignoreCase = true
                )
            ) {
                mContext.getString(R.string.SITE_ID_ACE_SAN_DIEGO_ZOO_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_ACE_SAN_DIEGO_ZOO_PROD)
            }
        }
        else if (BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_JEFFERSON_CITY_MO,
                ignoreCase = true
            )
        ) {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(
                    Constants.RELEASE_TYPE_UAT,
                    ignoreCase = true
                )
            ) {
                mContext.getString(R.string.SITE_ID_JEFFERSON_CITY_MO_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_JEFFERSON_CITY_MO_PROD)
            }
        }
        else if (BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_VEST_SECURITY_SYSTEM,
                ignoreCase = true
            )
        ) {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(
                    Constants.RELEASE_TYPE_UAT,
                    ignoreCase = true
                )
            ) {
                mContext.getString(R.string.SITE_ID_VEST_SECURITY_SYSTEM_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_VEST_SECURITY_SYSTEM_PROD)
            }
        }
        else if (BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_DEER_FIELD_BEACH_FL,
                ignoreCase = true
            )
        ) {
            return if (BuildConfig.BUILD_RELEASE_TYPE.equals(
                    Constants.RELEASE_TYPE_UAT,
                    ignoreCase = true
                )
            ) {
                mContext.getString(R.string.SITE_ID_DEER_FIELD_BEACH_FL_UAT)
            } else {
                mContext.getString(R.string.SITE_ID_DEER_FIELD_BEACH_FL_PROD)
            }
        }
        return ""
    }

    @JvmStatic
    fun after21DateFromCurrentDate(day: Int): String {
        var date = ""
        try {
            if(day>0) {
                val calendar = Calendar.getInstance()
                calendar.add(Calendar.DAY_OF_MONTH, day)
                val sdf = SimpleDateFormat("MM/dd/yy")
                date = sdf.format(calendar.time)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return date
    }
//    @JvmStatic
//    fun hearingDateAfter25DaysFirstWednesday(day: Int): String {
//        var date = ""
//        try {
//            if(day>0) {
//                var dayCountFive:Int = 0
//                repeat(5) {
//                    val calendar = Calendar.getInstance()
//                    calendar.add(Calendar.DAY_OF_MONTH, (day+dayCountFive))
//
//                    if (SimpleDateFormat("EEEE").format(calendar.time).equals("Tuesday") ||
//                        SimpleDateFormat("EEEE").format(calendar.time).equals("Thursday")
//                    ) {
//                        val sdf = SimpleDateFormat("MM/dd/yyyy")
//                        date = sdf.format(calendar.time)
//                        return@repeat
//                    } else {
//                        dayCountFive++
//                    }
//                }
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//        return (date.plus(" 9AM-2:30PM"))
//    }

    /**
     * This method will return hearing date after given day threashold
     * First Tuesday Or ThursDay will be returned after given threashold
     */
    @JvmStatic
    fun hearingDateAfterThresholdDifferenceDays(
        context: Context,
        dayDifferenceThreshold: Int
    ): String? {

        //This will be final hearing date which is going to be return
        var finalHearingDate = ""

        //List of all available hearing dates fall on/under holiday
        var hearingDatesOnHolidayList=  getHearingDates(context)

//        if (hearingDatesOnHolidayList.isNullOrEmpty()) {
//            hearingDatesOnHolidayList = getHearingDatesOnHolidayFromAssetFile(context)
//        }


        try {
            if (dayDifferenceThreshold > 0) {
                val simpleDateFormatForDate = SDF_MM_DD_YYYY
                val simpleDateFormatForDayOfWeek = SDF_EEEE

                val calendar = Calendar.getInstance()

                //Setting calender date to dayDifferenceThreshold after
                calendar.add(
                    Calendar.DAY_OF_MONTH,
                    dayDifferenceThreshold
                )
                //This while loop runs until we get first valid hearing date fall on either Tuesday or Thursday excluding holidays
                while (finalHearingDate.isEmpty()) {
                    //Increasing calender date to one day everytime when loop getting in
                    calendar.set(
                        Calendar.DAY_OF_MONTH,
                        calendar.get(Calendar.DAY_OF_MONTH) + 1
                    )

                    //Getting updated calender date
                    val calendarDate = simpleDateFormatForDate.format(calendar.time)
                    //Checking if updated calender is having holiday or not based on our holiday list
//                    val isHearingDateHoliday =
//                        hearingDatesOnHolidayList?.any { it.date == calenderDate }

                    val isHearingDateHoliday = hearingDatesOnHolidayList?.any { item ->
                        val apiLocalDate = LocalDate.parse(item.date?.substring(0, 10))
                        val outputFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")  // same as calendarDate
                        val apiDateFormatted = apiLocalDate.format(outputFormatter)

                        apiDateFormatted == calendarDate   // String == String ✔
                    } ?: false

                    //We are checking if the updated calender date fall on Tuesday or Thursday and should not have a holiday
                    if ((simpleDateFormatForDayOfWeek.format(calendar.time)
                            .equals(DAY_TUESDAY, true) ||
                                simpleDateFormatForDayOfWeek.format(calendar.time)
                                    .equals(DAY_THURSDAY, true)) &&
                        !isHearingDateHoliday.nullSafety()
                    ) {
                        //Assigning final hearing date to the variable & once we assign value to this, while loop will not execute
                        finalHearingDate = simpleDateFormatForDate.format(calendar.time)
                    }
                }
            } else {
                return null
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return finalHearingDate.plus(HEARING_FIXED_TIME)
    }
/**
     * This method will return hearing date after given day threashold
     */
@JvmStatic
fun hearingDateAfterThresholdDay(
    context: Context,
    dayDifferenceThreshold: Int
): String? {

    // This will be final hearing date which is going to be returned
    var finalHearingDate = ""

    try {
        if (dayDifferenceThreshold > 0) {
            val simpleDateFormatForDate = SDF_MM_DD_YYYY

            val calendar = Calendar.getInstance()

            // Setting calendar date to dayDifferenceThreshold after
            calendar.add(Calendar.DAY_OF_MONTH, dayDifferenceThreshold)

            // Directly take the calculated date without checking for holiday or weekday
            finalHearingDate = simpleDateFormatForDate.format(calendar.time)
        } else {
            return null
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return finalHearingDate.plus(HEARING_FIXED_TIME)
}

fun getHearingDates(context: Context): List<DatasetResponse>? {
        return try {
            val mDb = BaseApplication.instance?.getAppDatabase()

            val dbList: List<DatasetResponse>? =
                Singleton.getDataSetList(DATASET_HOLIDAY_CALENDAR_LIST, mDb)

            dbList   // ← MUST return something from try block
        } catch (e: Exception) {
            e.printStackTrace()
            null     // ← MUST return something from catch block
        }
    }

    //Function used to get Hearing holiday list from json file located in asset folder
    private fun getHearingDatesOnHolidayFromAssetFile(context: Context): List<HearingDatesOnHoliday>? {
        try {
            val jsonString =
                FileUtil.readJsonFromAssets(context, ASSET_HEARING_DATES_ON_HOLIDAY_JSON)
            return parseJsonToListOfHearingDatesOnHoliday(jsonString)
        } catch (ex: Exception) {
            ex.printStackTrace()
            return null
        }
    }

    //Function used to parse json string to List of HearingDatesOnHoliday
    private fun parseJsonToListOfHearingDatesOnHoliday(jsonString: String): List<HearingDatesOnHoliday> {
        return ObjectMapperProvider.fromJson(
            jsonString,
            object : TypeReference<List<HearingDatesOnHoliday>>() {}
        )
    }


    @JvmStatic
    fun deleteLprContinousModeFolder(mContext: Context, sharedPref: SharedPref) {
        try {
            val date = getCurrentDate("dd/MM/yyyy")
            val sdf = SimpleDateFormat("dd/MM/yyyy")
            val previousDate = sharedPref.read(SharedPrefKey.CONTINOUS_RESULT_DELETE_DATE, "")
            if(!previousDate.isNullOrEmpty()) {
                val strDate = sdf.parse(previousDate)
                if (Date().after(strDate)) {
                    val file = File(
                            Environment.getExternalStorageDirectory().absolutePath,
                            Constants.FILE_NAME + Constants.COTINOUS
                    )
                    deleteContinousModeFolderFromSD(file, mContext, date, sharedPref)
                }
            }
        } catch (e: ParseException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun deleteContinousModeFolderFromSD(
        fileOrDirectory: File,
        mContext: Context, date: String,
        sharedPref: SharedPref
    ) {
//        File fileOrDirectory = new File("root path");
        try {
            if (fileOrDirectory.isDirectory) {
                for (child in fileOrDirectory.listFiles()) {
                    deleteContinousModeFolderFromSD(child, mContext, date, sharedPref)
                    sharedPref.write(SharedPrefKey.CONTINOUS_RESULT_DELETE_DATE, date)
                }
                fileOrDirectory.delete()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun isServiceRunning(serviceName: String, mContext: Context): Boolean {
        var serviceRunning = false
        val am = mContext.getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val l = am.getRunningServices(50)
        val i: Iterator<ActivityManager.RunningServiceInfo> = l.iterator()
        while (i.hasNext()) {
            val runningServiceInfo = i.next()
            if (runningServiceInfo.service.className == serviceName) {
                serviceRunning = true
                if (runningServiceInfo.foreground) {
                    //service run in foreground
                }
            }
        }
        return serviceRunning
    }

    fun roundOfBlock(block : String): String{
        var returnBlock = block
        try {
            val mLenght = block!!.length
            if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DUNCAN,ignoreCase = true)||
                BuildConfig.FLAVOR.equals(DuncanBrandingApp13())) {

                if (mLenght == 3) {
                    returnBlock = block.substring(0, 1) + "00 BLK"
                } else if (mLenght == 4) {
                    returnBlock = block.substring(0, 2) + "00 BLK"
                } else if (mLenght == 5) {
                    returnBlock = block.substring(0, 3) + "00 BLK"
                }
            }else {
                if (mLenght == 3) {
                    returnBlock = block.substring(0, 1) + "00"
                } else if (mLenght == 4) {
                    returnBlock = block.substring(0, 2) + "00"
                } else if (mLenght == 5) {
                    returnBlock = block.substring(0, 3) + "00"
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return returnBlock
    }

    fun getTimeBasedRandomId() : String {
        return SimpleDateFormat("HHmmss", Locale.US).format(Date())
    }

    @JvmStatic
    fun dateFormateForFacsimil(sDate: String?): String {
        var sDateFormate = ""
        try {
            val pattern = "dd MM yyyy hh:mm a"
            val inputPattern = "dd MMM,yyyy hh:mm a"
            val fmt = SimpleDateFormat(inputPattern)
            val date = fmt.parse(sDate)
            val fmtOut = SimpleDateFormat(pattern)
            sDateFormate = fmtOut.format(date)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return sDateFormate
    }

    @JvmStatic
    fun timeFormatForReport(sDate: String?): String {
        var sDateFormate = ""
        try {
            val pattern = "hh:mm a"
            val inputPattern = "hh : mm"
            val fmt = SimpleDateFormat(inputPattern)
            val date = fmt.parse(sDate)
            val fmtOut = SimpleDateFormat(pattern)
            sDateFormate = fmtOut.format(date)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return sDateFormate
    }

    @JvmStatic
    fun getPrinterSetting(): String {
        val mDb: AppDatabase? = BaseApplication.instance?.getAppDatabase()
        val settingsList = Singleton.getDataSetList(DATASET_SETTINGS_LIST, mDb)
        return settingsList?.firstOrNull {
            it.type.equals(
                Constants.SETTING_PRINTER_TYPE,
                true
            )
        }?.mValue.nullSafety()
    }

    @JvmStatic
    fun getDayOfWeek():String
    {
        var weekDay = ""

        val c = Calendar.getInstance()
        val dayOfWeek = c[Calendar.DAY_OF_WEEK]

        if (Calendar.MONDAY === dayOfWeek) {
            weekDay = "Monday"
        } else if (Calendar.TUESDAY === dayOfWeek) {
            weekDay = "Tuesday"
        } else if (Calendar.WEDNESDAY === dayOfWeek) {
            weekDay = "Wednesday"
        } else if (Calendar.THURSDAY === dayOfWeek) {
            weekDay = "Thursday"
        } else if (Calendar.FRIDAY === dayOfWeek) {
            weekDay = "Friday"
        } else if (Calendar.SATURDAY === dayOfWeek) {
            weekDay = "Saturday"
        } else if (Calendar.SUNDAY === dayOfWeek) {
            weekDay = "Sunday"
        }

        println(weekDay)
        return weekDay
    }
    @JvmStatic
    fun timestampItAndSave(toEdit: Bitmap): Bitmap? {


        var bitmap: Bitmap? = toEdit
        try {
            var bitmapConfig: Bitmap.Config? = bitmap!!.config
            if (bitmapConfig == null) {
                bitmapConfig = Bitmap.Config.ARGB_8888
            }
            // resource bitmaps are imutable,
            // so we need to convert it to mutable one
            bitmap = bitmap.copy(bitmapConfig, true)
            val canvas: Canvas = Canvas(bitmap)

            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val dateTime = sdf.format(Calendar.getInstance().time) // reading local time in the system

            // new antialised Paint
            val paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
            // text color - #3D3D3D
            paint.color = Color.rgb(0, 0, 139)
            // text size in pixels
            paint.textSize = (30 * 2).toFloat()
            // text shadow
            paint.setShadowLayer(1f, 0f, 1f, Color.BLUE)

//
            // draw text to the Canvas center
            val bounds: Rect = Rect()
            paint.getTextBounds(dateTime, 0, dateTime!!.length, bounds)
//            val x: Int = (bitmap.width)
//            val y: Int = bitmap.height - 70
                canvas.drawText((dateTime), 60f,
                    120f, paint)
            return bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            return bitmap
        }



//
//
//
//        val dest = Bitmap.createBitmap(toEdit.width, toEdit.height, Bitmap.Config.ARGB_8888)
//        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
//        val dateTime = sdf.format(Calendar.getInstance().time) // reading local time in the system
//        val cs = Canvas(dest)
//        val tPaint = Paint()
//        tPaint.setTextSize(35F)
//        tPaint.setColor(Color.BLUE)
//        tPaint.setStyle(Paint.Style.FILL)
//        val height: Float = tPaint.measureText("yY")
//        cs.drawText(dateTime, 20f, height + 15f, tPaint)
//        try {
//            dest.compress(
//                Bitmap.CompressFormat.JPEG,
//                100,
//                FileOutputStream(
//                    File(
//                        Environment.getExternalStorageDirectory().toString() + "/timestamped"
//                    )
//                )
//            )
//        } catch (e: FileNotFoundException) {
//            e.printStackTrace()
//            return toEdit
//        }
//        return dest
    }

    @JvmStatic
    fun removeSpecialCharacterFromString(value : String): String{
        try {
            return value.replace("""[\p{P}\p{S}&&[^.]]+""".toRegex(), "")
        } catch (e: Exception) {
            return value
        }
    }
    fun setYaxis() {
        Yaxis = 190.0
        Ybox = 400.0
        mSection = 2
//        AppUtils.sectionFirst.clear()
    }


    fun drawStringonBitmap(
        src: Bitmap,
        string: String?,
        location: Point,
        color: Int,
        alpha: Int,
        size: Int,
        underline: Boolean,
        width: Int,
        height: Int
    ): Bitmap? {
        val result = Bitmap.createBitmap(width, height, src.config)
        val canvas = Canvas(result)
        canvas.drawBitmap(src, 0.0f, 0.0f, null)
        val paint = Paint()
        paint.color = color
        paint.alpha = alpha
        paint.textSize = size.toFloat()
        paint.isAntiAlias = true
        paint.isUnderlineText = underline
        canvas.drawText(string!!, location.x.toFloat(), location.y.toFloat(), paint)
        return result
    }


    fun openDataPicker(datePickerField: AppCompatAutoCompleteTextView?,calendarType:Int,supportFragmentManager: FragmentManager,
    mContext: Activity,myCalendar: Calendar) {
        if (calendarType == 0)
            AllReportActivity.MonthYearPickerDialog().apply {
                setListener { view, year, month, dayOfMonth ->
                    myCalendar[Calendar.YEAR] = year
                    myCalendar[Calendar.MONTH] = month
                    myCalendar[Calendar.DAY_OF_MONTH] = dayOfMonth
                    var myFormat = "MM/yy" //In which you need put here
                    var myFormatForAPI = "DD/MM/YYYY" //In which you need put here

                    val sdf = SimpleDateFormat(myFormat, Locale.US)
                    val sdfAPI = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        SimpleDateFormat(myFormatForAPI, Locale.US)
                    } else {
                        TODO("VERSION.SDK_INT < N")
                    }
                    datePickerField!!.setText(sdf.format(myCalendar.time))
                    datePickerField!!.setTag(sdfAPI.format(myCalendar.time))
                    datePickerField!!.setError(null)
//                    Toast.makeText(requireContext(), "Set date: $year/$month/$dayOfMonth", Toast.LENGTH_LONG).show()
                }
                show(supportFragmentManager, "MonthYearPickerDialog")
            }
        else {
            val date =
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth -> // TODO Auto-generated method stub
                    myCalendar[Calendar.YEAR] = year
                    myCalendar[Calendar.MONTH] = monthOfYear
                    myCalendar[Calendar.DAY_OF_MONTH] = dayOfMonth
                    var myFormat = "MM/yyyy" //In which you need put here
                    if (BuildConfig.FLAVOR.equals(
                            Constants.FLAVOR_TYPE_GLENDALE,
                            ignoreCase = true
                        )  || BuildConfig.FLAVOR.equals(
                            Constants.FLAVOR_TYPE_GLENDALE_POLICE,
                            ignoreCase = true
                        ) || BuildConfig.FLAVOR.equals(
                            Constants.FLAVOR_TYPE_BURBANK,
                            ignoreCase = true
                        ) || BuildConfig.FLAVOR.equals(
                            Constants.FLAVOR_TYPE_WESTCHESTER,
                            ignoreCase = true
                        ) || BuildConfig.FLAVOR.equals(
                            Constants.FLAVOR_TYPE_LAZLB,
                            ignoreCase = true
                        ) || BuildConfig.FLAVOR.equals(
                            Constants.FLAVOR_TYPE_VALLEJOL,
                            ignoreCase = true
                        ) || BuildConfig.FLAVOR.equals(
                            Constants.FLAVOR_TYPE_ENCINITAS,
                            ignoreCase = true
                        )
                    ) {
                        myFormat = "MM/yy" //In which you need put here
                    } else if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAMETRO, true)||
                        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CORPUSCHRISTI, true)) {
//                        mainScope.launch {
                        myFormat = "dd/MM/yyyy" //In which you need put here
//                        }
                    }
                    val sdf = SimpleDateFormat(myFormat, Locale.US)
                    datePickerField!!.setText(sdf.format(myCalendar.time))
                }
            DatePickerDialog(
                mContext, date, myCalendar[Calendar.YEAR], myCalendar[Calendar.MONTH],
                myCalendar[Calendar.DAY_OF_MONTH]
            ).show()
        }
    }

    fun printByCMD(printintByCMD : Boolean):Boolean{
        if(
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ_CCP, ignoreCase = true) &&
            AppUtils.printQueryStringBuilder.isNotEmpty() && AppUtils.printQueryStringBuilder.length>50||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PORTLAND_RO_REIMAGINED_PARKING, ignoreCase = true) &&
            AppUtils.printQueryStringBuilder.isNotEmpty() && AppUtils.printQueryStringBuilder.length>50||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CHATTANOOGA_TN_REIMAGINED_PARKING, ignoreCase = true) &&
            AppUtils.printQueryStringBuilder.isNotEmpty() && AppUtils.printQueryStringBuilder.length>50||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_JACKSONVILLE_FL_REIMAGINED_PARKING, ignoreCase = true) &&
            AppUtils.printQueryStringBuilder.isNotEmpty() && AppUtils.printQueryStringBuilder.length>50||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_OF_WILTON_MANORS_REIMAGINED_PARKING, ignoreCase = true) &&
            AppUtils.printQueryStringBuilder.isNotEmpty() && AppUtils.printQueryStringBuilder.length>50||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_REEF_CASPER_WY_REIMAGINED_PARKING, ignoreCase = true) &&
            AppUtils.printQueryStringBuilder.isNotEmpty() && AppUtils.printQueryStringBuilder.length>50||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MOBILE_AL_REIMAGINED_PARKING, ignoreCase = true) &&
            AppUtils.printQueryStringBuilder.isNotEmpty() && AppUtils.printQueryStringBuilder.length>50||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_FAYETTEVILLE_NC_REIMAGINED_PARKING, ignoreCase = true) &&
            AppUtils.printQueryStringBuilder.isNotEmpty() && AppUtils.printQueryStringBuilder.length>50||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_IMPARK_PHSA, ignoreCase = true) &&
            AppUtils.printQueryStringBuilder.isNotEmpty() && AppUtils.printQueryStringBuilder.length>50||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HILLSBORO_OR_REIMAGINED_PARKING, ignoreCase = true) &&
            AppUtils.printQueryStringBuilder.isNotEmpty() && AppUtils.printQueryStringBuilder.length>50||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PHSA_REIMAGINED_PARKING, ignoreCase = true) &&
            AppUtils.printQueryStringBuilder.isNotEmpty() && AppUtils.printQueryStringBuilder.length>50||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MERRICK_PARK_FL_REIMAGINED_PARKING, ignoreCase = true) &&
            AppUtils.printQueryStringBuilder.isNotEmpty() && AppUtils.printQueryStringBuilder.length>50||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ_KCMO_PRIVATE, ignoreCase = true) &&
            AppUtils.printQueryStringBuilder.isNotEmpty() && AppUtils.printQueryStringBuilder.length>50||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_A_BOBS_TOWING, ignoreCase = true) &&
            AppUtils.printQueryStringBuilder.isNotEmpty() && AppUtils.printQueryStringBuilder.length>50||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ASHLAND, ignoreCase = true) &&
            AppUtils.printQueryStringBuilder.isNotEmpty() && AppUtils.printQueryStringBuilder.length>50||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ_CONJUCTIVE_POINT, ignoreCase = true) &&
            AppUtils.printQueryStringBuilder.isNotEmpty() && AppUtils.printQueryStringBuilder.length>50||
            (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANIBEL, ignoreCase = true) &&
                    AppUtils.printQueryStringBuilder.isNotEmpty() && AppUtils.printQueryStringBuilder.length>50&& printintByCMD)||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SMYRNABEACH, ignoreCase = true) &&
            AppUtils.printQueryStringBuilder.isNotEmpty() && AppUtils.printQueryStringBuilder.length>50||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLASGOW, ignoreCase = true) &&
            AppUtils.printQueryStringBuilder.isNotEmpty() && AppUtils.printQueryStringBuilder.length>50||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_FASHION_CITY, ignoreCase = true) &&
            AppUtils.printQueryStringBuilder.isNotEmpty() && AppUtils.printQueryStringBuilder.length>50||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRRS, ignoreCase = true) &&
            AppUtils.printQueryStringBuilder.isNotEmpty() && AppUtils.printQueryStringBuilder.length>50||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_UPTOWN_ATLANTA, ignoreCase = true) &&
            AppUtils.printQueryStringBuilder.isNotEmpty() && AppUtils.printQueryStringBuilder.length>50||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GREENBURGH_NY, ignoreCase = true) &&
            AppUtils.printQueryStringBuilder.isNotEmpty() && AppUtils.printQueryStringBuilder.length>50||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DEER_FIELD_BEACH_FL, ignoreCase = true) &&
            AppUtils.printQueryStringBuilder.isNotEmpty() && AppUtils.printQueryStringBuilder.length>50||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACEAMAZON, ignoreCase = true) &&
            AppUtils.printQueryStringBuilder.isNotEmpty() && AppUtils.printQueryStringBuilder.length>50||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RIVEROAKS, ignoreCase = true) &&
            AppUtils.printQueryStringBuilder.isNotEmpty() && AppUtils.printQueryStringBuilder.length>50||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ISLEOFPALMS, ignoreCase = true) &&
            AppUtils.printQueryStringBuilder.isNotEmpty() && AppUtils.printQueryStringBuilder.length>50||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BEAUFORT, ignoreCase = true) &&
            AppUtils.printQueryStringBuilder.isNotEmpty() && AppUtils.printQueryStringBuilder.length>50||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SURF_CITY, ignoreCase = true) &&
            AppUtils.printQueryStringBuilder.isNotEmpty() && AppUtils.printQueryStringBuilder.length>50||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WOODSTOCK_GA, ignoreCase = true) &&
            AppUtils.printQueryStringBuilder.isNotEmpty() && AppUtils.printQueryStringBuilder.length>50||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_COB, ignoreCase = true) &&
            AppUtils.printQueryStringBuilder.isNotEmpty() && AppUtils.printQueryStringBuilder.length>50||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_OCEANCITY, ignoreCase = true) &&
            AppUtils.printQueryStringBuilder.isNotEmpty() && AppUtils.printQueryStringBuilder.length>50||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EPHRATA, ignoreCase = true) &&
            AppUtils.printQueryStringBuilder.isNotEmpty() && AppUtils.printQueryStringBuilder.length>50||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_Easton, ignoreCase = true) &&
            AppUtils.printQueryStringBuilder.isNotEmpty() && AppUtils.printQueryStringBuilder.length>50||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CLIFTON, ignoreCase = true) &&
            AppUtils.printQueryStringBuilder.isNotEmpty() && AppUtils.printQueryStringBuilder.length>50||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKPARKING, ignoreCase = true) &&
            AppUtils.printQueryStringBuilder.isNotEmpty() && AppUtils.printQueryStringBuilder.length>50||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PARKX, ignoreCase = true) &&
            AppUtils.printQueryStringBuilder.isNotEmpty() && AppUtils.printQueryStringBuilder.length>50||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RUTGERS, ignoreCase = true) &&
            AppUtils.printQueryStringBuilder.isNotEmpty() && AppUtils.printQueryStringBuilder.length>50||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKTEXAS, ignoreCase = true) &&
            AppUtils.printQueryStringBuilder.isNotEmpty() && AppUtils.printQueryStringBuilder.length>50||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOTTLEWORKS_IN, ignoreCase = true) &&
            AppUtils.printQueryStringBuilder.isNotEmpty() && AppUtils.printQueryStringBuilder.length>50||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HARTFORD, ignoreCase = true) &&
            AppUtils.printQueryStringBuilder.isNotEmpty() && AppUtils.printQueryStringBuilder.length>50||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DALLAS, ignoreCase = true) &&
            AppUtils.printQueryStringBuilder.isNotEmpty() && AppUtils.printQueryStringBuilder.length>50||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_KANSAS_CITY, ignoreCase = true) &&
            AppUtils.printQueryStringBuilder.isNotEmpty() && AppUtils.printQueryStringBuilder.length>50||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EL_CAMINO_COLLEGE, ignoreCase = true) &&
            AppUtils.printQueryStringBuilder.isNotEmpty() && AppUtils.printQueryStringBuilder.length>50||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VEST_SECURITY_SYSTEM, ignoreCase = true) &&
            AppUtils.printQueryStringBuilder.isNotEmpty() && AppUtils.printQueryStringBuilder.length>50||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_JEFFERSON_CITY_MO, ignoreCase = true) &&
            AppUtils.printQueryStringBuilder.isNotEmpty() && AppUtils.printQueryStringBuilder.length>50||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MTA_LONG_ISLAND_RAIL_ROAD, ignoreCase = true) &&
            AppUtils.printQueryStringBuilder.isNotEmpty() && AppUtils.printQueryStringBuilder.length>50||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SEASTREAK, ignoreCase = true) &&
            AppUtils.printQueryStringBuilder.isNotEmpty() && AppUtils.printQueryStringBuilder.length>50||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ATLANTIC_BEACH_NC, ignoreCase = true) &&
            AppUtils.printQueryStringBuilder.isNotEmpty() && AppUtils.printQueryStringBuilder.length>50||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ATLANTIC_BEACH_SC, ignoreCase = true) &&
            AppUtils.printQueryStringBuilder.isNotEmpty() && AppUtils.printQueryStringBuilder.length>50||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ST_JOHNSBURY_VT, ignoreCase = true) &&
            AppUtils.printQueryStringBuilder.isNotEmpty() && AppUtils.printQueryStringBuilder.length>50||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_OXFORD, ignoreCase = true) &&
            AppUtils.printQueryStringBuilder.isNotEmpty() && AppUtils.printQueryStringBuilder.length>50||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRIME_PARKING, ignoreCase = true) &&
            AppUtils.printQueryStringBuilder.isNotEmpty() && AppUtils.printQueryStringBuilder.length>50||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_OF_WATERLOO, ignoreCase = true) &&
            AppUtils.printQueryStringBuilder.isNotEmpty() && AppUtils.printQueryStringBuilder.length>50||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EL_PASO_TX_MACKAY, ignoreCase = true) &&
            AppUtils.printQueryStringBuilder.isNotEmpty() && AppUtils.printQueryStringBuilder.length>50||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DANVILLE_VA, ignoreCase = true) &&
            AppUtils.printQueryStringBuilder.isNotEmpty() && AppUtils.printQueryStringBuilder.length>50||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CAMDEN, ignoreCase = true) &&
            AppUtils.printQueryStringBuilder.isNotEmpty() && AppUtils.printQueryStringBuilder.length>50||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTH_LAKE, ignoreCase = true) &&
            AppUtils.printQueryStringBuilder.isNotEmpty() && AppUtils.printQueryStringBuilder.length>50||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WINPARK_TX, ignoreCase = true) &&
            AppUtils.printQueryStringBuilder.isNotEmpty() && AppUtils.printQueryStringBuilder.length>50||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HAMTRAMCK_MI, ignoreCase = true) &&
            AppUtils.printQueryStringBuilder.isNotEmpty() && AppUtils.printQueryStringBuilder.length>50||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_VIRGINIA, ignoreCase = true) &&
            AppUtils.printQueryStringBuilder.isNotEmpty() && AppUtils.printQueryStringBuilder.length>50 ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MEMORIALHERMAN, ignoreCase = true) &&
            AppUtils.printQueryStringBuilder.isNotEmpty() && AppUtils.printQueryStringBuilder.length>50||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MILLBRAE, ignoreCase = true) &&
            AppUtils.printQueryStringBuilder.isNotEmpty() && AppUtils.printQueryStringBuilder.length>50||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VOLUSIA, ignoreCase = true) &&
            AppUtils.printQueryStringBuilder.isNotEmpty() && AppUtils.printQueryStringBuilder.length>50||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CFFB, ignoreCase = true) &&
            AppUtils.printQueryStringBuilder.isNotEmpty() && AppUtils.printQueryStringBuilder.length>50||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_HGI_MANHATTAN, ignoreCase = true) &&
            AppUtils.printQueryStringBuilder.isNotEmpty() && AppUtils.printQueryStringBuilder.length>50||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HILTONHEAD, ignoreCase = true) &&
            AppUtils.printQueryStringBuilder.isNotEmpty() && AppUtils.printQueryStringBuilder.length>50)
        {
            return true
        }else{
            return false
        }
    }

    fun isSiteSupportCommandPrinting(printintByCMD : Boolean):Boolean{
       if((BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANIBEL, ignoreCase = true) && printintByCMD) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ_CCP, ignoreCase = true)||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PORTLAND_RO_REIMAGINED_PARKING, ignoreCase = true)||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CHATTANOOGA_TN_REIMAGINED_PARKING, ignoreCase = true)||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_JACKSONVILLE_FL_REIMAGINED_PARKING, ignoreCase = true)||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_OF_WILTON_MANORS_REIMAGINED_PARKING, ignoreCase = true)||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_REEF_CASPER_WY_REIMAGINED_PARKING, ignoreCase = true)||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MOBILE_AL_REIMAGINED_PARKING, ignoreCase = true)||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_FAYETTEVILLE_NC_REIMAGINED_PARKING, ignoreCase = true)||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_IMPARK_PHSA, ignoreCase = true)||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HILLSBORO_OR_REIMAGINED_PARKING, ignoreCase = true)||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PHSA_REIMAGINED_PARKING, ignoreCase = true)||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MERRICK_PARK_FL_REIMAGINED_PARKING, ignoreCase = true)||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ_KCMO_PRIVATE, ignoreCase = true)||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_A_BOBS_TOWING, ignoreCase = true)||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ISLEOFPALMS, ignoreCase = true)||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HARTFORD, ignoreCase = true)||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BEAUFORT, ignoreCase = true)||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SURF_CITY, ignoreCase = true)||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WOODSTOCK_GA, ignoreCase = true)||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_COB, ignoreCase = true)||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ASHLAND, ignoreCase = true)||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZ_CONJUCTIVE_POINT, ignoreCase = true)||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACEAMAZON, ignoreCase = true)||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SMYRNABEACH, ignoreCase = true)||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_FASHION_CITY, ignoreCase = true)||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRRS, ignoreCase = true)||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_UPTOWN_ATLANTA, ignoreCase = true)||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GREENBURGH_NY, ignoreCase = true)||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DEER_FIELD_BEACH_FL, ignoreCase = true)||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_OCEANCITY, ignoreCase = true)||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EPHRATA, ignoreCase = true)||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_VIRGINIA, ignoreCase = true)||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SATELLITE, ignoreCase = true)||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_INDIAN_HARBOUR_BEACH, ignoreCase = true)||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_Easton, ignoreCase = true)||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CLIFTON, ignoreCase = true)||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKPARKING, ignoreCase = true)||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PARKX, ignoreCase = true)||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RUTGERS, ignoreCase = true)||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKTEXAS, ignoreCase = true)||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOTTLEWORKS_IN, ignoreCase = true)||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DALLAS, ignoreCase = true)||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLASGOW, ignoreCase = true)||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_KANSAS_CITY, ignoreCase = true)||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EL_CAMINO_COLLEGE, ignoreCase = true)||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VEST_SECURITY_SYSTEM, ignoreCase = true)||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_JEFFERSON_CITY_MO, ignoreCase = true)||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MTA_LONG_ISLAND_RAIL_ROAD, ignoreCase = true)||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SEASTREAK, ignoreCase = true)||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ATLANTIC_BEACH_NC, ignoreCase = true)||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ATLANTIC_BEACH_SC, ignoreCase = true)||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ST_JOHNSBURY_VT, ignoreCase = true)||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_OXFORD, ignoreCase = true)||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRIME_PARKING, ignoreCase = true)||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITY_OF_WATERLOO, ignoreCase = true)||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EL_PASO_TX_MACKAY, ignoreCase = true)||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DANVILLE_VA, ignoreCase = true)||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CAMDEN, ignoreCase = true)||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTH_LAKE, ignoreCase = true)||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WINPARK_TX, ignoreCase = true)||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HAMTRAMCK_MI, ignoreCase = true)||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_HILTONHEAD, ignoreCase = true)||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RIVEROAKS, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MILLBRAE, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANMATEO_REDWOOD, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VOLUSIA, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CFFB, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_HGI_MANHATTAN, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_IMPARK_PHSA, ignoreCase = true) ||
        BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MEMORIALHERMAN, ignoreCase = true)){

        return true
    }else{
        return false
    }
}

    fun Bitmap?.combineBitmap(b: Bitmap?): Bitmap? =
        when {
            b == null || this == null -> {
                this ?: b
            } else -> {
            val resized = Bitmap.createScaledBitmap(b, b.width+100, b.height+100, true)
            val cs = Bitmap.createBitmap(
                max(this.width, b.width),
                this.height + b.height,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(cs)
            canvas.drawBitmap(this, 0f, 0f, null)
            canvas.drawBitmap(resized, 200f, this.height.toFloat()- lprImageHeightAdjustment(), null)
            cs
        }
        }

    /**
     * Function used to check if the app is running on a tablet or not, later when we implement tablet support, we wiil remove this function
     * and use boolean variable from tablet resource.
     */
    fun isTablet(context: Context): Boolean {
        return (context.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE
    }


    @JvmStatic
    fun getBeatSetEmptyFromSetting(): Boolean {
        val mDb: AppDatabase? = BaseApplication.instance?.getAppDatabase()
        val settingsList = Singleton.getDataSetList(DATASET_SETTINGS_LIST, mDb)
        return settingsList?.firstOrNull {
            it.type.equals(
                Constants.SETTINGS_FLAG_BEAT_FIELD_EMPTY_AFTER_EVERY_LOGIN,
                true
            )
        }?.mValue.toBooleanFromYesNo()
    }
     fun setExpiryYearBasedOnSettingResponse(): Boolean {
        try {
            val mDb: AppDatabase? = BaseApplication.instance?.getAppDatabase()
            val settingsList = Singleton.getDataSetList(DATASET_SETTINGS_LIST, mDb)
            if (settingsList != null && settingsList!!.isNotEmpty()) {
                for (i in settingsList!!.indices) {
                    if (settingsList!![i].type.equals(Constants.EXPIRY_YEAR_IS_DROP_DOWN, ignoreCase = true)
                        && settingsList!![i].mValue.equals("YES", ignoreCase = true)) {
                        return true
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    fun getColumWidthUsingColumnCount(columnCount: Int, columnMaxSize: Int): Int {
        return columnCount * columnMaxSize
    }

    fun autoFilledRemarkBasedOnExpiryMonthAndYear(
        context: Context,
        remark: AppCompatAutoCompleteTextView?,
        expiryMonth: AppCompatAutoCompleteTextView?,
        expiryYear: AppCompatAutoCompleteTextView?) {

        if (remark == null || expiryMonth == null || expiryYear == null) return

        expiryMonth.post {
            val isMonthEmpty = expiryMonth.text.isNullOrBlank()
            val isYearEmpty = expiryYear.text.isNullOrBlank()

            val message = when {
                isMonthEmpty && isYearEmpty -> context.getString(R.string.scr_lbl_expiry_year_month)
                isYearEmpty -> context.getString(R.string.scr_lbl_expiry_year)
                isMonthEmpty -> context.getString(R.string.scr_lbl_expiry_month)
                else -> ""
            }

            message?.let { remark.setText(it) }
        }
    }

    fun getFullName(firstName: String?, middleName: String?, lastName: String?): String {
        // Safely trim and filter out blank/null parts
        val nameParts = listOf(firstName, middleName, lastName)
            .map { it?.trim() }
            .filter { !it.isNullOrEmpty() }

        // Join the parts with a single space
        return nameParts.joinToString(" ")
    }

    fun getFullAddress(
        block: String?,
        street: String?,
        city: String?,
        state: String?,
        zip: String?
    ): String {
        // Safely trim and filter out blank/null parts
        val nameParts = listOf(block, street, city, state, zip)
            .map { it?.trim() }
            .filter { !it.isNullOrEmpty() }

        // Join the parts with a single space
        return nameParts.joinToString(" ")
    }
/**
  Check image file exist or not and its size
 */
        fun isValidFileToUpload(filePath: String): Boolean {
        val file = File(filePath)
        val fileExists = file.exists()
        val fileSizeInKB = file.length() / 1024  // Convert bytes to KB

        return fileExists && fileSizeInKB >= 1 // 1 KB
    }

    //Vehicle Sticker Data Store and Fetch
    private val vehicleStickerMap = mutableMapOf<String, VehicleInfoModel?>()

    // Store vehicle sticker data with LPR number as the key
    fun setVehicleStickerData(lprNumber: String, vehicle: VehicleInfoModel?) {
        vehicleStickerMap[lprNumber] = vehicle
    }

    // Retrieve vehicle sticker data by LPR number
    fun findVehicleStickerDetailsByKey(
        lprNumber: String
    ): VehicleInfoModel? {
        return vehicleStickerMap[lprNumber]
    }

    fun getWidthForHeight(givenHeight: Int, aspectWidth: Int, aspectHeight: Int): Int {
        return (givenHeight.toFloat() * aspectWidth / aspectHeight).toInt()
    }

    /**
     * Generates an API URL with dynamic time_from and time_to parameters.
     * By default, it sets:
     *  - time_from = yesterday's date (yyyy-MM-dd)
     *  - time_to = today's date (yyyy-MM-dd)
     */
    fun getDateRange(): Pair<String, String> {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val calendar = Calendar.getInstance()

        val toDate = dateFormat.format(calendar.time) // today

        calendar.add(Calendar.DAY_OF_MONTH, -1)
        val fromDate = dateFormat.format(calendar.time) // yesterday

        return Pair(fromDate, toDate)
    }

}
