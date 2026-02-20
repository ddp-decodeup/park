package com.parkloyalty.lpr.scan.ui.allreport.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fasterxml.jackson.databind.JsonNode
import com.parkloyalty.lpr.scan.network.ApiResponse
import com.parkloyalty.lpr.scan.network.Service
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class NoticeToTowViewModel  @Inject constructor(private val service: Service) : ViewModel() {
    private val disposables = CompositeDisposable()
    val response = MutableLiveData<ApiResponse?>()
    fun hitNoticeToTowReportSubmitApi(noticeToTowRequest: NoticeToTowRequest?) {
        disposables.add(
            service.executeNoticeToTowReportAPI(noticeToTowRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { d: Disposable? -> response.setValue(ApiResponse.loading()) }
                .subscribe(
                    { result: JsonNode? ->
                        response.setValue(
                            ApiResponse.success(
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