package com.parkloyalty.lpr.scan.ui.check_setup.activity

import java.text.DecimalFormat

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.os.PersistableBundle
import android.view.View
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.activity.viewModels
import androidx.appcompat.widget.*
import androidx.viewpager.widget.ViewPager
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.google.android.material.textfield.TextInputEditText
import com.parkloyalty.lpr.scan.R
import com.parkloyalty.lpr.scan.basecontrol.BaseActivity
import com.parkloyalty.lpr.scan.basecontrol.BaseApplication
import com.parkloyalty.lpr.scan.database.AppDatabase
import com.parkloyalty.lpr.scan.extensions.hideView
import com.parkloyalty.lpr.scan.extensions.nullSafety
import com.parkloyalty.lpr.scan.extensions.showView
import com.parkloyalty.lpr.scan.interfaces.CustomDialogHelper
import com.parkloyalty.lpr.scan.network.ApiResponse
import com.parkloyalty.lpr.scan.network.DynamicAPIPath
import com.parkloyalty.lpr.scan.network.Status
import com.parkloyalty.lpr.scan.ui.check_setup.activity.model.AbandonedVehicleViewModel
import com.parkloyalty.lpr.scan.ui.check_setup.adapter.AbandonedVehicleBannerAdapter
import com.parkloyalty.lpr.scan.ui.check_setup.model.timing.AddTimingRequest
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.CitationLayoutData
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DatasetResponse
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.TimingImagesModel
import com.parkloyalty.lpr.scan.ui.login.model.WelcomeForm
import com.parkloyalty.lpr.scan.util.*
import java.lang.reflect.Type
import java.text.ParseException
import java.util.*
import androidx.lifecycle.Observer
import com.parkloyalty.lpr.scan.BuildConfig
import com.parkloyalty.lpr.scan.database.Singleton
import com.parkloyalty.lpr.scan.extensions.DuncanBrandingApp13
import com.parkloyalty.lpr.scan.interfaces.Constants
import com.parkloyalty.lpr.scan.interfaces.SharedPrefKey
import com.parkloyalty.lpr.scan.network.NetworkCheck
import com.parkloyalty.lpr.scan.ui.check_setup.activity.model.AbandonedVehicleResponse
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.CitationImagesModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DropdownDatasetResponse
import com.parkloyalty.lpr.scan.ui.ticket.model.*
import com.parkloyalty.lpr.scan.utils.ObjectMapperProvider
import java.text.SimpleDateFormat
import kotlin.getValue


class AbandonedVehicleActivity : BaseActivity(), CustomDialogHelper {

    @JvmField
    @BindView(R.id.layTimingLayout)
    var layTimingLayout: LinearLayoutCompat? = null

    @JvmField
    @BindView(R.id.layBottomKeyboard)
    var layBottomKeyboard: LinearLayoutCompat? = null

    @BindView(R.id.pager_banner)
    lateinit var mViewPagerBanner: ViewPager

    @BindView(R.id.viewPagerCountDots)
    lateinit var pagerIndicator: LinearLayoutCompat

    @BindView(R.id.ll_tire_stem)
    lateinit var linearLayoutCompatTireStem: LinearLayoutCompat

    @BindView(R.id.appcomptext_front_tire)
    lateinit var textFrontTireStem: AppCompatTextView

    @BindView(R.id.appcomptext_rear_tire)
    lateinit var textRearTireStem: AppCompatTextView

    @BindView(R.id.appcomptext_valve)
    lateinit var textRearValveStem: AppCompatTextView

    @BindView(R.id.appcomimgview_front)
    lateinit var appCompatImageViewFrontTireStem: AppCompatImageView

    @BindView(R.id.appcomimgview_rear)
    lateinit var appCompatImageViewRearTireStem: AppCompatImageView

    @BindView(R.id.appcomimgview_tire_valve)
    lateinit var appCompatImageViewValveStem: AppCompatImageView

    @BindView(R.id.textstemvaluefront)
    lateinit var appCompatTextViewCircleStemValueFront: AppCompatTextView

    @BindView(R.id.textstemvaluerear)
    lateinit var appCompatTextViewCircleStemValueRear: AppCompatTextView

