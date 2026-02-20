package com.parkloyalty.lpr.scan.database

import com.parkloyalty.lpr.scan.basecontrol.BaseApplication
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DatasetBlockListModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DatasetCancelReasonListModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DatasetCarBodyStyleListModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DatasetCarColorListModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DatasetCarMakeListModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DatasetDecalYearListModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DatasetHolidayCalendarList
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DatasetLotListModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DatasetMeterListModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DatasetMunicipalBlockListModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DatasetMunicipalCityListModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DatasetMunicipalStateListModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DatasetMunicipalStreetListModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DatasetMunicipalViolationListModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DatasetNotesListModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DatasetPBCZoneListModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DatasetRegulationTimeListModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DatasetRemarksListModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DatasetResponse
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DatasetSettingsListModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DatasetSideListModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DatasetSpaceListModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DatasetStateListModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DatasetStreetListModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DatasetTierStemListModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DatasetVioListModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DatasetViolationListModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DatasetVoidAndReissueReasonListModel
import com.parkloyalty.lpr.scan.ui.login.model.WelcomeListDatatbase
import com.parkloyalty.lpr.scan.util.API_CONSTANT_TYPE_OF_HITS_ALL
import com.parkloyalty.lpr.scan.util.API_CONSTANT_TYPE_OF_HITS_PERMIT
import com.parkloyalty.lpr.scan.util.API_CONSTANT_TYPE_OF_HITS_SCOFFLAW
import com.parkloyalty.lpr.scan.util.API_CONSTANT_TYPE_OF_HITS_TIMING
import com.parkloyalty.lpr.scan.util.DATASET_BLOCK_LIST
import com.parkloyalty.lpr.scan.util.DATASET_CANCEL_REASON_LIST
import com.parkloyalty.lpr.scan.util.DATASET_CAR_BODY_STYLE_LIST
import com.parkloyalty.lpr.scan.util.DATASET_CAR_COLOR_LIST
import com.parkloyalty.lpr.scan.util.DATASET_CAR_MAKE_LIST
import com.parkloyalty.lpr.scan.util.DATASET_CAR_MODEL_LIST
import com.parkloyalty.lpr.scan.util.DATASET_DECAL_YEAR_LIST
import com.parkloyalty.lpr.scan.util.DATASET_HOLIDAY_CALENDAR_LIST
import com.parkloyalty.lpr.scan.util.DATASET_LOT_LIST
import com.parkloyalty.lpr.scan.util.DATASET_METER_LIST
import com.parkloyalty.lpr.scan.util.DATASET_MUNICIPAL_BLOCK_LIST
import com.parkloyalty.lpr.scan.util.DATASET_MUNICIPAL_CITY_LIST
import com.parkloyalty.lpr.scan.util.DATASET_MUNICIPAL_STATE_LIST
import com.parkloyalty.lpr.scan.util.DATASET_MUNICIPAL_STREET_LIST
import com.parkloyalty.lpr.scan.util.DATASET_MUNICIPAL_VIOLATION_LIST
import com.parkloyalty.lpr.scan.util.DATASET_NOTES_LIST
import com.parkloyalty.lpr.scan.util.DATASET_PBC_ZONE_LIST
import com.parkloyalty.lpr.scan.util.DATASET_REGULATION_TIME_LIST
import com.parkloyalty.lpr.scan.util.DATASET_REMARKS_LIST
import com.parkloyalty.lpr.scan.util.DATASET_SETTINGS_LIST
import com.parkloyalty.lpr.scan.util.DATASET_SIDE_LIST
import com.parkloyalty.lpr.scan.util.DATASET_SPACE_LIST
import com.parkloyalty.lpr.scan.util.DATASET_STATE_LIST
import com.parkloyalty.lpr.scan.util.DATASET_STREET_LIST
import com.parkloyalty.lpr.scan.util.DATASET_TIER_STEM_LIST
import com.parkloyalty.lpr.scan.util.DATASET_VIOLATION_LIST
import com.parkloyalty.lpr.scan.util.DATASET_VIO_LIST
import com.parkloyalty.lpr.scan.util.DATASET_VOID_AND_REISSUE_REASON_LIST


object Singleton {
    var mWelcomeListDataSet: WelcomeListDatatbase? = null
    var mDb: AppDatabase? = null

