package com.parkloyalty.lpr.scan.qrcode

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Point
import android.os.Bundle
import android.os.Environment
import android.text.TextUtils
import android.view.View
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.AdapterView.OnItemClickListener
import androidx.appcompat.widget.*
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.zxing.WriterException
import com.parkloyalty.lpr.scan.BuildConfig
import com.parkloyalty.lpr.scan.R
import com.parkloyalty.lpr.scan.basecontrol.BaseActivity
import com.parkloyalty.lpr.scan.basecontrol.BaseApplication
import com.parkloyalty.lpr.scan.database.AppDatabase
import com.parkloyalty.lpr.scan.extensions.nullSafety
import com.parkloyalty.lpr.scan.interfaces.Constants
import com.parkloyalty.lpr.scan.interfaces.CustomDialogHelper
import com.parkloyalty.lpr.scan.interfaces.SharedPrefKey
import com.parkloyalty.lpr.scan.ui.check_setup.activity.LprPreviewActivity
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DatasetResponse
import com.parkloyalty.lpr.scan.ui.login.model.WelcomeForm
import com.parkloyalty.lpr.scan.util.AppUtils
import com.parkloyalty.lpr.scan.util.LogUtil
import com.parkloyalty.lpr.scan.util.QRCodeUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.util.*

@AndroidEntryPoint
class GenerateQRCodeActivity : BaseActivity(), CustomDialogHelper {

    @JvmField
    @BindView(R.id.input_officer_name)
    var inputOfficerName: TextInputLayout? = null

    @JvmField
    @BindView(R.id.et_officer_name)
    var mEditTextOfficerName: TextInputEditText? = null

    @JvmField
    @BindView(R.id.et_date_time)
    var mEditTextDateTime: TextInputEditText? = null

    @JvmField
    @BindView(R.id.AutoComTextViewVehKey)
    var mAutoComTextViewKey: AppCompatAutoCompleteTextView? = null

    @JvmField
    @BindView(R.id.et_item_value)
    var mEditTextItemValue: AppCompatEditText? = null

    @JvmField
    @BindView(R.id.iv_qr_code)
    var mQrCode: AppCompatImageView? = null

    @JvmField
    @BindView(R.id.btn_generate_code)
    var btnGenerateCodeButton: AppCompatButton? = null

    private val mItemsList = arrayOf("Printer","Radio","Vehicle Keys","Pepper Spray","Flashlight")

