package com.parkloyalty.lpr.scan.vehiclestickerscan

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.RectF
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Size
import android.widget.TextView
import android.window.OnBackInvokedDispatcher
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.core.graphics.scale
import androidx.lifecycle.Observer
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.parkloyalty.lpr.scan.R
import com.parkloyalty.lpr.scan.basecontrol.BaseActivity
import com.parkloyalty.lpr.scan.database.Singleton
import com.parkloyalty.lpr.scan.extensions.nullSafety
import com.parkloyalty.lpr.scan.interfaces.Constants
import com.parkloyalty.lpr.scan.interfaces.SharedPrefKey
import com.parkloyalty.lpr.scan.network.ApiResponse
import com.parkloyalty.lpr.scan.network.DynamicAPIPath
import com.parkloyalty.lpr.scan.network.NetworkCheck.isInternetAvailable
import com.parkloyalty.lpr.scan.network.Status
import com.parkloyalty.lpr.scan.ui.check_setup.activity.LprDetailsActivity
import com.parkloyalty.lpr.scan.ui.check_setup.model.lpr_model.LprScanLoggerRequest
import com.parkloyalty.lpr.scan.ui.check_setup.model.lpr_model.LprScanLoggerResponse
import com.parkloyalty.lpr.scan.ui.check_setup.model.lpr_model.LprScanLoggerViewModel
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DropdownDatasetResponse
import com.parkloyalty.lpr.scan.util.AppUtils.showCustomAlertDialog
import com.parkloyalty.lpr.scan.util.AppUtils.splitDateLpr
import com.parkloyalty.lpr.scan.util.DATASET_SETTINGS_LIST
import com.parkloyalty.lpr.scan.util.FileUtil
import com.parkloyalty.lpr.scan.util.INTENT_KEY_VEHICLE_INFO
import com.parkloyalty.lpr.scan.util.INTENT_KEY_VEHICLE_STICKER_URL
import com.parkloyalty.lpr.scan.util.LogUtil
import com.parkloyalty.lpr.scan.util.LogUtil.printLog
import com.parkloyalty.lpr.scan.util.LogUtil.printToastMSG
import com.parkloyalty.lpr.scan.utils.ObjectMapperProvider
import com.parkloyalty.lpr.scan.vehiclestickerscan.model.VehicleInfoModel
import com.parkloyalty.lpr.scan.vehiclestickerscan.view.CustomVehicleStickerFramingOverlay
import java.io.IOException
import java.nio.ByteBuffer
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.getValue
import kotlin.math.max
import kotlin.math.min


class VehicleStickerScanActivity : BaseActivity() {

    private val mLoggerViewModel: LprScanLoggerViewModel? by viewModels()

    private lateinit var previewView: PreviewView
    private lateinit var tvResult: TextView
    private var imageCapture: ImageCapture? = null
    private var scanning = true

    private lateinit var overlay: CustomVehicleStickerFramingOverlay


    private val requestPerms = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { granted ->
        if (granted.values.all { it }) startCamera() else finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vehicle_sticker_scan)

        addObservers()

