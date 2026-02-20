package com.parkloyalty.lpr.scan.crop

import android.Manifest
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.util.Linkify
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import butterknife.BindView
import butterknife.ButterKnife
//import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.card.MaterialCardView
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.parkloyalty.lpr.scan.R
import com.parkloyalty.lpr.scan.basecontrol.BaseActivity
import com.parkloyalty.lpr.scan.basecontrol.BaseApplication
import com.parkloyalty.lpr.scan.database.AppDatabase
import com.parkloyalty.lpr.scan.interfaces.Constants
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.CitationImagesModel
import com.parkloyalty.lpr.scan.util.ConstructLayoutBuilder.mainScope
import com.parkloyalty.lpr.scan.util.LogUtil
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class OcrActivity : BaseActivity(){
    private lateinit var recognizer: TextRecognizer
    private val TAG = "Testing"
    private val SAVED_TEXT_TAG = "SavedText"

    //private val SAVED_IMAGE_BITMAP = "SavedImage"
    private val REQUEST_CODE_PERMISSIONS = 10
    private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)

    lateinit var camera: Camera
    var savedBitmap: Bitmap? = null
    private var mDb: AppDatabase? = null
    private var isButtonClick = false
    private var isImageSaving = false

    private lateinit var cameraExecutor: ExecutorService

    @BindView(com.parkloyalty.lpr.scan.R.id.textInImageLayout)
    lateinit var textInImageLayout: MaterialCardView

    @BindView(com.parkloyalty.lpr.scan.R.id.textInImage)
    lateinit var textInImage: TextView

    @BindView(com.parkloyalty.lpr.scan.R.id.torchButton)
    lateinit var torchButton: MaterialCardView

    @BindView(com.parkloyalty.lpr.scan.R.id.torchImage)
    lateinit var torchImage: AppCompatImageView

    @BindView(com.parkloyalty.lpr.scan.R.id.extractTextButton)
    lateinit var extractTextButton: AppCompatImageView

    @BindView(com.parkloyalty.lpr.scan.R.id.share)
    lateinit var share: MaterialCardView

    @BindView(com.parkloyalty.lpr.scan.R.id.ocrbitmap)
    lateinit var ocrbitmap: AppCompatImageView

    @BindView(com.parkloyalty.lpr.scan.R.id.overlay)
    lateinit var overlayFrameLayout: FrameLayout

    @BindView(com.parkloyalty.lpr.scan.R.id.previewImage)
    lateinit var previewImage: ImageView

    @BindView(com.parkloyalty.lpr.scan.R.id.copyToClipboard)
    lateinit var copyToClipboard: ImageView

    @BindView(com.parkloyalty.lpr.scan.R.id.close)
    lateinit var close: ImageView

    @BindView(com.parkloyalty.lpr.scan.R.id.viewFinder)
    lateinit var viewFinder: PreviewView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_ocr_activity)
        ButterKnife.bind(this)
        mDb = BaseApplication.instance?.getAppDatabase()
        if (savedInstanceState != null) {
            val savedText = savedInstanceState.getString(SAVED_TEXT_TAG)
            textInImageLayout.apply {
                if (isTextValid(savedText)) {
                    textInImageLayout.visibility = View.VISIBLE
                    textInImage.text = savedInstanceState.getString(SAVED_TEXT_TAG)
                }
                if (savedBitmap != null) {
                    previewImage.visibility = View.VISIBLE
                    previewImage.setImageBitmap(savedBitmap)
                    isButtonClick = false
                }else{
                    isButtonClick = true
                }
                //previewImage.setImageBitmap(savedInstanceState.getParcelable(SAVED_IMAGE_BITMAP))
            }
        }
        init()
        setToolbar()
        val directoryCamera = File(
            Environment.getExternalStorageDirectory().absolutePath,
            Constants.FILE_NAME + Constants.VINCAMERA
        )

        val sdf = SimpleDateFormat("EEEE")
        val d = Date()
        val dayOfTheWeek: String = sdf.format(d)
        LogUtil.printLog("day of today", dayOfTheWeek)
        if (dayOfTheWeek.equals("Friday", ignoreCase = true)) {
            deleteRecursive(directoryCamera)
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
            R.id.layOwnerBill
        )
    }

    private fun init() {

        cameraExecutor = Executors.newSingleThreadExecutor()

        try {
            recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
            Log.d("OCR", "Recognizer initialized successfully")
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            requestPermissions()
        }

        extractTextButton.apply {
            extractTextButton.setOnClickListener {
                if(isButtonClick == false) {
                    isButtonClick = true
                    when {
                        previewImage.visibility == View.VISIBLE -> {
                            savedBitmap = previewImage.drawable.toBitmap()
                            runTextRecognition(savedBitmap!!)
                        }

                        viewFinder.bitmap != null -> {
                            previewImage.visibility = View.VISIBLE
                            savedBitmap = viewFinder.bitmap
                            previewImage.setImageBitmap(viewFinder.bitmap!!)
                            runTextRecognition(savedBitmap!!)

                            ocrbitmap.setImageBitmap(savedBitmap!!)

                        }

                        else -> {
                            showToast(("camera_error_default_msg"))
                        }
                    }
                }
            }

            copyToClipboard.setOnClickListener {
                val textToCopy = textInImage.text
                if (isTextValid(textToCopy.toString())) {
                    copyToClipboard(textToCopy)
                } else {
                    showToast(("no_text_found"))
                }
            }

            share.setOnClickListener {
                val textToCopy = textInImage.text.toString()
                if (isTextValid(textToCopy)) {
                    shareText(textToCopy)
                } else {
                    showToast(("no_text_found"))
                }
            }

            close.setOnClickListener {
                textInImageLayout.visibility = View.GONE
            }

        }

    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
        )
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                showToast(
                    ("permission_denied_msg")
                )
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        if (item.itemId == R.id.gallery) {
//            textInImageLayout.visibility = View.GONE
//            ImagePicker.with(this)
//                .galleryOnly()
//                .crop()
//                .compress(1024)
//                .maxResultSize(1080, 1080)
//                .start()
//
//            return true
//        } else if (item.itemId == R.id.camera) {
//            if(!allPermissionsGranted()){
//                requestPermissions()
//            } else {
//                previewImage.visibility = View.GONE
//                savedBitmap = null
//            }
//            return true
//        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            Activity.RESULT_OK -> {
                //Image Uri will not be null for RESULT_OK
                val uri: Uri = data?.data!!

                // Use Uri object instead of File to avoid storage permissions
                previewImage.apply {
                    visibility = View.VISIBLE
                    setImageURI(uri)
                }
                //runTextRecognition(binding.previewImage.drawable.toBitmap())
            }
