package com.parkloyalty.lpr.scan.ui.ticket

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.app.DownloadManager
import android.app.RecoverableSecurityException
import android.content.Context
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.animation.AlphaAnimation
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresPermission
import androidx.appcompat.widget.*
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.parkloyalty.lpr.scan.BuildConfig
import com.parkloyalty.lpr.scan.R
import com.parkloyalty.lpr.scan.basecontrol.BaseActivity
import com.parkloyalty.lpr.scan.basecontrol.BaseApplication
import com.parkloyalty.lpr.scan.database.AppDatabase
import com.parkloyalty.lpr.scan.database.Singleton
import com.parkloyalty.lpr.scan.databinding.ActivityLoginBinding
import com.parkloyalty.lpr.scan.databinding.ActivityTicketDetailsBinding
import com.parkloyalty.lpr.scan.extensions.DuncanBrandingApp13
import com.parkloyalty.lpr.scan.extensions.hideView
import com.parkloyalty.lpr.scan.extensions.nullSafety
import com.parkloyalty.lpr.scan.extensions.showView
import com.parkloyalty.lpr.scan.extensions.toBooleanFromYesNo
import com.parkloyalty.lpr.scan.interfaces.*
import com.parkloyalty.lpr.scan.interfaces.Constants.Companion.FILE_NAME_FACSIMILE_PRINT_BITMAP
import com.parkloyalty.lpr.scan.interfaces.Constants.Companion.SETTINGS_FLAG_TIMING_RECORD_CARRY_FORWARD
import com.parkloyalty.lpr.scan.interfaces.Constants.Companion.SPACE_WITH_COMMA
import com.parkloyalty.lpr.scan.network.*
import com.parkloyalty.lpr.scan.network.NetworkCheck.isInternetAvailable
import com.parkloyalty.lpr.scan.startprinterfull.StarPrinterActivity
import com.parkloyalty.lpr.scan.ui.check_setup.activity.LprDetails2Activity
import com.parkloyalty.lpr.scan.ui.check_setup.license.LprScanActivity
import com.parkloyalty.lpr.scan.ui.check_setup.model.lpr_model.UploadImageViewModel
import com.parkloyalty.lpr.scan.ui.check_setup.model.lpr_model.UploadImagesResponse
import com.parkloyalty.lpr.scan.ui.check_setup.model.timing.AddTimingRequest
import com.parkloyalty.lpr.scan.ui.check_setup.model.timing.AddTimingViewModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.*
import com.parkloyalty.lpr.scan.ui.login.activity.WelcomeActivity
import com.parkloyalty.lpr.scan.ui.printer.PrinterType
import com.parkloyalty.lpr.scan.ui.printer.StarPrinterUseCase
import com.parkloyalty.lpr.scan.ui.printer.ZebraPrinterUseCase
import com.parkloyalty.lpr.scan.ui.reprint.ReprintReuploadActivity
import com.parkloyalty.lpr.scan.ui.ticket.model.*
import com.parkloyalty.lpr.scan.ui.unuploadimages.UnUploadImagesActivityView
import com.parkloyalty.lpr.scan.ui.xfprinter.XfPrinterUseCase
import com.parkloyalty.lpr.scan.util.ACTIVITY_LOG_CANCELLATION_REQUEST
import com.parkloyalty.lpr.scan.util.ACTIVITY_LOG_ISSUE_MORE
import com.parkloyalty.lpr.scan.util.ACTIVITY_LOG_REISSUE
import com.parkloyalty.lpr.scan.util.AppUtils
import com.parkloyalty.lpr.scan.util.DATASET_CANCEL_REASON_LIST
import com.parkloyalty.lpr.scan.util.DATASET_SETTINGS_LIST
import com.parkloyalty.lpr.scan.util.DATASET_VOID_AND_REISSUE_REASON_LIST
import com.parkloyalty.lpr.scan.util.LockLprModel
import com.parkloyalty.lpr.scan.util.LogUtil
import com.parkloyalty.lpr.scan.util.PermissionUtils
import com.parkloyalty.lpr.scan.util.permissions.BluetoothPermissionUtil
import com.parkloyalty.lpr.scan.util.setAccessibilityForTextInputLayoutDropdownButtons
import com.parkloyalty.lpr.scan.util.setAsAccessibilityHeading
import com.parkloyalty.lpr.scan.util.setCustomAccessibility
import com.parkloyalty.lpr.scan.utils.ObjectMapperProvider
import com.parkloyalty.lpr.scan.vehiclestickerscan.VehicleStickerScanActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.IOException
import java.net.URL
import java.util.*
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

/*Phase 2*/
//class TicketDetailsActivity : SwitchPrinterActivity(), PrintInterface {
//class TicketDetailsActivity : XfPrinterActivity(), PrintInterface {
@AndroidEntryPoint
class TicketDetailsActivity : BaseActivity(), PrintInterface {
//    class TicketDetailsActivity : StarPrinterActivity(), PrintInterface {

    var textViewTicketDetails: AppCompatTextView? = null
    var mTextViewVoilationDetails: AppCompatTextView? = null
    var mTextViewTicketNumber: AppCompatTextView? = null
    var mTextViewTicketDate: AppCompatTextView? = null
    var mTextViewAddress: AppCompatTextView? = null
    var mTextViewVehicle: AppCompatTextView? = null
    var mTextViewTicketSuccess: AppCompatImageView? = null
    var mTextViewTicketMsg: AppCompatTextView? = null
    var mTextViewSpace: AppCompatTextView? = null
    var mTextViewVin: AppCompatTextView? = null
    var mTextViewCitationType: AppCompatTextView? = null
    var mTextViewLot: AppCompatTextView? = null
    var layCitationOptions: LinearLayoutCompat? = null
    var layCitationButtons: LinearLayoutCompat? = null
    var mBtnIssueMore: AppCompatImageView? = null
    var mBtnCancelTicket: AppCompatButton? = null
    var mBtnVoidAndReissue: AppCompatButton? = null
    var mBTNBack: AppCompatImageView? = null
    var checkBoxDriveOff: CheckBox? = null
    var checkBoxTvr: CheckBox? = null
    var selectedCheckBoxValue: AppCompatTextView? = null
    var linearLayoutCompatPrint: LinearLayoutCompat? = null
    var linearLayoutCompatAddNote: LinearLayoutCompat? = null
    var ivPrintBtn: AppCompatImageView? = null
    var linearLayoutCompatIssueMore: LinearLayoutCompat? = null
    var linearLayoutCompatScan: LinearLayoutCompat? = null
    var linearLayoutCompatLot: LinearLayoutCompat? = null
    var linearLayoutCompatSpace: LinearLayoutCompat? = null
    var linearLayoutCompatVin: LinearLayoutCompat? = null
    var linearLayoutCompatCitationType: LinearLayoutCompat? = null
    var appCompatImageViewHomeIcon: AppCompatImageView? = null
    var mContentMainLayout: LinearLayoutCompat? = null
    var mCheckLinearLayout: LinearLayoutCompat? = null
    var appCompatPaperFeedButton: AppCompatButton? = null
    var btnCancellationRequest: AppCompatButton? = null
    var printProgressBitmapDownload: ProgressBar? = null
    lateinit var btnScanSticker: AppCompatButton

    private var mContext: Context? = null
    private var mCitationNumberId: String? = ""
    private var mTicketId: String? = ""
    private var mIssueTicketId: String? = ""
    private var mPrintBitmapPath: String? = null
    private var mMeter: String? = ""
    private var mVinNumber: String? = ""
    private var mAmount: String? = ""
    private var mSpace: String? = ""
    private var mLot: String? = ""
    private var mTicketNumber: String? = ""
    private var mTicketDate: String? = ""
    private var mVoilationDetails: String? = ""
    private var mLocationDetails: String? = ""
    private var mViolationDescription: String? = ""
    private var mMake: String? = ""
    private var mModel: String? = ""
    private var mColor: String? = ""
    private var mLprNumber: String? = ""
    private var mState: String? = ""
    private var mTicketType: String? = ""
    private var mVehicleBodyStyle: String? = ""
    private var printQuery: String? = ""
    private var mExpireYear: String? = ""
    private var mRescindButton: String? = ""
    private var mDb: AppDatabase? = null
    private var mScreenLabel: String? = ""
    private var m4ButtonType: String? = ""
    private var mTag = ""
    private var mVoidReissueSelectValue = ""
    private var uploadImageSize = 0
    private val OfflineCancelCitationModel = OfflineCancelCitationModel()
    private var driveOffTvrRequest = DriveOffTvrRequest()
    private var mImages: MutableList<String> = ArrayList()
    private var mCitaionImagesLinks: MutableList<String> = ArrayList()
    private var isFacsimileUploadLinkAvailable: Boolean = false

    private val mTicketCancelViewModel: TicketCancelViewModel? by viewModels()
    private val mTicketCancellationViewModel: TicketCancellationRequestViewModel? by viewModels()
    private val mUpdateMarkViewModel: UpdateTicketDataViewModel? by viewModels()
    private val mDownloadBitmapFIleViewModel: DownloadBitmapFIleViewModel? by viewModels()
    private val driveOffTVRViewModel: DriveOffTVRViewModel? by viewModels()
    private val addImageViewModel: AddImageViewModel? by viewModels()
    private val mUploadImageViewModel: UploadImageViewModel? by viewModels()
    private val mAddTimingViewModel: AddTimingViewModel? by viewModels()

    private var zebraPrinterUseCase: ZebraPrinterUseCase? = null
    private var starPrinterUseCase: StarPrinterUseCase? = null
    private var xfPrinterUseCase: XfPrinterUseCase? = null

