package com.parkloyalty.lpr.scan.ui.check_setup.activity.graph

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.location.Location
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.parkloyalty.lpr.scan.R
import com.parkloyalty.lpr.scan.basecontrol.BaseActivity
import com.parkloyalty.lpr.scan.basecontrol.BaseApplication
import com.parkloyalty.lpr.scan.database.AppDatabase
import com.parkloyalty.lpr.scan.database.Singleton
import com.parkloyalty.lpr.scan.extensions.nullSafety
import com.parkloyalty.lpr.scan.interfaces.Constants
import com.parkloyalty.lpr.scan.interfaces.CustomDialogHelper
import com.parkloyalty.lpr.scan.interfaces.SharedPrefKey
import com.parkloyalty.lpr.scan.network.ApiResponse
import com.parkloyalty.lpr.scan.network.DynamicAPIPath
import com.parkloyalty.lpr.scan.network.NetworkCheck.isInternetAvailable
import com.parkloyalty.lpr.scan.network.Status
import com.parkloyalty.lpr.scan.ui.check_setup.activity.graph.model.*
import com.parkloyalty.lpr.scan.ui.check_setup.activity.graph.model.check_setup.GetViolationCountResponse
import com.parkloyalty.lpr.scan.ui.check_setup.activity.graph.model.check_setup.GetViolationResonse
import com.parkloyalty.lpr.scan.ui.check_setup.adapter.ActivityAdapter
import com.parkloyalty.lpr.scan.ui.check_setup.adapter.ViolationAdapter
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DropdownDatasetResponse
import com.parkloyalty.lpr.scan.ui.login.activity.WelcomeActivity
import com.parkloyalty.lpr.scan.util.AppUtils
import com.parkloyalty.lpr.scan.util.DATASET_SETTINGS_LIST
import com.parkloyalty.lpr.scan.util.DirectionParser
import com.parkloyalty.lpr.scan.util.LogUtil
import com.parkloyalty.lpr.scan.util.Util
import com.parkloyalty.lpr.scan.utils.ObjectMapperProvider
import kotlinx.coroutines.async
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.text.DecimalFormat
import java.text.ParseException
import java.util.*
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

/*Phase 2*/
@AndroidEntryPoint
class MyActivityActivity : BaseActivity(), OnMapReadyCallback, CustomDialogHelper {
    private var mContext: Context? = null
    private val MAX_X_VALUE = 6
    private var MAX_Y_VALUE = 300
    private var MAX_Y_VALUE_LINE: Long = 300
    private var DIFFERENCE = 0
    private val MIN_Y_VALUE = 0
    private val SET_LABEL = "Number of citations"
    private val DAYS = arrayOf("Scan", "Tickets", "Timing", "permits", "scofflaws", "Drive off")
    private val DATA_BAR_GRAPH =
        arrayOfNulls<String>(6) //{"","09:00", "10:00", "11:00", "12:00", "13:00"};
    private val TIME_LINE_GRAPH = arrayOf(
        "0",
        "0",
        "01:00",
        "02:00",
        "03:00",
        "04:00",
        "05:00",
        "06:00",
        "07:00",
        "08:00",
        "09:00",
        "10:00",
        "11:00",
        "12:00",
        "13:00",
        "14:00",
        "15:00",
        "16:00",
        "17:00",
        "18:00",
        "19:00",
        "20:00",
        "22:00",
        "23:00",
        "24:00"
    )

    @JvmField
    @BindView(R.id.fragment_verticalbarchart_chart)
    var chartBarGraph: BarChart? = null

    @JvmField
    @BindView(R.id.activity_main_linechart)
    var chartLineGraph: LineChart? = null

    @JvmField
    @BindView(R.id.rvHistory)
    var mRecylerViewHistory: RecyclerView? = null

    @JvmField
    @BindView(R.id.tvViolation)
    var mRecylerViewViolation: RecyclerView? = null

    @JvmField
    @BindView(R.id.tvDate)
    var mTextViewDate: AppCompatTextView? = null
    private val mMarkerArray = ArrayList<LatLng>()
    private var mMap: GoogleMap? = null

    // creating array list for adding all our locations.
    private var mScanList: List<Long>? = ArrayList()
    private var mTimingList: List<Long>? = ArrayList()
    private var mCitationList: List<Long>? = ArrayList()
    private val pricesHigh: MutableList<Entry> = ArrayList()
    private val pricesLow: MutableList<Entry> = ArrayList()
    private val pricesClose: MutableList<Entry> = ArrayList()
    private var activityList: List<ActivityCountData>? = ArrayList()
    private var mViolationList: List<GetViolationResonse>? = ArrayList()
    private var mRouteData: ArrayList<DataItem>? = ArrayList()
    private val locationArrayList: MutableList<LatLng> = ArrayList()
    private val LoginArrayList: MutableList<LatLng> = ArrayList()
    private val LogoutArrayList: MutableList<LatLng> = ArrayList()
    private val CitationArrayList: MutableList<LatLng> = ArrayList()
    private val ActivityArrayList: MutableList<LatLng> = ArrayList()
    private var mActivityAdapter: ActivityAdapter? = null
    private var mViolationAdapter: ViolationAdapter? = null

