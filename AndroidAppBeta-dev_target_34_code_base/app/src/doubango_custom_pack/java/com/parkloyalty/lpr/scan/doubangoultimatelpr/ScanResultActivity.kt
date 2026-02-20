package com.parkloyalty.lpr.scan.doubangoultimatelpr

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.parkloyalty.lpr.scan.R
import com.parkloyalty.lpr.scan.doubangoultimatelpr.utils.ConstantsDoubango.KEY_FROM
import com.parkloyalty.lpr.scan.util.DoubangoConstants.FROM_POINT_AND_SCAN

import java.io.File

class ScanResultActivity : AppCompatActivity() {
    private var tvNumberPlate: TextView? = null
    private var tvMake: TextView? = null
    private var tvModel: TextView? = null
    private var tvColor: TextView? = null
    private var tvState: TextView? = null
    private var ivLicensePlate: ImageView? = null
    private var btRescan: AppCompatButton? = null
    private val llOutput: LinearLayout? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_result)
        tvNumberPlate = findViewById<View>(R.id.tvNumberPlate) as TextView
        tvMake = findViewById<View>(R.id.tvMake) as TextView
        tvModel = findViewById<View>(R.id.tvModel) as TextView
        tvColor = findViewById<View>(R.id.tvColor) as TextView
        tvState = findViewById<View>(R.id.tvState) as TextView
        ivLicensePlate = findViewById<View>(R.id.ivLicensePlate) as ImageView
        btRescan = findViewById<View>(R.id.btRescan) as AppCompatButton
        //llOutput = (LinearLayout) findViewById(R.id.llOutput);
        intentAndSetData
        listeners()
    }

    //ivLicensePlate.setImageBitmap(this.getIntent().getParcelableExtra("bitmap"));
    private val intentAndSetData: Unit
        private get() {
            tvNumberPlate!!.text =
                "Number Plate : " + getValuesFromVariable(this.intent.getStringExtra("numberplate"))
            tvMake!!.text = "Make : " + getValuesFromVariable(this.intent.getStringExtra("make"))
            tvModel!!.text = "Model : " + getValuesFromVariable(this.intent.getStringExtra("model"))
            tvColor!!.text = "Color : " + getValuesFromVariable(this.intent.getStringExtra("color"))
            tvState!!.text = "State : " + getValuesFromVariable(this.intent.getStringExtra("state"))
            val imgFile = File(this.intent.getStringExtra("bitmap"))
            if (imgFile.exists()) {
                val myBitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
                ivLicensePlate!!.setImageBitmap(myBitmap)
                ivLicensePlate!!.setOnClickListener {
                    //ImageViewerActivity.startActivity(this@ScanResultActivity, true, this@ScanResultActivity.intent.getStringExtra("bitmap").nullSafety())
                }
            }


            //ivLicensePlate.setImageBitmap(this.getIntent().getParcelableExtra("bitmap"));
        }

    private fun getValuesFromVariable(oldValue: String?): String {
        var result = "Not available"
        if (oldValue != null && oldValue !== "null") {
            result = oldValue
        }
        return result
    }

    private fun listeners() {
        btRescan!!.setOnClickListener {
            val intent = Intent(this@ScanResultActivity, AlprVideoParallelActivity::class.java)
            intent.putExtra(KEY_FROM, FROM_POINT_AND_SCAN)
            startActivity(intent)
            finishAffinity()
        }
    }
}