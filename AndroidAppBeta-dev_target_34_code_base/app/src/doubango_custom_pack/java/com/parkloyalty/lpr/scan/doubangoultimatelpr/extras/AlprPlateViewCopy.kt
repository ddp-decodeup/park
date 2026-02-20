//package com.parkloyalty.lpr.scan.doubangoultimatelpr.extras
//
//import android.content.Context
//import android.graphics.*
//import android.media.Image
//import android.renderscript.*
//import android.util.AttributeSet
//import android.util.Size
//import android.util.TypedValue
//import android.view.View
//import com.parkloyalty.lpr.vehiclemode.doubangoultimatelpr.utils.AlprUtils
//import com.parkloyalty.lpr.vehiclemode.doubangoultimatelpr.interfaces.VehicleDetailListener
//import com.parkloyalty.lpr.vehiclemode.extensions.nullSafety
//import com.parkloyalty.lpr.vehiclemode.extensions.showILog
//import org.doubango.ultimateAlpr.Sdk.UltAlprSdkResult
//
//class AlprPlateViewCopy(private val mContext: Context, attrs: AttributeSet?) : View(
//    mContext, attrs
//) {
//    private val mPaintTextNumber: Paint
//    private val mPaintTextNumberBackground: Paint
//    private val mPaintTextLPCI: Paint
//    private val mPaintTextLPCIBackground: Paint
//    private val mPaintTextCar: Paint
//    private val mPaintTextCarBackground: Paint
//    private val mPaintBorder: Paint
//    private val mPaintTextDurationTime: Paint
//    private val mPaintTextDurationTimeBackground: Paint
//    private val mPaintDetectROI: Paint
//    private var mRatioWidth = 0
//    private var mRatioHeight = 0
//    private var mOrientation = 0
//    private var mDurationTimeMillis: Long = 0
//    private var mImageSize: Size? = null
//    private var mPlates: List<AlprUtils.Plate?>? = null
//    private var mDetectROI: RectF? = null
//    private var mImage: Image? = null
//    private var vehicleDetailListener: VehicleDetailListener? = null
//    private val finalBitmap: Bitmap? = null
//    fun setDetectROI(roi: RectF?) {
//        mDetectROI = roi
//    }
//
//    /**
//     *
//     * @param width
//     * @param height
//     */
//    fun setAspectRatio(width: Int, height: Int) {
//        require(!(width < 0 || height < 0)) { "Size cannot be negative." }
//        mRatioWidth = width
//        mRatioHeight = height
//        requestLayout()
//    }
//
//    fun setVehicleDetailListener(vehicleDetailListener: VehicleDetailListener?) {
//        this.vehicleDetailListener = vehicleDetailListener
//    }
//
//    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
//        showILog(TAG, "onMeasure")
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
//        val width = MeasureSpec.getSize(widthMeasureSpec)
//        val height = MeasureSpec.getSize(heightMeasureSpec)
//        if (0 == mRatioWidth || 0 == mRatioHeight) {
//            setMeasuredDimension(width, height)
//        } else {
//            if (width < height * mRatioWidth / mRatioHeight) {
//                setMeasuredDimension(width, width * mRatioHeight / mRatioWidth)
//            } else {
//                setMeasuredDimension(height * mRatioWidth / mRatioHeight, height)
//            }
//        }
//    }
//
//    /**
//     *
//     * @param result
//     * @param imageSize
//     */
//    @Synchronized
//    fun setResult(result: UltAlprSdkResult, imageSize: Size, durationTime: Long, orientation: Int) {
//        mPlates = AlprUtils.extractPlates(result)
//        mImageSize = imageSize
//        mDurationTimeMillis = durationTime
//        mOrientation = orientation
//        postInvalidate()
//    }
//
//    /**
//     *
//     * @param result
//     * @param imageSize
//     */
//    @Synchronized
//    fun setResult(
//        image: Image?,
//        result: UltAlprSdkResult,
//        imageSize: Size,
//        durationTime: Long,
//        orientation: Int
//    ) {
//        mPlates = AlprUtils.extractPlates(result)
//        mImage = image
//        //finalBitmap = yuv420ToBitmap(image, mContext);
//        mImageSize = imageSize
//        mDurationTimeMillis = durationTime
//        mOrientation = orientation
//        postInvalidate()
//    }
//
//    private fun yuv420ToBitmap(image: Image, context: Context): Bitmap {
//        val rs = RenderScript.create(context)
//        val script = ScriptIntrinsicYuvToRGB.create(rs, Element.U8_4(rs))
//
//        // Refer the logic in a section below on how to convert a YUV_420_888 image
//        // to single channel flat 1D array. For sake of this example I'll abstract it
//        // as a method.
//        val yuvByteArray = image2byteArray(image)
//        val yuvType = Type.Builder(rs, Element.U8(rs)).setX(yuvByteArray.size)
//        val `in` = Allocation.createTyped(rs, yuvType.create(), Allocation.USAGE_SCRIPT)
//        val rgbaType = Type.Builder(rs, Element.RGBA_8888(rs))
//            .setX(image.width)
//            .setY(image.height)
//        val out = Allocation.createTyped(rs, rgbaType.create(), Allocation.USAGE_SCRIPT)
//
//        // The allocations above "should" be cached if you are going to perform
//        // repeated conversion of YUV_420_888 to Bitmap.
//        `in`.copyFrom(yuvByteArray)
//        script.setInput(`in`)
//        script.forEach(out)
//        val bitmap = Bitmap.createBitmap(image.width, image.height, Bitmap.Config.ARGB_8888)
//        out.copyTo(bitmap)
//        return bitmap
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
//    @Synchronized
//    override fun draw(canvas: Canvas) {
//        super.draw(canvas)
//        if (mImageSize == null) {
//            showILog(TAG, "Not initialized yet")
//            return
//        }
//
//        // Inference time
//        // Landscape faster: https://www.doubango.org/SDKs/anpr/docs/Improving_the_speed.html#landscape-mode
//        val mInferenceTimeMillisString =
//            "Total processing time: " + mDurationTimeMillis + if (mOrientation == 0) "" else " -> Rotate to landscape to speedup"
//        val boundsTextmInferenceTimeMillis = Rect()
//        mPaintTextDurationTime.getTextBounds(
//            mInferenceTimeMillisString,
//            0,
//            mInferenceTimeMillisString.length,
//            boundsTextmInferenceTimeMillis
//        )
//        canvas.drawRect(
//            0f,
//            0f,
//            boundsTextmInferenceTimeMillis.width().toFloat(),
//            boundsTextmInferenceTimeMillis.height().toFloat(),
//            mPaintTextDurationTimeBackground
//        )
//        canvas.drawText(
//            mInferenceTimeMillisString,
//            0f,
//            boundsTextmInferenceTimeMillis.height().toFloat(),
//            mPaintTextDurationTime
//        )
//
//        // Transformation info
//        val tInfo =
//            AlprUtils.AlprTransformationInfo(mImageSize!!.width, mImageSize!!.height, width, height)
//
//        // ROI
//        if (mDetectROI != null && !mDetectROI!!.isEmpty) {
//            canvas.drawRect(
//                RectF(
//                    tInfo.transformX(mDetectROI!!.left),
//                    tInfo.transformY(mDetectROI!!.top),
//                    tInfo.transformX(mDetectROI!!.right),
//                    tInfo.transformY(mDetectROI!!.bottom)
//                ),
//                mPaintDetectROI
//            )
//        }
//
//        // Plates
//        if (mPlates != null && !mPlates!!.isEmpty()) {
//            for (plate in mPlates!!) {
//                // Transform corners
//                val plateWarpedBox = plate?.warpedBox
//                val plateCornerA = PointF(
//                    tInfo.transformX(
//                        plateWarpedBox!![0]
//                    ), tInfo.transformY(plateWarpedBox[1])
//                )
//                val plateCornerB = PointF(
//                    tInfo.transformX(
//                        plateWarpedBox[2]
//                    ), tInfo.transformY(plateWarpedBox[3])
//                )
//                val plateCornerC = PointF(
//                    tInfo.transformX(
//                        plateWarpedBox[4]
//                    ), tInfo.transformY(plateWarpedBox[5])
//                )
//                val plateCornerD = PointF(
//                    tInfo.transformX(
//                        plateWarpedBox[6]
//                    ), tInfo.transformY(plateWarpedBox[7])
//                )
//                // Draw border
//                val platePathBorder = Path()
//                platePathBorder.moveTo(plateCornerA.x, plateCornerA.y)
//                platePathBorder.lineTo(plateCornerB.x, plateCornerB.y)
//                platePathBorder.lineTo(plateCornerC.x, plateCornerC.y)
//                platePathBorder.lineTo(plateCornerD.x, plateCornerD.y)
//                platePathBorder.lineTo(plateCornerA.x, plateCornerA.y)
//                platePathBorder.close()
//                mPaintBorder.color = mPaintTextNumberBackground.color
//                canvas.drawPath(platePathBorder, mPaintBorder)
//
//                // Draw text number
//                val number = plate.number
//                if (number != null && !number.isEmpty()) {
//                    vehicleDetailListener?.getLicensePlateNumber(number)
//                    val boundsTextNumber = Rect()
//                    mPaintTextNumber.getTextBounds(number, 0, number.length, boundsTextNumber)
//                    val rectTextNumber = RectF(
//                        plateCornerA.x,
//                        plateCornerA.y - boundsTextNumber.height(),
//                        plateCornerA.x + boundsTextNumber.width(),
//                        plateCornerA.y
//                    )
//                    val pathTextNumber = Path()
//                    pathTextNumber.moveTo(plateCornerA.x, plateCornerA.y)
//                    pathTextNumber.lineTo(
//                        Math.max(
//                            plateCornerB.x,
//                            plateCornerA.x + rectTextNumber.width()
//                        ), plateCornerB.y
//                    )
//                    pathTextNumber.addRect(rectTextNumber, Path.Direction.CCW)
//                    pathTextNumber.close()
//                    canvas.drawPath(pathTextNumber, mPaintTextNumberBackground)
//                    canvas.drawTextOnPath(number, pathTextNumber, 0f, 0f, mPaintTextNumber)
//                }
//
//                // Draw Car
//                if (plate.car != null) {
//                    val car = plate.car
//                    if (car?.confidence.nullSafety() >= 80f) {
//                        // Vehicle Color Recognition [VCR] (added in 3.0.0) : https://www.doubango.org/SDKs/anpr/docs/Features.html#vehicle-color-recognition-vcr
//                        var color: String? = null
//                        if (car?.colors != null) {
//                            val colorObj0 = car.colors!![0] // sorted, most higher confidence first
//                            if (colorObj0.confidence >= VCR_MIN_CONFIDENCE) {
//                                color = colorObj0.name
//                            } else if (car.colors?.size.nullSafety() >= 2) {
//                                // Color fusion: https://www.doubango.org/SDKs/anpr/docs/Improving_the_accuracy.html#fuse
//                                val colorObj1 = car.colors!![1]
//                                val colorMix = colorObj0.name + "/" + colorObj1.name
//                                var confidence = colorObj0.confidence
//                                if ("white/silver,silver/white,gray/silver,silver/gray".indexOf(
//                                        colorMix
//                                    ) != -1
//                                ) {
//                                    confidence += colorObj1.confidence
//                                }
//                                if (confidence >= VCR_MIN_CONFIDENCE) {
//                                    color =
//                                        if (colorMix.indexOf("white") == -1) "DarkSilver" else "LightSilver"
//                                    confidence =
//                                        Math.max(colorObj0.confidence, colorObj1.confidence)
//                                }
//                            }
//                        }
//                        vehicleDetailListener?.getVehicleColor(color)
//
//                        // Vehicle Make Model Recognition [VMMR] (added in 3.0.0): https://www.doubango.org/SDKs/anpr/docs/Features.html#vehicle-make-model-recognition-vmmr
//                        var make: String? = null
//                        var model: String? = null
//                        if (car?.makesModelsYears != null) {
//                            val makesModelsYears = car.makesModelsYears
//                            val makeModelYear =
//                                makesModelsYears!![0] // sorted, most higher confidence first
//                            if (makeModelYear.confidence >= VMMR_MIN_CONFIDENCE) {
//                                make = makeModelYear.make
//                                model = makeModelYear.model
//                            } else {
//                                // Fuse and defuse: https://www.doubango.org/SDKs/anpr/docs/Improving_the_accuracy.html#fuse-and-defuse
//                                val makes: MutableMap<String?, Float> = HashMap()
//                                val occurrences: MutableMap<String?, Int> = HashMap()
//                                // Fuse makes
//                                for (mmy in makesModelsYears) {
//                                    makes[mmy.make] = AlprUtils.getOrDefault(
//                                        makes,
//                                        mmy.make,
//                                        0f
//                                    ) + mmy.confidence // Map.getOrDefault requires API level 24
//                                    occurrences[mmy.make] = AlprUtils.getOrDefault(
//                                        occurrences,
//                                        mmy.make,
//                                        0
//                                    ) + 1 // Map.getOrDefault requires API level 24
//                                }
//                                // Find make with highest confidence. Stream requires Java8
//                                val itMake: Iterator<Map.Entry<String?, Float>> =
//                                    makes.entries.iterator()
//                                var bestMake = itMake.next()
//                                while (itMake.hasNext()) {
//                                    val makeE = itMake.next()
//                                    if (makeE.value > bestMake.value) {
//                                        bestMake = makeE
//                                    }
//                                }
//                                // Model fusion
//                                if (bestMake.value >= VMMR_MIN_CONFIDENCE || occurrences[bestMake.key]!! >= VMMR_FUSE_DEFUSE_MIN_OCCURRENCES && bestMake.value >= VMMR_FUSE_DEFUSE_MIN_CONFIDENCE) {
//                                    make = bestMake.key
//
//                                    // Fuse models
//                                    val models: MutableMap<String?, Float> = HashMap()
//                                    for (mmy in makesModelsYears) {
//                                        if (make == mmy.make) {
//                                            models[mmy.model] = AlprUtils.getOrDefault(
//                                                models,
//                                                mmy.model,
//                                                0f
//                                            ) + mmy.confidence // Map.getOrDefault requires API level 24
//                                        }
//                                    }
//                                    // Find model with highest confidence. Stream requires Java8
//                                    val itModel: Iterator<Map.Entry<String?, Float>> =
//                                        models.entries.iterator()
//                                    var bestModel = itModel.next()
//                                    while (itModel.hasNext()) {
//                                        val modelE = itModel.next()
//                                        if (modelE.value > bestModel.value) {
//                                            bestModel = modelE
//                                        }
//                                    }
//                                    model = bestModel.key
//                                }
//                            }
//                        }
//                        vehicleDetailListener?.getVehicleMakeBrand(make)
//                        vehicleDetailListener?.getVehicleModel(model)
//
//                        // Vehicle Body Style Recognition [VBSR] (added in 3.2.0): https://www.doubango.org/SDKs/anpr/docs/Features.html#features-vehiclebodystylerecognition
//                        var bodyStyle: String? = null
//                        if (car?.bodyStyles != null) {
//                            val vbsr = car.bodyStyles!![0] // sorted, most higher confidence first
//                            if (vbsr.confidence >= VBSR_MIN_CONFIDENCE) {
//                                bodyStyle = vbsr.name
//                            }
//                        }
//                        vehicleDetailListener?.getVehicleBodyStyle(bodyStyle)
//
//
//                        // Transform corners
//                        val carWarpedBox = car?.warpedBox
//                        val carCornerA = PointF(
//                            tInfo.transformX(
//                                carWarpedBox!![0]
//                            ), tInfo.transformY(carWarpedBox[1])
//                        )
//                        val carCornerB = PointF(
//                            tInfo.transformX(
//                                carWarpedBox[2]
//                            ), tInfo.transformY(carWarpedBox[3])
//                        )
//                        val carCornerC = PointF(
//                            tInfo.transformX(
//                                carWarpedBox[4]
//                            ), tInfo.transformY(carWarpedBox[5])
//                        )
//                        val carCornerD = PointF(
//                            tInfo.transformX(
//                                carWarpedBox[6]
//                            ), tInfo.transformY(carWarpedBox[7])
//                        )
//                        // Draw border
//                        val carPathBorder = Path()
//                        carPathBorder.moveTo(carCornerA.x, carCornerA.y)
//                        carPathBorder.lineTo(carCornerB.x, carCornerB.y)
//                        carPathBorder.lineTo(carCornerC.x, carCornerC.y)
//                        carPathBorder.lineTo(carCornerD.x, carCornerD.y)
//                        carPathBorder.lineTo(carCornerA.x, carCornerA.y)
//                        carPathBorder.close()
//                        mPaintBorder.color = mPaintTextCarBackground.color
//                        canvas.drawPath(carPathBorder, mPaintBorder)
//
//                        // Draw car information
//                        val carText = String.format(
//                            "%s%s%s%s",
//                            make ?: "Car",
//                            if (model != null) ", $model" else "",
//                            if (color != null) ", $color" else "",
//                            if (bodyStyle != null) ", $bodyStyle" else ""
//                        )
//                        val boundsTextCar = Rect()
//                        mPaintTextCar.getTextBounds(carText, 0, carText.length, boundsTextCar)
//                        val rectTextCar = RectF(
//                            carCornerA.x,
//                            carCornerA.y - boundsTextCar.height(),
//                            carCornerA.x + boundsTextCar.width(),
//                            carCornerA.y
//                        )
//                        val pathTextCar = Path()
//                        pathTextCar.moveTo(carCornerA.x, carCornerA.y)
//                        pathTextCar.lineTo(
//                            Math.max(
//                                carCornerB.x,
//                                carCornerA.x + rectTextCar.width()
//                            ), carCornerB.y
//                        )
//                        pathTextCar.addRect(rectTextCar, Path.Direction.CCW)
//                        pathTextCar.close()
//                        canvas.drawPath(pathTextCar, mPaintTextCarBackground)
//                        canvas.drawTextOnPath(carText, pathTextCar, 0f, 0f, mPaintTextCar)
//                    }
//                }
//
//                // License Plate Country Identification [LPCI] (Added in 3.0.0): https://www.doubango.org/SDKs/anpr/docs/Features.html#license-plate-country-identification-lpci
//                if (plate.countries != null) {
//                    val country = plate.countries!![0] // sorted, most higher confidence first
//                    if (country.confidence >= LPCI_MIN_CONFIDENCE) {
//                        //Start of State
//                        vehicleDetailListener?.getLicensePlateState(country.state)
//                        //End of State by Janak
//                        val countryString = country.code
//                        val boundsConfidenceLPCI = Rect()
//                        mPaintTextLPCI.getTextBounds(
//                            countryString,
//                            0,
//                            countryString!!.length,
//                            boundsConfidenceLPCI
//                        )
//                        val rectTextLPCI = RectF(
//                            plateCornerD.x,
//                            plateCornerD.y,
//                            plateCornerD.x + boundsConfidenceLPCI.width(),
//                            plateCornerD.y + boundsConfidenceLPCI.height()
//                        )
//                        val pathTextLPCI = Path()
//                        val dx = (plateCornerC.x - plateCornerD.x).toDouble()
//                        val dy = (plateCornerC.y - plateCornerD.y).toDouble()
//                        val angle = Math.atan2(dy, dx)
//                        val cosT = Math.cos(angle)
//                        val sinT = Math.sin(angle)
//                        val Cx = plateCornerD.x + rectTextLPCI.width()
//                        val Cy = plateCornerC.y
//                        val cornerCC = PointF(
//                            (Cx * cosT - Cy * sinT).toFloat(),
//                            (Cy * cosT + Cx * sinT).toFloat()
//                        )
//                        val cornerDD = PointF(
//                            (plateCornerD.x * cosT - plateCornerD.y * sinT).toFloat(),
//                            (plateCornerD.y * cosT + plateCornerD.x * sinT).toFloat()
//                        )
//                        pathTextLPCI.moveTo(cornerDD.x, cornerDD.y + boundsConfidenceLPCI.height())
//                        pathTextLPCI.lineTo(cornerCC.x, cornerCC.y + boundsConfidenceLPCI.height())
//                        pathTextLPCI.addRect(rectTextLPCI, Path.Direction.CCW)
//                        pathTextLPCI.close()
//                        canvas.drawPath(pathTextLPCI, mPaintTextLPCIBackground)
//                        canvas.drawTextOnPath(countryString, pathTextLPCI, 0f, 0f, mPaintTextLPCI)
//                        vehicleDetailListener?.getLicensePlateCountry(countryString)
//                    }
//                }
//            }
//        }
//    }
//
//    companion object {
//        val TAG = AlprPlateViewCopy::class.java.canonicalName
//        const val LPCI_MIN_CONFIDENCE = 80f
//        const val VCR_MIN_CONFIDENCE = 80f
//        const val VMMR_MIN_CONFIDENCE = 60f
//        const val VBSR_MIN_CONFIDENCE = 70f
//        const val VMMR_FUSE_DEFUSE_MIN_CONFIDENCE = 40f
//        const val VMMR_FUSE_DEFUSE_MIN_OCCURRENCES = 3
//        const val TEXT_NUMBER_SIZE_DIP = 20f
//        const val TEXT_LPCI_SIZE_DIP = 15f
//        const val TEXT_CAR_SIZE_DIP = 15f
//        const val TEXT_INFERENCE_TIME_SIZE_DIP = 10f
//        const val STROKE_WIDTH = 10
//    }
//
//    /**
//     *
//     * @param context
//     * @param attrs
//     */
//    init {
//        val fontALPR = Typeface.createFromAsset(
//            mContext.assets, "GlNummernschildEng-XgWd.ttf"
//        )
//        mPaintTextNumber = Paint()
//        mPaintTextNumber.textSize = TypedValue.applyDimension(
//            TypedValue.COMPLEX_UNIT_DIP, TEXT_NUMBER_SIZE_DIP, resources.displayMetrics
//        )
//        mPaintTextNumber.color = Color.BLACK
//        mPaintTextNumber.style = Paint.Style.FILL_AND_STROKE
//        mPaintTextNumber.typeface = Typeface.create(fontALPR, Typeface.BOLD)
//        mPaintTextNumberBackground = Paint()
//        mPaintTextNumberBackground.color = Color.YELLOW
//        mPaintTextNumberBackground.strokeWidth = STROKE_WIDTH.toFloat()
//        mPaintTextNumberBackground.style = Paint.Style.FILL_AND_STROKE
//        mPaintTextLPCI = Paint()
//        mPaintTextLPCI.textSize = TypedValue.applyDimension(
//            TypedValue.COMPLEX_UNIT_DIP, TEXT_LPCI_SIZE_DIP, resources.displayMetrics
//        )
//        mPaintTextLPCI.color = Color.WHITE
//        mPaintTextLPCI.style = Paint.Style.FILL_AND_STROKE
//        mPaintTextLPCI.typeface = Typeface.create(fontALPR, Typeface.BOLD)
//        mPaintTextLPCIBackground = Paint()
//        mPaintTextLPCIBackground.color = Color.BLUE
//        mPaintTextLPCIBackground.strokeWidth = STROKE_WIDTH.toFloat()
//        mPaintTextLPCIBackground.style = Paint.Style.FILL_AND_STROKE
//        mPaintTextCar = Paint()
//        mPaintTextCar.textSize = TypedValue.applyDimension(
//            TypedValue.COMPLEX_UNIT_DIP, TEXT_CAR_SIZE_DIP, resources.displayMetrics
//        )
//        mPaintTextCar.color = Color.BLACK
//        mPaintTextCar.style = Paint.Style.FILL_AND_STROKE
//        mPaintTextCar.typeface = Typeface.create(fontALPR, Typeface.BOLD)
//        mPaintTextCarBackground = Paint()
//        mPaintTextCarBackground.color = Color.RED
//        mPaintTextCarBackground.strokeWidth = STROKE_WIDTH.toFloat()
//        mPaintTextCarBackground.style = Paint.Style.FILL_AND_STROKE
//        mPaintBorder = Paint()
//        mPaintBorder.strokeWidth = STROKE_WIDTH.toFloat()
//        mPaintBorder.pathEffect = null
//        mPaintBorder.color = Color.YELLOW
//        mPaintBorder.style = Paint.Style.STROKE
//        mPaintTextDurationTime = Paint()
//        mPaintTextDurationTime.textSize = TypedValue.applyDimension(
//            TypedValue.COMPLEX_UNIT_DIP, TEXT_INFERENCE_TIME_SIZE_DIP, resources.displayMetrics
//        )
//        mPaintTextDurationTime.color = Color.BLACK
//        mPaintTextDurationTime.style = Paint.Style.FILL_AND_STROKE
//        mPaintTextDurationTime.typeface = Typeface.create(fontALPR, Typeface.BOLD)
//        mPaintTextDurationTimeBackground = Paint()
//        mPaintTextDurationTimeBackground.color = Color.WHITE
//        mPaintTextDurationTimeBackground.strokeWidth = STROKE_WIDTH.toFloat()
//        mPaintTextDurationTimeBackground.style = Paint.Style.FILL_AND_STROKE
//        mPaintDetectROI = Paint()
//        mPaintDetectROI.color = Color.RED
//        mPaintDetectROI.strokeWidth = STROKE_WIDTH.toFloat()
//        mPaintDetectROI.style = Paint.Style.STROKE
//        mPaintDetectROI.pathEffect = DashPathEffect(floatArrayOf(10f, 20f), 0f)
//    }
//}