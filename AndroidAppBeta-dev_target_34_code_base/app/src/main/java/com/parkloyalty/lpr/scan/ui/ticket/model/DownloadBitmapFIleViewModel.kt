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
class DownloadBitmapFIleViewModel @Inject constructor(private val service: Service) : ViewModel() {
    private val disposables = CompositeDisposable()
    val response = MutableLiveData<ApiResponse>()
    val responseHeaderFile = MutableLiveData<ApiResponse>()
    val responseFooterFile = MutableLiveData<ApiResponse>()
    fun downloadBitmapAPI(downloadBitmapRequest: DownloadBitmapRequest?) {
        disposables.add(
            service.executeDownloadBitmapAPI(downloadBitmapRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { d: Disposable? -> response.setValue(loading()) }
                .subscribe(
                    Consumer { result: JsonNode? ->
                        response.setValue(
                            success(
                                result!!
                            )
                        )
                    },
                    Consumer { throwable: Throwable? ->
                        response.setValue(
                            error(
                                throwable!!
                            )
                        )
                    }
                ))
    }


    /**
     * Function used to download header image for facsimile image
     */
    fun downloadHeaderImageURL(downloadRequest: DownloadBitmapRequest?) {
        disposables.add(
            service.executeDownloadBitmapAPI(downloadRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { d: Disposable? -> responseHeaderFile.setValue(loading()) }
                .subscribe(
                    { result: JsonNode? ->
                        responseHeaderFile.setValue(
                            success(
                                result!!
                            )
                        )
                    },
                    { throwable: Throwable? ->
                        responseHeaderFile.setValue(
                            error(
                                throwable!!
                            )
                        )
                    }
                ))
    }

    /**
     * Function used to download footer image for facsimile image
     */
    fun downloadFooterImageURL(downloadRequest: DownloadBitmapRequest?) {
        disposables.add(
            service.executeDownloadBitmapAPI(downloadRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { d: Disposable? -> responseFooterFile.setValue(loading()) }
                .subscribe(
                    { result: JsonNode? ->
                        responseFooterFile.setValue(
                            success(
                                result!!
                            )
                        )
                    },
                    { throwable: Throwable? ->
                        responseFooterFile.setValue(
                            error(
                                throwable!!
                            )
                        )
                    }
                ))
    }

    override fun onCleared() {
        disposables.clear()
    }
}