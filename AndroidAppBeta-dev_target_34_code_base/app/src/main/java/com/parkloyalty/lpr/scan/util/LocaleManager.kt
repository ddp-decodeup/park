package com.parkloyalty.lpr.scan.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build.VERSION_CODES
import android.os.LocaleList
import android.preference.PreferenceManager
import androidx.annotation.RequiresApi
import java.util.*

/**
 * Created by Vinod Makode
 * Date 26/12/2020.
 * Time 04:24 PM
 */
class LocaleManager(context: Context?) {
    private val prefs: SharedPreferences
    fun setLocale(c: Context): Context {
        return updateResources(c, getLanguage())
    }

    fun setNewLocale(c: Context, language: String): Context {
        persistLanguage(language)
        return updateResources(c, language)
    }

    private fun getLanguage(): String? {
        return prefs.getString(LANGUAGE_KEY, LANGUAGE_ENGLISH)
    }
    @SuppressLint("ApplySharedPref")
    private fun persistLanguage(language: String) {
        // use commit() instead of apply(), because sometimes we kill the application process
        // immediately that prevents apply() from finishing
        prefs.edit().putString(LANGUAGE_KEY, language).commit()
    }

    private fun updateResources(context: Context, language: String?): Context {
        var context = context
        val locale = Locale(language)
        Locale.setDefault(locale)
        val res = context.resources
        val config = Configuration(res.configuration)
        if (Util.isAtLeastVersion(VERSION_CODES.N)) {
            setLocaleForApi24(config, locale)
            context = context.createConfigurationContext(config)
        } else if (Util.isAtLeastVersion(VERSION_CODES.JELLY_BEAN_MR1)) {
            config.setLocale(locale)
            context = context.createConfigurationContext(config)
        } else {
            config.locale = locale
            res.updateConfiguration(config, res.displayMetrics)
        }
        return context
    }

    @RequiresApi(api = VERSION_CODES.N)
    private fun setLocaleForApi24(config: Configuration, target: Locale) {
        val set: MutableSet<Locale> = LinkedHashSet()
        // bring the target locale to the front of the list
        set.add(target)
        val all = LocaleList.getDefault()
        for (i in 0 until all.size()) {
            // append other locales supported by the user
            set.add(all[i])
        }
        val locales = set.toTypedArray()
        config.setLocales(LocaleList(*locales))
    }

    companion object {
        const val LANGUAGE_ENGLISH = "en"
        const val LANGUAGE_SWAHILI = "sw"
        private const val LANGUAGE_KEY = "language_key"
        fun getLocale(res: Resources): Locale {
            val config = res.configuration
            return if (Util.isAtLeastVersion(VERSION_CODES.N)) config.locales[0] else config.locale
        }
    }

    init {
        prefs = PreferenceManager.getDefaultSharedPreferences(context)
    }
}