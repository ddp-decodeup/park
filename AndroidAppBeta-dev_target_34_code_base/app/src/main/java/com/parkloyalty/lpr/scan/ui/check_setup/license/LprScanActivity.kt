package com.parkloyalty.lpr.scan.ui.check_setup.license

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.activity.viewModels
import androidx.appcompat.widget.AppCompatImageView
import androidx.lifecycle.Observer
import com.parkloyalty.lpr.scan.BuildConfig
import com.parkloyalty.lpr.scan.R
import com.parkloyalty.lpr.scan.basecontrol.BaseActivity
import com.parkloyalty.lpr.scan.basecontrol.BaseApplication
import com.parkloyalty.lpr.scan.database.AppDatabase
import com.parkloyalty.lpr.scan.database.Singleton
import com.parkloyalty.lpr.scan.interfaces.Constants
import com.parkloyalty.lpr.scan.interfaces.CustomDialogHelper
import com.parkloyalty.lpr.scan.interfaces.SharedPrefKey
import com.parkloyalty.lpr.scan.interfaces.TakeLicenceInterface
import com.parkloyalty.lpr.scan.network.ApiResponse
import com.parkloyalty.lpr.scan.network.DynamicAPIPath
import com.parkloyalty.lpr.scan.network.NetworkCheck.isInternetAvailable
import com.parkloyalty.lpr.scan.network.Status
import com.parkloyalty.lpr.scan.ui.check_setup.activity.LprDetailsActivity
import com.parkloyalty.lpr.scan.ui.check_setup.model.lpr_model.LprScanLoggerRequest
import com.parkloyalty.lpr.scan.ui.check_setup.model.lpr_model.LprScanLoggerResponse
import com.parkloyalty.lpr.scan.ui.check_setup.model.lpr_model.LprScanLoggerViewModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DropdownDatasetResponse
import com.parkloyalty.lpr.scan.util.AppUtils
import com.parkloyalty.lpr.scan.util.AppUtils.getDeviceTypePhoneOrTablet

import com.parkloyalty.lpr.scan.util.AppUtils.showCustomAlertDialog
import com.parkloyalty.lpr.scan.util.AppUtils.splitDateLpr
import com.parkloyalty.lpr.scan.util.DATASET_SETTINGS_LIST
import com.parkloyalty.lpr.scan.util.DoubangoConstants
import com.parkloyalty.lpr.scan.util.DoubangoConstants.CLASS_ALPR_VIDEO_PARALLEL_ACTIVITY
import com.parkloyalty.lpr.scan.util.DoubangoConstants.DOUBANGO_LPR_SCAN_RESULT
import com.parkloyalty.lpr.scan.util.DoubangoConstants.FROM_POINT_AND_SCAN
import com.parkloyalty.lpr.scan.util.DoubangoConstants.KEY_IS_LANDSCAPE_ORIENTATION_REQUIRED
import com.parkloyalty.lpr.scan.util.DoubangoConstants.KEY_IS_LENS_STAB
import com.parkloyalty.lpr.scan.util.DoubangoConstants.KEY_IS_VIDEO_STAB
import com.parkloyalty.lpr.scan.util.DoubangoConstants.KEY_PATH_TO_SAVE_IMAGE
import com.parkloyalty.lpr.scan.util.DoubangoConstants.KEY_VEHICLE_DETAIL_DATA
import com.parkloyalty.lpr.scan.util.DoubangoConstants.MODEL_VEHICLE_DETAIL_DATA
import com.parkloyalty.lpr.scan.util.INTENT_KEY_FROM
import com.parkloyalty.lpr.scan.util.INTENT_KEY_MESSAGE
import com.parkloyalty.lpr.scan.util.LogUtil
import com.parkloyalty.lpr.scan.util.LogUtil.printLog
import com.parkloyalty.lpr.scan.util.LogUtil.printToastMSG
import com.parkloyalty.lpr.scan.utils.ObjectMapperProvider
import java.io.File
import java.io.IOException
import java.text.ParseException
import java.util.*
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

