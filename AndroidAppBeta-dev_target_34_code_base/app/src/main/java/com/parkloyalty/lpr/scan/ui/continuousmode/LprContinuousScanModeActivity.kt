package com.parkloyalty.lpr.scan.ui.continuousmode

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.widget.AppCompatImageView
import com.parkloyalty.lpr.scan.R
import com.parkloyalty.lpr.scan.basecontrol.BaseActivity
import com.parkloyalty.lpr.scan.basecontrol.BaseApplication
import com.parkloyalty.lpr.scan.database.AppDatabase
import com.parkloyalty.lpr.scan.database.Singleton
import com.parkloyalty.lpr.scan.interfaces.Constants
import com.parkloyalty.lpr.scan.interfaces.CustomDialogHelper
import com.parkloyalty.lpr.scan.interfaces.SharedPrefKey
import com.parkloyalty.lpr.scan.interfaces.TakeLicenceInterface
import com.parkloyalty.lpr.scan.network.NetworkCheck.isInternetAvailable
import com.parkloyalty.lpr.scan.ui.check_setup.activity.LprDetailsActivity
import com.parkloyalty.lpr.scan.ui.check_setup.license.ImenseLicenseServer
import com.parkloyalty.lpr.scan.ui.check_setup.model.lpr_model.LprScanLoggerRequest
import com.parkloyalty.lpr.scan.util.AppUtils

import com.parkloyalty.lpr.scan.util.AppUtils.splitDateLpr
import com.parkloyalty.lpr.scan.util.DATASET_SETTINGS_LIST
import com.parkloyalty.lpr.scan.util.LogUtil.printToastMSG
import java.io.File
import java.io.IOException
import java.text.ParseException
import java.util.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LprContinuousScanModeActivity : BaseActivity(), TakeLicenceInterface, CustomDialogHelper {
    var launchButton1: Button? = null
    var launchButton2: Button? = null
    var launchButton2b: Button? = null
    var launchButton2c: Button? = null
    var launchButton1_portrait: Button? = null
    var launchButton2_portrait: Button? = null
    var mBtnCross: AppCompatImageView? = null
    var licenseKey: String? = null
    var fileName = ""
    var ptPlusIntent: Intent? = null
    var testManualExposureControls = false
    var minimalUIandResetSettingsToDefaults = false
    private var mFileName: String? = null
    private var mSessionID: String? = null

    private var mPath: String? = null
    private var mScreenVisible = 0
    private var mDb: AppDatabase? = null
    private var mContext: Context? = null

    /** Called when the activity is first created.  */
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lpr_scan)
        setFullScreenUI()
        mEventStartTimeStamp = AppUtils.getDateTime()
        mContext = this@LprContinuousScanModeActivity
        mPath = sharedPreference.read(SharedPrefKey.FILE_PATH, "")
        //createFolder();
        mDb = BaseApplication.instance?.getAppDatabase()
        if (intent.hasExtra("KEY_FILE_NAME")) {
            mFileName = intent.getStringExtra("KEY_FILE_NAME")
        }
        if (intent.hasExtra("KEY_SESSION_ID")) {
            mSessionID = intent.getStringExtra("KEY_SESSION_ID")
        }
        //
