package com.parkloyalty.lpr.scan.ui.officerdailysummary

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.text.TextUtils
import android.view.View
import android.widget.TableLayout
import android.widget.TableRow
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresPermission
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.net.toUri
import androidx.lifecycle.Observer
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.google.android.material.textfield.TextInputEditText
import com.parkloyalty.lpr.scan.BuildConfig
import com.parkloyalty.lpr.scan.R
import com.parkloyalty.lpr.scan.basecontrol.BaseActivity
import com.parkloyalty.lpr.scan.extensions.nullOrEmptySafety
import com.parkloyalty.lpr.scan.extensions.nullSafety
import com.parkloyalty.lpr.scan.extensions.showToast
import com.parkloyalty.lpr.scan.interfaces.Constants
import com.parkloyalty.lpr.scan.interfaces.CustomDialogHelper
import com.parkloyalty.lpr.scan.interfaces.PrintInterface
import com.parkloyalty.lpr.scan.interfaces.SharedPrefKey
import com.parkloyalty.lpr.scan.network.ApiResponse
import com.parkloyalty.lpr.scan.network.DynamicAPIPath
import com.parkloyalty.lpr.scan.network.Status
import com.parkloyalty.lpr.scan.startprinterfull.StarPrinterActivity
import com.parkloyalty.lpr.scan.startprinterfull.SwitchPrinterActivity
import com.parkloyalty.lpr.scan.ui.check_setup.activity.graph.model.check_setup.VehicleListModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DropdownDatasetResponse
import com.parkloyalty.lpr.scan.ui.officerdailysummary.model.OfficerDailySummaryResponse
import com.parkloyalty.lpr.scan.ui.officerdailysummary.model.OfficerDailySummaryViewModel
import com.parkloyalty.lpr.scan.ui.officerdailysummary.model.Quadruple
import com.parkloyalty.lpr.scan.ui.printer.PrinterActivity
import com.parkloyalty.lpr.scan.ui.printer.PrinterType
import com.parkloyalty.lpr.scan.ui.printer.StarPrinterUseCase
import com.parkloyalty.lpr.scan.ui.printer.ZebraPrinterUseCase
import com.parkloyalty.lpr.scan.ui.xfprinter.SectionType
import com.parkloyalty.lpr.scan.ui.xfprinter.TextUtils.CUSTOM_PRINT_COMMAND_SEPARATOR
import com.parkloyalty.lpr.scan.ui.xfprinter.XfPrinterActivity
import com.parkloyalty.lpr.scan.ui.xfprinter.XfPrinterUseCase
import com.parkloyalty.lpr.scan.util.AppUtils
import com.parkloyalty.lpr.scan.util.LogUtil
import com.parkloyalty.lpr.scan.util.permissions.BluetoothPermissionUtil
import com.parkloyalty.lpr.scan.utils.ObjectMapperProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.lang.StringBuilder
import java.text.SimpleDateFormat
import java.util.*
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

//class OfficerDailySummaryActivity : SwitchPrinterActivity(), CustomDialogHelper, PrintInterface {
@AndroidEntryPoint
class OfficerDailySummaryActivity : BaseActivity(), CustomDialogHelper, PrintInterface {
//class OfficerDailySummaryActivity : XfPrinterActivity(), CustomDialogHelper, PrintInterface {
//class OfficerDailySummaryActivity : StarPrinterActivity(), CustomDialogHelper, PrintInterface {
    @JvmField
    @BindView(R.id.txt_name)
    var appCompatTextViewOfcName: AppCompatTextView? = null

    @JvmField
    @BindView(R.id.txt_badgeid)
    var appCompatTextViewBadgeId: AppCompatTextView? = null

    @JvmField
    @BindView(R.id.txt_beat)
    var appCompatTextViewBeat: AppCompatTextView? = null

    @JvmField
    @BindView(R.id.txt_squad)
    var appCompatTextViewSquad: AppCompatTextView? = null

    @JvmField
    @BindView(R.id.txt_supervisor)
    var appCompatTextViewSupervisor: AppCompatTextView? = null

    @JvmField
    @BindView(R.id.txt_device)
    var appCompatTextViewDevice: AppCompatTextView? = null

    @JvmField
    @BindView(R.id.txt_printer)
    var appCompatTextViewPrinter: AppCompatTextView? = null

    @JvmField
    @BindView(R.id.txt_radio)
    var appCompatTextViewRadio: AppCompatTextView? = null

    //Shift
    @JvmField
    @BindView(R.id.txt_login)
    var appCompatTextViewLogin: AppCompatTextView? = null

    @JvmField
    @BindView(R.id.txt_first_city)
    var appCompatTextViewFirstCity: AppCompatTextView? = null

    @JvmField
    @BindView(R.id.txt_last_city)
    var appCompatTextViewLastCity: AppCompatTextView? = null

