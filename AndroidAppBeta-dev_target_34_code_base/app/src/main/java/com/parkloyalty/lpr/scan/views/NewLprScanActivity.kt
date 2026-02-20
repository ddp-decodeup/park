package com.parkloyalty.lpr.scan.views

import android.content.ComponentName
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.window.OnBackInvokedCallback
import android.window.OnBackInvokedDispatcher
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.parkloyalty.lpr.scan.BuildConfig
import com.parkloyalty.lpr.scan.extensions.logD
import com.parkloyalty.lpr.scan.extensions.logE
import com.parkloyalty.lpr.scan.extensions.toast
import com.parkloyalty.lpr.scan.interfaces.Constants
import com.parkloyalty.lpr.scan.interfaces.SharedPrefKey
import com.parkloyalty.lpr.scan.util.AppUtils.getDeviceTypePhoneOrTablet
import com.parkloyalty.lpr.scan.util.DoubangoConstants.CLASS_ALPR_VIDEO_PARALLEL_ACTIVITY
import com.parkloyalty.lpr.scan.util.DoubangoConstants.FROM_POINT_AND_SCAN
import com.parkloyalty.lpr.scan.util.DoubangoConstants.KEY_IS_LANDSCAPE_ORIENTATION_REQUIRED
import com.parkloyalty.lpr.scan.util.DoubangoConstants.KEY_IS_LENS_STAB
import com.parkloyalty.lpr.scan.util.DoubangoConstants.KEY_IS_VIDEO_STAB
import com.parkloyalty.lpr.scan.util.DoubangoConstants.KEY_PATH_TO_SAVE_IMAGE
import com.parkloyalty.lpr.scan.util.DoubangoConstants.KEY_VEHICLE_DETAIL_DATA
import com.parkloyalty.lpr.scan.util.DoubangoConstants.MODEL_VEHICLE_DETAIL_DATA
import com.parkloyalty.lpr.scan.util.FileUtil
import com.parkloyalty.lpr.scan.util.INTENT_KEY_FROM
import com.parkloyalty.lpr.scan.util.INTENT_KEY_LPR_BUNDLE
import com.parkloyalty.lpr.scan.util.INTENT_KEY_MESSAGE
import com.parkloyalty.lpr.scan.util.INTENT_KEY_SCANNER_TYPE
import com.parkloyalty.lpr.scan.util.SCANNER_TYPE_DOUBANGO
import com.parkloyalty.lpr.scan.util.SharedPref
import com.parkloyalty.lpr.scan.util.imense.ImenseConstants.INVOCATION_ADMIN
import com.parkloyalty.lpr.scan.util.imense.ImenseConstants.INVOCATION_USER
import com.parkloyalty.lpr.scan.util.imense.ImenseConstants.PT_ANPR_NOTONWHITELIST
import com.parkloyalty.lpr.scan.util.imense.ImenseConstants.PT_ANPR_PERMITEXPIRED
import com.parkloyalty.lpr.scan.util.imense.ImenseConstants.PT_ANPR_SCANTIMEOUT
import com.parkloyalty.lpr.scan.util.imense.ImenseConstants.PT_LICENSE_MISSING_OR_INVALID
import com.parkloyalty.lpr.scan.util.imense.ImenseConstants.PT_SCAN_SUCCESS
import com.parkloyalty.lpr.scan.util.imense.ImmenseLicenceInterface
import com.parkloyalty.lpr.scan.util.imense.NewImenseLicenseServer
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

//Copyright Imense Ltd 2021. Unauthorised usage or distribution strictly prohibited.
@AndroidEntryPoint
class NewLprScanActivity : AppCompatActivity() {

    @Inject
    lateinit var imenseLicenseServer: NewImenseLicenseServer

    @Inject
    lateinit var sharedPreference: SharedPref

    private lateinit var imenseScanActivityLauncher: ActivityResultLauncher<Intent>

    private lateinit var doubangoScanActivityLauncher: ActivityResultLauncher<Intent>

    private var legacyBackCallback: OnBackPressedCallback? = null
    private var predictiveCallback: OnBackInvokedCallback? = null

    var licenseKey: String? = null
    var ptPlusIntent: Intent? = null
    var testManualExposureControls = false
    var minimalUIandResetSettingsToDefaults = false

