package com.parkloyalty.lpr.scan.ui.unuploadimages

import com.parkloyalty.lpr.scan.ui.login.DatasetModel.UnUploadFacsimileImage

sealed class UnUploadImageSealedItem {
    data class Header(val ticketNumber: String, val lpNumber: String) : UnUploadImageSealedItem()
    data class Image(val path: String,val isUploaded: Boolean, val mObject: UnUploadFacsimileImage) : UnUploadImageSealedItem()
}