//Copyright Imense Ltd 2021. Unauthorised usage or distribution strictly prohibited.
@AndroidEntryPoint
class LprScanActivity : BaseActivity(), TakeLicenceInterface, CustomDialogHelper {
    var launchButton1: Button? = null
    var launchButton2: Button? = null
    var launchButton2b: Button? = null
    var launchButton2c: Button? = null
    var launchButton1_portrait: Button? = null
    var launchButton2_portrait: Button? = null
    var mBtnCross: AppCompatImageView? = null
    var licenseKey: String? = null
    var ptPlusIntent: Intent? = null
    var testManualExposureControls = false
    var minimalUIandResetSettingsToDefaults = false
    private var mFilepath: String? = null

    private val mLoggerViewModel: LprScanLoggerViewModel? by viewModels()
    private var mPath: String? = null
    private var mScreenVisible = 0
    private var mDb: AppDatabase? = null
    private var mContext: Context? = null
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
        setContentView(R.layout.activity_lpr_scan)
        setFullScreenUI()
        mEventStartTimeStamp = AppUtils.getDateTime()
        mContext = this@LprScanActivity
        mPath = sharedPreference.read(SharedPrefKey.FILE_PATH, "")
        //createFolder();
        mDb = BaseApplication.instance?.getAppDatabase()

        val directoryScan = File(
                Environment.getExternalStorageDirectory().absolutePath,
                Constants.FILE_NAME + Constants.SCANNER
        )
        deleteRecursive(directoryScan)

        val intent = intent
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