    private var scannerType: String? = null
    private var mPath: String? = null
    private var mScreenVisible = 0
    private var mLotItem: String? = null
    private var mSpaceId: String? = null
    private var mMeterNameItem: String? = null
    private var mPBCZone: String? = null
    private var mStreetItem: String? = null
    private var mBlock: String? = null
    private var mDirectionItem: String? = null
    private var mTtypeOfHit: String? = null
    private var mState: String? = null
    private var mFromScreen: String? = "lpr_details"

    /** Called when the activity is first created.  */
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mPath = sharedPreference.read(SharedPrefKey.FILE_PATH, "")

        val directoryScan = File(
            Environment.getExternalStorageDirectory().absolutePath,
            Constants.FILE_NAME + Constants.SCANNER
        )

        FileUtil.deleteRecursive(directoryScan)

        scannerType = intent.getStringExtra(INTENT_KEY_SCANNER_TYPE)

        if (intent != null && intent.getStringExtra("from_scr") != null && intent.getStringExtra("from_scr") == "PAYBYSPACE") {
            mFromScreen = intent.getStringExtra("from_scr")
            mLotItem = intent.getStringExtra("Lot")
            mLotItem = intent.getStringExtra("Location")
            mSpaceId = intent.getStringExtra("Space_id")
            mMeterNameItem = intent.getStringExtra("Meter")
            mPBCZone = intent.getStringExtra("Zone")
            mStreetItem = intent.getStringExtra("Street")
            mBlock = intent.getStringExtra("Block")
            mDirectionItem = intent.getStringExtra("Direction")
        }

        if (intent != null && intent.getStringExtra("from_scr") != null && intent.getStringExtra("from_scr") == Constants.DIRECTED_ENFORCEMENT) {
            mFromScreen = intent.getStringExtra("from_scr")
            mLotItem = intent.getStringExtra("Lot")
            mLotItem = intent.getStringExtra("Location")
            mSpaceId = intent.getStringExtra("Space_id")
            mMeterNameItem = intent.getStringExtra("Meter")
            mPBCZone = intent.getStringExtra("Zone")
            mStreetItem = intent.getStringExtra("Street")
            mBlock = intent.getStringExtra("Block")
            mDirectionItem = intent.getStringExtra("Direction")
            mTtypeOfHit = intent.getStringExtra("type_of_hit")
            mState = intent.getStringExtra("state")
        }

        registerActivityResultLauncher()

        val iSLoginLogged = sharedPreference.read(SharedPrefKey.IS_LPR_LICENCE, false)
        if (getDeviceTypePhoneOrTablet(this@NewLprScanActivity).equals(Constants.TABLET, true)) {
            launchPTplus(admin = true, portraitOrientation = false)
        } else {
            if (iSLoginLogged) {
                // If we verify License
                if (BuildConfig.FLAVOR.equals(
                        Constants.FLAVOR_TYPE_PILOTPITTSBURGPA, ignoreCase = true
                    )
                ) {
                    launchPTplus(admin = true, portraitOrientation = true)
                } else {
                    launchPTplus(admin = true, portraitOrientation = true)
                }
            } else {
                // If we not verify license
                if (BuildConfig.FLAVOR.equals(
                        Constants.FLAVOR_TYPE_PILOTPITTSBURGPA, ignoreCase = true
                    )
                ) {
                    launchPTplus(admin = false, portraitOrientation = true)
                } else {
                    launchPTplus(admin = false, portraitOrientation = true)
                }
            }
        }

