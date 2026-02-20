package com.parkloyalty.lpr.scan.basecontrol

import android.content.Context
import android.content.res.Configuration
import android.util.Log
import android.widget.EditText
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import androidx.room.Room
import com.fasterxml.jackson.databind.ObjectMapper
//import com.commonsware.cwac.saferoom.SQLCipherUtils
//import com.commonsware.cwac.saferoom.SafeHelperFactory
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.perf.FirebasePerformance
import com.parkloyalty.lpr.scan.database.AppDatabase
import com.parkloyalty.lpr.scan.database.DBMigration
import com.parkloyalty.lpr.scan.encryptionhandler.AESEncrypterUpdated
import com.parkloyalty.lpr.scan.interfaces.Constants
import com.parkloyalty.lpr.scan.interfaces.SharedPrefKey
import com.parkloyalty.lpr.scan.util.LocaleManager
import com.parkloyalty.lpr.scan.util.LogUtil
import com.parkloyalty.lpr.scan.util.LogUtil.isInvestigateAppPerformance
import com.parkloyalty.lpr.scan.util.SharedPref
import com.parkloyalty.lpr.scan.utility.ApplicationMode
import com.parkloyalty.lpr.scan.utils.ObjectMapperProvider
import com.seikoinstruments.sdk.thermalprinter.PrinterManager
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject


/**
 * this base application class
 */
@HiltAndroidApp
class BaseApplication : MultiDexApplication() {

   // @Inject lateinit var objectMapper: ObjectMapper


    /**
     * this method return application mode i.e.development mode or production mode
     *
     * @return ApplicationMode
     */
    private var applicationMode: ApplicationMode? = null
    private var appDatabase: AppDatabase? = null
    private val DB_NAME = "park_loyalty"
    var isApplicationBackgruond = false
    private var mCrashlytics: FirebaseCrashlytics? = null

    override fun onCreate() {
        super.onCreate()
        // objectMapper is injected by Hilt before onCreate -> warm-up already triggered in provider
        // If you want additional warm-up (e.g. using real model classes), do it here:
        try {
            //objectMapper.writeValueAsString(listOf<Map<String, String>>(mapOf("warm" to "up")))
            ObjectMapperProvider.instance.writeValueAsString(listOf<Map<String, String>>(mapOf("warm" to "up")))
        } catch (e: Exception) { }

        FirebasePerformance.getInstance().isPerformanceCollectionEnabled = true

        localeManager = LocaleManager(baseContext)
        instance = this
        //change application mode
        applicationMode = ApplicationMode.PRODUCTION
        initAppDatabase()
        mCrashlytics = FirebaseCrashlytics.getInstance()

//        if (BuildConfig.DEBUG && isInvestigateAppPerformance) {
//            AppWatcher.objectWatcher.watch(this, "Application instance")
//        }

        //To reduce user-facing delay, you can "pre-warm" the ObjectMapper on app startup in a background thread.
//        GlobalScope.launch(Dispatchers.Default) {
//            ObjectMapperProvider.instance.writeValueAsString("")
//        }
//
//        ObjectMapperProvider.instance // triggers static init
//        ObjectMapperProvider.instance.writeValueAsString("")
//        ObjectMapperProvider.toJson(mapOf("ping" to "pong")) // pre-warm serialization
    }

    private fun initAppDatabase() {
        appDatabase = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, DB_NAME
        )
            //TODO JANAK avoid calling database on main thread to avoid ANR & Leakage problems
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .addMigrations(DBMigration.MIGRATION_5_6)
            .addMigrations(DBMigration.MIGRATION_6_7)
            .addMigrations(DBMigration.MIGRATION_7_8)
            .addMigrations(DBMigration.MIGRATION_8_9)
            .addMigrations(DBMigration.MIGRATION_9_10)
            .addMigrations(DBMigration.MIGRATION_10_11)
            .addMigrations(DBMigration.MIGRATION_11_12)
            .addMigrations(DBMigration.MIGRATION_12_13)
            .addMigrations(DBMigration.MIGRATION_13_14)
            .addMigrations(DBMigration.MIGRATION_14_15)
            .addMigrations(DBMigration.MIGRATION_15_16)
            .addMigrations(DBMigration.MIGRATION_16_17)
            .addMigrations(DBMigration.MIGRATION_17_18)
            .build()
    }

    fun getAppDatabase(): AppDatabase? {
        return appDatabase
    }

    override fun attachBaseContext(base: Context) {
//        super.attachBaseContext(base);
        localeManager = LocaleManager(base)
        MultiDex.install(this)
        super.attachBaseContext(localeManager?.setLocale(base))
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        localeManager?.setLocale(this)
        Log.d(TAG, "onConfigurationChanged: " + newConfig.locale.language)
    }

    companion object {
        private const val TAG = "BaseApplication"

        // for the sake of simplicity. use DI in real apps instead
        var localeManager: LocaleManager? = null
        /**
         * @return ApplicationController singleton instance
         */
        /**
         * A singleton instance of the application class for easy access in other places
         */
        @get:Synchronized
        var instance: BaseApplication? = null

        private val hexArray = "0123456789ABCDEF".toCharArray()

        /**
         * converting bytes to Hex string
         * @param bytes data
         * @return hex string
         */
        fun bytesToHex(bytes: ByteArray): String {
            val hexChars = CharArray(bytes.size * 2)
            for (j in bytes.indices) {
                val v = bytes[j].toInt() and 0xFF

                hexChars[j * 2] = hexArray[v ushr 4]
                hexChars[j * 2 + 1] = hexArray[v and 0x0F]
            }
            return String(hexChars)
        }
    }


    /** PrinterManager(SDK)  */
    private var mPrinterManager: PrinterManager? = null


    fun setPrinterManager(manager: PrinterManager?) {
        mPrinterManager = manager
    }


    fun getPrinterManager(): PrinterManager? {
        return mPrinterManager
    }

}