//package com.parkloyalty.lpr.scan.locationservice
//
//import android.Manifest
//import android.app.Notification
//import android.app.NotificationChannel
//import android.app.NotificationManager
//import android.app.PendingIntent
//import android.app.Service
//import android.content.Context
//import android.content.Intent
//import android.content.pm.PackageManager
//import android.graphics.Color
//import android.location.Location
//import android.net.Uri
//import android.os.IBinder
//import android.os.Looper
//import android.provider.Settings
//import android.util.Log
//import androidx.core.app.ActivityCompat
//import androidx.core.app.NotificationCompat
//import androidx.localbroadcastmanager.content.LocalBroadcastManager
//import com.google.android.gms.location.FusedLocationProviderClient
//import com.google.android.gms.location.LocationCallback
//import com.google.android.gms.location.LocationRequest
//import com.google.android.gms.location.LocationResult
//import com.google.android.gms.location.LocationServices
//import com.google.android.gms.location.Priority
//import com.parkloyalty.lpr.scan.BuildConfig
//import com.parkloyalty.lpr.scan.R
//import com.parkloyalty.lpr.scan.interfaces.SharedPrefKey
//import com.parkloyalty.lpr.scan.util.SharedPref
//
//class LocationServiceUpdated : Service() {
//
//    private lateinit var fusedLocationClient: FusedLocationProviderClient
//    private lateinit var locationRequest: LocationRequest
//    private lateinit var locationCallback: LocationCallback
//
//
//    private val MIN_INTERVAL_IN_MILLISECONDS = 2000L
//    private val UPDATE_INTERVAL_IN_MILLISECONDS = 2000L
//    private val FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 5000L
//
//    private val TAG = "BackgroundLocationUpdateService"
//    private val TAG_LOCATION = "TAG_LOCATION"
//    private var context: Context? = null
//    private var stopService = false
//
//    /* For Google Fused API */
//    private var latitude = "0.0"
//    private var longitude = "0.0"
//    private var mCurrentLocation: Location? = null
//
//    private val NOTIFICATION_ID = 46542
//
//    override fun onCreate() {
//        super.onCreate()
//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
//
//        locationRequest = LocationRequest.Builder(
//            Priority.PRIORITY_HIGH_ACCURACY,
//            UPDATE_INTERVAL_IN_MILLISECONDS
//        )
//            .setMinUpdateIntervalMillis(MIN_INTERVAL_IN_MILLISECONDS)
//            .setWaitForAccurateLocation(false)
//            .setMaxUpdateDelayMillis(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS)
//            .build()
//
//
//        locationCallback = object : LocationCallback() {
//            override fun onLocationResult(result: LocationResult) {
//                for (location in result.locations) {
//                    Log.d(
//                        "==>LocationServiceUpdated:ChangedForLopp",
//                        "Location: ${location.latitude}, ${location.longitude}"
//                    )
//                    // Send to server or save locally
//                }
//
//                mCurrentLocation = result.lastLocation
//                onLocationChanged(mCurrentLocation!!)
//            }
//        }
//
//        createNotificationChannelAndStartForeground()
//        startLocationUpdates()
//    }
//
//    private fun onLocationChanged(location: Location) {
//        Log.e(
//            "==>LocationServiceUpdated:Changed",
//            "Location Changed Latitude : " + location.latitude + "\tLongitude : " + location.longitude
//        )
//        latitude = location.latitude.toString()
//        longitude = location.longitude.toString()
//        val intent = Intent(BackgroundLocationUpdateService.ACTION_BROADCAST)
//        intent.putExtra(BackgroundLocationUpdateService.EXTRA_LOCATION, location)
//        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
//        Log.e(
//            "==>LocationServiceUpdated:Changed",
//            "Latitude : " + location.latitude + "\tLongitude : " + location.longitude
//        )
//        SharedPref.getInstance(this).write(SharedPrefKey.LAT, location.latitude.toString())
//        SharedPref.getInstance(this).write(SharedPrefKey.LONG, location.longitude.toString())
//    }
//
//    private fun createNotificationChannelAndStartForeground() {
//        val NOTIFICATION_CHANNEL_ID = BuildConfig.APPLICATION_ID
//        val channelName = this.getString(R.string.notification_channel_live_location)
//        val chan = NotificationChannel(
//            NOTIFICATION_CHANNEL_ID,
//            channelName,
//            NotificationManager.IMPORTANCE_DEFAULT
//        )
//
//        chan.lightColor = Color.BLUE
//        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
//        chan.setShowBadge(false)
//        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//        manager.createNotificationChannel(chan)
//
//        val myAppSettings =
//            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:$packageName"))
//        myAppSettings.addCategory(Intent.CATEGORY_DEFAULT)
//        myAppSettings.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//
//        val pendingIntent = PendingIntent.getActivity(
//            this,
//            0,
//            myAppSettings,
//            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE/*Flag indicating that this PendingIntent can be used only once.*/
//        )
//
//        val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
//        val notification = notificationBuilder.setOngoing(true)
//            //.setContentTitle(this.baseContext.getString(R.string.app_name))
//            .setContentTitle("Location Service")
//            .setContentText("Service is working in the background.")
//            .setPriority(NotificationManager.IMPORTANCE_MIN)
//            .setCategory(Notification.CATEGORY_SERVICE)
//            .setContentIntent(pendingIntent)
//            .setSmallIcon(R.drawable.ic_lpr_app)
//            .build()
//
//        startForeground(NOTIFICATION_ID, notification)
//    }
//
//    private fun startLocationUpdates() {
//        if (ActivityCompat.checkSelfPermission(
//                this,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            Log.e("LocationService", "Location permission not granted")
//            return
//        }
//        fusedLocationClient.requestLocationUpdates(
//            locationRequest,
//            locationCallback,
//            Looper.getMainLooper()
//        )
//    }
//
//    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        return START_STICKY // Ensures service restarts if killed
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        fusedLocationClient.removeLocationUpdates(locationCallback)
//    }
//
//    override fun onBind(intent: Intent?): IBinder? = null
//
//
//    /* How to call this class ?
//    *         val serviceIntent = Intent(this, LocationServiceUpdated::class.java)
//        startForegroundService(serviceIntent)
//    * */
//}
