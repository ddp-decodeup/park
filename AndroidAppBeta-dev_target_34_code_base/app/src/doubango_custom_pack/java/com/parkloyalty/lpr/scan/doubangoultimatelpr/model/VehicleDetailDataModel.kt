package com.parkloyalty.lpr.scan.doubangoultimatelpr.model

import android.os.Parcel
import android.os.Parcelable

class VehicleDetailDataModel(
    var licensePlateNumber: String? = null,
    var vehicleMakeBrand: String? = null,
    var vehicleModel: String? = null,
    var vehicleColor: String? = null,
    var vehicleBodyStyle: String? = null,
    var licensePlateCountry: String? = null,
    var licensePlateState: String? = null,
    var vehicleImageURL: String? = null,
    var officerName: String? = null,
    var beat: String? = null,
    var squad: String? = null,
    var block: String? = null,
    var street: String? = null,
    var streetSide: String? = null,
    var timingLimit: String? = null,
    var date: String? = null,
    var time: String? = null,
    var timestamp: Long? = null,
    var violation: Int? = null,
    var scofflaw: Int? = null,
    var permitList: Int? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Long::class.java.classLoader) as? Long,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(licensePlateNumber)
        parcel.writeString(vehicleMakeBrand)
        parcel.writeString(vehicleModel)
        parcel.writeString(vehicleColor)
        parcel.writeString(vehicleBodyStyle)
        parcel.writeString(licensePlateCountry)
        parcel.writeString(licensePlateState)
        parcel.writeString(vehicleImageURL)
        parcel.writeString(officerName)
        parcel.writeString(beat)
        parcel.writeString(squad)
        parcel.writeString(block)
        parcel.writeString(street)
        parcel.writeString(streetSide)
        parcel.writeString(timingLimit)
        parcel.writeString(date)
        parcel.writeString(time)
        parcel.writeValue(timestamp)
        parcel.writeValue(violation)
        parcel.writeValue(scofflaw)
        parcel.writeValue(permitList)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<VehicleDetailDataModel> {
        override fun createFromParcel(parcel: Parcel): VehicleDetailDataModel {
            return VehicleDetailDataModel(parcel)
        }

        override fun newArray(size: Int): Array<VehicleDetailDataModel?> {
            return arrayOfNulls(size)
        }
    }
}