package com.parkloyalty.lpr.scan.database.repository

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
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
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DatasetSettingsListModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DatasetSideListModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DatasetSpaceListModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DatasetStateListModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DatasetStreetListModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DatasetTierStemListModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DatasetVioListModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DatasetViolationListModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DatasetVoidAndReissueReasonListModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatasetDaoRepository @Inject constructor(
    private val datasetDao: DatasetDao
) {
    //Holiday Calendar List
    suspend fun insertDatasetHolidayCalendarListModel(datasetHolidayCalendarList: DatasetHolidayCalendarList) =
        datasetDao.insertDatasetHolidayCalendarListModel(datasetHolidayCalendarList)

    suspend fun getDatasetHolidayCalendarListModel(): DatasetHolidayCalendarList? =
        datasetDao.getDatasetHolidayCalendarListModel()

    suspend fun deleteDatasetHolidayCalendarListModel() = datasetDao.deleteDatasetHolidayCalendarListModel()


    suspend fun insertDatasetDecalYearListModel(datasetDecalYearListModel: DatasetDecalYearListModel) =
        datasetDao.insertDatasetDecalYearListModel(datasetDecalYearListModel)

    suspend fun getDatasetDecalYearModel(): DatasetDecalYearListModel? =
        datasetDao.getDatasetDecalYearModel()

    suspend fun deleteDatasetDecalYearListModel() = datasetDao.deleteDatasetDecalYearListModel()

    //Car Make List
    suspend fun insertDatasetCarMakeListModel(datasetCarMakeListModel: DatasetCarMakeListModel) =
        datasetDao.insertDatasetCarMakeListModel(datasetCarMakeListModel)

    suspend fun getDatasetCarMakeListModel(): DatasetCarMakeListModel? =
        datasetDao.getDatasetCarMakeListModel()

    suspend fun deleteDatasetCarMakeListModel() = datasetDao.deleteDatasetCarMakeListModel()

    //Car Color List
    suspend fun insertDatasetCarColorListModel(datasetCarColorListModel: DatasetCarColorListModel) =
        datasetDao.insertDatasetCarColorListModel(datasetCarColorListModel)

    suspend fun getDatasetCarColorListModel(): DatasetCarColorListModel? =
        datasetDao.getDatasetCarColorListModel()

    suspend fun deleteDatasetCarColorListModel() = datasetDao.deleteDatasetCarColorListModel()

    //State List
    suspend fun insertDatasetStateListModel(datasetStateListModel: DatasetStateListModel) =
        datasetDao.insertDatasetStateListModel(datasetStateListModel)

    suspend fun getDatasetStateListModel(): DatasetStateListModel? =
        datasetDao.getDatasetStateListModel()

    suspend fun deleteDatasetStateListModel() = datasetDao.deleteDatasetStateListModel()

    //Block List
    suspend fun insertDatasetBlockListModel(datasetBlockListModel: DatasetBlockListModel) =
        datasetDao.insertDatasetBlockListModel(datasetBlockListModel)

    suspend fun getDatasetBlockListModel(): DatasetBlockListModel? =
        datasetDao.getDatasetBlockListModel()

    suspend fun deleteDatasetBlockListModel() = datasetDao.deleteDatasetBlockListModel()

    //Street List
    suspend fun insertDatasetStreetListModel(datasetStreetListModel: DatasetStreetListModel) =
        datasetDao.insertDatasetStreetListModel(datasetStreetListModel)

    suspend fun getMaxStreetListId(): Int? = datasetDao.getMaxStreetListId()

    suspend fun getDatasetStreetListModel(): DatasetStreetListModel? =
        datasetDao.getDatasetStreetListModel()

    suspend fun deleteDatasetStreetListModel() = datasetDao.deleteDatasetStreetListModel()

    //Meter List
    suspend fun insertDatasetMeterListModel(datasetMeterListModel: DatasetMeterListModel) =
        datasetDao.insertDatasetMeterListModel(datasetMeterListModel)

    suspend fun getDatasetMeterListModel(): DatasetMeterListModel? =
        datasetDao.getDatasetMeterListModel()

    suspend fun deleteDatasetMeterListModel() = datasetDao.deleteDatasetMeterListModel()

    //Space List
    suspend fun insertDatasetSpaceListModel(datasetSpaceListModel: DatasetSpaceListModel) =
        datasetDao.insertDatasetSpaceListModel(datasetSpaceListModel)

    suspend fun getDatasetSpaceListModel(): DatasetSpaceListModel? =
        datasetDao.getDatasetSpaceListModel()

    suspend fun deleteDatasetSpaceListModel() = datasetDao.deleteDatasetSpaceListModel()

    //Car body Style List
    suspend fun insertDatasetCarBodyStyleListModel(datasetCarBodyStyleListModel: DatasetCarBodyStyleListModel) =
        datasetDao.insertDatasetCarBodyStyleListModel(datasetCarBodyStyleListModel)

    suspend fun getDatasetCarBodyStyleListModel(): DatasetCarBodyStyleListModel? =
        datasetDao.getDatasetCarBodyStyleListModel()

    suspend fun deleteDatasetCarBodyStyleListModel() =
        datasetDao.deleteDatasetCarBodyStyleListModel()

    //Violation List
    suspend fun insertDatasetViolationListModel(datasetViolationListModel: DatasetViolationListModel) =
        datasetDao.insertDatasetViolationListModel(datasetViolationListModel)

    suspend fun getDatasetViolationListModel(): DatasetViolationListModel? =
        datasetDao.getDatasetViolationListModel()

    suspend fun deleteDatasetViolationListModel() = datasetDao.deleteDatasetViolationListModel()

    //Vio List
    suspend fun insertDatasetVioListModel(datasetVioListModel: DatasetVioListModel) =
        datasetDao.insertDatasetVioListModel(datasetVioListModel)

    suspend fun getDatasetVioListModel(): DatasetVioListModel? =
        datasetDao.getDatasetVioListModel()

    suspend fun deleteDatasetVioListModel() = datasetDao.deleteDatasetVioListModel()

    //Side List
    suspend fun insertDatasetSideListModel(datasetSideListModel: DatasetSideListModel) =
        datasetDao.insertDatasetSideListModel(datasetSideListModel)

    suspend fun getDatasetSideListModel(): DatasetSideListModel? =
        datasetDao.getDatasetSideListModel()

    suspend fun deleteDatasetSideListModel() = datasetDao.deleteDatasetSideListModel()

    //Tier Stem List
    suspend fun insertDatasetTierStemListModel(datasetSideListModel: DatasetTierStemListModel) =
        datasetDao.insertDatasetTierStemListModel(datasetSideListModel)

    suspend fun getDatasetTierStemListModel(): DatasetTierStemListModel? =
        datasetDao.getDatasetTierStemListModel()

    suspend fun deleteDatasetTierStemListModel() = datasetDao.deleteDatasetTierStemListModel()

    //Notes List
    suspend fun insertDatasetNotesListModel(datasetNotesListModel: DatasetNotesListModel) =
        datasetDao.insertDatasetNotesListModel(datasetNotesListModel)

    suspend fun getDatasetNotesListModel(): DatasetNotesListModel? =
        datasetDao.getDatasetNotesListModel()

    suspend fun deleteDatasetNotesListModel() = datasetDao.deleteDatasetNotesListModel()

    //Remarks List
    suspend fun insertDatasetRemarksListModel(datasetRemarksListModel: DatasetRemarksListModel) =
        datasetDao.insertDatasetRemarksListModel(datasetRemarksListModel)

    suspend fun getDatasetRemarksListModel(): DatasetRemarksListModel? =
        datasetDao.getDatasetRemarksListModel()

    suspend fun deleteDatasetRemarksListModel() = datasetDao.deleteDatasetRemarksListModel()

    //Lot List
    suspend fun insertDatasetLotListModel(datasetLotListModel: DatasetLotListModel) =
        datasetDao.insertDatasetLotListModel(datasetLotListModel)

    suspend fun getDatasetLotListModel(): DatasetLotListModel? = datasetDao.getDatasetLotListModel()

    suspend fun deleteDatasetLotListModel() = datasetDao.deleteDatasetLotListModel()

    //Regulation Time List
    suspend fun insertDatasetRegulationTimeListModel(datasetRegulationTimeListModel: DatasetRegulationTimeListModel) =
        datasetDao.insertDatasetRegulationTimeListModel(datasetRegulationTimeListModel)

    suspend fun getDatasetRegulationTimeListModel(): DatasetRegulationTimeListModel? =
        datasetDao.getDatasetRegulationTimeListModel()

    suspend fun deleteDatasetRegulationTimeListModel() =
        datasetDao.deleteDatasetRegulationTimeListModel()

    //Settings List
    suspend fun insertDatasetSettingsListModel(datasetSettingsListModel: DatasetSettingsListModel) =
        datasetDao.insertDatasetSettingsListModel(datasetSettingsListModel)

    suspend fun getDatasetSettingsListModel(): DatasetSettingsListModel? =
        datasetDao.getDatasetSettingsListModel()

    suspend fun deleteDatasetSettingsListModel() = datasetDao.deleteDatasetSettingsListModel()

    //Cancel Reason List
    suspend fun insertDatasetCancelReasonListModel(datasetCancelReasonListModel: DatasetCancelReasonListModel) =
        datasetDao.insertDatasetCancelReasonListModel(datasetCancelReasonListModel)

    suspend fun getDatasetCancelReasonListModel(): DatasetCancelReasonListModel? =
        datasetDao.getDatasetCancelReasonListModel()

    suspend fun deleteDatasetCancelReasonListModel() =
        datasetDao.deleteDatasetCancelReasonListModel()

    //PBC Zone List
    suspend fun insertDatasetPBCZoneListModel(datasetPBCZoneListModel: DatasetPBCZoneListModel) =
        datasetDao.insertDatasetPBCZoneListModel(datasetPBCZoneListModel)

    suspend fun getDatasetPBCZoneListModel(): DatasetPBCZoneListModel? =
        datasetDao.getDatasetPBCZoneListModel()

    suspend fun deleteDatasetPBCZoneListModel() = datasetDao.deleteDatasetPBCZoneListModel()

    //Void And Reissue Reason List
    suspend fun insertDatasetVoidAndReissueReasonListModel(datasetPBCZoneListModel: DatasetVoidAndReissueReasonListModel) =
        datasetDao.insertDatasetVoidAndReissueReasonListModel(datasetPBCZoneListModel)

    suspend fun getDatasetVoidAndReissueReasonListModel(): DatasetVoidAndReissueReasonListModel? =
        datasetDao.getDatasetVoidAndReissueReasonListModel()

    suspend fun deleteDatasetVoidAndReissueReasonListModel() =
        datasetDao.deleteDatasetVoidAndReissueReasonListModel()

    //Municipal Violation List
    suspend fun insertDatasetMunicipalViolationListModel(datasetMunicipalViolationListModel: DatasetMunicipalViolationListModel) =
        datasetDao.insertDatasetMunicipalViolationListModel(datasetMunicipalViolationListModel)

    suspend fun getDatasetMunicipalViolationListModel(): DatasetMunicipalViolationListModel? =
        datasetDao.getDatasetMunicipalViolationListModel()

    suspend fun deleteDatasetMunicipalViolationListModel() =
        datasetDao.deleteDatasetMunicipalViolationListModel()

    //Municipal Block List
    suspend fun insertDatasetMunicipalBlockListModel(datasetMunicipalBlockListModel: DatasetMunicipalBlockListModel) =
        datasetDao.insertDatasetMunicipalBlockListModel(datasetMunicipalBlockListModel)

    suspend fun getDatasetMunicipalBlockListModel(): DatasetMunicipalBlockListModel? =
        datasetDao.getDatasetMunicipalBlockListModel()

    suspend fun deleteDatasetMunicipalBlockListModel() =
        datasetDao.deleteDatasetMunicipalBlockListModel()

    //Municipal Street List
    suspend fun insertDatasetMunicipalStreetListModel(datasetMunicipalStreetListModel: DatasetMunicipalStreetListModel) =
        datasetDao.insertDatasetMunicipalStreetListModel(datasetMunicipalStreetListModel)

    suspend fun getDatasetMunicipalStreetListModel(): DatasetMunicipalStreetListModel? =
        datasetDao.getDatasetMunicipalStreetListModel()

    suspend fun deleteDatasetMunicipalStreetListModel() =
        datasetDao.deleteDatasetMunicipalStreetListModel()

    //Municipal City List
    suspend fun insertDatasetMunicipalCityListModel(datasetMunicipalCityListModel: DatasetMunicipalCityListModel) =
        datasetDao.insertDatasetMunicipalCityListModel(datasetMunicipalCityListModel)

    suspend fun getDatasetMunicipalCityListModel(): DatasetMunicipalCityListModel? =
        datasetDao.getDatasetMunicipalCityListModel()

    suspend fun deleteDatasetMunicipalCityListModel() =
        datasetDao.deleteDatasetMunicipalCityListModel()

    //Municipal State List
    suspend fun insertDatasetMunicipalStateListModel(datasetMunicipalStateListModel: DatasetMunicipalStateListModel) =
        datasetDao.insertDatasetMunicipalStateListModel(datasetMunicipalStateListModel)

    suspend fun getDatasetMunicipalStateListModel(): DatasetMunicipalStateListModel? =
        datasetDao.getDatasetMunicipalStateListModel()

    suspend fun deleteDatasetMunicipalStateListModel() =
        datasetDao.deleteDatasetMunicipalStateListModel()

    suspend fun deleteAllDataSet() {
        deleteDatasetDecalYearListModel()
        deleteDatasetCarMakeListModel()
        deleteDatasetCarColorListModel()
        deleteDatasetStateListModel()
        deleteDatasetBlockListModel()
        deleteDatasetStreetListModel()
        deleteDatasetMeterListModel()
        deleteDatasetSpaceListModel()
        deleteDatasetCarBodyStyleListModel()
        deleteDatasetViolationListModel()
        deleteDatasetVioListModel()
        deleteDatasetSideListModel()
        deleteDatasetTierStemListModel()
        deleteDatasetNotesListModel()
        deleteDatasetRemarksListModel()
        deleteDatasetLotListModel()
        deleteDatasetRegulationTimeListModel()
        deleteDatasetSettingsListModel()
        deleteDatasetCancelReasonListModel()
        deleteDatasetPBCZoneListModel()
        deleteDatasetVoidAndReissueReasonListModel()
        deleteDatasetMunicipalViolationListModel()
        deleteDatasetMunicipalBlockListModel()
        deleteDatasetMunicipalStreetListModel()
        deleteDatasetMunicipalCityListModel()
        deleteDatasetMunicipalStateListModel()
        deleteDatasetHolidayCalendarListModel()
    }
}