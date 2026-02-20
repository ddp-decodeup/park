package com.parkloyalty.lpr.scan.database.services

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.parkloyalty.lpr.scan.databasetable.ActivityImageTable
import com.parkloyalty.lpr.scan.ui.check_setup.model.municipalcitation.MunicipalCitationLayoutResponse
import com.parkloyalty.lpr.scan.ui.check_setup.model.timing.AddTimingDatabaseModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.ActivityLayoutResponse
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.CitationBookletModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.CitationImageModelOffline
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.CitationImagesModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.CitationInsurranceDatabaseModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.CitationInsurrancePrintData
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.CitationIssuranceModel
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
import com.parkloyalty.lpr.scan.ui.login.model.TimestampDatatbase
import com.parkloyalty.lpr.scan.ui.login.model.WelcomeListDatatbase

@Dao
interface CitationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCitationLayout(databaseModel: CitationLayoutResponse)

    @Query("SELECT * FROM citation_layout")
    suspend fun getCitationLayout(): CitationLayoutResponse?

    /* For Municipal Citation form Response*/
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMunicipalCitationLayout(databaseModel: MunicipalCitationLayoutResponse)

    @Query("SELECT * FROM municipal_citation_layout")
    fun getMunicipalCitationLayout(): MunicipalCitationLayoutResponse?

    @Insert
    suspend fun insertCitationBooklet(databaseModel: List<CitationBookletModel>)

    @Query("SELECT * FROM citation_booklet where status=:status ORDER BY citation_booklet ASC")
    suspend fun getCitationBooklet(status: Int): List<CitationBookletModel>

    @Query("SELECT * FROM citation_booklet where citation_booklet=:id ORDER BY citation_booklet ASC")
    suspend fun getCitationBookletByCitation(id: String?): List<CitationBookletModel?>?

    @Query("SELECT COUNT(*) FROM citation_booklet")
    fun getCountBooklet(): Int

    @Query("UPDATE citation_booklet SET status=:status WHERE citation_booklet = :id")
    fun updateCitationBooklet(status: Int, id: String?)

    @Query("SELECT status FROM citation_booklet WHERE citation_booklet=:id")
    suspend fun getBookletStatus(id: String?): Int

    /* For Citation Images*/
    @Insert
    suspend fun insertCitationImage(databaseModel: CitationImagesModel)

    @Query("SELECT * FROM citation_images ORDER BY id ASC")
    suspend fun getCitationImage(): List<CitationImagesModel?>?

    @Query("SELECT COUNT(*) FROM citation_images ORDER BY id ASC")
    suspend fun getCountImages(): Int

    @Query("DELETE FROM citation_images")
    fun deleteTempImages()

    @Query("DELETE FROM citation_images where id= :id")
    suspend fun deleteTempImagesWithId(id: Int)

    /* For Citation Images offline*/
    @Insert
    suspend fun insertCitationImageOffline(databaseModel: CitationImageModelOffline)

    @Query("SELECT * FROM citation_images_offline where citation_number_text=:id")
    fun getCitationImageOffline(id: String): List<CitationImageModelOffline>?

    @Query("DELETE FROM citation_images_offline where id= :id")
    fun deleteTempImagesOfflineWithId(id: String)

    /* For Citation Insurance */
    /**
     * 0 : Uploaded
     * 1: Unuploaded : API failed & uploaded in background
     * 2: UnUploaded : on screen change & move to citation form
     */
    @Insert
    suspend fun insertCitationInsurance(databaseModel: CitationInsurranceDatabaseModel)

    @Query("UPDATE citation_issurance SET citation_data=:model WHERE citation_number = :id")
    suspend fun updateCitationInsurance(model: CitationIssuranceModel?, id: String?)

    @Query("SELECT * FROM citation_issurance where form_status = 1")
    fun getCitationInsurance(): List<CitationInsurranceDatabaseModel?>?

    @Query("SELECT * FROM citation_issurance where form_status = 2")
    suspend fun getCitationInsuranceUnuploadCitation(): List<CitationInsurranceDatabaseModel?>?

    @Query("SELECT * FROM citation_issurance")
    fun getCitationInsuranceCheck(): List<CitationInsurranceDatabaseModel?>?

    @Query("SELECT * FROM citation_issurance where citation_number=:citationNumber")
    suspend fun getCitationWithTicket(citationNumber: String?): CitationInsurranceDatabaseModel?

    @Query("SELECT COUNT(*) FROM citation_issurance")
    fun getCountCitationIssurrance(): Int

    @Query("UPDATE citation_issurance SET form_status=:uploadStatus WHERE citation_number = :id")
    suspend fun updateCitationUploadStatus(uploadStatus: Int, id: String?)

    @Query("DELETE FROM citation_issurance where citation_number= :id")
    fun deleteSaveCitation(id: String)

    /* For Updated TimeStamp  */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUpdatedTime(databaseModel: TimestampDatatbase)

    @Query("SELECT * FROM timestamp")
    suspend fun getUpdateTimeResponse(): TimestampDatatbase?

    @Query("SELECT * FROM timestamp")
    fun getUpdateTimeResponseList(): List<TimestampDatatbase>?

    @Query("DELETE FROM timestamp")
    fun deleteTimeStampTable()

    /* For Timing Data */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTimingData(databaseModel: AddTimingDatabaseModel)

    @Query("SELECT * FROM timing_data")
    fun getTimingData(): AddTimingDatabaseModel?

    @Query("SELECT MAX(id) AS max_id FROM timing_data")
    suspend fun getLastIDFromTimingData(): Int

    @Query("SELECT * FROM timing_data where form_status = 1")
    fun getLocalTimingDataList(): List<AddTimingDatabaseModel?>?

    @Query("UPDATE timing_data SET form_status=:upload WHERE id = :id")
    fun updateTimingUploadStatus(upload: Int, id: Int)

    /* For Timing Images*/
    @Insert
    suspend fun insertTimingImage(databaseModel: TimingImagesModel)

    @Query("SELECT * FROM timing_images where timingRecordId= :timingRecordId")
    fun getTimingImageUsingTimingRecordId(timingRecordId: Int): List<TimingImagesModel?>?

    @Query("DELETE FROM timing_images where timingRecordId= :timingRecordId")
    fun deleteTimingImagesWithTimingRecordId(timingRecordId: Int)

    /* For cancel citation get error from API upload in welcome*/
    @Insert
    suspend fun insertOfflineCancelCitation(databaseModel: OfflineCancelCitationModel)

    @Query("SELECT * FROM offline_cancel_citation")
    fun getOfflineCancelCitation(): List<OfflineCancelCitationModel?>?

    @Query("SELECT * FROM offline_cancel_citation where ticketNumber= :citationNumber")
    fun getOfflineCancelCitation(citationNumber: String): List<OfflineCancelCitationModel?>?

    @Query("DELETE FROM offline_cancel_citation where uploadedCitationId = :uploadedCitationId")
    fun deleteOfflineCancelCitation(uploadedCitationId: String)

    @Query("DELETE FROM offline_cancel_citation where ticketNumber = (:mTicketNumber)")
    fun deleteOfflineRescindCitation(mTicketNumber: String)

    @Delete
    suspend fun deleteOfflineRescindCitation(deletecitation:OfflineCancelCitationModel):Int

    // Facsimile Image Table query
    @Insert
    suspend fun insertFacsimileImageObject(databaseModel: UnUploadFacsimileImage)

    @Query("SELECT * FROM UnUploadFacsimileImage where status= 0")
    fun getUnUploadFacsimile(): UnUploadFacsimileImage?

    @Query("SELECT * FROM UnUploadFacsimileImage")
    fun getUnUploadFacsimileAll(): List<UnUploadFacsimileImage?>?

    @Query("UPDATE unUploadFacsimileImage SET imageLink=:mImageLink WHERE ticketNumberText= :citationNumber AND ticketNumber = :dateTime")
    fun updateFacsimileImageLink(mImageLink: String, citationNumber: String, dateTime: Long)

    @Query("UPDATE unUploadFacsimileImage SET uploadedCitationId=:mUploadCitationId WHERE ticketNumberText = :citationNumber")
    fun updateFacsimileUploadCitationId(mUploadCitationId: String, citationNumber: String)

    @Query("UPDATE unUploadFacsimileImage SET status=:mStatus WHERE ticketNumberText = :citationNumber AND ticketNumber = :dateTime")
    fun updateFacsimileStatus(mStatus: Int, citationNumber: String, dateTime: Long)

    @Query("DELETE FROM UnUploadFacsimileImage where ticketNumberText = (:citationNumber)")
    fun deleteFacsimileData(citationNumber: String)

    @Query("SELECT * FROM unUploadFacsimileImage")
    fun getUnUploadFacsimileAllData(): List<UnUploadFacsimileImage>?

    // 👇 Get number of rows
    @Query("SELECT COUNT(*) FROM UnUploadFacsimileImage")
    fun getUnUploadFacsimileCount(): Int

    @Query("SELECT COUNT(*) FROM UnUploadFacsimileImage WHERE imagePath = :imagePath")
    suspend fun isImagePathExists(imagePath: String): Int

    @Query("DELETE FROM UnUploadFacsimileImage where imagePath = (:imagePath)")
    suspend fun deleteUnUploadCitationImages(imagePath: String)

    /**
     * Print command table
     */

    @Insert
    suspend fun insertprintCitation(databaseModel: CitationInsurrancePrintData)
    @Query("SELECT * FROM citation_issurance_printer where citation_number=:citationNumber")
    fun getCitationWithTicketForPrint(citationNumber: String?): CitationInsurrancePrintData?

    @Insert
    suspend fun insertActivityImageData(databaseModel: ActivityImageTable)
    @Query("SELECT * FROM activity_image_table")
    fun getActivityImageData(): List<ActivityImageTable?>?

    @Query("DELETE FROM activity_image_table where response_id = (:activityResponseId)")
    fun deleteActivityImageData(activityResponseId: String)

    @Query("SELECT * FROM citation_number")
    fun getCitationNumberResponse(): CitationNumberDatabaseModel?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivityList(databaseModel: WelcomeListDatatbase)

    @Query("SELECT * FROM welcome_list")
    suspend fun getActivityList(): WelcomeListDatatbase?

    @Query("DELETE FROM welcome_list")
    fun deleteActivityList()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCitationNumberResponse(databaseModel: CitationNumberDatabaseModel)

    /* For Activity form Response*/
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivityLayout(databaseModel: ActivityLayoutResponse)

    @Query("SELECT * FROM activity_layout")
    suspend fun getActivityLayout(): ActivityLayoutResponse?

    /* For Timing form Response*/
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTimingLayout(databaseModel: TimingLayoutResponse)

    @Query("SELECT * FROM timing_layout")
    suspend fun getTimingLayout(): TimingLayoutResponse?

    //Non Async Operations
    @Query("SELECT * FROM citation_issurance where form_status = 1")
    suspend fun getCitationInsuranceNonAsync() : List<CitationInsurranceDatabaseModel?>

}