    private val mRouteViewModel: RouteViewModel? by viewModels()
    private val mGetBarCountViewModel: GetBarCountViewModel? by viewModels()
    private val mActivityCountViewModel: ActivityCountViewModel? by viewModels()
    private val mGetViolationCountViewModel: GetViolationCountViewModel? by viewModels()
    private val mGetCountLineViewModel: GetCountLineViewModel? by viewModels()

    private var TotalScan: Long = 0
    private var TotalTim: Long = 0
    private var TotalCit: Long = 0
    private var mDb: AppDatabase? = null
    private var mZone = "CST"
    private var shift = ""
    private var markerPoints: ArrayList<LatLng>? = null
    private var polylineOptions1: PolylineOptions? = null

    //for reference
    //https://www.geeksforgeeks.org/how-to-create-a-barchart-in-android/
    //https://learntodroid.com/how-to-display-a-bar-chart-in-your-android-app/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_activity)
        setFullScreenUI()
        ButterKnife.bind(this)
        //for full screen toolbar
        requestPermission(Manifest.permission.ACCESS_FINE_LOCATION)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
        mContext = this
        markerPoints = ArrayList()
        mDb = BaseApplication.instance?.getAppDatabase()

        addObservers()
        getTimeZoneFromDatabase()
        //callCountBarGraphApi();
        callCountLineGraphApi()
        try {
            callBarCountApi()
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        callActivityCountApi()
        callViolationCountApi()
        callRouteApi("Route")
        setToolbar()
        mTextViewDate?.text = AppUtils.getCurrentDateTime()
    }

    //set location marker
    private fun setLocationDetails() {
        for (i in mRouteData?.indices!!) {
            if (mRouteData!![i].locationUpdatetype == "login") {
                LoginArrayList.add(
                    LatLng(
                        mRouteData!![i].latitude, mRouteData!![i].longitude
                    )
                )
            }
            if (mRouteData!![i].locationUpdatetype == "logout") {
                LogoutArrayList.add(
                    LatLng(
                        mRouteData!![i].latitude, mRouteData!![i].longitude
                    )
                )
            }
            if (mRouteData!![i].locationUpdatetype == "citation") {
                CitationArrayList.add(
                    LatLng(
                        mRouteData!![i].latitude, mRouteData!![i].longitude
                    )
                )
            }
            if (mRouteData!![i].locationUpdatetype == "break") {
                ActivityArrayList.add(
                    LatLng(
                        mRouteData!![i].latitude, mRouteData!![i].longitude
                    )
                )
            }
            if (mRouteData!![i].locationUpdatetype == "regular") {
                //locationArrayList.clear();
                locationArrayList.add(
                    LatLng(
                        mRouteData!![i].latitude, mRouteData!![i].longitude
                    )
                )
            }
        }
    }

