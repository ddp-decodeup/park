package com.parkloyalty.lpr.scan.ui.ticket

import android.annotation.SuppressLint
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import butterknife.ButterKnife
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.parkloyalty.lpr.scan.BuildConfig
import com.parkloyalty.lpr.scan.R
import com.parkloyalty.lpr.scan.basecontrol.BaseActivity
import com.parkloyalty.lpr.scan.basecontrol.BaseApplication
import com.parkloyalty.lpr.scan.database.AppDatabase
import com.parkloyalty.lpr.scan.extensions.nullSafety
import com.parkloyalty.lpr.scan.interfaces.APIConstant
import com.parkloyalty.lpr.scan.interfaces.Constants
import com.parkloyalty.lpr.scan.interfaces.CustomDialogHelper
import com.parkloyalty.lpr.scan.network.ApiResponse
import com.parkloyalty.lpr.scan.network.DynamicAPIPath
import com.parkloyalty.lpr.scan.network.NetworkCheck.isInternetAvailable
import com.parkloyalty.lpr.scan.network.Status
import com.parkloyalty.lpr.scan.ui.check_setup.adapter.SearchCitationAdapter
import com.parkloyalty.lpr.scan.ui.check_setup.model.lpr_model.UploadImageViewModel
import com.parkloyalty.lpr.scan.ui.check_setup.model.lpr_model.UploadImagesResponse
import com.parkloyalty.lpr.scan.ui.check_setup.model.ticket.*
import com.parkloyalty.lpr.scan.ui.guide_enforcement.model.PayBySpaceDataSetResponse
import com.parkloyalty.lpr.scan.ui.guide_enforcement.model.PayBySpaceResponse
import com.parkloyalty.lpr.scan.ui.guide_enforcement.model.SpaceCollectionsDataSetItem
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.*
import com.parkloyalty.lpr.scan.ui.login.model.*
import com.parkloyalty.lpr.scan.ui.ticket.fragment.CitationFragment
import com.parkloyalty.lpr.scan.ui.ticket.fragment.TimingRecordFragment
import com.parkloyalty.lpr.scan.util.AppUtils
import com.parkloyalty.lpr.scan.util.LogUtil
import com.parkloyalty.lpr.scan.util.LogUtil.printToastMSGForErrorWarning
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import androidx.lifecycle.Observer
import com.parkloyalty.lpr.scan.extensions.DuncanBrandingApp13
import com.parkloyalty.lpr.scan.interfaces.Constants.Companion.FILE_NAME_FACSIMILE_PRINT_BITMAP
import com.parkloyalty.lpr.scan.interfaces.LookUpCitationInterfaces
import com.parkloyalty.lpr.scan.interfaces.SharedPrefKey
import com.parkloyalty.lpr.scan.network.ApiLogsClass
import com.parkloyalty.lpr.scan.ui.check_setup.model.municipalcitation.CreateMunicipalCitationTicketRequest
import com.parkloyalty.lpr.scan.ui.check_setup.model.municipalcitation.CreateMunicipalCitationTicketResponse
import com.parkloyalty.lpr.scan.ui.check_setup.model.municipalcitation.CreateMunicipalCitationTicketViewModel
import com.parkloyalty.lpr.scan.ui.check_setup.model.municipalcitation.MunicipalCitationCommentsDetails
import com.parkloyalty.lpr.scan.ui.check_setup.model.municipalcitation.MunicipalCitationHeaderDetails
import com.parkloyalty.lpr.scan.ui.check_setup.model.municipalcitation.MunicipalCitationInvoiceFeeStructure
import com.parkloyalty.lpr.scan.ui.check_setup.model.municipalcitation.MunicipalCitationLocationDetails
import com.parkloyalty.lpr.scan.ui.check_setup.model.municipalcitation.MunicipalCitationOfficerDetails
import com.parkloyalty.lpr.scan.ui.check_setup.model.municipalcitation.MunicipalCitationVehicleDetails
import com.parkloyalty.lpr.scan.ui.check_setup.model.municipalcitation.MunicipalCitationViolationDetails
import com.parkloyalty.lpr.scan.ui.check_setup.model.ticket.VehicleDetails
import com.parkloyalty.lpr.scan.ui.ticket.model.*
import com.parkloyalty.lpr.scan.utils.ObjectMapperProvider
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

/*Phase 2*/
@AndroidEntryPoint
class SearchActivity : BaseActivity(), CustomDialogHelper,LookUpCitationInterfaces {

    private var mDate: String? = null
    private var simpleFrameLayout: FrameLayout? = null
    private var tabLayout: TabLayout? = null
    private var mDb: AppDatabase? = null
    private var mContext: Context? = null

    private var imageUploadSuccessCount = 0
    private val mImages: MutableList<String> = ArrayList()
    private val mImagesForTiming: MutableList<String> = ArrayList()
    private var offlineCitationImagesList: List<CitationImageModelOffline>? = null
    private var offlineCitationData: CitationInsurranceDatabaseModel? = null
    private var mCitationNumberId: String? = null
    private var cancelTicketDataObject: OfflineCancelCitationModel? = null
    private var mUploadCitationIdForCancel = ""
    private var offlineStatus:Int = 0

    private val mTicketStatusViewModel: TicketUploadStatusViewModel? by viewModels()
    private val mUploadImageViewModel: UploadImageViewModel? by viewModels()
    private val mCreateTicketViewModel: CreateTicketViewModel? by viewModels()
    private val mCreateMunicipalCitationTicketViewModel: CreateMunicipalCitationTicketViewModel? by viewModels()
    private val mTicketCancelViewModel: TicketCancelViewModel? by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        setFullScreenUI()
        ButterKnife.bind(this)
        mDb = BaseApplication.instance?.getAppDatabase()
        mContext = this

        setToolbar()
        // get the reference of FrameLayout and TabLayout
        simpleFrameLayout = findViewById<View>(R.id.simpleFrameLayout) as FrameLayout
        tabLayout = findViewById<View>(R.id.simpleTabLayout) as TabLayout
        // Create a new Tab named "First"
        val firstTab = tabLayout?.newTab()
        firstTab?.text = "Citations" // set the Text for the first Tab
        //firstTab.setIcon(R.drawable.ic_enforcement); // set an icon for the
        // first tab
        tabLayout?.addTab(firstTab!!) // add  the tab at in the TabLayout
        // Create a new Tab named "Second"
        val secondTab = tabLayout?.newTab()
        secondTab?.text = "Timing Records" // set the Text for the second Tab
        //secondTab.setIcon(R.drawable.ic_record_ticket); // set an icon for the second tab
        tabLayout?.addTab(secondTab!!) // add  the tab  in the TabLayout
        val fragment: Fragment = CitationFragment()
        val fm = supportFragmentManager
        val ft = fm.beginTransaction()
        ft.replace(R.id.simpleFrameLayout, fragment)
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        ft.commit()

