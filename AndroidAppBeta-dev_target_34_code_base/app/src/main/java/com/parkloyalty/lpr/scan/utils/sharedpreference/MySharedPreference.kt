package com.parkloyalty.lpr.scan.utils.sharedpreference

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.parkloyalty.lpr.scan.utils.sharedpreference.MySharedPreferenceConstant.MY_SHARED_PREFERENCE_NAME
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * MySharedPreference is a singleton class for managing SharedPreferences using Hilt DI.
 *
 * How to inject and use in Activity/Fragment:
 *   @AndroidEntryPoint
 *   class MyActivity : AppCompatActivity() {
 *       @Inject lateinit var mySharedPreference: MySharedPreference
 *       ...
 *   }
 *
 * How to inject and use in ViewModel:
 *   @HiltViewModel
 *   class MyViewModel @Inject constructor(
 *       private val mySharedPreference: MySharedPreference
 *   ) : ViewModel() {
 *       ...
 *   }
 *
 * Usage examples:
 *   mySharedPreference.putString("key", "value")
 *   val value = mySharedPreference.getString("key")
 *   mySharedPreference.remove("key")
 *   mySharedPreference.clear()
 */
@Singleton
class MySharedPreference @Inject constructor(@ApplicationContext context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences(MY_SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)

    fun putString(key: String, value: String?) {
        prefs.edit { putString(key, value) }
    }

    fun getString(key: String, default: String? = null): String? =
        prefs.getString(key, default)

    fun putInt(key: String, value: Int) {
        prefs.edit { putInt(key, value) }
    }

    fun getInt(key: String, default: Int = 0): Int =
        prefs.getInt(key, default)

    fun putBoolean(key: String, value: Boolean) {
        prefs.edit { putBoolean(key, value) }
    }

    fun getBoolean(key: String, default: Boolean = false): Boolean =
        prefs.getBoolean(key, default)

    fun putFloat(key: String, value: Float) {
        prefs.edit { putFloat(key, value) }
    }

    fun getFloat(key: String, default: Float = 0f): Float =
        prefs.getFloat(key, default)

    fun putLong(key: String, value: Long) {
        prefs.edit { putLong(key, value) }
    }

    fun getLong(key: String, default: Long = 0L): Long =
        prefs.getLong(key, default)

    fun remove(key: String) {
        prefs.edit { remove(key) }
    }

    fun clear() {
        prefs.edit { clear() }
    }
}