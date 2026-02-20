package com.parkloyalty.lpr.scan.ui.ticket.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fasterxml.jackson.databind.JsonNode
import com.parkloyalty.lpr.scan.network.ApiResponse
import com.parkloyalty.lpr.scan.network.ApiResponse.Companion.loading
import com.parkloyalty.lpr.scan.network.ApiResponse.Companion.success
import com.parkloyalty.lpr.scan.network.Service
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class TicketCancellationRequestViewModel @Inject constructor(private val service: Service) : ViewModel() {
    private val disposables = CompositeDisposable()
    val response = MutableLiveData<ApiResponse>()
    fun hitTicketCancellationApi(cancelRequest: TicketCancellationRequest?, id: String?) {
        disposables.add(
            service.executeTicketCancellationRequestAPI(cancelRequest, id!!)
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
                            ApiResponse.error(
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