        // perform setOnTabSelectedListener event on TabLayout
        tabLayout?.setOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                // get the current selected tab's position and replace the fragment accordingly
                var fragment: Fragment? = null
                fragment = when (tab.position) {
                    0 -> CitationFragment()
                    1 -> TimingRecordFragment()
                    else -> CitationFragment()
                }
                val fm = supportFragmentManager
                val ft = fm.beginTransaction()
                ft.replace(R.id.simpleFrameLayout, fragment)
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                ft.commit()
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
        addObservers()
    }

    //init toolbar navigation
    private fun setToolbar() {
        initToolbar(
            0,
            this,
            R.id.layHome,
            R.id.layTicketing,
            R.id.layMyActivity,
            R.id.laySetting,
            R.id.layReport,
            R.id.layLogout,
            R.id.drawerLy,
            R.id.imgBack,
            R.id.imgOptions,
            R.id.imgCross,
            R.id.cardTicketing,
            R.id.layIssue,
            R.id.layLookup,
            R.id.layScan,
            R.id.layMunicipalCitation,
            R.id.layGuideEnforcement,
            R.id.laySummary,
            R.id.cardMyActivity,
            R.id.layMap,
            R.id.layContinue,
            R.id.cardGuide,
            R.id.laypaybyplate,
            R.id.laypaybyspace,
            R.id.cardlookup,
            R.id.laycitation,
            R.id.laylpr,
            R.id.layClearcache,
            R.id.laySuperVisorView,
                R.id.layAllReport,
                R.id.layBrokenMeterReport,
                R.id.layCurbReport,
                R.id.layFullTimeReport,
                R.id.layHandHeldMalfunctionReport,
                R.id.laySignReport,
                R.id.layVehicleInspectionReport,
                R.id.lay72HourMarkedVehiclesReport,
                R.id.layBikeInspectionReport,
                R.id.cardAllReport,
                R.id.lay_eow_supervisor_shift_report,
                R.id.layPartTimeReport,
            R.id.layLprHits,
            R.id.laySpecialAssignmentReport,
            R.id.layQRCode,
            R.id.cardQRCode,
            R.id.layGenerateQRCode,
            R.id.layScanQRCode,
            R.id.laySunlight,
            R.id.imgSunlight,
            R.id.lay72hrNoticeToTowReport,
            R.id.layTowReport,
            R.id.laySignOffReport,
            R.id.layNFL,
            R.id.layHardSummer,
            R.id.layAfterSeven,
            R.id.layPayStationReport,
            R.id.laySignageReport,
            R.id.layHomelessReport,
            R.id.laySafetyReport,
            R.id.layTrashReport,
            R.id.layLotCountVioRateReport,
            R.id.layLotInspectionReport,
            R.id.layWordOrderReport,
            R.id.txtlogout,
            R.id.laycameraviolation,
            R.id.layScanSticker,
            R.id.laygenetichit,
            R.id.layDirectedEnforcement,
            R.id.layOwnerBill
        )
    }


    override fun onBackPressed() {
        if (backpressCloseDrawer()) {
            // close activity when drawer is closed
            super.onBackPressed()
        }
    }

    private fun splitsFormat(date: String): String {
        val separated = date.split("-").toTypedArray()
        val YYYY = separated[0]
        val MM = separated[1]
        val DD = separated[2]
        val calendar = Calendar.getInstance()
        calendar[Calendar.MONTH] = MM.toInt()
        val simpleDateFormat = SimpleDateFormat("MMM")
        simpleDateFormat.calendar = calendar
        val monthName = simpleDateFormat.format(calendar.time)
        return MM + "-" + DD + "-" + YYYY.substring(2, 4)
    }

    private fun getHours(hours: String): String? {
        val separated = hours.split(":").toTypedArray()
        val `val` = separated[0] + ":" + separated[1] // this will contain " they taste good"
        return dateConvert(`val`)
    }

    fun splitDate(date: String): String? {
        return try {
            val separated = date.split("T").toTypedArray()
            mDate = separated[0]
            val `val` = separated[1] // this will contain " they taste good"
            getHours(`val`)
        } catch (e: Exception) {
            date
        }
    }

    private fun dateConvert(value: String): String? {
        try {
            val _24HourSDF = SimpleDateFormat("HH:mm")
            val _12HourSDF = SimpleDateFormat("hh:mm a")
            val _24HourDt = _24HourSDF.parse(value)
            return splitsFormat(mDate.nullSafety()) + " " + _12HourSDF.format(_24HourDt)
            //System.out.println(_24HourDt);
            //System.out.println(_12HourSDF.format(_24HourDt));
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    private fun addObservers() {
        mCreateTicketViewModel?.response?.observe(this, createTicketResponseObserver)
        mCreateMunicipalCitationTicketViewModel?.response?.observe(this, createMunicipalCitationTicketResponseObserver)
        mTicketStatusViewModel?.response?.observe(this, ticketStatusResponseObserver)
        mUploadImageViewModel?.response?.observe(this, uploadImageResponseObserver)
    }

    override fun removeObservers() {
        super.removeObservers()
        mCreateTicketViewModel?.response?.removeObserver(createTicketResponseObserver)
        mCreateMunicipalCitationTicketViewModel?.response?.removeObserver(createMunicipalCitationTicketResponseObserver)
        mTicketStatusViewModel?.response?.removeObserver(ticketStatusResponseObserver)
        mUploadImageViewModel?.response?.removeObserver(uploadImageResponseObserver)
    }

    private val uploadImageResponseObserver = Observer { apiResponse: ApiResponse ->
        consumeResponse(apiResponse,DynamicAPIPath.POST_IMAGE)
    }

    private val createTicketResponseObserver = Observer{apiResponse: ApiResponse ->
        consumeResponse(apiResponse,DynamicAPIPath.POST_CREATE_TICKET)
    }

      private val createMunicipalCitationTicketResponseObserver = Observer{apiResponse: ApiResponse ->
        consumeResponse(apiResponse,DynamicAPIPath.POST_CREATE_MUNICIPAL_CITATION_TICKET)
    }

    private val ticketStatusResponseObserver = Observer { apiResponse: ApiResponse ->
        consumeResponse(apiResponse,DynamicAPIPath.POST_TICKET_UPLOADE_STATUS_META)
    }



    //save Citation Layout Databse
    private fun uploadOfflineCitation() {
        uploadOfflineMakeOneCitation().execute()
    }

   /**
     * only get status 1 citation which is fail by API
     * 2 Only preview screen citation
     */
    inner class uploadOfflineMakeOneCitation : AsyncTask<Void?, Int?, CitationInsurranceDatabaseModel?>() {
        override fun doInBackground(vararg voids: Void?): CitationInsurranceDatabaseModel? {
            try {
//                var mIssuranceModel: List<CitationInsurranceDatabaseModel?>? = ArrayList()
//                mIssuranceModel = mDb?.dbDAO?.getCitationInsurrance()
                var mIssuranceModel = mDb?.dbDAO?.getCitationWithTicket(mCitationNumberId)
//                for (i in mIssuranceModel!!.indices) {
//                    if (mIssuranceModel[i]!!.formStatus == 1) {
//                        mCitationNumberId = mIssuranceModel[i]!!.citationNumber
//                        return mIssuranceModel[i]
//                    }
//                }
                return mIssuranceModel
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
            return null
        }

        @SuppressLint("WrongThread")
        override fun onPostExecute(result: CitationInsurranceDatabaseModel?) {
            try {
                result?.let {
                    offlineStatus = 1
                    offlineCitationData = result
                    callTicketStatusApi(offlineCitationData!!.citationNumber)
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun uploadOfflineImages(result: CitationInsurranceDatabaseModel) {
        try {
            offlineCitationImagesList = ArrayList()
            offlineCitationImagesList =
                    mDb?.dbDAO?.getCitationImageOffline(result.citationNumber!!.toString()) as List<CitationImageModelOffline>?
            if (offlineCitationImagesList!!.size == 1) {
                callCreateTicketApi(result)
            } else {
                for (i in offlineCitationImagesList!!.indices) {
                    callOfflineCitationUploadImages(File(offlineCitationImagesList!![i].citationImage), i)
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    /* Call Api For update profile */
    private fun callOfflineCitationUploadImages(file: File?, num: Int) {
        if (isInternetAvailable(this@SearchActivity)) {
            val requestFile = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file!!)
            val files = MultipartBody.Part.createFormData(
                    "files",
                    if (file != null) file.name else "",
                    requestFile
            )
            val mDropdownList: Array<String>
            mDropdownList = if (file!!.name.contains("_"+FILE_NAME_FACSIMILE_PRINT_BITMAP)) {
                arrayOf(mCitationNumberId + "_" + num + "_" + FILE_NAME_FACSIMILE_PRINT_BITMAP)
            } else {
                arrayOf(mCitationNumberId + "_" + num)
            }
            val mRequestBodyType =
                    RequestBody.create("text/plain".toMediaTypeOrNull(), "CitationImages")
            mUploadImageViewModel!!.hitUploadImagesApi(mDropdownList, mRequestBodyType, files)
        } else {
        }

    }


    /* Call Api For Ticket Cancel */
    private fun callTicketStatusApi(citationNumber:String?) {

        if (isInternetAvailable(this@SearchActivity)) {
            class checkTicketUploadStatus :
                    AsyncTask<Void?, Int?, OfflineCancelCitationModel?>() {
                override fun doInBackground(vararg voids: Void?): OfflineCancelCitationModel? {
                    try {
//                        var cancelTicketData: List<OfflineCancelCitationModel?>? = ArrayList()
//                        cancelTicketData = mDb?.dbDAO?.getOfflineCancelCitation()
//                        for (i in cancelTicketData!!.indices) {
//                            return cancelTicketData[i]
//                        }
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
                    return null
                }

                @SuppressLint("WrongThread")
                override fun onPostExecute(result: OfflineCancelCitationModel?) {
                    try {
//                        if(result!=null) {
//                            Log.i("==>Offline:", "Called${ObjectMapperProvider.instance.writeValueAsString(result)}")
                        val ticketUploadStatusRequest = TicketUploadStatusRequest()
                        ticketUploadStatusRequest.citationNumber = citationNumber

                        mTicketStatusViewModel?.getTicketStatusApi(ticketUploadStatusRequest, citationNumber)

//                        }
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
                }
            }
            checkTicketUploadStatus().execute()
        } else {
            LogUtil.printToastMSG(
                    applicationContext,
                    getString(R.string.err_msg_connection_was_refused)
            )
        }
    }

    /* Call Api For citation layout details */
    private fun callCreateTicketApi(mIssuranceModel: CitationInsurranceDatabaseModel) {
        if (isInternetAvailable(this@SearchActivity)) {
            if (mIssuranceModel?.citationData?.municipalCitationMotoristDetailsModel == null) {
                val createTicketRequest = CreateTicketRequest()
                val locationDetails = LocationDetails()
                locationDetails.street = mIssuranceModel.citationData!!.location!!.street
                locationDetails.street_lookup_code =
                    mIssuranceModel?.citationData?.location?.mStreetLookupCode.nullSafety()
                locationDetails.block = mIssuranceModel.citationData!!.location!!.block
                locationDetails.side = mIssuranceModel.citationData!!.location!!.side
                locationDetails.meter = mIssuranceModel.citationData!!.location!!.meterName
                locationDetails.direction = mIssuranceModel.citationData!!.location!!.direction
                locationDetails.lot = mIssuranceModel.citationData!!.location!!.lot
                createTicketRequest.locationDetails = locationDetails
                val vehicleDetails = VehicleDetails()
                vehicleDetails.body_style = mIssuranceModel.citationData!!.vehicle!!.bodyStyle
                vehicleDetails.body_style_lookup_code =
                    mIssuranceModel?.citationData?.vehicle?.body_style_lookup_code.nullSafety()
                vehicleDetails.decal_year = mIssuranceModel.citationData!!.vehicle!!.decalYear
                vehicleDetails.decal_number = mIssuranceModel.citationData!!.vehicle!!.decalNumber
                vehicleDetails.vin_number = mIssuranceModel.citationData!!.vehicle!!.vinNumber
                vehicleDetails.make = mIssuranceModel.citationData!!.vehicle!!.make
                vehicleDetails.model_lookup_code =
                    mIssuranceModel?.citationData!!.vehicle!!.model_lookup_code.nullSafety("")
                vehicleDetails.color = mIssuranceModel.citationData!!.vehicle!!.color
                vehicleDetails.model = mIssuranceModel.citationData!!.vehicle!!.model
                vehicleDetails.model_lookup_code =
                    mIssuranceModel.citationData!!.vehicle!!.model_lookup_code.nullSafety("")
                vehicleDetails.lprNo = mIssuranceModel.citationData!!.vehicle!!.licensePlate
                vehicleDetails.state = mIssuranceModel.citationData!!.vehicle!!.state
                vehicleDetails.mLicenseExpiry =
                    if (mIssuranceModel.citationData!!.vehicle!!.expiration != null) mIssuranceModel.citationData!!.vehicle!!.expiration else ""
                createTicketRequest.vehicleDetails = vehicleDetails
                val violationDetails = ViolationDetails()
                violationDetails.code =
                    mIssuranceModel.citationData!!.voilation!!.code //mAutoComTextViewCode.getEditableText().toString().trim());
                violationDetails.violation =
                    mIssuranceModel?.citationData?.voilation?.violationCode.nullSafety()
                violationDetails.description =
                    mIssuranceModel.citationData!!.voilation!!.locationDescr
                try {
                    violationDetails.fine =
                        mIssuranceModel.citationData!!.voilation!!.amount.nullSafety("0").toDouble()
                    violationDetails.late_fine =
                        mIssuranceModel.citationData!!.voilation!!.amountDueDate.nullSafety("0")
                            .toDouble()
                    violationDetails.due_15_days =
                        mIssuranceModel.citationData!!.voilation!!.dueDate.nullSafety("0")
                            .toDouble()
                    violationDetails.due_30_days =
                        mIssuranceModel.citationData!!.voilation!!.dueDate30.nullSafety("0")
                            .toDouble()
                    violationDetails.due_45_days =
                        mIssuranceModel.citationData!!.voilation!!.dueDate45.nullSafety("0")
                            .toDouble()
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
                createTicketRequest.violationDetails = violationDetails

                try {
                    val invoiceFeeStructure = InvoiceFeeStructure()
                    invoiceFeeStructure.mSaleTax =
                        if (mIssuranceModel?.citationData?.voilation?.dueDateCost != null && !mIssuranceModel?.citationData?.voilation?.dueDateCost.equals(
                                "null"
                            )
                        ) mIssuranceModel?.citationData?.voilation?.dueDateCost.nullSafety("0")
                            .toDouble() else 0.0
                    invoiceFeeStructure.mCitationFee =
                        if (mIssuranceModel?.citationData?.voilation?.dueDateCitationFee != null && !mIssuranceModel?.citationData?.voilation?.dueDateCitationFee.equals(
                                "null"
                            )
                        ) mIssuranceModel?.citationData?.voilation?.dueDateCitationFee.nullSafety("0")
                            .toDouble() else 0.0
                    invoiceFeeStructure.mParkingFee =
                        if (mIssuranceModel?.citationData?.voilation?.dueDateParkingFee != null && !mIssuranceModel?.citationData?.voilation?.dueDateParkingFee.equals(
                                "null"
                            )
                        ) mIssuranceModel?.citationData?.voilation?.dueDateParkingFee.nullSafety("0")
                            .toDouble() else 0.0
                    createTicketRequest.invoiceFeeStructure = invoiceFeeStructure
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                val officerDetails = OfficerDetails()
                //officerDetails.setOfficerId(mData.getSiteOfficerId());
                officerDetails.badgeId = mIssuranceModel.citationData!!.officer!!.badgeId
                officerDetails.officer_lookup_code =
                    mIssuranceModel?.citationData?.officer?.officer_lookup_code.nullSafety()
                officerDetails.officer_name = AppUtils.getOfficerName(
                    mIssuranceModel?.citationData?.officer?.officerDetails.toString().trim()
                        .nullSafety()
                )
                if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DUNCAN, true) ||
                    BuildConfig.FLAVOR.equals(DuncanBrandingApp13()) ||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PILOTPITTSBURGPA, true) ||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MACKAY_SAMPLE, true)
                ) {
                    officerDetails.peo_fname = AppUtils.getPOEName(
                        mIssuranceModel?.citationData?.officer?.officerDetails.toString().trim()
                            .nullSafety(), 0
                    )
                    officerDetails.peo_lname = AppUtils.getPOEName(
                        mIssuranceModel?.citationData?.officer?.officerDetails.toString().trim()
                            .nullSafety(), 1
                    )
                    officerDetails.peo_name =
                        officerDetails.peo_fname + ", " + officerDetails.peo_lname
                }
                officerDetails.signature = ""
                try {
                    officerDetails.squad = mIssuranceModel.citationData!!.officer!!.squad
                    officerDetails.beat = mIssuranceModel.citationData!!.officer!!.beat
                    officerDetails.agency = mIssuranceModel.citationData!!.officer!!.agency
                    officerDetails.mShift = mIssuranceModel.citationData!!.officer!!.shift
                    officerDetails.zone =
                        if (mIssuranceModel.citationData!!.location!!.pcbZone != null)
                            mIssuranceModel.citationData!!.location!!.pcbZone else
                            if (mIssuranceModel.citationData!!.officer!!.zone != null)
                                mIssuranceModel.citationData!!.officer!!.zone else ""

                    val welcomeForm: WelcomeForm? = mDb!!.dbDAO!!.getWelcomeForm()
//                officerDetails.mDdeviceId = welcomeForm!!.officerDeviceId
                    officerDetails.mDdeviceId = welcomeForm!!.officerDeviceName
                    officerDetails.mDdeviceFriendlyName = welcomeForm!!.officerDeviceName
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
                val commentsDetails = CommentsDetails()
                commentsDetails.note1 = mIssuranceModel.citationData!!.locationNotes
                commentsDetails.note2 = mIssuranceModel.citationData!!.locationNotes1
                commentsDetails.remark1 = mIssuranceModel.citationData!!.locationRemarks
                commentsDetails.remark2 = mIssuranceModel.citationData!!.locationRemarks1
                createTicketRequest.commentsDetails = commentsDetails
                createTicketRequest.officerDetails = officerDetails
                val headerDetails = HeaderDetails()
                headerDetails.citationNumber = mIssuranceModel.citationData!!.ticketNumber
                headerDetails.timestamp = mIssuranceModel.citationData!!.ticketDate
                createTicketRequest.headerDetails = headerDetails
                createTicketRequest.lprNumber =
                    mIssuranceModel.citationData!!.vehicle!!.licensePlate
                createTicketRequest.code =
                    mIssuranceModel.citationData!!.voilation!!.code //mIssuranceModel.getCitationData().getCode());
                createTicketRequest.hearingDate =
                    mIssuranceModel?.citationData?.hearingDate
                createTicketRequest.ticketNo = mIssuranceModel.citationData!!.ticketNumber
                createTicketRequest.type = mIssuranceModel.citationData!!.ticketType
//            if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITYOFSANDIEGO,true)){
                createTicketRequest.timeLimitEnforcementObservedTime =
                    mIssuranceModel?.citationData?.officer?.observationTime
//            }else {
//                createTicketRequest.timeLimitEnforcementObservedTime =  sharedPreference.read(
//                    SharedPrefKey.TIMING_RECORD_FOR_OBSERVE_TIME, "")
//            }
                createTicketRequest.imageUrls = mImages
                createTicketRequest.notes = mIssuranceModel.citationData!!.locationNotes
                createTicketRequest.status = "Valid"
                createTicketRequest.citationStartTimestamp =
                    mIssuranceModel.citationData!!.startTime
                createTicketRequest.citationIssueTimestamp =
                    mIssuranceModel.citationData!!.issueTime
                createTicketRequest.isReissue = false
                mCreateTicketViewModel!!.hitCreateTicketApi(createTicketRequest)

                try {
                    if(LogUtil.WRITE_ALL_API_IN_LOGS_FILE) {
                        ApiLogsClass.writeApiPayloadTex(
                            BaseApplication.instance?.applicationContext!!,
                            "------------SEARCH Create API-----------------"
                        )
                        ApiLogsClass.writeApiPayloadTex(
                            BaseApplication.instance?.applicationContext!!,
                            "REQUEST: " + ObjectMapperProvider.instance.writeValueAsString(
                                createTicketRequest
                            )
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                val createTicketRequest = CreateMunicipalCitationTicketRequest()
                val locationDetails = MunicipalCitationLocationDetails()
                locationDetails.street = mIssuranceModel.citationData!!.location!!.street
                locationDetails.street_lookup_code =
                    mIssuranceModel?.citationData?.location?.mStreetLookupCode.nullSafety()
                locationDetails.block = mIssuranceModel.citationData!!.location!!.block
                locationDetails.side = mIssuranceModel.citationData!!.location!!.side
                locationDetails.meter = mIssuranceModel.citationData!!.location!!.meterName
                locationDetails.direction = mIssuranceModel.citationData!!.location!!.direction
                locationDetails.lot = mIssuranceModel.citationData!!.location!!.lot
                createTicketRequest.locationDetails = locationDetails
                val vehicleDetails = MunicipalCitationVehicleDetails()
                vehicleDetails.body_style = mIssuranceModel.citationData!!.vehicle!!.bodyStyle
                vehicleDetails.body_style_lookup_code =
                    mIssuranceModel?.citationData?.vehicle?.body_style_lookup_code.nullSafety()
                vehicleDetails.decal_year = mIssuranceModel.citationData!!.vehicle!!.decalYear
                vehicleDetails.decal_number = mIssuranceModel.citationData!!.vehicle!!.decalNumber
                vehicleDetails.vin_number = mIssuranceModel.citationData!!.vehicle!!.vinNumber
                vehicleDetails.make = mIssuranceModel.citationData!!.vehicle!!.make
                vehicleDetails.model_lookup_code =
                    mIssuranceModel?.citationData!!.vehicle!!.model_lookup_code.nullSafety("")
                vehicleDetails.color = mIssuranceModel.citationData!!.vehicle!!.color
                vehicleDetails.model = mIssuranceModel.citationData!!.vehicle!!.model
                vehicleDetails.model_lookup_code =
                    mIssuranceModel.citationData!!.vehicle!!.model_lookup_code.nullSafety("")
                vehicleDetails.lprNo = mIssuranceModel.citationData!!.vehicle!!.licensePlate
                vehicleDetails.state = mIssuranceModel.citationData!!.vehicle!!.state
                vehicleDetails.mLicenseExpiry =
                    if (mIssuranceModel.citationData!!.vehicle!!.expiration != null) mIssuranceModel.citationData!!.vehicle!!.expiration else ""
                createTicketRequest.vehicleDetails = vehicleDetails
                val violationDetails = MunicipalCitationViolationDetails()
                violationDetails.code =
                    mIssuranceModel.citationData!!.voilation!!.code //mAutoComTextViewCode.getEditableText().toString().trim());
                violationDetails.violation =
                    mIssuranceModel?.citationData?.voilation?.violationCode.nullSafety()
                violationDetails.description =
                    mIssuranceModel.citationData!!.voilation!!.locationDescr
                try {
                    violationDetails.fine =
                        mIssuranceModel.citationData!!.voilation!!.amount.nullSafety("0").toDouble()
                    violationDetails.late_fine =
                        mIssuranceModel.citationData!!.voilation!!.amountDueDate.nullSafety("0")
                            .toDouble()
                    violationDetails.due_15_days =
                        mIssuranceModel.citationData!!.voilation!!.dueDate.nullSafety("0")
                            .toDouble()
                    violationDetails.due_30_days =
                        mIssuranceModel.citationData!!.voilation!!.dueDate30.nullSafety("0")
                            .toDouble()
                    violationDetails.due_45_days =
                        mIssuranceModel.citationData!!.voilation!!.dueDate45.nullSafety("0")
                            .toDouble()
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
                createTicketRequest.violationDetails = violationDetails

                val motoristDetails = MotoristDetailsModel()
                motoristDetails.motoristFirstName =
                    mIssuranceModel.citationData?.municipalCitationMotoristDetailsModel?.motoristFirstName.nullSafety().uppercase()
                motoristDetails.motoristMiddleName =
                    mIssuranceModel.citationData?.municipalCitationMotoristDetailsModel?.motoristMiddleName.nullSafety().uppercase()
                motoristDetails.motoristLastName =
                    mIssuranceModel.citationData?.municipalCitationMotoristDetailsModel?.motoristLastName.nullSafety().uppercase()
                motoristDetails.motoristDateOfBirth =
                    mIssuranceModel.citationData?.municipalCitationMotoristDetailsModel?.motoristDateOfBirth.nullSafety().uppercase()
                motoristDetails.motoristDlNumber =
                    mIssuranceModel.citationData?.municipalCitationMotoristDetailsModel?.motoristDlNumber.nullSafety().uppercase()
                motoristDetails.motoristAddressBlock =
                    mIssuranceModel.citationData?.municipalCitationMotoristDetailsModel?.motoristAddressBlock.nullSafety().uppercase()
                motoristDetails.motoristAddressStreet =
                    mIssuranceModel.citationData?.municipalCitationMotoristDetailsModel?.motoristAddressStreet.nullSafety().uppercase()
                motoristDetails.motoristAddressCity =
                    mIssuranceModel.citationData?.municipalCitationMotoristDetailsModel?.motoristAddressCity.nullSafety().uppercase()
                motoristDetails.motoristAddressState =
                    mIssuranceModel.citationData?.municipalCitationMotoristDetailsModel?.motoristAddressState.nullSafety().uppercase()
                motoristDetails.motoristAddressZip =
                    mIssuranceModel.citationData?.municipalCitationMotoristDetailsModel?.motoristAddressZip.nullSafety().uppercase()

                createTicketRequest.motoristDetails = motoristDetails

                try {
                    val invoiceFeeStructure = MunicipalCitationInvoiceFeeStructure()
                    invoiceFeeStructure.mSaleTax =
                        if (mIssuranceModel?.citationData?.voilation?.dueDateCost != null && !mIssuranceModel?.citationData?.voilation?.dueDateCost.equals(
                                "null"
                            )
                        ) mIssuranceModel?.citationData?.voilation?.dueDateCost.nullSafety("0")
                            .toDouble() else 0.0
                    invoiceFeeStructure.mCitationFee =
                        if (mIssuranceModel?.citationData?.voilation?.dueDateCitationFee != null && !mIssuranceModel?.citationData?.voilation?.dueDateCitationFee.equals(
                                "null"
                            )
                        ) mIssuranceModel?.citationData?.voilation?.dueDateCitationFee.nullSafety("0")
                            .toDouble() else 0.0
                    invoiceFeeStructure.mParkingFee =
                        if (mIssuranceModel?.citationData?.voilation?.dueDateParkingFee != null && !mIssuranceModel?.citationData?.voilation?.dueDateParkingFee.equals(
                                "null"
                            )
                        ) mIssuranceModel?.citationData?.voilation?.dueDateParkingFee.nullSafety("0")
                            .toDouble() else 0.0
                    createTicketRequest.invoiceFeeStructure = invoiceFeeStructure
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                val officerDetails = MunicipalCitationOfficerDetails()
                //officerDetails.setOfficerId(mData.getSiteOfficerId());
                officerDetails.badgeId = mIssuranceModel.citationData!!.officer!!.badgeId
                officerDetails.officer_lookup_code =
                    mIssuranceModel?.citationData?.officer?.officer_lookup_code.nullSafety()
                officerDetails.officer_name = AppUtils.getOfficerName(
                    mIssuranceModel?.citationData?.officer?.officerDetails.toString().trim()
                        .nullSafety()
                )
                if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DUNCAN, true) ||
                    BuildConfig.FLAVOR.equals(DuncanBrandingApp13()) ||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PILOTPITTSBURGPA, true) ||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MACKAY_SAMPLE, true)
                ) {
                    officerDetails.peo_fname = AppUtils.getPOEName(
                        mIssuranceModel?.citationData?.officer?.officerDetails.toString().trim()
                            .nullSafety(), 0
                    )
                    officerDetails.peo_lname = AppUtils.getPOEName(
                        mIssuranceModel?.citationData?.officer?.officerDetails.toString().trim()
                            .nullSafety(), 1
                    )
                    officerDetails.peo_name =
                        officerDetails.peo_fname + ", " + officerDetails.peo_lname
                }
                officerDetails.signature = ""
                try {
                    officerDetails.squad = mIssuranceModel.citationData!!.officer!!.squad
                    officerDetails.beat = mIssuranceModel.citationData!!.officer!!.beat
                    officerDetails.agency = mIssuranceModel.citationData!!.officer!!.agency
                    officerDetails.mShift = mIssuranceModel.citationData!!.officer!!.shift
                    officerDetails.zone =
                        if (mIssuranceModel.citationData!!.location!!.pcbZone != null)
                            mIssuranceModel.citationData!!.location!!.pcbZone else
                            if (mIssuranceModel.citationData!!.officer!!.zone != null)
                                mIssuranceModel.citationData!!.officer!!.zone else ""

                    val welcomeForm: WelcomeForm? = mDb!!.dbDAO!!.getWelcomeForm()
//                officerDetails.mDdeviceId = welcomeForm!!.officerDeviceId
                    officerDetails.mDdeviceId = welcomeForm!!.officerDeviceName
                    officerDetails.mDdeviceFriendlyName = welcomeForm!!.officerDeviceName
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
                val commentsDetails = MunicipalCitationCommentsDetails()
                commentsDetails.note1 = mIssuranceModel.citationData!!.locationNotes
                commentsDetails.note2 = mIssuranceModel.citationData!!.locationNotes1
                commentsDetails.remark1 = mIssuranceModel.citationData!!.locationRemarks
                commentsDetails.remark2 = mIssuranceModel.citationData!!.locationRemarks1
                createTicketRequest.commentsDetails = commentsDetails
                createTicketRequest.officerDetails = officerDetails
                val headerDetails = MunicipalCitationHeaderDetails()
                headerDetails.citationNumber = mIssuranceModel.citationData!!.ticketNumber
                headerDetails.timestamp = mIssuranceModel.citationData!!.ticketDate
                createTicketRequest.headerDetails = headerDetails
                createTicketRequest.lprNumber =
                    mIssuranceModel.citationData!!.vehicle!!.licensePlate
                createTicketRequest.code =
                    mIssuranceModel.citationData!!.voilation!!.code //mIssuranceModel.getCitationData().getCode());
                createTicketRequest.hearingDate =
                    mIssuranceModel?.citationData?.hearingDate
                createTicketRequest.ticketNo = mIssuranceModel.citationData!!.ticketNumber
                createTicketRequest.type = mIssuranceModel.citationData!!.ticketType
//            if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITYOFSANDIEGO,true)){
                createTicketRequest.timeLimitEnforcementObservedTime =
                    mIssuranceModel?.citationData?.officer?.observationTime
//            }else {
//                createTicketRequest.timeLimitEnforcementObservedTime =  sharedPreference.read(
//                    SharedPrefKey.TIMING_RECORD_FOR_OBSERVE_TIME, "")
//            }
                createTicketRequest.imageUrls = mImages
                createTicketRequest.notes = mIssuranceModel.citationData!!.locationNotes
                createTicketRequest.status = "Valid"
                createTicketRequest.citationStartTimestamp =
                    mIssuranceModel.citationData!!.startTime
                createTicketRequest.citationIssueTimestamp =
                    mIssuranceModel.citationData!!.issueTime
                createTicketRequest.isReissue = false
                mCreateMunicipalCitationTicketViewModel?.hitCreateMunicipalCitationTicketApi(
                    createTicketRequest
                )
                try {
                    if(LogUtil.WRITE_ALL_API_IN_LOGS_FILE) {
                        ApiLogsClass.writeApiPayloadTex(
                            BaseApplication.instance?.applicationContext!!,
                            "------------SEARCH Create Municipal Citation API-----------------"
                        )
                        ApiLogsClass.writeApiPayloadTex(
                            BaseApplication.instance?.applicationContext!!,
                            "REQUEST: " + ObjectMapperProvider.instance.writeValueAsString(
                                createTicketRequest
                            )
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } else {
            mDb?.dbDAO?.updateCitationUploadStatus(1, mIssuranceModel.citationNumber)
            //LogUtil.printToastMSG(LprPreviewActivity.this, getString(R.string.err_msg_connection_was_refused));
        }
    }

    /* Call Api For Ticket Cancel */
    private fun callTicketCancelApi(mTicketId: String, mCitationNumber: String) {
        if (isInternetAvailable(this@SearchActivity)) {
            class UploadCancelTicket :
                    AsyncTask<Void?, Int?, OfflineCancelCitationModel?>() {
                override fun doInBackground(vararg voids: Void?): OfflineCancelCitationModel? {
                    try {
                        var cancelTicketDataList: List<OfflineCancelCitationModel?>? = ArrayList()
//                    var cancelTicketDataObject: OfflineCancelCitationModel?
                        if(mTicketId.toString().isEmpty()) {
                            cancelTicketDataList = mDb?.dbDAO?.getOfflineCancelCitation()
                            for (i in cancelTicketDataList!!.indices) {
                                if(cancelTicketDataList[i]!!.ticketNumber!!.equals(mCitationNumber)) {
                                    return cancelTicketDataList[i]
                                }
                            }
                        }else{
//
                            cancelTicketDataList = mDb?.dbDAO?.getOfflineCancelCitation()
//                        cancelTicketDataList = mDb?.dbDAO?.getOfflineCancelCitation("610023202")
                            for (i in cancelTicketDataList!!.indices) {
                                if(cancelTicketDataList[i]!!.ticketNumber!!.equals(mCitationNumber) &&
                                        cancelTicketDataList[i]!!.uploadedCitationId!!.equals("none")) {
                                    cancelTicketDataList!!.get(i)!!.uploadedCitationId = mTicketId
                                    return cancelTicketDataList[i]
                                }
                            }

//                        return cancelTicketDataList!!.get(0)!!
                        }

                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
                    return null
                }

                @SuppressLint("WrongThread")
                override fun onPostExecute(result: OfflineCancelCitationModel?) {
                    try {
                        if(result!=null) {
                            cancelTicketDataObject = result
                            if(!result!!.uploadedCitationId.toString().trim().isEmpty() &&
                                    !result!!.uploadedCitationId.toString().equals("none",ignoreCase = true)) {
                                Log.i("==>Offline:", "Called${ObjectMapperProvider.instance.writeValueAsString(result)}")
                                val ticketCancelRequest = TicketCancelRequest()
                                ticketCancelRequest.status = result!!.status.toString()
                                ticketCancelRequest.mNote = result!!.note.toString()
                                ticketCancelRequest.mReason = result!!.reason.toString()
                                ticketCancelRequest.mType = result!!.type.toString()
                                mUploadCitationIdForCancel = result!!.uploadedCitationId.toString()

                                mTicketCancelViewModel?.hitTicketCancelApi(ticketCancelRequest, result!!.uploadedCitationId.toString())
                            }
                        }
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
                }
            }
            UploadCancelTicket().execute()
        } else {
            LogUtil.printToastMSG(
                    applicationContext,
                    getString(R.string.err_msg_connection_was_refused)
            )
        }
    }

    /*Api response */
    private fun consumeResponse(apiResponse: ApiResponse, tag: String) {
        when (apiResponse.status) {
            Status.LOADING -> showProgressLoader(getString(R.string.scr_message_please_wait))
            Status.SUCCESS -> {
                if (!apiResponse.data!!.isNull) {
                    LogUtil.printLog(tag, apiResponse.data.toString())
                    try {
                        if (tag.equals(DynamicAPIPath.POST_TICKET_UPLOADE_STATUS_META, ignoreCase = true)) {

                            val responseModel = ObjectMapperProvider.fromJson(apiResponse.data.toString(), TicketUploadStatusResponse::class.java)

                            dismissLoader()
                            if (responseModel != null)
                            {
                                if(responseModel.citationUploaded!!)   {
                                    mDb!!.dbDAO!!.updateCitationUploadStatus(0, mCitationNumberId)
                                    callTicketCancelApi(responseModel.message!!.id.toString(),mCitationNumberId!!.toString())
                                }else{
                                    if(offlineStatus==2) {
//                                        moveToCitationFormWithUploadedCitation()
                                    }else if(offlineStatus==0)
                                    {
                                        offlineCitationData?.let { uploadOfflineImages(it) }
                                    }
                                }

                            } else {
//                                val message: String
//                                message = responseModel!!.message.nullSafety()
//                                showCustomAlertDialog(
//                                        mContext, getString(R.string.scr_lbl_activity),
//                                        message, getString(R.string.alt_lbl_OK),
//                                        getString(R.string.scr_btn_cancel), this
//                                )
                            }
                        }

                        if (tag.equals(DynamicAPIPath.POST_IMAGE, true)) {
                            try {

                                val responseModel = ObjectMapperProvider.fromJson(apiResponse.data.toString(), UploadImagesResponse::class.java)

                                if (responseModel != null && responseModel.status!!) {
                                    if (responseModel.data != null && responseModel.data!!.size > 0 && responseModel.data!![0].response != null && responseModel.data!![0].response!!.links != null && responseModel.data!![0].response!!.links!!.size > 0) {
                                        try {
                                            mDb?.dbDAO?.deleteTempImagesOfflineWithId(
                                                    offlineCitationImagesList?.get(
                                                            imageUploadSuccessCount)?.id.toString().nullSafety())
                                        } catch (e: java.lang.Exception) {
                                            e.printStackTrace()
                                        }
                                        imageUploadSuccessCount++
                                        mImages.add(responseModel.data!![0].response!!.links!![0])
                                        if (offlineCitationImagesList!=null && imageUploadSuccessCount == offlineCitationImagesList!!.size) {
                                            callCreateTicketApi(offlineCitationData!!)
                                        }else{
                                            dismissLoader()
                                        }
                                    } else {
                                        dismissLoader()
                                        AppUtils.showCustomAlertDialog(mContext, APIConstant.POST_IMAGE,
                                                getString(R.string.err_msg_something_went_wrong_imagearray),
                                                getString(R.string.alt_lbl_OK), getString(R.string.scr_btn_cancel), this)
                                    }
                                } else {
                                    dismissLoader()
                                    AppUtils.showCustomAlertDialog(mContext, APIConstant.POST_IMAGE,
                                            getString(R.string.err_msg_something_went_wrong),
                                            getString(R.string.alt_lbl_OK), getString(R.string.scr_btn_cancel), this)
                                }
                            } catch (e: java.lang.Exception) {
                                e.printStackTrace()
                            }
                        }

                        if (tag.equals(DynamicAPIPath.POST_CREATE_TICKET, true)) {

                            val responseModel = ObjectMapperProvider.fromJson(apiResponse.data.toString(), CreateTicketResponse::class.java)


                            try {
                                if(LogUtil.WRITE_ALL_API_IN_LOGS_FILE) {
                                    ApiLogsClass.writeApiPayloadTex(
                                        BaseApplication.instance?.applicationContext!!,
                                        "RESPONSE: " + ObjectMapperProvider.instance.writeValueAsString(
                                            responseModel
                                        )
                                    )
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                            if (responseModel != null && responseModel.success.nullSafety()) {
                                mDb?.dbDAO?.updateCitationUploadStatus(0, mCitationNumberId)
                                callTicketCancelApi(responseModel.data!!.id.toString(),mCitationNumberId!!.toString())
                            } else {
                                AppUtils.showCustomAlertDialog(mContext,
                                        APIConstant.POST_CREATE_TICKET,
                                        getString(R.string.err_msg_something_went_wrong),
                                        getString(R.string.alt_lbl_OK), getString(R.string.scr_btn_cancel), this@SearchActivity)
                            }
                            val fragment: Fragment = CitationFragment()
                            val fm = supportFragmentManager
                            val ft = fm.beginTransaction()
                            ft.replace(R.id.simpleFrameLayout, fragment)
                            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            ft.commit()

                            dismissLoader()
                        }

                        if (tag.equals(DynamicAPIPath.POST_CREATE_MUNICIPAL_CITATION_TICKET, true)) {


                            val responseModel = ObjectMapperProvider.fromJson(apiResponse.data.toString(), CreateMunicipalCitationTicketResponse::class.java)


                            try {
                                if(LogUtil.WRITE_ALL_API_IN_LOGS_FILE) {
                                    ApiLogsClass.writeApiPayloadTex(
                                        BaseApplication.instance?.applicationContext!!,
                                        "RESPONSE: " + ObjectMapperProvider.instance.writeValueAsString(
                                            responseModel
                                        )
                                    )
                                }
                                } catch (e: Exception) {
                                e.printStackTrace()
                            }
                            if (responseModel != null && responseModel.success.nullSafety()) {
                                mDb?.dbDAO?.updateCitationUploadStatus(0, mCitationNumberId)
                                callTicketCancelApi(responseModel.data!!.id.toString(),mCitationNumberId!!.toString())
                            } else {
                                AppUtils.showCustomAlertDialog(mContext,
                                        APIConstant.POST_CREATE_MUNICIPAL_CITATION_TICKET,
                                        getString(R.string.err_msg_something_went_wrong),
                                        getString(R.string.alt_lbl_OK), getString(R.string.scr_btn_cancel), this@SearchActivity)
                            }
                            val fragment: Fragment = CitationFragment()
                            val fm = supportFragmentManager
                            val ft = fm.beginTransaction()
                            ft.replace(R.id.simpleFrameLayout, fragment)
                            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            ft.commit()

                            dismissLoader()
                        }

                        if (tag.equals(DynamicAPIPath.POST_CANCEL, ignoreCase = true)) {


                            val responseModel = ObjectMapperProvider.fromJson(apiResponse.data.toString(), TicketCancelResponse::class.java)

                            if (responseModel != null && responseModel.success!!) {
                                dismissLoader()
                                cancelTicketDataObject!!?.let { mDb!!.dbDAO!!.deleteOfflineRescindCitation(it) }
                                mDb!!.dbDAO!!.deleteOfflineCancelCitation(mUploadCitationIdForCancel)
                                LogUtil.printToastMSG(this@SearchActivity, responseModel.msg)
                            } else {
                                dismissLoader()
                            }
                        }

                        //check Lot
                    } catch (e: Exception) {
                        LogUtil.printLog("kalyani", "new")
                        e.printStackTrace()
                        dismissLoader()
                        //token expires
                    }
                }
            }
            Status.ERROR -> {
                dismissLoader()
            }

            else -> {}
        }
    }


    override fun onYesButtonClick() {}
    override fun onNoButtonClick() {}
    override fun onYesButtonClickParam(msg: String?) {}
    override fun onCitationData(offlineCitationData: Datum?) {
        mCitationNumberId = offlineCitationData!!.ticketNo
        uploadOfflineCitation()
    }
}