    private var mContext: Context? = null
    private var mDb: AppDatabase? = null
    private var mWelcomeFormData: WelcomeForm? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_generate_qr_code)
        setFullScreenUI()
        ButterKnife.bind(this)
        setToolbar()
        init()
        getIntentData()
    }


    private fun getIntentData() {
        val intent = intent
        if (intent != null) {
            if (intent.getStringExtra("from_scr") != null &&
                intent.getStringExtra("from_scr").equals("QRCodeScanner")) {
                try {
                    val value = intent.getStringExtra("KEY_DATA")
                    mAutoComTextViewKey!!.setText(value!!.split(":")[0])
                    mItemsList[0] = value!!.split(":")[0]
                    mEditTextItemValue!!.setText(value!!.split(":")[1])
                    btnGenerateCodeButton!!.visibility = View.GONE
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            else {
                setDropdownItems()
            }
        }else {
            setDropdownItems()
        }
    }

    private fun init() {
        mContext = this
        mDb = BaseApplication.instance?.getAppDatabase()
        mWelcomeFormData = mDb?.dbDAO?.getWelcomeForm()
        mEditTextDateTime?.setText(AppUtils.getCurrentDateTimeforBoot("UI"))
        mEditTextOfficerName?.setText(
            mWelcomeFormData?.officerFirstName + " " +
                    mWelcomeFormData?.officerLastName
        )

    }


    //set value to Meter Name dropdown
    private fun setDropdownItems() {

        Arrays.sort(mItemsList)
        val adapter = ArrayAdapter(
            this,
            R.layout.row_dropdown_menu_popup_item,
            mItemsList
        )
        try {
            mAutoComTextViewKey?.threshold = 1
            mAutoComTextViewKey?.setAdapter<ArrayAdapter<String?>>(adapter)
            //mSelectedShiftStat = mApplicationList.get(pos);
            mAutoComTextViewKey?.onItemClickListener =
                OnItemClickListener { parent, view, position, id ->
                    AppUtils.hideSoftKeyboard(
                        this@GenerateQRCodeActivity
                    )
                }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /*perform click actions*/
    @OnClick(R.id.btn_generate_code)
    fun onClick(view: View) {
        val id = view.id
        when (id) {
            R.id.btn_generate_code -> if (isFormValid()) {
                GenerateQRCodePrint(mAutoComTextViewKey!!.text.toString(),
                    mEditTextItemValue!!.text.toString(),mQrCode!!)
            }
        }
    }

    private fun isFormValid(): Boolean {
        if (TextUtils.isEmpty(mAutoComTextViewKey?.text.toString().trim())) {
            //mTvBadgeId.setError(getString(R.string.val_msg_please_enter_badge_id));
            mAutoComTextViewKey?.requestFocus()
            mAutoComTextViewKey?.isFocusable = true
            mAutoComTextViewKey?.error = getString(R.string.val_msg_please_enter_state)
            AppUtils.showKeyboard(this@GenerateQRCodeActivity)
            return false
        }
        if (TextUtils.isEmpty(mEditTextItemValue?.text.toString().trim())) {
            //mTvBadgeId.setError(getString(R.string.val_msg_please_enter_badge_id));
            mEditTextItemValue?.requestFocus()
            mEditTextItemValue?.isFocusable = true
            mEditTextItemValue?.error = getString(R.string.val_msg_please_enter_state)
            AppUtils.showKeyboard(this@GenerateQRCodeActivity)
            return false
        }

        return true
    }

    private fun GenerateQRCodePrint(mKey: String,mValue: String, imageView: AppCompatImageView) {
        try {
            CoroutineScope(Dispatchers.IO).launch {
                // below line is for getting
                // the windowmanager service.
                val manager = getSystemService(WINDOW_SERVICE) as WindowManager

                // initializing a variable for default display.
                val display = manager.defaultDisplay

                // creating a variable for point which
                // is to be displayed in QR Code.
                val point = Point()
                display.getSize(point)

                // getting width and
                // height of a point
                val width = point.x
                val height = point.y

                // generating dimension from width and height.
                var dimen = if (width < height) width else height

                dimen = dimen * 2 / 8

                // setting this dimensions inside our qr code
                // encoder to generate our qr code.


               // val qrgEncoder =  QRGEncoder(mKey+":"+mValue, null, QRGContents.Type.TEXT, dimen)
                try {
                    // getting our qrcode in the form of bitmap.
                   // val bitmapQrCode = qrgEncoder.encodeAsBitmap()
                    val bitmapQrCode = QRCodeUtils.getQRCode(mKey+":"+mValue, dimen)
                    SaveQrCodeImage(bitmapQrCode,mKey)
                    // the bitmap is set inside our image
                    // view using .setimagebitmap method.
//                if (isPrint) {
                    mainScope.async  {
                        imageView!!.setImageBitmap(bitmapQrCode)

//                        val b = Bitmap.createBitmap(imageView!!.width, imageView.height, Bitmap.Config.ARGB_8888)
//                        val canvas = Canvas(b)
//                        imageView.draw(canvas)
//                        SaveQrCodeImage(b,mKey)
                    }
//                } else {
//                    mImageViewBarcode.setImageBitmap(bitmap)
//                }
                } catch (e: WriterException) {
                    // this method is called for
                    // exception handling.
                    e.printStackTrace()
                }
            }
        } catch (e: Exception) {
            LogUtil.printToastMSG(this@GenerateQRCodeActivity, e.message)
        }
    }

    private fun SaveQrCodeImage(finalBitmap: Bitmap?, imageNmae: String?) {
        val myDir = File(
            Environment.getExternalStorageDirectory().absolutePath,
            Constants.FILE_NAME + Constants.QRCODE
        )

        myDir.mkdirs()
        val fname = "${imageNmae?.trim()}.jpg" //"print_bitmap.jpg";
        val savePrintImagePath = File(myDir, fname)
        if (savePrintImagePath!!.exists()) savePrintImagePath!!.delete()
        try {
            sharedPreference.write(
                SharedPrefKey.REPRINT_PRINT_BITMAP,
                savePrintImagePath!!.absoluteFile.toString())

            val out = FileOutputStream(savePrintImagePath)
            finalBitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, out) //less than 300 kb

            out.flush()
            out.close()


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
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
            R.id.layOwnerBill)
    }// this will contain "Fruit"// If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()

}