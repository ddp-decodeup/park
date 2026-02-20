package com.parkloyalty.lpr.scan.ui.xfprinter

import android.os.Bundle
import com.parkloyalty.lpr.scan.R
import com.twotechnologies.n5library.N5Information
import com.twotechnologies.xfexample.EventSupport.XFActivityCoroutine

class FeedPrinterActivity :XfPrinterActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.imageprintlayout)
//        N5Information.isPrinterAvailable()
//        mContext = this
//        mCoroutine = XFActivityCoroutine(applicationContext, this)
//        mCoroutine!!.start()
//        getPrintSetFFValueFromSettingFile()
    }

    override fun onResume() {
        super.onResume()
        printerFeedButton()
        finish()
    }
}