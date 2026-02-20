package com.parkloyalty.lpr.scan.locationservice

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.common.api.ApiException
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
import com.google.android.gms.location.Priority
import com.parkloyalty.lpr.scan.views.MainActivity

@AndroidEntryPoint
class NewBackgroundLocationUpdateService : Service() {
    /* Declare in manifest
    <service android:name=".BackgroundLocationUpdateService"/>
    */
    private val SERVICE_ID: Int = 101010
    private val TAG_LOCATION = "TAG_LOCATION"

    private var stopService = false

    /* Location clients */
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private var mSettingsClient: SettingsClient? = null
    private var mLocationCallback: LocationCallback? = null
    private var mLocationRequest: LocationRequest? = null
    private var mCurrentLocation: Location? = null

    private val handler = Handler(Looper.getMainLooper())
    private val periodicRunnable = object : Runnable {
        override fun run() {
            try {
                if (!stopService) {
                    checkAndRequestLocation()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                if (!stopService) {
                    handler.postDelayed(this, TimeUnit.SECONDS.toMillis(Constants.SERVICE_TIME))
                }
            }
        }
    }

    @Inject
    lateinit var sharedPref: SharedPref

    override fun onCreate() {
        super.onCreate()
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mSettingsClient = LocationServices.getSettingsClient(this)

        // Build a sensible LocationRequest using the new Builder API (non-deprecated)
        mLocationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            5000//Constants.INTERVAL
        )
            .setMinUpdateIntervalMillis(5000)//Constants.FASTEST_INTERVAL
            .setWaitForAccurateLocation(false)
            .build()

//        mLocationRequest = LocationRequest.Builder(
//            Priority.PRIORITY_HIGH_ACCURACY,
//            10000
//        )
//            .setMinUpdateIntervalMillis(10000)
//            .setWaitForAccurateLocation(false)
//            .build()

        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                LogUtil.printLog(TAG_LOCATION, "Location Received")
                val location = locationResult.lastLocation
                location?.let { handleNewLocation(it) }
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForegroundServiceNotification()
        // start the periodic runnable immediately
        handler.post(periodicRunnable)
        return START_STICKY
    }

    override fun onDestroy() {
        LogUtil.printLog("TAG", "Service Stopped")
        stopService = true
        try {
            mLocationCallback?.let { mFusedLocationClient?.removeLocationUpdates(it) }
            LogUtil.printLog(TAG_LOCATION, "Location Update Callback Removed")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        handler.removeCallbacks(periodicRunnable)
        super.onDestroy()
    }

    override fun onBind(intent: Intent): IBinder? = null

    @Suppress("SENSELESS_COMPARISON")
    private fun startForegroundServiceNotification() {
        try {
            val intent = Intent(this, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
            val pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE
            )

            val CHANNEL_ID = "channel_location"
            val CHANNEL_NAME = "Location Service"
            val notificationManager = getSystemService(NotificationManager::class.java)

            // Create channel if necessary (O+)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_LOW
                ).apply {
                    description = "Foreground service for location updates"
                    // low importance to avoid noise, but the notification is ongoing so user will see it
                    lockscreenVisibility = Notification.VISIBILITY_PRIVATE
                }
                notificationManager.createNotificationChannel(channel)
            }

            val builder = NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_lpr_app)
                .setContentTitle(getString(R.string.app_name))
                .setContentText("Location service is active")
                .setContentIntent(pendingIntent)
                .setOngoing(true) // makes notification non-dismissible by user
                .setAutoCancel(false)
                .setPriority(NotificationCompat.PRIORITY_HIGH)

            val notification = builder.build().apply {
                // ensure flags set so notification cannot be cleared
                // use the notification's current flags and set ongoing/no-clear bits
                this.flags = this.flags or Notification.FLAG_ONGOING_EVENT or Notification.FLAG_NO_CLEAR
            }