        // For Android 12 and lower → fallback
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Your custom back logic
                moveToLprDetailsScreen(null, null)
            }
        })

        // For Android 13+ predictive back → use OnBackInvokedCallback
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            onBackInvokedDispatcher.registerOnBackInvokedCallback(
                OnBackInvokedDispatcher.PRIORITY_DEFAULT
            ) {
                // Your predictive back logic
                //finish()

                moveToLprDetailsScreen(null, null)
            }
        }

        previewView = findViewById(R.id.previewView)
        tvResult = findViewById(R.id.tvResult)
        overlay = findViewById(R.id.overlay)
        overlay.guideLineOffset = 60 * resources.displayMetrics.density

        if (!hasPermissions()) {
            requestPerms.launch(neededPermissions())
        } else startCamera()
    }

    private fun hasPermissions() = neededPermissions().all {
        ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun neededPermissions(): Array<String> = buildList {
        add(Manifest.permission.CAMERA)
        if (Build.VERSION.SDK_INT <= 28) add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (Build.VERSION.SDK_INT >= 33) add(Manifest.permission.READ_MEDIA_IMAGES)
    }.toTypedArray()


    @SuppressLint("UnsafeOptInUsageError")
    private fun startCamera() {
        val providerFuture = ProcessCameraProvider.getInstance(this)
        providerFuture.addListener({
            val provider = providerFuture.get()

            val preview = Preview.Builder()
                .setTargetResolution(Size(1280, 720))
                .build().also { it.setSurfaceProvider(previewView.surfaceProvider) }

            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()

            val analysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .setTargetResolution(Size(1280, 720))
                .build()

            val scanner = BarcodeScanning.getClient(
                BarcodeScannerOptions.Builder()
                    .setBarcodeFormats(Barcode.FORMAT_PDF417)
                    .build()
            )

            analysis.setAnalyzer(ContextCompat.getMainExecutor(this)) { proxy ->
                //Crop
                val rel = overlay.frameRectRelative()
                val w = proxy.width
                val h = proxy.height
                val crop = Rect(
                    (rel.left * w).toInt(),
                    (rel.top * h).toInt(),
                    (rel.right * w).toInt(),
                    (rel.bottom * h).toInt()
                )
                proxy.setCropRect(crop)
                //Crop

                val mediaImage = proxy.image ?: run { proxy.close(); return@setAnalyzer }
                val input = InputImage.fromMediaImage(mediaImage, proxy.imageInfo.rotationDegrees)

                scanner.process(input)
                    .addOnSuccessListener { list ->
                        val code = list.firstOrNull()
                        if (code?.rawValue != null && scanning) {
                            scanning = false
//                            val parsed = NyStickerParser.parse(code.rawValue!!)
//                            tvResult.text = parsed.pretty().joinToString("\n") { "${it.first}: ${it.second}" }
                            val parsed = VehicleStickerBarcodeParser.parse(code.rawValue!!)

                            var data = ""
                            parsed?.let {
                                // Use the parsed vehicle info here
                                println("Vehicle Info: $it")

                                data += "Vin Number: " + it.vin + "\n"
                                data += "Reg Expiry Date: " + it.expiryDate + "\n"
                                data += "Reg Expiry Year: " + it.expiryYear + "\n"
                                data += "Reg Expiry Month: " + it.expiryMonth + "\n"
                                data += "Reg Expiry Day: " + it.expiryDay + "\n"
                                data += "Plate Type: " + it.plateType + "\n"
                                data += "Plate Number: " + it.plateNumber + "\n"
                                data += "Vehicle Year: " + it.year + "\n"
                                data += "Vehicle Make: " + it.make + "\n"
                                data += "Vehicle Model: " + it.model + "\n"
                                data += "Vehicle Body Style: " + it.bodyStyle + "\n"
                            }

                            tvResult.setText(data)
                            LogUtil.printLog("==>ScannedData:", data)

                            //takeStickerPhoto()
                            takeStickerPhotoCropped(rel, parsed)
                        }
                    }
                    .addOnCompleteListener { proxy.close() }
            }

            provider.unbindAll()
            provider.bindToLifecycle(
                this,
                CameraSelector.DEFAULT_BACK_CAMERA,
                preview,
                imageCapture,
                analysis
            )
        }, ContextCompat.getMainExecutor(this))
    }

    private fun takeStickerPhoto() {
        val name = "ny_sticker_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())}.jpg"
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= 29) put(
                MediaStore.Images.Media.RELATIVE_PATH,
                "Pictures/NYRegScans"
            )
        }
        val output = ImageCapture.OutputFileOptions.Builder(
            contentResolver, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues
        ).build()

        imageCapture?.takePicture(
            output, ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(out: ImageCapture.OutputFileResults) {
                    tvResult.append("\nPhoto saved!")
                }

                override fun onError(exc: ImageCaptureException) {
                    tvResult.append("\nPhoto error: ${exc.message}")
                }
            })
    }


    private fun takeStickerPhotoCropped(relROI: RectF, vehicleInfoModel: VehicleInfoModel) {
        val cap = imageCapture ?: return
        cap.takePicture(
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(img: ImageProxy) {
                    try {
                        val rotated = imageProxyToBitmap(img)
                        img.close()

                        val w = rotated.width
                        val h = rotated.height
                        val crop = Rect(
                            (relROI.left * w).toInt().coerceIn(0, w - 1),
                            (relROI.top * h).toInt().coerceIn(0, h - 1),
                            (relROI.right * w).toInt().coerceIn(1, w),
                            (relROI.bottom * h).toInt().coerceIn(1, h)
                        )
                        val safeCrop = Rect(
                            min(crop.left, crop.right - 1),
                            min(crop.top, crop.bottom - 1),
                            max(crop.left + 1, crop.right),
                            max(crop.top + 1, crop.bottom)
                        )
                        val cropped = Bitmap.createBitmap(
                            rotated,
                            safeCrop.left,
                            safeCrop.top,
                            safeCrop.width(),
                            safeCrop.height()
                        )
                        saveBitmapToPictures(bmp = cropped, vehicleInfoModel = vehicleInfoModel)
                        tvResult.append("\nCropped photo saved.")
                    } catch (t: Throwable) {
                        tvResult.append("\nPhoto error: ${t.message}")
                    }
                }

                override fun onError(exc: ImageCaptureException) {
                    tvResult.append("\nPhoto error: ${exc.message}")
                }
            })
    }

    // --- helpers ---

