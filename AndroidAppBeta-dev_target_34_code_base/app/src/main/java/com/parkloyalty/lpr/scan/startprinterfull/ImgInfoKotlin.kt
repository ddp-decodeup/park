package com.parkloyalty.lpr.scan.startprinterfull

class ImgInfoKotlin {
    private var mImgResourceID = 0
    private var mResourceID = 0

    constructor(imgResourceID: Int, resourceID: Int) {
        mImgResourceID = imgResourceID
        mResourceID = resourceID
    }

    fun getImgResourceID(): Int {
        return mImgResourceID
    }

    fun getResourceID(): Int {
        return mResourceID
    }
}