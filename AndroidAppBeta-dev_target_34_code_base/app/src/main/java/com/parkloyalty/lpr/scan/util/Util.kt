package com.parkloyalty.lpr.scan.util

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.text.InputType
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import com.parkloyalty.lpr.scan.BuildConfig
import com.parkloyalty.lpr.scan.database.AppDatabase
import com.parkloyalty.lpr.scan.extensions.getInitials
import com.parkloyalty.lpr.scan.extensions.nullSafety
import com.parkloyalty.lpr.scan.interfaces.Constants
import com.parkloyalty.lpr.scan.interfaces.Constants.Companion.SETTINGS_FLAG_VALUE_OFFICER_NAME_FIRST_FULL_LAST_FULL
import com.parkloyalty.lpr.scan.interfaces.Constants.Companion.SETTINGS_FLAG_VALUE_OFFICER_NAME_FIRST_FULL_LAST_INITIAL
import com.parkloyalty.lpr.scan.interfaces.Constants.Companion.SETTINGS_FLAG_VALUE_OFFICER_NAME_FIRST_FULL_ONLY
import com.parkloyalty.lpr.scan.interfaces.Constants.Companion.SETTINGS_FLAG_VALUE_OFFICER_NAME_FIRST_INITIAL_LAST_FULL
import com.parkloyalty.lpr.scan.interfaces.Constants.Companion.SETTINGS_FLAG_VALUE_OFFICER_NAME_FIRST_INITIAL_LAST_INITIAL
import com.parkloyalty.lpr.scan.interfaces.Constants.Companion.SETTINGS_FLAG_VALUE_OFFICER_NAME_FIRST_NAME_BLANK_LAST_NAME_FULL
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DatasetResponse
import com.parkloyalty.lpr.scan.ui.login.model.*
import com.parkloyalty.lpr.scan.ui.officerdailysummary.model.OfficerDailySummaryResponse
import com.parkloyalty.lpr.scan.utils.ObjectMapperProvider
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.*


object Util {
    fun saveToInternalStorage(bitmapImage: Bitmap, context: Context?): String {
        val cw = ContextWrapper(context)
        // path to /data/data/yourapp/app_data/imageDir
        val directory = cw.getDir(Constants.INTERNAL_FOLDER_NAME, Context.MODE_PRIVATE)
        // Create imageDir
        val dateFormat = SimpleDateFormat("yyyyMMdd'T'HHmmss")
        val timeStamp = dateFormat.format(Date())
        val imageFileName = "picture_$timeStamp.jpg"
        val mypath = File(directory, imageFileName)
        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(mypath)
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fos)
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

    fun isAtLeastVersion(version: Int): Boolean {
        return Build.VERSION.SDK_INT >= version
    }



