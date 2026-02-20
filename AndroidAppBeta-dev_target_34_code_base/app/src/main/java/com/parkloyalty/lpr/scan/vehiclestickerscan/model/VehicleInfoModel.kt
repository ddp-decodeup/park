package com.parkloyalty.lpr.scan.vehiclestickerscan.model

import java.io.Serializable

data class VehicleInfoModel(
    val vin: String? = null,
    val make: String? = null,
    val model: String? = null,
    val year: String? = null,
    val expiryDate: String? = null,   // normalized: yyyy-MM-dd
    val expiryYear: String? = null,
    val expiryMonth: String? = null,
    val expiryDay: String? = null,
    val plateNumber: String? = null,
    val state: String? = null,        // 2-letter code if known
    val plateType: String? = null,    // e.g., Passenger, Commercial
    val bodyStyle: String? = null,    // e.g., SUV, Sedan
    val color: String? = null,
    val serialNumber: String? = null,   //
    val stickerSerialNumber: String? = null,   //
    val rawPdf417: String? = null,
    val raw1D: String? = null
) : Serializable