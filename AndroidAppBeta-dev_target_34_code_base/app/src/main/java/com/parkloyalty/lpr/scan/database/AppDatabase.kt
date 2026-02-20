package com.parkloyalty.lpr.scan.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.parkloyalty.lpr.scan.database.services.ActivityDao
import com.parkloyalty.lpr.scan.database.services.AuthDao
import com.parkloyalty.lpr.scan.database.services.CitationDao
import com.parkloyalty.lpr.scan.database.services.DatasetDao
import com.parkloyalty.lpr.scan.database.services.InventoryDao
import com.parkloyalty.lpr.scan.databasetable.ActivityImageTable
import com.parkloyalty.lpr.scan.databasetable.InventoryToShowTable
import com.parkloyalty.lpr.scan.databasetable.QrCodeInventoryTable
import com.parkloyalty.lpr.scan.ui.check_setup.model.municipalcitation.MunicipalCitationLayoutResponse
import com.parkloyalty.lpr.scan.ui.check_setup.model.timing.AddTimingDatabaseModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.ActivityLayoutResponse
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.CitationBookletModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.CitationImageModelOffline
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.CitationImagesModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.CitationInsurranceDatabaseModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.CitationInsurrancePrintData
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.CitationLayoutResponse
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.CitationNumberDatabaseModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DatasetBlockListModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DatasetCancelReasonListModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DatasetCarBodyStyleListModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DatasetCarColorListModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DatasetCarMakeListModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DatasetDecalYearListModel
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
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DatasetViolationListModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DatasetVoidAndReissueReasonListModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.OfflineCancelCitationModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.TimingImagesModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.TimingLayoutResponse
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.UnUploadFacsimileImage
import com.parkloyalty.lpr.scan.ui.login.model.CommonLoginResponse
import com.parkloyalty.lpr.scan.ui.login.model.TimestampDatatbase
import com.parkloyalty.lpr.scan.ui.login.model.UpdateTimeResponse
import com.parkloyalty.lpr.scan.ui.login.model.WelcomeForm
import com.parkloyalty.lpr.scan.ui.login.model.WelcomeListDatatbase
import com.parkloyalty.lpr.scan.ui.honorbill.responsemodel.HonorBillLayoutResponse
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DatasetHolidayCalendarList
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DatasetVioListModel

@Database(
    entities = [
        WelcomeForm::class, CommonLoginResponse::class,
        CitationNumberDatabaseModel::class, CitationBookletModel::class, CitationImagesModel::class,
        TimingImagesModel::class, CitationInsurranceDatabaseModel::class, ActivityLayoutResponse::class,
        TimingLayoutResponse::class, CitationLayoutResponse::class, WelcomeListDatatbase::class,
        UpdateTimeResponse::class, AddTimingDatabaseModel::class, TimestampDatatbase::class,
        CitationImageModelOffline::class, OfflineCancelCitationModel::class, UnUploadFacsimileImage::class,
        CitationInsurrancePrintData::class, ActivityImageTable::class, QrCodeInventoryTable::class, InventoryToShowTable::class, MunicipalCitationLayoutResponse::class,
        HonorBillLayoutResponse::class,
        DatasetDecalYearListModel::class,
        DatasetCarMakeListModel::class,
        DatasetCarColorListModel::class,
        DatasetStateListModel::class,
        DatasetBlockListModel::class,
        DatasetStreetListModel::class,
        DatasetMeterListModel::class,
        DatasetSpaceListModel::class,
        DatasetCarBodyStyleListModel::class,
        DatasetViolationListModel::class,
        DatasetVioListModel::class,
        DatasetSideListModel::class,
        DatasetTierStemListModel::class,
        DatasetNotesListModel::class,
        DatasetRemarksListModel::class,
        DatasetRegulationTimeListModel::class,
        DatasetLotListModel::class,
        DatasetSettingsListModel::class,
        DatasetCancelReasonListModel::class,
        DatasetPBCZoneListModel::class,
        DatasetVoidAndReissueReasonListModel::class,
        DatasetMunicipalViolationListModel::class,
        DatasetMunicipalBlockListModel::class,
        DatasetMunicipalStreetListModel::class,
        DatasetMunicipalCityListModel::class,
        DatasetMunicipalStateListModel::class,
        DatasetHolidayCalendarList::class,
    ],
    version = 22
) //9 add timeImagePath citation_images table and activityimagetable
// 10  database.execSQL("ALTER TABLE welcome_form ADD COLUMN officer_lookup_code TEXT")
//   10    database.execSQL("ALTER TABLE offline_cancel_citation ADD COLUMN void_reason_lookup_code TEXT")
//   11    database.execSQL("ALTER TABLE unUploadFacsimileImage ADD COLUMN ticket_id TEXT")
//  14 Add QR inventory table
// 16 Add QR Equipment to show Table
//20 Owner Bill table created
//21 Added vio table for vio list
//22 Added holiday calendar table for holiday calendar  list
@TypeConverters(
    TypeConverterRoleSpecifics::class,
    TypeConverterLoginMetadata::class,
    TypeConverterCitationNumber::class,
    TypeConverterCitationNumberModel::class,
    TypeConverterCitationInsurrance::class,
    TypeConverterBeat::class,
    TypeConverterZone::class,
    TypeConverterActivityLayout::class,
    TypeConverterCitationLayout::class,
    TypeConverterMunicipalCitationLayout::class,
    TypeConverterWelcomeList::class,
    TypeConverterUpdatedTime::class,
    TypeConverterUpdatedTimestamp::class,
    TypeConverterDatasetResponse::class,
    TypeConverterOwnerBillLayout::class
)
abstract class AppDatabase : RoomDatabase() {
    abstract val dbDAO: DBDAO

    //New Classes for DAO
    abstract val activityDao: ActivityDao
    abstract val authDao: AuthDao
    abstract val citationDao: CitationDao
    abstract val datasetDao: DatasetDao
    abstract val inventoryDao: InventoryDao


//    companion object {
//        @Volatile private var INSTANCE: AppDatabase? = null
//
//        fun getInstance(app: Application): AppDatabase = INSTANCE ?: synchronized(this) {
//            INSTANCE ?: initAppDatabase(app).also { INSTANCE = it }
//        }
//
//        private fun initAppDatabase(app: Application) : AppDatabase {
//            return Room.databaseBuilder(
//                app,
//                AppDatabase::class.java, "park_loyalty"
//            )
//                .allowMainThreadQueries()
//                .fallbackToDestructiveMigration()
//                .addMigrations(DBMigration.MIGRATION_5_6)
//                .addMigrations(DBMigration.MIGRATION_6_7)
//                .addMigrations(DBMigration.MIGRATION_7_8)
//                .addMigrations(DBMigration.MIGRATION_8_9)
//                .addMigrations(DBMigration.MIGRATION_9_10)
//                .addMigrations(DBMigration.MIGRATION_10_11)
//                .addMigrations(DBMigration.MIGRATION_11_12)
//                .build()
//        }
//    }
}