    var datasetDecalYearListModel: DatasetDecalYearListModel? = null
    var datasetCarMakeListModel: DatasetCarMakeListModel? = null
    var datasetCarColorListModel: DatasetCarColorListModel? = null
    var datasetStateListModel: DatasetStateListModel? = null
    var datasetBlockListModel: DatasetBlockListModel? = null
    var datasetStreetListModel: DatasetStreetListModel? = null
    var datasetMeterListModel: DatasetMeterListModel? = null
    var datasetSpaceListModel: DatasetSpaceListModel? = null
    var datasetCarBodyStyleListModel: DatasetCarBodyStyleListModel? = null
    var datasetViolationListModel: DatasetViolationListModel? = null
    var datasetVioListModel: DatasetVioListModel? = null
    var datasetHolidayCalendarListModel: DatasetHolidayCalendarList? = null
    var datasetSideListModel: DatasetSideListModel? = null
    var datasetTierStemListModel: DatasetTierStemListModel? = null
    var datasetNotesListModel: DatasetNotesListModel? = null
    var datasetRemarksListModel: DatasetRemarksListModel? = null
    var datasetLotListModel: DatasetLotListModel? = null
    var datasetRegulationTimeListModel: DatasetRegulationTimeListModel? = null
    var datasetSettingsListModel: DatasetSettingsListModel? = null
    var datasetCancelReasonListModel: DatasetCancelReasonListModel? = null
    var datasetPBCZoneListModel: DatasetPBCZoneListModel? = null
    var datasetVoidAndReissueReasonListModel: DatasetVoidAndReissueReasonListModel? = null
    var datasetMunicipalViolationListModel: DatasetMunicipalViolationListModel? = null
    var datasetMunicipalBlockListModel: DatasetMunicipalBlockListModel? = null
    var datasetMunicipalStreetListModel: DatasetMunicipalStreetListModel? = null
    var datasetMunicipalCityListModel: DatasetMunicipalCityListModel? = null
    var datasetMunicipalStateListModel: DatasetMunicipalStateListModel? = null


