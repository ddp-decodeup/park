package com.twotechnologies.xfexample.EventSupport

import android.content.Context
import android.content.Intent

interface ServiceEventCallback {
    fun listener(eEvent: ServiceEvents?, context: Context?, intent: Intent?)
}
