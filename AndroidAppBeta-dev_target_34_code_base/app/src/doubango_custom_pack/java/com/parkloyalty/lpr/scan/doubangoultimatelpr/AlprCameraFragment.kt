package com.parkloyalty.lpr.scan.doubangoultimatelpr

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.ContextWrapper
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.*
import android.hardware.camera2.*
import android.media.AudioManager
import android.media.Image
import android.media.ImageReader
import android.media.ToneGenerator
import android.os.Bundle
import android.os.Environment
import android.renderscript.*
import android.util.Size
import android.util.SparseIntArray
import android.view.LayoutInflater
import android.view.Surface
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.parkloyalty.lpr.scan.R
import com.parkloyalty.lpr.scan.basecontrol.BaseApplication
import com.parkloyalty.lpr.scan.database.AppDatabase
import com.parkloyalty.lpr.scan.doubangoultimatelpr.interfaces.FragmentDataIntercepter
import com.parkloyalty.lpr.scan.doubangoultimatelpr.interfaces.VehicleDetailListener
import com.parkloyalty.lpr.scan.doubangoultimatelpr.model.*
import com.parkloyalty.lpr.scan.doubangoultimatelpr.utils.AlprBackgroundTask
import com.parkloyalty.lpr.scan.util.DoubangoConstants.FROM_CONTINUOUS_VEHICLE_MODE
import com.parkloyalty.lpr.scan.util.DoubangoConstants.FROM_POINT_AND_SCAN
import com.parkloyalty.lpr.scan.util.DoubangoConstants.KEY_IS_FLASH_REQUIRED
import com.parkloyalty.lpr.scan.util.DoubangoConstants.KEY_IS_LENS_STAB
import com.parkloyalty.lpr.scan.util.DoubangoConstants.KEY_IS_VIDEO_STAB
import com.parkloyalty.lpr.scan.util.DoubangoConstants.KEY_PATH_TO_SAVE_IMAGE
import com.parkloyalty.lpr.scan.doubangoultimatelpr.views.AlprGLSurfaceView
import com.parkloyalty.lpr.scan.doubangoultimatelpr.views.AlprPlateView
import com.parkloyalty.lpr.scan.extensions.*
import com.parkloyalty.lpr.scan.doubangoultimatelpr.utils.ConstantsDoubango.API_CONSTANT_RUN_TYPE_TIMING
import com.parkloyalty.lpr.scan.doubangoultimatelpr.utils.ConstantsDoubango.FOLDER_APP_NAME
import com.parkloyalty.lpr.scan.doubangoultimatelpr.utils.ConstantsDoubango.FOLDER_CONTINUOUS_SCAN_IMAGES
import com.parkloyalty.lpr.scan.doubangoultimatelpr.utils.ConstantsDoubango.FOLDER_POINT_AND_SCAN_IMAGES
import com.parkloyalty.lpr.scan.doubangoultimatelpr.utils.ConstantsDoubango.KEY_BADGE_ID
import com.parkloyalty.lpr.scan.doubangoultimatelpr.utils.ConstantsDoubango.KEY_BEAT
import com.parkloyalty.lpr.scan.doubangoultimatelpr.utils.ConstantsDoubango.KEY_BLOCK
import com.parkloyalty.lpr.scan.doubangoultimatelpr.utils.ConstantsDoubango.KEY_FROM
import com.parkloyalty.lpr.scan.doubangoultimatelpr.utils.ConstantsDoubango.KEY_OFFICER_NAME
import com.parkloyalty.lpr.scan.doubangoultimatelpr.utils.ConstantsDoubango.KEY_SIDE
import com.parkloyalty.lpr.scan.doubangoultimatelpr.utils.ConstantsDoubango.KEY_SQUAD
import com.parkloyalty.lpr.scan.doubangoultimatelpr.utils.ConstantsDoubango.KEY_STREET
import com.parkloyalty.lpr.scan.doubangoultimatelpr.utils.ConstantsDoubango.KEY_TIME_LIMIT_TEXT
import com.parkloyalty.lpr.scan.doubangoultimatelpr.utils.ConstantsDoubango.KEY_TIME_LIMIT_VALUE
import com.parkloyalty.lpr.scan.doubangoultimatelpr.utils.ConstantsDoubango.KEY_VEHICLE_RUN_METHOD
import com.parkloyalty.lpr.scan.doubangoultimatelpr.utils.ConstantsDoubango.SDF_MARK_DATE
import com.parkloyalty.lpr.scan.doubangoultimatelpr.utils.ConstantsDoubango.SDF_TIME_12_HOURS
import com.parkloyalty.lpr.scan.util.DialogUtils
import com.parkloyalty.lpr.scan.util.DoubangoConstants.KEY_IS_LANDSCAPE_ORIENTATION_REQUIRED
import com.parkloyalty.lpr.scan.util.LogUtil.printLog
import com.parkloyalty.lpr.scan.util.SharedPref
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class AlprCameraFragment : Fragment, ActivityCompat.OnRequestPermissionsResultCallback,
    VehicleDetailListener {

    private var isPointAndScanned = false
    private var fromAction: String? = FROM_POINT_AND_SCAN
    private var pathToSaveImage: String? = ""
    private var isPointAndClickStarted = false
    private var isContinuousScanStarted = false
    private val scanDataModelList = ArrayList<ScanDataModel?>()
    private var recentScanDataModel = ArrayList<ScanDataModel?>()
    private var permitList = ArrayList<PlateModel?>()
    private var scofflawList = ArrayList<PlateModel?>()
    private var lensOpticalStab = false
    private var videoStab = false
    private var isFlashLightRequired = false
    private var isLandscapeOrientationRequired = false
    private var officerName: String? = ""
    private var badgeId: String? = ""
    private var beat: String? = ""
    private var squad: String? = ""
    private var block: String? = ""
    private var street: String? = ""
    private var side: String? = ""
    private var timeLimitText: String? = ""
    private var timeLimitValue: String? = ""
    private var vehicleRunMethod: String? = API_CONSTANT_RUN_TYPE_TIMING
    private val calendar = Calendar.getInstance()
    private val currentDate = SDF_MARK_DATE.format(calendar.time)
    private val vehicleRunStartTime = SDF_TIME_12_HOURS.format(calendar.time)
    private var toneGenerator: ToneGenerator? = null
    private var myDatabase: AppDatabase? = null
    private var fragmentDataIntercepter: FragmentDataIntercepter? = null

    private lateinit var llBottom: LinearLayout
    private lateinit var llBottomPointAndClick: LinearLayout
    private lateinit var btnStart: Button
    private lateinit var btnStop: Button
    private lateinit var btnCapture: Button

    private lateinit var ivCameraFrame: ImageView

    private var vehicleMakeDataItemList: ArrayList<VehicleMakeDataItem>? = ArrayList()
    private var vehicleModelDataItemList: ArrayList<VehicleModelDataItem>? = ArrayList()
    private var vehicleColorDataItemList: ArrayList<VehicleColorDataItem>? = ArrayList()

    fun setFragmentDataIntercepter(fragmentDataIntercepter: FragmentDataIntercepter) {
        this.fragmentDataIntercepter = fragmentDataIntercepter
    }

    private var mPreferredSize: Size? = null

    /**
     * ID of the current [CameraDevice].
     */
    private var mCameraId: String? = null
    private var mJpegOrientation = 1

    /**
     * An [AlprGLSurfaceView] for camera preview.
     */
    private var mGLSurfaceView: AlprGLSurfaceView? = null
    private var mPlateView: AlprPlateView? = null
    private var flParentContainer: FrameLayout? = null

    private var vehicleBitmapImage: Bitmap? = null
    private var licensePlateNumber: String? = ""
    private var vehicleMakeBrand: String? = ""
    private var vehicleModel: String? = ""
    private var vehicleColor: String? = ""
    private var vehicleBodyStyle: String? = ""
    private var licensePlateCountry: String? = ""
    private var licensePlateState: String? = ""

    /**
     * A [CameraCaptureSession] for camera preview.
     */
    private var mCaptureSession: CameraCaptureSession? = null

    /**
     * A reference to the opened [CameraDevice].
     */
    private var mCameraDevice: CameraDevice? = null

    /**
     * The [Size] of camera preview.
     */
    private var mPreviewSize: Size? = null
    private var mSink: AlprCameraFragmentSink? = null
    private val mBackgroundTaskCamera = AlprBackgroundTask()
    private val mBackgroundTaskDrawing = AlprBackgroundTask()
    private val mBackgroundTaskInference = AlprBackgroundTask()

    @Inject
    lateinit var sharedPreference: SharedPref

    private val dateFormatter: SimpleDateFormat = SimpleDateFormat("MMddyyyyhhmmss", Locale.US)

    /**
     * [CameraDevice.StateCallback] is called when [CameraDevice] changes its state.
     */
    private val mStateCallback: CameraDevice.StateCallback = object : CameraDevice.StateCallback() {
        override fun onOpened(cameraDevice: CameraDevice) {
            // This method is called when the camera is opened.  We start camera preview here.
            mCameraOpenCloseLock.release()
            mCameraDevice = cameraDevice
            createCameraCaptureSession()
        }

        override fun onDisconnected(cameraDevice: CameraDevice) {
            mCameraOpenCloseLock.release()
            cameraDevice.close()
            mCameraDevice = null
        }

        override fun onError(cameraDevice: CameraDevice, error: Int) {
            mCameraOpenCloseLock.release()
            cameraDevice.close()
            mCameraDevice = null
            val activity: Activity? = activity
            activity?.finish()
        }
    }
    private var mClosingCamera = false

    /**
     * An [ImageReader] that handles still image capture.
     */
    private var mImageReaderInference: ImageReader? = null
    private var mImageReaderDrawing: ImageReader? = null

    /**
     * This a callback object for the [ImageReader]. "onImageAvailable" will be called when a
     * still image is ready to be saved.
     */
    private val mOnImageAvailableListener = ImageReader.OnImageAvailableListener { reader ->
        if (mClosingCamera) {
            return@OnImageAvailableListener
        }


        try {
            val image = reader.acquireLatestImage() ?: return@OnImageAvailableListener
//            if (licensePlateNumber.nullSafety().isNotEmpty() && vehicleMakeBrand.nullSafety()
//                    .isNotEmpty() && vehicleModel.nullSafety()
//                    .isNotEmpty() && vehicleColor.nullSafety().isNotEmpty()
//            ){
//                vehicleBitmapImage = yuv420ToBitmap(image, requireContext())
//                licensePlateNumber = null
//                vehicleMakeBrand = null
//                vehicleModel = null
//                vehicleColor = null
//            }
            //vehicleBitmapImage = yuv420ToBitmap(image, requireContext())

//            mGLSurfaceView?.queueEvent {
//                vehicleBitmapImage = yuv420ToBitmap(image, requireContext())
//            }


//            val w: Int = image.width
//            val h: Int = image.height
//
//            Log.i("hari", "w:$w-----h:$h")
//
//            val b = IntArray((w * h))
//            val bt = IntArray((w * h))
//            val buffer: IntBuffer = IntBuffer.wrap(b)
//            buffer.position(0)
//            GLES20.glReadPixels(0, 0, w, h, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, buffer)
//            for (i in 0 until h) {
//                //remember, that OpenGL bitmap is incompatible with Android bitmap
//                //and so, some correction need.
//                for (j in 0 until w) {
//                    val pix = b[i * w + j]
//                    val pb = pix shr 16 and 0xff
//                    val pr = pix shl 16 and 0x00ff0000
//                    val pix1 = pix and -0xff0100 or pr or pb
//                    bt[(h - i - 1) * w + j] = pix1
//                }
//            }
//            var inBitmap: Bitmap? = null
//            if (inBitmap == null || !inBitmap.isMutable()
//                || inBitmap.getWidth() != w || inBitmap.getHeight() != h) {
//                inBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
//            }
//            //Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
//            //Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
//            inBitmap?.copyPixelsFromBuffer(buffer)
//            //return inBitmap ;
//            // return Bitmap.createBitmap(bt, w, h, Bitmap.Config.ARGB_8888);
//            //return inBitmap ;
//            // return Bitmap.createBitmap(bt, w, h, Bitmap.Config.ARGB_8888);
//            inBitmap = Bitmap.createBitmap(bt, w, h, Bitmap.Config.ARGB_8888)
//
//            val bos = ByteArrayOutputStream()
//            inBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bos)
//            val bitmapdata: ByteArray = bos.toByteArray()
//            val fis = ByteArrayInputStream(bitmapdata)
//
//            val c: Calendar = Calendar.getInstance()
//            val mytimestamp: Long = c.getTimeInMillis()
//            val timeStamp = mytimestamp.toString()
//            val myfile = "hari$timeStamp.jpeg"
//
//            val dir_image : File = File(
//                Environment.getExternalStorageDirectory().toString() + File.separator.toString() +
//                        "printerscreenshots_open" + File.separator.toString() + "image"
//            )
//            dir_image.mkdirs()
//            try {
//                val tmpFile = File(dir_image, myfile)
//                val fos = FileOutputStream(tmpFile)
//
//                val buf = ByteArray(1024)
//                var len: Int
//                while (fis.read(buf).also { len = it } > 0) {
//                    fos.write(buf, 0, len)
//                }
//                fis.close()
//                fos.close()
//            }catch (e : FileNotFoundException) {
//                e.printStackTrace();
//            } catch (e : IOException) {
//                e.printStackTrace();
//            }


            //tempImageModel.tempImage = image
            val isForDrawing = reader.surface === mImageReaderDrawing!!.surface

            if (isForDrawing) {
                mGLSurfaceView!!.setImage(image, mJpegOrientation, flParentContainer!!)
            } else {
                mSink!!.setImage(image, mJpegOrientation)
            }
            //vehicleBitmapImage = yuv420ToBitmap(image, requireContext())

            if (fromAction == FROM_POINT_AND_SCAN) {
                if (isPointAndClickStarted)
                    pointAndScanResult()
            } else {
//                vehicleBitmapImage = mGLSurfaceView?.getVehicleImage()
//                vehicleBitmapImage = getResizedBitmap(vehicleBitmapImage!!,1000)
                if (isContinuousScanStarted) setScannedDataInList(
                    licensePlateNumber,
                    vehicleMakeBrand,
                    vehicleModel,
                    vehicleColor,
                    vehicleBitmapImage
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getBitmapFromView(view: View): Bitmap? {
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        val bitmap = Bitmap.createBitmap(
            view.measuredWidth, view.measuredHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
        view.draw(canvas)
        return bitmap
    }

    private fun yuv420ToBitmap(
        image: Image,
        context: Context
    ): Bitmap {
        val rs =
            RenderScript.create(requireContext())
        val script =
            ScriptIntrinsicYuvToRGB.create(
                rs,
                Element.U8_4(rs)
            )

        // Refer the logic in a section below on how to convert a YUV_420_888 image
        // to single channel flat 1D array. For sake of this example I'll abstract it
        // as a method.
        val yuvByteArray = image2byteArray(image)
        val yuvType =
            Type.Builder(rs, Element.U8(rs))
                .setX(yuvByteArray.size)
        val `in` = Allocation.createTyped(
            rs,
            yuvType.create(),
            Allocation.USAGE_SCRIPT
        )
        val rgbaType =
            Type.Builder(rs, Element.RGBA_8888(rs))
                .setX(image.width)
                .setY(image.height)
        val out = Allocation.createTyped(
            rs,
            rgbaType.create(),
            Allocation.USAGE_SCRIPT
        )

        // The allocations above "should" be cached if you are going to perform
        // repeated conversion of YUV_420_888 to Bitmap.
        `in`.copyFrom(yuvByteArray)
        script.setInput(`in`)
        script.forEach(out)
        //Bitmap bitmap = Bitmap.createBitmap(image.getWidth(), image.getHeight(), Bitmap.Config.ARGB_8888);
        val bitmap = Bitmap.createBitmap(
            image.width,
            image.height,
            Bitmap.Config.ARGB_8888
        )
        out.copyTo(bitmap)

        return rotateBitmap(bitmap, 90f)
    }

    private fun createAndSaveScannedImage(
        mBitmap: Bitmap?,
        lprNumber: String?,
        pathToSaveImage: String?
    ): String {
        return try {
            var folder: File? = null
            if (pathToSaveImage.nullSafety().isEmpty()) {
                val innerFolder = if (fromAction == FROM_POINT_AND_SCAN) {
                    File.separator + FOLDER_POINT_AND_SCAN_IMAGES
                } else {
                    File.separator + FOLDER_CONTINUOUS_SCAN_IMAGES + File.separator + "$block-$street-$side"
                }

                val contextWrapper = ContextWrapper(BaseApplication.instance?.applicationContext)
                val documentDirectory =
                    contextWrapper.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS + FOLDER_APP_NAME)
                folder = File(documentDirectory, innerFolder)
                if (!folder.exists()) folder.mkdirs()
            } else {
                folder = File(pathToSaveImage.nullSafety())
                if (!folder.exists()) folder.mkdirs()
            }


//            val file = File(
//                folder,
//                lprNumber + "_" + dateFormatter.format(Calendar.getInstance().time) + ".png"
//            )

            val file = File(
                folder,
                "anpr_" + lprNumber + ".jpg"
            )

            printLog("==>File:", folder)
            printLog("==>File:", file.absolutePath)



            file.createNewFile()
            var outStream: OutputStream? = null
            outStream = FileOutputStream(file)
            mBitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, outStream)
            outStream.close()
            file.absolutePath
        } catch (e: IOException) {
            e.printStackTrace()
            ""
        }
    }

    private fun rotateBitmap(
        original: Bitmap,
        degrees: Float
    ): Bitmap {
        val x = original.width
        val y = original.height
        val matrix = Matrix()
        matrix.preRotate(degrees)
        return Bitmap.createBitmap(original, 0, 0, original.width, original.height, matrix, true)
    }

    private fun getResizedBitmap(fromAction: String?, image: Bitmap, maxSize: Int): Bitmap {
        var width = image.width
        var height = image.height
        val bitmapRatio = width.toFloat() / height.toFloat()
        if (bitmapRatio > 1) {
            width = maxSize
            height = (width / bitmapRatio).toInt()
        } else {
            height = maxSize
            width = (height * bitmapRatio).toInt()
        }

        if (fromAction == FROM_POINT_AND_SCAN) {
            val m = Matrix()

            val innerWidth = if (isLandscapeOrientationRequired) {
                (image.getWidth().nullSafety()) / 2
            } else {
                image.getWidth()
            }

            //val innerWidth = image.getWidth()

            val innerHeight: Int = image.getHeight()
            if (innerWidth != width || innerHeight != height) {
                val sx: Float = width / innerWidth.toFloat()
                val sy: Float = height / innerHeight.toFloat()
                m.setScale(sx, sy)
            }
              //If you need more height
//            val yBlockHeight = innerHeight/3;
//            return Bitmap.createBitmap(image, 0, yBlockHeight, innerWidth, yBlockHeight, m, true)

            var yBlockHeight = 0
            var x = 0
            var y = 0

            if (isLandscapeOrientationRequired) {
                x = (image.getWidth().nullSafety(5)) / 5
                y = (image.height.nullSafety(4)) / 4

                return Bitmap.createBitmap(image, x, y, (x * 3), (y * 2))
            } else {
                yBlockHeight = innerHeight / 4
                x = 0
                y = yBlockHeight + (yBlockHeight / 2)
                return Bitmap.createBitmap(image, x, y, innerWidth, yBlockHeight)
            }
        } else {
            //Original
            return Bitmap.createScaledBitmap(image, width, height, true)
        }


    }

    private fun image2byteArray(image: Image): ByteArray {
        require(image.format == ImageFormat.YUV_420_888) { "Invalid image format" }
        val width = image.width
        val height = image.height
        val yPlane = image.planes[0]
        val uPlane = image.planes[1]
        val vPlane = image.planes[2]
        val yBuffer = yPlane.buffer
        val uBuffer = uPlane.buffer
        val vBuffer = vPlane.buffer

        // Full size Y channel and quarter size U+V channels.
        val numPixels = (width * height * 1.5f).toInt()
        val nv21 = ByteArray(numPixels)
        var index = 0

        // Copy Y channel.
        val yRowStride = yPlane.rowStride
        val yPixelStride = yPlane.pixelStride
        for (y in 0 until height) {
            for (x in 0 until width) {
                nv21[index++] = yBuffer[y * yRowStride + x * yPixelStride]
            }
        }

        // Copy VU data; NV21 format is expected to have YYYYVU packaging.
        // The U/V planes are guaranteed to have the same row stride and pixel stride.
        val uvRowStride = uPlane.rowStride
        val uvPixelStride = uPlane.pixelStride
        val uvWidth = width / 2
        val uvHeight = height / 2
        for (y in 0 until uvHeight) {
            for (x in 0 until uvWidth) {
                val bufferIndex = y * uvRowStride + x * uvPixelStride
                // V channel.
                nv21[index++] = vBuffer[bufferIndex]
                // U channel.
                nv21[index++] = uBuffer[bufferIndex]
            }
        }
        return nv21
    }

    private var mCaptureRequestBuilder: CaptureRequest.Builder? = null

    /**
     * [CaptureRequest] generated by [.mCaptureRequestBuilder]
     */
    private var mCaptureRequest: CaptureRequest? = null

    /**
     * A [Semaphore] to prevent the app from exiting before closing the camera.
     */
    private val mCameraOpenCloseLock = Semaphore(1)

    /**
     * Orientation of the camera sensor
     */
    private var mSensorOrientation = 0

    /**
     * Default constructor automatically called when the fragment is recreated. Required.
     * https://stackoverflow.com/questions/51831053/could-not-find-fragment-constructor
     */
    constructor() {
        // nothing special here
    }

    private constructor(preferredSize: Size, sink: AlprCameraFragmentSink) {
        mPreferredSize = preferredSize
        mSink = sink
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_camera, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

//        vehicleMakeDataItemList = getVehicleMakeMappingList(requireContext())
//        vehicleModelDataItemList = getVehicleModelMappingList(requireContext())
//        vehicleColorDataItemList = getVehicleColorMappingList(requireContext())

        //myDatabase = BaseApplication.instance?.getAppDatabase()

        toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
        fromAction = this.requireArguments().getString(KEY_FROM)
        pathToSaveImage = this.requireArguments().getString(KEY_PATH_TO_SAVE_IMAGE).nullSafety()
        videoStab = this.requireArguments().getBoolean(KEY_IS_VIDEO_STAB)
        lensOpticalStab = this.requireArguments().getBoolean(KEY_IS_LENS_STAB)
        isFlashLightRequired = this.requireArguments().getBoolean(KEY_IS_FLASH_REQUIRED)
        isLandscapeOrientationRequired = this.requireArguments().getBoolean(
            KEY_IS_LANDSCAPE_ORIENTATION_REQUIRED
        )
        llBottom = view.findViewById(R.id.llBottom)
        llBottomPointAndClick = view.findViewById(R.id.llBottomPointAndScan)
        btnStart = view.findViewById(R.id.btnStart)
        btnStop = view.findViewById(R.id.btnStop)
        btnCapture = view.findViewById(R.id.btnCapture)
        ivCameraFrame = view.findViewById(R.id.ivCameraFrame)

        if (fromAction == FROM_CONTINUOUS_VEHICLE_MODE) {
            officerName = this.requireArguments().getString(KEY_OFFICER_NAME)
            badgeId = this.requireArguments().getString(KEY_BADGE_ID)
            beat = this.requireArguments().getString(KEY_BEAT)
            squad = this.requireArguments().getString(KEY_SQUAD)
            block = this.requireArguments().getString(KEY_BLOCK)
            street = this.requireArguments().getString(KEY_STREET)
            side = this.requireArguments().getString(KEY_SIDE)
            timeLimitText = this.requireArguments().getString(KEY_TIME_LIMIT_TEXT)
            timeLimitValue = this.requireArguments().getString(KEY_TIME_LIMIT_VALUE)
            vehicleRunMethod = this.requireArguments().getString(KEY_VEHICLE_RUN_METHOD)
            llBottom.visibility = View.VISIBLE
            btnStop.alpha = 0.5f
            btnStop.isEnabled = false

            ivCameraFrame.visibility = View.GONE
        } else {
            llBottomPointAndClick.visibility = View.VISIBLE
            ivCameraFrame.visibility = View.VISIBLE
            llBottom.visibility = View.GONE
        }
        mGLSurfaceView = view.findViewById(R.id.glSurfaceView)
        mPlateView = view.findViewById(R.id.plateView)
        flParentContainer = view.findViewById(R.id.flParentContainer)

//        CoroutineScope(Dispatchers.IO).launch {
//            recentScanDataModel =
//                myDatabase?.dbDAO?.getContinuousLPRScannedData(block, street, side) as ArrayList<ScanDataModel?>
////            permitList = getPermitList(requireContext())
////            scofflawList = getScofflawList(requireContext())
//        }

        btnStart.setOnClickListener(View.OnClickListener { //Violation beep
            DialogUtils.showConfirmationDialog(context = requireContext(),
                message = getString(
                    R.string.confirmation_msg_do_you_want_to_start_vehicle_mode_scan
                ),
                positiveText = getString(R.string.btn_text_yes),
                negativeText = getString(R.string.btn_text_no),
                callback = { dialog, which ->
                    if (which == DialogInterface.BUTTON_POSITIVE) {
                        //toneGen1.startTone(ToneGenerator.TONE_CDMA_CALL_SIGNAL_ISDN_INTERGROUP,800);

                        //toneGen1.startTone(ToneGenerator.TONE_CDMA_ONE_MIN_BEEP,800);
                        //toneGen1.release();
                        isContinuousScanStarted = true
                        fragmentDataIntercepter?.isContinuousModeStarted(isContinuousScanStarted)

                        showToast(requireContext(), getString(R.string.msg_scan_started))
                        btnStart.alpha = 0.5f
                        btnStart.isEnabled = false
                        btnStop.alpha = 1f
                        btnStop.isEnabled = true
                    }
                })

        })
        btnStop.setOnClickListener(View.OnClickListener {
            if (scanDataModelList.isEmpty()) {
                //Scan Tone
                //toneGen1.startTone(ToneGenerator.TONE_PROP_ACK,800);
                //toneGen1.release();
                showToast(
                    requireContext(),
                    getString(R.string.error_msg_no_vehicle_scanned_yet_please_scan_a_vehicle_to_proceed)
                )
            } else {
                DialogUtils.showConfirmationDialog(context = requireContext(),
                    message = getString(
                        R.string.confirmation_msg_do_you_want_to_stop_vehicle_mode_scan
                    ),
                    positiveText = getString(R.string.btn_text_yes),
                    negativeText = getString(R.string.btn_text_no),
                    callback = { dialog, which ->
                        if (which == DialogInterface.BUTTON_POSITIVE) {
                            isContinuousScanStarted = false
                            fragmentDataIntercepter?.isContinuousModeStarted(isContinuousScanStarted)

                            printLog("==>ScanList:", ObjectMapperProvider.instance.writeValueAsString(scanDataModelList))

//                            val intent = Intent(requireContext(), ScanResultListActivity::class.java)
//                            intent.putParcelableArrayListExtra(KEY_SCAN_RESULT, scanDataModelList)
//                            startActivity(intent)
//                            requireActivity().finishAffinity()
                        }
                    })
            }
        })

        btnCapture.setOnClickListener {
            isPointAndClickStarted = true;
            btnCapture.setText(getString(R.string.btn_capturing));
        }
    }

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

//    return ObjectMapperProvider.fromJson(
//    jsonString,
//    object : TypeReference<List<PlateModel>>() {}
//    ) as? ArrayList<PlateModel>
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

//    return ObjectMapperProvider.fromJson(
//    jsonString,
//    object : TypeReference<List<PlateModel>>() {}
//    ) as? ArrayList<PlateModel>
//    }

    @Synchronized
    override fun onResume() {
        super.onResume()
        startBackgroundThreads()

        // Forward the plateView to the sink
        if (mSink != null && mPlateView != null) {
            mSink!!.setAlprPlateView(mPlateView!!)
        }

        // Open the camera
        openCamera(mGLSurfaceView!!.width, mGLSurfaceView!!.height)
    }

    @Synchronized
    override fun onPause() {
        closeCamera()
        stopBackgroundThreads()
        super.onPause()
    }

    private fun requestCameraPermission() {
        if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
            ConfirmationDialog().show(childFragmentManager, FRAGMENT_DIALOG)
        } else {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.size != 1 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                ErrorDialog.newInstance(getString(R.string.request_permission_for_camera))
                    .show(childFragmentManager, FRAGMENT_DIALOG)
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    /**
     * Shows a [Toast] on the UI thread.
     *
     * @param text The message to show
     */
    private fun showToastOnUiThread(text: String) {
        val activity: Activity? = activity
        activity?.runOnUiThread {
            showToast(activity, text)
        }
    }

    /**
     * Sets up member variables related to camera.
     */
    private fun setUpCameraOutputs() {
        val activity: Activity? = activity
        val manager = activity!!.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            for (cameraId in manager.cameraIdList) {
                val characteristics = manager.getCameraCharacteristics(cameraId)

                // We don't use a front facing camera in this sample.
                val facing = characteristics.get(CameraCharacteristics.LENS_FACING)
                if (facing != null && facing == CameraCharacteristics.LENS_FACING_FRONT) {
                    continue
                }
                val map = characteristics.get(
                    CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP
                ) ?: continue
                mSensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION)!!

                // JPEG orientation
                // https://developer.android.com/reference/android/hardware/camera2/CaptureRequest#JPEG_ORIENTATION
                val rotation = activity.windowManager.defaultDisplay.rotation
                mJpegOrientation = (ORIENTATIONS[rotation] + mSensorOrientation + 270) % 360

                // Danger, W.R.! Attempting to use too large a preview size could  exceed the camera
                // bus' bandwidth limitation, resulting in gorgeous previews but the storage of
                // garbage capture data.
                mPreviewSize = chooseOptimalSize(
                    map.getOutputSizes(SurfaceTexture::class.java),
                    mPreferredSize!!.width,
                    mPreferredSize!!.height
                )


                //TODO Camera
                // We fit the aspect ratio of TextureView to the size of preview we picked.
                val orientation = resources.configuration.orientation
                if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    mGLSurfaceView!!.setAspectRatio(mPreviewSize!!.width, mPreviewSize!!.height)
                    mPlateView!!.setAspectRatio(mPreviewSize!!.width, mPreviewSize!!.height)
                } else {
                    mGLSurfaceView!!.setAspectRatio(mPreviewSize!!.height, mPreviewSize!!.width)
                    mPlateView!!.setAspectRatio(mPreviewSize!!.height, mPreviewSize!!.width)
                }
                //TODO Camera
                mPlateView!!.setVehicleDetailListener(this)
                mCameraId = cameraId
                return
            }
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        } catch (e: NullPointerException) {
            // Currently an NPE is thrown when the Camera2API is used but not supported on the
            // device this code runs.
            ErrorDialog.newInstance(getString(R.string.error_camera_not_supported))
                .show(childFragmentManager, FRAGMENT_DIALOG)
        }
    }

    /**
     * Opens the camera specified by [AlprCameraFragment.mCameraId].
     */
    private fun openCamera(width: Int, height: Int) {
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestCameraPermission()
            return
        }
        setUpCameraOutputs()
        val activity: Activity? = activity
        val manager = activity!!.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            if (!mCameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                throw RuntimeException("Time out waiting to lock camera opening.")
            }
            manager.openCamera(mCameraId!!, mStateCallback, mBackgroundTaskCamera.getHandler())
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            throw RuntimeException("Interrupted while trying to lock camera opening.", e)
        }
    }

    /**
     * Closes the current [CameraDevice].
     */
    private fun closeCamera() {
        try {
            mClosingCamera = true
            mCameraOpenCloseLock.acquire()
            if (null != mCaptureSession) {
                mCaptureSession!!.close()
                mCaptureSession = null
            }
            if (null != mCameraDevice) {
                mCameraDevice!!.close()
                mCameraDevice = null
            }
            if (null != mImageReaderInference) {
                mImageReaderInference!!.close()
                mImageReaderInference = null
            }
            if (null != mImageReaderDrawing) {
                mImageReaderDrawing!!.close()
                mImageReaderDrawing = null
            }
        } catch (e: InterruptedException) {
            throw RuntimeException("Interrupted while trying to lock camera closing.", e)
        } finally {
            mCameraOpenCloseLock.release()
            mClosingCamera = false
        }
    }

    /**
     * Starts a background threads
     */
    private fun startBackgroundThreads() {
        mBackgroundTaskInference.start("InferenceBackgroundThread")
        mBackgroundTaskDrawing.start("DrawingBackgroundThread")
        mBackgroundTaskCamera.start("CameraBackgroundThread")
    }

    /**
     * Stops the background threads
     */
    private fun stopBackgroundThreads() {
        mBackgroundTaskInference.stop()
        mBackgroundTaskDrawing.stop()
        mBackgroundTaskCamera.stop()
    }

    /**
     * Creates a new [CameraCaptureSession] for camera preview.
     */
    private fun createCameraCaptureSession() {
        try {
            // Create Image readers
            mImageReaderInference = ImageReader.newInstance(
                mPreviewSize!!.width, mPreviewSize!!.height,
                VIDEO_FORMAT, MAX_IMAGES
            )
            mImageReaderInference!!.setOnImageAvailableListener(
                mOnImageAvailableListener, mBackgroundTaskCamera.getHandler()
            )
            mImageReaderDrawing = ImageReader.newInstance(
                mPreviewSize!!.width, mPreviewSize!!.height,
                VIDEO_FORMAT, MAX_IMAGES
            )
            mImageReaderDrawing!!.setOnImageAvailableListener(
                mOnImageAvailableListener, mBackgroundTaskCamera.getHandler()
            )

            // We set up a CaptureRequest.Builder with the output Surface to the image reader
            mCaptureRequestBuilder =
                mCameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_RECORD)
            //mCaptureRequestBuilder.set(CaptureRequest.CONTROL_AE_TARGET_FPS_RANGE, new Range<>(1, 25));
            //mCaptureRequestBuilder.set(CaptureRequest.CONTROL_MODE,
            //        CaptureRequest.CONTROL_MODE_USE_SCENE_MODE);
            //mCaptureRequestBuilder.set(CaptureRequest.CONTROL_SCENE_MODE,
            //        CaptureRequest.CONTROL_SCENE_MODE_HIGH_SPEED_VIDEO);
            mCaptureRequestBuilder!!.addTarget(mImageReaderInference!!.surface)
            mCaptureRequestBuilder!!.addTarget(mImageReaderDrawing!!.surface)

            // Here, we create a CameraCaptureSession
            mCameraDevice!!.createCaptureSession(
                Arrays.asList(
                    mImageReaderInference!!.surface, mImageReaderDrawing!!.surface
                ),
                object : CameraCaptureSession.StateCallback() {
                    override fun onConfigured(cameraCaptureSession: CameraCaptureSession) {
                        // The camera is already closed
                        if (null == mCameraDevice) {
                            return
                        }

                        // When the session is ready, we start displaying the preview.
                        mCaptureSession = cameraCaptureSession
                        try {
                            // Auto focus should be continuous
                            mCaptureRequestBuilder!!.set(
                                CaptureRequest.CONTROL_AF_MODE,
                                CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE
                            )

                            if (isFlashLightRequired) {
                                mCaptureRequestBuilder!!.set(
                                    CaptureRequest.FLASH_MODE,
                                    CaptureRequest.FLASH_MODE_TORCH
                                )
                            } else {
                                // Flash is automatically enabled when necessary.
                                mCaptureRequestBuilder!!.set(
                                    CaptureRequest.CONTROL_AE_MODE,
                                    CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH
                                )
                            }

                            if (videoStab) {
                                //To on video stabilization - added by Janak
                                mCaptureRequestBuilder!!.set(
                                    CaptureRequest.CONTROL_VIDEO_STABILIZATION_MODE,
                                    CaptureRequest.CONTROL_VIDEO_STABILIZATION_MODE_ON
                                )
                            }
                            if (lensOpticalStab) {
                                //To on lens optical stabilization - added by Janak
                                mCaptureRequestBuilder!!.set(
                                    CaptureRequest.LENS_OPTICAL_STABILIZATION_MODE,
                                    CaptureRequest.LENS_OPTICAL_STABILIZATION_MODE_ON
                                )
                            }

                            // Finally, we start grabbing the frames
                            mCaptureRequest = mCaptureRequestBuilder!!.build()
                            val captureCallback = SimpleCaptureCallback()
                            mCaptureSession!!.setRepeatingRequest(
                                mCaptureRequest!!,
                                captureCallback, mBackgroundTaskCamera.getHandler()
                            )
                        } catch (e: CameraAccessException) {
                            e.printStackTrace()
                        }
                    }

                    override fun onConfigureFailed(
                        cameraCaptureSession: CameraCaptureSession
                    ) {
                        showToastOnUiThread(requireContext().getString(R.string.error_camera_capture_session_failed))
                    }
                }, mBackgroundTaskCamera.getHandler()
            )
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    inner class SimpleCaptureCallback : CameraCaptureSession.CaptureCallback() {
        override fun onCaptureStarted(
            session: CameraCaptureSession,
            request: CaptureRequest,
            timestamp: Long,
            frameNumber: Long
        ) {
            super.onCaptureStarted(session, request, timestamp, frameNumber)
            printLog("==>Capture Callback:-", "onCaptureStarted")
        }

        override fun onCaptureProgressed(
            session: CameraCaptureSession,
            request: CaptureRequest,
            partialResult: CaptureResult
        ) {
            super.onCaptureProgressed(session, request, partialResult)
            printLog("==>Capture Callback:-", "onCaptureProgressed")
        }

        override fun onCaptureCompleted(
            session: CameraCaptureSession,
            request: CaptureRequest,
            result: TotalCaptureResult
        ) {
            // super.onCaptureCompleted(session, request, result);
            printLog("==>Capture Callback:-", "onCaptureCompleted")

//            Image image = imageReaderAvailable.acquireLatestImage();
//            ByteBuffer buffer = image.getPlanes()[0].getBuffer();
//            byte[] bytes = new byte[buffer.remaining()];
//            buffer.get(bytes);
//            Bitmap myBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, null);
//            ivLicensePlate.setImageBitmap(myBitmap);
////            SetBitmap setBitmap = new SetBitmap();
////            setBitmap.setMbitmap(myBitmap);
//            image.close();
        }

    }

    private var isNumberReceived = false
    override fun getLicensePlateNumber(licensePlateNumber: String?) {
        //Replacing * from plate number because it is creating problem while creating file
        this.licensePlateNumber = licensePlateNumber?.replace("*", "")
        isNumberReceived = true
    }

    override fun getVehicleMakeBrand(vehicleMakeBrand: String?) {
        this.vehicleMakeBrand = vehicleMakeDataItemList?.firstOrNull {
            it.vehicleMake.contains(
                vehicleMakeBrand?.split("\\s".toRegex())?.firstOrNull().toString(), true
            )
        }?.vehicleMake.nullSafety(vehicleMakeBrand.nullSafety())
        //this.vehicleMakeBrand = vehicleMakeBrand
    }

    override fun getVehicleModel(vehicleModel: String?) {
        //this.vehicleModel = vehicleModel
        this.vehicleModel = vehicleModelDataItemList?.firstOrNull {
            it.vehicleModel.contains(
                vehicleModel?.split("\\s".toRegex())?.firstOrNull().toString(), true
            )
        }?.vehicleModel.nullSafety(vehicleModel.nullSafety())
    }

    override fun getVehicleColor(vehicleColor: String?) {
        if (vehicleColor != null) {
            this.vehicleColor = vehicleColorDataItemList?.firstOrNull {
                vehicleColor.contains(
                    it.vehicleColor,
                    true
                )
            }?.vehicleColor.nullSafety(vehicleColor)
            // this.vehicleColor = vehicleColor
        }
    }

    override fun getVehicleBodyStyle(vehicleBodyStyle: String?) {
        if (vehicleBodyStyle != null) {
            this.vehicleBodyStyle = vehicleBodyStyle
        }
    }

    override fun getLicensePlateCountry(licensePlateCountry: String?) {
        if (licensePlateCountry != null) {
            this.licensePlateCountry = licensePlateCountry
        }
    }

    override fun getLicensePlateState(licensePlateState: String?) {
        if (licensePlateState != null) {
            this.licensePlateState = licensePlateState
        }
    }

    private fun getVehicleMakeMappingList(context: Context): ArrayList<VehicleMakeDataItem>? {
        lateinit var jsonString: String
        try {
            jsonString = context.assets.open("vehicle_make.json")
                .bufferedReader()
                .use { it.readText() }
        } catch (ioException: IOException) {
            printLog("==>Exception", ioException.message.nullSafety())
        }

        return ObjectMapperProvider.fromJson(
            jsonString,
            object : TypeReference<List<VehicleMakeDataItem>>() {}
        ) as? ArrayList<VehicleMakeDataItem>
    }

    private fun getVehicleModelMappingList(context: Context): ArrayList<VehicleModelDataItem>? {
        lateinit var jsonString: String
        try {
            jsonString = context.assets.open("vehicle_model.json")
                .bufferedReader()
                .use { it.readText() }
        } catch (ioException: IOException) {
            printLog("==>Exception", ioException.message.nullSafety())
        }

        return ObjectMapperProvider.fromJson(
            jsonString,
            object : TypeReference<List<VehicleModelDataItem>>() {}
        ) as? ArrayList<VehicleModelDataItem>
    }

    private fun getVehicleColorMappingList(context: Context): ArrayList<VehicleColorDataItem>? {
        lateinit var jsonString: String
        try {
            jsonString = context.assets.open("vehicle_color.json")
                .bufferedReader()
                .use { it.readText() }
        } catch (ioException: IOException) {
            printLog("==>Exception", ioException.message.nullSafety())
        }

        return ObjectMapperProvider.fromJson(
            jsonString,
            object : TypeReference<List<VehicleColorDataItem>>() {}
        ) as? ArrayList<VehicleColorDataItem>
    }

    private fun pointAndScanResult() {
//        if (licensePlateNumber.nullSafety().isNotEmpty() &&
//            vehicleMakeBrand.nullSafety().isNotEmpty() &&
//            vehicleModel.nullSafety().isNotEmpty() &&
//            vehicleColor.nullSafety().isNotEmpty() &&
//            !isPointAndScanned
//        ) {
//            isPointAndScanned = true
//            toneGenerator?.startTone(ToneGenerator.TONE_PROP_ACK, 1000)
//
//            fragmentDataIntercepter?.vehicleDetailsInSingleShotMode(
//                licensePlateNumber,
//                vehicleMakeBrand,
//                vehicleModel,
//                vehicleColor,
//                vehicleBodyStyle,
//                licensePlateCountry,
//                licensePlateState,
//                createAndSaveScannedImage(vehicleBitmapImage, licensePlateNumber )
//            )
//        } else{
//            Log.e("PLATE:==>","Not found");
//        }

        if (licensePlateNumber.nullSafety().isNotEmpty() &&

            !isPointAndScanned
        ) {
            isPointAndScanned = true
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_ACK, 1000)

            vehicleBitmapImage = mGLSurfaceView?.getVehicleImage()
            vehicleBitmapImage = getResizedBitmap(fromAction, vehicleBitmapImage!!, 1100)

            printLog("PLATE:==>", "FOund plate: licensePlateNumber");

            fragmentDataIntercepter?.vehicleDetailsInSingleShotMode(
                licensePlateNumber,
                vehicleMakeBrand,
                vehicleModel,
                vehicleColor,
                vehicleBodyStyle,
                licensePlateCountry,
                licensePlateState,
                createAndSaveScannedImage(vehicleBitmapImage, licensePlateNumber, pathToSaveImage)
            )
        } else {
            printLog("PLATE:==>", "Not found");
        }
    }

    private fun setScannedDataInList(
        mNumberPlate: String?,
        mMake: String?,
        mModel: String?,
        mColor: String?,
        mFinalBitmap: Bitmap?
    ) {
//        if (mNumberPlate.nullSafety().isNotEmpty() && mMake.nullSafety()
//                .isNotEmpty() && mModel.nullSafety()
//                .isNotEmpty() && mColor.nullSafety().isNotEmpty()
//        ) {
//            printLog("==>isPlate:", "tempPlate-" + mNumberPlate)
//            printLog("==>isPlate:", "tempMake-" + mMake)
//
//            if (mNumberPlate?.length.nullSafety() > 3 && !isPlateAlreadyAdded(
//                    mNumberPlate,
//                    mMake
//                )
//            ) {
//                vehicleBitmapImage = mGLSurfaceView?.getVehicleImage()
//                vehicleBitmapImage = getResizedBitmap(vehicleBitmapImage!!,1100)
//               // vehicleBitmapImage = BitmapUtils.getCompressedBitmap(vehicleBitmapImage)
//
//                //Scan Tone
//                toneGenerator?.startTone(ToneGenerator.TONE_PROP_ACK, 1000)
//
//                printLog("==>Scanned:", "Start Tone")
//
//                val timestamp = System.currentTimeMillis()
//                val vehicleMarkTime = SDF_TIME_12_HOURS.format(timestamp)
//                val markTimingTimestamp = SDF_TIMESTAMP_UTC.format(timestamp)
//                val scanDataModel = ScanDataModel()
//                scanDataModel.vehicleImageURL = createAndSaveScannedImage(vehicleBitmapImage, mNumberPlate)
//               // val compressedImageFile = Compressor.compress(context, actualImageFile)
//                //scanDataModel.vehicleImageURL = createAndSaveScannedImage(mFinalBitmap)
//                scanDataModel.licensePlateNumber = mNumberPlate
//                scanDataModel.vehicleMakeBrand = mMake
//                scanDataModel.vehicleModel = mModel
//                scanDataModel.vehicleColor = mColor
//                scanDataModel.licensePlateState = licensePlateState
//                scanDataModel.officerName = officerName
//                scanDataModel.badgeId = badgeId
//                scanDataModel.beat = beat
//                scanDataModel.squad = squad
//                scanDataModel.block = block
//                scanDataModel.street = street
//                scanDataModel.streetSide = side
//                scanDataModel.timeLimitText = timeLimitText
//                scanDataModel.timeLimitValue = timeLimitValue
//                scanDataModel.date = currentDate
//                scanDataModel.time = vehicleMarkTime
//                scanDataModel.timestamp = timestamp
//                scanDataModel.markTimingTimestamp = markTimingTimestamp
//                scanDataModel.vehicleRunStartTime = vehicleRunStartTime
//                scanDataModel.latitude = sharedPreference.read(SharedPrefKey.SHARED_PREF_LAT, "0.0").nullSafety().toDouble()
//                scanDataModel.longitude = sharedPreference.read(SharedPrefKey.SHARED_PREF_LONG, "0.0").nullSafety().toDouble()
//
//                if (isTimingViolation(mNumberPlate, timestamp)) {
//                    scanDataModel.violationExists = 1
//                    //Scan Tone
//                    CoroutineScope(Dispatchers.IO).launch {
//                        delay(1000)
//                        toneGenerator?.startTone(ToneGenerator.TONE_SUP_ERROR, 1000)
//                    }
//                    printLog("==>Scanned:", "timing Violation")
//                } else {
//                    scanDataModel.violationExists = 0
//                }
//
//                scanDataModel.scofflawExists = 0
//                scanDataModel.permitExists = 0
//                scanDataModel.paymentExists = 0
//                scanDataModel.vehicleRunType = 1
//
//                scanDataModel.vehicleRunMethod = vehicleRunMethod
//
////                if (isScofflowPlate(mNumberPlate)) {
////                    scanDataModel.scofflaw = 1
////                    CoroutineScope(Dispatchers.IO).launch {
////                        delay(2000)
////                        //Voilation high sound
////                        toneGenerator?.startTone(ToneGenerator.TONE_CDMA_HIGH_L, 1000)
////                    }
////                    showILog("==>Scanned:", "Scofflaw")
////
////                } else {
////                    scanDataModel.scofflaw = 0
////                }
////
////                if (isPermitListPlate(mNumberPlate)) {
////                    scanDataModel.permitList = 1
////                    CoroutineScope(Dispatchers.IO).launch {
////                        delay(3000)
////                        //Scan Tone
////                        toneGenerator?.startTone(ToneGenerator.TONE_SUP_INTERCEPT, 1000)
////                    }
////                    showILog("==>Scanned:", "permit list")
////                } else {
////                    scanDataModel.permitList = 0
////                }
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
//            mGLSurfaceView?.resetVehicleImage()
//        }

    }

    private fun isPlateAlreadyAdded(lprNumber: String?, make: String?): Boolean {
        val tempList =
            scanDataModelList.filter { it?.licensePlateNumber == lprNumber && it?.vehicleMakeBrand == make }
        return tempList.isNotEmpty()
    }

    private fun isTimingViolation(numberPlate: String?, currentTimestamp: Long): Boolean {
        val tempList = recentScanDataModel.filter {
            it?.licensePlateNumber == numberPlate
                    && it?.block == block
                    && it?.street == street
                    && it?.streetSide == side
                    && differenceInMinutes(
                it?.timestamp,
                currentTimestamp
            ) > it?.timeLimitValue.nullSafety("0").toInt()
        }
        return tempList.isNotEmpty()
    }

