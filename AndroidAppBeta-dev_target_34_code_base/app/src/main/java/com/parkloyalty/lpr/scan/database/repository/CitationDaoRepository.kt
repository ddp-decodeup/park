package com.parkloyalty.lpr.scan.database.repository

import com.parkloyalty.lpr.scan.database.services.CitationDao
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
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.OfflineCancelCitationModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.TimingImagesModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.TimingLayoutResponse
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.UnUploadFacsimileImage
import com.parkloyalty.lpr.scan.ui.login.model.TimestampDatatbase
import com.parkloyalty.lpr.scan.ui.login.model.WelcomeListDatatbase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CitationDaoRepository @Inject constructor(
    private val citationDao: CitationDao
) {
    // Citation Layout
    suspend fun insertCitationLayout(databaseModel: CitationLayoutResponse) =
        citationDao.insertCitationLayout(databaseModel)

    suspend fun getCitationLayout(): CitationLayoutResponse? =
        citationDao.getCitationLayout()

    // Municipal Citation Layout
    suspend fun insertMunicipalCitationLayout(databaseModel: MunicipalCitationLayoutResponse) =
        citationDao.insertMunicipalCitationLayout(databaseModel)

    fun getMunicipalCitationLayout(): MunicipalCitationLayoutResponse? =
        citationDao.getMunicipalCitationLayout()

    // Citation Booklet
    suspend fun insertCitationBooklet(databaseModel: List<CitationBookletModel>) =
        citationDao.insertCitationBooklet(databaseModel)

    suspend fun getCitationBooklet(status: Int): List<CitationBookletModel> =
        citationDao.getCitationBooklet(status)

    suspend fun getCitationBookletByCitation(id: String?): List<CitationBookletModel?>? =
        citationDao.getCitationBookletByCitation(id)

    fun getCountBooklet(): Int =
        citationDao.getCountBooklet()

    fun updateCitationBooklet(status: Int, id: String?) =
        citationDao.updateCitationBooklet(status, id)

    suspend fun getBookletStatus(id: String?): Int =
        citationDao.getBookletStatus(id)

    // Citation Images
    suspend fun insertCitationImage(databaseModel: CitationImagesModel) =
        citationDao.insertCitationImage(databaseModel)

    suspend fun getCitationImage(): List<CitationImagesModel?>? =
        citationDao.getCitationImage()

    suspend fun getCountImages(): Int =
        citationDao.getCountImages()

    fun deleteTempImages() =
        citationDao.deleteTempImages()

    suspend fun deleteTempImagesWithId(id: Int) =
        citationDao.deleteTempImagesWithId(id)

    // Citation Images Offline
    suspend fun insertCitationImageOffline(databaseModel: CitationImageModelOffline) =
        citationDao.insertCitationImageOffline(databaseModel)

    fun getCitationImageOffline(id: String): List<CitationImageModelOffline>? =
        citationDao.getCitationImageOffline(id)

    fun deleteTempImagesOfflineWithId(id: String) =
        citationDao.deleteTempImagesOfflineWithId(id)

    // Citation Insurance
    suspend fun insertCitationInsurance(databaseModel: CitationInsurranceDatabaseModel) =
        citationDao.insertCitationInsurance(databaseModel)

    suspend fun updateCitationInsurance(model: CitationIssuranceModel?, id: String?) =
        citationDao.updateCitationInsurance(model, id)

    fun getCitationInsurance(): List<CitationInsurranceDatabaseModel?>? =
        citationDao.getCitationInsurance()

    suspend fun getCitationInsuranceUnuploadCitation(): List<CitationInsurranceDatabaseModel?>? =
        citationDao.getCitationInsuranceUnuploadCitation()

    fun getCitationInsuranceCheck(): List<CitationInsurranceDatabaseModel?>? =
        citationDao.getCitationInsuranceCheck()

    suspend fun getCitationWithTicket(citationNumber: String?): CitationInsurranceDatabaseModel? =
        citationDao.getCitationWithTicket(citationNumber)

    fun getCountCitationIssurrance(): Int =
        citationDao.getCountCitationIssurrance()

    suspend fun updateCitationUploadStatus(uploadStatus: Int, id: String?) =
        citationDao.updateCitationUploadStatus(uploadStatus, id)

    fun deleteSaveCitation(id: String) =
        citationDao.deleteSaveCitation(id)

    suspend fun insertUpdatedTime(databaseModel: TimestampDatatbase) =
        citationDao.insertUpdatedTime(databaseModel)

    suspend fun getUpdateTimeResponse(): TimestampDatatbase? =
        citationDao.getUpdateTimeResponse()

    fun getUpdateTimeResponseList(): List<TimestampDatatbase>? =
        citationDao.getUpdateTimeResponseList()

    fun deleteTimeStampTable() =
        citationDao.deleteTimeStampTable()

    // Timing Data
    suspend fun insertTimingData(databaseModel: AddTimingDatabaseModel) =
        citationDao.insertTimingData(databaseModel)

    fun getTimingData(): AddTimingDatabaseModel? =
        citationDao.getTimingData()

    suspend fun getLastIDFromTimingData(): Int =
        citationDao.getLastIDFromTimingData()

    fun getLocalTimingDataList(): List<AddTimingDatabaseModel?>? =
        citationDao.getLocalTimingDataList()

    fun updateTimingUploadStatus(upload: Int, id: Int) =
        citationDao.updateTimingUploadStatus(upload, id)

    // Timing Images
    suspend fun insertTimingImage(databaseModel: TimingImagesModel) =
        citationDao.insertTimingImage(databaseModel)

    fun getTimingImageUsingTimingRecordId(timingRecordId: Int): List<TimingImagesModel?>? =
        citationDao.getTimingImageUsingTimingRecordId(timingRecordId)

    fun deleteTimingImagesWithTimingRecordId(timingRecordId: Int) =
        citationDao.deleteTimingImagesWithTimingRecordId(timingRecordId)

    // Offline Cancel Citation
    suspend fun insertOfflineCancelCitation(databaseModel: OfflineCancelCitationModel) =
        citationDao.insertOfflineCancelCitation(databaseModel)

    fun getOfflineCancelCitation(): List<OfflineCancelCitationModel?>? =
        citationDao.getOfflineCancelCitation()

    fun getOfflineCancelCitation(citationNumber: String): List<OfflineCancelCitationModel?>? =
        citationDao.getOfflineCancelCitation(citationNumber)

    fun deleteOfflineCancelCitation(uploadedCitationId: String) =
        citationDao.deleteOfflineCancelCitation(uploadedCitationId)

    fun deleteOfflineRescindCitation(mTicketNumber: String) =
        citationDao.deleteOfflineRescindCitation(mTicketNumber)

    suspend fun deleteOfflineRescindCitation(deleteCitation: OfflineCancelCitationModel): Int =
        citationDao.deleteOfflineRescindCitation(deleteCitation)

    // Facsimile Image Table
    suspend fun insertFacsimileImageObject(databaseModel: UnUploadFacsimileImage) =
        citationDao.insertFacsimileImageObject(databaseModel)

    fun getUnUploadFacsimile(): UnUploadFacsimileImage? =
        citationDao.getUnUploadFacsimile()

    fun getUnUploadFacsimileAll(): List<UnUploadFacsimileImage?>? =
        citationDao.getUnUploadFacsimileAll()

    fun updateFacsimileImageLink(
        mImageLink: String,
        citationNumber: String,
        dateTime: Long
    ) =
        citationDao.updateFacsimileImageLink(mImageLink, citationNumber, dateTime)

    fun updateFacsimileUploadCitationId(mUploadCitationId: String, citationNumber: String) =
        citationDao.updateFacsimileUploadCitationId(mUploadCitationId, citationNumber)

    fun updateFacsimileStatus(status: Int, citationNumber: String, dateTime: Long) =
        citationDao.updateFacsimileStatus(status, citationNumber, dateTime)

    fun deleteFacsimileData(citationNumber: String) =
        citationDao.deleteFacsimileData(citationNumber)

    fun getUnUploadFacsimileAllData(): List<UnUploadFacsimileImage>? =
        citationDao.getUnUploadFacsimileAllData()

    fun getUnUploadFacsimileCount(): Int =
        citationDao.getUnUploadFacsimileCount()

    suspend fun isImagePathExists(imagePath: String): Int =
        citationDao.isImagePathExists(imagePath)

    suspend fun deleteUnUploadCitationImages(imagePath: String) =
        citationDao.deleteUnUploadCitationImages(imagePath)

    // Print command table
    suspend fun insertprintCitation(databaseModel: CitationInsurrancePrintData) =
        citationDao.insertprintCitation(databaseModel)

    fun getCitationWithTicketForPrint(citationNumber: String?): CitationInsurrancePrintData? =
        citationDao.getCitationWithTicketForPrint(citationNumber)

    // Activity Image Table
    suspend fun insertActivityImageData(databaseModel: ActivityImageTable) =
        citationDao.insertActivityImageData(databaseModel)

    fun getActivityImageData(): List<ActivityImageTable?>? =
        citationDao.getActivityImageData()

    fun deleteActivityImageData(activityResponseId: String) =
        citationDao.deleteActivityImageData(activityResponseId)

    // Citation Number
    suspend fun getCitationNumberResponse(): CitationNumberDatabaseModel? =
        citationDao.getCitationNumberResponse()

    suspend fun insertCitationNumberResponse(databaseModel: CitationNumberDatabaseModel) =
        citationDao.insertCitationNumberResponse(databaseModel)

    // Activity List
    suspend fun insertActivityList(databaseModel: WelcomeListDatatbase) =
        citationDao.insertActivityList(databaseModel)

    suspend fun getActivityList(): WelcomeListDatatbase? =
        citationDao.getActivityList()

    fun deleteActivityList() =
        citationDao.deleteActivityList()

    // Activity Layout
    suspend fun insertActivityLayout(databaseModel: ActivityLayoutResponse) =
        citationDao.insertActivityLayout(databaseModel)

    suspend fun getActivityLayout(): ActivityLayoutResponse? =
        citationDao.getActivityLayout()

    // Timing Layout
    suspend fun insertTimingLayout(databaseModel: TimingLayoutResponse) =
        citationDao.insertTimingLayout(databaseModel)

    suspend fun getTimingLayout(): TimingLayoutResponse? =
        citationDao.getTimingLayout()
}