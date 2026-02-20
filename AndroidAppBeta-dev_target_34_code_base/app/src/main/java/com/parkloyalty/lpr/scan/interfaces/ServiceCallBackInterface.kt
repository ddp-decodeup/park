package com.parkloyalty.lpr.scan.interfaces

import android.os.IBinder

interface ServiceCallBackInterface {
    fun isServiceActiveCall(`is`: Boolean, service: IBinder?)
}