    @JvmField
    @BindView(R.id.txt_first_scan)
    var appCompatTextViewFirstScan: AppCompatTextView? = null

    @JvmField
    @BindView(R.id.txt_lunch)
    var appCompatTextViewLunch: AppCompatTextView? = null

    @JvmField
    @BindView(R.id.txt_break_1)
    var appCompatTextViewBreak1: AppCompatTextView? = null

    @JvmField
    @BindView(R.id.txt_break_2)
    var appCompatTextViewBreak2: AppCompatTextView? = null

    @JvmField
    @BindView(R.id.txt_departure)
    var appCompatTextViewDeparture: AppCompatTextView? = null

    @JvmField
    @BindView(R.id.txt_dropoff)
    var appCompatTextViewDropOff: AppCompatTextView? = null

    @JvmField
    @BindView(R.id.txt_logout)
    var appCompatTextViewLogout: AppCompatTextView? = null

    //        Issuance Info  Details
    @JvmField
    @BindView(R.id.txt_citation)
    var appCompatTextViewCitation: AppCompatTextView? = null

    @JvmField
    @BindView(R.id.txt_cancel)
    var appCompatTextViewCancel: AppCompatTextView? = null

    @JvmField
    @BindView(R.id.txt_last_refused_tvr)
    var appCompatTextViewLastRefusedTvr: AppCompatTextView? = null

    @JvmField
    @BindView(R.id.txt_drive_off)
    var appCompatTextViewDriveOff: AppCompatTextView? = null

    @JvmField
    @BindView(R.id.txt_reisssue)
    var appCompatTextViewReissue: AppCompatTextView? = null

    @JvmField
    @BindView(R.id.txt_rescind)
    var appCompatTextViewRescind: AppCompatTextView? = null

    @JvmField
    @BindView(R.id.txt_warning)
    var appCompatTextViewWarning: AppCompatTextView? = null

    @JvmField
    @BindView(R.id.txt_pcb_cancel)
    var appCompatTextViewPbcCancel: AppCompatTextView? = null

    // Scan Hit Info  Details-
    @JvmField
    @BindView(R.id.txt_payment)
    var appCompatTextViewPayment: AppCompatTextView? = null

    @JvmField
    @BindView(R.id.txt_permit)
    var appCompatTextViewPermit: AppCompatTextView? = null

    @JvmField
    @BindView(R.id.txt_scofflaw)
    var appCompatTextViewScofflaw: AppCompatTextView? = null

    @JvmField
    @BindView(R.id.txt_mark)
    var appCompatTextViewMark: AppCompatTextView? = null

    @JvmField
    @BindView(R.id.edittxt_comment)
    var textInputEditTextComment: TextInputEditText? = null

    @JvmField
    @BindView(R.id.lay_button_submit)
    var linearLayoutCompatSubmit: LinearLayoutCompat? = null

    @JvmField
    @BindView(R.id.ll_table_data)
    var tableLayoutSummary: LinearLayoutCompat? = null

    @JvmField
    @BindView(R.id.txt_scan_count)
    var appCompatTextViewTotalScanCount: AppCompatTextView? = null

    @JvmField
    @BindView(R.id.txt_issuance_count)
    var appCompatTextViewTotalIssuanceCount: AppCompatTextView? = null

    @JvmField
    @BindView(R.id.txt_current_date)
    var appCompatTextViewDate: AppCompatTextView? = null

    @JvmField
    @BindView(R.id.tb_summary)
    var tableLayoutOfficerDetails: TableLayout? = null

    private var responseModel: OfficerDailySummaryResponse? = null
    private var savePrintImagePath: File? = null
    val commandXFPrinter : java.lang.StringBuilder?= StringBuilder()

    private val officerDailySummaryViewModel: OfficerDailySummaryViewModel? by viewModels()

    private var zebraPrinterUseCase: ZebraPrinterUseCase? = null
    private var starPrinterUseCase: StarPrinterUseCase? = null
    private var xfPrinterUseCase: XfPrinterUseCase? = null

