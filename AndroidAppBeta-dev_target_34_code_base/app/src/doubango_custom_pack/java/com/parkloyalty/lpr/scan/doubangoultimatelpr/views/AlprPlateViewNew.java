package com.parkloyalty.lpr.scan.doubangoultimatelpr.views; ///* Copyright (C) 2016-2019 Doubango Telecom <https://www.doubango.org>
// * File author: Mamadou DIOP (Doubango Telecom, France).
// * License: GPLv3. For commercial license please contact us.
// * Source code: https://github.com/DoubangoTelecom/compv
// * WebSite: http://compv.org
// */
//package com.parkloyalty.lpr.vehiclemode.doubangoultimatelpr.views;
//
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.graphics.Canvas;
//import android.graphics.Color;
//import android.graphics.DashPathEffect;
//import android.graphics.ImageFormat;
//import android.graphics.Paint;
//import android.graphics.Path;
//import android.graphics.PointF;
//import android.graphics.Rect;
//import android.graphics.RectF;
//import android.graphics.Typeface;
//import android.media.Image;
//import android.renderscript.Allocation;
//import android.renderscript.Element;
//import android.renderscript.RenderScript;
//import android.renderscript.ScriptIntrinsicYuvToRGB;
//import android.renderscript.Type;
//import android.util.AttributeSet;
//import android.util.Log;
//import android.util.Size;
//import android.util.TypedValue;
//import android.view.View;
//
//import androidx.annotation.NonNull;
//
//import com.parkloyalty.lpr.vehiclemode.doubangoultimatelpr.interfaces.GetLPRDetails;
//import com.parkloyalty.lpr.vehiclemode.doubangoultimatelpr.utils.AlprUtilsNew;
//
//import org.doubango.ultimateAlpr.Sdk.UltAlprSdkResult;
////import org.doubango.ultimateAlpr.common.interfaces.GetLPRDetails;
//
//import java.nio.ByteBuffer;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//
//public class AlprPlateViewNew extends View {
//
//    static final String TAG = AlprPlateViewNew.class.getCanonicalName();
//
//    static final float LPCI_MIN_CONFIDENCE = 80.f;
//    static final float VCR_MIN_CONFIDENCE = 80.f;
//    static final float VMMR_MIN_CONFIDENCE = 60.f;
//    static final float VBSR_MIN_CONFIDENCE = 70.f;
//    static final float VMMR_FUSE_DEFUSE_MIN_CONFIDENCE = 40.f;
//    static final int VMMR_FUSE_DEFUSE_MIN_OCCURRENCES = 3;
//
//    static final float TEXT_NUMBER_SIZE_DIP = 20;
//    static final float TEXT_LPCI_SIZE_DIP = 15;
//    static final float TEXT_CAR_SIZE_DIP = 15;
//    static final float TEXT_INFERENCE_TIME_SIZE_DIP = 10;
//    static final int STROKE_WIDTH = 10;
//
//    private final Paint mPaintTextNumber;
//    private final Paint mPaintTextNumberBackground;
//    private final Paint mPaintTextLPCI;
//    private final Paint mPaintTextLPCIBackground;
//    private final Paint mPaintTextCar;
//    private final Paint mPaintTextCarBackground;
//    private final Paint mPaintBorder;
//    private final Paint mPaintTextDurationTime;
//    private final Paint mPaintTextDurationTimeBackground;
//    private final Paint mPaintDetectROI;
//
//    private int mRatioWidth = 0;
//    private int mRatioHeight = 0;
//
//    private int mOrientation = 0;
//
//    private long mDurationTimeMillis;
//
//    private Size mImageSize;
//    private List<AlprUtilsNew.Plate> mPlates = null;
//    private RectF mDetectROI;
//    private Image mImage;
//    private GetLPRDetails getLPRDetails;
//    private Bitmap finalBitmap;
//
//    private Context mContext;
//    /**
//     *
//     * @param context
//     * @param attrs
//     */
//    public AlprPlateViewNew(final Context context, final AttributeSet attrs) {
//        super(context, attrs);
//        mContext = context;
//        final Typeface fontALPR = Typeface.createFromAsset(context.getAssets(), "GlNummernschildEng-XgWd.ttf");
//
//        mPaintTextNumber = new Paint();
//        mPaintTextNumber.setTextSize(TypedValue.applyDimension(
//                TypedValue.COMPLEX_UNIT_DIP, TEXT_NUMBER_SIZE_DIP, getResources().getDisplayMetrics()));
//        mPaintTextNumber.setColor(Color.BLACK);
//        mPaintTextNumber.setStyle(Paint.Style.FILL_AND_STROKE);
//        mPaintTextNumber.setTypeface(Typeface.create(fontALPR, Typeface.BOLD));
//
//        mPaintTextNumberBackground = new Paint();
//        mPaintTextNumberBackground.setColor(Color.YELLOW);
//        mPaintTextNumberBackground.setStrokeWidth(STROKE_WIDTH);
//        mPaintTextNumberBackground.setStyle(Paint.Style.FILL_AND_STROKE);
//
//        mPaintTextLPCI = new Paint();
//        mPaintTextLPCI.setTextSize(TypedValue.applyDimension(
//                TypedValue.COMPLEX_UNIT_DIP, TEXT_LPCI_SIZE_DIP, getResources().getDisplayMetrics()));
//        mPaintTextLPCI.setColor(Color.WHITE);
//        mPaintTextLPCI.setStyle(Paint.Style.FILL_AND_STROKE);
//        mPaintTextLPCI.setTypeface(Typeface.create(fontALPR, Typeface.BOLD));
//
//        mPaintTextLPCIBackground = new Paint();
//        mPaintTextLPCIBackground.setColor(Color.BLUE);
//        mPaintTextLPCIBackground.setStrokeWidth(STROKE_WIDTH);
//        mPaintTextLPCIBackground.setStyle(Paint.Style.FILL_AND_STROKE);
//
//        mPaintTextCar = new Paint();
//        mPaintTextCar.setTextSize(TypedValue.applyDimension(
//                TypedValue.COMPLEX_UNIT_DIP, TEXT_CAR_SIZE_DIP, getResources().getDisplayMetrics()));
//        mPaintTextCar.setColor(Color.BLACK);
//        mPaintTextCar.setStyle(Paint.Style.FILL_AND_STROKE);
//        mPaintTextCar.setTypeface(Typeface.create(fontALPR, Typeface.BOLD));
//
//        mPaintTextCarBackground = new Paint();
//        mPaintTextCarBackground.setColor(Color.RED);
//        mPaintTextCarBackground.setStrokeWidth(STROKE_WIDTH);
//        mPaintTextCarBackground.setStyle(Paint.Style.FILL_AND_STROKE);
//
//        mPaintBorder = new Paint();
//        mPaintBorder.setStrokeWidth(STROKE_WIDTH);
//        mPaintBorder.setPathEffect(null);
//        mPaintBorder.setColor(Color.YELLOW);
//        mPaintBorder.setStyle(Paint.Style.STROKE);
//
//        mPaintTextDurationTime = new Paint();
//        mPaintTextDurationTime.setTextSize(TypedValue.applyDimension(
//                TypedValue.COMPLEX_UNIT_DIP, TEXT_INFERENCE_TIME_SIZE_DIP, getResources().getDisplayMetrics()));
//        mPaintTextDurationTime.setColor(Color.BLACK);
//        mPaintTextDurationTime.setStyle(Paint.Style.FILL_AND_STROKE);
//        mPaintTextDurationTime.setTypeface(Typeface.create(fontALPR, Typeface.BOLD));
//
//        mPaintTextDurationTimeBackground = new Paint();
//        mPaintTextDurationTimeBackground.setColor(Color.WHITE);
//        mPaintTextDurationTimeBackground.setStrokeWidth(STROKE_WIDTH);
//        mPaintTextDurationTimeBackground.setStyle(Paint.Style.FILL_AND_STROKE);
//
//        mPaintDetectROI = new Paint();
//        mPaintDetectROI.setColor(Color.RED);
//        mPaintDetectROI.setStrokeWidth(STROKE_WIDTH);
//        mPaintDetectROI.setStyle(Paint.Style.STROKE);
//        mPaintDetectROI.setPathEffect(new DashPathEffect(new float[] {10,20}, 0));
//    }
//
//    public void setDetectROI(final RectF roi) { mDetectROI = roi; }
//
//    /**
//     *
//     * @param width
//     * @param height
//     */
//    public void setAspectRatio(int width, int height) {
//        if (width < 0 || height < 0) {
//            throw new IllegalArgumentException("Size cannot be negative.");
//        }
//        mRatioWidth = width;
//        mRatioHeight = height;
//        requestLayout();
//    }
//
//    public void setLPRListener(GetLPRDetails getLPRDetails){
//        this.getLPRDetails = getLPRDetails;
//    }
//
//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        Log.i(TAG, "onMeasure");
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        int width = MeasureSpec.getSize(widthMeasureSpec);
//        int height = MeasureSpec.getSize(heightMeasureSpec);
//        if (0 == mRatioWidth || 0 == mRatioHeight) {
//            setMeasuredDimension(width, height);
//        } else {
//            if (width < height * mRatioWidth / mRatioHeight) {
//                setMeasuredDimension(width, width * mRatioHeight / mRatioWidth);
//            } else {
//                setMeasuredDimension(height * mRatioWidth / mRatioHeight, height);
//            }
//        }
//    }
//
//    /**
//     *
//     * @param result
//     * @param imageSize
//     */
//    public synchronized void setResult(@NonNull final UltAlprSdkResult result, @NonNull final Size imageSize, @NonNull final long durationTime, @NonNull final int orientation) {
//        mPlates = AlprUtilsNew.extractPlates(result);
//        mImageSize = imageSize;
//        mDurationTimeMillis = durationTime;
//        mOrientation = orientation;
//        postInvalidate();
//    }
//
//    /**
//     *
//     * @param result
//     * @param imageSize
//     */
//    public synchronized void setResult(Image image, @NonNull final UltAlprSdkResult result, @NonNull final Size imageSize, @NonNull final long durationTime, @NonNull final int orientation) {
//        mPlates = AlprUtilsNew.extractPlates(result);
//        mImage = image;
//        //finalBitmap = yuv420ToBitmap(image, mContext);
//        mImageSize = imageSize;
//        mDurationTimeMillis = durationTime;
//        mOrientation = orientation;
//        postInvalidate();
//    }
//
//    private Bitmap yuv420ToBitmap(Image image, Context context) {
//        RenderScript rs = RenderScript.create(context);
//        ScriptIntrinsicYuvToRGB script = ScriptIntrinsicYuvToRGB.create(rs, Element.U8_4(rs));
//
//        // Refer the logic in a section below on how to convert a YUV_420_888 image
//        // to single channel flat 1D array. For sake of this example I'll abstract it
//        // as a method.
//        byte[] yuvByteArray = image2byteArray(image);
//
//        Type.Builder yuvType = new Type.Builder(rs, Element.U8(rs)).setX(yuvByteArray.length);
//        Allocation in = Allocation.createTyped(rs, yuvType.create(), Allocation.USAGE_SCRIPT);
//
//        Type.Builder rgbaType = new Type.Builder(rs, Element.RGBA_8888(rs))
//                .setX(image.getWidth())
//                .setY(image.getHeight());
//        Allocation out = Allocation.createTyped(rs, rgbaType.create(), Allocation.USAGE_SCRIPT);
//
//        // The allocations above "should" be cached if you are going to perform
//        // repeated conversion of YUV_420_888 to Bitmap.
//        in.copyFrom(yuvByteArray);
//        script.setInput(in);
//        script.forEach(out);
//        Bitmap bitmap = Bitmap.createBitmap(image.getWidth(), image.getHeight(), Bitmap.Config.ARGB_8888);
//        out.copyTo(bitmap);
//        return bitmap;
//    }
//
//    private byte[] image2byteArray(Image image) {
//        if (image.getFormat() != ImageFormat.YUV_420_888) {
//            throw new IllegalArgumentException("Invalid image format");
//        }
//
//        int width = image.getWidth();
//        int height = image.getHeight();
//
//        Image.Plane yPlane = image.getPlanes()[0];
//        Image.Plane uPlane = image.getPlanes()[1];
//        Image.Plane vPlane = image.getPlanes()[2];
//
//        ByteBuffer yBuffer = yPlane.getBuffer();
//        ByteBuffer uBuffer = uPlane.getBuffer();
//        ByteBuffer vBuffer = vPlane.getBuffer();
//
//        // Full size Y channel and quarter size U+V channels.
//        int numPixels = (int) (width * height * 1.5f);
//        byte[] nv21 = new byte[numPixels];
//        int index = 0;
//
//        // Copy Y channel.
//        int yRowStride = yPlane.getRowStride();
//        int yPixelStride = yPlane.getPixelStride();
//        for(int y = 0; y < height; ++y) {
//            for (int x = 0; x < width; ++x) {
//                nv21[index++] = yBuffer.get(y * yRowStride + x * yPixelStride);
//            }
//        }
//
//        // Copy VU data; NV21 format is expected to have YYYYVU packaging.
//        // The U/V planes are guaranteed to have the same row stride and pixel stride.
//        int uvRowStride = uPlane.getRowStride();
//        int uvPixelStride = uPlane.getPixelStride();
//        int uvWidth = width / 2;
//        int uvHeight = height / 2;
//
//        for(int y = 0; y < uvHeight; ++y) {
//            for (int x = 0; x < uvWidth; ++x) {
//                int bufferIndex = (y * uvRowStride) + (x * uvPixelStride);
//                // V channel.
//                nv21[index++] = vBuffer.get(bufferIndex);
//                // U channel.
//                nv21[index++] = uBuffer.get(bufferIndex);
//            }
//        }
//        return nv21;
//    }
//
//
//    @Override
//    public synchronized void draw(final Canvas canvas) {
//        super.draw(canvas);
//
//        if (mImageSize == null) {
//            Log.i(TAG, "Not initialized yet");
//            return;
//        }
//
//        // Inference time
//        // Landscape faster: https://www.doubango.org/SDKs/anpr/docs/Improving_the_speed.html#landscape-mode
//        final String mInferenceTimeMillisString = "Total processing time: " + mDurationTimeMillis + (mOrientation == 0 ? "" : " -> Rotate to landscape to speedup");
//        Rect boundsTextmInferenceTimeMillis = new Rect();
//        mPaintTextDurationTime.getTextBounds(mInferenceTimeMillisString, 0, mInferenceTimeMillisString.length(), boundsTextmInferenceTimeMillis);
//        canvas.drawRect(0, 0, boundsTextmInferenceTimeMillis.width(), boundsTextmInferenceTimeMillis.height(), mPaintTextDurationTimeBackground);
//        canvas.drawText(mInferenceTimeMillisString, 0, boundsTextmInferenceTimeMillis.height(), mPaintTextDurationTime);
//
//        // Transformation info
//        final AlprUtilsNew.AlprTransformationInfo tInfo = new AlprUtilsNew.AlprTransformationInfo(mImageSize.getWidth(), mImageSize.getHeight(), getWidth(), getHeight());
//
//        // ROI
//        if (mDetectROI != null && !mDetectROI.isEmpty()) {
//            canvas.drawRect(
//                    new RectF(
//                            tInfo.transformX(mDetectROI.left),
//                            tInfo.transformY(mDetectROI.top),
//                            tInfo.transformX(mDetectROI.right),
//                            tInfo.transformY(mDetectROI.bottom)
//                    ),
//                    mPaintDetectROI
//            );
//        }
//
//        // Plates
//        if (mPlates != null && !mPlates.isEmpty()) {
//            for (final AlprUtilsNew.Plate plate : mPlates) {
//                // Transform corners
//                final float[] plateWarpedBox = plate.getWarpedBox();
//                final PointF plateCornerA = new PointF(tInfo.transformX(plateWarpedBox[0]), tInfo.transformY(plateWarpedBox[1]));
//                final PointF plateCornerB = new PointF(tInfo.transformX(plateWarpedBox[2]), tInfo.transformY(plateWarpedBox[3]));
//                final PointF plateCornerC = new PointF(tInfo.transformX(plateWarpedBox[4]), tInfo.transformY(plateWarpedBox[5]));
//                final PointF plateCornerD = new PointF(tInfo.transformX(plateWarpedBox[6]), tInfo.transformY(plateWarpedBox[7]));
//                // Draw border
//                final Path platePathBorder = new Path();
//                platePathBorder.moveTo(plateCornerA.x, plateCornerA.y);
//                platePathBorder.lineTo(plateCornerB.x, plateCornerB.y);
//                platePathBorder.lineTo(plateCornerC.x, plateCornerC.y);
//                platePathBorder.lineTo(plateCornerD.x, plateCornerD.y);
//                platePathBorder.lineTo(plateCornerA.x, plateCornerA.y);
//                platePathBorder.close();
//                mPaintBorder.setColor(mPaintTextNumberBackground.getColor());
//                canvas.drawPath(platePathBorder, mPaintBorder);
//
//                // Draw text number
//                final String number = plate.getNumber();
//                if (number != null && !number.isEmpty()) {
//                    getLPRDetails.getLPNumber(number);
//                    getLPRDetails.getImage(mImage);
//                    getLPRDetails.getImageBitmap(finalBitmap);
//                    Rect boundsTextNumber = new Rect();
//                    mPaintTextNumber.getTextBounds(number, 0, number.length(), boundsTextNumber);
//                    final RectF rectTextNumber = new RectF(
//                            plateCornerA.x,
//                            plateCornerA.y - boundsTextNumber.height(),
//                            plateCornerA.x + boundsTextNumber.width(),
//                            plateCornerA.y
//                    );
//                    final Path pathTextNumber = new Path();
//                    pathTextNumber.moveTo(plateCornerA.x, plateCornerA.y);
//                    pathTextNumber.lineTo(Math.max(plateCornerB.x, (plateCornerA.x + rectTextNumber.width())), plateCornerB.y);
//                    pathTextNumber.addRect(rectTextNumber, Path.Direction.CCW);
//                    pathTextNumber.close();
//                    canvas.drawPath(pathTextNumber, mPaintTextNumberBackground);
//                    canvas.drawTextOnPath(number, pathTextNumber, 0, 0, mPaintTextNumber);
//                }
//
//                // Draw Car
//                if (plate.getCar() != null) {
//                    final AlprUtilsNew.Car car = plate.getCar();
//                    if (car.getConfidence() >= 80.f) {
//                        // Vehicle Color Recognition [VCR] (added in 3.0.0) : https://www.doubango.org/SDKs/anpr/docs/Features.html#vehicle-color-recognition-vcr
//                        String color = null;
//                        if (car.getColors() != null) {
//                            final AlprUtilsNew.Car.Attribute colorObj0 = car.getColors().get(0); // sorted, most higher confidence first
//                            if (colorObj0.getConfidence() >= VCR_MIN_CONFIDENCE) {
//                                color = colorObj0.getName();
//                            }
//                            else if (car.getColors().size() >= 2) {
//                                // Color fusion: https://www.doubango.org/SDKs/anpr/docs/Improving_the_accuracy.html#fuse
//                                final AlprUtilsNew.Car.Attribute colorObj1 = car.getColors().get(1);
//                                final String colorMix = colorObj0.getName() + "/" + colorObj1.getName();
//                                float confidence = colorObj0.getConfidence();
//                                if ("white/silver,silver/white,gray/silver,silver/gray".indexOf(colorMix) != -1) {
//                                    confidence += colorObj1.getConfidence();
//                                }
//                                if (confidence >= VCR_MIN_CONFIDENCE) {
//                                    color = (colorMix.indexOf("white") == -1) ? "DarkSilver" : "LightSilver";
//									confidence = Math.max(colorObj0.getConfidence(), colorObj1.getConfidence());
//                                }
//                            }
//                        }
//
//                        getLPRDetails.getColor(color);
//
//                        // Vehicle Make Model Recognition [VMMR] (added in 3.0.0): https://www.doubango.org/SDKs/anpr/docs/Features.html#vehicle-make-model-recognition-vmmr
//                        String make = null, model = null;
//                        if (car.getMakesModelsYears() != null) {
//                            final List<AlprUtilsNew.Car.MakeModelYear> makesModelsYears = car.getMakesModelsYears();
//                            final AlprUtilsNew.Car.MakeModelYear makeModelYear = makesModelsYears.get(0); // sorted, most higher confidence first
//                            if (makeModelYear.getConfidence() >= VMMR_MIN_CONFIDENCE) {
//                                make = makeModelYear.getMake();
//                                model = makeModelYear.getModel();
//                            }
//                            else {
//                                // Fuse and defuse: https://www.doubango.org/SDKs/anpr/docs/Improving_the_accuracy.html#fuse-and-defuse
//                                Map<String, Float> makes =  new HashMap<>();
//                                Map<String, Integer> occurrences =  new HashMap<>();
//								// Fuse makes
//                                for (final AlprUtilsNew.Car.MakeModelYear mmy : makesModelsYears) {
//                                    makes.put(mmy.getMake(), AlprUtilsNew.getOrDefault(makes, mmy.getMake(), 0.f) + mmy.getConfidence()); // Map.getOrDefault requires API level 24
//                                    occurrences.put(mmy.getMake(), AlprUtilsNew.getOrDefault(occurrences, mmy.getMake(), 0) + 1); // Map.getOrDefault requires API level 24
//                                }
//                                // Find make with highest confidence. Stream requires Java8
//                                Iterator<Map.Entry<String, Float> > itMake = makes.entrySet().iterator();
//                                Map.Entry<String, Float> bestMake = itMake.next();
//                                while (itMake.hasNext()) {
//                                    Map.Entry<String, Float> makeE = itMake.next();
//                                    if (makeE.getValue() > bestMake.getValue()) {
//                                        bestMake = makeE;
//                                    }
//                                }
//								// Model fusion
//                                if (bestMake.getValue() >= VMMR_MIN_CONFIDENCE || (occurrences.get(bestMake.getKey()) >= VMMR_FUSE_DEFUSE_MIN_OCCURRENCES && bestMake.getValue() >= VMMR_FUSE_DEFUSE_MIN_CONFIDENCE)) {
//                                    make = bestMake.getKey();
//
//                                    // Fuse models
//                                    Map<String, Float> models =  new HashMap<>();
//                                    for (final AlprUtilsNew.Car.MakeModelYear mmy : makesModelsYears) {
//                                        if (make.equals(mmy.getMake())) {
//                                            models.put(mmy.getModel(), AlprUtilsNew.getOrDefault(models, mmy.getModel(), 0.f) + mmy.getConfidence()); // Map.getOrDefault requires API level 24
//                                        }
//                                    }
//                                    // Find model with highest confidence. Stream requires Java8
//                                    Iterator<Map.Entry<String, Float> > itModel = models.entrySet().iterator();
//                                    Map.Entry<String, Float> bestModel = itModel.next();
//                                    while (itModel.hasNext()) {
//                                        Map.Entry<String, Float> modelE = itModel.next();
//                                        if (modelE.getValue() > bestModel.getValue()) {
//                                            bestModel = modelE;
//                                        }
//                                    }
//                                    model = bestModel.getKey();
//                                }
//                            }
//                        }
//
//                        getLPRDetails.getMake(make);
//                        getLPRDetails.getModel(model);
//
//                        // Vehicle Body Style Recognition [VBSR] (added in 3.2.0): https://www.doubango.org/SDKs/anpr/docs/Features.html#features-vehiclebodystylerecognition
//                        String bodyStyle = null;
//                        if (car.getBodyStyles() != null) {
//                            final AlprUtilsNew.Car.Attribute vbsr = car.getBodyStyles().get(0); // sorted, most higher confidence first
//                            if (vbsr.getConfidence() >= VBSR_MIN_CONFIDENCE) {
//                                bodyStyle = vbsr.getName();
//                            }
//                        }
//
//                        getLPRDetails.getBodyStyle(bodyStyle);
//
//
//                        // Transform corners
//                        final float[] carWarpedBox = car.getWarpedBox();
//                        final PointF carCornerA = new PointF(tInfo.transformX(carWarpedBox[0]), tInfo.transformY(carWarpedBox[1]));
//                        final PointF carCornerB = new PointF(tInfo.transformX(carWarpedBox[2]), tInfo.transformY(carWarpedBox[3]));
//                        final PointF carCornerC = new PointF(tInfo.transformX(carWarpedBox[4]), tInfo.transformY(carWarpedBox[5]));
//                        final PointF carCornerD = new PointF(tInfo.transformX(carWarpedBox[6]), tInfo.transformY(carWarpedBox[7]));
//                        // Draw border
//                        final Path carPathBorder = new Path();
//                        carPathBorder.moveTo(carCornerA.x, carCornerA.y);
//                        carPathBorder.lineTo(carCornerB.x, carCornerB.y);
//                        carPathBorder.lineTo(carCornerC.x, carCornerC.y);
//                        carPathBorder.lineTo(carCornerD.x, carCornerD.y);
//                        carPathBorder.lineTo(carCornerA.x, carCornerA.y);
//                        carPathBorder.close();
//                        mPaintBorder.setColor(mPaintTextCarBackground.getColor());
//                        canvas.drawPath(carPathBorder, mPaintBorder);
//
//                        // Draw car information
//                        final String carText = String.format(
//                                "%s%s%s%s",
//                                make != null ? make : "Car",
//                                model != null ? ", " + model : "",
//                                color != null ? ", " + color : "",
//                                bodyStyle != null ? ", " + bodyStyle : ""
//                        );
//                        Rect boundsTextCar = new Rect();
//                        mPaintTextCar.getTextBounds(carText, 0, carText.length(), boundsTextCar);
//                        final RectF rectTextCar = new RectF(
//                                carCornerA.x,
//                                carCornerA.y - boundsTextCar.height(),
//                                carCornerA.x + boundsTextCar.width(),
//                                carCornerA.y
//                        );
//                        final Path pathTextCar = new Path();
//                        pathTextCar.moveTo(carCornerA.x, carCornerA.y);
//                        pathTextCar.lineTo(Math.max(carCornerB.x, (carCornerA.x + rectTextCar.width())), carCornerB.y);
//                        pathTextCar.addRect(rectTextCar, Path.Direction.CCW);
//                        pathTextCar.close();
//                        canvas.drawPath(pathTextCar, mPaintTextCarBackground);
//                        canvas.drawTextOnPath(carText, pathTextCar, 0, 0, mPaintTextCar);
//                    }
//                }
//
//                // License Plate Country Identification [LPCI] (Added in 3.0.0): https://www.doubango.org/SDKs/anpr/docs/Features.html#license-plate-country-identification-lpci
//                if (plate.getCountries() != null) {
//                    final AlprUtilsNew.Country country = plate.getCountries().get(0); // sorted, most higher confidence first
//                    if (country.getConfidence() >= LPCI_MIN_CONFIDENCE) {
//                        //Start of State
//                        getLPRDetails.getState(country.getState());
//                        //End of State by Janak
//
//                        final String countryString = country.getCode();
//                        Rect boundsConfidenceLPCI = new Rect();
//                        mPaintTextLPCI.getTextBounds(countryString, 0, countryString.length(), boundsConfidenceLPCI);
//                        final RectF rectTextLPCI = new RectF(
//                                plateCornerD.x,
//                                plateCornerD.y,
//                                plateCornerD.x + boundsConfidenceLPCI.width(),
//                                plateCornerD.y +  boundsConfidenceLPCI.height()
//                        );
//                        final Path pathTextLPCI = new Path();
//                        final double dx = plateCornerC.x - plateCornerD.x;
//                        final double dy = plateCornerC.y - plateCornerD.y;
//                        final double angle = Math.atan2(dy, dx);
//                        final double cosT = Math.cos(angle);
//                        final double sinT = Math.sin(angle);
//                        final float Cx = plateCornerD.x + rectTextLPCI.width();
//                        final float Cy = plateCornerC.y;
//                        final PointF cornerCC = new PointF((float) (Cx * cosT - Cy * sinT), (float) (Cy * cosT + Cx * sinT));
//                        final PointF cornerDD = new PointF((float) (plateCornerD.x * cosT - plateCornerD.y * sinT), (float) (plateCornerD.y * cosT + plateCornerD.x * sinT));
//                        pathTextLPCI.moveTo(cornerDD.x, cornerDD.y + boundsConfidenceLPCI.height());
//                        pathTextLPCI.lineTo(cornerCC.x, cornerCC.y + boundsConfidenceLPCI.height());
//                        pathTextLPCI.addRect(rectTextLPCI, Path.Direction.CCW);
//                        pathTextLPCI.close();
//                        canvas.drawPath(pathTextLPCI, mPaintTextLPCIBackground);
//                        canvas.drawTextOnPath(countryString, pathTextLPCI, 0, 0, mPaintTextLPCI);
//                        getLPRDetails.getCountry(countryString);
//                    }
//                }
//            }
//        }
//    }
//}