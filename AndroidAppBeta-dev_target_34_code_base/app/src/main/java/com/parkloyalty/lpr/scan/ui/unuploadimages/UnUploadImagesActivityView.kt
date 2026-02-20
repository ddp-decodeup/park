package com.parkloyalty.lpr.scan.ui.unuploadimages

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.ButterKnife
import com.parkloyalty.lpr.scan.R
import com.parkloyalty.lpr.scan.basecontrol.BaseActivity
import com.parkloyalty.lpr.scan.basecontrol.BaseApplication
import com.parkloyalty.lpr.scan.database.AppDatabase
import com.parkloyalty.lpr.scan.extensions.nullSafety
import com.parkloyalty.lpr.scan.network.ApiResponse
import com.parkloyalty.lpr.scan.network.NetworkCheck
import com.parkloyalty.lpr.scan.network.Status
import com.parkloyalty.lpr.scan.ui.check_setup.model.lpr_model.UploadImageViewModel
import com.parkloyalty.lpr.scan.ui.check_setup.model.lpr_model.UploadImagesResponse
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.DropdownDatasetResponse
import com.parkloyalty.lpr.scan.ui.login.DatasetModel.UnUploadFacsimileImage
import com.parkloyalty.lpr.scan.ui.ticket.model.AddImageRequest
import com.parkloyalty.lpr.scan.ui.ticket.model.AddImageViewModel
import com.parkloyalty.lpr.scan.ui.ticket.model.AddNotesResponse
import com.parkloyalty.lpr.scan.util.LogUtil
import com.parkloyalty.lpr.scan.utils.ObjectMapperProvider
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.util.ArrayList
import kotlin.getValue

