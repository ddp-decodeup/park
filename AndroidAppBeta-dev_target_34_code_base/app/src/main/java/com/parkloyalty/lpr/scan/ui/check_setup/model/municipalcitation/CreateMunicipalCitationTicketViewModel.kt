package com.parkloyalty.lpr.scan.ui.check_setup.model.municipalcitation

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
import io.reactivex.schedulers.Schedulers
import kotlin.String
import javax.inject.Inject

@HiltViewModel
class CreateMunicipalCitationTicketViewModel @Inject constructor(private val service: Service) : ViewModel() {
    private val disposables = CompositeDisposable()
    val response = MutableLiveData<ApiResponse>()
    val responseGetMunicipalCitationTicketHistoryAPI = MutableLiveData<ApiResponse>()

    fun hitCreateMunicipalCitationTicketApi(createMunicipalCitationTicketRequest: CreateMunicipalCitationTicketRequest?) {
        disposables.add(
            service.executeCreateMunicipalCitationTicketAPI(createMunicipalCitationTicketRequest)
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

    fun executeGetMunicipalCitationTicketHistoryAPI(values: Map<String, String>) {
        disposables.add(
            service.executeGetMunicipalCitationTicketHistoryAPI(values)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { d: Disposable? ->
                    responseGetMunicipalCitationTicketHistoryAPI.setValue(
                        loading()
                    )
                }
                .subscribe(
                    { result: JsonNode? ->
                        responseGetMunicipalCitationTicketHistoryAPI.postValue(
                            success(
                                result!!
                            )
                        )
                    },
                    { throwable: Throwable? ->
                        responseGetMunicipalCitationTicketHistoryAPI.postValue(
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