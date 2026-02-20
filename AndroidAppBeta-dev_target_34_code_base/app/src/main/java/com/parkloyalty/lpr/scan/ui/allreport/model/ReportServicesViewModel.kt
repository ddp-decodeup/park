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
class ReportServicesViewModel @Inject constructor(private val service: Service) : ViewModel() {
    private val disposables = CompositeDisposable()
    val response = MutableLiveData<ApiResponse?>()
    fun hitNFLReportSubmitApi(nflRequset: NFLRequest?) {
        disposables.add(
            service.executeNFLReportAPI(nflRequset)
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
    fun hitLotCountVioRateReportSubmitApi(lotCountVioRateRequest: LotCountVioRateRequest?) {
        disposables.add(
            service.executeLotCountVioRateReportAPI(lotCountVioRateRequest)
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

    fun hitHardSummerReportSubmitApi(hardRequset: NFLRequest?) {
        disposables.add(
            service.executeHardSummerReportAPI(hardRequset)
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

    fun hitAfterSevenPMReportSubmitApi(afterSevenRequset: AfterSevenPMRequest?) {
        disposables.add(
            service.executeAfterSevenReportAPI(afterSevenRequset)
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

    fun hitPayStationReportSubmitApi(afterSevenRequset: PayStationRequest?) {
        disposables.add(
            service.executePayStationReportAPI(afterSevenRequset)
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

    fun hitSignageReportSubmitApi(afterSevenRequset: SignageReportRequest?) {
        disposables.add(
            service.executeSignageReportAPI(afterSevenRequset)
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
    fun hitHomelessReportSubmitApi(homelessRequest: HomelessRequest?) {
        disposables.add(
            service.executeHomelessReportAPI(homelessRequest)
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
    fun hitWorkOrderReportSubmitApi(workOrderRequest: WorkOrderRequest?) {
        disposables.add(
            service.executeWorkOrderReportAPI(workOrderRequest)
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
    fun hitSafetyIssueReportSubmitApi(safetyIssueRequest: SafetyIssueRequest?) {
        disposables.add(
            service.executeSafetyIssueReportAPI(safetyIssueRequest)
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
    fun hitTrashLotReportSubmitApi(trashLotRequest: TrashLotRequest?) {
        disposables.add(
            service.executeTrashLotReportAPI(trashLotRequest)
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
    fun hitLotInspectionReportSubmitApi(lotInspection: LotInspectionRequest?) {
        disposables.add(
            service.executeLotInspectionReportAPI(lotInspection)
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