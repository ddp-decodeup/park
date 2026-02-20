//package com.parkloyalty.lpr.scan.doubangoultimatelpr.extras
//
//import android.Manifest
//import android.app.Activity
//import android.app.AlertDialog
//import android.app.Dialog
//import android.content.Context
//import android.content.Intent
//import android.content.pm.PackageManager
//import android.content.res.Configuration
//import android.graphics.Bitmap
//import android.graphics.ImageFormat
//import android.graphics.Matrix
//import android.graphics.SurfaceTexture
//import android.hardware.camera2.*
//import android.media.AudioManager
//import android.media.Image
//import android.media.ImageReader
//import android.media.ToneGenerator
//import android.os.AsyncTask
//import android.os.Bundle
//import android.os.Environment
//import android.renderscript.*
//import android.text.TextUtils
//import android.util.Size
//import android.util.SparseIntArray
//import android.view.LayoutInflater
//import android.view.Surface
//import android.view.View
//import android.view.ViewGroup
//import android.widget.Button
//import android.widget.LinearLayout
//import android.widget.Toast
//import androidx.core.app.ActivityCompat
//import androidx.core.content.ContextCompat
//import androidx.fragment.app.DialogFragment
//import androidx.fragment.app.Fragment

//import com.parkloyalty.lpr.vehiclemode.doubangoultimatelpr.interfaces.VehicleDetailListener
//import com.parkloyalty.lpr.vehiclemode.doubangoultimatelpr.model.ScanDataModel
//import com.parkloyalty.lpr.vehiclemode.R
//import com.parkloyalty.lpr.vehiclemode.ui.vehiclemode.ScanResultListActivity
//import com.parkloyalty.lpr.vehiclemode.extensions.nullSafety
//import com.parkloyalty.lpr.vehiclemode.doubangoultimatelpr.database.MyDatabase
//import com.parkloyalty.lpr.vehiclemode.doubangoultimatelpr.database.MyDatabase.Companion.getInstance
//import com.parkloyalty.lpr.vehiclemode.doubangoultimatelpr.model.PlateModel
//import com.parkloyalty.lpr.vehiclemode.doubangoultimatelpr.utils.AlprBackgroundTask
//import com.parkloyalty.lpr.vehiclemode.doubangoultimatelpr.utils.DoubangoConstants.FROM_CONTINUOUS_VEHICLE_MODE
//import com.parkloyalty.lpr.vehiclemode.doubangoultimatelpr.utils.DoubangoConstants.FROM_POINT_AND_SCAN
//import com.parkloyalty.lpr.vehiclemode.doubangoultimatelpr.utils.DoubangoConstants.KEY_BEAT
//import com.parkloyalty.lpr.vehiclemode.doubangoultimatelpr.utils.DoubangoConstants.KEY_BLOCK
//import com.parkloyalty.lpr.vehiclemode.doubangoultimatelpr.utils.DoubangoConstants.KEY_FROM
//import com.parkloyalty.lpr.vehiclemode.doubangoultimatelpr.utils.DoubangoConstants.KEY_IS_LENS_STAB
//import com.parkloyalty.lpr.vehiclemode.doubangoultimatelpr.utils.DoubangoConstants.KEY_IS_VIDEO_STAB
//import com.parkloyalty.lpr.vehiclemode.doubangoultimatelpr.utils.DoubangoConstants.KEY_OFFICER_NAME
//import com.parkloyalty.lpr.vehiclemode.doubangoultimatelpr.utils.DoubangoConstants.KEY_SCAN_RESULT
//import com.parkloyalty.lpr.vehiclemode.doubangoultimatelpr.utils.DoubangoConstants.KEY_SQUAD
//import com.parkloyalty.lpr.vehiclemode.doubangoultimatelpr.utils.DoubangoConstants.KEY_STREET
//import com.parkloyalty.lpr.vehiclemode.doubangoultimatelpr.utils.DoubangoConstants.KEY_STREET_SIDE
//import com.parkloyalty.lpr.vehiclemode.doubangoultimatelpr.utils.DoubangoConstants.KEY_TIMING_LIMIT
//import com.parkloyalty.lpr.vehiclemode.doubangoultimatelpr.views.AlprGLSurfaceView
//import com.parkloyalty.lpr.vehiclemode.doubangoultimatelpr.views.AlprPlateView
//import com.parkloyalty.lpr.vehiclemode.extensions.showDLog
//import com.parkloyalty.lpr.vehiclemode.extensions.showELog
//import com.parkloyalty.lpr.vehiclemode.extensions.showILog
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.delay
//import kotlinx.coroutines.launch
//import java.io.File
//import java.io.FileOutputStream
//import java.io.IOException
//import java.io.OutputStream
//import java.text.SimpleDateFormat
//import java.util.*
//import java.util.concurrent.Semaphore
//import java.util.concurrent.TimeUnit
//
//class AlprCameraFragmentCopy : Fragment, ActivityCompat.OnRequestPermissionsResultCallback,
//    VehicleDetailListener {
//    private var fromAction: String? = FROM_POINT_AND_SCAN
//    private var isContinuousScanStarted = false
//    private val scanDataModelList = ArrayList<ScanDataModel?>()
//    private var recentScanDataModel = ArrayList<ScanDataModel?>()
//    private var permitList = ArrayList<PlateModel?>()
//    private var scofflawList = ArrayList<PlateModel?>()
//    private var lensOpticalStab = false
//    private var videoStab = false
//    private var officerName: String? = ""
//    private var beat: String? = ""
//    private var squad: String? = ""
//    private var block: String? = ""
//    private var street: String? = ""
//    private var streetSide: String? = ""
//    private var timingLimit: String? = ""
//    private val calendar = Calendar.getInstance()
//    private val dateFormat = SimpleDateFormat("dd-MM-yyyy")
//    private val timeFormat = SimpleDateFormat("HH:mm:ss")
//    private val currentDate = dateFormat.format(calendar.time)
//    private var toneGen1: ToneGenerator? = null
//    private var myDatabase: MyDatabase? = null
//
//    companion object {
//        const val REQUEST_CAMERA_PERMISSION = 1
//        const val FRAGMENT_DIALOG = "dialog"
//        val TAG = AlprCameraFragmentCopy::class.java.canonicalName
//        const val VIDEO_FORMAT =
//            ImageFormat.YUV_420_888 // All Android devices are required to support this format
//        private val ORIENTATIONS = SparseIntArray()
//
//        /**
//         * Using #2: processing and pending.
//         */
//        const val MAX_IMAGES = 2
//
//        /**
//         * The camera preview size will be chosen to be the smallest frame by pixel size capable of
//         * containing a DESIRED_SIZE x DESIRED_SIZE square.
//         */
//        //static final int MINIMUM_PREVIEW_SIZE = 320;
//        const val MINIMUM_PREVIEW_SIZE = 320
//
//        /**
//         * Public function to be called to create the fragment.
//         *
//         * @param preferredSize
//         * @return
//         */
//        fun newInstance(preferredSize: Size, sink: AlprCameraFragmentSink): AlprCameraFragmentCopy {
//            return AlprCameraFragmentCopy(preferredSize, sink)
//        }
//
//        /**
//         * Given `choices` of `Size`s supported by a camera, chooses the smallest one whose
//         * width and height are at least as large as the minimum of both, or an exact match if possible.
//         *
//         * @param choices The list of sizes that the camera supports for the intended output class
//         * @param width   The minimum desired width
//         * @param height  The minimum desired height
//         * @return The optimal `Size`, or an arbitrary one if none were big enough
//         */
//        private fun chooseOptimalSize(choices: Array<Size>, width: Int, height: Int): Size? {
//            val minSize = Math.max(Math.min(width, height), MINIMUM_PREVIEW_SIZE)
//            val desiredSize = Size(width, height)
//
//            // Collect the supported resolutions that are at least as big as the preview Surface
//            var exactSizeFound = false
//            val bigEnough: MutableList<Size?> = ArrayList()
//            val tooSmall: MutableList<Size?> = ArrayList()
//            for (option in choices) {
//                if (option == desiredSize) {
//                    // Set the size but don't return yet so that remaining sizes will still be logged.
//                    exactSizeFound = true
//                }
//                if (option.height >= minSize && option.width >= minSize) {
//                    bigEnough.add(option)
//                } else {
//                    tooSmall.add(option)
//                }
//            }
//            showILog(TAG, "Desired size: " + desiredSize + ", min size: " + minSize + "x" + minSize)
//            showILog(TAG, "Valid preview sizes: [" + TextUtils.join(", ", bigEnough) + "]")
//            showILog(TAG, "Rejected preview sizes: [" + TextUtils.join(", ", tooSmall) + "]")
//            if (exactSizeFound) {
//                showILog(TAG, "Exact size match found.")
//                return desiredSize
//            }
//
//            // Pick the smallest of those, assuming we found any
//            return if (bigEnough.size > 0) {
//                val chosenSize =
//                    Collections.min(bigEnough, CompareSizesByArea() as Comparator<in Size?>)
//                showILog(TAG, "Chosen size: " + chosenSize!!.width + "x" + chosenSize.height)
//                chosenSize
//            } else {
//                showELog(TAG, "Couldn't find any suitable preview size")
//                choices[0]
//            }
//        }
//
//        init {
//            ORIENTATIONS.append(Surface.ROTATION_0, 90)
//            ORIENTATIONS.append(Surface.ROTATION_90, 0)
//            ORIENTATIONS.append(Surface.ROTATION_180, 270)
//            ORIENTATIONS.append(Surface.ROTATION_270, 180)
//        }
//    }
//
//    private var mPreferredSize: Size? = null
//
//    /**
//     * ID of the current [CameraDevice].
//     */
//    private var mCameraId: String? = null
//    private var mJpegOrientation = 1
//
//    /**
//     * An [AlprGLSurfaceView] for camera preview.
//     */
//    private var mGLSurfaceView: AlprGLSurfaceView? = null
//    private var mPlateView: AlprPlateView? = null
//
//    private var vehicleBitmapImage: Bitmap? = null
//    private var licensePlateNumber: String? = ""
//    private var vehicleMakeBrand: String? = ""
//    private var vehicleModel: String? = ""
//    private var vehicleColor: String? = ""
//    private var licensePlateState: String? = ""
//
//    /**
//     * A [CameraCaptureSession] for camera preview.
//     */
//    private var mCaptureSession: CameraCaptureSession? = null
//
//    /**
//     * A reference to the opened [CameraDevice].
//     */
//    private var mCameraDevice: CameraDevice? = null
//
//    /**
//     * The [Size] of camera preview.
//     */
//    private var mPreviewSize: Size? = null
//    private var mSink: AlprCameraFragmentSink? = null
//    private val mBackgroundTaskCamera = AlprBackgroundTask()
//    private val mBackgroundTaskDrawing = AlprBackgroundTask()
//    private val mBackgroundTaskInference = AlprBackgroundTask()
//
//    /**
//     * [CameraDevice.StateCallback] is called when [CameraDevice] changes its state.
//     */
//    private val mStateCallback: CameraDevice.StateCallback = object : CameraDevice.StateCallback() {
//        override fun onOpened(cameraDevice: CameraDevice) {
//            // This method is called when the camera is opened.  We start camera preview here.
//            mCameraOpenCloseLock.release()
//            mCameraDevice = cameraDevice
//            createCameraCaptureSession()
//        }
//
//        override fun onDisconnected(cameraDevice: CameraDevice) {
//            mCameraOpenCloseLock.release()
//            cameraDevice.close()
//            mCameraDevice = null
//        }
//
//        override fun onError(cameraDevice: CameraDevice, error: Int) {
//            mCameraOpenCloseLock.release()
//            cameraDevice.close()
//            mCameraDevice = null
//            val activity: Activity? = activity
//            activity?.finish()
//        }
//    }
//    private var mClosingCamera = false
//
//    /**
//     * An [ImageReader] that handles still image capture.
//     */
//    private var mImageReaderInference: ImageReader? = null
//    private var mImageReaderDrawing: ImageReader? = null
//    private var imageReaderAvailable: ImageReader? = null
//    private var imageAvailable: Image? = null
//
//    /**
//     * This a callback object for the [ImageReader]. "onImageAvailable" will be called when a
//     * still image is ready to be saved.
//     */
//    private val mOnImageAvailableListener = ImageReader.OnImageAvailableListener { reader ->
//        if (mClosingCamera) {
//            showDLog(TAG, "Closing camera")
//            return@OnImageAvailableListener
//        }
//        try {
//            val image = reader.acquireLatestImage() ?: return@OnImageAvailableListener
//            imageReaderAvailable = reader
//            imageAvailable = image
//            vehicleBitmapImage = yuv420ToBitmap(image, requireContext())
//            val isForDrawing = reader.surface === mImageReaderDrawing!!.surface
//            showILog("==>isDrawing:", isForDrawing.toString() + "")
//            if (isForDrawing) {
//                /*mBackgroundTaskDrawing.post(() ->*/
//                mGLSurfaceView!!.setImage(image, mJpegOrientation) /*)*/
//            } else {
//                /*mBackgroundTaskInference.post(() ->*/
//                mSink!!.setImage(image, mJpegOrientation) /*)*/
//            }
//            if (fromAction == FROM_POINT_AND_SCAN) {
//                callResultActivity()
//            } else {
//                if (isContinuousScanStarted) setScannedDataInList(
//                    licensePlateNumber,
//                    vehicleMakeBrand,
//                    vehicleModel,
//                    vehicleColor,
//                    vehicleBitmapImage
//                )
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//            showELog(TAG, e.toString())
//        }
//    }
//
//    private fun yuv420ToBitmap(
//        image: Image,
//        context: Context
//    ): Bitmap {
//        val rs =
//            RenderScript.create(requireContext())
//        val script =
//            ScriptIntrinsicYuvToRGB.create(
//                rs,
//                Element.U8_4(rs)
//            )
//
//        // Refer the logic in a section below on how to convert a YUV_420_888 image
//        // to single channel flat 1D array. For sake of this example I'll abstract it
//        // as a method.
//        val yuvByteArray = image2byteArray(image)
//        val yuvType =
//            Type.Builder(rs, Element.U8(rs))
//                .setX(yuvByteArray.size)
//        val `in` = Allocation.createTyped(
//            rs,
//            yuvType.create(),
//            Allocation.USAGE_SCRIPT
//        )
//        val rgbaType =
//            Type.Builder(rs, Element.RGBA_8888(rs))
//                .setX(image.width)
//                .setY(image.height)
//        val out = Allocation.createTyped(
//            rs,
//            rgbaType.create(),
//            Allocation.USAGE_SCRIPT
//        )
//
//        // The allocations above "should" be cached if you are going to perform
//        // repeated conversion of YUV_420_888 to Bitmap.
//        `in`.copyFrom(yuvByteArray)
//        script.setInput(`in`)
//        script.forEach(out)
//        //Bitmap bitmap = Bitmap.createBitmap(image.getWidth(), image.getHeight(), Bitmap.Config.ARGB_8888);
//        val bitmap = Bitmap.createBitmap(
//            image.width,
//            image.height,
//            Bitmap.Config.ARGB_8888
//        )
//        out.copyTo(bitmap)
//
//        //Bitmap resizedBitmap = getResizedBitmap(bitmap,600);
//        return rotateBitmap(bitmap, 90f)
//    }
//
//    inner class AsyncTaskBitmapGeneration : AsyncTask<Bitmap?, String?, String>() {
//
//        override fun doInBackground(vararg bitmaps: Bitmap?): String {
//            return try {
//                val dateFormatter: SimpleDateFormat
//                dateFormatter = SimpleDateFormat("MMddyyyyhhmmss", Locale.US)
//                val folder = File(
//                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
//                        .toString() + "/UltimateALPR/"
//                )
//                if (!folder.exists()) folder.mkdirs()
//                val file = File(folder, dateFormatter.format(Calendar.getInstance().time) + ".png")
//                file.createNewFile()
//                var outStream: OutputStream? = null
//                outStream = FileOutputStream(file)
//                bitmaps[0]?.compress(Bitmap.CompressFormat.PNG, 100, outStream)
//                outStream.close()
//                file.absolutePath
//            } catch (e: IOException) {
//                ""
//            }
//        }
//
//        override fun onPostExecute(bitmapFileURL: String) {
//            super.onPostExecute(bitmapFileURL)
//        }
//    }
//
//    fun createTempFile(mBitmap: Bitmap?): String {
//        return try {
//            val dateFormatter: SimpleDateFormat
//            dateFormatter = SimpleDateFormat("MMddyyyyhhmmss", Locale.US)
//            val folder = File(
//                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
//                    .toString() + "/UltimateALPR/"
//            )
//            if (!folder.exists()) folder.mkdirs()
//            val file = File(folder, dateFormatter.format(Calendar.getInstance().time) + ".png")
//            file.createNewFile()
//            var outStream: OutputStream? = null
//            outStream = FileOutputStream(file)
//            mBitmap!!.compress(Bitmap.CompressFormat.PNG, 60, outStream)
//            outStream.close()
//            file.absolutePath
//        } catch (e: IOException) {
//            ""
//        }
//    }
//
//    fun rotateBitmap(
//        original: Bitmap,
//        degrees: Float
//    ): Bitmap {
//        val x = original.width
//        val y = original.height
//        val matrix = Matrix()
//        matrix.preRotate(degrees)
//        return Bitmap.createBitmap(original, 0, 0, original.width, original.height, matrix, true)
//    }
//
//    fun getResizedBitmap(image: Bitmap, maxSize: Int): Bitmap {
//        var width = image.width
//        var height = image.height
//        val bitmapRatio = width.toFloat() / height.toFloat()
//        if (bitmapRatio > 1) {
//            width = maxSize
//            height = (width / bitmapRatio).toInt()
//        } else {
//            height = maxSize
//            width = (height * bitmapRatio).toInt()
//        }
//        return Bitmap.createScaledBitmap(image, width, height, true)
//    }
//
//    private fun image2byteArray(image: Image): ByteArray {
//        require(image.format == ImageFormat.YUV_420_888) { "Invalid image format" }
//        val width = image.width
//        val height = image.height
//        val yPlane = image.planes[0]
//        val uPlane = image.planes[1]
//        val vPlane = image.planes[2]
//        val yBuffer = yPlane.buffer
//        val uBuffer = uPlane.buffer
//        val vBuffer = vPlane.buffer
//
//        // Full size Y channel and quarter size U+V channels.
//        val numPixels = (width * height * 1.5f).toInt()
//        val nv21 = ByteArray(numPixels)
//        var index = 0
//
//        // Copy Y channel.
//        val yRowStride = yPlane.rowStride
//        val yPixelStride = yPlane.pixelStride
//        for (y in 0 until height) {
//            for (x in 0 until width) {
//                nv21[index++] = yBuffer[y * yRowStride + x * yPixelStride]
//            }
//        }
//
//        // Copy VU data; NV21 format is expected to have YYYYVU packaging.
//        // The U/V planes are guaranteed to have the same row stride and pixel stride.
//        val uvRowStride = uPlane.rowStride
//        val uvPixelStride = uPlane.pixelStride
//        val uvWidth = width / 2
//        val uvHeight = height / 2
//        for (y in 0 until uvHeight) {
//            for (x in 0 until uvWidth) {
//                val bufferIndex = y * uvRowStride + x * uvPixelStride
//                // V channel.
//                nv21[index++] = vBuffer[bufferIndex]
//                // U channel.
//                nv21[index++] = uBuffer[bufferIndex]
//            }
//        }
//        return nv21
//    }
//
//    //    public Bitmap bitmapResize(Bitmap imageBitmap) {
//    //
//    //        Bitmap bitmap = imageBitmap;
//    //        float heightbmp = bitmap.getHeight();
//    //        float widthbmp = bitmap.getWidth();
//    //
//    //        // Get Screen width
//    //        DisplayMetrics displaymetrics = new DisplayMetrics();
//    //        this.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
//    //        float height = displaymetrics.heightPixels / 3;
//    //        float width = displaymetrics.widthPixels / 3;
//    //
//    //        int convertHeight = (int) hight, convertWidth = (int) width;
//    //
//    //        // higher
//    //        if (heightbmp > height) {
//    //            convertHeight = (int) height - 20;
//    //            bitmap = Bitmap.createScaledBitmap(bitmap, convertWidth,
//    //                    convertHighet, true);
//    //        }
//    //
//    //        // wider
//    //        if (widthbmp > width) {
//    //            convertWidth = (int) width - 20;
//    //            bitmap = Bitmap.createScaledBitmap(bitmap, convertWidth,
//    //                    convertHeight, true);
//    //        }
//    //
//    //        return bitmap;
//    //    }
//    private var mCaptureRequestBuilder: CaptureRequest.Builder? = null
//
//    /**
//     * [CaptureRequest] generated by [.mCaptureRequestBuilder]
//     */
//    private var mCaptureRequest: CaptureRequest? = null
//
//    /**
//     * A [Semaphore] to prevent the app from exiting before closing the camera.
//     */
//    private val mCameraOpenCloseLock = Semaphore(1)
//
//    /**
//     * Orientation of the camera sensor
//     */
//    private var mSensorOrientation = 0
//
//    /**
//     * Default constructor automatically called when the fragment is recreated. Required.
//     * https://stackoverflow.com/questions/51831053/could-not-find-fragment-constructor
//     */
//    constructor() {
//        // nothing special here
//    }
//
//    private constructor(preferredSize: Size, sink: AlprCameraFragmentSink) {
//        mPreferredSize = preferredSize
//        mSink = sink
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        return inflater.inflate(R.layout.fragment_camera, container, false)
//    }
//
//    var llBottom: LinearLayout? = null
//    var btnStart: Button? = null
//    var btnStop: Button? = null
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        myDatabase = getInstance(requireContext())
//        toneGen1 = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
//        fromAction = this.requireArguments().getString(KEY_FROM)
//        videoStab = this.requireArguments().getBoolean(KEY_IS_VIDEO_STAB)
//        lensOpticalStab = this.requireArguments().getBoolean(KEY_IS_LENS_STAB)
//        llBottom = view.findViewById(R.id.llBottom)
//        btnStart = view.findViewById(R.id.btnStart)
//        btnStop = view.findViewById(R.id.btnStop)
//        if (fromAction == FROM_CONTINUOUS_VEHICLE_MODE) {
//            officerName = this.requireArguments().getString(KEY_OFFICER_NAME)
//            beat = this.requireArguments().getString(KEY_BEAT)
//            squad = this.requireArguments().getString(KEY_SQUAD)
//            block = this.requireArguments().getString(KEY_BLOCK)
//            street = this.requireArguments().getString(KEY_STREET)
//            streetSide = this.requireArguments().getString(KEY_STREET_SIDE)
//            timingLimit = this.requireArguments().getString(KEY_TIMING_LIMIT)
//            llBottom?.visibility = View.VISIBLE
//            btnStop?.alpha = 0.5f
//            btnStop?.isEnabled = false
//        } else {
//            llBottom?.visibility = View.GONE
//        }
//        mGLSurfaceView = view.findViewById(R.id.glSurfaceView)
//        mPlateView = view.findViewById(R.id.plateView)
//
//        CoroutineScope(Dispatchers.IO).launch {
//            recentScanDataModel =
//                myDatabase?.dataBaseDAO()?.getScannedData(currentDate) as ArrayList<ScanDataModel?>
//            permitList = getPermitList(requireContext())
//            scofflawList = getScofflawList(requireContext())
//        }
//
//        btnStart?.setOnClickListener(View.OnClickListener { //Violation beep
//            //toneGen1.startTone(ToneGenerator.TONE_CDMA_CALL_SIGNAL_ISDN_INTERGROUP,800);
//
//            //toneGen1.startTone(ToneGenerator.TONE_CDMA_ONE_MIN_BEEP,800);
//            //toneGen1.release();
//            isContinuousScanStarted = true
//            Toast.makeText(requireContext(), "Scan Started", Toast.LENGTH_SHORT).show()
//            btnStart?.alpha = 0.5f
//            btnStart?.isEnabled = false
//            btnStop?.alpha = 1f
//            btnStop?.isEnabled = true
//        })
//        btnStop?.setOnClickListener(View.OnClickListener {
//            if (scanDataModelList.isEmpty()) {
//                //Scan Tone
//                //toneGen1.startTone(ToneGenerator.TONE_PROP_ACK,800);
//
//                //toneGen1.release();
//                Toast.makeText(
//                    requireContext(),
//                    "No scan result found yet, please scan some more plates",
//                    Toast.LENGTH_SHORT
//                ).show()
//            } else {
//                isContinuousScanStarted = false
//                AsyncTask.execute {
//                    myDatabase?.dataBaseDAO()?.insertAll(scanDataModelList)
//                    val i = Intent(requireContext(), ScanResultListActivity::class.java)
//                    i.putParcelableArrayListExtra(KEY_SCAN_RESULT, scanDataModelList)
//                    startActivity(i)
//                    requireActivity().finishAffinity()
//                }
//                Toast.makeText(requireContext(), "Scan Stopped", Toast.LENGTH_SHORT).show()
//            }
//        })
//    }
//
//    private fun getPermitList(context: Context): ArrayList<PlateModel?> {
//        lateinit var jsonString: String
//        try {
//            jsonString = context.assets.open("permit_list.json")
//                .bufferedReader()
//                .use { it.readText() }
//        } catch (ioException: IOException) {
//            showILog("==>Exception", ioException.message.nullSafety())
//        }
//
//return ObjectMapperProvider.fromJson(
//jsonString,
//object : TypeReference<List<PlateModel>>() {}
//) as? ArrayList<PlateModel>
//    }
//
//    private fun getScofflawList(context: Context): ArrayList<PlateModel?> {
//        lateinit var jsonString: String
//        try {
//            jsonString = context.assets.open("scofflaw_number.json")
//                .bufferedReader()
//                .use { it.readText() }
//        } catch (ioException: IOException) {
//            showILog("==>Exception", ioException.message.nullSafety())
//        }
//

