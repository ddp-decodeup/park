package com.twotechnologies.xfexample.EventSupport

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.twotechnologies.n5library.N5Information
import com.twotechnologies.n5library.N5ReadyListener

class XFActivityCoroutine : AppCompatActivity, ServiceEventCallback {
    private var mlistenerCallback: ListenerCallback? = null
    private var mPlatformListeners: ServiceListeners? = null
    private var mbStarted = false
    private lateinit var mContext: Context

    constructor()
    constructor(context: Context?, listenerCallback: ListenerCallback?) {
        mContext = context!!
        mlistenerCallback = listenerCallback
        mPlatformListeners = ServiceListeners(context)
        mPlatformListeners!!.setCallback(this)
        mbStarted = false
    }

    //**************************************************************************************************
    // setListenerCallback
    //  activty can set where to call when a broadcast is received
    //**************************************************************************************************
    fun setListenerCallback(listenerCallback: ListenerCallback?) {
        mlistenerCallback = listenerCallback

        // issue platform status when this is changed
        var status = N5ReadyListener.ACTION_N5_NOT_READY
        if (N5Information.isPlatformAvailable()) {
            status = N5ReadyListener.ACTION_N5_READY
        }
        val localIntent = Intent(status)
        mContext.sendBroadcast(localIntent)
        // LocalBroadcastManager.getInstance( mContext ).sendBroadcast( localIntent );
    }

    //**************************************************************************************************
    //
    //**************************************************************************************************
    fun destroy() {
        stop()
    }

    //**************************************************************************************************
    // start
    //  requests listeners to start listening
    //**************************************************************************************************
    fun start() {
        if (!mbStarted) mPlatformListeners!!.start()
        mbStarted = true
    }

    //**************************************************************************************************
    //
    //**************************************************************************************************
    fun stop() {
        if (mbStarted) mPlatformListeners!!.stop()
        mbStarted = false
    }

    //**************************************************************************************************
    // listener
    //**************************************************************************************************
    //fun listener(eEvent: ServiceEvents, context: Context, intent: Intent) {
    //    if (mlistenerCallback != null) mlistenerCallback!!.listenerEvent(eEvent)
    //}

    override fun listener(eEvent: ServiceEvents?, context: Context?, intent: Intent?) {
        if (mlistenerCallback != null) mlistenerCallback!!.listenerEvent(eEvent)
    }
}
