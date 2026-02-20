package com.parkloyalty.lpr.scan.ui.ticket

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.widget.AppCompatImageView
import androidx.lifecycle.Observer
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.internal.DebouncingOnClickListener
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.parkloyalty.lpr.scan.R
import com.parkloyalty.lpr.scan.basecontrol.BaseActivity
import com.parkloyalty.lpr.scan.network.ApiResponse
import com.parkloyalty.lpr.scan.network.DynamicAPIPath
import com.parkloyalty.lpr.scan.network.NetworkCheck
import com.parkloyalty.lpr.scan.network.Status
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DropdownDatasetResponse
import com.parkloyalty.lpr.scan.ui.ticket.model.*
import com.parkloyalty.lpr.scan.util.LogUtil
import com.parkloyalty.lpr.scan.utils.ObjectMapperProvider
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class ViewFacsimileImageActivity: BaseActivity() {

    @JvmField
    @BindView(R.id.image_view_facsimile)
    var appCompatImageViewFacsimile: AppCompatImageView? = null

    @JvmField
    @BindView(R.id.imgBack)
    var appCompatImageViewBack: AppCompatImageView? = null

    private var mPrintBitmapPath: String? = null
    private val mDownloadBitmapFIleViewModel: DownloadBitmapFIleViewModel? by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_facsimile_image_activity)
        setFullScreenUI()
        ButterKnife.bind(this)
        addObservers()
        init()
    }


    private fun addObservers() {
        mDownloadBitmapFIleViewModel?.response?.observe(this, downloadBitmapResponseObserver)
    }

    override fun removeObservers() {
        super.removeObservers()
        mDownloadBitmapFIleViewModel?.response?.removeObserver(downloadBitmapResponseObserver)
    }// this will contain "Fruit"// If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()

    private val downloadBitmapResponseObserver = Observer { apiResponse: ApiResponse ->
        consumeResponse(
                apiResponse,
                DynamicAPIPath.POST_DOWNLOAD_FILE
        )
    }

    private fun init()
    {
        mPrintBitmapPath = intent.getStringExtra("print_bitmap")
        callDownloadBitmapApi()

        appCompatImageViewBack!!.setOnClickListener(object : DebouncingOnClickListener() {
            override fun doClick(p0: View) {
                finish()
            }
        })
    }

    /* Call Api For Ticket Cancel */
    private fun callDownloadBitmapApi() {
        if (NetworkCheck.isInternetAvailable(this@ViewFacsimileImageActivity)) {
            if (mPrintBitmapPath != null && !mPrintBitmapPath!!.isEmpty() && mPrintBitmapPath!!.length > 5) {
                val downloadBitmapRequest = DownloadBitmapRequest()
                downloadBitmapRequest.downloadType = "CitationImages"
                val links = Links()
                links.img1 = mPrintBitmapPath
                downloadBitmapRequest.links = links
                mDownloadBitmapFIleViewModel?.downloadBitmapAPI(downloadBitmapRequest)
            } else {
                LogUtil.printToastMSG(
                        applicationContext,
                        getString(R.string.err_msg_down_load_image)
                )
            }
        } else {
            LogUtil.printToastMSG(
                    applicationContext,
                    getString(R.string.err_msg_connection_was_refused)
            )
        }
    }

    /*Api response */
    private fun consumeResponse(apiResponse: ApiResponse, tag: String) {
        when (apiResponse.status) {
            Status.LOADING -> showProgressLoader(getString(R.string.scr_message_please_wait))
            Status.SUCCESS -> {
                dismissLoader()
                if (!apiResponse.data!!.isNull) {
                    LogUtil.printLog(tag, apiResponse.data.toString())
                    try {
                        if (tag.equals(
                                        DynamicAPIPath.POST_DOWNLOAD_FILE,
                                        ignoreCase = true
                                )
                        ) {

                            val responseModel = ObjectMapperProvider.fromJson(apiResponse.data.toString(), DownloadBitmapResponse::class.java)

                            if (responseModel != null && responseModel.isStatus) {
                                if (responseModel.metadata!![0].url?.length!! > 0) {
//                                    DownloadingPrintBitmapFromUrl().execute(
//                                            responseModel.metadata!![0].url
//                                    )
                                    appCompatImageViewFacsimile?.let {
                                        Glide.with(this@ViewFacsimileImageActivity)
                                                .load(responseModel.metadata!![0].url)
                                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                                .skipMemoryCache(true)
                                                //                .placeholder(R.drawable.placeholder)
                                                //                .error(R.drawable.imagenotfound)
                                                .into(it)
                                    }

                                }
                            }
                            dismissLoader()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        dismissLoader()
                    }
                }
            }
            Status.ERROR -> {
                dismissLoader()
            }

            else -> {}
        }
    }

}