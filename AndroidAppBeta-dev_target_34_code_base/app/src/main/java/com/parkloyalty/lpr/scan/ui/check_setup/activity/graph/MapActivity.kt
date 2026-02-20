package com.parkloyalty.lpr.scan.ui.check_setup.activity.graph

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import butterknife.ButterKnife
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.parkloyalty.lpr.scan.R
import com.parkloyalty.lpr.scan.basecontrol.BaseActivity
import com.parkloyalty.lpr.scan.extensions.nullSafety
import com.parkloyalty.lpr.scan.interfaces.Constants
import com.parkloyalty.lpr.scan.ui.check_setup.activity.graph.model.DataItem
import com.parkloyalty.lpr.scan.ui.check_setup.activity.graph.model.DirectionsJSONParser
import com.parkloyalty.lpr.scan.util.AppUtils
import com.parkloyalty.lpr.scan.util.LogUtil
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

/*Phase 2*/
class MapActivity : BaseActivity(), OnMapReadyCallback {
    private var mMap: GoogleMap? = null

    // creating array list for adding all our locations.
    private var mRouteData: List<DataItem>? = ArrayList()
    private var locationArrayList: List<LatLng>? = ArrayList()
    private var LoginArrayList: MutableList<LatLng>? = ArrayList()
    private var LogoutArrayList: MutableList<LatLng>? = ArrayList()
    private var CitationArrayList: MutableList<LatLng>? = ArrayList()
    private var ActivityArrayList: MutableList<LatLng>? = ArrayList()
    private var APIKEY: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        setFullScreenUI()
        ButterKnife.bind(this)
        requestPermission(Manifest.permission.ACCESS_FINE_LOCATION)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
        APIKEY = resources.getString(R.string.google_maps_key)
        setLocationDetails()
        setToolbar()
        getIntentData()
    }

    private fun getIntentData() {
            try {
                if (intent != null) {
                    locationArrayList = intent.extras!![Constants.LOCATION_KEY] as List<LatLng>?
                    LoginArrayList =
                        intent.extras!![Constants.LOCATION_KEYLogin] as MutableList<LatLng>?
                    LogoutArrayList =
                        intent.extras!![Constants.LOCATION_KEYLogout] as MutableList<LatLng>?
                    CitationArrayList =
                        intent.extras!![Constants.LOCATION_KEYCitation] as MutableList<LatLng>?
                    ActivityArrayList =
                        intent.extras!![Constants.LOCATION_KEYActivity] as MutableList<LatLng>?
                    mRouteData = intent.getParcelableArrayListExtra(Constants.SEND_LOCATION_DATA)
                    if (mRouteData != null) {
                        for (i in mRouteData!!.indices) {
                            if (mRouteData?.get(i)?.locationUpdatetype != null) {
                                if (mRouteData?.get(i)?.locationUpdatetype == "login") {
                                    LoginArrayList?.add(
                                        LatLng(
                                            mRouteData?.get(i)?.latitude.nullSafety(),
                                            mRouteData?.get(i)?.longitude.nullSafety()
                                        )
                                    )
                                }
                                if (mRouteData?.get(i)?.locationUpdatetype == "logout") {
                                    LogoutArrayList?.add(
                                        LatLng(
                                            mRouteData?.get(i)?.latitude.nullSafety(),
                                            mRouteData?.get(i)?.longitude.nullSafety()
                                        )
                                    )
                                }
                                if (mRouteData?.get(i)?.locationUpdatetype == "citation") {
                                    CitationArrayList?.add(
                                        LatLng(
                                            mRouteData?.get(i)?.latitude.nullSafety(),
                                            mRouteData?.get(i)?.longitude.nullSafety()
                                        )
                                    )
                                }
                                if (mRouteData?.get(i)?.locationUpdatetype == "break") {
                                    ActivityArrayList?.add(
                                        LatLng(
                                            mRouteData?.get(i)?.latitude.nullSafety(),
                                            mRouteData?.get(i)?.longitude.nullSafety()
                                        )
                                    )
                                }
                            }
                        }
                        setMarkerOnMap()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    //set location marker
    private fun setLocationDetails() {
        // in below line we are initializing our array list.
        locationArrayList = ArrayList()
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

    //adding customize marker
    private fun BitmapFromVector(context: Context, vectorResId: Int): BitmapDescriptor {
        // below line is use to generate a drawable.
        val vectorDrawable = ContextCompat.getDrawable(context, vectorResId)

        // below line is use to set bounds to our vector drawable.
        vectorDrawable?.setBounds(
            0,
            0,
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight
        )

        // below line is use to create a bitmap for our
        // drawable which we have added.
        val bitmap = Bitmap.createBitmap(
            vectorDrawable?.intrinsicWidth.nullSafety(),
            vectorDrawable?.intrinsicHeight.nullSafety(),
            Bitmap.Config.ARGB_8888
        )

        // below line is use to add bitmap in our canvas.
        val canvas = Canvas(bitmap)

        // below line is use to draw our
        // vector drawable in canvas.
        vectorDrawable?.draw(canvas)

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
        setMarkerOnMap()
    }

    private fun setMarkerOnMap() {
        try {
            if (mMap != null && mRouteData != null && mRouteData!!.size > 0) {
                mMap?.clear()
                for (i in mRouteData?.indices!!) {
                    if (mRouteData!![i].locationUpdatetype != null) {
                        if (mRouteData!![i].locationUpdatetype == "login") {
                            val latLng = LatLng(
                                mRouteData!![i].latitude, mRouteData!![i].longitude
                            )
                            mMap!!.addMarker(
                                MarkerOptions().position(latLng).title(mRouteData!![i].activityType)
                                    .snippet(
                                        AppUtils.splitDateWelcome(
                                            mRouteData!![i].clientTimestamp,
                                            ""
                                        )
                                    )
                                    .title(mRouteData!![i].locationUpdatetype) // below line is use to add custom marker on our map.
                                    .icon(
                                        BitmapFromVector(
                                            applicationContext,
                                            R.drawable.ic_marker
                                        )
                                    )
                            )
                            //
                        } else if (mRouteData!![i].locationUpdatetype == "logout") {
                            val latLng = LatLng(
                                mRouteData!![i].latitude, mRouteData!![i].longitude
                            )
                            mMap!!.addMarker(
                                MarkerOptions().position(latLng).title(mRouteData!![i].activityType)
                                    .snippet(
                                        AppUtils.splitDateWelcome(
                                            mRouteData!![i].clientTimestamp,
                                            ""
                                        )
                                    )
                                    .title(mRouteData!![i].locationUpdatetype)
                                    .icon(
                                        BitmapFromVector(
                                            applicationContext,
                                            R.drawable.ic_marker_red
                                        )
                                    )
                            )
                            //
                        } else if (mRouteData!![i].locationUpdatetype == "citation") {
                            val latLng = LatLng(
                                mRouteData!![i].latitude, mRouteData!![i].longitude
                            )
                            mMap!!.addMarker(
                                MarkerOptions().position(latLng).title(mRouteData!![i].activityType)
                                    .snippet(
                                        AppUtils.splitDateWelcome(
                                            mRouteData!![i].clientTimestamp,
                                            ""
                                        )
                                    )
                                    .title(mRouteData!![i].locationUpdatetype)
                                    .icon(
                                        BitmapFromVector(
                                            applicationContext,
                                            R.drawable.ic_marker_purple
                                        )
                                    )
                            )
                            //
                        } else if (mRouteData!![i].locationUpdatetype == "break") {
                            val latLng = LatLng(
                                mRouteData!![i].latitude, mRouteData!![i].longitude
                            )
                            mMap!!.addMarker(
                                MarkerOptions().position(latLng).title(mRouteData!![i].activityType)
                                    .snippet(
                                        AppUtils.splitDateWelcome(
                                            mRouteData!![i].clientTimestamp,
                                            ""
                                        )
                                    )
                                    .title(mRouteData!![i].locationUpdatetype)
                                    .icon(
                                        BitmapFromVector(
                                            applicationContext,
                                            R.drawable.ic_marker_yellow
                                        )
                                    )
                            )
                            //
                        }
                    }
                }
                val routeDrawArraySize = locationArrayList!!.size
                var url = ""
                try {
                    if (routeDrawArraySize > 24) {
                        var latLngs: MutableList<LatLng> = ArrayList()
                        for (i in 0 until routeDrawArraySize) {
                            latLngs.add(locationArrayList!![i])
                            if (i != 0 && i % 24 == 0 || i == routeDrawArraySize - 1) {
                                url = getDirectionsUrl(
                                    latLngs[0], latLngs[latLngs.size - 1],
                                    latLngs
                                )
                                latLngs = ArrayList()
                            }
                            if (i != 0 && i % 24 == 0 || i == routeDrawArraySize - 1) {
                                val downloadTask = DownloadTask()
                                downloadTask.execute(url)
                            }
                        }
                    } else {
                        url = getDirectionsUrl(
                            locationArrayList!![0], locationArrayList!![routeDrawArraySize - 1],
                            locationArrayList
                        )
                        val downloadTask = DownloadTask()
                        downloadTask.execute(url)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                // for camera bound
                try {
                    var builder = LatLngBounds.Builder()
                    val width = resources.displayMetrics.widthPixels
                    val height = resources.displayMetrics.heightPixels
                    val padding =
                        (width * 0.10).toInt() // offset from edges of the map 10% of screen

//                    for (LatLng latLng : locationArrayList) {
                    for (i in 0 until if (locationArrayList!!.size > 24) 24 else locationArrayList!!.size) {
                        builder = builder.include(locationArrayList!![i])
                    }
                    val bounds = builder.build()
                    if (bounds != null) {
                        mMap!!.animateCamera(
                            CameraUpdateFactory.newLatLngBounds(bounds, padding),
                            2000,
                            null
                        )
                    } else {
                        mMap!!.moveCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                if (location != null) LatLng(
                                    location!!.latitude, location!!.longitude
                                ) else locationArrayList!![0], 14.6f
                            )
                        )
                    }
                    //                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locationArrayList.get(0), 14.6f));
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onBackPressed() {
        if (backpressCloseDrawer()) {
            // close activity when drawer is closed
            super.onBackPressed()
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

    /*-----------------------*/
    private fun getDirectionsUrl(
        origin: LatLng,
        dest: LatLng,
        markerPoints: List<LatLng>?
    ): String {

        // Origin of route
        val str_origin = "origin=" + origin.latitude + "," + origin.longitude

        // Destination of route
        val str_dest = "destination=" + dest.latitude + "," + dest.longitude

        // Sensor enabled
        val sensor = "sensor=false"
        val waypointsoptimize = "waypoints=optimize:true"
        val travelMode = "travelMode: WALKING"

        // Waypoints
        var waypoints = ""
        for (i in markerPoints!!.indices) {
            val point = markerPoints[i]
            if (i == 2) waypoints = "waypoints="
            waypoints += point.latitude.toString() + "," + point.longitude + "|"
        }

        // Building the parameters to the web service
        val parameters = "$str_origin&$str_dest&$sensor&$travelMode&$waypoints"

        // Output format
        val output = "json"

        // Building the url to the web service
//        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
//        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + "AIzaSyC3RwBupXyFdul5XtIAWjDsF9f8ogyLam4";
        val url = "https://maps.googleapis.com/maps/api/directions/$output?$parameters&key=$APIKEY"
        LogUtil.printLog("Map url", url)
        return url
    }

    /**
     * A method to download json data from url
     */
    @SuppressLint("LongLogTag")
    @Throws(IOException::class)
    private fun downloadUrl(strUrl: String): String {
        var data = ""
        var iStream: InputStream? = null
        var urlConnection: HttpURLConnection? = null
        try {
            val url = URL(strUrl)

            // Creating an http connection to communicate with url
            urlConnection = url.openConnection() as HttpURLConnection

            // Connecting to url
            urlConnection.connect()

            // Reading data from url
            iStream = urlConnection.inputStream
            val br = BufferedReader(InputStreamReader(iStream))
            val sb = StringBuffer()
            var line: String? = ""
            while (br.readLine().also { line = it } != null) {
                sb.append(line)
            }
            data = sb.toString()
            br.close()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            iStream!!.close()
            urlConnection!!.disconnect()
        }
        return data
    }

    // Fetches data from url passed
    private inner class DownloadTask : AsyncTask<String?, Void?, String>() {
        // Downloading data in non-ui thread
        override fun doInBackground(vararg url: String?): String? {

            // For storing data from web service
            var data = ""
            try {
                // Fetching the data from web service
                data = downloadUrl(url[0].nullSafety())
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return data
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        override fun onPostExecute(result: String) {
            super.onPostExecute(result)
            Log.i("google api response ", result)
            val parserTask = ParserTask()

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result)
        }
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private inner class ParserTask :
        AsyncTask<String?, Int?, List<List<HashMap<String, String>>>?>() {
        // Parsing the data in non-ui thread
        override fun doInBackground(vararg jsonData: String?): List<List<HashMap<String, String>>>? {
            val jObject: JSONObject
            var routes: List<List<HashMap<String, String>>>? = null
            try {
                jObject = JSONObject(jsonData[0])
                val parser = DirectionsJSONParser()

                // Starts parsing data
                routes = parser.parse(jObject)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return routes
        }

        // Executes in UI thread, after the parsing process
        override fun onPostExecute(result: List<List<HashMap<String, String>>>?) {
            try {
                var points: ArrayList<LatLng?>? = null
                var lineOptions: PolylineOptions? = null

                // Traversing through all the routes
                for (i in result!!.indices) {
                    points = ArrayList()
                    lineOptions = PolylineOptions()

                    // Fetching i-th route
                    val path = result[i]

                    // Fetching all the points in i-th route
                    for (j in path.indices) {
                        val point = path[j]
                        val lat = point["lat"]!!.toDouble()
                        val lng = point["lng"]!!.toDouble()
                        val position = LatLng(lat, lng)
                        points.add(position)
                    }

                    // Adding all the points in the route to LineOptions
                    lineOptions.addAll(points)
                    lineOptions.width(10f)
                    lineOptions.color(Color.RED)
                }

                // Drawing polyline in the Google Map for the i-th route
                if (lineOptions != null) {
                    mMap!!.addPolyline(lineOptions)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    companion object {
        private const val MY_PERMISSIONS_REQUEST = 32
    }
}