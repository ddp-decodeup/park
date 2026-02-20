package com.parkloyalty.lpr.scan.ui.check_setup.model.lpr_model

import android.content.Context
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fasterxml.jackson.databind.JsonNode
import com.parkloyalty.lpr.scan.R
import com.parkloyalty.lpr.scan.network.ApiResponse
import com.parkloyalty.lpr.scan.network.ApiResponse.Companion.error
import com.parkloyalty.lpr.scan.network.ApiResponse.Companion.loading
import com.parkloyalty.lpr.scan.network.ApiResponse.Companion.success
import com.parkloyalty.lpr.scan.network.Resource
import com.parkloyalty.lpr.scan.network.Service
import com.parkloyalty.lpr.scan.network.models.ScannedImageUploadResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class UploadImageViewModel @Inject constructor(private val service: Service) : ViewModel() {
    private val disposables = CompositeDisposable()
    val response = MutableLiveData<ApiResponse>()
    val responseTimingImageUpload = MutableLiveData<ApiResponse>()
    val responseUploadAllImages = MutableLiveData<ApiResponse>()

    val uploadAllImagesAPIStatus: MediatorLiveData<Any> by lazy {
        MediatorLiveData<Any>()
    }

    fun hitUploadImagesApi(
        mIDList: Array<String>?, mRequestBodyType: RequestBody?, image: MultipartBody.Part?
    ) {
        disposables.add(
            service.executeUploadImagesAPI(mIDList, mRequestBodyType, image)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { d: Disposable? -> response.setValue(loading()) }
                .subscribe(
                    { result: JsonNode? ->
                        response.setValue(
                            success(
                                result!!
                            )
                        )
                    },
                    { throwable: Throwable? ->
                        response.setValue(
                            error(
                                throwable!!
                            )
                        )
                    }
                ))
    }

    private val disposablesTicketDetailsScreen = CompositeDisposable()
    val responseTicketDetailsScreen = MutableLiveData<ApiResponse>()

fun hitUploadImagesApiForTicketDetailsScreen(
        mIDList: Array<String>?, mRequestBodyType: RequestBody?, image: MultipartBody.Part?
    ) {
    disposablesTicketDetailsScreen.add(
            service.executeUploadImagesAPI(mIDList, mRequestBodyType, image)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { d: Disposable? -> responseTicketDetailsScreen.setValue(loading()) }
                .subscribe(
                    { result: JsonNode? ->
                        responseTicketDetailsScreen.setValue(
                            success(
                                result!!
                            )
                        )
                    },
                    { throwable: Throwable? ->
                        responseTicketDetailsScreen.setValue(
                            error(
                                throwable!!
                            )
                        )
                    }
                ))
    }

    private val disposablesBaseActivity = CompositeDisposable()
    val responseBaseActivity = MutableLiveData<ApiResponse>()

fun hitUploadImagesApiForBaseActivity(
        mIDList: Array<String>?, mRequestBodyType: RequestBody?, image: MultipartBody.Part?
    ) {
    disposablesBaseActivity.add(
            service.executeUploadImagesAPI(mIDList, mRequestBodyType, image)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { d: Disposable? -> responseBaseActivity.setValue(loading()) }
                .subscribe(
                    { result: JsonNode? ->
                        responseBaseActivity.setValue(
                            success(
                                result!!
                            )
                        )
                    },
                    { throwable: Throwable? ->
                        responseBaseActivity.setValue(
                            error(
                                throwable!!
                            )
                        )
                    }
                ))
    }


    private val disposablesGalleryViewActivity = CompositeDisposable()
    val responseGalleryViewActivity = MutableLiveData<ApiResponse>()

fun hitUploadImagesApiForGalleryViewActivity(
        mIDList: Array<String>?, mRequestBodyType: RequestBody?, image: MultipartBody.Part?
    ) {
    disposablesGalleryViewActivity.add(
            service.executeUploadImagesAPI(mIDList, mRequestBodyType, image)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { d: Disposable? -> responseGalleryViewActivity.setValue(loading()) }
                .subscribe(
                    { result: JsonNode? ->
                        responseGalleryViewActivity.setValue(
                            success(
                                result!!
                            )
                        )
                    },
                    { throwable: Throwable? ->
                        responseGalleryViewActivity.setValue(
                            error(
                                throwable!!
                            )
                        )
                    }
                ))
    }


    private val disposablesTextFile = CompositeDisposable()
    val responseTextFile = MutableLiveData<ApiResponse>()

    fun hitUploadTextFileApi(
        mIDList: Array<String>?, mRequestBodyType: RequestBody?, image: MultipartBody.Part?
    ) {
        disposablesTextFile.add(
            service.executeUploadTextAPI(mIDList, mRequestBodyType, image)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { d: Disposable? -> responseTextFile.setValue(loading()) }
                .subscribe(
                    { result: JsonNode? ->
                        responseTextFile.setValue(
                            success(
                                result!!
                            )
                        )
                    },
                    { throwable: Throwable? ->
                        responseTextFile.setValue(
                            error(
                                throwable!!
                            )
                        )
                    }
                ))
    }
    fun hitUploadImagesApiBC(
        mIDList: Array<String>?, mRequestBodyType: RequestBody?, image: MultipartBody.Part?
    ) {
        disposables.add(
            service.executeUploadImagesAPIBC(mIDList, mRequestBodyType, image)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { d: Disposable? -> response.setValue(loading()) }
                .subscribe(
                    { result: JsonNode? ->
                        response.postValue(
                            success(
                                result!!
                            )
                        )
                    },
                    { throwable: Throwable? ->
                        response.postValue(
                            error(
                                throwable!!
                            )
                        )
                    }
                ))
    }

    fun hitUploadTimingImagesApi(
        mIDList: Array<String>?, mRequestBodyType: RequestBody?, image: MultipartBody.Part?
    ) {
        disposables.add(
            service.executeUploadImagesAPI(mIDList, mRequestBodyType, image)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { d: Disposable? -> responseTimingImageUpload.setValue(loading()) }
                .subscribe(
                    { result: JsonNode? ->
                        responseTimingImageUpload.setValue(
                            success(
                                result!!
                            )
                        )
                    },
                    { throwable: Throwable? ->
                        responseTimingImageUpload.setValue(
                            error(
                                throwable!!
                            )
                        )
                    }
                ))
    }

    fun hitUploadAllImagesApi(
        mIDList: List<String?>, mRequestBodyType: RequestBody?, image: List<MultipartBody.Part?>
    ) {
        disposables.add(
            service.executeUploadAllImagesAPI(mIDList, mRequestBodyType, image)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { d: Disposable? -> responseUploadAllImages.setValue(loading()) }
                .subscribe(
                    { result: JsonNode? ->
                        responseUploadAllImages.setValue(
                            success(
                                result!!
                            )
                        )
                    },
                    { throwable: Throwable? ->
                        responseUploadAllImages.setValue(
                            error(
                                throwable!!
                            )
                        )
                    }
                ))
    }

    fun callUploadScannedImagesAPI(mContext: Context, data: Array<String?>,
                                   files: List<MultipartBody.Part?>, imageUploadType : String,screen : String) {
        val callUploadScannedImagesAPICall = service.executeUploadAllImagesAPI(data, files, imageUploadType)

        if(screen.equals("LprDetails2Activity")) {
        uploadAllImagesAPIStatus.value = Resource.Loading<Boolean>(true)
        }else {
            uploadAllImagesAPIStatus.value = loading()
        }
        callUploadScannedImagesAPICall.enqueue(object : Callback<ScannedImageUploadResponse> {
            override fun onFailure(call: Call<ScannedImageUploadResponse>, t: Throwable) {
                t.printStackTrace()
//                uploadAllImagesAPIStatus.value = Resource.Loading<Boolean>(false)
//                uploadAllImagesAPIStatus.value = Resource.Loading<Boolean>(false)
                uploadAllImagesAPIStatus.value =
                    Resource.Error<String>(mContext.getString(R.string.err_msg_something_went_wrong))
            }

            override fun onResponse(
                call: Call<ScannedImageUploadResponse>,
                response: Response<ScannedImageUploadResponse>
            ) {
                uploadAllImagesAPIStatus.value = Resource.Loading<Boolean>(false)

                if (response.isSuccessful) {
                    val mBean: ScannedImageUploadResponse? = response.body()
                    uploadAllImagesAPIStatus.value = Resource.Success(mBean)
                } else {
                    uploadAllImagesAPIStatus.value =
                        Resource.Error<String>(mContext.getString(R.string.err_msg_something_went_wrong))
                }
            }
        })
    }

    override fun onCleared() {
        disposables.clear()
        disposablesTicketDetailsScreen.clear()
        disposablesBaseActivity.clear()
        disposablesTextFile.clear()
        disposablesGalleryViewActivity.clear()
    }



}