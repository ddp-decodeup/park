package com.parkloyalty.lpr.scan.views.fragments.addtimerecord

import DialogUtil
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Environment
import android.text.InputFilter
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.PopupWindow
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.JsonNode
import com.google.android.material.textfield.TextInputLayout
import com.parkloyalty.lpr.scan.BuildConfig
import com.parkloyalty.lpr.scan.R
import com.parkloyalty.lpr.scan.database.AppDatabase
import com.parkloyalty.lpr.scan.databinding.FragmentAddTimeRecordScreenBinding
import com.parkloyalty.lpr.scan.extensions.getErrorMessage
import com.parkloyalty.lpr.scan.extensions.getIndexOfLocation
import com.parkloyalty.lpr.scan.extensions.getIndexOfRegulation
import com.parkloyalty.lpr.scan.extensions.hideSoftKeyboard
import com.parkloyalty.lpr.scan.extensions.hideView
import com.parkloyalty.lpr.scan.extensions.isInternetAvailable
import com.parkloyalty.lpr.scan.extensions.logD
import com.parkloyalty.lpr.scan.extensions.nav
import com.parkloyalty.lpr.scan.extensions.nullSafety
import com.parkloyalty.lpr.scan.extensions.safeNavigate
import com.parkloyalty.lpr.scan.extensions.setListOnlyDropDown
import com.parkloyalty.lpr.scan.extensions.showErrorWithShake
import com.parkloyalty.lpr.scan.extensions.showView
import com.parkloyalty.lpr.scan.extensions.toast
import com.parkloyalty.lpr.scan.interfaces.Constants
import com.parkloyalty.lpr.scan.interfaces.SharedPrefKey
import com.parkloyalty.lpr.scan.network.response_handler.NewApiResponse
import com.parkloyalty.lpr.scan.ui.check_setup.adapter.TimingViewPagerBannerAdapter
import com.parkloyalty.lpr.scan.ui.check_setup.adapter.TireStemAdapter
import com.parkloyalty.lpr.scan.ui.check_setup.model.lpr_model.UploadImagesResponse
import com.parkloyalty.lpr.scan.ui.check_setup.model.timing.AddTimingDatabaseModel
import com.parkloyalty.lpr.scan.ui.check_setup.model.timing.AddTimingRequest
import com.parkloyalty.lpr.scan.ui.check_setup.model.timing.AddTimingResponse
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.CitationLayoutData
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DatasetResponse
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.TimingImagesModel
import com.parkloyalty.lpr.scan.ui.login.model.WelcomeForm
import com.parkloyalty.lpr.scan.util.API_CONSTANT_UPLOAD_TYPE_TIMING_IMAGES
import com.parkloyalty.lpr.scan.util.AppUtils
import com.parkloyalty.lpr.scan.util.FileUtil
import com.parkloyalty.lpr.scan.util.SDF_IMAGE_ID_TIMESTAMP
import com.parkloyalty.lpr.scan.util.SDF_IMAGE_TIMESTAMP
import com.parkloyalty.lpr.scan.util.SHOW_DELETE_BUTTON
import com.parkloyalty.lpr.scan.util.SharedPref
import com.parkloyalty.lpr.scan.util.Util
import com.parkloyalty.lpr.scan.utils.AlertDialogUtils
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_ADD_TIMING
import com.parkloyalty.lpr.scan.utils.ApiConstants.API_TAG_NAME_UPLOAD_IMAGES
import com.parkloyalty.lpr.scan.utils.AppConstants.TEMP_IMAGE_FILE_NAME
import com.parkloyalty.lpr.scan.utils.CitationFormUtils.CITATION_FORM_FIELD_TYPE_LIST_ONLY
import com.parkloyalty.lpr.scan.utils.NewConstructLayoutBuilder
import com.parkloyalty.lpr.scan.utils.ObjectMapperProvider
import com.parkloyalty.lpr.scan.utils.SettingsUtils
import com.parkloyalty.lpr.scan.utils.ViewPagerUtils
import com.parkloyalty.lpr.scan.utils.camerahelper.CameraHelper
import com.parkloyalty.lpr.scan.utils.permissions.PermissionManager
import com.parkloyalty.lpr.scan.utils.permissions.PermissionUtils.getCameraPermission
import com.parkloyalty.lpr.scan.views.MainActivityAction
import com.parkloyalty.lpr.scan.views.MainActivityViewModel
import com.parkloyalty.lpr.scan.views.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import java.text.DecimalFormat
import java.util.Date
import javax.inject.Inject

@AndroidEntryPoint
class AddTimeRecordScreenFragment : BaseFragment<FragmentAddTimeRecordScreenBinding>() {
    private val mainActivityViewModel: MainActivityViewModel by activityViewModels()

    //Can be used later for settings specific logic
    private val addTimeRecordScreenViewModel: AddTimeRecordScreenViewModel by viewModels()

    @Inject
    lateinit var sharedPreference: SharedPref

    @Inject
    lateinit var appDatabase: AppDatabase

    @Inject
    lateinit var settingsUtils: SettingsUtils

    @Inject
    lateinit var permissionFactory: PermissionManager.Factory

    private lateinit var permissionManager: PermissionManager

    @Inject
    lateinit var constructLayoutBuilder: NewConstructLayoutBuilder

    @Inject
    lateinit var cameraHelper: CameraHelper

    private var session: CameraHelper.Session? = null


    var mBtnSubmit: AppCompatButton? = null
    var layTimingLayout: LinearLayoutCompat? = null
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

    private val mModelList: MutableList<DatasetResponse> = ArrayList()
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

    private var textInputLayoutMeter: TextInputLayout? = null
    private var textInputLayoutLocation: TextInputLayout? = null
    private var textInputLayoutBlock: TextInputLayout? = null
    private var textInputLayoutStreet: TextInputLayout? = null
    private var textInputLayoutDirection: TextInputLayout? = null
    private var textInputLayoutLicNo: TextInputLayout? = null
    private var textInputLayoutLicState: TextInputLayout? = null
    private var textInputLayoutVin: TextInputLayout? = null
    private var textInputLayoutTimeLimit: TextInputLayout? = null
    private var textInputLayoutTierLeft: TextInputLayout? = null
    private var textInputLayoutTierRight: TextInputLayout? = null
    private var textInputLayoutZone: TextInputLayout? = null
    private var textInputLayoutPBCZone: TextInputLayout? = null
    private var textInputLayoutRemarks: TextInputLayout? = null
    private var textInputLayoutRemarks2: TextInputLayout? = null
    private var textInputLayoutColor: TextInputLayout? = null
    private var textInputLayoutMake: TextInputLayout? = null
    private var textInputLayoutModel: TextInputLayout? = null

    private var latestLayoutTimings = 0
    private val name: Array<AppCompatAutoCompleteTextView>? = null
    private val textInputLayoutName: Array<TextInputLayout>? = null
    private var mWelcomeFormData: WelcomeForm? = WelcomeForm()
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
    private var bannerList: MutableList<TimingImagesModel?>? = ArrayList()
    private var mBannerAdapter: TimingViewPagerBannerAdapter? = null
    private var mList: MutableList<TimingImagesModel> = ArrayList()
    private val mImages: MutableList<String> = ArrayList()
    private var showTireStemDropDown: PopupWindow? = null
    private var width: Int = 0
    private var height: Int = 0
    private var isTireStemWithImageView = false
    private var mAddTimingRequest: AddTimingRequest? = null
    private var mImageJsonString: String? = null

    val formatter = DecimalFormat("00")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        permissionManager =
            permissionFactory.create(caller = this, context = requireContext(), fragment = this)

