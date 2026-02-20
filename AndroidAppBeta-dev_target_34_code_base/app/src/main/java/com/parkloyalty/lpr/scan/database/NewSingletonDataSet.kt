package com.parkloyalty.lpr.scan.database

import com.parkloyalty.lpr.scan.database.services.CitationDao
import com.parkloyalty.lpr.scan.database.services.DatasetDao
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
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NewSingletonDataSet @Inject constructor(
    private val citationDao: CitationDao, private val datasetDao: DatasetDao
) {
    var mWelcomeListDataSet: WelcomeListDatatbase? = null

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


    suspend fun getDataSetList(dataSetType: String): List<DatasetResponse>? {
        try {
            when (dataSetType) {
                DATASET_CAR_MAKE_LIST -> {
                    if (datasetCarMakeListModel == null || datasetCarMakeListModel?.carMakeList.isNullOrEmpty()) {
                        datasetCarMakeListModel = datasetDao.getDatasetCarMakeListModel()
                    }

                    return datasetCarMakeListModel?.carMakeList
                }

                DATASET_CAR_MODEL_LIST -> {
                    if (datasetCarMakeListModel == null || datasetCarMakeListModel?.carMakeList.isNullOrEmpty()) {
                        datasetCarMakeListModel = datasetDao.getDatasetCarMakeListModel()
                    }

                    return datasetCarMakeListModel?.carMakeList
                }

                DATASET_CAR_COLOR_LIST -> {
                    if (datasetCarColorListModel == null || datasetCarColorListModel?.carColorList.isNullOrEmpty()) {
                        datasetCarColorListModel = datasetDao.getDatasetCarColorListModel()
                    }

                    return datasetCarColorListModel?.carColorList
                }

                DATASET_BLOCK_LIST -> {
                    if (datasetBlockListModel == null || datasetBlockListModel?.blockList.isNullOrEmpty()) {
                        datasetBlockListModel = datasetDao.getDatasetBlockListModel()
                    }

                    return datasetBlockListModel?.blockList
                }

                DATASET_TIER_STEM_LIST -> {
                    if (datasetTierStemListModel == null || datasetTierStemListModel?.tierStemList.isNullOrEmpty()) {
                        datasetTierStemListModel = datasetDao.getDatasetTierStemListModel()
                    }

                    return datasetTierStemListModel?.tierStemList
                }

                DATASET_REMARKS_LIST -> {
                    if (datasetRemarksListModel == null || datasetRemarksListModel?.remarksList.isNullOrEmpty()) {
                        datasetRemarksListModel = datasetDao.getDatasetRemarksListModel()
                    }

                    return datasetRemarksListModel?.remarksList
                }

                DATASET_LOT_LIST -> {
                    if (datasetLotListModel == null || datasetLotListModel?.lotList.isNullOrEmpty()) {
                        datasetLotListModel = datasetDao.getDatasetLotListModel()
                    }

                    return datasetLotListModel?.lotList
                }

                DATASET_STREET_LIST -> {
                    if (datasetStreetListModel == null || datasetStreetListModel?.streetList.isNullOrEmpty()) {
                        datasetStreetListModel = datasetDao.getDatasetStreetListModel()
                    }


                    return datasetStreetListModel?.streetList
                }

                DATASET_REGULATION_TIME_LIST -> {
                    if (datasetRegulationTimeListModel == null || datasetRegulationTimeListModel?.regulationTimeList.isNullOrEmpty()) {
                        datasetRegulationTimeListModel =
                            datasetDao.getDatasetRegulationTimeListModel()
                    }

                    return datasetRegulationTimeListModel?.regulationTimeList
                }

                DATASET_SIDE_LIST -> {
                    if (datasetSideListModel == null || datasetSideListModel?.sideList.isNullOrEmpty()) {
                        datasetSideListModel = datasetDao.getDatasetSideListModel()
                    }


                    return datasetSideListModel?.sideList
                }

                DATASET_STATE_LIST -> {
                    if (datasetStateListModel == null || datasetStateListModel?.stateList.isNullOrEmpty()) {
                        datasetStateListModel = datasetDao.getDatasetStateListModel()
                    }

                    return datasetStateListModel?.stateList
                }

                DATASET_METER_LIST -> {

                    if (datasetMeterListModel == null || datasetMeterListModel?.meterList.isNullOrEmpty()) {
                        datasetMeterListModel = datasetDao.getDatasetMeterListModel()
                    }

                    return datasetMeterListModel?.meterList
                }

                DATASET_SETTINGS_LIST -> {
                    datasetSettingsListModel = datasetDao.getDatasetSettingsListModel()
                    return datasetSettingsListModel?.settingsList
                }

                DATASET_CANCEL_REASON_LIST -> {
                    if (datasetCancelReasonListModel == null || datasetCancelReasonListModel?.cancelReasonList.isNullOrEmpty()) {
                        datasetCancelReasonListModel = datasetDao.getDatasetCancelReasonListModel()
                    }

                    return datasetCancelReasonListModel?.cancelReasonList
                }

                DATASET_DECAL_YEAR_LIST -> {
                    if (datasetDecalYearListModel == null || datasetDecalYearListModel?.decalYearList.isNullOrEmpty()) {
                        datasetDecalYearListModel = datasetDao.getDatasetDecalYearModel()
                    }


                    return datasetDecalYearListModel?.decalYearList
                }

                DATASET_CAR_BODY_STYLE_LIST -> {
                    if (datasetCarBodyStyleListModel == null || datasetCarBodyStyleListModel?.carBodyStyleList.isNullOrEmpty()) {
                        datasetCarBodyStyleListModel = datasetDao.getDatasetCarBodyStyleListModel()
                    }

                    return datasetCarBodyStyleListModel?.carBodyStyleList
                }

                DATASET_SPACE_LIST -> {
                    if (datasetSpaceListModel == null || datasetSpaceListModel?.spaceList.isNullOrEmpty()) {
                        datasetSpaceListModel = datasetDao.getDatasetSpaceListModel()
                    }

                    return datasetSpaceListModel?.spaceList
                }

                DATASET_VIOLATION_LIST -> {
                    datasetViolationListModel = datasetDao.getDatasetViolationListModel()
                    return datasetViolationListModel?.violationList
                }

                DATASET_VIO_LIST -> {
                    datasetVioListModel = datasetDao.getDatasetVioListModel()
                    return datasetVioListModel?.vioList
                }

                DATASET_HOLIDAY_CALENDAR_LIST -> {
                    datasetHolidayCalendarListModel = datasetDao.getDatasetHolidayCalendarListModel()
                    return datasetHolidayCalendarListModel?.holidayCalendatList
                }

                DATASET_NOTES_LIST -> {
                    if (datasetNotesListModel == null || datasetNotesListModel?.notesList.isNullOrEmpty()) {
                        datasetNotesListModel = datasetDao.getDatasetNotesListModel()
                    }

                    return datasetNotesListModel?.notesList
                }

                DATASET_PBC_ZONE_LIST -> {
                    if (datasetPBCZoneListModel == null || datasetPBCZoneListModel?.pbcZoneList.isNullOrEmpty()) {
                        datasetPBCZoneListModel = datasetDao.getDatasetPBCZoneListModel()
                    }

                    return datasetPBCZoneListModel?.pbcZoneList
                }

                DATASET_VOID_AND_REISSUE_REASON_LIST -> {
                    if (datasetVoidAndReissueReasonListModel == null || datasetVoidAndReissueReasonListModel?.voidAndReissueReasonList.isNullOrEmpty()) {
                        datasetVoidAndReissueReasonListModel =
                            datasetDao.getDatasetVoidAndReissueReasonListModel()
                    }
                    return datasetVoidAndReissueReasonListModel?.voidAndReissueReasonList
                }

                DATASET_MUNICIPAL_VIOLATION_LIST -> {
                    datasetMunicipalViolationListModel =
                        datasetDao.getDatasetMunicipalViolationListModel()
                    return datasetMunicipalViolationListModel?.municipalViolationList
                }

                DATASET_MUNICIPAL_BLOCK_LIST -> {
                    if (datasetMunicipalBlockListModel == null || datasetMunicipalBlockListModel?.municipalBlockList.isNullOrEmpty()) {
                        datasetMunicipalBlockListModel =
                            datasetDao.getDatasetMunicipalBlockListModel()
                    }

                    return datasetMunicipalBlockListModel?.municipalBlockList
                }

                DATASET_MUNICIPAL_STREET_LIST -> {
                    if (datasetMunicipalStreetListModel == null || datasetMunicipalStreetListModel?.municipalStreetList.isNullOrEmpty()) {
                        datasetMunicipalStreetListModel =
                            datasetDao.getDatasetMunicipalStreetListModel()
                    }

                    return datasetMunicipalStreetListModel?.municipalStreetList
                }

                DATASET_MUNICIPAL_CITY_LIST -> {
                    if (datasetMunicipalCityListModel == null || datasetMunicipalCityListModel?.municipalCityList.isNullOrEmpty()) {
                        datasetMunicipalCityListModel =
                            datasetDao.getDatasetMunicipalCityListModel()
                    }

                    return datasetMunicipalCityListModel?.municipalCityList
                }

                DATASET_MUNICIPAL_STATE_LIST -> {
                    if (datasetMunicipalStateListModel == null || datasetMunicipalStateListModel?.municipalStateList.isNullOrEmpty()) {
                        datasetMunicipalStateListModel =
                            datasetDao.getDatasetMunicipalStateListModel()
                    }

                    return datasetMunicipalStateListModel?.municipalStateList
                }

                else -> {
                    return arrayListOf()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return arrayListOf()
        }
    }

    suspend fun getWelcomeDbObject(): WelcomeListDatatbase? {
        if (mWelcomeListDataSet == null) {
            mWelcomeListDataSet = citationDao.getActivityList()
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
