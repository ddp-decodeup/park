package com.parkloyalty.lpr.scan.qrcode.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fasterxml.jackson.databind.JsonNode
import com.parkloyalty.lpr.scan.network.ApiResponse
import com.parkloyalty.lpr.scan.network.ApiResponse.Companion.loading
import com.parkloyalty.lpr.scan.network.ApiResponse.Companion.success
import com.parkloyalty.lpr.scan.network.Service
import com.parkloyalty.lpr.scan.network.model.inventorymanagementsystem.EquipmentCheckInOutRequest
import com.parkloyalty.lpr.scan.network.model.inventorymanagementsystem.LogoutNoteForEquipmentRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class InventoryViewModel @Inject constructor(private val service: Service) : ViewModel() {
    private val disposables = CompositeDisposable()
    private val disposableForEquipmentCheckout = CompositeDisposable()
    private val disposableForEquipmentCheckIn = CompositeDisposable()
    private val disposableForAddNoteForNotCheckedInEquipment = CompositeDisposable()
    private val disposableForGetOfficerEquipment = CompositeDisposable()

    val response = MutableLiveData<ApiResponse>()
    val responseForEquipmentCheckout = MutableLiveData<ApiResponse>()
    val responseForEquipmentCheckIn = MutableLiveData<ApiResponse>()
    val responseForAddNoteForNotCheckedInEquipment = MutableLiveData<ApiResponse>()
    val responseForGetOfficerEquipment = MutableLiveData<ApiResponse>()

    /**
     * API used to get officer's equipment list, basically it will return the equipments which is being
     * checked out by the logged in officer
     */
    fun getOfficerEquipmentList() {
        disposableForGetOfficerEquipment.add(
            service.getOfficersEquipmentList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { d: Disposable? -> responseForGetOfficerEquipment.setValue(loading()) }
                .subscribe(
                    { result: JsonNode? ->
                        responseForGetOfficerEquipment.setValue(
                            success(
                                result!!
                            )
                        )
                    },
                    { throwable: Throwable? ->
                        responseForGetOfficerEquipment.setValue(
                            ApiResponse.error(
                                throwable!!
                            )
                        )
                    }
                ))
    }

    /**
     * API being used to checkout the equipment after the QR code scanning
     * Checkout : means taking the device for use
     */
    fun callEquipmentCheckOutAPI(equipmentCheckInOutRequest: EquipmentCheckInOutRequest?) {
        disposableForEquipmentCheckout.add(
            service.logEquipmentCheckedOut(equipmentCheckInOutRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { d: Disposable? -> responseForEquipmentCheckout.setValue(ApiResponse.loading()) }
                .subscribe(
                    { result: JsonNode? ->
                        responseForEquipmentCheckout.postValue(
                            success(
                                result!!
                            )
                        )
                    }
                ) { throwable: Throwable? ->
                    responseForEquipmentCheckout.postValue(
                        ApiResponse.error(
                            throwable!!
                        )
                    )
                })
    }

    /**
     * API being used to checkin the equipment after the QR code scanning
     * checkin : means returning the device back
     */
    fun callEquipmentCheckInAPI(equipmentCheckInOutRequest: EquipmentCheckInOutRequest?) {
        disposableForEquipmentCheckIn.add(
            service.logEquipmentCheckedIn(equipmentCheckInOutRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { d: Disposable? -> responseForEquipmentCheckIn.setValue(ApiResponse.loading()) }
                .subscribe(
                    { result: JsonNode? ->
                        responseForEquipmentCheckIn.postValue(
                            success(
                                result!!
                            )
                        )
                    }
                ) { throwable: Throwable? ->
                    responseForEquipmentCheckIn.postValue(
                        ApiResponse.error(
                            throwable!!
                        )
                    )
                })
    }

    /**
     * API being used to add a note before logout when you have checked an equipment & not able to check in back
     * and wanted to logout
     */
    fun addNoteForNotCheckedInEquipment(logoutNoteForEquipmentRequest: LogoutNoteForEquipmentRequest?) {
        disposableForAddNoteForNotCheckedInEquipment.add(
            service.addNoteForNotCheckedInEquipment(logoutNoteForEquipmentRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { d: Disposable? ->
                    responseForAddNoteForNotCheckedInEquipment.setValue(
                        ApiResponse.loading()
                    )
                }
                .subscribe(
                    { result: JsonNode? ->
                        responseForAddNoteForNotCheckedInEquipment.postValue(
                            success(
                                result!!
                            )
                        )
                    }
                ) { throwable: Throwable? ->
                    responseForAddNoteForNotCheckedInEquipment.postValue(
                        ApiResponse.error(
                            throwable!!
                        )
                    )
                })
    }

    override fun onCleared() {
        disposables.clear()
        disposableForEquipmentCheckout.clear()
        disposableForEquipmentCheckIn.clear()
        disposableForGetOfficerEquipment.clear()
        disposableForAddNoteForNotCheckedInEquipment.clear()
    }
}