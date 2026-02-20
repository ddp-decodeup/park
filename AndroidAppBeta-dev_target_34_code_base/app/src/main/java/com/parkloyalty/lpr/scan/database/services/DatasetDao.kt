package com.parkloyalty.lpr.scan.database.services

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
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

@Dao
interface DatasetDao {
    //Holiday Calendar List
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDatasetHolidayCalendarListModel(datasetHolidayCalendarList: DatasetHolidayCalendarList)

    @Query("SELECT * FROM dataset_holiday_calendar_list")
    suspend fun getDatasetHolidayCalendarListModel(): DatasetHolidayCalendarList

    @Query("DELETE FROM dataset_holiday_calendar_list")
    suspend fun deleteDatasetHolidayCalendarListModel()


    //Decal Year List
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDatasetDecalYearListModel(datasetDecalYearListModel: DatasetDecalYearListModel)

    @Query("SELECT * FROM dataset_decal_year_list")
    suspend fun getDatasetDecalYearModel(): DatasetDecalYearListModel?

    @Query("DELETE FROM dataset_decal_year_list")
    suspend fun deleteDatasetDecalYearListModel()

    //Car Make List
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDatasetCarMakeListModel(datasetCarMakeListModel: DatasetCarMakeListModel)

    @Query("SELECT * FROM dataset_car_make_list")
    suspend fun getDatasetCarMakeListModel(): DatasetCarMakeListModel?

    @Query("DELETE FROM dataset_car_make_list")
    suspend fun deleteDatasetCarMakeListModel()

    //Car Color List
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDatasetCarColorListModel(datasetCarColorListModel: DatasetCarColorListModel)

    @Query("SELECT * FROM dataset_car_color_list")
    suspend fun getDatasetCarColorListModel(): DatasetCarColorListModel?

    @Query("DELETE FROM dataset_car_color_list")
    suspend fun deleteDatasetCarColorListModel()

    //State List
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDatasetStateListModel(datasetStateListModel: DatasetStateListModel)

    @Query("SELECT * FROM dataset_state_list")
    suspend fun getDatasetStateListModel(): DatasetStateListModel?

    @Query("DELETE FROM dataset_state_list")
    suspend fun deleteDatasetStateListModel()

    //Block List
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDatasetBlockListModel(datasetBlockListModel: DatasetBlockListModel)

    @Query("SELECT * FROM dataset_block_list")
    suspend fun getDatasetBlockListModel(): DatasetBlockListModel?

    @Query("DELETE FROM dataset_block_list")
    suspend fun deleteDatasetBlockListModel()

    //Street List
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDatasetStreetListModel(datasetStreetListModel: DatasetStreetListModel)

    @Query("SELECT MAX(id) FROM dataset_street_list")
    suspend fun getMaxStreetListId(): Int?

    @Query("SELECT * FROM dataset_street_list")
    suspend fun getDatasetStreetListModel(): DatasetStreetListModel?

    @Query("DELETE FROM dataset_street_list")
    suspend fun deleteDatasetStreetListModel()

    //Meter List
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDatasetMeterListModel(datasetMeterListModel: DatasetMeterListModel)

    @Query("SELECT * FROM dataset_meter_list")
    suspend fun getDatasetMeterListModel(): DatasetMeterListModel?

    @Query("DELETE FROM dataset_meter_list")
    suspend fun deleteDatasetMeterListModel()

    //Space List
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDatasetSpaceListModel(datasetSpaceListModel: DatasetSpaceListModel)

    @Query("SELECT * FROM dataset_space_list")
    suspend fun getDatasetSpaceListModel(): DatasetSpaceListModel?

    @Query("DELETE FROM dataset_space_list")
    suspend fun deleteDatasetSpaceListModel()

    //Car body Style List
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDatasetCarBodyStyleListModel(datasetCarBodyStyleListModel: DatasetCarBodyStyleListModel)

    @Query("SELECT * FROM dataset_car_body_style_list")
    suspend fun getDatasetCarBodyStyleListModel(): DatasetCarBodyStyleListModel?

    @Query("DELETE FROM dataset_car_body_style_list")
    suspend fun deleteDatasetCarBodyStyleListModel()

    //Violation List
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDatasetViolationListModel(datasetViolationListModel: DatasetViolationListModel)

    @Query("SELECT * FROM dataset_violation_list")
    suspend fun getDatasetViolationListModel(): DatasetViolationListModel?

    @Query("DELETE FROM dataset_violation_list")
    suspend fun deleteDatasetViolationListModel()

    //Vio List
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDatasetVioListModel(datasetVioListModel: DatasetVioListModel)

    @Query("SELECT * FROM dataset_vio_list")
    suspend fun getDatasetVioListModel(): DatasetVioListModel?

    @Query("DELETE FROM dataset_vio_list")
    suspend fun deleteDatasetVioListModel()

    //Side List
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDatasetSideListModel(datasetSideListModel: DatasetSideListModel)

    @Query("SELECT * FROM dataset_side_list")
    suspend fun getDatasetSideListModel(): DatasetSideListModel?

    @Query("DELETE FROM dataset_side_list")
    suspend fun deleteDatasetSideListModel()

    //Tier Stem List
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDatasetTierStemListModel(datasetSideListModel: DatasetTierStemListModel)

    @Query("SELECT * FROM dataset_tier_stem_list")
    suspend fun getDatasetTierStemListModel(): DatasetTierStemListModel?

