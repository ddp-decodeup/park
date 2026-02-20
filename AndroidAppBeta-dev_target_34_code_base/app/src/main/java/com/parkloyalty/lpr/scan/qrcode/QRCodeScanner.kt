package com.parkloyalty.lpr.scan.qrcode

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.Detector.Detections
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import com.parkloyalty.lpr.scan.R
import com.parkloyalty.lpr.scan.databinding.ActivityQrcodeLayoutBinding
import com.parkloyalty.lpr.scan.extensions.getEquipmentName
import com.parkloyalty.lpr.scan.extensions.getEquipmentValue
import com.parkloyalty.lpr.scan.extensions.logD
import com.parkloyalty.lpr.scan.util.INTENT_KEY_FROM
import com.parkloyalty.lpr.scan.util.INTENT_KEY_SCANNED_EQUIPMENT_KEY
import com.parkloyalty.lpr.scan.util.INTENT_KEY_SCANNED_EQUIPMENT_VALUE
import com.parkloyalty.lpr.scan.util.SharedPref
import com.parkloyalty.lpr.scan.utils.permissions.PermissionManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject
import androidx.core.util.size

@AndroidEntryPoint
class QRCodeScanner : AppCompatActivity() {
    private lateinit var cameraSource: CameraSource
    private lateinit var barcodeDetector: BarcodeDetector
    private var scannedValue = ""

    private var mFrom: String? = ""

    @Inject
    lateinit var permissionFactory: PermissionManager.Factory

    private lateinit var permissionManager: PermissionManager

    @Inject
    lateinit var sharedPreference: SharedPref

    private var cameraSurfaceView: SurfaceView? = null
    private var barCodeLine: View? = null
    private var mImageViewClose: AppCompatImageView? = null
    private var mImageViewFlashLight: AppCompatImageView? = null

    private lateinit var binding: ActivityQrcodeLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQrcodeLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        permissionManager = permissionFactory.create(caller = this, context = this@QRCodeScanner, activity = this)

        findViewsByViewBinding()
        setupClickListeners()

        lifecycleScope.launch {
            permissionManager.ensurePermissionsThen(
                permissions = arrayOf(Manifest.permission.CAMERA),
                rationaleMessage = getString(R.string.permission_message_camera_permission_required)
            ) {
                setupControls()
            }
        }

        init()
    }

    private fun findViewsByViewBinding() {
        cameraSurfaceView = binding.cameraSurfaceView
        barCodeLine = binding.barcodeLine
        mImageViewClose = binding.imageViewClose
        mImageViewFlashLight = binding.imageViewflash
    }

    private fun setupClickListeners() {
        mImageViewClose?.setOnClickListener {
            finish()
        }

        mImageViewFlashLight?.setOnClickListener {
//            cameraSource!!.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        }
    }

    private fun getIntentData() {
        mFrom = intent?.getStringExtra(INTENT_KEY_FROM)
    }

    private fun init() {
        getIntentData()
    }

    private fun setResultBackToPreviousActivity(scannedValue: String) {
        val intent = Intent()
        intent.putExtra(INTENT_KEY_FROM, mFrom)
        intent.putExtra(INTENT_KEY_SCANNED_EQUIPMENT_KEY, scannedValue.getEquipmentName())
        intent.putExtra(INTENT_KEY_SCANNED_EQUIPMENT_VALUE, scannedValue.getEquipmentValue())
        setResult(RESULT_OK, intent)
        finish()
    }

    private fun setupControls() {
        barcodeDetector =
            BarcodeDetector.Builder(this@QRCodeScanner).setBarcodeFormats(Barcode.ALL_FORMATS)
                .build()

        cameraSource = CameraSource.Builder(this@QRCodeScanner, barcodeDetector)
            .setRequestedPreviewSize(1920, 1080)
            .setAutoFocusEnabled(true) //you should add this feature
            .build()

        cameraSurfaceView?.holder?.addCallback(object : SurfaceHolder.Callback {
            @SuppressLint("MissingPermission")
            override fun surfaceCreated(holder: SurfaceHolder) {
                try {
                    //Start preview after 1s delay
                    cameraSource.start(holder)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            @SuppressLint("MissingPermission")
            override fun surfaceChanged(
                holder: SurfaceHolder, format: Int, width: Int, height: Int
            ) {
                try {
                    cameraSource.start(holder)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                cameraSource.stop()
            }
        })


        barcodeDetector.setProcessor(object : Detector.Processor<Barcode> {
            override fun release() {
                Toast.makeText(applicationContext, "Scanner has been closed", Toast.LENGTH_SHORT)
                    .show()
            }

            override fun receiveDetections(detections: Detections<Barcode>) {
                val barcodes = detections.detectedItems
                if (barcodes.size == 1) {
                    scannedValue = barcodes.valueAt(0).rawValue
                    setResultBackToPreviousActivity(scannedValue)
                } else {
                    logD("result", "value- else")
                }
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraSource.stop()
    }
}