//    private fun yuvToArgb8888(image: ImageProxy): Bitmap {
//        val yPlane = image.planes[0].buffer.toByteArray()
//        val uPlane = image.planes[1].buffer.toByteArray()
//        val vPlane = image.planes[2].buffer.toByteArray()
//        val w = image.width
//        val h = image.height
//        val out = IntArray(w * h)
//
//        val yRowStride = image.planes[0].rowStride
//        val uRowStride = image.planes[1].rowStride
//        val vRowStride = image.planes[2].rowStride
//        val uPixelStride = image.planes[1].pixelStride
//        val vPixelStride = image.planes[2].pixelStride
//
//        var yp = 0
//        for (j in 0 until h) {
//            val pY = j * yRowStride
//            val pUV = (j shr 1) * uRowStride
//            for (i in 0 until w) {
//                val y = (yPlane[pY + i].toInt() and 0xFF)
//                val u = (uPlane[pUV + (i shr 1) * uPixelStride].toInt() and 0xFF) - 128
//                val v = (vPlane[pUV + (i shr 1) * vPixelStride].toInt() and 0xFF) - 128
//                // YUV -> RGB
//                val r = (y + 1.370705f * v).toInt()
//                val g = (y - 0.337633f * u - 0.698001f * v).toInt()
//                val b = (y + 1.732446f * u).toInt()
//                out[yp++] = (0xFF shl 24) or
//                        (r.coerceIn(0,255) shl 16) or
//                        (g.coerceIn(0,255) shl 8) or
//                        (b.coerceIn(0,255))
//            }
//        }
//        return Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888).apply {
//            setPixels(out, 0, w, 0, 0, w, h)
//        }
//    }

