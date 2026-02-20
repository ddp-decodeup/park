package com.parkloyalty.lpr.scan.ui.login.DatasetModel

import android.util.Log
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
class CitationDatasetModel @Inject constructor(private val service: Service) : ViewModel() {
    private val disposables = CompositeDisposable()
    private val disposablesForEquipmentInventoryAPI = CompositeDisposable()
    val response = MutableLiveData<ApiResponse>()
    val responseForEquipmentInventoryAPI = MutableLiveData<ApiResponse>()
    fun hitCitationDatasetApi(dropdownDatasetRequest: DropdownDatasetRequest?) {
        disposables.add(
            service.executeCitationDatasetAPI(dropdownDatasetRequest)
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

    fun hitCitationDatasetApiLoginPage(dropdownDatasetRequest: DropdownDatasetRequest?) {
        disposables.add(
            service.executeCitationDatasetAPILoginPage(dropdownDatasetRequest)
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

    fun callGetEquipmentInventoryAPI(dropdownDatasetRequest: DropdownDatasetRequest?) {
        disposablesForEquipmentInventoryAPI.add(
            service.executeCitationDatasetAPI(dropdownDatasetRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { d: Disposable? -> responseForEquipmentInventoryAPI.setValue(loading()) }
                .subscribe(
                    { result: JsonNode? ->
                        responseForEquipmentInventoryAPI.setValue(
                            success(
                                result!!
                            )
                        )
                    },
                    { throwable: Throwable? ->
                        responseForEquipmentInventoryAPI.setValue(
                            error(
                                throwable!!
                            )
                        )
                    }
                ))
    }

    override fun onCleared() {
        disposables.clear()
        disposablesForEquipmentInventoryAPI.clear()
    }
}