        registerBackPress()
    }

    private fun registerBackPress(){
        // legacy dispatcher
        legacyBackCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() = handleBackButtonTrigger()
        }
        onBackPressedDispatcher.addCallback(this, legacyBackCallback!!)

        // predictive back (API 33+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            predictiveCallback = OnBackInvokedCallback { handleBackButtonTrigger() }
            onBackInvokedDispatcher.registerOnBackInvokedCallback(
                OnBackInvokedDispatcher.PRIORITY_DEFAULT, predictiveCallback!!
            )
        }
    }

    private fun registerActivityResultLauncher() {
        imenseScanActivityLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                logD(
                    "LprScanActivityImense",
                    "onActivityResult:  requestCode=${result.resultCode}, data=${
                        result.data?.extras?.getInt(INTENT_KEY_MESSAGE)
                    }"
                )
                    val intent = result.data

                    var returnMessage = 0
                    if (intent != null) returnMessage = intent.extras!!.getInt(INTENT_KEY_MESSAGE)

                when (returnMessage) {
                    0 -> {
                        //User cancelled or back button pressed
                        returnBackToOrigin()
                    }
                    PT_SCAN_SUCCESS -> {
                    }
                    PT_ANPR_NOTONWHITELIST -> {
                        logE("==>LPRSCAN", "PT_ANPR_NOTONWHITELIST")

                        val sRegNumber = intent!!.extras!!.getString("anpr_not_in_whitelist")
                        val regConf = intent.extras!!.getInt("anpr_not_in_whitelist_conf")
                        returnBackToOrigin(sRegNumber)
                    }
                    PT_ANPR_PERMITEXPIRED -> {
                        val sRegNumber = intent!!.extras!!.getString("anpr_permit_expired")
                        val regConf = intent.extras!!.getInt("anpr_permit_expired_conf")
                        val sTimeExceeded = intent.extras!!.getString("time_since_permit_expired")
                    }
                    PT_ANPR_SCANTIMEOUT -> {
                        logE("==>LPRSCAN", "timeout")

                        val intent = Intent()
                        setResult(RESULT_OK, intent)
                        finish()
                    }
                    PT_LICENSE_MISSING_OR_INVALID -> {
                        val deviceID = intent!!.extras!!.getString("duid") //unique device ID
                        lifecycleScope.launch {
                            imenseLicenseServer.fetchAndSaveLicense(
                                deviceUid = deviceID, callback = object : ImmenseLicenceInterface {
                                    override fun onLicenseFetchedAndSaved() {
                                        toast(message = "New License Fetched & Activated")
                                        launchPTplus(admin = false, portraitOrientation = true)
                                    }

                                    override fun onLicenseFetchFailed() {
                                        toast(message = "New License Fetch Failed")
                                    }
                                })
                        }
                    }
                }

            }


        doubangoScanActivityLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                logD(
                    "LprScanActivityDoubango",
                    "onActivityResult:  requestCode=${result.resultCode}, data=${result.data}"
                )

                if (result.resultCode == RESULT_OK) {
                    val intent = result.data

                    val dataForLpr = getVehicleDetailDataModelFromIntent(intent!!)

                    if (dataForLpr != null) {
                        val clazz = dataForLpr::class.java
                        val licensePlateNumber = clazz.getDeclaredField("licensePlateNumber")
                        licensePlateNumber.isAccessible = true  //This bypasses 'private' access

                        val lprNumber = licensePlateNumber.get(dataForLpr)

                        returnBackToOrigin(lprNumber?.toString())

                        logD("Reflection", "LprNumber: $lprNumber,")
                    }
                } else{
                    returnBackToOrigin()
                }
            }
    }


    /**
     * Function used to extract intent parcelable using reflection, so that it will not give error in other sites
     */
    private fun getVehicleDetailDataModelFromIntent(intent: Intent): Any? {
        return try {
            val clazz = Class.forName(MODEL_VEHICLE_DETAIL_DATA)

            //Use ClassLoader to load Parcelable
            intent.setExtrasClassLoader(clazz.classLoader)

            //Safe cast using reflection (no direct reference to FlavorAData)
            intent.getParcelableExtra(KEY_VEHICLE_DETAIL_DATA)  // This returns an object of type Any
        } catch (e: ClassNotFoundException) {
            logD("Reflection", "FlavorAData class not found" + e.localizedMessage)
            null
        } catch (e: Exception) {
            logD("Reflection", "Error loading Parcelable:${e.localizedMessage}")
            null
        }
    }

    private fun handleBackButtonTrigger() {
        returnBackToOrigin()
    }


    override fun onStart() {
        super.onStart()
        mScreenVisible++
        if (mScreenVisible == 2) {
            //If needed, we will uncomment this to relaunch scanner when returning from LPR details screen
            //moveToLprDetailsScreen("")
        }
    }

    fun launchPTplus(admin: Boolean, portraitOrientation: Boolean) {
        @Suppress("KotlinConstantConditions") if (scannerType == SCANNER_TYPE_DOUBANGO) {
            try {
                //Load the class by name — no import!
                val clazz = Class.forName(CLASS_ALPR_VIDEO_PARALLEL_ACTIVITY)

                val i = Intent(this@NewLprScanActivity, clazz)
                i.putExtra(INTENT_KEY_FROM, FROM_POINT_AND_SCAN)
                i.putExtra(KEY_IS_VIDEO_STAB, true)
                i.putExtra(KEY_IS_LENS_STAB, true)

                if (BuildConfig.FLAVOR == Constants.FLAVOR_TYPE_CITYOFSANDIEGO||
                    BuildConfig.FLAVOR == Constants.FLAVOR_TYPE_STORMWATER_DIVISION) {
                    i.putExtra(KEY_IS_LANDSCAPE_ORIENTATION_REQUIRED, true)
                } else {
                    i.putExtra(KEY_IS_LANDSCAPE_ORIENTATION_REQUIRED, false)
                }

                i.putExtra(KEY_PATH_TO_SAVE_IMAGE, mPath + Constants.SCANNER)

                doubangoScanActivityLauncher.launch(i)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            //obtain an Intent to launch ANPR/ALPR Platform Plus
            try {
                ptPlusIntent = Intent()
                ptPlusIntent!!.component = ComponentName(
                    "com.imense.anprPlatformPlusIntentUS",
                    "com.imense.anprPlatformPlusIntentUS.ImenseParkingEnforcer"
                )

                //authenticate the request with the correct invocation code
                if (true) ptPlusIntent!!.putExtra("invocationcode", INVOCATION_ADMIN)
                else ptPlusIntent!!.putExtra("invocationcode", INVOCATION_USER)


                //set PT into portrait mode (not recommended since it reduces effective plate pixel resolution)

                if (portraitOrientation) ptPlusIntent!!.putExtra("orientation", "portrait")
                if (testManualExposureControls) {
                    //test manual camera controls
                    ptPlusIntent!!.putExtra(
                        "preferences_show_anpr_fps", "true"
                    ) //Display ANPR reads per second. Value can be "true" or "false" (default="true")
                    ptPlusIntent!!.putExtra(
                        "preferences_showexposurecontrols", "true"
                    ) //Display shutter and ISO controls. Value can be "true" or "false" (default="true")
                    ptPlusIntent!!.putExtra(
                        "preferences_showfocuscontrol", "false"
                    ) //Display focus distance control. Value can be "true" or "false" (default="false"). Note: some devices do not support setting shutter, ISO and focus independently, i.e. either all of them have to be set to "auto" or all of them must have manual values specified.
                    ptPlusIntent!!.putExtra(
                        "preferences_chosen_iso", "500"
                    ) //Attempt to set the ISO (sensor sensitivity) for the device camera. The range of values actually supported depends on the device and its software stack. Possible values are {"AutoISO", "6400", "3200", "2500", "2000", "1600", "1250", "1000", "800", "640", "500", "400", "320", "250", "200", "160", "125", "80", "64", "50", "32", "25"}; The default/fallback is "AutoISO".
                    ptPlusIntent!!.putExtra(
                        "preferences_chosen_shutter", "1/2000"
                    ) //Attempt to set the shutter speed (expressed in terms of exposure time in seconds) for the device camera. The range of values actually supported depends on the device and its software stack. Possible values are {"AutoExp", "1/50k", "1/30k", "1/25k", "1/20k", "1/15k", "1/10k", "1/8000", "1/6000", "1/5000", "1/4000", "1/3000", "1/2000", "1/1000", "1/500", "1/250", "1/160", "1/125", "1/100", "1/60", "1/50", "1/30", "1/25", "1/15", "1/10", "1/8"}; The default/fallback is "AutoExp".
                    ptPlusIntent!!.putExtra(
                        "preferences_videoResolutionWidth", "1920"
                    ) //Preferred device video resolution (horizontal pixels). Value must be positive numeric.
                    ptPlusIntent!!.putExtra(
                        "preferences_videoResolutionHeight", "1080"
                    ) //Preferred device video resolution (vertical pixels). Value must be positive numeric.
                    ptPlusIntent!!.putExtra(
                        "preferences_viewfinder", "true"
                    ) //Enable second adjustable zone-of-interest. Value can be "true" or "false" (default="false")
                    ptPlusIntent!!.putExtra(
                        "preferences_viewfinder2", "false"
                    ) //Enable second adjustable zone-of-interest. Value can be "true" or "false" (default="false")
                    ptPlusIntent!!.putExtra(
                        "preferences_accuracyVsSpeed", "4"
                    ) //Accuracy vs Speed (0: most accurate; 4: fastest).
                    testManualExposureControls = false //reset
                } else if (minimalUIandResetSettingsToDefaults) {
                    ptPlusIntent!!.putExtra(
                        "hideUI", "1"
                    ) //If value is "1" then hide all UI elements (apart from viewfinder), regardless of other settings
                    ptPlusIntent!!.putExtra(
                        "resetSettingsToDefaults", "true"
                    ) //Reset ALL settings (except license key) to default values
                    minimalUIandResetSettingsToDefaults = false //reset
                }


                //optionally instruct PTplus to start scan (i.e. invoke shutter button) immediately; 0: off; 1: start scan using in-built device camera
                ptPlusIntent!!.putExtra("startscan", "1")
                ptPlusIntent!!.putExtra("hideUI", "0")

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
                    "preferences_viewfinder", "false"
                ) //Enable second adjustable zone-of-interest. Value can be "true" or "false" (default="false")
                ptPlusIntent!!.putExtra(
                    "preferences_viewfinder2", "false"
                ) //Enable second adjustable zone-of-interest. Value can be "true" or "false" (default="false")
                ptPlusIntent!!.putExtra(
                    "preferences_zoiGranularity", "false"
                ) //Enable ZOI adjustment grid. Value can be "true" or "false" (default="false")
                ptPlusIntent!!.putExtra(
                    "preferences_showmagnifier", "false"
                ) //Display magnifier button. Value can be "true" or "false" (default="true")
                ptPlusIntent!!.putExtra(
                    "preferences_zoiFixed", "false"
                ) //Fix position and size of ZOI. Value can be "true" or "false" (default="false")
                ptPlusIntent!!.putExtra(
                    "preferences_alertsListRatherThanWhitelist", "false"
                ) // VINOD true mean continuous mode and false means single scan and return to result
                ptPlusIntent!!.putExtra(
                    "preferences_show_anpr_fps", "false"
                ) //Display ANPR reads per second. Value can be "true" or "false" (default="true")
                ptPlusIntent!!.putExtra(
                    "preferences_showexposurecontrols", "false"
                ) //Display shutter and ISO controls. Value can be "true" or "false" (default="true")
                ptPlusIntent!!.putExtra(
                    "preferences_saveimages_path", mPath + Constants.SCANNER
                ) //Folder for data and images; has to exist and be writable

                //ptPlusIntent.putExtra("preferences_showsingleshot", "true"); //Display button to save single image to SD card. Value can be "true" or "false" (default="false")
                //ptPlusIntent.putExtra("preferences_saveSingleshotInColour", "true"); //Store single PIC images in colour. Value can be "true" or "false" (default="false")
//            ptPlusIntent!!.putExtra("preferences_savecutouts", "true"); //Save plate cut-out image after every good read. Value can be "true" or "false" (default="true")
//            ptPlusIntent!!.putExtra("preferences_savecontextimages", "false"); //Save context image to SD card after every good read. Value can be "true" or "false" (default="false")
                //ptPlusIntent.putExtra("preferences_savecontextimagescolour", "true"); //Save context images in colour. Value can be "true" or "false" (default="false")

                //ptPlusIntent.putExtra("returnOnScanTimeout", "1");
                //ptPlusIntent.putExtra("preferences_scanTimeout", "15");
                //if we already have a license key, we send it to Platform Plus
                // Toast.makeText(LprScanActivity.this,  licenseKey, Toast.LENGTH_LONG).show();
                if (licenseKey != null) ptPlusIntent!!.putExtra("licensekey", licenseKey)

                imenseScanActivityLauncher.launch(ptPlusIntent)
            } catch (err: Exception) {/**/
                logE("LprScanActivity", "launchPTplus Error: $err")
                err.printStackTrace()
            }
        }
    }

    private fun returnBackToOrigin(lprNumber: String? = null) {
        val bundle = Bundle()
        bundle.putString("lpr_number", lprNumber)
        bundle.putString("Lot", mLotItem)
        bundle.putString("Location", mLotItem)
        bundle.putString("Space_id", mSpaceId)
        bundle.putString("Meter", mMeterNameItem)
        bundle.putString("Zone", mPBCZone)
        bundle.putString("Street", mStreetItem)
        bundle.putString("Block", mBlock)
        bundle.putString("Direction", mDirectionItem)
        bundle.putString("from_scr", mFromScreen)
        bundle.putString("type_of_hit", mTtypeOfHit)
        bundle.putString("state", mState)

        val mIntent = Intent()
        mIntent.putExtra(INTENT_KEY_LPR_BUNDLE, bundle)
        setResult(RESULT_OK, mIntent)
        finish()
    }

    override fun onResume() {
        super.onResume()
        FileUtil.createFolderForLprImages()
    }
}