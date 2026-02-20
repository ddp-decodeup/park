//package com.parkloyalty.lpr.scan.locationservice
//
//import android.annotation.TargetApi
//import android.app.*
//import android.content.Context
//import android.content.Intent
//import android.content.pm.PackageManager
//import android.content.pm.ServiceInfo
//import android.graphics.Color
//import android.location.Location
//import android.net.Uri
//import android.os.Build
//import android.os.IBinder
//import android.os.Looper
//import android.provider.Settings
//import android.util.Log
//import androidx.annotation.NonNull
//import androidx.core.app.ActivityCompat
//import androidx.core.app.NotificationCompat
//import androidx.localbroadcastmanager.content.LocalBroadcastManager
//import com.google.android.gms.location.*
//import com.parkloyalty.lpr.scan.BuildConfig
//import com.parkloyalty.lpr.scan.R
//import com.parkloyalty.lpr.scan.interfaces.SharedPrefKey
//import com.parkloyalty.lpr.scan.util.SharedPref
//
//
//class LocationService : Service() {
//    private lateinit var mFuse: FusedLocationProviderClient
//    private lateinit var locationRequest: LocationRequest
//    private lateinit var locationCallback: LocationCallback
//    private lateinit var locationSettingsRequest: LocationSettingsRequest
//    private val UPDATE_INTERVAL_IN_MILLISECONDS = 10000
//    private val FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 3000
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
//    override fun onBind(arg0: Intent): IBinder? {
//        return null
//    }
//
//    //TODO old code
////    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
////        super.onStartCommand(intent, flags, startId)
////        return super.onStartCommand(intent, flags, startId)
////    }
//
//    //TODO new code
//    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        super.onStartCommand(intent, flags, startId)
//        return START_STICKY
//    }
//
//    override fun onCreate() {
//        startForeground()
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        //stopSelf()
//
//        stopService = true
//        if (mFuse != null) {
//            mFuse?.removeLocationUpdates(locationCallback)
//            Log.e(TAG_LOCATION, "Location Update Callback Removed")
//        }
//    }
//
//
//    private fun startForeground() {
//        val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_ID.toString())
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
//            startMyOwnForeground()
//        else
////            startForeground(NOTIFICATION_ID, notificationBuilder.build(),ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION)
//            startForeground(NOTIFICATION_ID, notificationBuilder.build(),ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION)
////        startForeground(NOTIFICATION_ID, notificationBuilder.build(), ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC)
//
//
//        startLocation()
//    }
//
//    @NonNull
//    @TargetApi(26)
//    private fun startMyOwnForeground() {
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
//            PendingIntent.FLAG_ONE_SHOT/*Flag indicating that this PendingIntent can be used only once.*/
//        )
//
//        val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
//        val notification = notificationBuilder.setOngoing(true)
//            //.setContentTitle(this.baseContext.getString(R.string.app_name))
//            .setContentTitle("Location Service LL")
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
//    private fun startLocation() {
//        if (ActivityCompat.checkSelfPermission(
//                this,
//                android.Manifest.permission.ACCESS_FINE_LOCATION
//            )
//            != PackageManager.PERMISSION_GRANTED &&
//            ActivityCompat.checkSelfPermission(
//                this,
//                android.Manifest.permission.ACCESS_COARSE_LOCATION
//            )
//            != PackageManager.PERMISSION_GRANTED
//        ) {
//            return
//        }
//
//        locationRequest = LocationRequest()
//        locationRequest.interval = UPDATE_INTERVAL_IN_MILLISECONDS.toLong()
//        locationRequest.fastestInterval = FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS.toLong()
//        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
//        val builder = LocationSettingsRequest.Builder()
//        builder.addLocationRequest(locationRequest)
//
//        locationSettingsRequest = builder.build()
//
//        locationCallback = object : LocationCallback() {
//            override fun onLocationResult(locationResult: LocationResult) {
//                super.onLocationResult(locationResult)
//
//                Log.e(TAG_LOCATION, "Location Received")
//                mCurrentLocation = locationResult.lastLocation
//                onLocationChanged(mCurrentLocation!!)
//            }
//        }
//        mFuse = LocationServices.getFusedLocationProviderClient(applicationContext)
//        mFuse.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())
//    }
//
//    private fun onLocationChanged(location: Location) {
//        Log.e(
//            TAG_LOCATION,
//            "Location Changed Latitude : " + location.latitude + "\tLongitude : " + location.longitude
//        )
//        latitude = location.latitude.toString()
//        longitude = location.longitude.toString()
//        val intent = Intent(BackgroundLocationUpdateService.ACTION_BROADCAST)
//        intent.putExtra(BackgroundLocationUpdateService.EXTRA_LOCATION, location)
//        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
//        Log.e(
//            TAG_LOCATION,
//            "Latitude : " + location.latitude + "\tLongitude : " + location.longitude
//        )
//        SharedPref.getInstance(this).write(SharedPrefKey.LAT, location.latitude.toString())
//        SharedPref.getInstance(this).write(SharedPrefKey.LONG, location.longitude.toString())
//    }
//
//
//    companion object {
//        private const val PACKAGE_NAME = "com.fiveexceptions.lpr.scan"
//        const val ACTION_BROADCAST = PACKAGE_NAME + ".broadcast"
//        const val EXTRA_LOCATION = PACKAGE_NAME + ".location"
//    }
//}