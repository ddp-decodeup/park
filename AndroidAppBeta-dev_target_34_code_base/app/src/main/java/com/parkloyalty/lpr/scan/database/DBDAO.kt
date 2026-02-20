package com.parkloyalty.lpr.scan.database

import androidx.room.*
import com.parkloyalty.lpr.scan.databasetable.ActivityImageTable
import com.parkloyalty.lpr.scan.databasetable.InventoryToShowTable
import com.parkloyalty.lpr.scan.databasetable.QrCodeInventoryTable
import com.parkloyalty.lpr.scan.ui.check_setup.model.municipalcitation.MunicipalCitationLayoutResponse
import com.parkloyalty.lpr.scan.ui.check_setup.model.timing.AddTimingDatabaseModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.*
import com.parkloyalty.lpr.scan.ui.login.model.CommonLoginResponse
import com.parkloyalty.lpr.scan.ui.login.model.TimestampDatatbase
import com.parkloyalty.lpr.scan.ui.login.model.WelcomeForm
import com.parkloyalty.lpr.scan.ui.login.model.WelcomeListDatatbase


@Dao
interface DBDAO {
    /* For login Data*/
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLogin(commonLoginResponse: CommonLoginResponse)

    @Query("SELECT * FROM login")
    fun getLogin() : CommonLoginResponse?

    /* For welcome form Data*/
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertWelcomeForm(welcomeForm: WelcomeForm)

    //SELECT * FROM welcome_form ORDER BY table_id DESC
    @Query("SELECT * FROM welcome_form ORDER BY table_id DESC LIMIT 1")
    fun getWelcomeForm() : WelcomeForm?

//    @Query("SELECT * FROM welcome_form")
//    fun getWelcomeForm() : WelcomeForm?

    @Query("SELECT * FROM welcome_form")
    fun getWelcomeFormList() : List<WelcomeForm?>?

    /* For Dataset*/
    //Decal Year List
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDatasetDecalYearListModel(datasetDecalYearListModel: DatasetDecalYearListModel)

    @Query("SELECT * FROM dataset_decal_year_list")
    fun getDatasetDecalYearModel(): DatasetDecalYearListModel

    @Query("DELETE FROM dataset_decal_year_list")
    fun deleteDatasetDecalYearListModel()

    //Car Make List
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDatasetCarMakeListModel(datasetCarMakeListModel: DatasetCarMakeListModel)

    @Query("SELECT * FROM dataset_car_make_list")
    fun getDatasetCarMakeListModel(): DatasetCarMakeListModel

    @Query("DELETE FROM dataset_car_make_list")
    fun deleteDatasetCarMakeListModel()

    //Car Color List
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDatasetCarColorListModel(datasetCarColorListModel: DatasetCarColorListModel)

    @Query("SELECT * FROM dataset_car_color_list")
    fun getDatasetCarColorListModel(): DatasetCarColorListModel

    @Query("DELETE FROM dataset_car_color_list")
    fun deleteDatasetCarColorListModel()

    //State List
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDatasetStateListModel(datasetStateListModel: DatasetStateListModel)

    @Query("SELECT * FROM dataset_state_list")
    fun getDatasetStateListModel(): DatasetStateListModel

    @Query("DELETE FROM dataset_state_list")
    fun deleteDatasetStateListModel()

    //Block List
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDatasetBlockListModel(datasetBlockListModel: DatasetBlockListModel)

    @Query("SELECT * FROM dataset_block_list")
    fun getDatasetBlockListModel(): DatasetBlockListModel

    @Query("DELETE FROM dataset_block_list")
    fun deleteDatasetBlockListModel()

    //Street List
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDatasetStreetListModel(datasetStreetListModel: DatasetStreetListModel)

    @Query("SELECT MAX(id) FROM dataset_street_list")
    fun getMaxStreetListId(): Int?

    @Query("SELECT * FROM dataset_street_list")
    fun getDatasetStreetListModel(): DatasetStreetListModel

    @Query("DELETE FROM dataset_street_list")
    fun deleteDatasetStreetListModel()

    //Meter List
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDatasetMeterListModel(datasetMeterListModel: DatasetMeterListModel)

