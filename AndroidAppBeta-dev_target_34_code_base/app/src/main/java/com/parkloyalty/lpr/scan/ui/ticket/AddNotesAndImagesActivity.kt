package com.parkloyalty.lpr.scan.ui.ticket

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.PersistableBundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.parkloyalty.lpr.scan.R
import com.parkloyalty.lpr.scan.basecontrol.BaseActivity
import com.parkloyalty.lpr.scan.basecontrol.BaseApplication
import com.parkloyalty.lpr.scan.database.AppDatabase
import com.parkloyalty.lpr.scan.database.Singleton
import com.parkloyalty.lpr.scan.databinding.ActivityAddNotesAndImagesBinding
import com.parkloyalty.lpr.scan.extensions.nullSafety
import com.parkloyalty.lpr.scan.extensions.setButtonForShowHideOnKeyboard
import com.parkloyalty.lpr.scan.extensions.setViewForProperDrowDown
import com.parkloyalty.lpr.scan.interfaces.APIConstant
import com.parkloyalty.lpr.scan.interfaces.Constants
import com.parkloyalty.lpr.scan.interfaces.CustomDialogHelper
import com.parkloyalty.lpr.scan.network.ApiResponse
import com.parkloyalty.lpr.scan.network.DynamicAPIPath
import com.parkloyalty.lpr.scan.network.NetworkCheck.isInternetAvailable
import com.parkloyalty.lpr.scan.network.Status
import com.parkloyalty.lpr.scan.ui.check_setup.model.lpr_model.UploadImageViewModel
import com.parkloyalty.lpr.scan.ui.check_setup.model.lpr_model.UploadImagesResponse
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.CitationImagesModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DatasetResponse
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DropdownDatasetResponse
import com.parkloyalty.lpr.scan.ui.ticket.model.*
import com.parkloyalty.lpr.scan.util.AppUtils
import com.parkloyalty.lpr.scan.util.DATASET_NOTES_LIST
import com.parkloyalty.lpr.scan.util.DATASET_SETTINGS_LIST
import com.parkloyalty.lpr.scan.util.LogUtil
import com.parkloyalty.lpr.scan.util.PermissionUtils
import com.parkloyalty.lpr.scan.util.Util
import com.parkloyalty.lpr.scan.util.setAccessibilityForTextInputLayoutDropdownButtons
import com.parkloyalty.lpr.scan.utils.ObjectMapperProvider
import kotlinx.coroutines.async
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class AddNotesAndImagesActivity : BaseActivity(), CustomDialogHelper {

    //Start of UI Views
    var textInputEditTextNote: TextInputEditText? = null
    var appCompatImageViewMenu: AppCompatImageView? = null
    var mViewPagerBanner: ViewPager? = null
    var pagerIndicator: LinearLayoutCompat? = null
    var mEditTextViewTextViewNote1: AppCompatAutoCompleteTextView? = null
    var mEditTextViewTextViewNote2: AppCompatAutoCompleteTextView? = null
    var mEditTextViewTextViewNote3: AppCompatAutoCompleteTextView? = null
    var textInputLayoutNote1: TextInputLayout? = null
    var textInputLayoutNote2: TextInputLayout? = null
    var textInputLayoutNote3: TextInputLayout? = null
    var clParent: ConstraintLayout? = null
    //End of UI Views

    private var mContext: Context? = null
    private var mDb: AppDatabase? = null
    private var mBannerAdapter: ViewPagerBannerAddNoteAdapter? = null
    private var mShowBannerCount = 0
    private var mDotsCount = 0
    private var mDots: Array<ImageView?>? = null
    private var mTimer: Timer? = null
    private val mImageCount = 0
    private var picUri: Uri? = null
    private var tempUri: String? = null
    private val mTimingRecordValue = ""
    private var mTicketID: String? = null
    private var mTicketNumber: String? = null
    private val cameraImageLisrArray: MutableList<CitationImagesModel> = ArrayList()
    private val cameraImageLisrArrayForUpload: MutableList<CitationImagesModel> = ArrayList()
    private val sNote = StringBuilder()
    private val mImages: MutableList<String> = ArrayList()
    private var uploadImageSize = 0
    private val mNoteItem1: String? = null
    private val mNoteItem2: String? = null
    private val mNoteItem3: String? = null
    private var uploadImageCount = 0
    private var totalImageCount = 0
    private var imageArraysResponseGS: List<String>? = null
    private var isUploadLinkCalledOnce:Boolean=true

    private var isActivityInForeground = false

    private val addNotesViewModel: AddNotesViewModel? by viewModels()
    private val addImageViewModel: AddImageViewModel? by viewModels()
    private val getNotesViewModel: GetNotesViewModel? by viewModels()
    private val mUploadImageViewModel: UploadImageViewModel? by viewModels()
    private val mDownloadBitmapFIleViewModel: DownloadBitmapFIleViewModel? by viewModels()

    private  var settingsList: List<DatasetResponse>? = ArrayList()

    private lateinit var binding: ActivityAddNotesAndImagesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNotesAndImagesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        findViewsByViewBinding()
        setupClickListeners()

        setFullScreenUI()
        mContext = this@AddNotesAndImagesActivity

        addObservers()
        val directory = File(
            Environment.getExternalStorageDirectory().absolutePath,
            Constants.FILE_NAME + Constants.AddNote
        )
        deleteRecursive(directory)
        init()

        clParent.setButtonForShowHideOnKeyboard(findViewById<AppCompatButton>(R.id.btn_done))
        mEditTextViewTextViewNote1?.setViewForProperDrowDown()
        mEditTextViewTextViewNote2?.setViewForProperDrowDown()
        mEditTextViewTextViewNote3?.setViewForProperDrowDown()

        setCrossClearButton(context = this@AddNotesAndImagesActivity, textInputLayout = textInputLayoutNote1, appCompatAutoCompleteTextView = mEditTextViewTextViewNote1)
        setCrossClearButton(context = this@AddNotesAndImagesActivity, textInputLayout = textInputLayoutNote2, appCompatAutoCompleteTextView = mEditTextViewTextViewNote2)
        setCrossClearButton(context = this@AddNotesAndImagesActivity, textInputLayout = textInputLayoutNote3, appCompatAutoCompleteTextView = mEditTextViewTextViewNote3)

        setAccessibilityForComponents()
    }

    private fun findViewsByViewBinding(){
        textInputEditTextNote = binding.layoutContentAddNoteImages.etNote
        appCompatImageViewMenu = binding.layoutContentAddNoteImages.layoutDashboardHeader.imgOptions
        mViewPagerBanner = binding.layoutContentAddNoteImages.layoutContentBanner.pagerBanner
        pagerIndicator = binding.layoutContentAddNoteImages.layoutContentBanner.viewPagerCountDots
        mEditTextViewTextViewNote1 = binding.layoutContentAddNoteImages.AutoComTextViewNote1
        mEditTextViewTextViewNote2 = binding.layoutContentAddNoteImages.AutoComTextViewNote2
        mEditTextViewTextViewNote3 = binding.layoutContentAddNoteImages.AutoComTextViewNote3
        textInputLayoutNote1 = binding.layoutContentAddNoteImages.textInputLayoutNote1
        textInputLayoutNote2 = binding.layoutContentAddNoteImages.textInputLayoutNote2
        textInputLayoutNote3 = binding.layoutContentAddNoteImages.textInputLayoutNote3
        clParent = binding.clParent
    }

    private fun setupClickListeners(){
        binding.layoutContentAddNoteImages.btnDone.setOnClickListener {
            //if (isFormValid) {
            isUploadLinkCalledOnce = true
            callAddNotesApi()
        }

        binding.layoutContentAddNoteImages.ivCamera.setOnClickListener {
            requestPermission()
        }
    }

    private fun setAccessibilityForComponents() {
        setAccessibilityForTextInputLayoutDropdownButtons(this@AddNotesAndImagesActivity, textInputLayoutNote1)
        setAccessibilityForTextInputLayoutDropdownButtons(this@AddNotesAndImagesActivity, textInputLayoutNote2)
        setAccessibilityForTextInputLayoutDropdownButtons(this@AddNotesAndImagesActivity, textInputLayoutNote3)
    }

    private fun deleteRecursive(directory: File) {
        try {
            if (directory.exists()) {
                if (directory.isDirectory) for (child in directory.listFiles()) deleteRecursive(
                    child
                )
                directory.delete()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun init() {
        mContext = this@AddNotesAndImagesActivity
        mDb = BaseApplication.instance?.getAppDatabase()
        setToolbar()
        mViewPagerBanner!!.visibility = View.VISIBLE
        mBannerAdapter = ViewPagerBannerAddNoteAdapter(
            this@AddNotesAndImagesActivity,
            object : ViewPagerBannerAddNoteAdapter.ListItemSelectListener {
                override fun onItemClick(position: Int) {
//            mDb.getDbDAO().deleteTempImagesWithId(bannerList.get(position).getId());
//             setCameraImages();
                    try {
                        cameraImageLisrArrayForUpload.removeAt(cameraImageLisrArray.size - position - 1)
                        cameraImageLisrArray.removeAt(position)
                        showImagesBanner(cameraImageLisrArray)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            })
        if (intent != null && intent.hasExtra("TICEKTID")) {
            mTicketID = intent.getStringExtra("TICEKTID")
            mTicketNumber = intent.getStringExtra("booklet_id")
            uploadImageSize = intent.getIntExtra("image_size", 0)
            getNotesViewModel?.hitGetNotesAPI(mTicketID)
        }

        val notesDatasetResponseList = Singleton.getDataSetList(DATASET_NOTES_LIST, mDb)
        if (notesDatasetResponseList != null && notesDatasetResponseList.isNotEmpty()) {
            setDropdownNote1(notesDatasetResponseList)
            setDropdownNote2(notesDatasetResponseList)
            setDropdownNote3(notesDatasetResponseList)
            AppUtils.hideSoftKeyboard(this@AddNotesAndImagesActivity)
        }
    }


    private val addNotesResponseObserver = Observer { apiResponse: ApiResponse ->
        consumeResponse(apiResponse,
            DynamicAPIPath.GET_ADD_NOTES + "Add")
    }
    private val addImageResponseObserver = Observer { apiResponse: ApiResponse ->
        consumeResponse(apiResponse,
            DynamicAPIPath.POST_ADD_IMAGE + "Image")
    }
    private val getNotesResponseObserver = Observer { apiResponse: ApiResponse ->
        consumeResponse(apiResponse,
            DynamicAPIPath.GET_NOTES + "Get")
    }
    private val uploadImageResponseObserver = Observer { apiResponse: ApiResponse ->
        consumeResponse(apiResponse,
            DynamicAPIPath.POST_IMAGE+"AddNote")
    }
    private val downloadBitmapFileResponseObserver = Observer { apiResponse: ApiResponse ->
        consumeResponse(apiResponse,
            DynamicAPIPath.POST_DOWNLOAD_FILE)
    }

    private fun addObservers() {
        addNotesViewModel?.response?.observe(this@AddNotesAndImagesActivity, addNotesResponseObserver)
        addImageViewModel?.response?.observe(this@AddNotesAndImagesActivity, addImageResponseObserver)
        getNotesViewModel?.response?.observe(this@AddNotesAndImagesActivity, getNotesResponseObserver)
        mUploadImageViewModel?.response?.observe(this@AddNotesAndImagesActivity, uploadImageResponseObserver)
        mDownloadBitmapFIleViewModel?.response?.observe(this@AddNotesAndImagesActivity, downloadBitmapFileResponseObserver)
    }

    override fun removeObservers() {
        super.removeObservers()
        addNotesViewModel?.response?.removeObserver(addNotesResponseObserver)
        addImageViewModel?.response?.removeObserver(addImageResponseObserver)
        getNotesViewModel?.response?.removeObserver(getNotesResponseObserver)
        mUploadImageViewModel?.response?.removeObserver(uploadImageResponseObserver)
        mDownloadBitmapFIleViewModel?.response?.removeObserver(downloadBitmapFileResponseObserver)
    }

    //init toolbar navigation
    private fun setToolbar() {
        appCompatImageViewMenu?.visibility = View.GONE
        initToolbar(
            0,
            this@AddNotesAndImagesActivity,
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

    override fun onResume() {
        super.onResume()
        isActivityInForeground = true

    }
    override fun onPause() {
        super.onPause()
        isActivityInForeground = false
    }

    //request camera and storage prmission
    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (PermissionUtils.requestCameraAndStoragePermission(this@AddNotesAndImagesActivity)) {
                cameraIntent()
            }
        } else {
            cameraIntent()
        }
    }

    private fun cameraIntent() {
        val mIMageCount = AppUtils.maxImageCount("MAX_IMAGES")
        // mImageCount = mList.size();
        if (cameraImageLisrArrayForUpload.size < mIMageCount - 1) {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            picUri = getOutputPhotoFile() //Uri.fromFile(getOutputPhotoFile());
            //tempUri=picUri;
            intent.putExtra(MediaStore.EXTRA_OUTPUT, picUri)
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)

            //intent.putExtra("URI", picUri);
            startActivityForResult(intent, REQUEST_CAMERA)
            //.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        } else {
            val errorMsg =
                getString(R.string.msg_max_image).replace("#", mIMageCount.toString() + "")
            LogUtil.printToastMSG(applicationContext, getString(R.string.msg_max_image))
        }
    }

    private fun getOutputPhotoFile(): Uri? {
            val directory = File(Environment.getExternalStorageDirectory().absolutePath,
                Constants.FILE_NAME + Constants.AddNote)
            tempUri = directory.path
            if (!directory.exists()) {
                if (!directory.mkdirs()) {
                    Log.e("getOutputPhotoFile", "Failed to create storage directory.")
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
                path = FileProvider.getUriForFile(mContext!!, this@AddNotesAndImagesActivity.packageName + ".provider", file)
            } else {
                path = Uri.fromFile(File(directory.path + File.separator + "IMG_temp.jpg"))
            }
            return path
        }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CAMERA) {
            if (resultCode == RESULT_OK) {
                try {
                    val file = File("$tempUri/IMG_temp.jpg")
                    var mImgaeBitmap: Bitmap? = null
                    try {
                        val options = BitmapFactory.Options()
                        options.inSampleSize = 4
                        options.inJustDecodeBounds = true
                        BitmapFactory.decodeFile(file.absolutePath, options)

                        // Calculate inSampleSize
                        options.inSampleSize = Util.calculateInSampleSize(options, 300, 300)

                        // Decode bitmap with inSampleSize set
                        options.inJustDecodeBounds = false
                        // /storage/emulated/0/ParkLoyalty/CameraImages/IMG_temp.jpg
                        // /external_files/ParkLoyalty/CameraImages/IMG_temp.jpg
                        // content://com.fiveexceptions.lpr.scan.provider/external_files/ParkLoyalty/CameraImages/IMG_temp.jpg
                        val scaledBitmap = BitmapFactory.decodeFile(file.absolutePath, options)

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
                        mImgaeBitmap = Bitmap.createBitmap(scaledBitmap!!, 0, 0,
                            scaledBitmap.width, scaledBitmap.height, matrix, true)


                        //passing bitmap for converting it to base64
                        //mImageViewNumberPlate.setImageBitmap(mImgaeBitmap);
//                        val timeStampBitmap = AppUtils.timestampItAndSave(mImgaeBitmap);
//                        SaveImageMM(timeStampBitmap)

                            mainScope.async {
//                                LogUtil.printToastMSG(mContext,"orientation bitmap save")
                                if(setImageTimeStampBasedOnSettingResponse()) {
                                    val timeStampBitmap = AppUtils.timestampItAndSave(mImgaeBitmap);
                                    SaveImageMM(timeStampBitmap)
                                }else{
                                    SaveImageMM(mImgaeBitmap)
                                }
//
//                                requestPermission()
                            }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

//                    CropImage.activity(picUri).start(this);
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    //TODO will add comments later
    private fun SaveImageMM(finalBitmap: Bitmap?) {
        if (finalBitmap == null || !isActivityInForeground) {
            LogUtil.printLog("SaveImageMM", "Bitmap is null. Skipping save.")
            LogUtil.printToastMSG(this@AddNotesAndImagesActivity,getString(R.string.wrn_lbl_capture_image))
            return
        }

        if (Environment.getExternalStorageState() != Environment.MEDIA_MOUNTED) {
            LogUtil.printLog("SaveImageMM", "External storage not mounted.")
            return
        }

        val myDir = File(
            Environment.getExternalStorageDirectory().absolutePath,
            Constants.FILE_NAME + Constants.AddNote)
        myDir.mkdirs()
        val timeStamp = SimpleDateFormat("yyyMMdd_HHmmss", Locale.US).format(Date())
        val fname = "Image_" + timeStamp + "_capture.jpg"
        val file = File(myDir, fname)
        if (file.exists()) file.delete()
        try {
            //new ImageCompression(this,file.getAbsolutePath()).execute(finalBitmap);
            val out = FileOutputStream(file)
            //finalBitmap = Bitmap.createScaledBitmap(finalBitmap,(int)1080/2,(int)1920/2, true);
            val isSuccess = finalBitmap!!.compress(Bitmap.CompressFormat.JPEG, 30, out) //less than 300 kb
            out.flush()
            out.close()
            if (!isSuccess || file.length() == 0L) {
                LogUtil.printLog("SaveImageMM", "Compression failed or file is empty.")
                file.delete()
                return
            }

            val oldFname = "IMG_temp.jpg"
            val oldFile = File(myDir, oldFname)
            if (oldFile.exists()) oldFile.delete()
            val id = SimpleDateFormat("HHmmss", Locale.US).format(Date())
            val pathDb = file.path
            val mImage = CitationImagesModel()
            mImage.citationImage = pathDb
            mImage.status = 0
            mImage.id = id.toInt()
            cameraImageLisrArray.add(mImage)
            cameraImageLisrArrayForUpload.add(mImage)
            mBannerAdapter?.setAnimalBannerList(cameraImageLisrArray)
            if (cameraImageLisrArray != null) {
                setUiPageViewController(mBannerAdapter!!.count)
            }
            mBannerAdapter?.notifyDataSetChanged()
//            showImagesBanner(cameraImageLisrArray)
            //            mDb.getDbDAO().insertCitationImage(mImage);
            //set image list adapter
            //setImages();

            finalBitmap.recycle()
        } catch (e: Exception) {
            e.printStackTrace()
            LogUtil.printToastMSG(this@AddNotesAndImagesActivity,getString(R.string.wrn_lbl_capture_image))

        }
    }

    //show banner images
    private fun showImagesBanner(mList: List<CitationImagesModel>?) {

        //if (mBannerAdapter == null) {
//        Glide.with(context)
//                .load("http://via.placeholder.com/300.png")
//                .placeholder(R.drawable.placeholder)
//                .error(R.drawable.imagenotfound)
//                .into(ivImg);
        mBannerAdapter?.setAnimalBannerList(mList)
        mViewPagerBanner?.adapter = mBannerAdapter
        mViewPagerBanner?.currentItem = mShowBannerCount
        mViewPagerBanner?.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                if (mList == null || mList.size == 0) {
                    // Log.e("length--",""+animalInfo.getImageList().size());
                    return
                }
                try {
                    for (i in mList.indices) {
                        mDots?.get(i)?.setImageDrawable(resources.getDrawable(R.drawable.ic_pager_unselected_dot))
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                try {
                    mDots?.get(position)?.setImageDrawable(resources.getDrawable(R.drawable.ic_pager_selected_dot))
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
        if (mList != null) {
            setUiPageViewController(mBannerAdapter!!.count)
        }
    }

    //managing view pager ui
    protected fun setUiPageViewController(count: Int) {
        try {
            mDotsCount = count
            mDots = arrayOfNulls(mDotsCount)
            pagerIndicator!!.removeAllViews()
            for (i in 0 until mDotsCount) {
                mDots!![i] = ImageView(this@AddNotesAndImagesActivity)
                mDots!![i]?.setImageDrawable(resources.getDrawable(R.drawable.ic_pager_unselected_dot))
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                mDots!![i]!!.setPadding(8, 0, 8, 0)
                params.setMargins(4, 0, 4, 0)
                pagerIndicator!!.addView(mDots!![i], params)
            }
            if (mShowBannerCount == 0) {
                mShowBannerCount += 1
            }
            mDots!![mShowBannerCount - 1]!!.setImageDrawable(resources.getDrawable(R.drawable.ic_pager_selected_dot))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //set value to Body Style dropdown
    private fun setDropdownNote1(mApplicationList: List<DatasetResponse>?) {
//        AppUtils.hideSoftKeyboard(this@AddNotesAndImagesActivity)
        val pos = 0
        if (mApplicationList != null && mApplicationList.size > 0) {
            val mDropdownList = arrayOfNulls<String>(mApplicationList.size)
            for (i in mApplicationList.indices) {
                mDropdownList[i] = mApplicationList[i].note.toString()
                //                try {
//                    if (mNoteItem1 != null) {
//                        if (mApplicationList.get(i).getNote().equalsIgnoreCase(mNoteItem1)) {
//                            pos = i;
//                            try {
//                                mEditTextViewTextViewNote1.setText(mDropdownList[pos]);
//                            } catch (Exception e) {
//                            }
//                        }
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
            }
            Arrays.sort(mDropdownList)
            val adapter = ArrayAdapter(this@AddNotesAndImagesActivity,
                R.layout.row_dropdown_menu_popup_item, mDropdownList)

            try {
                mEditTextViewTextViewNote1!!.threshold = 1
                mEditTextViewTextViewNote1!!.setAdapter<ArrayAdapter<String?>>(adapter)
                //mSelectedShiftStat = mApplicationList.get(pos);
                mEditTextViewTextViewNote1!!.onItemClickListener =
                    OnItemClickListener { parent, view, position, id -> // mSelectedShiftStat = mApplicationList.get(position);
                        AppUtils.hideSoftKeyboard(this@AddNotesAndImagesActivity)
                    }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
        }
    }

    //set value to Body Style dropdown
    private fun setDropdownNote2(mApplicationList: List<DatasetResponse>?) {
//        AppUtils.hideSoftKeyboard(this@AddNotesAndImagesActivity)
        val pos = 0
        if (mApplicationList != null && mApplicationList.size > 0) {
            val mDropdownList = arrayOfNulls<String>(mApplicationList.size)
            for (i in mApplicationList.indices) {
                mDropdownList[i] = mApplicationList[i].note.toString()
                //                try {
//                    if (mNoteItem2 != null) {
//                        if (mApplicationList.get(i).getNote().equalsIgnoreCase(mNoteItem2)) {
//                            pos = i;
//                            try {
//                                mEditTextViewTextViewNote2.setText(mDropdownList[pos]);
//                            } catch (Exception e) {
//                            }
//                        }
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
            }
            Arrays.sort(mDropdownList)
            val adapter = ArrayAdapter(this@AddNotesAndImagesActivity,
                R.layout.row_dropdown_menu_popup_item, mDropdownList)
            try {
                mEditTextViewTextViewNote2!!.threshold = 1
                mEditTextViewTextViewNote2!!.setAdapter<ArrayAdapter<String?>>(adapter)
                //mSelectedShiftStat = mApplicationList.get(pos);
                mEditTextViewTextViewNote2!!.onItemClickListener =
                    OnItemClickListener { parent, view, position, id -> // mSelectedShiftStat = mApplicationList.get(position);
                        AppUtils.hideSoftKeyboard(this@AddNotesAndImagesActivity)
                    }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
        }
    }

    //set value to Body Style dropdown
    private fun setDropdownNote3(mApplicationList: List<DatasetResponse>?) {
//        AppUtils.hideSoftKeyboard(this@AddNotesAndImagesActivity)
        val pos = 0
        if (mApplicationList != null && mApplicationList.size > 0) {
            val mDropdownList = arrayOfNulls<String>(mApplicationList.size)
            for (i in mApplicationList.indices) {
                mDropdownList[i] = mApplicationList[i].note.toString()
                //                try {
//                    if (mNoteItem3 != null) {
//                        if (mApplicationList.get(i).getNote().equalsIgnoreCase(mNoteItem3)) {
//                            pos = i;
//                            try {
//                                mEditTextViewTextViewNote3.setText(mDropdownList[pos]);
//                            } catch (Exception e) {
//                            }
//                        }
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
            }
            Arrays.sort(mDropdownList)
            val adapter = ArrayAdapter(this@AddNotesAndImagesActivity,
                R.layout.row_dropdown_menu_popup_item, mDropdownList)
            try {
                mEditTextViewTextViewNote3?.threshold = 1
                mEditTextViewTextViewNote3?.setAdapter<ArrayAdapter<String?>>(adapter)
                //mSelectedShiftStat = mApplicationList.get(pos);
                mEditTextViewTextViewNote3?.onItemClickListener =
                    OnItemClickListener { parent, view, position, id -> // mSelectedShiftStat = mApplicationList.get(position);
                        AppUtils.hideSoftKeyboard(this@AddNotesAndImagesActivity)
                    }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
        }
    }

    //             if (TextUtils.isEmpty(mEditTextViewTextViewNote3.getText().toString().trim())) {
    //                 return false;
//             }
    private fun isFormValid(): Boolean{
            try {
                if (TextUtils.isEmpty(mEditTextViewTextViewNote3?.getText().toString().trim())) {
                    mEditTextViewTextViewNote3?.requestFocus()
                    mEditTextViewTextViewNote3?.isFocusableInTouchMode = true
                    mEditTextViewTextViewNote3?.isFocusable = true
                    AppUtils.showKeyboard(this@AddNotesAndImagesActivity)
                    LogUtil.printToastMSGForErrorWarning(applicationContext,
                            getString(R.string.val_msg_please_enter_note))
                    return false;
                }
            } catch (e: Exception) {
                return true
            }
            return true
        }

    /* Call Api For update profile */
    private fun callUploadImagesUrl() {
        if (isInternetAvailable(this@AddNotesAndImagesActivity)) {
            val endPoint = "$mTicketID/images"
            val addImageRequest = AddImageRequest()
            addImageRequest.images = mImages
            addImageViewModel!!.hitAddImagesApi(addImageRequest, endPoint)
        } else {
            LogUtil.printToastMSG(applicationContext,
                getString(R.string.err_msg_connection_was_refused))
        }
    }

    /* Call Api For update profile */
    private fun callUploadImages(file: File?, num: Int) {
        if (isInternetAvailable(this@AddNotesAndImagesActivity)) {
            val requestFile = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file!!)
            val files = MultipartBody.Part.createFormData(
                "files",
                if (file != null) file.name else "",
                requestFile
            )
            val mDropdownList = arrayOf(mTicketNumber + "_" + (num + totalImageCount))
            val mRequestBodyType =
                RequestBody.create("text/plain".toMediaTypeOrNull(), "CitationImages")
            mUploadImageViewModel?.hitUploadImagesApi(mDropdownList, mRequestBodyType, files)
        } else {
            LogUtil.printToastMSG(applicationContext,
                getString(R.string.err_msg_connection_was_refused))
        }
    }

    /* Call Api For Update Mark */
    private fun callAddNotesApi() {
        try {
            if (isInternetAvailable(this@AddNotesAndImagesActivity)) {
                 if (!TextUtils.isEmpty(mEditTextViewTextViewNote1?.text)
                    || !TextUtils.isEmpty(mEditTextViewTextViewNote2?.text)
                    || !TextUtils.isEmpty(mEditTextViewTextViewNote3?.text)) {
                    val addNoteRequest = AddNoteRequest()
                    addNoteRequest.mNote1=mEditTextViewTextViewNote1?.text.toString()
                    addNoteRequest.mNote2=mEditTextViewTextViewNote2?.text.toString()
                    addNoteRequest.mNote3=mEditTextViewTextViewNote3?.text.toString()
                    val endPoint = "$mTicketID/note"
                    addNotesViewModel?.hitAddNotesAPI(endPoint, addNoteRequest)
                } else if (cameraImageLisrArrayForUpload.size > 0) {
                    callUploadImages(
                        File(cameraImageLisrArrayForUpload[uploadImageCount].citationImage),
                        uploadImageCount)
                }
            } else {
                LogUtil.printToastMSG(
                    applicationContext,
                    getString(R.string.err_msg_connection_was_refused)
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /* Call Api For Ticket Cancel */
    private fun callDownloadBitmapApi() {
        if (isInternetAvailable(this@AddNotesAndImagesActivity)) {
            if (imageArraysResponseGS != null && imageArraysResponseGS!!.size > 0) {
                val downloadBitmapRequest = DownloadBitmapRequest()
                downloadBitmapRequest.downloadType = "CitationImages"
                val links = Links()
                if (imageArraysResponseGS!!.size > 0) links.img1 = imageArraysResponseGS!![0]
                if (imageArraysResponseGS!!.size > 1) links.img2 = imageArraysResponseGS!![1]
                if (imageArraysResponseGS!!.size > 2) links.img3 = imageArraysResponseGS!![2]
                if (imageArraysResponseGS!!.size > 3) links.img4 = imageArraysResponseGS!![3]
                if (imageArraysResponseGS!!.size > 4) links.img5 = imageArraysResponseGS!![4]
                if (imageArraysResponseGS!!.size > 5) links.img6 = imageArraysResponseGS!![5]
                if (imageArraysResponseGS!!.size > 6) links.img7 = imageArraysResponseGS!![6]
                if (imageArraysResponseGS!!.size > 7) links.img8 = imageArraysResponseGS!![7]
                if (imageArraysResponseGS!!.size > 8) links.img9 = imageArraysResponseGS!![8]
                if (imageArraysResponseGS!!.size > 9) links.img10 = imageArraysResponseGS!![9]
                if (imageArraysResponseGS!!.size > 10) links.img11 = imageArraysResponseGS!![10]
                if (imageArraysResponseGS!!.size > 11) links.img12 = imageArraysResponseGS!![11]
                downloadBitmapRequest.links = links
                mDownloadBitmapFIleViewModel?.downloadBitmapAPI(downloadBitmapRequest)
            } else {
                LogUtil.printToastMSG(
                    applicationContext,
                    getString(R.string.err_msg_down_load_image)
                )
            }
        } else {
            LogUtil.printToastMSG(
                applicationContext,
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
                        if (tag.equals(DynamicAPIPath.GET_ADD_NOTES + "Add", ignoreCase = true)) {

                            val responseModel = ObjectMapperProvider.fromJson(apiResponse.data.toString(), AddNotesResponse::class.java)

                            if (responseModel != null && responseModel.isSuccess) {
                                if (cameraImageLisrArrayForUpload.size > 0) {
                                    callUploadImages(
                                        File(cameraImageLisrArrayForUpload[uploadImageCount].citationImage),
                                        uploadImageCount)
                                } else {
                                    AppUtils.showCustomAlertDialog(
                                        this@AddNotesAndImagesActivity,
                                        APIConstant.ADD_NOTE,
                                        responseModel.message,
                                        getString(R.string.alt_lbl_OK),
                                        getString(R.string.scr_btn_cancel),
                                        this@AddNotesAndImagesActivity
                                    )
                                }
                            } else {
                                AppUtils.showCustomAlertDialog(
                                    this@AddNotesAndImagesActivity,
                                    APIConstant.ADD_NOTE,
                                    getString(R.string.err_msg_something_went_wrong),
                                    getString(R.string.alt_lbl_OK),
                                    getString(R.string.scr_btn_cancel),
                                    this@AddNotesAndImagesActivity
                                )
                                dismissLoader()
                            }
                            dismissLoader()
                        } else if (tag.equals(
                                DynamicAPIPath.POST_ADD_IMAGE + "Image",
                                ignoreCase = true
                            )
                        ) {
                            dismissLoader()


                            val responseModel = ObjectMapperProvider.fromJson(apiResponse.data.toString(), AddNotesResponse::class.java)

                            if (responseModel != null && responseModel.isSuccess) {
                                AppUtils.showCustomAlertDialog(
                                    this@AddNotesAndImagesActivity,
                                    APIConstant.UPLOAD_IMAGE,
                                    if (responseModel.message != null) responseModel.message else getString(
                                        R.string.err_msg_something_went_wrong
                                    ),
                                    getString(R.string.alt_lbl_OK),
                                    getString(R.string.scr_btn_cancel),
                                    this@AddNotesAndImagesActivity
                                )
                            } else {
                                AppUtils.showCustomAlertDialog(
                                    this@AddNotesAndImagesActivity,
                                    APIConstant.UPLOAD_IMAGE,
                                    if (responseModel!!.message != null) responseModel.message else getString(
                                        R.string.err_msg_something_went_wrong
                                    ),
                                    getString(R.string.alt_lbl_OK),
                                    getString(R.string.scr_btn_cancel),
                                    this@AddNotesAndImagesActivity
                                )
                            }
                        } else if (tag.equals(DynamicAPIPath.GET_NOTES + "Get",
                                ignoreCase = true)) {


                            val responseModel = ObjectMapperProvider.fromJson(apiResponse.data.toString(), GetNotesResponse::class.java)

                            if (responseModel != null && responseModel.isSuccess) {
//                                if(responseModel.getDataNote()!=null && responseModel.getDataNote().getNotes()!=null) {
//                                    for(int i=0; i<responseModel.getDataNote().getNotes().size();i++) {
//                                        sNote.append(responseModel.getDataNote().getNotes().get(i).getNote());
//                                        textInputEditTextNote.setText(sNote);
//                                    }
                                textInputEditTextNote!!.setSelection(textInputEditTextNote!!.length())
                                if (responseModel.dataNote?.images != null &&
                                    responseModel.dataNote?.images!!.size > 0
                                ) {
                                    try {
                                        imageArraysResponseGS = responseModel.dataNote!!.images
                                        totalImageCount = responseModel.dataNote!!.images!!.size
                                        callDownloadBitmapApi()
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                }
                                if (responseModel.dataNote!!.commentDetailsNote != null) {
                                    try {
                                        mEditTextViewTextViewNote1?.setText(if (responseModel.dataNote?.commentDetailsNote?.note1 != null) responseModel.dataNote?.commentDetailsNote?.note1 else "")
                                        mEditTextViewTextViewNote2?.setText(if (responseModel.dataNote?.commentDetailsNote?.note2 != null) responseModel.dataNote?.commentDetailsNote?.note2 else "")
                                        mEditTextViewTextViewNote3?.setText(if (responseModel.dataNote?.commentDetailsNote?.note3 != null) responseModel.dataNote?.commentDetailsNote?.note3 else "")
                                        mEditTextViewTextViewNote1!!.setSelection(mEditTextViewTextViewNote1!!.length());
                                        mEditTextViewTextViewNote2!!.setSelection(mEditTextViewTextViewNote2!!.length());
                                        mEditTextViewTextViewNote3!!.setSelection(mEditTextViewTextViewNote3!!.length());
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                }
                                //                                }
                            }
                            dismissLoader()
                        }
                        if (tag.equals(DynamicAPIPath.POST_IMAGE+"AddNote", ignoreCase = true)) {
                            try {

                                val responseModel = ObjectMapperProvider.fromJson(apiResponse.data.toString(), UploadImagesResponse::class.java)

                                if (responseModel != null && responseModel.status.nullSafety()) {
                                    if (responseModel.data != null && responseModel.data?.size!! > 0 && responseModel.data!![0].response != null && responseModel.data!![0].response?.links != null && responseModel.data!![0].response?.links?.size!! > 0) {
//                                        mImages.clear()
                                        mImages.add(responseModel.data!![0].response?.links!![0])
                                        uploadImageCount++
                                        if (uploadImageCount < cameraImageLisrArrayForUpload.size) {
                                            callUploadImages(
                                                File(
                                                    cameraImageLisrArrayForUpload[uploadImageCount].citationImage
                                                ), uploadImageCount
                                            )
                                        }
                                        if (uploadImageCount >= cameraImageLisrArrayForUpload.size && isUploadLinkCalledOnce) {
                                            isUploadLinkCalledOnce = false
                                            callUploadImagesUrl()
                                        }
                                    } else {
                                        AppUtils.showCustomAlertDialog(
                                            this@AddNotesAndImagesActivity,
                                            APIConstant.POST_IMAGE,
                                            getString(R.string.err_msg_something_went_wrong_imagearray),
                                            getString(R.string.alt_lbl_OK),
                                            getString(R.string.scr_btn_cancel),
                                            this@AddNotesAndImagesActivity
                                        )
                                    }
                                    //LogUtil.printToastMSG(LprPreviewActivity.this, responseModel.getMessage());
                                } else {
                                    dismissLoader()
                                    AppUtils.showCustomAlertDialog(
                                        this@AddNotesAndImagesActivity,
                                        APIConstant.POST_IMAGE,
                                        getString(R.string.err_msg_something_went_wrong),
                                        getString(R.string.alt_lbl_OK),
                                        getString(R.string.scr_btn_cancel),
                                        this@AddNotesAndImagesActivity
                                    )
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        } else if (tag.equals(
                                DynamicAPIPath.POST_DOWNLOAD_FILE,
                                ignoreCase = true
                            )
                        ) {
                            val responseModel = ObjectMapperProvider.fromJson(apiResponse.data.toString(), DownloadBitmapResponse::class.java)

                            if (responseModel != null && responseModel.isStatus) {
                                if (responseModel.metadata!![0].url!!.length > 0) {
//                                     new TicketDetailsActivity.DownloadingPrintBitmapFromUrl().execute(responseModel.getMetadata().
//                                             get(0).getUrl());
                                    setServeImageOnUI(responseModel.metadata)
                                }
                            }
                            dismissLoader()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        //token expires
                        dismissLoader()
                    }
                }
            }
            Status.ERROR -> {
                dismissLoader()
                LogUtil.printToastMSG(
                    applicationContext,
                    getString(R.string.err_msg_error)
                )
            }

            else -> {}
        }
    }

    private fun setServeImageOnUI(metadataItems: List<MetadataItem>?) {
        for (metadata in metadataItems!!) {
            val id = SimpleDateFormat("HHmmss", Locale.US).format(Date())
            val model = CitationImagesModel()
            model.status = 1
            model.id = id.toInt()
            model.citationImage = metadata.url
            cameraImageLisrArray.add(model)
        }
        showImagesBanner(cameraImageLisrArray)
    }

    override fun onYesButtonClick() {
        finish()
    }

    override fun onNoButtonClick() {}
    override fun onYesButtonClickParam(msg: String?) {
//        if(msg.equals(APIConstant.ADD_NOTE)) {
            finish()
//        }
    }

    override fun onDestroy() {
        mContext = null
        super.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
    }


    private fun setImageTimeStampBasedOnSettingResponse(): Boolean {
        try {
            settingsList = Singleton.getDataSetList(DATASET_SETTINGS_LIST, mDb)
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

    companion object {
        private const val REQUEST_CAMERA = 0
        private const val PERMISSION_REQUEST_CODE = 2
    }
}