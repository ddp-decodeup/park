package com.parkloyalty.lpr.scan.ui.login.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fasterxml.jackson.databind.JsonNode
import com.parkloyalty.lpr.scan.network.ApiResponse
import com.parkloyalty.lpr.scan.network.ApiResponse.Companion.error
import com.parkloyalty.lpr.scan.network.ApiResponse.Companion.loading
import com.parkloyalty.lpr.scan.network.ApiResponse.Companion.success
import com.parkloyalty.lpr.scan.network.Service
import com.parkloyalty.lpr.scan.util.FileUtil.getHeaderFooterDirectory
import com.parkloyalty.lpr.scan.util.FileUtil.getHeaderFooterFileName
import com.parkloyalty.lpr.scan.util.FileUtil.saveRequestBodyStreamToFileStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class WelcomeViewModel @Inject constructor(private val service: Service) : ViewModel() {
    private val disposables = CompositeDisposable()
    val response = MutableLiveData<ApiResponse>()
    fun hitWelcomeApi() {
        disposables.add(
            service.executeWelcomeAPI()
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

    override fun onCleared() {
        disposables.clear()
    }

    val downloadStatus: MutableLiveData<String> = MutableLiveData()

    fun downloadHeaderFooterImage(isHeader: Boolean, url: String) {
        //downloadStatus.value = "Downloading..."

        service.downloadFile(url) { response, error ->
            if (error != null) {
                downloadStatus.value = "Error: ${error.message}"
            } else {
                if (response != null && response.isSuccessful) {
                    response.body()?.let {
                        saveRequestBodyStreamToFileStorage(
                            it, getHeaderFooterDirectory(),
                            getHeaderFooterFileName(isHeader)
                        )
                    }
                    //downloadStatus.value = "Image downloaded successfully"
                } else {
                    downloadStatus.value = "Download failed"
                }
            }
        }
    }
}