    fun toObject(jsonString: String, type: RadioResponse?): RadioResponse? {
        var jsonString = jsonString
        jsonString = jsonString.replace(" ", "")
        jsonString = jsonString.replace("ï¹ nbsp", "")
        jsonString = jsonString.replace("nbsp", "")
        jsonString = jsonString.replace("&", "")
        jsonString = jsonString.replace("&", "")
        jsonString = jsonString.replace("amp", "")

        return if (type is RadioResponse) {
            try {
                ObjectMapperProvider.fromJson(jsonString, RadioResponse::class.java)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        } else {
            throw RuntimeException("å?ªèƒ½æ˜¯Class<?>æˆ–è€…é€šè¿‡TypeTokenèŽ·å?–çš„Typeç±»åž‹")
        }
    }

    @JvmStatic
    fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        // Raw height and width of image
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {
            val halfHeight = height / 2
            val halfWidth = width / 2

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }

    @JvmStatic
    fun setFieldCaps(
        mContext: Context?,
        autoCompleteTextView: AppCompatAutoCompleteTextView
    ): AppCompatAutoCompleteTextView {
        autoCompleteTextView.inputType =
            InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE or InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS
        return autoCompleteTextView
    }

    fun getDefaultTimeZoneID(): String {
            var ID = "America/Los_Angeles"
            try {
                ID = TimeZone.getDefault().id
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return ID
        }

    @JvmStatic
    fun setTimeDataList(
        timeDataList: UpdateTimeDataList, mTimingDatabaseList: TimestampDatatbase,
        mTimeResponse: UpdateTimeResponse, mResponseEntity: UpdateTimeData
    ): UpdateTimeDataList {
        if (mTimingDatabaseList.timeList?.agencyList != null && mTimeResponse.data?.get(0)?.agencyList != null &&
            mTimingDatabaseList.timeList?.agencyList?.name != mTimeResponse.data?.get(0)?.agencyList ||
            mTimingDatabaseList.timeList?.agencyList == null &&
            mTimeResponse.data?.get(0)?.agencyList != null
        ) {
            timeDataList.agencyList = UpdateTimeDb(mResponseEntity.agencyList.nullSafety(), true)
        }
        if (mTimingDatabaseList.timeList?.shiftList != null && mTimeResponse.data?.get(0)?.shiftList != null &&
            mTimingDatabaseList.timeList?.shiftList?.name != mTimeResponse.data?.get(0)?.shiftList ||
            mTimingDatabaseList.timeList?.shiftList == null &&
            mTimeResponse.data?.get(0)?.shiftList != null
        ) {
            timeDataList.shiftList = UpdateTimeDb(mResponseEntity.shiftList.nullSafety(), true)
        }
        if (mTimingDatabaseList.timeList?.radioList != null && mTimeResponse.data?.get(0)?.radioList != null &&
            mTimingDatabaseList.timeList?.radioList?.name != mTimeResponse.data?.get(0)?.radioList ||
            mTimingDatabaseList.timeList?.radioList == null &&
            mTimeResponse.data?.get(0)?.radioList != null
        ) {
            timeDataList.radioList = UpdateTimeDb(mResponseEntity.radioList.nullSafety(), true)
        }
        if (mTimingDatabaseList.timeList?.zoneList != null && mTimeResponse.data?.get(0)?.zoneList != null &&
            mTimingDatabaseList.timeList?.zoneList?.name != mTimeResponse.data?.get(0)?.zoneList ||
            mTimingDatabaseList.timeList?.zoneList == null &&
            mTimeResponse.data?.get(0)?.zoneList != null
        ) {
            timeDataList.zoneList = UpdateTimeDb(mResponseEntity.zoneList.nullSafety(), true)
        }
        if (mTimingDatabaseList.timeList?.activityList != null && mTimeResponse.data?.get(0)?.activityList != null &&
            mTimingDatabaseList.timeList?.activityList?.name != mTimeResponse.data?.get(0)?.activityList ||
            mTimingDatabaseList.timeList?.activityList == null &&
            mTimeResponse.data?.get(0)?.activityList != null
        ) {
            timeDataList.activityList =
                UpdateTimeDb(mResponseEntity.activityList.nullSafety(), true)
        }
        if (mTimingDatabaseList.timeList?.commentsList != null && mTimeResponse.data?.get(0)?.commentsList != null &&
            mTimingDatabaseList.timeList?.commentsList?.name != mTimeResponse.data?.get(0)?.commentsList ||
            mTimingDatabaseList.timeList?.commentsList == null &&
            mTimeResponse.data?.get(0)?.commentsList != null
        ) {
            timeDataList.commentsList =
                UpdateTimeDb(mResponseEntity.commentsList.nullSafety(), true)
        }
        if (mTimingDatabaseList.timeList?.supervisorList != null && mTimeResponse.data?.get(0)?.supervisorList != null &&
            mTimingDatabaseList.timeList?.supervisorList?.name != mTimeResponse.data?.get(0)?.supervisorList ||
            mTimingDatabaseList.timeList?.supervisorList == null &&
            mTimeResponse.data?.get(0)?.supervisorList != null
        ) {
            timeDataList.supervisorList =
                UpdateTimeDb(mResponseEntity.supervisorList.nullSafety(), true)
        }
        if (mTimingDatabaseList.timeList?.beatList != null && mTimeResponse.data?.get(0)?.beatList != null &&
            mTimingDatabaseList.timeList?.beatList?.name != mTimeResponse.data?.get(0)?.beatList ||
            mTimingDatabaseList.timeList?.beatList == null &&
            mTimeResponse.data?.get(0)?.beatList != null
        ) {
            timeDataList.beatList = UpdateTimeDb(mResponseEntity.beatList.nullSafety(), true)
        }
        if (mTimingDatabaseList.timeList?.decalYearList != null && mTimeResponse.data?.get(0)?.decalYearList != null &&
            mTimingDatabaseList.timeList?.decalYearList?.name != mTimeResponse.data?.get(0)?.decalYearList ||
            mTimingDatabaseList.timeList?.decalYearList == null &&
            mTimeResponse.data?.get(0)?.decalYearList != null
        ) {
            timeDataList.decalYearList =
                UpdateTimeDb(mResponseEntity.decalYearList.nullSafety(), true)
            //myDatabase?.dbDAO?.deleteDatasetDecalYearListModel()
        }
        if (mTimingDatabaseList.timeList?.carMakeList != null && mTimeResponse.data?.get(0)?.carMakeList != null &&
            mTimingDatabaseList.timeList?.carMakeList?.name != mTimeResponse.data?.get(0)?.carMakeList ||
            mTimingDatabaseList.timeList?.carMakeList == null &&
            mTimeResponse.data?.get(0)?.carMakeList != null
        ) {
            timeDataList.carMakeList = UpdateTimeDb(mResponseEntity.carMakeList.nullSafety(), true)
            //myDatabase?.dbDAO?.deleteDatasetCarMakeListModel()
        }
        if (mTimingDatabaseList.timeList?.carModelList != null && mTimeResponse.data?.get(0)?.carModelList != null &&
            mTimingDatabaseList.timeList?.carModelList?.name != mTimeResponse.data?.get(0)?.carModelList ||
            mTimingDatabaseList.timeList?.carModelList == null &&
            mTimeResponse.data?.get(0)?.carModelList != null
        ) {
            timeDataList.carModelList =
                UpdateTimeDb(mResponseEntity.carModelList.nullSafety(), true)
            //myDatabase?.dbDAO?.deleteDatasetCarMakeListModel()
        }
        if (mTimingDatabaseList.timeList?.carColorList != null && mTimeResponse.data?.get(0)?.carColorList != null &&
            mTimingDatabaseList.timeList?.carColorList?.name != mTimeResponse.data?.get(0)?.carColorList ||
            mTimingDatabaseList.timeList?.carColorList == null &&
            mTimeResponse.data?.get(0)?.carColorList != null
        ) {
            timeDataList.carColorList =
                UpdateTimeDb(mResponseEntity.carColorList.nullSafety(), true)
            //myDatabase?.dbDAO?.deleteDatasetCarColorListModel()
        }
        if (mTimingDatabaseList.timeList?.stateList != null && mTimeResponse.data?.get(0)?.stateList != null &&
            mTimingDatabaseList.timeList?.stateList?.name != mTimeResponse.data?.get(0)?.stateList ||
            mTimingDatabaseList.timeList?.stateList == null &&
            mTimeResponse.data?.get(0)?.stateList != null
        ) {
            timeDataList.stateList = UpdateTimeDb(mResponseEntity.stateList.nullSafety(), true)
            //myDatabase?.dbDAO?.deleteDatasetStateListModel()

        }
        if (mTimingDatabaseList.timeList?.streetList != null && mTimeResponse.data?.get(0)?.streetList != null &&
            mTimingDatabaseList.timeList?.streetList?.name != mTimeResponse.data?.get(0)?.streetList ||
            mTimingDatabaseList.timeList?.streetList == null &&
            mTimeResponse.data?.get(0)?.streetList != null
        ) {
            timeDataList.streetList = UpdateTimeDb(mResponseEntity.streetList.nullSafety(), true)
            //myDatabase?.dbDAO?.deleteDatasetStreetListModel()
        }
        if (mTimingDatabaseList.timeList?.meterList != null && mTimeResponse.data?.get(0)?.meterList != null &&
            mTimingDatabaseList.timeList?.meterList?.name != mTimeResponse.data?.get(0)?.meterList ||
            mTimingDatabaseList.timeList?.meterList == null &&
            mTimeResponse.data?.get(0)?.meterList != null
        ) {
            timeDataList.meterList = UpdateTimeDb(mResponseEntity.meterList.nullSafety(), true)
            //myDatabase?.dbDAO?.deleteDatasetMeterListModel()
        }
        if (mTimingDatabaseList.timeList?.carBodyStyleList != null && mTimeResponse.data?.get(0)?.carBodyStyleList != null &&
            mTimingDatabaseList.timeList?.carBodyStyleList?.name != mTimeResponse.data?.get(0)?.carBodyStyleList ||
            mTimingDatabaseList.timeList?.carBodyStyleList == null &&
            mTimeResponse.data?.get(0)?.carBodyStyleList != null
        ) {
            timeDataList.carBodyStyleList =
                UpdateTimeDb(mResponseEntity.carBodyStyleList.nullSafety(), true)
            //myDatabase?.dbDAO?.deleteDatasetCarBodyStyleListModel()
        }
        if (mTimingDatabaseList.timeList?.violationList != null && mTimeResponse.data?.get(0)?.violationList != null &&
            mTimingDatabaseList.timeList?.violationList?.name != mTimeResponse.data?.get(0)?.violationList ||
            mTimingDatabaseList.timeList?.violationList == null &&
            mTimeResponse.data?.get(0)?.violationList != null
        ) {
            //        if(true){
            timeDataList.violationList =
                UpdateTimeDb(mResponseEntity.violationList.nullSafety(), true)
            //myDatabase?.dbDAO?.deleteDatasetViolationListModel()
        }
        if (mTimingDatabaseList.timeList?.holidayCalendarList != null && mTimeResponse.data?.get(0)?.holidayCalendarList != null &&
            mTimingDatabaseList.timeList?.holidayCalendarList?.name != mTimeResponse.data?.get(0)?.holidayCalendarList ||
            mTimingDatabaseList.timeList?.holidayCalendarList == null &&
            mTimeResponse.data?.get(0)?.holidayCalendarList != null
        ) {
            //        if(true){
            timeDataList.holidayCalendarList =
                UpdateTimeDb(mResponseEntity.holidayCalendarList.nullSafety(), true)
        }

        if (mTimingDatabaseList.timeList?.sideList != null && mTimeResponse.data?.get(0)?.sideList != null &&
            mTimingDatabaseList.timeList?.sideList?.name != mTimeResponse.data?.get(0)?.sideList ||
            mTimingDatabaseList.timeList?.sideList == null &&
            mTimeResponse.data?.get(0)?.sideList != null
        ) {
            timeDataList.sideList = UpdateTimeDb(mResponseEntity.sideList.nullSafety(), true)
            //myDatabase?.dbDAO?.deleteDatasetSideListModel()
        }
//        timeDataList.sideList = UpdateTimeDb(mResponseEntity.sideList.nullSafety(), true)
        if (mTimingDatabaseList.timeList?.tierStemList != null && mTimeResponse.data?.get(0)?.tierStemList != null &&
            mTimingDatabaseList.timeList?.tierStemList?.name != mTimeResponse.data?.get(0)?.tierStemList ||
            mTimingDatabaseList.timeList?.tierStemList == null &&
            mTimeResponse.data?.get(0)?.tierStemList != null
        ) {
            timeDataList.tierStemList =
                UpdateTimeDb(mResponseEntity.tierStemList.nullSafety(), true)
            //myDatabase?.dbDAO?.deleteDatasetTierStemListModel()
        }
        if (mTimingDatabaseList.timeList?.notesList != null && mTimeResponse.data?.get(0)?.notesList != null &&
            mTimingDatabaseList.timeList?.notesList?.name != mTimeResponse.data?.get(0)?.notesList ||
            mTimingDatabaseList.timeList?.notesList == null &&
            mTimeResponse.data?.get(0)?.notesList != null
        ) {
            timeDataList.notesList = UpdateTimeDb(mResponseEntity.notesList.nullSafety(), true)
            //myDatabase?.dbDAO?.deleteDatasetNotesListModel()
        }
        if (mTimingDatabaseList.timeList?.remarksList != null && mTimeResponse.data?.get(0)?.remarksList != null &&
            mTimingDatabaseList.timeList?.remarksList?.name != mTimeResponse.data?.get(0)?.remarksList ||
            mTimingDatabaseList.timeList?.remarksList == null &&
            mTimeResponse.data?.get(0)?.remarksList != null
        ) {
            timeDataList.remarksList = UpdateTimeDb(mResponseEntity.remarksList.nullSafety(), true)
            //myDatabase?.dbDAO?.deleteDatasetRemarksListModel()
        }
        if (mTimingDatabaseList.timeList?.regulationTimeList != null && mTimeResponse.data?.get(0)?.regulationTimeList != null &&
            mTimingDatabaseList.timeList?.regulationTimeList?.name != mTimeResponse.data?.get(0)?.regulationTimeList ||
            mTimingDatabaseList.timeList?.regulationTimeList == null &&
            mTimeResponse.data?.get(0)?.regulationTimeList != null
        ) {
            timeDataList.regulationTimeList =
                UpdateTimeDb(mResponseEntity.regulationTimeList.nullSafety(), true)
            //myDatabase?.dbDAO?.deleteDatasetRegulationTimeListModel()
        }
        if (mTimingDatabaseList.timeList?.lotList != null && mTimeResponse.data?.get(0)?.lotList != null &&
            mTimingDatabaseList.timeList?.lotList?.name != mTimeResponse.data?.get(0)?.lotList ||
            mTimingDatabaseList.timeList?.lotList == null &&
            mTimeResponse.data?.get(0)?.lotList != null
        ) {
            timeDataList.lotList = UpdateTimeDb(mResponseEntity.lotList.nullSafety(), true)
            //myDatabase?.dbDAO?.deleteDatasetLotListModel()
        }
        if (mTimingDatabaseList.timeList?.cancelReasonList != null && mTimeResponse.data?.get(0)?.cancelReasonList != null &&
            mTimingDatabaseList.timeList?.cancelReasonList?.name != mTimeResponse.data?.get(0)?.cancelReasonList ||
            mTimingDatabaseList.timeList?.cancelReasonList == null &&
            mTimeResponse.data?.get(0)?.cancelReasonList != null
        ) {
            timeDataList.cancelReasonList =
                UpdateTimeDb(mResponseEntity.cancelReasonList.nullSafety(), true)
            //myDatabase?.dbDAO?.deleteDatasetCancelReasonListModel()
        }
        if (mTimingDatabaseList.timeList?.citationData != null && mTimeResponse.data?.get(0)?.citationData != null &&
            mTimingDatabaseList.timeList?.citationData?.name != mTimeResponse.data?.get(0)?.citationData ||
            mTimingDatabaseList.timeList?.citationData == null &&
            mTimeResponse.data?.get(0)?.citationData != null
        ) {
            timeDataList.citationData =
                UpdateTimeDb(mResponseEntity.citationData.nullSafety(), true)
        }
        if (mTimingDatabaseList.timeList?.citationLayout != null && mTimeResponse.data?.get(0)?.citationLayout != null &&
            mTimingDatabaseList.timeList?.citationLayout?.name != mTimeResponse.data?.get(0)?.citationLayout ||
            mTimingDatabaseList.timeList?.citationLayout == null &&
            mTimeResponse.data?.get(0)?.citationLayout != null
        ) {
            timeDataList.citationLayout =
                UpdateTimeDb(mResponseEntity.citationLayout.nullSafety(), true)
        }
        if (mTimingDatabaseList.timeList?.directionList != null && mTimeResponse.data?.get(0)?.directionList != null &&
            mTimingDatabaseList.timeList?.directionList?.name != mTimeResponse.data?.get(0)?.directionList ||
            mTimingDatabaseList.timeList?.directionList == null &&
            mTimeResponse.data?.get(0)?.directionList != null
        ) {
            timeDataList.directionList =
                UpdateTimeDb(mResponseEntity.directionList.nullSafety(), true)
        }
        if (mTimingDatabaseList.timeList?.exemptData != null && mTimeResponse.data?.get(0)?.exemptData != null &&
            mTimingDatabaseList.timeList?.exemptData?.name != mTimeResponse.data?.get(0)?.exemptData ||
            mTimingDatabaseList.timeList?.exemptData == null &&
            mTimeResponse.data?.get(0)?.exemptData != null
        ) {
            timeDataList.exemptData = UpdateTimeDb(mResponseEntity.exemptData.nullSafety(), true)
        }
        if (mTimingDatabaseList.timeList?.makeModelColorData != null && mTimeResponse.data?.get(0)?.makeModelColorData != null &&
            mTimingDatabaseList.timeList?.makeModelColorData?.name != mTimeResponse.data?.get(0)?.makeModelColorData
        ) {
            timeDataList.makeModelColorData =
                UpdateTimeDb(mResponseEntity.makeModelColorData.nullSafety(), true)
        }
        if (mTimingDatabaseList.timeList?.permitData != null && mTimeResponse.data?.get(0)?.permitData != null &&
            mTimingDatabaseList.timeList?.permitData?.name != mTimeResponse.data?.get(0)?.permitData ||
            mTimingDatabaseList.timeList?.permitData == null &&
            mTimeResponse.data?.get(0)?.permitData != null
        ) {
            timeDataList.permitData = UpdateTimeDb(mResponseEntity.permitData.nullSafety(), true)
        }
        if (mTimingDatabaseList.timeList?.timingData != null && mTimeResponse.data?.get(0)?.timingData != null &&
            mTimingDatabaseList.timeList?.timingData?.name != mTimeResponse.data?.get(0)?.timingData ||
            mTimingDatabaseList.timeList?.timingData == null &&
            mTimeResponse.data?.get(0)?.timingData != null
        ) {
            timeDataList.timingData = UpdateTimeDb(mResponseEntity.timingData.nullSafety(), true)
        }
        if (mTimingDatabaseList.timeList?.timingRecordLayout != null && mTimeResponse.data?.get(0)?.timingRecordLayout != null &&
            mTimingDatabaseList.timeList?.timingRecordLayout?.name != mTimeResponse.data?.get(0)?.timingRecordLayout ||
            mTimingDatabaseList.timeList?.timingRecordLayout == null &&
            mTimeResponse.data?.get(0)?.timingRecordLayout != null
        ) {
            timeDataList.timingRecordLayout =
                UpdateTimeDb(mResponseEntity.timingRecordLayout.nullSafety(), true)
        }
        if (mTimingDatabaseList.timeList?.vehiclePlateTypeList != null && mTimeResponse.data?.get(0)?.vehiclePlateTypeList != null &&
            mTimingDatabaseList.timeList?.vehiclePlateTypeList?.name != mTimeResponse.data?.get(0)?.vehiclePlateTypeList ||
            mTimingDatabaseList.timeList?.vehiclePlateTypeList == null &&
            mTimeResponse.data?.get(0)?.vehiclePlateTypeList != null
        ) {
            timeDataList.vehiclePlateTypeList =
                UpdateTimeDb(mResponseEntity.vehiclePlateTypeList.nullSafety(), true)
        }
        if (mTimingDatabaseList.timeList?.mCityZoneList != null && mTimeResponse.data?.get(0)
                ?.mCityZoneList != null &&
            mTimingDatabaseList.timeList?.mCityZoneList?.name != mTimeResponse.data?.get(0)
                ?.mCityZoneList ||
            mTimingDatabaseList.timeList?.mCityZoneList == null &&
            mTimeResponse.data?.get(0)?.mCityZoneList != null
        ) {
            timeDataList.mCityZoneList =
                UpdateTimeDb(
                    mResponseEntity.mCityZoneList.nullSafety(), true
                )
            //myDatabase?.dbDAO?.deleteDatasetPBCZoneListModel()
        }
        if (mTimingDatabaseList.timeList?.mVoidAndReissueList != null && mTimeResponse.data?.get(
                0
            )?.mVoidAndReissueList != null &&
            mTimingDatabaseList.timeList?.mVoidAndReissueList?.name != mTimeResponse.data?.get(
                0
            )?.mVoidAndReissueList ||
            mTimingDatabaseList.timeList?.mVoidAndReissueList == null &&
            mTimeResponse.data?.get(0)?.mVoidAndReissueList != null
        ) {
            timeDataList.mVoidAndReissueList =
                UpdateTimeDb(
                    mResponseEntity.mVoidAndReissueList.nullSafety(),
                    true
                )

            //myDatabase?.dbDAO?.deleteDatasetVoidAndReissueReasonListModel()
        }
        if (mTimingDatabaseList.timeList?.mDeviceList != null && mTimeResponse.data?.get(0)
                ?.mDeviceList != null &&
            mTimingDatabaseList.timeList?.mDeviceList?.name != mTimeResponse.data?.get(0)
                ?.mDeviceList ||
            mTimingDatabaseList.timeList?.mDeviceList == null &&
            mTimeResponse.data?.get(0)?.mDeviceList != null
        ) {
            timeDataList.mDeviceList =
                UpdateTimeDb(
                    mResponseEntity.mDeviceList.nullSafety(),
                    true
                )
        }
        if (mTimingDatabaseList.timeList?.mEquipmentList != null && mTimeResponse.data?.get(0)
                ?.mEquipmentList != null &&
            mTimingDatabaseList.timeList?.mEquipmentList?.name != mTimeResponse.data?.get(0)
                ?.mEquipmentList
        ) {
            timeDataList.mEquipmentList = UpdateTimeDb(mResponseEntity.mEquipmentList.nullSafety(), true)
        }
        if (mTimingDatabaseList.timeList?.mBlockList != null && mTimeResponse.data?.get(0)
                ?.mBlockList != null &&
            mTimingDatabaseList.timeList?.mBlockList?.name != mTimeResponse.data?.get(0)
                ?.mBlockList ||
            mTimingDatabaseList.timeList?.mBlockList == null &&
            mTimeResponse.data?.get(0)?.mBlockList != null
        ) {
            timeDataList.mBlockList =
                UpdateTimeDb(
                    mResponseEntity.mBlockList.nullSafety(),
                    true
                )

            //myDatabase?.dbDAO?.deleteDatasetBlockListModel()
        }

        if (mTimingDatabaseList.timeList?.mSpaceList != null && mTimeResponse.data?.get(0)?.mSpaceList != null &&
            !mTimingDatabaseList.timeList?.mSpaceList?.name
                .equals(mTimeResponse.data?.get(0)?.mSpaceList) ||
            mTimingDatabaseList.timeList?.mSpaceList == null &&
            mTimeResponse.data?.get(0)?.mSpaceList != null
        ) {
            timeDataList.mSpaceList = UpdateTimeDb(mResponseEntity.mSpaceList, true)
            //myDatabase?.dbDAO?.deleteDatasetSpaceListModel()
        }

        if (mTimingDatabaseList.timeList?.mSquadList != null && mTimeResponse.data?.get(0)?.mSquadList != null &&
            !mTimingDatabaseList.timeList?.mSquadList?.name.equals(mTimeResponse.data?.get(0)?.mSquadList) ||
            mTimingDatabaseList.timeList?.mSquadList == null &&
            mTimeResponse.data?.get(0)?.mSquadList != null
        ) {
            timeDataList.mSquadList = UpdateTimeDb(mResponseEntity.mSquadList, true)
        }

        if (LogUtil.isMunicipalCitationEnabled()){
            if (mTimingDatabaseList.timeList?.municipalViolationList != null && mTimeResponse.data?.get(0)?.municipalViolationList != null &&
                mTimingDatabaseList.timeList?.municipalViolationList?.name != mTimeResponse.data?.get(0)?.municipalViolationList ||
                mTimingDatabaseList.timeList?.municipalViolationList == null &&
                mTimeResponse.data?.get(0)?.municipalViolationList != null
            ) {
                //        if(true){
                timeDataList.municipalViolationList =
                    UpdateTimeDb(mResponseEntity.municipalViolationList.nullSafety(), true)
                //myDatabase?.dbDAO?.deleteDatasetMunicipalViolationListModel()
            }

            if (mTimingDatabaseList.timeList?.municipalBlockList != null && mTimeResponse.data?.get(0)?.municipalBlockList != null &&
                mTimingDatabaseList.timeList?.municipalBlockList?.name != mTimeResponse.data?.get(0)?.municipalBlockList ||
                mTimingDatabaseList.timeList?.municipalBlockList == null &&
                mTimeResponse.data?.get(0)?.municipalBlockList != null
            ) {
                //        if(true){
                timeDataList.municipalBlockList =
                    UpdateTimeDb(mResponseEntity.municipalBlockList.nullSafety(), true)
                //myDatabase?.dbDAO?.deleteDatasetMunicipalBlockListModel()
            }

            if (mTimingDatabaseList.timeList?.municipalStreetList != null && mTimeResponse.data?.get(0)?.municipalStreetList != null &&
                mTimingDatabaseList.timeList?.municipalStreetList?.name != mTimeResponse.data?.get(0)?.municipalStreetList ||
                mTimingDatabaseList.timeList?.municipalStreetList == null &&
                mTimeResponse.data?.get(0)?.municipalStreetList != null
            ) {
                //        if(true){
                timeDataList.municipalStreetList =
                    UpdateTimeDb(mResponseEntity.municipalStreetList.nullSafety(), true)
                //myDatabase?.dbDAO?.deleteDatasetMunicipalStreetListModel()
            }

            if (mTimingDatabaseList.timeList?.municipalCityList != null && mTimeResponse.data?.get(0)?.municipalCityList != null &&
                mTimingDatabaseList.timeList?.municipalCityList?.name != mTimeResponse.data?.get(0)?.municipalCityList ||
                mTimingDatabaseList.timeList?.municipalCityList == null &&
                mTimeResponse.data?.get(0)?.municipalCityList != null
            ) {
                //        if(true){
                timeDataList.municipalCityList =
                    UpdateTimeDb(mResponseEntity.municipalCityList.nullSafety(), true)
                //myDatabase?.dbDAO?.deleteDatasetMunicipalCityListModel()
            }

            if (mTimingDatabaseList.timeList?.municipalStateList != null && mTimeResponse.data?.get(0)?.municipalStateList != null &&
                mTimingDatabaseList.timeList?.municipalStateList?.name != mTimeResponse.data?.get(0)?.municipalStateList ||
                mTimingDatabaseList.timeList?.municipalStateList == null &&
                mTimeResponse.data?.get(0)?.municipalStateList != null
            ) {
                //        if(true){
                timeDataList.municipalStateList =
                    UpdateTimeDb(mResponseEntity.municipalStateList.nullSafety(), true)
                //myDatabase?.dbDAO?.deleteDatasetMunicipalStateListModel()
            }
        }


        return timeDataList
    }

    @JvmStatic
    fun setLastUpdateDataSetStatus(
        mList: TimestampDatatbase?,
        mStatus: Boolean
    ): TimestampDatatbase? {
        if (mList?.timeList != null) {
            if (mList.timeList?.cancelReasonList != null) {
                mList.timeList?.cancelReasonList?.status = mStatus
            }
            if (mList.timeList?.decalYearList != null) {
                mList.timeList?.decalYearList?.status = mStatus
            }
            if (mList.timeList?.carMakeList != null) {
                mList.timeList?.carMakeList?.status = mStatus
            }
            if (mList.timeList?.carModelList != null) {
                mList.timeList?.carModelList?.status = mStatus
            }
            if (mList.timeList?.carColorList != null) {
                mList.timeList?.carColorList?.status = mStatus
            }
            if (mList.timeList?.stateList != null) {
                mList.timeList?.stateList?.status = mStatus
            }
            if (mList.timeList?.streetList != null) {
                mList.timeList?.streetList?.status = mStatus
            }
            if (mList.timeList?.meterList != null) {
                mList.timeList?.meterList?.status = mStatus
            }
            if (mList.timeList?.carBodyStyleList != null) {
                mList.timeList?.carBodyStyleList?.status = mStatus
            }
            if (mList.timeList?.violationList != null) {
                mList.timeList?.violationList?.status = mStatus
            }
            if (mList.timeList?.holidayCalendarList != null) {
                mList.timeList?.holidayCalendarList?.status = mStatus
            }
            if (mList.timeList?.sideList != null) {
                mList.timeList?.sideList?.status = mStatus
            }
            if (mList.timeList?.tierStemList != null) {
                mList.timeList?.tierStemList?.status = mStatus
            }
            if (mList.timeList?.notesList != null) {
                mList.timeList?.notesList?.status = mStatus
            }
            if (mList.timeList?.remarksList != null) {
                mList.timeList?.remarksList?.status = mStatus
            }
            if (mList.timeList?.regulationTimeList != null) {
                mList.timeList?.regulationTimeList?.status = mStatus
            }
            if (mList.timeList?.lotList != null) {
                mList.timeList?.lotList?.status = mStatus
            }
            if (mList.timeList?.mVoidAndReissueList != null) {
                mList.timeList?.mVoidAndReissueList?.status = mStatus
            }
            if (mList.timeList?.activityList != null) {
                mList.timeList?.activityList?.status = mStatus
            }
            if (mList.timeList?.commentsList != null) {
                mList.timeList?.commentsList?.status = mStatus
            }
            if (mList.timeList?.supervisorList != null) {
                mList.timeList?.supervisorList?.status = mStatus
            }
            if (mList.timeList?.beatList != null) {
                mList.timeList?.beatList?.status = mStatus
            }
            if (mList.timeList?.zoneList != null) {
                mList.timeList?.zoneList?.status = mStatus
            }
            if (mList.timeList?.radioList != null) {
                mList.timeList?.radioList?.status = mStatus
            }
            if (mList.timeList?.shiftList != null) {
                mList.timeList?.shiftList?.status = mStatus
            }
            if (mList.timeList?.agencyList != null) {
                mList.timeList?.agencyList?.status = mStatus
            }
            if (mList.timeList?.mDeviceList != null) {
                mList.timeList?.mDeviceList?.status = mStatus
            }
            if (mList.timeList?.mCityZoneList != null) {
                mList.timeList?.mCityZoneList?.status = mStatus
            }
            if (mList.timeList?.mEquipmentList != null) {
                mList.timeList?.mEquipmentList?.status = mStatus
            }
            if (mList.timeList?.mBlockList != null) {
                mList.timeList?.mBlockList?.status = mStatus
            }
            if (mList.timeList?.mSpaceList != null) {
                mList.timeList?.mSpaceList?.status = mStatus
            }
            if (mList.timeList?.mSquadList != null) {
                mList.timeList?.mSquadList?.status = mStatus
            }

            if (LogUtil.isMunicipalCitationEnabled()){
                if (mList.timeList?.municipalViolationList != null) {
                    mList.timeList?.municipalViolationList?.status = mStatus
                }
                if (mList.timeList?.municipalBlockList != null) {
                    mList.timeList?.municipalBlockList?.status = mStatus
                }
                if (mList.timeList?.municipalStreetList != null) {
                    mList.timeList?.municipalStreetList?.status = mStatus
                }
                if (mList.timeList?.municipalCityList != null) {
                    mList.timeList?.municipalCityList?.status = mStatus
                }
                if (mList.timeList?.municipalStateList != null) {
                    mList.timeList?.municipalStateList?.status = mStatus
                }
            }

        }
        return mList
    }


    fun officerName(name: String): String {
        var sName = name
        try {
            val sOfficerName = name.trim().split(" ").toTypedArray()
//            if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DUNCAN, true)) {
                sName = sOfficerName[0][0].toString() + " " + sOfficerName[1]
//            }else{
//                sName = sOfficerName[0] + " " + sOfficerName[1]
//            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return sName
    }
    fun officerNameForBurbank(name: String): String {
        var sName = name
        try {
            val sOfficerName = name.trim().split(" ").toTypedArray()
            if (sOfficerName.size>1) {
                sName = sOfficerName[1].uppercase()
            }
            if (sOfficerName.size>2) {
                sName = sOfficerName[1].uppercase()+" "+sOfficerName[2].uppercase()
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return sName
    }

    /**
     * Function used to get officer name based on settings file flag
     * @param officerName: complete officer name
     * @param flag : Flag name which is used to define the officer name format we needed
     */
    fun getOfficerNameBasedOnSettingsFlag(officerName: String, flag: String): String {
        try {
            val sOfficerNameArray = officerName.trim().split(" ").toTypedArray()
            if (sOfficerNameArray.size == 1) {
                return when (flag) {
                    SETTINGS_FLAG_VALUE_OFFICER_NAME_FIRST_INITIAL_LAST_FULL -> {
                        "${sOfficerNameArray.first().uppercase().getInitials()}"
                    }

                    SETTINGS_FLAG_VALUE_OFFICER_NAME_FIRST_FULL_LAST_INITIAL -> {
                        sOfficerNameArray.first().uppercase()
                    }

                    else -> {
                        sOfficerNameArray.first().uppercase()
                    }
                }

            } else if (sOfficerNameArray.size > 1) {
                when (flag) {
                    SETTINGS_FLAG_VALUE_OFFICER_NAME_FIRST_INITIAL_LAST_FULL -> {
                        return "${sOfficerNameArray.first().uppercase().getInitials()} ${
                            sOfficerNameArray.last().uppercase()
                        }"
                    }

                    SETTINGS_FLAG_VALUE_OFFICER_NAME_FIRST_FULL_LAST_INITIAL -> {
                        return "${sOfficerNameArray.first().uppercase()} ${
                            sOfficerNameArray.last().uppercase().getInitials()
                        }"
                    }

                    SETTINGS_FLAG_VALUE_OFFICER_NAME_FIRST_FULL_LAST_FULL -> {
                        return "${sOfficerNameArray.first().uppercase()} ${
                            sOfficerNameArray.last().uppercase()
                        }"
                    }

                    SETTINGS_FLAG_VALUE_OFFICER_NAME_FIRST_INITIAL_LAST_INITIAL -> {
                        return "${sOfficerNameArray.first().uppercase().getInitials()} ${
                            sOfficerNameArray.last().uppercase().getInitials()
                        }"
                    }

                    SETTINGS_FLAG_VALUE_OFFICER_NAME_FIRST_FULL_ONLY -> {
                        return sOfficerNameArray.first().uppercase()
                    }

                    SETTINGS_FLAG_VALUE_OFFICER_NAME_FIRST_NAME_BLANK_LAST_NAME_FULL -> {
                        return sOfficerNameArray.last().uppercase()
                    }

                    else -> {
                        return "${sOfficerNameArray.first().uppercase().getInitials()} ${
                            sOfficerNameArray.last().uppercase()
                        }"
                    }
                }
            } else {
                return officerName
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            return officerName
        }
    }

    fun isVinHaveIOQ(vinString: String): Boolean {
        val valueI = 'I'
        val valueO = 'O'
        val valueQ = 'Q'
        val valuei = 'i'
        val valueo = 'o'
        val valueq = 'q'
        var equal = false
        for (i in 0 until vinString.length) {
            val compareI = Character.compare(valueI, vinString[i])
            val compareO = Character.compare(valueO, vinString[i])
            val compareQ = Character.compare(valueQ, vinString[i])
            val comparei = Character.compare(valuei, vinString[i])
            val compareo = Character.compare(valueo, vinString[i])
            val compareq = Character.compare(valueq, vinString[i])
            if (compareI == 0 || compareO == 0 || compareQ == 0 ||comparei == 0 || compareo == 0 || compareq == 0) {
                equal = true
                break
            }
        }
        return equal
    }

    /**
     * Function used to get index of lot value from dataset list
     */
     fun getIndexOfLot(list: List<DatasetResponse>, name: String): Int {
         for ((pos, myObj) in list.withIndex()) {
            if (name.equals(myObj.lot, ignoreCase = true)) return pos
         }
        return -1
    }

    /**
     * Function used to get index of space value from dataset list
     */
     fun getIndexOfSpaceName(list: List<DatasetResponse>, name: String): Int {
         for ((pos, myObj) in list.withIndex()) {
            if (name.equals(myObj.spaceName, ignoreCase = true)) return pos
         }
        return -1
    }
}