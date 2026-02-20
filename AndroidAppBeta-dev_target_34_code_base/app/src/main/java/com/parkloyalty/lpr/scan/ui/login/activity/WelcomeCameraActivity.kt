package com.parkloyalty.lpr.scan.ui.login.activity

import android.content.Context
import android.os.Bundle
import butterknife.ButterKnife
import com.google.android.gms.common.api.GoogleApiClient
import com.parkloyalty.lpr.scan.R
import com.parkloyalty.lpr.scan.basecontrol.BaseActivity
import com.parkloyalty.lpr.scan.basecontrol.BaseApplication
import com.parkloyalty.lpr.scan.interfaces.CustomDialogHelper

 open class WelcomeCameraActivity: BaseActivity() {

  private var mContext: Context? = null
  override fun onCreate(savedInstanceState: Bundle?) {
   super.onCreate(savedInstanceState)
   setContentView(R.layout.activity_abandoned_vehicle)
   setFullScreenUI()
   ButterKnife.bind(this)
   mContext = this
  }
}