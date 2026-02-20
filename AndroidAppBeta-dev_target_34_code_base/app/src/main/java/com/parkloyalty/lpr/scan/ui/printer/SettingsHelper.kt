package com.parkloyalty.lpr.scan.ui.printer

import android.content.Context
import android.content.SharedPreferences

/**
 * Created by BWai on 8/17/2015.
 */
/**
 * SettingsHelper saves WIFI information as a SharedPreferences key value pair that can be
 * exported into other applications. This is used in lieu of server communication, which can be implemented
 * with the TCP/IP connections in the StarPrinterActivity.
 */
object SettingsHelper {
    private val PREFS_NAME: String = "SavedAddresses"
    private val bluetoothAddressKey: String = "ZEBRA_BLUETOOTH_ADDRESS"
    private val ipAddressKey: String = "ZEBRA_IP_ADDRESS"
    private val wlanPortKey: String = "ZEBRA_WLAN_PORT"
    private val wlanAddressKey: String = "ZEBRA_WLAN_ADDRESS"
    private val wiredAddressKey: String = "ZEBRA_WIRED_ADDRESS"
    fun getIp(context: Context): String? {
        val settings: SharedPreferences = context.getSharedPreferences(PREFS_NAME, 0)
        return settings.getString(ipAddressKey, "")
    }

    fun getPort(context: Context): String? {
        val settings: SharedPreferences = context.getSharedPreferences(PREFS_NAME, 0)
        return settings.getString(wlanPortKey, "")
    }

    fun getBluetoothAddressKey(context: Context): String? {
        val settings: SharedPreferences = context.getSharedPreferences(PREFS_NAME, 0)
        return settings.getString(bluetoothAddressKey, "")
    }

    fun saveIp(context: Context, ip: String?) {
        val settings: SharedPreferences = context.getSharedPreferences(PREFS_NAME, 0)
        val editor: SharedPreferences.Editor = settings.edit()
        editor.putString(ipAddressKey, ip)
        editor.commit()
    }

    fun savePort(context: Context, port: String?) {
        val settings: SharedPreferences = context.getSharedPreferences(PREFS_NAME, 0)
        val editor: SharedPreferences.Editor = settings.edit()
        editor.putString(wlanPortKey, port)
        editor.commit()
    }

    fun saveBluetoothAddress(context: Context, address: String?) {
        val settings: SharedPreferences = context.getSharedPreferences(PREFS_NAME, 0)
        val editor: SharedPreferences.Editor = settings.edit()
        editor.putString(bluetoothAddressKey, address)
        editor.commit()
    }

    fun saveWlanAddress(context: Context, address: String?) {
        val settings: SharedPreferences = context.getSharedPreferences(PREFS_NAME, 0)
        val editor: SharedPreferences.Editor = settings.edit()
        editor.putString(wlanAddressKey, address)
        editor.commit()
    }

    fun saveWiredAddress(context: Context, address: String?) {
        val settings: SharedPreferences = context.getSharedPreferences(PREFS_NAME, 0)
        val editor: SharedPreferences.Editor = settings.edit()
        editor.putString(wiredAddressKey, address)
        editor.commit()
    }
}