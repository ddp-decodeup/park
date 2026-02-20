package com.parkloyalty.lpr.scan.ui.login.DatasetModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fasterxml.jackson.databind.JsonNode
import com.parkloyalty.lpr.scan.network.ApiResponse
import com.parkloyalty.lpr.scan.network.ApiResponse.Companion.error
import com.parkloyalty.lpr.scan.network.ApiResponse.Companion.loading
import com.parkloyalty.lpr.scan.network.ApiResponse.Companion.success
import com.parkloyalty.lpr.scan.network.DynamicAPIPath
import com.parkloyalty.lpr.scan.network.Service
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class CitationLayoutViewModel @Inject constructor(private val service: Service) : ViewModel() {
    private val disposables = CompositeDisposable()
    val response = MutableLiveData<ApiResponse>()
    val municipalCitationResponse = MutableLiveData<ApiResponse>()
    fun hitGetCitationLayoutApi() {
        disposables.add(
            service.executeCitationLayoutAPI(DynamicAPIPath.GET_CITATION_LAYOUT + "citation")
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

    fun hitGetMunicipalCitationLayoutApi() {
        disposables.add(
            service.executeMunicipalCitationLayoutAPI(DynamicAPIPath.GET_MUNICIPAL_CITATION_LAYOUT)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { d: Disposable? -> municipalCitationResponse.setValue(loading()) }
                .subscribe(
                    { result: JsonNode? ->
                        municipalCitationResponse.setValue(
                            success(
                                result!!
                            )
                        )
                    },
                    { throwable: Throwable? ->
                        municipalCitationResponse.setValue(
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