    private val bluetoothPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val granted = permissions.entries.all { it.value }
            if (granted) {
                // Nothing to implement here
            } else {
                BluetoothPermissionUtil.requestBluetoothSetting(this@TicketDetailsActivity)
            }
        }

    private lateinit var binding: ActivityTicketDetailsBinding


    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTicketDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        findViewsByViewBinding()
        setupClickListeners()

        appCompatPaperFeedButton?.visibility = View.GONE

        if (LogUtil.getPrinterTypeForPrint() == PrinterType.STAR_PRINTER) {
            LogUtil.printLog("==>Printer","Inside")
            starPrinterUseCase = StarPrinterUseCase()
            starPrinterUseCase?.setPrintInterfaceCallback(this)
            starPrinterUseCase?.initialize(this@TicketDetailsActivity)
        }else if (LogUtil.getPrinterTypeForPrint() == PrinterType.XF2T_PRINTER) {
            LogUtil.printLog("==>Printer","Inside")
            xfPrinterUseCase = XfPrinterUseCase(this)
            lifecycle.addObserver(xfPrinterUseCase!!)
            xfPrinterUseCase?.setPrintInterfaceCallback(this)
            xfPrinterUseCase?.initialize(this@TicketDetailsActivity)
            appCompatPaperFeedButton?.visibility = View.VISIBLE
        } else if (LogUtil.getPrinterTypeForPrint() == PrinterType.ZEBRA_PRINTER) {
            LogUtil.printLog("==>Printer", "Inside")
            zebraPrinterUseCase = ZebraPrinterUseCase()
            zebraPrinterUseCase?.setPrintInterfaceCallback(this)
            zebraPrinterUseCase?.initialize(
                activity = this@TicketDetailsActivity,
                contentResolver = contentResolver,
                sharedPreference = sharedPreference
            )
        }

        setFullScreenUI()
        addObservers()
        init()

        if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DUNCAN, ignoreCase = true)) {
            linearLayoutCompatIssueMore?.visibility = View.GONE
        } else {
            linearLayoutCompatIssueMore?.visibility = View.VISIBLE
        }
        if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_OCEANCITY, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_CLIFTON, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_Easton, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_EPHRATA, ignoreCase = true)
        ) {
            mBtnCancelTicket!!.visibility = View.GONE
        }
        isButtonVisibilitySettingDrive()
        if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_ORLEANS, ignoreCase = true) ||
            BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_BOSTON, ignoreCase = true)
        ) {
            mCheckLinearLayout!!.visibility = View.GONE
        }
        if (BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_CITYOFSANDIEGO,
                ignoreCase = true
            )||BuildConfig.FLAVOR.equals(
                Constants.FLAVOR_TYPE_STORMWATER_DIVISION,
                ignoreCase = true
            )
        ) {
            mBtnCancelTicket!!.text = "VOID"
            mBtnVoidAndReissue!!.text = "Reissue"
        }

        BluetoothPermissionUtil.checkBluetoothPermissions(
            activity = this,
            permissionLauncher = bluetoothPermissionLauncher
        ) {
            //Nothing to implement here in onCreate, This will check bluetooth permission and redirect user to allow bluetooth permission, in case misssed
        }

        setAccessibilityForComponents()
    }


    private fun findViewsByViewBinding() {
        textViewTicketDetails = binding.layoutContentTicketDetails.txtTicketReprint
        mTextViewVoilationDetails = binding.layoutContentTicketDetails.tvVoilationDetails
        mTextViewTicketNumber = binding.layoutContentTicketDetails.tvTicketNumber
        mTextViewTicketDate = binding.layoutContentTicketDetails.tvTicketDate
        mTextViewAddress = binding.layoutContentTicketDetails.tvAddressValue
        mTextViewVehicle = binding.layoutContentTicketDetails.tvVehicleValue
        mTextViewTicketSuccess = binding.layoutContentTicketDetails.ivTicketSuccess
        mTextViewTicketMsg = binding.layoutContentTicketDetails.tvTicketMsg
        mTextViewSpace = binding.layoutContentTicketDetails.tvSpace
        mTextViewVin = binding.layoutContentTicketDetails.tvVin
        mTextViewCitationType = binding.layoutContentTicketDetails.tvCitationType
        mTextViewLot = binding.layoutContentTicketDetails.tvLot
        layCitationOptions = binding.layoutContentTicketDetails.layCitationOptions
        layCitationButtons = binding.layoutContentTicketDetails.layCitationButtons
        mBtnIssueMore = binding.layoutContentTicketDetails.ivIssueMore
        mBtnCancelTicket = binding.layoutContentTicketDetails.btnCancelTicket
        mBtnVoidAndReissue = binding.layoutContentTicketDetails.btnDashboard
        mBTNBack = binding.layoutContentTicketDetails.layoutDashboardHeader.imgBack
        checkBoxDriveOff = binding.layoutContentTicketDetails.checkDriveOff
        checkBoxTvr = binding.layoutContentTicketDetails.checkTvr
        selectedCheckBoxValue = binding.layoutContentTicketDetails.textSelectCheckbox
        linearLayoutCompatPrint = binding.layoutContentTicketDetails.llPrint
        linearLayoutCompatAddNote = binding.layoutContentTicketDetails.llRecord
        ivPrintBtn = binding.layoutContentTicketDetails.ivPrint
        linearLayoutCompatIssueMore = binding.layoutContentTicketDetails.llIssuemore
        linearLayoutCompatScan = binding.layoutContentTicketDetails.llScan
        linearLayoutCompatLot = binding.layoutContentTicketDetails.llLot
        linearLayoutCompatSpace = binding.layoutContentTicketDetails.llSpace
        linearLayoutCompatVin = binding.layoutContentTicketDetails.llVin
        linearLayoutCompatCitationType = binding.layoutContentTicketDetails.llCitationType
        appCompatImageViewHomeIcon =
            binding.layoutContentTicketDetails.layoutDashboardHeader.imgOptions
        mContentMainLayout = binding.layoutContentTicketDetails.contentMain
        mCheckLinearLayout = binding.layoutContentTicketDetails.layCheckBox
        appCompatPaperFeedButton = binding.layoutContentTicketDetails.btnFeed
        btnCancellationRequest = binding.layoutContentTicketDetails.btnCancellationRequest
        printProgressBitmapDownload = binding.layoutContentTicketDetails.printProgress
        btnScanSticker = binding.layoutContentTicketDetails.btnScanSticker

        /*if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_RUTGERS,ignoreCase = true)) {
            btnCancellationRequest!!.visibility = View.VISIBLE
        }*/
    }

    private fun setupClickListeners() {
        val sharedClickForRecord = View.OnClickListener { v ->
            when (v?.id) {
                binding.layoutContentTicketDetails.ivRecord.id, binding.layoutContentTicketDetails.llRecord.id -> {
                    //                if (mScreenLabel.equals("preview")) {
//                    Intent dataEdit = new Intent();
//                    dataEdit.putExtra("edit", "edit_note");
//                    setResult(Activity.RESULT_OK, dataEdit);
//                    finish();
//                }
//                    showAddNoteDialog();
                    val mStartActivity =
                        Intent(this@TicketDetailsActivity, AddNotesAndImagesActivity::class.java)
                    mStartActivity.putExtra("TICEKTID", mIssueTicketId)
                    mStartActivity.putExtra("booklet_id", mCitationNumberId)
                    mStartActivity.putExtra("image_size", uploadImageSize)
                    startActivity(mStartActivity)
                }
            }
        }
        binding.layoutContentTicketDetails.ivRecord.setOnClickListener(sharedClickForRecord)
        binding.layoutContentTicketDetails.llRecord.setOnClickListener(sharedClickForRecord)

        binding.layoutContentTicketDetails.btnDashboard.setOnClickListener {
            //                    removeFolderImages();
//                    //delete temporary images list
//                    mDb.getDbDAO().deleteTempImages();
//                }
//                catch (Exception e){e.printStackTrace();}
//                finish();
//                removeActivity("PreviewActivity");
//                removeActivity("DemoActivity");
//                launchScreen(this,DashboardActivity.class);

//                sharedPreference.getInstance(TicketDetailsActivity.this).write(SharedPrefKey.isReissueTicket, "true");
//                Intent mIntent = new Intent(TicketDetailsActivity.this,LprDetails2Activity.class);
//                mIntent.putExtra("from_scr", "ticket_details");
//                mIntent.putExtra("booklet_id", String.valueOf(mCitationNumberId));
//                mIntent.putExtra("btn_action", "VoidReissue");
////                mDb.getDbDAO().updateCitationBooklet(1, mCitationNumberId);
//                startActivity(mIntent);
//                removeActivity("DemoActivity");
//                finish();

            if (mBtnVoidAndReissue!!.isEnabled) {
                mBtnVoidAndReissue!!.isEnabled = false
                val alpha = 0.45f
                val alphaUp = AlphaAnimation(alpha, alpha)
                alphaUp.fillAfter = true
                mBtnVoidAndReissue!!.startAnimation(alphaUp)
                m4ButtonType = "void"
                showVoidAndReissueDialog("")
            }
        }

        mBtnCancelTicket?.setOnClickListener {
            if (mBtnCancelTicket?.isEnabled.nullSafety()) {
                mBtnCancelTicket?.isEnabled = false
                val alpha = 0.45f
                val alphaUp = AlphaAnimation(alpha, alpha)
                alphaUp.fillAfter = true
                mBtnCancelTicket!!.startAnimation(alphaUp)
                m4ButtonType = ""
                showAddCancelNoteDialog("btnCancelTicket")
            }
        }

        binding.layoutContentTicketDetails.ivScan.setOnClickListener {
            launchScreenWithFlagNewTask(this@TicketDetailsActivity, LprScanActivity::class.java)
        }

        binding.layoutContentTicketDetails.btnScanSticker.setOnClickListener {
            launchScreenWithFlagNewTask(
                this@TicketDetailsActivity,
                VehicleStickerScanActivity::class.java
            )
        }

        binding.layoutContentTicketDetails.ivPrint.setOnClickListener {
            BluetoothPermissionUtil.checkBluetoothPermissions(
                activity = this,
                permissionLauncher = bluetoothPermissionLauncher
            ) {
                if (linearLayoutCompatPrint!!.isEnabled) {
                    try {
                        linearLayoutCompatPrint!!.isEnabled = false
                        ivPrintBtn!!.isEnabled = false
                        val alpha = 0.45f
                        val alphaUp = AlphaAnimation(alpha, alpha)
                        alphaUp.fillAfter = true
                        linearLayoutCompatPrint!!.startAnimation(alphaUp)

                        val filePath =
                            sharedPreference.read(SharedPrefKey.REPRINT_PRINT_BITMAP, "")
                        val file = File(filePath)
                        if (file.exists()) {
                            var iAmount: Int =
                                if (mAmount != null) mAmount!!.toDouble().toInt() else 0
                            var sAmount: String = iAmount.toString();
                            val formatAmount: String = fineAmountFormating(sAmount)

                            if (LogUtil.getPrinterTypeForPrint() == PrinterType.STAR_PRINTER) {
                                if (selectedCheckBoxValue!!.text.toString()
                                        .equals("Valid", ignoreCase = true)
                                ) {
                                    starPrinterUseCase?.mPrintDownloadFacsimileImage(
                                        file,
                                        this@TicketDetailsActivity,
                                        "TicketDetails",
                                        "R",
                                        mTextViewTicketNumber!!.text.toString(),
                                        formatAmount,
                                        mState,
                                        mLprNumber,
                                        ""
                                    )
                                } else {
                                    starPrinterUseCase?.mPrintDownloadFacsimileImage(
                                        file,
                                        this@TicketDetailsActivity,
                                        "SuccessScreen",
                                        selectedCheckBoxValue!!.text.toString(),
                                        mTextViewTicketNumber!!.text.toString(),
                                        formatAmount,
                                        mState,
                                        mLprNumber,
                                        ""
                                    )
                                }
                            } else if (LogUtil.getPrinterTypeForPrint() == PrinterType.XF2T_PRINTER) {
                                if (selectedCheckBoxValue!!.text.toString()
                                        .equals("Valid", ignoreCase = true)
                                ) {
                                    xfPrinterUseCase?.mPrintDownloadFacsimileImage(
                                        file,
                                        this@TicketDetailsActivity,
                                        "TicketDetails",
                                        "R",
                                        mTextViewTicketNumber!!.text.toString(),
                                        formatAmount,
                                        mState,
                                        mLprNumber,
                                        ""
                                    )
                                } else {
                                    xfPrinterUseCase?.mPrintDownloadFacsimileImage(
                                        file,
                                        this@TicketDetailsActivity,
                                        "SuccessScreen",
                                        selectedCheckBoxValue!!.text.toString(),
                                        mTextViewTicketNumber!!.text.toString(),
                                        formatAmount,
                                        mState,
                                        mLprNumber,
                                        ""
                                    )
                                }
                            } else if (LogUtil.getPrinterTypeForPrint() == PrinterType.ZEBRA_PRINTER) {
                                if (selectedCheckBoxValue!!.text.toString()
                                        .equals("Valid", ignoreCase = true)
                                ) {
                                    zebraPrinterUseCase?.mPrintDownloadFacsimileImage(
                                        file,
                                        this@TicketDetailsActivity,
                                        "TicketDetails",
                                        "R",
                                        mTextViewTicketNumber!!.text.toString(),
                                        formatAmount,
                                        mState,
                                        mLprNumber,
                                        ""
                                    )
                                } else {
                                    zebraPrinterUseCase?.mPrintDownloadFacsimileImage(
                                        file,
                                        this@TicketDetailsActivity,
                                        "SuccessScreen",
                                        selectedCheckBoxValue!!.text.toString(),
                                        mTextViewTicketNumber!!.text.toString(),
                                        formatAmount,
                                        mState,
                                        mLprNumber,
                                        ""
                                    )
                                }
                            }

                        } else {
                            LogUtil.printSnackBar(
                                mContentMainLayout!!,
                                this@TicketDetailsActivity,
                                "Downloading the facsimile image wait.."
                            )

                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }

        binding.layoutContentTicketDetails.ivIssueMore.setOnClickListener {
            AppUtils.showCustomAlertDialog(
                mContext,
                getString(R.string.scr_lbl_issue_more),
                getString(R.string.scr_lbl_issue_more_details),
                getString(R.string.alt_lbl_OK),
                getString(R.string.scr_btn_cancel),
                object : CustomDialogHelper {
                    override fun onYesButtonClick() {
                        //TODO("Not yet implemented")
                    }

                    override fun onNoButtonClick() {
                        //TODO No Functionality
                    }

                    override fun onYesButtonClickParam(msg: String?) {
                        if (LogUtil.isEnableActivityLogs)
                            callEventActivityLogApiForBaseActivity(
                                mValue = ACTIVITY_LOG_ISSUE_MORE,
                                isDisplay = true
                            )

                        sharedPreference.writeOverTimeParkingTicketDetails(
                            SharedPrefKey.TIMING_DATA_OVERTIME_PARKING_CHARLESTION,
                            AddTimingRequest()
                        )
                        sharedPreference.write(SharedPrefKey.isReissueTicket, "false")
                        val mIntent1 = Intent(
                            this@TicketDetailsActivity,
                            LprDetails2Activity::class.java
                        )
                        mIntent1.putExtra("from_scr", "ticket_details")
                        mIntent1.putExtra("booklet_id", mCitationNumberId.toString())
                        mIntent1.putExtra("btn_action", "")
                        //mIntent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) SONU
                        //mIntent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        // mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        mDb?.dbDAO?.updateCitationBooklet(1, mCitationNumberId)
                        startActivity(mIntent1)
                        //finish()
                    }
                })
        }

        binding.layoutContentTicketDetails.btnRegenerateFacsimile.setOnClickListener {
            BluetoothPermissionUtil.checkBluetoothPermissions(
                activity = this,
                permissionLauncher = bluetoothPermissionLauncher
            ) {
                val mStartActivity =
                    Intent(this@TicketDetailsActivity, ReprintReuploadActivity::class.java)
                mStartActivity.putExtra("ticket_id", mIssueTicketId)
                mStartActivity.putExtra("ticket_number", mCitationNumberId)
                mStartActivity.putExtra("image_size", uploadImageSize)
                startActivity(mStartActivity)
            }
        }

        binding.layoutContentTicketDetails.btnFeed.setOnClickListener {
                if (LogUtil.getPrinterTypeForPrint() == PrinterType.XF2T_PRINTER) {
                    if (xfPrinterUseCase != null) {
                        xfPrinterUseCase?.printerFeedButton()
                    }
                }
        }
        binding.layoutContentTicketDetails.btnCancellationRequest.setOnClickListener {
                if (appCompatPaperFeedButton?.isEnabled.nullSafety()) {
                    appCompatPaperFeedButton?.isEnabled = false
                    val alpha = 0.45f
                    val alphaUp = AlphaAnimation(alpha, alpha)
                    alphaUp.fillAfter = true
                    appCompatPaperFeedButton!!.startAnimation(alphaUp)
                    m4ButtonType = ""
                    showAddCancelNoteDialog("btnCancellationTicket")
                }

        }
    }

    private fun setAccessibilityForComponents() {
        setAsAccessibilityHeading(mTextViewTicketMsg as View)
        setAsAccessibilityHeading(textViewTicketDetails as View)
        linearLayoutCompatScan?.setCustomAccessibility(contentDescription = getString(R.string.scr_btn_scan), role = getString(R.string.ada_role_button), actionLabel = getString(R.string.ada_action_empty))
        linearLayoutCompatIssueMore?.setCustomAccessibility(contentDescription = getString(R.string.scr_lbl_issue_more), role = getString(R.string.ada_role_button), actionLabel = getString(R.string.ada_action_empty))
        linearLayoutCompatPrint?.setCustomAccessibility(contentDescription = getString(R.string.scr_btn_print), role = getString(R.string.ada_role_button), actionLabel = getString(R.string.ada_action_empty))
        linearLayoutCompatAddNote?.setCustomAccessibility(contentDescription = getString(R.string.scr_lbl_add_note), role = getString(R.string.ada_role_button), actionLabel = getString(R.string.ada_action_empty))
    }

    override fun onResume() {
        super.onResume()
        try {
            val handler = Handler(Looper.getMainLooper())
            handler.postDelayed({
                val lockLprModel = LockLprModel()
                lockLprModel.mLprNumber = ""
                lockLprModel.mMake = ""
                lockLprModel.mModel = ""
                lockLprModel.mColor = ""
                lockLprModel.mAddress = ""
                lockLprModel.mViolationCode = ""
                lockLprModel.ticketCategory = ""
                AppUtils.setLprLock(lockLprModel, this@TicketDetailsActivity, sharedPreference)
                sharedPreference.write(SharedPrefKey.LOCKED_LPR_BOOL, false)
            }, 600)
        } catch (e: Exception) {
            e.printStackTrace()
        }

//        if (!mScreenLabel.equals("SearchScreen")) {
//            registerBroadcastReceiver()
//        }
        checkMissingFacsimile()
    }

    override fun onStop() {
        super.onStop()

        if (LogUtil.getPrinterTypeForPrint() == PrinterType.ZEBRA_PRINTER) {
            zebraPrinterUseCase?.disconnect()
        }
    }

    private fun init() {
        if (showAndEnableScanVehicleStickerModule) {
            btnScanSticker.showView()
        }else{
            btnScanSticker.hideView()
        }

        mContext = this
        mBTNBack?.visibility = View.GONE
        mDb = BaseApplication.instance?.getAppDatabase()

        getCitationNumberId()
        setToolbar()
    }

    private val ticketCancelResponseObserver = Observer { apiResponse: ApiResponse ->
        consumeResponse(apiResponse, DynamicAPIPath.POST_CANCEL)
    }
    private val ticketCancellationResponseObserver = Observer { apiResponse: ApiResponse ->
        consumeResponse(apiResponse, DynamicAPIPath.POST_CANCELLATION_REQUEST)
    }
    private val updateMarkResponseObserver = Observer { apiResponse: ApiResponse ->
        consumeResponse(apiResponse, DynamicAPIPath.POST_UPDATE_TICKET)
    }
    private val downloadBitmapResponseObserver = Observer { apiResponse: ApiResponse ->
        consumeResponse(apiResponse, DynamicAPIPath.POST_DOWNLOAD_FILE)
    }
    private val driveOffTVRResponseObserver = Observer { apiResponse: ApiResponse ->
        consumeResponse(apiResponse, DynamicAPIPath.PATCH_DRIVE_OFF_TVR)
    }
    private val addNotesResponseObserver = Observer { apiResponse: ApiResponse ->
        consumeResponse(apiResponse, DynamicAPIPath.GET_ADD_NOTES)
    }

    private val uploadImageResponseObserver = Observer { apiResponse: ApiResponse ->
        consumeResponse(apiResponse, DynamicAPIPath.POST_IMAGE)
    }

    private val addImageResponseObserver = Observer { apiResponse: ApiResponse ->
        consumeResponse(apiResponse, DynamicAPIPath.POST_ADD_IMAGE + "Image")
    }

    private val addTimingResponseObserver = Observer { apiResponse: ApiResponse ->
        consumeResponse(apiResponse,
            DynamicAPIPath.POST_ADD_TIMING)
    }

    private fun addObservers() {
        mTicketCancelViewModel?.response?.observe(this, ticketCancelResponseObserver)
        mTicketCancellationViewModel?.response?.observe(this, ticketCancellationResponseObserver)
        mUpdateMarkViewModel?.response?.observe(this, updateMarkResponseObserver)
        mDownloadBitmapFIleViewModel?.response?.observe(this, downloadBitmapResponseObserver)
        driveOffTVRViewModel?.response?.observe(this, driveOffTVRResponseObserver)
        mUploadImageViewModel?.responseTicketDetailsScreen?.observe(this, uploadImageResponseObserver)
        addImageViewModel?.responseTicketDetailSCreen?.observe(this, addImageResponseObserver)
        mAddTimingViewModel?.response?.observe(this, addTimingResponseObserver)
    }

    override fun removeObservers() {
        super.removeObservers()
        mTicketCancelViewModel?.response?.removeObserver(ticketCancelResponseObserver)
        mTicketCancellationViewModel?.response?.removeObserver(ticketCancellationResponseObserver)
        mUpdateMarkViewModel?.response?.removeObserver(updateMarkResponseObserver)
        mDownloadBitmapFIleViewModel?.response?.removeObserver(downloadBitmapResponseObserver)
        driveOffTVRViewModel?.response?.removeObserver(driveOffTVRResponseObserver)
        mUploadImageViewModel?.responseTicketDetailsScreen?.removeObserver(uploadImageResponseObserver)
        addImageViewModel?.responseTicketDetailSCreen?.removeObserver(addImageResponseObserver)
        mAddTimingViewModel?.response?.removeObserver(addTimingResponseObserver)
    }// this will contain "Fruit"// If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()

    /* Call Api For Ticket Cancel */
    private fun callTicketCancelApi(Id: String?, note: String, reason: String) {
        if (isInternetAvailable(this@TicketDetailsActivity)) {
            mTag = DATASET_CANCEL_REASON_LIST
            val ticketCancelRequest = TicketCancelRequest()
            ticketCancelRequest.status = "Cancelled"
            ticketCancelRequest.mNote = note
            ticketCancelRequest.mReason = reason
            ticketCancelRequest.mType = m4ButtonType

            OfflineCancelCitationModel.uploadedCitationId = Id
            OfflineCancelCitationModel.lprNumber = mLprNumber
            OfflineCancelCitationModel.note = note
            OfflineCancelCitationModel.reason = reason
            OfflineCancelCitationModel.type = m4ButtonType
            OfflineCancelCitationModel.status = "Cancelled"
            OfflineCancelCitationModel.ticketNumber = mTicketNumber.nullSafety()

//            mDb?.dbDAO?.insertOfflineCancelCitation(OfflineCancelCitationModel)
            mTicketCancelViewModel?.hitTicketCancelApi(ticketCancelRequest, Id)

            try {
                ApiLogsClass.writeApiPayloadTex(BaseApplication.instance?.applicationContext!!,"------------TICKET DETAILS TICKET  Cancelled API---------------ID--"+ Id)
                ApiLogsClass.writeApiPayloadTex(BaseApplication.instance?.applicationContext!!,"REQUEST: "+ObjectMapperProvider.instance.writeValueAsString(ticketCancelRequest))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            LogUtil.printToastMSG(
                    applicationContext,
                    getString(R.string.err_msg_connection_was_refused)
            )

            OfflineCancelCitationModel.uploadedCitationId = Id
            OfflineCancelCitationModel.lprNumber = mLprNumber
            OfflineCancelCitationModel.note = note
            OfflineCancelCitationModel.reason = reason
            OfflineCancelCitationModel.type = m4ButtonType
            OfflineCancelCitationModel.status = "Cancelled"
            OfflineCancelCitationModel.ticketNumber = mTicketNumber.nullSafety()

            try {
                mDb?.dbDAO?.insertOfflineCancelCitation(OfflineCancelCitationModel)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            mBtnCancelTicket!!.visibility = View.GONE
            mBtnCancelTicket!!.visibility = View.GONE
            linearLayoutCompatIssueMore!!.visibility = View.GONE
            mBtnVoidAndReissue!!.visibility = View.GONE
        }
    }

    /* Call Api For Ticket Cancellation Request for backend and make final cancel fromportal */
    private fun callTicketCancellationRequestApi(citationNumber: String?, note: String, reason: String, Id: String?) {
        if (isInternetAvailable(this@TicketDetailsActivity)) {
            mTag = DATASET_CANCEL_REASON_LIST
            val ticketCancellationRequest = TicketCancellationRequest()
            ticketCancellationRequest.requestType = "HANDHELD"
            ticketCancellationRequest.notes = note
            ticketCancellationRequest.ticketNumber = citationNumber

            mTicketCancellationViewModel?.hitTicketCancellationApi(ticketCancellationRequest, Id)

            try {
                ApiLogsClass.writeApiPayloadTex(BaseApplication.instance?.applicationContext!!,"------------TICKET DETAILS TICKET  Cancellation request  API---------------ID--"+ Id)
                ApiLogsClass.writeApiPayloadTex(BaseApplication.instance?.applicationContext!!,"REQUEST: "+ObjectMapperProvider.instance.writeValueAsString(ticketCancellationRequest))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            LogUtil.printToastMSG(
                    applicationContext,
                    getString(R.string.err_msg_connection_was_refused)
            )

            mBtnCancelTicket!!.visibility = View.GONE
            btnCancellationRequest!!.visibility = View.GONE
            linearLayoutCompatIssueMore!!.visibility = View.GONE
            mBtnVoidAndReissue!!.visibility = View.GONE
        }
    }

    /* Call Api For Ticket Cancel */
    private fun callTicketVoidReissueApi(Id: String?, note: String, reason: String,voidReasonLookupCode: String) {
        if (isInternetAvailable(this@TicketDetailsActivity)) {
            mTag = DATASET_VOID_AND_REISSUE_REASON_LIST
            val ticketCancelRequest = TicketCancelRequest()
            ticketCancelRequest.status = "VoidAndReissue"
            ticketCancelRequest.mNote = note
            ticketCancelRequest.mReason = reason
            ticketCancelRequest.mType = m4ButtonType
            ticketCancelRequest.void_reason_lookup_code = voidReasonLookupCode

            OfflineCancelCitationModel.uploadedCitationId = Id
            OfflineCancelCitationModel.lprNumber = mLprNumber
            OfflineCancelCitationModel.note = note
            OfflineCancelCitationModel.reason = reason
            OfflineCancelCitationModel.type = m4ButtonType
            OfflineCancelCitationModel.status = "VoidAndReissue"
            OfflineCancelCitationModel.ticketNumber = mTicketNumber.nullSafety()

            mTicketCancelViewModel?.hitTicketCancelApi(ticketCancelRequest, Id)

            try {
                ApiLogsClass.writeApiPayloadTex(BaseApplication.instance?.applicationContext!!,"------------TICKET  VoidAndReissue API-----------------")
                ApiLogsClass.writeApiPayloadTex(BaseApplication.instance?.applicationContext!!,"REQUEST: "+ObjectMapperProvider.instance.writeValueAsString(ticketCancelRequest))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            LogUtil.printToastMSG(
                    applicationContext,
                    getString(R.string.err_msg_connection_was_refused)
            )
            OfflineCancelCitationModel.uploadedCitationId = Id
            OfflineCancelCitationModel.lprNumber = mLprNumber
            OfflineCancelCitationModel.note = note
            OfflineCancelCitationModel.reason = reason
            OfflineCancelCitationModel.type = m4ButtonType
            OfflineCancelCitationModel.status = "VoidAndReissue"
            OfflineCancelCitationModel.ticketNumber = mTicketNumber.nullSafety()
            mDb?.dbDAO?.insertOfflineCancelCitation(OfflineCancelCitationModel)

            mBtnCancelTicket!!.visibility = View.GONE
            btnCancellationRequest!!.visibility = View.GONE
            linearLayoutCompatIssueMore!!.visibility = View.GONE
            mBtnVoidAndReissue!!.visibility = View.GONE

            mDb!!.dbDAO!!.updateCitationBooklet(1, mCitationNumberId)
                sharedPreference.write(SharedPrefKey.isReissueTicket, "true")
                if (mVoidReissueSelectValue.equals("PLATE ERROR", ignoreCase = true)) {
                    sharedPreference.write(SharedPrefKey.VOIDANDREISSUEBYPLATE, true)
                } else {
                    sharedPreference.write(SharedPrefKey.VOIDANDREISSUEBYPLATE, false)
                }
            sharedPreference.writeOverTimeParkingTicketDetails(
                SharedPrefKey.TIMING_DATA_OVERTIME_PARKING_CHARLESTION,
                AddTimingRequest()!!
            )
                val mIntent = Intent(
                        this@TicketDetailsActivity,
                        LprDetails2Activity::class.java
                )
                mIntent.putExtra("from_scr", "ticket_details")
                mIntent.putExtra("booklet_id", mCitationNumberId.toString())
                mIntent.putExtra("btn_action", "VoidReissue")
                mDb!!.dbDAO!!.updateCitationBooklet(1, mCitationNumberId)
                startActivity(mIntent)
                finish()
        }
    }

    /* Call Api For Ticket Cancel */
    private fun callDownloadBitmapApi() {
        if (isInternetAvailable(this@TicketDetailsActivity)) {
            if (mPrintBitmapPath != null && !mPrintBitmapPath!!.isEmpty() && mPrintBitmapPath!!.length > 5) {
                val downloadBitmapRequest = DownloadBitmapRequest()
                downloadBitmapRequest.downloadType = "CitationImages"
                val links = Links()
                links.img1 = mPrintBitmapPath
                downloadBitmapRequest.links = links
                mDownloadBitmapFIleViewModel?.downloadBitmapAPI(downloadBitmapRequest)
            } else {
                LogUtil.printToastMSG(applicationContext,
                        getString(R.string.err_msg_down_load_image))
            }
        } else {
            LogUtil.printToastMSG(applicationContext,
                    getString(R.string.err_msg_connection_was_refused)
            )
        }
    }

    /* Call Api For Update Mark */
    private fun callUpdateMarkApi(msg: String) {
        if (isInternetAvailable(this@TicketDetailsActivity)) {
            val updateMarkRequest = UpdateTicketRequest()
            updateMarkRequest.arrivalStatus = msg
            mUpdateMarkViewModel?.hitUpdateTicketApi(updateMarkRequest, mIssueTicketId)
        } else {
            LogUtil.printToastMSG(applicationContext,
                    getString(R.string.err_msg_connection_was_refused))
        }
    }

    /* Call Api For add un-upload images*/
    private fun callUploadImagesUrl() {
        if (isInternetAvailable(this@TicketDetailsActivity)) {
            val endPoint = "$mIssueTicketId/images"
            val addImageRequest = AddImageRequest()
            addImageRequest.images = mImages
            addImageViewModel!!.hitAddImagesApiTicketDetalsScreen(addImageRequest, endPoint)
        } else {
            LogUtil.printToastMSG(applicationContext,
                    getString(R.string.err_msg_connection_was_refused))
        }
    }


    /* Call Api For update profile */
    private fun callUploadImages(file: File?, num: Int) {
        if (isInternetAvailable(this@TicketDetailsActivity)) {
            val requestFile = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file!!)
            val files = MultipartBody.Part.createFormData("files",
                    if (file != null) file.name else "", requestFile)
//            var mDropdownList: Array<String>? = null;
//            bannerListForUpload.forEachIndexed { index, scanDataModel ->
//                if (scanDataModel.citationImage.nullSafety().contains(FILE_NAME_FACSIMILE_PRINT_BITMAP)) {
               var mDropdownList = arrayOf(mCitationNumberId + "_" + num + "_" + FILE_NAME_FACSIMILE_PRINT_BITMAP)
//                }
//            }
                val mRequestBodyType =
                        RequestBody.create("text/plain".toMediaTypeOrNull(), "CitationImages")
                mUploadImageViewModel?.hitUploadImagesApiForTicketDetailsScreen(mDropdownList, mRequestBodyType, files)
            } else {
                LogUtil.printToastMSG(applicationContext,
                        getString(R.string.err_msg_connection_was_refused))
            }
        }

        private fun getCitationNumberId() {
            val intent = intent
            if (intent != null) {
                if (intent.getStringExtra("from_scr") != null) {
                    mScreenLabel = intent.getStringExtra("from_scr")
                    mCitationNumberId = intent.getStringExtra("booklet_id")
                    mRescindButton = intent.getStringExtra("event")
                    mIssueTicketId = intent.getStringExtra("ticket_id")
                    uploadImageSize = intent.getIntExtra("image_size", 0)
                    if (mScreenLabel == "SearchScreen") {
                        mBtnCancelTicket?.visibility = View.GONE
                        btnCancellationRequest?.visibility = View.GONE
                        linearLayoutCompatIssueMore?.visibility = View.GONE
                        mBtnVoidAndReissue?.visibility = View.GONE
                    }
                }
                mTicketNumber = intent.getStringExtra("ticket_number")
                mTicketDate = intent.getStringExtra("ticket_date")
                mVoilationDetails = intent.getStringExtra("voilation_details")
                mLocationDetails = intent.getStringExtra("address_")
                mViolationDescription = intent.getStringExtra("voilation_description")
                mPrintBitmapPath = intent.getStringExtra("print_bitmap")
                mMeter = intent.getStringExtra("meter_")
                mVinNumber = intent.getStringExtra("vinNumber_")
                mAmount = intent.getStringExtra("fineAmount_")
                mSpace = intent.getStringExtra("space_")
                mLot = intent.getStringExtra("lot_")
                mMake = intent.getStringExtra("make")
                mModel = intent.getStringExtra("model")
                mColor = intent.getStringExtra("color")
                mLprNumber = intent.getStringExtra("lpr_number")
                mState = intent.getStringExtra("state")
                mTicketType = intent.getStringExtra("ticket_type")
                mVehicleBodyStyle = intent.getStringExtra("bodystyle_")
                printQuery = intent.getStringExtra("printQuery")
                if (intent.hasExtra("expire_date")) {
                    mExpireYear = intent.getStringExtra("expire_date")
                }

                if (intent.hasExtra("tvr") && intent.getBooleanExtra("tvr", true)) {
                    checkBoxTvr!!.isChecked = true
                }
                if (intent.hasExtra("driveoff") && intent.getBooleanExtra("driveoff", true)) {
                    checkBoxDriveOff!!.isChecked = true
                }
                if (mExpireYear.toString().isNullOrEmpty() || mExpireYear.toString().equals("null",ignoreCase = true)) {
//                    mTextViewVehicle?.text = "$mMake, $mColor, $mLprNumber, $mState"
                    mTextViewVehicle?.text = "$mMake $mColor" + (if (mLprNumber != null && !TextUtils.isEmpty(mLprNumber)) SPACE_WITH_COMMA + mLprNumber else "") +
                            (if (mState != null && !TextUtils.isEmpty(mState)) SPACE_WITH_COMMA + mState else "") +
                            (if (mVehicleBodyStyle != null && !TextUtils.isEmpty(mVehicleBodyStyle)) SPACE_WITH_COMMA + "Body Style " + mVehicleBodyStyle else "")
                } else {
//                    mTextViewVehicle?.text = "$mMake, $mColor, $mLprNumber, $mState, $mExpireYear"
                    mTextViewVehicle?.text = "$mMake $mColor" + (if (mLprNumber != null && !TextUtils.isEmpty(mLprNumber)) SPACE_WITH_COMMA + mLprNumber else "") +
                            (if (mState != null && !TextUtils.isEmpty(mState)) SPACE_WITH_COMMA + mState else "") +
                            (if (mExpireYear != null && !TextUtils.isEmpty(mExpireYear)) SPACE_WITH_COMMA + mExpireYear else "") +
                            (if (mVehicleBodyStyle != null && !TextUtils.isEmpty(mVehicleBodyStyle)) SPACE_WITH_COMMA + "Body Style " + mVehicleBodyStyle else "")
                }
                if (mLot != null && !mLot!!.isEmpty()) {
                    mTextViewAddress?.text =
                            mLot + SPACE_WITH_COMMA + mLocationDetails + " " + if (mMeter != null && !TextUtils.isEmpty(mMeter)) mMeter else ""
                } else {
                    mTextViewAddress?.text =
                            mLocationDetails + " " + if (mMeter != null && !TextUtils.isEmpty(mMeter)) mMeter else ""
                }
                mTextViewTicketNumber?.text = mTicketNumber
                mTextViewTicketDate?.text = mTicketDate
                mTextViewVoilationDetails?.text = mVoilationDetails
                //for new orleans
                sharedPreference.write(
                    SharedPrefKey.CITATION_NUMBER_FOR_PRINT, mTicketNumber)
                if (mSpace != null && !mSpace!!.isEmpty()) {
                    linearLayoutCompatSpace!!.visibility = View.VISIBLE
                    mTextViewSpace?.text = mSpace
                }
                if (mVinNumber != null && !mVinNumber!!.isEmpty()) {
                    linearLayoutCompatVin!!.visibility = View.VISIBLE
                    mTextViewVin?.text = mVinNumber
                }
                if (mLot != null && !mLot!!.isEmpty()) {
                    linearLayoutCompatLot!!.visibility = View.GONE
                    mTextViewLot?.text = mLot
                }
                if (intent.getStringExtra("ticket_id") != null) {
                    mTicketId = intent.getStringExtra("ticket_id")
                    if (mTicketId.equals("none", ignoreCase = true)) {
                        mTextViewTicketSuccess?.setBackgroundDrawable(getDrawable(R.drawable.upload_ticket))
                        mTextViewTicketMsg?.text =
                                getString(R.string.val_msg_please_enter_ticket_pending)
                        mBtnCancelTicket?.visibility = View.GONE
                        btnCancellationRequest?.visibility = View.GONE
                        linearLayoutCompatIssueMore?.visibility = View.GONE
                    } else {
                        mTextViewTicketSuccess?.setBackgroundDrawable(getDrawable(R.drawable.ic_ticket_details))
                        mTextViewTicketMsg?.text = getString(R.string.msg_ticket_posted)
                        //layCitationButtons.setVisibility(View.GONE);
                        /**
                         * If facsimile not upload then get citation intent data
                         * check in previewScreen
                         */
                        if (intent.hasExtra("Citation_Images_Link")) {
//                            uploadFacsimileImages(intent)
                        }
                    }
                } else {
                    mTextViewTicketSuccess?.setBackgroundDrawable(getDrawable(R.drawable.ic_ticket_details))
                    mTextViewTicketMsg?.text = getString(R.string.msg_ticket_posted)
                    if (intent.hasExtra("Citation_Images_Link")) {
                        uploadFacsimileImages(intent)
                    }
                }

                if (mScreenLabel == "SearchScreen") {
                    AppUtils.printQueryStringBuilder.clear()
                    AppUtils.clearYAxisSet()
                    AppUtils.clearDrawableElementList()
                }

                if (mScreenLabel == "SearchScreen") {
                    AppUtils.printQueryStringBuilder.append(printQuery)
                    callDownloadBitmapApi()
                } else {
                    try {
                        getBitmapFromUrl(mPrintBitmapPath)
                    } catch (e: Exception) {
                        Log.e("ticketDetailsActivity", e.message.nullSafety())
                    }
                }
            }
            val status = intent?.getStringExtra("ticket_status")
            //        if(status!=null && !status.equalsIgnoreCase("Rescind"))
//        {
//            status = "Valid";
//        }
            selectedCheckBoxValue?.text = status
            if (status != null && status.equals("Cancelled", ignoreCase = true) ||
                    status != null && status.equals("PBC Cancel", ignoreCase = true)
            ) {
                mBtnCancelTicket?.visibility = View.GONE
                btnCancellationRequest?.visibility = View.GONE
                linearLayoutCompatIssueMore?.visibility = View.GONE
                mBtnVoidAndReissue?.visibility = View.GONE
                m4ButtonType = "pbc_cancel"
            } else if (status != null && status.equals("Rescind", ignoreCase = true)) {
                mBtnCancelTicket?.visibility = View.GONE
                btnCancellationRequest?.visibility = View.GONE
                linearLayoutCompatIssueMore?.visibility = View.GONE
                mBtnVoidAndReissue?.visibility = View.GONE
            } else if (status != null && status.equals("VoidAndReissue", ignoreCase = true)) {
                mBtnCancelTicket?.visibility = View.GONE
                btnCancellationRequest?.visibility = View.GONE
                linearLayoutCompatIssueMore?.visibility = View.GONE
                mBtnVoidAndReissue?.visibility = View.GONE
            } else if (status != null && status.equals("Valid", ignoreCase = true)) {
                mBtnCancelTicket?.visibility = View.VISIBLE
                mBtnVoidAndReissue?.visibility = View.VISIBLE
                if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DUNCAN, ignoreCase = true)||
                    (BuildConfig.FLAVOR.equals(DuncanBrandingApp13()))&&
                    !BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_NORWALK, ignoreCase = true)) {
                    linearLayoutCompatIssueMore?.visibility = View.GONE
                } else {
                    linearLayoutCompatIssueMore?.visibility = View.VISIBLE
                }
            }

            if (mTicketType != null && !mTicketType!!.isEmpty()) {
                if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_DURANGO,true)){
                    mTextViewCitationType!!.setText(mTicketType.toString()!!.replace("Warning", "Interruption"))
                }else{
                    mTextViewCitationType!!.setText(mTicketType.toString())
                }
//                mTextViewCitationType!!.setText(mTicketType.toString())
                linearLayoutCompatCitationType!!.visibility = View.VISIBLE
            }
            // call drive off API if user already select drive off in citation form
            if (mTicketType != null && !mTicketType!!.isEmpty() && mTicketType!!.contains("Drive Off")) {
                checkBoxDriveOff!!.isSelected = true
                checkBoxDriveOff!!.isChecked = true
                driveOffTvrRequest.isDriveOff = true
                driveOffTvrRequest.isTvr = false
                driveOffTVRViewModel?.driveOffTvrAPI(driveOffTvrRequest, mIssueTicketId)
            }

            if (mViolationDescription!=null && mViolationDescription!!.trim().equals("1",ignoreCase = true)==true)
            {
                isOverTimeParkingAPICallingSettingDrive()
            }
        }

    /**
     * this method call when facsimile image not uploaded
     */
    private fun uploadFacsimileImages(intent:Intent)
    {
        mCitaionImagesLinks = intent.getStringArrayListExtra("Citation_Images_Link")!!
        val filePath = sharedPreference.read(SharedPrefKey.REPRINT_PRINT_BITMAP, "")
        savePrintImagePath = File(filePath)
        if (mCitaionImagesLinks != null && mCitaionImagesLinks.size>0) {
            for (i in mCitaionImagesLinks.indices) {
                if (mCitaionImagesLinks[i].contains("_"+FILE_NAME_FACSIMILE_PRINT_BITMAP+".jpg")) {
                    isFacsimileUploadLinkAvailable = true
                    mImages.add(mCitaionImagesLinks[i]!!)
                    break
                }
            }
            /**
             * If facsimile link available then add link in citation
             * otherwise upload image and get link
             */
            if(isFacsimileUploadLinkAvailable)
            {
                callUploadImagesUrl()
            }else{
                callUploadImages(savePrintImagePath, mCitaionImagesLinks.size+1)
            }
        }
    }
    //init toolbar navigation
        private fun setToolbar() {
//        appCompatImageViewHomeIcon.setImageResource(R.drawable.ic_home);
//        appCompatImageViewHomeIcon.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                System.gc();
//                finishAffinity();
//                launchScreenWithFlag(TicketDetailsActivity.this, WelcomeActivity.class);
////                System.exit(0);
//            }
//        });
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

        /*Api response */
        private fun consumeResponse(apiResponse: ApiResponse, tag: String) {
            when (apiResponse.status) {
                Status.LOADING -> showProgressLoader(getString(R.string.scr_message_please_wait))
                Status.SUCCESS -> {
                    dismissLoader()
                    if (!apiResponse.data!!.isNull) {
                        LogUtil.printLog(tag, apiResponse.data.toString())
                        try {
                            if (tag.equals(DynamicAPIPath.POST_CANCEL, ignoreCase = true)) {
                                val responseModel = ObjectMapperProvider.fromJson(apiResponse.data.toString(), TicketCancelResponse::class.java)

                                try {
                                    ApiLogsClass.writeApiPayloadTex(BaseApplication.instance?.applicationContext!!,"RESPONSE: "+ObjectMapperProvider.instance.writeValueAsString(responseModel))
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                                if (responseModel != null && responseModel.success!!) {
                                    LogUtil.printToastMSG(this@TicketDetailsActivity, responseModel.msg)
                                    mDb!!.dbDAO!!.updateCitationBooklet(1, mCitationNumberId)
                                    if (mTag.equals(DATASET_VOID_AND_REISSUE_REASON_LIST, ignoreCase = true)) {
                                        if (LogUtil.isEnableActivityLogs)
                                            callEventActivityLogApiForBaseActivity(mValue = ACTIVITY_LOG_REISSUE, isDisplay = true)

                                        sharedPreference.write(SharedPrefKey.isReissueTicket, "true")
                                        if (mVoidReissueSelectValue.equals("PLATE ERROR",
                                                        ignoreCase = true)) {
                                            sharedPreference.write(SharedPrefKey.VOIDANDREISSUEBYPLATE, true)
                                        } else {
                                            sharedPreference.write(SharedPrefKey.VOIDANDREISSUEBYPLATE, false)
                                        }
                                        sharedPreference.writeOverTimeParkingTicketDetails(
                                            SharedPrefKey.TIMING_DATA_OVERTIME_PARKING_CHARLESTION,
                                            AddTimingRequest()!!
                                        )
                                        val mIntent = Intent(this@TicketDetailsActivity,
                                                LprDetails2Activity::class.java)
                                        mIntent.putExtra("from_scr", "ticket_details")
                                        mIntent.putExtra("booklet_id", mCitationNumberId.toString())
                                        mIntent.putExtra("btn_action", "VoidReissue")
                                        mDb!!.dbDAO!!.updateCitationBooklet(1, mCitationNumberId)
//                                        mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                                        mIntent.addFlags(Intent.FLAG_ACTIVITY_)
                                        startActivity(mIntent)
//                                        finish()
                                    } else {
                                        try {
                                            mBtnCancelTicket!!.visibility = View.GONE
                                            linearLayoutCompatIssueMore!!.visibility = View.GONE
                                            mBtnVoidAndReissue!!.visibility = View.GONE
                                            selectedCheckBoxValue!!.text = getString(R.string.scr_lbl_cancelled)
                                            removeFolderImages()
                                            //delete temporary images list
                                            mDb!!.dbDAO!!.deleteTempImages()
                                            mDb!!.dbDAO!!.updateCitationBooklet(1, mCitationNumberId)
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }

//                                    finish();
//                                    removeActivity("PreviewActivity");
//                                    removeActivity("DemoActivity");
//                                    launchScreen(mContext, DashboardActivity.class);
//                                    sharedPreference.getInstance(TicketDetailsActivity.this).write(SharedPrefKey.isCancelTicket, "true");
                                    }
                                } else {
                                    dismissLoader()
                                    AppUtils.showCustomAlertDialog(mContext,
                                        APIConstant.POST_CANCEL,
                                        getString(R.string.err_msg_something_went_wrong),
                                        getString(R.string.alt_lbl_OK),
                                        getString(R.string.scr_btn_cancel),
                                        object : CustomDialogHelper {
                                            override fun onYesButtonClick() {
                                                //TODO("Not yet implemented")
                                            }

                                            override fun onNoButtonClick() {
                                                //TODO("Not yet implemented")
                                            }

                                            override fun onYesButtonClickParam(msg: String?) {
                                                //TODO("Not yet implemented")
                                            }

                                        })
                                }
                            }
                            if (tag.equals(DynamicAPIPath.POST_CANCELLATION_REQUEST, ignoreCase = true)) {
                                val responseModel = ObjectMapperProvider.fromJson(apiResponse.data.toString(), TicketCancelResponse::class.java)

                                try {
                                    ApiLogsClass.writeApiPayloadTex(BaseApplication.instance?.applicationContext!!,"RESPONSE: cancellation request "+ObjectMapperProvider.instance.writeValueAsString(responseModel))
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                                if (responseModel != null && responseModel.success!!) {
                                    LogUtil.printToastMSG(this@TicketDetailsActivity, responseModel.msg)
                                    mDb!!.dbDAO!!.updateCitationBooklet(1, mCitationNumberId)
                                        try {
                                            btnCancellationRequest!!.visibility = View.GONE
                                            linearLayoutCompatIssueMore!!.visibility = View.GONE
                                            mBtnVoidAndReissue!!.visibility = View.GONE
                                            selectedCheckBoxValue!!.text = getString(R.string.scr_lbl_cancelled)
                                            removeFolderImages()
                                            //delete temporary images list
                                            mDb!!.dbDAO!!.deleteTempImages()
                                            mDb!!.dbDAO!!.updateCitationBooklet(1, mCitationNumberId)
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }

                                } else {
                                    dismissLoader()
                                    AppUtils.showCustomAlertDialog(mContext,
                                        APIConstant.POST_CANCELLATION_REQUEST_API,
                                        getString(R.string.err_msg_something_went_wrong),
                                        getString(R.string.alt_lbl_OK),
                                        getString(R.string.scr_btn_cancel),
                                        object : CustomDialogHelper {
                                            override fun onYesButtonClick() {
                                                //TODO("Not yet implemented")
                                            }

                                            override fun onNoButtonClick() {
                                                //TODO("Not yet implemented")
                                            }

                                            override fun onYesButtonClickParam(msg: String?) {
                                                //TODO("Not yet implemented")
                                            }

                                        })
                                }
                            } else if (tag.equals(DynamicAPIPath.POST_UPDATE_TICKET,
                                            ignoreCase = true)) {
                                val responseModel = ObjectMapperProvider.fromJson(apiResponse.data.toString(), UpdateTicketResponse::class.java)

                                if (responseModel != null && responseModel.success!!) {
                                    LogUtil.printToastMSG(this@TicketDetailsActivity, responseModel.msg)
                                } else {
                                    AppUtils.showCustomAlertDialog(mContext,
                                        APIConstant.POST_UPDATE_TICKET,
                                        getString(R.string.err_msg_something_went_wrong),
                                        getString(R.string.alt_lbl_OK),
                                        getString(R.string.scr_btn_cancel),
                                        object : CustomDialogHelper {
                                            override fun onYesButtonClick() {
                                                //TODO("Not yet implemented")
                                            }

                                            override fun onNoButtonClick() {
                                                //TODO("Not yet implemented")
                                            }

                                            override fun onYesButtonClickParam(msg: String?) {
                                                //TODO("Not yet implemented")
                                            }

                                        })
                                    dismissLoader()
                                }
                            } else if (tag.equals(DynamicAPIPath.POST_DOWNLOAD_FILE,
                                            ignoreCase = true)) {

                                val responseModel = ObjectMapperProvider.fromJson(apiResponse.data.toString(), DownloadBitmapResponse::class.java)

                                if (responseModel != null && responseModel.isStatus) {
                                    if (PermissionUtils.requestCameraAndStoragePermission(this@TicketDetailsActivity) && responseModel.metadata!![0].url?.length!! > 0) {
                                        DownloadingPrintBitmapFromUrl().execute(
                                                responseModel.metadata!![0].url)
                                    }
                                }
                                dismissLoader()
                            }  else if (tag.equals(DynamicAPIPath.POST_IMAGE, ignoreCase = true)) {
                                try {
                                    val responseModel = ObjectMapperProvider.fromJson(apiResponse.data.toString(), UploadImagesResponse::class.java)

                                    if (responseModel != null && responseModel.status.nullSafety()) {
                                        if (responseModel.data != null && responseModel.data?.size!! > 0 && responseModel.data!![0].response != null && responseModel.data!![0].response?.links != null && responseModel.data!![0].response?.links?.size!! > 0) {
                                            mImages.add(responseModel.data!![0].response?.links!![0])
                                            callUploadImagesUrl()
                                        } else {
                                            AppUtils.showCustomAlertDialog(mContext,
                                                    APIConstant.POST_IMAGE,
                                                    getString(R.string.err_msg_something_went_wrong_imagearray),
                                                    getString(R.string.alt_lbl_OK), getString(R.string.scr_btn_cancel), object : CustomDialogHelper {
                                                    override fun onYesButtonClick() {
                                                        //TODO("Not yet implemented")
                                                    }

                                                    override fun onNoButtonClick() {
                                                        //TODO("Not yet implemented")
                                                    }

                                                    override fun onYesButtonClickParam(msg: String?) {
                                                        //TODO("Not yet implemented")
                                                    }

                                                })
                                        }
                                    } else {
                                        dismissLoader()
                                        AppUtils.showCustomAlertDialog(mContext,
                                                APIConstant.POST_IMAGE,
                                                getString(R.string.err_msg_something_went_wrong),
                                                getString(R.string.alt_lbl_OK), getString(R.string.scr_btn_cancel), object : CustomDialogHelper {
                                                override fun onYesButtonClick() {
                                                    //TODO("Not yet implemented")
                                                }

                                                override fun onNoButtonClick() {
                                                    //TODO("Not yet implemented")
                                                }

                                                override fun onYesButtonClickParam(msg: String?) {
                                                    //TODO("Not yet implemented")
                                                }

                                            })
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }else if (tag.equals(DynamicAPIPath.POST_ADD_IMAGE + "Image",ignoreCase = true)) {
                                dismissLoader()

                                val responseModel = ObjectMapperProvider.fromJson(apiResponse.data.toString(), AddNotesResponse::class.java)

                                if (responseModel != null && responseModel.isSuccess) {
                                    try {
                                        mDb!!.dbDAO!!.updateFacsimileUploadCitationId(mIssueTicketId.toString()
                                                ,mCitationNumberId!!)
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                    AppUtils.showCustomAlertDialog(mContext,
                                            APIConstant.UPLOAD_IMAGE,
                                            if (responseModel.message != null) responseModel.message else getString(
                                                    R.string.err_msg_something_went_wrong
                                            ),
                                            getString(R.string.alt_lbl_OK), getString(R.string.scr_btn_cancel), object : CustomDialogHelper {
                                            override fun onYesButtonClick() {
                                                //TODO("Not yet implemented")
                                            }

                                            override fun onNoButtonClick() {
                                                //TODO("Not yet implemented")
                                            }

                                            override fun onYesButtonClickParam(msg: String?) {
                                                //TODO("Not yet implemented")
                                            }

                                        })
                                } else {
                                    AppUtils.showCustomAlertDialog(mContext,
                                            APIConstant.UPLOAD_IMAGE,
                                            if (responseModel!!.message != null) responseModel.message else getString(
                                                    R.string.err_msg_something_went_wrong
                                            ),
                                            getString(R.string.alt_lbl_OK), getString(R.string.scr_btn_cancel), object : CustomDialogHelper {
                                            override fun onYesButtonClick() {
                                                //TODO("Not yet implemented")
                                            }

                                            override fun onNoButtonClick() {
                                                //TODO("Not yet implemented")
                                            }

                                            override fun onYesButtonClickParam(msg: String?) {
                                                //TODO("Not yet implemented")
                                            }

                                        })
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            if (tag.equals(DynamicAPIPath.POST_CANCEL, ignoreCase = true)) {
                                mDb?.dbDAO?.insertOfflineCancelCitation(OfflineCancelCitationModel)
                            }
                            //token expires
                            dismissLoader()
                            logout(this)
                        }
                    }
                }
                Status.ERROR -> {
                    if (tag.equals(DynamicAPIPath.POST_CANCEL, ignoreCase = true)) {
                        try {
                            mDb?.dbDAO?.insertOfflineCancelCitation(OfflineCancelCitationModel)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    dismissLoader()
                    LogUtil.printToastMSG(this@TicketDetailsActivity, getString(R.string.err_msg_error))
                    try {
                        ApiLogsClass.writeApiPayloadTex(BaseApplication.instance?.applicationContext!!,"ERROR: "+ObjectMapperProvider.instance.writeValueAsString(apiResponse!!.error!!.message))
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                else -> {}
            }
        }

        private fun removeFolderImages() {
            if (!mTicketId.equals("none", ignoreCase = true)) {
                try {
                    val bannerList = mDb?.dbDAO?.getCitationWithTicket(mCitationNumberId)!!
                            .citationData?.imagesList
                    if (bannerList?.size!! > 0) {
                        for (i in bannerList.indices) {
                            val oldFile = File(bannerList[i].citationImage)
                            if (oldFile.exists()) oldFile.delete()
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        private fun showAddCancelNoteDialog(id: String) {
            val mDialog = Dialog(mContext!!, R.style.ThemeDialogCustom)
            mDialog.setContentView(R.layout.dialog_add_note)
            val mEditTextNote: TextInputEditText = mDialog.findViewById(R.id.etNote)
            val textInputNote: TextInputLayout = mDialog.findViewById(R.id.input_textNote)
            val mEditTextStatus: AppCompatAutoCompleteTextView = mDialog.findViewById(R.id.etStatus)
            val textInputStatus: TextInputLayout = mDialog.findViewById(R.id.input_textStatus)
            val appCompatButton: AppCompatButton = mDialog.findViewById(R.id.btn_done)
//        val appCompatLayout: LinearLayoutCompat = mDialog.findViewById(R.id.layPopup)
            val tv_dialogTitle: AppCompatTextView = mDialog.findViewById(R.id.tv_dialogTitle)

            setAccessibilityForTextInputLayoutDropdownButtons(this@TicketDetailsActivity, textInputStatus)

            if (id.equals("btnCancelTicket", ignoreCase = true)) {
                appCompatButton.visibility = View.VISIBLE
                textInputNote.visibility = View.VISIBLE
                textInputStatus.visibility = View.VISIBLE
                setDropdownStatus(mEditTextStatus)
                tv_dialogTitle.text = getString(R.string.msg_add_cancel_reasons)
            }else if (id.equals("btnCancellationTicket", ignoreCase = true)) {
                appCompatButton.visibility = View.VISIBLE
                textInputNote.visibility = View.VISIBLE
                textInputStatus.visibility = View.GONE
//                setDropdownStatus(mEditTextStatus)
                tv_dialogTitle.text = getString(R.string.msg_add_cancel_reasons)
            }
            val appCompatImageView = mDialog.findViewById<ImageView>(R.id.btn_cancel)
            appCompatImageView.setOnClickListener { v: View? -> mDialog.dismiss() }
            appCompatButton.setOnClickListener {

                if (id.equals("btnCancelTicket", ignoreCase = true)) {
                    if (TextUtils.isEmpty(mEditTextStatus.text.toString())) {
                        LogUtil.printToastMSG(mContext, getString(R.string.val_msg_please_enter_status))
                    } else {
                        if (mEditTextStatus!!.text!!.toString().isEmpty()) {
                            mEditTextStatus?.requestFocus()
                            mEditTextStatus?.isFocusable = true
                            mEditTextStatus?.error = getString(R.string.val_msg_please_enter_cancel)
                            LogUtil.printToastMSGForErrorWarning(
                                    applicationContext,
                                    getString(R.string.val_msg_please_enter_cancel)
                            )
                        } else {
                            mDialog.dismiss()
                            callTicketCancelApi(
                                    mTicketId, mEditTextNote.text.toString(),
                                    mEditTextStatus.text.toString()
                            )
                        }
                    }
                }else if (id.equals("btnCancellationTicket", ignoreCase = true)) {
                    mDialog.dismiss()
                    callTicketCancellationRequestApi(
                        mCitationNumberId, mEditTextNote.text.toString(),
                        mEditTextStatus.text.toString(),mTicketId
                    )
                }
            }
            mDialog.show()
            val window = mDialog.window
            window!!.setLayout(
                    LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                    LinearLayoutCompat.LayoutParams.WRAP_CONTENT
            )
        }

        //set value to Status dropdown
        private fun setDropdownStatus(mEditTextStatus: AppCompatAutoCompleteTextView) {
            try {
                var mApplicationList: List<DatasetResponse> = ArrayList()

                if (Singleton.getDataSetList(DATASET_CANCEL_REASON_LIST, getMyDatabase()) != null) {
                    mApplicationList = Singleton.getDataSetList(DATASET_CANCEL_REASON_LIST, getMyDatabase())!!
                }
                if (mApplicationList != null && mApplicationList.isNotEmpty()) {
                    val mDropdownList = arrayOfNulls<String>(mApplicationList.size)
                    for (i in mApplicationList.indices) {
                        mDropdownList[i] = mApplicationList[i].cancelReason.toString()
                        /* if (mApplicationList.get(i).get_id().equalsIgnoreCase(mWelcomeResponseData.getData().get(0).getUser().getOfficerShift().get_id())) {
                            pos = i;
                        }*/
                    }
                    val adapter = ArrayAdapter(
                            mContext!!,
                            R.layout.row_dropdown_menu_popup_item,
                            mDropdownList
                    )
                    mEditTextStatus.threshold = 1
                    mEditTextStatus.setAdapter<ArrayAdapter<String?>>(adapter)
                    mEditTextStatus.onItemClickListener =
                            OnItemClickListener { parent, view, position, id ->
                                // mSelectedShiftStat = mApplicationList.get(position);
                                mEditTextStatus?.error = null
                            }
                } else {
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        private fun showVoidAndReissueDialog(id: String) {
            try {
                val mDialog = Dialog(mContext!!, R.style.ThemeDialogCustom)
                mDialog.setContentView(R.layout.dialog_add_note)
                val mEditTextNote: TextInputEditText = mDialog.findViewById(R.id.etNote)
                val textInputNote: TextInputLayout = mDialog.findViewById(R.id.input_textNote)
                val mEditTextStatus: AppCompatAutoCompleteTextView = mDialog.findViewById(R.id.etStatus)
                val textInputStatus: TextInputLayout = mDialog.findViewById(R.id.input_textStatus)
                val appCompatButton: AppCompatButton = mDialog.findViewById(R.id.btn_done)
//        val appCompatLayout: LinearLayoutCompat = mDialog.findViewById(R.id.layPopup)
                val tv_dialogTitle: AppCompatTextView = mDialog.findViewById(R.id.tv_dialogTitle)

                setAccessibilityForTextInputLayoutDropdownButtons(this@TicketDetailsActivity, textInputStatus)

                appCompatButton.visibility = View.VISIBLE
                textInputNote.visibility = View.VISIBLE
                textInputStatus.visibility = View.VISIBLE
                setDropdownVoidReissue(mEditTextStatus)
                tv_dialogTitle.text = getString(R.string.msg_add_void_reissue_reasons)
                val appCompatImageView = mDialog.findViewById<ImageView>(R.id.btn_cancel)
                appCompatImageView.setOnClickListener { v: View? ->
                    val alpha = 1.00f
                    val alphaUp = AlphaAnimation(alpha, alpha)
                    alphaUp.fillAfter = true
                    mBtnVoidAndReissue!!.startAnimation(alphaUp)
                    mBtnVoidAndReissue!!.isEnabled = true
                    mDialog.dismiss()
                }
                appCompatButton.setOnClickListener {
                    val alpha = 1.00f
                    val alphaUp = AlphaAnimation(alpha, alpha)
                    alphaUp.fillAfter = true
                    mBtnVoidAndReissue!!.startAnimation(alphaUp)
                    mBtnVoidAndReissue!!.isEnabled = true
                    if (mEditTextStatus!!.text!!.toString().isEmpty()) {
                        mEditTextStatus?.requestFocus()
                        mEditTextStatus?.isFocusable = true
                        mEditTextStatus?.error = getString(R.string.val_msg_please_enter_reason)
                        LogUtil.printToastMSGForErrorWarning(applicationContext,
                                getString(R.string.val_msg_please_enter_reason))
                    } else {
                        mDialog.dismiss()
                        mVoidReissueSelectValue = mEditTextStatus.text.toString()
                        callTicketVoidReissueApi(
                                mTicketId, mEditTextNote.text.toString(),
                                mEditTextStatus.text.toString(),
                            if(mEditTextStatus.tag!=null) mEditTextStatus.tag.toString() else "")
                    }
                }
                mDialog.show()
                val window = mDialog.window
                window?.setLayout(
                        LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                        LinearLayoutCompat.LayoutParams.WRAP_CONTENT
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        //set value to Status dropdown
        private fun setDropdownVoidReissue(mEditTextStatus: AppCompatAutoCompleteTextView) {
            val pos = 0
            try {
                var mApplicationList: List<DatasetResponse> = ArrayList()
                if (Singleton.getDataSetList(DATASET_VOID_AND_REISSUE_REASON_LIST, getMyDatabase()) != null) {
                    mApplicationList = Singleton.getDataSetList(DATASET_VOID_AND_REISSUE_REASON_LIST, getMyDatabase())!!
                }
                if (mApplicationList != null && mApplicationList.isNotEmpty()) {
                    val mDropdownList = arrayOfNulls<String>(mApplicationList.size)
                    for (i in mApplicationList.indices) {
                        mDropdownList[i] = mApplicationList[i].mVoidAndReiccueReason.toString()
                    }
                    val adapter = ArrayAdapter(mContext!!,
                            R.layout.row_dropdown_menu_popup_item,
                            mDropdownList)
                    mEditTextStatus.threshold = 1
                    mEditTextStatus.setAdapter<ArrayAdapter<String?>>(adapter)
                    mEditTextStatus.onItemClickListener =
                            OnItemClickListener { parent, view, position, id ->
                                // mSelectedShiftStat = mApplicationList.get(position);
                                mEditTextStatus?.error = null
                                val index: Int =
                                    getIndexViodReissue(
                                        mApplicationList,
                                        mEditTextStatus?.text.toString())
                                mEditTextStatus.setTag(mApplicationList[index].void_reason_lookup_code)
                            }
                } else {
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    private fun getIndexViodReissue(list: List<DatasetResponse>, name: String): Int {
        var pos = 0
        for (myObj in list) {
            if (name.equals(myObj.mVoidAndReiccueReason, ignoreCase = true)) return pos
            pos++
        }
        return 0
    }
        private fun showAddNoteDialog() {
            val mDialog = Dialog(this, R.style.ThemeDialogCustom)
            mDialog.setContentView(R.layout.dialog_add_note)
            val mEditTextNote: TextInputEditText = mDialog.findViewById(R.id.etNote)
            val appCompatButton: AppCompatButton = mDialog.findViewById(R.id.btn_done)
            val appCompatImageView = mDialog.findViewById<ImageView>(R.id.btn_cancel)
            val appCompatImageView1 = mDialog.findViewById<ImageView>(R.id.ivCamera_1)
            val appCompatImageView2 = mDialog.findViewById<ImageView>(R.id.ivCamera_2)
            val cardView1: CardView = mDialog.findViewById(R.id.card1)
            val cardView2: CardView = mDialog.findViewById(R.id.card2)
            appCompatImageView.setOnClickListener { v: View? -> mDialog.dismiss() }
//        val appCompatLayout: LinearLayoutCompat = mDialog.findViewById(R.id.layPopup)
            appCompatButton.visibility = View.VISIBLE
            appCompatButton.setOnClickListener {
                if (TextUtils.isEmpty(mEditTextNote.text.toString())) {
                    LogUtil.printToastMSG(mContext, getString(R.string.val_msg_please_enter_note))
                } else {
                }
            }
            mDialog.show()
        }

        override fun onBackPressed() {
            try {
                if (!mScreenLabel.equals("SearchScreen")) {
                    if (backpressCloseDrawer()) {
                        closeDrawer()
                        finishAffinity()
                        startActivity(Intent(this, WelcomeActivity::class.java)
                                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP or
                                                        Intent.FLAG_ACTIVITY_NO_HISTORY))
                        System.exit(0)
                    }
                } else {
                    finish()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        override fun onActionSuccess(value: String) {
            try {
                enablePrintButton()
                val oldFile = File(sharedPreference.read(SharedPrefKey.REPRINT_PRINT_BITMAP, ""))
                //            if (oldFile.exists()) oldFile.delete();
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        private fun getBitmapFromUrl(path: String?): Bitmap? {
            var printBitmap: Bitmap? = null
            try {
                if (path != null && path.length > 6) {
                    sharedPreference.write(SharedPrefKey.REPRINT_PRINT_BITMAP, path)
                    //            URL url = new URL("http://....");
                    val url = URL(path)
                    LogUtil.printLogHeader("print bitmap ", url.toString())
                    printBitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream())
                }
            } catch (e: IOException) {
                println(e)
            }
            return printBitmap
        }

        inner class DownloadingPrintBitmapFromUrl : AsyncTask<String?, Int?, String?>() {
            public override fun onPreExecute() {
                super.onPreExecute()
                linearLayoutCompatPrint!!.isEnabled = false
                ivPrintBtn!!.isEnabled = false
                printProgressBitmapDownload!!.visibility = View.VISIBLE
                showProgressLoader("downloading facsimile")
            }

            override fun doInBackground(vararg url: String?): String? {
                if (!TextUtils.isEmpty(url[0])) {
                    try {
//                        val files = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).listFiles();
//                        val files1 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS+File.separator+Constants.FILE_NAME+Constants.CAMERA).listFiles()

                        val mydir = File(
                            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                                Constants.FILE_NAME + Constants.CAMERA
                        )
                        mydir.deleteRecursively()
                        if (!mydir.exists()) {
                            mydir.mkdirs()
                        }

                        val file = File(mydir.absolutePath, "print_bitmap_download" + ".jpg")

                        if (file.exists()) file.delete()
                        val manager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
                        val downloadUri = Uri.parse((url[0])!!)
                        val request = DownloadManager.Request(downloadUri)
                        request.setAllowedNetworkTypes(
                                DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE
                        )
                                .setAllowedOverRoaming(false)
                                .setTitle("Downloading")
//                                .setDestinationUri(Uri.fromFile(file))
//                                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                                .setDestinationInExternalPublicDir(
                                    Environment.DIRECTORY_DOWNLOADS,
                                    Constants.FILE_NAME + Constants.CAMERA +File.separator+"print_bitmap_download" + ".jpg"
                                )
//                            .setDestinationInExternalFilesDir(
//                            mContext!!.applicationContext,
//                                Constants.FILE_NAME + "" + Constants.CAMERA,
//                                "print_bitmap_download" + ".jpg"
//                        )
                        manager?.enqueue(request)
                        MediaScannerConnection.scanFile(
                            this@TicketDetailsActivity, arrayOf<String>(file.toString()), null
                        ) { path, uri -> }

                        return mydir.absolutePath + File.separator + "print_bitmap_download" + ".jpg"
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {

                    return ""
                }
                return ""
            }

            public override fun onPostExecute(s: String?) {
                try {
                    if (s != null && s.length > 6) {
                        sharedPreference.write(SharedPrefKey.REPRINT_PRINT_BITMAP, s)
                        lifecycleScope.launch {
                            delay(2000) // delay for 2 seconds (2000 milliseconds)
                            linearLayoutCompatPrint!!.isEnabled = true
                            ivPrintBtn!!.isEnabled = true
                            dismissLoader()
                            printProgressBitmapDownload!!.visibility = View.GONE
                        }

                    }
                    //                getBitmapFromUrl(s);
                } catch (e: Exception) {
                    LogUtil.printLog("error mesg", e.message)
                }
                super.onPostExecute(s)
            }
        }


    @Throws(SecurityException::class, SendIntentException::class, IllegalArgumentException::class)
    fun delete(activity: Activity, uriList: Uri, requestCode: Int) {

        val resolver = activity.contentResolver
        try {
            resolver.delete(uriList!!, null, null)
        } catch (e: Exception) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                // WARNING: if the URI isn't a MediaStore Uri and specifically
                // only for media files (images, videos, audio) then the request
                // will throw an IllegalArgumentException, with the message:
                // 'All requested items must be referenced by specific ID'

                // No need to handle 'onActivityResult' callback, when the system returns
                // from the user permission prompt the files will be already deleted.
                // Multiple 'owned' and 'not-owned' files can be combined in the
                // same batch request. The system will automatically delete them
                // using the same prompt dialog, making the experience homogeneous.




                MediaStore.createDeleteRequest(resolver, listOf(uriList)).intentSender





//            val collection: ArrayList<Uri> = ArrayList()
//            collection.add(uriList)
//            val pendingIntent = MediaStore.createDeleteRequest(resolver, collection!!)
//            activity.startIntentSenderForResult(
//                pendingIntent.intentSender,
//                requestCode,
//                null,
//                0,
//                0,
//                0,
//                null
//            )
            } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
                try {
//                 In Android == Q a RecoverableSecurityException is thrown for not-owned.
                    // For a batch request the deletion will stop at the failed not-owned
                    // file, so you may want to restrict deletion in Android Q to only
                    // 1 file at a time, to make the experience less ugly.
                    // Fortunately this gets solved in Android R.

//                val recoverableSecurityException = e as? RecoverableSecurityException
//                recoverableSecurityException?.userAction?.actionIntent?.intentSender
//                for (uri in uriList!!) {
                    resolver.delete(uriList!!, null, null)
//                }
                } catch (ex: RecoverableSecurityException) {
                    val intent = ex.userAction
                        .actionIntent
                        .intentSender

                    // IMPORTANT: still need to perform the actual deletion
                    // as usual, so again getContentResolver().delete(...),
                    // in your 'onActivityResult' callback, as in Android Q
                    // all this extra code is necessary 'only' to get the permission,
                    // as the system doesn't perform any actual deletion at all.
                    // The onActivityResult doesn't have the target Uri, so you
                    // need to cache it somewhere.
                    activity.startIntentSenderForResult(intent, requestCode, null, 0, 0, 0, null)
                }
            } else {
                // As usual for older APIs
//            for (uri in uriList) {
                resolver.delete(uriList!!, null, null)
//            }
            }
        }

    }
        //runtime storage permission
        private fun checkPermission(): Boolean {
            val READ_EXTERNAL_PERMISSION =
                    ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            if (READ_EXTERNAL_PERMISSION != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSION_WRITE)
                return false
            }
            return true
        }

//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == PERMISSION_WRITE && grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            //do somethings
//        }
//    }

        fun onCheckboxClicked(view: View) {
            // Is the view now checked?
//        boolean checked = ((CheckBox) view).isChecked();
            // Check which checkbox was clicked
            selectedCheckBoxValue?.text = ""
            when (view.id) {
                R.id.check_drive_off -> driveOffTvrRequest.isDriveOff = (view as CheckBox).isChecked
                R.id.check_tvr -> driveOffTvrRequest.isTvr = (view as CheckBox).isChecked
            }
            driveOffTVRViewModel?.driveOffTvrAPI(driveOffTvrRequest, mIssueTicketId)
        }

        override fun onDestroy() {
            mContext = null
            super.onDestroy()
        }

        companion object {
            const val PERMISSION_WRITE = 0
            var savePrintImagePath: File? = null
        }

        fun fineAmountFormating(amount: String?): String {
            var mAmount: String = ""
            val amountLenght: Int = amount!!.length
            var amount1: String = "0"
            var amount2: String = "0"
            var amount3: String = "0"
            var amount4: String = "0"
            var amount5: String = "0"
            var amount6: String = "0"

            try {
                if (amountLenght == 1) {
                    amount4 = amount + ""
                } else if (amountLenght == 2) {
                    amount3 = amount[amountLenght - 1].toString() + ""
                    amount4 = amount[amountLenght - 2].toString() + ""
                } else if (amountLenght == 3) {
                    amount3 = amount[amountLenght - 1].toString() + ""
                    amount4 = amount[amountLenght - 2].toString() + ""
                    amount5 = amount[amountLenght - 3].toString() + ""
                } else if (amountLenght == 4) {
                    amount2 = amount[amountLenght - 1].toString() + ""
                    amount3 = amount[amountLenght - 2].toString() + ""
                    amount4 = amount[amountLenght - 3].toString() + ""
                    amount5 = amount[amountLenght - 4].toString() + ""
                } else if (amountLenght == 5) {
                    amount2 = amount[amountLenght - 1].toString() + ""
                    amount3 = amount[amountLenght - 2].toString() + ""
                    amount4 = amount[amountLenght - 3].toString() + ""
                    amount5 = amount[amountLenght - 4].toString() + ""
                    amount6 = amount[amountLenght - 5].toString() + ""
                } else if (amountLenght == 6) {
                    amount1 = amount[amountLenght - 1].toString() + ""
                    amount2 = amount[amountLenght - 2].toString() + ""
                    amount3 = amount[amountLenght - 3].toString() + ""
                    amount4 = amount[amountLenght - 4].toString() + ""
                    amount5 = amount[amountLenght - 5].toString() + ""
                    amount6 = amount[amountLenght - 6].toString() + ""
                }
                mAmount = amount6 + amount5 + amount4 + amount3 + amount2 + amount1
//            Toast.makeText(mContext,mAmount, Toast.LENGTH_SHORT).show()
                return mAmount;
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return mAmount;
        }

    private fun isOverTimeParkingAPICallingSettingDrive() {
        try {
            val settingsList = Singleton.getDataSetList(DATASET_SETTINGS_LIST, mDb)

            val timingRecordCarryForwardValue = settingsList?.firstOrNull {
                it.type.equals(
                    SETTINGS_FLAG_TIMING_RECORD_CARRY_FORWARD,
                    true
                ) && it.mValue.toBooleanFromYesNo()
            }?.mValue.toBooleanFromYesNo()

            if (timingRecordCarryForwardValue != null&& timingRecordCarryForwardValue==true) {
                callAddTimingApi()
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
            if (isInternetAvailable(this@TicketDetailsActivity)) {
                val mAddTimingRequest = sharedPreference.readOverTimeParkingTicketDetails(SharedPrefKey.TIMING_DATA_OVERTIME_PARKING_CHARLESTION,"")
                mAddTimingRequest!!.markStartTimestamp = AppUtils.splitDateLpr("")
                mAddTimingRequest!!.markIssueTimestamp = AppUtils.splitDateLpr("")
                if(mAddTimingRequest!!.lprNumber!=null && mAddTimingRequest!!.lprNumber!!.isNotEmpty()) {
                    mAddTimingViewModel?.hitAddTimingApi(mAddTimingRequest)
                }
            } else {
                LogUtil.printToastMSGForErrorWarning(
                    applicationContext,
                    getString(R.string.err_msg_connection_was_refused)
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun isButtonVisibilitySettingDrive() {
        try {
            val settingsList = Singleton.getDataSetList(DATASET_SETTINGS_LIST, mDb)
            if (settingsList != null && settingsList!!.isNotEmpty()) {
                for (i in settingsList!!.indices) {
                    LogUtil.printLog("Setting", settingsList!![i].type.toString())
                    if (settingsList!![i].type.equals(
                            "IS_CANCEL_HIDE",
                            ignoreCase = true
                        )
                        && settingsList!![i].mValue.equals("YES", ignoreCase = true)
                    ) {
                        mBtnCancelTicket!!.visibility = View.GONE
                    }
                    if (settingsList!![i].type.equals("IS_VOIDANDREISSUE_HIDE",
                            ignoreCase = true)
                        && settingsList!![i].mValue.equals("YES", ignoreCase = true)){
                        mBtnVoidAndReissue!!.visibility = View.GONE
                    }
                    if (settingsList!![i].type.equals("IS_ISSUEMORE_HIDE",
                            ignoreCase = true)
                        && settingsList!![i].mValue.equals("YES", ignoreCase = true)){
                        linearLayoutCompatIssueMore!!.visibility = View.GONE
                    }
                    if (settingsList!![i].type.equals("IS_DRIVE_OFF_HIDE",
                            ignoreCase = true)
                        && settingsList!![i].mValue.equals("YES", ignoreCase = true)){
                        checkBoxDriveOff!!.visibility = View.GONE
                    }
                    if (settingsList!![i].type.equals("IS_TVR_HIDE",
                            ignoreCase = true)
                        && settingsList!![i].mValue.equals("YES", ignoreCase = true)){
                        checkBoxTvr!!.visibility = View.GONE
                    }
                    if (settingsList!![i].type.equals("IS_CANCELLATION_REQUEST_BUTTON_VISIBLE",
                            ignoreCase = true)
                        && settingsList!![i].mValue.equals("YES", ignoreCase = true)){
                        btnCancellationRequest!!.visibility = View.VISIBLE
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun enablePrintButton(){
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            linearLayoutCompatPrint!!.isEnabled = true
            ivPrintBtn!!.isEnabled = true
            val alpha = 1.00f
            val alphaUp = AlphaAnimation(alpha, alpha)
            alphaUp.fillAfter = true
            linearLayoutCompatPrint!!.startAnimation(alphaUp)
        }, 300)
    }
 }