package com.parkloyalty.lpr.scan.locationservice

import android.Manifest
import android.app.Activity
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.google.android.gms.location.SettingsClient
import com.parkloyalty.lpr.scan.R
import com.parkloyalty.lpr.scan.interfaces.Constants
import com.parkloyalty.lpr.scan.interfaces.SharedPrefKey
import com.parkloyalty.lpr.scan.ui.SplashActivity
import com.parkloyalty.lpr.scan.util.LogUtil
import com.parkloyalty.lpr.scan.util.SharedPref
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class BackgroundLocationUpdateService : Service(), GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener, LocationListener {
    /* Declare in manifest
    <service android:name=".BackgroundLocationUpdateService"/>
    */
    private val SERVICE_ID: Int = 101010
    private val TAG = "BackgroundLocationUpdateService"
    private val TAG_LOCATION = "TAG_LOCATION"
    private var context: Context? = null
    private var stopService = false

    /* For Google Fused API */
    private var mGoogleApiClient: GoogleApiClient? = null
    private var mLocationSettingsRequest: LocationSettingsRequest? = null
    private var latitude = "0.0"
    private var longitude = "0.0"
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private var mSettingsClient: SettingsClient? = null
    private var mLocationCallback: LocationCallback? = null
    private var mLocationRequest: LocationRequest? = null
    private var mCurrentLocation: Location? = null

    @Inject
    lateinit var sharedPref: SharedPref

    /* For Google Fused API */
    override fun onCreate() {
        super.onCreate()
        context = this
    }

    //@RequiresApi(Build.VERSION_CODES.Q)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground()
        val handler = Handler(Looper.getMainLooper())
        val runnable: Runnable = object : Runnable {
            override fun run() {
                try {
                    if (!stopService) {
                        requestLocationUpdate()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    if (!stopService) {
                        handler.postDelayed(
                            this,
                            TimeUnit.SECONDS.toMillis(Constants.SERVICE_TIME)
                        ) //5 min //300
                    }
                }
            }
        }
        handler.postDelayed(runnable, 0)
        buildGoogleApiClient()
        return START_STICKY
    }

    override fun onDestroy() {
        LogUtil.printLog("TAG", "Service Stopped")
        stopService = true
        if (mFusedLocationClient != null) {
            mLocationCallback?.let { mFusedLocationClient?.removeLocationUpdates(it) }
            LogUtil.printLog(TAG_LOCATION, "Location Update Callback Removed")
        }
        super.onDestroy()
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    //    @RequiresApi(Build.VERSION_CODES.Q)
    private fun startForeground() {
        try {
            val intent = Intent(context, SplashActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            val pendingIntent = PendingIntent.getActivity(
                this,
                0 /* Request code */,
                intent,
                PendingIntent.FLAG_IMMUTABLE
            )
            val CHANNEL_ID = "channel_location"
            val CHANNEL_NAME = "channel_location"
            var builder: NotificationCompat.Builder? = null
            val notificationManager =
                applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            notificationManager.createNotificationChannel(channel)
            builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            builder.setChannelId(CHANNEL_ID)
            builder.setBadgeIconType(NotificationCompat.BADGE_ICON_NONE)
            builder.setContentTitle("Location Service")
            builder.setContentText("Foreground location service is active & running")
            builder.setSmallIcon(R.drawable.ic_lpr_app)
            builder.setContentIntent(pendingIntent)
            builder.setOngoing(true)
            builder.setPriority(NotificationCompat.PRIORITY_HIGH)


            val notification = builder.build()

            startForeground(SERVICE_ID, notification)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onLocationChanged(location: Location) {
        try {
            LogUtil.printLog(
                TAG_LOCATION,
                "Location Changed Latitude : " + location.latitude + "\tLongitude : " + location.longitude
            )
            latitude = location.latitude.toString()
            longitude = location.longitude.toString()
            val intent = Intent(ACTION_BROADCAST)
            intent.putExtra(EXTRA_LOCATION, location)
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
            if (latitude.equals("0.0", ignoreCase = true) && longitude.equals(
                    "0.0",
                    ignoreCase = true
                )
            ) {
                requestLocationUpdate()
            } else {
                LogUtil.printLog(
                    TAG_LOCATION,
                    "Latitude : " + location.latitude + "\tLongitude : " + location.longitude
                )
                sharedPref.write(SharedPrefKey.LAT, location.latitude.toString())
                sharedPref.write(SharedPrefKey.LONG, location.longitude.toString())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
    override fun onProviderEnabled(provider: String) {}
    override fun onProviderDisabled(provider: String) {}
    override fun onConnected(bundle: Bundle?) {
        mLocationRequest = LocationRequest()
        mLocationRequest?.interval = Constants.INTERVAL
        mLocationRequest?.fastestInterval = Constants.FASTEST_INTERVAL
        mLocationRequest?.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
//        mLocationRequest?.priority = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000).build()

        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(mLocationRequest!!)
        builder.setAlwaysShow(true)
        mLocationSettingsRequest = builder.build()
        mSettingsClient
            ?.checkLocationSettings(mLocationSettingsRequest!!)
            ?.addOnSuccessListener {
                LogUtil.printLog(TAG_LOCATION, "GPS Success")
                requestLocationUpdate()
            }?.addOnFailureListener { e ->
                val statusCode = (e as ApiException).statusCode
                when (statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                        val REQUEST_CHECK_SETTINGS = 214
                        val rae = e as ResolvableApiException
                        try {
                            (context as Activity?)?.let {
                                rae.startResolutionForResult(
                                    it,
                                    REQUEST_CHECK_SETTINGS
                                )
                            }
                        } catch (m: ClassCastException) {
                            m.printStackTrace()
                        }
                    } catch (sie: SendIntentException) {
                        LogUtil.printLog(TAG_LOCATION, "Unable to execute request.")
                    }

                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> LogUtil.printLog(
                        TAG_LOCATION,
                        "Location settings are inadequate, and cannot be fixed here. Fix in Settings."
                    )
                }
            }?.addOnCanceledListener {
                LogUtil.printLog(
                    TAG_LOCATION,
                    "checkLocationSettings -> onCanceled"
                )
            }
    }

    override fun onConnectionSuspended(i: Int) {
        connectGoogleClient()
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        buildGoogleApiClient()
    }

    @Synchronized
    protected fun buildGoogleApiClient() {
        mFusedLocationClient = context?.let { LocationServices.getFusedLocationProviderClient(it) }
        mSettingsClient = context?.let { LocationServices.getSettingsClient(it) }
        mGoogleApiClient = context?.let {
            GoogleApiClient.Builder(it)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build()
        }
        connectGoogleClient()
        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                LogUtil.printLog(TAG_LOCATION, "Location Received")
                mCurrentLocation = locationResult.lastLocation
                onLocationChanged(mCurrentLocation!!)
            }
        }
    }

    private fun connectGoogleClient() {
        val googleAPI = GoogleApiAvailability.getInstance()
        val resultCode = context?.let { googleAPI.isGooglePlayServicesAvailable(it) }
        if (resultCode == ConnectionResult.SUCCESS) {
            mGoogleApiClient!!.connect()
        }
    }

    private fun requestLocationUpdate() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        mFusedLocationClient?.requestLocationUpdates(
            mLocationRequest!!,
            mLocationCallback!!,
            Looper.myLooper()
        )
    }

    companion object {
        private const val PACKAGE_NAME = "com.fiveexceptions.lpr.scan"
        const val ACTION_BROADCAST = PACKAGE_NAME + ".broadcast"
        const val EXTRA_LOCATION = PACKAGE_NAME + ".location"
    }
}