    @Query("SELECT * FROM dataset_meter_list")
    fun getDatasetMeterListModel(): DatasetMeterListModel

    @Query("DELETE FROM dataset_meter_list")
    fun deleteDatasetMeterListModel()

    //Space List
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDatasetSpaceListModel(datasetSpaceListModel: DatasetSpaceListModel)

    @Query("SELECT * FROM dataset_space_list")
    fun getDatasetSpaceListModel(): DatasetSpaceListModel

    @Query("DELETE FROM dataset_space_list")
    fun deleteDatasetSpaceListModel()

    //Car body Style List
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDatasetCarBodyStyleListModel(datasetCarBodyStyleListModel: DatasetCarBodyStyleListModel)

    @Query("SELECT * FROM dataset_car_body_style_list")
    fun getDatasetCarBodyStyleListModel(): DatasetCarBodyStyleListModel

    @Query("DELETE FROM dataset_car_body_style_list")
    fun deleteDatasetCarBodyStyleListModel()


    //Violation List
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDatasetViolationListModel(datasetViolationListModel: DatasetViolationListModel)

    @Query("SELECT * FROM dataset_violation_list")
    fun getDatasetViolationListModel(): DatasetViolationListModel

    @Query("DELETE FROM dataset_violation_list")
    fun deleteDatasetViolationListModel()

    //Vio List
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDatasetVioListModel(datasetVioListModel: DatasetVioListModel)

    @Query("SELECT * FROM dataset_vio_list")
    fun getDatasetVioListModel(): DatasetVioListModel

    @Query("DELETE FROM dataset_vio_list")
    fun deleteDatasetVioListModel()

    //Holiday Calendar List
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDatasetHolidayCalendarListModel(datasetHolidayCalendarList: DatasetHolidayCalendarList)

    @Query("SELECT * FROM dataset_holiday_calendar_list")
    fun getDatasetHolidayCalendarListModel(): DatasetHolidayCalendarList

    @Query("DELETE FROM dataset_holiday_calendar_list")
    fun deleteDatasetHolidayCalendarListModel()

    //Side List
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDatasetSideListModel(datasetSideListModel: DatasetSideListModel)

    @Query("SELECT * FROM dataset_side_list")
    fun getDatasetSideListModel(): DatasetSideListModel

    @Query("DELETE FROM dataset_side_list")
    fun deleteDatasetSideListModel()

    //Tier Stem List
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDatasetTierStemListModel(datasetSideListModel: DatasetTierStemListModel)

    @Query("SELECT * FROM dataset_tier_stem_list")
    fun getDatasetTierStemListModel(): DatasetTierStemListModel

    @Query("DELETE FROM dataset_tier_stem_list")
    fun deleteDatasetTierStemListModel()

    //Notes List
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDatasetNotesListModel(datasetNotesListModel: DatasetNotesListModel)

    @Query("SELECT * FROM dataset_notes_list")
    fun getDatasetNotesListModel(): DatasetNotesListModel

    @Query("DELETE FROM dataset_notes_list")
    fun deleteDatasetNotesListModel()

    //Remarks List
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDatasetRemarksListModel(datasetRemarksListModel: DatasetRemarksListModel)

    @Query("SELECT * FROM dataset_remarks_list")
    fun getDatasetRemarksListModel(): DatasetRemarksListModel

    @Query("DELETE FROM dataset_remarks_list")
    fun deleteDatasetRemarksListModel()


    //Lot List
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDatasetLotListModel(datasetLotListModel: DatasetLotListModel)

    @Query("SELECT * FROM dataset_lot_list")
    fun getDatasetLotListModel(): DatasetLotListModel

    @Query("DELETE FROM dataset_lot_list")
    fun deleteDatasetLotListModel()

    //Regulation Time List
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDatasetRegulationTimeListModel(datasetRegulationTimeListModel: DatasetRegulationTimeListModel)

    @Query("SELECT * FROM dataset_regulation_time_list")
    fun getDatasetRegulationTimeListModel(): DatasetRegulationTimeListModel

    @Query("DELETE FROM dataset_regulation_time_list")
    fun deleteDatasetRegulationTimeListModel()