//    private fun imageProxyToBitmap(img: ImageProxy): Bitmap {
//        val rotated = when (img.format) {
//            ImageFormat.YUV_420_888 -> yuvToArgb8888(img)
//            else -> throw IllegalStateException("Unsupported format: ${img.format}")
//        }
//        val m = Matrix().apply { postRotate(img.imageInfo.rotationDegrees.toFloat()) }
//        return Bitmap.createBitmap(rotated, 0, 0, rotated.width, rotated.height, m, true)
//    }

    private fun ByteBuffer.toByteArray(): ByteArray {
        rewind()
        val bytes = ByteArray(remaining())
        get(bytes)
        return bytes
    }

    private fun saveBitmapToPictures(bmp: Bitmap, vehicleInfoModel: VehicleInfoModel) {
//        val name = "ny_sticker_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())}.jpg"
//        val values = ContentValues().apply {
//            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
//            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
//            if (Build.VERSION.SDK_INT >= 29) {
//                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/$subdir")
//            }
//        }
//        val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
//        uri?.let {
//            contentResolver.openOutputStream(it)?.use { os: OutputStream ->
//                bmp.compress(Bitmap.CompressFormat.JPEG, 95, os)
//            }
//        }

        val maxWidth = 800
        val scale = if (bmp.width > maxWidth) maxWidth.toFloat() / bmp.width else 1f
        val resized = bmp.scale((bmp.width * scale).toInt(), (bmp.height * scale).toInt())

        //val savedPath = FileUtil.saveBitmapToStorage(bmp,"ny_sticker_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())}.jpg" )
        val savedPath = FileUtil.saveBitmapToStorage(
            resized,
            "ny_sticker_${vehicleInfoModel.plateNumber.nullSafety()}"
        )

        moveToLprDetailsScreen(vehicleInfoModel, savedPath)

//        val out = Intent().apply {
//            putExtra(EXTRA_VEHICLE_INFO, vehicleInfo)
//            putExtra(EXTRA_STICKER_URL, savedPath)
//        }
//        setResult(Activity.RESULT_OK, out)
//        finish()
    }

    // --- helpers ---

    private fun imageProxyToBitmap(img: ImageProxy): Bitmap {
        // Handle both JPEG (most common for ImageCapture) and YUV
        val bmp = when (img.format) {
            ImageFormat.JPEG -> {
                val bytes = img.planes[0].buffer.useToByteArray()
                BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            }

            ImageFormat.YUV_420_888 -> yuvToArgb8888(img)
            else -> {
                // Fallback: try decoding as JPEG anyway
                val bytes = img.planes[0].buffer.useToByteArray()
                BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            }
        }

        // Rotate into upright orientation
        val matrix = Matrix().apply { postRotate(img.imageInfo.rotationDegrees.toFloat()) }
        return Bitmap.createBitmap(bmp, 0, 0, bmp.width, bmp.height, matrix, true)
    }

    private fun ByteBuffer.useToByteArray(): ByteArray {
        rewind()
        val arr = ByteArray(remaining())
        get(arr)
        return arr
    }

    private fun yuvToArgb8888(image: ImageProxy): Bitmap {
        val yPlane = image.planes[0].buffer.useToByteArray()
        val uPlane = image.planes[1].buffer.useToByteArray()
        val vPlane = image.planes[2].buffer.useToByteArray()
        val w = image.width
        val h = image.height
        val out = IntArray(w * h)

        val yRowStride = image.planes[0].rowStride
        val uRowStride = image.planes[1].rowStride
        val vRowStride = image.planes[2].rowStride
        val uPixelStride = image.planes[1].pixelStride
        val vPixelStride = image.planes[2].pixelStride

        var idx = 0
        for (j in 0 until h) {
            val pY = j * yRowStride
            val pUV = (j shr 1) * uRowStride
            for (i in 0 until w) {
                val y = (yPlane[pY + i].toInt() and 0xFF)
                val u = (uPlane[pUV + (i shr 1) * uPixelStride].toInt() and 0xFF) - 128
                val v = (vPlane[pUV + (i shr 1) * vPixelStride].toInt() and 0xFF) - 128
                val r = (y + 1.370705f * v).toInt().coerceIn(0, 255)
                val g = (y - 0.337633f * u - 0.698001f * v).toInt().coerceIn(0, 255)
                val b = (y + 1.732446f * u).toInt().coerceIn(0, 255)
                out[idx++] = (0xFF shl 24) or (r shl 16) or (g shl 8) or b
            }
        }
        return Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888).apply {
            setPixels(out, 0, w, 0, 0, w, h)
        }
    }

