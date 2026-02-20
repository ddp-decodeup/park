package com.parkloyalty.lpr.scan.ui.check_setup.model

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fasterxml.jackson.databind.JsonNode
import com.parkloyalty.lpr.scan.network.ApiResponse
import com.parkloyalty.lpr.scan.network.ApiResponse.Companion.error
import com.parkloyalty.lpr.scan.network.ApiResponse.Companion.errorPermitData
import com.parkloyalty.lpr.scan.network.ApiResponse.Companion.loading
import com.parkloyalty.lpr.scan.network.ApiResponse.Companion.success
import com.parkloyalty.lpr.scan.network.ApiResponse.Companion.successPermitData
import com.parkloyalty.lpr.scan.network.Service
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class DataFromLprViewModel @Inject constructor(private val service: Service) : ViewModel() {
    private val disposables = CompositeDisposable()
    val response = MutableLiveData<ApiResponse>()
    fun hitGetDataFromLprApi(mDataFromLprRequest: DataFromLprRequest?) {
        disposables.add(
            service.executeDataFromLprAPI(mDataFromLprRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { d: Disposable? -> response.postValue(loading()) }
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

    private val disposableForLprPermitData = CompositeDisposable()
    val responseForLprPermitData = MediatorLiveData<ApiResponse>()

    fun hitGetPermitDataFromLprApi(
        mDataFromLprRequest: DataFromLprRequest?,
        type: String,
        lprNumber: String
    ) {
        disposableForLprPermitData.add(
            service.executeDataFromLprAPI(mDataFromLprRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { d: Disposable? ->
                    responseForLprPermitData.postValue(loading())
                }
                .subscribe(
                    { result: JsonNode? ->
                        responseForLprPermitData.setValue(
                            successPermitData(
                                data = result!!,
                                lprNumber = lprNumber,
                                type = type
                            )
                        )
                    },
                    { throwable: Throwable? ->
                        responseForLprPermitData.setValue(
                            errorPermitData(
                                error = throwable!!,
                                lprNumber = lprNumber,
                                type = type
                            )
                        )
                    }
                ))
    }

    private val disposableForLprPaymentData = CompositeDisposable()
    val responseForLprPaymentData = MediatorLiveData<ApiResponse>()

    fun hitGetPaymentDataFromLprApi(
        mDataFromLprRequest: DataFromLprRequest?,
        type: String,
        lprNumber: String
    ) {
        disposableForLprPaymentData.add(
            service.executeDataFromLprAPI(mDataFromLprRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { d: Disposable? ->
                    responseForLprPaymentData.postValue(loading())
                }
                .subscribe(
                    { result: JsonNode? ->
                        responseForLprPaymentData.setValue(
                            successPermitData(
                                data = result!!,
                                lprNumber = lprNumber,
                                type = type
                            )
                        )
                    },
                    { throwable: Throwable? ->
                        responseForLprPaymentData.setValue(
                            errorPermitData(
                                error = throwable!!,
                                lprNumber = lprNumber,
                                type = type
                            )
                        )
                    }
                ))
    }

    private val disposableForLprScofflawData = CompositeDisposable()
    val responseForLprScofflawData = MediatorLiveData<ApiResponse>()

    fun hitGetScofflawDataFromLprApi(
        mDataFromLprRequest: DataFromLprRequest?,
        type: String,
        lprNumber: String
    ) {
        disposableForLprScofflawData.add(
            service.executeDataFromLprAPI(mDataFromLprRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { d: Disposable? ->
                    responseForLprScofflawData.postValue(loading())
                }
                .subscribe(
                    { result: JsonNode? ->
                        responseForLprScofflawData.setValue(
                            successPermitData(
                                data = result!!,
                                lprNumber = lprNumber,
                                type = type
                            )
                        )
                    },
                    { throwable: Throwable? ->
                        responseForLprScofflawData.setValue(
                            errorPermitData(
                                error = throwable!!,
                                lprNumber = lprNumber,
                                type = type
                            )
                        )
                    }
                ))
    }

    private val disposableForLprExemptData = CompositeDisposable()
    val responseForLprExemptData = MediatorLiveData<ApiResponse>()

    fun hitGetExemptDataFromLprApi(
        mDataFromLprRequest: DataFromLprRequest?,
        type: String,
        lprNumber: String
    ) {
        disposableForLprExemptData.add(
            service.executeDataFromLprAPI(mDataFromLprRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { d: Disposable? ->
                    responseForLprExemptData.postValue(loading())
                }
                .subscribe(
                    { result: JsonNode? ->
                        responseForLprExemptData.setValue(
                            successPermitData(
                                data = result!!,
                                lprNumber = lprNumber,
                                type = type
                            )
                        )
                    },
                    { throwable: Throwable? ->
                        responseForLprExemptData.setValue(
                            errorPermitData(
                                error = throwable!!,
                                lprNumber = lprNumber,
                                type = type
                            )
                        )
                    }
                ))
    }

    private val disposableForLprStolenData = CompositeDisposable()
    val responseForLprStolenData = MediatorLiveData<ApiResponse>()

    fun hitGetStolenDataFromLprApi(
        mDataFromLprRequest: DataFromLprRequest?,
        type: String,
        lprNumber: String
    ) {
        disposableForLprStolenData.add(
            service.executeDataFromLprAPI(mDataFromLprRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { d: Disposable? ->
                    responseForLprStolenData.postValue(loading())
                }
                .subscribe(
                    { result: JsonNode? ->
                        responseForLprStolenData.setValue(
                            successPermitData(
                                data = result!!,
                                lprNumber = lprNumber,
                                type = type
                            )
                        )
                    },
                    { throwable: Throwable? ->
                        responseForLprStolenData.setValue(
                            errorPermitData(
                                error = throwable!!,
                                lprNumber = lprNumber,
                                type = type
                            )
                        )
                    }
                ))
    }

    private val disposableForCameraRawFeedData = CompositeDisposable()
    val responseForCameraRawFeedData = MediatorLiveData<ApiResponse>()

    fun hitGetCameraRawFeedApi(
        mDataFromLprRequest: DataFromLprRequest?,
        type: String,
        lprNumber: String
    ) {
        disposableForCameraRawFeedData.add(
            service.executeDataFromLprAPI(mDataFromLprRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { d: Disposable? ->
                    responseForCameraRawFeedData.postValue(loading())
                }
                .subscribe(
                    { result: JsonNode? ->
                        responseForCameraRawFeedData.setValue(
                            successPermitData(
                                data = result!!,
                                lprNumber = lprNumber,
                                type = type
                            )
                        )
                    },
                    { throwable: Throwable? ->
                        responseForCameraRawFeedData.setValue(
                            errorPermitData(
                                error = throwable!!,
                                lprNumber = lprNumber,
                                type = type
                            )
                        )
                    }
                ))
    }

    override fun onCleared() {
        disposables.clear()
        disposableForLprPermitData.clear()
        disposableForLprPaymentData.clear()
        disposableForLprScofflawData.clear()
        disposableForLprExemptData.clear()
        disposableForLprStolenData.clear()
        disposableForCameraRawFeedData.clear()
    }
}