    //Settings List
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDatasetSettingsListModel(datasetSettingsListModel: DatasetSettingsListModel)

    @Query("SELECT * FROM dataset_settings_list")
    fun getDatasetSettingsListModel(): DatasetSettingsListModel

    @Query("DELETE FROM dataset_settings_list")
    fun deleteDatasetSettingsListModel()

    //Cancel Reason List
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDatasetCancelReasonListModel(datasetCancelReasonListModel: DatasetCancelReasonListModel)

    @Query("SELECT * FROM dataset_cancel_reason_list")
    fun getDatasetCancelReasonListModel(): DatasetCancelReasonListModel

    @Query("DELETE FROM dataset_cancel_reason_list")
    fun deleteDatasetCancelReasonListModel()

    //PBC Zone List
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDatasetPBCZoneListModel(datasetPBCZoneListModel: DatasetPBCZoneListModel)

    @Query("SELECT * FROM dataset_pbc_zone_list")
    fun getDatasetPBCZoneListModel(): DatasetPBCZoneListModel

    @Query("DELETE FROM dataset_pbc_zone_list")
    fun deleteDatasetPBCZoneListModel()

    //PBC Zone List
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDatasetVoidAndReissueReasonListModel(datasetPBCZoneListModel: DatasetVoidAndReissueReasonListModel)

    @Query("SELECT * FROM dataset_void_and_reissue_reason_list")
    fun getDatasetVoidAndReissueReasonListModel(): DatasetVoidAndReissueReasonListModel

    @Query("DELETE FROM dataset_void_and_reissue_reason_list")
    fun deleteDatasetVoidAndReissueReasonListModel()

    //Municipal Violation List
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDatasetMunicipalViolationListModel(datasetMunicipalViolationListModel: DatasetMunicipalViolationListModel)

    @Query("SELECT * FROM dataset_municipal_violation_list")
    fun getDatasetMunicipalViolationListModel(): DatasetMunicipalViolationListModel

    @Query("DELETE FROM dataset_municipal_violation_list")
    fun deleteDatasetMunicipalViolationListModel()

    //Municipal Block List
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDatasetMunicipalBlockListModel(datasetMunicipalBlockListModel: DatasetMunicipalBlockListModel)

    @Query("SELECT * FROM dataset_municipal_block_list")
    fun getDatasetMunicipalBlockListModel(): DatasetMunicipalBlockListModel

    @Query("DELETE FROM dataset_municipal_block_list")
    fun deleteDatasetMunicipalBlockListModel()

    //Municipal Street List
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDatasetMunicipalStreetListModel(datasetMunicipalStreetListModel: DatasetMunicipalStreetListModel)

    @Query("SELECT * FROM dataset_municipal_street_list")
    fun getDatasetMunicipalStreetListModel(): DatasetMunicipalStreetListModel

    @Query("DELETE FROM dataset_municipal_street_list")
    fun deleteDatasetMunicipalStreetListModel()

    //Municipal City List
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDatasetMunicipalCityListModel(datasetMunicipalCityListModel: DatasetMunicipalCityListModel)

    @Query("SELECT * FROM dataset_municipal_city_list")
    fun getDatasetMunicipalCityListModel(): DatasetMunicipalCityListModel

    @Query("DELETE FROM dataset_municipal_city_list")
    fun deleteDatasetMunicipalCityListModel()


    //State List
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDatasetMunicipalStateListModel(datasetMunicipalStateListModel: DatasetMunicipalStateListModel)

    @Query("SELECT * FROM dataset_municipal_state_list")
    fun getDatasetMunicipalStateListModel(): DatasetMunicipalStateListModel

    @Query("DELETE FROM dataset_municipal_state_list")
    fun deleteDatasetMunicipalStateListModel()