class UnUploadImagesActivityView : BaseActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var mDb: AppDatabase
    private lateinit var unUploadImagesAdapter: UnUploadImagesAdapter

    private val addFacsimileImageLinkViewModel: AddImageViewModel? by viewModels()
    private val mFacsimileUploadImageViewModel: UploadImageViewModel? by viewModels()

    private var mFacsimileImagesLink: MutableList<String> = ArrayList()
    private var resultFacsimileImage: UnUploadFacsimileImage? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_un_upload_images_view)

        recyclerView = findViewById(R.id.recyclerView)
        mDb = BaseApplication.instance?.getAppDatabase()!!
        ButterKnife.bind(this)
        setToolbar()
        addObservers()
        setupRecyclerView()
        loadImages()
    }

    private fun setupRecyclerView() {
        val layoutManager = GridLayoutManager(this, 3)

        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (recyclerView.adapter?.getItemViewType(position)) {
                    0 -> 3 // Header spans 3 columns
                    else -> 1 // Image takes 1 column
                }
            }
        }
        recyclerView.layoutManager = layoutManager
    }

    private fun loadImages() {
        lifecycleScope.launch {
            refreshAdapterData()
        }
    }

    private fun refreshAdapterData() {
        lifecycleScope.launch {
            val mFacsimileImages: List<UnUploadFacsimileImage> =
                mDb.dbDAO!!.getUnUploadFacsimileAllData().filter { File(it.imagePath ?: "").exists() }

            val items = mutableListOf<UnUploadImageSealedItem>()
            var lastTicket: String? = null

            mFacsimileImages.forEach { image ->
                if (lastTicket != image.ticketNumberText) {
                    val header = UnUploadImageSealedItem.Header(
                        ticketNumber = image.ticketNumberText ?: "",
                        lpNumber = image.lprNumber ?: ""
                    )
                    items.add(header)
                    lastTicket = image.ticketNumberText
                }

                image.imagePath?.let { path ->
                    val uploaded = image.status == 1
                    items.add(UnUploadImageSealedItem.Image(path, uploaded, image))
                }
            }

            if (!::unUploadImagesAdapter.isInitialized) {
                unUploadImagesAdapter = UnUploadImagesAdapter(items) { clickedItem ->
                    if (clickedItem is UnUploadImageSealedItem.Image) {
                        try {
                            if (clickedItem.mObject.imageLink?.isNotEmpty() == true) {
                                resultFacsimileImage = clickedItem.mObject
                                mFacsimileImagesLink.add(clickedItem.mObject.imageLink!!)
                                callUploadImagesUrl()
                            } else {
                                callUploadImages(clickedItem.mObject, "CitationImages")
                            }
                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                        }
                        Toast.makeText(this@UnUploadImagesActivityView, "Clicked: ${clickedItem.path}", Toast.LENGTH_SHORT).show()
                    }
                }
                recyclerView.adapter = unUploadImagesAdapter
            } else {
                unUploadImagesAdapter.submitList(items)
            }
        }
    }

    private fun setToolbar() {
        // ... (toolbar initialization logic, no change)
    }

    private fun addObservers() {
        mFacsimileUploadImageViewModel?.responseGalleryViewActivity?.observe(this@UnUploadImagesActivityView, addFacsimileImageObserver)
        addFacsimileImageLinkViewModel?.responseGalleryViewActivityActivity?.observe(this@UnUploadImagesActivityView, mFacsimileUploadLinkImageObserver)
    }

    override fun removeObservers() {
        super.removeObservers()
        mFacsimileUploadImageViewModel?.responseGalleryViewActivity?.removeObserver(addFacsimileImageObserver)
        addFacsimileImageLinkViewModel?.responseGalleryViewActivityActivity?.removeObserver(mFacsimileUploadLinkImageObserver)
    }

    private val mFacsimileUploadLinkImageObserver = Observer { apiResponse: ApiResponse ->
        consumeResponse(apiResponse, "IMAGE_LINK_UPLOADED")
    }

    private val addFacsimileImageObserver = Observer { apiResponse: ApiResponse ->
        consumeResponse(apiResponse, "IMAGE_UPLOADED")
    }

    private fun callUploadImages(result: UnUploadFacsimileImage, folderName: String) {
        resultFacsimileImage = result
        val file: File? = File(result.imagePath)
        val num: Int = (result.imageCount + 1)
        if (NetworkCheck.isInternetAvailable(this@UnUploadImagesActivityView)) {
            val requestFile = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file!!)
            val files = MultipartBody.Part.createFormData(
                "files",
                if (file != null) file.name else "",
                requestFile
            )
            val mDropdownList = if (file!!.name.contains("_" + com.parkloyalty.lpr.scan.interfaces.Constants.FILE_NAME_FACSIMILE_PRINT_BITMAP)) {
                arrayOf(result.uploadedCitationId + "_" + num + "_" + com.parkloyalty.lpr.scan.interfaces.Constants.FILE_NAME_FACSIMILE_PRINT_BITMAP)
            } else {
                arrayOf(result.uploadedCitationId + "_" + num + "_" + result.dateTime)
            }
            val mRequestBodyType =
                RequestBody.create("text/plain".toMediaTypeOrNull(), "CitationImages")
            mFacsimileUploadImageViewModel?.hitUploadImagesApiForGalleryViewActivity(
                mDropdownList,
                mRequestBodyType,
                files
            )
        } else {
            LogUtil.printToastMSG(applicationContext, getString(R.string.err_msg_connection_was_refused))
        }
    }

    private fun callUploadImagesUrl() {
        if (NetworkCheck.isInternetAvailable(this@UnUploadImagesActivityView)) {
            if (resultFacsimileImage != null) {
                val endPoint = "${resultFacsimileImage!!.uploadedCitationId}/images"
                val addImageRequest = AddImageRequest()
                addImageRequest.images = mFacsimileImagesLink
                addFacsimileImageLinkViewModel!!.hitAddImagesApiGalleryViewActivityScreen(addImageRequest, endPoint)
            }
        } else {
            LogUtil.printToastMSG(applicationContext, getString(R.string.err_msg_connection_was_refused))
        }
    }

    private fun consumeResponse(apiResponse: ApiResponse, tag: String) {
        when (apiResponse.status) {
            Status.LOADING -> {
                showProgressLoader(getString(R.string.scr_message_please_wait))
            }
            Status.SUCCESS -> {
                dismissLoader()
                if (!apiResponse.data!!.isNull) {
                    if (tag.equals("IMAGE_UPLOADED", ignoreCase = true)) {
                        try {


                            val responseModel = ObjectMapperProvider.fromJson(apiResponse.data.toString(), UploadImagesResponse::class.java)

                            if (responseModel.status.nullSafety()) {
                                if (responseModel.data?.size!! > 0 && responseModel.data!![0].response != null && responseModel.data!![0].response?.links?.size!! > 0) {
                                    mFacsimileImagesLink.add(responseModel.data!![0].response?.links!![0])
                                    callUploadImagesUrl()
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    if (tag.equals("IMAGE_LINK_UPLOADED", ignoreCase = true)) {
                        try {

                            val responseModel = ObjectMapperProvider.fromJson(apiResponse.data.toString(), AddNotesResponse::class.java)

                            if (responseModel.isSuccess) {
                                resultFacsimileImage?.dateTime?.let {
                                    getMyDatabase()?.dbDAO?.updateFacsimileStatus(
                                        1,
                                        resultFacsimileImage?.ticketNumberText.toString(),
                                        it
                                    )
                                    // Refresh the adapter data after a successful upload
                                    refreshAdapterData()
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
            Status.ERROR -> {
                dismissLoader()
                LogUtil.printLog("BaseActivity", apiResponse.error.toString())
            }
            else -> {
                // Do nothing
            }
        }
    }
}