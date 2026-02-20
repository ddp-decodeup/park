package com.parkloyalty.lpr.scan.ui.uploadfacsimile

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import butterknife.ButterKnife
import com.parkloyalty.lpr.scan.R
import com.parkloyalty.lpr.scan.basecontrol.BaseActivity
import com.parkloyalty.lpr.scan.basecontrol.BaseApplication
import com.parkloyalty.lpr.scan.database.AppDatabase
import com.parkloyalty.lpr.scan.extensions.nullSafety
import com.parkloyalty.lpr.scan.interfaces.Constants.Companion.FILE_NAME_FACSIMILE_PRINT_BITMAP
import com.parkloyalty.lpr.scan.interfaces.CustomDialogHelper
import com.parkloyalty.lpr.scan.network.ApiResponse
import com.parkloyalty.lpr.scan.network.NetworkCheck
import com.parkloyalty.lpr.scan.network.Status
import com.parkloyalty.lpr.scan.ui.check_setup.model.lpr_model.UploadImageViewModel
import com.parkloyalty.lpr.scan.ui.check_setup.model.lpr_model.UploadImagesResponse
import com.parkloyalty.lpr.scan.ui.check_setup.model.ticket.*
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.*
import com.parkloyalty.lpr.scan.ui.login.model.*
import com.parkloyalty.lpr.scan.ui.reprint.ReprintReuploadActivity
import com.parkloyalty.lpr.scan.ui.ticket.model.AddImageRequest
import com.parkloyalty.lpr.scan.ui.ticket.model.AddImageViewModel
import com.parkloyalty.lpr.scan.ui.ticket.model.AddNotesResponse
import com.parkloyalty.lpr.scan.util.AppUtils
import com.parkloyalty.lpr.scan.util.LogUtil
import com.parkloyalty.lpr.scan.utils.ObjectMapperProvider
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.util.*
import kotlin.getValue

class MissingFacsimileActivity : BaseActivity(), CustomDialogHelper {

    private val mUploadImageViewModel: UploadImageViewModel? by viewModels()
    private val mAddFacsimileImageViewModel: AddImageViewModel? by viewModels()

    private var mFacsimileImagesLink: MutableList<String> = ArrayList()
    private var resultFacsimileImage:UnUploadFacsimileImage?=null