    private val mModelList: MutableList<DatasetResponse>? = ArrayList()
    private var mContext: Context? = null
    private var mSelectedMakeValue = ""
    private var mSelectedMake: String? = ""
    private var mSelectedModel: String? = ""
    private var mSelectedColor: String? = ""
    private val mViolationCode = ""
    private var mAddress: String? = ""
    private var mFrontTireStemText: String? = ""
    private var mRearTireStemText: String? = ""
    private var mFrontTireStemValue: String? = ""
    private var mRearTireStemValue: String? = ""
    private var mValveTireStemValue: String? = ""
    private var mAutoComTextViewMeter: TextInputEditText? = null
    private var mAutoComTextViewLocation: TextInputEditText? = null
    private var mAutoComTextViewBlock: TextInputEditText? = null
    private var mAutoComTextViewStreet: TextInputEditText? = null
    private var mAutoComTextViewDirection: TextInputEditText? = null
    private var mAutoComTextViewLicNo: TextInputEditText? = null
    private var mAutoComTextViewLicState: TextInputEditText? = null
    private var mAutoComTextViewVin: TextInputEditText? = null
    private var mAutoComTextViewTimeLimit: TextInputEditText? = null
    private var mAutoComTextViewTierLeft: TextInputEditText? = null
    private var mAutoComTextViewTierRight: TextInputEditText? = null
    private var mAutoComTextViewZone: TextInputEditText? = null
    private var mAutoComTextViewRemarks: TextInputEditText? = null
    private var mAutoComTextViewRemarks2: TextInputEditText? = null
    private var mAutoComTextViewColor: TextInputEditText? = null
    private var mAutoComTextViewMake: TextInputEditText? = null
    private var mAutoComTextViewModel: TextInputEditText? = null
    private var latestLayoutTimings = 0
    private var mTimingImages: MutableList<String> = ArrayList()
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
    private var mBannerAdapter: AbandonedVehicleBannerAdapter? = null
    private var mShowBannerCount = 0
    private var mDotsCount = 0
    private var mDots: Array<ImageView?>? = null
    private var mTimer: Timer? = null
    private var mList: MutableList<TimingImagesModel> = ArrayList()
    private val mImages: MutableList<String> = ArrayList()
    private var showTireStemDropDown: PopupWindow? = null
    private  var width: Int = 0
    private var height: Int = 0
    private var isTireStemWithImageView = false
    private var objAbandonedVehicle:AddTimingRequest?=null