    @Query("DELETE FROM dataset_tier_stem_list")
    suspend fun deleteDatasetTierStemListModel()

    //Notes List
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDatasetNotesListModel(datasetNotesListModel: DatasetNotesListModel)

    @Query("SELECT * FROM dataset_notes_list")
    suspend fun getDatasetNotesListModel(): DatasetNotesListModel?

    @Query("DELETE FROM dataset_notes_list")
    suspend fun deleteDatasetNotesListModel()

    //Remarks List
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDatasetRemarksListModel(datasetRemarksListModel: DatasetRemarksListModel)

    @Query("SELECT * FROM dataset_remarks_list")
    suspend fun getDatasetRemarksListModel(): DatasetRemarksListModel?

    @Query("DELETE FROM dataset_remarks_list")
    suspend fun deleteDatasetRemarksListModel()

    //Lot List
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDatasetLotListModel(datasetLotListModel: DatasetLotListModel)

    @Query("SELECT * FROM dataset_lot_list")
    suspend fun getDatasetLotListModel(): DatasetLotListModel?

    @Query("DELETE FROM dataset_lot_list")
    suspend fun deleteDatasetLotListModel()

    //Regulation Time List
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDatasetRegulationTimeListModel(datasetRegulationTimeListModel: DatasetRegulationTimeListModel)

    @Query("SELECT * FROM dataset_regulation_time_list")
    suspend fun getDatasetRegulationTimeListModel(): DatasetRegulationTimeListModel?

    @Query("DELETE FROM dataset_regulation_time_list")
    suspend fun deleteDatasetRegulationTimeListModel()

    //Settings List
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDatasetSettingsListModel(datasetSettingsListModel: DatasetSettingsListModel)

    @Query("SELECT * FROM dataset_settings_list")
    suspend fun getDatasetSettingsListModel(): DatasetSettingsListModel?

    @Query("DELETE FROM dataset_settings_list")
    suspend fun deleteDatasetSettingsListModel()

    //Cancel Reason List
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDatasetCancelReasonListModel(datasetCancelReasonListModel: DatasetCancelReasonListModel)

    @Query("SELECT * FROM dataset_cancel_reason_list")
    suspend fun getDatasetCancelReasonListModel(): DatasetCancelReasonListModel?

    @Query("DELETE FROM dataset_cancel_reason_list")
    suspend fun deleteDatasetCancelReasonListModel()

    //PBC Zone List
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDatasetPBCZoneListModel(datasetPBCZoneListModel: DatasetPBCZoneListModel)

    @Query("SELECT * FROM dataset_pbc_zone_list")
    suspend fun getDatasetPBCZoneListModel(): DatasetPBCZoneListModel?

    @Query("DELETE FROM dataset_pbc_zone_list")
    fun deleteDatasetPBCZoneListModel()

    //Void And Reissue Reason List
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDatasetVoidAndReissueReasonListModel(datasetPBCZoneListModel: DatasetVoidAndReissueReasonListModel)

    @Query("SELECT * FROM dataset_void_and_reissue_reason_list")
    suspend fun getDatasetVoidAndReissueReasonListModel(): DatasetVoidAndReissueReasonListModel?

    @Query("DELETE FROM dataset_void_and_reissue_reason_list")
    suspend fun deleteDatasetVoidAndReissueReasonListModel()

    //Municipal Violation List
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDatasetMunicipalViolationListModel(datasetMunicipalViolationListModel: DatasetMunicipalViolationListModel)

    @Query("SELECT * FROM dataset_municipal_violation_list")
    suspend fun getDatasetMunicipalViolationListModel(): DatasetMunicipalViolationListModel?

    @Query("DELETE FROM dataset_municipal_violation_list")
    suspend fun deleteDatasetMunicipalViolationListModel()

    //Municipal Block List
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDatasetMunicipalBlockListModel(datasetMunicipalBlockListModel: DatasetMunicipalBlockListModel)

    @Query("SELECT * FROM dataset_municipal_block_list")
    suspend fun getDatasetMunicipalBlockListModel(): DatasetMunicipalBlockListModel?

    @Query("DELETE FROM dataset_municipal_block_list")
    suspend fun deleteDatasetMunicipalBlockListModel()

    //Municipal Street List
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDatasetMunicipalStreetListModel(datasetMunicipalStreetListModel: DatasetMunicipalStreetListModel)

    @Query("SELECT * FROM dataset_municipal_street_list")
    suspend fun getDatasetMunicipalStreetListModel(): DatasetMunicipalStreetListModel?

    @Query("DELETE FROM dataset_municipal_street_list")
    suspend fun deleteDatasetMunicipalStreetListModel()

    //Municipal City List
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDatasetMunicipalCityListModel(datasetMunicipalCityListModel: DatasetMunicipalCityListModel)

    @Query("SELECT * FROM dataset_municipal_city_list")
    suspend fun getDatasetMunicipalCityListModel(): DatasetMunicipalCityListModel?

    @Query("DELETE FROM dataset_municipal_city_list")
    suspend fun deleteDatasetMunicipalCityListModel()

    //Municipal State List
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDatasetMunicipalStateListModel(datasetMunicipalStateListModel: DatasetMunicipalStateListModel)

    @Query("SELECT * FROM dataset_municipal_state_list")
    suspend fun getDatasetMunicipalStateListModel(): DatasetMunicipalStateListModel?

    @Query("DELETE FROM dataset_municipal_state_list")
    suspend fun deleteDatasetMunicipalStateListModel()

    @Transaction
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
    }
}