//return ObjectMapperProvider.fromJson(
//jsonString,
//object : TypeReference<List<PlateModel>>() {}
//) as? ArrayList<PlateModel>
//    }
//
//    @Synchronized
//    override fun onResume() {
//        super.onResume()
//        startBackgroundThreads()
//
//        // Forward the plateView to the sink
//        if (mSink != null && mPlateView != null) {
//            mSink!!.setAlprPlateView(mPlateView!!)
//        }
//
//        // Open the camera
//        openCamera(mGLSurfaceView!!.width, mGLSurfaceView!!.height)
//    }
//
//    @Synchronized
//    override fun onPause() {
//        closeCamera()
//        stopBackgroundThreads()
//        super.onPause()
//    }
//
//    private fun requestCameraPermission() {
//        if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
//            ConfirmationDialog().show(childFragmentManager, FRAGMENT_DIALOG)
//        } else {
//            requestPermissions(arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION)
//        }
//    }
//
//    override fun onRequestPermissionsResult(
//        requestCode: Int, permissions: Array<String>,
//        grantResults: IntArray
//    ) {
//        if (requestCode == REQUEST_CAMERA_PERMISSION) {
//            if (grantResults.size != 1 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
//                ErrorDialog.newInstance(getString(R.string.request_permission))
//                    .show(childFragmentManager, FRAGMENT_DIALOG)
//            }
//        } else {
//            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        }
//    }
//
//    /**
//     * Shows a [Toast] on the UI thread.
//     *
//     * @param text The message to show
//     */
//    private fun showToast(text: String) {
//        val activity: Activity? = activity
//        activity?.runOnUiThread { Toast.makeText(activity, text, Toast.LENGTH_SHORT).show() }
//    }
//
//    /**
//     * Sets up member variables related to camera.
//     */
//    private fun setUpCameraOutputs() {
//        val activity: Activity? = activity
//        val manager = activity!!.getSystemService(Context.CAMERA_SERVICE) as CameraManager
//        try {
//            for (cameraId in manager.cameraIdList) {
//                val characteristics = manager.getCameraCharacteristics(cameraId)
//
//                // We don't use a front facing camera in this sample.
//                val facing = characteristics.get(CameraCharacteristics.LENS_FACING)
//                if (facing != null && facing == CameraCharacteristics.LENS_FACING_FRONT) {
//                    continue
//                }
//                val map = characteristics.get(
//                    CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP
//                ) ?: continue
//                mSensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION)!!
//
//                // JPEG orientation
//                // https://developer.android.com/reference/android/hardware/camera2/CaptureRequest#JPEG_ORIENTATION
//                val rotation = activity.windowManager.defaultDisplay.rotation
//                mJpegOrientation = (ORIENTATIONS[rotation] + mSensorOrientation + 270) % 360
//
//                // Danger, W.R.! Attempting to use too large a preview size could  exceed the camera
//                // bus' bandwidth limitation, resulting in gorgeous previews but the storage of
//                // garbage capture data.
//                mPreviewSize = chooseOptimalSize(
//                    map.getOutputSizes(SurfaceTexture::class.java),
//                    mPreferredSize!!.width,
//                    mPreferredSize!!.height
//                )
//
//
//                // We fit the aspect ratio of TextureView to the size of preview we picked.
//                val orientation = resources.configuration.orientation
//                if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
//                    mGLSurfaceView!!.setAspectRatio(mPreviewSize!!.width, mPreviewSize!!.height)
//                    mPlateView!!.setAspectRatio(mPreviewSize!!.width, mPreviewSize!!.height)
//                } else {
//                    mGLSurfaceView!!.setAspectRatio(mPreviewSize!!.height, mPreviewSize!!.width)
//                    mPlateView!!.setAspectRatio(mPreviewSize!!.height, mPreviewSize!!.width)
//                }
//                mPlateView!!.setVehicleDetailListener(this)
//                mCameraId = cameraId
//                return
//            }
//        } catch (e: CameraAccessException) {
//            e.printStackTrace()
//        } catch (e: NullPointerException) {
//            // Currently an NPE is thrown when the Camera2API is used but not supported on the
//            // device this code runs.
//            ErrorDialog.newInstance(getString(R.string.camera_error))
//                .show(childFragmentManager, FRAGMENT_DIALOG)
//        }
//    }
//
//    /**
//     * Opens the camera specified by [AlprCameraFragmentCopy.mCameraId].
//     */
//    private fun openCamera(width: Int, height: Int) {
//        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.CAMERA)
//            != PackageManager.PERMISSION_GRANTED
//        ) {
//            requestCameraPermission()
//            return
//        }
//        setUpCameraOutputs()
//        val activity: Activity? = activity
//        val manager = activity!!.getSystemService(Context.CAMERA_SERVICE) as CameraManager
//        try {
//            if (!mCameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
//                throw RuntimeException("Time out waiting to lock camera opening.")
//            }
//            manager.openCamera(mCameraId!!, mStateCallback, mBackgroundTaskCamera.getHandler())
//        } catch (e: CameraAccessException) {
//            e.printStackTrace()
//        } catch (e: InterruptedException) {
//            throw RuntimeException("Interrupted while trying to lock camera opening.", e)
//        }
//    }
//
//    /**
//     * Closes the current [CameraDevice].
//     */
//    private fun closeCamera() {
//        try {
//            mClosingCamera = true
//            mCameraOpenCloseLock.acquire()
//            if (null != mCaptureSession) {
//                mCaptureSession!!.close()
//                mCaptureSession = null
//            }
//            if (null != mCameraDevice) {
//                mCameraDevice!!.close()
//                mCameraDevice = null
//            }
//            if (null != mImageReaderInference) {
//                mImageReaderInference!!.close()
//                mImageReaderInference = null
//            }
//            if (null != mImageReaderDrawing) {
//                mImageReaderDrawing!!.close()
//                mImageReaderDrawing = null
//            }
//        } catch (e: InterruptedException) {
//            throw RuntimeException("Interrupted while trying to lock camera closing.", e)
//        } finally {
//            mCameraOpenCloseLock.release()
//            mClosingCamera = false
//        }
//    }
//
//    /**
//     * Starts a background threads
//     */
//    private fun startBackgroundThreads() {
//        mBackgroundTaskInference.start("InferenceBackgroundThread")
//        mBackgroundTaskDrawing.start("DrawingBackgroundThread")
//        mBackgroundTaskCamera.start("CameraBackgroundThread")
//    }
//
//    /**
//     * Stops the background threads
//     */
//    private fun stopBackgroundThreads() {
//        mBackgroundTaskInference.stop()
//        mBackgroundTaskDrawing.stop()
//        mBackgroundTaskCamera.stop()
//    }
//
//    /**
//     * Creates a new [CameraCaptureSession] for camera preview.
//     */
//    private fun createCameraCaptureSession() {
//        try {
//            // Create Image readers
//            mImageReaderInference = ImageReader.newInstance(
//                mPreviewSize!!.width, mPreviewSize!!.height,
//                VIDEO_FORMAT, MAX_IMAGES
//            )
//            mImageReaderInference!!.setOnImageAvailableListener(
//                mOnImageAvailableListener, mBackgroundTaskCamera.getHandler()
//            )
//            mImageReaderDrawing = ImageReader.newInstance(
//                mPreviewSize!!.width, mPreviewSize!!.height,
//                VIDEO_FORMAT, MAX_IMAGES
//            )
//            mImageReaderDrawing!!.setOnImageAvailableListener(
//                mOnImageAvailableListener, mBackgroundTaskCamera.getHandler()
//            )
//
//            // We set up a CaptureRequest.Builder with the output Surface to the image reader
//            mCaptureRequestBuilder =
//                mCameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_RECORD)
//            //mCaptureRequestBuilder.set(CaptureRequest.CONTROL_AE_TARGET_FPS_RANGE, new Range<>(1, 25));
//            //mCaptureRequestBuilder.set(CaptureRequest.CONTROL_MODE,
//            //        CaptureRequest.CONTROL_MODE_USE_SCENE_MODE);
//            //mCaptureRequestBuilder.set(CaptureRequest.CONTROL_SCENE_MODE,
//            //        CaptureRequest.CONTROL_SCENE_MODE_HIGH_SPEED_VIDEO);
//            mCaptureRequestBuilder!!.addTarget(mImageReaderInference!!.surface)
//            mCaptureRequestBuilder!!.addTarget(mImageReaderDrawing!!.surface)
//
//            // Here, we create a CameraCaptureSession
//            mCameraDevice!!.createCaptureSession(
//                Arrays.asList(
//                    mImageReaderInference!!.surface, mImageReaderDrawing!!.surface
//                ),
//                object : CameraCaptureSession.StateCallback() {
//                    override fun onConfigured(cameraCaptureSession: CameraCaptureSession) {
//                        // The camera is already closed
//                        if (null == mCameraDevice) {
//                            return
//                        }
//
//                        // When the session is ready, we start displaying the preview.
//                        mCaptureSession = cameraCaptureSession
//                        try {
//                            // Auto focus should be continuous
//                            mCaptureRequestBuilder!!.set(
//                                CaptureRequest.CONTROL_AF_MODE,
//                                CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE
//                            )
//                            // Flash is automatically enabled when necessary.
//                            mCaptureRequestBuilder!!.set(
//                                CaptureRequest.CONTROL_AE_MODE,
//                                CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH
//                            )
//                            if (videoStab) {
//                                //To on video stabilization - added by Janak
//                                mCaptureRequestBuilder!!.set(
//                                    CaptureRequest.CONTROL_VIDEO_STABILIZATION_MODE,
//                                    CaptureRequest.CONTROL_VIDEO_STABILIZATION_MODE_ON
//                                )
//                            }
//                            if (lensOpticalStab) {
//                                //To on lens optical stabilization - added by Janak
//                                mCaptureRequestBuilder!!.set(
//                                    CaptureRequest.LENS_OPTICAL_STABILIZATION_MODE,
//                                    CaptureRequest.LENS_OPTICAL_STABILIZATION_MODE_ON
//                                )
//                            }
//
//                            // Finally, we start grabbing the frames
//                            mCaptureRequest = mCaptureRequestBuilder!!.build()
//                            val captureCallback = SimpleCaptureCallback()
//                            mCaptureSession!!.setRepeatingRequest(
//                                mCaptureRequest!!,
//                                SimpleCaptureCallback(), mBackgroundTaskCamera.getHandler()
//                            )
//                        } catch (e: CameraAccessException) {
//                            e.printStackTrace()
//                        }
//                    }
//
//                    override fun onConfigureFailed(
//                        cameraCaptureSession: CameraCaptureSession
//                    ) {
//                        showToast("Failed")
//                    }
//                }, mBackgroundTaskCamera.getHandler()
//            )
//        } catch (e: CameraAccessException) {
//            e.printStackTrace()
//        }
//    }
//
//    inner class SimpleCaptureCallback : CameraCaptureSession.CaptureCallback() {
//        override fun onCaptureStarted(
//            session: CameraCaptureSession,
//            request: CaptureRequest,
//            timestamp: Long,
//            frameNumber: Long
//        ) {
//            super.onCaptureStarted(session, request, timestamp, frameNumber)
//            showELog("==>Capture Callback:-", "onCaptureStarted")
//        }
//
//        override fun onCaptureProgressed(
//            session: CameraCaptureSession,
//            request: CaptureRequest,
//            partialResult: CaptureResult
//        ) {
//            super.onCaptureProgressed(session, request, partialResult)
//            showELog("==>Capture Callback:-", "onCaptureProgressed")
//        }
//
//        override fun onCaptureCompleted(
//            session: CameraCaptureSession,
//            request: CaptureRequest,
//            result: TotalCaptureResult
//        ) {
//            // super.onCaptureCompleted(session, request, result);
//            showELog("==>Capture Callback:-", "onCaptureCompleted")
//
////            Image image = imageReaderAvailable.acquireLatestImage();
////            ByteBuffer buffer = image.getPlanes()[0].getBuffer();
////            byte[] bytes = new byte[buffer.remaining()];
////            buffer.get(bytes);
////            Bitmap myBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, null);
////            ivLicensePlate.setImageBitmap(myBitmap);
//////            SetBitmap setBitmap = new SetBitmap();
//////            setBitmap.setMbitmap(myBitmap);
////            image.close();
//        }
//
//        override fun onCaptureFailed(
//            session: CameraCaptureSession,
//            request: CaptureRequest,
//            failure: CaptureFailure
//        ) {
//            super.onCaptureFailed(session, request, failure)
//            showELog("==>Capture Callback:-", "onCaptureFailed")
//        }
//
//        override fun onCaptureSequenceCompleted(
//            session: CameraCaptureSession,
//            sequenceId: Int,
//            frameNumber: Long
//        ) {
//            super.onCaptureSequenceCompleted(session, sequenceId, frameNumber)
//            showELog("==>Capture Callback:-", "onCaptureSequenceCompleted")
//        }
//
//        override fun onCaptureSequenceAborted(session: CameraCaptureSession, sequenceId: Int) {
//            super.onCaptureSequenceAborted(session, sequenceId)
//            showELog("==>Capture Callback:-", "onCaptureSequenceAborted")
//        }
//
//        override fun onCaptureBufferLost(
//            session: CameraCaptureSession,
//            request: CaptureRequest,
//            target: Surface,
//            frameNumber: Long
//        ) {
//            super.onCaptureBufferLost(session, request, target, frameNumber)
//            showELog("==>Capture Callback:-", "onCaptureBufferLost")
//        }
//    }
//
//    private var isNumberReceived = false
//    override fun getLicensePlateNumber(licensePlateNumber: String?) {
//        //tvNumberPlate.setText(number);
//        this.licensePlateNumber = licensePlateNumber
//        isNumberReceived = true
//        showILog("==>Number", "" + licensePlateNumber)
//        try {
//            //callResultActivity();
//
//
//            //mCaptureSession.abortCaptures();
//
////            final CaptureRequest.Builder captureBuilder =
////                    mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
////            captureBuilder.addTarget(mImageReader.getSurface());
////
////            // Orientation
////            int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
////            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation));
////
////            CameraCaptureSession.CaptureCallback CaptureCallback
////                    = new CameraCaptureSession.CaptureCallback() {
////
////                @Override
////                public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request,
////                                               TotalCaptureResult result) {
////                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss:SSS");
////                    Date resultdate = new Date(System.currentTimeMillis());
////                    String mFileName = sdf.format(resultdate);
////                    mFile = new File(getActivity().getExternalFilesDir(null), "pic "+mFileName+" preview.jpg");
////
////                    showILog("Saved file", ""+mFile.toString());
////                    unlockFocus();
////                }
////            };
//
//            //if (mCaptureSession != null)
//            //TODO existing
//            //mCaptureSession.abortCaptures();
//
//
//            //closeCamera();
//            //stopBackgroundThreads();
////            mCaptureSession.stopRepeating();
////            mCaptureSession.capture(mCaptureRequestBuilder.build(), new SimpleCaptureCallback(), null);
//
//            //TODO existing
////            llOutput.setVisibility(View.VISIBLE);
////            ivLicensePlate.setImageBitmap(finalBitmap);
//
//
////            getActivity().runOnUiThread(new Runnable() {
////                @Override
////                public void run() {
////                    //ivLicensePlate.setImageBitmap(finalBitmap);
////                }
////            });
//        } //        catch (CameraAccessException e) {
//        //            e.printStackTrace();
//        //        }
//        catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }
//
//    override fun getVehicleMakeBrand(vehicleMakeBrand: String?) {
//        //tvMake.setText(make);
//        this.vehicleMakeBrand = vehicleMakeBrand
//        showILog("==>make", "" + vehicleMakeBrand)
//    }
//
//    override fun getVehicleModel(vehicleModel: String?) {
//        //tvModel.setText(model);
//        this.vehicleModel = vehicleModel
//        showILog("==>model", "" + vehicleModel)
//    }
//
//    override fun getVehicleColor(vehicleColor: String?) {
//        if (vehicleColor != null) {
//            // tvColor.setText(color);
//            this.vehicleColor = vehicleColor
//            showILog("==>color", "" + vehicleColor)
//        }
//    }
//
//    override fun getVehicleBodyStyle(vehicleBodyStyle: String?) {
//        //getting this null everytime
//        showILog("==>BodyStyle", "" + vehicleBodyStyle)
//    }
//
//    override fun getLicensePlateCountry(licensePlateCountry: String?) {
//        showILog("==>Country", "" + licensePlateCountry)
//    }
//
//    override fun getLicensePlateState(licensePlateState: String?) {
//        if (licensePlateState != null) {
//            this.licensePlateState = licensePlateState
//        }
//        showILog("==>State", "" + licensePlateState)
//    }
//
//    private var isPointAndScanDone = false
//    private fun callResultActivity() {
//        if (!licensePlateNumber!!.isEmpty() && !vehicleMakeBrand!!.isEmpty() && !vehicleModel!!.isEmpty() && !vehicleColor!!.isEmpty() && !isPointAndScanDone) {
//            isPointAndScanDone = true
////            val i = Intent(requireActivity(), ScanResultActivity::class.java)
////            i.putExtra("bitmap", createTempFile(finalBitmap))
////            i.putExtra("numberplate", numberPlate)
////            i.putExtra("make", make)
////            i.putExtra("model", model)
////            i.putExtra("color", color)
////            i.putExtra("state", state)
////            startActivity(i)
////            requireActivity().finishAffinity()
//
////            val i = Intent()
////            i.putExtra("bitmap", createTempFile(finalBitmap))
////            i.putExtra("numberplate", numberPlate)
////            i.putExtra("make", make)
////            i.putExtra("model", model)
////            i.putExtra("color", color)
////            i.putExtra("state", state)
////            requireActivity().setResult(Activity.RESULT_OK, i)
////            requireActivity().finish()
//
//            mSink?.getScannedPlate(
//                licensePlateNumber,
//                vehicleMakeBrand,
//                vehicleModel,
//                vehicleColor,
//                licensePlateState,
//                createTempFile(vehicleBitmapImage)
//            )
//        }
//    }
//
//    private fun setScannedDataInList(
//        mNumberPlate: String?,
//        mMake: String?,
//        mModel: String?,
//        mColor: String?,
//        mFinalBitmap: Bitmap?
//    ) {
//        if (mNumberPlate.nullSafety().isNotEmpty() && mMake.nullSafety()
//                .isNotEmpty() && mModel.nullSafety()
//                .isNotEmpty() && mColor.nullSafety().isNotEmpty()
//        ) {
//            showELog("==>isPlate:", "tempPlate-" + mNumberPlate)
//            showELog("==>isPlate:", "tempMake-" + mMake)
//
//            if (mNumberPlate?.length.nullSafety() > 3 && !isPlateAlreadyAdded(
//                    mNumberPlate,
//                    mMake
//                )
//            ) {
//                //Scan Tone
//                toneGen1!!.startTone(ToneGenerator.TONE_PROP_ACK, 1000)
//                showILog("==>Scanned:", "Start Tone")
//
//                val timestamp = System.currentTimeMillis()
//                val currentTime = timeFormat.format(timestamp)
//                val scanDataModel = ScanDataModel()
//                //scanDataModel.setBitmap(mFinalBitmap);
//                scanDataModel.vehicleImageURL = createTempFile(mFinalBitmap)
//                scanDataModel.licensePlateNumber = mNumberPlate
//                scanDataModel.vehicleMakeBrand = mMake
//                scanDataModel.vehicleModel = mModel
//                scanDataModel.vehicleColor = mColor
//                scanDataModel.licensePlateState = licensePlateState
//                scanDataModel.officerName = officerName
//                scanDataModel.beat = beat
//                scanDataModel.squad = squad
//                scanDataModel.block = block
//                scanDataModel.street = street
//                scanDataModel.streetSide = streetSide
//                scanDataModel.timingLimit = timingLimit
//                scanDataModel.date = currentDate
//                scanDataModel.time = currentTime
//                scanDataModel.timestamp = timestamp
//
//                if (isTimingViolation(mNumberPlate, timestamp)) {
//                    scanDataModel.violation = 1
//                    //Scan Tone
//                    CoroutineScope(Dispatchers.IO).launch {
//                        delay(1000)
//                        toneGen1!!.startTone(ToneGenerator.TONE_SUP_ERROR, 1000)
//                    }
//                    showILog("==>Scanned:", "timing Violation")
//
//                } else {
//                    scanDataModel.violation = 0
//                }
//
//                if (isScofflowPlate(mNumberPlate)) {
//                    scanDataModel.scofflaw = 1
//                    CoroutineScope(Dispatchers.IO).launch {
//                        delay(2000)
//                        //Voilation high sound
//                        toneGen1!!.startTone(ToneGenerator.TONE_CDMA_HIGH_L, 1000)
//                    }
//                    showILog("==>Scanned:", "Scofflaw")
//
//                } else {
//                    scanDataModel.scofflaw = 0
//                }
//
//                if (isPermitListPlate(mNumberPlate)) {
//                    scanDataModel.permitList = 1
//                    CoroutineScope(Dispatchers.IO).launch {
//                        delay(3000)
//                        //Scan Tone
//                        toneGen1!!.startTone(ToneGenerator.TONE_SUP_INTERCEPT, 1000)
//                    }
//                    showILog("==>Scanned:", "permit list")
//                } else {
//                    scanDataModel.permitList = 0
//                }
//                scanDataModelList.add(scanDataModel)
//            }
//
//            mFinalBitmap?.recycle()
//            vehicleBitmapImage?.recycle()
//            vehicleBitmapImage = null
//            licensePlateNumber = null
//            vehicleMakeBrand = null
//            vehicleModel = null
//            vehicleColor = null
//            licensePlateState = null
//        }
//
//    }
//
//    private fun containsLprNumber(lprNumber: String?, make: String?): Boolean {
//        val tempList = scanDataModelList.filter { it?.licensePlateNumber == lprNumber && it?.vehicleMakeBrand == make }
//        return tempList.isNotEmpty()
//
////        for (o in scanDataModelList) {
////            if (o != null && o.lprNumber == lprNumber && o.make == make) {
////                return true
////            }
////        }
////        return false
//    }
//
//    private fun isPlateAlreadyAdded(lprNumber: String?, make: String?): Boolean {
//        val tempList = scanDataModelList.filter { it?.licensePlateNumber == lprNumber && it?.vehicleMakeBrand == make }
//        //val tempListMap = scanDataModelList.map { it?.lprNumber == lprNumber && it?.make == make }
//        showELog("==>isPlate:", "list-" + ObjectMapperProvider.instance.writeValueAsString(scanDataModelList))
//        showELog("==>isPlate:", "tempFilter-" + ObjectMapperProvider.instance.writeValueAsString(tempList))
//        //showELog("==>isPlate:","tempMap-"+ObjectMapperProvider.instance.writeValueAsString(tempListMap))
//        showELog("==>isPlate:", "isContentPlate?-" + tempList.isNotEmpty())
//        showELog("==>isPlate:", "**************************")
//
//        return tempList.isNotEmpty()
//
//        //return tempList.isNotEmpty()
//
////        for (o in scanDataModelList) {
////            if (o != null && o.lprNumber == lprNumber && o.make == make) {
////                return true
////            }
////        }
////        return false
//    }
//
//    private fun isTimingViolation(numberPlate: String?, currentTimestamp: Long): Boolean {
//        val tempList = recentScanDataModel.filter {
//            it?.licensePlateNumber == numberPlate
//                    && it?.block == block
//                    && it?.street == street
//                    && it?.streetSide == streetSide
//                    && differenceInMinutes(
//                it?.timestamp,
//                currentTimestamp
//            ) > it?.timingLimit.nullSafety("0").toInt()
//        }
//        return tempList.isNotEmpty()
//    }
//
//    private fun isScofflowPlate(numberPlate: String?): Boolean {
//        val tempList = scofflawList.filter { it?.licensePlateNumber == numberPlate }
//        return tempList.isNotEmpty()
//    }
//
//    private fun isPermitListPlate(numberPlate: String?): Boolean {
//        val tempList = permitList.filter { it?.licensePlateNumber == numberPlate }
//        return tempList.isNotEmpty()
//    }
//
//    private fun differenceInMinutes(oldTimeStamp: Long?, currentTimeStamp: Long): Int {
//        val diff: Long = currentTimeStamp.nullSafety() - oldTimeStamp.nullSafety()
//        val seconds = diff / 1000
//        return (seconds / 60).toInt()
////        val hours = minutes / 60
////        val days = hours / 24
//    }
//
//    /**
//     *
//     */
//    interface AlprCameraFragmentSink {
//        /**
//         * @param view
//         */
//        fun setAlprPlateView(view: AlprPlateView)
//
//        /**
//         * @param image
//         * @param jpegOrientation
//         */
//        fun setImage(image: Image, jpegOrientation: Int)
//
//        fun getScannedPlate(
//            number: String?,
//            make: String?,
//            model: String?,
//            color: String?,
//            state: String?,
//            bitmapUrl: String?
//        ) {
//        }
//    }
//
//    /**
//     * Compares two `Size`s based on their areas.
//     */
//    internal class CompareSizesByArea : Comparator<Size> {
//        override fun compare(lhs: Size, rhs: Size): Int {
//            // We cast here to ensure the multiplications won't overflow
//            return java.lang.Long.signum(
//                lhs.width.toLong() * lhs.height -
//                        rhs.width.toLong() * rhs.height
//            )
//        }
//    }
//
//    /**
//     * Shows an error message dialog.
//     */
//    class ErrorDialog : DialogFragment() {
//        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
//            val activity: Activity? = activity
//            return AlertDialog.Builder(activity)
//                .setMessage(requireArguments().getString(ARG_MESSAGE))
//                .setPositiveButton(android.R.string.ok) { dialogInterface, i -> activity!!.finish() }
//                .create()
//        }
//
//        companion object {
//            private const val ARG_MESSAGE = "message"
//            fun newInstance(message: String?): ErrorDialog {
//                val dialog = ErrorDialog()
//                val args = Bundle()
//                args.putString(ARG_MESSAGE, message)
//                dialog.arguments = args
//                return dialog
//            }
//        }
//    }
//
//    /**
//     * Shows OK/Cancel confirmation dialog about camera permission.
//     */
//    class ConfirmationDialog : DialogFragment() {
//        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
//            val parent = parentFragment
//            return AlertDialog.Builder(activity)
//                .setMessage(R.string.request_permission)
//                .setPositiveButton(android.R.string.ok) { dialog, which ->
//                    parent!!.requestPermissions(
//                        arrayOf(Manifest.permission.CAMERA),
//                        REQUEST_CAMERA_PERMISSION
//                    )
//                }
//                .setNegativeButton(
//                    android.R.string.cancel
//                ) { dialog, which ->
//                    val activity: Activity? = parent!!.activity
//                    activity?.finish()
//                }
//                .create()
//        }
//    }
//}