            startForeground(SERVICE_ID, notification)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun checkAndRequestLocation() {
        // Prepare settings request
        //        val request = LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest!!).build()
        //
        //        mSettingsClient?.checkLocationSettings(request)
        //            ?.addOnSuccessListener {
        //                LogUtil.printLog(TAG_LOCATION, "GPS Success - requesting updates")
        //                requestLocationUpdate()
        //            }
        //            ?.addOnFailureListener { e ->
        //                if (e is ApiException) {
        //                    val statusCode = e.statusCode
        //                    when (statusCode) {
        //                        LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
        //                            // Can't resolve from a Service context; notify the UI via broadcast so Activity can prompt user
        //                            LogUtil.printLog(TAG_LOCATION, "Location settings resolution required")
        //                            // Optionally broadcast an event so UI can show resolution dialog
        //                        }
        //                        LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> LogUtil.printLog(
        //                            TAG_LOCATION,
        //                            "Location settings are inadequate and cannot be fixed here."
        //                        )
        //                    }
        //                } else {
        //                    LogUtil.printLog(TAG_LOCATION, "checkLocationSettings failed: ${e?.message}")
        //                }
        //            }

        // take a local immutable copy to avoid smart-cast/mutation issues
        val locationRequest = mLocationRequest ?: run {
            LogUtil.printLog(TAG_LOCATION, "LocationRequest is null, skipping settings check")
            return
        }

        val request = LocationSettingsRequest.Builder().addLocationRequest(locationRequest).build()

        mSettingsClient?.checkLocationSettings(request)
            ?.addOnSuccessListener {
                LogUtil.printLog(TAG_LOCATION, "GPS Success - requesting updates")
                requestLocationUpdate()
            }
            ?.addOnFailureListener { e ->
                if (e is ApiException) {
                    val statusCode = e.statusCode
                    when (statusCode) {
                        LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                            // Can't resolve from a Service context; notify the UI via broadcast so Activity can prompt user
                            LogUtil.printLog(TAG_LOCATION, "Location settings resolution required")
                            // Optionally broadcast an event so UI can show resolution dialog
                        }
                        LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> LogUtil.printLog(
                            TAG_LOCATION,
                            "Location settings are inadequate and cannot be fixed here."
                        )
                    }
                } else {
                    LogUtil.printLog(TAG_LOCATION, "checkLocationSettings failed: ${e.message}")
                }
            }
    }

    private fun requestLocationUpdate() {
        try {
            // Permission checks
            val hasFine = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            val hasCoarse = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
            if (!hasFine && !hasCoarse) {
                LogUtil.printLog(TAG_LOCATION, "Missing location permissions")
                return
            }

            // copy mutable vars to local vals to allow smart cast and avoid race conditions
            val locationRequest = mLocationRequest ?: return
            val locationCallback = mLocationCallback ?: return

            mFusedLocationClient?.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        } catch (e: SecurityException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun handleNewLocation(location: Location) {
        try {
            LogUtil.printLog(
                TAG_LOCATION,
                "Location Changed Latitude : ${location.latitude}\tLongitude : ${location.longitude}"
            )
            mCurrentLocation = location
            val intent = Intent(ACTION_BROADCAST).apply { putExtra(EXTRA_LOCATION, location) }
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent)

            val latStr = location.latitude.toString()
            val longStr = location.longitude.toString()
            if (latStr == "0.0" && longStr == "0.0") {
                requestLocationUpdate()
            } else {
                LogUtil.printLog(TAG_LOCATION, "Latitude : ${location.latitude}\tLongitude : ${location.longitude}")
                sharedPref.write(SharedPrefKey.LAT, latStr)
                sharedPref.write(SharedPrefKey.LONG, longStr)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    companion object {
        private const val PACKAGE_NAME = "com.fiveexceptions.lpr.scan"
        const val ACTION_BROADCAST = "$PACKAGE_NAME.broadcast"
        const val EXTRA_LOCATION = "$PACKAGE_NAME.location"
    }
}