    @Transaction
    fun deleteAllDataSet() {
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
    /*For Dataset*/

    /* For Citation number Response*/
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCitationNumberResponse(databaseModel: CitationNumberDatabaseModel)

    /* For Activity form Response*/
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertActivityLayout(databaseModel: ActivityLayoutResponse)

    @Query("SELECT * FROM activity_layout")
    fun getActivityLayout(): ActivityLayoutResponse?

    /* For Timing form Response*/
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTimingLayout(databaseModel: TimingLayoutResponse)

    @Query("SELECT * FROM timing_layout")
    fun getTimingLayout(): TimingLayoutResponse?

    /* For Citation form Response*/
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCitationLayout(databaseModel: CitationLayoutResponse)

    @Query("SELECT * FROM citation_layout")
    fun getCitationLayout(): CitationLayoutResponse?

    /* For Municipal Citation form Response*/
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMunicipalCitationLayout(databaseModel: MunicipalCitationLayoutResponse)

    @Query("SELECT * FROM municipal_citation_layout")
    fun getMunicipalCitationLayout(): MunicipalCitationLayoutResponse?

    /* For Owner Bill form Response*/
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    fun insertOwnerBillLayout(databaseModel: OwnerBillLayoutResponse)
//
//    @Query("SELECT * FROM owner_bill_layout")
//    fun getOwnerBillLayout(): OwnerBillLayoutResponse?

    /* For Welcome form list*/
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertActivityList(databaseModel: WelcomeListDatatbase)

    @Query("SELECT * FROM welcome_list")
    fun getActivityList(): WelcomeListDatatbase?

    @Query("DELETE FROM welcome_list")
    fun deleteActivityList()

    @Query("SELECT * FROM citation_number")
    fun getCitationNumberResponse(): CitationNumberDatabaseModel?

    /* For Citation number list*/
    @Insert
    fun insertCitationBooklet(databaseModel: List<CitationBookletModel>)

    @Query("SELECT * FROM citation_booklet where status=:status ORDER BY citation_booklet ASC")
    fun getCitationBooklet(status: Int): List<CitationBookletModel>

    @Query("SELECT * FROM citation_booklet where citation_booklet=:id ORDER BY citation_booklet ASC")
    fun getCitationBookletByCitation(id: String?): List<CitationBookletModel?>?

    @Query("SELECT COUNT(*) FROM citation_booklet")
    fun getCountBooklet(): Int

    @Query("UPDATE citation_booklet SET status=:status WHERE citation_booklet = :id")
    fun updateCitationBooklet(status: Int, id: String?)

    @Query("SELECT status FROM citation_booklet WHERE citation_booklet=:id")
    fun getBookletStatus(id: String?): Int

    /* For Citation Images*/
    @Insert
    fun insertCitationImage(databaseModel: CitationImagesModel)

    @Query("SELECT * FROM citation_images ORDER BY id ASC")
    fun getCitationImage(): List<CitationImagesModel?>?

    @Query("SELECT COUNT(*) FROM citation_images ORDER BY id ASC")
    fun getCountImages(): Int

    @Query("DELETE FROM citation_images")
    fun deleteTempImages()

    @Query("DELETE FROM citation_images where id= :id")
    fun deleteTempImagesWithId(id: Int)

    /* For Citation Images offline*/
    @Insert
    fun insertCitationImageOffline(databaseModel: CitationImageModelOffline)

    @Query("SELECT * FROM citation_images_offline where citation_number_text=:id")
    fun getCitationImageOffline(id: String): List<CitationImageModelOffline?>?

    @Query("DELETE FROM citation_images_offline where id= :id")
    fun deleteTempImagesOfflineWithId(id: String)

    /* For Citation Insurrance */
    /**
     * 0 : Uploaded
     * 1: Unuploaded : API failed & uploaded in background
     * 2: UnUploaded : on screen change & move to citation form
     */
    @Insert
    fun insertCitationInssurrance(databaseModel: CitationInsurranceDatabaseModel)

    @Query("UPDATE citation_issurance SET citation_data=:model WHERE citation_number = :id")
    fun updateCitationInsurrance(model: CitationIssuranceModel?, id: String?)

    @Query("SELECT * FROM citation_issurance where form_status = 1")
    fun getCitationInsurrance() : List<CitationInsurranceDatabaseModel?>?

    @Query("SELECT * FROM citation_issurance where form_status = 2")
    fun getCitationInsurranceUnuploadCitation(): List<CitationInsurranceDatabaseModel?>?

    @Query("SELECT * FROM citation_issurance")
    fun getCitationInsurranceCheck(): List<CitationInsurranceDatabaseModel?>?

    @Query("SELECT * FROM citation_issurance where citation_number=:citationNumber")
    fun getCitationWithTicket(citationNumber: String?): CitationInsurranceDatabaseModel?

    @Query("SELECT COUNT(*) FROM citation_issurance")
    fun getCountCitationIssurrance(): Int

    /**
     * 0 : Uploaded
     * 1: Unuploaded : API failed & uploaded in background
     * 2: UnUploaded : on screen change & move to citation form
     */
    @Query("UPDATE citation_issurance SET form_status=:uploadStatus WHERE citation_number = :id")
    fun updateCitationUploadStatus(uploadStatus: Int, id: String?)

    @Query("DELETE FROM citation_issurance where citation_number= :id")
    fun deleteSaveCitation(id: String)
//    --------------------------------------

    /* For Updated TimeStamp  */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUpdatedTime(databaseModel: TimestampDatatbase)

    @Query("SELECT * FROM timestamp")
    fun getUpdateTimeResponse(): TimestampDatatbase

    @Query("SELECT * FROM timestamp")
    fun getUpdateTimeResponseList(): List<TimestampDatatbase>?

    @Query("DELETE FROM timestamp")
    fun deleteTimeStampTable()

    /* For Timing Data */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTimingData(databaseModel: AddTimingDatabaseModel)

    @Query("SELECT * FROM timing_data")
    fun getTimingData(): AddTimingDatabaseModel?

    @Query("SELECT MAX(id) AS max_id FROM timing_data")
    fun getLastIDFromTimingData(): Int

    @Query("SELECT * FROM timing_data where form_status = 1")
    fun getLocalTimingDataList(): List<AddTimingDatabaseModel?>?

    @Query("UPDATE timing_data SET form_status=:upload WHERE id = :id")
    fun updateTimingUploadStatus(upload: Int, id: Int)

    /* For Timing Images*/
    @Insert
    fun insertTimingImage(databaseModel: TimingImagesModel)

    @Query("SELECT * FROM timing_images where timingRecordId= :timingRecordId")
    fun getTimingImageUsingTimingRecordId(timingRecordId: Int): List<TimingImagesModel?>?

    @Query("DELETE FROM timing_images where timingRecordId= :timingRecordId")
    fun deleteTimingImagesWithTimingRecordId(timingRecordId: Int)


    /* For cancel citation get error from API upload in welcome*/
    @Insert
    fun insertOfflineCancelCitation(databaseModel: OfflineCancelCitationModel)

    @Query("SELECT * FROM offline_cancel_citation")
    fun getOfflineCancelCitation(): List<OfflineCancelCitationModel?>?

    @Query("SELECT * FROM offline_cancel_citation where ticketNumber= :citationNumber")
    fun getOfflineCancelCitation(citationNumber: String):  List<OfflineCancelCitationModel?>?

    /*@Query("DELETE FROM offline_cancel_citation where id= :id")
    fun deleteOfflineCancelCitationWithId(id: Int)*/

    @Query("DELETE FROM offline_cancel_citation where uploadedCitationId = :uploadedCitationId")
    fun deleteOfflineCancelCitation(uploadedCitationId: String)

    @Query("DELETE FROM offline_cancel_citation where ticketNumber = (:mTicketNumber)")
    abstract fun deleteOfflineRescindCitation(mTicketNumber: String)

    @Delete
    fun deleteOfflineRescindCitation(deletecitation:OfflineCancelCitationModel):Int

    // Facsimile Image Table query
    @Insert
    fun insertFacsimileImageObject(databaseModel: UnUploadFacsimileImage)

    @Query("SELECT * FROM UnUploadFacsimileImage where status= 0")
    fun getUnUploadFacsimile():  UnUploadFacsimileImage?

    @Query("SELECT * FROM UnUploadFacsimileImage")
    fun getUnUploadFacsimileAll():  List<UnUploadFacsimileImage?>?

    @Query("UPDATE unUploadFacsimileImage SET imageLink=:mImageLink WHERE ticketNumberText= :citationNumber AND ticketNumber = :dateTime")
    fun updateFacsimileImageLink(mImageLink: String, citationNumber: String, dateTime: Long)

//    @Query("UPDATE unUploadFacsimileImage SET ticketId=:ticketId WHERE ticketNumber= :citationNumber")
//    fun updateFacsimileTicketId(ticketId: String, citationNumber: Int)

    @Query("UPDATE unUploadFacsimileImage SET uploadedCitationId=:mUploadCitationId WHERE ticketNumberText = :citationNumber")
    fun updateFacsimileUploadCitationId(mUploadCitationId: String, citationNumber: String)


    @Query("UPDATE unUploadFacsimileImage SET status=:mStatus WHERE ticketNumberText = :citationNumber AND ticketNumber = :dateTime")
    fun updateFacsimileStatus(mStatus: Int, citationNumber: String, dateTime: Long)

    @Query("DELETE FROM UnUploadFacsimileImage where ticketNumberText = (:citationNumber)")
    abstract fun deleteFacsimileData(citationNumber: String)

    @Query("SELECT * FROM unUploadFacsimileImage")
     fun getUnUploadFacsimileAllData(): List<UnUploadFacsimileImage>

    // 👇 Get number of rows
    @Query("SELECT COUNT(*) FROM UnUploadFacsimileImage")
    fun getUnUploadFacsimileCount(): Int

    @Query("SELECT COUNT(*) FROM UnUploadFacsimileImage WHERE imagePath = :imagePath")
    fun isImagePathExists(imagePath: String): Int

    @Query("DELETE FROM UnUploadFacsimileImage where imagePath = (:imagePath)")
     fun deleteUnUploadCitationImages(imagePath: String)
//    @Query("SELECT COUNT(*) FROM UnUploadFacsimileImage")
//    fun getCountForUnUploadFacsimileImage(): MutableLiveData<Int?>?

    /**
     * Print command table
     */

    @Insert
    fun insertprintCitation(databaseModel: CitationInsurrancePrintData)
    @Query("SELECT * FROM citation_issurance_printer where citation_number=:citationNumber")
    fun getCitationWithTicketForPrint(citationNumber: String?): CitationInsurrancePrintData?

    @Insert
    fun insertActivityImageData(databaseModel: ActivityImageTable)
    @Query("SELECT * FROM activity_image_table")
    fun getActivityImageData(): List<ActivityImageTable?>?

    @Query("DELETE FROM activity_image_table where response_id = (:activityResponseId)")
    abstract fun deleteActivityImageData(activityResponseId: String)

    /**
     * Start of QR code inventory DB Services
     */
    @Insert
    fun insertQrCodeInventoryData(databaseModel: QrCodeInventoryTable)

    @Query("SELECT * FROM qr_code_inventory_table")
    fun getQrCodeInventoryData(): List<QrCodeInventoryTable?>?

    @Query("UPDATE qr_code_inventory_table SET is_checked_out=:isCheckedOut WHERE equipment_id=:equipmentId")
    fun updateQrCodeInventoryData(isCheckedOut: Int, equipmentId: String)

    @Query("DELETE FROM qr_code_inventory_table")
    fun deleteQrCodeInventoryTable()


    //Extra Inventory DB services used to show Items only
    @Insert
    fun insertInventoryToShowData(databaseModel: InventoryToShowTable)

    @Insert
    fun insertAllInventoryToShowData(databaseModelList: List<InventoryToShowTable>): LongArray

    @Query("SELECT * FROM inventory_to_show_table")
    fun getInventoryToShowData(): List<InventoryToShowTable?>?

    @Query("UPDATE inventory_to_show_table SET is_checked_out=:isCheckedOut WHERE equipment_id=:equipmentId")
    fun updateInventoryToShowData(isCheckedOut: Int, equipmentId: String)

    @Query("UPDATE inventory_to_show_table SET is_checked_out=:isCheckedOut, equipment_value=:equipmentValue WHERE equipment_name=:equipmentName")
    fun updateInventoryToShowDataByName(isCheckedOut: Int, equipmentName: String, equipmentValue: String)

    @Query("DELETE FROM inventory_to_show_table")
    fun deleteInventoryToShowTable()
    /**
     * End of QR code inventory DB Services
     */
}