//    private fun isScofflowPlate(numberPlate: String?): Boolean {
//        val tempList = scofflawList.filter { it?.licensePlateNumber == numberPlate }
//        return tempList.isNotEmpty()
//    }
//
//    private fun isPermitListPlate(numberPlate: String?): Boolean {
//        val tempList = permitList.filter { it?.licensePlateNumber == numberPlate }
//        return tempList.isNotEmpty()
//    }

    private fun differenceInMinutes(oldTimeStamp: Long?, currentTimeStamp: Long): Int {
        val diff: Long = currentTimeStamp.nullSafety() - oldTimeStamp.nullSafety()
        val seconds = diff / 1000
        return (seconds / 60).toInt()
//        val hours = minutes / 60
//        val days = hours / 24
    }

    /**
     *
     */
    interface AlprCameraFragmentSink {
        /**
         * @param view
         */
        fun setAlprPlateView(view: AlprPlateView)

        /**
         * @param image
         * @param jpegOrientation
         */
        fun setImage(image: Image, jpegOrientation: Int)
    }

    /**
     * Compares two `Size`s based on their areas.
     */
    internal class CompareSizesByArea : Comparator<Size> {
        override fun compare(lhs: Size, rhs: Size): Int {
            // We cast here to ensure the multiplications won't overflow
            return java.lang.Long.signum(
                lhs.width.toLong() * lhs.height -
                        rhs.width.toLong() * rhs.height
            )
        }
    }

    /**
     * Shows an error message dialog.
     */
    class ErrorDialog : DialogFragment() {
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val activity: Activity? = activity
            return AlertDialog.Builder(activity)
                .setMessage(requireArguments().getString(ARG_MESSAGE))
                .setPositiveButton(android.R.string.ok) { dialogInterface, i -> activity!!.finish() }
                .create()
        }

        companion object {
            private const val ARG_MESSAGE = "message"
            fun newInstance(message: String?): ErrorDialog {
                val dialog = ErrorDialog()
                val args = Bundle()
                args.putString(ARG_MESSAGE, message)
                dialog.arguments = args
                return dialog
            }
        }
    }

    /**
     * Shows OK/Cancel confirmation dialog about camera permission.
     */
    class ConfirmationDialog : DialogFragment() {
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val parent = parentFragment
            return AlertDialog.Builder(activity)
                .setMessage(R.string.request_permission_for_camera)
                .setPositiveButton(android.R.string.ok) { dialog, which ->
                    parent!!.requestPermissions(
                        arrayOf(Manifest.permission.CAMERA),
                        REQUEST_CAMERA_PERMISSION
                    )
                }
                .setNegativeButton(
                    android.R.string.cancel
                ) { dialog, which ->
                    val activity: Activity? = parent!!.activity
                    activity?.finish()
                }
                .create()
        }
    }

    companion object {
        const val REQUEST_CAMERA_PERMISSION = 1
        const val FRAGMENT_DIALOG = "dialog"
        val TAG = AlprCameraFragment::class.java.canonicalName
        const val VIDEO_FORMAT =
            ImageFormat.YUV_420_888 // All Android devices are required to support this format
        private val ORIENTATIONS = SparseIntArray()

        /**
         * Using #2: processing and pending.
         */
        const val MAX_IMAGES = 2

        /**
         * The camera preview size will be chosen to be the smallest frame by pixel size capable of
         * containing a DESIRED_SIZE x DESIRED_SIZE square.
         */
        //static final int MINIMUM_PREVIEW_SIZE = 320;
        const val MINIMUM_PREVIEW_SIZE = 320

        /**
         * Public function to be called to create the fragment.
         *
         * @param preferredSize
         * @return
         */
        fun newInstance(preferredSize: Size, sink: AlprCameraFragmentSink): AlprCameraFragment {
            return AlprCameraFragment(preferredSize, sink)
        }

        /**
         * Given `choices` of `Size`s supported by a camera, chooses the smallest one whose
         * width and height are at least as large as the minimum of both, or an exact match if possible.
         *
         * @param choices The list of sizes that the camera supports for the intended output class
         * @param width   The minimum desired width
         * @param height  The minimum desired height
         * @return The optimal `Size`, or an arbitrary one if none were big enough
         */
        private fun chooseOptimalSize(choices: Array<Size>, width: Int, height: Int): Size? {
            val minSize = Math.max(Math.min(width, height), MINIMUM_PREVIEW_SIZE)
            val desiredSize = Size(width, height)

            // Collect the supported resolutions that are at least as big as the preview Surface
            var exactSizeFound = false
            val bigEnough: MutableList<Size?> = ArrayList()
            val tooSmall: MutableList<Size?> = ArrayList()
            for (option in choices) {
                if (option == desiredSize) {
                    // Set the size but don't return yet so that remaining sizes will still be logged.
                    exactSizeFound = true
                }
                if (option.height >= minSize && option.width >= minSize) {
                    bigEnough.add(option)
                } else {
                    tooSmall.add(option)
                }
            }

            if (exactSizeFound) {
                return desiredSize
            }

            // Pick the smallest of those, assuming we found any
            return if (bigEnough.size > 0) {
                val chosenSize =
                    Collections.min(bigEnough, CompareSizesByArea() as Comparator<in Size?>)
                chosenSize
            } else {
                choices[0]
            }
        }

        init {
            ORIENTATIONS.append(Surface.ROTATION_0, 90)
            ORIENTATIONS.append(Surface.ROTATION_90, 0)
            ORIENTATIONS.append(Surface.ROTATION_180, 270)
            ORIENTATIONS.append(Surface.ROTATION_270, 180)
        }
    }
}