    //            shift = mDb.getDbDAO().getWelcomeForm().getOfficerShift();
    private fun getTimeZoneFromDatabase() {
        try {
            if (Singleton.getDataSetList(DATASET_SETTINGS_LIST, getMyDatabase()) != null) {
                mZone = Singleton.getDataSetList(DATASET_SETTINGS_LIST, getMyDatabase())!![0].mValue.nullSafety()
            }
            //            shift = mDb.getDbDAO().getWelcomeForm().getOfficerShift();
            shift = sharedPreference.read(SharedPrefKey.LOGIN_SHIFT, "").nullSafety()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initGraphs() {
        //set bar graph------
        val data = createChartData()
        configureChartAppearance()
        if (data != null) {
            prepareChartData(data)
        }
        chartBarGraph?.invalidate()
        //checkLocationPermission();
    }

    //set line graph
    private fun initLineGraph() {
        if (mCitationList!!.isNotEmpty()) {
            for (i in mCitationList?.indices!!) {
                pricesClose.add(
                    Entry(
                        (i + 1).toFloat(), mCitationList!![i].toFloat()
                    )
                )
                TotalCit = TotalCit + mCitationList!![i]
                LogUtil.printLog("TotalCit", TotalCit.toString())
            }
        }
        if (mScanList!!.isNotEmpty()) {
            for (i in mScanList!!.indices) {
                pricesHigh.add(
                    Entry(
                        (i + 1).toFloat(), mScanList!![i].toFloat()
                    )
                )
                TotalScan = TotalScan + mScanList!![i]
                LogUtil.printLog("TotalScan", TotalScan.toString())
            }
        }
        if (mTimingList!!.isNotEmpty()) {
            for (i in mTimingList!!.indices) {
                pricesLow.add(
                    Entry(
                        (i + 1).toFloat(), mTimingList!![i].toFloat()
                    )
                )
                TotalTim = TotalTim + mTimingList!![i]
                LogUtil.printLog("TotalTim", TotalTim.toString())
            }
        }
        configureLineChart()
        setLineChartData()
    }

    private val activityCountResponseObserver = Observer { apiResponse: ApiResponse ->
        consumeResponse(
            apiResponse,
            DynamicAPIPath.POST_ACTIVITY_UPDATES
        )
    }
    private val violationCountResponseObserver = Observer { apiResponse: ApiResponse ->
        consumeResponse(
            apiResponse,
            DynamicAPIPath.POST_VIOLATION_COUNT
        )
    }
    private val countLineResponseObserver = Observer { apiResponse: ApiResponse ->
        consumeResponse(
            apiResponse,
            DynamicAPIPath.POST_GET_COUNT_LINE
        )
    }
    private val barCountResponseObserver = Observer { apiResponse: ApiResponse ->
        consumeResponse(
            apiResponse,
            DynamicAPIPath.POST_GET_BAR_COUNT
        )
    }
    private val routeResponseObserver = Observer { apiResponse: ApiResponse ->
        consumeResponse(
            apiResponse,
            DynamicAPIPath.GET_ROUTE_DATA
        )
    }

    private fun addObservers() {
        mActivityCountViewModel?.response?.observe(this, activityCountResponseObserver)
        mGetViolationCountViewModel?.response?.observe(this, violationCountResponseObserver)
        mGetCountLineViewModel?.response?.observe(this, countLineResponseObserver)
        mGetBarCountViewModel?.response?.observe(this, barCountResponseObserver)
        mRouteViewModel?.response?.observe(this, routeResponseObserver)
    }

    override fun removeObservers() {
        super.removeObservers()
        mActivityCountViewModel?.response?.removeObserver(activityCountResponseObserver)
        mGetViolationCountViewModel?.response?.removeObserver(violationCountResponseObserver)
        mGetCountLineViewModel?.response?.removeObserver(countLineResponseObserver)
        mGetBarCountViewModel?.response?.removeObserver(barCountResponseObserver)
        mRouteViewModel?.response?.removeObserver(routeResponseObserver)
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

    override fun onYesButtonClick() {}
    override fun onNoButtonClick() {}
    override fun onYesButtonClickParam(msg: String?) {}
    inner class MyValueFormatter : ValueFormatter() {
        private val mFormat: DecimalFormat
        override fun getFormattedValue(value: Float): String {
            return mFormat.format(value.toDouble())
        }

        init {
            mFormat = DecimalFormat("#")
        }
    }

    //configure bar graph
    private fun configureChartAppearance() {
        chartBarGraph?.description?.isEnabled = false
        chartBarGraph?.description?.textAlign = Paint.Align.LEFT
        chartBarGraph?.setDrawValueAboveBar(true)
        chartBarGraph?.animateY(1000)
        val xAxis = chartBarGraph!!.xAxis
        //set labels des to bottom
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        //set x axis label values
        xAxis.labelRotationAngle = 90f
        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return DAYS[Math.floor(value.toDouble()).toInt()]
            }
        }
        val axisLeft = chartBarGraph?.axisLeft
        //axisLeft.setGranularity(0.6f);
        axisLeft?.axisMinimum = Math.floor(0.0).toFloat()
        axisLeft?.axisMaximum = Math.floor(MAX_Y_VALUE.toDouble()).toFloat()
        //customize - y axis rows numbers
        axisLeft?.setLabelCount(4, true)
        axisLeft?.setDrawGridLines(true)

        //set right side
        val axisRight = chartBarGraph?.axisRight
        //axisRight.setGranularity(0.6f);
        axisRight?.axisMinimum = 0f
        axisRight?.axisMaximum = 0f
        axisRight?.setLabelCount(0, false)
        axisRight?.setDrawGridLines(true)
        axisRight?.axisMaximum = 0f
        chartBarGraph?.setDrawValueAboveBar(true)
        chartBarGraph?.xAxis?.granularity = 1f
        chartBarGraph?.xAxis?.isGranularityEnabled = true
        val mv = CustomMarkerView(this, R.layout.custom_marker_view_layout)
        chartBarGraph?.marker = mv
    }

    //create bar graph with DATA
    private fun createChartData(): BarData {
        val values = ArrayList<BarEntry>()
        for (i in 0 until MAX_X_VALUE) {
            val r = Random()
            val x = i.toFloat()
            val y = DATA_BAR_GRAPH[i]!!.toInt().toFloat()
            values.add(BarEntry(x, y))
        }
        val set1 = BarDataSet(values, SET_LABEL)
        set1.barBorderColor = Color.YELLOW
        set1.color = resources.getColor(R.color.deep_yellow)
        set1.setDrawValues(false)
        val dataSets = ArrayList<IBarDataSet>()
        set1.isHighlightEnabled = false
        set1.setDrawValues(true)
        dataSets.add(set1)
        return BarData(dataSets)
    }

    /* Call Api For Activity Count Type */
    private fun callActivityCountApi() {
        if (isInternetAvailable(this@MyActivityActivity)) {
            val timeline = ActivityLogRequest()
            val arrayTimeline = BarCountArrayTimeline()
            arrayTimeline.tz = Util.getDefaultTimeZoneID()
            timeline.countArrayTimeline = arrayTimeline
            val shiftEndURL = "&shift=$shift"
            mActivityCountViewModel!!.hitActivityCountApi(shiftEndURL)
        } else {
            LogUtil.printToastMSG(
                applicationContext,
                getString(R.string.err_msg_connection_was_refused)
            )
        }
    }

    /* Call Api For bar Count Type */
    @Throws(ParseException::class)
    private fun callBarCountApi() {
        if (isInternetAvailable(this@MyActivityActivity)) {
            val shiftEndURL = "&shift=$shift"
            mGetBarCountViewModel!!.hitGetBarCountApi(shiftEndURL)
        } else {
            LogUtil.printToastMSG(
                applicationContext,
                getString(R.string.err_msg_connection_was_refused)
            )
        }
    }

    /* Call Api For Violation Count Type */
    private fun callViolationCountApi() {
        val timeMilli1 = AppUtils.getStartT(mZone)
        val timeMilli2 = AppUtils.getEndT(mZone)
        if (isInternetAvailable(this@MyActivityActivity)) {
            val timeline = ActivityLogRequest()
            val arrayTimeline = BarCountArrayTimeline()
            arrayTimeline.tz = Util.getDefaultTimeZoneID()
            arrayTimeline.timestampStart = timeMilli1
            arrayTimeline.timestampEnd = timeMilli2
            timeline.countArrayTimeline = arrayTimeline
            val shiftEndURL = "&shift=$shift"
            mGetViolationCountViewModel!!.hitViolationCountApi(shiftEndURL)
        } else {
            LogUtil.printToastMSG(
                applicationContext,
                getString(R.string.err_msg_connection_was_refused)
            )
        }
    }

    /* Call Api For Count Line Graph */
    private fun callCountLineGraphApi() {
        val addEndPoint = "&shift=$shift"
        if (isInternetAvailable(this@MyActivityActivity)) {
            mGetCountLineViewModel!!.hitGetCountLineApi(addEndPoint)
        } else {
            LogUtil.printToastMSG(
                applicationContext,
                getString(R.string.err_msg_connection_was_refused)
            )
        }
    }

    /* Call Api For route */
    private fun callRouteApi(mType: String) {
        var mType: String? = mType
        if (isInternetAvailable(this@MyActivityActivity)) {
//            mType = "tz=Asia/Kolkata";
//            mType = "tz="+Util.getDefaultTimeZoneID();
            mType = "shift=" + sharedPreference.read(SharedPrefKey.LOGIN_SHIFT, "")
            mRouteViewModel!!.hitGetRouteApi(mType)
        } else {
            LogUtil.printToastMSG(
                applicationContext,
                getString(R.string.err_msg_connection_was_refused)
            )
        }
    }

    //prepare bar graph details
    private fun prepareChartData(data: BarData) {
        data.setValueTextSize(12f)
        data.setValueTextColor(resources.getColor(R.color.light_grey_graph))
        //customize - set width
        data.barWidth = 0.4f
        data.setValueFormatter(MyValueFormatter())
        chartBarGraph!!.data = data
        chartBarGraph!!.invalidate()
    }

    private fun setAdapterForActivityList() {
        if (activityList!!.isNotEmpty()) {
            mActivityAdapter = ActivityAdapter(
                mContext!!,
                activityList,
                object : ActivityAdapter.ListItemSelectListener {
                    override fun onItemClick(mData: ActivityCountData?) {

                    }
                })
            mRecylerViewHistory?.setHasFixedSize(true)
            mRecylerViewHistory?.layoutManager =
                LinearLayoutManager(mContext, RecyclerView.VERTICAL, false)
            mRecylerViewHistory?.adapter = mActivityAdapter
            mRecylerViewHistory?.visibility = View.VISIBLE
        } else {
            mRecylerViewHistory?.visibility = View.GONE
        }
    }

    private fun setAdapterForViolationList() {
        if (mViolationList!!.isNotEmpty()) {
            mViolationAdapter = ViolationAdapter(
                mContext!!,
                mViolationList,
                object : ViolationAdapter.ListItemSelectListener {
                    override fun onItemClick(mDataTicket: ActivityCountData?) {

                    }
                })
            mRecylerViewViolation?.setHasFixedSize(true)
            mRecylerViewViolation?.layoutManager =
                LinearLayoutManager(mContext, RecyclerView.VERTICAL, false)
            mRecylerViewViolation?.adapter = mViolationAdapter
            mRecylerViewViolation?.visibility = View.VISIBLE
        } else {
            mRecylerViewViolation?.visibility = View.GONE
        }
    }

    //configure line graph
    private fun configureLineChart() {
        ioScope.async {
            var maxCitation: Long = 0
            var maxScan: Long = 0
            var maxTiming: Long = 0
            if (mCitationList != null) {
                try {
                    maxCitation = Collections.max(mCitationList)
                } catch (e: Exception) {
                }
            }
            if (mScanList != null) {
                try {
                    maxScan = Collections.max(mScanList)
                } catch (e: Exception) {
                }
            }
            if (mTimingList != null) {
                try {
                    maxTiming = Collections.max(mTimingList)
                } catch (e: Exception) {
                }
            }
            MAX_Y_VALUE_LINE = if (maxCitation >= maxScan && maxCitation >= maxTiming) {
                maxCitation + 4
            } else if (maxScan >= maxCitation && maxScan >= maxTiming) {
                maxScan + 4
            } else {
                maxTiming + 4
            }

            //MAX_Y_VALUE_LINE = (int)(((TotalCit/2) + (TotalScan/2) + (TotalTim/2))*(1.5)) ;
            LogUtil.printLog("MAX_Y_VALUE_LINE", MAX_Y_VALUE_LINE.toString())
            val desc = Description()
            //desc.setText("Stock Price History");
            //desc.setTextSize(28);
            chartLineGraph?.description?.isEnabled = false
            chartLineGraph?.description?.textAlign = Paint.Align.LEFT
            chartLineGraph?.animateY(1000)
            val xAxis = chartLineGraph?.xAxis
            //set labels des to bottom
            xAxis?.position = XAxis.XAxisPosition.BOTTOM
            xAxis?.setDrawGridLines(false)
            xAxis?.axisMinimum = 0f
            xAxis?.axisMaximum = 25f
            xAxis?.setDrawLabels(true)
            xAxis?.setLabelCount(25, true)
            val xEntrys = ArrayList(Arrays.asList(*TIME_LINE_GRAPH).subList(1, TIME_LINE_GRAPH.size))
            xAxis?.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return try {
                        xEntrys[Math.floor(value.toDouble()).toInt()]
                    } catch (e: Exception) {
                        " "
                    }
                }
            }

            //xAxis.setValueFormatter(new IndexAxisValueFormatter(TIME_LINE_GRAPH));
            //set x axis label values
            xAxis?.labelRotationAngle = 90f

            //set y axis left
            val axisLeft = chartLineGraph?.axisLeft
            //axisLeft.setGranularity(0.6f);
            axisLeft?.axisMinimum = 0f
            axisLeft?.axisMaximum = MAX_Y_VALUE_LINE.toFloat()
            //customize - y axis rows numbers
            axisLeft?.setLabelCount(4, true)
            axisLeft?.setDrawGridLines(true)

            //set right side
            val axisRight = chartLineGraph?.axisRight
            //axisRight.setGranularity(0.6f);
            axisRight?.axisMinimum = 0f
            axisRight?.axisMaximum = 0f
            axisRight?.setLabelCount(0, false)
            axisRight?.setDrawGridLines(true)
            axisRight?.axisMaximum = 0f
            chartLineGraph?.isHighlightPerTapEnabled = true
            chartLineGraph?.notifyDataSetChanged()
            chartLineGraph?.invalidate()
            val mv = CustomMarkerView(this@MyActivityActivity, R.layout.custom_marker_view_layout)
            chartLineGraph?.marker = mv
        }
    }

    //create line graph with DATA
    private fun setLineChartData() {
        ioScope.async {
            val dataSets = ArrayList<ILineDataSet>()
            val closeLineDataSet = LineDataSet(pricesHigh, "Scans")
            closeLineDataSet.setDrawCircles(true)
            closeLineDataSet.circleRadius = 0f
            closeLineDataSet.setDrawValues(false)
            closeLineDataSet.lineWidth = 1f
            closeLineDataSet.setDrawCircles(false)
            closeLineDataSet.color = resources.getColor(R.color.deep_yellow)
            closeLineDataSet.setCircleColor(resources.getColor(R.color.deep_yellow))
            dataSets.add(closeLineDataSet)
            val highLineDataSet = LineDataSet(pricesLow, "Timing")
            highLineDataSet.setDrawCircles(true)
            highLineDataSet.circleRadius = 0f
            highLineDataSet.setDrawValues(false)
            highLineDataSet.lineWidth = 1f
            highLineDataSet.setDrawCircles(false)
            highLineDataSet.color = resources.getColor(R.color.graph_1)
            highLineDataSet.setCircleColor(resources.getColor(R.color.graph_1))
            dataSets.add(highLineDataSet)
            val lowLineDataSet = LineDataSet(pricesClose, "Tickets")
            lowLineDataSet.setDrawCircles(true)
            lowLineDataSet.circleRadius = 0f
            lowLineDataSet.setDrawValues(false)
            lowLineDataSet.lineWidth = 1f
            lowLineDataSet.setDrawCircles(false)
            lowLineDataSet.color = resources.getColor(R.color.graph_2Red)
            lowLineDataSet.setCircleColor(resources.getColor(R.color.graph_2Red))
            dataSets.add(lowLineDataSet)
            val lineData = LineData(dataSets)
            lineData.setValueFormatter(MyValueFormatter())
            chartLineGraph?.data = lineData
            chartLineGraph?.invalidate()
        }
    }

    //location permission
    private fun checkLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this@MyActivityActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
                )
            } else {
                val mLoc: Location? = null
                mLoc?.latitude = 79.389900
                mLoc?.longitude = 20.498831
                //checkUserLocation(mLoc);
            }
        } else {
            val mLoc: Location? = null
            mLoc?.latitude = 79.389900
            mLoc?.longitude = 20.498831
            // checkUserLocation(mLoc);
        }
    }


    /*Api response */
    private fun consumeResponse(apiResponse: ApiResponse, tag: String) {
        when (apiResponse.status) {
            Status.LOADING -> showProgressLoader(getString(R.string.scr_message_please_wait))
            Status.SUCCESS -> {
                dismissLoader()
                if (!apiResponse.data!!.isNull) {
                    LogUtil.printLog(tag, apiResponse.data.toString())
                    try {
                        //"Scan", "Tickets", "Timing", "permits", "scofflaws", "Drive off"
                        if (tag.equals(DynamicAPIPath.POST_GET_COUNT_LINE, ignoreCase = true)) {
                            try {

                                val responseModel = ObjectMapperProvider.fromJson(apiResponse.data.toString(), LineGraphResponse::class.java)

                                if (responseModel != null && responseModel.status!!) {
                                    if (responseModel.data!![0].response!!.isNotEmpty()) {
                                        var i = 0
                                        while (i < responseModel.data!![0].response!!.size) {
                                            if (responseModel.data!![0].response!![i].datasetName == "citations") {
                                                mCitationList =
                                                    responseModel.data!![0].response!![i].aggregate
                                            }
                                            if (responseModel.data!![0].response!![i].datasetName == "scans") {
                                                mScanList =
                                                    responseModel.data!![0].response!![i].aggregate
                                            }
                                            if (responseModel.data!![0].response!![i].datasetName == "timings") {
                                                mTimingList =
                                                    responseModel.data!![0].response!![i].aggregate
                                            }
                                            i++
                                        }
                                        initLineGraph()
                                    }
                                } else if (responseModel != null && !responseModel.status!!) {
                                    // Not getting response from server..!!
                                    AppUtils.showCustomAlertDialog(
                                        mContext, "POST_GET_COUNT_LINE",
                                        responseModel.message, getString(R.string.alt_lbl_OK),
                                        getString(R.string.scr_btn_cancel), this
                                    )
                                } else {
                                    AppUtils.showCustomAlertDialog(
                                        mContext,
                                        "POST_GET_COUNT_LINE",
                                        getString(R.string.err_msg_something_went_wrong),
                                        getString(R.string.alt_lbl_OK),
                                        getString(R.string.scr_btn_cancel),
                                        this
                                    )
                                    dismissLoader()
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        } else if (tag.equals(
                                DynamicAPIPath.POST_GET_BAR_COUNT,
                                ignoreCase = true
                            )
                        ) {

                            val responseModel = ObjectMapperProvider.fromJson(apiResponse.data.toString(), GetBarCountResponse::class.java)

                            if (responseModel != null && responseModel.status!!) {
                                if (responseModel.data!!.isNotEmpty()) {
                                    DATA_BAR_GRAPH[0] = responseModel.data!![0].scans.toString()
                                    DATA_BAR_GRAPH[1] = responseModel.data!![0].tickets.toString()
                                    DATA_BAR_GRAPH[2] = responseModel.data!![0].timings.toString()
                                    DATA_BAR_GRAPH[3] = responseModel.data!![0].permits.toString()
                                    DATA_BAR_GRAPH[4] = responseModel.data!![0].scofflaws.toString()
                                    DATA_BAR_GRAPH[5] = responseModel.data!![0].driveOffs.toString()
                                    DIFFERENCE = (conToInt(DATA_BAR_GRAPH[0]) + conToInt(
                                        DATA_BAR_GRAPH[1]
                                    )
                                            + conToInt(DATA_BAR_GRAPH[2]) + conToInt(DATA_BAR_GRAPH[3])
                                            + conToInt(DATA_BAR_GRAPH[4]) + conToInt(DATA_BAR_GRAPH[5])) / 2
                                    var max = 0
                                    for (s in DATA_BAR_GRAPH) {
                                        if (s!!.toInt() > max) {
                                            max = s.toInt()
                                        }
                                    }
                                    MAX_Y_VALUE = max + 4
                                    initGraphs()
                                }
                            } else if (responseModel != null && !responseModel.status!!) {
                                // Not getting response from server..!!
                                AppUtils.showCustomAlertDialog(
                                    mContext,
                                    "POST_GET_BAR_COUNT",
                                    responseModel.message,
                                    getString(R.string.alt_lbl_OK),
                                    getString(R.string.scr_btn_cancel),
                                    this
                                )
                            } else {
                                AppUtils.showCustomAlertDialog(
                                    mContext,
                                    "POST_GET_BAR_COUNT",
                                    getString(R.string.err_msg_something_went_wrong),
                                    getString(R.string.alt_lbl_OK),
                                    getString(R.string.scr_btn_cancel),
                                    this
                                )
                                dismissLoader()
                            }
                        } else if (tag.equals(
                                DynamicAPIPath.POST_ACTIVITY_UPDATES,
                                ignoreCase = true
                            )
                        ) {


                            val responseModel = ObjectMapperProvider.fromJson(apiResponse.data.toString(), ActivityCountResponse::class.java)

                            if (responseModel != null && responseModel.status!!) {
                                val mData = responseModel.data!![0].activityCountData
                                activityList = responseModel.data!![0].activityCountData
                                setAdapterForActivityList()
                            } else if (responseModel != null && !responseModel.status!!) {
                                // Not getting response from server..!!
                                AppUtils.showCustomAlertDialog(
                                    mContext, "POST_ACTIVITY_UPDATES",
                                    responseModel.message, getString(R.string.alt_lbl_OK),
                                    getString(R.string.scr_btn_cancel), this
                                )
                            } else {
                                AppUtils.showCustomAlertDialog(
                                    mContext,
                                    "POST_ACTIVITY_UPDATES",
                                    getString(R.string.err_msg_something_went_wrong),
                                    getString(R.string.alt_lbl_OK),
                                    getString(R.string.scr_btn_cancel),
                                    this
                                )
                                dismissLoader()
                            }
                        } else if (tag.equals(
                                DynamicAPIPath.POST_VIOLATION_COUNT,
                                ignoreCase = true
                            )
                        ) {

                            val responseModel = ObjectMapperProvider.fromJson(apiResponse.data.toString(), GetViolationCountResponse::class.java)


                            if (responseModel != null && responseModel.status.nullSafety()) {
                                if (responseModel.data!![0].resonse!!.isNotEmpty()) {
                                    mViolationList = responseModel.data!![0].resonse
                                    setAdapterForViolationList()
                                }
                            } else if (responseModel != null && !responseModel.status!!) {
                                // Not getting response from server..!!
                                AppUtils.showCustomAlertDialog(
                                    mContext, "POST_VIOLATION_COUNT",
                                    responseModel.message, getString(R.string.alt_lbl_OK),
                                    getString(R.string.scr_btn_cancel), this
                                )
                            } else {
                                AppUtils.showCustomAlertDialog(
                                    mContext,
                                    "POST_VIOLATION_COUNT",
                                    getString(R.string.err_msg_something_went_wrong),
                                    getString(R.string.alt_lbl_OK),
                                    getString(R.string.scr_btn_cancel),
                                    this
                                )
                                dismissLoader()
                            }
                        } else if (tag.equals(DynamicAPIPath.GET_ROUTE_DATA, ignoreCase = true)) {


                            val responseModel = ObjectMapperProvider.fromJson(apiResponse.data.toString(), RouteDataResponse::class.java)

                            if (responseModel != null && responseModel.isSuccess) {
                                if (responseModel.data != null && responseModel.data!!.size > 0) {
                                    ioScope.async {
                                        mRouteData = responseModel.data
                                        setLocationDetails()
                                        mainScope.async {
                                            setMarkerOnMap()
                                        }
                                    }
                                }
                            } else if (responseModel != null && !responseModel.isSuccess) {
                                // Not getting response from server..!!
                                AppUtils.showCustomAlertDialog(
                                    mContext,
                                    "GET_ROUTE_DATA",
                                    getString(R.string.err_msg_something_went_wrong),
                                    getString(R.string.alt_lbl_OK),
                                    getString(R.string.scr_btn_cancel),
                                    this
                                )
                            } else {
                                AppUtils.showCustomAlertDialog(
                                    mContext,
                                    "GET_ROUTE_DATA",
                                    getString(R.string.err_msg_something_went_wrong),
                                    getString(R.string.alt_lbl_OK),
                                    getString(R.string.scr_btn_cancel),
                                    this
                                )
                                dismissLoader()
                            }
                        }
                    } catch (e: Exception) {
                        LogUtil.printLog("error line activity", "")
                        e.printStackTrace()
                        dismissLoader()
                        //                        logout(mContext);
                    }
                }
            }
            Status.ERROR -> {
                dismissLoader()
                //                logout(mContext);
                LogUtil.printToastMSG(
                    applicationContext,
                    getString(R.string.err_msg_connection_was_refused)
                )
            }

            else -> {}
        }
    }

    private fun conToInt(mValue: String?): Int {
        return mValue!!.toInt()
    }

    //adding customize marker
    private fun BitmapFromVector(context: Context, vectorResId: Int): BitmapDescriptor {
        // below line is use to generate a drawable.
        val vectorDrawable = ContextCompat.getDrawable(context, vectorResId)

        // below line is use to set bounds to our vector drawable.
        vectorDrawable!!.setBounds(
            0,
            0,
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight
        )

        // below line is use to create a bitmap for our
        // drawable which we have added.
        val bitmap = Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )

        // below line is use to add bitmap in our canvas.
        val canvas = Canvas(bitmap)

        // below line is use to draw our
        // vector drawable in canvas.
        vectorDrawable.draw(canvas)

        // after generating our bitmap we are returning our bitmap.
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
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
        mMap?.setOnMapClickListener {
            val intent = Intent(mContext, MapActivity::class.java)
            intent.putExtra(Constants.LOCATION_KEY, locationArrayList as Serializable)
            intent.putExtra(Constants.LOCATION_KEYLogin, LoginArrayList as Serializable)
            intent.putExtra(Constants.LOCATION_KEYLogout, LogoutArrayList as Serializable)
            intent.putExtra(Constants.LOCATION_KEYCitation, CitationArrayList as Serializable)
            intent.putExtra(Constants.LOCATION_KEYActivity, ActivityArrayList as Serializable)
            intent.putParcelableArrayListExtra(Constants.SEND_LOCATION_DATA, mRouteData)
            startActivity(intent)
        }
    }

    //set Map Data
    private fun setMarkerOnMap() {
        try {
            if (mMap != null && mRouteData != null && mRouteData!!.size > 0) {
                for (i in LoginArrayList.indices) {
                    mMap?.addMarker(
                        MarkerOptions().position(LoginArrayList[i]).title(
                            mRouteData!![i].activityType
                        )
                            .snippet("Login")
                            .icon(BitmapFromVector(applicationContext, R.drawable.ic_marker))
                    )
                }
                for (i in LogoutArrayList.indices) {
                    mMap?.addMarker(
                        MarkerOptions().position(LogoutArrayList[i]).title(
                            mRouteData!![i].activityType
                        )
                            .snippet("Logout")
                            .icon(BitmapFromVector(applicationContext, R.drawable.ic_marker_red))
                    )
                }
                for (i in CitationArrayList.indices) {
                    mMap?.addMarker(
                        MarkerOptions().position(CitationArrayList[i]).title(
                            mRouteData!![i].activityType
                        )
                            .snippet("Citation")
                            .icon(BitmapFromVector(applicationContext, R.drawable.ic_marker_purple))
                    )
                }
                for (i in ActivityArrayList.indices) {
                    mMap?.addMarker(
                        MarkerOptions().position(ActivityArrayList[i]).title(
                            mRouteData!![i].activityType
                        )
                            .snippet("Activity")
                            .icon(BitmapFromVector(applicationContext, R.drawable.ic_marker_yellow))
                    )
                }
                var builder = LatLngBounds.Builder()
                polylineOptions1 = PolylineOptions()

                // Setting the color of the polyline
                polylineOptions1?.color(resources.getColor(R.color.deep_blue))

                // Setting the width of the polyline
                polylineOptions1?.width(10f)
                // Adding the taped point to the ArrayList
                val partitionSize = 1000
                val partitions: MutableList<List<LatLng>> = LinkedList()
                run {
                    var i = 0
                    while (i < LoginArrayList.size) {
                        partitions.add(
                            LoginArrayList.subList(
                                i,
                                Math.min(i + partitionSize, LoginArrayList.size)
                            )
                        )
                        i += partitionSize
                    }
                }
                val width = resources.displayMetrics.widthPixels
                val height = resources.displayMetrics.heightPixels
                val padding = (width * 0.10).toInt() // offset from edges of the map 10% of screen
                for (i in 0 until if (locationArrayList.size > 24) 24 else locationArrayList.size) {
                    builder = builder.include(locationArrayList[i])
                }
                val bounds = builder.build()
                mMap!!.animateCamera(
                    CameraUpdateFactory.newLatLngBounds(bounds, padding),
                    2000,
                    null
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /*perform click actions*/
    @OnClick(R.id.cardMap)
    fun onClick(view: View) {
        val id = view.id
        when (id) {
            R.id.cardMap -> {
            }
        }
    }

    override fun onBackPressed() {
        if (backpressCloseDrawer()) {
            closeDrawer()
            finish()
            startActivity(
                Intent(this, WelcomeActivity::class.java)
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            )
        }
    }

    /**
     * Request direction from Google Direction API
     *
     * @param requestedUrl see [.]
     * @return JSON data routes/direction
     */
    private fun requestDirection(requestedUrl: String): String {
        var responseString = ""
        var inputStream: InputStream? = null
        var httpURLConnection: HttpURLConnection? = null
        try {
            val url = URL(requestedUrl)
            httpURLConnection = url.openConnection() as HttpURLConnection
            httpURLConnection.connect()
            inputStream = httpURLConnection.inputStream
            val reader = InputStreamReader(inputStream)
            val bufferedReader = BufferedReader(reader)
            val stringBuffer = StringBuffer()
            var line: String? = ""
            while (bufferedReader.readLine().also { line = it } != null) {
                stringBuffer.append(line)
            }
            responseString = stringBuffer.toString()
            bufferedReader.close()
            reader.close()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        httpURLConnection!!.disconnect()
        return responseString
    }

    //Get JSON data from Google Direction
    inner class TaskDirectionRequest : AsyncTask<String?, Void?, String>() {
        override fun doInBackground(vararg strings: String?): String? {
            var responseString = ""
            try {
                responseString = requestDirection(strings[0].nullSafety())
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return responseString
        }

        override fun onPostExecute(responseString: String) {
            super.onPostExecute(responseString)
            //Json object parsing
            val parseResult = TaskParseDirection()
            parseResult.execute(responseString)
        }
    }

    //Parse JSON Object from Google Direction API & display it on Map
    inner class TaskParseDirection :
        AsyncTask<String?, Void?, List<List<HashMap<String, String>>>?>() {
        override fun doInBackground(vararg jsonString: String?): List<List<HashMap<String, String>>>? {
            var routes: List<List<HashMap<String, String>>>? = null
            var jsonObject: JSONObject? = null
            try {
                jsonObject = JSONObject(jsonString[0])
                val parser = DirectionParser()
                routes = parser.parse(jsonObject)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            return routes
        }

        override fun onPostExecute(lists: List<List<HashMap<String, String>>>?) {
            super.onPostExecute(lists)
            var points: ArrayList<Any?>? = null
            var polylineOptions: PolylineOptions? = null
            for (path in lists!!) {
                points = ArrayList<Any?>()
                polylineOptions = PolylineOptions()
                for (point in path) {
                    val lat = point["lat"]!!.toDouble()
                    val lon = point["lng"]!!.toDouble()
                    points.add(LatLng(lat, lon))
                }
                polylineOptions.addAll(points as MutableIterable<LatLng>)
                polylineOptions.width(15f)
                polylineOptions.color(Color.BLUE)
                polylineOptions.geodesic(true)
            }
            if (polylineOptions != null) {
                mMap?.addPolyline(polylineOptions)
                Toast.makeText(applicationContext, "Direction found", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(applicationContext, "Direction not found", Toast.LENGTH_LONG).show()
                polylineOptions1!!.addAll(locationArrayList)
                mMap?.addPolyline(polylineOptions1)
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

    override fun onDestroy() {
        mContext = null
        super.onDestroy()
    }

    companion object {
        private const val MY_PERMISSIONS_REQUEST = 32
        private const val MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 10
    }
}