    private val mAbandonedVehicleViewModel: AbandonedVehicleViewModel? by viewModels()
    private val mDownloadBitmapFIleViewModel: DownloadBitmapFIleViewModel? by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_abandoned_vehicle)
        setFullScreenUI()
        ButterKnife.bind(this)
        mContext = this
        mDb = BaseApplication.instance?.getAppDatabase()
        addObservers()
        settingFile()
        getIntentData()

    }

    private fun addObservers() {
        mAbandonedVehicleViewModel?.response?.observe(this, addAbandonedResponseObserver)
        mDownloadBitmapFIleViewModel?.response?.observe(this, downloadBitmapFileResponseObserver)
    }

    override fun removeObservers() {
        super.removeObservers()
        mAbandonedVehicleViewModel?.response?.removeObserver(addAbandonedResponseObserver)
        mDownloadBitmapFIleViewModel?.response?.removeObserver(downloadBitmapFileResponseObserver)
    }

    private val addAbandonedResponseObserver = Observer { apiResponse: ApiResponse ->
        consumeResponse(apiResponse,
            DynamicAPIPath.GET_abandoned_HIT)
    }

    private val downloadBitmapFileResponseObserver = Observer { apiResponse: ApiResponse ->
        consumeResponse(
            apiResponse,
            DynamicAPIPath.POST_DOWNLOAD_FILE
        )
    }

    private fun getIntentData() {
        try {
             objAbandonedVehicle = ObjectMapperProvider.fromJson(intent.getStringExtra("timeData").nullSafety(), AddTimingRequest::class.java)

            mAbandonedVehicleViewModel!!.hitAbandonedHitApi("lp_number="+ objAbandonedVehicle!!.lprNumber)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun init() {
        setToolbar()
        mWelcomeFormData = mDb?.dbDAO?.getWelcomeForm()
        try {
            mStartTime = AppUtils.splitDateLpr("")
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        val response = mDb?.dbDAO?.getTimingLayout()
        if (response != null) {
            if (response.data!![0].response?.size.nullSafety() > 0) {
                mCitationLayout = response.data!![0].response

            }
        }

        timingDataIDForTable = mDb?.dbDAO?.getLastIDFromTimingData().nullSafety() + 1
    }


    @OnClick(R.id.ivCamera,R.id.btnSubmit)
    fun onClick(view: View) {
        val id = view.id
        when (id) {

            R.id.btnSubmit ->{
                moveToNext("")
//                finish()
            }
            R.id.ivCamera -> {
            }

        }
    }



    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
    }

    private fun setBannerImageAdapter() {
        mBannerAdapter = AbandonedVehicleBannerAdapter(
            this@AbandonedVehicleActivity,
            object : AbandonedVehicleBannerAdapter.ListItemSelectListener {
                override fun onItemClick(position: Int) {
                    bannerList?.removeAt(position)
                    //mDb?.dbDAO?.deleteTempTimingImagesWithId(bannerList!![position]!!.id)
                    setCameraImages()
                }
            })
    }

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

        if (mList.isNotEmpty() && mBannerAdapter != null) {
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

    /**
     * TODO Create UI
     */
    private fun createLayoutUI() {
        if (mCitationLayout!!.isNotEmpty()) {
            for (iCit in mCitationLayout!!.indices) {
//                showProgressLoader(getString(R.string.scr_message_please_wait))

                layTimingLayout?.post {
                    if (mCitationLayout!![iCit].component.equals(
                            "marking_vehicle",ignoreCase = true)
                        || mCitationLayout!![iCit].component.equals("Location", ignoreCase = true)
                        || mCitationLayout!![iCit].component.equals("Vehicle", ignoreCase = true)
                    ) {
                        layTimingLayout?.visibility = View.VISIBLE
//                        mBtnSubmit?.visibility = View.VISIBLE
                        for (iOff in mCitationLayout!![iCit].fields?.indices!!) {
                            if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                    "make",ignoreCase = true)) {
                                mAutoComTextViewMake = ConstructLayoutBuilder.CheckTypeOfFieldAbandoned(
                                    mCitationLayout!![iCit].fields!![iOff],
                                    layTimingLayout, mCitationLayout!![iCit].component!!, mContext
                                )
//                                mAutoComTextViewMake!!.setText(objAbandonedVehicle!!.mMake)
                                mAutoComTextViewMake!!.setText(responseModel!!.data!!.make)
                            } else if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                    "model",ignoreCase = true)) {
                                mAutoComTextViewModel = ConstructLayoutBuilder.CheckTypeOfFieldAbandoned(
                                    mCitationLayout!![iCit].fields!![iOff],
                                    layTimingLayout, mCitationLayout!![iCit].component!!, mContext
                                )
//                                mAutoComTextViewModel!!.setText(objAbandonedVehicle!!.mModel)
                                mAutoComTextViewModel!!.setText(responseModel!!.data!!.model)
                            } else if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                    "color",ignoreCase = true)) {
                                mAutoComTextViewColor = ConstructLayoutBuilder.CheckTypeOfFieldAbandoned(
                                    mCitationLayout!![iCit].fields!![iOff],
                                    layTimingLayout, mCitationLayout!![iCit].component!!, mContext
                                )
//                                mAutoComTextViewColor!!.setText(objAbandonedVehicle!!.mColor)
                                mAutoComTextViewColor!!.setText(responseModel!!.data!!.color)
                            } else if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                    "meter",ignoreCase = true )) {
                                mAutoComTextViewMeter = ConstructLayoutBuilder.CheckTypeOfFieldAbandoned(
                                    mCitationLayout!![iCit].fields!![iOff],
                                    layTimingLayout, mCitationLayout!![iCit].component!!, mContext
                                )
//                                mAutoComTextViewMeter!!.setText(objAbandonedVehicle!!.meterNumber)
                                mAutoComTextViewMeter!!.setText(responseModel!!.data!!.meterNumber)
                            } else if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                    "location",ignoreCase = true)
                                || mCitationLayout!![iCit].fields!![iOff].name.equals(
                                    "lot",ignoreCase = true)) {
                                mAutoComTextViewLocation = ConstructLayoutBuilder.CheckTypeOfFieldAbandoned(
                                    mCitationLayout!![iCit].fields!![iOff],
                                    layTimingLayout, mCitationLayout!![iCit].component!!, mContext
                                )
//                                mAutoComTextViewLocation!!.setText(objAbandonedVehicle!!.mLot)
                                mAutoComTextViewLocation!!.setText(responseModel!!.data!!.lot)
                            } else if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                    "block",ignoreCase = true)) {
                                mAutoComTextViewBlock = ConstructLayoutBuilder.CheckTypeOfFieldAbandoned(
                                    mCitationLayout!![iCit].fields!![iOff],
                                    layTimingLayout, mCitationLayout!![iCit].component!!, mContext)

//                                mAutoComTextViewBlock?.filters = arrayOf<InputFilter>(InputFilter.AllCaps())
//                                mAutoComTextViewBlock!!.setText(objAbandonedVehicle!!.block)
                                mAutoComTextViewBlock!!.setText(responseModel!!.data!!.block)

                            } else if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                    "street",ignoreCase = true) ||
                                mCitationLayout!![iCit].fields!![iOff].name.equals(
                                    "street_textbox",ignoreCase = true)) {
                                mAutoComTextViewStreet = ConstructLayoutBuilder.CheckTypeOfFieldAbandoned(
                                    mCitationLayout!![iCit].fields!![iOff],
                                    layTimingLayout, mCitationLayout!![iCit].component!!, mContext)
                                try {
//                                    mAutoComTextViewStreet?.filters = arrayOf<InputFilter>(
//                                        InputFilter.AllCaps())
//                                    mAutoComTextViewStreet!!.setText(objAbandonedVehicle!!.street)
                                    mAutoComTextViewStreet!!.setText(responseModel!!.data!!.street)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            } else if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                    "side_of_street",ignoreCase = true) ||
                                mCitationLayout!![iCit].fields!![iOff].name.equals(
                                    "side", ignoreCase = true) ||
                                mCitationLayout!![iCit].fields!![iOff].name.equals(
                                    "direction", ignoreCase = true)) {
                                mAutoComTextViewDirection = ConstructLayoutBuilder.CheckTypeOfFieldAbandoned(
                                    mCitationLayout!![iCit].fields!![iOff],
                                    layTimingLayout, mCitationLayout!![iCit].component!!, mContext)
//                                mAutoComTextViewDirection!!.setText(objAbandonedVehicle!!.side)
                                mAutoComTextViewDirection!!.setText(responseModel!!.data!!.side)
                            }  else if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                    "lic_no",ignoreCase = true)
                                || mCitationLayout!![iCit].fields!![iOff].name.equals(
                                    "lp_number",ignoreCase = true)) {
                                mAutoComTextViewLicNo = ConstructLayoutBuilder.CheckTypeOfFieldAbandoned(
                                    mCitationLayout!![iCit].fields!![iOff],
                                    layTimingLayout, mCitationLayout!![iCit].component!!, mContext
                                )
                                mAutoComTextViewLicNo!!.setText(objAbandonedVehicle!!.lprNumber)
                            } else if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                    "lic_state",ignoreCase = true)
                                || mCitationLayout!![iCit].fields!![iOff].name.equals(
                                    "state",ignoreCase = true)) {
                                mAutoComTextViewLicState = ConstructLayoutBuilder.CheckTypeOfFieldAbandoned(
                                    mCitationLayout!![iCit].fields!![iOff],
                                    layTimingLayout, mCitationLayout!![iCit].component!!, mContext
                                )!!
//                                mAutoComTextViewLicState!!.setText(objAbandonedVehicle!!.lprState)
                                mAutoComTextViewLicState!!.setText(responseModel?.data?.lpState)

                            } else if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                    "vin",ignoreCase = true) || mCitationLayout!![iCit].fields!![iOff].name.equals(
                                    "vin_number",ignoreCase = true)) {
                                mAutoComTextViewVin = ConstructLayoutBuilder.CheckTypeOfFieldAbandoned(
                                    mCitationLayout!![iCit].fields!![iOff],
                                    layTimingLayout, mCitationLayout!![iCit].component!!, mContext
                                )
//                                mAutoComTextViewVin!!.setText(objAbandonedVehicle!!.mVin)
                                mAutoComTextViewVin!!.setText(responseModel!!.data!!.vinNumber)
                            } else if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                    "time_limit_select",ignoreCase = true)) {
                                mAutoComTextViewTimeLimit = ConstructLayoutBuilder.CheckTypeOfFieldAbandoned(
                                    mCitationLayout!![iCit].fields!![iOff],
                                    layTimingLayout, mCitationLayout!![iCit].component!!, mContext
                                )
//                                mAutoComTextViewTimeLimit!!.setText(objAbandonedVehicle!!.regulationTimeValue)
                                mAutoComTextViewTimeLimit!!.setText(responseModel!!.data!!.regulationTime.toString())
                                //setFocus(mAutoComTextViewShift);
                            } else if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                    "tier_stem_left",ignoreCase = true)) {
                                if(isTireStemWithImageView == false) {
                                    mAutoComTextViewTierLeft = ConstructLayoutBuilder.CheckTypeOfFieldAbandoned(
                                        mCitationLayout!![iCit].fields!![iOff],
                                        layTimingLayout, mCitationLayout!![iCit].component!!, mContext)
//                                    mAutoComTextViewTierLeft!!.setText(objAbandonedVehicle!!.mTireStemFront)
                                    mAutoComTextViewTierLeft!!.setText(responseModel!!.data!!.tireStemFront.toString())
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
                                        viewTreeObserver.addOnGlobalLayoutListener(object :
                                            ViewTreeObserver.OnGlobalLayoutListener {
                                            override fun onGlobalLayout() {
                                                if(width<=0) {

                                                    appCompatImageViewValveStem.getViewTreeObserver()
                                                        .removeGlobalOnLayoutListener(this)
                                                    width =
                                                        appCompatImageViewValveStem.getMeasuredWidth()
                                                    height =
                                                        appCompatImageViewValveStem.getMeasuredHeight()
                                                    setPositionOfStemValue(responseModel!!.data!!.tireStemFront!!.toInt(), appCompatTextViewCircleStemValueFront)
                                                    setPositionOfStemImage(responseModel!!.data!!.tireStemFront!!.toInt(),
                                                        objAbandonedVehicle!!.mTireStemBack.toInt(), appCompatImageViewFrontTireStem)
                                                    textFrontTireStem.text = mFrontTireStemText + " = " + responseModel!!.data!!.tireStemFront
                                                    appCompatTextViewCircleStemValueFront.text =  responseModel!!.data!!.tireStemFront.toString()
                                                }
                                            }
                                        })
                                    }

//                                    setPositionOfStemValue(objAbandonedVehicle!!.mTireStemFront.toInt(), textFrontTireStem)
                                }
                                //setFocus(mAutoComTextViewAgency);
                            } else if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                    "tier_stem_right",ignoreCase = true)) {
                                if(isTireStemWithImageView == false) {
                                    mAutoComTextViewTierRight = ConstructLayoutBuilder.CheckTypeOfFieldAbandoned(
                                        mCitationLayout!![iCit].fields!![iOff],
                                        layTimingLayout, mCitationLayout!![iCit].component!!, mContext)
                                    mAutoComTextViewTierRight!!.setText(responseModel!!.data!!.tireStemBack.toString())
                                }else {
                                    linearLayoutCompatTireStem!!.visibility = View.VISIBLE
                                    if (mCitationLayout!![iCit].fields!![iOff]!!.isRequired.nullSafety()) {
                                        textRearTireStem.setText(mCitationLayout!![iCit].fields!![iOff]!!.repr + "*")
                                    } else {
                                        textRearTireStem.setText(mCitationLayout!![iCit].fields!![iOff]!!.repr)
                                    }
                                    mRearTireStemText = textRearTireStem!!.text!!.toString()

                                    textRearTireStem.text = mRearTireStemText + " = " + responseModel!!.data!!.tireStemBack

                                    val viewTreeObserver: ViewTreeObserver = appCompatImageViewValveStem.getViewTreeObserver()
                                    linearLayoutCompatTireStem.visibility = View.VISIBLE
                                    viewTreeObserver.addOnGlobalLayoutListener(object :
                                        ViewTreeObserver.OnGlobalLayoutListener {
                                        override fun onGlobalLayout() {
                                            setPositionOfStemValue(responseModel!!.data!!.tireStemBack!!.toInt(), appCompatTextViewCircleStemValueRear)
                                            appCompatTextViewCircleStemValueRear.text =  responseModel!!.data!!.tireStemBack.toString()
                                        }
                                    })
                                }
                            } else if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                    "zone",ignoreCase = true)) {
                                mAutoComTextViewZone = ConstructLayoutBuilder.CheckTypeOfFieldAbandoned(
                                    mCitationLayout!![iCit].fields!![iOff],
                                    layTimingLayout, mCitationLayout!![iCit].component!!, mContext
                                )
//                                setFocus(mAutoComTextViewZone)
                                mAutoComTextViewZone?.setText(if (mWelcomeFormData != null) mWelcomeFormData!!.officerZone else "")
                            } else if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                    "remark",ignoreCase = true )) {
                                //mCitationLayout.get(finalICit).getFields().get(iOff).setTag("dropdown");
                                mAutoComTextViewRemarks = ConstructLayoutBuilder.CheckTypeOfFieldAbandoned(
                                    mCitationLayout!![iCit].fields!![iOff],
                                    layTimingLayout, mCitationLayout!![iCit].component!!, mContext
                                )
                                mAutoComTextViewRemarks!!.setText(responseModel!!.data!!.remark)
                            } else if (mCitationLayout!![iCit].fields!![iOff].name.equals(
                                    "remark_2",ignoreCase = true )) {
                                //mCitationLayout.get(finalICit).getFields().get(iOff).setTag("dropdown");
                                mAutoComTextViewRemarks2 = ConstructLayoutBuilder.CheckTypeOfFieldAbandoned(
                                    mCitationLayout!![iCit].fields!![iOff],
                                    layTimingLayout, mCitationLayout!![iCit].component!!, mContext
                                )
                                mAutoComTextViewRemarks2!!.setText(responseModel!!.data!!.remark2)
                            } else {
//                                try {
//                                    latestLayoutTimings++
//                                    name!![latestLayoutTimings] =
//                                        ConstructLayoutBuilder.CheckTypeOfFieldAbandoned(
//                                            mCitationLayout!![iCit].fields!![iOff],
//                                            layTimingLayout!!,
//                                            mCitationLayout!![iCit].component!!,
//                                            mContext!!
//                                        )!!
//                                } catch (e: Exception) {
//                                }
                            }
                            if (iOff == mCitationLayout!![iCit].fields?.size!! - 1) {
//                                getDatasetFromDb()
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

    private fun setPositionOfStemValue(selectedValue:Int,appCompatTextViewStemValue:AppCompatTextView)
    {
        try {
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
//        appCompatTextViewStemValue.x = (65f-26f)
//        appCompatTextViewStemValue.y = (8f)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    private fun setPositionOfStemImage(selectedValueFront:Int,selectedValueRear:Int,
                                       appComImgViewTireIcon:AppCompatImageView) {

        try {
            if (selectedValueFront>0 && selectedValueRear>0) {
                appComImgViewTireIcon.setImageResource(R.drawable.look_both_tire_red)
            } else if (selectedValueFront>0 && selectedValueRear<=0) {
                appComImgViewTireIcon.setImageResource(R.drawable.look_front_tire_red)
            } else if (selectedValueFront<=0 && selectedValueRear>0) {
                appComImgViewTireIcon.setImageResource(R.drawable.look_rear_tire_red)
            }else{
                appComImgViewTireIcon.setImageResource(R.drawable.both_tire_black)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun settingFile(){
        try {
            var settingsList: List<DatasetResponse>? = ArrayList()
            settingsList = Singleton.getDataSetList(DATASET_SETTINGS_LIST, getMyDatabase())
            if (settingsList != null && settingsList.size > 0) {
                for (i in settingsList.indices) {
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
                init()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    var responseModel:AbandonedVehicleResponse? = null
    /*Api response */
    private fun consumeResponse(apiResponse: ApiResponse, tag: String) {
        when (apiResponse.status) {
            Status.LOADING -> showProgressLoader(getString(R.string.scr_message_please_wait))
            Status.SUCCESS -> {
                dismissLoader()
                if (!apiResponse.data!!.isNull) {
                    LogUtil.printLog(tag, apiResponse.data.toString())
                    try {
                        if (tag.equals(DynamicAPIPath.GET_abandoned_HIT, ignoreCase = true)) {


                             responseModel = ObjectMapperProvider.fromJson(apiResponse.data.toString(), AbandonedVehicleResponse::class.java)

                            if (responseModel != null && responseModel!!.success.nullSafety()) {
                                mTimingImages = responseModel!!.data!!.latestMarkImages as MutableList<String>
                                mTimingImages.addAll(responseModel!!.data!!.firstMarkImages as MutableList<String>)
                                callDownloadBitmapApi()
                                createLayoutUI()
                            } else if (responseModel != null && !responseModel!!.success.nullSafety()) {
                                val message: String
                                if (responseModel!!.data != null) {
                                    message =" API ERROR" //responseModel.data
                                    AppUtils.showCustomAlertDialog(
                                        mContext,
                                        getString(R.string.scr_lbl_abandoned_vehicle_details),
                                        message,
                                        getString(R.string.alt_lbl_OK),
                                        getString(R.string.scr_btn_cancel),
                                        this
                                    )
                                } else {
                                    message =  getString(R.string.err_msg_something_went_wrong)
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
                        if (tag.equals(DynamicAPIPath.POST_DOWNLOAD_FILE,
                                ignoreCase = true)) {

                            val responseModel = ObjectMapperProvider.fromJson(apiResponse.data.toString(), DownloadBitmapResponse::class.java)

                            if (responseModel != null && responseModel.isStatus) {
                                if (responseModel.metadata!![0].url!!.length > 0) {
                                    setServerTimingImageOnUI(responseModel.metadata)
                                    setBannerImageAdapter()
                                    setCameraImages()
                                    if(mBannerAdapter!=null)
                                    {
                                        mBannerAdapter?.notifyDataSetChanged()
                                    }
                                }
                            }
                            dismissLoader()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        dismissLoader()
//                        logout(mContext!!)
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

    private fun setServerTimingImageOnUI(metadataItems: List<MetadataItem>?) {
//        downloadedTimingBannerList?.clear()
        for (metadata in metadataItems!!) {
            val id = SimpleDateFormat("HHmmss", Locale.US).format(Date())
            val model = TimingImagesModel()
            model.status = 1
            model.id = id.toInt()
            model.timingImage = metadata.url
            bannerList?.add(model)
        }
//        setCameraImages()
    }

    private fun callDownloadBitmapApi() {
        if (NetworkCheck.isInternetAvailable(this@AbandonedVehicleActivity)) {
            if (mTimingImages!= null && mTimingImages.size > 0) {
                val downloadBitmapRequest = DownloadBitmapRequest()
                downloadBitmapRequest.downloadType = API_CONSTANT_DOWNLOAD_TYPE_TIMING_IMAGES
                //downloadBitmapRequest.downloadType = "CitationImages"
                val links = Links()
                if (mTimingImages.size > 0) links.img1 = mTimingImages[0]
                if (mTimingImages.size > 1) links.img2 = mTimingImages[1]
                if (mTimingImages.size > 2) links.img3 = mTimingImages[2]
                if (mTimingImages.size > 3) links.img4 = mTimingImages[3]
                if (mTimingImages.size > 4) links.img5 = mTimingImages[4]
                if (mTimingImages.size > 5) links.img6 = mTimingImages[5]
                if (mTimingImages.size > 6) links.img7 = mTimingImages[6]
                if (mTimingImages.size > 7) links.img8 = mTimingImages[7]
                if (mTimingImages.size > 8) links.img9 = mTimingImages[8]
                if (mTimingImages.size > 9) links.img10 = mTimingImages[9]
                if (mTimingImages.size > 10) links.img11 = mTimingImages[10]
                if (mTimingImages.size > 11) links.img12 = mTimingImages[11]
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

    private fun moveToNext(violationCode: String) {

        try {
            var mTimingRecordRemarkValue = ""
            var mTimingTireStem = ""
            if (responseModel!=null) {

                if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLENDALE, ignoreCase = true) ||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GLENDALE_POLICE, ignoreCase = true) ||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAMETRO, ignoreCase = true) ||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CORPUSCHRISTI, ignoreCase = true) ||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CLEMENS, ignoreCase = true) ||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_KALAMAZOO, ignoreCase = true) ||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BURBANK, ignoreCase = true) ||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WESTCHESTER, ignoreCase = true) ||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZLB, ignoreCase = true) ||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VALLEJOL, ignoreCase = true) ||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ENCINITAS, ignoreCase = true) ||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PHOENIX, ignoreCase = true) ||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANDIEGO, ignoreCase = true) ||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MILLBRAE, ignoreCase = true) ||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANMATEO_REDWOOD, ignoreCase = true) ||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANMATEO_REDWOOD, ignoreCase = true) ||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CITYOFSANDIEGO, ignoreCase = true) ||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_STORMWATER_DIVISION, ignoreCase = true) ||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CHULAVISTA, ignoreCase = true) ||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ROSEBURG, ignoreCase = true) ||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_COHASSET, ignoreCase = true) ||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MARTIN, ignoreCase = true) ||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_IRVINE, ignoreCase = true) ||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_NORTHERN_CALIFORNIA,ignoreCase = true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_SAN_DIEGO_ZOO,ignoreCase = true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANFRANCISCO,ignoreCase = true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_FRESNO,ignoreCase = true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_HGI_MANHATTAN,ignoreCase = true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_VALET_DIVISION,ignoreCase = true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_SAN_ANTONIO,ignoreCase = true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DALLAS,ignoreCase = true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SANIBEL,ignoreCase = true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SMYRNABEACH,ignoreCase = true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_VOLUSIA,ignoreCase = true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACE_LAKE_TAHOE,ignoreCase = true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ADOBE,ignoreCase = true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_FASHION_CITY,ignoreCase = true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PRRS,ignoreCase = true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_UPTOWN_ATLANTA,ignoreCase = true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_GREENBURGH_NY,ignoreCase = true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DEER_FIELD_BEACH_FL,ignoreCase = true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ACEAMAZON,ignoreCase = true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LEAVENWORTH, ignoreCase = true) ||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BELLINGHAM, ignoreCase = true) ||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_OCEANCITY, ignoreCase = true) ||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EPHRATA, ignoreCase = true) ||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_Easton, ignoreCase = true) ||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CLIFTON, ignoreCase = true) ||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DURANGO, ignoreCase = true) ||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LITTLEROCK, ignoreCase = true) ||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKPARKING, ignoreCase = true) ||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PARKX, ignoreCase = true) ||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RUTGERS, ignoreCase = true) ||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PEAKTEXAS, ignoreCase = true) ||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOTTLEWORKS_IN, ignoreCase = true) ||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CARTA, ignoreCase = true)) {
                    mTimingRecordRemarkValue = ""
//                        AppUtils.splitDateLPRTime(objAbandonedVehicle!!.markStartTimestamp.toString()) + " elapsed: " +
//                                AppUtils.isElapsTime(objAbandonedVehicle!!.markStartTimestamp!!,
//                                    responseModel!!.data!!.regulationTime!!, this@AbandonedVehicleActivity)
                } else  if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DUNCAN, ignoreCase = true)||
                    BuildConfig.FLAVOR.equals(DuncanBrandingApp13())||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PILOTPITTSBURGPA, ignoreCase = true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MACKAY_SAMPLE, ignoreCase = true)) {
                    mTimingRecordRemarkValue = responseModel!!.data!!.remark.toString()
                    mTimingTireStem =responseModel!!.data!!.remark2.toString()
                }

                val formatter = DecimalFormat("00")
                val aFormattedFront: String = formatter.format(responseModel!!.data!!.tireStemFront!!.toLong())
                val aFormattedBack: String = formatter.format(responseModel!!.data!!.tireStemBack!!.toLong())
                mTimingTireStem =
                    "Tire Stem :".plus((aFormattedFront.toString() + "/"
                            + aFormattedBack.toString()))
                if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BURBANK, ignoreCase = true) ||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_WESTCHESTER, ignoreCase = true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_LAZLB, ignoreCase = true) )
                {
                    mTimingTireStem = ""
                }
                sharedPreference.write(SharedPrefKey.isTimeLimitEnforcement, "true")
                if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DUNCAN, ignoreCase = true)||
                    BuildConfig.FLAVOR.equals(DuncanBrandingApp13())||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_PILOTPITTSBURGPA, ignoreCase = true)||
                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_MACKAY_SAMPLE, ignoreCase = true)) {
                    mTimingRecordRemarkValue = responseModel!!.data!!.remark.toString()
                    mTimingTireStem = responseModel!!.data!!.remark2.toString()
                }

                /**
                 * time for comment
                 */
                if (!violationCode.equals("time", true)) {
                    sharedPreference.write(SharedPrefKey.isTimeLimitEnforcement, "false")
                }
                sharedPreference.write(SharedPrefKey.VOIDANDREISSUEBYPLATE, false)
                sharedPreference.writeOverTimeParkingTicketDetails(
                    SharedPrefKey.TIMING_DATA_OVERTIME_PARKING_CHARLESTION,
                    AddTimingRequest()!!
                )
                val mIntent = Intent(this, LprDetails2Activity::class.java)
                mIntent.putExtra("make", responseModel!!.data!!.make)
                mIntent.putExtra("from_scr", "")
                if (mSelectedModel != null) {
                    mIntent.putExtra("model", responseModel!!.data!!.model)
                }
                mIntent.putExtra("color", responseModel!!.data!!.color)
                mIntent.putExtra("lpr_number", responseModel!!.data!!.lpNumber)
                mIntent.putExtra("violation_code", violationCode)
                mIntent.putExtra("address", responseModel!!.data!!.address)
                mIntent.putExtra("timing_record_value", mTimingRecordRemarkValue)
                mIntent.putExtra("timing_tire_stem_value", mTimingTireStem)
                mIntent.putExtra("Lot", responseModel!!.data!!.lot)
                mIntent.putExtra("Location", "")
                mIntent.putExtra("Space_id", responseModel!!.data!!.space)
                mIntent.putExtra("Meter", responseModel!!.data!!.meterNumber)
                mIntent.putExtra("Zone", responseModel!!.data!!.zone)
                mIntent.putExtra("Street", responseModel!!.data!!.street)
                mIntent.putExtra("SideItem", responseModel!!.data!!.side)
                mIntent.putExtra("Block", responseModel!!.data!!.block)
                mIntent.putExtra("Direction", responseModel!!.data!!.side)
                mIntent.putExtra("BodyStyle", "")
                mIntent.putExtra("TimingID", "")
                mIntent.putExtra("State", responseModel!!.data!!.lpState)
                mIntent.putExtra("Vin", responseModel!!.data!!.vinNumber)
                mIntent.putExtra("Note", "")
                mIntent.putExtra("Note1", "")
                mIntent.putExtra("VIOLATION", "")

//                if(responseModel!!.data!!.latestMarkImages!!.isNotEmpty().nullSafety() && responseModel!!.data!!.latestMarkImages!!.get(0)?.length ?: >200) {
//                    mIntent.putExtra(INTENT_KEY_TIMING_IMAGES_BASE64, "YES")
//                }else if(responseModel!!.data!!.latestMarkImages!!.isNotEmpty().nullSafety()) {
                    mIntent.putExtra(INTENT_KEY_TIMING_IMAGES, responseModel!!.data!!.latestMarkImages!! as ArrayList<out Parcelable>)
//                }

//                if (mListCitation?.isNotEmpty().nullSafety()) {
//                    mIntent.putExtra(INTENT_KEY_UNPAID_CITATION_COUNT, mListCitation?.count { it.status == API_CONSTANT_CITATION_STATUS_VALID }.nullSafety())
//                }
                mIntent.putExtra("vendor_name", "")

                startActivity(mIntent)
                finish()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}