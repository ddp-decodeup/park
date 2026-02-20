package com.parkloyalty.lpr.scan.ui.check_setup.activity

import android.content.Context
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.media.ExifInterface
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.text.InputFilter
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.view.View.OnFocusChangeListener
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import androidx.activity.viewModels
import androidx.appcompat.widget.*
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import androidx.loader.content.AsyncTaskLoader
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.parkloyalty.lpr.scan.BuildConfig
import com.parkloyalty.lpr.scan.R
import com.parkloyalty.lpr.scan.basecontrol.BaseActivity
import com.parkloyalty.lpr.scan.basecontrol.BaseApplication
import com.parkloyalty.lpr.scan.database.AppDatabase
import com.parkloyalty.lpr.scan.database.Singleton
import com.parkloyalty.lpr.scan.databinding.ActivityAddTimeRecordBinding
import com.parkloyalty.lpr.scan.databinding.ActivityLprDetailsBinding
import com.parkloyalty.lpr.scan.extensions.hideView
import com.parkloyalty.lpr.scan.extensions.nullSafety
import com.parkloyalty.lpr.scan.extensions.showView
import com.parkloyalty.lpr.scan.interfaces.APIConstant
import com.parkloyalty.lpr.scan.interfaces.Constants
import com.parkloyalty.lpr.scan.interfaces.CustomDialogHelper
import com.parkloyalty.lpr.scan.interfaces.SharedPrefKey
import com.parkloyalty.lpr.scan.network.ApiResponse
import com.parkloyalty.lpr.scan.network.DynamicAPIPath
import com.parkloyalty.lpr.scan.network.NetworkCheck.isInternetAvailable
import com.parkloyalty.lpr.scan.network.Status
import com.parkloyalty.lpr.scan.ui.check_setup.adapter.TimingViewPagerBannerAdapter
import com.parkloyalty.lpr.scan.ui.check_setup.adapter.TireStemAdapter
import com.parkloyalty.lpr.scan.ui.check_setup.model.lpr_model.UploadImageViewModel
import com.parkloyalty.lpr.scan.ui.check_setup.model.lpr_model.UploadImagesResponse
import com.parkloyalty.lpr.scan.ui.check_setup.model.timing.AddTimingDatabaseModel
import com.parkloyalty.lpr.scan.ui.check_setup.model.timing.AddTimingRequest
import com.parkloyalty.lpr.scan.ui.check_setup.model.timing.AddTimingResponse
import com.parkloyalty.lpr.scan.ui.check_setup.model.timing.AddTimingViewModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.*
import com.parkloyalty.lpr.scan.ui.login.model.WelcomeForm
import com.parkloyalty.lpr.scan.ui.login.model.ZoneStat
import com.parkloyalty.lpr.scan.ui.ticket.model.TicketUploadStatusViewModel
import com.parkloyalty.lpr.scan.util.*
import com.parkloyalty.lpr.scan.util.AppUtils.hideSoftKeyboard
import com.parkloyalty.lpr.scan.util.AppUtils.setListOnly
import com.parkloyalty.lpr.scan.utils.ObjectMapperProvider
import kotlinx.coroutines.*
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.FileOutputStream
import java.lang.ref.WeakReference
import java.text.DecimalFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

/*Phase 2*/
@AndroidEntryPoint
class AddTimeRecordActivity : BaseActivity(), CustomDialogHelper {

    var mBtnSubmit: AppCompatButton? = null
    var layTimingLayout: LinearLayoutCompat? = null
    var layBottomKeyboard: LinearLayoutCompat? = null
    lateinit var mViewPagerBanner: ViewPager
    lateinit var pagerIndicator: LinearLayoutCompat
    lateinit var linearLayoutCompatTireStem: LinearLayoutCompat
    lateinit var textFrontTireStem: AppCompatTextView
    lateinit var textRearTireStem: AppCompatTextView
    lateinit var textRearValveStem: AppCompatTextView
    lateinit var appCompatImageViewFrontTireStem: AppCompatImageView
    lateinit var appCompatImageViewRearTireStem: AppCompatImageView
    lateinit var appCompatImageViewValveStem: AppCompatImageView
    lateinit var appCompatTextViewCircleStemValueFront: AppCompatTextView
    lateinit var appCompatTextViewCircleStemValueRear: AppCompatTextView

    private val mModelList: MutableList<DatasetResponse>? = ArrayList()
    private var mContext: Context? = null
    private var mSelectedMakeValue = ""
    private var mSelectedMake: String? = ""
    private var mSelectedModel: String? = ""
    private var mSelectedColor: String? = ""
    private var mSelectedVin: String? = ""
    private var mAddress: String? = ""
    private var mFrontTireStemText: String? = ""
    private var mRearTireStemText: String? = ""
    private var mFrontTireStemValue: String? = ""
    private var mRearTireStemValue: String? = ""
    private var mValveTireStemValue: String? = ""
    private var mAutoComTextViewMeter: AppCompatAutoCompleteTextView? = null
    private var mAutoComTextViewLocation: AppCompatAutoCompleteTextView? = null
    private var mAutoComTextViewBlock: AppCompatAutoCompleteTextView? = null
    private var mAutoComTextViewStreet: AppCompatAutoCompleteTextView? = null
    private var mAutoComTextViewDirection: AppCompatAutoCompleteTextView? = null
    private var mAutoComTextViewLicNo: AppCompatAutoCompleteTextView? = null
    private var mAutoComTextViewLicState: AppCompatAutoCompleteTextView? = null
    private var mAutoComTextViewVin: AppCompatAutoCompleteTextView? = null
    private var mAutoComTextViewTimeLimit: AppCompatAutoCompleteTextView? = null
    private var mAutoComTextViewTierLeft: AppCompatAutoCompleteTextView? = null
    private var mAutoComTextViewTierRight: AppCompatAutoCompleteTextView? = null
    private var mAutoComTextViewZone: AppCompatAutoCompleteTextView? = null
    private var mAutoComTextViewPBCZone: AppCompatAutoCompleteTextView? = null
    private var mAutoComTextViewRemarks: AppCompatAutoCompleteTextView? = null
    private var mAutoComTextViewRemarks2: AppCompatAutoCompleteTextView? = null
    private var mAutoComTextViewColor: AppCompatAutoCompleteTextView? = null
    private var mAutoComTextViewMake: AppCompatAutoCompleteTextView? = null
    private var mAutoComTextViewModel: AppCompatAutoCompleteTextView? = null
    private var latestLayoutTimings = 0
    private val name: Array<AppCompatAutoCompleteTextView>? = null
    private var mWelcomeFormData: WelcomeForm? = WelcomeForm()
    private var mDb: AppDatabase? = null
//    private var mDatasetList: DatasetDatabaseModel? = DatasetDatabaseModel()
    private var mLprNumber: String? = ""
    private var mRegulation: String? = ""
    private var defaultValueOfState = "California"
    private var scanValueOfState = ""
    private var mZone = "CST"
    private var mStartTime = ""
    private var mRegulationTime = ""
    private var mRegulationTimeValue = ""
    private var mCitationLayout: List<CitationLayoutData>? = ArrayList()
    private var timingDataIDForTable = 0
    private var mImageCount = 0
    private var tempUri: String? = null
    private var bannerList: MutableList<TimingImagesModel?>? = ArrayList()
    private var imageURLs: MutableList<String?>? = ArrayList()
    private var mBannerAdapter: TimingViewPagerBannerAdapter? = null
    private var mShowBannerCount = 0
    private var mDotsCount = 0
    private var mDots: Array<ImageView?>? = null
    private var mList: MutableList<TimingImagesModel> = ArrayList()
    private val mImages: MutableList<String> = ArrayList()
    private var showTireStemDropDown:PopupWindow? = null
    private  var width: Int = 0
    private var height: Int = 0
    private var isTireStemWithImageView = false
    private var mAddTimingRequest:AddTimingRequest?=null
    private var mImageJsonString:String?=null

    private val mAddTimingViewModel: AddTimingViewModel? by viewModels()
    private val mUploadImageViewModel: UploadImageViewModel? by viewModels()


    val formatter = DecimalFormat("00")

    private lateinit var binding: ActivityAddTimeRecordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTimeRecordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        findViewsByViewBinding()
        setupClickListeners()

        setFullScreenUI()
        init()
        setBannerImageAdapter()
        addObservers()

