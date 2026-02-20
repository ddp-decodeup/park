package com.parkloyalty.lpr.scan.ui.check_setup.activity.graph

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import butterknife.ButterKnife
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.parkloyalty.lpr.scan.R
import com.parkloyalty.lpr.scan.basecontrol.BaseActivity
import com.parkloyalty.lpr.scan.basecontrol.BaseApplication
import com.parkloyalty.lpr.scan.database.AppDatabase
import com.parkloyalty.lpr.scan.interfaces.CustomDialogHelper
import com.parkloyalty.lpr.scan.network.ApiResponse
import com.parkloyalty.lpr.scan.network.DynamicAPIPath
import com.parkloyalty.lpr.scan.network.Status
import com.parkloyalty.lpr.scan.ui.boot.model.ResponseBoot
import com.parkloyalty.lpr.scan.ui.check_setup.activity.model.LprHitGenetecViewModel
import com.parkloyalty.lpr.scan.ui.check_setup.model.DataFromLprViewModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DropdownDatasetResponse
import com.parkloyalty.lpr.scan.util.AppUtils
import com.parkloyalty.lpr.scan.util.LogUtil
import com.parkloyalty.lpr.scan.utils.ObjectMapperProvider
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class LprHitsActivity : BaseActivity(), OnMapReadyCallback, CustomDialogHelper {
    private var mContext: Context? = null
    private var markerPoints: ArrayList<LatLng>? = null
    private var mDb: AppDatabase? = null
    private var APIKEY: String? = null
    private var mMap: GoogleMap? = null

    private val lprHitGenetecViewModel: LprHitGenetecViewModel? by viewModels()

    companion object {
        private const val MY_PERMISSIONS_REQUEST = 32
        private const val MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 10
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lpr_hits)
        setFullScreenUI()
        ButterKnife.bind(this)
        //for full screen toolbar
        requestPermission(Manifest.permission.ACCESS_FINE_LOCATION)

        mContext = this
        markerPoints = ArrayList()
        mDb = BaseApplication.instance?.getAppDatabase()
        setToolbar()
        addObservers()

    }

    override fun onResume() {
        super.onResume()
        lprHitRequest()

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
        APIKEY = resources.getString(R.string.google_maps_key)
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


    private fun addObservers() {
        lprHitGenetecViewModel?.response?.observe(this, lprHitResponseObserver)
    }

    override fun removeObservers() {
        super.removeObservers()
        lprHitGenetecViewModel?.response?.removeObserver(lprHitResponseObserver)
    }


    private fun lprHitRequest()
    {
//        https://api.parkloyalty.com/parking-timing/mark?arrival_status=Open
//        // &issue_ts_from=2023-10-02T03:00:00Z&issue_ts_to=2023-10-03T03:00:00Z&
//        // vendor_name=Genetec

        val timeMilli1 = AppUtils.getStartTDate("")
        val timeMilli2 = AppUtils.getEndTDate("")
        val mReq = "arrival_status=Open&" +
                "issue_ts_from=" + timeMilli1 + "&issue_ts_to=" + timeMilli2 + "&vendor_name=Genetec"
        lprHitGenetecViewModel?.hitGenetecHitApi(mReq)
    }

    private val lprHitResponseObserver = Observer { apiResponse: ApiResponse? ->
        consumeResponse(
            apiResponse,
            DynamicAPIPath.GET_GENETEC_HIT
        )
    }


    /*Api response */
    private fun consumeResponse(apiResponse: ApiResponse?, tag: String) {
        when (apiResponse?.status) {
            Status.LOADING -> showProgressLoader(getString(R.string.scr_message_please_wait))
            Status.SUCCESS -> {
                dismissLoader()
                if (!apiResponse.data!!.isNull) {
                    LogUtil.printLog(tag, apiResponse.data.toString())
                    try {
                        if (tag.equals(DynamicAPIPath.SUMIT_BOOT, ignoreCase = true)) {

                            val responseModel = ObjectMapperProvider.fromJson(apiResponse.data.toString(), ResponseBoot::class.java)

                            if (responseModel != null && responseModel.isSuccess) {
                                try {
                                    AppUtils.showCustomAlertDialog(
                                        mContext, "Boot",
                                        "Submit boot successfully", getString(R.string.alt_lbl_OK),
                                        getString(R.string.scr_btn_cancel), this
                                    )
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            } else {
                                AppUtils.showCustomAlertDialog(
                                    mContext,
                                    "Boot",
                                    getString(R.string.err_msg_something_went_wrong),
                                    getString(R.string.alt_lbl_OK),
                                    getString(R.string.scr_btn_cancel),
                                    this
                                )
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        AppUtils.showCustomAlertDialog(
                            mContext,
                            "Boot",
                            getString(R.string.err_msg_something_went_wrong),
                            getString(R.string.alt_lbl_OK),
                            getString(R.string.scr_btn_cancel),
                            this
                        )
                    }
                }
            }
            Status.ERROR -> {
                dismissLoader()
                AppUtils.showCustomAlertDialog(
                    mContext,
                    "Boot",
                    getString(R.string.err_msg_something_went_wrong),
                    getString(R.string.alt_lbl_OK),
                    getString(R.string.scr_btn_cancel),
                    this
                )
            }
            else -> {
            }
        }
    }
    /**
     * Request app permission for API 23/ Android 6.0
     *
     * @param permission
     */
    private fun requestPermission(permission: String) {
        if (ContextCompat.checkSelfPermission(
                this,
                permission
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(permission),
                MY_PERMISSIONS_REQUEST
            )
        }
    }

    override fun onMapReady(p0: GoogleMap) {
        mMap = p0
        mMap?.uiSettings?.isZoomControlsEnabled = true
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        mMap?.isMyLocationEnabled = true
        setMarkerOnMap()
    }


    //set Map Data
    private fun setMarkerOnMap() {
        try {
            if (mMap != null ) {

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    override fun onDestroy() {
        mContext = null
        super.onDestroy()
    }

}