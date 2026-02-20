package com.parkloyalty.lpr.scan.database

import com.parkloyalty.lpr.scan.util.ConstructLayoutBuilder.ioScope
import com.parkloyalty.lpr.scan.util.DATASET_CAR_MAKE_LIST
import com.parkloyalty.lpr.scan.util.DATASET_MAKE_MODEL_LIST
import com.parkloyalty.lpr.scan.util.DATASET_VIOLATION_LIST
import kotlinx.coroutines.async

object DataBaseUtil {
    // Citation upload status codes
    const val CITATION_UPLOADED = 0
    const val CITATION_UNUPLOADED_API_FAILED = 1
    const val CITATION_UNUPLOADED_ON_SCREEN_CHANGE = 2

    //Booklet Status
    const val BOOKLET_NOT_ISSUED = 0
    const val BOOKLET_ISSUED = 1
    const val BOOKLET_NOT_AVAILABLE = 2

    /**
     * Checks if the ViolationList dataset is empty in the local database.
     * If the list is empty or null, it clears the related dataset tables.
     *
     * @param mDb The instance of the local [AppDatabase].
     * @return `true` if the list is empty and tables were cleared, `false` otherwise.
     */
    fun getViolationListEmpty(mDb: AppDatabase?): Boolean {
        val mApplicationViolationList = Singleton.getDataSetList(DATASET_VIOLATION_LIST)
        val mApplicationMakeList = Singleton.getDataSetList(DATASET_CAR_MAKE_LIST)
        return if (mApplicationViolationList.isNullOrEmpty()||mApplicationViolationList?.size!!<1||
            mApplicationMakeList.isNullOrEmpty()||mApplicationMakeList?.size!!<1) {
            clearDataSetTable(mDb)
            true
        } else {
            false
        }
    }

    fun isViolationListEmpty(): Boolean =
        Singleton.getDataSetList(DATASET_VIOLATION_LIST).isNullOrEmpty() || Singleton.getDataSetList(DATASET_CAR_MAKE_LIST).isNullOrEmpty()


    /**
     * Checks if the ViolationList dataset is empty in the local database.
     * If the list is empty or null, it clears the related dataset tables.
     *
     * @param mDb The instance of the local [AppDatabase].
     * @return `true` if the list is empty and tables were cleared, `false` otherwise.
     */
    fun getViolationListEmptyForOfflineCitation(mDb: AppDatabase?): Boolean {
        val mApplicationList = Singleton.getDataSetList(DATASET_VIOLATION_LIST, mDb)
        return if (mApplicationList.isNullOrEmpty()) {
            true
        } else {
            false
        }
    }

    /**
     * Clears relevant dataset tables in the local database, including:
     * - Timestamp table
     * - All dataset entries
     * - Activity list
     *
     * @param mDb The instance of the local [AppDatabase].
     * @return `true` if the operation was performed, regardless of success.
     */
    fun clearDataSetTable(mDb: AppDatabase?): Boolean {
        mDb?.dbDAO?.apply {
            deleteTimeStampTable()
            deleteAllDataSet()
            deleteActivityList()
        }
        return true
    }
}