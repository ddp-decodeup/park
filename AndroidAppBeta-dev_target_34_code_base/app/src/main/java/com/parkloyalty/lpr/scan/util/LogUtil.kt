package com.parkloyalty.lpr.scan.util

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.parkloyalty.lpr.scan.BuildConfig
import com.parkloyalty.lpr.scan.extensions.getSettingFileValuesForCMDPrinting
import com.parkloyalty.lpr.scan.extensions.getSettingFileValuesForCrossButton
import com.parkloyalty.lpr.scan.interfaces.Constants
import com.parkloyalty.lpr.scan.ui.printer.PaperWidthBasedPrinterType
import com.parkloyalty.lpr.scan.ui.printer.PrinterType

object LogUtil {
    var isEnableLogs = false
    var isEnableAPILogs = true
    var isSavePrintCommand = true


    /**
     * For false only write citation flow API payload logs for true write all API payload in logs file
     */
    var WRITE_ALL_API_IN_LOGS_FILE = false

    var isNewJacksonInstance = true

    /**
     * New Orleans set false dynamic height taking height from setting file
     */
    //This flag is used to get print height dynamic from command & image printing (ZEBRA)
    var isPrintHeightDynamicFromTicket = true && !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ORLEANS, ignoreCase = true)


    //This flag is used to generate facsimile image based on command given for zebra printer
    var isEnableCommandBasedFacsimile = true && AppUtils.isSiteSupportCommandPrinting(getSettingFileValuesForCMDPrinting())

    var isEnableToast = true
    var isEnableGoogleAnalytics = false

    //This flag is used to enable disable extra activity logs
    var isEnableActivityLogs = true


    var isInvestigateAppPerformance = false

    //Hem Menu
    var isShowQRCodeOptionInHemMenu = true

    //This flag is used to show hide cross clear icon with input field
    fun isEnableCrossClearButton() : Boolean{
        if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITYOFSANDIEGO, ignoreCase = true) || getSettingFileValuesForCrossButton()){
            return true
        }else{
            return false
        }
    }

    //This flag is used to show hide cross clear icon with input field
    fun isMunicipalCitationEnabled(): Boolean {
        if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANIBEL, ignoreCase = true)
            || BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_IMPARK_PHSA, ignoreCase = true)
        ) {
            return true
        } else {
            return false
        }
    }
    //This flag is used to show hide cross clear icon with input field
    fun isOwnerBillEnabled(): Boolean {
        if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_IMPARK_PHSA, ignoreCase = true)
        ) {
            return false
        } else {
            return false
        }
    }

    //Function used to get printer name to be used for ticket printing
    fun getPrinterTypeForPrint(): PrinterType {
        //return PrinterType.XF2T_PRINTER
//        return PrinterType.STAR_PRINTER
        return PrinterType.ZEBRA_PRINTER
    }

    //Start of Zebra Printer Paper Width
    fun getPaperWidthBasedPrinterType(): PaperWidthBasedPrinterType {
        return if (BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_UPTOWN_ATLANTA,
                ignoreCase = true
            ) || BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_WINPARK_TX,
                ignoreCase = true
            )
        ) {
            PaperWidthBasedPrinterType.ZEBRA_FOUR_INCH_PRINTER
        } else {
            PaperWidthBasedPrinterType.ZEBRA_THREE_INCH_PRINTER
        }
    }
    //End of Zebra Printer Paper Width

    //  public static boolean isEnableLogs = false;
    //  public static boolean isEnableAPILogs = false;
    //  public static boolean isEnableTo;ast = false;

    fun printLog(tag: String?, `object`: Any?) {
        if (isEnableLogs && `object` != null) {
            Log.d(tag, "" + `object`)
        }
    }

    @JvmStatic
    fun printLog(tag: String?, `object`: String?) {
        if (isEnableLogs && `object` != null) {
            Log.d(tag, "" + `object`)
        }
    }

    fun printLogHeader(tag: String?, `object`: String?) {
        if (isEnableAPILogs && `object` != null) {
            Log.e(tag, "" + `object`)
        }
    }
    fun printLogCommand(tag: String?, `object`: String?) {
        if (isEnableAPILogs && `object` != null) {
            Log.i(tag, "" + `object`)
        }
    }
    fun printLogPrinterQuery(tag: String?, `object`: String?) {
        if (isEnableAPILogs && `object` != null) {
            Log.e(tag, "" + `object`)
        }
    }

    fun printLog(tag: String?, `object`: String?, tr: Throwable?) {
        if (isEnableLogs && `object` != null) {
            printLog(tag, "" + `object`)
        }
    }

    @JvmStatic
    fun printToastMSGForErrorWarning(mContext: Context?, `object`: String?) {
        if (isEnableToast && `object` != null) {
            Handler(Looper.getMainLooper()).post {
                Toast.makeText(mContext, `object`, Toast.LENGTH_SHORT).show()
            }
        }
    }

    @JvmStatic
    fun printToastMSG(mContext: Context?, `object`: String?) {
        if (isEnableToast && `object` != null) {
            Handler(Looper.getMainLooper()).post {
                Toast.makeText(mContext, `object`, Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun printToastMSGCenter(mContext: Context?, `object`: String?) {
        if (isEnableToast && `object` != null) {
            val tm = Toast.makeText(mContext, `object`, Toast.LENGTH_SHORT)
            tm.setGravity(Gravity.CENTER /*|Gravity.LEFT*/, 0, 0)
            tm.show()
        }
    }

    fun printSnackBar(view: View, mContext: Activity?, mObject: String?)
    {
        mContext!!.runOnUiThread {
                Snackbar.make(view, mObject.toString(), Snackbar.LENGTH_LONG).show()

        }
    }
}