    private val bluetoothPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val granted = permissions.entries.all { it.value }
            if (granted) {
                // Nothing to implement here
            } else {
                BluetoothPermissionUtil.requestBluetoothSetting(this@OfficerDailySummaryActivity)
            }
        }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_officer_daily_summary)

        if (LogUtil.getPrinterTypeForPrint() == PrinterType.STAR_PRINTER) {
            LogUtil.printLog("==>Printer","Inside")
            starPrinterUseCase = StarPrinterUseCase()
            starPrinterUseCase?.setPrintInterfaceCallback(this)
            starPrinterUseCase?.initialize(this@OfficerDailySummaryActivity)
        }else if (LogUtil.getPrinterTypeForPrint() == PrinterType.XF2T_PRINTER) {
            LogUtil.printLog("==>Printer","Inside")
            xfPrinterUseCase = XfPrinterUseCase(this)
            lifecycle.addObserver(xfPrinterUseCase!!)
            xfPrinterUseCase?.setPrintInterfaceCallback(this)
            xfPrinterUseCase?.initialize(this@OfficerDailySummaryActivity)
        } else if (LogUtil.getPrinterTypeForPrint() == PrinterType.ZEBRA_PRINTER) {
            LogUtil.printLog("==>Printer", "Inside")
            zebraPrinterUseCase = ZebraPrinterUseCase()
            zebraPrinterUseCase?.setPrintInterfaceCallback(this)
            zebraPrinterUseCase?.initialize(
                activity = this@OfficerDailySummaryActivity,
                contentResolver = contentResolver,
                sharedPreference = sharedPreference
            )
        }

        ButterKnife.bind(this)
        addObservers()
        init()

        BluetoothPermissionUtil.checkBluetoothPermissions(
            activity = this,
            permissionLauncher = bluetoothPermissionLauncher
        ) {
            //Nothing to implement here in onCreate, This will check bluetooth permission and redirect user to allow bluetooth permission, in case misssed
        }
    }

    private val officerDailySummeryResponseObserver = Observer { apiResponse: ApiResponse? ->
        consumeResponse(
            apiResponse,
            DynamicAPIPath.SUMIT_BOOT
        )
    }

    private fun addObservers() {
        officerDailySummaryViewModel?.response?.observe(this, officerDailySummeryResponseObserver)
    }

    override fun removeObservers() {
        super.removeObservers()
        officerDailySummaryViewModel?.response?.removeObserver(officerDailySummeryResponseObserver)
    }

    private fun init() {
        //for full screen toolbar
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        setToolbar()
        callAPI()
        appCompatTextViewDate!!.text = AppUtils.getCurrentDateTime()
    }

    //init toolbar navigation
    private fun setToolbar() {
//        if(imgOptions)
        initToolbar(
            2,
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

    private fun callAPI() {
        try {
            val shift = "?shift=" + sharedPreference.read(SharedPrefKey.LOGIN_SHIFT, "")
            officerDailySummaryViewModel!!.hitOfficerDailySummaryApi(shift)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /*Api response */
    private fun consumeResponse(apiResponse: ApiResponse?, tag: String) {
        when (apiResponse!!.status) {
            Status.LOADING -> showProgressLoader(getString(R.string.scr_message_please_wait))
            Status.SUCCESS -> {
                dismissLoader()
                if (!apiResponse.data!!.isNull) {
                    LogUtil.printLog(tag, apiResponse.data.toString())
                    try {
                        if (tag.equals(DynamicAPIPath.SUMIT_BOOT, ignoreCase = true)) {

                             responseModel = ObjectMapperProvider.fromJson(apiResponse.data.toString(), OfficerDailySummaryResponse::class.java)

                            if (responseModel != null && responseModel!!.isStatus) {
                                try {
                                    setDataOnUI()
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            } else {
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            Status.ERROR -> dismissLoader()
            else -> {}
        }
    }

    private fun setDataOnUI() {
        try {
            try {
//                //XF 2T printer
//                if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_NORWALK, ignoreCase = true)||
//                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTHMIAMI, ignoreCase = true)||
//                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_COHASSET, ignoreCase = true)) {
//                    tableLayoutOfficerDetails?.visibility = View.GONE
//                }
                appCompatTextViewOfcName?.text = responseModel?.data?.officerDailySummary?.officerDetails?.username
                appCompatTextViewBadgeId?.text = responseModel?.data?.officerDailySummary?.officerDetails?.badgeId
                appCompatTextViewBeat?.text = responseModel?.data?.officerDailySummary?.officerDetails?.beat
                appCompatTextViewSquad?.text = responseModel?.data?.officerDailySummary?.officerDetails?.squad
                appCompatTextViewSupervisor?.text = responseModel?.data?.officerDailySummary?.officerDetails?.supervisor
                appCompatTextViewDevice?.text = responseModel?.data?.officerDailySummary?.officerDetails?.deviceName
                appCompatTextViewPrinter?.text = responseModel?.data?.officerDailySummary?.officerDetails?.printer
                appCompatTextViewRadio?.text = responseModel?.data?.officerDailySummary?.officerDetails?.radio

                //Set content description for accessibility
                appCompatTextViewOfcName?.contentDescription = responseModel?.data?.officerDailySummary?.officerDetails?.username.nullOrEmptySafety(getString(R.string.lbl_no_data))
                appCompatTextViewBadgeId?.contentDescription = responseModel?.data?.officerDailySummary?.officerDetails?.badgeId.nullOrEmptySafety(getString(R.string.lbl_no_data))
                appCompatTextViewBeat?.contentDescription = responseModel?.data?.officerDailySummary?.officerDetails?.beat.nullOrEmptySafety(getString(R.string.lbl_no_data))
                appCompatTextViewSquad?.contentDescription = responseModel?.data?.officerDailySummary?.officerDetails?.squad.nullOrEmptySafety(getString(R.string.lbl_no_data))
                appCompatTextViewSupervisor?.contentDescription = responseModel?.data?.officerDailySummary?.officerDetails?.supervisor.nullOrEmptySafety(getString(R.string.lbl_no_data))
                appCompatTextViewDevice?.contentDescription = responseModel?.data?.officerDailySummary?.officerDetails?.deviceName.nullOrEmptySafety(getString(R.string.lbl_no_data))
                appCompatTextViewPrinter?.contentDescription = responseModel?.data?.officerDailySummary?.officerDetails?.printer.nullOrEmptySafety(getString(R.string.lbl_no_data))
                appCompatTextViewRadio?.contentDescription = responseModel?.data?.officerDailySummary?.officerDetails?.radio.nullOrEmptySafety(getString(R.string.lbl_no_data))


                if (LogUtil.getPrinterTypeForPrint() == PrinterType.XF2T_PRINTER) {
                    makeObjectForCommandPrint((getString(R.string.scr_lbl_details)), "",2, 0,1301)
                    makeObjectForCommandPrint((appCompatTextViewDate!!.text!!.toString()), "",2, 2,2401)
                    responseModel?.data?.officerDailySummary?.officerDetails?.let { officer ->
                        val printItems = listOf(
//                            Triple(getString(R.string.scr_lbl_details), "", 2),
//                            Triple(appCompatTextViewDate!!.text!!.toString(), "", 2),
                            Quadruple("Name", officer.username ?: "",1,1020,),
                            Quadruple("Id", officer.badgeId ?: "", 1,1006),
                            Quadruple(getString(R.string.scr_lbl_beat), officer.beat.takeIf { it!!.isNotEmpty() } ?: "0", 1,1006),
                            Quadruple(getString(R.string.scr_lbl_squad), officer.squad.takeIf { it!!.isNotEmpty() } ?: "0", 1,1006),

                            Quadruple(getString(R.string.scr_lbl_supervisor), officer.supervisor.takeIf { it!!.isNotEmpty() } ?: "0", 1,1214),
                            Quadruple(getString(R.string.scr_lbl_device), officer.deviceName.takeIf { it!!.isNotEmpty() } ?: "0", 1,808),
                            Quadruple(getString(R.string.scr_lbl_printer), officer.printer.takeIf { it!!.isNotEmpty() } ?: "0", 1,808),
                            Quadruple(getString(R.string.scr_lbl_radio), officer.radio.takeIf { it!!.isNotEmpty() } ?: "0", 1,808)
                        )


                        printItems.forEachIndexed { index, (labelResId, value,type,width) ->
                            makeObjectForCommandPrint((labelResId), value,type, index,width)
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            // SHIFT
            try {
                appCompatTextViewLogin?.text =
                    if (responseModel?.data?.officerDailySummary?.shifts?.get(0)?.shiftDetails?.loginTimestamp != null &&
                        !responseModel?.data?.officerDailySummary?.shifts?.get(0)?.shiftDetails?.loginTimestamp?.isEmpty()
                            .nullSafety()
                    ) {
                        responseModel?.data?.officerDailySummary?.shifts?.get(0)?.shiftDetails?.loginTimestamp?.split(
                                "T".toRegex())?.toTypedArray()?.get(1)?.let { AppUtils.dateConvertLPRPayment(it) }
                    } else "0"
                appCompatTextViewLogout!!.text =
                    if (responseModel?.data?.officerDailySummary?.shifts?.get(0)?.shiftDetails?.logoutTimestamp != null &&
                        !responseModel?.data?.officerDailySummary?.shifts?.get(0)?.shiftDetails?.logoutTimestamp?.isEmpty()
                            .nullSafety()
                    ) responseModel?.data?.officerDailySummary?.shifts?.get(0)?.shiftDetails?.logoutTimestamp?.split(
                        "T".toRegex()
                    )?.toTypedArray()
                        ?.get(1)?.let { AppUtils.dateConvertLPRPayment(it) } else "0"
                appCompatTextViewFirstCity!!.text =
                    if (responseModel?.data?.officerDailySummary?.shifts?.get(0)?.issuanceMetrics?.firstIssuanceTimestamp != null
                        && !responseModel?.data?.officerDailySummary?.shifts?.get(0)?.issuanceMetrics?.firstIssuanceTimestamp?.isEmpty()
                            .nullSafety()
                    ) responseModel?.data?.officerDailySummary?.shifts?.get(0)?.issuanceMetrics?.firstIssuanceTimestamp?.split(
                        "T".toRegex()
                    )?.toTypedArray()
                        ?.get(1)?.let { AppUtils.dateConvertLPRPayment(it) } else "0"
                appCompatTextViewLastCity!!.text =
                    if (responseModel?.data?.officerDailySummary?.shifts?.get(0)?.issuanceMetrics?.lastIssuanceTimestamp != null &&
                        !responseModel?.data?.officerDailySummary?.shifts?.get(0)?.issuanceMetrics?.lastIssuanceTimestamp?.isEmpty()
                            .nullSafety()
                    ) responseModel?.data?.officerDailySummary?.shifts?.get(0)?.issuanceMetrics?.lastIssuanceTimestamp?.split(
                        "T".toRegex()
                    )?.toTypedArray()
                        ?.get(1)?.let { AppUtils.dateConvertLPRPayment(it) } else "0"
                appCompatTextViewFirstScan!!.text =
                    if (responseModel?.data?.officerDailySummary?.shifts?.get(0)?.scanMetrics?.firstScanTimestamp != null &&
                        !responseModel?.data?.officerDailySummary?.shifts?.get(0)?.scanMetrics?.firstScanTimestamp?.isEmpty()
                            .nullSafety()
                    ) responseModel?.data?.officerDailySummary?.shifts?.get(0)?.scanMetrics?.firstScanTimestamp?.split(
                        "T".toRegex()
                    )?.toTypedArray()
                        ?.get(1)?.let { AppUtils.dateConvertLPRPayment(it) } else "0"
                appCompatTextViewLunch!!.text =
                    if (responseModel?.data?.officerDailySummary?.shifts?.get(0)?.shiftDetails?.lunchTimestamp != null &&
                        !responseModel?.data?.officerDailySummary?.shifts?.get(0)?.shiftDetails?.lunchTimestamp?.isEmpty()
                            .nullSafety()
                    ) responseModel?.data?.officerDailySummary?.shifts?.get(0)?.shiftDetails?.lunchTimestamp?.split(
                        "T".toRegex()
                    )?.toTypedArray()
                        ?.get(1)?.let { AppUtils.dateConvertLPRPayment(it) } else "0"
                appCompatTextViewBreak1!!.text =
                    if (responseModel?.data?.officerDailySummary?.shifts?.get(0)?.shiftDetails?.break1Timestamp != null &&
                        !responseModel?.data?.officerDailySummary?.shifts?.get(0)?.shiftDetails?.break1Timestamp?.isEmpty()
                            .nullSafety()
                    ) responseModel?.data?.officerDailySummary?.shifts?.get(0)?.shiftDetails?.break1Timestamp?.split(
                        "T".toRegex()
                    )?.toTypedArray()
                        ?.get(1)?.let { AppUtils.dateConvertLPRPayment(it) } else "0"
                appCompatTextViewBreak2!!.text =
                    if (responseModel?.data?.officerDailySummary?.shifts?.get(0)?.shiftDetails?.break2Timestamp != null &&
                        !responseModel?.data?.officerDailySummary?.shifts?.get(0)?.shiftDetails?.break2Timestamp?.isEmpty()
                            .nullSafety()
                    ) responseModel?.data?.officerDailySummary?.shifts?.get(0)?.shiftDetails?.break2Timestamp?.split(
                        "T".toRegex()
                    )?.toTypedArray()
                        ?.get(1)?.let { AppUtils.dateConvertLPRPayment(it) } else "0"


                if (LogUtil.getPrinterTypeForPrint() == PrinterType.XF2T_PRINTER) {

                    val shift = responseModel
                        ?.data?.officerDailySummary?.shifts?.firstOrNull() ?: return

                    makeObjectForCommandPrint((getString(R.string.scr_ibi_shift)), " ",4, 0,3008)
                    val shiftPrintItems = listOf(
//                        Triple((R.string.scr_ibi_shift), " ", 4),
                        Quadruple(R.string.scr_btn_login, appCompatTextViewLogin!!.text.toString(), 1,1010),
                        Quadruple(R.string.scr_lbl_first_city, appCompatTextViewLastCity!!.text.toString(), 1,1010),
                        Quadruple(R.string.scr_lbl_last_city,appCompatTextViewLastCity!!.text.toString(), 1,1010),
                        Quadruple(R.string.scr_lbl_first_scan,appCompatTextViewFirstScan!!.text.toString(), 1,1010),
                        Quadruple(R.string.scr_lbl_lunch,appCompatTextViewLunch!!.text.toString(), 1,1010),
                        Quadruple(R.string.scr_lbl_break_1,appCompatTextViewBreak1!!.text.toString(), 1,1010),
                        Quadruple(R.string.scr_lbl_break_2,appCompatTextViewBreak2!!.text.toString(), 1,1010),
                        Quadruple(R.string.scr_lbl_departure,"0", 1,1010),
//                        Triple(R.string.scr_lbl_drop_off,"0", 2),
//                        Triple(R.string.scr_lbl_logout,appCompatTextViewLogout!!.text.toString(), 2)
                    )
                    shiftPrintItems.forEachIndexed { index, (labelResId, value,type,width) ->
                        makeObjectForCommandPrint(getString(labelResId), value!!,type, index, width)
                    }
                    makeObjectForCommandPrint((getString(R.string.scr_lbl_drop_off)), "",2, 0,1818)
                    makeObjectForCommandPrint((getString(R.string.scr_lbl_logout)), appCompatTextViewLogout!!.text.toString(),2, 2,2020)

                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            // SCAN HIT INFO
            try {
                appCompatTextViewPayment?.text = responseModel?.data?.officerDailySummary?.shifts?.get(
                    0
                )?.scanMetrics?.scanPaymentHit.toString() + ""
                appCompatTextViewPermit?.text = responseModel?.data?.officerDailySummary?.shifts?.get(
                    0
                )?.scanMetrics?.scanPermitHit.toString() + ""
                appCompatTextViewScofflaw?.text = responseModel?.data?.officerDailySummary?.shifts?.get(
                    0
                )?.scanMetrics?.scanScofflawHit.toString() + ""
                appCompatTextViewMark?.text = responseModel?.data?.officerDailySummary?.shifts?.get(
                    0
                )?.scanMetrics?.scanTimingHit.toString() + ""
                appCompatTextViewTotalScanCount?.text =
                    getString(R.string.scr_lbl_total_scans) + " : " + responseModel?.data?.officerDailySummary?.shifts?.get(
                        0
                    )?.scanMetrics?.scanTotalHits + ""

                if (LogUtil.getPrinterTypeForPrint() == PrinterType.XF2T_PRINTER) {

                    val shift = responseModel
                        ?.data?.officerDailySummary?.shifts?.firstOrNull() ?: return

                    makeObjectForCommandPrint((getString(R.string.scr_lbl_scan_hit_info)), " ",2, 0,1802)
                    makeObjectForCommandPrint((appCompatTextViewTotalScanCount!!.text!!.toString()), " ",2, 2,1802)

                    val scanPrintItems = listOf(
//                        Triple(getString(R.string.scr_lbl_scan_hit_info), " ", 2),
//                        Triple(appCompatTextViewTotalScanCount!!.text!!.toString(), " ", 2),

                        Quadruple(getString(R.string.scr_btn_payment), appCompatTextViewPayment!!.text.toString(), 1,1010),
                        Quadruple(getString(R.string.scr_btn_permit), appCompatTextViewPermit!!.text.toString(), 1,1010),
                        Quadruple(getString(R.string.scr_btn_scofflaw),appCompatTextViewScofflaw!!.text.toString(), 1,1010),
                        Quadruple(getString(R.string.scr_lbl_mark),appCompatTextViewMark!!.text.toString(), 1,1010)
                    )
                    scanPrintItems.forEachIndexed { index, (labelResId, value,type,width) ->
                        makeObjectForCommandPrint((labelResId),
                            value!!.toString(),type, index,width)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }


            // ISSUANCE INFO
            try {
//                  for(int i=0; i<responseModel.data.getOfficerDailySummary().
//                          getShifts().get(0).getIssuanceCountsMetrics().size();i++) {
                appCompatTextViewCitation?.text = responseModel?.data?.officerDailySummary?.shifts?.get(
                    0
                )?.issuanceMetrics?.issuanceValid.toString() + ""

                appCompatTextViewCancel?.text = responseModel?.data?.officerDailySummary?.shifts?.get(
                    0
                )?.issuanceMetrics?.totalCancel.toString() + ""

                appCompatTextViewLastRefusedTvr?.text = responseModel?.data?.officerDailySummary?.shifts?.get(
                    0
                )?.issuanceMetrics?.tvrCount.toString() + ""

                appCompatTextViewDriveOff?.text = responseModel?.data?.officerDailySummary?.shifts?.get(
                    0
                )?.issuanceMetrics?.driveOffCount.toString() + ""

                appCompatTextViewReissue?.text = responseModel?.data?.officerDailySummary?.shifts?.get(
                    0
                )?.issuanceMetrics?.reissueCount.toString() + ""

                appCompatTextViewRescind?.text = responseModel?.data?.officerDailySummary?.shifts?.get(
                    0
                )?.issuanceMetrics?.issuanceRescind.toString() + ""

                appCompatTextViewWarning?.text = responseModel?.data?.officerDailySummary?.shifts?.get(
                        0
                )?.issuanceMetrics?.cancel.toString() + ""

                appCompatTextViewPbcCancel?.text = responseModel?.data?.officerDailySummary?.shifts?.get(
                    0
                )?.issuanceMetrics?.pbcCancelCount.toString() + ""

                appCompatTextViewTotalIssuanceCount?.text =
                        getString(R.string.scr_lbl_total_count) + " : " + responseModel?.data?.officerDailySummary?.shifts?.get(
                                0
                        )?.issuanceMetrics?.issuanceTotal + ""
                //                  }

                if (LogUtil.getPrinterTypeForPrint() == PrinterType.XF2T_PRINTER) {

                    val shift = responseModel
                        ?.data?.officerDailySummary?.shifts?.firstOrNull() ?: return

                    makeObjectForCommandPrint(getString(R.string.scr_lbl_issuance_info), " ",2, 0,1402)
                    makeObjectForCommandPrint((appCompatTextViewTotalIssuanceCount!!.text!!.toString()), " ",2, 2,1402)

                    val issuancePrintItems = listOf(
//                        Triple(getString(R.string.scr_lbl_issuance_info), " ", 2),
//                        Triple((appCompatTextViewTotalIssuanceCount?.text!!.toString()), " ", 2),
                        Quadruple(getString(R.string.scr_lbl_citation), appCompatTextViewCitation!!.text.toString(), 1,1010),
                        Quadruple(getString(R.string.scr_btn_total_cancel), appCompatTextViewCancel!!.text.toString(), 1,1010),
                        Quadruple(getString(R.string.scr_lbl_refused_tvr),appCompatTextViewLastRefusedTvr!!.text.toString(), 1,1010),
                        Quadruple(getString(R.string.scr_lbl_drive_off),appCompatTextViewDriveOff!!.text.toString(), 1,1010),

                        Quadruple(getString(R.string.scr_btn_reissue),appCompatTextViewReissue!!.text.toString(), 1,1010),
                        Quadruple(getString(R.string.scr_btn_rescind),appCompatTextViewRescind!!.text.toString(), 1,1010),
                        Quadruple(getString(R.string.scr_btn_cancel),appCompatTextViewCancel!!.text.toString(), 1,1010),
                        Quadruple(getString(R.string.scr_lbl_pbc_cancel),appCompatTextViewPbcCancel!!.text.toString(), 1,1010),
//                        Triple(getString(R.string.scr_lbl_comments),"", 4),
//                        Triple(textInputEditTextComment!!.text!!.toString(),"", 4),
                    )
                    issuancePrintItems.forEachIndexed { index, (labelResId, value,type,width) ->
                        makeObjectForCommandPrint((labelResId),
                            value!!.toString(),type, index,width)
                    }
                    makeObjectForCommandPrint(getString(R.string.scr_lbl_comments), " ",4, 0,3001)
                    makeObjectForCommandPrint((textInputEditTextComment!!.text!!.toString()), " ",4, 0,3801)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onYesButtonClick() {}
    override fun onNoButtonClick() {}
    override fun onYesButtonClickParam(msg: String?) {}

    /*perform click actions*/
    @OnClick(R.id.btn_submit)
    fun onClick(view: View) {
        val id = view.id
        when (id) {
            R.id.btn_submit -> {
//                //XF 2T printer
//                if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_NORWALK, ignoreCase = true)||
//                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTHMIAMI, ignoreCase = true)||
//                    BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_COHASSET, ignoreCase = true)) {
//                    tableLayoutOfficerDetails?.post {
//                        tableLayoutOfficerDetails?.visibility = View.GONE
//                        tableLayoutOfficerDetails?.invalidate()
//                        loadBitmapFromView(tableLayoutSummary)
//                    }
//                }else{
                BluetoothPermissionUtil.checkBluetoothPermissions(
                    activity = this,
                    permissionLauncher = bluetoothPermissionLauncher
                ) {
                    loadBitmapFromView(tableLayoutSummary)
                }

//                }

            }
        }
    }

    //mTvBadgeId.setError(getString(R.string.val_msg_please_enter_badge_id));
    private fun isFormValid(): Boolean {
        if (TextUtils.isEmpty(textInputEditTextComment!!.text.toString().trim())) {
            //mTvBadgeId.setError(getString(R.string.val_msg_please_enter_badge_id));
            textInputEditTextComment!!.requestFocus()
            textInputEditTextComment!!.isFocusable = true
            textInputEditTextComment!!.error = getString(R.string.err_lbl_side_of_street)
            AppUtils.showKeyboard(this@OfficerDailySummaryActivity)
            return false
        }
        return true
    }

    fun loadBitmapFromView(view: View?) {
        try {

//            view?.measure(
//                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
//                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
//            )
//            view?.layout(0, 0, view.measuredWidth, view.measuredHeight)

            val b = Bitmap.createBitmap(
                view!!.width,
                view.height, Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(b)
            view.draw(canvas)
            //                drawTextToBitmap(b,"R");
            //XF 2T printer
//            if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_NORWALK, ignoreCase = true)||
//                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_SOUTHMIAMI, ignoreCase = true)||
//                BuildConfig.FLAVOR.equals(Constants.FLAVOR_TYPE_COHASSET, ignoreCase = true)) {
//                val paddedBitmap =
//                    increaseBitmapWidthWithWhiteSpace(b, 500) // Add 100px white space
//                SaveImageMM(paddedBitmap)
//            }else{
                SaveImageMM(b)
//            }

            mainScope.launch{
                delay(400)
                if (LogUtil.getPrinterTypeForPrint() == PrinterType.STAR_PRINTER) {
                    starPrinterUseCase?.printDailySummery(
                        savePrintImagePath!!, this@OfficerDailySummaryActivity,
                        "OfficerDailySummary", "", ""
                    )
                }else if (LogUtil.getPrinterTypeForPrint() == PrinterType.XF2T_PRINTER) {
                    makeObjectForCommandPrint((textInputEditTextComment!!.text!!.toString()), " ",4, 0,3502)
                    xfPrinterUseCase?.printDailySummery(
                        savePrintImagePath!!, this@OfficerDailySummaryActivity,
                        "OfficerDailySummary", "", "",commandXFPrinter!!
                    )
                } else if (LogUtil.getPrinterTypeForPrint() == PrinterType.ZEBRA_PRINTER) {
                    zebraPrinterUseCase?.printDailySummery(
                        savePrintImagePath!!, this@OfficerDailySummaryActivity,
                        "OfficerDailySummary", "", ""
                    )
//                    printDialySummery(savePrintImagePath!!,this@OfficerDailySummaryActivity,
//                        "OfficerDailySummary", "","")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //TODO will add comments later
    private fun SaveImageMM(finalBitmap: Bitmap) {
        val myDir = File(
            Environment.getExternalStorageDirectory().absolutePath,
            Constants.FILE_NAME + Constants.CAMERA
        )
        myDir.mkdirs()
//        val timeStamp = SimpleDateFormat("yyyMMdd_HHmmss", Locale.US).format(Date())
        val fname = "OfficerDailySummary.jpg"
        savePrintImagePath = File(myDir, fname)
        if (savePrintImagePath!!.exists()) savePrintImagePath!!.delete()
        try {
            sharedPreference.write(
                SharedPrefKey.REPRINT_PRINT_BITMAP,
                savePrintImagePath!!.absoluteFile.toString()
            )
            //new ImageCompression(this,file.getAbsolutePath()).execute(finalBitmap);
            val out = FileOutputStream(savePrintImagePath)
            //finalBitmap = Bitmap.createScaledBitmap(finalBitmap,(int)1080/2,(int)1920/2, true);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 60, out) //less than 300 kb
            out.flush()
            out.close()
            //String oldFname = "IMG_temp.jpg";
            //File oldFile = new File(myDir, oldFname);
            //if (oldFile.exists()) oldFile.delete();
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onActionSuccess(value:String) {}

    fun makeObjectForCommandPrint(label:String,value: String,type :Int,index :Int,width:Int){
        val model : VehicleListModel?= VehicleListModel()
        model?.offNameFirst = label
        model?.offTypeFirst = value
        model?.type = type
//        model?.mPrintOrder = printOrderCount!!.get(index)
//
        val xValue = if (type == 2) {
            when (index % 2) {
                0 -> printXValue[0]  // Indices 0, 2, 4, ...
                1 -> printXValue[2]  // Indices 1, 3, 5, ...
                else -> 0.0
            }
        } else {
            when (index % 4) {
                0 -> printXValue[0]
                1 -> printXValue[1]
                2 -> printXValue[2]
                3 -> printXValue[3]
                else -> 0.0
            }
        }
        model?.mAxisX = xValue
        model?.mAxisY = calculateYPosition(index)
//        model?.mFontSizeInt = 1623
        model?.mFontSizeInt = width
        model?.type = type
        officerSummeryByCommand(model!!)
    }

    fun officerSummeryByCommand(mObject : VehicleListModel){


            mObject.sectionType = SectionType.BODY
//            mObject.mTextAlignment = TextAlignmentForCommandPrint.LEFT
        commandXFPrinter?.append(ObjectMapperProvider.toJson(mObject))
        commandXFPrinter?.append(CUSTOM_PRINT_COMMAND_SEPARATOR)
            LogUtil.printLog("==>",ObjectMapperProvider.toJson(mObject))

    }
    val printOrderCount = doubleArrayOf(
        1.0, 1.5, 1.66, 2.0, 2.5, 2.66, 3.0, 3.5, 3.66, 4.0, 4.5, 4.66,
        5.0, 5.5, 5.66, 6.0, 6.5, 6.66, 7.0, 7.5, 7.66, 8.0, 8.5, 8.66, 9.0, 9.5, 9.66, 10.0
    )
    val printXValue = doubleArrayOf(
        10.0, 22.0, 34.0,46.0
    )

    var y=1010.0
    fun calculateYPosition(index: Int): Double{

            val value = when (index % 4) {
                0 -> {
                    y += 120
                    y
                }  // Indices 0, 3, 6, 9
                1 -> y  // Indices 1, 4, 7
                else -> y // Optionally use 3rd element or fallback
            }
        return value
    }

    override fun onStop() {
        super.onStop()
        if (LogUtil.getPrinterTypeForPrint() == PrinterType.ZEBRA_PRINTER) {
            zebraPrinterUseCase?.disconnect()
        }
    }
}