        setAccessibilityForComponents()
    }

    private fun findViewsByViewBinding() {
        mBtnSubmit = binding.layoutContentAddTimeRecord.btnSubmit
        layTimingLayout = binding.layoutContentAddTimeRecord.layTimingLayout
        layBottomKeyboard = binding.layoutContentAddTimeRecord.layBottomKeyboard
        mViewPagerBanner = binding.layoutContentAddTimeRecord.layoutContentBanner.pagerBanner
        pagerIndicator = binding.layoutContentAddTimeRecord.layoutContentBanner.viewPagerCountDots
        linearLayoutCompatTireStem = binding.layoutContentAddTimeRecord.llTireStem
        textFrontTireStem = binding.layoutContentAddTimeRecord.appcomptextFrontTire
        textRearTireStem = binding.layoutContentAddTimeRecord.appcomptextRearTire
        textRearValveStem = binding.layoutContentAddTimeRecord.appcomptextValve
        appCompatImageViewFrontTireStem = binding.layoutContentAddTimeRecord.appcomimgviewFront
        appCompatImageViewRearTireStem = binding.layoutContentAddTimeRecord.appcomimgviewRear
        appCompatImageViewValveStem = binding.layoutContentAddTimeRecord.appcomimgviewTireValve
        appCompatTextViewCircleStemValueFront =
            binding.layoutContentAddTimeRecord.textstemvaluefront
        appCompatTextViewCircleStemValueRear = binding.layoutContentAddTimeRecord.textstemvaluerear
    }

    private fun setupClickListeners() {
        mBtnSubmit?.setOnClickListener {
            removeFocus()
            if (isFormValid()) {
                removeFocus()
                if (isFormValid()) {
                    callAddTimingApi()
                }
            }
        }

        binding.layoutContentAddTimeRecord.ivCamera.setOnClickListener {
            if (PermissionUtils.requestCameraAndStoragePermission(this@AddTimeRecordActivity)) {
                launchCameraIntent()
            }
        }

        appCompatImageViewFrontTireStem.setOnClickListener {
            dismissPopup()
            showTireStemDropDown = ShowTireStemDropDown("FRONT")
            showTireStemDropDown?.isOutsideTouchable = true
            showTireStemDropDown?.isFocusable = true
            showTireStemDropDown?.setBackgroundDrawable(ColorDrawable(Color.WHITE))
            showTireStemDropDown!!.showAtLocation(
                mViewPagerBanner, Gravity.BOTTOM, 0,
                mViewPagerBanner.getBottom() - 60
            );
            showTireStemDropDown?.showAsDropDown(mViewPagerBanner)
        }

        appCompatImageViewRearTireStem.setOnClickListener {
            dismissPopup()
            showTireStemDropDown = ShowTireStemDropDown("REAR")
            showTireStemDropDown?.isOutsideTouchable = true
            showTireStemDropDown?.isFocusable = true
            showTireStemDropDown?.setBackgroundDrawable(ColorDrawable(Color.WHITE))
            showTireStemDropDown!!.showAtLocation(
                mViewPagerBanner, Gravity.BOTTOM, 0,
                mViewPagerBanner.getBottom() - 60
            );
            showTireStemDropDown?.showAsDropDown(mViewPagerBanner)

        }

        appCompatImageViewValveStem.setOnClickListener {
            //                dismissPopup()
//                showTireStemDropDown = ShowTireStemDropDown("VALVE")
//                showTireStemDropDown?.isOutsideTouchable = true
//                showTireStemDropDown?.isFocusable = true
//                showTireStemDropDown?.setBackgroundDrawable(ColorDrawable(Color.WHITE))
//                showTireStemDropDown!!.showAtLocation(mViewPagerBanner, Gravity.BOTTOM, 0,
//                        mViewPagerBanner.getBottom() - 60);
//                showTireStemDropDown?.showAsDropDown(mViewPagerBanner)
//                appCompatImageViewValveStem!!.setColorFilter(ContextCompat.getColor(this@AddTimeRecordActivity,
//                        R.color.deep_red));
        }
    }

    private fun setAccessibilityForComponents() {
        appCompatImageViewFrontTireStem.contentDescription = textFrontTireStem.text.toString()
        appCompatImageViewRearTireStem.contentDescription = textRearTireStem.text.toString()
        appCompatImageViewValveStem.contentDescription = textRearValveStem.text.toString()
    }

    private fun init() {
        mContext = this
        mDb = BaseApplication.instance?.getAppDatabase()
        setToolbar()
        //get lpr number
        val intent = intent
        if (intent != null) {
            mLprNumber = intent.getStringExtra("lpr_number")
            mRegulation = intent.getStringExtra("regulation")
            mSelectedMake = intent.getStringExtra("make")
            mSelectedModel = intent.getStringExtra("model")
            mSelectedColor = intent.getStringExtra("color")
            mSelectedVin = intent.getStringExtra("vinNumber")
            mAddress = intent.getStringExtra("address")
            if(intent.hasExtra("state")) {
                scanValueOfState = intent.getStringExtra("state").toString()
            }
            if(!mAddress.isNullOrEmpty())
            {
//                mAddress = mAddress!!.replace(" ","",ignoreCase = false)
                mAddress = mAddress!!.replace(".0","",ignoreCase = false)
            }
//            mAddress = "1101 market st"
        }
        //get Activity log data
        mWelcomeFormData = getMyDatabase()?.dbDAO?.getWelcomeForm()

        val settingList = Singleton.getDataSetList(DATASET_SETTINGS_LIST, getMyDatabase())

        //val model = mDb?.dbDAO?.getDataset()
        if (settingList != null && settingList.isNotEmpty()) {
            mZone = settingList[0].mValue.nullSafety()
        }
        try {
            mStartTime = AppUtils.splitDateLpr(mZone)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        val response = getMyDatabase()?.dbDAO?.getTimingLayout()
        if (response != null) {
            if (response.data!![0].response?.size.nullSafety() > 0) {
                mCitationLayout = response.data!![0].response
                setUserDetails()
            }
        }

        timingDataIDForTable = getMyDatabase()?.dbDAO?.getLastIDFromTimingData().nullSafety() + 1
    }

    private fun setBannerImageAdapter() {
        mBannerAdapter = TimingViewPagerBannerAdapter(
            this@AddTimeRecordActivity,
            object : TimingViewPagerBannerAdapter.ListItemSelectListener {
                override fun onItemClick(position: Int) {
                    bannerList?.removeAt(position)
                    //mDb?.dbDAO?.deleteTempTimingImagesWithId(bannerList!![position]!!.id)
                    setCameraImages()
                }
            })
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

    private val addTimingResponseObserver = Observer { apiResponse: ApiResponse ->
        consumeResponse(apiResponse,
            DynamicAPIPath.POST_ADD_TIMING)
    }

    private val uploadImageResponseObserver = Observer { apiResponse: ApiResponse ->
        consumeResponse(apiResponse,
            DynamicAPIPath.POST_IMAGE)
    }

    private fun addObservers() {
        mAddTimingViewModel?.response?.observe(this, addTimingResponseObserver)
        mUploadImageViewModel?.response?.observe(this, uploadImageResponseObserver)
        mUploadImageViewModel?.responseUploadAllImages?.observe(this, uploadImageResponseObserver)
    }

    override fun removeObservers() {
        super.removeObservers()
        mAddTimingViewModel?.response?.removeObserver(addTimingResponseObserver)
        mUploadImageViewModel?.response?.removeObserver(uploadImageResponseObserver)
        mUploadImageViewModel?.responseUploadAllImages?.removeObserver(uploadImageResponseObserver)
    }

    private fun launchCameraIntent() {
        // mImageCount = mDb?.dbDAO?.getTimingImagesCount().nullSafety()
        mImageCount = bannerList?.size.nullSafety()
        val maxImageCount = AppUtils.maxImageCount(SETTING_MAX_IMAGES_COUNT)
        if (mImageCount < maxImageCount) {
            val picUri: Uri? = getOutputPhotoFile()
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, picUri)
            startActivityForResult(intent, REQUEST_CAMERA)
        } else {
            LogUtil.printToastMSGForErrorWarning(
                applicationContext,
                getString(R.string.msg_max_image).replace("#", maxImageCount.toString())
            )
        }
    }

    private fun getOutputPhotoFile(): Uri? {
        val directory = File(
            Environment.getExternalStorageDirectory().absolutePath,
            Constants.FILE_NAME + Constants.CAMERA
        )
        tempUri = directory.path
        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                LogUtil.printLog("getOutputPhotoFile", "Failed to create storage directory.")
                return null
            }
        }
        val path: Uri
        if (Build.VERSION.SDK_INT > 23) {
            val oldPath = File(directory.path + File.separator + "IMG_temp.jpg")
            var fileUrl = oldPath.path
            if (fileUrl.substring(0, 7).matches(Regex("file://"))) {
                fileUrl = fileUrl.substring(7)
            }
            val file = File(fileUrl)
            path = FileProvider.getUriForFile(mContext!!, this.packageName + ".provider", file)
        } else {
            path = Uri.fromFile(File(directory.path + File.separator + "IMG_temp.jpg"))
        }
        return path
    }
    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CAMERA) {
            if (resultCode == RESULT_OK) {
                try {
                    ioScope.launch {
                        if (tempUri == null) {
                            getOutputPhotoFile()
                        }
                        mViewPagerBanner.post {
                            mViewPagerBanner.visibility = View.VISIBLE
                        }
                        val file = File("$tempUri/IMG_temp.jpg")

                        val options = BitmapFactory.Options()
                        options.inSampleSize = 4
                        options.inJustDecodeBounds = true
                        BitmapFactory.decodeFile(file.absolutePath, options)

                        // Calculate inSampleSize
                        options.inSampleSize = Util.calculateInSampleSize(options, 300, 300)

                        // Decode bitmap with inSampleSize set
                        options.inJustDecodeBounds = false
                        val weakScaledBitmap = WeakReference<Bitmap>(
                            BitmapFactory.decodeFile(
                                file.absolutePath,
                                options
                            )
                        )

                        //var scaledBitmap = BitmapFactory.decodeFile(file.absolutePath, options)

                        //check the rotation of the image and display it properly
                        val exif: ExifInterface
                        exif = ExifInterface(file.absolutePath)
                        val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0)
                        val matrix = Matrix()
                        if (orientation == 6) {
                            matrix.postRotate(90f)
                        } else if (orientation == 3) {
                            matrix.postRotate(180f)
                        } else if (orientation == 8) {
                            matrix.postRotate(270f)
                        }
                        mainScope.async {
                            if (setImageTimeStampBasedOnSettingResponse()) {
                                val mImgaeBitmap = (weakScaledBitmap.get()?.let {
                                    Bitmap.createBitmap(
                                        it,
                                        0,
                                        0,
                                        it.width.nullSafety(),
                                        it.height.nullSafety(),
                                        matrix,
                                        true
                                    )
                                })

                                val timeStampBitmap =
                                    mImgaeBitmap?.let { AppUtils.timestampItAndSave(it) };
                                saveImageMM(timeStampBitmap)
                            } else {
                                saveImageMM(weakScaledBitmap.get()?.let {
                                    Bitmap.createBitmap(
                                        it,
                                        0,
                                        0,
                                        it.width.nullSafety(),
                                        it.height.nullSafety(),
                                        matrix,
                                        true
                                    )
                                })
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun saveImageMM(finalBitmap: Bitmap?) {
        val myDir = File(
            Environment.getExternalStorageDirectory().absolutePath,
            Constants.FILE_NAME + Constants.CAMERA
        )
        myDir.mkdirs()
        val timeStamp = SimpleDateFormat("yyyMMdd_HHmmss", Locale.US).format(Date())
        val fname = "Image_" + timeStamp + "_capture.jpg"
        val file = File(myDir, fname)
        if (file.exists()) file.delete()
        try {
            //new ImageCompression(this,file.getAbsolutePath()).execute(finalBitmap);
            val out = FileOutputStream(file)
            //finalBitmap = Bitmap.createScaledBitmap(finalBitmap,(int)1080/2,(int)1920/2, true);
            finalBitmap?.compress(Bitmap.CompressFormat.JPEG, 30, out) //less than 300 kb
            out.flush()
            out.close()
            val oldFname = "IMG_temp.jpg"
            val oldFile = File(myDir, oldFname)
            if (oldFile.exists()) oldFile.delete()

            val id = AppUtils.getTimeBasedRandomId()

            val pathDb = file.path
            val mImage = TimingImagesModel()
            mImage.timingImage = pathDb
            mImage.status = 0
            mImage.id = id.toInt()
            mImage.timingRecordId = timingDataIDForTable
            mImage.deleteButtonStatus = SHOW_DELETE_BUTTON

            bannerList?.add(mImage)

            setCameraImages()

            finalBitmap?.recycle()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //set images to viewpager
    private fun setCameraImages() {
        mViewPagerBanner.post {
            //bannerList = mDb?.dbDAO?.getTimingImage()
            if (bannerList?.isNotEmpty().nullSafety()) {
                showImagesBanner(bannerList!!)
                mViewPagerBanner.showView()
                pagerIndicator.showView()
            } else {
                mViewPagerBanner.hideView()
                pagerIndicator.hideView()
            }
        }
    }

    private fun showImagesBanner(mImageList: List<TimingImagesModel?>?) {
        mList.clear()
        mList.addAll(mImageList as MutableList<TimingImagesModel>)

        if (mList !=null && mList.isNotEmpty() && mBannerAdapter != null && mList!!.size>0) {
            mBannerAdapter?.setTimingBannerList(mList)
            mViewPagerBanner.adapter = mBannerAdapter
            mViewPagerBanner.currentItem = 0
        }
        mViewPagerBanner.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                if (mList.size == 0) {
                    return
                }
                try {
                    for (i in mList.indices) {
                        mDots!![i]?.setImageDrawable(resources.getDrawable(R.drawable.ic_pager_unselected_dot))
                    }
                    mDots!![position]?.setImageDrawable(resources.getDrawable(R.drawable.ic_pager_selected_dot))
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
        if (mList.size > 0 && mBannerAdapter != null) {
            setUiPageViewController(mBannerAdapter?.count.nullSafety())

        }
    }

    //managing view pager ui
    private fun setUiPageViewController(count: Int) {
        try {
            mDotsCount = count
            mDots = arrayOfNulls(mDotsCount)
            pagerIndicator.removeAllViews()
            for (i in 0 until mDotsCount) {
                mDots!![i] = ImageView(this)
                mDots!![i]?.setImageDrawable(resources.getDrawable(R.drawable.ic_pager_unselected_dot))
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                mDots!![i]?.setPadding(8, 0, 8, 0)
                params.setMargins(4, 0, 4, 0)
                pagerIndicator.addView(mDots!![i], params)
            }
            if (mShowBannerCount == 0) {
                mShowBannerCount += 1
            }
            mDots!![mShowBannerCount - 1]?.setImageDrawable(resources.getDrawable(R.drawable.ic_pager_selected_dot))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //set value to vehicle model dropdown
    private fun setDropdownVehicleModel(value: String?) {
        try {
            if (mAutoComTextViewModel != null) {
                //CoroutineScope(Dispatchers.IO).async {
                ioScope.launch {
                    val mApplicationList = Singleton.getDataSetList("carModelList", getMyDatabase())

                    for (i in mApplicationList!!.indices) {
                        if (mApplicationList!![i].make == mSelectedMakeValue) {
                            val mDatasetResponse = DatasetResponse()
                            mDatasetResponse.model = mApplicationList!![i].model
                            mModelList?.add(mDatasetResponse)
                        }
                    }
                    var pos = -1
                    if (mModelList != null && mModelList.size > 0) {
                        val mDropdownList = arrayOfNulls<String>(mModelList.size)
                        for (i in mModelList.indices) {
                            mDropdownList[i] = mModelList[i].model.toString()
                            if (value != "") {
                                if (mDropdownList[i] == value) {
                                    pos = i

                                }
                            }
                        }
                        mAutoComTextViewModel?.post {
                        try {
                            if(pos>=0)
                            mAutoComTextViewModel?.setText(mDropdownList[pos])
                        } catch (e: Exception) {
                        }
                            //
                            Arrays.sort(mDropdownList)
                            val adapter = ArrayAdapter(this@AddTimeRecordActivity,
                                    R.layout.row_dropdown_lpr_details_item, mDropdownList)
                            try {
                                mAutoComTextViewModel?.threshold = 1
                                mAutoComTextViewModel?.setAdapter<ArrayAdapter<String?>>(adapter)
                                mSelectedModel = mModelList[pos].model
                                mAutoComTextViewModel?.onItemClickListener =
                                        OnItemClickListener { parent, view, position, id ->

                                            val index = getIndexOfModel(
                                                    mApplicationList,
                                                    parent.getItemAtPosition(position).toString()
                                            )
                                            mSelectedModel = mModelList[index].model
                                            //mAutoComTextViewVehModel.setEllipsize(TextUtils.TruncateAt.END);
                                            //mAutoComTextViewVehModel.setMinLines(2);
                                        }
                                if (mAutoComTextViewModel?.tag != null && mAutoComTextViewModel?.tag == "listonly") {
                                    AppUtils.setListOnly(this@AddTimeRecordActivity, mAutoComTextViewModel!!)
                                }
                            } catch (e: Exception) {
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getIndexOfModel(list: List<DatasetResponse>?, name: String): Int {
        var pos = 0
        for (myObj in list!!) {
            if (name.equals(myObj.model, ignoreCase = true)) return pos
            pos++
        }
        return 0
    }

    //set value to make vehicle dropdown
    private fun setDropdownMakeVehicle(value: String?) {
        //init array list
        try {
            ioScope.launch {
            //CoroutineScope(Dispatchers.IO).async {
//            val mApplicationList = mDatasetList?.dataset?.carMakeList
                val mApplicationList = Singleton.getDataSetList(DATASET_CAR_MAKE_LIST, getMyDatabase())
                val uniqueDataSet: MutableSet<String> = HashSet()
                if (uniqueDataSet == null || uniqueDataSet.size < 1) {

                    if (mApplicationList != null && mApplicationList.isNotEmpty()) {
                        for (i in mApplicationList.indices) {
                            uniqueDataSet.add(mApplicationList[i].make.toString() + "#" + mApplicationList[i].makeText.toString())
                        }
                    }
                }
                val Geeks = uniqueDataSet.toTypedArray()
                var pos = -1
                Arrays.sort(Geeks)

                if (uniqueDataSet != null && uniqueDataSet.size > 0) {
                    val mDropdownList = arrayOfNulls<String>(uniqueDataSet.size)
                    for (i in Geeks.indices) {
                        mDropdownList[i] = Geeks[i].split("#").toTypedArray()[1]
                        if (value != "") {
                            if (value != "") {
                                val splitValue = Geeks[i].split("#").toTypedArray()
                                if (splitValue[0] == (value) || splitValue[1] == (value)) {
                                    pos = i
                                    try {
                                        mSelectedMake =  splitValue[1]
                                        mSelectedMakeValue = splitValue[1]
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                    }
                            }
                        }
                    }
                    mAutoComTextViewMake?.post {
                        if(pos>=0) {
                            mAutoComTextViewMake?.setText(mSelectedMake)
                        }
                        Arrays.sort(mDropdownList)
                        val adapter = ArrayAdapter(this@AddTimeRecordActivity,
                                R.layout.row_dropdown_lpr_details_item, mDropdownList)
                        try {
                            mAutoComTextViewMake?.threshold = 1
                            mAutoComTextViewMake?.setAdapter<ArrayAdapter<String?>>(adapter)
//                    mSelectedMake = mDropdownList[pos]
//                    mSelectedMakeValue = mApplicationList[pos].make.nullSafety()
                            mAutoComTextViewMake?.onItemClickListener =
                                    OnItemClickListener { parent, view, position, id ->
                                        val index = getIndexOfMake(
                                                mApplicationList,
                                                parent.getItemAtPosition(position).toString()
                                        )
                                        mSelectedMake = mApplicationList!![index].make
                                        mSelectedMakeValue = mApplicationList!![index].makeText.nullSafety()
                                        if (mSelectedMakeValue != null) {
                                            setDropdownVehicleModel(mSelectedMake)
                                        } else {
                                            setDropdownVehicleModel("")
                                        }
//                                        setDropdownVehicleModel("")
                                        AppUtils.hideSoftKeyboard(this@AddTimeRecordActivity)
                                    }
                            if (mAutoComTextViewMake?.tag != null && mAutoComTextViewMake?.tag == "listonly") {
                                AppUtils.setListOnly(this@AddTimeRecordActivity, mAutoComTextViewMake!!)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getIndexOfMake(list: List<DatasetResponse>?, name: String): Int {
        var pos = 0
        for (myObj in list!!) {
            if (name.equals(myObj.makeText, ignoreCase = true)) return pos
            pos++
        }
        return 0
    }

    //set value to vehicle colour dropdown
    private fun setDropdownVehicleColour(value: String?) {
        //init array list
        //CoroutineScope(Dispatchers.IO).async {
        ioScope.launch {
//        val mApplicationList = mDatasetList?.dataset?.carColorList
            val mApplicationList = Singleton.getDataSetList(DATASET_CAR_COLOR_LIST, getMyDatabase())

            var pos = -1
            if (mApplicationList != null && mApplicationList!!.isNotEmpty()) {
                val mDropdownList = arrayOfNulls<String>(mApplicationList!!.size)
                for (i in mApplicationList!!.indices) {
                    mDropdownList[i] = mApplicationList!![i].description.toString()
                    if (value != "") {
                        if (mDropdownList[i] == value) {
                            pos = i
                        }
                    }
                }
                mAutoComTextViewColor?.post {
                    try {
                        if(pos>=0) {
                            mAutoComTextViewColor?.setText(mDropdownList[pos])
                            mSelectedColor = mDropdownList[pos]
                        }
                    } catch (e: Exception) {
                    }
                    Arrays.sort(mDropdownList)
                    val adapter = ArrayAdapter(this@AddTimeRecordActivity,
                            R.layout.row_dropdown_lpr_details_item,mDropdownList)
                    try {
                        mAutoComTextViewColor?.threshold = 1
                        mAutoComTextViewColor?.setAdapter<ArrayAdapter<String?>>(adapter)
                        mAutoComTextViewColor?.onItemClickListener =
                                OnItemClickListener { parent, view, position, id ->
                                    val index: Int =
                                            getIndexOcolor(mApplicationList!!, mAutoComTextViewColor?.text.toString())
                                    mSelectedColor = mDropdownList[index]
                                    AppUtils.hideSoftKeyboard(this@AddTimeRecordActivity)
                                }
                        if (mAutoComTextViewColor?.tag != null && mAutoComTextViewColor?.tag == "listonly") {
                            AppUtils.setListOnly(this@AddTimeRecordActivity, mAutoComTextViewColor!!)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    private fun getIndexOcolor(list: List<DatasetResponse>, name: String): Int {
        var pos = 0
        for (myObj in list) {
            if (name.equals(myObj.description, ignoreCase = true)) return pos
            pos++
        }
        return 0
    }


    inner class MyLoader(context: Context?) :
        AsyncTaskLoader<Any?>(context!!) {
        override fun loadInBackground(): Any? {
            setUserDetails()
            return null
        }
    }

    /**
     * TODO Create UI
     */
    private fun setUserDetails() {
        if (mCitationLayout!!.isNotEmpty()) {
            for (iCit in mCitationLayout!!.indices) {
                    showProgressLoader(getString(R.string.scr_message_please_wait))

                layTimingLayout?.post {
                    if (mCitationLayout!![iCit].component.equals(
                            "marking_vehicle",ignoreCase = true)
                        || mCitationLayout!![iCit].component.equals("Location", ignoreCase = true)
                        || mCitationLayout!![iCit].component.equals("Vehicle", ignoreCase = true)
                    ) {
                        layTimingLayout?.visibility = View.VISIBLE
                        mBtnSubmit?.visibility = View.VISIBLE
                        for (iOff in mCitationLayout!![iCit].fields?.indices!!) {
                            if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                    "make",ignoreCase = true)) {
                                mAutoComTextViewMake = ConstructLayoutBuilder.CheckTypeOfField(
                                    mCitationLayout!![iCit].fields!![iOff],
                                    layTimingLayout, mCitationLayout!![iCit].component!!, mContext
                                )
                            } else if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                    "model",ignoreCase = true)) {
                                mAutoComTextViewModel = ConstructLayoutBuilder.CheckTypeOfField(
                                    mCitationLayout!![iCit].fields!![iOff],
                                    layTimingLayout, mCitationLayout!![iCit].component!!, mContext
                                )
                            } else if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                    "color",ignoreCase = true)) {
                                mAutoComTextViewColor = ConstructLayoutBuilder.CheckTypeOfField(
                                    mCitationLayout!![iCit].fields!![iOff],
                                    layTimingLayout, mCitationLayout!![iCit].component!!, mContext
                                )
                            } else if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                    "meter",ignoreCase = true )) {
                                mAutoComTextViewMeter = ConstructLayoutBuilder.CheckTypeOfField(
                                    mCitationLayout!![iCit].fields!![iOff],
                                    layTimingLayout, mCitationLayout!![iCit].component!!, mContext
                                )
                            } else if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                    "location",ignoreCase = true)
                                || mCitationLayout!![iCit].fields!![iOff].name.equals(
                                    "lot",ignoreCase = true)) {
                                mAutoComTextViewLocation = ConstructLayoutBuilder.CheckTypeOfField(
                                    mCitationLayout!![iCit].fields!![iOff],
                                    layTimingLayout, mCitationLayout!![iCit].component!!, mContext
                                )
                            } else if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                    "block",ignoreCase = true)) {
                                mAutoComTextViewBlock = ConstructLayoutBuilder.CheckTypeOfField(
                                        mCitationLayout!![iCit].fields!![iOff],
                                        layTimingLayout, mCitationLayout!![iCit].component!!, mContext)

                                mAutoComTextViewBlock?.filters = arrayOf<InputFilter>(InputFilter.AllCaps())

                                if (!BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLENDALE, true)&&
                                    !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLENDALE_POLICE, true)&&
                                    !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAMETRO, true)&&
                                    !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CORPUSCHRISTI, true)) {
                                    var mRoundOfAddress = ""
                                    try {
                                        var mAdd: String = ""
                                        if (mAddress!!.contains("#")) {
                                            mAdd = if (mAddress!!.split(" ")
                                                            .toTypedArray().size > 1
                                            ) mAddress!!.split("#").toTypedArray()[0] else mAddress!!
                                        } else {
                                            mAdd = if (mAddress!!.split(" ")
                                                            .toTypedArray().size > 1
                                            ) mAddress!!.split(" ").toTypedArray()[0] else mAddress!!

                                        }
                                        mRoundOfAddress = AppUtils.roundOfBlock(mAdd.replace("#"," "))

                                        mAutoComTextViewBlock?.setText(mRoundOfAddress)
                                    } catch (e: java.lang.Exception) {
                                        e.printStackTrace()
                                        mAutoComTextViewBlock?.setText(
                                                if (mAddress!!.split("#").toTypedArray().size > 1
                                                ) mAddress!!.split("#").toTypedArray()[0] else ""
                                        )
                                    }
                                }
                                Util.setFieldCaps(this@AddTimeRecordActivity, mAutoComTextViewBlock!!)

                            } else if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                    "street",ignoreCase = true) ||
                                mCitationLayout!![iCit].fields!![iOff].name.equals(
                                    "street_textbox",ignoreCase = true)) {
                                mAutoComTextViewStreet = ConstructLayoutBuilder.CheckTypeOfField(
                                    mCitationLayout!![iCit].fields!![iOff],
                                    layTimingLayout, mCitationLayout!![iCit].component!!, mContext)
                                try {
                                    mAutoComTextViewStreet?.filters = arrayOf<InputFilter>(InputFilter.AllCaps())
                                    if (!BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLENDALE, true)&&
                                        !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLENDALE_POLICE, true)&&
                                        !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAMETRO, true)&&
                                        !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CORPUSCHRISTI, true)) {
                                        if (mAddress!!.contains("#")) {
                                            val count =
                                                    mAddress?.split("#")?.toTypedArray()?.size.nullSafety()
                                            if (count > 2) {
                                                val mStreet = if (count > 0) mAddress?.substring(mAddress!!.indexOf(' ') + 1)
                                                else mAddress
                                                mAutoComTextViewStreet?.setText(mStreet.toString())
                                            } else {
                                                mAutoComTextViewStreet?.setText(
                                                        if (count > 1) mAddress!!.split("#"
                                                        ).toTypedArray()[1] else mAddress)
                                            }
                                        } else {
                                            val count =
                                                    mAddress?.split(" ")?.toTypedArray()?.size.nullSafety()
                                            if (count > 2) {
                                                val mStreet = if (count > 0) mAddress?.substring(mAddress!!.indexOf(' ') + 1)
                                                else mAddress
                                                mAutoComTextViewStreet?.setText(mStreet.toString())
                                            } else {
                                                mAutoComTextViewStreet?.setText(
                                                        if (count > 1) mAddress!!.split(" "
                                                        ).toTypedArray()[1] else mAddress)
                                            }
                                        }
                                    }
                                    Util.setFieldCaps(this@AddTimeRecordActivity, mAutoComTextViewStreet!!)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            } else if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                    "side_of_street",ignoreCase = true) ||
                                mCitationLayout!![iCit].fields!![iOff].name.equals(
                                    "side", ignoreCase = true) ||
                                mCitationLayout!![iCit].fields!![iOff].name.equals(
                                    "direction", ignoreCase = true)) {
                                mAutoComTextViewDirection = ConstructLayoutBuilder.CheckTypeOfField(
                                    mCitationLayout!![iCit].fields!![iOff],
                                    layTimingLayout, mCitationLayout!![iCit].component!!, mContext)
                                Util.setFieldCaps(this@AddTimeRecordActivity, mAutoComTextViewDirection!!)
                            }  else if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                    "lic_no",ignoreCase = true)
                                || mCitationLayout!![iCit].fields!![iOff].name.equals(
                                    "lp_number",ignoreCase = true)) {
                                mAutoComTextViewLicNo = ConstructLayoutBuilder.CheckTypeOfField(
                                    mCitationLayout!![iCit].fields!![iOff],
                                    layTimingLayout, mCitationLayout!![iCit].component!!, mContext
                                )
                            } else if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                    "lic_state",ignoreCase = true)
                                || mCitationLayout!![iCit].fields!![iOff].name.equals(
                                    "state",ignoreCase = true)) {
                                mAutoComTextViewLicState = ConstructLayoutBuilder.CheckTypeOfField(
                                    mCitationLayout!![iCit].fields!![iOff],
                                    layTimingLayout, mCitationLayout!![iCit].component!!, mContext
                                )!!
                                try {
                                    var settingsList: List<DatasetResponse>? = ArrayList()
                                    settingsList = Singleton.getDataSetList(DATASET_SETTINGS_LIST, getMyDatabase())
                                    if (settingsList != null && settingsList.size > 0) {
                                        for (i in settingsList.indices) {
                                            if (settingsList[i].type.equals("DEFAULT_STATE",
                                                            ignoreCase = true)) {
                                                if (scanValueOfState.isEmpty()) {
                                                    defaultValueOfState =
                                                            settingsList[i].mValue.nullSafety()
                                                    //defaultValueOfState = mCitationLayout.get(finalICit).getFields().get(iOff).getmDefaultValue();
                                                } else {
                                                    defaultValueOfState = scanValueOfState
                                                }
                                            }
                                            if (settingsList!![i].type.equals("IS_TIRE_STEM_ICON",
                                                            ignoreCase = true )) {
                                                if(settingsList!![i].mValue.equals("YES", ignoreCase = true)) {
                                                    try {
                                                        isTireStemWithImageView = true
                                                    } catch (e: java.lang.Exception) {
                                                        e.printStackTrace()
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    //                                        mStateItem = mCitationLayout.get(finalICit).getFields().get(iOff).getmDefaultValue();
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            } else if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                    "vin",ignoreCase = true) || mCitationLayout!![iCit].fields!![iOff].name.equals(
                                    "vin_number",ignoreCase = true)) {
                                mAutoComTextViewVin = ConstructLayoutBuilder.CheckTypeOfField(
                                    mCitationLayout!![iCit].fields!![iOff],
                                    layTimingLayout, mCitationLayout!![iCit].component!!, mContext
                                )
                            } else if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                    "time_limit_select",ignoreCase = true)) {
                                mAutoComTextViewTimeLimit = ConstructLayoutBuilder.CheckTypeOfField(
                                    mCitationLayout!![iCit].fields!![iOff],
                                    layTimingLayout, mCitationLayout!![iCit].component!!, mContext
                                )
                                //setFocus(mAutoComTextViewShift);
                            } else if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                    "tier_stem_left",ignoreCase = true)) {
                                if(isTireStemWithImageView == false) {
                                    mAutoComTextViewTierLeft = ConstructLayoutBuilder.CheckTypeOfField(
                                            mCitationLayout!![iCit].fields!![iOff],
                                            layTimingLayout, mCitationLayout!![iCit].component!!, mContext)
                                }else {
                                linearLayoutCompatTireStem.visibility = View.VISIBLE
                                    if (mCitationLayout!![iCit].fields!![iOff]!!.isRequired.nullSafety()) {
                                        textFrontTireStem.setText(mCitationLayout!![iCit].fields!![iOff]!!.repr + "*")
                                    } else {
                                        textFrontTireStem.setText(mCitationLayout!![iCit].fields!![iOff]!!.repr)
                                    }
                                    mFrontTireStemText = textFrontTireStem!!.text!!.toString()
                                    if (isTireStemWithImageView == true) {
                                        val viewTreeObserver: ViewTreeObserver = appCompatImageViewValveStem.getViewTreeObserver()
                                        linearLayoutCompatTireStem.visibility = View.VISIBLE
                                        viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
                                            override fun onGlobalLayout() {
                                                appCompatImageViewValveStem.getViewTreeObserver().removeGlobalOnLayoutListener(this)
                                                width = appCompatImageViewValveStem.getMeasuredWidth()
                                                height = appCompatImageViewValveStem.getMeasuredHeight()
                                            }
                                        })
                                    }
                                }
                                //setFocus(mAutoComTextViewAgency);
                            } else if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                    "tier_stem_right",ignoreCase = true)) {
                                if(isTireStemWithImageView == false) {
                                mAutoComTextViewTierRight = ConstructLayoutBuilder.CheckTypeOfField(
                                    mCitationLayout!![iCit].fields!![iOff],
                                    layTimingLayout, mCitationLayout!![iCit].component!!, mContext)
                                }else {
                                    linearLayoutCompatTireStem!!.visibility = View.VISIBLE
                                    if (mCitationLayout!![iCit].fields!![iOff]!!.isRequired.nullSafety()) {
                                        textRearTireStem.setText(mCitationLayout!![iCit].fields!![iOff]!!.repr + "*")
                                    } else {
                                        textRearTireStem.setText(mCitationLayout!![iCit].fields!![iOff]!!.repr)
                                    }
                                    mRearTireStemText = textRearTireStem!!.text!!.toString()
                                }
                            } else if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                    "zone",ignoreCase = true)) {
                                mAutoComTextViewZone = ConstructLayoutBuilder.CheckTypeOfField(
                                    mCitationLayout!![iCit].fields!![iOff],
                                    layTimingLayout, mCitationLayout!![iCit].component!!, mContext
                                )
                                setFocus(mAutoComTextViewZone)
                                mAutoComTextViewZone?.setText(if (mWelcomeFormData != null) mWelcomeFormData!!.officerZone else "")
                                Util.setFieldCaps(
                                    this@AddTimeRecordActivity,
                                    mAutoComTextViewZone!!
                                )
                            }else if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                    "city_zone", ignoreCase = true)
                                || mCitationLayout!![iCit].fields!![iOff].name.equals(
                                    "pbc_zone",ignoreCase = true)) {
                                mAutoComTextViewPBCZone = ConstructLayoutBuilder.CheckTypeOfField(
                                    mCitationLayout!![iCit].fields!![iOff],
                                    layTimingLayout, mCitationLayout!![iCit].component!!, mContext
                                )
                                setFocus(mAutoComTextViewPBCZone)
                                Util.setFieldCaps(
                                    this@AddTimeRecordActivity,
                                    mAutoComTextViewPBCZone!!
                                )
                            }else if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                    "remark",ignoreCase = true )) {
                                //mCitationLayout.get(finalICit).getFields().get(iOff).setTag("dropdown");
                                mAutoComTextViewRemarks = ConstructLayoutBuilder.CheckTypeOfField(
                                    mCitationLayout!![iCit].fields!![iOff],
                                    layTimingLayout, mCitationLayout!![iCit].component!!, mContext
                                )
                                setFocus(mAutoComTextViewRemarks)
                                Util.setFieldCaps(
                                    this@AddTimeRecordActivity,
                                    mAutoComTextViewRemarks!!
                                )
                                mAutoComTextViewRemarks?.setText(sharedPreference.read(
                                        SharedPrefKey.LOCK_TIMING_REMARK,""))
                            } else if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                    "remark_2",ignoreCase = true )) {
                                //mCitationLayout.get(finalICit).getFields().get(iOff).setTag("dropdown");
                                mAutoComTextViewRemarks2 = ConstructLayoutBuilder.CheckTypeOfField(
                                    mCitationLayout!![iCit].fields!![iOff],
                                    layTimingLayout, mCitationLayout!![iCit].component!!, mContext
                                )
                                setFocus(mAutoComTextViewRemarks2)
                                Util.setFieldCaps(
                                    this@AddTimeRecordActivity,
                                    mAutoComTextViewRemarks2!!
                                )
                                mAutoComTextViewRemarks2?.setText(sharedPreference.read(
                                        SharedPrefKey.LOCK_TIMING_REMARK2,""))
                            } else {
                                try {
                                    latestLayoutTimings++
                                    name!![latestLayoutTimings] =
                                        ConstructLayoutBuilder.CheckTypeOfField(
                                            mCitationLayout!![iCit].fields!![iOff],
                                            layTimingLayout!!,
                                            mCitationLayout!![iCit].component!!,
                                            mContext!!
                                        )!!
                                } catch (e: Exception) {
                                }
                            }
                            if (iOff == mCitationLayout!![iCit].fields?.size!! - 1) {
                                    getDatasetFromDb()
                            }
                        }
                    }
                }
            }
        }
        if (mCitationLayout!!.size == 0) {
            //linearLayoutEmptyActivity.setVisibility(View.VISIBLE);
        }
    }

    private fun setFocus(mAutoCompleteTextView: AutoCompleteTextView?) {
        mAutoCompleteTextView?.onFocusChangeListener = OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                layBottomKeyboard?.visibility = View.VISIBLE
            } else {
                layBottomKeyboard?.visibility = View.GONE
            }
        }
    }

    private fun getDatasetFromDb() {
//        val handler: Handler = object : Handler(Looper.getMainLooper()) {
//            override fun handleMessage(msg: Message) {
//                if (msg.what == 0) {
//
//                    try {
//                        mAutoComTextViewLicNo?.setText(mLprNumber)
//                    } catch (e: Exception) {
//                    }
////                    mDatasetList = mDb?.dbDAO?.getDataset()
//                    setDropdownState()
//                    setDropdownSide()
//                    setDropdownStreet("")
//                    setDropdownMeterName()
//                    setDropdownLocationName()
//                    setDropdownZone()
//                    setDropdownRemark()
//                    setDropdownRegulation()
//                    setDropdownTierLeft()
//                    setDropdownTierRight()
//                    setDropdownMakeVehicle(mSelectedMake)
//                    setDropdownVehicleColour(mSelectedColor)
//                    setDropdownVehicleModel(mSelectedModel)
//                    setDropdownBlock()
//                    dismissLoader()
//                }
//            }
//        }
//
//        object : Thread() {
//            override fun run() {
////                mDatasetList = mDb?.dbDAO?.getDataset()
////
//                handler.sendEmptyMessage(0)
//            }
//        }.start()

        ioScope.launch {
            try {
                mAutoComTextViewLicNo!!.post {
                    mAutoComTextViewLicNo?.setText(mLprNumber)
                }
                if (mAutoComTextViewVin != null)
                mAutoComTextViewVin!!.post {
                     mAutoComTextViewVin?.setText(mSelectedVin)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
//                    mDatasetList = mDb?.dbDAO?.getDataset()
            setDropdownState()
            setDropdownSide()
            setDropdownStreet("")
            setDropdownMeterName()
            setDropdownLocationName()
            setDropdownZone()
            setDropdownPBCZone("")
            setDropdownRemark()
            setDropdownRemark2()
            setDropdownRegulation("")
            setDropdownTierLeft()
            setDropdownTierRight()
            setDropdownMakeVehicle(mSelectedMake)
            setDropdownVehicleColour(mSelectedColor)
            setDropdownVehicleModel(mSelectedModel)
            setDropdownBlock()
            mainScope.async {
                lockTimeData(true, null)
                dismissLoader()

            }
        }
    }


    //set value to Meter Name dropdown
    private fun setDropdownBlock() {
        try {
            if (mAutoComTextViewBlock != null) {
                //CoroutineScope(Dispatchers.IO).async {
                ioScope.launch {
                    //        val mApplicationList = mDatasetList!!.dataset!!.blockList
                    val mApplicationList = Singleton.getDataSetList(DATASET_BLOCK_LIST, getMyDatabase())
                    val pos = 0
                    if (mApplicationList != null && mApplicationList!!.size > 0) {
                        val mDropdownList = arrayOfNulls<String>(mApplicationList!!.size)
                        for (i in mApplicationList!!.indices) {
                            mDropdownList[i] = mApplicationList!![i].blockName.toString()
                        }
                        mAutoComTextViewBlock?.post {
                            Arrays.sort(mDropdownList)
                            val adapter = ArrayAdapter(
                                    this@AddTimeRecordActivity,
                                    R.layout.row_dropdown_menu_popup_item,
                                    mDropdownList
                            )
                            try {
                                mAutoComTextViewBlock!!.threshold = 1
                                mAutoComTextViewBlock!!.setAdapter<ArrayAdapter<String?>>(adapter)
                                //mSelectedShiftStat = mApplicationList.get(pos);
                                mAutoComTextViewBlock!!.onItemClickListener =
                                        OnItemClickListener { parent, view, position, id -> hideSoftKeyboard(this@AddTimeRecordActivity) }
                                // listonly
                                if (mAutoComTextViewBlock!!.tag != null && mAutoComTextViewBlock!!.tag == "listonly") {
                                    setListOnly(this@AddTimeRecordActivity, mAutoComTextViewBlock!!)
                                }
                            } catch (e: java.lang.Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    //----------set dropdown-------------------------------------------
    //set value to State dropdown
    private fun setDropdownState() {
        try {
            //CoroutineScope(Dispatchers.IO).async {
            ioScope.launch {
                if (mAutoComTextViewLicState != null) {
                    //        val mApplicationList = mDatasetList?.dataset?.stateList
                    val mApplicationList = Singleton.getDataSetList(DATASET_STATE_LIST, getMyDatabase())

                    var pos = -1
                    if (mApplicationList != null && mApplicationList!!.isNotEmpty()) {
                        val mDropdownList = arrayOfNulls<String>(mApplicationList!!.size)
                        for (i in mApplicationList!!.indices) {
                            mDropdownList[i] = mApplicationList!![i].state_name.toString()
                        }

//                            if (pos == 0) {
                                for (i in mApplicationList!!.indices) {
                                    mDropdownList[i] = mApplicationList!![i].state_name.toString()
                                    if (mApplicationList!![i].state_name.equals(
                                                    defaultValueOfState,ignoreCase = true)) {
                                        pos = i
                                    }
                                }
//                            }
                        mAutoComTextViewLicState?.post {
                            if(pos>=0)
                            {
//                                mAutoComTextViewLicState?.setText(mDropdownList[pos])
                                mAutoComTextViewLicState?.setText(mDropdownList[pos])
                            }
                            val adapter = ArrayAdapter(
                                    this@AddTimeRecordActivity,
                                    R.layout.row_dropdown_menu_popup_item,
                                    mDropdownList
                            )
                            try {
                                mAutoComTextViewLicState?.threshold = 1
                                mAutoComTextViewLicState?.setAdapter<ArrayAdapter<String?>>(adapter)
                                //mSelectedShiftStat = mApplicationList.get(pos);
                                mAutoComTextViewLicState?.onItemClickListener =
                                        OnItemClickListener { parent, view, position, id -> // mSelectedShiftStat = mApplicationList.get(position);
                                            AppUtils.hideSoftKeyboard(this@AddTimeRecordActivity)
                                           /* val index = getIndexOfState(
                                                    mApplicationList,
                                                    parent.getItemAtPosition(position).toString()
                                            )
                                            mAutoComTextViewLicState?.setText(mApplicationList[index].state_name.toString())*/
                                        }
                                if (mAutoComTextViewLicState?.tag != null && mAutoComTextViewLicState?.tag == "listonly") {
                                    AppUtils.setListOnly(this@AddTimeRecordActivity, mAutoComTextViewLicState!!)
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    } else {
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getIndexOfState(list: List<DatasetResponse>, name: String): Int {
        var pos = 0
        for (myObj in list) {
            if (name.equals(myObj.state_name, ignoreCase = true)) return pos
            pos++
        }
        return -1
    }

    //set value to side dropdown
    private fun setDropdownSide() {
        try {
            if (mAutoComTextViewDirection != null) {
                //CoroutineScope(Dispatchers.IO).async {
                ioScope.launch {
                    //        val mApplicationList = mDatasetList?.dataset?.sideList
                    val mApplicationList = Singleton.getDataSetList(DATASET_SIDE_LIST, getMyDatabase())

                    if (mApplicationList != null && mApplicationList!!.isNotEmpty()) {
                        val mDropdownList = arrayOfNulls<String>(mApplicationList!!.size)
                        for (i in mApplicationList!!.indices) {
                            mDropdownList[i] = mApplicationList!![i].sideName.toString()
                            /*if (mApplicationList.get(i).getSideName().equalsIgnoreCase(mSideItem)) {
                            pos = i;
                        }*/
                        }
                        mAutoComTextViewDirection?.post {
                            val adapter = ArrayAdapter(
                                    this@AddTimeRecordActivity,
                                    R.layout.row_dropdown_menu_popup_item,
                                    mDropdownList
                            )
                            try {
                                mAutoComTextViewDirection?.threshold = 1
                                mAutoComTextViewDirection?.setAdapter<ArrayAdapter<String?>>(adapter)
                                //mSelectedShiftStat = mApplicationList.get(pos);
                                mAutoComTextViewDirection?.onItemClickListener =
                                        OnItemClickListener { parent, view, position, id -> // mSelectedShiftStat = mApplicationList.get(position);
                                            AppUtils.hideSoftKeyboard(this@AddTimeRecordActivity)
                                        }
                                if (mAutoComTextViewDirection?.tag != null && mAutoComTextViewDirection?.tag == "listonly") {
                                    AppUtils.setListOnly(this@AddTimeRecordActivity, mAutoComTextViewDirection!!)
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //set value to side dropdown
    private fun setDropdownRegulation(value: String?) {
        try {
            if (mAutoComTextViewTimeLimit != null) {
                //CoroutineScope(Dispatchers.IO).async {
                ioScope.launch {
                    //        val mApplicationList = mDatasetList?.dataset?.regulationTimeList
                    val mApplicationList = Singleton.getDataSetList(DATASET_REGULATION_TIME_LIST, getMyDatabase())

                    var pos = -1
                    if (mApplicationList != null && mApplicationList!!.isNotEmpty()) {
                        val mDropdownList = arrayOfNulls<String>(mApplicationList!!.size)
                        for (i in mApplicationList!!.indices) {
                            mDropdownList[i] = mApplicationList!![i].regulation.toString()
                            if(value.equals(mApplicationList!![i].regulation, ignoreCase = true))
                            {
                                pos = i
                            }
                        }
                        //mAutoComTextViewTimeLimit.setText(mDropdownList[pos]);
                        mAutoComTextViewTimeLimit?.post {
                            if(pos>=0)
                            {
                                mAutoComTextViewTimeLimit?.setText(mApplicationList[pos].regulation)
                                mRegulationTime = mApplicationList!![pos].mTime.nullSafety()
                                mRegulationTimeValue = mApplicationList!![pos].regulation.nullSafety()
                            }
                            val adapter = ArrayAdapter(
                                    this@AddTimeRecordActivity,
                                    R.layout.row_dropdown_menu_popup_item,
                                    mDropdownList
                            )
                            try {
                                mAutoComTextViewTimeLimit?.threshold = 1
                                mAutoComTextViewTimeLimit?.setAdapter<ArrayAdapter<String?>>(adapter)
                                //mSelectedShiftStat = mApplicationList.get(pos);
                                mAutoComTextViewTimeLimit?.onItemClickListener =
                                        OnItemClickListener { parent, view, position, id ->
                                            val index = getIndexOfRegulation(
                                                    mApplicationList,
                                                    parent.getItemAtPosition(position).toString())
                                            mRegulationTime = mApplicationList!![index].mTime.nullSafety()
                                            mRegulationTimeValue = mApplicationList!![index].regulation.nullSafety()
                                            AppUtils.hideSoftKeyboard(this@AddTimeRecordActivity)
                                        }
                                if (mAutoComTextViewTimeLimit?.tag != null && mAutoComTextViewTimeLimit?.tag == "listonly") {
                                    AppUtils.setListOnly(this@AddTimeRecordActivity, mAutoComTextViewTimeLimit!!)
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    } else {
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getIndexOfRegulation(list: List<DatasetResponse>, name: String): Int {
        var pos = 0
        for (myObj in list) {
            if (name.equals(myObj.regulation, ignoreCase = true)) return pos
            pos++
        }
        return -1
    }

    //set value to Street dropdown
    private fun setDropdownStreet(value: String?) {
//        val mApplicationList = mDatasetList?.dataset?.streetList
        try {
            if (mAutoComTextViewStreet != null) {
                ioScope.launch {
                //CoroutineScope(Dispatchers.IO).async {
                    val mApplicationList = Singleton.getDataSetList(DATASET_STREET_LIST, getMyDatabase())
                    var pos = -1
                    if (mApplicationList != null && mApplicationList!!.isNotEmpty()) {
                        val mDropdownList = arrayOfNulls<String>(mApplicationList!!.size)
                        for (i in mApplicationList!!.indices) {
                            mDropdownList[i] = mApplicationList!![i].street_name.toString()
                            try {
                                if (value != null && value.isNotEmpty()) {
                                    if (mApplicationList!![i].street_name.equals(value, ignoreCase = true)) {
                                        pos = i
                                    }
                                }
                            } catch (e: Exception) {
                            }
                        }
                        //runOnUiThread {
                        mAutoComTextViewStreet?.post {
                            try {
                                if(pos>0)
                                    mAutoComTextViewStreet?.setText(mDropdownList[pos])
                            } catch (e: Exception) {
                            }

                            //mAutoComTextViewDirection.setText(mApplicationList.get(pos).getDirection()); ;
                            val adapter = ArrayAdapter(
                                this@AddTimeRecordActivity,
                                R.layout.row_dropdown_menu_popup_item,
                                mDropdownList)
                            try {
                                mAutoComTextViewStreet?.threshold = 1
                                mAutoComTextViewStreet?.setAdapter<ArrayAdapter<String?>>(adapter)
                                //mSelectedShiftStat = mApplicationList.get(pos);
                                mAutoComTextViewStreet?.onItemClickListener =
                                    OnItemClickListener { parent, view, position, id ->
                                        AppUtils.hideSoftKeyboard(this@AddTimeRecordActivity)
                                    }
                                if (mAutoComTextViewStreet?.tag != null && mAutoComTextViewStreet?.tag == "listonly") {
                                    AppUtils.setListOnly(this@AddTimeRecordActivity, mAutoComTextViewStreet!!)
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }

                        //}
                    }
               }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun setDropdownZone() {
//        val mApplicationList = mDatasetList?.dataset?.streetList
        try {
            if (mAutoComTextViewZone != null) {
                ioScope.launch {
                    //CoroutineScope(Dispatchers.IO).async {
                    val mWelcomeList = getMyDatabase()?.dbDAO?.getActivityList()
                    val mApplicationList = mWelcomeList!!.welcomeList?.zoneStats

//                    val mApplicationList = Singleton.getDataSetList(DATASET_STREET_LIST, mDb)
                    var pos = -1
                    if (mApplicationList != null && mApplicationList!!.isNotEmpty()) {
                        val mDropdownList = arrayOfNulls<String>(mApplicationList!!.size)
                        for (i in mApplicationList!!.indices) {
                            mDropdownList[i] = mApplicationList!![i].zoneName.toString()

                        }
                        //runOnUiThread {
                        mAutoComTextViewZone?.post {

                            //mAutoComTextViewDirection.setText(mApplicationList.get(pos).getDirection()); ;
                            val adapter = ArrayAdapter(
                                    this@AddTimeRecordActivity,
                                    R.layout.row_dropdown_menu_popup_item,
                                    mDropdownList)
                            try {
                                mAutoComTextViewZone?.threshold = 1
                                mAutoComTextViewZone?.setAdapter<ArrayAdapter<String?>>(adapter)
                                //mSelectedShiftStat = mApplicationList.get(pos);
                                mAutoComTextViewZone?.onItemClickListener =
                                        OnItemClickListener { parent, view, position, id ->
                                            AppUtils.hideSoftKeyboard(this@AddTimeRecordActivity)
                                        }
                                if (mAutoComTextViewZone?.tag != null && mAutoComTextViewZone?.tag == "listonly") {
                                    AppUtils.setListOnly(this@AddTimeRecordActivity, mAutoComTextViewZone!!)
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }

                        //}
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //set value to zone dropdown
    private fun setDropdownPBCZone(vlaue: String?) {
        ioScope.launch {
            if (mAutoComTextViewPBCZone != null) {
                var mApplicationList: List<ZoneStat>? = null
                val mWelcomeList = Singleton.getWelcomeDbObject(getMyDatabase())
                if (mWelcomeList != null) {
                    mApplicationList = mWelcomeList.welcomeList!!.pbcZoneStats
                }
                var pos = -1
                if (mApplicationList != null && mApplicationList.size > 0) {
                    val mDropdownList = arrayOfNulls<String>(mApplicationList.size)
                    for (i in mApplicationList.indices) {
//                mDropdownList[i] = String.valueOf(mApplicationList.get(i).getmCityZoneName());
                        mDropdownList[i] = mApplicationList[i].zoneName.toString()
//                    try {
//                        if (vlaue != null) {
//                            if (mApplicationList[i].mCityZoneName
//                                    .equals(vlaue, ignoreCase = true)
//                            ) {
//                                pos = i
//                            }
//                        }
//                    } catch (e: Exception) {
//                        e.printStackTrace()
//                    }
                    }
                    //insertFormToDb(true, false, null);
//                Arrays.sort(mDropdownList)
                    mAutoComTextViewPBCZone?.post {
                        if (pos >= 0) {
                            try {
                                mAutoComTextViewPBCZone?.setText(mApplicationList[pos].mCityZoneName)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                        val adapter = ArrayAdapter(
                            this@AddTimeRecordActivity,
                            R.layout.row_dropdown_menu_popup_item,
                            mDropdownList
                        )
                        try {
                            mAutoComTextViewPBCZone?.threshold = 1
                            mAutoComTextViewPBCZone?.setAdapter<ArrayAdapter<String?>>(adapter)
                            mAutoComTextViewPBCZone?.onItemClickListener =
                                OnItemClickListener { parent, view, position, id ->
                                    hideSoftKeyboard(this@AddTimeRecordActivity)
//                                mAutoComTextViewMeterName!!.setText(mAutoComTextViewPBCZone!!.text.toString())
                                }
                            // listonly
                            if (mAutoComTextViewPBCZone?.tag != null && mAutoComTextViewPBCZone?.tag == "listonly") {
                                setListOnly(this@AddTimeRecordActivity, mAutoComTextViewPBCZone!!)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }
    }

   /* //set value to zone dropdown
    private fun setDropdownZone() {
        try {
            //CoroutineScope(Dispatchers.IO).async {
            ioScope.launch {
                if (mAutoComTextViewZone != null) {
                    val mWelcomeList = mDb?.dbDAO?.getActivityList()
                    val mApplicationList = mWelcomeList!!.welcomeList?.zoneStats
                    val pos = 0
                    if (mApplicationList != null && mApplicationList.isNotEmpty()) {
                        val mDropdownList = arrayOfNulls<String>(mApplicationList.size)
                        for (i in mApplicationList.indices) {
                            mDropdownList[i] = mApplicationList[i].zoneName.toString()
                        }
                        val adapter = ArrayAdapter(
                                this@AddTimeRecordActivity,
                                R.layout.row_dropdown_menu_popup_item,
                                mDropdownList
                        )
                        try {
                            mAutoComTextViewZone?.threshold = 1
                            mAutoComTextViewZone?.setAdapter<ArrayAdapter<String?>>(adapter)
                            mAutoComTextViewZone?.onItemClickListener =
                                    OnItemClickListener { parent, view, position, id ->
//                                        AppUtils.hideSoftKeyboard(this@AddTimeRecordActivity)
                                    }
                            if (mAutoComTextViewZone?.tag != null && mAutoComTextViewZone?.tag == "listonly") {
                                AppUtils.setListOnly(this@AddTimeRecordActivity, mAutoComTextViewZone!!)
                            }
                        } catch (e: Exception) {
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
*/
    //set value to Meter Name dropdown
    private fun setDropdownMeterName() {
        try {
            if (mAutoComTextViewMeter != null) {
                //CoroutineScope(Dispatchers.IO).async {
                ioScope.launch {
                    //        val mApplicationList = mDatasetList?.dataset?.meterList
                    val mApplicationList = Singleton.getDataSetList(DATASET_METER_LIST, getMyDatabase())

                    val pos = 0
                    if (mApplicationList != null && mApplicationList!!.isNotEmpty()) {
                        val mDropdownList = arrayOfNulls<String>(mApplicationList!!.size)
                        for (i in mApplicationList!!.indices) {
                            mDropdownList[i] = mApplicationList!![i].name.toString()
                        }

                        mAutoComTextViewMeter?.post {
                            val adapter = ArrayAdapter(
                                    this@AddTimeRecordActivity,
                                    R.layout.row_dropdown_menu_popup_item,
                                    mDropdownList
                            )
                            try {
                                mAutoComTextViewMeter?.threshold = 1
                                mAutoComTextViewMeter?.setAdapter<ArrayAdapter<String?>>(adapter)
                                //mSelectedShiftStat = mApplicationList.get(pos);
                                mAutoComTextViewMeter?.onItemClickListener =
                                        OnItemClickListener { parent, view, position, id -> // mSelectedShiftStat = mApplicationList.get(position);
                                            AppUtils.hideSoftKeyboard(this@AddTimeRecordActivity)
                                            try {
                                                mAutoComTextViewBlock?.setText(
                                                        mApplicationList!![position].block!!.toString()
                                                )
                                            } catch (e: Exception) {
                                            }
                                            setDropdownStreet(mApplicationList!![position].street)
                                        }
                                if (mAutoComTextViewMeter?.tag != null && mAutoComTextViewMeter?.tag == "listonly") {
                                    AppUtils.setListOnly(this@AddTimeRecordActivity, mAutoComTextViewMeter!!)
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //set value to Meter Name dropdown
    private fun setDropdownLocationName() {
        try {
            if (mAutoComTextViewLocation != null) {
                //CoroutineScope(Dispatchers.IO).async {
                ioScope.launch {
                    //        val mApplicationList = mDatasetList?.dataset?.lotList
                    val mApplicationList = Singleton.getDataSetList(DATASET_LOT_LIST, getMyDatabase())

                    if (mApplicationList != null && mApplicationList!!.size > 0) {
                        val mDropdownList = arrayOfNulls<String>(mApplicationList!!.size)
                        for (i in mApplicationList!!.indices) {
                            mDropdownList[i] = mApplicationList!![i].location.toString()
                        }
                        mAutoComTextViewLocation?.post {
                            val adapter = ArrayAdapter(
                                    this@AddTimeRecordActivity,
                                    R.layout.row_dropdown_menu_popup_item,
                                    mDropdownList
                            )
                            try {
                                mAutoComTextViewLocation?.threshold = 1
                                mAutoComTextViewLocation?.setAdapter<ArrayAdapter<String?>>(adapter)
                                //mSelectedShiftStat = mApplicationList.get(pos);
                                mAutoComTextViewLocation?.onItemClickListener =
                                        OnItemClickListener { parent, view, position, id -> // mSelectedShiftStat = mApplicationList.get(position);
                                            AppUtils.hideSoftKeyboard(this@AddTimeRecordActivity)
                                            val index = getIndexOfLocation(
                                                    mApplicationList,
                                                    parent.getItemAtPosition(position).toString()
                                            )
                                            try {
                                                mAutoComTextViewBlock?.setText(
                                                        mApplicationList!![index].block?.toString()
                                                )
                                            } catch (e: Exception) {
                                            }
                                            setDropdownStreet(mApplicationList!![index].street)
                                        }
                                if (mAutoComTextViewLocation?.tag != null && mAutoComTextViewLocation?.tag == "listonly") {
                                    AppUtils.setListOnly(this@AddTimeRecordActivity, mAutoComTextViewLocation!!)
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun getIndexOfLocation(list: List<DatasetResponse>, name: String): Int {
        var pos = 0
        for (myObj in list) {
            if (name.equals(myObj.location, ignoreCase = true)) return pos
            pos++
        }
        return -1
    }

    //set value to Remark dropdown
    private fun setDropdownRemark() {
        try {
            if (mAutoComTextViewRemarks != null) {
                //CoroutineScope(Dispatchers.IO).async {
                ioScope.launch {
                    //        val mApplicationList = mDatasetList?.dataset?.remarksList
                    val mApplicationList = Singleton.getDataSetList(DATASET_REMARKS_LIST, getMyDatabase())
//                    if(DataSetClass.getRemarkList().isNullOrEmpty()) {
//                        mApplicationList = Singleton.getDataSetList(DATASET_REMARKS_LIST, mDb)
//                        DataSetClass.setRemarkList(mApplicationList)
//                    }else{
//                        mApplicationList = DataSetClass.getRemarkList()!!
//                    }

                    if (mApplicationList != null && mApplicationList!!.isNotEmpty()) {
                        val mDropdownList = arrayOfNulls<String>(mApplicationList!!.size)
                        for (i in mApplicationList!!.indices) {
                            mDropdownList[i] = mApplicationList!![i].remark.toString()
                        }
                        mAutoComTextViewRemarks?.post {
                            val adapter = ArrayAdapter(
                                    this@AddTimeRecordActivity,
                                    R.layout.row_dropdown_menu_popup_item,
                                    mDropdownList
                            )
                            try {
                                mAutoComTextViewRemarks?.threshold = 1
                                mAutoComTextViewRemarks?.setAdapter<ArrayAdapter<String?>>(adapter)
                                //mSelectedShiftStat = mApplicationList.get(pos);
                                mAutoComTextViewRemarks?.onItemClickListener =
                                        OnItemClickListener { parent, view, position, id -> // mSelectedShiftStat = mApplicationList.get(position);
                                            setFocus(mAutoComTextViewRemarks)
                                            layBottomKeyboard?.visibility = View.GONE
                                            AppUtils.hideSoftKeyboard(this@AddTimeRecordActivity)
                                        }
                                if (mAutoComTextViewRemarks?.tag != null && mAutoComTextViewRemarks?.tag == "listonly") {
                                    AppUtils.setListOnly(this@AddTimeRecordActivity, mAutoComTextViewRemarks!!)
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    //set value to Remark2 dropdown
    private fun setDropdownRemark2() {
        try {
            if (mAutoComTextViewRemarks2 != null) {
                ioScope.launch {
                    val mApplicationList = Singleton.getDataSetList(DATASET_REMARKS_LIST, getMyDatabase())

                    if (mApplicationList != null && mApplicationList!!.isNotEmpty()) {
                        val mDropdownList = arrayOfNulls<String>(mApplicationList!!.size)
                        for (i in mApplicationList!!.indices) {
                            mDropdownList[i] = mApplicationList!![i].remark.toString()
                        }
                        mAutoComTextViewRemarks2?.post {
                            val adapter = ArrayAdapter(
                                    this@AddTimeRecordActivity,
                                    R.layout.row_dropdown_menu_popup_item,
                                    mDropdownList
                            )
                            try {
                                mAutoComTextViewRemarks2?.threshold = 1
                                mAutoComTextViewRemarks2?.setAdapter<ArrayAdapter<String?>>(adapter)
                                //mSelectedShiftStat = mApplicationList.get(pos);
                                mAutoComTextViewRemarks2?.onItemClickListener =
                                        OnItemClickListener { parent, view, position, id -> // mSelectedShiftStat = mApplicationList.get(position);
                                            setFocus(mAutoComTextViewRemarks2)
                                            layBottomKeyboard?.visibility = View.GONE
                                            AppUtils.hideSoftKeyboard(this@AddTimeRecordActivity)
                                        }
                                if (mAutoComTextViewRemarks2?.tag != null && mAutoComTextViewRemarks2?.tag == "listonly") {
                                    AppUtils.setListOnly(this@AddTimeRecordActivity, mAutoComTextViewRemarks2!!)
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //set value to Tier Left dropdown
    private fun setDropdownTierLeft() {
        try {
            if (mAutoComTextViewTierLeft != null) {
                ioScope.launch {
                //CoroutineScope(Dispatchers.IO).async {
                    //        val mApplicationList = mDatasetList?.dataset?.tierStemList
                    val mApplicationList = Singleton.getDataSetList(DATASET_TIER_STEM_LIST, getMyDatabase())
                    if (mApplicationList != null && mApplicationList!!.isNotEmpty()) {
                        val mDropdownList = arrayOfNulls<String>(mApplicationList!!.size)
                        for (i in mApplicationList!!.indices) {
                            val aFormatted: String = formatter.format(mApplicationList!![i].tierStem)
                            mDropdownList[i] = aFormatted
                        }

                        Arrays.sort(mDropdownList, Comparator<String?> { s1, s2 ->
                            Integer.valueOf(s1).compareTo(Integer.valueOf(s2))
                        })
                        mAutoComTextViewTierLeft?.post {
                            val adapter = ArrayAdapter(
                                    this@AddTimeRecordActivity,
                                    R.layout.row_dropdown_menu_popup_item,
                                    mDropdownList
                            )
                            try {
                                mAutoComTextViewTierLeft?.threshold = 1
                                mAutoComTextViewTierLeft?.setAdapter<ArrayAdapter<String?>>(adapter)
                                //mSelectedShiftStat = mApplicationList.get(pos);
                                mAutoComTextViewTierLeft?.onItemClickListener =
                                        OnItemClickListener { parent, view, position, id -> // mSelectedShiftStat = mApplicationList.get(position);
                                            AppUtils.hideSoftKeyboard(this@AddTimeRecordActivity)
                                            mFrontTireStemValue = mAutoComTextViewTierLeft!!.text.toString()
                                        }
                                if (mAutoComTextViewTierLeft?.tag != null && mAutoComTextViewTierLeft?.tag == "listonly") {
                                    AppUtils.setListOnly(this@AddTimeRecordActivity, mAutoComTextViewTierLeft!!)
                                }
                            } catch (e: Exception) {
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //set value to Tier Right dropdown
    private fun setDropdownTierRight() {
        try {
            if (mAutoComTextViewTierRight != null) {
                //CoroutineScope(Dispatchers.IO).async {
                ioScope.launch {
                    //        val mApplicationList = mDatasetList?.dataset?.tierStemList
                    val mApplicationList = Singleton.getDataSetList(DATASET_TIER_STEM_LIST, getMyDatabase())

//                    mApplicationList = mApplicationList
                    if (mApplicationList != null && mApplicationList!!.isNotEmpty()) {
                        val mDropdownList = arrayOfNulls<String>(mApplicationList!!.size)
                        for (i in mApplicationList!!.indices) {
                            val aFormatted: String = formatter.format(mApplicationList!![i].tierStem)
                            mDropdownList[i] = aFormatted
//                            mDropdownList[i] = mApplicationList!![i].tierStem.toString()
                        }
                        //mAutoComTextViewTierRight.setText(mDropdownList[pos]);
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                            Arrays.sort(mDropdownList, Comparator.comparingInt(String::length))
//                        }
                        Arrays.sort(mDropdownList, Comparator<String?> { s1, s2 ->
                            Integer.valueOf(s1).compareTo(Integer.valueOf(s2))
                        })
                        mAutoComTextViewTierRight?.post {
                            val adapter = ArrayAdapter(
                                    this@AddTimeRecordActivity,
                                    R.layout.row_dropdown_menu_popup_item,
                                    mDropdownList
                            )
                            try {
                                mAutoComTextViewTierRight?.threshold = 1
                                mAutoComTextViewTierRight?.setAdapter<ArrayAdapter<String?>>(adapter)
                                //mSelectedShiftStat = mApplicationList.get(pos);
                                mAutoComTextViewTierRight!!.onItemClickListener =
                                        OnItemClickListener { parent, view, position, id -> // mSelectedShiftStat = mApplicationList.get(position);
                                            AppUtils.hideSoftKeyboard(this@AddTimeRecordActivity)
                                            mRearTireStemValue = mAutoComTextViewTierRight!!.text.toString()
                                        }
                                if (mAutoComTextViewTierRight?.tag != null && mAutoComTextViewTierRight?.tag == "listonly") {
                                    AppUtils.setListOnly(this@AddTimeRecordActivity, mAutoComTextViewTierRight!!)
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /* Call Api to Add timing */
    private fun callAddTimingApi() {
        try {
            /**
             * Save selected block and street when address is locked
             */
            sharedPreference.write(
                    SharedPrefKey.LOCK_GEO_SAVE_ADDRESS,
                (if(mAutoComTextViewBlock!=null)mAutoComTextViewBlock!!.text.toString().trim() else "") + "#" + mAutoComTextViewStreet?.text.toString().trim()+", A")

            val mLat = sharedPreference.read(SharedPrefKey.LAT, "0.0")?.toDouble()
            val mLong = sharedPreference.read(SharedPrefKey.LONG, "0.0")?.toDouble()

             mAddTimingRequest = AddTimingRequest()
            try {
                mAddTimingRequest!!.lprState =
                        mAutoComTextViewLicState!!.text.toString().trim()
            } catch (e: Exception) {
                mAddTimingRequest!!.lprState = ""
            }
            try {
                mAddTimingRequest!!.lprNumber =
                        mAutoComTextViewLicNo!!.text.toString().trim()
            } catch (e: Exception) {
                mAddTimingRequest!!.lprNumber = ""
            }
            try {
                mAddTimingRequest!!.meterNumber =
                        mAutoComTextViewMeter!!.text.toString().trim()
            } catch (e: Exception) {
                mAddTimingRequest!!.meterNumber = ""
            }
            try {
                mAddTimingRequest!!.mLot =
                        mAutoComTextViewLocation!!.text.toString().trim()
            } catch (e: Exception) {
                mAddTimingRequest!!.mLot = ""
            }
            try {
                mAddTimingRequest!!.mLocation =
                        mAutoComTextViewLocation!!.text.toString().trim()
            } catch (e: Exception) {
                mAddTimingRequest!!.mLocation = ""
            }
            try {
                mAddTimingRequest!!.block =
                        mAutoComTextViewBlock!!.text.toString().trim()
            } catch (e: Exception) {
                mAddTimingRequest!!.block = ""
            }
            try {
                mAddTimingRequest!!.regulationTime = mRegulationTime.trim().toLong()
                mAddTimingRequest!!.regulationTimeValue = mRegulationTimeValue.trim()
            } catch (e: Exception) {
                mAddTimingRequest!!.regulationTime = "0".toLong()
            }
            try {
                mAddTimingRequest!!.street =
                        mAutoComTextViewStreet!!.text.toString().trim()
            } catch (e: Exception) {
                mAddTimingRequest!!.street = ""
            }
            try {
                mAddTimingRequest!!.side =
                        mAutoComTextViewDirection!!.text.toString().trim()
            } catch (e: Exception) {
                mAddTimingRequest!!.side = ""
            }
            try {
                mAddTimingRequest!!.zone =
                        mAutoComTextViewZone!!.text.toString().trim()
            } catch (e: Exception) {
                mAddTimingRequest!!.zone = ""
            }
            try {
                mAddTimingRequest!!.pbcZone =
                        mAutoComTextViewPBCZone!!.text.toString().trim()
            } catch (e: Exception) {
                mAddTimingRequest!!.pbcZone = ""
            }
            try {
                mAddTimingRequest!!.remark =
                        mAutoComTextViewRemarks!!.text.toString().trim()

            } catch (e: Exception) {
                mAddTimingRequest!!.remark = ""
            }
            try {
                mAddTimingRequest!!.remark2 =
                        mAutoComTextViewRemarks2!!.text.toString().trim()
            } catch (e: Exception) {
                mAddTimingRequest!!.remark2 = ""
            }
            try {
                sharedPreference.write(
                        SharedPrefKey.LOCK_TIMING_REMARK,
                        mAutoComTextViewRemarks?.text.toString().trim())
                sharedPreference.write(
                        SharedPrefKey.LOCK_TIMING_REMARK2,
                        mAutoComTextViewRemarks2?.text.toString().trim()?.let { (it) } ?: "")
            } catch (e: Exception) {
            }
            try {
                mAddTimingRequest!!.mTireStemFront = mFrontTireStemValue!!.toInt()
            } catch (e: Exception) {
                mAddTimingRequest!!.mTireStemFront = 0
            }
            try {
                mAddTimingRequest!!.mTireStemBack = mRearTireStemValue!!.toInt()
            } catch (e: Exception) {
                mAddTimingRequest!!.mTireStemBack = 0
            }
            try {
                mAddTimingRequest!!.mVin =
                        mAutoComTextViewVin!!.text.toString().trim()
            } catch (e: Exception) {
                mAddTimingRequest!!.mVin = ""
            }
            mAddTimingRequest!!.status = "Open"
            mAddTimingRequest!!.latitude = mLat
            mAddTimingRequest!!.longitiude = mLong
            mAddTimingRequest!!.source = "officer"
            mAddTimingRequest!!.officerName = mWelcomeFormData?.officerFirstName.nullSafety() + " " +
                    mWelcomeFormData?.officerLastName.nullSafety()
            mAddTimingRequest!!.badgeId = mWelcomeFormData?.officerBadgeId.nullSafety()
            //            mAddTimingRequest.setShift(mWelcomeFormData.getOfficerShift());
            mAddTimingRequest!!.shift = sharedPreference.read(SharedPrefKey.LOGIN_SHIFT, "")
            mAddTimingRequest!!.supervisor = mWelcomeFormData?.officerSupervisor.nullSafety()
            mAddTimingRequest!!.markStartTimestamp = mStartTime
            mAddTimingRequest!!.markIssueTimestamp = AppUtils.splitDateLpr(mZone)
            mAddTimingRequest!!.mMake = mSelectedMake
            mAddTimingRequest!!.mModel = mSelectedModel
            mAddTimingRequest!!.mColor = mSelectedColor
//            mAddTimingRequest.mAddress = mAddress
            mAddTimingRequest!!.mAddress = mAddTimingRequest!!.block+" "+mAddTimingRequest!!.street
            mAddTimingRequest!!.imageUrls = mImages
            lockTimeData(false,mAddTimingRequest)
            if (isInternetAvailable(this@AddTimeRecordActivity)) {
                if (bannerList?.isEmpty().nullSafety()) {
                    mAddTimingViewModel?.hitAddTimingApi(mAddTimingRequest)
                } else {
                    callUploadAllImages()
                }
            } else {
                saveTimingImagesOffline()
                saveTimingDataForm(mAddTimingRequest!!)
                LogUtil.printToastMSGForErrorWarning(
                        applicationContext,
                        getString(R.string.err_msg_connection_was_refused)
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun callUploadAllImages() {
        if (isInternetAvailable(this@AddTimeRecordActivity)) {
            mImageJsonString = ObjectMapperProvider.toJson(bannerList!!)

            for (i in bannerList?.indices!!) {
                val file = File(bannerList!![i]?.timingImage.nullSafety())
                val requestFile = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file!!)
                val mPart : MultipartBody.Part = MultipartBody.Part.createFormData(
                    "files",
                    if (file != null) file.name else "",
                    requestFile
                )

                val fileNames = arrayOf(FileUtil.getFileNameWithoutExtension(file.name))

                //Code for one by one upload
                val mRequestBodyType = RequestBody.create("text/plain".toMediaTypeOrNull(), API_CONSTANT_UPLOAD_TYPE_TIMING_IMAGES)
                mUploadImageViewModel?.hitUploadImagesApi(fileNames, mRequestBodyType, mPart)
            }
        } else {
            LogUtil.printToastMSG(
                this@AddTimeRecordActivity,
                getString(R.string.err_msg_connection_was_refused)
            )
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
                        if (tag.equals(DynamicAPIPath.POST_ADD_TIMING, ignoreCase = true)) {


                            val responseModel = ObjectMapperProvider.fromJson(apiResponse.data.toString(), AddTimingResponse::class.java)

                            if (responseModel != null && responseModel.success.nullSafety()) {
                                getMyDatabase()?.dbDAO?.updateTimingUploadStatus(0, timingDataIDForTable)
                                if(responseModel!!.data!!.isAbandonVehicle==true)
//                                if(true)
                                {
                                    val mIntent = Intent(this, AbandonedVehicleActivity::class.java)
                                    val myJson = ObjectMapperProvider.toJson(mAddTimingRequest!!)
                                    mIntent.putExtra("timeData", myJson)
//                                    mIntent.putExtra("timeImages", mImageJsonString)
                                    startActivity(mIntent)
                                    finish()
                                }else {
                                    if (BuildConfig.FLAVOR.equals(
                                            Constants.FLAVOR_TYPE_BISMARCK, ignoreCase = true) )
                                    {
                                        val mIntent = Intent(this, LprDetailsActivity::class.java)
                                        startActivity(mIntent)
                                    }
                                    finish()
                                }
                            } else if (responseModel != null && !responseModel.success.nullSafety()) {
                                val message: String
                                if (responseModel.response != null && responseModel.response != "") {
                                    message = responseModel.response.nullSafety()
                                    AppUtils.showCustomAlertDialog(
                                        mContext,
                                        getString(R.string.scr_lbl_add_time_record),
                                        message,
                                        getString(R.string.alt_lbl_OK),
                                        getString(R.string.scr_btn_cancel),
                                        this
                                    )
                                } else {
                                    responseModel.response =
                                        getString(R.string.err_msg_something_went_wrong)
                                    message = responseModel.response.nullSafety()
                                    AppUtils.showCustomAlertDialog(
                                        mContext, getString(R.string.scr_lbl_add_time_record),
                                        message, getString(R.string.alt_lbl_OK),
                                        getString(R.string.scr_btn_cancel), this
                                    )
                                }
                            } else {
                                AppUtils.showCustomAlertDialog(
                                    mContext,
                                    getString(R.string.scr_lbl_add_time_record),
                                    getString(R.string.err_msg_something_went_wrong),
                                    getString(R.string.alt_lbl_OK),
                                    getString(R.string.scr_btn_cancel),
                                    this
                                )
                                dismissLoader()
                            }
                        }
                        if (tag.equals(DynamicAPIPath.POST_IMAGE, ignoreCase = true)) {
                            try {

                                val responseModel = ObjectMapperProvider.fromJson(apiResponse.data.toString(), UploadImagesResponse::class.java)

                                if (responseModel != null && responseModel.status.nullSafety()) {
                                    if (responseModel.data != null &&
                                        responseModel.data?.isNotEmpty().nullSafety()
                                        && responseModel.data!![0].response != null
                                        && responseModel.data!![0].response?.links != null
                                        && responseModel.data!![0].response?.links?.isNotEmpty()
                                            .nullSafety()
                                    ) {
                                        //Code for sequential API call image upload
                                        mImages.add(responseModel.data!![0].response!!.links!![0])
                                        if (mImages.size.nullSafety() == bannerList?.size.nullSafety()) {
//                                            removeTimingImagesFromFolder()
                                            getMyDatabase()?.dbDAO?.deleteTimingImagesWithTimingRecordId(
                                                timingDataIDForTable
                                            )
                                            bannerList?.clear()
                                            mainScope.launch {
                                                delay(500)
                                                callAddTimingApi()
                                            }
//                                            Handler(Looper.getMainLooper()).postDelayed(
//                                                { callAddTimingApi() },
//                                                500
//                                            )
                                        }

                                    } else {
                                        AppUtils.showCustomAlertDialog(
                                            mContext,
                                            APIConstant.POST_IMAGE,
                                            getString(R.string.err_msg_something_went_wrong_imagearray),
                                            getString(R.string.alt_lbl_OK),
                                            getString(R.string.scr_btn_cancel),
                                            this
                                        )
                                    }
                                    //LogUtil.printToastMSG(LprPreviewActivity.this, responseModel.getMessage());
                                } else {
                                    dismissLoader()
                                    //lastSecondTag = ""
                                    AppUtils.showCustomAlertDialog(
                                        mContext,
                                        APIConstant.POST_IMAGE,
                                        getString(R.string.err_msg_something_went_wrong),
                                        getString(R.string.alt_lbl_OK),
                                        getString(R.string.scr_btn_cancel),
                                        this
                                    )
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    } catch (e: Exception) {
                        dismissLoader()
                        logout(mContext!!)
                    }
                }
            }
            Status.ERROR -> {
                dismissLoader()
                LogUtil.printToastMSGForErrorWarning(
                    applicationContext,
                    getString(R.string.err_msg_connection_was_refused)
                )
            }

            else -> {}
        }
    }

    private fun removeTimingImagesFromFolder() {
        try {
            if (bannerList?.size.nullSafety() > 0) {
                bannerList?.forEach {
                    val oldFile = File(it?.timingImage)
                    if (oldFile.exists()) oldFile.delete()
                }
            }
        } catch (e: Exception) {
        }
    }

    private fun removeFocus() {
        try {
            if (mAutoComTextViewMeter != null) {
                mAutoComTextViewMeter?.clearFocus()
            }
        } catch (e: Exception) {
        }
        try {
            if (mAutoComTextViewBlock != null) {
                mAutoComTextViewBlock?.clearFocus()
            }
        } catch (e: Exception) {
        }
        try {
            if (mAutoComTextViewStreet != null) {
                mAutoComTextViewStreet?.clearFocus()
            }
        } catch (e: Exception) {
        }
        try {
            if (mAutoComTextViewDirection != null) {
                mAutoComTextViewDirection?.clearFocus()
            }
        } catch (e: Exception) {
        }
        try {
            if (mAutoComTextViewLicNo != null) {
                mAutoComTextViewLicNo?.clearFocus()
            }
        } catch (e: Exception) {
        }
        try {
            if (mAutoComTextViewLicState != null) {
                mAutoComTextViewLicState?.clearFocus()
            }
        } catch (e: Exception) {
        }
        try {
            if (mAutoComTextViewVin != null) {
                mAutoComTextViewVin?.clearFocus()
            }
        } catch (e: Exception) {
        }
        try {
            if (mAutoComTextViewTimeLimit != null) {
                mAutoComTextViewTimeLimit?.clearFocus()
            }
        } catch (e: Exception) {
        }
        try {
            if (mAutoComTextViewTierLeft != null) {
                mAutoComTextViewTierLeft?.clearFocus()
            }
        } catch (e: Exception) {
        }
        try {
            if (mAutoComTextViewTierRight != null) {
                mAutoComTextViewTierRight?.clearFocus()
            }
        } catch (e: Exception) {
        }
        try {
            if (mAutoComTextViewZone != null) {
                mAutoComTextViewZone?.clearFocus()
            }
        } catch (e: Exception) {
        }
        try {
            if (mAutoComTextViewRemarks != null) {
                mAutoComTextViewRemarks?.clearFocus()
            }
        } catch (e: Exception) {
        }
        try {
            if (mAutoComTextViewRemarks2 != null) {
                mAutoComTextViewRemarks2?.clearFocus()
            }
        } catch (e: Exception) {
        }

    }//mAutoComTextViewRemarks.setError(getString(R.string.val_msg_please_enter_remark1));//mAutoComTextViewZone.setError(getString(R.string.val_msg_please_enter_zone));//mAutoComTextViewTierRight.setError(getString(R.string.val_msg_please_enter_tier_stem_right));//mAutoComTextViewTierLeft.setError(getString(R.string.val_msg_please_enter_tier_stem_left));//mAutoComTextViewTimeLimit.setError(getString(R.string.val_msg_please_enter_time_limit));//mAutoComTextViewVin.setError(getString(R.string.val_msg_please_enter_vin_number));//mAutoComTextViewLicState.setError(getString(R.string.val_msg_please_enter_state));//mAutoComTextViewLicNo.setError(getString(R.string.val_msg_please_enter_lpr_number));//mAutoComTextViewDirection.setError(getString(R.string.val_msg_please_enter_side));//mAutoComTextViewStreet.setError(getString(R.string.val_msg_please_enter_street));//mAutoComTextViewBlock.setError(getString(R.string.val_msg_please_enter_block));//mAutoComTextViewMeter.setError(getString(R.string.val_msg_please_enter_meter_name));

    //                if (mCitationLayout.get(0).getComponent().equalsIgnoreCase("marking_vehicle")) {
    private fun isFormValid(): Boolean {
        try {
            if (mCitationLayout != null) {
//                if (mCitationLayout.get(0).getComponent().equalsIgnoreCase("marking_vehicle")) {
                for (i in mCitationLayout!!.indices) {
                    for (iOff in mCitationLayout!![i].fields?.indices!!) {
                        if (mCitationLayout!![i].fields!![iOff].name.equals(
                                "meter",ignoreCase = true)) {
                            if (mCitationLayout!![i].fields!![iOff].isRequired.nullSafety()) {
                                if (TextUtils.isEmpty(mAutoComTextViewMeter?.text.toString().trim())) {
                                    //mAutoComTextViewMeter.setError(getString(R.string.val_msg_please_enter_meter_name));
                                    mAutoComTextViewMeter?.requestFocus()
                                    mAutoComTextViewMeter?.isFocusableInTouchMode = true
                                    mAutoComTextViewMeter?.isFocusable = true
                                    AppUtils.showKeyboard(this@AddTimeRecordActivity)
                                    LogUtil.printToastMSGForErrorWarning(
                                        applicationContext,
                                        getString(R.string.val_msg_please_enter_meter_name)
                                    )
                                    //FOR LIST ONLY IN
                                    if (TextUtils.isEmpty(mAutoComTextViewMeter?.text.toString()
                                                    .trim())){
                                        return true
                                    }
                                    return false
                                }
                            }
                        } else if (mCitationLayout!![i].fields!![iOff].name.equals(
                                "block",ignoreCase = true)) {
                            if (mCitationLayout!![i].fields!![iOff].isRequired.nullSafety()) {
                                if (TextUtils.isEmpty(
                                        mAutoComTextViewBlock?.text.toString()
                                            .trim())) {
                                    //mAutoComTextViewBlock.setError(getString(R.string.val_msg_please_enter_block));
                                    mAutoComTextViewBlock?.requestFocus()
                                    mAutoComTextViewBlock?.isFocusableInTouchMode = true
                                    mAutoComTextViewBlock?.isFocusable = true
                                    AppUtils.showKeyboard(this@AddTimeRecordActivity)
                                    LogUtil.printToastMSGForErrorWarning(
                                        applicationContext,
                                        getString(R.string.val_msg_please_enter_block)
                                    )
                                    return false
                                }
                            }
                        } else if (mCitationLayout!![i].fields!![iOff].name.equals(
                                "street",ignoreCase = true)
                            || mCitationLayout!![i].fields!![iOff].name.equals(
                                "street_textbox",ignoreCase = true)) {
                            if (mCitationLayout!![i].fields!![iOff].isRequired.nullSafety()) {
                                if (TextUtils.isEmpty(mAutoComTextViewStreet?.text.toString()
                                            .trim())) {
                                    //mAutoComTextViewStreet.setError(getString(R.string.val_msg_please_enter_street));
                                    mAutoComTextViewStreet?.requestFocus()
//                                    mAutoComTextViewStreet?.isFocusableInTouchMode = true
                                    mAutoComTextViewStreet?.isFocusable = true
                                    AppUtils.showKeyboard(this@AddTimeRecordActivity)
                                    LogUtil.printToastMSGForErrorWarning(
                                        applicationContext,
                                        getString(R.string.val_msg_please_enter_street)
                                    )
                                    return false
                                }
                            }
                        } else if (mCitationLayout!![i].fields!![iOff].name.equals(
                                "side_of_street",ignoreCase = true)
                            || mCitationLayout!![i].fields!![iOff].name.equals(
                                "side", ignoreCase = true )) {
                            if (mCitationLayout!![i].fields!![iOff].isRequired.nullSafety()) {
                                if (TextUtils.isEmpty(
                                        mAutoComTextViewDirection!!.text.toString()
                                            .trim() )) {
                                    //mAutoComTextViewDirection.setError(getString(R.string.val_msg_please_enter_side));
                                    mAutoComTextViewDirection?.requestFocus()
                                    mAutoComTextViewDirection?.isFocusableInTouchMode = true
                                    mAutoComTextViewDirection?.isFocusable = true
                                    AppUtils.showKeyboard(this@AddTimeRecordActivity)
                                    LogUtil.printToastMSGForErrorWarning(
                                        applicationContext,
                                        getString(R.string.val_msg_please_enter_side)
                                    )
                                    return false
                                }
                            }
                        } else if (mCitationLayout!![i].fields!![iOff].name.equals(
                                "lic_no",ignoreCase = true)
                            || mCitationLayout!![i].fields!![iOff].name.equals(
                                "lp_number",ignoreCase = true)) {
                            if (mCitationLayout!![i].fields!![iOff].isRequired.nullSafety()) {
                                if (mAutoComTextViewVin != null) {
                                    //Start of conditional either lpr or vin number validation for Glendale
                                    if (TextUtils.isEmpty(
                                            mAutoComTextViewVin?.text.toString().trim())
                                        && TextUtils.isEmpty(
                                            mAutoComTextViewLicNo?.text.toString().trim()) && BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLENDALE,ignoreCase = true)||
                                        TextUtils.isEmpty(
                                            mAutoComTextViewVin?.text.toString().trim())
                                        && TextUtils.isEmpty(
                                            mAutoComTextViewLicNo?.text.toString().trim()) && BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLENDALE_POLICE,ignoreCase = true)) {
//                                        mAutoComTextViewVin?.requestFocus()
//                                        mAutoComTextViewVin?.isFocusableInTouchMode = true
//                                        mAutoComTextViewVin?.isFocusable = true
//                                        AppUtils.showKeyboard(this@AddTimeRecordActivity)
                                        LogUtil.printToastMSGForErrorWarning(
                                            applicationContext,
                                            getString(R.string.val_msg_please_enter_lpr_number_or_vin_number)
                                        )
                                        return false
                                    }
                                    //End of conditional either lpr or vin number validation for Glendale
                                    else if (TextUtils.isEmpty(
                                            mAutoComTextViewVin?.text.toString().trim())
                                        &&TextUtils.isEmpty(
                                            mAutoComTextViewLicNo?.text.toString().trim())) {
                                        //mAutoComTextViewLicNo.setError(getString(R.string.val_msg_please_enter_lpr_number));
                                        mAutoComTextViewLicNo?.requestFocus()
                                        mAutoComTextViewLicNo?.isFocusableInTouchMode = true
                                        mAutoComTextViewLicNo?.isFocusable = true
                                        AppUtils.showKeyboard(this@AddTimeRecordActivity)
                                        LogUtil.printToastMSGForErrorWarning(
                                            applicationContext,
                                            getString(R.string.val_msg_please_enter_lpr_number)
                                        )
                                        return false
                                    }
                                } else if (TextUtils.isEmpty(
                                        mAutoComTextViewLicNo?.text.toString().trim() )) {
                                    mAutoComTextViewLicNo?.requestFocus()
                                    mAutoComTextViewLicNo?.isFocusableInTouchMode = true
                                    mAutoComTextViewLicNo?.isFocusable = true
                                    AppUtils.showKeyboard(this@AddTimeRecordActivity)
                                    LogUtil.printToastMSGForErrorWarning(
                                        applicationContext,
                                        getString(R.string.val_msg_please_enter_lpr_number)
                                    )
                                    return false
                                }
                            }
                        } else if (mCitationLayout!![i].fields!![iOff].name.equals(
                                "lic_state",ignoreCase = true)
                            || mCitationLayout!![i].fields!![iOff].name.equals(
                                "state",ignoreCase = true)) {
                            if (mCitationLayout!![i].fields!![iOff].isRequired.nullSafety()) {
                                if (mAutoComTextViewVin != null) {
                                    if (TextUtils.isEmpty(
                                            mAutoComTextViewVin?.text.toString().trim())
                                        &&TextUtils.isEmpty(
                                            mAutoComTextViewLicState?.text.toString().trim())) {
                                        //mAutoComTextViewLicState.setError(getString(R.string.val_msg_please_enter_state));
                                        mAutoComTextViewLicState?.requestFocus()
                                        mAutoComTextViewLicState?.isFocusableInTouchMode = true
                                        mAutoComTextViewLicState?.isFocusable = true
                                        AppUtils.showKeyboard(this@AddTimeRecordActivity)
                                        LogUtil.printToastMSGForErrorWarning(
                                            applicationContext,
                                            getString(R.string.val_msg_please_enter_state)
                                        )
                                        return false
                                    }
                                } else if (TextUtils.isEmpty(
                                        mAutoComTextViewLicState?.text.toString().trim())) {
                                    mAutoComTextViewLicState?.requestFocus()
                                    mAutoComTextViewLicState?.isFocusableInTouchMode = true
                                    mAutoComTextViewLicState?.isFocusable = true
                                    AppUtils.showKeyboard(this@AddTimeRecordActivity)
                                    LogUtil.printToastMSGForErrorWarning(
                                        applicationContext,
                                        getString(R.string.val_msg_please_enter_state)
                                    )
                                    return false
                                }
                            }
                        } else if (mCitationLayout!![i].fields!![iOff].name.equals(
                                "vin",ignoreCase = true) || mCitationLayout!![i].fields!![iOff].name.equals(
                                "vin_number",ignoreCase = true) ) {
                            if (mCitationLayout!![i].fields!![iOff].isRequired.nullSafety()) {
                                if (TextUtils.isEmpty(
                                        mAutoComTextViewLicNo!!.text.toString().trim()) &&
                                    TextUtils.isEmpty(
                                        mAutoComTextViewLicState!!.text.toString().trim()) &&
                                    TextUtils.isEmpty(
                                        mAutoComTextViewVin!!.text.toString().trim())) {
                                    //mAutoComTextViewVin.setError(getString(R.string.val_msg_please_enter_vin_number));
                                    mAutoComTextViewVin?.requestFocus()
                                    mAutoComTextViewVin?.isFocusableInTouchMode = true
                                    mAutoComTextViewVin?.isFocusable = true
                                    AppUtils.showKeyboard(this@AddTimeRecordActivity)
                                    LogUtil.printToastMSGForErrorWarning(
                                        applicationContext,
                                        getString(R.string.val_msg_please_enter_vin_number)
                                    )
                                    return false
                                }
                            }
                        }else if (mCitationLayout!![i].fields!![iOff].name.equals(
                                "make",ignoreCase = true)) {
                            if (mCitationLayout!![i].fields!![iOff].isRequired.nullSafety()) {
                                if (TextUtils.isEmpty(mAutoComTextViewMake?.text.toString().trim())
                                    || mSelectedMake == null || mSelectedMake!!.isEmpty()
                                ) {
                                    mAutoComTextViewMake?.requestFocus()
                                    mAutoComTextViewMake?.isFocusable = true
                                    AppUtils.showKeyboard(this@AddTimeRecordActivity)
                                    LogUtil.printToastMSGForErrorWarning(
                                        applicationContext,
                                        getString(R.string.val_msg_please_enter_make)
                                    )
                                    return false
                                }
                            }
                        } else if (mCitationLayout!![i].fields!![iOff].name.equals(
                                "model",
                                ignoreCase = true
                            )
                        ) {
                            if (mCitationLayout!![i].fields!![iOff].isRequired.nullSafety()) {
                                if (TextUtils.isEmpty(
                                        mAutoComTextViewModel?.text.toString().trim())) {
                                    if (mModelList != null && mModelList.size > 0) {
                                        mAutoComTextViewModel?.requestFocus()
                                        mAutoComTextViewModel?.isFocusable = true
                                        AppUtils.showKeyboard(this@AddTimeRecordActivity)
                                        LogUtil.printToastMSGForErrorWarning(
                                            applicationContext,
                                            getString(R.string.val_msg_please_enter_model)
                                        )
                                        return false
                                    }
                                }
                            }
                        }   else if (mCitationLayout!![i].fields!![iOff].name.equals("color",
                                ignoreCase = true)) {
                            if (mCitationLayout!![i].fields!![iOff].isRequired.nullSafety()) {
                                if (TextUtils.isEmpty(
                                        mAutoComTextViewColor?.text.toString().trim()
                                    ) || mSelectedColor == null || mSelectedColor!!.isEmpty()){
                                    mAutoComTextViewColor?.requestFocus()
                                    mAutoComTextViewColor?.isFocusable = true
                                    AppUtils.showKeyboard(this@AddTimeRecordActivity)
                                    LogUtil.printToastMSGForErrorWarning(
                                        applicationContext,
                                        getString(R.string.val_msg_please_enter_color)
                                    )
                                    return false
                                }
                            }
                        }  else if (mCitationLayout!![i].fields!![iOff].name.equals(
                                "time_limit_select",
                                ignoreCase = true
                            )
                        ) {
                            if (mCitationLayout!![i].fields!![iOff].isRequired.nullSafety()) {
                                if (TextUtils.isEmpty(
                                        mAutoComTextViewTimeLimit!!.text.toString()
                                            .trim()
                                    )
                                ) {
                                    //mAutoComTextViewTimeLimit.setError(getString(R.string.val_msg_please_enter_time_limit));
                                    mAutoComTextViewTimeLimit?.requestFocus()
                                    mAutoComTextViewTimeLimit?.isFocusableInTouchMode = true
                                    mAutoComTextViewTimeLimit?.isFocusable = true
                                    AppUtils.showKeyboard(this@AddTimeRecordActivity)
                                    LogUtil.printToastMSGForErrorWarning(
                                        applicationContext,
                                        getString(R.string.val_msg_please_enter_time_limit)
                                    )
                                    return false
                                }
                            }
                        } else if (mCitationLayout!![i].fields!![iOff].name.equals(
                                "tier_stem_left",ignoreCase = true)) {
                            if (mCitationLayout!![i].fields!![iOff].isRequired.nullSafety()) {
                                if(isTireStemWithImageView == false) {
                                    if (TextUtils.isEmpty(
                                                    mAutoComTextViewTierLeft!!.text.toString().trim())) {
                                        //mAutoComTextViewTierLeft.setError(getString(R.string.val_msg_please_enter_tier_stem_left));
                                        mAutoComTextViewTierLeft?.requestFocus()
                                        mAutoComTextViewTierLeft?.isFocusableInTouchMode = true
                                        mAutoComTextViewTierLeft?.isFocusable = true
                                        AppUtils.showKeyboard(this@AddTimeRecordActivity)
                                        LogUtil.printToastMSGForErrorWarning(applicationContext,
                                                getString(R.string.val_msg_please_enter_tier_stem_left)
                                        )
                                        return false
                                    }
                                }else if(mFrontTireStemValue!!.isEmpty()  ||
                                    (mFrontTireStemValue!!.isNotEmpty() && mFrontTireStemValue!!.toInt()<=0)){
                                    LogUtil.printToastMSGForErrorWarning(applicationContext,
                                            getString(R.string.val_msg_please_enter_tier_stem_left)
                                    )
                                    return false
                                }
                            }
                        } else if (mCitationLayout!![i].fields!![iOff].name.equals(
                                "tier_stem_right",ignoreCase = true)) {
                            if (mCitationLayout!![i].fields!![iOff].isRequired.nullSafety()) {
                                if(isTireStemWithImageView == false) {
                                    if (TextUtils.isEmpty(
                                                    mAutoComTextViewTierRight?.text.toString().trim())) {
                                        //mAutoComTextViewTierRight.setError(getString(R.string.val_msg_please_enter_tier_stem_right));
                                        mAutoComTextViewTierRight?.requestFocus()
                                        mAutoComTextViewTierRight?.isFocusableInTouchMode = true
                                        mAutoComTextViewTierRight?.isFocusable = true
                                        AppUtils.showKeyboard(this@AddTimeRecordActivity)
                                        LogUtil.printToastMSGForErrorWarning(applicationContext,
                                                getString(R.string.val_msg_please_enter_tier_stem_right)
                                        )
                                        return false
                                    }
                                }else if(mRearTireStemValue!!.isEmpty()  ||
                                    (mRearTireStemValue!!.isNotEmpty() && mRearTireStemValue!!.toInt()<=0)){
                                    LogUtil.printToastMSGForErrorWarning(applicationContext,
                                            getString(R.string.val_msg_please_enter_tier_stem_right)
                                    )
                                    return false
                                }
                            }
                        } else if (mCitationLayout!![i].fields!![iOff].name.equals(
                                "zone",ignoreCase = true)) {
                            if (mCitationLayout!![i].fields!![iOff].isRequired.nullSafety()) {
                                if (TextUtils.isEmpty(
                                        mAutoComTextViewZone?.text.toString().trim())) {
                                    //mAutoComTextViewZone.setError(getString(R.string.val_msg_please_enter_zone));
                                    mAutoComTextViewZone?.requestFocus()
                                    mAutoComTextViewZone?.isFocusableInTouchMode = true
                                    mAutoComTextViewZone?.isFocusable = true
                                    AppUtils.showKeyboard(this@AddTimeRecordActivity)
                                    LogUtil.printToastMSGForErrorWarning(
                                        applicationContext,
                                        getString(R.string.val_msg_please_enter_zone)
                                    )
                                    return false
                                }
                            }
                        } else if (mCitationLayout!![i].fields!![iOff].name.equals(
                                "remark", ignoreCase = true)) {
                            if (mCitationLayout!![i].fields!![iOff].isRequired.nullSafety()) {
                                if (TextUtils.isEmpty(
                                        mAutoComTextViewRemarks!!.text.toString().trim())) {
                                    //mAutoComTextViewRemarks.setError(getString(R.string.val_msg_please_enter_remark1));
                                    mAutoComTextViewRemarks?.requestFocus()
                                    mAutoComTextViewRemarks?.isFocusableInTouchMode = true
                                    mAutoComTextViewRemarks?.isFocusable = true
                                    AppUtils.showKeyboard(this@AddTimeRecordActivity)
                                    LogUtil.printToastMSGForErrorWarning(
                                        applicationContext,
                                        getString(R.string.val_msg_please_enter_remark1)
                                    )
                                    return false
                                }
                            }
                        }else if (mCitationLayout!![i].fields!![iOff].name.equals(
                                "remark_2", ignoreCase = true)) {
                            if (mCitationLayout!![i].fields!![iOff].isRequired.nullSafety()) {
                                if (TextUtils.isEmpty(
                                        mAutoComTextViewRemarks2!!.text.toString().trim())) {
                                    //mAutoComTextViewRemarks.setError(getString(R.string.val_msg_please_enter_remark1));
                                    mAutoComTextViewRemarks2?.requestFocus()
                                    mAutoComTextViewRemarks2?.isFocusableInTouchMode = true
                                    mAutoComTextViewRemarks2?.isFocusable = true
                                    AppUtils.showKeyboard(this@AddTimeRecordActivity)
                                    LogUtil.printToastMSGForErrorWarning(
                                        applicationContext,
                                        getString(R.string.val_msg_please_enter_remark1)
                                    )
                                    return false
                                }
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return true
        }
        return true
    }

    override fun onBackPressed() {
        if (backpressCloseDrawer()) {
            // close activity when drawer is closed
            super.onBackPressed()
        }
    }

    //save Timing Data form if offline
    private fun saveTimingDataForm(mResponse: AddTimingRequest) {
        class SaveTask : AsyncTask<Void?, Int?, String>() {
            override fun doInBackground(vararg voids: Void?): String? {
                try {
                    val model = AddTimingDatabaseModel()
                    model.lprState = mResponse.lprState
                    model.lprNumber = mResponse.lprNumber
                    model.meterNumber = mResponse.meterNumber
                    model.block = mResponse.block
                    model.regulationTime = mResponse.regulationTime
                    model.street = mResponse.street
                    model.side = mResponse.side
                    model.zone = mResponse.zone
                    model.remark = mResponse.remark
                    model.mStatus = mResponse.status
                    model.latitude = mResponse.latitude
                    model.longitiude = mResponse.longitiude
                    model.source = mResponse.source
                    model.officerName = mResponse.officerName
                    model.badgeId = mResponse.badgeId
                    model.shift = mResponse.shift
                    model.supervisor = mResponse.supervisor
                    model.markStartTimestamp = mResponse.markStartTimestamp
                    model.markIssueTimestamp = mResponse.markIssueTimestamp
                    model.formStatus = 1
                    model.mLocation = mResponse.mLocation
                    model.mMake = mResponse.mMake
                    model.mColor = mResponse.mColor
                    model.mModel = mResponse.mModel
                    model.mAddress = mResponse.mAddress
                    model.id = timingDataIDForTable
                    //model.imageUrls = mResponse.imageUrls

//                    Log.i("==>OfflineTiming:","Save=${ObjectMapperProvider.instance.writeValueAsString(model)}")

                    getMyDatabase()?.dbDAO?.insertTimingData(model)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                return "saved"
            }

            protected fun onPostExecute(result: CitationNumberDatabaseModel?) {
                //LogUtil.printToastMSG(mContext,"Booklet saved!");
            }
        }
        SaveTask().execute()
    }

    private fun saveTimingImagesOffline() {
        class SaveTask : AsyncTask<Void?, Int?, String>() {
            override fun doInBackground(vararg voids: Void?): String? {
                try {
                    bannerList?.forEach {
//                        Log.i("==>OfflineTiming:","SaveImage=${ObjectMapperProvider.instance.writeValueAsString(it)}")
                        getMyDatabase()?.dbDAO?.insertTimingImage(it!!)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                return "saved"
            }

            protected fun onPostExecute(result: CitationNumberDatabaseModel?) {
                //LogUtil.printToastMSG(mContext,"Booklet saved!");
            }
        }
        SaveTask().execute()
    }

    override fun onYesButtonClick() {}
    override fun onNoButtonClick() {}
    override fun onYesButtonClickParam(msg: String?) {}

    override fun onDestroy() {
        layTimingLayout?.removeAllViews()
        super.onDestroy()
    }

    companion object {
        private const val REQUEST_CAMERA = 0
        private const val PERMISSION_REQUEST_CODE = 2
    }

    private fun lockTimeData(isLoadFromPref: Boolean, addTimingRequest: AddTimingRequest?)
    {
        if (sharedPreference.read(SharedPrefKey.LOCK_GEO_ADDRESS, "").equals("unlock", true)) {
//            Toast.makeText(this@AddTimeRecordActivity,"lock",Toast.LENGTH_SHORT).show()

        }else{
            try {
//                Toast.makeText(this@AddTimeRecordActivity," UN lock",Toast.LENGTH_SHORT).show()
                if(isLoadFromPref)
                {
                    var mTimeData: AddTimingRequest? = sharedPreference.readTime(SharedPrefKey.TIMING_DATA, "")
                    if(mTimeData!=null)
                    {
//                        mAutoComTextViewTimeLimit?.setText(mTimeData!!.regulationTime!!.toString())
                        setDropdownRegulation(mTimeData!!.regulationTimeValue!!.toString())
//                        mAutoComTextViewTierLeft?.setText(mTimeData!!.mTireStemBack.toString())
//                        mAutoComTextViewTierRight?.setText(mTimeData!!.mTireStemFront.toString())
                        mAutoComTextViewDirection?.setText(mTimeData!!.side.toString())
                        if(mAutoComTextViewLocation!=null) {
                            mAutoComTextViewLocation?.setText(mTimeData!!.mLot.toString())
                        }
                        if(mAutoComTextViewMeter!=null) {
                            mAutoComTextViewMeter?.setText(mTimeData!!.meterNumber.toString())
                        }
                    }
                }else {
                    // when lock then getting call back here
                    sharedPreference.write(SharedPrefKey.TIMING_DATA, addTimingRequest)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun ShowTireStemDropDown(mValue:String): PopupWindow {
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.popup_category, null)
        val recyclerView = view.findViewById<RecyclerView>(R.id.rvCategory)

        ioScope.launch {
            val mApplicationList = Singleton.getDataSetList(DATASET_TIER_STEM_LIST, getMyDatabase())
            if (mApplicationList != null && mApplicationList!!.isNotEmpty()) {
//                val sortedList = mApplicationList.sortedWith(compareBy({ it.tierStem }, { it.tierStem }))
                val sortedList = mApplicationList.sortedBy { it.tierStem?.toIntOrNull() ?: 0 }
                recyclerView?.post {
                    val mTireStemAdapter = TireStemAdapter(this@AddTimeRecordActivity!!,sortedList,
                            object :TireStemAdapter.ListItemSelectListener {
                                override fun onItemClick(mObject: DatasetResponse?,position: Int) {

                                    if(mValue.equals("FRONT")) {
                                        mFrontTireStemValue = mObject!!.tierStem.toString()
                                        textFrontTireStem.text = mFrontTireStemText + " = " + mObject!!.tierStem
                                        setPositionOfStemValue(mObject!!.tierStem!!.toInt(),appCompatTextViewCircleStemValueFront)
                                        appCompatImageViewFrontTireStem.setImageResource(R.drawable.front_tire_red);
                                        appCompatTextViewCircleStemValueFront.text =  mObject!!.tierStem.toString()
                                    }else if(mValue.equals("REAR")) {
                                        mRearTireStemValue = mObject!!.tierStem.toString()
                                        textRearTireStem.text = mRearTireStemText + " = " + mObject!!.tierStem
                                        setPositionOfStemValue(mObject!!.tierStem!!.toInt(),appCompatTextViewCircleStemValueRear)
                                        appCompatImageViewRearTireStem.setImageResource(R.drawable.back_tire_red);
                                        appCompatTextViewCircleStemValueRear.text =  mObject!!.tierStem.toString()
                                    }else if(mValue.equals("VALVE")) {
                                        mValveTireStemValue = mObject!!.tierStem.toString()
                                        textRearValveStem.text = "Valve Stem" + " = " + mObject!!.tierStem
                                    }

                                    dismissPopup()
                                }
                            })

                    recyclerView.isNestedScrollingEnabled = false
                    recyclerView.setHasFixedSize(true)
                    recyclerView.layoutManager =
                            LinearLayoutManager(this@AddTimeRecordActivity, RecyclerView.VERTICAL, false)
                    recyclerView.adapter = mTireStemAdapter
                }
            }
        }

        return PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    private fun dismissPopup() {
        showTireStemDropDown?.let {
            if(it.isShowing){
                it.dismiss()
            }
            showTireStemDropDown = null
        }

    }



    override fun onResume() {
        super.onResume()

    }

    private fun setPositionOfStemValue(selectedValue:Int,appCompatTextViewStemValue:AppCompatTextView)
    {
        appCompatTextViewStemValue.visibility = View.VISIBLE
        when(selectedValue)
        {
            1,15-> {
                appCompatTextViewStemValue.x = (width*0.55f)
                appCompatTextViewStemValue.y = (10f)
            }
            2,30 -> {
                appCompatTextViewStemValue.x = (width*0.65f)
                appCompatTextViewStemValue.y = (height*0.2f)
            }
            3,45 -> {
                appCompatTextViewStemValue.x = (width*0.7f)
                appCompatTextViewStemValue.y = (height*0.38f)
            }
            4,60 -> {
                appCompatTextViewStemValue.x = (width*0.65f)
                appCompatTextViewStemValue.y = (height*0.6f)
            }
            5,75 -> {
                appCompatTextViewStemValue.x = (width*0.55f)
                appCompatTextViewStemValue.y = (height*0.72f)
            }
            6,90 -> {
                appCompatTextViewStemValue.x = (width*0.41f)
                appCompatTextViewStemValue.y = (height*0.8f)
            }
            7,105 -> {
                appCompatTextViewStemValue.x = (width*0.24f)
                appCompatTextViewStemValue.y = (height*0.72f)
            }
            8,120 -> {
                appCompatTextViewStemValue.x = (width*0.16f)
                appCompatTextViewStemValue.y = (height*0.6f)
            }
            9,135 -> {
                appCompatTextViewStemValue.x = (width*0.12f)
                appCompatTextViewStemValue.y = (height*0.4f)
            }
            10,150 -> {
                appCompatTextViewStemValue.x = (width*0.16f)
                appCompatTextViewStemValue.y = (height*0.2f)
            }
            11,165 -> {
                appCompatTextViewStemValue.x = (width*0.27f)
                appCompatTextViewStemValue.y = (height*0.1f)
            }
            12,180 -> {
                appCompatTextViewStemValue.x = (width*0.41f)
                appCompatTextViewStemValue.y = (height*0.06f)
            }

        }
    }

    private fun setImageTimeStampBasedOnSettingResponse(): Boolean {
        try {
           val settingsList = Singleton.getDataSetList(DATASET_SETTINGS_LIST, getMyDatabase())
            if (settingsList != null && settingsList!!.isNotEmpty()) {
                for (i in settingsList!!.indices) {
                    if (settingsList!![i].type.equals("IMAGE_TIMESTAMP", ignoreCase = true)
                        && settingsList!![i].mValue.equals("YES", ignoreCase = true)) {
                        return true
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }
}