    private var mContext: Context? = null
    private var mDb: AppDatabase? = null
    private var imageUploadSuccessCount = 0
    private var mCitationNumberId: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_missing_facsimile)
        setFullScreenUI()
        ButterKnife.bind(this)
        mContext = this
        mDb = BaseApplication.instance?.getAppDatabase()
        addObservers()
        init()
        uploadMissingFacsimileImages()
    }

    private fun init() {
        mEventStartTimeStamp = AppUtils.getDateTime()
    }

    private fun addObservers() {
        mUploadImageViewModel?.response?.observe(this, uploadImageResponseObserver)
        mAddFacsimileImageViewModel!!.response.observe(this, addImageResponseObserver)
    }

    override fun removeObservers() {
        super.removeObservers()
        mUploadImageViewModel?.response?.observe(this, uploadImageResponseObserver)
        mAddFacsimileImageViewModel!!.response.removeObserver(addImageResponseObserver)
    }


    private val uploadImageResponseObserver = Observer { apiResponse: ApiResponse ->
        consumeResponseMissingFacsimileActivity(apiResponse, "MissingFacsimileActivityUploadImage")
    }

    private val addImageResponseObserver = Observer { apiResponse: ApiResponse ->
        consumeResponseMissingFacsimileActivity(apiResponse,  "MissingFacsimileActivityAddLinkOnServer")
    }

    //save Citation Layout Databse
    private fun uploadMissingFacsimileImages() {
        unUploadFacsimileImages().execute()
    }

    /**
     * only get status 0 citation which is facsimile not upload
     *
     */
    inner class unUploadFacsimileImages : AsyncTask<Void?, Int?, UnUploadFacsimileImage?>() {
        override fun doInBackground(vararg voids: Void?): UnUploadFacsimileImage? {
            try {
                var mFacsimileImage: UnUploadFacsimileImage? = mDb!!.dbDAO!!.getUnUploadFacsimile()
//                var mFacsimileImage: List<UnUploadFacsimileImage?>? = mDb!!.dbDAO!!.getUnUploadFacsimileAll()
                if (mFacsimileImage != null && mFacsimileImage!!.uploadedCitationId!!.isNotEmpty()) {
                    return mFacsimileImage
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
            return null
        }

        @SuppressLint("WrongThread")
        override fun onPostExecute(result: UnUploadFacsimileImage?) {
            try {
                if (result != null) {
                    result?.let {
                        if (it.imageLink!!.isNotEmpty()) {
                            resultFacsimileImage = it
                            mFacsimileImagesLink.add(it.imageLink!!)
                            callUploadImagesUrl()
                        } else {
                            callUploadImages(it)
                        }
                    }
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
    }


    /* Call Api For update profile */
    private fun callUploadImages(result: UnUploadFacsimileImage ) {
        resultFacsimileImage = result
        val file: File? = File(result!!.imagePath)
        if(file!!.exists()) {
            val num: Int = (result.imageCount + 1)
            if (NetworkCheck.isInternetAvailable(this@MissingFacsimileActivity)) {
                val requestFile =
                    RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file!!)
                val files = MultipartBody.Part.createFormData(
                    "files",
                    if (file != null) file.name else "",
                    requestFile
                )
                var mDropdownList =
                    arrayOf(result!!.uploadedCitationId + "_" + num + "_" + FILE_NAME_FACSIMILE_PRINT_BITMAP)
                val mRequestBodyType =
                    RequestBody.create("text/plain".toMediaTypeOrNull(), "CitationImages")
                mUploadImageViewModel?.hitUploadImagesApi(
                    mDropdownList,
                    mRequestBodyType,
                    files
                )
            } else {
                LogUtil.printToastMSG(
                    applicationContext,
                    getString(R.string.err_msg_connection_was_refused)
                )
            }
        }else{
            val mStartActivity =
                Intent(this@MissingFacsimileActivity, ReprintReuploadActivity::class.java)
            mStartActivity.putExtra("ticket_id", result!!.uploadedCitationId)
            mStartActivity.putExtra("ticket_number", result!!.ticketNumberText)
            mStartActivity.putExtra("image_size", "1")
            startActivity(mStartActivity)
            finish()
        }
    }


    /* Call Api For add un-upload images*/
    private fun callUploadImagesUrl() {
        if (NetworkCheck.isInternetAvailable(this@MissingFacsimileActivity)) {
            if (resultFacsimileImage != null) {
                val endPoint = "${resultFacsimileImage!!.uploadedCitationId}/images"
                val addImageRequest = AddImageRequest()
                addImageRequest.images = mFacsimileImagesLink
                mAddFacsimileImageViewModel!!.hitAddImagesApi(addImageRequest, endPoint)
            }
        } else {
            LogUtil.printToastMSG(
                applicationContext,
                getString(R.string.err_msg_connection_was_refused)
            )
        }
    }


    /*Api response */
    private fun consumeResponseMissingFacsimileActivity(apiResponse: ApiResponse, tag: String) {
        when (apiResponse.status) {
            Status.LOADING -> showProgressLoader(getString(R.string.scr_message_please_wait))
            Status.SUCCESS -> {
                if (!apiResponse.data!!.isNull) {
                    LogUtil.printLog(tag, apiResponse.data.toString())
                    try {
                        if (tag.equals("MissingFacsimileActivityUploadImage", ignoreCase = true)) {
                            try {

                                val responseModel = ObjectMapperProvider.fromJson(apiResponse.data.toString(), UploadImagesResponse::class.java)

                                if (responseModel != null && responseModel.status.nullSafety()) {
                                    if (responseModel.data != null && responseModel.data?.size!! > 0 && responseModel.data!![0].response != null && responseModel.data!![0].response?.links != null && responseModel.data!![0].response?.links?.size!! > 0) {
                                        mFacsimileImagesLink.add(responseModel.data!![0].response?.links!![0])
                                        callUploadImagesUrl()
                                    }
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                        if (tag.equals("MissingFacsimileActivityAddLinkOnServer", ignoreCase = true)) {
//                            dismissLoader()


                            val responseModel = ObjectMapperProvider.fromJson(apiResponse.data.toString(), AddNotesResponse::class.java)

                            if (responseModel != null && responseModel.isSuccess) {
                                resultFacsimileImage?.dateTime?.let {
                                    mDb!!.dbDAO!!.updateFacsimileStatus(
                                        1, resultFacsimileImage!!.ticketNumberText!!.toString(),
                                        it
                                    )
                                }

//                                mDb!!.dbDAO!!.deleteFacsimileData(resultFacsimileImage!!.ticketNumber!!.toString())
                                AppUtils.showCustomAlertDialog(
                                    mContext,
                                    "Uploaded Successfully",
                                     "",
                                    getString(R.string.alt_lbl_OK),
                                    getString(R.string.scr_btn_cancel),
                                    this@MissingFacsimileActivity
                                )

                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

            }
            Status.ERROR -> {
                dismissLoader()
                LogUtil.printToastMSG(
                    this@MissingFacsimileActivity,
                    tag + " " + getString(R.string.err_msg_connection_was_refused)
                )
            }

            else -> {}
        }
    }
    override fun onYesButtonClick() {
        finish()
    }
    override fun onNoButtonClick() {
        finish()
    }
    override fun onYesButtonClickParam(msg: String?) {
        if(msg.equals("Uploaded Successfully"))
        {
            finish()
        }
    }
}