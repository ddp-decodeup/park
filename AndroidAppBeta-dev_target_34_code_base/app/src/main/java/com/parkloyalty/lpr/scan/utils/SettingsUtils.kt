package com.parkloyalty.lpr.scan.utils

import com.parkloyalty.lpr.scan.database.NewSingletonDataSet
import com.parkloyalty.lpr.scan.util.DATASET_SETTINGS_LIST
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsUtils
@Inject constructor(
    private val singletonDataSet: NewSingletonDataSet,
) {

    suspend fun getDefaultZoneAtZeroIndex(): String? {
        return try {
            val settingsList = withContext(Dispatchers.IO) {
                singletonDataSet.getDataSetList(dataSetType = DATASET_SETTINGS_LIST)
            }
            settingsList?.firstOrNull()?.mValue
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun getDefaultState(): String? {
        return try {
            val settingsList = withContext(Dispatchers.IO) {
                singletonDataSet.getDataSetList(dataSetType = DATASET_SETTINGS_LIST)
            }

            settingsList?.firstOrNull {
                it.type.equals("DEFAULT_STATE", ignoreCase = true)
            }?.mValue
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun isShowTireStemIcon(): Boolean {
        return try {
            val settingsList = withContext(Dispatchers.IO) {
                singletonDataSet.getDataSetList(dataSetType = DATASET_SETTINGS_LIST)
            }

            settingsList?.any {
                it.type.equals("IS_TIRE_STEM_ICON", ignoreCase = true) && it.mValue.equals(
                    "YES", ignoreCase = true
                )
            } ?: false
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun isTimestampNeedOnImage(): Boolean {
        return try {
            val settingsList = withContext(Dispatchers.IO) {
                singletonDataSet.getDataSetList(dataSetType = DATASET_SETTINGS_LIST)
            }

            settingsList?.any {
                it.type.equals("IMAGE_TIMESTAMP", ignoreCase = true) && it.mValue.equals(
                    "YES", ignoreCase = true
                )
            } ?: false
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun hasModel(): Boolean {
        return try {
            val settingsList = withContext(Dispatchers.IO) {
                singletonDataSet.getDataSetList(dataSetType = DATASET_SETTINGS_LIST)
            }

            settingsList?.any {
                it.type.equals("HAS_MODEL", ignoreCase = true) && it.mValue.equals(
                    "YES", ignoreCase = true
                )
            } ?: false
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}