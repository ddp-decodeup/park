///*
// * Most of the code from this file comes from CompV project: https://github.com/DoubangoTelecom/compv/blob/master/gl/compv_gl_renderer.cxx
// * The GL surface view allows displaying the YUV data without CPU-based conversion to RGB. As everything is done on GPU
// */
//package com.parkloyalty.lpr.scan.doubangoultimatelpr.extras
//
//import android.content.Context
//import android.graphics.PixelFormat
//import android.media.Image
//import android.opengl.GLES20
//import android.opengl.GLSurfaceView
//import android.util.AttributeSet
//import android.view.SurfaceHolder
//import com.parkloyalty.lpr.vehiclemode.doubangoultimatelpr.utils.AlprUtils
//import com.parkloyalty.lpr.vehiclemode.extensions.showDLog
//import com.parkloyalty.lpr.vehiclemode.extensions.showELog
//import com.parkloyalty.lpr.vehiclemode.extensions.showILog
//import java.lang.RuntimeException
//import java.nio.ByteBuffer
//import java.nio.ByteOrder
//import java.nio.FloatBuffer
//import java.nio.ShortBuffer
//import javax.microedition.khronos.egl.EGLConfig
//import javax.microedition.khronos.opengles.GL10
//
///**
// * GL surface view
// */
//class AlprGLSurfaceViewCopy : GLSurfaceView, GLSurfaceView.Renderer {
//    private var mTriangleVertices: FloatBuffer? = null
//    private var mIndices: ShortBuffer? = null
//    private var mJpegOrientation = 0
//    private var mJpegOrientationChanged = false
//    private var mProgram = 0
//    private var maPositionHandle = 0
//    private var maTextureHandle = 0
//    private var muSamplerYHandle = 0
//    private var muSamplerUHandle = 0
//    private var muSamplerVHandle = 0
//    private val mTextureY = IntArray(1)
//    private val mTextureU = IntArray(1)
//    private val mTextureV = IntArray(1)
//    private var isReady = false
//    private var mImage: Image? = null
//    private var mRatioWidth = 0
//    private var mRatioHeight = 0
//    private val TAG = AlprGLSurfaceViewCopy::class.java.canonicalName
//
//    constructor(context: Context?) : super(context) {
//        initGL()
//    }
//
//    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
//        initGL()
//    }
//
//    private fun initGL() {
//        setEGLContextClientVersion(2)
//        setEGLConfigChooser(8, 8, 8, 8, 16, 0)
//        setRenderer(this)
//        holder.setFormat(PixelFormat.TRANSLUCENT)
//        renderMode = RENDERMODE_WHEN_DIRTY
//        mTriangleVertices = ByteBuffer.allocateDirect(
//            TRIANGLE_VERTICES_DATA_0.size
//                    * FLOAT_SIZE_BYTES
//        ).order(ByteOrder.nativeOrder()).asFloatBuffer()
//        mTriangleVertices?.put(TRIANGLE_VERTICES_DATA_0)?.position(0)
//        mIndices = ByteBuffer.allocateDirect(
//            INDICES_DATA_0.size
//                    * SHORT_SIZE_BYTES
//        ).order(ByteOrder.nativeOrder()).asShortBuffer()
//        mIndices?.put(INDICES_DATA_0)?.position(0)
//    }
//
//    /**
//     * Sets the aspect ratio for this view. The size of the view will be measured based on the ratio
//     * calculated from the parameters. Note that the actual sizes of parameters don't matter, that
//     * is, calling setAspectRatio(2, 3) and setAspectRatio(4, 6) make the same result.
//     *
//     * @param width  Relative horizontal size
//     * @param height Relative vertical size
//     */
//    fun setAspectRatio(width: Int, height: Int) {
//        require(!(width < 0 || height < 0)) { "Size cannot be negative." }
//        mRatioWidth = width
//        mRatioHeight = height
//        requestLayout()
//    }
//
//    /**
//     *
//     * @param
//     */
//    fun setImage(image: Image, jpegOrientation: Int) {
//        if (!isReady) {
//            showILog(TAG, "Not ready")
//            image.close()
//            return
//        }
//        if (mImage != null) {
//            showILog(TAG, "Already rendering previous image")
//            image.close()
//            return
//        }
//
//        // We need to save the image as the rendering is asynchronous
//        mImage = image
//        if (mJpegOrientation != jpegOrientation) {
//            showILog(TAG, "Orientation changed: $mJpegOrientation -> $jpegOrientation")
//            mJpegOrientation = jpegOrientation
//            mJpegOrientationChanged = true
//        }
//
//        // Signal the surface as dirty to force redrawing
//        requestRender()
//    }
//
//    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
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
//    override fun surfaceCreated(holder: SurfaceHolder) {
//        super.surfaceCreated(holder)
//        isReady = true
//    }
//
//    override fun surfaceDestroyed(holder: SurfaceHolder) {
//        isReady = false
//        if (mImage != null) {
//            mImage!!.close()
//            mImage = null
//        }
//        super.surfaceDestroyed(holder)
//    }
//
//    override fun onDrawFrame(glUnused: GL10) {
//        if (mImage == null) {
//            return
//        }
//        if (mJpegOrientationChanged) {
//            updateVertices()
//            mJpegOrientationChanged = false
//        }
//        val swapSize = mJpegOrientation % 180 != 0
//        val imageWidth = mImage!!.width
//        val imageHeight = mImage!!.height
//        val tInfo = AlprUtils.AlprTransformationInfo(
//            if (swapSize) imageHeight else imageWidth,
//            if (swapSize) imageWidth else imageHeight,
//            width,
//            height
//        )
//        GLES20.glViewport(tInfo.xOffset, tInfo.yOffset, tInfo.width, tInfo.height)
//        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT /*| GLES20.GL_DEPTH_BUFFER_BIT*/)
//        GLES20.glUseProgram(mProgram)
//        checkGlError("glUseProgram")
//        val planes = mImage!!.planes
//        val bufferY = planes[0].buffer
//        val bufferU = planes[1].buffer
//        val bufferV = planes[2].buffer
//        val uvPixelStride = planes[1].pixelStride
//        val bufferWidthY = planes[0].rowStride
//        val bufferWidthUV = planes[1].rowStride shr uvPixelStride - 1
//        val bufferHeightUV = imageHeight shr 1 // Always YUV420_888 -> half-height
//        val uvFormat =
//            if (uvPixelStride == 1) GLES20.GL_LUMINANCE else GLES20.GL_LUMINANCE_ALPHA // Interleaved UV
//        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
//        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureY[0])
//        GLES20.glTexImage2D(
//            GLES20.GL_TEXTURE_2D,
//            0,
//            GLES20.GL_LUMINANCE,
//            bufferWidthY,
//            imageHeight,
//            0,
//            GLES20.GL_LUMINANCE,
//            GLES20.GL_UNSIGNED_BYTE,
//            bufferY
//        )
//        GLES20.glUniform1i(muSamplerYHandle, 0)
//        GLES20.glActiveTexture(GLES20.GL_TEXTURE1)
//        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureU[0])
//        GLES20.glTexImage2D(
//            GLES20.GL_TEXTURE_2D,
//            0,
//            uvFormat,
//            bufferWidthUV,
//            bufferHeightUV,
//            0,
//            uvFormat,
//            GLES20.GL_UNSIGNED_BYTE,
//            bufferU
//        )
//        GLES20.glUniform1i(muSamplerUHandle, 1)
//        GLES20.glActiveTexture(GLES20.GL_TEXTURE2)
//        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureV[0])
//        GLES20.glTexImage2D(
//            GLES20.GL_TEXTURE_2D,
//            0,
//            uvFormat,
//            bufferWidthUV,
//            bufferHeightUV,
//            0,
//            uvFormat,
//            GLES20.GL_UNSIGNED_BYTE,
//            bufferV
//        )
//        GLES20.glUniform1i(muSamplerVHandle, 2)
//        GLES20.glDrawElements(
//            GLES20.GL_TRIANGLES,
//            INDICES_DATA_0.size,
//            GLES20.GL_UNSIGNED_SHORT,
//            mIndices
//        )
//
//        mImage!!.close()
//        mImage = null
//    }
//
//    override fun onSurfaceChanged(glUnused: GL10, width: Int, height: Int) {
//        GLES20.glViewport(0, 0, width, height)
//        // GLU.gluPerspective(glUnused, 45.0f, (float)width/(float)height, 0.1f, 100.0f);
//    }
//
//    override fun onSurfaceCreated(glUnused: GL10, config: EGLConfig) {
//        GLES20.glEnable(GLES20.GL_BLEND)
//        GLES20.glDisable(GLES20.GL_DEPTH_TEST)
//        GLES20.glDisable(GLES20.GL_DITHER)
//        GLES20.glDisable(GLES20.GL_STENCIL_TEST)
//        GLES20.glDisable(GL10.GL_DITHER)
//        val extensions = GLES20.glGetString(GL10.GL_EXTENSIONS)
//        showDLog(TAG, "OpenGL extensions=$extensions")
//
//        // Ignore the passed-in GL10 interface, and use the GLES20
//        // class's static methods instead.
//        mProgram = createProgram(VERTEX_SHADER_SOURCE, FRAGMENT_SHADER_SOURCE)
//        if (mProgram == 0) {
//            return
//        }
//        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition")
//        checkGlError("glGetAttribLocation aPosition")
//        if (maPositionHandle == -1) {
//            throw RuntimeException("Could not get attrib location for aPosition")
//        }
//        maTextureHandle = GLES20.glGetAttribLocation(mProgram, "aTextureCoord")
//        checkGlError("glGetAttribLocation aTextureCoord")
//        if (maTextureHandle == -1) {
//            throw RuntimeException("Could not get attrib location for aTextureCoord")
//        }
//        muSamplerYHandle = GLES20.glGetUniformLocation(mProgram, "SamplerY")
//        if (muSamplerYHandle == -1) {
//            throw RuntimeException("Could not get uniform location for SamplerY")
//        }
//        muSamplerUHandle = GLES20.glGetUniformLocation(mProgram, "SamplerU")
//        if (muSamplerUHandle == -1) {
//            throw RuntimeException("Could not get uniform location for SamplerU")
//        }
//        muSamplerVHandle = GLES20.glGetUniformLocation(mProgram, "SamplerV")
//        if (muSamplerVHandle == -1) {
//            throw RuntimeException("Could not get uniform location for SamplerV")
//        }
//        updateVertices()
//        GLES20.glGenTextures(1, mTextureY, 0)
//        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureY[0])
//        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
//        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
//        GLES20.glTexParameteri(
//            GLES20.GL_TEXTURE_2D,
//            GLES20.GL_TEXTURE_WRAP_S,
//            GLES20.GL_CLAMP_TO_EDGE
//        )
//        GLES20.glTexParameteri(
//            GLES20.GL_TEXTURE_2D,
//            GLES20.GL_TEXTURE_WRAP_T,
//            GLES20.GL_CLAMP_TO_EDGE
//        )
//        GLES20.glGenTextures(1, mTextureU, 0)
//        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureU[0])
//        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
//        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
//        GLES20.glTexParameteri(
//            GLES20.GL_TEXTURE_2D,
//            GLES20.GL_TEXTURE_WRAP_S,
//            GLES20.GL_CLAMP_TO_EDGE
//        )
//        GLES20.glTexParameteri(
//            GLES20.GL_TEXTURE_2D,
//            GLES20.GL_TEXTURE_WRAP_T,
//            GLES20.GL_CLAMP_TO_EDGE
//        )
//        GLES20.glGenTextures(1, mTextureV, 0)
//        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureV[0])
//        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
//        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
//        GLES20.glTexParameteri(
//            GLES20.GL_TEXTURE_2D,
//            GLES20.GL_TEXTURE_WRAP_S,
//            GLES20.GL_CLAMP_TO_EDGE
//        )
//        GLES20.glTexParameteri(
//            GLES20.GL_TEXTURE_2D,
//            GLES20.GL_TEXTURE_WRAP_T,
//            GLES20.GL_CLAMP_TO_EDGE
//        )
//        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
//    }
//
//    private fun loadShader(shaderType: Int, source: String): Int {
//        var shader = GLES20.glCreateShader(shaderType)
//        if (shader != 0) {
//            GLES20.glShaderSource(shader, source)
//            GLES20.glCompileShader(shader)
//            val compiled = IntArray(1)
//            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0)
//            if (compiled[0] == 0) {
//                showDLog(TAG, "Could not compile shader $shaderType:")
//                showDLog(TAG, GLES20.glGetShaderInfoLog(shader))
//                GLES20.glDeleteShader(shader)
//                shader = 0
//            }
//        }
//        return shader
//    }
//
//    private fun createProgram(vertexSource: String, fragmentSource: String): Int {
//        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexSource)
//        if (vertexShader == 0) {
//            return 0
//        }
//        val pixelShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource)
//        if (pixelShader == 0) {
//            return 0
//        }
//        var program = GLES20.glCreateProgram()
//        if (program != 0) {
//            GLES20.glAttachShader(program, vertexShader)
//            checkGlError("glAttachShader")
//            GLES20.glAttachShader(program, pixelShader)
//            checkGlError("glAttachShader")
//            GLES20.glLinkProgram(program)
//            val linkStatus = IntArray(1)
//            GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0)
//            if (linkStatus[0] != GLES20.GL_TRUE) {
//                showELog(TAG, "Could not link program: ")
//                showELog(TAG, GLES20.glGetProgramInfoLog(program))
//                GLES20.glDeleteProgram(program)
//                program = 0
//            }
//        }
//        return program
//    }
//
//    private fun updateVertices() {
//        mTriangleVertices!!.rewind()
//        mIndices!!.rewind()
//        when (mJpegOrientation) {
//            90 -> {
//                mTriangleVertices!!.put(TRIANGLE_VERTICES_DATA_90).position(0)
//                mIndices!!.put(INDICES_DATA_90).position(0)
//            }
//            180 -> {
//                mTriangleVertices!!.put(TRIANGLE_VERTICES_DATA_180).position(0)
//                mIndices!!.put(INDICES_DATA_180).position(0)
//            }
//            270 -> {
//                mTriangleVertices!!.put(TRIANGLE_VERTICES_DATA_270).position(0)
//                mIndices!!.put(INDICES_DATA_270).position(0)
//            }
//            0 -> {
//                mTriangleVertices!!.put(TRIANGLE_VERTICES_DATA_0).position(0)
//                mIndices!!.put(INDICES_DATA_0).position(0)
//            }
//            else -> throw RuntimeException("Invalid orientation:$mJpegOrientation")
//        }
//        mTriangleVertices!!.position(TRIANGLE_VERTICES_DATA_POS_OFFSET)
//        GLES20.glVertexAttribPointer(
//            maPositionHandle,
//            3,
//            GLES20.GL_FLOAT,
//            false,
//            TRIANGLE_VERTICES_DATA_STRIDE_BYTES,
//            mTriangleVertices
//        )
//        checkGlError("glVertexAttribPointer maPosition")
//        mTriangleVertices!!.position(TRIANGLE_VERTICES_DATA_UV_OFFSET)
//        GLES20.glEnableVertexAttribArray(maPositionHandle)
//        checkGlError("glEnableVertexAttribArray maPositionHandle")
//        GLES20.glVertexAttribPointer(
//            maTextureHandle,
//            2,
//            GLES20.GL_FLOAT,
//            false,
//            TRIANGLE_VERTICES_DATA_STRIDE_BYTES,
//            mTriangleVertices
//        )
//        checkGlError("glVertexAttribPointer maTextureHandle")
//        GLES20.glEnableVertexAttribArray(maTextureHandle)
//        checkGlError("glEnableVertexAttribArray maTextureHandle")
//    }
//
//    private fun checkGlError(op: String) {
//        var error: Int
//        while (GLES20.glGetError().also { error = it } != GLES20.GL_NO_ERROR) {
//            showELog(TAG, "$op: glError $error")
//            throw RuntimeException("$op: glError $error")
//        }
//    }
//
//    companion object {
//        private const val FLOAT_SIZE_BYTES = 4
//        private const val SHORT_SIZE_BYTES = 2
//        private const val TRIANGLE_VERTICES_DATA_STRIDE_BYTES = 5 * FLOAT_SIZE_BYTES
//        private const val TRIANGLE_VERTICES_DATA_POS_OFFSET = 0
//        private const val TRIANGLE_VERTICES_DATA_UV_OFFSET = 3
//        private val TRIANGLE_VERTICES_DATA_0 = floatArrayOf(
//            1f,
//            -1f,
//            0f,
//            1f,
//            1f,
//            1f,
//            1f,
//            0f,
//            1f,
//            0f,
//            -1f,
//            1f,
//            0f,
//            0f,
//            0f,
//            -1f,
//            -1f,
//            0f,
//            0f,
//            1f
//        )
//        private val INDICES_DATA_0 = shortArrayOf(
//            0, 1, 2,  // triangle #1: bottom/right, top/right, top/left
//            2, 3, 0 // triangle #2: top/left, bottom/left, bottom/right
//        )
//        private val TRIANGLE_VERTICES_DATA_90 = floatArrayOf(
//            1f,
//            -1f,
//            0f,
//            1f,
//            0f,
//            1f,
//            1f,
//            0f,
//            0f,
//            0f,
//            -1f,
//            1f,
//            0f,
//            0f,
//            1f,
//            -1f,
//            -1f,
//            0f,
//            1f,
//            1f
//        )
//        private val INDICES_DATA_90 = shortArrayOf(
//            3, 0, 1,
//            1, 2, 3
//        )
//        private val TRIANGLE_VERTICES_DATA_180 = floatArrayOf(
//            1f,
//            -1f,
//            0f,
//            0f,
//            0f,
//            1f,
//            1f,
//            0f,
//            0f,
//            1f,
//            -1f,
//            1f,
//            0f,
//            1f,
//            1f,
//            -1f,
//            -1f,
//            0f,
//            1f,
//            0f
//        )
//        private val INDICES_DATA_180 = shortArrayOf(
//            2, 3, 0,
//            0, 1, 2
//        )
//        private val TRIANGLE_VERTICES_DATA_270 = floatArrayOf(
//            1f,
//            -1f,
//            0f,
//            0f,
//            1f,
//            1f,
//            1f,
//            0f,
//            1f,
//            1f,
//            -1f,
//            1f,
//            0f,
//            1f,
//            0f,
//            -1f,
//            -1f,
//            0f,
//            0f,
//            0f
//        )
//        private val INDICES_DATA_270 = shortArrayOf(
//            1, 2, 3,
//            3, 0, 1
//        )
//        private const val VERTEX_SHADER_SOURCE = "precision mediump float;" +
//                "attribute vec4 aPosition;\n" +
//                "attribute vec2 aTextureCoord;\n" +
//                "varying vec2 vTextureCoord;\n" +
//                "void main() {\n" +
//                "  gl_Position = aPosition;\n" +
//                "  vTextureCoord = aTextureCoord;\n" +
//                "}\n"
//        private const val FRAGMENT_SHADER_SOURCE = "precision mediump float;" +
//                "varying vec2 vTextureCoord;" +
//                "" +
//                "uniform sampler2D SamplerY; " +
//                "uniform sampler2D SamplerU;" +
//                "uniform sampler2D SamplerV;" +
//                "" +
//                "const mat3 yuv2rgb = mat3(1.164, 0, 1.596, 1.164, -0.391, -0.813, 1.164, 2.018, 0);" +
//                "" +
//                "void main() {    " +
//                "    vec3 yuv = vec3(1.1643 * (texture2D(SamplerY, vTextureCoord).r - 0.06274)," +
//                "                    texture2D(SamplerU, vTextureCoord).r - 0.5019," +
//                "                    texture2D(SamplerV, vTextureCoord).r - 0.5019);" +
//                "    vec3 rgb = yuv * yuv2rgb;    " +
//                "    gl_FragColor = vec4(rgb, 1.0);" +
//                "} "
//    }
//}