        // create a session tied to this fragment lifecycle
        session = cameraHelper.createSession(fragment = this)
    }


    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentAddTimeRecordScreenBinding.inflate(inflater, container, false)

    override fun findViewsByViewBinding() {
        mBtnSubmit = binding.btnSubmit
        layTimingLayout = binding.layTimingLayout
        mViewPagerBanner = binding.layoutContentBanner.pagerBanner
        pagerIndicator = binding.layoutContentBanner.viewPagerCountDots
        linearLayoutCompatTireStem = binding.llTireStem
        textFrontTireStem = binding.appcomptextFrontTire
        textRearTireStem = binding.appcomptextRearTire
        textRearValveStem = binding.appcomptextValve
        appCompatImageViewFrontTireStem = binding.appcomimgviewFront
        appCompatImageViewRearTireStem = binding.appcomimgviewRear
        appCompatImageViewValveStem = binding.appcomimgviewTireValve
        appCompatTextViewCircleStemValueFront = binding.textstemvaluefront
        appCompatTextViewCircleStemValueRear = binding.textstemvaluerear
    }

    @SuppressLint("ObsoleteSdkInt")
    override fun initViewLifecycleScope() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                launch {
                    mainActivityViewModel.setToolbarVisibility(true)
                    mainActivityViewModel.setToolbarComponents(
                        showBackButton = true, showLogo = true, showHemMenu = false
                    )
                }

                launch {
                    addTimeRecordScreenViewModel.addTimingResponse.collect(::consumeResponse)
                }

                launch {
                    addTimeRecordScreenViewModel.uploadImageResponse.collect(::consumeResponse)
                }
            }
        }
    }

    override fun initialiseData() {
        init()
        setBannerImageAdapter()
        setAccessibilityForComponents()
    }

    override fun setupClickListeners() {
        mBtnSubmit?.setOnClickListener {
            removeFocus()
            if (isFormValid()) {
                removeFocus()
                if (isFormValid()) {
                    callAddTimingApi()
                }
            }
        }

        binding.ivCamera.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                permissionManager.ensurePermissionsThen(
                    permissions = getCameraPermission(),
                    rationaleMessage = getString(R.string.permission_message_camera_permission_required)
                ) {
                    launchCameraIntent()
                }
            }
        }

        appCompatImageViewFrontTireStem.setOnClickListener {
            dismissPopup()
            showTireStemDropDown = showTireStemDropDown("FRONT")
            showTireStemDropDown?.isOutsideTouchable = true
            showTireStemDropDown?.isFocusable = true
            showTireStemDropDown?.setBackgroundDrawable(ColorDrawable(Color.WHITE))
            showTireStemDropDown!!.showAtLocation(
                mViewPagerBanner, Gravity.BOTTOM, 0, mViewPagerBanner.bottom - 60
            )
            showTireStemDropDown?.showAsDropDown(mViewPagerBanner)
        }

        appCompatImageViewRearTireStem.setOnClickListener {
            dismissPopup()
            showTireStemDropDown = showTireStemDropDown("REAR")
            showTireStemDropDown?.isOutsideTouchable = true
            showTireStemDropDown?.isFocusable = true
            showTireStemDropDown?.setBackgroundDrawable(ColorDrawable(Color.WHITE))
            showTireStemDropDown!!.showAtLocation(
                mViewPagerBanner, Gravity.BOTTOM, 0, mViewPagerBanner.bottom - 60
            )
            showTireStemDropDown?.showAsDropDown(mViewPagerBanner)

        }

        appCompatImageViewValveStem.setOnClickListener {
            //Nothing to do for now
        }
    }

    private fun setAccessibilityForComponents() {
        appCompatImageViewFrontTireStem.contentDescription = textFrontTireStem.text.toString()
        appCompatImageViewRearTireStem.contentDescription = textRearTireStem.text.toString()
        appCompatImageViewValveStem.contentDescription = textRearValveStem.text.toString()
    }

    private fun init() {
        arguments?.let { bundle ->
            mLprNumber = bundle.getString("lpr_number")
            mRegulation = bundle.getString("regulation")
            mSelectedMake = bundle.getString("make")
            mSelectedModel = bundle.getString("model")
            mSelectedColor = bundle.getString("color")
            mSelectedVin = bundle.getString("vinNumber")
            mAddress = bundle.getString("address")?.replace(".0", "")
            if (bundle.containsKey("state")) {
                bundle.getString("state")?.let { scanValueOfState = it }
            }
        }

        mWelcomeFormData = mainActivityViewModel.getWelcomeForm()

        viewLifecycleOwner.lifecycleScope.launch {
            mZone = settingsUtils.getDefaultZoneAtZeroIndex().nullSafety("CST")
            defaultValueOfState = scanValueOfState.ifEmpty {
                settingsUtils.getDefaultState().nullSafety()
            }

            isTireStemWithImageView = settingsUtils.isShowTireStemIcon()

            mStartTime = runCatching { AppUtils.splitDateLpr(mZone) }.getOrDefault("")

            val response = addTimeRecordScreenViewModel.getTimingLayout()
            response?.data?.getOrNull(0)?.response?.takeIf { it.size.nullSafety() > 0 }
                ?.also { mCitationLayout = it; setUserDetails() }

            timingDataIDForTable =
                addTimeRecordScreenViewModel.getLastIDFromTimingData().nullSafety() + 1
        }
    }

    private fun setBannerImageAdapter() {
        mBannerAdapter = TimingViewPagerBannerAdapter(
            requireContext(), object : TimingViewPagerBannerAdapter.ListItemSelectListener {
                override fun onItemClick(position: Int) {
                    bannerList?.removeAt(position)
                    setCameraImages()
                }
            })
    }

    private fun launchCameraIntent() {
        viewLifecycleOwner.lifecycleScope.launch {
            mImageCount = bannerList?.size.nullSafety()
            val maxCount = mainActivityViewModel.getMaxImageCount()
            if (mImageCount >= maxCount) {
                AlertDialogUtils.showDialog(
                    context = requireContext(),
                    title = getString(R.string.error_title_max_image_reached),
                    message = getString(
                        R.string.error_desc_max_image_reached, maxCount.toString()
                    ),
                    positiveButtonText = getString(R.string.button_text_ok),
                )
                return@launch
            }

            val mySession = session ?: return@launch
            mySession.takePicture(
                TEMP_IMAGE_FILE_NAME, CameraHelper.SaveLocation.APP_EXTERNAL_FILES
            ) { bmp ->
                bmp?.let {
                    mViewPagerBanner.post {
                        mViewPagerBanner.showView()
                    }

                    viewLifecycleOwner.lifecycleScope.launch {
                        if (settingsUtils.isTimestampNeedOnImage()) {
                            val timeStampBitmap = AppUtils.timestampItAndSave(bmp)
                            saveImageMM(timeStampBitmap)
                        } else {
                            saveImageMM(bmp)
                        }
                    }
                }
            }
        }
    }

    private fun saveImageMM(finalBitmap: Bitmap?) {
        if (finalBitmap == null) {
            requireContext().toast(getString(R.string.wrn_lbl_capture_image))
            return
        }

        if (Environment.getExternalStorageState() != Environment.MEDIA_MOUNTED) {
            requireContext().toast(getString(R.string.wrn_lbl_capture_image))
            return
        }

        val myDir = File(
            Environment.getExternalStorageDirectory().absolutePath,
            Constants.FILE_NAME + Constants.CAMERA
        )

        if (!myDir.exists()) myDir.mkdirs()

        val timeStamp = SDF_IMAGE_TIMESTAMP.format(Date())
        val fileName = "Image_${timeStamp}_capture.jpg"
        val file = File(myDir, fileName)
        if (file.exists()) file.delete()


        try {
            val out = FileOutputStream(file)

            val compressQuality = 40
            val isSuccess = finalBitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, out)
            out.flush()
            out.close()

            if (!isSuccess || file.length() == 0L) {
                logD("SaveImageMM", "Compression failed or file is empty.")
                file.delete()
                return
            }

            // Delete temp image if exists
            val oldFile = File(myDir, "IMG_temp.jpg")
            if (oldFile.exists()) oldFile.delete()

            // Save path to DB
            val id = SDF_IMAGE_ID_TIMESTAMP.format(Date())

            val pathDb = file.path
            val mImage = TimingImagesModel()
            mImage.timingImage = pathDb
            mImage.status = 0
            mImage.id = id.toInt()
            mImage.timingRecordId = timingDataIDForTable
            mImage.deleteButtonStatus = SHOW_DELETE_BUTTON

            bannerList?.add(mImage)

            setCameraImages()

            finalBitmap.recycle()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //set images to viewpager
    private fun setCameraImages() {
        mViewPagerBanner.post {
            if (bannerList?.isNotEmpty().nullSafety()) {
                showImagesBanner(bannerList)
                mViewPagerBanner.showView()
                pagerIndicator.showView()
            } else {
                mViewPagerBanner.hideView()
                pagerIndicator.hideView()
            }
        }
    }

    private fun showImagesBanner(mImageList: List<TimingImagesModel?>?) {
        val items = mImageList.orEmpty().mapNotNull { it } // safely ignore null entries

        mList.clear()
        if (items.isNotEmpty()) {
            mList.addAll(items)
        }

        // Update adapter and viewpager only when there are items and an adapter exists
        mBannerAdapter?.takeIf { mList.isNotEmpty() }?.let { adapter ->
            adapter.setTimingBannerList(mList)
            mViewPagerBanner.adapter = adapter
            mViewPagerBanner.currentItem = 0
        }

        ViewPagerUtils.setupViewPagerDots(
            context = requireContext(),
            viewPager = mViewPagerBanner,
            dotsContainer = pagerIndicator,
            totalCount = mBannerAdapter?.count.nullSafety()
        )
    }

    private fun setUserDetails() {
        val citations = mCitationLayout ?: return
        if (citations.isEmpty()) return

        layTimingLayout?.post {
            citations.forEach { citation ->
                val comp = citation.component ?: return@forEach
                val compLower = comp.lowercase(java.util.Locale.getDefault())
                if (compLower !in setOf("marking_vehicle", "location", "vehicle")) return@forEach

                layTimingLayout?.visibility = View.VISIBLE
                mBtnSubmit?.visibility = View.VISIBLE

                val fields = citation.fields.orEmpty()
                fields.forEachIndexed { _, field ->
                    val fname = field.name?.lowercase(java.util.Locale.getDefault())
                        ?: return@forEachIndexed

                    when (fname) {
                        "make" -> {
                            val (autoCompleteTextView, textInputLayout, _) = constructLayoutBuilder.checkTypeOfField(
                                citationLayoutField = field,
                                layout = layTimingLayout,
                                component = comp,
                                mContext = requireContext()
                            ) ?: Triple(null, null, null)

                            mAutoComTextViewMake = autoCompleteTextView
                            textInputLayoutMake = textInputLayout
                        }

                        "model" -> {
                            val (autoCompleteTextView, textInputLayout, _) = constructLayoutBuilder.checkTypeOfField(
                                citationLayoutField = field,
                                layout = layTimingLayout,
                                component = comp,
                                mContext = requireContext()
                            ) ?: Triple(null, null, null)

                            mAutoComTextViewModel = autoCompleteTextView
                            textInputLayoutModel = textInputLayout
                        }

                        "color" -> {
                            val (autoCompleteTextView, textInputLayout, _) = constructLayoutBuilder.checkTypeOfField(
                                citationLayoutField = field,
                                layout = layTimingLayout,
                                component = comp,
                                mContext = requireContext()
                            ) ?: Triple(null, null, null)

                            mAutoComTextViewColor = autoCompleteTextView
                            textInputLayoutColor = textInputLayout
                        }

                        "meter" -> {
                            val (autoCompleteTextView, textInputLayout, _) = constructLayoutBuilder.checkTypeOfField(
                                citationLayoutField = field,
                                layout = layTimingLayout,
                                component = comp,
                                mContext = requireContext()
                            ) ?: Triple(null, null, null)

                            mAutoComTextViewMeter = autoCompleteTextView
                            textInputLayoutMeter = textInputLayout
                        }

                        "location", "lot" -> {
                            val (autoCompleteTextView, textInputLayout, _) = constructLayoutBuilder.checkTypeOfField(
                                citationLayoutField = field,
                                layout = layTimingLayout,
                                component = comp,
                                mContext = requireContext()
                            ) ?: Triple(null, null, null)

                            mAutoComTextViewLocation = autoCompleteTextView
                            textInputLayoutLocation = textInputLayout
                        }

                        "block" -> {
                            val (autoCompleteTextView, textInputLayout, _) = constructLayoutBuilder.checkTypeOfField(
                                citationLayoutField = field,
                                layout = layTimingLayout,
                                component = comp,
                                mContext = requireContext()
                            ) ?: Triple(null, null, null)

                            mAutoComTextViewBlock = autoCompleteTextView
                            textInputLayoutBlock = textInputLayout
                            mAutoComTextViewBlock?.filters =
                                arrayOf<InputFilter>(InputFilter.AllCaps())

                            try {
                                val excludedFlavors = setOf(
                                    Constants.FLAVOR_TYPE_GLENDALE,
                                    Constants.FLAVOR_TYPE_GLENDALE_POLICE,
                                    Constants.FLAVOR_TYPE_LAMETRO,
                                    Constants.FLAVOR_TYPE_CORPUSCHRISTI
                                )
                                if (excludedFlavors.none {
                                        it.equals(
                                            BuildConfig.FLAVOR, ignoreCase = true
                                        )
                                    }) {
                                    val source = mAddress?.trim().orEmpty()
                                    val base =
                                        if (source.contains('#')) source.substringBefore('#') else source.substringBefore(
                                            ' '
                                        )
                                    val mRoundOfAddress =
                                        AppUtils.roundOfBlock(base.replace("#", " ").trim())
                                    mAutoComTextViewBlock?.setText(mRoundOfAddress)
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                                mAutoComTextViewBlock?.setText(
                                    mAddress?.substringBefore("#")?.trim().orEmpty()
                                )
                            }

                            mAutoComTextViewBlock?.let { Util.setFieldCaps(requireContext(), it) }
                        }

                        "street", "street_textbox" -> {
                            val (autoCompleteTextView, textInputLayout, _) = constructLayoutBuilder.checkTypeOfField(
                                citationLayoutField = field,
                                layout = layTimingLayout,
                                component = comp,
                                mContext = requireContext()
                            ) ?: Triple(null, null, null)

                            mAutoComTextViewStreet = autoCompleteTextView
                            textInputLayoutStreet = textInputLayout

                            try {
                                mAutoComTextViewStreet?.filters =
                                    arrayOf<InputFilter>(InputFilter.AllCaps())

                                val addr = mAddress.orEmpty()
                                val streetText = if (addr.contains("#")) {
                                    val parts = addr.split("#")
                                    if (parts.size > 2) addr.substringAfter(' ') else parts.getOrNull(
                                        1
                                    ).orEmpty()
                                } else {
                                    val parts = addr.split(" ")
                                    if (parts.size > 2) addr.substringAfter(' ') else parts.getOrNull(
                                        1
                                    ).orEmpty()
                                }
                                if (streetText.isNotEmpty()) {
                                    mAutoComTextViewStreet?.setText(streetText)
                                } else {
                                    mAutoComTextViewStreet?.setText(addr)
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }

                            mAutoComTextViewStreet?.let { Util.setFieldCaps(requireContext(), it) }
                        }

                        "side_of_street", "side", "direction" -> {
                            val (autoCompleteTextView, textInputLayout, _) = constructLayoutBuilder.checkTypeOfField(
                                citationLayoutField = field,
                                layout = layTimingLayout,
                                component = comp,
                                mContext = requireContext()
                            ) ?: Triple(null, null, null)

                            mAutoComTextViewDirection = autoCompleteTextView
                            textInputLayoutDirection = textInputLayout
                            mAutoComTextViewDirection?.let {
                                Util.setFieldCaps(
                                    requireContext(), it
                                )
                            }
                        }

                        "lic_no", "lp_number" -> {
                            val (autoCompleteTextView, textInputLayout, _) = constructLayoutBuilder.checkTypeOfField(
                                citationLayoutField = field,
                                layout = layTimingLayout,
                                component = comp,
                                mContext = requireContext()
                            ) ?: Triple(null, null, null)

                            mAutoComTextViewLicNo = autoCompleteTextView
                            textInputLayoutLicNo = textInputLayout
                        }

                        "lic_state", "state" -> {
                            val (autoCompleteTextView, textInputLayout, _) = constructLayoutBuilder.checkTypeOfField(
                                citationLayoutField = field,
                                layout = layTimingLayout,
                                component = comp,
                                mContext = requireContext()
                            ) ?: Triple(null, null, null)

                            mAutoComTextViewLicState = autoCompleteTextView
                            textInputLayoutLicState = textInputLayout
                        }

                        "vin", "vin_number" -> {
                            val (autoCompleteTextView, textInputLayout, _) = constructLayoutBuilder.checkTypeOfField(
                                citationLayoutField = field,
                                layout = layTimingLayout,
                                component = comp,
                                mContext = requireContext()
                            ) ?: Triple(null, null, null)

                            mAutoComTextViewVin = autoCompleteTextView
                            textInputLayoutVin = textInputLayout
                        }

                        "time_limit_select" -> {
                            val (autoCompleteTextView, textInputLayout, _) = constructLayoutBuilder.checkTypeOfField(
                                citationLayoutField = field,
                                layout = layTimingLayout,
                                component = comp,
                                mContext = requireContext()
                            ) ?: Triple(null, null, null)

                            mAutoComTextViewTimeLimit = autoCompleteTextView
                            textInputLayoutTimeLimit = textInputLayout
                        }

                        "tier_stem_left" -> {
                            if (!isTireStemWithImageView) {
                                val (autoCompleteTextView, textInputLayout, _) = constructLayoutBuilder.checkTypeOfField(
                                    citationLayoutField = field,
                                    layout = layTimingLayout,
                                    component = comp,
                                    mContext = requireContext()
                                ) ?: Triple(null, null, null)

                                mAutoComTextViewTierLeft = autoCompleteTextView
                                textInputLayoutTierLeft = textInputLayout
                            } else {
                                linearLayoutCompatTireStem.visibility = View.VISIBLE
                                if (field.isRequired.nullSafety()) {
                                    textFrontTireStem.text = field.repr + "*"
                                } else {
                                    textFrontTireStem.text = field.repr
                                }
                                mFrontTireStemText = textFrontTireStem.text.toString()
                                if (isTireStemWithImageView) {
                                    val vto = appCompatImageViewValveStem.viewTreeObserver
                                    linearLayoutCompatTireStem.visibility = View.VISIBLE
                                    vto.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
                                        override fun onGlobalLayout() {
                                            appCompatImageViewValveStem.viewTreeObserver.removeOnGlobalLayoutListener(
                                                this
                                            )
                                            width = appCompatImageViewValveStem.measuredWidth
                                            height = appCompatImageViewValveStem.measuredHeight
                                        }
                                    })
                                }
                            }
                        }

                        "tier_stem_right" -> {
                            if (!isTireStemWithImageView) {
                                val (autoCompleteTextView, textInputLayout, _) = constructLayoutBuilder.checkTypeOfField(
                                    citationLayoutField = field,
                                    layout = layTimingLayout,
                                    component = comp,
                                    mContext = requireContext()
                                ) ?: Triple(null, null, null)

                                mAutoComTextViewTierRight = autoCompleteTextView
                                textInputLayoutTierRight = textInputLayout
                            } else {
                                linearLayoutCompatTireStem.visibility = View.VISIBLE
                                if (field.isRequired.nullSafety()) {
                                    textRearTireStem.text = field.repr + "*"
                                } else {
                                    textRearTireStem.text = field.repr
                                }
                                mRearTireStemText = textRearTireStem.text.toString()
                            }
                        }

                        "zone" -> {
                            val (autoCompleteTextView, textInputLayout, _) = constructLayoutBuilder.checkTypeOfField(
                                citationLayoutField = field,
                                layout = layTimingLayout,
                                component = comp,
                                mContext = requireContext()
                            ) ?: Triple(null, null, null)

                            mAutoComTextViewZone = autoCompleteTextView
                            textInputLayoutZone = textInputLayout
                            mAutoComTextViewZone?.setText(mWelcomeFormData?.officerZone.orEmpty())
                            mAutoComTextViewZone?.let { Util.setFieldCaps(requireContext(), it) }
                        }

                        "city_zone", "pbc_zone" -> {
                            val (autoCompleteTextView, textInputLayout, _) = constructLayoutBuilder.checkTypeOfField(
                                citationLayoutField = field,
                                layout = layTimingLayout,
                                component = comp,
                                mContext = requireContext()
                            ) ?: Triple(null, null, null)

                            mAutoComTextViewPBCZone = autoCompleteTextView
                            textInputLayoutPBCZone = textInputLayout
                            mAutoComTextViewPBCZone?.let { Util.setFieldCaps(requireContext(), it) }
                        }

                        "remark" -> {
                            val (autoCompleteTextView, textInputLayout, _) = constructLayoutBuilder.checkTypeOfField(
                                citationLayoutField = field,
                                layout = layTimingLayout,
                                component = comp,
                                mContext = requireContext()
                            ) ?: Triple(null, null, null)

                            mAutoComTextViewRemarks = autoCompleteTextView
                            textInputLayoutRemarks = textInputLayout
                            mAutoComTextViewRemarks?.let { Util.setFieldCaps(requireContext(), it) }
                            mAutoComTextViewRemarks?.setText(
                                sharedPreference.read(
                                    SharedPrefKey.LOCK_TIMING_REMARK, ""
                                )
                            )
                        }

                        "remark_2" -> {
                            val (autoCompleteTextView, textInputLayout, _) = constructLayoutBuilder.checkTypeOfField(
                                citationLayoutField = field,
                                layout = layTimingLayout,
                                component = comp,
                                mContext = requireContext()
                            ) ?: Triple(null, null, null)

                            mAutoComTextViewRemarks2 = autoCompleteTextView
                            textInputLayoutRemarks2 = textInputLayout
                            mAutoComTextViewRemarks2?.let {
                                Util.setFieldCaps(
                                    requireContext(), it
                                )
                            }
                            mAutoComTextViewRemarks2?.setText(
                                sharedPreference.read(
                                    SharedPrefKey.LOCK_TIMING_REMARK2, ""
                                )
                            )
                        }

                        else -> {
                            try {
                                val (autoCompleteTextView, textInputLayout, _) = constructLayoutBuilder.checkTypeOfField(
                                    citationLayoutField = field,
                                    layout = layTimingLayout,
                                    component = comp,
                                    mContext = requireContext()
                                ) ?: Triple(null, null, null)

                                latestLayoutTimings++
                                autoCompleteTextView?.let { ac ->
                                    name?.let { arr ->
                                        if (latestLayoutTimings in arr.indices) arr[latestLayoutTimings] =
                                            ac
                                    }
                                }
                                textInputLayout?.let { til ->
                                    textInputLayoutName?.let { arr ->
                                        if (latestLayoutTimings in arr.indices) arr[latestLayoutTimings] =
                                            til
                                    }
                                }
                            } catch (_: Exception) { /* ignore */
                            }
                        }
                    }
                }

                // call after processing fields for this citation (keeps original behavior)
                getDatasetFromDb()
            }
        }
    }

    private fun getDatasetFromDb() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                mAutoComTextViewLicNo!!.post {
                    mAutoComTextViewLicNo?.setText(mLprNumber)
                }
                if (mAutoComTextViewVin != null) mAutoComTextViewVin!!.post {
                    mAutoComTextViewVin?.setText(mSelectedVin)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
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
            lockTimeData(true, null)
        }
    }

    //----------set dropdown-------------------------------------------
    //set value to vehicle model dropdown
    private fun setDropdownVehicleModel(value: String?) {
        val view = mAutoComTextViewModel ?: return

        // keep mModelList consistent — clear previous entries
        mModelList.clear()

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val allModels = withContext(Dispatchers.IO) {
                    mainActivityViewModel.getCarModelListFromDataSet().orEmpty()
                }

                val models = allModels.asSequence().filter { it.make == mSelectedMakeValue }
                    .mapNotNull { it.model?.takeIf { s -> s.isNotBlank() } }.distinct().sorted()
                    .toList()

                if (models.isEmpty()) return@launch

                // populate mModelList with DatasetResponse objects (preserve original type)
                mModelList.apply {
                    clear()
                    models.forEach {
                        val dr = DatasetResponse()
                        dr.model = it
                        add(dr)
                    }
                }

                val items = models.toTypedArray()
                val adapter =
                    ArrayAdapter(requireContext(), R.layout.row_dropdown_lpr_details_item, items)

                view.threshold = 1
                view.setAdapter(adapter)

                val selectedIndex = models.indexOfFirst { it.equals(value, ignoreCase = true) }
                if (selectedIndex >= 0) {
                    mSelectedModel = models[selectedIndex]
                    view.setText(
                        models[selectedIndex], false
                    ) // avoid filtering when programmatically setting text
                }

                view.onItemClickListener = OnItemClickListener { parent, _, position, _ ->
                    mSelectedModel = parent.getItemAtPosition(position).toString()
                    requireActivity().hideSoftKeyboard()
                }

                if (view.tag == CITATION_FORM_FIELD_TYPE_LIST_ONLY) {
                    view.setListOnlyDropDown(
                        context = requireContext(), textInputLayout = textInputLayoutModel
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    //set value to make vehicle dropdown
    private fun setDropdownMakeVehicle(value: String?) {
        val view = mAutoComTextViewMake ?: return

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val rawList = withContext(Dispatchers.IO) {
                    mainActivityViewModel.getCarMakeListFromDataSet().orEmpty()
                }
                if (rawList.isEmpty()) return@launch

                val pairs = rawList.mapNotNull { item ->
                    val make = item.make?.takeIf { it.isNotBlank() }
                    val makeText = item.makeText?.takeIf { it.isNotBlank() }
                    if (make != null && makeText != null) make to makeText else null
                }.distinctBy { "${it.first}#${it.second}" }.sortedBy { it.second }

                if (pairs.isEmpty()) return@launch

                val items = pairs.map { it.second }.toTypedArray()
                val adapter =
                    ArrayAdapter(requireContext(), R.layout.row_dropdown_lpr_details_item, items)

                view.threshold = 1
                view.setAdapter(adapter)

                val selectedIndex = pairs.indexOfFirst { (make, makeText) ->
                    value != null && (make.equals(
                        value, ignoreCase = true
                    ) || makeText.equals(value, ignoreCase = true))
                }
                if (selectedIndex >= 0) {
                    mSelectedMake = pairs[selectedIndex].first
                    mSelectedMakeValue = pairs[selectedIndex].second
                    view.setText(pairs[selectedIndex].second, false)
                }

                view.onItemClickListener = OnItemClickListener { parent, _, position, _ ->
                    val display = parent.getItemAtPosition(position).toString()
                    val idx = pairs.indexOfFirst { it.second == display }
                    if (idx >= 0) {
                        mSelectedMake = pairs[idx].first
                        mSelectedMakeValue = pairs[idx].second
                    }
                    setDropdownVehicleModel(mSelectedMake)
                    requireActivity().hideSoftKeyboard()
                }

                if (view.tag == CITATION_FORM_FIELD_TYPE_LIST_ONLY) {
                    view.setListOnlyDropDown(
                        context = requireContext(), textInputLayout = textInputLayoutMake
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    //set value to vehicle colour dropdown
    private fun setDropdownVehicleColour(value: String?) {
        val view = mAutoComTextViewColor ?: return

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val colors = withContext(Dispatchers.IO) {
                    mainActivityViewModel.getCarColorListFromDataSet().orEmpty()
                }

                val items =
                    colors.mapNotNull { it.description?.takeIf { s -> s.isNotBlank() } }.distinct()
                        .sorted()

                if (items.isEmpty()) return@launch

                val adapter = ArrayAdapter(
                    requireContext(), R.layout.row_dropdown_lpr_details_item, items.toTypedArray()
                )

                view.threshold = 1
                view.setAdapter(adapter)

                val selectedIndex = items.indexOfFirst { it.equals(value, ignoreCase = true) }
                if (selectedIndex >= 0) {
                    // setText with false to avoid triggering filtering
                    view.setText(items[selectedIndex], false)
                    mSelectedColor = items[selectedIndex]
                }

                view.onItemClickListener = OnItemClickListener { _, _, position, _ ->
                    val selected = items.getOrNull(position).orEmpty()
                    mSelectedColor = selected
                    requireActivity().hideSoftKeyboard()
                }

                if (view.tag == CITATION_FORM_FIELD_TYPE_LIST_ONLY) {
                    view.setListOnlyDropDown(
                        context = requireContext(), textInputLayout = textInputLayoutColor
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    //set value to Meter Name dropdown
    private fun setDropdownBlock() {
        val view = mAutoComTextViewBlock ?: return

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val blocks = withContext(Dispatchers.IO) {
                    mainActivityViewModel.getBlockListFromDataSet().orEmpty()
                }

                val items =
                    blocks.mapNotNull { it.blockName?.takeIf { s -> s.isNotBlank() } }.distinct()
                        .sorted().toTypedArray()

                if (items.isEmpty()) return@launch

                val adapter =
                    ArrayAdapter(requireContext(), R.layout.row_dropdown_menu_popup_item, items)
                view.threshold = 1
                view.setAdapter(adapter)
                view.onItemClickListener = OnItemClickListener { _, _, _, _ ->
                    requireActivity().hideSoftKeyboard()
                }
                if (view.tag == CITATION_FORM_FIELD_TYPE_LIST_ONLY) {
                    view.setListOnlyDropDown(
                        context = requireContext(), textInputLayout = textInputLayoutBlock
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    //set value to State dropdown
    private fun setDropdownState() {
        val view = mAutoComTextViewLicState ?: return

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val stateList = withContext(Dispatchers.IO) {
                    mainActivityViewModel.getStateListFromDataSet().orEmpty()
                }

                val items = stateList.mapNotNull {
                    it.state_name?.takeIf { s -> s.isNotBlank() }
                }.distinct()

                if (items.isEmpty()) return@launch

                val adapter = ArrayAdapter(
                    requireContext(), R.layout.row_dropdown_menu_popup_item, items.toTypedArray()
                )

                view.threshold = 1
                view.setAdapter(adapter)

                val selectedIndex =
                    items.indexOfFirst { it.equals(defaultValueOfState, ignoreCase = true) }
                if (selectedIndex >= 0) {
                    // use false to avoid triggering filtering
                    view.setText(items[selectedIndex], false)
                }

                view.onItemClickListener = OnItemClickListener { _, _, _, _ ->
                    requireActivity().hideSoftKeyboard()
                }

                if (view.tag == CITATION_FORM_FIELD_TYPE_LIST_ONLY) {
                    view.setListOnlyDropDown(
                        context = requireContext(), textInputLayout = textInputLayoutLicState
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    //set value to side dropdown
    private fun setDropdownSide() {
        val view = mAutoComTextViewDirection ?: return

        viewLifecycleOwner.lifecycleScope.launch {
            val sideList = withContext(Dispatchers.IO) {
                mainActivityViewModel.getSideListFromDataSet().orEmpty()
            }

            val items =
                sideList.mapNotNull { it.sideName?.takeIf { s -> s.isNotBlank() } }.distinct()
                    .toTypedArray()

            if (items.isEmpty()) return@launch

            try {
                val adapter =
                    ArrayAdapter(requireContext(), R.layout.row_dropdown_menu_popup_item, items)
                view.threshold = 1
                view.setAdapter(adapter)
                view.onItemClickListener = OnItemClickListener { _, _, _, _ ->
                    requireActivity().hideSoftKeyboard()
                }
                if (view.tag == CITATION_FORM_FIELD_TYPE_LIST_ONLY) {
                    view.setListOnlyDropDown(
                        context = requireContext(), textInputLayout = textInputLayoutDirection
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    //set value to side dropdown
    private fun setDropdownRegulation(value: String?) {
        val view = mAutoComTextViewTimeLimit ?: return

        viewLifecycleOwner.lifecycleScope.launch {
            // load data off the main thread
            val regs = withContext(Dispatchers.IO) {
                mainActivityViewModel.getRegulationTimeListFromDataSet().orEmpty()
            }
            if (regs.isEmpty()) return@launch

            // prepare display items and selected index
            val items = regs.map { it.regulation.orEmpty() }
            val selectedIndex = value?.let { v ->
                regs.indexOfFirst { r ->
                    r.regulation?.equals(
                        v, ignoreCase = true
                    ) == true
                }
            } ?: -1

            // update UI on main thread
            withContext(Dispatchers.Main) {
                if (selectedIndex >= 0) {
                    view.setText(items[selectedIndex], false)
                    mRegulationTime = regs[selectedIndex].mTime.nullSafety()
                    mRegulationTimeValue = regs[selectedIndex].regulation.nullSafety()
                }

                val adapter = ArrayAdapter(
                    requireContext(), R.layout.row_dropdown_menu_popup_item, items.toTypedArray()
                )
                view.threshold = 1
                view.setAdapter(adapter)
                view.onItemClickListener = OnItemClickListener { parent, _, position, _ ->
                    // find original index and update related values
                    val index =
                        regs.getIndexOfRegulation(parent.getItemAtPosition(position).toString())
                    if (index in regs.indices) {
                        mRegulationTime = regs[index].mTime.nullSafety()
                        mRegulationTimeValue = regs[index].regulation.nullSafety()
                    }
                    requireActivity().hideSoftKeyboard()
                }

                if (view.tag == CITATION_FORM_FIELD_TYPE_LIST_ONLY) {
                    view.setListOnlyDropDown(
                        context = requireContext(), textInputLayout = textInputLayoutTimeLimit
                    )
                }
            }
        }
    }

    //set value to Street dropdown
    private fun setDropdownStreet(value: String?) {
        val view = mAutoComTextViewStreet ?: return

        viewLifecycleOwner.lifecycleScope.launch {
            // Fetch off main thread
            val streetList = withContext(Dispatchers.IO) {
                mainActivityViewModel.getStreetListFromDataSet().orEmpty()
            }
            if (streetList.isEmpty()) return@launch

            val items =
                streetList.mapNotNull { it.street_name?.takeIf { s -> s.isNotBlank() } }.distinct()
            if (items.isEmpty()) return@launch

            val selectedIndex = value?.takeIf { it.isNotBlank() }
                ?.let { v -> items.indexOfFirst { it.equals(v, ignoreCase = true) } } ?: -1

            val adapter =
                ArrayAdapter(requireContext(), R.layout.row_dropdown_menu_popup_item, items)
            view.threshold = 1
            view.setAdapter(adapter)

            if (selectedIndex >= 0) {
                // false avoids triggering filtering
                view.setText(items[selectedIndex], false)
            }

            view.onItemClickListener = OnItemClickListener { parent, _, position, _ ->
                requireActivity().hideSoftKeyboard()
                // Handle selection if needed
            }

            if (view.tag == CITATION_FORM_FIELD_TYPE_LIST_ONLY) {
                view.setListOnlyDropDown(
                    context = requireContext(), textInputLayout = textInputLayoutStreet
                )
            }
        }
    }

    private fun setDropdownZone() {
        val view = mAutoComTextViewZone ?: return

        viewLifecycleOwner.lifecycleScope.launch {
            val zoneStats =
                addTimeRecordScreenViewModel.getWelcomeDbObject()?.welcomeList?.zoneStats.orEmpty()

            if (zoneStats.isEmpty()) return@launch

            val items =
                zoneStats.mapNotNull { it.zoneName?.takeIf { s -> s.isNotBlank() } }.distinct()

            if (items.isEmpty()) return@launch

            val adapter =
                ArrayAdapter(requireContext(), R.layout.row_dropdown_menu_popup_item, items)
            view.threshold = 1
            view.setAdapter(adapter)
            view.onItemClickListener = OnItemClickListener { _, _, _, _ ->
                requireActivity().hideSoftKeyboard()
            }
            if (view.tag == CITATION_FORM_FIELD_TYPE_LIST_ONLY) {
                view.setListOnlyDropDown(
                    context = requireContext(), textInputLayout = textInputLayoutZone
                )
            }
        }
    }

    //set value to zone dropdown
    private fun setDropdownPBCZone(value: String?) {
        val view = mAutoComTextViewPBCZone ?: return

        viewLifecycleOwner.lifecycleScope.launch {
            val zoneList =
                mainActivityViewModel.getWelcomeObject()?.welcomeList?.pbcZoneStats.orEmpty()

            if (zoneList.isEmpty()) return@launch

            val items = zoneList.map { it.zoneName.orEmpty() }.filter { it.isNotBlank() }.distinct()
                .toTypedArray()

            if (items.isEmpty()) return@launch

            val selectedIndex =
                value?.let { v -> items.indexOfFirst { it.equals(v, ignoreCase = true) } } ?: -1

            val adapter =
                ArrayAdapter(requireContext(), R.layout.row_dropdown_menu_popup_item, items)
            view.threshold = 1
            view.setAdapter(adapter)

            if (selectedIndex >= 0) {
                // setText with false to avoid triggering filtering
                view.setText(items[selectedIndex], false)
            }

            view.onItemClickListener = OnItemClickListener { parent, _, position, _ ->
                requireActivity().hideSoftKeyboard()
                // Optional: handle selection if needed
            }

            if (view.tag == CITATION_FORM_FIELD_TYPE_LIST_ONLY) {
                view.setListOnlyDropDown(
                    context = requireContext(), textInputLayout = textInputLayoutPBCZone
                )
            }
        }
    }

    //set value to Meter Name dropdown
    private fun setDropdownMeterName() {
        val view = mAutoComTextViewMeter ?: return

        viewLifecycleOwner.lifecycleScope.launch {
            val meterList = mainActivityViewModel.getMeterListFromDataSet().orEmpty()
            if (meterList.isEmpty()) return@launch

            val items = meterList.map { it.name.orEmpty() }.toTypedArray()

            withContext(Dispatchers.Main) {
                val adapter =
                    ArrayAdapter(requireContext(), R.layout.row_dropdown_menu_popup_item, items)
                view.threshold = 1
                view.setAdapter(adapter)
                view.onItemClickListener = OnItemClickListener { parent, _, position, _ ->
                    requireActivity().hideSoftKeyboard()
                    meterList.getOrNull(position)?.let { meter ->
                        meter.block?.let { blockStr ->
                            mAutoComTextViewBlock?.setText(blockStr)
                        }
                        setDropdownStreet(meter.street)
                    }
                }
                if (view.tag == CITATION_FORM_FIELD_TYPE_LIST_ONLY) {
                    view.setListOnlyDropDown(
                        context = requireContext(), textInputLayout = textInputLayoutMeter
                    )
                }
            }
        }
    }

    //set value to Meter Name dropdown
    private fun setDropdownLocationName() {
        val view = mAutoComTextViewLocation ?: return

        viewLifecycleOwner.lifecycleScope.launch {
            val lotList = mainActivityViewModel.getLotListFromDataSet().orEmpty()
            if (lotList.isEmpty()) return@launch

            val items = lotList.map { it.location.orEmpty() }.distinct().toTypedArray()

            withContext(Dispatchers.Main) {
                val adapter =
                    ArrayAdapter(requireContext(), R.layout.row_dropdown_menu_popup_item, items)
                view.threshold = 1
                view.setAdapter(adapter)
                view.onItemClickListener = OnItemClickListener { parent, _, position, _ ->
                    requireActivity().hideSoftKeyboard()
                    val selected = parent.getItemAtPosition(position).toString()
                    val index = lotList.getIndexOfLocation(selected)
                    lotList.getOrNull(index)?.let { lot ->
                        mAutoComTextViewBlock?.setText(lot.block.orEmpty())
                        setDropdownStreet(lot.street)
                    }
                }
                if (view.tag == CITATION_FORM_FIELD_TYPE_LIST_ONLY) {
                    view.setListOnlyDropDown(
                        context = requireContext(), textInputLayout = textInputLayoutLocation
                    )
                }
            }
        }
    }

    //set value to Remark dropdown
    private fun setDropdownRemark() {
        val view = mAutoComTextViewRemarks ?: return

        viewLifecycleOwner.lifecycleScope.launch {
            val rawList = mainActivityViewModel.getRemarkListFromDataSet().orEmpty()
            if (rawList.isEmpty()) return@launch

            val items = rawList.mapNotNull { it.remark?.takeIf { s -> s.isNotBlank() } }
            if (items.isEmpty()) return@launch

            withContext(Dispatchers.Main) {
                val adapter = ArrayAdapter(
                    requireContext(), R.layout.row_dropdown_menu_popup_item, items.toTypedArray()
                )
                view.threshold = 1
                view.setAdapter(adapter)
                view.onItemClickListener = OnItemClickListener { _, _, _, _ ->
                    requireActivity().hideSoftKeyboard()
                }
                if (view.tag == CITATION_FORM_FIELD_TYPE_LIST_ONLY) {
                    view.setListOnlyDropDown(
                        context = requireContext(), textInputLayout = textInputLayoutRemarks
                    )
                }
            }
        }
    }

    //set value to Remark2 dropdown
    private fun setDropdownRemark2() {
        val view = mAutoComTextViewRemarks2 ?: return

        viewLifecycleOwner.lifecycleScope.launch {
            val rawList = mainActivityViewModel.getRemarkListFromDataSet().orEmpty()
            if (rawList.isEmpty()) return@launch

            val items = rawList.mapNotNull { it1 -> it1.remark?.takeIf { it.isNotBlank() } }
            if (items.isEmpty()) return@launch

            withContext(Dispatchers.Main) {
                val adapter =
                    ArrayAdapter(requireContext(), R.layout.row_dropdown_menu_popup_item, items)
                view.threshold = 1
                view.setAdapter(adapter)
                view.onItemClickListener = OnItemClickListener { _, _, _, _ ->
                    requireActivity().hideSoftKeyboard()
                }
                if (view.tag == CITATION_FORM_FIELD_TYPE_LIST_ONLY) {
                    view.setListOnlyDropDown(
                        context = requireContext(), textInputLayout = textInputLayoutRemarks2
                    )
                }
            }
        }
    }

    //set value to Tier Left dropdown
    private fun setDropdownTierLeft() {
        val view = mAutoComTextViewTierLeft ?: return

        viewLifecycleOwner.lifecycleScope.launch {
            val rawList = mainActivityViewModel.getTierStemListFromDataSet().orEmpty()
            if (rawList.isEmpty()) return@launch

            val items = rawList.mapNotNull { node ->
                runCatching {
                    formatter.format(node.tierStem.nullSafety("0.0").toLong())
                }.getOrNull()
            }.sortedWith(compareBy { it.toIntOrNull() ?: Int.MAX_VALUE })

            if (items.isEmpty()) return@launch

            withContext(Dispatchers.Main) {
                val adapter =
                    ArrayAdapter(requireContext(), R.layout.row_dropdown_menu_popup_item, items)
                view.threshold = 1
                view.setAdapter(adapter)
                view.onItemClickListener = OnItemClickListener { _, _, _, _ ->
                    requireActivity().hideSoftKeyboard()
                    mFrontTireStemValue = view.text.toString()
                }
                if (view.tag == CITATION_FORM_FIELD_TYPE_LIST_ONLY) {
                    view.setListOnlyDropDown(
                        context = requireContext(), textInputLayout = textInputLayoutTierLeft
                    )
                }
            }
        }
    }

    //set value to Tier Right dropdown
    private fun setDropdownTierRight() {
        val view = mAutoComTextViewTierRight ?: return

        viewLifecycleOwner.lifecycleScope.launch {
            val rawList = mainActivityViewModel.getTierStemListFromDataSet().orEmpty()
            if (rawList.isEmpty()) return@launch

            // prepare formatted numeric strings and sort by numeric value
            val items = rawList.mapNotNull { node ->
                runCatching {
                    formatter.format(node.tierStem.nullSafety("0.0").toLong())
                }.getOrNull()
            }.sortedWith(compareBy { it.toIntOrNull() ?: Int.MAX_VALUE })

            if (items.isEmpty()) return@launch

            withContext(Dispatchers.Main) {
                val adapter =
                    ArrayAdapter(requireContext(), R.layout.row_dropdown_menu_popup_item, items)
                view.threshold = 1
                view.setAdapter(adapter)
                view.onItemClickListener = OnItemClickListener { _, _, _, _ ->
                    requireActivity().hideSoftKeyboard()
                    mRearTireStemValue = view.text.toString()
                }
                if (view.tag == CITATION_FORM_FIELD_TYPE_LIST_ONLY) {
                    view.setListOnlyDropDown(
                        context = requireContext(), textInputLayout = textInputLayoutTierRight
                    )
                }
            }
        }
    }

    /* Call Api to Add timing */
    private fun callAddTimingApi() {
        try {
            fun textOf(view: AppCompatAutoCompleteTextView?) =
                view?.text?.toString()?.trim().orEmpty()

            val blockText = textOf(mAutoComTextViewBlock)
            val streetText = textOf(mAutoComTextViewStreet)
            // Save selected block and street when address is locked
            sharedPreference.write(
                SharedPrefKey.LOCK_GEO_SAVE_ADDRESS, "$blockText#$streetText, A"
            )

            val mLat = sharedPreference.read(SharedPrefKey.LAT, "0.0")?.toDoubleOrNull() ?: 0.0
            val mLong = sharedPreference.read(SharedPrefKey.LONG, "0.0")?.toDoubleOrNull() ?: 0.0

            val remarkText = textOf(mAutoComTextViewRemarks)
            val remark2Text = textOf(mAutoComTextViewRemarks2)
            // persist remarks safely
            try {
                sharedPreference.write(SharedPrefKey.LOCK_TIMING_REMARK, remarkText)
                sharedPreference.write(SharedPrefKey.LOCK_TIMING_REMARK2, remark2Text)
            } catch (_: Exception) { /* ignore preference write errors */
            }

            mAddTimingRequest = AddTimingRequest().apply {
                lprState = textOf(mAutoComTextViewLicState)
                lprNumber = textOf(mAutoComTextViewLicNo)
                meterNumber = textOf(mAutoComTextViewMeter)
                mLot = textOf(mAutoComTextViewLocation)
                mLocation = textOf(mAutoComTextViewLocation)
                block = blockText
                regulationTime = mRegulationTime.trim().toLongOrNull() ?: 0L
                regulationTimeValue = mRegulationTimeValue.trim()
                street = streetText
                side = textOf(mAutoComTextViewDirection)
                zone = textOf(mAutoComTextViewZone)
                pbcZone = textOf(mAutoComTextViewPBCZone)
                remark = remarkText
                remark2 = remark2Text
                mTireStemFront = mFrontTireStemValue?.toIntOrNull() ?: 0
                mTireStemBack = mRearTireStemValue?.toIntOrNull() ?: 0
                mVin = textOf(mAutoComTextViewVin)
                status = "Open"
                latitude = mLat
                longitiude = mLong
                source = "officer"
                officerName =
                    mWelcomeFormData?.officerFirstName.nullSafety() + " " + mWelcomeFormData?.officerLastName.nullSafety()
                badgeId = mWelcomeFormData?.officerBadgeId.nullSafety()
                shift = sharedPreference.read(SharedPrefKey.LOGIN_SHIFT, "")
                supervisor = mWelcomeFormData?.officerSupervisor.nullSafety()
                markStartTimestamp = mStartTime
                markIssueTimestamp = AppUtils.splitDateLpr(mZone)
                mMake = mSelectedMake
                mModel = mSelectedModel
                mColor = mSelectedColor
                mAddress = "$blockText $streetText"
                imageUrls = mImages
            }

            lockTimeData(false, mAddTimingRequest)

            if (requireContext().isInternetAvailable()) {
                if (bannerList.isNullOrEmpty()) {
                    addTimeRecordScreenViewModel.callAddTimingAPI(mAddTimingRequest)
                } else {
                    callUploadAllImages()
                }
            } else {
                saveTimingImagesOffline()
                saveTimingDataForm(mAddTimingRequest!!)
                requireContext().toast(getString(R.string.err_msg_connection_was_refused))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun callUploadAllImages() {
        if (requireContext().isInternetAvailable()) {
            mImageJsonString = ObjectMapperProvider.toJson(bannerList!!)

            bannerList?.forEach { item ->
                val file = File(item?.timingImage.nullSafety())
                val requestFile = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                val mPart: MultipartBody.Part = MultipartBody.Part.createFormData(
                    "files", file.name, requestFile
                )

                val fileNames = arrayOf(FileUtil.getFileNameWithoutExtension(file.name))

                //Code for one by one upload
                val mRequestBodyType =
                    API_CONSTANT_UPLOAD_TYPE_TIMING_IMAGES.toRequestBody("text/plain".toMediaTypeOrNull())
                addTimeRecordScreenViewModel.callUploadImageAPI(fileNames, mRequestBodyType, mPart)
            }
        } else {
            requireContext().toast(
                getString(R.string.err_msg_connection_was_refused)
            )
        }
    }

    private fun removeFocus() {
        try {
            listOfNotNull(
                mAutoComTextViewMeter,
                mAutoComTextViewBlock,
                mAutoComTextViewStreet,
                mAutoComTextViewDirection,
                mAutoComTextViewLicNo,
                mAutoComTextViewLicState,
                mAutoComTextViewVin,
                mAutoComTextViewTimeLimit,
                mAutoComTextViewTierLeft,
                mAutoComTextViewTierRight,
                mAutoComTextViewZone,
                mAutoComTextViewRemarks,
                mAutoComTextViewRemarks2
            ).forEach { it.clearFocus() }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun isFormValid(): Boolean {
        try {
            if (mCitationLayout.isNullOrEmpty()) return true

            fun textOf(view: AppCompatAutoCompleteTextView?) =
                view?.text?.toString()?.trim().orEmpty()

            fun showFieldError(
                view: AppCompatAutoCompleteTextView?, layout: TextInputLayout?, msgResId: Int
            ) {
                view?.requestFocus()
                layout?.showErrorWithShake(getString(msgResId))
            }

            val isGlendale = BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLENDALE, true)
            val isGlendalePolice =
                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLENDALE_POLICE, true)

            mCitationLayout?.forEach { layoutItem ->
                layoutItem.fields?.forEach { field ->
                    if (!field.isRequired.nullSafety()) return@forEach

                    when (field.name?.lowercase().orEmpty()) {
                        "meter" -> {
                            if (textOf(mAutoComTextViewMeter).isEmpty()) {
                                // preserve original odd "FOR LIST ONLY IN" behavior: early true if empty
                                if (textOf(mAutoComTextViewMeter).isEmpty()) return true
                                showFieldError(
                                    mAutoComTextViewMeter,
                                    textInputLayoutMeter,
                                    R.string.val_msg_please_enter_meter_name
                                )
                                return false
                            }
                        }

                        "block" -> {
                            if (textOf(mAutoComTextViewBlock).isEmpty()) {
                                showFieldError(
                                    mAutoComTextViewBlock,
                                    textInputLayoutBlock,
                                    R.string.val_msg_please_enter_block
                                )
                                return false
                            }
                        }

                        "street", "street_textbox" -> {
                            if (textOf(mAutoComTextViewStreet).isEmpty()) {
                                showFieldError(
                                    mAutoComTextViewStreet,
                                    textInputLayoutStreet,
                                    R.string.val_msg_please_enter_street
                                )
                                return false
                            }
                        }

                        "side_of_street", "side" -> {
                            if (textOf(mAutoComTextViewDirection).isEmpty()) {
                                showFieldError(
                                    mAutoComTextViewDirection,
                                    textInputLayoutDirection,
                                    R.string.val_msg_please_enter_side
                                )
                                return false
                            }
                        }

                        "lic_no", "lp_number" -> {
                            val vinEmpty = textOf(mAutoComTextViewVin).isEmpty()
                            val licEmpty = textOf(mAutoComTextViewLicNo).isEmpty()
                            if (vinEmpty && licEmpty && (isGlendale || isGlendalePolice)) {
                                requireContext().toast(getString(R.string.val_msg_please_enter_lpr_number_or_vin_number))
                                return false
                            }
                            if (vinEmpty && licEmpty) {
                                showFieldError(
                                    mAutoComTextViewLicNo,
                                    textInputLayoutLicNo,
                                    R.string.val_msg_please_enter_lpr_number
                                )
                                return false
                            }
                        }

                        "lic_state", "state" -> {
                            val vinEmpty = textOf(mAutoComTextViewVin).isEmpty()
                            val stateEmpty = textOf(mAutoComTextViewLicState).isEmpty()
                            if (vinEmpty && stateEmpty) {
                                showFieldError(
                                    mAutoComTextViewLicState,
                                    textInputLayoutLicState,
                                    R.string.val_msg_please_enter_state
                                )
                                return false
                            }
                        }

                        "vin", "vin_number" -> {
                            val licNoEmpty = textOf(mAutoComTextViewLicNo).isEmpty()
                            val licStateEmpty = textOf(mAutoComTextViewLicState).isEmpty()
                            val vinEmpty = textOf(mAutoComTextViewVin).isEmpty()
                            if (licNoEmpty && licStateEmpty && vinEmpty) {
                                showFieldError(
                                    mAutoComTextViewVin,
                                    textInputLayoutVin,
                                    R.string.val_msg_please_enter_vin_number
                                )
                                return false
                            }
                        }

                        "make" -> {
                            val makeText = textOf(mAutoComTextViewMake)
                            if (makeText.isEmpty() || mSelectedMake.isNullOrEmpty()) {
                                showFieldError(
                                    mAutoComTextViewMake,
                                    textInputLayoutMake,
                                    R.string.val_msg_please_enter_make
                                )
                                return false
                            }
                        }

                        "model" -> {
                            if (textOf(mAutoComTextViewModel).isEmpty() && mModelList.isNotEmpty()) {
                                showFieldError(
                                    mAutoComTextViewModel,
                                    textInputLayoutModel,
                                    R.string.val_msg_please_enter_model
                                )
                                return false
                            }
                        }

                        "color" -> {
                            val colorText = textOf(mAutoComTextViewColor)
                            if (colorText.isEmpty() || mSelectedColor.isNullOrEmpty()) {
                                showFieldError(
                                    mAutoComTextViewColor,
                                    textInputLayoutColor,
                                    R.string.val_msg_please_enter_color
                                )
                                return false
                            }
                        }

                        "time_limit_select" -> {
                            if (textOf(mAutoComTextViewTimeLimit).isEmpty()) {
                                showFieldError(
                                    mAutoComTextViewTimeLimit,
                                    textInputLayoutTimeLimit,
                                    R.string.val_msg_please_enter_time_limit
                                )
                                return false
                            }
                        }

                        "tier_stem_left" -> {
                            if (!isTireStemWithImageView) {
                                if (textOf(mAutoComTextViewTierLeft).isEmpty()) {
                                    showFieldError(
                                        mAutoComTextViewTierLeft,
                                        textInputLayoutTierLeft,
                                        R.string.val_msg_please_enter_tier_stem_left
                                    )
                                    return false
                                }
                            } else {
                                val frontVal = mFrontTireStemValue?.toIntOrNull() ?: 0
                                if (frontVal <= 0) {
                                    requireContext().toast(getString(R.string.val_msg_please_enter_tier_stem_left))
                                    return false
                                }
                            }
                        }

                        "tier_stem_right" -> {
                            if (!isTireStemWithImageView) {
                                if (textOf(mAutoComTextViewTierRight).isEmpty()) {
                                    showFieldError(
                                        mAutoComTextViewTierRight,
                                        textInputLayoutTierRight,
                                        R.string.val_msg_please_enter_tier_stem_right
                                    )
                                    return false
                                }
                            } else {
                                val rearVal = mRearTireStemValue?.toIntOrNull() ?: 0
                                if (rearVal <= 0) {
                                    requireContext().toast(getString(R.string.val_msg_please_enter_tier_stem_right))
                                    return false
                                }
                            }
                        }

                        "zone" -> {
                            if (textOf(mAutoComTextViewZone).isEmpty()) {
                                showFieldError(
                                    mAutoComTextViewZone,
                                    textInputLayoutZone,
                                    R.string.val_msg_please_enter_zone
                                )
                                return false
                            }
                        }

                        "remark" -> {
                            if (textOf(mAutoComTextViewRemarks).isEmpty()) {
                                showFieldError(
                                    mAutoComTextViewRemarks,
                                    textInputLayoutRemarks,
                                    R.string.val_msg_please_enter_remark1
                                )
                                return false
                            }
                        }

                        "remark_2" -> {
                            if (textOf(mAutoComTextViewRemarks2).isEmpty()) {
                                showFieldError(
                                    mAutoComTextViewRemarks2,
                                    textInputLayoutRemarks2,
                                    R.string.val_msg_please_enter_remark1
                                )
                                return false
                            }
                        }

                        // add additional field names here as needed
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return true
        }
        return true
    }

    //save Timing Data form if offline
    private fun saveTimingDataForm(mResponse: AddTimingRequest) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val model = AddTimingDatabaseModel().apply {
                        lprState = mResponse.lprState
                        lprNumber = mResponse.lprNumber
                        meterNumber = mResponse.meterNumber
                        block = mResponse.block
                        regulationTime = mResponse.regulationTime
                        street = mResponse.street
                        side = mResponse.side
                        zone = mResponse.zone
                        remark = mResponse.remark
                        mStatus = mResponse.status
                        latitude = mResponse.latitude
                        longitiude = mResponse.longitiude
                        source = mResponse.source
                        officerName = mResponse.officerName
                        badgeId = mResponse.badgeId
                        shift = mResponse.shift
                        supervisor = mResponse.supervisor
                        markStartTimestamp = mResponse.markStartTimestamp
                        markIssueTimestamp = mResponse.markIssueTimestamp
                        formStatus = 1
                        mLocation = mResponse.mLocation
                        mMake = mResponse.mMake
                        mColor = mResponse.mColor
                        mModel = mResponse.mModel
                        mAddress = mResponse.mAddress
                        id = timingDataIDForTable
                    }

                    try {
                        addTimeRecordScreenViewModel.insertTimingData(model)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun saveTimingImagesOffline() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    bannerList?.filterNotNull()?.forEach { image ->
                        try {
                            addTimeRecordScreenViewModel.insertTimingImage(image)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onDestroy() {
        layTimingLayout?.removeAllViews()
        super.onDestroy()
    }

    private fun lockTimeData(isLoadFromPref: Boolean, addTimingRequest: AddTimingRequest?) {
        // Return early if unlocked
        if (sharedPreference.read(SharedPrefKey.LOCK_GEO_ADDRESS, "")
                .equals("unlock", ignoreCase = true)
        ) return

        try {
            if (isLoadFromPref) {
                val mTimeData: AddTimingRequest? =
                    sharedPreference.readTime(SharedPrefKey.TIMING_DATA, "")
                mTimeData?.let { data ->
                    data.regulationTimeValue?.let { setDropdownRegulation(it) }
                    data.side?.let { mAutoComTextViewDirection?.setText(it) }
                    data.mLot?.let { mAutoComTextViewLocation?.setText(it) }
                    data.meterNumber?.let { mAutoComTextViewMeter?.setText(it) }
                }
            } else {
                sharedPreference.write(SharedPrefKey.TIMING_DATA, addTimingRequest)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showTireStemDropDown(mValue: String): PopupWindow {
        val inflater =
            requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.popup_category, null)
        val recyclerView = view.findViewById<RecyclerView>(R.id.rvCategory)

        viewLifecycleOwner.lifecycleScope.launch {
            val mApplicationList = mainActivityViewModel.getTierStemListFromDataSet()
            if (mApplicationList.isNullOrEmpty()) return@launch

            val sortedList = mApplicationList.sortedBy { it.tierStem }

            recyclerView?.post {
                val handleSelection: (DatasetResponse) -> Unit = { item ->
                    // safe parse tier to Int
                    val tierInt = try {
                        item.tierStem?.toInt()
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                        item.tierStem?.toIntOrNull()
                    } ?: 0

                    when {
                        mValue.equals("FRONT", true) -> {
                            mFrontTireStemValue = item.tierStem.toString()
                            textFrontTireStem.text = "$mFrontTireStemText = ${item.tierStem}"
                            setPositionOfStemValue(tierInt, appCompatTextViewCircleStemValueFront)
                            appCompatImageViewFrontTireStem.setImageResource(R.drawable.front_tire_red)
                            appCompatTextViewCircleStemValueFront.text = item.tierStem.toString()
                        }

                        mValue.equals("REAR", true) -> {
                            mRearTireStemValue = item.tierStem.toString()
                            textRearTireStem.text = "$mRearTireStemText = ${item.tierStem}"
                            setPositionOfStemValue(tierInt, appCompatTextViewCircleStemValueRear)
                            appCompatImageViewRearTireStem.setImageResource(R.drawable.back_tire_red)
                            appCompatTextViewCircleStemValueRear.text = item.tierStem.toString()
                        }

                        mValue.equals("VALVE", true) -> {
                            mValveTireStemValue = item.tierStem.toString()
                            textRearValveStem.text = "Valve Stem = ${item.tierStem}"
                        }
                    }
                    dismissPopup()
                }

                val adapter = TireStemAdapter(
                    requireContext(), sortedList, object : TireStemAdapter.ListItemSelectListener {
                        override fun onItemClick(dataObject: DatasetResponse?, position: Int) {
                            dataObject?.let { handleSelection(it) }
                        }
                    })

                recyclerView.isNestedScrollingEnabled = false
                recyclerView.setHasFixedSize(true)
                recyclerView.layoutManager =
                    LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
                recyclerView.adapter = adapter
            }
        }

        return PopupWindow(
            view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    private fun dismissPopup() {
        showTireStemDropDown?.let {
            if (it.isShowing) {
                it.dismiss()
            }
            showTireStemDropDown = null
        }
    }

    private fun setPositionOfStemValue(
        selectedValue: Int, appCompatTextViewStemValue: AppCompatTextView
    ) {
        appCompatTextViewStemValue.visibility = View.VISIBLE
        when (selectedValue) {
            1, 15 -> {
                appCompatTextViewStemValue.x = (width * 0.55f)
                appCompatTextViewStemValue.y = (10f)
            }

            2, 30 -> {
                appCompatTextViewStemValue.x = (width * 0.65f)
                appCompatTextViewStemValue.y = (height * 0.2f)
            }

            3, 45 -> {
                appCompatTextViewStemValue.x = (width * 0.7f)
                appCompatTextViewStemValue.y = (height * 0.38f)
            }

            4, 60 -> {
                appCompatTextViewStemValue.x = (width * 0.65f)
                appCompatTextViewStemValue.y = (height * 0.6f)
            }

            5, 75 -> {
                appCompatTextViewStemValue.x = (width * 0.55f)
                appCompatTextViewStemValue.y = (height * 0.72f)
            }

            6, 90 -> {
                appCompatTextViewStemValue.x = (width * 0.41f)
                appCompatTextViewStemValue.y = (height * 0.8f)
            }

            7, 105 -> {
                appCompatTextViewStemValue.x = (width * 0.24f)
                appCompatTextViewStemValue.y = (height * 0.72f)
            }

            8, 120 -> {
                appCompatTextViewStemValue.x = (width * 0.16f)
                appCompatTextViewStemValue.y = (height * 0.6f)
            }

            9, 135 -> {
                appCompatTextViewStemValue.x = (width * 0.12f)
                appCompatTextViewStemValue.y = (height * 0.4f)
            }

            10, 150 -> {
                appCompatTextViewStemValue.x = (width * 0.16f)
                appCompatTextViewStemValue.y = (height * 0.2f)
            }

            11, 165 -> {
                appCompatTextViewStemValue.x = (width * 0.27f)
                appCompatTextViewStemValue.y = (height * 0.1f)
            }

            12, 180 -> {
                appCompatTextViewStemValue.x = (width * 0.41f)
                appCompatTextViewStemValue.y = (height * 0.06f)
            }

        }
    }

    private fun consumeResponse(newApiResponse: NewApiResponse<Any>) {
        requireActivity().hideSoftKeyboard()

        when (newApiResponse) {
            is NewApiResponse.Idle -> {
                DialogUtil.hideLoader()
            }

            is NewApiResponse.Loading -> {
                DialogUtil.showLoader(
                    context = requireContext(),
                    message = getString(R.string.loader_text_please_wait_we_are_loading_data)
                )
            }

            is NewApiResponse.Success -> {
                try {
                    when (newApiResponse.apiNameTag) {
                        API_TAG_NAME_ADD_TIMING -> {
                            handleAddTimingResponse(newApiResponse.data as JsonNode)
                        }

                        API_TAG_NAME_UPLOAD_IMAGES -> {
                            handleUploadImagesResponse(newApiResponse.data as JsonNode)
                        }
                    }
                } catch (e: JsonMappingException) {
                    requireContext().toast(message = getString(R.string.error_desc_please_login_again_to_use_the_application))
                    viewLifecycleOwner.lifecycleScope.launch {
                        mainActivityViewModel.sendActionToMain(MainActivityAction.EventProcessLogout)
                    }
                    e.printStackTrace()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            is NewApiResponse.ApiError -> {
                DialogUtil.hideLoader()
                AlertDialogUtils.showDialog(
                    context = requireContext(),
                    title = getString(R.string.error_title_api_error),
                    message = getString(
                        R.string.error_desc_api_error,
                        newApiResponse.code.toString(),
                        newApiResponse.getErrorMessage()
                            .nullSafety(getString(R.string.error_desc_something_went_wrong))
                    ),
                    positiveButtonText = getString(R.string.button_text_ok)
                )
            }

            is NewApiResponse.NetworkError -> {
                DialogUtil.hideLoader()

                AlertDialogUtils.showDialog(
                    context = requireContext(),
                    title = getString(R.string.error_title_network_error),
                    message = getString(
                        R.string.error_desc_network_error,
                        newApiResponse.exception.message.nullSafety(getString(R.string.error_desc_something_went_wrong))
                    ),
                    positiveButtonText = getString(R.string.button_text_ok)
                )
            }

            is NewApiResponse.UnknownError -> {
                DialogUtil.hideLoader()

                AlertDialogUtils.showDialog(
                    context = requireContext(),
                    title = getString(R.string.error_title_unknown_error),
                    message = getString(
                        R.string.error_desc_unknown_error,
                        newApiResponse.throwable.localizedMessage.nullSafety(getString(R.string.error_desc_something_went_wrong))
                    ),
                    positiveButtonText = getString(R.string.button_text_ok)
                )
            }
        }
    }

    private fun handleAddTimingResponse(jsonNodeValue: JsonNode) {
        val responseModel = runCatching {
            ObjectMapperProvider.fromJson(
                jsonNodeValue.toString(), AddTimingResponse::class.java
            )
        }.getOrNull()

        if (responseModel != null && responseModel.success.nullSafety()) {
            viewLifecycleOwner.lifecycleScope.launch {
                addTimeRecordScreenViewModel.updateTimingUploadStatus(0, timingDataIDForTable)

                if (responseModel.data!!.isAbandonVehicle == true) {
                    val myJson = ObjectMapperProvider.toJson(mAddTimingRequest!!)
                    val bundle = Bundle()
                    bundle.putString("timeData", myJson)
                    nav.safeNavigate(
                        R.id.action_bootScreenFragment_to_citationFormScreenFragment, bundle
                    )
                } else {
                    mainActivityViewModel.backButtonPressed()
                }
            }

        } else {
            AlertDialogUtils.showDialog(
                context = requireContext(),
                title = getString(R.string.error_title_add_time_record_api_response),
                message = responseModel?.response.nullSafety(getString(R.string.err_msg_something_went_wrong)),
                positiveButtonText = getString(R.string.button_text_ok)
            )

        }
    }

    private fun handleUploadImagesResponse(jsonNodeValue: JsonNode) {
        val responseModel = runCatching {
            ObjectMapperProvider.fromJson(
                jsonNodeValue.toString(), UploadImagesResponse::class.java
            )
        }.getOrNull()

        val ctx = requireContext()
        val title = getString(R.string.error_title_upload_image_api_response)

        if (responseModel?.status == true) {
            val link = responseModel.data?.firstOrNull()?.response?.links?.firstOrNull()
            if (!link.isNullOrEmpty()) {
                mImages.add(link)
                if (mImages.size == (bannerList?.size ?: 0)) {
                    viewLifecycleOwner.lifecycleScope.launch {
                        val result =
                            addTimeRecordScreenViewModel.deleteTimingImagesWithTimingRecordId(
                                timingDataIDForTable
                            )
                        bannerList?.clear()
                        callAddTimingApi()
                    }
                }
            } else {
                AlertDialogUtils.showDialog(
                    context = ctx,
                    title = title,
                    message = responseModel.message.nullSafety(getString(R.string.error_desc_getting_empty_image_array_from_server)),
                    positiveButtonText = getString(R.string.button_text_ok)
                )
            }
        } else {
            AlertDialogUtils.showDialog(
                context = ctx,
                title = title,
                message = responseModel?.message.nullSafety(getString(R.string.err_msg_something_went_wrong)),
                positiveButtonText = getString(R.string.button_text_ok)
            )
        }
    }
}