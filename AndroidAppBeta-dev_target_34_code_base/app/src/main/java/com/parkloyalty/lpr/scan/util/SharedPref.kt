package com.parkloyalty.lpr.scan.util

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import com.parkloyalty.lpr.scan.ui.check_setup.model.timing.AddTimingRequest
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.CitationInsurranceDatabaseModel
import androidx.core.content.edit
import com.parkloyalty.lpr.scan.utils.ObjectMapperProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * SharedPref class is used for saving data into sharedPreference
 */
@Singleton
class SharedPref @Inject constructor(@ApplicationContext val mContext: Context) {
    private var mSharedPref: SharedPreferences =
        mContext.getSharedPreferences(mContext.packageName, Activity.MODE_PRIVATE)

    fun read(key: String?, defValue: String?): String? {
        return mSharedPref.getString(key, defValue)
    }

    fun write(key: String?, value: String?) {
        val prefsEditor = mSharedPref.edit()
        prefsEditor.putString(key, value)
        prefsEditor.apply()
    }
    fun readInt(key: String?, defValue: Int?): Int? {
        return mSharedPref.getInt(key, defValue!!)
    }

    fun writeInt(key: String?, value: Int?) {
        val prefsEditor = mSharedPref.edit()
        prefsEditor.putInt(key, value!!)
        prefsEditor.apply()
    }

    fun write(key: String?, value: CitationInsurranceDatabaseModel?) {
        try {
            val json = ObjectMapperProvider.instance.writeValueAsString(value)
            val prefsEditor = mSharedPref.edit()
            prefsEditor.putString(key, json)
            prefsEditor.apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun read(key: String?, defValue: Boolean): Boolean {
        return mSharedPref.getBoolean(key, defValue)
    }

    fun write(key: String?, value: Boolean) {
        val prefsEditor = mSharedPref.edit()
        prefsEditor.putBoolean(key, value)
        prefsEditor.apply()
    }

    fun read(key: String?): CitationInsurranceDatabaseModel? {
        var obj: CitationInsurranceDatabaseModel? = null
        try {
            val json = mSharedPref.getString(key, "")
            obj = ObjectMapperProvider.instance.readValue(json, CitationInsurranceDatabaseModel::class.java)
            return obj
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return obj
    }

    //Clear data based on key
    fun clearSharedPrefData(key: String?) {
        mSharedPref.edit {
            remove(key)
        }
    }

//    companion object {
//        @SuppressLint("StaticFieldLeak")
//        private var mInstance: SharedPref? = null
//
//        @JvmStatic
//        fun getInstance(context: Context?): SharedPref {
//            if (mInstance == null) {
//                mInstance = SharedPref(context!!)
//            }
//            return mInstance!!
//        }
//    }

    fun write(key: String?, value: AddTimingRequest?) {
        try {
            val json = ObjectMapperProvider.instance.writeValueAsString(value)
            val prefsEditor = mSharedPref.edit()
            prefsEditor.putString(key, json)
            prefsEditor.apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun readTime(key: String?, s: String): AddTimingRequest? {
        var obj: AddTimingRequest? = null
        try {
            val json = mSharedPref.getString(key, "")
            obj = ObjectMapperProvider.instance.readValue(json, AddTimingRequest::class.java)
            return obj
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return obj
    }

    fun writeOverTimeParkingTicketDetails(key: String?, value: AddTimingRequest?) {
        try {
            val json = ObjectMapperProvider.instance.writeValueAsString(value)
            val prefsEditor = mSharedPref.edit()
            prefsEditor.putString(key, json)
            prefsEditor.apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun readOverTimeParkingTicketDetails(key: String?, s: String): AddTimingRequest? {
        var obj: AddTimingRequest? = null
        try {
            val json = mSharedPref.getString(key, "")
            obj = ObjectMapperProvider.instance.readValue(json, AddTimingRequest::class.java)
            return obj
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return obj
    }
    fun writeInventory(key: String?, value: AddTimingRequest?) {
        try {
            val json = ObjectMapperProvider.instance.writeValueAsString(value)
            val prefsEditor = mSharedPref.edit()
            prefsEditor.putString(key, json)
            prefsEditor.apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun readInventory(key: String?, s: String): AddTimingRequest? {
        var obj: AddTimingRequest? = null
        try {
            val json = mSharedPref.getString(key, "")
            obj = ObjectMapperProvider.instance.readValue(json, AddTimingRequest::class.java)
            return obj
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return obj
    }

}