        addObservers()
            val iSLoginLogged = sharedPreference.read(SharedPrefKey.IS_LPR_LICENCE, false)
        if(getDeviceTypePhoneOrTablet(this@LprScanActivity).equals(Constants.TABLET,true)){
            launchPTplus(true, false)
        }else {
            if (iSLoginLogged) {
                // If we verify License
                if (BuildConfig.FLAVOR.equals(
                        Constants.FLAVOR_TYPE_PILOTPITTSBURGPA,
                        ignoreCase = true
                    )
                ) {
//                    launchPTplus(false, false)//landscape
                    launchPTplus(true, true)//portrait
                } else {
                    launchPTplus(true, true)
                }
            } else {
                // If we not verify license
                if (BuildConfig.FLAVOR.equals(
                        Constants.FLAVOR_TYPE_PILOTPITTSBURGPA,
                        ignoreCase = true
                    )
                ) {
//                    launchPTplus(false, false)//landscape
                    launchPTplus(false, true)//portait
                } else {
                    launchPTplus(false, true)
                }
            }
        }
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
            mFilepath = file.absolutePath
        }
    }

    private val loggerResponseObserver = Observer { apiResponse: ApiResponse ->
        consumeResponse(apiResponse, DynamicAPIPath.POST_LPR_SCAN_LOGGER)
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
            LogUtil.printLog("Reflection", "FlavorAData class not found", e)
            null
        } catch (e: Exception) {
            LogUtil.printLog("Reflection", "Error loading Parcelable", e)
            null
        }
    }

    private fun addObservers() {
        mLoggerViewModel!!.response.observe(this, loggerResponseObserver)
    }

    override fun removeObservers() {
        super.removeObservers()
        mLoggerViewModel!!.response.removeObserver(loggerResponseObserver)
    }

    /* Call Api For Lpr scan details */
    @Throws(IOException::class, ParseException::class)
    private fun callPushEventApi(LprNum: String?) {
        val mWelcomeForm = mDb?.dbDAO?.getWelcomeForm()
        val mLat = sharedPreference.read(SharedPrefKey.LAT, "0.0")!!.toDouble()
        val mLong = sharedPreference.read(SharedPrefKey.LONG, "0.0")!!.toDouble()
        val myLocation = Geocoder(applicationContext, Locale.getDefault())
        val myList = myLocation.getFromLocation(mLat, mLong, 1)
        try {
            val address = myList!![0]
            if (isInternetAvailable(this@LprScanActivity)) {
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
            e.printStackTrace()
        }
    }

    override fun onStart() {
        super.onStart()
        mScreenVisible++
        if (mScreenVisible == 2) {
            val mIntent = Intent(this@LprScanActivity, LprDetailsActivity::class.java)
            mIntent.putExtra("lpr_number", "")
            mIntent.putExtra("Lot", mLotItem)
            mIntent.putExtra("Location", mLotItem)
            mIntent.putExtra("Space_id", mSpaceId)
            mIntent.putExtra("Meter", mMeterNameItem)
            mIntent.putExtra("Zone", mPBCZone)
            mIntent.putExtra("Street", mStreetItem)
            mIntent.putExtra("Block", mBlock)
            mIntent.putExtra("Direction", mDirectionItem)
            mIntent.putExtra("from_scr", mFromScreen)
            mIntent.putExtra("type_of_hit", mTtypeOfHit)
            mIntent.putExtra("state", mState)
            startActivity(mIntent)
            finish()
//            finish()
//            launchScreen(this, LprDetailsActivity::class.java)
        }
    }

    /*Api response */
    private fun consumeResponse(apiResponse: ApiResponse, tag: String) {
        when (apiResponse.status) {
            Status.LOADING -> showProgressLoader(getString(R.string.scr_message_please_wait))
            Status.SUCCESS -> {
                dismissLoader()
                if (!apiResponse.data!!.isNull) {
                    printLog(tag, apiResponse.data.toString())
                    if (tag.equals(DynamicAPIPath.POST_LPR_SCAN_LOGGER, ignoreCase = true)) {
                        val responseModel = ObjectMapperProvider.fromJson(apiResponse.data.toString(), LprScanLoggerResponse::class.java)

                        if (responseModel != null && responseModel.success!!) {
                            //LogUtil.printToastMSG(LprScanActivity.this, responseModel.getMessage());
                        } else if (responseModel != null && !responseModel.success!!) {
                            val message: String?
                            if (responseModel.response != null && responseModel.response != "") {
                                message = responseModel.response
                                showCustomAlertDialog(mContext, "POST_LPR_SCAN_LOGGER",
                                    message, "Ok", "Cancel", this)
                            } else {
                                responseModel.response = "Not getting response from server..!!"
                                message = responseModel.response
                                showCustomAlertDialog(mContext, "POST_LPR_SCAN_LOGGER",
                                    message, "Ok", "Cancel", this)
                            }
                            //   AppUtils.showCustomAlertDialog(mContext,"POST_LPR_SCAN_LOGGER",
                            //   "Not getting response from server ..!!","Ok","Cancel", this);
                        } else {
                            showCustomAlertDialog(mContext, "POST_LPR_SCAN_LOGGER",
                                "Something wen't wrong..!!", "Ok", "Cancel",
                                this)
                            dismissLoader()

                            // LogUtil.printToastMSG(LprDetails2Activity.this, responseModel.getMessage());
                        }
                    }
                }
            }
            Status.ERROR -> {
                dismissLoader()
                printToastMSG(applicationContext,
                    getString(R.string.err_msg_connection_was_refused))
            }

            else -> {}
        }
    }

    fun launchPTplus(admin: Boolean, portraitOrientation: Boolean) {
        @Suppress("KotlinConstantConditions")
        if ((BuildConfig.FLAVOR == Constants.FLAVOR_TYPE_SEPTA || BuildConfig.FLAVOR == Constants.FLAVOR_TYPE_CITYOFSANDIEGO
                    || BuildConfig.FLAVOR == Constants.FLAVOR_TYPE_STORMWATER_DIVISION||
                    BuildConfig.FLAVOR == Constants.FLAVOR_TYPE_REEF_CASPER_WY_REIMAGINED_PARKING) && BuildConfig.ENABLE_DOUBANGO_FOR_LPR) {
            try {
                //Load the class by name — no import!
                val clazz =
                    Class.forName(CLASS_ALPR_VIDEO_PARALLEL_ACTIVITY)

                //Create instance of it
//                val instance = clazz.getDeclaredConstructor().newInstance()

                // Call its "functionXyz" method
//                val method = clazz.getDeclaredMethod("functionXyz")
//                method.invoke(instance)

                val i = Intent(this@LprScanActivity, clazz)
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

                startActivityForResult(i, DOUBANGO_LPR_SCAN_RESULT)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
        //obtain an Intent to launch ANPR/ALPR Platform Plus
        try {
            ptPlusIntent = Intent()
            ptPlusIntent!!.component = ComponentName("com.imense.anprPlatformPlusIntentUS",
                "com.imense.anprPlatformPlusIntentUS.ImenseParkingEnforcer")

            //authenticate the request with the correct invocation code
            if (true) ptPlusIntent!!.putExtra("invocationcode", INVOCATION_ADMIN)
            else ptPlusIntent!!.putExtra("invocationcode", INVOCATION_USER)


            //set PT into portrait mode (not recommended since it reduces effective plate pixel resolution)

            if (portraitOrientation) ptPlusIntent!!.putExtra("orientation", "portrait")
            if (testManualExposureControls) {
                //test manual camera controls
                ptPlusIntent!!.putExtra("preferences_show_anpr_fps",
                    "true") //Display ANPR reads per second. Value can be "true" or "false" (default="true")
                ptPlusIntent!!.putExtra("preferences_showexposurecontrols",
                    "true") //Display shutter and ISO controls. Value can be "true" or "false" (default="true")
                ptPlusIntent!!.putExtra("preferences_showfocuscontrol", "false"
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
                "preferences_viewfinder",
                "false"
            ) //Enable second adjustable zone-of-interest. Value can be "true" or "false" (default="false")
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
                "false"
            ) //Fix position and size of ZOI. Value can be "true" or "false" (default="false")
            ptPlusIntent!!.putExtra(
                "preferences_alertsListRatherThanWhitelist",
                "false"
            ) // VINOD true mean continuous mode and false means single scan and return to result
            ptPlusIntent!!.putExtra(
                "preferences_show_anpr_fps",
                "false"
            ) //Display ANPR reads per second. Value can be "true" or "false" (default="true")
            ptPlusIntent!!.putExtra(
                "preferences_showexposurecontrols",
                "false"
            ) //Display shutter and ISO controls. Value can be "true" or "false" (default="true")
            ptPlusIntent!!.putExtra("preferences_saveimages_path",
                mPath + Constants.SCANNER) //Folder for data and images; has to exist and be writable

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
            if (debug > 0) Log.d(tag, "startActivityForResult ptPlusIntent=$ptPlusIntent")
            startActivityForResult(ptPlusIntent!!, REQUESTCODE)
        } catch (err: Exception) {
            /**/
            if (debug > 0) {
                Log.e(tag, "launchPTplus Error: $err")
                err.printStackTrace()
            }
            //            Toast.makeText(LprScanActivity.this, "US ALPR PTplus Intent not found: please install it", Toast.LENGTH_LONG).show();
        }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        var returnMessage = 0
        if (data != null) returnMessage = data.extras!!.getInt(INTENT_KEY_MESSAGE)
        if (debug > 0) Log.d(
            tag,
            "onActivityResult:  requestCode=$requestCode, resultCode=$resultCode, data=$data, ptPlusIntent=$ptPlusIntent, returnMessage=$returnMessage"
        )
        if (returnMessage == DOUBANGO_LPR_SCAN_RESULT) {
            if (resultCode == Activity.RESULT_OK) {
                val dataForLpr = getVehicleDetailDataModelFromIntent(data!!)

                if (dataForLpr != null) {
                    val clazz = dataForLpr::class.java
                    val licensePlateNumber = clazz.getDeclaredField("licensePlateNumber")
                    licensePlateNumber.isAccessible = true  //This bypasses 'private' access

                    val lprNumber = licensePlateNumber.get(dataForLpr)

                    moveToLprDetailsScreen(lprNumber.toString());

                    LogUtil.printLog("Reflection", "LprNumber: $lprNumber,")
                }

//                    showILog("==>License:","Number Plate : " + getValuesFromVariable(vehicleDetailDataModel?.licensePlateNumber))
//                    showILog("==>License:","Make : " + getValuesFromVariable(vehicleDetailDataModel?.vehicleMakeBrand))
//                    showILog("==>License:","Model : " + getValuesFromVariable(vehicleDetailDataModel?.vehicleModel))
//                    showILog("==>License:","Color : " + getValuesFromVariable(vehicleDetailDataModel?.vehicleColor))
//                    showILog("==>License:","country : " + getValuesFromVariable(vehicleDetailDataModel?.licensePlateCountry))
//                    showILog("==>License:","State : " + getValuesFromVariable(vehicleDetailDataModel?.licensePlateState))
//                    showILog("==>License:","Bitmap : " + getValuesFromVariable(vehicleDetailDataModel?.vehicleImageURL))

            }
        } else {
            if (returnMessage == PT_SCAN_SUCCESS) {
            } else if (returnMessage == PT_ANPR_NOTONWHITELIST) {
            val sRegNumber = data!!.extras!!.getString("anpr_not_in_whitelist")
            val regConf = data.extras!!.getInt("anpr_not_in_whitelist_conf")
            moveToLprDetailsScreen(sRegNumber)
        //            val mIntent = Intent(this@LprScanActivity, LprDetailsActivity::class.java)
//            mIntent.putExtra("lpr_number", sRegNumber)
//            mIntent.putExtra("Lot", mLotItem)
//            mIntent.putExtra("Location", mLotItem)
//            mIntent.putExtra("Space_id", mSpaceId)
//            mIntent.putExtra("Meter", mMeterNameItem)
//            mIntent.putExtra("Zone", mPBCZone)
//            mIntent.putExtra("Street", mStreetItem)
//            mIntent.putExtra("Block", mBlock)
//            mIntent.putExtra("Direction", mDirectionItem)
//            mIntent.putExtra("from_scr", mFromScreen)
//            startActivity(mIntent)
//            finish()
//            //user event logging
//            try {
//                callPushEventApi(sRegNumber)
//            } catch (e: IOException) {
//                e.printStackTrace()
//            } catch (e: ParseException) {
//                e.printStackTrace()
//            }
            //Toast.makeText(this, "PTplusUS returned with vehicle plate that is not in the whitelist: "+sRegNumber+" (conf="+regConf+")", Toast.LENGTH_LONG).show();
        } else if (returnMessage == PT_ANPR_PERMITEXPIRED) {
            val sRegNumber = data!!.extras!!.getString("anpr_permit_expired")
            val regConf = data.extras!!.getInt("anpr_permit_expired_conf")
            val sTimeExceeded = data.extras!!.getString("time_since_permit_expired")
            //            Toast.makeText(this, "PTplusUS returned with whitelisted plate: "+sRegNumber+" (conf="+regConf+") having exceeded parking permit by "+sTimeExceeded, Toast.LENGTH_LONG).show();
        } else if (returnMessage == PT_ANPR_SCANTIMEOUT) {
            finish()
            launchScreen(this@LprScanActivity, LprDetailsActivity::class.java)
            //            Toast.makeText(this, "PTplusUS returned after scan timeout", Toast.LENGTH_LONG).show();
        } else if (returnMessage == PT_LICENSE_MISSING_OR_INVALID) {
            val deviceID = data!!.extras!!.getString("duid") //unique device ID
            val caller = this
            ImenseLicenseServer(sharedPref = sharedPreference, androidAppContext = caller, device_uid = deviceID).execute()
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
            }
        }
    }

    private fun moveToLprDetailsScreen(lprNumber: String?) {
        val mIntent = Intent(this@LprScanActivity, LprDetailsActivity::class.java)
        mIntent.putExtra("lpr_number", lprNumber)
        mIntent.putExtra("Lot", mLotItem)
        mIntent.putExtra("Location", mLotItem)
        mIntent.putExtra("Space_id", mSpaceId)
        mIntent.putExtra("Meter", mMeterNameItem)
        mIntent.putExtra("Zone", mPBCZone)
        mIntent.putExtra("Street", mStreetItem)
        mIntent.putExtra("Block", mBlock)
        mIntent.putExtra("Direction", mDirectionItem)
        mIntent.putExtra("from_scr", mFromScreen)
        startActivity(mIntent)
        finish()
        //user event logging
        try {
            callPushEventApi(lprNumber)
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: ParseException) {
            e.printStackTrace()
        }
    }

    private fun createFolderForLprImages() {
        val localFolder = File(
            Environment.getExternalStorageDirectory().absolutePath,
            "/ParkLoyalty" + Constants.SCANNER
        )
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
            //mFilepath = file.getAbsolutePath();
        }
    }


    override fun onResume() {
        super.onResume()
        createFolderForLprImages()
    }

    override fun onDestroy() {
        mContext = null
        super.onDestroy()
    }

    override fun onBackPressed() {
        super.onBackPressed()

    }

    override fun onGetLience() {
        //Toast.makeText(LprScanActivity.this,  "get licence interface", Toast.LENGTH_LONG).show();
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

    private fun deleteRecursive(directory: File) {
        try {
            if (directory.exists()) {
                if (directory.isDirectory)
                    for (child in directory.listFiles())
                        deleteRecursive(child)

                directory.delete()
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }
}