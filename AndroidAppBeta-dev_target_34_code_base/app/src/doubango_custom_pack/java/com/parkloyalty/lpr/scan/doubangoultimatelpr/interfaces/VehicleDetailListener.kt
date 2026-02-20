package com.parkloyalty.lpr.scan.doubangoultimatelpr.interfaces

interface VehicleDetailListener {
    fun getLicensePlateNumber(licensePlateNumber: String?)
    fun getVehicleMakeBrand(vehicleMakeBrand: String?)
    fun getVehicleModel(vehicleModel: String?)
    fun getVehicleColor(vehicleColor: String?)
    fun getVehicleBodyStyle(vehicleBodyStyle: String?)
    fun getLicensePlateCountry(licensePlateCountry: String?)
    fun getLicensePlateState(licensePlateState: String?)
}