    fun getDataSetList(dataSetType: String, mDbOldNotUsed: AppDatabase? = null): List<DatasetResponse>? {
        if (mDb == null) {
            mDb = BaseApplication.instance?.getAppDatabase()
        }

        try {
            if (dataSetType.equals(DATASET_CAR_MAKE_LIST)) {
                if (datasetCarMakeListModel == null || datasetCarMakeListModel?.carMakeList.isNullOrEmpty()) {
                    datasetCarMakeListModel = mDb?.dbDAO?.getDatasetCarMakeListModel()
                }

                return datasetCarMakeListModel?.carMakeList
            } else if (dataSetType.equals(DATASET_CAR_MODEL_LIST)) {
                if (datasetCarMakeListModel == null || datasetCarMakeListModel?.carMakeList.isNullOrEmpty()) {
                    datasetCarMakeListModel = mDb?.dbDAO?.getDatasetCarMakeListModel()
                }

                return datasetCarMakeListModel?.carMakeList
            } else if (dataSetType.equals(DATASET_CAR_COLOR_LIST)) {
                if (datasetCarColorListModel == null || datasetCarColorListModel?.carColorList.isNullOrEmpty()) {
                    datasetCarColorListModel = mDb?.dbDAO?.getDatasetCarColorListModel();
                }

                return datasetCarColorListModel?.carColorList
            } else if (dataSetType.equals(DATASET_BLOCK_LIST)) {
                if (datasetBlockListModel == null || datasetBlockListModel?.blockList.isNullOrEmpty()) {
                    datasetBlockListModel = mDb?.dbDAO?.getDatasetBlockListModel();
                }

                return datasetBlockListModel?.blockList
            } else if (dataSetType.equals(DATASET_TIER_STEM_LIST)) {
                if (datasetTierStemListModel == null || datasetTierStemListModel?.tierStemList.isNullOrEmpty()) {
                    datasetTierStemListModel = mDb?.dbDAO?.getDatasetTierStemListModel();
                }

                return datasetTierStemListModel?.tierStemList
            } else if (dataSetType.equals(DATASET_REMARKS_LIST)) {
                if (datasetRemarksListModel == null || datasetRemarksListModel?.remarksList.isNullOrEmpty()) {
                    datasetRemarksListModel = mDb?.dbDAO?.getDatasetRemarksListModel();
                }

                return datasetRemarksListModel?.remarksList
            } else if (dataSetType.equals(DATASET_LOT_LIST)) {
                if (datasetLotListModel == null || datasetLotListModel?.lotList.isNullOrEmpty()) {
                    datasetLotListModel = mDb?.dbDAO?.getDatasetLotListModel();
                }

                return datasetLotListModel?.lotList
            } else if (dataSetType.equals(DATASET_STREET_LIST)) {
                if (datasetStreetListModel == null || datasetStreetListModel?.streetList.isNullOrEmpty()) {
                    datasetStreetListModel = mDb?.dbDAO?.getDatasetStreetListModel();
                }


                return datasetStreetListModel?.streetList
            } else if (dataSetType.equals(DATASET_REGULATION_TIME_LIST)) {
                if (datasetRegulationTimeListModel == null || datasetRegulationTimeListModel?.regulationTimeList.isNullOrEmpty()) {
                    datasetRegulationTimeListModel =
                        mDb?.dbDAO?.getDatasetRegulationTimeListModel();
                }

                return datasetRegulationTimeListModel?.regulationTimeList
            } else if (dataSetType.equals(DATASET_SIDE_LIST)) {
                if (datasetSideListModel == null || datasetSideListModel?.sideList.isNullOrEmpty()) {
                    datasetSideListModel = mDb?.dbDAO?.getDatasetSideListModel();
                }


                return datasetSideListModel?.sideList
            } else if (dataSetType.equals(DATASET_STATE_LIST)) {
                if (datasetStateListModel == null || datasetStateListModel?.stateList.isNullOrEmpty()) {
                    datasetStateListModel = mDb?.dbDAO?.getDatasetStateListModel();
                }

                return datasetStateListModel?.stateList
            } else if (dataSetType.equals(DATASET_METER_LIST)) {

                if (datasetMeterListModel == null || datasetMeterListModel?.meterList.isNullOrEmpty()) {
                    datasetMeterListModel = mDb?.dbDAO?.getDatasetMeterListModel();
                }

                return datasetMeterListModel?.meterList
            } else if (dataSetType.equals(DATASET_SETTINGS_LIST)) {
//                if (datasetSettingsListModel == null || datasetSettingsListModel?.settingsList.isNullOrEmpty()) {
//                    datasetSettingsListModel = mDb?.dbDAO?.getDatasetSettingsListModel()
//                }
                datasetSettingsListModel = mDb?.dbDAO?.getDatasetSettingsListModel()
                return datasetSettingsListModel?.settingsList
            } else if (dataSetType.equals(DATASET_CANCEL_REASON_LIST)) {
                if (datasetCancelReasonListModel == null || datasetCancelReasonListModel?.cancelReasonList.isNullOrEmpty()) {
                    datasetCancelReasonListModel = mDb?.dbDAO?.getDatasetCancelReasonListModel();
                }

                return datasetCancelReasonListModel?.cancelReasonList
            } else if (dataSetType.equals(DATASET_DECAL_YEAR_LIST)) {
                if (datasetDecalYearListModel == null || datasetDecalYearListModel?.decalYearList.isNullOrEmpty()) {
                    datasetDecalYearListModel = mDb?.dbDAO?.getDatasetDecalYearModel();
                }


                return datasetDecalYearListModel?.decalYearList
            } else if (dataSetType.equals(DATASET_CAR_BODY_STYLE_LIST)) {
                if (datasetCarBodyStyleListModel == null || datasetCarBodyStyleListModel?.carBodyStyleList.isNullOrEmpty()) {
                    datasetCarBodyStyleListModel = mDb?.dbDAO?.getDatasetCarBodyStyleListModel();
                }

                return datasetCarBodyStyleListModel?.carBodyStyleList
            } else if (dataSetType.equals(DATASET_SPACE_LIST)) {
                if (datasetSpaceListModel == null || datasetSpaceListModel?.spaceList.isNullOrEmpty()) {
                    datasetSpaceListModel = mDb?.dbDAO?.getDatasetSpaceListModel();
                }

                return datasetSpaceListModel?.spaceList;
            } else if (dataSetType.equals(DATASET_VIOLATION_LIST)) {
//                if (datasetViolationListModel == null || datasetViolationListModel?.violationList.isNullOrEmpty()) {
//                    datasetViolationListModel = mDb?.dbDAO?.getDatasetViolationListModel()
//                }
                datasetViolationListModel = mDb?.dbDAO?.getDatasetViolationListModel()
                return datasetViolationListModel?.violationList
            } else if (dataSetType.equals(DATASET_VIO_LIST)) {
                datasetVioListModel = mDb?.dbDAO?.getDatasetVioListModel()
                return datasetVioListModel?.vioList
            }  else if (dataSetType.equals(DATASET_HOLIDAY_CALENDAR_LIST)) {
                datasetHolidayCalendarListModel = mDb?.dbDAO?.getDatasetHolidayCalendarListModel()
                return datasetHolidayCalendarListModel?.holidayCalendatList
            } else if (dataSetType.equals(DATASET_NOTES_LIST)) {
                if (datasetNotesListModel == null || datasetNotesListModel?.notesList.isNullOrEmpty()) {
                    datasetNotesListModel = mDb?.dbDAO?.getDatasetNotesListModel();
                }


                return datasetNotesListModel?.notesList
            } else if (dataSetType.equals(DATASET_PBC_ZONE_LIST)) {
                if (datasetPBCZoneListModel == null || datasetPBCZoneListModel?.pbcZoneList.isNullOrEmpty()) {
                    datasetPBCZoneListModel = mDb?.dbDAO?.getDatasetPBCZoneListModel()
                }

                return datasetPBCZoneListModel?.pbcZoneList
            } else if (dataSetType.equals(DATASET_VOID_AND_REISSUE_REASON_LIST)) {
                if (datasetVoidAndReissueReasonListModel == null || datasetVoidAndReissueReasonListModel?.voidAndReissueReasonList.isNullOrEmpty()) {
                    datasetVoidAndReissueReasonListModel =
                        mDb?.dbDAO?.getDatasetVoidAndReissueReasonListModel()
                }
                return datasetVoidAndReissueReasonListModel?.voidAndReissueReasonList
            } else if (dataSetType.equals(DATASET_MUNICIPAL_VIOLATION_LIST)) {
//                if (datasetMunicipalViolationListModel == null || datasetMunicipalViolationListModel?.municipalViolationList.isNullOrEmpty()) {
//                    datasetMunicipalViolationListModel =
//                        mDb?.dbDAO?.getDatasetMunicipalViolationListModel()
//                }

                datasetMunicipalViolationListModel =
                    mDb?.dbDAO?.getDatasetMunicipalViolationListModel()
                return datasetMunicipalViolationListModel?.municipalViolationList
            } else if (dataSetType.equals(DATASET_MUNICIPAL_BLOCK_LIST)) {
                if (datasetMunicipalBlockListModel == null || datasetMunicipalBlockListModel?.municipalBlockList.isNullOrEmpty()) {
                    datasetMunicipalBlockListModel = mDb?.dbDAO?.getDatasetMunicipalBlockListModel()
                }

                return datasetMunicipalBlockListModel?.municipalBlockList
            } else if (dataSetType.equals(DATASET_MUNICIPAL_STREET_LIST)) {
                if (datasetMunicipalStreetListModel == null || datasetMunicipalStreetListModel?.municipalStreetList.isNullOrEmpty()) {
                    datasetMunicipalStreetListModel =
                        mDb?.dbDAO?.getDatasetMunicipalStreetListModel()
                }

                return datasetMunicipalStreetListModel?.municipalStreetList
            } else if (dataSetType.equals(DATASET_MUNICIPAL_CITY_LIST)) {
                if (datasetMunicipalCityListModel == null || datasetMunicipalCityListModel?.municipalCityList.isNullOrEmpty()) {
                    datasetMunicipalCityListModel = mDb?.dbDAO?.getDatasetMunicipalCityListModel()
                }

                return datasetMunicipalCityListModel?.municipalCityList
            } else if (dataSetType.equals(DATASET_MUNICIPAL_STATE_LIST)) {
                if (datasetMunicipalStateListModel == null || datasetMunicipalStateListModel?.municipalStateList.isNullOrEmpty()) {
                    datasetMunicipalStateListModel = mDb?.dbDAO?.getDatasetMunicipalStateListModel()
                }

                return datasetMunicipalStateListModel?.municipalStateList
            } else {
                return arrayListOf()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return arrayListOf()
        }
    }

    fun getWelcomeDbObject(mDbOldNotUsed: AppDatabase? = null): WelcomeListDatatbase? {
        if (mDb == null) {
            mDb = BaseApplication.instance?.getAppDatabase()
        }

        if (mWelcomeListDataSet == null) {
            mWelcomeListDataSet = mDb?.dbDAO?.getActivityList()
        }

        return mWelcomeListDataSet
    }

    fun getTypeOfHitsForGenetic(): Array<String> {
        return arrayOf(
            API_CONSTANT_TYPE_OF_HITS_ALL,
            API_CONSTANT_TYPE_OF_HITS_PERMIT,
            API_CONSTANT_TYPE_OF_HITS_TIMING,
            API_CONSTANT_TYPE_OF_HITS_SCOFFLAW
        )
    }

    fun reset() {
        datasetDecalYearListModel = null
        datasetCarMakeListModel = null
        datasetCarColorListModel = null
        datasetStateListModel = null
        datasetBlockListModel = null
        datasetStreetListModel = null
        datasetMeterListModel = null
        datasetSpaceListModel = null
        datasetCarBodyStyleListModel = null
        datasetViolationListModel = null
        datasetSideListModel = null
        datasetTierStemListModel = null
        datasetNotesListModel = null
        datasetRemarksListModel = null
        datasetLotListModel = null
        datasetRegulationTimeListModel = null
        datasetSettingsListModel = null
        datasetCancelReasonListModel = null
        datasetPBCZoneListModel = null
        datasetVoidAndReissueReasonListModel = null
        datasetMunicipalViolationListModel = null
        datasetMunicipalBlockListModel = null
        datasetMunicipalStreetListModel = null
        datasetMunicipalCityListModel = null
        datasetMunicipalStateListModel = null
    }
}