//
        mBtnCross = findViewById(R.id.imgCross)
        mBtnCross?.setOnClickListener(View.OnClickListener { finish() })
        launchButton1 = findViewById<View>(R.id.launchButton1) as Button
        launchButton1!!.setOnClickListener { launchPTplus(false, false) }
        launchButton2 = findViewById<View>(R.id.launchButton2) as Button
        launchButton2!!.setOnClickListener { launchPTplus(true, false) }
        launchButton2b = findViewById<View>(R.id.launchButton2b) as Button
        launchButton2b!!.setOnClickListener {
            testManualExposureControls = true
            launchPTplus(true, false)
        }
        launchButton2c = findViewById<View>(R.id.launchButton2c) as Button
        launchButton2c!!.setOnClickListener {
            minimalUIandResetSettingsToDefaults = true
            launchPTplus(true, false)
        }
        launchButton1_portrait = findViewById<View>(R.id.launchButton1_portrait) as Button
        launchButton1_portrait!!.setOnClickListener { launchPTplus(false, true) }
        launchButton2_portrait = findViewById<View>(R.id.launchButton2_portrait) as Button
        launchButton2_portrait!!.setOnClickListener { launchPTplus(true, true) }
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            val iSLoginLogged = sharedPreference.read(SharedPrefKey.IS_LPR_LICENCE, false)
            if (iSLoginLogged) {
                // If we verify License
                launchPTplus(true, false)
            } else {
                // If we not verify license
                launchPTplus(false, false)
            }
        }, 100)
    }

    private fun createFolder() {
        val localFolder = File(Environment.getExternalStorageDirectory().absolutePath, "Lpr_scan")
        if (!localFolder.exists()) {
            localFolder.mkdirs()
        }
        val file = File(localFolder.toString())
        if (!file.exists()) {
            // file.delete(); //you might want to check if delete was successful
            try {
                file.createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
                return
            }
            //if file not exists the download
//            mFilepath = file.getAbsolutePath();
        }
    }

    /* Call Api For Lpr scan details */
    @Throws(IOException::class, ParseException::class)
    private fun callPushEventApi(LprNum: String) {
        val mWelcomeForm = mDb?.dbDAO?.getWelcomeForm()
        val mLat = sharedPreference.read(SharedPrefKey.LAT, "0.0")!!.toDouble()
        val mLong = sharedPreference.read(SharedPrefKey.LONG, "0.0")!!.toDouble()
        val myLocation = Geocoder(applicationContext, Locale.getDefault())
        val myList = myLocation.getFromLocation(mLat, mLong, 1)
        try {
            val address = myList!![0]
            if (isInternetAvailable(this@LprContinuousScanModeActivity)) {
                val mPushEventRequest = LprScanLoggerRequest()
                mPushEventRequest.activityType = "LPRScan"
                mPushEventRequest.lpNumber = LprNum
                mPushEventRequest.logType = Constants.LOG_TYPE_NODE_PORT
                mPushEventRequest.latitude = mLat
                mPushEventRequest.longitude = mLong
                var mZone: String? = "CST"
                if (Singleton.getDataSetList(DATASET_SETTINGS_LIST, getMyDatabase()) != null) {
                    mZone = Singleton.getDataSetList(DATASET_SETTINGS_LIST, getMyDatabase())!![0].mValue
                }
                mPushEventRequest.clientTimestamp = splitDateLpr(mZone)
                mPushEventRequest.siteOfficerName = mWelcomeForm!!.officerFirstName
                mPushEventRequest.supervisorName = mWelcomeForm.officerSupervisor
                mPushEventRequest.badgeId = mWelcomeForm.officerBadgeId
                mPushEventRequest.zone = mWelcomeForm.officerZone
                mPushEventRequest.beat = mWelcomeForm.officerBeatName
                mPushEventRequest.block = address.locality
                mPushEventRequest.street = address.locality
            } else {
                printToastMSG(
                    applicationContext,
                    getString(R.string.err_msg_connection_was_refused)
                )
            }
        } catch (e: Exception) {
        }
    }

    override fun onStart() {
        super.onStart()
        mScreenVisible++
        if (mScreenVisible == 2) {
            finish()
            launchScreen(this, LprDetailsActivity::class.java)
        }
    }

    fun launchPTplus(admin: Boolean, portraitOrientation: Boolean) {
        //obtain an Intent to launch ANPR/ALPR Platform Plus
        try {
            ptPlusIntent = Intent()
            ptPlusIntent!!.component = ComponentName(
                "com.imense.anprPlatformPlusIntentUS",
                "com.imense.anprPlatformPlusIntentUS.ImenseParkingEnforcer"
            )
            //            ptPlusIntent.setComponent(new ComponentName("com.imense.anprPlatformPlusIntentUS", "com.imense.anprPlatformPlusIntentUS.ImenseParkingEnforcer"));

            //authenticate the request with the correct invocation code
            if (true) ptPlusIntent!!.putExtra(
                "invocationcode",
                INVOCATION_ADMIN
            ) else ptPlusIntent!!.putExtra("invocationcode", INVOCATION_USER)


            //set PT into portrait mode (not recommended since it reduces effective plate pixel resolution)
            if (portraitOrientation) ptPlusIntent!!.putExtra("orientation", "portrait")
            if (testManualExposureControls) {
                //test manual camera controls
                ptPlusIntent!!.putExtra(
                    "preferences_show_anpr_fps",
                    "true"
                ) //Display ANPR reads per second. Value can be "true" or "false" (default="true")
                ptPlusIntent!!.putExtra(
                    "preferences_showexposurecontrols",
                    "true"
                ) //Display shutter and ISO controls. Value can be "true" or "false" (default="true")
                ptPlusIntent!!.putExtra(
                    "preferences_showfocuscontrol",
                    "false"
                ) //Display focus distance control. Value can be "true" or "false" (default="false"). Note: some devices do not support setting shutter, ISO and focus independently, i.e. either all of them have to be set to "auto" or all of them must have manual values specified.
                ptPlusIntent!!.putExtra(
                    "preferences_chosen_iso",
                    "500"
                ) //Attempt to set the ISO (sensor sensitivity) for the device camera. The range of values actually supported depends on the device and its software stack. Possible values are {"AutoISO", "6400", "3200", "2500", "2000", "1600", "1250", "1000", "800", "640", "500", "400", "320", "250", "200", "160", "125", "80", "64", "50", "32", "25"}; The default/fallback is "AutoISO".
                ptPlusIntent!!.putExtra(
                    "preferences_chosen_shutter",
                    "1/2000"
                ) //Attempt to set the shutter speed (expressed in terms of exposure time in seconds) for the device camera. The range of values actually supported depends on the device and its software stack. Possible values are {"AutoExp", "1/50k", "1/30k", "1/25k", "1/20k", "1/15k", "1/10k", "1/8000", "1/6000", "1/5000", "1/4000", "1/3000", "1/2000", "1/1000", "1/500", "1/250", "1/160", "1/125", "1/100", "1/60", "1/50", "1/30", "1/25", "1/15", "1/10", "1/8"}; The default/fallback is "AutoExp".
                ptPlusIntent!!.putExtra(
                    "preferences_videoResolutionWidth",
                    "1920"
                ) //Preferred device video resolution (horizontal pixels). Value must be positive numeric.
                ptPlusIntent!!.putExtra(
                    "preferences_videoResolutionHeight",
                    "1080"
                ) //Preferred device video resolution (vertical pixels). Value must be positive numeric.
                ptPlusIntent!!.putExtra(
                    "preferences_viewfinder",
                    "true"
                ) //Enable second adjustable zone-of-interest. Value can be "true" or "false" (default="false")
                ptPlusIntent!!.putExtra(
                    "preferences_viewfinder2",
                    "false"
                ) //Enable second adjustable zone-of-interest. Value can be "true" or "false" (default="false")
                ptPlusIntent!!.putExtra(
                    "preferences_accuracyVsSpeed",
                    "4"
                ) //Accuracy vs Speed (0: most accurate; 4: fastest).
                testManualExposureControls = false //reset
            } else if (minimalUIandResetSettingsToDefaults) {
                ptPlusIntent!!.putExtra(
                    "hideUI",
                    "1"
                ) //If value is "1" then hide all UI elements (apart from viewfinder), regardless of other settings
                ptPlusIntent!!.putExtra(
                    "resetSettingsToDefaults",
                    "true"
                ) //Reset ALL settings (except license key) to default values
                minimalUIandResetSettingsToDefaults = false //reset
            }


            //optionally instruct PTplus to start scan (i.e. invoke shutter button) immediately; 0: off; 1: start scan using in-built device camera
            ptPlusIntent!!.putExtra("startscan", "1")
            ptPlusIntent!!.putExtra(
                "hideUI",
                "0"
            ) //If value is "1" then hide all UI elements (apart from viewfinder), regardless of other settings

            /*//////////////////////////////
			//Optionally explicitly specify values for settings such as folder for data and images, option to save context image, scan time threshold, minimum confidence threshold, region and read options.

			ptPlusIntent.putExtra("hideUI", "0"); //If value is "1" then hide all UI elements (apart from viewfinder), regardless of other settings

			ptPlusIntent.putExtra("returnOnScanTimeout", "1"); //Return control to invoking application (with "PT_ANPR_SCANTIMEOUT") on a continuous scan timeout (see also "preferences_scanTimeout" and "startscan")

			/////////// PTplus new settings

			ptPlusIntent.putExtra("preferences_show_anpr_fps", "true"); //Display ANPR reads per second. Value can be "true" or "false" (default="true")
			ptPlusIntent.putExtra("preferences_showexposurecontrols", "true"); //Display shutter and ISO controls. Value can be "true" or "false" (default="true")
			ptPlusIntent.putExtra("preferences_showfocuscontrol", "false"); //Display focus distance control. Value can be "true" or "false" (default="false")
			ptPlusIntent.putExtra("preferences_zoiFixed", "false"); //Fix position and size of ZOI. Value can be "true" or "false" (default="false")
			ptPlusIntent.putExtra("preferences_zoiGranularity", "false"); //Enable ZOI adjustment grid. Value can be "true" or "false" (default="false")

			ptPlusIntent.putExtra("preferences_zoiAuto", "false"); //Automatically adjust ZOI based on recent observations. Value can be "true" or "false" (default="false"). "True" implies that "preferences_zoiFixed" must be false.
			ptPlusIntent.putExtra("preferences_trackingmode", "false"); //Tracking mode (predict next plate location and size based on recent observations). Value can be "true" or "false" (default="false")

			ptPlusIntent.putExtra("preferences_resultdisplaysize", "0"); //Font size used to display scan results (0: Default; 1: Large; 2: Medium; 3: Small; 4: Tiny).

			ptPlusIntent.putExtra("preferences_chosen_iso", "AutoISO"); //Attempt to set the ISO (sensor sensitivity) for the device camera. The range of values actually supported depends on the device and its software stack. Possible values are {"AutoISO", "6400", "3200", "2500", "2000", "1600", "1250", "1000", "800", "640", "500", "400", "320", "250", "200", "160", "125", "80", "64", "50", "32", "25"}; The default/fallback is "AutoISO". Note: it is generally not possible to set automatic ISO in conjunction with a manual shutter speed and vice versa: either both have to be set to "auto" or both have to be set to specific values.

			ptPlusIntent.putExtra("preferences_chosen_shutter", "AutoExp"); //Attempt to set the shutter speed (expressed in terms of exposure time in seconds) for the device camera. The range of values actually supported depends on the device and its software stack. Possible values are {"AutoExp", "1/50k", "1/30k", "1/25k", "1/20k", "1/15k", "1/10k", "1/8000", "1/6000", "1/5000", "1/4000", "1/3000", "1/2000", "1/1000", "1/500", "1/250", "1/160", "1/125", "1/100", "1/60", "1/50", "1/30", "1/25", "1/15", "1/10", "1/8"}; The default/fallback is "AutoExp". Note: it is generally not possible to set automatic shutter speed in conjunction with a manual ISO and vice versa: either both have to be set to "auto" or both have to be set to specific values.

			ptPlusIntent.putExtra("preferences_chosen_focusdist", "AF"); //Attempt to set the focus distance for the device camera. The range of values actually supported depends on the device and its software stack. Possible values are {"AF", "inf", "25m", "15m", "10m", "5m", "4m", "3m", "2m", "1m", "50cm", "35cm", "25cm", "15cm", "10cm", "7cm", "5cm"}; The default/fallback is "AF".

			ptPlusIntent.putExtra("preferences_alertsListRatherThanWhitelist", "true"); //Alerts list rather than Whitelist. Value can be "true" or "false" (default="true")
			ptPlusIntent.putExtra("preferences_showmagnifier", "true"); //Display magnifier button. Value can be "true" or "false" (default="true")
			ptPlusIntent.putExtra("preferences_viewfinder2", "false"); //Enable second adjustable zone-of-interest. Value can be "true" or "false" (default="false")
			ptPlusIntent.putExtra("preferences_minplatelength", "4"); //Minimum plate length (#characters). Value must be positive numeric, default="4".
			ptPlusIntent.putExtra("preferences_accuracyVsSpeed", "0"); //Accuracy vs Speed (0: most accurate; 4: fastest).
			ptPlusIntent.putExtra("preferences_IRmode", "false"); //Enable infrared ANPR. Value can be "true" or "false" (default="false")
			ptPlusIntent.putExtra("preferences_dataButtonPersistence", "5"); //Show data entry button for this many seconds after plate observation. Value must be positive numeric, default="5".

			//Scene modes (aspects) for "drive-by" ANPR. See documentation for details. Scene mode settings are applied after other settings and may modify and override  other options set via the API.
			//Currently valid values for "sceneMode" are:
			//	sceneMode = "Left roadside only";		//C1 = Centre Mount - Scanning Roadside Parked cars on Left
			//	sceneMode = "Right roadside only";		//C2 = Centre Mount - Scanning Roadside Parked cars on Right
			//	sceneMode = "Left&right roadside (narrow road)";		//C3 = Centre Mount - Scanning Roadside parked cars on both sides of Narrow Road
			//	sceneMode = "Left&right roadside (wide road, slow speed)";		//C3 = Centre Mount - Scanning Roadside parked cars on both sides of Narrow Road
			//	sceneMode = "Oncoming left lane";   //C4 = Left Mount - Scanning Oncoming cars on the Left lane
			//	sceneMode = "Oncoming right lane";	//C5 = Right Mount - Scanning Oncoming cars on the Right lane
			//	sceneMode = "Parked left parallel and/or roadside";	//L1 = Left Mount - Scanning Roadside & Parallel parked Cars on Left
			//	sceneMode = "Adjacent left lane from on or off-road";	//L3 = Left Mount - Scanning Moving Cars on Adjacent Left lane
			//	sceneMode = "Left two lanes from off-road";	//L2 = Left Mount - Scanning Moving Cars on Left Two lanes
			//	sceneMode = "Parked right parallel and/or roadside";	//R1 = Right Mount - Scanning Roadside & Parallel parked Cars on Right
			//	sceneMode = "Adjacent right lane from on or off-road";	//R3 = Right Mount - Scanning Moving Cars on Adjacent Right lanes
			//	sceneMode = "Right two lanes from off-road";	//R2 = Right Mount - Scanning Moving Cars on Right Two lanes
			ptPlusIntent.putExtra("preferences_sceneMode", sceneMode);

			ptPlusIntent.putExtra("resetSettingsToDefaults", "false"); //Reset ALL settings (except license key) to default values


			///////////List settings
			ptPlusIntent.putExtra("preferences_saveimages_path", "/mnt/sdcard"); //Folder for data and images; has to exist and be writable
			ptPlusIntent.putExtra("preferences_vehiclesfilename", "parkingList.csv"); //Vehicles list file name. Default value="parkingList.csv"
			ptPlusIntent.putExtra("preferences_alertsfilename", "parkingAlerts.csv"); //Alerts list\Whitelist file name. Default value="parkingAlerts.csv"
			ptPlusIntent.putExtra("preferences_alertsfilename", "parkingAlerts"+fileName+".csv"); //Alerts list\Whitelist file name. Default value="parkingAlerts.csv"

			///////////General settings
			ptPlusIntent.putExtra("preferences_savecutouts", "true"); //Save plate cut-out image after every good read. Value can be "true" or "false" (default="true")
			ptPlusIntent.putExtra("preferences_savecontextimages", "false"); //Save context image to SD card after every good read. Value can be "true" or "false" (default="false")
			ptPlusIntent.putExtra("preferences_savecontextimagescolour", "false"); //Save context images in colour. Value can be "true" or "false" (default="false")

			ptPlusIntent.putExtra("preferences_expungePlatesAfterNhours", "72"); //Expunge vehicle list entries after this many hours. Value must be positive numeric, default="72".
			ptPlusIntent.putExtra("preferences_warnAfterNmins", "0"); //Warn if parked vehicle time exceeds this many minutes. Value must be positive numeric, default="0".

			ptPlusIntent.putExtra("preferences_confGoodread", "80"); //"High confidence threshold (0-100). Value must be positive numeric, default="80".

			ptPlusIntent.putExtra("preferences_scanTimeout", "90"); //Continuous scan timeout (seconds). Value must be positive numeric, default="120".

			ptPlusIntent.putExtra("preferences_playsound", "true"); //Play beep after every high confidence scan. Value can be "true" or "false" (default="true")
			ptPlusIntent.putExtra("preferences_showsingleshot", "false"); //Display button to save single image to SD card. Value can be "true" or "false" (default="false")
			ptPlusIntent.putExtra("preferences_saveSingleshotInColour", "false"); //Store single PIC images in colour. Value can be "true" or "false" (default="false")

			ptPlusIntent.putExtra("preferences_showtorch", "false"); //Display torch button. Value can be "true" or "false" (default="false")

			ptPlusIntent.putExtra("preferences_viewfinder", "false"); //Enable adjustable zone-of-interest within viewfinder for faster processing. Value can be "true" or "false" (default="false")

		   ///////////Optionally restrict ANPR to specific countries, regions or states

			//ONLY ONE of the following can be set. The default is "All regions: no restriction".
			ptsIntent.putExtra("preferences_region", "All regions: no restriction"); //No restrictions, i.e. all countries are considered.
			//ptsIntent.putExtra("preferences_region", "Quebec, Canada");
			//ptsIntent.putExtra("preferences_region", "Alberta, Canada");
			//ptsIntent.putExtra("preferences_region", "Ontario, Canada");
			//ptsIntent.putExtra("preferences_region", "Pennsylvania, USA");
			//ptsIntent.putExtra("preferences_region", "Maryland and DC, USA");
			//ptsIntent.putExtra("preferences_region", "New York, USA");
			//ptsIntent.putExtra("preferences_region", "Florida, USA");
			//ptsIntent.putExtra("preferences_region", "Idaho, USA");
			//ptsIntent.putExtra("preferences_region", "Mexico");


			///////////Parking Bay Numbers
			ptPlusIntent.putExtra("preferences_pbn_enable", "false"); //Automatically apply PBN (parking bay number). Value can be "true" or "false" (default="false")
			ptPlusIntent.putExtra("preferences_pbn_prefix", ""); //PBN prefix string. Text value of 0 to 5 characters, default is "" (empty string).
			ptPlusIntent.putExtra("preferences_pbn_start", "00"); //PBN start value that is applied to the next parking bay. Must be a string of digits of between 2 and 5 characters, default is "00".
			ptPlusIntent.putExtra("preferences_pbn_increment", "1"); //PBN increment value (can be positive or negative). Must be a string of digits (optionally starting with "-" to indicate a negative increment) of between 1 and 3 characters, default is "1".

			///////////Custom Data Fields
			ptPlusIntent.putExtra("preferences_data1prompt", "Custom Data 1"); //Prompt for custom data field 1. Must be a text string of 0 to 20 characters, default is "Custom Data 1".
			ptPlusIntent.putExtra("preferences_data2prompt", "Custom Data 2"); //Prompt for custom data field 2. Must be a text string of 0 to 20 characters, default is "Custom Data 2".
			ptPlusIntent.putExtra("preferences_data3prompt", "Custom Data 3"); //Prompt for custom data field 3. Must be a text string of 0 to 20 characters, default is "Custom Data 3".

			ptPlusIntent.putExtra("preferences_audiomax", "60"); //Maximum duration of voice note audio recordings in seconds. Value must be positive numeric, default="60".


			///////////Advanced Settings
			ptPlusIntent.putExtra("preferences_minConsecutiveReads", "1"); //Minimum number of consecutive ANPR reads of a particular plate before result can be accepted (default=1)

			ptPlusIntent.putExtra("preferences_videoResolutionWidth", "800"); //Preferred device video resolution (horizontal pixels). Value must be positive numeric.
			ptPlusIntent.putExtra("preferences_videoResolutionHeight", "600"); //Preferred device video resolution (vertical pixels). Value must be positive numeric.

			// **/
            ptPlusIntent!!.putExtra(
                "preferences_vehiclesfilename",
                "vehicleList_$mFileName.csv"
            ) //Alerts list\Whitelist file name. Default value="parkingAlerts.csv"
            ptPlusIntent!!.putExtra(
                "preferences_viewfinder",
                "true"
            ) // VINOD jali Enable second adjustable zone-of-interest. Value can be "true" or "false" (default="false")
            ptPlusIntent!!.putExtra(
                "preferences_viewfinder2",
                "false"
            ) //Enable second adjustable zone-of-interest. Value can be "true" or "false" (default="false")
            ptPlusIntent!!.putExtra(
                "preferences_zoiGranularity",
                "false"
            ) //Enable ZOI adjustment grid. Value can be "true" or "false" (default="false")
            ptPlusIntent!!.putExtra(
                "preferences_showmagnifier",
                "false"
            ) //Display magnifier button. Value can be "true" or "false" (default="true")
            ptPlusIntent!!.putExtra(
                "preferences_zoiFixed",
                "true"
            ) //VINOD jali Fix position and size of ZOI. Value can be "true" or "false" (default="false")
            ptPlusIntent!!.putExtra(
                "preferences_alertsListRatherThanWhitelist",
                "true"
            ) // VINOD true mean continuous mode and false means single scan and return to result
            ptPlusIntent!!.putExtra(
                "preferences_show_anpr_fps",
                "false"
            ) //Display ANPR reads per second. Value can be "true" or "false" (default="true")
            ptPlusIntent!!.putExtra(
                "preferences_showexposurecontrols",
                "false"
            ) //Display shutter and ISO controls. Value can be "true" or "false" (default="true")
            ptPlusIntent!!.putExtra(
                "preferences_saveimages_path",
                mPath + Constants.COTINOUS
            ) //Folder for data and images; has to exist and be writable

            //ptPlusIntent.putExtra("preferences_showsingleshot", "true"); //Display button to save single image to SD card. Value can be "true" or "false" (default="false")
            //ptPlusIntent.putExtra("preferences_saveSingleshotInColour", "true"); //Store single PIC images in colour. Value can be "true" or "false" (default="false")
            //ptPlusIntent.putExtra("preferences_savecutouts", "true"); //Save plate cut-out image after every good read. Value can be "true" or "false" (default="true")
            //ptPlusIntent.putExtra("preferences_savecontextimages", "true"); //Save context image to SD card after every good read. Value can be "true" or "false" (default="false")
            //ptPlusIntent.putExtra("preferences_savecontextimagescolour", "true"); //Save context images in colour. Value can be "true" or "false" (default="false")
            ptPlusIntent!!.putExtra("returnOnScanTimeout", "1")
            //ptPlusIntent.putExtra("preferences_scanTimeout", "15");
            //if we already have a license key, we send it to Platform Plus
            // Toast.makeText(LprContinuousScanModeActivity.this,  licenseKey, Toast.LENGTH_LONG).show();
            ptPlusIntent!!.putExtra(
                "preferences_scanTimeout",
                "200"
            ) //Continuous scan timeout (seconds). Value must be positive numeric, default="120".
            if (licenseKey != null) ptPlusIntent!!.putExtra("licensekey", licenseKey)
            if (debug > 0) Log.d(tag, "startActivityForResult ptPlusIntent=$ptPlusIntent")
            startActivityForResult(ptPlusIntent!!, REQUESTCODE)
        } catch (err: Exception) {
            /**/
            if (debug > 0) {
                Log.e(tag, "launchPTplus Error: $err")
                err.printStackTrace()
            }
            //            Toast.makeText(LprContinuousScanModeActivity.this, "US ALPR PTplus Intent not found: please install it", Toast.LENGTH_LONG).show();
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        var returnMessage = 0
        if (data != null) returnMessage = data.extras!!.getInt("message")
        if (debug > 0) Log.d(
            tag,
            "onActivityResult:  requestCode=$requestCode, resultCode=$resultCode, data=$data, ptPlusIntent=$ptPlusIntent, returnMessage=$returnMessage"
        )
        if (returnMessage == PT_SCAN_SUCCESS) {
        } else if (returnMessage == PT_ANPR_NOTONWHITELIST) {
            val sRegNumber = data!!.extras!!.getString("anpr_not_in_whitelist")
            val regConf = data.extras!!.getInt("anpr_not_in_whitelist_conf")
            //            finish();
//            Intent mIntent = new Intent(LprContinuousScanModeActivity.this,ContinuousResultActivity.class);
//            mIntent.putExtra("lpr_number", sRegNumber);
//            startActivity(mIntent);
            //user event logging
//            try {
//                callPushEventApi(sRegNumber);
//            } catch (IOException | ParseException e) {
//                e.printStackTrace();
//            }
            //Toast.makeText(this, "PTplusUS returned with vehicle plate that is not in the whitelist: "+sRegNumber+" (conf="+regConf+")", Toast.LENGTH_LONG).show();
        } else if (returnMessage == PT_ANPR_PERMITEXPIRED) {
            val sRegNumber = data!!.extras!!.getString("anpr_permit_expired")
            val regConf = data.extras!!.getInt("anpr_permit_expired_conf")
            val sTimeExceeded = data.extras!!.getString("time_since_permit_expired")
            //            Toast.makeText(this, "PTplusUS returned with whitelisted plate: "+sRegNumber+" (conf="+regConf+") having exceeded parking permit by "+sTimeExceeded, Toast.LENGTH_LONG).show();
        } else if (returnMessage == PT_ANPR_SCANTIMEOUT) {
            finish()
            val mIntent =
                Intent(this@LprContinuousScanModeActivity, ContinuousResultActivity::class.java)
            mIntent.putExtra("KEY_FILE_NAME", mFileName)
            mIntent.putExtra("KEY_SESSION_ID", mSessionID)
            //        mIntent.putExtra("lpr_number", sRegNumber);
            startActivity(mIntent)
            //launchScreenClear(LprContinuousScanModeActivity.this,LprDetailsActivity.class);
//            Toast.makeText(this, "PTplusUS returned after scan timeout", Toast.LENGTH_LONG).show();
        } else if (returnMessage == PT_LICENSE_MISSING_OR_INVALID) {
            val deviceID = data!!.extras!!.getString("duid") //unique device ID
            val caller = this
            ImenseLicenseServer(sharedPref = sharedPreference,androidAppContext1 = caller, device_uid = deviceID).execute()
            //            //obtain new license key
//            new AlertDialog.Builder(this)
//                    .setTitle( "License Verification Problem" )
//                    .setCancelable(false)
//                    .setMessage( "PTplus reports: license key missing or invalid. Please ensure that your device's WiFi adapter is enabled and has Internet access, then "+
//                            "click <"+this.getString(android.R.string.ok)+"> to (re)generate a valid license key from our server.")
//                    .setPositiveButton( android.R.string.ok,
//                            new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog,
//                                                    int which) {
//                                    dialog.dismiss();
//
//                                    // try to obtain new license key from Imense Server
//                                    new ImenseLicenseServer( caller, deviceID ).execute();
//                                }
//                            })
//                    .setNegativeButton( android.R.string.cancel,
//                            new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog,
//                                                    int which) {
//                                    dialog.dismiss();
//                                }
//                            }).show();
        } else {
            finish()
            val mIntent =
                Intent(this@LprContinuousScanModeActivity, ContinuousResultActivity::class.java)
            mIntent.putExtra("KEY_FILE_NAME", mFileName)
            //        mIntent.putExtra("lpr_number", sRegNumber);
            startActivity(mIntent)
        }
    }

    override fun onBackPressed() {
//        super.onBackPressed();
        launchScreen(this@LprContinuousScanModeActivity, ContinuousResultActivity::class.java)
    }

    override fun onGetLience() {
        //Toast.makeText(LprContinuousScanModeActivity.this,  "get licence interface", Toast.LENGTH_LONG).show();
        launchPTplus(false, true)
    }

    override fun onYesButtonClick() {}
    override fun onNoButtonClick() {}
    override fun onYesButtonClickParam(msg: String?) {}

    companion object {
        var tag = "launchPTplusUS" //tag for debugging

        /**/
        const val debug = 1

        //invocation codes for ANPR/ALPR Platform Plus
        //    private final static String INVOCATION_USER  = "3AAdkhd8Bdsug551k87"; //Standard user: not allowed to change preferences or view list entries
        //    private final static String INVOCATION_ADMIN = "Ndkp2kgs7JGs581Hka0"; //Privileged user: able to change settings and/or edit list entries
        private const val INVOCATION_USER =
            "3AAdkhd8Bdsug551k87" //Standard user: not allowed to change preferences or view list entries
        private const val INVOCATION_ADMIN =
            "Ndkp2kgs7JGs581Hka0" //Privileged user: able to change settings and/or edit list entries
        private const val REQUESTCODE = 55

        //return messages from ANPR/ALPR Platform Plus
        private const val PT_SCAN_SUCCESS = 99999
        private const val PT_INVALID_INVOCATION = 99
        private const val PT_LICENSE_MISSING_OR_INVALID = 100
        private const val PT_ANPR_NOTONWHITELIST = 101
        private const val PT_ANPR_PERMITEXPIRED = 102
        private const val PT_ANPR_SCANTIMEOUT = 103
    }
}