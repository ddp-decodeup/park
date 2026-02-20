package com.parkloyalty.lpr.scan.doubangoultimatelpr.interfaces

import com.parkloyalty.lpr.scan.doubangoultimatelpr.model.VehicleDetailDataModel
import java.util.ArrayList

interface FragmentDataIntercepter {
    fun vehicleDetailsInSingleShotMode(
        licensePlateNumber: String?,
        vehicleMakeBrand: String?,
        vehicleModel: String?,
        vehicleColor: String?,
        vehicleBodyStyle: String?,
        licensePlateCountry: String?,
        licensePlateState: String?,
        vehicleImageURL: String?
    )

    fun vehicleDetailsInContinuousMode(listOfScannedVehicles: ArrayList<VehicleDetailDataModel?>) {}

    fun isContinuousModeStarted(isStarted : Boolean) {}
}