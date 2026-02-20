package com.parkloyalty.lpr.scan.doubangoultimatelpr.model

import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.RequiresApi
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.Entity
import com.parkloyalty.lpr.scan.extensions.nullSafety

@Entity(tableName = "scan_data_table")
class ScanDataModel : Parcelable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id = 0

    @ColumnInfo(name = "licensePlateNumber")
    var licensePlateNumber: String? = null

    @ColumnInfo(name = "vehicleMakeBrand")
    var vehicleMakeBrand: String? = null

    @ColumnInfo(name = "vehicleModel")
    var vehicleModel: String? = null

    @ColumnInfo(name = "vehicleColor")
    var vehicleColor: String? = null

    @ColumnInfo(name = "vehicleBodyStyle")
    var vehicleBodyStyle: String? = null

    @ColumnInfo(name = "licensePlateCountry")
    var licensePlateCountry: String? = null

    @ColumnInfo(name = "licensePlateState")
    var licensePlateState: String? = null

    @ColumnInfo(name = "vehicleImageURL")
    var vehicleImageURL: String? = null

    @ColumnInfo(name = "officerName")
    var officerName: String? = null

    @ColumnInfo(name = "badgeId")
    var badgeId: String? = null

    @ColumnInfo(name = "beat")
    var beat: String? = null

    @ColumnInfo(name = "squad")
    var squad: String? = null

    @ColumnInfo(name = "block")
    var block: String? = null

    @ColumnInfo(name = "street")
    var street: String? = null

    @ColumnInfo(name = "streetSide")
    var streetSide: String? = null

    @ColumnInfo(name = "timeLimitText")
    var timeLimitText: String? = null

    @ColumnInfo(name = "vehicleRunStartTime")
    var vehicleRunStartTime: String? = null

    @ColumnInfo(name = "timeLimitValue")
    var timeLimitValue: String? = null

    @ColumnInfo(name = "date")
    var date: String? = null

    @ColumnInfo(name = "time")
    var time: String? = null

    @ColumnInfo(name = "markTimingTimestamp")
    var markTimingTimestamp: String? = null

    @ColumnInfo(name = "timestamp")
    var timestamp: Long? = null

    @ColumnInfo(name = "payment_exists")
    var paymentExists: Int? = null

    @ColumnInfo(name = "violation_exists")
    var violationExists: Int? = null

    @ColumnInfo(name = "scofflaw_exists")
    var scofflawExists: Int? = null

    @ColumnInfo(name = "permit_exists")
    var permitExists: Int? = null

    @ColumnInfo(name = "vehicle_run_type")
    var vehicleRunType: Int? = null

    @ColumnInfo(name = "vehicle_run_method")
    var vehicleRunMethod: String? = null

    @ColumnInfo(name = "is_result_selected")
    var isResultSelected : Boolean? = false

    @ColumnInfo(name = "latitude")
    var latitude : Double? = 0.0

    @ColumnInfo(name = "longitude")
    var longitude : Double? = 0.0

    constructor() {}
    @RequiresApi(Build.VERSION_CODES.Q)
    constructor(`in`: Parcel) {
        id = `in`.readInt()
        licensePlateNumber = `in`.readString()
        vehicleMakeBrand = `in`.readString()
        vehicleModel = `in`.readString()
        vehicleColor = `in`.readString()
        licensePlateState = `in`.readString()
        vehicleImageURL = `in`.readString()
        officerName = `in`.readString()
        badgeId = `in`.readString()
        beat = `in`.readString()
        squad = `in`.readString()
        block = `in`.readString()
        street = `in`.readString()
        streetSide = `in`.readString()
        timeLimitText = `in`.readString()
        vehicleRunStartTime = `in`.readString()
        timeLimitValue = `in`.readString()
        date = `in`.readString()
        time = `in`.readString()
        markTimingTimestamp = `in`.readString()
        timestamp = `in`.readLong()
        paymentExists = `in`.readInt()
        violationExists = `in`.readInt()
        scofflawExists = `in`.readInt()
        permitExists = `in`.readInt()
        vehicleRunType = `in`.readInt()
        vehicleRunMethod = `in`.readString()
        isResultSelected = `in`.readBoolean()
        latitude = `in`.readDouble()
        longitude = `in`.readDouble()
    }

    override fun describeContents(): Int {
        return 0
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun writeToParcel(parcel: Parcel, i: Int) {
        parcel.writeInt(id)
        parcel.writeString(licensePlateNumber)
        parcel.writeString(vehicleMakeBrand)
        parcel.writeString(vehicleModel)
        parcel.writeString(vehicleColor)
        parcel.writeString(licensePlateState)
        parcel.writeString(vehicleImageURL)
        parcel.writeString(officerName)
        parcel.writeString(badgeId)
        parcel.writeString(beat)
        parcel.writeString(squad)
        parcel.writeString(block)
        parcel.writeString(street)
        parcel.writeString(streetSide)
        parcel.writeString(timeLimitText)
        parcel.writeString(vehicleRunStartTime)
        parcel.writeString(timeLimitValue)
        parcel.writeString(date)
        parcel.writeString(time)
        parcel.writeString(markTimingTimestamp)
        parcel.writeLong(timestamp.nullSafety())
        parcel.writeInt(paymentExists.nullSafety())
        parcel.writeInt(violationExists.nullSafety())
        parcel.writeInt(scofflawExists.nullSafety())
        parcel.writeInt(permitExists.nullSafety())
        parcel.writeInt(vehicleRunType.nullSafety())
        parcel.writeString(vehicleRunMethod.nullSafety())
        parcel.writeBoolean(isResultSelected.nullSafety())
        parcel.writeDouble(latitude.nullSafety())
        parcel.writeDouble(longitude.nullSafety())
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<ScanDataModel> =
            object : Parcelable.Creator<ScanDataModel> {
                @RequiresApi(Build.VERSION_CODES.Q)
                override fun createFromParcel(`in`: Parcel): ScanDataModel? {
                    return ScanDataModel(`in`)
                }

                override fun newArray(size: Int): Array<ScanDataModel?> {
                    return arrayOfNulls(size)
                }
            }
    }
}