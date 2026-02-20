package com.parkloyalty.lpr.scan.ui.check_setup.activity.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fasterxml.jackson.databind.JsonNode
import com.parkloyalty.lpr.scan.network.ApiResponse
import com.parkloyalty.lpr.scan.network.Service
import com.parkloyalty.lpr.scan.ui.ticket.model.DriveOffTvrRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class LprHitGenetecViewModel @Inject constructor(private val service: Service) : ViewModel() {
    private val disposables = CompositeDisposable()
    val response = MutableLiveData<ApiResponse>()

    fun hitGenetecHitApi(mDataFromLprRequest: String?) {
        disposables.add(
            service.executeGenetecHitAPI(mDataFromLprRequest!!)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { d: Disposable? -> response.postValue(ApiResponse.loading()) }
                .subscribe(
                    { result: JsonNode? ->
                        response.postValue(
                            ApiResponse.success(
                                result!!
                            )
                        )
                    },
                    { throwable: Throwable? ->
                        response.postValue(
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