package com.parkloyalty.lpr.scan.ui.ticket.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fasterxml.jackson.databind.JsonNode
import com.parkloyalty.lpr.scan.network.ApiResponse
import com.parkloyalty.lpr.scan.network.ApiResponse.Companion.error
import com.parkloyalty.lpr.scan.network.ApiResponse.Companion.loading
import com.parkloyalty.lpr.scan.network.ApiResponse.Companion.success
import com.parkloyalty.lpr.scan.network.Service
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class AddImageViewModel @Inject constructor(private val service: Service) : ViewModel() {
    private val disposables = CompositeDisposable()
    val response = MutableLiveData<ApiResponse>()
    fun hitAddImagesApi(mIDList: AddImageRequest?, endPoint: String?) {
        disposables.add(
            service.executeAddImagesAPI(mIDList, endPoint!!)
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

    private val disposablesTicketDetailSCreen = CompositeDisposable()
    val responseTicketDetailSCreen = MutableLiveData<ApiResponse>()

    fun hitAddImagesApiTicketDetalsScreen(mIDList: AddImageRequest?, endPoint: String?) {
        disposablesTicketDetailSCreen.add(
            service.executeAddImagesAPI(mIDList, endPoint!!)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { d: Disposable? -> responseTicketDetailSCreen.setValue(loading()) }
                .subscribe(
                    { result: JsonNode? ->
                        responseTicketDetailSCreen.setValue(
                            success(
                                result!!
                            )
                        )
                    },
                    { throwable: Throwable? ->
                        responseTicketDetailSCreen.setValue(
                            error(
                                throwable!!
                            )
                        )
                    }
                ))
    }


    private val disposablesBaseActivitySCreen = CompositeDisposable()
    val responseBaseActivityActivity = MutableLiveData<ApiResponse>()

    fun hitAddImagesApiBaseActivityScreen(mIDList: AddImageRequest?, endPoint: String?) {
        disposablesBaseActivitySCreen.add(
            service.executeAddImagesAPI(mIDList, endPoint!!)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { d: Disposable? -> responseBaseActivityActivity.setValue(loading()) }
                .subscribe(
                    { result: JsonNode? ->
                        responseBaseActivityActivity.setValue(
                            success(
                                result!!
                            )
                        )
                    },
                    { throwable: Throwable? ->
                        responseBaseActivityActivity.setValue(
                            error(
                                throwable!!
                            )
                        )
                    }
                ))
    }


    private val disposablesGalleryViewActivitySCreen = CompositeDisposable()
    val responseGalleryViewActivityActivity = MutableLiveData<ApiResponse>()

    fun hitAddImagesApiGalleryViewActivityScreen(mIDList: AddImageRequest?, endPoint: String?) {
        disposablesGalleryViewActivitySCreen.add(
            service.executeAddImagesAPI(mIDList, endPoint!!)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { d: Disposable? -> responseGalleryViewActivityActivity.setValue(loading()) }
                .subscribe(
                    { result: JsonNode? ->
                        responseGalleryViewActivityActivity.setValue(
                            success(
                                result!!
                            )
                        )
                    },
                    { throwable: Throwable? ->
                        responseGalleryViewActivityActivity.setValue(
                            error(
                                throwable!!
                            )
                        )
                    }
                ))
    }

    override fun onCleared() {
        disposables.clear()
        disposablesTicketDetailSCreen.clear()
        disposablesBaseActivitySCreen.clear()
        disposablesGalleryViewActivitySCreen.clear()
    }
}