//    private fun saveBitmapToPictures(bmp: Bitmap, subdir: String): Uri? {
//        val name = "ny_sticker_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())}.jpg"
//        val values = ContentValues().apply {
//            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
//            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
//            put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
//            if (Build.VERSION.SDK_INT >= 29) {
//                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/$subdir")
//            }
//        }
//        val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
//        if (uri != null) {
//            contentResolver.openOutputStream(uri)?.use { os ->
//                if (!bmp.compress(Bitmap.CompressFormat.JPEG, 95, os)) {
//                    // If compress failed, clean up the row
//                    contentResolver.delete(uri, null, null)
//                    return null
//                }
//            }
//        }
//        return uri
//    }

    //APP ITEMS
    private fun moveToLprDetailsScreen(
        vehicleInfoModel: VehicleInfoModel?,
        stickerImageUrl: String?
    ) {
        val mIntent = Intent(this@VehicleStickerScanActivity, LprDetailsActivity::class.java)
        if (vehicleInfoModel != null) {
            mIntent.putExtra("lpr_number", vehicleInfoModel.plateNumber)
            mIntent.putExtra(INTENT_KEY_VEHICLE_INFO, vehicleInfoModel)
            mIntent.putExtra(INTENT_KEY_VEHICLE_STICKER_URL, stickerImageUrl)
        }

//        mIntent.putExtra("Lot", mLotItem)
//        mIntent.putExtra("Location", mLotItem)
//        mIntent.putExtra("Space_id", mSpaceId)
//        mIntent.putExtra("Meter", mMeterNameItem)
//        mIntent.putExtra("Zone", mPBCZone)
//        mIntent.putExtra("Street", mStreetItem)
//        mIntent.putExtra("Block", mBlock)
//        mIntent.putExtra("Direction", mDirectionItem)
//        mIntent.putExtra("from_scr", mFromScreen)
        startActivity(mIntent)
        finish()
        //user event logging
        try {
            callPushEventApi(vehicleInfoModel?.plateNumber.nullSafety())
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: ParseException) {
            e.printStackTrace()
        }
    }

    private val loggerResponseObserver = Observer { apiResponse: ApiResponse ->
        consumeResponse(apiResponse, DynamicAPIPath.POST_LPR_SCAN_LOGGER)
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
        val mWelcomeForm = getMyDatabase()?.dbDAO?.getWelcomeForm()
        val mLat = sharedPreference.read(SharedPrefKey.LAT, "0.0")!!.toDouble()
        val mLong = sharedPreference.read(SharedPrefKey.LONG, "0.0")!!.toDouble()
        val myLocation = Geocoder(applicationContext, Locale.getDefault())
        val myList = myLocation.getFromLocation(mLat, mLong, 1)
        try {
            val address = myList!![0]
            if (isInternetAvailable(this@VehicleStickerScanActivity)) {
                val mPushEventRequest = LprScanLoggerRequest()
                mPushEventRequest.activityType = "LPRScan"
                mPushEventRequest.lpNumber = LprNum
                mPushEventRequest.logType = Constants.LOG_TYPE_NODE_PORT
                mPushEventRequest.latitude = mLat
                mPushEventRequest.longitude = mLong
                var mZone: String? = "CST"
                if (Singleton.getDataSetList(DATASET_SETTINGS_LIST, getMyDatabase()) != null) {
                    mZone =
                        Singleton.getDataSetList(DATASET_SETTINGS_LIST, getMyDatabase())!![0].mValue
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
                                showCustomAlertDialog(
                                    this@VehicleStickerScanActivity, "POST_LPR_SCAN_LOGGER",
                                    message, "Ok", "Cancel", this
                                )
                            } else {
                                responseModel.response = "Not getting response from server..!!"
                                message = responseModel.response
                                showCustomAlertDialog(
                                    this@VehicleStickerScanActivity, "POST_LPR_SCAN_LOGGER",
                                    message, "Ok", "Cancel", this
                                )
                            }
                            //   AppUtils.showCustomAlertDialog(mContext,"POST_LPR_SCAN_LOGGER",
                            //   "Not getting response from server ..!!","Ok","Cancel", this);
                        } else {
                            showCustomAlertDialog(
                                this@VehicleStickerScanActivity, "POST_LPR_SCAN_LOGGER",
                                "Something wen't wrong..!!", "Ok", "Cancel",
                                this
                            )
                            dismissLoader()

                            // LogUtil.printToastMSG(LprDetails2Activity.this, responseModel.getMessage());
                        }
                    }
                }
            }

            Status.ERROR -> {
                dismissLoader()
                printToastMSG(
                    applicationContext,
                    getString(R.string.err_msg_connection_was_refused)
                )
            }

            else -> {}
        }
    }

}