//            ImagePicker.RESULT_ERROR -> {
//                showToast(data.toString())
//            }
            else -> {
                showToast("No Image Selected")
            }
        }
    }

    private fun runTextRecognition(bitmap: Bitmap) {
        val inputImage = InputImage.fromBitmap(bitmap, 0)
        mainScope.launch {
            if(isImageSaving==false) {
                isImageSaving = true
                SaveImageMM(savedBitmap)
            }
        }
        recognizer
            .process(inputImage)
            .addOnSuccessListener { text ->
                textInImageLayout.visibility = View.VISIBLE
                processTextRecognitionResult(text)
            }.addOnFailureListener { e ->
                e.printStackTrace()
                showToast(e.localizedMessage ?: ("error_default_msg"))
            }
    }

    private fun processTextRecognitionResult(result: Text) {
        var finalText = ""
        for (block in result.textBlocks) {
            for (line in block.lines) {
//                finalText += line.text + " \n"
                finalText += line.text
                break
            }
//            finalText += "\n"
            finalText
        }

        Log.d(TAG, finalText)
        Log.d(TAG, result.text)

        textInImage.text = if (finalText.isNotEmpty()) {
            finalText
        } else {
            ("no_text_found")
        }

        Linkify.addLinks(textInImage, Linkify.ALL)

        val finishIntent = Intent()
        finishIntent.putExtra("OCR_TEXT", textInImage.text)
        setResult(RESULT_OK, finishIntent)
        finish()
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                .build()
                .also {
                    it.setSurfaceProvider(viewFinder.surfaceProvider)
                }
            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                camera = cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview
                )
                title.apply {
                    if(!allPermissionsGranted()){
                        requestPermissions()
                    } else {
                        previewImage.visibility = View.GONE
                        savedBitmap = null
                    }
                }

                torchImage.apply {

                    camera.apply {
                        torchImage.setBackgroundColor(resources.getColor(R.color.ripple_material_light))
                        if (cameraInfo.hasFlashUnit()) {
                            torchButton.setOnClickListener {
                                cameraControl.enableTorch(cameraInfo.torchState.value == TorchState.OFF)
                            }
                        } else {
                            torchButton.setOnClickListener {
                                showToast(("torch_not_available_msg"))
                            }
                        }

                        cameraInfo.torchState.observe(this@OcrActivity) { torchState ->
                            if (torchState == TorchState.OFF) {
                                torchImage.setImageResource(R.drawable.baseline_flashlight_on_24)
                            } else {
                                torchImage.setImageResource(R.drawable.baseline_flashlight_off_24)
                            }
                        }

                    }

                }
            } catch (exc: Exception) {
                showToast(getString(R.string.error_default_msg))
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))

    }

    private fun showToast(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }

    private fun isTextValid(text: String?): Boolean {
        if (text == null)
            return false

        return text.isNotEmpty() and !text.equals(getString(R.string.no_text_found))
    }

    private fun shareText(text: String) {
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_TEXT, text)
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_text_title)))
    }

    private fun copyToClipboard(text: CharSequence) {
        val clipboard =
            ContextCompat.getSystemService(applicationContext, ClipboardManager::class.java)
        val clip = ClipData.newPlainText("label", text)
        clipboard?.setPrimaryClip(clip)
        showToast(getString(R.string.clipboard_text))
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.clear()
        val textInImage = (textInImage.text).toString()
        if (isTextValid(textInImage)) {
            outState.putString(SAVED_TEXT_TAG, textInImage)
        }
        /*if (binding.previewImage.visibility == View.VISIBLE) {
            outState.putParcelable(SAVED_IMAGE_BITMAP, binding.previewImage.drawable.toBitmap())
        }*/
    }

    //TODO will add comments later
    private fun SaveImageMM(finalBitmap: Bitmap?) {

        val bytes = cropImage(finalBitmap!!, viewFinder, overlayFrameLayout)

        val croppedImage = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        val myDir = File(
            Environment.getExternalStorageDirectory().absolutePath,
            Constants.FILE_NAME + Constants.VINCAMERA
        )
        myDir.mkdirs()
        val timeStamp = SimpleDateFormat("yyyMMdd_HHmmss", Locale.US).format(Date())
        val fname = "Vin_" + timeStamp + "_capture.jpg"
        val file = File(myDir, fname)
        if (file.exists()) file.delete()
        try {
            val out = FileOutputStream(file)
            croppedImage!!.compress(Bitmap.CompressFormat.JPEG, 100, out) //20less than 300 kb
            out.flush()
            out.close()
            val oldFname = "IMG_temp.jpg"
            val oldFile = File(myDir, oldFname)
            if (oldFile.exists()) oldFile.delete()
            val id = SimpleDateFormat("ddHHmmss", Locale.US).format(Date())
            //save image to db
            val pathDb = file.path
            val mImage = CitationImagesModel()
            mImage.status = 0
            mImage.citationImage = pathDb
            mImage.id = id.toInt()
            //            mImage.setmCitationNumber(0);
            mDb?.dbDAO?.insertCitationImage(mImage)
            //set image list adapter
//            ioScope.launch {
//                finalBitmap.recycle()
//            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun cropImage(bitmap: Bitmap, containerImage: View, containerOverlay: View): ByteArray {
        val heightOriginal = containerImage.height
        val widthOriginal = containerImage.width
        val heightFrame = containerOverlay.height
        val widthFrame = containerOverlay.width
        val leftFrame = containerOverlay.left
        val topFrame = containerOverlay.top
        val heightReal = bitmap.height
        val widthReal = bitmap.width
        val widthFinal = widthFrame * widthReal / widthOriginal
        val heightFinal = heightFrame * heightReal / heightOriginal
        val leftFinal = leftFrame * widthReal / widthOriginal
        val topFinal = topFrame * heightReal / heightOriginal
        val bitmapFinal = Bitmap.createBitmap(
            bitmap,
            leftFinal, topFinal, widthFinal, heightFinal
        )
        val stream = ByteArrayOutputStream()
        bitmapFinal.compress(
            Bitmap.CompressFormat.JPEG,
            100,
            stream
        ) //100 is the best quality possibe
        return stream.toByteArray()
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